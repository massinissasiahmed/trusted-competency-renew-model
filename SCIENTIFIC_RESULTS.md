# Scientific results — stopped on P7

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT AVAILABLE FOR THE CURRENT REFERENCE-NET TOOLCHAIN**

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**

## A. Structural facts

The checkpoint contains 11 nets, 80 places, 87 transitions and 174 ordinary directed arcs: generated results/structure.csv and actual native compilation in results/baseline_validation.txt. The repaired working tree is uncommitted; cite its file hashes in results/baseline_hashes.csv, not the older commit alone. RNW bytes remain unchanged (results/baseline_preservation.csv).

## B. Directed validation results

All 15 original repaired regressions passed again (results/baseline_regressions.csv; full native output in results/baseline_validation.txt). This reproduces the scoped repaired tests; it does not establish the newly requested issuer-success safety property.

## C. Systematic exploration results

One new risk-directed competing-order execution refutes P7 (results/interleaving_results.csv; results/p7_counterexample.txt). It reaches anchored control-flow success despite an invalid issuer signature. The endpoint has zero enabled bindings and zero active unfinished instances, with all 11/11 instances reference-retained (same evidence and results/p7_final_marking.csv). This is an authorization-safety counterexample, not a deadlock. Observed transition coverage, if used, must be cited as directed coverage from results/transition_coverage.csv, never as complete domain reachability.

## D. Exhaustive formal results

None. Reachable-state counts, state-space deadlock totals, boundedness, liveness and exhaustive deadlock freedom are NOT MEASURED/NOT VERIFIED. An actual native counterexample suffices to refute P7's universal statement; no exhaustive verification claim follows.

## E. Performance results

NOT MEASURED. No benchmark repetitions, timing distribution, memory profile or concurrency workload was performed. No simulator time is presented as blockchain performance.

## F. Unverified / stopped work

The remaining interleaving campaign, full finite-domain safety evaluation, multi-instance concurrency and repeated performance experiments were stopped on the discovered defect. No Poisson/MMPP or batching workload was implemented. See NEW_CORRECTNESS_DEFECT.md, PROPERTY_RESULTS.md and the capability/domain/reproducibility reports.

Numbers safe for a paper: the hash-identified structural inventory, the count and outcomes of rerun directed regressions, the single concrete P7 counterexample, and its explicitly scoped endpoint checks. They must not support claims of exhaustive verification, complete failure-security handling or scalability. Publication source CSVs are results/paper_scenario_table.csv, results/paper_property_table.csv and results/paper_performance_table.csv; missing categories remain NOT MEASURED.
