# Failure scenario final markings

Audit date: 2026-09-11. Repository: `trusted-competency-renew-model`; branch: `feat/renew-functional-integration`; inspected commit: `b9f19c2618904646d2dc712a2620eebeb5a4e907`.

Scope: the saved 11 RNW drawings, compiled and replayed in Renew 4.1 / Java 17. Native deserialization verified every node, normal directed arc, and stored text against `model.tsv`; the manifest was independently reconciled with that TSV. No model, marking, inscription, project script, Java helper, README, or existing validation document was edited. An external temporary observer in `../.validation/deadlock_audit` replayed the existing directed sequences and recorded additional continuations and enabled bindings. Reports are the only repository additions.

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**

All reachability claims below concern witnessed directed executions or explicitly identified structural deductions. Endpoint binding searches inspect the current configuration; they do not enumerate its reachable state space, prove boundedness, or prove liveness. Time is the carried `currentTime` integer, not a clock that advances while waiting. Signatures are matching placeholder strings, not cryptography.

## Interpretation

- **Local deadlock:** an instance has no enabled local or synchronized transition while the rest of the system may still progress. A temporary peer wait also has no current binding; it is distinguished from a permanently blocked, unfinished instance.
- **Global deadlock:** no transition in the entire reachable configuration can fire. If this is because every activated lifecycle ended coherently, classify it as expected termination instead of a defect.
- **Expected termination:** successful or valid failure sinks, plus conditional peers that never received a request. A sink alone is not a deadlock finding.
- **Protocol wait:** a required peer event may still occur. **Permanent wait:** that event cannot occur again in this execution.
- A transition with a token and satisfied local guard can still lack a synchronous binding. Such a transition is **locally ready but disabled**, not literally “enabled-but-unsynchronizable.” No contradictory full-enabledness claim is made.
- A reference-reachable but inactive object is not an orphan. Root pool tokens retain the eight agents; EvidenceNet and CompetencyNet retain their created objects.

## Measurement method and limits

All seven existing scenario selectors were replayed successfully. The external observer uses the unmodified reader/compiler and directed sequence from `tools/BuildProject.java`; it adds marking output and a non-firing native `SimulatorHelper.searchOnce` search for every spontaneous transition in every registered instance. Each found occurrence lists the full participating transition set, including uplinks. Markings were compared before/after every endpoint search and were unchanged. Uplink-only transitions are not treated as independent initiators.

“Cutoff” means where the existing directed test returns. “Settled” means the separately recorded suffix below has exhausted enabled bindings in that concrete configuration. These are witnesses, not exhaustive exploration or a scheduler/fairness proof. Existing scenario assertions passing do not imply whole-system failure completion.

The seven replays each deserialized, reconciled and compiled all 11 saved drawings. The only fixture overrides are those already present in the validator: grade 40 instead of 80 for evidence rejection; currentTime 100 instead of 0 for expiry; `wrong-signature` instead of `student-signature` for invalid student signature. They affect in-memory drawings only.

## Nominal success (`happy`)

Normal completion. All activated business and object lifecycles reach their successful sinks; root pools retain references. Zero enabled bindings is expected termination.

Additional suffix: None; the original cutoff is already quiescent.

| Participating net | Original test cutoff marking | Measured settled marking |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) |
| StudentAgent.rnw | p_VPShared (one token) | p_VPShared (one token) |
| ProfessorAgent.rnw | p_Approved (one token) | p_Approved (one token) |
| UniversityAgent.rnw | p_CredentialIssued (one token) | p_CredentialIssued (one token) |
| EvidenceNet.rnw | p_Graded (one token) | p_Graded (one token) |
| CompetencyNet.rnw | p_ProfileAggregated (one token) | p_ProfileAggregated (one token) |
| WalletNet.rnw | p_VPShared (one token) | p_VPShared (one token) |
| HEDULedgerNet.rnw | p_Anchored (one token) | p_Anchored (one token) |
| HRAgent.rnw | p_ResultReady (one token) | p_ResultReady (one token) |
| EvidenceObject.rnw | p_Graded (one token) | p_Graded (one token) |
| CredentialObject.rnw | p_Aggregated (one token) | p_Aggregated (one token) |

**cutoff:** No enabled initiating transition or complete synchronized binding found anywhere in this configuration. Reference-reachable instances from SystemNet: 11/11.

**settled:** No enabled initiating transition or complete synchronized binding found anywhere in this configuration. Reference-reachable instances from SystemNet: 11/11.

All unlisted places are empty. Complete token values at the settled endpoint:

```text
CompetencyNet.p_ProfileAggregated = [[CredentialObject[35],WalletNet[4],HEDULedgerNet[10],HRAgent[8],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001]]
CredentialObject.p_Aggregated = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001]]
EvidenceNet.p_Graded = [[EvidenceObject[27],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Anchored = [[did:example:student1,vp-001]]
HRAgent.p_ResultReady = [[did:example:student1,vp-001]]
ProfessorAgent.p_Approved = [[UniversityAgent[16],CompetencyNet[12],WalletNet[4],HEDULedgerNet[10],HRAgent[8],EvidenceNet[14],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_VPShared = [[ProfessorAgent[6],UniversityAgent[16],EvidenceNet[14],CompetencyNet[12],WalletNet[4],HEDULedgerNet[10],HRAgent[8],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature]]
SystemNet.p_CompetencyPool = [CompetencyNet[12]]
SystemNet.p_EvidencePool = [EvidenceNet[14]]
SystemNet.p_HRPool = [HRAgent[8]]
SystemNet.p_LedgerPool = [HEDULedgerNet[10]]
SystemNet.p_ProfessorPool = [ProfessorAgent[6]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[16]]
SystemNet.p_WalletPool = [WalletNet[4]]
UniversityAgent.p_CredentialIssued = [[CompetencyNet[12],WalletNet[4],HEDULedgerNet[10],HRAgent[8],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_VPShared = [[CompetencyNet[12],did:example:issuer1,did:example:student1,credential-001]]
```

## Evidence rejection (`grade_reject`)

Professor/Evidence have valid local rejection sinks. Student remains unfinished and EvidenceObject has no failure notification. The original cutoff is not globally deadlocked; after the enabled local wait step, Student and EvidenceObject wait permanently and the full configuration is a global deadlock. University, Competency, Wallet, Ledger and HR were never invoked for their downstream service.

Additional suffix: StudentAgent.t_WaitCredential.

| Participating net | Original test cutoff marking | Measured settled marking |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) |
| StudentAgent.rnw | p_Submitted (one token) | p_WaitingCredential (one token) |
| ProfessorAgent.rnw | p_Rejected (one token) | p_Rejected (one token) |
| UniversityAgent.rnw | p_Idle (one token) | p_Idle (one token) |
| EvidenceNet.rnw | p_Rejected (one token) | p_Rejected (one token) |
| CompetencyNet.rnw | p_Submitted (one token) | p_Submitted (one token) |
| WalletNet.rnw | p_Empty (one token) | p_Empty (one token) |
| HEDULedgerNet.rnw | p_Received (one token) | p_Received (one token) |
| HRAgent.rnw | p_Idle (one token) | p_Idle (one token) |
| EvidenceObject.rnw | p_SubmittedLMS (one token) | p_SubmittedLMS (one token) |
| CredentialObject.rnw | NOT CREATED (not an empty existing instance) | NOT CREATED (not an empty existing instance) |

**cutoff:** Enabled initiating transitions and synchronization participants: `StudentAgent.t_WaitCredential [StudentAgent.t_WaitCredential]`. Reference-reachable instances from SystemNet: 10/10.

**settled:** No enabled initiating transition or complete synchronized binding found anywhere in this configuration. Reference-reachable instances from SystemNet: 10/10.

All unlisted places are empty. Complete token values at the settled endpoint:

```text
CompetencyNet.p_Submitted = [[did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001]]
EvidenceNet.p_Rejected = [[EvidenceObject[27],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(40),https://example.org/criteria]]
EvidenceObject.p_SubmittedLMS = [[did:example:student1,bafyEvidence001]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Rejected = [[UniversityAgent[16],CompetencyNet[8],WalletNet[4],HEDULedgerNet[10],HRAgent[14],EvidenceNet[12],int(40),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_WaitingCredential = [[ProfessorAgent[6],UniversityAgent[16],EvidenceNet[12],CompetencyNet[8],WalletNet[4],HEDULedgerNet[10],HRAgent[14],int(40),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature]]
SystemNet.p_CompetencyPool = [CompetencyNet[8]]
SystemNet.p_EvidencePool = [EvidenceNet[12]]
SystemNet.p_HRPool = [HRAgent[14]]
SystemNet.p_LedgerPool = [HEDULedgerNet[10]]
SystemNet.p_ProfessorPool = [ProfessorAgent[6]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[16]]
SystemNet.p_WalletPool = [WalletNet[4]]
UniversityAgent.p_Idle = [[did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Empty = [[]]
```

## SBT rejection (`reject`)

Expected partial termination. Student, Wallet, Competency and CredentialObject jointly reach valid rejection/termination sinks. Ledger and HR are intentionally inactive; they never received a transaction or VP. This zero-enabled configuration is coherent failure completion, not a defective deadlock.

Additional suffix: None; the original cutoff is already quiescent.

| Participating net | Original test cutoff marking | Measured settled marking |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) |
| StudentAgent.rnw | p_Rejected (one token) | p_Rejected (one token) |
| ProfessorAgent.rnw | p_Approved (one token) | p_Approved (one token) |
| UniversityAgent.rnw | p_CredentialIssued (one token) | p_CredentialIssued (one token) |
| EvidenceNet.rnw | p_Graded (one token) | p_Graded (one token) |
| CompetencyNet.rnw | p_Terminated (one token) | p_Terminated (one token) |
| WalletNet.rnw | p_Rejected (one token) | p_Rejected (one token) |
| HEDULedgerNet.rnw | p_Received (one token) | p_Received (one token) |
| HRAgent.rnw | p_Idle (one token) | p_Idle (one token) |
| EvidenceObject.rnw | p_Graded (one token) | p_Graded (one token) |
| CredentialObject.rnw | p_Rejected (one token) | p_Rejected (one token) |

**cutoff:** No enabled initiating transition or complete synchronized binding found anywhere in this configuration. Reference-reachable instances from SystemNet: 11/11.

**settled:** No enabled initiating transition or complete synchronized binding found anywhere in this configuration. Reference-reachable instances from SystemNet: 11/11.

All unlisted places are empty. Complete token values at the settled endpoint:

```text
CompetencyNet.p_Terminated = [[CredentialObject[35],WalletNet[6],HEDULedgerNet[12],HRAgent[16],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001]]
CredentialObject.p_Rejected = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001]]
EvidenceNet.p_Graded = [[EvidenceObject[27],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[UniversityAgent[8],CompetencyNet[4],WalletNet[6],HEDULedgerNet[12],HRAgent[16],EvidenceNet[10],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[14],UniversityAgent[8],EvidenceNet[10],CompetencyNet[4],WalletNet[6],HEDULedgerNet[12],HRAgent[16],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature]]
SystemNet.p_CompetencyPool = [CompetencyNet[4]]
SystemNet.p_EvidencePool = [EvidenceNet[10]]
SystemNet.p_HRPool = [HRAgent[16]]
SystemNet.p_LedgerPool = [HEDULedgerNet[12]]
SystemNet.p_ProfessorPool = [ProfessorAgent[14]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[8]]
SystemNet.p_WalletPool = [WalletNet[6]]
UniversityAgent.p_CredentialIssued = [[CompetencyNet[4],WalletNet[6],HEDULedgerNet[12],HRAgent[16],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[4],did:example:issuer1,did:example:student1,credential-001]]
```

## Issuer cancellation (`cancel`)

CredentialObject cancellation and Competency termination are locally valid. Student and Wallet remain active, with no accept/reject binding or cancellation notification. This is an incomplete protocol and a confirmed global deadlock, not a valid end-to-end failure execution.

Additional suffix: None; the original cutoff is already quiescent.

| Participating net | Original test cutoff marking | Measured settled marking |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) |
| StudentAgent.rnw | p_CredentialReceived (one token) | p_CredentialReceived (one token) |
| ProfessorAgent.rnw | p_Approved (one token) | p_Approved (one token) |
| UniversityAgent.rnw | p_CredentialIssued (one token) | p_CredentialIssued (one token) |
| EvidenceNet.rnw | p_Graded (one token) | p_Graded (one token) |
| CompetencyNet.rnw | p_Terminated (one token) | p_Terminated (one token) |
| WalletNet.rnw | p_PendingConsent (one token) | p_PendingConsent (one token) |
| HEDULedgerNet.rnw | p_Received (one token) | p_Received (one token) |
| HRAgent.rnw | p_Idle (one token) | p_Idle (one token) |
| EvidenceObject.rnw | p_Graded (one token) | p_Graded (one token) |
| CredentialObject.rnw | p_Cancelled (one token) | p_Cancelled (one token) |

**cutoff:** No enabled initiating transition or complete synchronized binding found anywhere in this configuration. Reference-reachable instances from SystemNet: 11/11.

**settled:** No enabled initiating transition or complete synchronized binding found anywhere in this configuration. Reference-reachable instances from SystemNet: 11/11.

All unlisted places are empty. Complete token values at the settled endpoint:

```text
CompetencyNet.p_Terminated = [[CredentialObject[35],WalletNet[6],HEDULedgerNet[12],HRAgent[16],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001]]
CredentialObject.p_Cancelled = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001]]
EvidenceNet.p_Graded = [[EvidenceObject[27],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[UniversityAgent[8],CompetencyNet[4],WalletNet[6],HEDULedgerNet[12],HRAgent[16],EvidenceNet[10],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_CredentialReceived = [[ProfessorAgent[14],UniversityAgent[8],EvidenceNet[10],CompetencyNet[4],WalletNet[6],HEDULedgerNet[12],HRAgent[16],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature]]
SystemNet.p_CompetencyPool = [CompetencyNet[4]]
SystemNet.p_EvidencePool = [EvidenceNet[10]]
SystemNet.p_HRPool = [HRAgent[16]]
SystemNet.p_LedgerPool = [HEDULedgerNet[12]]
SystemNet.p_ProfessorPool = [ProfessorAgent[14]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[8]]
SystemNet.p_WalletPool = [WalletNet[6]]
UniversityAgent.p_CredentialIssued = [[CompetencyNet[4],WalletNet[6],HEDULedgerNet[12],HRAgent[16],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_PendingConsent = [[CompetencyNet[4],did:example:issuer1,did:example:student1,credential-001]]
```

## Expiry (`expire`)

Expired object and terminated competency are locally valid. Student and Wallet are not notified and remain unfinished. Zero enabled bindings confirms an incomplete protocol with global deadlock. The expiry time is an in-memory fixture value of 100, not elapsed wall time.

Additional suffix: None; the original cutoff is already quiescent.

| Participating net | Original test cutoff marking | Measured settled marking |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) |
| StudentAgent.rnw | p_CredentialReceived (one token) | p_CredentialReceived (one token) |
| ProfessorAgent.rnw | p_Approved (one token) | p_Approved (one token) |
| UniversityAgent.rnw | p_CredentialIssued (one token) | p_CredentialIssued (one token) |
| EvidenceNet.rnw | p_Graded (one token) | p_Graded (one token) |
| CompetencyNet.rnw | p_Terminated (one token) | p_Terminated (one token) |
| WalletNet.rnw | p_PendingConsent (one token) | p_PendingConsent (one token) |
| HEDULedgerNet.rnw | p_Received (one token) | p_Received (one token) |
| HRAgent.rnw | p_Idle (one token) | p_Idle (one token) |
| EvidenceObject.rnw | p_Graded (one token) | p_Graded (one token) |
| CredentialObject.rnw | p_Expired (one token) | p_Expired (one token) |

**cutoff:** No enabled initiating transition or complete synchronized binding found anywhere in this configuration. Reference-reachable instances from SystemNet: 11/11.

**settled:** No enabled initiating transition or complete synchronized binding found anywhere in this configuration. Reference-reachable instances from SystemNet: 11/11.

All unlisted places are empty. Complete token values at the settled endpoint:

```text
CompetencyNet.p_Terminated = [[CredentialObject[35],WalletNet[6],HEDULedgerNet[14],HRAgent[12],int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001]]
CredentialObject.p_Expired = [[int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001]]
EvidenceNet.p_Graded = [[EvidenceObject[27],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[UniversityAgent[8],CompetencyNet[4],WalletNet[6],HEDULedgerNet[14],HRAgent[12],EvidenceNet[16],int(80),int(50),int(100),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_CredentialReceived = [[ProfessorAgent[10],UniversityAgent[8],EvidenceNet[16],CompetencyNet[4],WalletNet[6],HEDULedgerNet[14],HRAgent[12],int(80),int(50),int(100),int(100),did:example:student1,bafyEvidence001,student-signature]]
SystemNet.p_CompetencyPool = [CompetencyNet[4]]
SystemNet.p_EvidencePool = [EvidenceNet[16]]
SystemNet.p_HRPool = [HRAgent[12]]
SystemNet.p_LedgerPool = [HEDULedgerNet[14]]
SystemNet.p_ProfessorPool = [ProfessorAgent[10]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[8]]
SystemNet.p_WalletPool = [WalletNet[6]]
UniversityAgent.p_CredentialIssued = [[CompetencyNet[4],WalletNet[6],HEDULedgerNet[14],HRAgent[12],int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_PendingConsent = [[CompetencyNet[4],did:example:issuer1,did:example:student1,credential-001]]
```

## Invalid student signature (`bad_signature`)

The original cutoff correctly denies acceptance but does not terminate. Student and Wallet have no binding, while Competency/CredentialObject can cancel: local deadlocks of unfinished peers, not a global deadlock at cutoff. The measured valid-cancellation continuation produces a global deadlock with the same unfinished peers. No retry or signature-denial response exists.

Additional suffix: CompetencyNet.t_SBT_Cancel (synchronizes CredentialObject.t_Cancel). This is one chosen continuation, not the original invalid-signature assertion.

| Participating net | Original test cutoff marking | Measured settled marking |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) |
| StudentAgent.rnw | p_CredentialReceived (one token) | p_CredentialReceived (one token) |
| ProfessorAgent.rnw | p_Approved (one token) | p_Approved (one token) |
| UniversityAgent.rnw | p_CredentialIssued (one token) | p_CredentialIssued (one token) |
| EvidenceNet.rnw | p_Graded (one token) | p_Graded (one token) |
| CompetencyNet.rnw | p_PendingAccept (one token) | p_Terminated (one token) |
| WalletNet.rnw | p_PendingConsent (one token) | p_PendingConsent (one token) |
| HEDULedgerNet.rnw | p_Received (one token) | p_Received (one token) |
| HRAgent.rnw | p_Idle (one token) | p_Idle (one token) |
| EvidenceObject.rnw | p_Graded (one token) | p_Graded (one token) |
| CredentialObject.rnw | p_PendingAccept (one token) | p_Cancelled (one token) |

**cutoff:** Enabled initiating transitions and synchronization participants: `CompetencyNet.t_SBT_Cancel [CompetencyNet.t_SBT_Cancel,CredentialObject.t_Cancel]`. Reference-reachable instances from SystemNet: 11/11.

**settled:** No enabled initiating transition or complete synchronized binding found anywhere in this configuration. Reference-reachable instances from SystemNet: 11/11.

All unlisted places are empty. Complete token values at the settled endpoint:

```text
CompetencyNet.p_Terminated = [[CredentialObject[35],WalletNet[16],HEDULedgerNet[8],HRAgent[14],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001]]
CredentialObject.p_Cancelled = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001]]
EvidenceNet.p_Graded = [[EvidenceObject[27],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[UniversityAgent[10],CompetencyNet[4],WalletNet[16],HEDULedgerNet[8],HRAgent[14],EvidenceNet[12],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_CredentialReceived = [[ProfessorAgent[6],UniversityAgent[10],EvidenceNet[12],CompetencyNet[4],WalletNet[16],HEDULedgerNet[8],HRAgent[14],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,wrong-signature]]
SystemNet.p_CompetencyPool = [CompetencyNet[4]]
SystemNet.p_EvidencePool = [EvidenceNet[12]]
SystemNet.p_HRPool = [HRAgent[14]]
SystemNet.p_LedgerPool = [HEDULedgerNet[8]]
SystemNet.p_ProfessorPool = [ProfessorAgent[6]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[10]]
SystemNet.p_WalletPool = [WalletNet[16]]
UniversityAgent.p_CredentialIssued = [[CompetencyNet[4],WalletNet[16],HEDULedgerNet[8],HRAgent[14],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_PendingConsent = [[CompetencyNet[4],did:example:issuer1,did:example:student1,credential-001]]
```

## Ledger rejection (additional existing scenario) (`ledger_reject`)

The ledger reaches a valid local rejection sink, while Competency and CredentialObject remain blocked on aggregation. Student and Wallet can still generate/share. After that measured suffix HR also waits at p_Parsed and there are no enabled bindings: global deadlock. VPShared does not demonstrate successful ledger commit.

Additional suffix: StudentAgent.t_GenerateVP; StudentAgent.t_ShareVP (HRAgent.t_ReceiveVP); HRAgent.t_ParseVP; WalletNet.t_GenerateVP; WalletNet.t_ShareVP.

| Participating net | Original test cutoff marking | Measured settled marking |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) | p_CompetencyPool (one token); p_EvidencePool (one token); p_HRPool (one token); p_LedgerPool (one token); p_ProfessorPool (one token); p_StudentPool (one token); p_UniversityPool (one token); p_WalletPool (one token) |
| StudentAgent.rnw | p_Accepted (one token) | p_VPShared (one token) |
| ProfessorAgent.rnw | p_Approved (one token) | p_Approved (one token) |
| UniversityAgent.rnw | p_CredentialIssued (one token) | p_CredentialIssued (one token) |
| EvidenceNet.rnw | p_Graded (one token) | p_Graded (one token) |
| CompetencyNet.rnw | p_BlockchainAnchored (one token) | p_BlockchainAnchored (one token) |
| WalletNet.rnw | p_Accepted (one token) | p_VPShared (one token) |
| HEDULedgerNet.rnw | p_Rejected (one token) | p_Rejected (one token) |
| HRAgent.rnw | p_Idle (one token) | p_Parsed (one token) |
| EvidenceObject.rnw | p_Graded (one token) | p_Graded (one token) |
| CredentialObject.rnw | p_Anchored (one token) | p_Anchored (one token) |

**cutoff:** Enabled initiating transitions and synchronization participants: `StudentAgent.t_GenerateVP [StudentAgent.t_GenerateVP]`; `WalletNet.t_GenerateVP [WalletNet.t_GenerateVP]`. Reference-reachable instances from SystemNet: 11/11.

**settled:** No enabled initiating transition or complete synchronized binding found anywhere in this configuration. Reference-reachable instances from SystemNet: 11/11.

All unlisted places are empty. Complete token values at the settled endpoint:

```text
CompetencyNet.p_BlockchainAnchored = [[CredentialObject[35],WalletNet[6],HEDULedgerNet[10],HRAgent[4],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001]]
CredentialObject.p_Anchored = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001]]
EvidenceNet.p_Graded = [[EvidenceObject[27],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Rejected = [[]]
HRAgent.p_Parsed = [[]]
ProfessorAgent.p_Approved = [[UniversityAgent[12],CompetencyNet[8],WalletNet[6],HEDULedgerNet[10],HRAgent[4],EvidenceNet[16],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_VPShared = [[ProfessorAgent[14],UniversityAgent[12],EvidenceNet[16],CompetencyNet[8],WalletNet[6],HEDULedgerNet[10],HRAgent[4],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature]]
SystemNet.p_CompetencyPool = [CompetencyNet[8]]
SystemNet.p_EvidencePool = [EvidenceNet[16]]
SystemNet.p_HRPool = [HRAgent[4]]
SystemNet.p_LedgerPool = [HEDULedgerNet[10]]
SystemNet.p_ProfessorPool = [ProfessorAgent[14]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[12]]
SystemNet.p_WalletPool = [WalletNet[6]]
UniversityAgent.p_CredentialIssued = [[CompetencyNet[8],WalletNet[6],HEDULedgerNet[10],HRAgent[4],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_VPShared = [[CompetencyNet[8],did:example:issuer1,did:example:student1,credential-001]]
```

## Invalid issuer signature

**NOT MEASURED.** The existing validator has no invalid-issuer-signature scenario and this audit did not introduce a new initial-marking fixture. With differing fixed `issuerSignature` values, `CompetencyNet.t_SBT_Cancel` cannot synchronize with `CredentialObject.t_Cancel` by unification (structural deduction). That does not establish a global deadlock: correctly signed student accept/reject may remain available. No final marking is claimed for an unexecuted scenario.

## Structural coverage and orphans

No reference-unreachable registered instance was found at any cutoff or settled endpoint. Root retains all eight agents, EvidenceNet retains EvidenceObject, and CompetencyNet retains CredentialObject when it was created. The rejected evidence object is therefore a stalled reachable instance, not an orphan. This endpoint inspection is not a universal garbage-collection or reachability theorem.

The University rejection branch was not executed and is unreachable from the given root fixtures by the unchanged passing-grade argument. There is no independent University validation-failure notification path, but no observed University.p_CredentialRequested deadlock due to grade failure. Earlier cancellation before consent is structurally possible; that timing was not separately replayed. It can leave Wallet at p_CredentialReceived temporarily, still permitting consent and then the same permanent decision wait.

No reachable-state total, state-space deadlock total, boundedness, performance metric, or general liveness result is claimed.
