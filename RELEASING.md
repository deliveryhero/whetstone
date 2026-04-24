Releasing
=========

Whetstone uses an automated release process triggered by GitHub Releases. The workflow automatically handles testing, publishing, tagging, and preparing the next development version.

## Quick Release Guide

1. **Update Version in gradle.properties**
   ```bash
   # Change VERSION_NAME from X.Y.Z-SNAPSHOT to X.Y.Z
   # Example: 1.1.5-SNAPSHOT → 1.1.5
   vim gradle.properties
   ```

2. **Open a PR for the Release Version**
   `main` is protected — you cannot push directly. Open a PR titled
   `Prepare for release X.Y.Z` containing only the `gradle.properties`
   change above, get review, and merge.
   ```bash
   git checkout -b release/X.Y.Z
   git commit -am "Prepare for release X.Y.Z"
   git push -u origin release/X.Y.Z
   gh pr create --base main --fill
   ```

3. **Create a GitHub Release** (only after the prep PR is merged to `main`)
   - Go to [GitHub Releases](https://github.com/deliveryhero/whetstone/releases/new)
   - Click "Choose a tag" and create a new tag `X.Y.Z` (e.g., `1.2.3`).
   - Write release notes describing changes
   - Click "Publish release"

4. **Automated Publishing**
   - GitHub Actions automatically:
     - Validates version format (must be X.Y.Z, no SNAPSHOT)
     - Runs full test suite
     - Publishes to Maven Central (automatic promotion enabled)
     - Commits next SNAPSHOT version to main (X.Y.Z+1-SNAPSHOT)

5. **Done!**
   - Verify the release on [Maven Central](https://central.sonatype.com/artifact/com.deliveryhero.whetstone/whetstone)
   - Check that main branch was automatically updated to next SNAPSHOT version

## What the Automation Does

When you create a GitHub release:

1. **Version Validation**: Reads `VERSION_NAME` from gradle.properties and validates format (X.Y.Z, no SNAPSHOT)
2. **Testing**: Runs `./gradlew build` to ensure all tests pass
3. **Publishing**: Publishes artifacts to Maven Central with automatic promotion
4. **Next Version**: Automatically commits `X.Y.(Z+1)-SNAPSHOT` to main branch

## Required repo configuration

The post-release SNAPSHOT bump pushes a commit directly to the protected `main` branch.
That push is performed by the `delivery-hero-tech` org bot and depends on:

- The `GH_TOKEN` repo secret (the bot's PAT) being available to
  `.github/workflows/publish-release.yml` — wired via `actions/checkout`'s `token:`
  input so the persisted git credentials carry the bot identity for the rest of the job.
- `delivery-hero-tech` being included in the `main` branch-protection bypass list
  ("Allow specified actors to bypass required pull requests"). Without this the push
  is rejected with `GH006: Protected branch update failed`.

If a fork needs to run this workflow, configure a `GH_TOKEN` secret with an
equivalent bot/PAT and add that identity to the fork's branch-protection bypass list.

## Version Scheme

- **Release versions**: `1.2.3` (no SNAPSHOT suffix) — landed on `main` via the prep PR before you create the GitHub Release
- **Development versions**: `1.2.4-SNAPSHOT` — always on `main` between releases; auto-committed by the bot after each release
- **Tags**: `X.Y.Z` — created by you when you publish the GitHub Release. The workflow never inspects the tag name; only `gradle.properties:VERSION_NAME` is validated.

## Manual Publishing (Fallback)

If the automated workflow is broken and you must publish manually:

```bash
# 1. From a release prep PR branch (gradle.properties already at X.Y.Z), run tests
./gradlew clean build

# 2. Propagate parent properties into the includeBuild plugin module
#    (mirrors what the workflow does — required because the gradle plugin
#    is a composite build and reads its own gradle.properties).
cat gradle.properties >> whetstone-gradle-plugin/gradle.properties

# 3. Publish
./gradlew clean -x test -x lint publish :whetstone-gradle-plugin:publish \
  -PmavenCentralAutomaticPublishing=true

# 4. Restore the working tree (don't commit the cat-append)
git checkout -- whetstone-gradle-plugin/gradle.properties

# 5. Tag and push the tag
git tag -a X.Y.Z -m "Version X.Y.Z"
git push origin X.Y.Z

# 6. Open a follow-up PR bumping gradle.properties to X.Y.(Z+1)-SNAPSHOT
#    (main is protected — you cannot push the bump directly).
```

Note: Using GitHub Releases is strongly recommended as it provides full automation and audit trail.

## Troubleshooting

**`Error: Release version cannot contain SNAPSHOT: X.Y.Z-SNAPSHOT`**
- The tag points at a commit whose `gradle.properties` still has `-SNAPSHOT`. Cause: you created the tag before (or without) merging the prep PR. Fix: merge the prep PR (so `gradle.properties` on `main` reads `X.Y.Z`), delete the tag, and re-create the GitHub Release pointing at the merged prep commit.

**`Error: Invalid version format 'X.Y.Z'. Expected format: X.Y.Z`**
- The workflow validates `VERSION_NAME` from `gradle.properties` (it never inspects the tag). The value must match `^[0-9]+\.[0-9]+\.[0-9]+$` — three dot-separated integers, no suffixes. Fix `gradle.properties` and open a new prep PR.

**Release workflow failed on tests**
- The workflow runs the full test suite before publishing.
- Fix failing tests on `main`, then create a new GitHub Release.

**Maven Central not showing new version**
- Automatic publishing is enabled, but Maven Central can take a few minutes to sync.
- Check [Sonatype Nexus](https://central.sonatype.com/publishing/deployments) for deployment status.

**Next SNAPSHOT version not committed to `main`**
- Most common cause: `GH_TOKEN` is missing or the bot is not on the branch-protection bypass list — see [Required repo configuration](#required-repo-configuration). The default `GITHUB_TOKEN` cannot push to a protected `main`, and the failure surfaces only at the bump step (after the artifact is already published to Maven Central).
- Other causes: check the GitHub Actions logs for the "Bump to next SNAPSHOT version" step.
- Workaround: open a PR manually bumping `gradle.properties` to `X.Y.(Z+1)-SNAPSHOT`.
