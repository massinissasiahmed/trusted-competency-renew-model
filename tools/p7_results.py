"""Check and summarize captured native P7 verification; never run a simulation."""
import csv
import hashlib
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'results'


def read(path):
    raw = path.read_bytes()
    return raw.decode('utf-16' if raw[:2] in (b'\xff\xfe', b'\xfe\xff') else 'utf-8-sig').replace('\r\n', '\n')


def csv_out(name, headers, rows):
    with (OUT / name).open('w', encoding='utf-8', newline='') as stream:
        writer = csv.writer(stream)
        writer.writerow(headers)
        writer.writerows(rows)


def main():
    baseline = read(OUT / 'p7_post_repair_regressions.txt')
    replay = read(OUT / 'p7_post_repair.txt')
    old = read(OUT / 'p7_counterexample.txt')
    expected = {'happy', 'reject', 'cancel', 'expire', 'grade_reject', 'ledger_reject',
                'bad_signature', 'bad_issuer_signature', 'grade_reject_waiting',
                'cancel_submitted', 'cancel_waiting', 'expire_submitted', 'expire_waiting',
                'bad_issuer_signature_submitted', 'bad_issuer_signature_waiting'}
    passes = re.findall(r'^PASS SCENARIO (\S+) (\S+) OUTCOME (\S+)$', baseline, re.M)
    assert len(passes) == 15 and {x[0] for x in passes} == expected
    assert 'PASS: all 11 RNW files deserialized and compiled together' in baseline
    assert 'AUTH ENUMERATION PASS invalid_student_received' in baseline
    for phase in ('submitted', 'waiting', 'received'):
        assert f'P7 COMPETING DENIAL AVAILABLE {phase}' in replay
        assert f'P7 COMPETING ACCEPTANCE UNAVAILABLE {phase}' in replay
        assert f'AUTH ENUMERATION PASS {phase}' in replay
    assert 'forbidden_bindings=0' in replay
    assert 'ENABLED BINDINGS 0' in replay and 'ACTIVE UNFINISHED 0' in replay
    assert 'PASS SCENARIO bad_issuer_signature EXPECTED_FAILURE_TERMINATION OUTCOME INVALID_ISSUER_SIGNATURE' in replay
    assert 'wrong-issuer-signature' in replay
    old_fired = re.findall(r'^FIRED (.+)$', old, re.M)
    new_fired = re.findall(r'^FIRED (.+)$', replay, re.M)
    stop = old_fired.index('StudentAgent.t_AcceptCredential')
    assert old_fired[:stop] == new_fired[:stop]
    assert new_fired[stop:] == ['CompetencyNet.t_DenyInvalidIssuerSignature']
    history = ['NEW_CORRECTNESS_DEFECT.md', 'results/p7_counterexample.txt',
               'results/p7_final_marking.csv', 'results/interleaving_results.csv']
    before = json.loads(read(OUT / 'p7_repair_before_hashes.json'))
    preserved = []
    for name in history:
        actual = hashlib.sha256((ROOT / name).read_bytes()).hexdigest()
        assert actual == before[name], name
        preserved.append((name, before[name], actual, 'UNCHANGED'))
    csv_out('p7_history_preservation.csv', ['file', 'before_sha256', 'after_sha256', 'status'], preserved)
    csv_out('p7_regression_results.csv', ['scenario', 'termination', 'outcome'], passes)
    final = re.findall(r'^FINAL TOKEN (\S+) = (.+)$', replay, re.M)
    assert final
    csv_out('p7_post_repair_final_marking.csv', ['net_place', 'tokens'], final)
    enums = re.findall(r'^AUTH ENUMERATION PASS (\S+) complete_bindings=(\d+) forbidden_bindings=(\d+)$', replay, re.M)
    assert len(enums) == 3 and all(x[2] == '0' for x in enums)
    csv_out('p7_complete_binding_summary.csv', ['phase', 'complete_bindings', 'forbidden_bindings'], enums)
    csv_out('p7_test_results.csv', ['test', 'status', 'native_evidence'], [
        ('P7-A', 'PASS', 'p7_post_repair_regressions.txt: happy SUCCESS'),
        ('P7-B', 'PASS', 'p7_post_repair.txt: all complete bindings, received; finite denial'),
        ('P7-C', 'PASS', 'p7_post_repair.txt: disabled valid cancellation; INVALID_ISSUER_SIGNATURE'),
        ('P7-D', 'PASS', 'p7_post_repair_regressions.txt: cancel CANCELLED'),
        ('P7-E', 'PASS', 'p7_post_repair_regressions.txt: submitted/waiting/received native denial endpoints'),
        ('P7-F', 'PASS', 'p7_post_repair.txt: no acceptance or ledger participant; order/validate/commit/anchor disabled'),
        ('P6', 'PASS', 'p7_post_repair_regressions.txt: invalid_student_received enumeration; INVALID_SIGNATURE')])
    # Exact byte comparison after reversing ONLY the authorized inscription substitutions.
    changes = json.loads(read(OUT / 'p7_inscription_changes.json'))
    drawings = []
    for path in sorted(ROOT.glob('*.rnw')):
        data = path.read_bytes()
        restored = data
        for old_text, new_text in changes.get(path.stem, []):
            assert restored.count(new_text.encode()) == 1
            restored = restored.replace(new_text.encode(), old_text.encode())
        assert hashlib.sha256(restored).hexdigest() == before[path.name], path.name
        drawings.append((path.name, len(changes.get(path.stem, [])), 'PRESERVED', hashlib.sha256(data).hexdigest()))
    csv_out('p7_geometry_preservation.csv', ['drawing', 'inscriptions_changed', 'other_bytes_ids_geometry', 'after_sha256'], drawings)
    summary = {'tested_P7': 'PASS', 'exhaustive_P7': False, 'cryptographic_verification': False,
               'regressions_passed': len(passes), 'old_firing_prefix_replayed': stop,
               'drawings': len(drawings), 'changed_drawings': len(changes),
               'changed_inscriptions': sum(map(len, changes.values())),
               'places_added': 0, 'transitions_added': 0, 'arcs_added': 0,
               'replay_enabled_at_endpoint': 0, 'replay_active_unfinished': 0,
               'historical_evidence_preserved': True}
    (OUT / 'p7_repair_summary.json').write_text(json.dumps(summary, indent=2) + '\n', encoding='utf-8')
    print('PASS: 15 regressions, P7-A through P7-F, P6, exact old prefix replay, historical preservation and unchanged geometry.')


if __name__ == '__main__':
    main()
