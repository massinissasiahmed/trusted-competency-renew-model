"""Reproducible native directed campaign. Generated artifacts stay under post_p7."""
import argparse
import csv
import hashlib
import itertools
import json
import os
from pathlib import Path
import platform
import re
import shutil
import statistics
import subprocess
import sys
sys.dont_write_bytecode = True

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'results/post_p7'
STAGE = OUT / 'checkpoint'
SCENARIOS = ['happy','reject','cancel','expire','grade_reject','ledger_reject','bad_signature',
             'bad_issuer_signature','grade_reject_waiting','cancel_submitted','cancel_waiting',
             'expire_submitted','expire_waiting','bad_issuer_signature_submitted','bad_issuer_signature_waiting']
DECISIONS = {'accept':'StudentAgent.t_AcceptCredential','reject':'StudentAgent.t_RejectCredential',
             'cancel':'CompetencyNet.t_SBT_Cancel','expire':'CompetencyNet.t_SBT_Expire',
             'student_deny':'StudentAgent.t_DenyInvalidSignature','issuer_deny':'CompetencyNet.t_DenyInvalidIssuerSignature'}


def write_json(name, value):
    (OUT / name).write_text(json.dumps(value, indent=2) + '\n', encoding='utf-8')


def csv_write(name, fields, rows):
    with (OUT / name).open('w', newline='', encoding='utf-8') as f:
        w=csv.DictWriter(f, fieldnames=fields);w.writeheader();w.writerows(rows)


def text_read(p):
    b=p.read_bytes()
    return b.decode('utf-16' if b[:2] in (b'\xff\xfe',b'\xfe\xff') else 'utf-8-sig').replace('\r\n','\n')


def command(args, name, cwd=STAGE):
    with (OUT / (name+'.txt')).open('w',encoding='utf-8') as out, (OUT / (name+'_stderr.txt')).open('w',encoding='utf-8') as err:
        proc=subprocess.run([str(x) for x in args],cwd=cwd,stdout=out,stderr=err)
    return proc.returncode,text_read(OUT/(name+'.txt'))


def prepare(home):
    OUT.mkdir(parents=True,exist_ok=True);STAGE.mkdir(exist_ok=True)
    files=list(ROOT.glob('*.rnw'))+list(ROOT.glob('*.md'))+[ROOT/x for x in ['model.tsv','model_manifest.json','Validate.ps1','VerifyP7.ps1','tools/BuildProject.java','tools/p7_results.py']]
    files += [p for p in (ROOT/'results').iterdir() if p.is_file()]
    hashes={str(p.relative_to(ROOT)):hashlib.sha256(p.read_bytes()).hexdigest() for p in files}
    write_json('run_input_hashes.json',hashes)
    for p in files:
        q=STAGE/p.relative_to(ROOT);q.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(p,q)
    env={'commit':subprocess.check_output(['git','rev-parse','HEAD'],cwd=ROOT,text=True).strip(),
         'branch':subprocess.check_output(['git','branch','--show-current'],cwd=ROOT,text=True).strip(),
         'os':platform.platform(),'python':sys.version,'java':subprocess.run(['java','-version'],capture_output=True,text=True).stderr,
         'renew':'4.1, local distribution manual','renew_home':str(home),'formalism':'Timed Java Compiler / JavaNetCompiler(true,true,true)',
         'engine':'Sequential simulatorMode=-1','identity':'uncommitted working tree; see run_input_hashes.json'}
    write_json('environment.json',env)
    csv_write('domain.csv',['dimension','representatives'],[{'dimension':k,'representatives':v} for k,v in {
        'grade':'80;40','threshold':'50','current_time':'0;100','expiry':'100 fixed',
        'student_signature':'valid;invalid','issuer_signature':'valid;invalid','holder':'accept;reject where enabled',
        'ledger':'success;reject from ordered where enabled','roots':'1 (representative domain)'}.items()])
    return hashes


def baseline(home):
    for script,extra,name in [('Validate.ps1',['-Smoke'],'baseline_validation'),('VerifyP7.ps1',[],'focused_verification')]:
        code,output=command(['powershell.exe','-NoProfile','-ExecutionPolicy','Bypass','-File',STAGE/script,'-RenewHome',home,*extra],name)
        if code:raise RuntimeError('BASELINE FAILED: '+name+'; no experiments may follow')
        if script=='Validate.ps1':assert len(re.findall(r'^PASS SCENARIO ',output,re.M))==15
        else:assert 'PASS: 15 regressions, P7-A through P7-F' in output
    for source,target in [('p7_post_repair_regressions.txt','focused_baseline_native.txt'),('p7_post_repair.txt','focused_p7_native.txt'),('p7_post_repair_final_marking.csv','focused_p7_final_marking.csv'),('p7_post_repair_regressions_stderr.txt','focused_baseline_stderr.txt'),('p7_post_repair_stderr.txt','focused_p7_stderr.txt')]:
        shutil.copyfile(STAGE/'results'/source,OUT/target)
    print('Baseline and focused P7 verification PASS',flush=True)


def compile_driver(home):
    build=OUT/'build';build.mkdir(exist_ok=True)
    source=(ROOT/'tools/BuildProject.java').read_text(encoding='utf-8')
    source=source.replace('public class BuildProject','public class PostP7Base')
    source=source.replace('de.renew.net.NetInstanceList.getAll()','PostP7Driver.instances()')
    start=source.index('            var system=lookup.getNet("SystemNet").buildInstance();')
    end=source.index('            return true;',start)
    source=source[:start]+'            PostP7Driver.run(lookup);\n'+source[end:]
    start=source.index('              if(scenario.startsWith("expire")) marking=')
    end=source.index('              txt.setText(marking);',start)
    source=source[:start]+'''              if(drawing.getName().equals("StudentAgent")) {
                marking=marking.replace("[80,50,0,100", "["+Integer.getInteger("post.grade",80)+",50,"+Integer.getInteger("post.time",0)+",100");
                if(Boolean.getBoolean("post.badStudent"))marking=marking.replace("student-signature","wrong-signature");
              }
              if(drawing.getName().equals("CompetencyNet")&&Boolean.getBoolean("post.badIssuer"))marking=marking.replace("issuer-signature","wrong-issuer-signature");
'''+source[end:]
    (build/'PostP7Base.java').write_text(source,encoding='utf-8')
    cp=os.pathsep.join([str(home/'plugins/*'),str(home/'libs/*'),str(home/'de.renew.loader.jar')])
    code,_=command(['javac','-cp',cp,'-d',build,build/'PostP7Base.java',ROOT/'tools/PostP7Driver.java'],'driver_compile')
    if code:raise RuntimeError('Driver compilation failed')
    write_json('driver_identity.json',{str(p.relative_to(ROOT)):hashlib.sha256(p.read_bytes()).hexdigest() for p in [ROOT/'tools/PostP7Driver.java',build/'PostP7Base.java',build/'PostP7Base.class',build/'PostP7Driver.class']})
    return os.pathsep.join([str(build),cp])


class Campaign:
    def __init__(self,cp):
        self.cp=cp;self.runs=[];self.refs=[];self.bindings=[];self.coverage=set();self.defect=None

    def execution(self,kind,identifier,config,fixture=''):
        name='raw/'+identifier;(OUT/'raw').mkdir(exist_ok=True)
        props=['-Dpost.'+k+'='+str(v).lower() for k,v in config.items()]
        code,output=command(['java','-Djava.awt.headless=true',*props,'-cp',self.cp,'PostP7Base',STAGE,'smoke','happy'],name)
        violation=re.findall(r'^PROPERTY_VIOLATION (P\d+) (.+)$',output,re.M)
        if code and not violation:raise RuntimeError('Native tooling/execution error in '+identifier+'; inspect '+name+'_stderr.txt')
        results=re.findall(r'^RESULT\t(\d+)\t(\S+)\t(\d+)\t(\d+)\t(\d+)\t(\S+)$',output,re.M)
        fired=re.findall(r'^FIRED (.+)$',output,re.M)
        runtime=re.search(r'^RUNTIME_NS (\d+)$',output,re.M)
        for line in re.findall(r'^BINDING .+ = (.+)$',output,re.M):self.coverage.update(line.split(','))
        self.coverage.update(re.findall(r'^BOOTSTRAP OBSERVED (.+)$',output,re.M))
        for line in re.findall(r'^REFERENCE\t([^\n]+)$',output,re.M):
            root,phase,created,reachable,active,lost=line.split('\t')
            self.refs.append(dict(execution=identifier,root=root,checkpoint=phase,created_instances=created,reachable_instances=reachable,active_instances=active,lost_references=lost))
        for line in re.findall(r'^AVAILABLE\t([^\n]+)$',output,re.M):
            root,phase,initiator,participants=line.split('\t')
            self.bindings.append(dict(execution=identifier,root=root,checkpoint=phase,initiator=initiator,participants=participants))
        row=dict(kind=kind,execution=identifier,fixture_id=fixture,grade=config.get('grade',80),current_time=config.get('time',0),
                 student_signature_class='invalid' if config.get('badStudent',False) else 'valid',
                 issuer_signature_class='invalid' if config.get('badIssuer',False) else 'valid',
                 decision_trace_id=identifier,transition_sequence=';'.join(fired),
                 final_classification='DEFECTIVE_GLOBAL_DEADLOCK' if any(p=='P3' for p,_ in violation) else
                    ('EXPECTED_SUCCESS_TERMINATION' if results and all(x[1]=='SUCCESS' for x in results) else 'EXPECTED_FAILURE_TERMINATION'),
                 final_outcome=';'.join(x[1] for x in results) if results else 'NOT MEASURED',
                 enabled_bindings_at_end='0' if results else 'NOT MEASURED',active_unfinished='0' if results else 'NOT MEASURED',
                 retained_references=';'.join(x[5] for x in results) if results else 'NOT MEASURED',
                 violated_properties=';'.join(p for p,_ in violation),roots=config.get('roots',1),
                 completed_workflows=len(results),runtime_ms=int(runtime.group(1))/1e6 if runtime else 'NOT MEASURED',
                 transition_firings=sum(int(x[2]) for x in results) if results else len(fired),raw_evidence=name+'.txt',config=config)
        self.runs.append(row);write_json('execution_records.json',self.runs)
        if violation:
            self.defect={'execution':identifier,'violations':violation,'evidence':name+'.txt'}
            write_json('new_defect.json',self.defect)
            raise CorrectnessStop(identifier)
        if not results or len(results)!=config.get('roots',1):raise RuntimeError('Missing native completion in '+identifier)
        print(kind,identifier,row['final_outcome'],flush=True)
        return output

    def correctness(self):
        # Each fixture's first execution discovers the actual post-consent choices.
        for number,(grade,time,bad_s,bad_i) in enumerate(itertools.product([80,40],[0,100],[False,True],[False,True]),1):
            fixture=f'f{number:02d}';config={'grade':grade,'time':time,'badStudent':bad_s,'badIssuer':bad_i}
            output=self.execution('representative',fixture+'_auto',config,fixture)
            if grade<50:continue
            choices=set(re.search(r'^DECISION_CHOICES (.*)$',output,re.M).group(1).split(','))
            already=self.runs[-1]['transition_sequence'].split(';')
            for decision,transition in DECISIONS.items():
                if transition not in choices:continue
                if transition not in already:
                    self.execution('representative',fixture+'_'+decision,{**config,'decision':decision},fixture)
                if decision=='accept':self.execution('representative',fixture+'_ledger_reject',{**config,'decision':'accept','ledgerReject':True},fixture)
        print('Representative fixtures complete; running dedicated interleaving schedules.',flush=True)
        cases=[]
        # A/O: both sides of Student wait and grade-dependent assessment/rejection.
        cases += [('A_reject_before_wait',{'grade':40}),('A_wait_before_reject',{'grade':40,'failureWait':True}),
                  ('O_wait_before_assessment_pass',{'earlyWait':True}),('O_wait_before_assessment_fail',{'grade':40,'earlyWait':True})]
        # B/C/G/H: every supported phase; successful choices remain native-discovered.
        for decision,extra in [('cancel',{}),('expire',{'time':100}),('issuer_deny',{'badIssuer':True})]:
            for phase in range(3):cases.append((f'{decision}_phase{phase}',{**extra,'phase':phase,'decision':decision}))
        cases += [('D_E_accept',{'decision':'accept'}),('D_reject',{'decision':'reject'}),
                  ('G_student_denial',{'badStudent':True,'decision':'student_deny'}),
                  ('G_cancel_instead_of_student_denial',{'badStudent':True,'decision':'cancel'}),
                  ('F_expiry_excludes_accept',{'time':100,'decision':'expire'}),
                  ('I_ledger_reject',{'decision':'accept','ledgerReject':True})]
        # J/K are negative binding checks before atomic anchor in every success.
        # L/M/N: all placements of two ordered Wallet VP steps among seven Student/HR steps.
        for generation in range(8):
            for share in range(generation,8):cases.append((f'VP_wallet_g{generation}_s{share}',{'walletGenerate':generation,'walletShare':share,'decision':'accept'}))
        for identifier,config in cases:self.execution('interleaving','inter_'+identifier,config)

    def concurrency(self):
        for roots in [1,2,5,10]:
            for run in range(1,11):self.execution('concurrency',f'roots{roots}_run{run:02d}',{'roots':roots})

    def benchmark(self):
        for scenario in SCENARIOS:
            config={}
            if scenario.startswith('grade_reject'):config['grade']=40;config['failureWait']=scenario.endswith('waiting')
            elif scenario.startswith('bad_issuer_signature'):config.update(badIssuer=True,decision='issuer_deny')
            elif scenario.startswith('cancel'):config['decision']='cancel'
            elif scenario.startswith('expire'):config.update(time=100,decision='expire')
            elif scenario=='bad_signature':config.update(badStudent=True,decision='student_deny')
            elif scenario=='reject':config['decision']='reject'
            elif scenario=='ledger_reject':config['ledgerReject']=True
            if scenario.endswith('submitted'):config['phase']=0
            elif scenario.endswith('waiting') and not scenario.startswith('grade_reject'):config['phase']=1
            for run in range(1,31):self.execution('runtime',f'bench_{scenario}_{run:02d}',config)


class CorrectnessStop(Exception):pass


def main():
    parser=argparse.ArgumentParser();parser.add_argument('--renew-home',type=Path,default=ROOT.parent/'.validation')
    parser.add_argument('--resume-baseline',action='store_true',help='Use successful baseline logs already produced in this same frozen namespace.')
    parser.add_argument('--correctness-only',action='store_true')
    parser.add_argument('--resume-measurements',action='store_true',help='Resume after the complete successful correctness phase in this namespace.')
    args=parser.parse_args();home=args.renew_home.resolve()
    if not args.resume_baseline and not args.resume_measurements:prepare(home);baseline(home)
    else:
        assert len(re.findall(r'^PASS SCENARIO ',text_read(OUT/'baseline_validation.txt'),re.M))==15
        focused=OUT/'focused_verification_wrapper_fixed.txt'
        assert 'PASS: 15 regressions, P7-A through P7-F' in text_read(focused)
    campaign=Campaign(compile_driver(home))
    if args.resume_measurements:
        campaign.runs=json.loads(text_read(OUT/'execution_records.json'))
        campaign.refs=json.loads(text_read(OUT/'reference_records.json'))
        campaign.bindings=json.loads(text_read(OUT/'binding_records.json'))
        campaign.coverage=set(json.loads(text_read(OUT/'observed_transitions.json')))
        assert len({r['fixture_id'] for r in campaign.runs if r['kind']=='representative'})==16
        assert len([r for r in campaign.runs if r['kind']=='interleaving'])==55
        assert all(not r['violated_properties'] for r in campaign.runs)
        assert not any(r['kind'] in ('runtime','concurrency') for r in campaign.runs)
    try:
        if not args.resume_measurements:campaign.correctness()
        if not args.correctness_only:campaign.concurrency();campaign.benchmark()
    except CorrectnessStop:
        print('STOP: new native correctness defect; no further execution or benchmark.',flush=True)
    finally:
        write_json('execution_records.json',campaign.runs)
        write_json('reference_records.json',campaign.refs)
        write_json('binding_records.json',campaign.bindings)
        write_json('observed_transitions.json',sorted(campaign.coverage))
        from post_p7_reports import main as generate_reports
        generate_reports()
    return 2 if campaign.defect else 0


if __name__=='__main__':sys.exit(main())
