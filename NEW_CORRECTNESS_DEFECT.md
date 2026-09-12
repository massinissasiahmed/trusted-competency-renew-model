# Correctness defect: P7 invalid issuer signature can reach success

**STOPPED ON A NEW CORRECTNESS DEFECT, as requested. The RNW model was not repaired or modified.**

P7 has two clauses. Its cancellation-authorization clause holds in this witness: `CompetencyNet.t_SBT_Cancel` is disabled because the issuer strings differ. Its stronger successful-lifecycle prohibition is **FAIL**: with the same mismatch, holder acceptance and the issuer-denial transaction are both enabled. Selecting acceptance consumes the pending token, permanently removes the denial option, and permits ledger anchoring, both VP flows and HR result completion.

This is a safety/policy violation, not a defective global deadlock. The final configuration is coherent control-flow success with no active unfinished instance; it is unacceptable under P7. The invalid-issuer string was changed only in memory using the same fixture mechanism as the existing regression. No transition, guard, arc, token count or domain model was altered.

Why the previous tests passed: they deliberately choose t_DenyInvalidIssuerSignature, demonstrating an available denial path. An available denial path does not exclude a competing successful path. Issuer equality is checked on CredentialObject.t_Cancel, and mismatch on t_DenyInvalidIssuerSignature, but it is not a prerequisite of t_Mint or t_Accept. Competency's t_SBT_Accept requires valid time and the student's signature, not issuer validity. Neither ledger confirmation nor later sharing adds that missing condition. An invalid cancellation signature was previously scoped to cancellation; this analysis explicitly requests the stronger end-to-end property.

No claim of real cryptographic compromise is made: these are simulation strings. Before resuming the campaign, resolve the intended issuer-authorization contract and address the failing property in a separate repair task. Do not narrow P7 merely to turn this result into PASS.

Reproduce with `powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\RunFormalExperiments.ps1`. Expected process exit: **2**, meaning an observed property violation. Exit zero must not be reported as the experiment result. Complete native trace: results/p7_counterexample.txt. Exact instrumentation: results/probe_instrumentation.txt and the generated results/.build/P7Probe.java. Exact final tokens: results/p7_final_marking.csv. Baseline identity: results/environment.json and results/baseline_hashes.csv.

## Native competing bindings and endpoint

```text
EXPECTED DISABLED CompetencyNet.t_SBT_Cancel
P7 COMPETING DENIAL [CompetencyNet.t_DenyInvalidIssuerSignature,CredentialObject.t_DenyInvalidIssuerSignature,StudentAgent.t_AbortReceived,WalletNet.t_AbortConsent]
P7 COMPETING ACCEPTANCE [CompetencyNet.t_SBT_Accept,CredentialObject.t_Accept,HEDULedgerNet.t_Execute,StudentAgent.t_AcceptCredential,WalletNet.t_AcceptCredential]
FINAL TOKEN CompetencyNet.p_ProfileAggregated = [[StudentAgent[2],CredentialObject[39],WalletNet[8],HEDULedgerNet[14],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,wrong-issuer-signature,did:example:student1,vp-001]]
FINAL TOKEN CredentialObject.p_Aggregated = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001]]
FINAL TOKEN EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
FINAL TOKEN EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
FINAL TOKEN HEDULedgerNet.p_Anchored = [[CompetencyNet[4],ANCHORED]]
FINAL TOKEN HRAgent.p_ResultReady = [[did:example:student1,vp-001]]
FINAL TOKEN ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[12],CompetencyNet[4],WalletNet[8],HEDULedgerNet[14],HRAgent[10],EvidenceNet[16],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
FINAL TOKEN StudentAgent.p_VPShared = [[ProfessorAgent[6],UniversityAgent[12],EvidenceNet[16],CompetencyNet[4],WalletNet[8],HEDULedgerNet[14],HRAgent[10],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature]]
FINAL TOKEN SystemNet.p_CompetencyPool = [CompetencyNet[4]]
FINAL TOKEN SystemNet.p_EvidencePool = [EvidenceNet[16]]
FINAL TOKEN SystemNet.p_HRPool = [HRAgent[10]]
FINAL TOKEN SystemNet.p_LedgerPool = [HEDULedgerNet[14]]
FINAL TOKEN SystemNet.p_ProfessorPool = [ProfessorAgent[6]]
FINAL TOKEN SystemNet.p_StudentPool = [StudentAgent[2]]
FINAL TOKEN SystemNet.p_UniversityPool = [UniversityAgent[12]]
FINAL TOKEN SystemNet.p_WalletPool = [WalletNet[8]]
FINAL TOKEN UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[4],WalletNet[8],HEDULedgerNet[14],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
FINAL TOKEN WalletNet.p_VPShared = [[CompetencyNet[4],did:example:issuer1,did:example:student1,credential-001]]
ENABLED BINDINGS 0
ACTIVE UNFINISHED 0
RETAINED REFERENCES 11/11
PROPERTY_VIOLATION P7: invalid issuer signature reached complete anchored success
```
