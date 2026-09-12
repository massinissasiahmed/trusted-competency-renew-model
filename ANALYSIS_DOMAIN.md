# Finite experimental domain

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT AVAILABLE FOR THE CURRENT REFERENCE-NET TOOLCHAIN**

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**

| Dimension | Representatives |
| --- | --- |
| grade | [80, 40] |
| passing_threshold | 50 |
| currentTime | [0, 100] |
| expiryDate | 100 |
| student_signature | ['student-signature', 'wrong-signature'] |
| expected_student_signature | student-signature |
| issuer_signature | ['issuer-signature', 'wrong-issuer-signature'] |
| expected_issuer_signature | issuer-signature |
| ledger_choice | ['validate/commit/anchor', 'reject from ordered'] |
| holder_choice | ['accept', 'reject'] |
| roots | 1 |
| notes | four binary input dimensions; holder/ledger values are transition choices, not extra tokens or guard overrides; not an assertion that every cross-product tuple is feasible |


These are representative classes for the guards/equality predicates actually present. They are not a proof over arbitrary integers, strings, nulls, inconsistent identifiers or external data. At-expiry represents the >= guard; it does not model advancing time. All DID, evidence, credential and VP fixtures stay fixed and consistent. Initial tokens retain their counts; overrides are applied only to in-memory drawings before compilation.

One SystemNet creates one independent workflow using its one-shot [] seeds. Dynamic creation remains native Renew semantics. Cancellation/expiry/denial are additional competitors, not suppressed by artificial guards. Holder and ledger choices are imposed by the experiment scheduler only where actual bindings exist. Infeasible choices must be recorded as disabled, never forced. The proposed combinations are a finite design domain, not a measured reachable-state count or a claim of complete cross-product coverage.

First risk-directed probe: passing grade, before expiry, valid student signature, invalid issuer signature; choose the enabled holder-accept and ledger-success continuation instead of issuer denial. This investigates the stronger P7 clause before any timing or scalability campaign. If it violates P7, the requested stop rule takes precedence over the remaining experiment plan.
