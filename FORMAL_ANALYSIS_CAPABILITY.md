# Formal-analysis capability

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT AVAILABLE FOR THE CURRENT REFERENCE-NET TOOLCHAIN**

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**

| Requested capability | Classification | Scope |
| --- | --- | --- |
| Reachability graph generation | NOT AVAILABLE | No installed graph enumerator/state canonicalizer for reference instances and synchronous creation was identified. |
| Exhaustive state-space exploration | NOT AVAILABLE | Simulator bindings are current-marking searches, not an exploration of successor configurations. |
| Deadlock detection over complete state space | PARTIALLY SUPPORTED | Complete binding search of a supplied concrete endpoint is supported; exhaustive reachable-configuration quantification is not. |
| Boundedness analysis | NOT AVAILABLE | No installed boundedness/coverability analyzer for these Reference Nets. |
| Liveness analysis | NOT AVAILABLE | No installed all-executions temporal/liveness analyzer. |
| Transition reachability | PARTIALLY SUPPORTED | Actual native firings establish existential witnesses; unobserved transitions are not thereby unreachable. |
| Home-state analysis | NOT AVAILABLE | No global returnability/home-state procedure identified. |
| Invariant analysis | NOT AVAILABLE | No installed invariant solver supporting this reference/dynamic-instance semantics identified. |


The classification concerns the installed local distribution, not every optional Renew extension ever published. See results/plugin_inventory.json (each JAR's manifest and hash), results/native_api.txt, and results/manual_capability_excerpts.txt. All installed plugin archives were inspected; no reachability/model-checking plugin or documented exhaustive Reference-Net procedure was found. The base documentation describes simulation and engine multiplicity, not a complete global-state explorer. The stale 4.0 installation example in doc/README is not used as the distribution version; the local manual labels Release 4.1.

Available concrete procedure: load all templates with the version-aware StorableInputDrawingLoader, compile with JavaNetCompiler(true,true,true), create SystemNet with Net.buildInstance(), and call TransitionInstance.fireOneBinding(false) for spontaneous initiators. For a concrete configuration, use SimulatorHelper.searchOnce (or isFirable/findAllBindings) for each spontaneous transition in each NetInstanceList entry. The finder reports the entire synchronized occurrence set. These native operations support reference tokens, synchronous channels and dynamic :new instantiation in actual executions. Uplinks must not be fired as independent events. NetInstanceList is an instance registry, not a reachable-state graph.

Reproduction: RunFormalExperiments.ps1 records the API signatures with javap, validates using Validate.ps1 -Smoke, then runs the bounded native probe. It does not install a plugin, translate to ordinary P/T nets, implement a synthetic graph enumerator, or claim an equivalence result. No supported exhaustive command can be supplied because no such capability was identified. Endpoint absence of bindings must still be classified using active lifecycle states; a valid terminal is not a defective deadlock.
