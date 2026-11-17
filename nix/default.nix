{
  lib,
  gradle,
  gradle_9,
  newScope,
}:

lib.makeScope newScope (
  self: with self; {
    inherit gradle;
    # The tool itself is built with Gradle 9; `gradle` stays the default for
    # consumers' builds.
    gradle9 = gradle_9;
    buildGradlePackage = callPackage ./build-gradle-package.nix { };
    buildMavenRepo = callPackage ./build-maven-repo.nix { };
    gradleSetupHook = callPackage ./gradle-setup-hook.nix { };
    gradle2nix = callPackage ./gradle2nix.nix { };
  }
)
