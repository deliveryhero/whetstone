Releasing
========

1. Change the `VERSION_NAME` in root `gradle.properties` to a non-SNAPSHOT version to be released. 
2. Run `./gradlew clean build` to make sure project builds successfully.
3. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the new version)
4. Create a pre-release on GitHub Releases with a tag. This will trigger the release action. Alternatively, run `./gradlew clean publish :whetstone-gradle-plugin:publish` locally.
5. Visit [Sonatype Nexus](https://s01.oss.sonatype.org/) and promote the artifact.
6. Skip if you used githup releases on step 4. `git tag -a X.Y.Z -m "Version X.Y.Z"` (where X.Y.Z is the new version)
7. Update the root `gradle.properties` to the next SNAPSHOT version.
8. `git commit -am "Prepare next development version."`
9. `git push && git push --tags`
10. Go to Github and create a new release (including necessary changelog) using the new tag that was just pushed
