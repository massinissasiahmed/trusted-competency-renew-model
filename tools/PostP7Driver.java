import java.util.*;
import de.renew.net.*;
import de.renew.shadow.ShadowLookup;

/** Native scheduling and assertions only. No model semantics are implemented here. */
public class PostP7Driver extends PostP7Base {
  static final Properties cfg=System.getProperties();
  static final IdentityHashMap<NetInstance,Context> owners=new IdentityHashMap<>();
  static final List<Context> contexts=new ArrayList<>();
  static Context current;
  static class Context {
    NetInstance root; Set<String> activeNames=new TreeSet<>(); boolean anchored;
    int firings; String outcome="", decision=""; int index;
    Context(NetInstance root){this.root=root;}
  }
  static class Violation extends RuntimeException {Violation(String message){super(message);}}
  static void violation(String property,String detail){
    System.out.println("PROPERTY_VIOLATION "+property+" "+detail);markings("VIOLATION");
    throw new Violation(property+" "+detail);
  }
  static NetInstance[] instances(){
    if(current==null)return NetInstanceList.getAll();
    return Arrays.stream(NetInstanceList.getAll()).filter(n->owners.get(n)==current).toArray(NetInstance[]::new);
  }
  static void select(Context c){current=c;PostP7Base.active=c.activeNames;PostP7Base.confirmed=c.anchored;}
  static String key(NetInstance n,Transition t){return n.getNet().getName()+"."+t.getName();}
  static void assignNew(){for(var n:NetInstanceList.getAll())if(!owners.containsKey(n))owners.put(n,current);}
  static Set<NetInstance> references(){var found=Collections.newSetFromMap(new IdentityHashMap<NetInstance,Boolean>());walk(current.root,found);return found;}
  static void retention(String checkpoint){
    var found=references();int lost=0;
    for(var n:instances())if(!found.contains(n))lost++;
    for(var n:found)if(owners.get(n)!=current)violation("P8","cross-root reference "+n);
    System.out.println("REFERENCE\t"+contexts.indexOf(current)+"\t"+checkpoint+"\t"+instances().length+"\t"+found.size()+"\t"+active.size()+"\t"+lost);
    if(lost!=0)violation("P8","lost created reference at "+checkpoint);
  }
  static SortedSet<String> enumerate(String phase){
    var available=new TreeSet<String>();int[] count={0};
    for(var n:instances())for(var t:n.getNet().transitions())if(t.isSpontaneous()){
      var ti=n.getInstance(t);
      var finder=new de.renew.engine.searcher.Finder(){
        public boolean isCompleted(){return false;}
        public void found(de.renew.engine.searcher.Searcher s){
          var parts=new ArrayList<String>();
          for(var occurrence:s.getOccurrences()){
            var tr=occurrence.getTransition();var target=tr.getNetInstance();String p=key(target,tr.getTransition());parts.add(p);
            if(owners.containsKey(target)&&owners.get(target)!=current)violation("P8","cross-root synchronization "+p);
            boolean successful=p.endsWith(".t_AcceptCredential")||p.equals("CredentialObject.t_Accept")||p.equals("CompetencyNet.t_SBT_Accept")||p.startsWith("HEDULedgerNet.")||p.endsWith(".t_GenerateVP")||p.endsWith(".t_ShareVP")||p.equals("HRAgent.t_GenerateResult");
            if(successful&&Boolean.getBoolean("post.badStudent"))violation("P6","successful complete binding "+p);
            if((successful||p.equals("CompetencyNet.t_SBT_Cancel"))&&Boolean.getBoolean("post.badIssuer"))violation("P7","unauthorized complete binding "+p);
            if(!confirmed&&(p.endsWith(".t_GenerateVP")||p.endsWith(".t_ShareVP")))violation("P4","pre-anchor VP binding "+p);
          }
          Collections.sort(parts);available.add(key(n,t));count[0]++;
          System.out.println("AVAILABLE\t"+contexts.indexOf(current)+"\t"+phase+"\t"+key(n,t)+"\t"+String.join(",",parts));
        }
      };
      de.renew.engine.simulator.SimulatorHelper.searchOnce(new de.renew.engine.searcher.Searcher(),finder,ti,ti);
    }
    System.out.println("ENUMERATED\t"+contexts.indexOf(current)+"\t"+phase+"\t"+count[0]);return available;
  }
  static void step(String full){
    var available=enumerate("before:"+full);
    if(!available.contains(full))throw new IllegalStateException("INFEASIBLE SCHEDULE "+full+" available="+available);
    String[] part=full.split("\\.");
    if(full.equals("HEDULedgerNet.t_Anchor")){
      var required=Set.of("HEDULedgerNet.t_Anchor","CompetencyNet.t_AnchorConfirmed","CredentialObject.t_AnchorConfirmed","StudentAgent.t_AnchorConfirmed","WalletNet.t_AnchorConfirmed");
      for(String row:search(instance(part[0]),transition(part[0],part[1]),false))if(!new HashSet<>(Arrays.asList(row.split(","))).containsAll(required))violation("P5","incomplete confirmation partners "+row);
    }
    fire(part[0],part[1]);current.firings++;current.anchored=confirmed;assignNew();retention("after:"+full);
    if(full.equals("HEDULedgerNet.t_Anchor")){
      terminal(instance("CompetencyNet"),"p_BlockchainAnchored");terminal(instance("CredentialObject"),"p_Anchored");
      System.out.println("ANCHOR_ATOMIC PASS");
    }
  }
  static void endpoint(){
    var enabled=enumerate("terminal");if(!enabled.isEmpty())throw new IllegalStateException("Scheduler left enabled work: "+enabled);
    String scenario=switch(current.outcome){
      case "SUCCESS"->"happy";case "EVIDENCE_REJECTED"->"grade_reject";case "CANCELLED"->"cancel";case "EXPIRED"->"expire";
      case "LEDGER_REJECTED"->"ledger_reject";case "INVALID_SIGNATURE"->"bad_signature";case "INVALID_ISSUER_SIGNATURE"->"bad_issuer_signature";default->"reject";
    };
    try{finish(scenario,current.outcome);}catch(IllegalStateException e){
      if(e.getMessage().startsWith("Wrong failure outcome"))violation("P9",e.getMessage());
      else if(e.getMessage().contains("Orphan")||e.getMessage().contains("reference"))violation("P8",e.getMessage());
      else violation("P3",e.getMessage());
    }
    retention("terminal");System.out.println("RESULT\t"+contexts.indexOf(current)+"\t"+current.outcome+"\t"+current.firings+"\t0\t0\t"+references().size()+"/"+instances().length);
  }
  static void create(ShadowLookup lookup){
    var root=lookup.getNet("SystemNet").buildInstance();var c=new Context(root);contexts.add(c);select(c);assignNew();
    var t=root.getNet().transitions().stream().filter(x->x.getName().equals("t_CreateStudent")).findFirst().orElseThrow();
    if(!root.getInstance(t).fireOneBinding(false))throw new IllegalStateException("Creation disabled");
    assignNew();active.add("StudentAgent");current.firings++;
    for(String name:List.of("t_CreateStudent","t_CreateProfessor","t_CreateUniversity","t_CreateEvidence","t_CreateCompetency","t_CreateWallet","t_CreateLedger","t_CreateHR"))System.out.println("BOOTSTRAP OBSERVED SystemNet."+name);
    System.out.println("BOOTSTRAP OBSERVED StudentAgent.t_PrepareEvidence");
    if(instances().length!=9)throw new IllegalStateException("Unexpected bootstrap instances");
    retention("creation");markings("CREATED");
  }
  static final List<String> EARLY=List.of("StudentAgent.t_SubmitEvidence","EvidenceNet.t_LMS_Submit","EvidenceNet.t_Review_Request","ProfessorAgent.t_StartReview","ProfessorAgent.t_AssessEvidence");
  static final List<String> MINT=List.of("ProfessorAgent.t_ApproveEvidence","UniversityAgent.t_VerifyEvidence","UniversityAgent.t_RequestCredential","CompetencyNet.t_Verification_Pass","UniversityAgent.t_IssueCredential");
  static final List<String> VP=List.of("StudentAgent.t_GenerateVP","StudentAgent.t_ShareVP","HRAgent.t_ParseVP","CompetencyNet.t_Agg_Ingest","HRAgent.t_SemanticMatch","HRAgent.t_GapAnalysis","HRAgent.t_GenerateResult");
  static List<String> successTail(){
    var steps=new ArrayList<String>(List.of("HEDULedgerNet.t_Order","HEDULedgerNet.t_Validate","HEDULedgerNet.t_Commit","HEDULedgerNet.t_Anchor"));
    int g=Integer.getInteger("post.walletGenerate",7),s=Integer.getInteger("post.walletShare",7);
    for(int i=0;i<=VP.size();i++){
      if(i==g)steps.add("WalletNet.t_GenerateVP");if(i==s)steps.add("WalletNet.t_ShareVP");if(i<VP.size())steps.add(VP.get(i));
    }return steps;
  }
  static void single(){
    boolean early=Boolean.getBoolean("post.earlyWait");
    for(String s:EARLY){step(s);if(early&&s.equals("StudentAgent.t_SubmitEvidence"))step("StudentAgent.t_WaitCredential");}
    if(Integer.getInteger("post.grade",80)<50){
      if(!early&&Boolean.getBoolean("post.failureWait"))step("StudentAgent.t_WaitCredential");
      step("ProfessorAgent.t_RejectEvidence");current.outcome="EVIDENCE_REJECTED";endpoint();return;
    }
    for(String s:MINT)step(s);
    int phase=Integer.getInteger("post.phase",2);
    if(phase>=1&&!early)step("StudentAgent.t_WaitCredential");if(phase>=2)step("StudentAgent.t_ReceiveCredential");
    var choices=enumerate("decision");System.out.println("DECISION_CHOICES "+String.join(",",choices));
    var decisions=new LinkedHashMap<String,String>();
    decisions.put("accept","StudentAgent.t_AcceptCredential");decisions.put("reject","StudentAgent.t_RejectCredential");decisions.put("student_deny","StudentAgent.t_DenyInvalidSignature");decisions.put("issuer_deny","CompetencyNet.t_DenyInvalidIssuerSignature");decisions.put("expire","CompetencyNet.t_SBT_Expire");decisions.put("cancel","CompetencyNet.t_SBT_Cancel");
    String selected=cfg.getProperty("post.decision","auto");
    if(selected.equals("auto"))selected=decisions.entrySet().stream().filter(e->choices.contains(e.getValue())).findFirst().orElseThrow().getKey();
    current.decision=selected;step(decisions.get(selected));
    current.outcome=switch(selected){case "accept"->"SUCCESS";case "reject"->"HOLDER_REJECTED";case "cancel"->"CANCELLED";case "expire"->"EXPIRED";case "student_deny"->"INVALID_SIGNATURE";default->"INVALID_ISSUER_SIGNATURE";};
    if(selected.equals("accept")){
      if(Boolean.getBoolean("post.ledgerReject")){step("HEDULedgerNet.t_Order");step("HEDULedgerNet.t_Reject");current.outcome="LEDGER_REJECTED";}
      else for(String s:successTail())step(s);
    }endpoint();
  }
  static void run(ShadowLookup lookup){
    long started=System.nanoTime();int roots=Integer.getInteger("post.roots",1);
    for(int i=0;i<roots;i++)create(lookup);
    if(roots==1)single();else{
      var sequence=new ArrayList<String>();sequence.addAll(EARLY);sequence.addAll(MINT);sequence.addAll(List.of("StudentAgent.t_WaitCredential","StudentAgent.t_ReceiveCredential","StudentAgent.t_AcceptCredential"));sequence.addAll(successTail());
      for(String s:sequence)for(var c:contexts){select(c);step(s);}
      for(var c:contexts){select(c);c.outcome="SUCCESS";endpoint();}
    }
    long elapsed=System.nanoTime()-started;System.out.println("RUNTIME_NS "+elapsed);System.out.println("ROOTS_COMPLETED "+contexts.size());
  }
}
