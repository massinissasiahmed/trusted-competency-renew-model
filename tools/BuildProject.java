import java.io.*;
import java.nio.file.*;
import java.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.*;
import de.renew.gui.*;
import de.renew.shadow.*;
import de.renew.formalism.java.JavaNetCompiler;

/** Read-only validation and scenario tooling; never referenced by the model. */
public class BuildProject {
  static de.renew.net.NetInstance instance(String name) {
    return Arrays.stream(de.renew.net.NetInstanceList.getAll()).filter(n->n.getNet().getName().equals(name)).findFirst().orElseThrow();
  }
  static void terminal(de.renew.net.NetInstance instance,String name) {
    var p=instance.getNet().places().stream().filter(x->x.getName().equals(name)).findFirst().orElseThrow();
    if(instance.getInstance(p).getNumberOfTokens()!=1) throw new IllegalStateException("Missing marking "+instance.getNet().getName()+"."+name);
    System.out.println("TERMINAL OK "+instance.getNet().getName()+"."+name);
  }

  static Set<String> active=new TreeSet<>();
  static boolean confirmed=false;
  static List<String> search(de.renew.net.NetInstance n,de.renew.net.Transition t,boolean activate) {
    var rows=new TreeSet<String>();var ti=n.getInstance(t);
    var finder=new de.renew.engine.searcher.Finder(){
      public boolean isCompleted(){return false;}
      public void found(de.renew.engine.searcher.Searcher searcher){
        var participants=new TreeSet<String>();
        for(var o:searcher.getOccurrences()) {var tr=o.getTransition();String net=tr.getNetInstance().getNet().getName();participants.add(net+"."+tr.getTransition().getName());if(activate)active.add(net);}
        rows.add(String.join(",",participants));
      }
    };
    de.renew.engine.simulator.SimulatorHelper.searchOnce(new de.renew.engine.searcher.Searcher(),finder,ti,ti);
    return new ArrayList<>(rows);
  }
  static de.renew.net.Transition transition(String net,String name){return instance(net).getNet().transitions().stream().filter(t->t.getName().equals(name)).findFirst().orElseThrow();}
  static void fire(String net,String name){
    var n=instance(net);var t=transition(net,name);
    if(!t.isSpontaneous())throw new IllegalStateException("Cannot initiate uplink "+net+"."+name);
    var bindings=search(n,t,true);if(bindings.isEmpty())throw new IllegalStateException("Disabled: "+net+"."+name);
    for(var b:bindings)System.out.println("BINDING "+net+"."+name+" = "+b);
    if(!n.getInstance(t).fireOneBinding(false))throw new IllegalStateException("Binding vanished");
    if(net.equals("HEDULedgerNet")&&name.equals("t_Anchor"))confirmed=true;
    System.out.println("FIRED "+net+"."+name);markings("STEP");
    if(!confirmed) {
      for(String target:List.of("StudentAgent","WalletNet")) {
        var inst=instance(target);
        for(var p:inst.getNet().places())if(Set.of("p_Accepted","p_PresentationReady","p_VPReady","p_VPShared").contains(p.getName())&&!inst.getInstance(p).isEmpty())throw new IllegalStateException("Premature VP stage: "+target+"."+p.getName());
      }
      for(var inst:de.renew.net.NetInstanceList.getAll())if(Set.of("CompetencyNet","CredentialObject").contains(inst.getNet().getName()))for(var p:inst.getNet().places())if(Set.of("p_BlockchainAnchored","p_Anchored").contains(p.getName())&&!inst.getInstance(p).isEmpty())throw new IllegalStateException("Premature anchoring");
    }
  }
  static void disabled(String n,String t){if(!search(instance(n),transition(n,t),false).isEmpty())throw new IllegalStateException("Must be disabled: "+n+"."+t);System.out.println("EXPECTED DISABLED "+n+"."+t);}
  static void gated(){for(String n:List.of("StudentAgent","WalletNet"))for(String t:List.of("t_GenerateVP","t_ShareVP"))disabled(n,t);}
  static void markings(String label){
    var rows=new TreeSet<String>();for(var n:de.renew.net.NetInstanceList.getAll())for(var p:n.getNet().places())if(!n.getInstance(p).isEmpty())rows.add(n.getNet().getName()+"."+p.getName()+" = "+n.getInstance(p).getDistinctTokens());
    for(var row:rows)System.out.println(label+" TOKEN "+row);
  }
  static void walk(Object x,Set<de.renew.net.NetInstance> seen){
    if(x instanceof de.renew.net.NetInstance n && seen.add(n))for(var p:n.getNet().places())for(var token:n.getInstance(p).getDistinctTokens())walk(token,seen);
    else if(x instanceof de.renew.unify.Tuple t)for(int i=0;i<t.length();i++)walk(t.getComponent(i),seen);
  }
  static void finish(String scenario,String outcome){
    boolean evidence=scenario.startsWith("grade_reject"),success=scenario.equals("happy"),ledger=scenario.equals("ledger_reject");
    var expected=new TreeMap<String,String>();
    expected.put("StudentAgent",success?"p_VPShared":"p_Rejected");expected.put("ProfessorAgent",evidence?"p_Rejected":"p_Approved");expected.put("EvidenceNet",evidence?"p_Rejected":"p_Graded");expected.put("EvidenceObject","p_Graded");
    expected.put("UniversityAgent",evidence?"p_Idle":"p_CredentialIssued");expected.put("CompetencyNet",evidence?"p_Submitted":success?"p_ProfileAggregated":"p_Terminated");
    expected.put("WalletNet",evidence?"p_Empty":success?"p_VPShared":"p_Rejected");expected.put("HEDULedgerNet",success?"p_Anchored":ledger?"p_Rejected":"p_Received");expected.put("HRAgent",success?"p_ResultReady":"p_Idle");
    if(!evidence)expected.put("CredentialObject",success?"p_Aggregated":scenario.startsWith("cancel")?"p_Cancelled":scenario.startsWith("expire")?"p_Expired":"p_Rejected");
    var inactive=new TreeSet<String>();
    if(evidence)inactive.addAll(List.of("UniversityAgent","CompetencyNet","WalletNet","HEDULedgerNet","HRAgent"));
    else if(!success){inactive.add("HRAgent");if(!ledger)inactive.add("HEDULedgerNet");}
    int enabled=0;var before=new TreeMap<String,String>();
    for(var n:de.renew.net.NetInstanceList.getAll()) {
      String name=n.getNet().getName();
      if(name.equals("SystemNet")){for(var p:n.getNet().places()){var pi=n.getInstance(p);if(pi.getNumberOfTokens()!=1 || !(pi.getDistinctTokens().iterator().next() instanceof de.renew.net.NetInstance))throw new IllegalStateException("Lost root reference");}}
      else {
        String target=expected.remove(name);if(target==null)throw new IllegalStateException("Unexpected instance "+name);
        for(var p:n.getNet().places())if(n.getInstance(p).getNumberOfTokens()!=(p.getName().equals(target)?1:0))throw new IllegalStateException("Unexpected final marking "+name+"."+p.getName());
        if(inactive.contains(name)){if(active.contains(name))throw new IllegalStateException("Active instance relabeled inactive: "+name);}
        else if(!active.contains(name))throw new IllegalStateException("Missing activation evidence "+name);
        if(!success && (name.equals("StudentAgent")||(!evidence&&Set.of("WalletNet","CompetencyNet","CredentialObject").contains(name)))){
          var p=n.getNet().places().stream().filter(x->x.getName().equals(target)).findFirst().orElseThrow();
          var token=(de.renew.unify.Tuple)n.getInstance(p).getDistinctTokens().iterator().next();
          if(!outcome.equals(token.getComponent(token.length()-1)))throw new IllegalStateException("Wrong failure outcome "+name+" "+token);
        }
      }
      for(var p:n.getNet().places())before.put(name+"."+p.getName(),n.getInstance(p).getDistinctTokens().toString());
      for(var t:n.getNet().transitions())if(t.isSpontaneous())for(var b:search(n,t,false)){enabled++;System.out.println("UNEXPECTED ENABLED "+b);}
    }
    if(!expected.isEmpty())throw new IllegalStateException("Missing instances "+expected);
    for(var n:de.renew.net.NetInstanceList.getAll())for(var p:n.getNet().places())if(!before.get(n.getNet().getName()+"."+p.getName()).equals(n.getInstance(p).getDistinctTokens().toString()))throw new IllegalStateException("Search mutated marking");
    if(enabled!=0)throw new IllegalStateException("Not quiescent");
    var seen=Collections.newSetFromMap(new IdentityHashMap<de.renew.net.NetInstance,Boolean>());walk(instance("SystemNet"),seen);
    if(seen.size()!=de.renew.net.NetInstanceList.getAll().length)throw new IllegalStateException("Orphaned instance");
    if(success&&!confirmed)throw new IllegalStateException("No anchor confirmation");
    if(evidence){var eo=instance("EvidenceObject");var p=eo.getNet().places().stream().filter(x->x.getName().equals("p_Graded")).findFirst().orElseThrow();var token=(de.renew.unify.Tuple)eo.getInstance(p).getDistinctTokens().iterator().next();if(!token.toString().contains("int(40)"))throw new IllegalStateException("Failing assessment not recorded");}
    markings("FINAL");System.out.println("ENABLED BINDINGS 0");System.out.println("ACTIVE UNFINISHED 0");System.out.println("ACTIVE "+active);System.out.println("INACTIVE UNINVOKED "+inactive);System.out.println("RETAINED REFERENCES "+seen.size()+"/"+de.renew.net.NetInstanceList.getAll().length);
    System.out.println("PASS SCENARIO "+scenario+" "+(success?"EXPECTED_SUCCESS_TERMINATION":"EXPECTED_FAILURE_TERMINATION")+" OUTCOME "+outcome);
  }
  // Read-only enumeration of every complete binding from each spontaneous initiator.
  // Finder never short-circuits; emit every occurrence, without participant deduplication.
  static void authorizationBindings(String phase,boolean issuer) {
    int[] total={0};
    for(var n:de.renew.net.NetInstanceList.getAll())for(var t:n.getNet().transitions())if(t.isSpontaneous()) {
      var ti=n.getInstance(t);
      var finder=new de.renew.engine.searcher.Finder(){
        public boolean isCompleted(){return false;}
        public void found(de.renew.engine.searcher.Searcher searcher){
          var parts=new ArrayList<String>();
          for(var occurrence:searcher.getOccurrences()) {
            var tr=occurrence.getTransition();String net=tr.getNetInstance().getNet().getName(),name=tr.getTransition().getName();
            parts.add(net+"."+name);
            if((Set.of("StudentAgent","WalletNet").contains(net)&&Set.of("t_AcceptCredential","t_GenerateVP","t_ShareVP").contains(name)) ||
               (net.equals("CompetencyNet")&&Set.of("t_SBT_Accept","t_Agg_Ingest").contains(name)) ||
               (net.equals("CredentialObject")&&Set.of("t_Accept","t_Aggregate").contains(name)) ||
               net.equals("HEDULedgerNet") || (net.equals("HRAgent")&&name.equals("t_GenerateResult")))
              throw new IllegalStateException("Unauthorized complete binding: "+net+"."+name);
          }
          Collections.sort(parts);total[0]++;
          System.out.println("AUTH COMPLETE BINDING "+phase+" #"+total[0]+" initiator="+n.getNet().getName()+"."+t.getName()+" participants="+parts);
        }
      };
      de.renew.engine.simulator.SimulatorHelper.searchOnce(new de.renew.engine.searcher.Searcher(),finder,ti,ti);
    }
    disabled("StudentAgent","t_AcceptCredential");gated();
    for(String t:List.of("t_Order","t_Validate","t_Commit","t_Anchor"))disabled("HEDULedgerNet",t);
    if(issuer) {
      disabled("CompetencyNet","t_SBT_Cancel");
      for(String t:List.of("t_SBT_Expire"))disabled("CompetencyNet",t);
      disabled("StudentAgent","t_RejectCredential");disabled("StudentAgent","t_DenyInvalidSignature");
      if(search(instance("CompetencyNet"),transition("CompetencyNet","t_DenyInvalidIssuerSignature"),false).isEmpty())throw new IllegalStateException("Missing finite issuer denial");
      System.out.println("P7 COMPETING DENIAL AVAILABLE "+phase);
      System.out.println("P7 COMPETING ACCEPTANCE UNAVAILABLE "+phase);
    }
    for(var n:de.renew.net.NetInstanceList.getAll())for(var place:n.getNet().places()) {
      if(Set.of("p_AwaitingAnchor","p_BlockchainAnchored","p_Anchored","p_Accepted","p_PresentationReady","p_VPReady","p_VPShared","p_ResultReady").contains(place.getName())&&!n.getInstance(place).isEmpty())
        throw new IllegalStateException("Unauthorized success marking: "+n.getNet().getName()+"."+place.getName());
    }
    System.out.println("AUTH ENUMERATION PASS "+phase+" complete_bindings="+total[0]+" forbidden_bindings=0");
  }
  static void runScenario(String scenario){
    fire("StudentAgent","t_SubmitEvidence");fire("EvidenceNet","t_LMS_Submit");fire("EvidenceNet","t_Review_Request");fire("ProfessorAgent","t_StartReview");fire("ProfessorAgent","t_AssessEvidence");
    if(scenario.startsWith("grade_reject")) {
      if(scenario.endsWith("waiting"))fire("StudentAgent","t_WaitCredential");
      fire("ProfessorAgent","t_RejectEvidence");finish(scenario,"EVIDENCE_REJECTED");return;
    }
    fire("ProfessorAgent","t_ApproveEvidence");fire("UniversityAgent","t_VerifyEvidence");fire("UniversityAgent","t_RequestCredential");fire("CompetencyNet","t_Verification_Pass");fire("UniversityAgent","t_IssueCredential");
    if(scenario.startsWith("bad_issuer_signature"))authorizationBindings("submitted",true);
    if(!scenario.endsWith("submitted")) {
      fire("StudentAgent","t_WaitCredential");
      if(scenario.startsWith("bad_issuer_signature"))authorizationBindings("waiting",true);
    }
    if(!scenario.endsWith("submitted")&&!scenario.endsWith("waiting")) {
      fire("StudentAgent","t_ReceiveCredential");
      if(scenario.startsWith("bad_issuer_signature"))authorizationBindings("received",true);
    }
    if(scenario.startsWith("cancel")){fire("CompetencyNet","t_SBT_Cancel");finish(scenario,"CANCELLED");return;}
    if(scenario.startsWith("expire")){disabled("StudentAgent","t_AcceptCredential");fire("CompetencyNet","t_SBT_Expire");finish(scenario,"EXPIRED");return;}
    if(scenario.startsWith("bad_issuer_signature")){disabled("CompetencyNet","t_SBT_Cancel");fire("CompetencyNet","t_DenyInvalidIssuerSignature");finish(scenario,"INVALID_ISSUER_SIGNATURE");return;}
    disabled("CompetencyNet","t_DenyInvalidIssuerSignature");
    if(scenario.equals("bad_signature")){authorizationBindings("invalid_student_received",false);disabled("StudentAgent","t_AcceptCredential");disabled("StudentAgent","t_RejectCredential");fire("StudentAgent","t_DenyInvalidSignature");finish(scenario,"INVALID_SIGNATURE");return;}
    disabled("StudentAgent","t_DenyInvalidSignature");
    if(scenario.equals("reject")){fire("StudentAgent","t_RejectCredential");finish(scenario,"HOLDER_REJECTED");return;}
    fire("StudentAgent","t_AcceptCredential");gated();fire("HEDULedgerNet","t_Order");gated();
    if(scenario.equals("ledger_reject")){fire("HEDULedgerNet","t_Reject");finish(scenario,"LEDGER_REJECTED");return;}
    fire("HEDULedgerNet","t_Validate");gated();fire("HEDULedgerNet","t_Commit");gated();fire("HEDULedgerNet","t_Anchor");
    terminal(instance("CompetencyNet"),"p_BlockchainAnchored");terminal(instance("CredentialObject"),"p_Anchored");
    fire("StudentAgent","t_GenerateVP");fire("StudentAgent","t_ShareVP");fire("HRAgent","t_ParseVP");fire("CompetencyNet","t_Agg_Ingest");fire("HRAgent","t_SemanticMatch");fire("HRAgent","t_GapAnalysis");fire("HRAgent","t_GenerateResult");fire("WalletNet","t_GenerateVP");fire("WalletNet","t_ShareVP");finish(scenario,"SUCCESS");
  }
  public static void main(String[] args) throws Exception {
    try {
      Path dir=Path.of(args[0]);
      String mode=args.length>1?args[1]:"validate";
      String scenario=args.length>2?args[2]:"happy";
      if(!Set.of("validate","smoke").contains(mode)) throw new IllegalArgumentException("Use validate or smoke; drawing generation is disabled.");
      if(!Set.of("happy","reject","cancel","expire","grade_reject","ledger_reject","bad_signature","bad_issuer_signature","grade_reject_waiting","cancel_submitted","cancel_waiting","expire_submitted","expire_waiting","bad_issuer_signature_submitted","bad_issuer_signature_waiting").contains(scenario)) throw new IllegalArgumentException("Unknown scenario: "+scenario);
      System.out.println("MODE "+mode+" SCENARIO "+scenario+" (existing drawings are read-only)");
      ShadowNetSystem sns=new ShadowNetSystem(new JavaNetCompiler(true,true,true));
      var paths=Files.list(dir).filter(p->p.toString().endsWith(".rnw")).sorted().toList();
      for(Path path:paths) {
        StorableInput in=new StorableInput(path.toFile(),true);
        CPNDrawing drawing=(CPNDrawing)CH.ifa.draw.io.StorableInputDrawingLoader.readStorableDrawing(in);
        if(in.getVersion()!=12) throw new IllegalStateException("Missing RNW version 12 header: "+path);
        in.close();
        drawing.setName(path.getFileName().toString().replace(".rnw",""));
        var en=drawing.figures();var names=new IdentityHashMap<Figure,String>();var arcs=new ArrayList<ArcConnection>();
        var actualNodes=new TreeSet<String>();var actualArcs=new ArrayList<String>();
        while(en.hasMoreElements()) {
          var f=en.nextFigure();
          if(f instanceof CPNTextFigure txt && txt.getType()==CPNTextFigure.NAME) {
            String kind=txt.parent() instanceof PlaceFigure?"P":"T";
            if(!actualNodes.add(kind+"\t"+txt.getText())) throw new IllegalStateException("Duplicate node name");
            names.put((Figure)txt.parent(),txt.getText());
          }
          if(f instanceof ArcConnection arc) arcs.add(arc);
        }
        for(var a:arcs) {
          if(a.getArcType()!=1) throw new IllegalStateException("Non-normal arc");
          actualArcs.add(names.get(a.startFigure())+"\t"+names.get(a.endFigure()));
        }
        var expectedNodes=new TreeSet<String>();var expectedArcs=new ArrayList<String>();boolean selected=false;
        for(String line:Files.readAllLines(dir.resolve("model.tsv"))) {
          String[] fields=line.split("\t",-1);
          if(fields[0].equals("NET")) selected=fields[1].equals(drawing.getName());
          else if(selected && (fields[0].equals("P")||fields[0].equals("T"))) expectedNodes.add(fields[0]+"\t"+fields[1]);
          else if(selected && fields[0].equals("A")) expectedArcs.add(fields[1]+"\t"+fields[2]);
        }
        Collections.sort(actualArcs);Collections.sort(expectedArcs);
        if(!actualNodes.equals(expectedNodes)||!actualArcs.equals(expectedArcs)) throw new IllegalStateException("Structure mismatch: "+path);
        System.out.println("STRUCTURE OK "+drawing.getName()+" directed arcs="+arcs.size());
        var actualText=new TreeMap<String,String>();
        var texts=drawing.figures();
        while(texts.hasMoreElements()) {
          var f=texts.nextFigure();if(f instanceof CPNTextFigure txt) {
            var parent=(Figure)txt.parent();String key;
            if(parent instanceof ArcConnection arc) key="A:"+names.get(arc.startFigure())+":"+names.get(arc.endFigure());
            else key="N:"+names.get(parent);
            key+=":"+txt.getType();
            if(actualText.put(key,txt.getText())!=null)throw new IllegalStateException("Duplicate annotation "+key);
          }
        }
        var expectedText=new TreeMap<String,String>();selected=false;
        for(String line:Files.readAllLines(dir.resolve("model.tsv"))) {
          String[] a=line.split("\t",-1);
          if(a[0].equals("NET"))selected=a[1].equals(drawing.getName());
          else if(selected && (a[0].equals("P")||a[0].equals("T"))) {
            expectedText.put("N:"+a[1]+":"+CPNTextFigure.NAME,a[1]);
            if(!a[4].isEmpty())expectedText.put("N:"+a[1]+":"+CPNTextFigure.INSCRIPTION,a[4].replace("\\n","\n"));
          } else if(selected && a[0].equals("A"))expectedText.put("A:"+a[1]+":"+a[2]+":"+CPNTextFigure.INSCRIPTION,a[3]);
        }
        if(!actualText.equals(expectedText))throw new IllegalStateException("Inscription/owner mismatch: "+path);
        System.out.println("INSCRIPTIONS OK "+drawing.getName());
        // Test overrides modify only in-memory markings; the saved RNW is never written.
        if(mode.equals("smoke") && (drawing.getName().equals("StudentAgent") || drawing.getName().equals("CompetencyNet"))) {
          var figures=drawing.figures();
          while(figures.hasMoreElements()) {
            var f=figures.nextFigure();
            if(f instanceof CPNTextFigure txt && txt.getType()==CPNTextFigure.INSCRIPTION && txt.parent() instanceof PlaceFigure) {
              String marking=txt.getText();
              if(scenario.startsWith("expire")) marking=marking.replace("[80,50,0,100", "[80,50,100,100");
              if(scenario.startsWith("grade_reject")) marking=marking.replace("[80,50,0,100", "[40,50,0,100");
              if(scenario.equals("bad_signature")) marking=marking.replace("student-signature", "wrong-signature");
              if(scenario.startsWith("bad_issuer_signature") && drawing.getName().equals("CompetencyNet")) marking=marking.replace("issuer-signature","wrong-issuer-signature");
              txt.setText(marking);
            }
          }
        }
        drawing.buildShadow(sns);
        System.out.println("READ OK "+path.getFileName());
      }
      ShadowLookup lookup=sns.compile();
      for(Path path:paths) {
        String name=path.getFileName().toString().replace(".rnw","");
        var net=lookup.getNet(name);
        System.out.println("COMPILE OK "+name+" places="+net.placeCount()+" transitions="+net.transitionCount());
      }
      System.out.println("PASS: all "+paths.size()+" RNW files deserialized and compiled together with Renew Timed Java Compiler.");
      if(mode.equals("smoke")) {
        var plugin=new de.renew.application.SimulatorPlugin(new de.renew.plugin.PluginProperties(dir.toUri().toURL()));
        plugin.init();var properties=new java.util.Properties();properties.setProperty("de.renew.simulatorMode","-1");plugin.setupSimulation(properties);
        lookup.makeNetsKnown();
        de.renew.engine.simulator.SimulationThreadPool.getNew().submitAndWait(()->{
          try {
            var system=lookup.getNet("SystemNet").buildInstance();
            for(var pl:system.getNet().places()) System.out.println("INITIAL "+pl.getName()+" "+system.getInstance(pl).getDistinctTokens());
            var create=system.getNet().transitions().stream().filter(t->t.getName().equals("t_CreateStudent")).findFirst().orElseThrow();
            if(!system.getInstance(create).fireOneBinding(false)) throw new IllegalStateException("Creation not enabled");
            System.out.println("SMOKE OK: t_CreateStudent fired");
            var agents=new HashMap<String,de.renew.net.NetInstance>();
            for(var p:system.getNet().places()) {
              var tokens=system.getInstance(p).getDistinctTokens();
              if(tokens.size()!=1 || !(tokens.iterator().next() instanceof de.renew.net.NetInstance)) throw new IllegalStateException("Missing instance in "+p);
              System.out.println("INSTANCE OK "+p.getName()+" "+tokens);
              var instance=(de.renew.net.NetInstance)tokens.iterator().next();agents.put(instance.getNet().getName(),instance);
            }
            active.add("StudentAgent");
            runScenario(scenario);
            return true;
          }catch(Exception e){throw new RuntimeException(e);}
        }).get();
      }
      System.exit(0);
    } catch(SyntaxException ex) {
      System.err.println("SYNTAX ERROR: "+ex.getMessage());
      if(ex.detailed!=null) for(String s:ex.detailed) System.err.println(s);
      System.err.println("Objects: "+ex.errorObjects);ex.printStackTrace();System.exit(1);
    } catch(Throwable ex) {ex.printStackTrace();System.exit(1);}
  }
}
