# Model validation — failure protocol completion

Validated 2026-09-11 on `fix/failure-protocol-completion`, branched from `feat/renew-functional-integration` at `b9f19c2618904646d2dc712a2620eebeb5a4e907`. Renew 4.1, Java 17, Timed Java Compiler with early tokens, sequential engine.

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**

These are directed native-engine witnesses and endpoint binding checks. No deadlock-freedom, boundedness, general liveness, state-space coverage, formal verification, cryptographic security, or performance claim is made. Signatures and identifiers remain simulation strings; ledger anchoring is the modeled workflow event, not an external blockchain transaction. Logical `currentTime` is a fixed carried integer.

All eleven saved drawings were opened from disk in the actual Renew GUI after closing the unchanged cached drawings, and all eleven names were visible in the Windows menu. Native deserialization and compilation also passed. A separate fresh launcher attempt encountered Renew plugin startup errors (`Server Socket is occupied` / `ConcurrentModificationException`) while other sessions were running; the successful GUI check used an existing Renew session. No launcher change was needed for the RNW repairs.

The updated read-only `tools/BuildProject.java` loads the actual saved RNW files with Renew's version-aware GUI loader. It compares node names, directed arcs and every inscription **with its owning node/arc** against `model.tsv`, then compiles all templates together. Every scenario uses a fresh JVM. Saved initial markings are unchanged; the existing in-memory grade/time/student-signature overrides remain, and invalid-issuer fixtures change only Competency's issuer-signature input in memory.

The validator fires directed initiating transitions using Renew itself. It records complete native binding participants, per-step markings, and actual final token values. At each endpoint it searches every spontaneous transition in every registered instance with `SimulatorHelper.searchOnce`; uplinks are counted only as partners in complete bindings. Search does not fire; endpoint token values are checked for no change. Exact final places and token counts are checked for **every** instance, including uninvoked services. Active service names are collected from synchronization participants, so an activated service cannot be relabeled inactive to pass. Student is active from setup; root pools are checked separately as reference repositories. Failure tokens must contain the exact expected outcome. References are traversed recursively from SystemNet through tuple tokens, and every registered instance must be retained.

Zero enabled bindings passes only after these checks establish expected success/failure termination, never just because execution stopped. These checks inspect each directed endpoint; they do not explore all reachable configurations.

## Results and reproduction

PASS: all 11 drawings open and compile; 80 places, 87 transitions, 174 ordinary directed arcs. Fifteen directed runs reach checked quiescence with exact expected outcomes, zero active unfinished instances, and retained references. See [final markings](POST_REPAIR_FINAL_MARKINGS.md) and [native evidence](POST_REPAIR_VALIDATION_EVIDENCE.txt).

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Validate.ps1 -Smoke
```

The execution-policy override applies only to this process. `-RenewHome` can point to another compatible Renew installation. Without `-Smoke`, the script only checks saved files and compilation. Neither mode edits RNW files. Each smoke scenario starts a fresh JVM. BuildProject is test tooling, not a Java class referenced by model inscriptions.

For GUI inspection use `Launch.ps1` or open all eleven drawings in an existing Renew window. Select Timed Java Compiler and sequential multiplicity 1. After holder acceptance, manually fire Ledger t_Order, t_Validate, t_Commit, **t_Anchor**, then Student/Wallet VP stages and HR aggregation. Automatic execution may choose a competing valid failure branch.

## Scenarios

| Scenario | Expected terminal class | Outcome | Enabled bindings | Active unfinished | Uninvoked | Retained |
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

## Saved initial markings (unchanged)

| Net | Place | Single token |
| --- | --- | --- |
| SystemNet | p_StudentPool | `[]` |
| SystemNet | p_ProfessorPool | `[]` |
| SystemNet | p_UniversityPool | `[]` |
| SystemNet | p_EvidencePool | `[]` |
| SystemNet | p_CompetencyPool | `[]` |
| SystemNet | p_WalletPool | `[]` |
| SystemNet | p_LedgerPool | `[]` |
| SystemNet | p_HRPool | `[]` |
| StudentAgent | p_Idle | `[80,50,0,100,"did:example:student1","bafyEvidence001","student-signature"]` |
| ProfessorAgent | p_Idle | `["did:example:assessor1","https://example.org/criteria"]` |
| UniversityAgent | p_Idle | `["did:example:issuer1","did:example:student1","credential-001"]` |
| EvidenceNet | p_Created | `50` |
| CompetencyNet | p_Submitted | `["did:example:issuer1","did:example:student1","credential-001","issuer-signature","did:example:student1","vp-001"]` |
| WalletNet | p_Empty | `[]` |
| HEDULedgerNet | p_Received | `[]` |
| HRAgent | p_Idle | `[]` |
| EvidenceObject | p_Created | `[]` |
| CredentialObject | p_Submitted | `["did:example:issuer1","did:example:student1","credential-001","student-signature","issuer-signature","did:example:student1","vp-001"]` |

All other places start empty. New p_AwaitingAnchor places have no seed. EvidenceObject p_Graded means assessment recorded regardless of pass/fail. Student/Wallet p_Rejected tokens retain failure cause. Competency/Object anchored places are entered only on Ledger anchor confirmation. HR remains uninvoked on every pre-share failure and is not relabeled an active terminal state.

## Exact current inventory

### SystemNet.rnw

Places: `p_StudentPool`, `p_ProfessorPool`, `p_UniversityPool`, `p_EvidencePool`, `p_CompetencyPool`, `p_WalletPool`, `p_LedgerPool`, `p_HRPool`.

| Transition | Source → destination | Inscription | Input arc pattern | Output arc pattern |
| --- | --- | --- | --- | --- |
| t_CreateStudent | p_StudentPool → p_StudentPool | `s :new StudentAgent; this:createp(p); this:createu(u); this:createe(e); this:createc(c); this:createw(w); this:createl(l); this:createh(h); s:setup(p,u,e,c,w,l,h)` | `[]` | `s` |
| t_CreateProfessor | p_ProfessorPool → p_ProfessorPool | `p :new ProfessorAgent; :createp(p)` | `[]` | `p` |
| t_CreateUniversity | p_UniversityPool → p_UniversityPool | `u :new UniversityAgent; :createu(u)` | `[]` | `u` |
| t_CreateEvidence | p_EvidencePool → p_EvidencePool | `e :new EvidenceNet; :createe(e)` | `[]` | `e` |
| t_CreateCompetency | p_CompetencyPool → p_CompetencyPool | `c :new CompetencyNet; :createc(c)` | `[]` | `c` |
| t_CreateWallet | p_WalletPool → p_WalletPool | `w :new WalletNet; :createw(w)` | `[]` | `w` |
| t_CreateLedger | p_LedgerPool → p_LedgerPool | `l :new HEDULedgerNet; :createl(l)` | `[]` | `l` |
| t_CreateHR | p_HRPool → p_HRPool | `h :new HRAgent; :createh(h)` | `[]` | `h` |

### StudentAgent.rnw

Places: `p_Idle`, `p_EvidenceReady`, `p_Submitted`, `p_WaitingCredential`, `p_CredentialReceived`, `p_Accepted`, `p_Rejected`, `p_PresentationReady`, `p_VPShared`, `p_AwaitingAnchor`.

| Transition | Source → destination | Inscription | Input arc pattern | Output arc pattern |
| --- | --- | --- | --- | --- |
| t_PrepareEvidence | p_Idle → p_EvidenceReady | `:setup(p,u,e,c,w,l,h)` | `[grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` |
| t_SubmitEvidence | p_EvidenceReady → p_Submitted | `p:submit(this,u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate); e:submit(studentDID,evidenceCID)` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` |
| t_WaitCredential | p_Submitted → p_WaitingCredential | — | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` |
| t_ReceiveCredential | p_WaitingCredential → p_CredentialReceived | `w:consent()` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` |
| t_AcceptCredential | p_CredentialReceived → p_AwaitingAnchor | `w:sbt_accept(studentSignature)` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` |
| t_RejectCredential | p_CredentialReceived → p_Rejected | `w:sbt_reject(studentSignature)` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature,"HOLDER_REJECTED"]` |
| t_GenerateVP | p_Accepted → p_PresentationReady | — | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` |
| t_ShareVP | p_PresentationReady → p_VPShared | `h:submit()` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` |
| t_AbortSubmitted | p_Submitted → p_Rejected | `:abort(outcome); guard (outcome.equals("EVIDENCE_REJECTED") \| outcome.equals("CANCELLED") \| outcome.equals("EXPIRED") \| outcome.equals("INVALID_ISSUER_SIGNATURE"))` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature,outcome]` |
| t_AbortWaiting | p_WaitingCredential → p_Rejected | `:abort(outcome); guard (outcome.equals("EVIDENCE_REJECTED") \| outcome.equals("CANCELLED") \| outcome.equals("EXPIRED") \| outcome.equals("INVALID_ISSUER_SIGNATURE"))` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature,outcome]` |
| t_AbortReceived | p_CredentialReceived → p_Rejected | `:abort(outcome); guard (outcome.equals("CANCELLED") \| outcome.equals("EXPIRED") \| outcome.equals("INVALID_ISSUER_SIGNATURE"))` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature,outcome]` |
| t_AbortAwaitingAnchor | p_AwaitingAnchor → p_Rejected | `:abort(outcome); guard (outcome.equals("LEDGER_REJECTED"))` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature,outcome]` |
| t_DenyInvalidSignature | p_CredentialReceived → p_Rejected | `w:deny(studentSignature)` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature,"INVALID_SIGNATURE"]` |
| t_AnchorConfirmed | p_AwaitingAnchor → p_Accepted | `:anchor_confirm()` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` | `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]` |

### ProfessorAgent.rnw

Places: `p_Idle`, `p_EvidenceReceived`, `p_UnderReview`, `p_Evaluated`, `p_Approved`, `p_Rejected`.

| Transition | Source → destination | Inscription | Input arc pattern | Output arc pattern |
| --- | --- | --- | --- | --- |
| t_ReceiveEvidence | p_Idle → p_EvidenceReceived | `:submit(s,u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)` | `[assessorDID,criteriaURL]` | `[s,u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]` |
| t_StartReview | p_EvidenceReceived → p_UnderReview | — | `[s,u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]` | `[s,u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]` |
| t_AssessEvidence | p_UnderReview → p_Evaluated | `e:evaluate(assessorDID,grade,criteriaURL)` | `[s,u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]` | `[s,u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]` |
| t_ApproveEvidence | p_Evaluated → p_Approved | `guard grade >= passing_threshold; u:submit(s,c,w,l,h,grade,passing_threshold,currentTime,expiryDate)` | `[s,u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]` | `[s,u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]` |
| t_RejectEvidence | p_Evaluated → p_Rejected | `guard grade < passing_threshold; s:abort("EVIDENCE_REJECTED")` | `[s,u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]` | `[s,u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]` |

### UniversityAgent.rnw

Places: `p_Idle`, `p_EvidenceReceived`, `p_Verifying`, `p_CredentialRequested`, `p_CredentialIssued`, `p_Rejected`.

| Transition | Source → destination | Inscription | Input arc pattern | Output arc pattern |
| --- | --- | --- | --- | --- |
| t_ReceiveEvidence | p_Idle → p_EvidenceReceived | `:submit(s,c,w,l,h,grade,passing_threshold,currentTime,expiryDate)` | `[issuerDID,studentDID,credentialData]` | `[s,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]` |
| t_VerifyEvidence | p_EvidenceReceived → p_Verifying | — | `[s,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]` | `[s,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]` |
| t_RequestCredential | p_Verifying → p_CredentialRequested | `guard grade >= passing_threshold; c:submit(s,w,l,h,grade,passing_threshold,currentTime,expiryDate)` | `[s,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]` | `[s,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]` |
| t_IssueCredential | p_CredentialRequested → p_CredentialIssued | `c:sbt_mint(issuerDID,studentDID,credentialData)` | `[s,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]` | `[s,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]` |
| t_RejectEvidence | p_Verifying → p_Rejected | `guard grade < passing_threshold` | `[s,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]` | `[s,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]` |

### EvidenceNet.rnw

Places: `p_Created`, `p_UploadedIPFS`, `p_SubmittedLMS`, `p_UnderReview`, `p_Graded`, `p_Rejected`.

| Transition | Source → destination | Inscription | Input arc pattern | Output arc pattern |
| --- | --- | --- | --- | --- |
| t_IPFS_Upload | p_Created → p_UploadedIPFS | `:submit(studentDID,evidenceCID); eo :new EvidenceObject; eo:submit(studentDID,evidenceCID)` | `passing_threshold` | `[eo,studentDID,evidenceCID,passing_threshold]` |
| t_LMS_Submit | p_UploadedIPFS → p_SubmittedLMS | `eo:lms()` | `[eo,studentDID,evidenceCID,passing_threshold]` | `[eo,studentDID,evidenceCID,passing_threshold]` |
| t_Review_Request | p_SubmittedLMS → p_UnderReview | — | `[eo,studentDID,evidenceCID,passing_threshold]` | `[eo,studentDID,evidenceCID,passing_threshold]` |
| t_Grade_Assign | p_UnderReview → p_Graded | `:evaluate(assessorDID,grade,criteriaURL); guard grade >= passing_threshold; eo:evaluate(assessorDID,grade,criteriaURL)` | `[eo,studentDID,evidenceCID,passing_threshold]` | `[eo,studentDID,evidenceCID,passing_threshold,assessorDID,grade,criteriaURL]` |
| t_Reject | p_UnderReview → p_Rejected | `:evaluate(assessorDID,grade,criteriaURL); guard grade < passing_threshold; eo:evaluate(assessorDID,grade,criteriaURL)` | `[eo,studentDID,evidenceCID,passing_threshold]` | `[eo,studentDID,evidenceCID,passing_threshold,assessorDID,grade,criteriaURL]` |

### CompetencyNet.rnw

Places: `p_Submitted`, `p_UnderReview`, `p_Validated`, `p_PendingAccept`, `p_BlockchainAnchored`, `p_ProfileAggregated`, `p_Terminated`, `p_AwaitingAnchor`.

| Transition | Source → destination | Inscription | Input arc pattern | Output arc pattern |
| --- | --- | --- | --- | --- |
| t_Review_Init | p_Submitted → p_UnderReview | `:submit(s,w,l,h,grade,passing_threshold,currentTime,expiryDate); co :new CredentialObject; co:evaluate(grade,passing_threshold,currentTime,expiryDate)` | `[issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` |
| t_Verification_Pass | p_UnderReview → p_Validated | `guard grade >= passing_threshold; co:validate()` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` |
| t_SBT_Mint | p_Validated → p_PendingAccept | `:sbt_mint(issuerDID,studentDID,credentialData); co:sbt_mint(issuerDID,studentDID,credentialData); w:sbt_mint(this,issuerDID,studentDID,credentialData)` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` |
| t_SBT_Accept | p_PendingAccept → p_AwaitingAnchor | `:sbt_accept(studentSignature); guard currentTime < expiryDate; co:sbt_accept(studentSignature); l:anchor_request(this)` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` |
| t_SBT_Reject | p_PendingAccept → p_Terminated | `:sbt_reject(studentSignature); co:sbt_reject(studentSignature)` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData,"HOLDER_REJECTED"]` |
| t_SBT_Cancel | p_PendingAccept → p_Terminated | `co:sbt_cancel(issuerSignature); s:abort("CANCELLED"); w:abort("CANCELLED")` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData,"CANCELLED"]` |
| t_SBT_Expire | p_PendingAccept → p_Terminated | `guard currentTime >= expiryDate; co:expire(); s:abort("EXPIRED"); w:abort("EXPIRED")` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData,"EXPIRED"]` |
| t_Agg_Ingest | p_BlockchainAnchored → p_ProfileAggregated | `co:update_profile(holderDID,vpData); h:update_profile(holderDID,vpData)` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` |
| t_DenyInvalidSignature | p_PendingAccept → p_Terminated | `:deny(providedSignature); co:deny(providedSignature)` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData,"INVALID_SIGNATURE"]` |
| t_DenyInvalidIssuerSignature | p_PendingAccept → p_Terminated | `co:deny_issuer(issuerSignature); s:abort("INVALID_ISSUER_SIGNATURE"); w:abort("INVALID_ISSUER_SIGNATURE")` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData,"INVALID_ISSUER_SIGNATURE"]` |
| t_AnchorConfirmed | p_AwaitingAnchor → p_BlockchainAnchored | `:anchor_confirm(); co:anchor_confirm(); s:anchor_confirm(); w:anchor_confirm()` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` |
| t_LedgerFailed | p_AwaitingAnchor → p_Terminated | `:ledger_failed(); co:ledger_failed(); s:abort("LEDGER_REJECTED"); w:abort("LEDGER_REJECTED")` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` | `[s,co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData,"LEDGER_REJECTED"]` |

### WalletNet.rnw

Places: `p_Empty`, `p_CredentialReceived`, `p_PendingConsent`, `p_Accepted`, `p_Rejected`, `p_VPReady`, `p_VPShared`, `p_AwaitingAnchor`.

| Transition | Source → destination | Inscription | Input arc pattern | Output arc pattern |
| --- | --- | --- | --- | --- |
| t_ReceiveCredential | p_Empty → p_CredentialReceived | `:sbt_mint(c,issuerDID,studentDID,credentialData)` | `[]` | `[c,issuerDID,studentDID,credentialData]` |
| t_RequestConsent | p_CredentialReceived → p_PendingConsent | `:consent()` | `[c,issuerDID,studentDID,credentialData]` | `[c,issuerDID,studentDID,credentialData]` |
| t_AcceptCredential | p_PendingConsent → p_AwaitingAnchor | `:sbt_accept(studentSignature); c:sbt_accept(studentSignature)` | `[c,issuerDID,studentDID,credentialData]` | `[c,issuerDID,studentDID,credentialData]` |
| t_RejectCredential | p_PendingConsent → p_Rejected | `:sbt_reject(studentSignature); c:sbt_reject(studentSignature)` | `[c,issuerDID,studentDID,credentialData]` | `[c,issuerDID,studentDID,credentialData,"HOLDER_REJECTED"]` |
| t_GenerateVP | p_Accepted → p_VPReady | — | `[c,issuerDID,studentDID,credentialData]` | `[c,issuerDID,studentDID,credentialData]` |
| t_ShareVP | p_VPReady → p_VPShared | — | `[c,issuerDID,studentDID,credentialData]` | `[c,issuerDID,studentDID,credentialData]` |
| t_AbortReceived | p_CredentialReceived → p_Rejected | `:abort(outcome); guard (outcome.equals("CANCELLED") \| outcome.equals("EXPIRED") \| outcome.equals("INVALID_ISSUER_SIGNATURE"))` | `[c,issuerDID,studentDID,credentialData]` | `[c,issuerDID,studentDID,credentialData,outcome]` |
| t_AbortConsent | p_PendingConsent → p_Rejected | `:abort(outcome); guard (outcome.equals("CANCELLED") \| outcome.equals("EXPIRED") \| outcome.equals("INVALID_ISSUER_SIGNATURE"))` | `[c,issuerDID,studentDID,credentialData]` | `[c,issuerDID,studentDID,credentialData,outcome]` |
| t_AbortAwaitingAnchor | p_AwaitingAnchor → p_Rejected | `:abort(outcome); guard (outcome.equals("LEDGER_REJECTED"))` | `[c,issuerDID,studentDID,credentialData]` | `[c,issuerDID,studentDID,credentialData,outcome]` |
| t_DenyInvalidSignature | p_PendingConsent → p_Rejected | `:deny(providedSignature); c:deny(providedSignature)` | `[c,issuerDID,studentDID,credentialData]` | `[c,issuerDID,studentDID,credentialData,"INVALID_SIGNATURE"]` |
| t_AnchorConfirmed | p_AwaitingAnchor → p_Accepted | `:anchor_confirm()` | `[c,issuerDID,studentDID,credentialData]` | `[c,issuerDID,studentDID,credentialData]` |

### HEDULedgerNet.rnw

Places: `p_Received`, `p_Executed`, `p_Ordered`, `p_Validated`, `p_Committed`, `p_Anchored`, `p_Rejected`.

| Transition | Source → destination | Inscription | Input arc pattern | Output arc pattern |
| --- | --- | --- | --- | --- |
| t_Execute | p_Received → p_Executed | `:anchor_request(c)` | `[]` | `[c]` |
| t_Order | p_Executed → p_Ordered | — | `[c]` | `[c]` |
| t_Validate | p_Ordered → p_Validated | — | `[c]` | `[c]` |
| t_Commit | p_Validated → p_Committed | — | `[c]` | `[c]` |
| t_Anchor | p_Committed → p_Anchored | `c:anchor_confirm()` | `[c]` | `[c,"ANCHORED"]` |
| t_Reject | p_Ordered → p_Rejected | `c:ledger_failed()` | `[c]` | `[c,"LEDGER_REJECTED"]` |

### HRAgent.rnw

Places: `p_Idle`, `p_VPReceived`, `p_Parsed`, `p_CompetencyProfile`, `p_Matching`, `p_GapAnalysis`, `p_ResultReady`.

| Transition | Source → destination | Inscription | Input arc pattern | Output arc pattern |
| --- | --- | --- | --- | --- |
| t_ReceiveVP | p_Idle → p_VPReceived | `:submit()` | `[]` | `[]` |
| t_ParseVP | p_VPReceived → p_Parsed | — | `[]` | `[]` |
| t_BuildProfile | p_Parsed → p_CompetencyProfile | `:update_profile(holderDID,vpData)` | `[]` | `[holderDID,vpData]` |
| t_SemanticMatch | p_CompetencyProfile → p_Matching | — | `[holderDID,vpData]` | `[holderDID,vpData]` |
| t_GapAnalysis | p_Matching → p_GapAnalysis | — | `[holderDID,vpData]` | `[holderDID,vpData]` |
| t_GenerateResult | p_GapAnalysis → p_ResultReady | — | `[holderDID,vpData]` | `[holderDID,vpData]` |

### EvidenceObject.rnw

Places: `p_Created`, `p_UploadedIPFS`, `p_SubmittedLMS`, `p_Graded`.

| Transition | Source → destination | Inscription | Input arc pattern | Output arc pattern |
| --- | --- | --- | --- | --- |
| t_IPFS_Upload | p_Created → p_UploadedIPFS | `:submit(studentDID,evidenceCID)` | `[]` | `[studentDID,evidenceCID]` |
| t_LMS_Submit | p_UploadedIPFS → p_SubmittedLMS | `:lms()` | `[studentDID,evidenceCID]` | `[studentDID,evidenceCID]` |
| t_Grade_Assign | p_SubmittedLMS → p_Graded | `:evaluate(assessorDID,grade,criteriaURL)` | `[studentDID,evidenceCID]` | `[studentDID,evidenceCID,assessorDID,grade,criteriaURL]` |

### CredentialObject.rnw

Places: `p_Submitted`, `p_UnderReview`, `p_Validated`, `p_PendingAccept`, `p_Anchored`, `p_Rejected`, `p_Cancelled`, `p_Expired`, `p_Aggregated`, `p_AwaitingAnchor`.

| Transition | Source → destination | Inscription | Input arc pattern | Output arc pattern |
| --- | --- | --- | --- | --- |
| t_StartReview | p_Submitted → p_UnderReview | `:evaluate(grade,passing_threshold,currentTime,expiryDate)` | `[issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` |
| t_Validate | p_UnderReview → p_Validated | `:validate(); guard grade >= passing_threshold` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` |
| t_Mint | p_Validated → p_PendingAccept | `:sbt_mint(issuerDID,studentDID,credentialData)` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` |
| t_Accept | p_PendingAccept → p_AwaitingAnchor | `:sbt_accept(studentSignature); guard currentTime < expiryDate` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` |
| t_Reject | p_PendingAccept → p_Rejected | `:sbt_reject(studentSignature)` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData,"HOLDER_REJECTED"]` |
| t_Cancel | p_PendingAccept → p_Cancelled | `:sbt_cancel(issuerSignature)` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData,"CANCELLED"]` |
| t_Expire | p_PendingAccept → p_Expired | `:expire(); guard currentTime >= expiryDate` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData,"EXPIRED"]` |
| t_Aggregate | p_Anchored → p_Aggregated | `:update_profile(holderDID,vpData)` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` |
| t_DenyInvalidSignature | p_PendingAccept → p_Rejected | `:deny(providedSignature); guard !providedSignature.equals(studentSignature)` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData,"INVALID_SIGNATURE"]` |
| t_DenyInvalidIssuerSignature | p_PendingAccept → p_Rejected | `:deny_issuer(providedIssuerSignature); guard !providedIssuerSignature.equals(issuerSignature)` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData,"INVALID_ISSUER_SIGNATURE"]` |
| t_AnchorConfirmed | p_AwaitingAnchor → p_Anchored | `:anchor_confirm()` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` |
| t_LedgerFailed | p_AwaitingAnchor → p_Rejected | `:ledger_failed()` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData,"LEDGER_REJECTED"]` |

## Limitations

- Placeholder string equality/mismatch models signatures; there is no cryptography, authenticated issuer actor, external IPFS/LMS/blockchain call, real SBT, computed VP, semantic matching algorithm or real ledger finality.
- Fixed logical time must be set deliberately in the expiry fixture; wall-clock waiting does not enable expiry.
- EvidenceNet's threshold is independently seeded to 50, matching the supplied Student threshold. Arbitrary policy/identity mismatches and multiple-root workloads are outside these directed tests.
- The unchanged University rejection branch is inconsistent with the passing grade forwarded by Professor in the base workflow and is not an independent tested university failure.
- All pending consent phase pairs are tested for the implemented abort protocols, but the complete set of schedules and reachable states was not enumerated. No fairness, liveness, boundedness or formal-verification proof is claimed.
- Layout keeps existing node positions and adds right-side protocol columns. Long tuple labels and crossing edges may require scrolling/zooming; native readability and opening are validated, not a publication-layout redesign.

State-space size, reachable-state counts, deadlock totals over the state space, boundedness proof, throughput, latency, TPS, concurrency benchmarks and workload experiments: **NOT MEASURED**.

## RNW hashes

| File | Checkpoint SHA-256 | Repaired SHA-256 |
| --- | --- | --- |
| SystemNet.rnw | `04bc84a59bff43a5ec27947f14635163f472e1e28d433d0a3ce4c9382a7553c3` | `04bc84a59bff43a5ec27947f14635163f472e1e28d433d0a3ce4c9382a7553c3` |
| StudentAgent.rnw | `133532724e32604450742a28f3ae8d1b6c5b327de8df0d06d201ab8109eaf3a5` | `8b07a2406267b8bd964b75d8565ac7a49262f5b40e4e83c90bf5f244c2ea2ab8` |
| ProfessorAgent.rnw | `df197490580639c655631b3d299513ed1bcab92e46a82e5788a5aa0b647fbae5` | `a169f319dc89df8896459ee8d231bbfce6a00bf62178ff5f2814ca4a1be4ef35` |
| UniversityAgent.rnw | `a3b0acd2e1239ea339c100816c46378c236afe2ce89ad0559a86dce7d98ef724` | `95725af7b9bd57e7f7665e2ca74a5594b833ace785882cc880633e989c715967` |
| EvidenceNet.rnw | `9870d408906cbf2ec3f8587ce406423bd30926efe5b3d72ef782e0581794734a` | `76e6d3c9489e2ec0ffbfb84ad23414975a3b502ecc0ec8163e5b5a35397d56a0` |
| CompetencyNet.rnw | `a5eb0638e67693ca36d09ccfc1ff62dad4a152f613b63f04dec154c6f849c42f` | `e5cbe12f12445d86955d985009d9c61922f824051ef8fec82c6c4f90af4e2a7a` |
| WalletNet.rnw | `4ee79412f4d8d59cbf1c89fa23ae6d18565206a6e2bebc4ec53b8ef3347dd5cb` | `ecbc5bb8e4bb302ec121380ea058f265f164c3c90849e2a4edd7befa166ada8a` |
| HEDULedgerNet.rnw | `d64298af1fb12da370d1fc7946cde44333dca5c8f01d4d1b18712f2e5049c110` | `0ff9aae085212dd0233559149801ea37033b255eb0139fc6a77fd924451544c4` |
| HRAgent.rnw | `1e68fa0ad1ac2851276cdd79ff8038bc31f01f8b15a5c872eeec26b7f9a72fab` | `1e68fa0ad1ac2851276cdd79ff8038bc31f01f8b15a5c872eeec26b7f9a72fab` |
| EvidenceObject.rnw | `80d7e066372fbc8ebec486a486082362f7e13abe99c5c9dc47f4c5997bbba08a` | `80d7e066372fbc8ebec486a486082362f7e13abe99c5c9dc47f4c5997bbba08a` |
| CredentialObject.rnw | `c5b15e6fc8fce1c88eddb30854b0f7fafa7886cc352462df4e06f585a54fd444` | `559bf6314e8a9fd0dde584f70ac4205a4ad1087b3afc6d7852f7f0b64a6f2064` |
