# Regression report — failure protocol completion

Validated 2026-09-11 on `fix/failure-protocol-completion`, branched from `feat/renew-functional-integration` at `b9f19c2618904646d2dc712a2620eebeb5a4e907`. Renew 4.1, Java 17, Timed Java Compiler with early tokens, sequential engine.

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**

These are directed native-engine witnesses and endpoint binding checks. No deadlock-freedom, boundedness, general liveness, state-space coverage, formal verification, cryptographic security, or performance claim is made. Signatures and identifiers remain simulation strings; ledger anchoring is the modeled workflow event, not an external blockchain transaction. Logical `currentTime` is a fixed carried integer.

The checkpoint precedes all repairs and preserves the six original deadlock audit documents. The current branch has not been committed, pushed or merged. Main was not changed.

## Structural delta

| Drawing | Added places | Added transitions | Added arcs | Reconnected existing arcs |
| --- | --- | --- | --- | --- |
| SystemNet | 0 | 0 | 0 | 0 |
| StudentAgent | 1 | 6 | 12 | 1 |
| ProfessorAgent | 0 | 0 | 0 | 0 |
| UniversityAgent | 0 | 0 | 0 | 0 |
| EvidenceNet | 0 | 0 | 0 | 0 |
| CompetencyNet | 1 | 4 | 8 | 1 |
| WalletNet | 1 | 5 | 10 | 1 |
| HEDULedgerNet | 0 | 0 | 0 | 0 |
| HRAgent | 0 | 0 | 0 | 0 |
| EvidenceObject | 0 | 0 | 0 | 0 |
| CredentialObject | 1 | 4 | 8 | 1 |

Totals: +4 places, +19 transitions, +38 normal arcs; four existing output arcs reconnected. No old place, transition, arc or text figure removed. Eight RNW files changed. SystemNet, EvidenceObject and HRAgent remain byte-identical. All existing initial markings and their token counts are unchanged. No domain Java helper or external dependency was added to any inscription.

## Saved-file preservation check

```text
GEOMETRY PASS CompetencyNet.rnw retained IDs=71 added figures=30 retained node boxes=15 redirected existing arcs=1
GEOMETRY PASS CredentialObject.rnw retained IDs=75 added figures=30 retained node boxes=17 redirected existing arcs=1
GEOMETRY PASS EvidenceNet.rnw retained IDs=47 added figures=0 retained node boxes=11 redirected existing arcs=0
GEOMETRY PASS EvidenceObject.rnw retained IDs=30 added figures=0 retained node boxes=7 redirected existing arcs=0
GEOMETRY PASS HEDULedgerNet.rnw retained IDs=53 added figures=1 retained node boxes=13 redirected existing arcs=0
GEOMETRY PASS HRAgent.rnw retained IDs=53 added figures=0 retained node boxes=13 redirected existing arcs=0
GEOMETRY PASS ProfessorAgent.rnw retained IDs=47 added figures=0 retained node boxes=11 redirected existing arcs=0
GEOMETRY PASS StudentAgent.rnw retained IDs=73 added figures=44 retained node boxes=17 redirected existing arcs=1
GEOMETRY PASS SystemNet.rnw retained IDs=80 added figures=0 retained node boxes=16 redirected existing arcs=0
GEOMETRY PASS UniversityAgent.rnw retained IDs=47 added figures=0 retained node boxes=11 redirected existing arcs=0
GEOMETRY PASS WalletNet.rnw retained IDs=55 added figures=37 retained node boxes=13 redirected existing arcs=1
```

This independently reopens checkpoint/current drawings and compares each retained FigureWithID/type, all old node boxes, retained style attributes, unchanged non-arc text boxes, and unaffected arc geometry. Intentional reconnected arcs are identified separately. Native serialization changes REF indexes as new nested objects are added; REF indexes are serialization references, not stable FigureWithID values. All saved references deserialize correctly.

Renew's native serializer emits trailing spaces, and `model.tsv` uses empty trailing fields. Standard `git diff --check` reports these format-level whitespace warnings. They were retained rather than rewriting the validated native serialization. The source/script/Markdown whitespace check passes. Effective geometry is compared after native deserialization; raw cached text positions can be normalized by Renew's writer while their displayed positions remain unchanged.

## Native and GUI checks

All eleven saved drawings were opened from disk in the actual Renew GUI after closing the unchanged cached drawings, and all eleven names were visible in the Windows menu. Native deserialization and compilation also passed. A separate fresh launcher attempt encountered Renew plugin startup errors (`Server Socket is occupied` / `ConcurrentModificationException`) while other sessions were running; the successful GUI check used an existing Renew session. No launcher change was needed for the RNW repairs.

| Scenario | Final classification | Outcome | Enabled | Active unfinished | Uninvoked | References |
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

All 15 directed assertions are backed by actual final marking inspection and complete binding search. Correct signature paths remain executable, invalid signatures remain unacceptable, and pre-anchor VP steps are negatively checked. Every new receiver has an executed intended witness; incompatible phase alternatives are explicitly excluded by guards.

## Changed supporting files

`model.tsv` and `model_manifest.json` track the saved graph/annotations. `tools/BuildProject.java` verifies annotation ownership, executes expanded scenarios and checks complete endpoints. `Validate.ps1` includes the fifteen scenarios. CHANNEL_MATRIX, MODEL_VALIDATION and this report are rebuilt for the repaired state. README's manual sequence and scope are corrected to include explicit ledger anchoring. The three requested repair/post-repair reports and a compact native evidence file are new. The six original audit reports and old VALIDATION_EVIDENCE remain historical records without edits.

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
