# Frozen pilot run

Generated with `scripts/run-pilot.sh pilot/results` on synthetic data (6 tasks, all conditions, budget 1500
approx. tokens, top-k 8). Environment: Spring Boot 3.5.16, Java 17. Output is deterministic; CI regenerates it and
fails on any difference (`scripts/check-results.sh`). After changing code or fixtures, re-run the command above and
commit the new `pilot/results/`.

Reading the numbers: oracles in `pilot/oracles/` are hand-written by the authors from the same rules the evaluator
implements, so CASE's precision is high by construction. The informative parts are the baselines' failures, the
RQ3 ablation costs, and the replay probe. Lineage evidence is redundant in this fixture (flags alone decide every
case), so `CASE_NO_LINEAGE_EVIDENCE` equals full CASE here; fixtures where flags and lineage disagree are future work.
