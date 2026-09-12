# P7 issuer-authorization repair

Status: **P7 PASS in the tested fixtures and phases; NOT exhaustively verified.** Work stopped after this repair verification. No broader formal, concurrency or performance campaign was resumed. No commit, push or merge was made; work is on fix/p7-issuer-authorization.

## Root cause and contract

The provided issuerSignature in CompetencyNet was checked against CredentialObject's expected issuerSignature only for cancellation and the optional denial branch. The acceptance transaction checked student signature and time but omitted issuer authorization, allowing it to consume the same pending tokens before denial.

P7_AUTHORIZATION_CONTRACT.md was written before RNW edits. Equality of these two already-carried strings means valid issuer authorization; mismatch must prevent every successful decision/ledger/VP/HR continuation and retain finite INVALID_ISSUER_SIGNATURE denial. Issuer signatures remain simulation strings; this repair models authorization logic and is not cryptographic verification.

Both strings can first be consulted through the newly created co reference during review creation. Blocking review/mint alone would strand the old post-mint denial path. Early rejection there would need additional failure topology and University/Wallet abort phases. The chosen minimal repair treats mint as untrusted staging and gates the first consuming credential decision, before the atomic ledger request. It does not claim to prevent untrusted mint. No signature state is copied into new places or globally looked up.

## Exact changes

Eight channel inscriptions in CompetencyNet.rnw and CredentialObject.rnw now pass/unify the provided issuerSignature against the existing expected issuerSignature token variable. Channel argument unification is the equality constraint, so a mismatching transaction has no complete binding and no partial ledger side effect. Cancellation already enforced equality. Reject, expiry and invalid-student denial are also gated, so they cannot consume an unauthorized credential into another post-mint outcome; issuer denial remains unchanged.

| Drawing | Old fragment | New fragment |
| --- | --- | --- |
| CompetencyNet | `co:sbt_accept(studentSignature)` | `co:sbt_accept(studentSignature,issuerSignature)` |
| CompetencyNet | `co:sbt_reject(studentSignature)` | `co:sbt_reject(studentSignature,issuerSignature)` |
| CompetencyNet | `co:expire()` | `co:expire(issuerSignature)` |
| CompetencyNet | `co:deny(providedSignature)` | `co:deny(providedSignature,issuerSignature)` |
| CredentialObject | `:sbt_accept(studentSignature)` | `:sbt_accept(studentSignature,issuerSignature)` |
| CredentialObject | `:sbt_reject(studentSignature)` | `:sbt_reject(studentSignature,issuerSignature)` |
| CredentialObject | `:expire()` | `:expire(issuerSignature)` |
| CredentialObject | `:deny(providedSignature)` | `:deny(providedSignature,issuerSignature)` |

model.tsv and model_manifest.json contain the matching inscription changes. tools/BuildProject.java adds read-only complete-binding enumeration, success-binding/marking exclusions and issuer-denial availability checks at supported phases, and a P6 binding check. Existing fixtures, denial tests, success firings and endpoint assertions remain intact. VerifyP7.ps1 and tools/p7_results.py provide focused reproduction and evidence checks. New contract/report and results/p7_* files document the repair; historical files listed below are preserved.

Topology delta: **0 places, 0 transitions, 0 arcs** added/removed/reconnected. All figure IDs, geometry and other RNW bytes are preserved. results/p7_geometry_preservation.csv checks exact byte equality to the pre-edit hash after reversing only the authorized substitutions. Nine other drawings are byte-identical. The unchanged structural inventory is 11 nets, 80 places, 87 transitions, 174 arcs (native compilation in results/p7_post_repair_regressions.txt and prior results/structure.csv).

## Counterexample and replay

NEW_CORRECTNESS_DEFECT.md, results/p7_counterexample.txt, results/p7_final_marking.csv and results/interleaving_results.csv remain historical failing evidence. Their before/after SHA256 values match in results/p7_history_preservation.csv. Historical PROPERTY_RESULTS.md and SCIENTIFIC_RESULTS.md describe the earlier analysis and have not been rewritten as new successes; this report is the current repair result.

The old fixture is unchanged: Competency carries wrong-issuer-signature, CredentialObject expects issuer-signature, Student carries student-signature, grade 80/threshold 50 and current time 0/expiry 100. The replay matches the exact 12 native firings preceding the old acceptance step, verified against the preserved old trace by tools/p7_results.py. Extra read-only observations do not fire transitions. Where the old scheduler selected Student.t_AcceptCredential, the native complete-binding search now finds no such binding. It also finds no complete binding with ledger execution. The replay therefore cannot execute the rest of the old success order; it takes the existing finite issuer-denial transaction instead.

results/p7_post_repair.txt contains the native replay, all complete binding occurrences from every spontaneous initiator (Finder.isCompleted always false, no output deduplication), denial/acceptance checks, disabled ledger scheduler steps and exact final tokens. At submitted and waiting there are two complete bindings: Student progress or atomic issuer denial. At received there is one: atomic issuer denial. These counts are current-marking bindings, not reachable-state counts. See results/p7_complete_binding_summary.csv. The final marking has zero enabled bindings and zero active unfinished instances, with all 11/11 references retained. Student and Wallet are rejected, Competency terminated and CredentialObject rejected with INVALID_ISSUER_SIGNATURE; ledger and HR remain uninvoked. Exact tokens are in results/p7_post_repair_final_marking.csv.

## Required verification

| Test | Result | Native evidence |
| --- | --- | --- |
| P7-A valid issuer + valid student + accept | PASS | happy reaches anchored SUCCESS and both VP flows/HR result |
| P7-B invalid issuer + valid student | PASS | ALL complete bindings enumerated at received; acceptance absent, denial fires |
| P7-C invalid issuer cancellation attempt | PASS | native cancellation binding unavailable; INVALID_ISSUER_SIGNATURE endpoint |
| P7-D valid issuer cancellation | PASS | cancel and both phase variants retain CANCELLED |
| P7-E supported invalid-issuer phases | PASS | submitted, waiting, received binding checks; separate finite-denial endpoints |
| P7-F invalid issuer ledger-success attempt | PASS | no unauthorized accept/ledger complete binding; Order/Validate/Commit/Anchor all disabled |
| P6 invalid student signature | PASS | no acceptance/ledger/VP complete binding; finite INVALID_SIGNATURE endpoint |

All original 15 scenarios pass without fixture or expected-outcome changes: happy, reject, cancel, expire, grade_reject, ledger_reject, bad_signature, bad_issuer_signature, grade_reject_waiting, cancel_submitted, cancel_waiting, expire_submitted, expire_waiting, bad_issuer_signature_submitted, bad_issuer_signature_waiting. Every scenario checks exact final marking, complete native binding participants, global endpoint quiescence and retained references. Sources: results/p7_post_repair_regressions.txt, results/p7_regression_results.csv and results/p7_test_results.csv.

All 11 actual saved RNWs deserialize and compile together under Renew's Timed Java Compiler, with structure and annotation-owner checks against model.tsv. GUI reopening is separately recorded in results/p7_gui_verification.txt; startup diagnostics are retained separately and are not substituted for native validation.

## Reproduction and limits

Run `powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\VerifyP7.ps1` (optional `-RenewHome 'C:\path\to\Renew'`). Requires the local Renew 4.1 runtime, Java/Javac 17 and Python 3. The entry point runs Validate.ps1 -Smoke, then the same invalid-issuer native scenario as a dedicated replay, then checks/extracts evidence. Success is exit zero; native or evidence errors fail the run. The individual entry-point commands were executed successfully during this task. It writes only distinct post-repair artifacts. Do not rerun RunFormalExperiments.ps1 against this changed checkpoint: it belongs to the stopped historical analysis and writes historical artifact names.

These are directed native executions and complete-binding searches at selected markings, not an exhaustive state-space exploration or proof over arbitrary strings/roots/times. In particular P7-E covers the three existing post-mint abort phases, not a new pre-mint rejection protocol. P6 is a regression for the existing invalid-student fixture with a valid issuer. No cryptographic algorithm, Fabric integration or blockchain benchmark was added. No performance, concurrency or broader interleaving experiment was performed. Tested P7 is PASS; exhaustive P7 and real cryptographic verification are NO.
