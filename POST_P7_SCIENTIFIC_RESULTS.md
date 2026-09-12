# Post-P7 scientific results

SYSTEMATIC DIRECTED EXPERIMENTAL CAMPAIGN / FINITE REPRESENTATIVE-DOMAIN EXPLORATION.
EXHAUSTIVE REFERENCE-NET STATE-SPACE ANALYSIS NOT PERFORMED.

## A. Model structural facts

The frozen working tree has 11 native RNW drawings, 80 places, 87 transitions and 174 ordinary arcs (results/post_p7/structure.csv). Commit, working-tree hashes, OS, Java, Renew version, Timed Java Compiler and sequential engine are recorded in environment.json and checkpoint_hashes.json. RNWs, model.tsv and model_manifest.json remain byte-identical (input_preservation.csv). HEAD alone does not identify these uncommitted repairs.

## B. Baseline regression evidence

All 15 original scenarios passed (baseline_regressions.csv; baseline_validation.txt). Tests ran inside a byte-identical staged copy under this namespace, preventing fixed validator output paths from overwriting historical results. A Java compiler startup attempt failed; retry then exposed VerifyP7.ps1 treating harmless Log4j stderr as a PowerShell failure. Its wrapper now captures stdout/stderr separately and checks process exit codes. No assertions, fixtures or model files changed. Initial diagnostics are retained in focused_verification_retry.txt and focused_verification_wrapper_fixed.txt where present; current child diagnostics are preserved in focused_baseline_stderr.txt and focused_p7_stderr.txt. The focused check succeeded after the tooling fix.

## C. P7 repair evidence

Focused verification passes; its native replay blocks the old acceptance and reaches finite issuer denial. See the focused_verification*.txt logs and focused_p7_native.txt. Historical P7 failing files are unchanged (input_preservation.csv). Both signature classes are string fixtures, not cryptographic verification.

## D. Representative-domain exploration

OBSERVED: 24 actual executions covering 16 representative input fixtures (fixture_exploration.csv). Grade, fixed time and both signature classes are defined in domain.csv. Each fixture discovers actual complete decision bindings, then fresh executions choose each feasible modeled decision, including ledger success/rejection after authorized acceptance. Infeasible choices are recorded in fixture_feasibility.csv; no guard is overridden. This is not arbitrary-value coverage or exhaustive state-space exploration.

## E. Interleaving exploration

OBSERVED: 55 dedicated executions, with 53 distinct fixture-and-firing-sequence combinations (interleaving_executions.csv; summary.json). Duplicate acceptance/expiry checks serve overlapping obligations and are not counted twice as distinct traces. competition_summary.csv maps obligations A–O to evidence. complete_bindings.csv enumerates all complete native bindings at each observed pre-firing marking; raw logs retain exact tokens and firing participants. Disabled acceptance/expiry, invalid-signature success and pre-anchor VP alternatives are explicitly excluded as simultaneous races. Ledger rejection competes only at Ordered. All ordered Wallet VP placements among the fixed Student/HR success steps were executed; this is a prescribed schedule family, not all model interleavings.

Across the new correctness executions, 79 endpoints were inspected: 39 expected successes, 40 expected failures and 0 defective global deadlocks (quiescence_summary.csv). These are execution counts only, never complete reachable-state deadlock totals. Each endpoint checks active versus uninvoked services, exact place/token/outcome values and complete binding absence. Per-step checks enforce VP-before-anchor exclusions, atomic confirmation partners and reference retention. Reference checkpoints include creation and every firing through termination (reference_retention.csv). Actual branch-to-label checks are in outcome_consistency.csv.

## F. Property P1–P10 results

See POST_P7_PROPERTY_RESULTS.md and paper_properties.csv. Existential properties use native witnesses; universal properties are scoped to tested executions and remain NOT EXHAUSTIVELY VERIFIED. Observed violations: none (summary.json and per-execution violated_properties). Binding search is native Renew, not a custom simulator or a synthetic ordinary-net graph.

## G. Directed transition coverage

DIRECTED EXPERIMENTAL TRANSITION COVERAGE: 86 / 87 = 98.85% (transition_coverage.csv; summary.json). Coverage includes native fired participants from baseline/focused/new traces plus verified atomic bootstrap participants. The University rejection transition is structurally excluded by the unchanged passing grade forwarded by Professor; it is not declared globally unreachable. Every other unobserved transition would remain UNOBSERVED.

## H. Multi-instance concurrency

MEASURED: 40 completed experiment runs, maximum 10 independent roots (concurrency_results.csv; summary.json). The driver owns instances by identity, follows each root’s actual references, checks every complete binding for foreign participants, and checks reference isolation after every firing. Roots coexist and advance round-robin in the native sequential engine; this measures concurrent workflow interleavings, not parallel CPU throughput. These controlled multi-root runs use the valid nominal fixture and an acceptance-success schedule; they do not cover arbitrary mixed workloads or failure interleavings across roots. It makes no Fabric/scalability claim. No RNW semantics were changed.

## I. Renew simulator runtime

MEASURED: 450 observations across 15 standard scenarios (runtime_raw.csv; runtime_summary.csv). Where measured, each run uses a fresh JVM. System.nanoTime measures from root creation through native execution and endpoint checks, after loading/compilation, including complete-binding searches, reference/marking checks and trace output. There is no warmup exclusion. These are instrumented Renew simulation execution times, not blockchain latency, TPS or network latency. Summary statistics are computed from raw observations; stddev is sample standard deviation and p95 uses the nearest-rank convention. Per-scenario n is explicit in the CSV; no estimated repetitions are supplied. The runtime_raw transition_firings column counts all actual synchronized transition participants, including the verified bootstrap, rather than counting only spontaneous initiating transactions. The driver RESULT counter records initiating transactions; the publication CSV derives participant counts directly from the native BINDING/BOOTSTRAP records. The success field means the expected outcome and validation checks passed; a modeled failure can therefore have success=true while final_outcome records its failure cause. Host load is not isolated, so these times do not support hardware-independent performance claims.

## J. Formal-analysis limitations

No exhaustive Reference-Net explorer is available in the installed toolchain (FORMAL_ANALYSIS_CAPABILITY.md and prior local API/plugin evidence). Reachable-state counts, formal boundedness and liveness remain NOT MEASURED / NOT EXHAUSTIVELY VERIFIED. Finite input classes and passing schedules are not global liveness/deadlock-freedom proofs.

## K. Unimplemented workload claims

Poisson, MMPP, Bmax/Tout batching, external arrival rates, agent-load claims, TPS and MRT remain NOT IMPLEMENTED / NOT MEASURED / OUTSIDE CURRENT RENEW MODEL. The experiment does not add these mechanisms or contact a blockchain. Existing WORKLOAD_CLAIM_AUDIT.md remains applicable.

## L. Numbers safe for publication

Use only the hash-identified structural inventory, native baseline/focused witness outcomes, finite fixture/schedule execution counts and outcomes, observed transition coverage, per-checkpoint reference retention, and explicitly scoped instrumented runtime/concurrent-root results where measured. Every count above is generated in summary.json or its source CSVs. Publication tables are paper_scenarios.csv, paper_properties.csv, paper_transition_coverage.csv, paper_runtime.csv and paper_concurrency.csv. Do not present any of them as exhaustive state-space verification or blockchain performance.

Reproduce using RunPostP7Experiments.ps1. The generated PostP7Base reuses the native loader and saved-inscription checks; post.* JVM properties change only in-memory initial fixtures. Its loader banner retains the happy scenario name, while execution_records.json and the actual token logs identify each experiment fixture. driver_identity.json hashes the executed Java source/classes. The runner writes only under results/post_p7, preserving historical artifacts; this report is generated there and copied to the repository root for review. Inspect input_preservation.csv for the sole authorized existing-tooling change (VerifyP7 stderr handling). No commit, push or merge is performed.
