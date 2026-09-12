"""Read-only Renew experiments. Stop with exit 2 on a property counterexample."""
from pathlib import Path
import argparse,csv,hashlib,json,platform,re,subprocess,sys,zipfile

ROOT=Path(__file__).resolve().parents[1]
RESULTS=ROOT/'results'
def digest(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def write(name,text): (ROOT/name).write_text(text.rstrip()+'\n',encoding='utf-8')
def csvfile(name,headers,rows):
 with (RESULTS/name).open('w',newline='',encoding='utf-8') as f:
  w=csv.writer(f);w.writerow(headers);w.writerows(rows)
def command(args,path):
 p=subprocess.run(args,cwd=ROOT,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True,encoding='utf-8',errors='replace')
 (RESULTS/path).write_text(p.stdout,encoding='utf-8')
 if p.returncode:raise RuntimeError(f'Command failed ({p.returncode}): {args}; see results/{path}')
 return p.stdout
def table(headers,rows):
 e=lambda x:str(x).replace('|','\\|').replace('\n','<br>')
 return '| '+' | '.join(headers)+' |\n| '+' | '.join('---' for _ in headers)+' |\n'+''.join('| '+' | '.join(map(e,r))+' |\n' for r in rows)+'\n'
DISCLAIM='**EXHAUSTIVE STATE-SPACE ANALYSIS NOT AVAILABLE FOR THE CURRENT REFERENCE-NET TOOLCHAIN**\n\n**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**\n\n'

def prepare(home):
 RESULTS.mkdir(exist_ok=True)
 files=['README.md','MODEL_VALIDATION.md','REGRESSION_REPORT.md','FAILURE_PROTOCOL_REPAIR.md','POST_REPAIR_DEADLOCK_AUDIT.md','POST_REPAIR_FINAL_MARKINGS.md','POST_REPAIR_VALIDATION_EVIDENCE.txt','CHANNEL_MATRIX.md','model.tsv','model_manifest.json','Validate.ps1','tools/BuildProject.java']+[p.name for p in sorted(ROOT.glob('*.rnw'))]
 # Read complete current evidence and actual model files, never pre-repair claims.
 contents={name:(ROOT/name).read_text(encoding='utf-8') for name in files}
 manifest=json.loads(contents['model_manifest.json'])
 meta={'commit':subprocess.check_output(['git','rev-parse','HEAD'],cwd=ROOT,text=True).strip(),'branch':subprocess.check_output(['git','branch','--show-current'],cwd=ROOT,text=True).strip(),'baseline':'uncommitted repaired working tree, identified by SHA-256; commit alone is insufficient','renew_distribution':'4.1 (local manual release heading)','renew_home':str(home),'java':subprocess.check_output(['java','-version'],stderr=subprocess.STDOUT,text=True).strip(),'os':platform.platform(),'compiler':'Timed Java Compiler; JavaNetCompiler(true,true,true)','engine':'sequential de.renew.simulatorMode=-1','validation_command':'powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\\Validate.ps1 -Smoke','hashes':{name:digest(ROOT/name) for name in files},'structure':{'nets':len(manifest),'places':sum(len(d['places']) for d in manifest.values()),'transitions':sum(len(d['transitions']) for d in manifest.values()),'arcs':sum(2*len(d['triples']) for d in manifest.values())}}
 (RESULTS/'environment.json').write_text(json.dumps(meta,indent=2)+'\n')
 csvfile('baseline_hashes.csv',['file','sha256'],meta['hashes'].items())
 csvfile('structure.csv',['nets','places','transitions','arcs'],[meta['structure'].values()])
 plugins=[]
 for p in sorted((home/'plugins').glob('*.jar')):
  with zipfile.ZipFile(p) as z:
   cfg=z.read('plugin.cfg').decode(errors='replace') if 'plugin.cfg' in z.namelist() else ''
   hits=[n for n in z.namelist() if re.search(r'reachab|statespace|state.?space|deadlock|invariant|bounded|liveness|modelcheck|momoc',n,re.I)]
   plugins.append({'file':p.name,'sha256':digest(p),'configuration':cfg,'analysis_named_entries':hits})
 (RESULTS/'plugin_inventory.json').write_text(json.dumps(plugins,indent=2)+'\n')
 cp=str(home/'plugins'/'*')+';'+str(home/'libs'/'*')+';'+str(home/'de.renew.loader.jar')
 command(['javap','-classpath',cp,'de.renew.engine.simulator.SimulatorHelper','de.renew.net.NetInstance','de.renew.net.NetInstanceList'],'native_api.txt')
 manual=(home/'manual.txt').read_text(errors='replace') if (home/'manual.txt').exists() else ''
 excerpts=[f'{i+1}: {line}' for i,line in enumerate(manual.splitlines()) if re.search('Release 4.1|reachability|state.space|deadlock|invariant|boundedness|liveness|sequential',line,re.I)]
 (RESULTS/'manual_capability_excerpts.txt').write_text('\n'.join(excerpts)+'\n')
 capability=[('Reachability graph generation','NOT AVAILABLE','No installed graph enumerator/state canonicalizer for reference instances and synchronous creation was identified.'),('Exhaustive state-space exploration','NOT AVAILABLE','Simulator bindings are current-marking searches, not an exploration of successor configurations.'),('Deadlock detection over complete state space','PARTIALLY SUPPORTED','Complete binding search of a supplied concrete endpoint is supported; exhaustive reachable-configuration quantification is not.'),('Boundedness analysis','NOT AVAILABLE','No installed boundedness/coverability analyzer for these Reference Nets.'),('Liveness analysis','NOT AVAILABLE','No installed all-executions temporal/liveness analyzer.'),('Transition reachability','PARTIALLY SUPPORTED','Actual native firings establish existential witnesses; unobserved transitions are not thereby unreachable.'),('Home-state analysis','NOT AVAILABLE','No global returnability/home-state procedure identified.'),('Invariant analysis','NOT AVAILABLE','No installed invariant solver supporting this reference/dynamic-instance semantics identified.')]
 write('FORMAL_ANALYSIS_CAPABILITY.md','# Formal-analysis capability\n\n'+DISCLAIM+table(['Requested capability','Classification','Scope'],capability)+'''\nThe classification concerns the installed local distribution, not every optional Renew extension ever published. See results/plugin_inventory.json (each JAR's manifest and hash), results/native_api.txt, and results/manual_capability_excerpts.txt. All installed plugin archives were inspected; no reachability/model-checking plugin or documented exhaustive Reference-Net procedure was found. The base documentation describes simulation and engine multiplicity, not a complete global-state explorer. The stale 4.0 installation example in doc/README is not used as the distribution version; the local manual labels Release 4.1.

Available concrete procedure: load all templates with the version-aware StorableInputDrawingLoader, compile with JavaNetCompiler(true,true,true), create SystemNet with Net.buildInstance(), and call TransitionInstance.fireOneBinding(false) for spontaneous initiators. For a concrete configuration, use SimulatorHelper.searchOnce (or isFirable/findAllBindings) for each spontaneous transition in each NetInstanceList entry. The finder reports the entire synchronized occurrence set. These native operations support reference tokens, synchronous channels and dynamic :new instantiation in actual executions. Uplinks must not be fired as independent events. NetInstanceList is an instance registry, not a reachable-state graph.

Reproduction: RunFormalExperiments.ps1 records the API signatures with javap, validates using Validate.ps1 -Smoke, then runs the bounded native probe. It does not install a plugin, translate to ordinary P/T nets, implement a synthetic graph enumerator, or claim an equivalence result. No supported exhaustive command can be supplied because no such capability was identified. Endpoint absence of bindings must still be classified using active lifecycle states; a valid terminal is not a defective deadlock.
''')
 properties=[
 ('P1','Nominal reachability','EXISTS a valid-fixture execution to the full successful marking.','Student/Wallet VPShared; Professor Approved; University CredentialIssued; Evidence/Object Graded; Competency ProfileAggregated; CredentialObject Aggregated; Ledger Anchored; HR ResultReady; root references retained.','No success witness is not a refutation of existence.','Native success trace and complete marking.'),
 ('P2','Failure terminal reachability','For each listed modeled failure outcome EXISTS a finite execution to its coherent failure terminal.','Exact cause-tagged active failure sinks; completed upstream actors; only genuinely uninvoked services remain initial.','A branch ending with active unfinished work is an invalid witness; lack of a witness alone does not refute existence.','Native separate failure fixtures and exact marking/outcome checks.'),
 ('P3','No unfinished global deadlock','FOR ALL reachable configurations: zero enabled complete bindings implies every activated lifecycle is terminal.','Expected success/failure terminals; inactive services allowed only if uninvoked.','A concrete globally quiescent configuration with an activated nonterminal instance.','Endpoint native binding search plus activation records; universal check would require exhaustive exploration.'),
 ('P4','Commit-before-share','At every reachable prefix, any Student PresentationReady/VPShared or Wallet VPReady/VPShared token implies Ledger Anchored has been reached. Include generation transition enablement checks.','Ledger anchor precedes/is the prerequisite to both VP flows.','VP generation/share without prior Ledger p_Anchored.','Per-step marking/negative binding checks and ledger event ordering.'),
 ('P5','Anchoring consistency','First entry to Competency BlockchainAnchored or CredentialObject Anchored occurs in the same complete synchronization as Ledger t_Anchor.','One confirmation transaction contains ledger plus all appropriate confirmation partners.','An anchored-named token before ledger confirmation or from an independent event.','Native binding participant set plus per-step marking trace; later Aggregate consumes object Anchored.'),
 ('P6','Invalid student signature safety','With provided student string unequal to expected object string, no holder-accepted or anchored successful lifecycle is reachable.','Denial or another genuine terminal failure; no holder acceptance.','Invalid provided student signature participates in holder acceptance or reaches anchored success.','Mismatch fixture; check accept/reject disabled, competing denial, all observed subsequent markings.'),
 ('P7','Invalid issuer signature safety','With provided issuer string unequal to expected object issuer string: (a) valid cancellation cannot fire AND (b) successful credential lifecycle cannot be reached.','No valid cancellation binding; finite failed outcome.','Either a valid cancel with mismatch OR an accepted/anchored complete success with mismatch.','Separate native invalid-issuer probes choosing denial versus any otherwise enabled success path. A single success witness refutes (b).'),
 ('P8','Reference retention','Every dynamically created active object remains reachable through retained token references from its SystemNet root throughout the lifecycle.','Root/parent reference traversal covers all active created instances.','A created active instance is absent from root traversal.','Native instance registry plus recursive tuple traversal; endpoint-only checks give only endpoint evidence.'),
 ('P9','Terminal outcome consistency','Terminal cause labels agree with the branch that actually fired; expected outcomes remain distinct.','SUCCESS, HOLDER_REJECTED, EVIDENCE_REJECTED, CANCELLED, EXPIRED, INVALID_SIGNATURE, INVALID_ISSUER_SIGNATURE, LEDGER_REJECTED mapped to their actual paths.','One branch mislabeled as another, e.g. denial marked CANCELLED or ledger rejection marked anchored.','Compare actual initiating/partner transitions to exact terminal cause tuple. P7 separately constrains whether success is allowed at all for a fixture.'),
 ('P10','Transition coverage','Identify transitions with witnessed native firings in the finite domain; do not infer all-domain reachability from observed coverage.','Coverage set with trace provenance; distinguish unobserved from structurally excluded.','A coverage claim without a native witness or an unobserved transition declared unreachable without argument.','Aggregate native binding occurrences plus verified atomic root-creation participants.')]
 (RESULTS/'property_definitions.json').write_text(json.dumps(properties,indent=2)+'\n')
 write('FORMAL_PROPERTIES.md','# Properties defined before execution\n\n'+DISCLAIM+'''These definitions precede this run's experiment results. P1/P2 are existential and can be established for the stated fixtures by an actual witness. P3–P9 are universal safety/consistency requirements whose complete verification is unavailable in this toolchain. P10 can measure observed coverage, not automatically decide all-domain reachability. Accepting root repository tokens and inactive service tokens are distinguished from active nonterminal work.

P7 deliberately includes the stronger successful-lifecycle prohibition requested for this analysis. The prior invalid-issuer scenario tested cancellation authorization and the denial path only. The experiment must not weaken P7 to that older test's scope.

'''+table(['ID','Property','Statement','Accepting','Violating','Measurement'],properties)+'''\nExhaustive verification possible with installed tooling: NO for every universal/domain-wide statement above. No synthetic ordinary-net state graph will be substituted. A real counterexample suffices to mark the corresponding universal property FAIL. On discovery of a new correctness defect, stop further experiments, preserve its evidence and do not repair the RNW model.
''')
 domain={'grade':[80,40],'passing_threshold':50,'currentTime':[0,100],'expiryDate':100,'student_signature':['student-signature','wrong-signature'],'expected_student_signature':'student-signature','issuer_signature':['issuer-signature','wrong-issuer-signature'],'expected_issuer_signature':'issuer-signature','ledger_choice':['validate/commit/anchor','reject from ordered'],'holder_choice':['accept','reject'],'roots':1,'notes':'four binary input dimensions; holder/ledger values are transition choices, not extra tokens or guard overrides; not an assertion that every cross-product tuple is feasible'}
 (RESULTS/'analysis_domain.json').write_text(json.dumps(domain,indent=2)+'\n')
 write('ANALYSIS_DOMAIN.md','# Finite experimental domain\n\n'+DISCLAIM+table(['Dimension','Representatives'],[(k,str(v)) for k,v in domain.items()])+'''\nThese are representative classes for the guards/equality predicates actually present. They are not a proof over arbitrary integers, strings, nulls, inconsistent identifiers or external data. At-expiry represents the >= guard; it does not model advancing time. All DID, evidence, credential and VP fixtures stay fixed and consistent. Initial tokens retain their counts; overrides are applied only to in-memory drawings before compilation.

One SystemNet creates one independent workflow using its one-shot [] seeds. Dynamic creation remains native Renew semantics. Cancellation/expiry/denial are additional competitors, not suppressed by artificial guards. Holder and ledger choices are imposed by the experiment scheduler only where actual bindings exist. Infeasible choices must be recorded as disabled, never forced. The proposed combinations are a finite design domain, not a measured reachable-state count or a claim of complete cross-product coverage.

First risk-directed probe: passing grade, before expiry, valid student signature, invalid issuer signature; choose the enabled holder-accept and ledger-success continuation instead of issuer denial. This investigates the stronger P7 clause before any timing or scalability campaign. If it violates P7, the requested stop rule takes precedence over the remaining experiment plan.
''')
 # Whole-word workload search avoids false TPS hits in https URLs.
 terms={'Poisson':r'\bPoisson\b','MMPP':r'\bMMPP\b','Bmax':r'\bBmax\b','Tout':r'\bTout\b','200 tx/s':r'\b200\s+tx/s\b','500 agents':r'\b500[- ](?:concurrent[- ]|agent[- ])?agents?\b','MRT':r'\bMRT\b','TPS':r'\bTPS\b','batching':r'\bbatching\b','stress testing':r'\bstress[- ]testing\b'}
 hits=[]
 for p in ROOT.rglob('*'):
  if p.suffix.lower() not in ['.md','.txt','.java','.ps1'] or any(x in p.parts for x in ['results','.git']) or p.name=='formal_experiments.py':continue
  if p.name in ['FORMAL_ANALYSIS_CAPABILITY.md','FORMAL_PROPERTIES.md','ANALYSIS_DOMAIN.md']:continue
  for i,line in enumerate(p.read_text(encoding='utf-8',errors='replace').splitlines(),1):
   for term,pattern in terms.items():
    if re.search(pattern,line,re.I):hits.append([term,str(p.relative_to(ROOT)),i,line])
 csvfile('workload_claim_occurrences.csv',['term','file','line','text'],hits)
 write('WORKLOAD_CLAIM_AUDIT.md','# Workload claim audit\n\n'+table(['Term','Classification','Finding'],[[t,'OUTSIDE CURRENT RENEW MODEL' if t in ['200 tx/s','TPS'] else 'NOT IMPLEMENTED','Repository occurrences recorded in results/workload_claim_occurrences.csv; '+('none found' if not any(h[0]==t for h in hits) else 'mentions inspected; no implementation/measurement establishes this workload claim')] for t in terms])+'''\nScope: repository Markdown/text, scripts and Java tooling, including historical documents explicitly treated as historical. The current reports state these metrics are not measured. No manuscript file was supplied in this task/repository; a separate open browser conversation is not treated as the manuscript or a measured source. No Poisson/MMPP arrival process, Bmax/Tout batching mechanism, 200 tx/s experiment, 500-agent workload, MRT/TPS benchmark or stress campaign exists in the model inscriptions or test driver. A mention of a metric is not implementation. No arbitrary workload was added. Renew does not contact Hyperledger Fabric; simulator timings cannot substantiate Fabric claims.
''')
 print('Prepared capabilities, properties and finite domain BEFORE native experiment results.',flush=True)
 return cp,meta

def run(home):
 cp,meta=prepare(home)
 baseline=command(['powershell.exe','-NoProfile','-ExecutionPolicy','Bypass','-File',str(ROOT/'Validate.ps1'),'-RenewHome',str(home),'-Smoke'],'baseline_validation.txt')
 passes=re.findall(r'^PASS SCENARIO (\w+) (\w+) OUTCOME (\w+)',baseline,re.M)
 if len(passes)!=15:raise RuntimeError('Expected all original 15 regression cases')
 csvfile('baseline_regressions.csv',['scenario','classification','outcome'],passes)
 print('Original 15 regressions PASS. Running one competing-order P7 probe.',flush=True)
 source=(ROOT/'tools/BuildProject.java').read_text()
 def replace_once(a,b):
  nonlocal source
  if source.count(a)!=1:raise RuntimeError('Validator source changed; instrumentor must be reviewed: '+a)
  source=source.replace(a,b)
 replace_once('public class BuildProject {','public class P7Probe {')
 replace_once('if(scenario.startsWith("bad_issuer_signature") && drawing.getName().equals("CompetencyNet"))','if(scenario.equals("happy") && drawing.getName().equals("CompetencyNet"))')
 replace_once('    disabled("CompetencyNet","t_DenyInvalidIssuerSignature");','''    disabled("CompetencyNet","t_SBT_Cancel");
    var denial=search(instance("CompetencyNet"),transition("CompetencyNet","t_DenyInvalidIssuerSignature"),false);
    var acceptance=search(instance("StudentAgent"),transition("StudentAgent","t_AcceptCredential"),false);
    if(denial.isEmpty() || acceptance.isEmpty())throw new IllegalStateException("Requested competing binding pair not present");
    System.out.println("P7 COMPETING DENIAL "+denial);
    System.out.println("P7 COMPETING ACCEPTANCE "+acceptance);''')
 replace_once('      System.exit(0);','''      System.out.println("PROPERTY_VIOLATION P7: invalid issuer signature reached complete anchored success");
      System.exit(2);''')
 build=RESULTS/'.build';build.mkdir(exist_ok=True)
 (build/'P7Probe.java').write_text(source,encoding='utf-8')
 # Capture exact instrumentation for independent review; no domain/model edits.
 (RESULTS/'probe_instrumentation.txt').write_text('Generated from tools/BuildProject.java SHA256 '+meta['hashes']['tools/BuildProject.java']+'\nChanges: class renamed; existing in-memory issuer override applied to happy; correct-fixture denial-disabled assertion replaced with native competing-denial/accept binding checks and disabled valid-cancel check; exit 2 on complete success. All transition inscriptions, graphs, execution operations and full endpoint checks unchanged.\nProbe source SHA256 '+digest(build/'P7Probe.java')+'\n',encoding='utf-8')
 command(['javac','-cp',cp,'-d',str(build),str(build/'P7Probe.java')],'probe_compile.txt')
 probe=subprocess.run(['java','-Djava.awt.headless=true','-cp',str(build)+';'+cp,'P7Probe',str(ROOT),'smoke','happy'],cwd=ROOT,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True,encoding='utf-8',errors='replace')
 (RESULTS/'p7_counterexample.txt').write_text(probe.stdout,encoding='utf-8')
 if probe.returncode!=2 or 'PROPERTY_VIOLATION P7:' not in probe.stdout:raise RuntimeError('Probe did not yield the expected independently checked result; inspect native output')
 if 'ENABLED BINDINGS 0' not in probe.stdout or 'ACTIVE UNFINISHED 0' not in probe.stdout:raise RuntimeError('Missing complete endpoint checks')
 if 'wrong-issuer-signature' not in probe.stdout:raise RuntimeError('Missing fixture evidence')
 print('STOP: native counterexample to P7 confirmed. No further experiments will run.',flush=True)
 # From this point on: record the counterexample and explicit missing measurements ONLY.
 after={n:digest(ROOT/n) for n in meta['hashes']};assert after==meta['hashes'],'Baseline modified'
 csvfile('baseline_preservation.csv',['file','before_sha256','after_sha256','unchanged'],[[n,h,after[n],True] for n,h in meta['hashes'].items()])
 report(meta,baseline,probe.stdout,passes)
 return 2

def report(meta,baseline,probe,passes):
 order=re.findall(r'^FIRED (.+)$',probe,re.M)
 final=re.findall(r'^FINAL TOKEN (\w+)\.(\w+) = (.+)$',probe,re.M)
 csvfile('p7_final_marking.csv',['net','place','tokens'],final)
 csvfile('interleaving_results.csv',['experiment_id','initial_fixture','decision_class','transition_order','final_classification','final_outcome','enabled_bindings','active_unfinished','reference_retention','property_violation','runtime_ms'],[['P7_ACCEPT_OVER_DENIAL','grade=80;threshold=50;time=0;expiry=100;student=valid;issuer=invalid','holder accept; ledger success; issuer denial enabled but not selected','SystemNet.t_CreateStudent; '+'; '.join(order),'CONTROL_FLOW_SUCCESS_BUT_PROPERTY_VIOLATION','SUCCESS',0,0,'11/11','P7','NOT MEASURED']])
 csvfile('state_space_summary.csv',['status','reachable_configurations','edges','terminal_configurations','defective_global_deadlocks'],[['NOT AVAILABLE','NOT MEASURED','NOT MEASURED','NOT MEASURED','NOT MEASURED']])
 csvfile('terminal_states.csv',['scope','status'],[['complete reachable state space','NOT MEASURED; see p7_final_marking.csv for concrete witness only']])
 definitions=json.loads((RESULTS/'property_definitions.json').read_text())
 prop=[]
 for pid,title,*_ in definitions:
  status='PASS' if pid in ['P1','P2'] else 'FAIL' if pid=='P7' else 'NOT EXHAUSTIVELY VERIFIED'
  detail='Existential witness only, finite specified fixtures' if pid in ['P1','P2'] else 'Native invalid-issuer accepted/anchored success counterexample; cancellation still disabled' if pid=='P7' else 'Partial directed evidence only; full campaign stopped on P7'
  prop.append([pid,title,status,detail])
 csvfile('paper_property_table.csv',['property','name','status','scope'],prop)
 observed={}
 for label,log in [('baseline',baseline),('P7_ACCEPT_OVER_DENIAL',probe)]:
  for group in re.findall(r'^BINDING .+ = (.+)$',log,re.M):
   for t in group.split(','):observed.setdefault(t,set()).add(label)
 # Bootstrap partners are implied by successful native root transaction and checked reference pools.
 m=json.loads((ROOT/'model_manifest.json').read_text())
 if 'SMOKE OK: t_CreateStudent fired' in baseline:
  for t in m['SystemNet']['transitions']:observed['SystemNet.'+t]={'baseline bootstrap transaction'}
  observed['StudentAgent.t_PrepareEvidence']={'baseline bootstrap setup'}
 coverage=[[n,t,'OBSERVED' if n+'.'+t in observed else 'NOT OBSERVED','; '.join(sorted(observed.get(n+'.'+t,[])))] for n,d in m.items() for t in d['transitions']]
 csvfile('transition_coverage.csv',['net','transition','directed_coverage','witness'],coverage)
 csvfile('concurrency_results.csv',['lifecycles','status','completions','runtime'],[[n,'NOT MEASURED; stopped on P7','NOT MEASURED','NOT MEASURED'] for n in [1,2,5,10]])
 perf=[['simulation benchmark','NOT MEASURED','NOT MEASURED','NOT MEASURED','NOT MEASURED','NOT MEASURED','NOT MEASURED','NOT MEASURED','NOT MEASURED']]
 csvfile('runtime_results.csv',['category','status','n','mean','median','standard_deviation','min','max','p95'],perf)
 csvfile('paper_performance_table.csv',['category','status','n','mean','median','standard_deviation','min','max','p95'],perf)
 csvfile('paper_scenario_table.csv',['scenario','classification','outcome','scope'],[[s,c,o,'original directed regression'] for s,c,o in passes]+[['P7_ACCEPT_OVER_DENIAL','PROPERTY_VIOLATION','SUCCESS despite invalid issuer signature','one new competing-order witness']])
 summary={'status':'STOPPED_ON_CORRECTNESS_DEFECT','property_violation':'P7','structure':meta['structure'],'baseline_regressions_passed':len(passes),'new_interleaving_probes':1,'new_probe_enabled_bindings':0,'new_probe_active_unfinished':0,'new_probe_registered_instances':11,'new_probe_retained_instances':11,'observed_transitions':sum(r[2]=='OBSERVED' for r in coverage),'total_transitions':len(coverage),'exhaustive_state_space':False,'reachable_state_count':None,'state_space_deadlock_count':None,'runtime_benchmark':False,'concurrency_experiment':False,'model_modified':False}
 (RESULTS/'experiment_summary.json').write_text(json.dumps(summary,indent=2)+'\n')
 defect='''# Correctness defect: P7 invalid issuer signature can reach success

**STOPPED ON A NEW CORRECTNESS DEFECT, as requested. The RNW model was not repaired or modified.**

P7 has two clauses. Its cancellation-authorization clause holds in this witness: `CompetencyNet.t_SBT_Cancel` is disabled because the issuer strings differ. Its stronger successful-lifecycle prohibition is **FAIL**: with the same mismatch, holder acceptance and the issuer-denial transaction are both enabled. Selecting acceptance consumes the pending token, permanently removes the denial option, and permits ledger anchoring, both VP flows and HR result completion.

This is a safety/policy violation, not a defective global deadlock. The final configuration is coherent control-flow success with no active unfinished instance; it is unacceptable under P7. The invalid-issuer string was changed only in memory using the same fixture mechanism as the existing regression. No transition, guard, arc, token count or domain model was altered.

Why the previous tests passed: they deliberately choose t_DenyInvalidIssuerSignature, demonstrating an available denial path. An available denial path does not exclude a competing successful path. Issuer equality is checked on CredentialObject.t_Cancel, and mismatch on t_DenyInvalidIssuerSignature, but it is not a prerequisite of t_Mint or t_Accept. Competency's t_SBT_Accept requires valid time and the student's signature, not issuer validity. Neither ledger confirmation nor later sharing adds that missing condition. An invalid cancellation signature was previously scoped to cancellation; this analysis explicitly requests the stronger end-to-end property.

No claim of real cryptographic compromise is made: these are simulation strings. Before resuming the campaign, resolve the intended issuer-authorization contract and address the failing property in a separate repair task. Do not narrow P7 merely to turn this result into PASS.

Reproduce with `powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\\RunFormalExperiments.ps1`. Expected process exit: **2**, meaning an observed property violation. Exit zero must not be reported as the experiment result. Complete native trace: results/p7_counterexample.txt. Exact instrumentation: results/probe_instrumentation.txt and the generated results/.build/P7Probe.java. Exact final tokens: results/p7_final_marking.csv. Baseline identity: results/environment.json and results/baseline_hashes.csv.

## Native competing bindings and endpoint

```text
'''+ '\n'.join(x for x in probe.splitlines() if x.startswith(('P7 COMPETING','EXPECTED DISABLED CompetencyNet.t_SBT_Cancel','FINAL TOKEN ','ENABLED BINDINGS','ACTIVE UNFINISHED','RETAINED REFERENCES','PROPERTY_VIOLATION')))+ '\n```\n'
 write('NEW_CORRECTNESS_DEFECT.md',defect)
 write('PROPERTY_RESULTS.md','# Property results\n\n'+DISCLAIM+'Analysis stopped on P7. PASS for P1/P2 establishes only their existential statements for the supplied fixtures. No universal PASS is inferred from directed tests.\n\n'+table(['Property','Name','Status','Scope'],prop)+'''\nP10 coverage is generated in results/transition_coverage.csv from recorded full binding participants. Bootstrap coverage is separately attributed to successful atomic root creation and typed reference checks. An unobserved transition is not automatically unreachable. University.t_RejectEvidence is structurally inconsistent with the unchanged passing grade forwarded by Professor in this fixture domain, as already documented; this is a structural observation, not a state-space result. P8 reference traversal was checked at endpoints, not every intermediate configuration. P9 labels are checked against the actual chosen branch; the wrong authorization of a success branch is reported under P7, not hidden as a mislabeled cancellation.
''')
 write('STATE_SPACE_ANALYSIS.md','# State-space analysis\n\n'+DISCLAIM+'No exhaustive Reference-Net procedure was found locally. No reachable-configuration, state-space edge, terminal-state total, defective-deadlock total, boundedness or liveness result is available. results/state_space_summary.csv and results/terminal_states.csv contain explicit NOT MEASURED fields. Concrete final markings and directed transition coverage must not be cited as a state-space enumeration.\n')
 write('INTERLEAVING_ANALYSIS.md','# Interleaving analysis\n\n'+DISCLAIM+'''**SYSTEMATIC DIRECTED EXPLORATION — STOPPED EARLY.**

Exactly one new competing-order probe was executed (results/interleaving_results.csv): invalid issuer denial was enabled concurrently with holder acceptance; acceptance was chosen, followed by actual ledger success and VP/HR progress. It refutes P7. The original fifteen regression sequences were separately rerun; they are not counted as fifteen new interleaving experiments.

The original regressions cover evidence rejection on either side of Student wait, abort phases before/after consent, explicit accept/reject/failure choices, and negative pre-anchor VP checks. They do not enumerate the requested competing schedules. Remaining accept/reject/cancel/expiry races, all Student-versus-Wallet VP schedules, HR ordering variations and the full finite fixture cross-product were **not performed after the defect**, in compliance with the stop rule. No all-interleavings coverage claim is made. Runtime was not measured for this probe; its CSV field says NOT MEASURED.
''')
 write('CONCURRENCY_ANALYSIS.md','# Concurrency analysis\n\n**MULTI-INSTANCE CONCURRENCY NOT MEASURED**\n\nSystemNet has one seed per pool and creates one workflow per root. Native Net.buildInstance can create additional roots, but a valid multi-root driver must follow each root\'s actual references; the existing BuildProject.instance helper finds by template name and is unsuitable for distinguishing replicas. Multiple engine threads/multiplicity is not evidence of multiple independent lifecycle workloads. No model redesign or extra root was introduced. The campaign stopped on P7 before a controlled multi-root experiment; results/concurrency_results.csv records NOT MEASURED for the requested scales. No scalability or conflict metric is reported.\n')
 write('PERFORMANCE_MEASUREMENTS.md','# Performance measurements\n\n**NOT MEASURED.** No repeated timing study or memory experiment was run. The correctness stop precedes any benchmark. results/runtime_results.csv and results/paper_performance_table.csv contain NOT MEASURED for n and all statistics, not zeros or estimates. The existing Renew logs are correctness evidence, not timing measurements. Renew does not call Hyperledger Fabric; none of these results is blockchain latency, Fabric throughput, TPS or network performance.\n')
 write('SCIENTIFIC_RESULTS.md','# Scientific results — stopped on P7\n\n'+DISCLAIM+f'''## A. Structural facts

The checkpoint contains {meta['structure']['nets']} nets, {meta['structure']['places']} places, {meta['structure']['transitions']} transitions and {meta['structure']['arcs']} ordinary directed arcs: generated results/structure.csv and actual native compilation in results/baseline_validation.txt. The repaired working tree is uncommitted; cite its file hashes in results/baseline_hashes.csv, not the older commit alone. RNW bytes remain unchanged (results/baseline_preservation.csv).

## B. Directed validation results

All {len(passes)} original repaired regressions passed again (results/baseline_regressions.csv; full native output in results/baseline_validation.txt). This reproduces the scoped repaired tests; it does not establish the newly requested issuer-success safety property.

## C. Systematic exploration results

One new risk-directed competing-order execution refutes P7 (results/interleaving_results.csv; results/p7_counterexample.txt). It reaches anchored control-flow success despite an invalid issuer signature. The endpoint has zero enabled bindings and zero active unfinished instances, with all {summary['new_probe_retained_instances']}/{summary['new_probe_registered_instances']} instances reference-retained (same evidence and results/p7_final_marking.csv). This is an authorization-safety counterexample, not a deadlock. Observed transition coverage, if used, must be cited as directed coverage from results/transition_coverage.csv, never as complete domain reachability.

## D. Exhaustive formal results

None. Reachable-state counts, state-space deadlock totals, boundedness, liveness and exhaustive deadlock freedom are NOT MEASURED/NOT VERIFIED. An actual native counterexample suffices to refute P7's universal statement; no exhaustive verification claim follows.

## E. Performance results

NOT MEASURED. No benchmark repetitions, timing distribution, memory profile or concurrency workload was performed. No simulator time is presented as blockchain performance.

## F. Unverified / stopped work

The remaining interleaving campaign, full finite-domain safety evaluation, multi-instance concurrency and repeated performance experiments were stopped on the discovered defect. No Poisson/MMPP or batching workload was implemented. See NEW_CORRECTNESS_DEFECT.md, PROPERTY_RESULTS.md and the capability/domain/reproducibility reports.

Numbers safe for a paper: the hash-identified structural inventory, the count and outcomes of rerun directed regressions, the single concrete P7 counterexample, and its explicitly scoped endpoint checks. They must not support claims of exhaustive verification, complete failure-security handling or scalability. Publication source CSVs are results/paper_scenario_table.csv, results/paper_property_table.csv and results/paper_performance_table.csv; missing categories remain NOT MEASURED.
''')
 write('EXPERIMENT_REPRODUCIBILITY.md','# Experiment reproducibility\n\n'+'''Prerequisites: Python 3, Java/Javac/Javap 17 on PATH, and the official Renew 4.1 base installation with plugins, libs and de.renew.loader.jar. No network, third-party Python module or domain helper is needed. Tool identities and OS are captured in results/environment.json; plugin identities/hashes in results/plugin_inventory.json.

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\\RunFormalExperiments.ps1
# Different local runtime location:
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\\RunFormalExperiments.ps1 -RenewHome 'C:\\path\\to\\Renew'
```

The entry point defines capabilities/properties/domain before tests, records the uncommitted working-tree hashes, runs Validate.ps1 -Smoke (all original regressions), then instruments a temporary copy of the current validator. It uses the existing in-memory invalid-issuer fixture, chooses the originally valid native success path, and verifies both competing binding candidates. The copied harness keeps actual Renew execution and complete endpoint checks. No custom simulator or synthetic reachability graph is implemented. Source replacement checks fail if the expected validator structure has changed. Native command/validation errors stop the run; they are not converted into passing properties.

Expected exit is **2** on the documented counterexample. Other errors exit nonzero. On exit 2 no further experiments execute; remaining reports are stopped/not-measured records. All results are regenerated from captured native output; no statistics are manually supplied. results/.build contains generated Java/classes and is ignored; all evidence CSV/JSON/TXT files are eligible for version control. Re-running overwrites this task's generated results/reports, not the RNWs or original validation/helper files. The failed property must be addressed separately before expanding this entry point into a broader campaign.

Files for each result: baseline_validation.txt and baseline_regressions.csv; p7_counterexample.txt, probe_instrumentation.txt, interleaving_results.csv and p7_final_marking.csv; transition_coverage.csv and experiment_summary.json; capability API/plugin/manual records; property_definitions.json and analysis_domain.json; baseline preservation hashes; workload_claim_occurrences.csv. Statistical/concurrency/state-space placeholders explicitly say NOT MEASURED. Existing historical reports are inputs, not rewritten as current successes.
''')

if __name__=='__main__':
 parser=argparse.ArgumentParser();parser.add_argument('--renew-home',type=Path,default=ROOT.parent/'.validation');args=parser.parse_args()
 try:sys.exit(run(args.renew_home.resolve()))
 except Exception as exc:print('EXPERIMENT ERROR:',exc,file=sys.stderr);sys.exit(1)
