# C# version — agent instructions

The solution lives one level down, in `EnterprisePhonebook-TestDesign-Kata/`.

## Build and test

    cd csharp/EnterprisePhonebook-TestDesign-Kata
    dotnet test

* Requires a .NET 10 SDK (projects target `net10.0`). There is no `global.json`.

## Gotchas

* The test HTTP server binds the fixed address `127.0.0.1:9998`, as do the other language versions.
  Never run two language versions' test suites at the same time, or they will fail with "address in use".
