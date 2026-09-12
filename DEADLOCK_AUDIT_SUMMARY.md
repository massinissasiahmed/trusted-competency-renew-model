# Deadlock audit summary

Audit date: 2026-09-11. Repository: `trusted-competency-renew-model`; branch: `feat/renew-functional-integration`; inspected commit: `b9f19c2618904646d2dc712a2620eebeb5a4e907`.

Scope: the saved 11 RNW drawings, compiled and replayed in Renew 4.1 / Java 17. Native deserialization verified every node, normal directed arc, and stored text against `model.tsv`; the manifest was independently reconciled with that TSV. No model, marking, inscription, project script, Java helper, README, or existing validation document was edited. An external temporary observer in `../.validation/deadlock_audit` replayed the existing directed sequences and recorded additional continuations and enabled bindings. Reports are the only repository additions.

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**

All reachability claims below concern witnessed directed executions or explicitly identified structural deductions. Endpoint binding searches inspect the current configuration; they do not enumerate its reachable state space, prove boundedness, or prove liveness. Time is the carried `currentTime` integer, not a clock that advances while waiting. Signatures are matching placeholder strings, not cryptography.

**Overall: FAIL for complete failure termination; PASS for the directed nominal lifecycle.** SBT rejection completes coherently. Evidence rejection, cancellation, expiry and ledger rejection have concrete unfinished global-deadlock witnesses. The invalid-student-signature assertion stops before global quiescence; a valid cancellation continuation reaches the same peer-notification defect.

No model repair was made. The original seven checks still pass their directed assertions; those assertions do not establish failure liveness.

## 1. Does the nominal success scenario terminate correctly?

**PASS** — The directed happy replay reaches every intended successful sink and retains the root references. Native endpoint search finds no further binding: expected termination. This is one witnessed path, not all schedules.

## 2. Are reject/cancel/expire valid terminal executions?

**FAIL** — SBT rejection is valid expected partial termination. Evidence rejection, cancellation and expiry only terminate some participants; unfinished peers remain and settled configurations are globally deadlocked. Valid local object sinks do not make the whole failure protocol complete.

## 3. Which agents remain waiting after each failure branch?

**PASS** — The inventory is complete for measured scenarios: evidence rejection leaves Student (p_Submitted at cutoff, then p_WaitingCredential) and EvidenceObject (p_SubmittedLMS); cancellation/expiry leave Student p_CredentialReceived and Wallet p_PendingConsent; bad signature leaves those same peers while Competency/CredentialObject still permit cancellation; ledger rejection leaves Competency p_BlockchainAnchored and CredentialObject p_Anchored, then HR p_Parsed after the remaining progress. SBT rejection leaves no unfinished activated decision peer. Uninvoked conditional services are listed separately below. PASS means the audit identified the states, not that all states are acceptable.

## 4. Are those waits temporary or permanent?

**FAIL** — The listed failure waits are permanent once their required sender/pending token is lost. At evidence/ledger cutoffs some other local transitions remain enabled; those are temporary remaining work, not a release for the permanent failure dependency. With a fixed bad signature, Student/Wallet cannot decide; valid cancellation remains possible elsewhere but still does not notify them. Successful-path waits, such as HR awaiting nominal aggregation, can be released.

## 5. Are there any confirmed local deadlocks?

**FAIL** — Yes. At the invalid-signature cutoff unfinished Student and Wallet have no complete binding while Competency/CredentialObject can cancel. At the ledger-rejection cutoff Competency/CredentialObject cannot aggregate while Student and Wallet can progress. EvidenceObject is permanently blocked after evidence rejection. Expected completed sinks and uninvoked service readiness are excluded from defect findings.

## 6. Are there any confirmed global deadlocks?

**FAIL** — Yes: measured cancellation and expiry endpoints, the evidence-rejection plus t_WaitCredential continuation, the invalid-signature plus valid-cancellation continuation, and the ledger-rejection plus VP/parse continuation all have zero enabled bindings with unfinished active work. These are concrete witnesses, not a state-space deadlock total. Happy and SBT-reject zero-enabled endpoints are expected termination.

## 7. Are there unmatched synchronous channels?

**FAIL** — Every declared caller resolves to a matching receiver template/name/arity, but dynamic permanent mismatches exist: missing consent after evidence rejection, lost pending accept/reject receivers after cancel/expire, wrong-signature unification failure, missing object evaluate call after rejected assessment, and blocked multiway update_profile after ledger rejection. Locally ready does not mean fully enabled.

## 8. Does any issue invalidate the current paper architecture?

**NOT MEASURED** — The paper specification was not supplied or checked against a formal property set. No finding prevents the measured nominal architecture from executing. Any claim that this implementation guarantees complete failure propagation, commit-before-share, cryptographic verification, or deadlock freedom is unsupported or contradicted by these witnesses. Full paper-architecture validity cannot be inferred from the seven tests.

## 9. Can this branch be safely merged into main as a functional baseline?

**PASS WITH LIMITATIONS** — Only as an explicitly scoped partial prototype that demonstrates the nominal lifecycle and SBT rejection, with D01–D06 recorded as open limitations and no claim of complete failure handling or deadlock freedom. It fails acceptance as a complete end-to-end failure baseline. This is an audit judgment, not a merge action or authorization; nothing was committed, pushed or merged.

## 10. What must be fixed before formal state-space analysis?

**PASS WITH LIMITATIONS** — No model repair is logically required to explore the current defects: analyzing the existing model can confirm counterexamples. Before claiming the intended failure-complete model is verified, resolve evidence notification/assessment persistence, cancel/expire peer termination, signature-denial handling, and ledger failure/order semantics (R1–R6). Before any formal run, specify properties and accepting terminal configurations, finite domains/reference-instance assumptions, logical time and fairness, and a tool/abstraction that supports these Renew reference-net synchronizations. These are analysis prerequisites, not measured bounds. Re-run directed witnesses after any future fixes, then report exhaustive results only if actually obtained.

## Conditional peers versus unfinished participants

After evidence rejection, University.p_Idle, Competency.p_Submitted, Wallet.p_Empty, Ledger.p_Received and HR.p_Idle never received the downstream request. CredentialObject was not created. These inactive services are distinct from the waiting Student and already-created EvidenceObject.

After SBT rejection, cancellation and expiry, Ledger.p_Received and HR.p_Idle are uninvoked service states. Their inactivity is expected. Student/Wallet do terminate on SBT rejection; they do **not** terminate on cancellation or expiry. After ledger rejection, HR eventually receives and parses a VP, so HR.p_Parsed is an unfinished active wait rather than an unused service.

No reference-unreachable instance was observed at any measured endpoint. SystemNet's eight occupied pools are intentional reference repositories after initialization, although their places have outgoing creation arcs.

## Delivered evidence

- [Terminal state audit](TERMINAL_STATE_AUDIT.md): every place, exact initial markings, and transitions entering sinks.
- [Waiting state matrix](WAITING_STATE_MATRIX.md): every non-sink stage, including local continuations and inactive services.
- [Channel liveness audit](CHANNEL_LIVENESS_AUDIT.md): exact caller/receiver interfaces, token patterns, guards, references, and dynamic mismatch cases.
- [Failure scenario final markings](FAILURE_SCENARIO_FINAL_MARKINGS.md): seven original cutoffs, explicit continuations, all instance markings/token values, enabled bindings and reference retention; invalid issuer signature NOT MEASURED.
- [Deadlock findings](DEADLOCK_FINDINGS.md): severities and minimum proposed repairs with topology impact; recommendations only.

Endpoint searches are enabled-binding inspections, not exhaustive state-space exploration. No state count, deadlock count over the state space, boundedness result, cryptographic result, performance result, or liveness proof is reported.

## Repository safety

All previously tracked files were checked by SHA-256 against their pre-audit bytes. The six reports are new, untracked files. No existing tracked file changed. No commit, push or merge was performed. `git diff --stat` is empty because untracked reports do not appear in that command. The final status verification is recorded below.

```text
$ git status
On branch feat/renew-functional-integration
Your branch is up to date with 'origin/feat/renew-functional-integration'.

Untracked files:
  (use "git add <file>..." to include in what will be committed)
	CHANNEL_LIVENESS_AUDIT.md
	DEADLOCK_AUDIT_SUMMARY.md
	DEADLOCK_FINDINGS.md
	FAILURE_SCENARIO_FINAL_MARKINGS.md
	TERMINAL_STATE_AUDIT.md
	WAITING_STATE_MATRIX.md

nothing added to commit but untracked files present (use "git add" to track)

$ git diff --stat
```

`git diff --stat` returned no output. SHA-256 verification: all 24 previously tracked files unchanged.
