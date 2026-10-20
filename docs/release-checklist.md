# Release checklist

The repository is public on GitHub under the author's own account. Reviewers get an anonymized view through
anonymous.4open.science, which hides the account, commit history and any configured terms.

## Before submission (double-anonymous review)
- [ ] File contents contain no author or employer names (`grep -ri` for them); domain terms stay generic.
- [ ] `./mvnw verify`, `scripts/check-results.sh` and `experiments/agent-demo/scripts/check-reference-solutions.sh` pass.
- [ ] Push to the public GitHub repo.
- [ ] Create an anonymous.4open.science mirror of the repo; add the author's name, GitHub username, email and
      employer as terms to anonymize; cite the mirror URL (not the GitHub URL) in the submitted paper.
- [ ] Legal review of publishing the repository is complete (license decision pending).

## After acceptance
- [ ] Add the chosen `LICENSE` (delete `LICENSE.TODO`) and a license note in the README.
- [ ] Add authors, `CITATION.cff` and the "How to cite" section.
- [ ] Archive a tagged release on Zenodo and cite the DOI and GitHub URL in the camera-ready paper.
- [ ] Update the README badges and the artifact link in the paper.
