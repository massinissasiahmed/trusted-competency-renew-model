# Interleaving analysis

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT AVAILABLE FOR THE CURRENT REFERENCE-NET TOOLCHAIN**

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**

**SYSTEMATIC DIRECTED EXPLORATION — STOPPED EARLY.**

Exactly one new competing-order probe was executed (results/interleaving_results.csv): invalid issuer denial was enabled concurrently with holder acceptance; acceptance was chosen, followed by actual ledger success and VP/HR progress. It refutes P7. The original fifteen regression sequences were separately rerun; they are not counted as fifteen new interleaving experiments.

The original regressions cover evidence rejection on either side of Student wait, abort phases before/after consent, explicit accept/reject/failure choices, and negative pre-anchor VP checks. They do not enumerate the requested competing schedules. Remaining accept/reject/cancel/expiry races, all Student-versus-Wallet VP schedules, HR ordering variations and the full finite fixture cross-product were **not performed after the defect**, in compliance with the stop rule. No all-interleavings coverage claim is made. Runtime was not measured for this probe; its CSV field says NOT MEASURED.
