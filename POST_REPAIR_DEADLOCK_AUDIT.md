# Post-repair deadlock audit

Validated 2026-09-11 on `fix/failure-protocol-completion`, branched from `feat/renew-functional-integration` at `b9f19c2618904646d2dc712a2620eebeb5a4e907`. Renew 4.1, Java 17, Timed Java Compiler with early tokens, sequential engine.

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**

These are directed native-engine witnesses and endpoint binding checks. No deadlock-freedom, boundedness, general liveness, state-space coverage, formal verification, cryptographic security, or performance claim is made. Signatures and identifiers remain simulation strings; ledger anchoring is the modeled workflow event, not an external blockchain transaction. Logical `currentTime` is a fixed carried integer.

The updated read-only `tools/BuildProject.java` loads the actual saved RNW files with Renew's version-aware GUI loader. It compares node names, directed arcs and every inscription **with its owning node/arc** against `model.tsv`, then compiles all templates together. Every scenario uses a fresh JVM. Saved initial markings are unchanged; the existing in-memory grade/time/student-signature overrides remain, and invalid-issuer fixtures change only Competency's issuer-signature input in memory.

The validator fires directed initiating transitions using Renew itself. It records complete native binding participants, per-step markings, and actual final token values. At each endpoint it searches every spontaneous transition in every registered instance with `SimulatorHelper.searchOnce`; uplinks are counted only as partners in complete bindings. Search does not fire; endpoint token values are checked for no change. Exact final places and token counts are checked for **every** instance, including uninvoked services. Active service names are collected from synchronization participants, so an activated service cannot be relabeled inactive to pass. Student is active from setup; root pools are checked separately as reference repositories. Failure tokens must contain the exact expected outcome. References are traversed recursively from SystemNet through tuple tokens, and every registered instance must be retained.

Zero enabled bindings passes only after these checks establish expected success/failure termination, never just because execution stopped. These checks inspect each directed endpoint; they do not explore all reachable configurations.

## Directed endpoint results

All 15 runs terminate at a verified intended configuration. Zero enabled bindings represents expected termination here. There is no confirmed **defective** global deadlock in these tested configurations, and no active participant is left at an intermediate waiting place. This is not a deadlock-freedom result for the whole model.

| Scenario | Classification | Outcome | Enabled bindings | Active unfinished | Uninvoked services | Retained references |
| --- | --- | --- | --- | --- | --- | --- |
| happy | EXPECTED_SUCCESS_TERMINATION | SUCCESS | 0 | 0 | [] | 11/11 |
| reject | EXPECTED_FAILURE_TERMINATION | HOLDER_REJECTED | 0 | 0 | [HEDULedgerNet, HRAgent] | 11/11 |
| cancel | EXPECTED_FAILURE_TERMINATION | CANCELLED | 0 | 0 | [HEDULedgerNet, HRAgent] | 11/11 |
| expire | EXPECTED_FAILURE_TERMINATION | EXPIRED | 0 | 0 | [HEDULedgerNet, HRAgent] | 11/11 |
| grade_reject | EXPECTED_FAILURE_TERMINATION | EVIDENCE_REJECTED | 0 | 0 | [CompetencyNet, HEDULedgerNet, HRAgent, UniversityAgent, WalletNet] | 10/10 |
| ledger_reject | EXPECTED_FAILURE_TERMINATION | LEDGER_REJECTED | 0 | 0 | [HRAgent] | 11/11 |
| bad_signature | EXPECTED_FAILURE_TERMINATION | INVALID_SIGNATURE | 0 | 0 | [HEDULedgerNet, HRAgent] | 11/11 |
| bad_issuer_signature | EXPECTED_FAILURE_TERMINATION | INVALID_ISSUER_SIGNATURE | 0 | 0 | [HEDULedgerNet, HRAgent] | 11/11 |
| grade_reject_waiting | EXPECTED_FAILURE_TERMINATION | EVIDENCE_REJECTED | 0 | 0 | [CompetencyNet, HEDULedgerNet, HRAgent, UniversityAgent, WalletNet] | 10/10 |
| cancel_submitted | EXPECTED_FAILURE_TERMINATION | CANCELLED | 0 | 0 | [HEDULedgerNet, HRAgent] | 11/11 |
| cancel_waiting | EXPECTED_FAILURE_TERMINATION | CANCELLED | 0 | 0 | [HEDULedgerNet, HRAgent] | 11/11 |
| expire_submitted | EXPECTED_FAILURE_TERMINATION | EXPIRED | 0 | 0 | [HEDULedgerNet, HRAgent] | 11/11 |
| expire_waiting | EXPECTED_FAILURE_TERMINATION | EXPIRED | 0 | 0 | [HEDULedgerNet, HRAgent] | 11/11 |
| bad_issuer_signature_submitted | EXPECTED_FAILURE_TERMINATION | INVALID_ISSUER_SIGNATURE | 0 | 0 | [HEDULedgerNet, HRAgent] | 11/11 |
| bad_issuer_signature_waiting | EXPECTED_FAILURE_TERMINATION | INVALID_ISSUER_SIGNATURE | 0 | 0 | [HEDULedgerNet, HRAgent] | 11/11 |

## Active versus inactive

Student starts the requested workflow. Professor/Evidence/Object activation is witnessed on evidence submission and assessment. University/Competency/Object activation occurs only after passing evidence; Wallet activates at mint; Ledger at `anchor_request`; HR only at post-anchor share. The validator checks those participation records against final classifications.

Evidence rejection leaves University.p_Idle, Competency.p_Submitted, Wallet.p_Empty, Ledger.p_Received and HR.p_Idle uninvoked; CredentialObject does not exist. The active Student, Professor, EvidenceNet and EvidenceObject all terminate. In later decision failures Ledger/HR remain uninvoked. In ledger rejection Ledger is active and ends p_Rejected, but HR was never invoked because sharing is gated. These initial service states are not being relabeled active terminal places.

All registered instances remain reachable from SystemNet's eight agent references and parent object-bearing tokens: ten instances including root after evidence rejection, eleven otherwise. Failure tokens retain routing/object references; no orphan was detected.

## Directed dynamic checks

- Evidence rejection is exercised with Student on each side of `t_WaitCredential`; object grading records the failed result in the same rejection assessment transaction.
- Cancel, expire and invalid-issuer denial each execute in all three reachable pending-decision phase pairs. All abort receivers are exercised. Normal holder rejection retains its original synchronized decision chain.
- Wrong student signature has no accept **or reject** binding; denial reaches its explicit finite outcome. Wrong issuer signature has no valid cancel binding. Correct fixtures cannot take the respective denial paths.
- After holder acceptance, and after order, validate and commit, neither Student nor Wallet can generate/share a VP. Per-step marking checks also prohibit anchored/VP-stage tokens before ledger t_Anchor. The successful t_Anchor binding contains Ledger plus all four confirmation transitions.
- Ledger rejection consumes the active awaiting tokens into failure sinks with LEDGER_REJECTED. No artificial ledger anchor or HR result is produced.
- Native final binding search covers every registered instance and complete synchronous transactions, not just the transition the script expected next.

Every implemented downlink resolves to a concrete carried/created reference and matching uplink. Receiver alternatives with guards deliberately excluding an outcome are identified separately in the [channel matrix](CHANNEL_MATRIX.md). No required invocation remains unmatched in the measured runs. A locally token-ready but synchronously disabled transition is not called fully enabled.

## Remaining scope limits

The drawn University rejection guard still contradicts the unchanged passing grade forwarded by Professor in the base root workflow; this pre-existing branch is not a new independent university-rejection scenario. Arbitrary mismatched DID/credential fixtures, multiple roots, advancing time, all scheduling interleavings, fairness, and external service failures have not been exhaustively examined. Expiry/cancel/holder rejection remain competing choices; the model need not choose nominal success automatically. Changes establish the requested finite scenario completions at this abstraction level, not real issuer authentication, real ledger finality or real VP computation.

## GUI and preservation

All eleven saved drawings were opened from disk in the actual Renew GUI after closing the unchanged cached drawings, and all eleven names were visible in the Windows menu. Native deserialization and compilation also passed. A separate fresh launcher attempt encountered Renew plugin startup errors (`Server Socket is occupied` / `ConcurrentModificationException`) while other sessions were running; the successful GUI check used an existing Renew session. No launcher change was needed for the RNW repairs.

The saved-file comparison checks all retained figure IDs/types, node boxes, style attributes and unaffected arc geometry after deserialization. Four intentional arc reconnections are separately recorded. The old six audit reports remain untouched as historical counterexample evidence.

## Acceptance gate

| Acceptance gate | Result |
| --- | --- |
| A. All 11 RNW drawings open? | PASS |
| B. All 11 compile? | PASS |
| C. Nominal lifecycle terminates? | PASS |
| D. Evidence rejection terminates coherently? | PASS |
| E. SBT rejection terminates coherently? | PASS |
| F. Cancellation terminates coherently? | PASS |
| G. Expiry terminates coherently? | PASS |
| H. Invalid signature terminates coherently? | PASS |
| I. Ledger rejection terminates coherently? | PASS |
| J. Commit-before-share enforced? | PASS |
| K. Active permanent waits in tested scenarios? | NO |
| L. Confirmed global deadlock in tested scenarios? | NO |
| M. Exhaustive state-space analysis performed? | NO |
