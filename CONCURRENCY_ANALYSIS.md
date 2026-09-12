# Concurrency analysis

**MULTI-INSTANCE CONCURRENCY NOT MEASURED**

SystemNet has one seed per pool and creates one workflow per root. Native Net.buildInstance can create additional roots, but a valid multi-root driver must follow each root's actual references; the existing BuildProject.instance helper finds by template name and is unsuitable for distinguishing replicas. Multiple engine threads/multiplicity is not evidence of multiple independent lifecycle workloads. No model redesign or extra root was introduced. The campaign stopped on P7 before a controlled multi-root experiment; results/concurrency_results.csv records NOT MEASURED for the requested scales. No scalability or conflict metric is reported.
