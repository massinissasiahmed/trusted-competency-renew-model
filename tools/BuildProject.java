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
  static void fire(de.renew.net.NetInstance instance,String name) {
    var t=instance.getNet().transitions().stream().filter(x->x.getName().equals(name)).findFirst().orElseThrow();
    if(!instance.getInstance(t).fireOneBinding(false)) throw new IllegalStateException("Disabled: "+instance.getNet().getName()+"."+name);
    System.out.println("FIRED "+instance.getNet().getName()+"."+name);
  }
  public static void main(String[] args) throws Exception {
    try {
      Path dir=Path.of(args[0]);
      String mode=args.length>1?args[1]:"validate";
      String scenario=args.length>2?args[2]:"happy";
      if(!Set.of("validate","smoke").contains(mode)) throw new IllegalArgumentException("Use validate or smoke; drawing generation is disabled.");
      if(!Set.of("happy","reject","cancel","expire","grade_reject","ledger_reject","bad_signature").contains(scenario)) throw new IllegalArgumentException("Unknown scenario: "+scenario);
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
        // Compare every stored text figure, including arc inscriptions and markings.
        var actualText=new ArrayList<String>();
        var texts=drawing.figures();
        while(texts.hasMoreElements()) {var f=texts.nextFigure();if(f instanceof CPNTextFigure txt) actualText.add(txt.getText());}
        var expectedText=new ArrayList<String>();selected=false;
        for(String line:Files.readAllLines(dir.resolve("model.tsv"))) {
          String[] a=line.split("\t",-1);
          if(a[0].equals("NET")) selected=a[1].equals(drawing.getName());
          else if(selected && (a[0].equals("P")||a[0].equals("T"))) {expectedText.add(a[1]);if(!a[4].isEmpty())expectedText.add(a[4].replace("\\n","\n"));}
          else if(selected && a[0].equals("A")) expectedText.add(a[3]);
        }
        if(!actualText.equals(expectedText)) throw new IllegalStateException("Inscription mismatch: "+path);
        System.out.println("INSCRIPTIONS OK "+drawing.getName());
        // Test overrides modify only in-memory markings; the saved RNW is never written.
        if(mode.equals("smoke") && drawing.getName().equals("StudentAgent")) {
          var figures=drawing.figures();
          while(figures.hasMoreElements()) {
            var f=figures.nextFigure();
            if(f instanceof CPNTextFigure txt && txt.getType()==CPNTextFigure.INSCRIPTION && txt.parent() instanceof PlaceFigure) {
              String marking=txt.getText();
              if(scenario.equals("expire")) marking=marking.replace("[80,50,0,100", "[80,50,100,100");
              if(scenario.equals("grade_reject")) marking=marking.replace("[80,50,0,100", "[40,50,0,100");
              if(scenario.equals("bad_signature")) marking=marking.replace("student-signature", "wrong-signature");
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
            String[][] sequence={
              {"StudentAgent","t_SubmitEvidence"},{"EvidenceNet","t_LMS_Submit"},{"EvidenceNet","t_Review_Request"},
              {"ProfessorAgent","t_StartReview"},{"ProfessorAgent","t_AssessEvidence"},{"ProfessorAgent","t_ApproveEvidence"},
              {"UniversityAgent","t_VerifyEvidence"},{"UniversityAgent","t_RequestCredential"},
              {"CompetencyNet","t_Verification_Pass"},{"UniversityAgent","t_IssueCredential"},
              {"StudentAgent","t_WaitCredential"},{"StudentAgent","t_ReceiveCredential"},{"StudentAgent","t_AcceptCredential"},
              {"HEDULedgerNet","t_Order"},{"HEDULedgerNet","t_Validate"},{"HEDULedgerNet","t_Commit"},
              {"StudentAgent","t_GenerateVP"},{"StudentAgent","t_ShareVP"},{"HRAgent","t_ParseVP"},
              {"CompetencyNet","t_Agg_Ingest"},{"HRAgent","t_SemanticMatch"},{"HRAgent","t_GapAnalysis"},
              {"HRAgent","t_GenerateResult"},{"WalletNet","t_GenerateVP"},{"WalletNet","t_ShareVP"}
            };
            for(String[] step:sequence) {
              if(step[0].equals("ProfessorAgent") && step[1].equals("t_ApproveEvidence") && scenario.equals("grade_reject")) {
                fire(agents.get("ProfessorAgent"),"t_RejectEvidence");
                terminal(agents.get("ProfessorAgent"),"p_Rejected");terminal(agents.get("EvidenceNet"),"p_Rejected");
                System.out.println("PASS SCENARIO grade_reject; StudentAgent has no evidence-rejection notification path.");return true;
              }
              if(step[0].equals("StudentAgent") && step[1].equals("t_AcceptCredential") && Set.of("reject","cancel","expire","bad_signature").contains(scenario)) {
                if(scenario.equals("reject")) {fire(agents.get("StudentAgent"),"t_RejectCredential");terminal(agents.get("StudentAgent"),"p_Rejected");terminal(agents.get("WalletNet"),"p_Rejected");}
                if(scenario.equals("cancel")) fire(agents.get("CompetencyNet"),"t_SBT_Cancel");
                if(scenario.equals("expire")||scenario.equals("bad_signature")) {
                  var st=agents.get("StudentAgent");var accept=st.getNet().transitions().stream().filter(t->t.getName().equals("t_AcceptCredential")).findFirst().orElseThrow();
                  if(st.getInstance(accept).fireOneBinding(false)) throw new IllegalStateException("Invalid acceptance was enabled");
                  System.out.println("EXPECTED DISABLED StudentAgent.t_AcceptCredential "+scenario);
                  if(scenario.equals("bad_signature")) {terminal(st,"p_CredentialReceived");System.out.println("PASS SCENARIO bad_signature");return true;}
                  fire(agents.get("CompetencyNet"),"t_SBT_Expire");
                }
                terminal(agents.get("CompetencyNet"),"p_Terminated");
                terminal(instance("CredentialObject"),scenario.equals("reject")?"p_Rejected":scenario.equals("cancel")?"p_Cancelled":"p_Expired");
                System.out.println("PASS SCENARIO "+scenario);return true;
              }
              if(step[0].equals("HEDULedgerNet") && step[1].equals("t_Validate") && scenario.equals("ledger_reject")) {
                fire(agents.get("HEDULedgerNet"),"t_Reject");terminal(agents.get("HEDULedgerNet"),"p_Rejected");
                System.out.println("PASS SCENARIO ledger_reject; aggregation cannot complete.");return true;
              }
              fire(agents.get(step[0]),step[1]);
            }
            String[][] targets={{"StudentAgent","p_VPShared"},{"ProfessorAgent","p_Approved"},{"UniversityAgent","p_CredentialIssued"},{"EvidenceNet","p_Graded"},{"CompetencyNet","p_ProfileAggregated"},{"WalletNet","p_VPShared"},{"HEDULedgerNet","p_Anchored"},{"HRAgent","p_ResultReady"}};
            for(String[] target:targets) {
              var instance=agents.get(target[0]);var place=instance.getNet().places().stream().filter(p->p.getName().equals(target[1])).findFirst().orElseThrow();
              if(instance.getInstance(place).getNumberOfTokens()!=1) throw new IllegalStateException("Missing terminal marking "+Arrays.toString(target));
              System.out.println("TERMINAL OK "+target[0]+"."+target[1]);
            }
            terminal(instance("EvidenceObject"),"p_Graded");terminal(instance("CredentialObject"),"p_Aggregated");
            for(var instance:de.renew.net.NetInstanceList.getAll()) for(var place:instance.getNet().places()) {
              if(!instance.getInstance(place).isEmpty()) System.out.println("FINAL TOKEN "+instance.getNet().getName()+"."+place.getName()+" = "+instance.getInstance(place).getDistinctTokens());
            }
            System.out.println("PASS SCENARIO happy: directed lifecycle in actual Renew engine.");
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
