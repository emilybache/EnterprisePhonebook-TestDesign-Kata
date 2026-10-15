# Kotlin version — agent instructions

## Build and test

    cd kotlin
    gradle clean test --console=plain

* There are no `gradlew` / `gradlew.bat` scripts in the repo (only `gradle/wrapper/*`), so use an
  installed Gradle 9.x (verified with 9.1.0; the wrapper properties ask for 9.0.0).
* `build.gradle.kts` sets `jvmToolchain(22)`, so a JDK 22 must be installed. Gradle finds it
  automatically; the JDK running Gradle itself can be newer.
* Gradle does not print test counts. Check them in the JUnit XML reports:

      grep -ho 'tests="[0-9]*" skipped="[0-9]*" failures="[0-9]*" errors="[0-9]*"' build/test-results/test/*.xml

## Gotchas

* The test HTTP server binds the fixed address `127.0.0.1:9998`, as do the other language versions.
  Never run two language versions' test suites at the same time, or they will fail with "address in use".
