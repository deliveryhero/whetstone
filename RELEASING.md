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

2. **Commit the Release Version**
   ```bash
   git commit -am "Prepare for release X.Y.Z"
   git push origin main
   ```

3. **Create a GitHub Release**
   - Go to [GitHub Releases](https://github.com/deliveryhero/whetstone/releases/new)
   - Click "Choose a tag" and create a new tag `vX.Y.Z` (e.g., `v1.2.3`)
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

## Version Scheme

- **Release versions**: `1.2.3` (no SNAPSHOT suffix) - committed to main before creating GitHub release
- **Development versions**: `1.2.4-SNAPSHOT` (always on main branch between releases)
- **Tags**: `v1.2.3` (automatically created by workflow with 'v' prefix)

## Manual Publishing (Alternative)

If you need to publish manually without GitHub Releases:

```bash
# 1. Update version in gradle.properties (remove -SNAPSHOT)
# 2. Run tests
./gradlew clean build

# 3. Publish
./gradlew clean publish :whetstone-gradle-plugin:publish -PmavenCentralAutomaticPublishing=true

# 4. Create git tag
git tag -a vX.Y.Z -m "Version X.Y.Z"
git push --tags

# 5. Update to next SNAPSHOT version in gradle.properties
# 6. Commit and push
```

Note: Using GitHub Releases is strongly recommended as it provides full automation and audit trail.

## Troubleshooting

**Release workflow failed on tests**
- The workflow runs the full test suite before publishing
- Fix failing tests and create a new release

**Version mismatch error**
- Ensure your tag matches the format `vX.Y.Z` or `X.Y.Z`
- Tag version must be a valid semantic version (major.minor.patch)

**Maven Central not showing new version**
- Automatic publishing is enabled, but Maven Central can take a few minutes to sync
- Check [Sonatype Nexus](https://central.sonatype.com/publishing/deployments) for deployment status

**Next SNAPSHOT version not committed**
- Check GitHub Actions logs for errors
- Ensure the workflow has permission to push to main branch
- You can manually update `gradle.properties` to `X.Y.(Z+1)-SNAPSHOT` and commit
