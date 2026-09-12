# Terminal state audit

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

## All places

Every place appears once. “Terminal” distinguishes structural sinks from completed root pool markings. Initial markings are listed explicitly below; all remaining non-sinks are intermediate. A terminal actor can coexist with a deadlocked peer.

| Net | Place | Terminal? | Expected terminal? | Reason | Outgoing transitions | Synchronization dependency | Risk |
| --- | --- | --- | --- | --- | --- | --- | --- |
| SystemNet.rnw | p_StudentPool | Data-dependent, not a sink | Yes after creation | Initially [] enables creation; the resulting net reference does not match the [] input arc. One-shot root repository. | t_CreateStudent | this → SystemNet.t_CreateProfessor via createp/1; this → SystemNet.t_CreateUniversity via createu/1; this → SystemNet.t_CreateEvidence via createe/1; this → SystemNet.t_CreateCompetency via createc/1; this → SystemNet.t_CreateWallet via createw/1; this → SystemNet.t_CreateLedger via createl/1; this → SystemNet.t_CreateHR via createh/1; s → StudentAgent.t_PrepareEvidence via setup/7 | S0 |
| SystemNet.rnw | p_ProfessorPool | Data-dependent, not a sink | Yes after creation | Initially [] enables creation; the resulting net reference does not match the [] input arc. One-shot root repository. | t_CreateProfessor | SystemNet.t_CreateStudent → this via createp/1 | S0 |
| SystemNet.rnw | p_UniversityPool | Data-dependent, not a sink | Yes after creation | Initially [] enables creation; the resulting net reference does not match the [] input arc. One-shot root repository. | t_CreateUniversity | SystemNet.t_CreateStudent → this via createu/1 | S0 |
| SystemNet.rnw | p_EvidencePool | Data-dependent, not a sink | Yes after creation | Initially [] enables creation; the resulting net reference does not match the [] input arc. One-shot root repository. | t_CreateEvidence | SystemNet.t_CreateStudent → this via createe/1 | S0 |
| SystemNet.rnw | p_CompetencyPool | Data-dependent, not a sink | Yes after creation | Initially [] enables creation; the resulting net reference does not match the [] input arc. One-shot root repository. | t_CreateCompetency | SystemNet.t_CreateStudent → this via createc/1 | S0 |
| SystemNet.rnw | p_WalletPool | Data-dependent, not a sink | Yes after creation | Initially [] enables creation; the resulting net reference does not match the [] input arc. One-shot root repository. | t_CreateWallet | SystemNet.t_CreateStudent → this via createw/1 | S0 |
| SystemNet.rnw | p_LedgerPool | Data-dependent, not a sink | Yes after creation | Initially [] enables creation; the resulting net reference does not match the [] input arc. One-shot root repository. | t_CreateLedger | SystemNet.t_CreateStudent → this via createl/1 | S0 |
| SystemNet.rnw | p_HRPool | Data-dependent, not a sink | Yes after creation | Initially [] enables creation; the resulting net reference does not match the [] input arc. One-shot root repository. | t_CreateHR | SystemNet.t_CreateStudent → this via createh/1 | S0 |
| StudentAgent.rnw | p_Idle | No | Only if this conditional service is never invoked | Initial; outgoing transition continues the lifecycle | t_PrepareEvidence | SystemNet.t_CreateStudent → this via setup/7 | No additional defect witnessed |
| StudentAgent.rnw | p_EvidenceReady | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_SubmitEvidence | p → ProfessorAgent.t_ReceiveEvidence via submit/10; e → EvidenceNet.t_IPFS_Upload via submit/2 | No additional defect witnessed |
| StudentAgent.rnw | p_Submitted | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_WaitCredential | None (local transition) | No additional defect witnessed |
| StudentAgent.rnw | p_WaitingCredential | No | No (active lifecycle) | Intermediate; Wallet stays p_Empty because issuance never starts; consent cannot occur. After the local t_WaitCredential continuation this is permanent. | t_ReceiveCredential | w → WalletNet.t_RequestConsent via consent/0 | See failure findings |
| StudentAgent.rnw | p_CredentialReceived | No | No (active lifecycle) | Intermediate; Accept/reject synchronizes through Wallet and Competency to CredentialObject. Cancel/expire consumes the pending credential; a wrong signature matches neither accept nor reject. | t_AcceptCredential; t_RejectCredential | w → WalletNet.t_AcceptCredential via sbt_accept/1; w → WalletNet.t_RejectCredential via sbt_reject/1 | See failure findings |
| StudentAgent.rnw | p_Accepted | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_GenerateVP | None (local transition) | No additional defect witnessed |
| StudentAgent.rnw | p_Rejected | Yes — structural sink | Yes, locally | Failure sink | None | None after entry | S0 locally; peer failure handling is separate |
| StudentAgent.rnw | p_PresentationReady | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_ShareVP | h → HRAgent.t_ReceiveVP via submit/0 | No additional defect witnessed |
| StudentAgent.rnw | p_VPShared | Yes — structural sink | Yes, locally | Successful local lifecycle sink | None | None after entry | S0 locally; peer failure handling is separate |
| ProfessorAgent.rnw | p_Idle | No | Only if this conditional service is never invoked | Initial; outgoing transition continues the lifecycle | t_ReceiveEvidence | StudentAgent.t_SubmitEvidence → this via submit/10 | No additional defect witnessed |
| ProfessorAgent.rnw | p_EvidenceReceived | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_StartReview | None (local transition) | No additional defect witnessed |
| ProfessorAgent.rnw | p_UnderReview | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_AssessEvidence | e → EvidenceNet.t_Grade_Assign via evaluate/3; e → EvidenceNet.t_Reject via evaluate/3 | No additional defect witnessed |
| ProfessorAgent.rnw | p_Evaluated | No | No (active lifecycle) | Intermediate; Above threshold submits to the ready University; below threshold t_RejectEvidence is local. The unchanged grade chooses the branch. | t_ApproveEvidence; t_RejectEvidence | u → UniversityAgent.t_ReceiveEvidence via submit/8 | No additional defect witnessed |
| ProfessorAgent.rnw | p_Approved | Yes — structural sink | Yes, locally | Successful local lifecycle sink | None | None after entry | S0 locally; peer failure handling is separate |
| ProfessorAgent.rnw | p_Rejected | Yes — structural sink | Yes, locally | Failure sink | None | None after entry | S0 locally; peer failure handling is separate |
| UniversityAgent.rnw | p_Idle | No | Only if this conditional service is never invoked | Initial; Professor rejects before sending submit/8; no university request was received. This is not a failed active issuance. | t_ReceiveEvidence | ProfessorAgent.t_ApproveEvidence → this via submit/8 | No additional defect witnessed |
| UniversityAgent.rnw | p_EvidenceReceived | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_VerifyEvidence | None (local transition) | No additional defect witnessed |
| UniversityAgent.rnw | p_Verifying | No | No (active lifecycle) | Intermediate; Professor submits only a passing grade. Request synchronizes with Competency; the local rejection guard cannot hold on this base path. | t_RequestCredential; t_RejectEvidence | c → CompetencyNet.t_Review_Init via submit/7 | No additional defect witnessed |
| UniversityAgent.rnw | p_CredentialRequested | No | No (active lifecycle) | Intermediate; Competency can validate with the same passing grade then mint with CredentialObject and empty Wallet. No validation-failed trace reaches this place in the provided fixtures. | t_IssueCredential | c → CompetencyNet.t_SBT_Mint via sbt_mint/3 | No additional defect witnessed |
| UniversityAgent.rnw | p_CredentialIssued | Yes — structural sink | Yes, locally | Successful local lifecycle sink | None | None after entry | S0 locally; peer failure handling is separate |
| UniversityAgent.rnw | p_Rejected | Yes — structural sink | Yes, locally | Failure sink | None | None after entry | S1: not reached by base root execution |
| EvidenceNet.rnw | p_Created | No | Only if this conditional service is never invoked | Initial; outgoing transition continues the lifecycle | t_IPFS_Upload | StudentAgent.t_SubmitEvidence → this via submit/2; eo → EvidenceObject.t_IPFS_Upload via submit/2 | No additional defect witnessed |
| EvidenceNet.rnw | p_UploadedIPFS | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_LMS_Submit | eo → EvidenceObject.t_LMS_Submit via lms/0 | No additional defect witnessed |
| EvidenceNet.rnw | p_SubmittedLMS | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_Review_Request | None (local transition) | No additional defect witnessed |
| EvidenceNet.rnw | p_UnderReview | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_Grade_Assign; t_Reject | ProfessorAgent.t_AssessEvidence → this via evaluate/3; eo → EvidenceObject.t_Grade_Assign via evaluate/3 | No additional defect witnessed |
| EvidenceNet.rnw | p_Graded | Yes — structural sink | Yes, locally | Successful local lifecycle sink | None | None after entry | S0 locally; peer failure handling is separate |
| EvidenceNet.rnw | p_Rejected | Yes — structural sink | Yes, locally | Failure sink | None | None after entry | S0 locally; peer failure handling is separate |
| CompetencyNet.rnw | p_Submitted | No | Only if this conditional service is never invoked | Initial; University never sends submit/7. The initial place name does not mean an actual request was received. CredentialObject is not created. | t_Review_Init | UniversityAgent.t_RequestCredential → this via submit/7; co → CredentialObject.t_StartReview via evaluate/4 | No additional defect witnessed |
| CompetencyNet.rnw | p_UnderReview | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_Verification_Pass | co → CredentialObject.t_Validate via validate/0 | No additional defect witnessed |
| CompetencyNet.rnw | p_Validated | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_SBT_Mint | UniversityAgent.t_IssueCredential → this via sbt_mint/3; co → CredentialObject.t_Mint via sbt_mint/3; w → WalletNet.t_ReceiveCredential via sbt_mint/4 | No additional defect witnessed |
| CompetencyNet.rnw | p_PendingAccept | No | No (active lifecycle) | Intermediate; Valid issuer cancellation remains enabled for the bad student signature. Reject and accept cannot match that signature; expiry is disabled at fixed 0 < 100. | t_SBT_Accept; t_SBT_Reject; t_SBT_Cancel; t_SBT_Expire | co → CredentialObject.t_Accept via sbt_accept/1; l → HEDULedgerNet.t_Execute via anchor_request/0; co → CredentialObject.t_Reject via sbt_reject/1; co → CredentialObject.t_Cancel via sbt_cancel/1; co → CredentialObject.t_Expire via expire/0; WalletNet.t_AcceptCredential → this via sbt_accept/1; WalletNet.t_RejectCredential → this via sbt_reject/1 | No additional defect witnessed |
| CompetencyNet.rnw | p_BlockchainAnchored | No | No (active lifecycle) | Intermediate; t_Agg_Ingest requires ledger p_Committed and HR p_Parsed together; ledger p_Rejected is a sink. | t_Agg_Ingest | co → CredentialObject.t_Aggregate via update_profile/2; l → HEDULedgerNet.t_Anchor via update_profile/2; h → HRAgent.t_BuildProfile via update_profile/2 | See failure findings |
| CompetencyNet.rnw | p_ProfileAggregated | Yes — structural sink | Yes, locally | Successful local lifecycle sink | None | None after entry | S0 locally; peer failure handling is separate |
| CompetencyNet.rnw | p_Terminated | Yes — structural sink | Yes, locally | Failure sink | None | None after entry | S3/S4 for unfinished Student/Wallet peers; sink itself valid |
| WalletNet.rnw | p_Empty | No | Only if this conditional service is never invoked | Initial; No mint is sent; wallet has not started a credential lifecycle. Student waiting on this inactive wallet is the defect. | t_ReceiveCredential | CompetencyNet.t_SBT_Mint → this via sbt_mint/4 | No additional defect witnessed |
| WalletNet.rnw | p_CredentialReceived | No | No (active lifecycle) | Intermediate; Student can move p_Submitted to p_WaitingCredential locally and request consent. Cancellation before consent still permits this exchange, then leaves the peers stuck at their decision places (structural deduction). | t_RequestConsent | StudentAgent.t_ReceiveCredential → this via consent/0 | No additional defect witnessed |
| WalletNet.rnw | p_PendingConsent | No | No (active lifecycle) | Intermediate; Both decision receivers require a Competency pending token and matching CredentialObject signature; neither has an abort reception path. | t_AcceptCredential; t_RejectCredential | StudentAgent.t_AcceptCredential → this via sbt_accept/1; StudentAgent.t_RejectCredential → this via sbt_reject/1; c → CompetencyNet.t_SBT_Accept via sbt_accept/1; c → CompetencyNet.t_SBT_Reject via sbt_reject/1 | See failure findings |
| WalletNet.rnw | p_Accepted | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_GenerateVP | None (local transition) | No additional defect witnessed |
| WalletNet.rnw | p_Rejected | Yes — structural sink | Yes, locally | Failure sink | None | None after entry | S0 locally; peer failure handling is separate |
| WalletNet.rnw | p_VPReady | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_ShareVP | None (local transition) | No additional defect witnessed |
| WalletNet.rnw | p_VPShared | Yes — structural sink | Yes, locally | Successful local lifecycle sink | None | None after entry | S0 locally; peer failure handling is separate |
| HEDULedgerNet.rnw | p_Received | No | Only if this conditional service is never invoked | Initial; No anchor_request is sent on evidence rejection, SBT rejection, cancellation, expiry, or blocked signature. Initial [] is a readiness token, not a submitted transaction. | t_Execute | CompetencyNet.t_SBT_Accept → this via anchor_request/0 | No additional defect witnessed |
| HEDULedgerNet.rnw | p_Executed | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_Order | None (local transition) | No additional defect witnessed |
| HEDULedgerNet.rnw | p_Ordered | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_Validate; t_Reject | None (local transition) | No additional defect witnessed |
| HEDULedgerNet.rnw | p_Validated | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_Commit | None (local transition) | No additional defect witnessed |
| HEDULedgerNet.rnw | p_Committed | No | No (active lifecycle) | Intermediate; Aggregation waits for HR to parse Student share; Student sharing does not depend on aggregation, so the nominal dependency is not circular. | t_Anchor | CompetencyNet.t_Agg_Ingest → this via update_profile/2 | No additional defect witnessed |
| HEDULedgerNet.rnw | p_Anchored | Yes — structural sink | Yes, locally | Successful local lifecycle sink | None | None after entry | S0 locally; peer failure handling is separate |
| HEDULedgerNet.rnw | p_Rejected | Yes — structural sink | Yes, locally | Failure sink | None | None after entry | S0 locally; peer failure handling is separate |
| HRAgent.rnw | p_Idle | No | Only if this conditional service is never invoked | Initial; Student never shares after evidence rejection, SBT rejection, cancellation, expiry, or blocked signature. HR has not received a VP. | t_ReceiveVP | StudentAgent.t_ShareVP → this via submit/0 | No additional defect witnessed |
| HRAgent.rnw | p_VPReceived | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_ParseVP | None (local transition) | No additional defect witnessed |
| HRAgent.rnw | p_Parsed | No | No (active lifecycle) | Intermediate; t_BuildProfile is an uplink in Competency.t_Agg_Ingest; ledger rejection prevents the whole transaction. | t_BuildProfile | CompetencyNet.t_Agg_Ingest → this via update_profile/2 | See failure findings |
| HRAgent.rnw | p_CompetencyProfile | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_SemanticMatch | None (local transition) | No additional defect witnessed |
| HRAgent.rnw | p_Matching | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_GapAnalysis | None (local transition) | No additional defect witnessed |
| HRAgent.rnw | p_GapAnalysis | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_GenerateResult | None (local transition) | No additional defect witnessed |
| HRAgent.rnw | p_ResultReady | Yes — structural sink | Yes, locally | Successful local lifecycle sink | None | None after entry | S0 locally; peer failure handling is separate |
| EvidenceObject.rnw | p_Created | No | Only if this conditional service is never invoked | Initial; outgoing transition continues the lifecycle | t_IPFS_Upload | EvidenceNet.t_IPFS_Upload → this via submit/2 | No additional defect witnessed |
| EvidenceObject.rnw | p_UploadedIPFS | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_LMS_Submit | EvidenceNet.t_LMS_Submit → this via lms/0 | No additional defect witnessed |
| EvidenceObject.rnw | p_SubmittedLMS | No | No (active lifecycle) | Intermediate; EvidenceNet.t_Reject omits eo:evaluate; the only remaining object transition t_Grade_Assign has no future caller. | t_Grade_Assign | EvidenceNet.t_Grade_Assign → this via evaluate/3 | See failure findings |
| EvidenceObject.rnw | p_Graded | Yes — structural sink | Yes, locally | Successful local lifecycle sink | None | None after entry | S0 locally; peer failure handling is separate |
| CredentialObject.rnw | p_Submitted | No | Only if this conditional service is never invoked | Initial; outgoing transition continues the lifecycle | t_StartReview | CompetencyNet.t_Review_Init → this via evaluate/4 | No additional defect witnessed |
| CredentialObject.rnw | p_UnderReview | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_Validate | CompetencyNet.t_Verification_Pass → this via validate/0 | No additional defect witnessed |
| CredentialObject.rnw | p_Validated | No | No (active lifecycle) | Intermediate; outgoing transition continues the lifecycle | t_Mint | CompetencyNet.t_SBT_Mint → this via sbt_mint/3 | No additional defect witnessed |
| CredentialObject.rnw | p_PendingAccept | No | No (active lifecycle) | Intermediate; The valid cancellation binding still involves this object. Accept/reject need the stored student signature; expiry needs currentTime >= expiryDate. | t_Accept; t_Reject; t_Cancel; t_Expire | CompetencyNet.t_SBT_Accept → this via sbt_accept/1; CompetencyNet.t_SBT_Reject → this via sbt_reject/1; CompetencyNet.t_SBT_Cancel → this via sbt_cancel/1; CompetencyNet.t_SBT_Expire → this via expire/0 | No additional defect witnessed |
| CredentialObject.rnw | p_Anchored | No | No (active lifecycle) | Intermediate; t_Aggregate participates in the same blocked aggregation; its parent cannot complete the ledger downlink. | t_Aggregate | CompetencyNet.t_Agg_Ingest → this via update_profile/2 | See failure findings |
| CredentialObject.rnw | p_Rejected | Yes — structural sink | Yes, locally | Failure sink | None | None after entry | S0 locally; peer failure handling is separate |
| CredentialObject.rnw | p_Cancelled | Yes — structural sink | Yes, locally | Failure sink | None | None after entry | S3/S4 for unfinished Student/Wallet peers; sink itself valid |
| CredentialObject.rnw | p_Expired | Yes — structural sink | Yes, locally | Failure sink | None | None after entry | S3/S4 for unfinished Student/Wallet peers; sink itself valid |
| CredentialObject.rnw | p_Aggregated | Yes — structural sink | Yes, locally | Successful local lifecycle sink | None | None after entry | S0 locally; peer failure handling is separate |

## Exact saved initial markings

| Net | Initial place | Token expression |
| --- | --- | --- |
| SystemNet.rnw | p_StudentPool | `[]` |
| SystemNet.rnw | p_ProfessorPool | `[]` |
| SystemNet.rnw | p_UniversityPool | `[]` |
| SystemNet.rnw | p_EvidencePool | `[]` |
| SystemNet.rnw | p_CompetencyPool | `[]` |
| SystemNet.rnw | p_WalletPool | `[]` |
| SystemNet.rnw | p_LedgerPool | `[]` |
| SystemNet.rnw | p_HRPool | `[]` |
| StudentAgent.rnw | p_Idle | `[80,50,0,100,"did:example:student1","bafyEvidence001","student-signature"]` |
| ProfessorAgent.rnw | p_Idle | `["did:example:assessor1","https://example.org/criteria"]` |
| UniversityAgent.rnw | p_Idle | `["did:example:issuer1","did:example:student1","credential-001"]` |
| EvidenceNet.rnw | p_Created | `50` |
| CompetencyNet.rnw | p_Submitted | `["did:example:issuer1","did:example:student1","credential-001","issuer-signature","did:example:student1","vp-001"]` |
| WalletNet.rnw | p_Empty | `[]` |
| HEDULedgerNet.rnw | p_Received | `[]` |
| HRAgent.rnw | p_Idle | `[]` |
| EvidenceObject.rnw | p_Created | `[]` |
| CredentialObject.rnw | p_Submitted | `["did:example:issuer1","did:example:student1","credential-001","student-signature","issuer-signature","did:example:student1","vp-001"]` |

Student is created with an idle token but `setup` consumes it during root creation. EvidenceObject and CredentialObject initial tokens similarly participate in their creation synchronization. All other places start empty. Every tested final occupied non-root place contains one token; all eight SystemNet pools retain one reference each.

## Transitions entering sinks

“No continuation” here refers to that instance's token path, not necessarily to the entire system. Root creation transitions are data-terminal after replacing [] with a reference even though their output places have outgoing arcs.

| Net | Transition | Branch | Interpretation |
| --- | --- | --- | --- |
| StudentAgent.rnw | t_RejectCredential | p_CredentialReceived → p_Rejected | Local branch ends; see peer marking report |
| StudentAgent.rnw | t_ShareVP | p_PresentationReady → p_VPShared | Local branch ends; see peer marking report |
| ProfessorAgent.rnw | t_ApproveEvidence | p_Evaluated → p_Approved | Local branch ends; see peer marking report |
| ProfessorAgent.rnw | t_RejectEvidence | p_Evaluated → p_Rejected | Local branch ends; see peer marking report |
| UniversityAgent.rnw | t_RejectEvidence | p_Verifying → p_Rejected | Local branch ends; see peer marking report |
| UniversityAgent.rnw | t_IssueCredential | p_CredentialRequested → p_CredentialIssued | Local branch ends; see peer marking report |
| EvidenceNet.rnw | t_Grade_Assign | p_UnderReview → p_Graded | Local branch ends; see peer marking report |
| EvidenceNet.rnw | t_Reject | p_UnderReview → p_Rejected | Local branch ends; see peer marking report |
| CompetencyNet.rnw | t_SBT_Reject | p_PendingAccept → p_Terminated | Local branch ends; see peer marking report |
| CompetencyNet.rnw | t_SBT_Cancel | p_PendingAccept → p_Terminated | Local branch ends; see peer marking report |
| CompetencyNet.rnw | t_SBT_Expire | p_PendingAccept → p_Terminated | Local branch ends; see peer marking report |
| CompetencyNet.rnw | t_Agg_Ingest | p_BlockchainAnchored → p_ProfileAggregated | Local branch ends; see peer marking report |
| WalletNet.rnw | t_RejectCredential | p_PendingConsent → p_Rejected | Local branch ends; see peer marking report |
| WalletNet.rnw | t_ShareVP | p_VPReady → p_VPShared | Local branch ends; see peer marking report |
| HEDULedgerNet.rnw | t_Reject | p_Ordered → p_Rejected | Local branch ends; see peer marking report |
| HEDULedgerNet.rnw | t_Anchor | p_Committed → p_Anchored | Local branch ends; see peer marking report |
| HRAgent.rnw | t_GenerateResult | p_GapAnalysis → p_ResultReady | Local branch ends; see peer marking report |
| EvidenceObject.rnw | t_Grade_Assign | p_SubmittedLMS → p_Graded | Local branch ends; see peer marking report |
| CredentialObject.rnw | t_Reject | p_PendingAccept → p_Rejected | Local branch ends; see peer marking report |
| CredentialObject.rnw | t_Cancel | p_PendingAccept → p_Cancelled | Local branch ends; see peer marking report |
| CredentialObject.rnw | t_Expire | p_PendingAccept → p_Expired | Local branch ends; see peer marking report |
| CredentialObject.rnw | t_Aggregate | p_Anchored → p_Aggregated | Local branch ends; see peer marking report |

## Important structural distinctions

`UniversityAgent.p_Rejected` is a drawn failure sink but cannot be reached from the provided root marking: `ProfessorAgent.t_ApproveEvidence` only forwards `grade >= passing_threshold`, and neither intervening transition changes those values. There is no measured “validation failure while University waits” in these fixtures.

`CompetencyNet.p_BlockchainAnchored` and `CredentialObject.p_Anchored` are **intermediate**, despite their names. They are reached when acceptance invokes ledger `t_Execute`; ledger commit and anchor have not yet happened. In the ledger-rejection witness they remain occupied permanently.

See [waiting states](WAITING_STATE_MATRIX.md), [measured configurations](FAILURE_SCENARIO_FINAL_MARKINGS.md), and [findings](DEADLOCK_FINDINGS.md).
