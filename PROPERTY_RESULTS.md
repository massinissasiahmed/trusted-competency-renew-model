# Property results

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT AVAILABLE FOR THE CURRENT REFERENCE-NET TOOLCHAIN**

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**

Analysis stopped on P7. PASS for P1/P2 establishes only their existential statements for the supplied fixtures. No universal PASS is inferred from directed tests.

| Property | Name | Status | Scope |
| --- | --- | --- | --- |
| P1 | Nominal reachability | PASS | Existential witness only, finite specified fixtures |
| P2 | Failure terminal reachability | PASS | Existential witness only, finite specified fixtures |
| P3 | No unfinished global deadlock | NOT EXHAUSTIVELY VERIFIED | Partial directed evidence only; full campaign stopped on P7 |
| P4 | Commit-before-share | NOT EXHAUSTIVELY VERIFIED | Partial directed evidence only; full campaign stopped on P7 |
| P5 | Anchoring consistency | NOT EXHAUSTIVELY VERIFIED | Partial directed evidence only; full campaign stopped on P7 |
| P6 | Invalid student signature safety | NOT EXHAUSTIVELY VERIFIED | Partial directed evidence only; full campaign stopped on P7 |
| P7 | Invalid issuer signature safety | FAIL | Native invalid-issuer accepted/anchored success counterexample; cancellation still disabled |
| P8 | Reference retention | NOT EXHAUSTIVELY VERIFIED | Partial directed evidence only; full campaign stopped on P7 |
| P9 | Terminal outcome consistency | NOT EXHAUSTIVELY VERIFIED | Partial directed evidence only; full campaign stopped on P7 |
| P10 | Transition coverage | NOT EXHAUSTIVELY VERIFIED | Partial directed evidence only; full campaign stopped on P7 |


P10 coverage is generated in results/transition_coverage.csv from recorded full binding participants. Bootstrap coverage is separately attributed to successful atomic root creation and typed reference checks. An unobserved transition is not automatically unreachable. University.t_RejectEvidence is structurally inconsistent with the unchanged passing grade forwarded by Professor in this fixture domain, as already documented; this is a structural observation, not a state-space result. P8 reference traversal was checked at endpoints, not every intermediate configuration. P9 labels are checked against the actual chosen branch; the wrong authorization of a success branch is reported under P7, not hidden as a mislabeled cancellation.
