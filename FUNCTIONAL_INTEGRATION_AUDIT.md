# Functional integration audit — before model changes

Baseline commit: `4c98b05507d7e92ea7223c9e4bf000371297c75e`. Branch: `feat/renew-functional-integration`. Working tree was clean before branching.

## Decision

Pre-integration replay completed with the read-only harness: happy, reject, cancel, expire, grade_reject, and ledger_reject all passed in Renew. RNW byte hashes were identical before and after these tests. This upgrades the alternative-channel enablement evidence below from static analysis to executed witnesses; it does not establish exhaustive reachability or deadlock freedom.

PROCEED with inscription-only integration. The existing graph supports actual Nets-within-Nets synchronization. SystemNet retains eight created NetInstance references in its pool places; those same instances are passed through setup and workflow channels. The baseline native compiler passes, and its previously executed directed successful scenario demonstrates real inter-net firing. No place, transition, or arc change is required for the requested base lifecycle and local reject/cancel/expire terminal branches.

The intended business argument signatures are not all implemented yet. Existing submit/evaluate/mint calls include internal routing parameters or no business data. Integration can replace selected signatures and carry primitive payloads on the same arcs while preserving the existing internal handoff channels where reference transport still requires them.

## Files and serialization inspected

Read README.md, MODEL_VALIDATION.md, model_manifest.json, model.tsv, Validate.ps1, Launch.ps1, tools/BuildProject.java, .gitignore and LICENSE; enumerated tracked files and inspected all eleven RNW drawings. These are version-12 JHotDraw/Renew Storable text files, **not XML**. Nested objects are shared using REF indexes. Coordinates, figure IDs, fonts, colors, connectors, version header, and locator offsets must be retained.

All saved text figures were decoded and compared in order against model.tsv (names, transition inscriptions, arc inscriptions, and initial markings); every comparison passed. The existing Validate.ps1 reopened the eleven drawings through the version-aware loader and compiled them together.

Tooling issue: BuildProject currently regenerates all drawings for any mode other than validate, including smoke. This must be made read-only before rerunning smoke tests on the frozen drawings. Validate.ps1 currently checks compilation only, so its success alone cannot establish channel enablement.

## Markings and retained references

SystemNet has one [] unit/black token per pool. Each creation input arc requires []; each output arc stores its created reference s, p, u, e, c, w, l, or h. These inscriptions already do exactly what phases 2 and 3 require and should remain unchanged. StudentAgent starts with [80,50,0,100]; every other subordinate template has one [] start token. No extra token count is needed.

The Timed Java Compiler (early tokens enabled) and sequential engine are used. This permits StudentAgent setup and object-start transitions to consume the new instance's initial token in the same synchronized creation step.

### Reference provenance

- `this` in SystemNet identifies the current root instance. Local create channels bind each reference through its actual `:new` creation inscription.
- `s` is bound by `s :new StudentAgent`; `s:setup(...)` transfers p,u,e,c,w,l,h to the StudentAgent output token.
- StudentAgent forwards u,c,w,l,h,e to ProfessorAgent through submit/10. Professor forwards c,w,l,h to UniversityAgent through submit/8. University forwards w,l,h to CompetencyNet through submit/7.
- EvidenceNet creates `eo :new EvidenceObject` and stores eo on all successor workflow arcs.
- CompetencyNet creates `co :new CredentialObject`, stores co, and passes its own `this` reference to WalletNet via sbt_mint/1. WalletNet stores this as c.
- Each later downlink target is bound by the incoming tuple/reference arc; no Java lookup, name-to-instance assumption, or global mutable object is used.

## Variables and matching

Undefined baseline variables detected: **none**. Grade, threshold, currentTime and expiryDate originate in StudentAgent's initial tuple, then flow through input arcs and channel arguments. Creation variables originate in :new inscriptions. Every baseline downlink has a matching uplink in the actual target template with the same arity; no name/arity mismatch was detected. Different arities of the same channel name are intentional internal protocols, not interchangeable signatures.

Endpoint table below resolves by target net, channel name, and arity. “Successful trace” refers to the existing directed engine execution; alternative traces require explicit replay before strengthening their status. Bindings are positional: the caller argument list unifies with the shown receiver list.

| Caller | Downlink | Target template / bound reference | Receiver | Uplink | Enablement evidence |
|---|---|---|---|---|---|
| SystemNet.t_CreateStudent | `this:createp(p)` | SystemNet / `this` | SystemNet.t_CreateProfessor | `:createp(p)` | Successful trace |
| SystemNet.t_CreateStudent | `this:createu(u)` | SystemNet / `this` | SystemNet.t_CreateUniversity | `:createu(u)` | Successful trace |
| SystemNet.t_CreateStudent | `this:createe(e)` | SystemNet / `this` | SystemNet.t_CreateEvidence | `:createe(e)` | Successful trace |
| SystemNet.t_CreateStudent | `this:createc(c)` | SystemNet / `this` | SystemNet.t_CreateCompetency | `:createc(c)` | Successful trace |
| SystemNet.t_CreateStudent | `this:createw(w)` | SystemNet / `this` | SystemNet.t_CreateWallet | `:createw(w)` | Successful trace |
| SystemNet.t_CreateStudent | `this:createl(l)` | SystemNet / `this` | SystemNet.t_CreateLedger | `:createl(l)` | Successful trace |
| SystemNet.t_CreateStudent | `this:createh(h)` | SystemNet / `this` | SystemNet.t_CreateHR | `:createh(h)` | Successful trace |
| SystemNet.t_CreateStudent | `s:setup(p,u,e,c,w,l,h)` | StudentAgent / `s` | StudentAgent.t_PrepareEvidence | `:setup(p,u,e,c,w,l,h)` | Successful trace |
| StudentAgent.t_SubmitEvidence | `p:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)` | ProfessorAgent / `p` | ProfessorAgent.t_ReceiveEvidence | `:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)` | Successful trace |
| StudentAgent.t_SubmitEvidence | `e:submit(grade,passing_threshold)` | EvidenceNet / `e` | EvidenceNet.t_IPFS_Upload | `:submit(grade,passing_threshold)` | Successful trace |
| StudentAgent.t_ReceiveCredential | `w:consent()` | WalletNet / `w` | WalletNet.t_RequestConsent | `:consent()` | Successful trace |
| StudentAgent.t_AcceptCredential | `w:sbt_accept()` | WalletNet / `w` | WalletNet.t_AcceptCredential | `:sbt_accept()` | Successful trace |
| StudentAgent.t_RejectCredential | `w:sbt_reject()` | WalletNet / `w` | WalletNet.t_RejectCredential | `:sbt_reject()` | Alternative branch; compile + binding analysis; replay pending |
| StudentAgent.t_ShareVP | `h:submit()` | HRAgent / `h` | HRAgent.t_ReceiveVP | `:submit()` | Successful trace |
| ProfessorAgent.t_AssessEvidence | `e:evaluate()` | EvidenceNet / `e` | EvidenceNet.t_Grade_Assign | `:evaluate()` | Successful trace |
| ProfessorAgent.t_AssessEvidence | `e:evaluate()` | EvidenceNet / `e` | EvidenceNet.t_Reject | `:evaluate()` | Alternative branch; compile + binding analysis; replay pending |
| ProfessorAgent.t_ApproveEvidence | `u:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)` | UniversityAgent / `u` | UniversityAgent.t_ReceiveEvidence | `:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)` | Successful trace |
| UniversityAgent.t_RequestCredential | `c:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate)` | CompetencyNet / `c` | CompetencyNet.t_Review_Init | `:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate)` | Successful trace |
| UniversityAgent.t_IssueCredential | `c:sbt_mint()` | CompetencyNet / `c` | CompetencyNet.t_SBT_Mint | `:sbt_mint()` | Successful trace |
| EvidenceNet.t_IPFS_Upload | `eo:submit()` | EvidenceObject / `eo` | EvidenceObject.t_IPFS_Upload | `:submit()` | Successful trace |
| EvidenceNet.t_LMS_Submit | `eo:lms()` | EvidenceObject / `eo` | EvidenceObject.t_LMS_Submit | `:lms()` | Successful trace |
| EvidenceNet.t_Grade_Assign | `eo:evaluate()` | EvidenceObject / `eo` | EvidenceObject.t_Grade_Assign | `:evaluate()` | Successful trace |
| CompetencyNet.t_Review_Init | `co:evaluate(grade,passing_threshold,currentTime,expiryDate)` | CredentialObject / `co` | CredentialObject.t_StartReview | `:evaluate(grade,passing_threshold,currentTime,expiryDate)` | Successful trace |
| CompetencyNet.t_Verification_Pass | `co:validate()` | CredentialObject / `co` | CredentialObject.t_Validate | `:validate()` | Successful trace |
| CompetencyNet.t_SBT_Mint | `co:sbt_mint()` | CredentialObject / `co` | CredentialObject.t_Mint | `:sbt_mint()` | Successful trace |
| CompetencyNet.t_SBT_Mint | `w:sbt_mint(this)` | WalletNet / `w` | WalletNet.t_ReceiveCredential | `:sbt_mint(c)` | Successful trace |
| CompetencyNet.t_SBT_Accept | `co:sbt_accept()` | CredentialObject / `co` | CredentialObject.t_Accept | `:sbt_accept()` | Successful trace |
| CompetencyNet.t_SBT_Accept | `l:anchor_request()` | HEDULedgerNet / `l` | HEDULedgerNet.t_Execute | `:anchor_request()` | Successful trace |
| CompetencyNet.t_SBT_Reject | `co:sbt_reject()` | CredentialObject / `co` | CredentialObject.t_Reject | `:sbt_reject()` | Alternative branch; compile + binding analysis; replay pending |
| CompetencyNet.t_SBT_Cancel | `co:sbt_cancel()` | CredentialObject / `co` | CredentialObject.t_Cancel | `:sbt_cancel()` | Alternative branch; compile + binding analysis; replay pending |
| CompetencyNet.t_SBT_Expire | `co:expire()` | CredentialObject / `co` | CredentialObject.t_Expire | `:expire()` | Alternative branch; compile + binding analysis; replay pending |
| CompetencyNet.t_Agg_Ingest | `co:update_profile()` | CredentialObject / `co` | CredentialObject.t_Aggregate | `:update_profile()` | Successful trace |
| CompetencyNet.t_Agg_Ingest | `l:update_profile()` | HEDULedgerNet / `l` | HEDULedgerNet.t_Anchor | `:update_profile()` | Successful trace |
| CompetencyNet.t_Agg_Ingest | `h:update_profile()` | HRAgent / `h` | HRAgent.t_BuildProfile | `:update_profile()` | Successful trace |
| WalletNet.t_AcceptCredential | `c:sbt_accept()` | CompetencyNet / `c` | CompetencyNet.t_SBT_Accept | `:sbt_accept()` | Successful trace |
| WalletNet.t_RejectCredential | `c:sbt_reject()` | CompetencyNet / `c` | CompetencyNet.t_SBT_Reject | `:sbt_reject()` | Alternative branch; compile + binding analysis; replay pending |

## Topology boundaries

Base acceptance, local credential rejection/cancellation/expiry, and real synchronous inter-net communication are possible without structural changes. No structural approval is needed for these scoped changes.

Complete rejection notification and recovery across every agent are not present. For example StudentAgent waiting for a credential has no transition receiving evidence rejection, and WalletNet has no cancellation/expiry terminal transition. The requested local terminal branches can be preserved, but a broader “all agents finish every failure scenario” requirement would need additional transitions and arcs. No such structural changes will be made.

## Complete frozen inventory

### SystemNet.rnw

Places: p_StudentPool, p_ProfessorPool, p_UniversityPool, p_EvidencePool, p_CompetencyPool, p_WalletPool, p_LedgerPool, p_HRPool.

Transitions: t_CreateStudent, t_CreateProfessor, t_CreateUniversity, t_CreateEvidence, t_CreateCompetency, t_CreateWallet, t_CreateLedger, t_CreateHR.

Initial markings: p_StudentPool = `[]`, p_ProfessorPool = `[]`, p_UniversityPool = `[]`, p_EvidencePool = `[]`, p_CompetencyPool = `[]`, p_WalletPool = `[]`, p_LedgerPool = `[]`, p_HRPool = `[]`. All others empty.

Transition inscriptions:

- `t_CreateStudent`: `s :new StudentAgent; this:createp(p); this:createu(u); this:createe(e); this:createc(c); this:createw(w); this:createl(l); this:createh(h); s:setup(p,u,e,c,w,l,h)`
- `t_CreateProfessor`: `p :new ProfessorAgent; :createp(p)`
- `t_CreateUniversity`: `u :new UniversityAgent; :createu(u)`
- `t_CreateEvidence`: `e :new EvidenceNet; :createe(e)`
- `t_CreateCompetency`: `c :new CompetencyNet; :createc(c)`
- `t_CreateWallet`: `w :new WalletNet; :createw(w)`
- `t_CreateLedger`: `l :new HEDULedgerNet; :createl(l)`
- `t_CreateHR`: `h :new HRAgent; :createh(h)`

All directed arcs with inscriptions:

- `p_StudentPool -> t_CreateStudent`: `[]`
- `t_CreateStudent -> p_StudentPool`: `s`
- `p_ProfessorPool -> t_CreateProfessor`: `[]`
- `t_CreateProfessor -> p_ProfessorPool`: `p`
- `p_UniversityPool -> t_CreateUniversity`: `[]`
- `t_CreateUniversity -> p_UniversityPool`: `u`
- `p_EvidencePool -> t_CreateEvidence`: `[]`
- `t_CreateEvidence -> p_EvidencePool`: `e`
- `p_CompetencyPool -> t_CreateCompetency`: `[]`
- `t_CreateCompetency -> p_CompetencyPool`: `c`
- `p_WalletPool -> t_CreateWallet`: `[]`
- `t_CreateWallet -> p_WalletPool`: `w`
- `p_LedgerPool -> t_CreateLedger`: `[]`
- `t_CreateLedger -> p_LedgerPool`: `l`
- `p_HRPool -> t_CreateHR`: `[]`
- `t_CreateHR -> p_HRPool`: `h`

### StudentAgent.rnw

Places: p_Idle, p_EvidenceReady, p_Submitted, p_WaitingCredential, p_CredentialReceived, p_Accepted, p_Rejected, p_PresentationReady, p_VPShared.

Transitions: t_PrepareEvidence, t_SubmitEvidence, t_WaitCredential, t_ReceiveCredential, t_AcceptCredential, t_RejectCredential, t_GenerateVP, t_ShareVP.

Initial markings: p_Idle = `[80,50,0,100]`. All others empty.

Transition inscriptions:

- `t_PrepareEvidence`: `:setup(p,u,e,c,w,l,h)`
- `t_SubmitEvidence`: `p:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate); e:submit(grade,passing_threshold)`
- `t_WaitCredential`: `(none)`
- `t_ReceiveCredential`: `w:consent()`
- `t_AcceptCredential`: `w:sbt_accept()`
- `t_RejectCredential`: `w:sbt_reject()`
- `t_GenerateVP`: `(none)`
- `t_ShareVP`: `h:submit()`

All directed arcs with inscriptions:

- `p_Idle -> t_PrepareEvidence`: `[grade,passing_threshold,currentTime,expiryDate]`
- `t_PrepareEvidence -> p_EvidenceReady`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_EvidenceReady -> t_SubmitEvidence`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_SubmitEvidence -> p_Submitted`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_Submitted -> t_WaitCredential`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_WaitCredential -> p_WaitingCredential`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_WaitingCredential -> t_ReceiveCredential`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_ReceiveCredential -> p_CredentialReceived`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_CredentialReceived -> t_AcceptCredential`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_AcceptCredential -> p_Accepted`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_CredentialReceived -> t_RejectCredential`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_RejectCredential -> p_Rejected`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_Accepted -> t_GenerateVP`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_GenerateVP -> p_PresentationReady`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_PresentationReady -> t_ShareVP`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_ShareVP -> p_VPShared`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`

### ProfessorAgent.rnw

Places: p_Idle, p_EvidenceReceived, p_UnderReview, p_Evaluated, p_Approved, p_Rejected.

Transitions: t_ReceiveEvidence, t_StartReview, t_AssessEvidence, t_ApproveEvidence, t_RejectEvidence.

Initial markings: p_Idle = `[]`. All others empty.

Transition inscriptions:

- `t_ReceiveEvidence`: `:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)`
- `t_StartReview`: `(none)`
- `t_AssessEvidence`: `e:evaluate()`
- `t_ApproveEvidence`: `guard grade >= passing_threshold; u:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)`
- `t_RejectEvidence`: `guard grade < passing_threshold`

All directed arcs with inscriptions:

- `p_Idle -> t_ReceiveEvidence`: `[]`
- `t_ReceiveEvidence -> p_EvidenceReceived`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate]`
- `p_EvidenceReceived -> t_StartReview`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate]`
- `t_StartReview -> p_UnderReview`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate]`
- `p_UnderReview -> t_AssessEvidence`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate]`
- `t_AssessEvidence -> p_Evaluated`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate]`
- `p_Evaluated -> t_ApproveEvidence`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate]`
- `t_ApproveEvidence -> p_Approved`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate]`
- `p_Evaluated -> t_RejectEvidence`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate]`
- `t_RejectEvidence -> p_Rejected`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate]`

### UniversityAgent.rnw

Places: p_Idle, p_EvidenceReceived, p_Verifying, p_CredentialRequested, p_CredentialIssued, p_Rejected.

Transitions: t_ReceiveEvidence, t_VerifyEvidence, t_RequestCredential, t_IssueCredential, t_RejectEvidence.

Initial markings: p_Idle = `[]`. All others empty.

Transition inscriptions:

- `t_ReceiveEvidence`: `:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)`
- `t_VerifyEvidence`: `(none)`
- `t_RequestCredential`: `guard grade >= passing_threshold; c:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate)`
- `t_IssueCredential`: `c:sbt_mint()`
- `t_RejectEvidence`: `guard grade < passing_threshold`

All directed arcs with inscriptions:

- `p_Idle -> t_ReceiveEvidence`: `[]`
- `t_ReceiveEvidence -> p_EvidenceReceived`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_EvidenceReceived -> t_VerifyEvidence`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_VerifyEvidence -> p_Verifying`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_Verifying -> t_RequestCredential`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_RequestCredential -> p_CredentialRequested`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_Verifying -> t_RejectEvidence`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_RejectEvidence -> p_Rejected`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_CredentialRequested -> t_IssueCredential`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_IssueCredential -> p_CredentialIssued`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate]`

### EvidenceNet.rnw

Places: p_Created, p_UploadedIPFS, p_SubmittedLMS, p_UnderReview, p_Graded, p_Rejected.

Transitions: t_IPFS_Upload, t_LMS_Submit, t_Review_Request, t_Grade_Assign, t_Reject.

Initial markings: p_Created = `[]`. All others empty.

Transition inscriptions:

- `t_IPFS_Upload`: `:submit(grade,passing_threshold); eo :new EvidenceObject; eo:submit()`
- `t_LMS_Submit`: `eo:lms()`
- `t_Review_Request`: `(none)`
- `t_Grade_Assign`: `:evaluate(); guard grade >= passing_threshold; eo:evaluate()`
- `t_Reject`: `:evaluate(); guard grade < passing_threshold`

All directed arcs with inscriptions:

- `p_Created -> t_IPFS_Upload`: `[]`
- `t_IPFS_Upload -> p_UploadedIPFS`: `[eo,grade,passing_threshold]`
- `p_UploadedIPFS -> t_LMS_Submit`: `[eo,grade,passing_threshold]`
- `t_LMS_Submit -> p_SubmittedLMS`: `[eo,grade,passing_threshold]`
- `p_SubmittedLMS -> t_Review_Request`: `[eo,grade,passing_threshold]`
- `t_Review_Request -> p_UnderReview`: `[eo,grade,passing_threshold]`
- `p_UnderReview -> t_Grade_Assign`: `[eo,grade,passing_threshold]`
- `t_Grade_Assign -> p_Graded`: `[eo,grade,passing_threshold]`
- `p_UnderReview -> t_Reject`: `[eo,grade,passing_threshold]`
- `t_Reject -> p_Rejected`: `[eo,grade,passing_threshold]`

### CompetencyNet.rnw

Places: p_Submitted, p_UnderReview, p_Validated, p_PendingAccept, p_BlockchainAnchored, p_ProfileAggregated, p_Terminated.

Transitions: t_Review_Init, t_Verification_Pass, t_SBT_Mint, t_SBT_Accept, t_SBT_Reject, t_SBT_Cancel, t_SBT_Expire, t_Agg_Ingest.

Initial markings: p_Submitted = `[]`. All others empty.

Transition inscriptions:

- `t_Review_Init`: `:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate); co :new CredentialObject; co:evaluate(grade,passing_threshold,currentTime,expiryDate)`
- `t_Verification_Pass`: `guard grade >= passing_threshold; co:validate()`
- `t_SBT_Mint`: `:sbt_mint(); co:sbt_mint(); w:sbt_mint(this)`
- `t_SBT_Accept`: `:sbt_accept(); guard currentTime < expiryDate; co:sbt_accept(); l:anchor_request()`
- `t_SBT_Reject`: `:sbt_reject(); co:sbt_reject()`
- `t_SBT_Cancel`: `co:sbt_cancel()`
- `t_SBT_Expire`: `guard currentTime >= expiryDate; co:expire()`
- `t_Agg_Ingest`: `co:update_profile(); l:update_profile(); h:update_profile()`

All directed arcs with inscriptions:

- `p_Submitted -> t_Review_Init`: `[]`
- `t_Review_Init -> p_UnderReview`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_UnderReview -> t_Verification_Pass`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_Verification_Pass -> p_Validated`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_Validated -> t_SBT_Mint`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_SBT_Mint -> p_PendingAccept`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_PendingAccept -> t_SBT_Accept`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_SBT_Accept -> p_BlockchainAnchored`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_PendingAccept -> t_SBT_Reject`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_SBT_Reject -> p_Terminated`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_PendingAccept -> t_SBT_Cancel`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_SBT_Cancel -> p_Terminated`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_PendingAccept -> t_SBT_Expire`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_SBT_Expire -> p_Terminated`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `p_BlockchainAnchored -> t_Agg_Ingest`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`
- `t_Agg_Ingest -> p_ProfileAggregated`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]`

### WalletNet.rnw

Places: p_Empty, p_CredentialReceived, p_PendingConsent, p_Accepted, p_Rejected, p_VPReady, p_VPShared.

Transitions: t_ReceiveCredential, t_RequestConsent, t_AcceptCredential, t_RejectCredential, t_GenerateVP, t_ShareVP.

Initial markings: p_Empty = `[]`. All others empty.

Transition inscriptions:

- `t_ReceiveCredential`: `:sbt_mint(c)`
- `t_RequestConsent`: `:consent()`
- `t_AcceptCredential`: `:sbt_accept(); c:sbt_accept()`
- `t_RejectCredential`: `:sbt_reject(); c:sbt_reject()`
- `t_GenerateVP`: `(none)`
- `t_ShareVP`: `(none)`

All directed arcs with inscriptions:

- `p_Empty -> t_ReceiveCredential`: `[]`
- `t_ReceiveCredential -> p_CredentialReceived`: `c`
- `p_CredentialReceived -> t_RequestConsent`: `c`
- `t_RequestConsent -> p_PendingConsent`: `c`
- `p_PendingConsent -> t_AcceptCredential`: `c`
- `t_AcceptCredential -> p_Accepted`: `c`
- `p_PendingConsent -> t_RejectCredential`: `c`
- `t_RejectCredential -> p_Rejected`: `c`
- `p_Accepted -> t_GenerateVP`: `c`
- `t_GenerateVP -> p_VPReady`: `c`
- `p_VPReady -> t_ShareVP`: `c`
- `t_ShareVP -> p_VPShared`: `c`

### HEDULedgerNet.rnw

Places: p_Received, p_Executed, p_Ordered, p_Validated, p_Committed, p_Anchored, p_Rejected.

Transitions: t_Execute, t_Order, t_Validate, t_Commit, t_Anchor, t_Reject.

Initial markings: p_Received = `[]`. All others empty.

Transition inscriptions:

- `t_Execute`: `:anchor_request()`
- `t_Order`: `(none)`
- `t_Validate`: `(none)`
- `t_Commit`: `(none)`
- `t_Anchor`: `:update_profile()`
- `t_Reject`: `(none)`

All directed arcs with inscriptions:

- `p_Received -> t_Execute`: `[]`
- `t_Execute -> p_Executed`: `[]`
- `p_Executed -> t_Order`: `[]`
- `t_Order -> p_Ordered`: `[]`
- `p_Ordered -> t_Validate`: `[]`
- `t_Validate -> p_Validated`: `[]`
- `p_Ordered -> t_Reject`: `[]`
- `t_Reject -> p_Rejected`: `[]`
- `p_Validated -> t_Commit`: `[]`
- `t_Commit -> p_Committed`: `[]`
- `p_Committed -> t_Anchor`: `[]`
- `t_Anchor -> p_Anchored`: `[]`

### HRAgent.rnw

Places: p_Idle, p_VPReceived, p_Parsed, p_CompetencyProfile, p_Matching, p_GapAnalysis, p_ResultReady.

Transitions: t_ReceiveVP, t_ParseVP, t_BuildProfile, t_SemanticMatch, t_GapAnalysis, t_GenerateResult.

Initial markings: p_Idle = `[]`. All others empty.

Transition inscriptions:

- `t_ReceiveVP`: `:submit()`
- `t_ParseVP`: `(none)`
- `t_BuildProfile`: `:update_profile()`
- `t_SemanticMatch`: `(none)`
- `t_GapAnalysis`: `(none)`
- `t_GenerateResult`: `(none)`

All directed arcs with inscriptions:

- `p_Idle -> t_ReceiveVP`: `[]`
- `t_ReceiveVP -> p_VPReceived`: `[]`
- `p_VPReceived -> t_ParseVP`: `[]`
- `t_ParseVP -> p_Parsed`: `[]`
- `p_Parsed -> t_BuildProfile`: `[]`
- `t_BuildProfile -> p_CompetencyProfile`: `[]`
- `p_CompetencyProfile -> t_SemanticMatch`: `[]`
- `t_SemanticMatch -> p_Matching`: `[]`
- `p_Matching -> t_GapAnalysis`: `[]`
- `t_GapAnalysis -> p_GapAnalysis`: `[]`
- `p_GapAnalysis -> t_GenerateResult`: `[]`
- `t_GenerateResult -> p_ResultReady`: `[]`

### EvidenceObject.rnw

Places: p_Created, p_UploadedIPFS, p_SubmittedLMS, p_Graded.

Transitions: t_IPFS_Upload, t_LMS_Submit, t_Grade_Assign.

Initial markings: p_Created = `[]`. All others empty.

Transition inscriptions:

- `t_IPFS_Upload`: `:submit()`
- `t_LMS_Submit`: `:lms()`
- `t_Grade_Assign`: `:evaluate()`

All directed arcs with inscriptions:

- `p_Created -> t_IPFS_Upload`: `[]`
- `t_IPFS_Upload -> p_UploadedIPFS`: `[]`
- `p_UploadedIPFS -> t_LMS_Submit`: `[]`
- `t_LMS_Submit -> p_SubmittedLMS`: `[]`
- `p_SubmittedLMS -> t_Grade_Assign`: `[]`
- `t_Grade_Assign -> p_Graded`: `[]`

### CredentialObject.rnw

Places: p_Submitted, p_UnderReview, p_Validated, p_PendingAccept, p_Anchored, p_Rejected, p_Cancelled, p_Expired, p_Aggregated.

Transitions: t_StartReview, t_Validate, t_Mint, t_Accept, t_Reject, t_Cancel, t_Expire, t_Aggregate.

Initial markings: p_Submitted = `[]`. All others empty.

Transition inscriptions:

- `t_StartReview`: `:evaluate(grade,passing_threshold,currentTime,expiryDate)`
- `t_Validate`: `:validate(); guard grade >= passing_threshold`
- `t_Mint`: `:sbt_mint()`
- `t_Accept`: `:sbt_accept(); guard currentTime < expiryDate`
- `t_Reject`: `:sbt_reject()`
- `t_Cancel`: `:sbt_cancel()`
- `t_Expire`: `:expire(); guard currentTime >= expiryDate`
- `t_Aggregate`: `:update_profile()`

All directed arcs with inscriptions:

- `p_Submitted -> t_StartReview`: `[]`
- `t_StartReview -> p_UnderReview`: `[grade,passing_threshold,currentTime,expiryDate]`
- `p_UnderReview -> t_Validate`: `[grade,passing_threshold,currentTime,expiryDate]`
- `t_Validate -> p_Validated`: `[grade,passing_threshold,currentTime,expiryDate]`
- `p_Validated -> t_Mint`: `[grade,passing_threshold,currentTime,expiryDate]`
- `t_Mint -> p_PendingAccept`: `[grade,passing_threshold,currentTime,expiryDate]`
- `p_PendingAccept -> t_Accept`: `[grade,passing_threshold,currentTime,expiryDate]`
- `t_Accept -> p_Anchored`: `[grade,passing_threshold,currentTime,expiryDate]`
- `p_PendingAccept -> t_Reject`: `[grade,passing_threshold,currentTime,expiryDate]`
- `t_Reject -> p_Rejected`: `[grade,passing_threshold,currentTime,expiryDate]`
- `p_PendingAccept -> t_Cancel`: `[grade,passing_threshold,currentTime,expiryDate]`
- `t_Cancel -> p_Cancelled`: `[grade,passing_threshold,currentTime,expiryDate]`
- `p_PendingAccept -> t_Expire`: `[grade,passing_threshold,currentTime,expiryDate]`
- `t_Expire -> p_Expired`: `[grade,passing_threshold,currentTime,expiryDate]`
- `p_Anchored -> t_Aggregate`: `[grade,passing_threshold,currentTime,expiryDate]`
- `t_Aggregate -> p_Aggregated`: `[grade,passing_threshold,currentTime,expiryDate]`

## Measurements

State-space/reachable states, state-space transitions, deadlock counts, boundedness proof, throughput, latency, TPS, 500-agent concurrency, race conditions, Poisson workloads and MMPP workloads: **NOT MEASURED YET**.
