import java.io.*;
import java.nio.file.*;
import java.awt.Point;
import java.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.*;
import de.renew.gui.*;
import de.renew.shadow.*;
import de.renew.formalism.java.JavaNetCompiler;

/** Build-time tooling only: never referenced by the model. */
public class BuildProject {
  static void fire(de.renew.net.NetInstance instance,String name) {
    var t=instance.getNet().transitions().stream().filter(x->x.getName().equals(name)).findFirst().orElseThrow();
    if(!instance.getInstance(t).fireOneBinding(false)) throw new IllegalStateException("Disabled: "+instance.getNet().getName()+"."+name);
    System.out.println("FIRED "+instance.getNet().getName()+"."+name);
  }
  static void text(CPNDrawing d, Figure parent, int type, String value, int x, int y) {
    if(value.isEmpty()) return;
    CPNTextFigure t=new CPNTextFigure(type);
    t.setText(value.replace("\\n", "\n"));
    t.setParent((ParentFigure)parent);
    t.moveBy(x-t.displayBox().x,y-t.displayBox().y);
    d.add(t);
  }
  public static void main(String[] args) throws Exception {
    try {
      Path dir=Path.of(args[0]);
      if(args.length<2 || !args[1].equals("validate")) {
        CPNDrawing d=null; Map<String,Figure> nodes=new LinkedHashMap<>();
        for(String line:Files.readAllLines(dir.resolve("model.tsv"))) {
          String[] a=line.split("\t",-1);
          if(a[0].equals("NET")) {d=new CPNDrawing();d.setName(a[1]);nodes.clear();}
          else if(a[0].equals("P")||a[0].equals("T")) {
            int x=Integer.parseInt(a[2]),y=Integer.parseInt(a[3]);
            Figure f=a[0].equals("P")?new PlaceFigure():new TransitionFigure();
            f.displayBox(new Point(x,y),new Point(x+24,y+24));d.add(f);nodes.put(a[1],f);
            text(d,f,CPNTextFigure.NAME,a[1],x+35,y);
            text(d,f,CPNTextFigure.INSCRIPTION,a[4],x+35,y+22);
          } else if(a[0].equals("A")) {
            Figure start=nodes.get(a[1]),end=nodes.get(a[2]);
            ArcConnection arc=new ArcConnection(1);
            arc.startPoint(start.center());arc.endPoint(end.center());
            arc.connectStart(start.connectorAt(start.center().x,start.center().y));
            arc.connectEnd(end.connectorAt(end.center().x,end.center().y));
            arc.updateConnection();d.add(arc);
            if(d.getName().equals("SystemNet")) {
              int bend=start instanceof PlaceFigure?-65:65;
              arc.insertPointAt(new Point((start.center().x+end.center().x)/2,start.center().y+bend),1);
              arc.updateConnection();
            }
            text(d,arc,CPNTextFigure.INSCRIPTION,a[3],arc.center().x+12,arc.center().y-16);
          } else if(a[0].equals("END")) {
            File file=dir.resolve(d.getName()+".rnw").toFile();d.setFilename(file);
            StorableOutput out=new StorableOutput(file);out.writeInt(12);out.writeStorable(d);out.close();
          }
        }
      }
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
      if(args.length>1 && args[1].equals("smoke")) {
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
            for(String[] step:sequence) fire(agents.get(step[0]),step[1]);
            String[][] targets={{"StudentAgent","p_VPShared"},{"ProfessorAgent","p_Approved"},{"UniversityAgent","p_CredentialIssued"},{"EvidenceNet","p_Graded"},{"CompetencyNet","p_ProfileAggregated"},{"WalletNet","p_VPShared"},{"HEDULedgerNet","p_Anchored"},{"HRAgent","p_ResultReady"}};
            for(String[] target:targets) {
              var instance=agents.get(target[0]);var place=instance.getNet().places().stream().filter(p->p.getName().equals(target[1])).findFirst().orElseThrow();
              if(instance.getInstance(place).getNumberOfTokens()!=1) throw new IllegalStateException("Missing terminal marking "+Arrays.toString(target));
              System.out.println("TERMINAL OK "+target[0]+"."+target[1]);
            }
            System.out.println("PASS: directed happy-path smoke test in actual Renew engine.");
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
