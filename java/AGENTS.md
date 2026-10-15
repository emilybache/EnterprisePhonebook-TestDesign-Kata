# Java version — agent instructions

## Build and test

    cd java
    mvn -B clean test

* Always include `clean`. `target/` is gitignored and can contain compiled test classes whose sources
  no longer exist (e.g. an old `ProbeTest`); without `clean`, Surefire runs them and reports failures.
* The pom compiles with `source`/`target` 22; any JDK 22 or newer works (verified with JDK 25).
  Warnings from Maven itself about `sun.misc.Unsafe` / restricted methods are noise and can be ignored.

## Gotchas

* The test HTTP server binds the fixed address `127.0.0.1:9998`, as do the other language versions.
  Never run two language versions' test suites at the same time, or they will fail with "address in use".
