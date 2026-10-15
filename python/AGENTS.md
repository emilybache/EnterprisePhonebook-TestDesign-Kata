# Python version — agent instructions

## Build and test

Use the virtual environment in `python/.venv` (gitignored). Create it if it is missing:

    cd python
    python3 -m venv .venv
    .venv/bin/python -m pip install --upgrade -r requirements.txt

Run the tests from the `python` folder:

    .venv/bin/python -m pytest


## Gotchas

* The test HTTP server binds the fixed address `127.0.0.1:9998`, as do the other language versions.
  Never run two language versions' test suites at the same time, or they will fail with "address in use".
* The `http_server` fixture in `tests/conftest.py` is `autouse`, so every test starts a server.
