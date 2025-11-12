Enterprise Phonebook Test Design Kata
=====================================

For exercise instructions see [top level README](../README.md)

Suggestion: create a python virtual environment for this project. See the [documentation](https://docs.python.org/3/library/venv.html)

install requirements:

    pip install --upgrade -r requirements.txt

run tests:

    python -m pytest

Test Fixtures
-------------
There are some test fixtures supplied in `conftest.py`. The `http_server` fixture will start automatically before the tests and be shut down afterwards. The fixture `url` will give you the url of this webserver. You can configure the responses it gives using the `test_double_handler` fixture. You can also query this TestDoubleHandler instance to find out the most recent call to the webserver.

The example below shows how to use these fixtures to control the response of the webserver and check the request that was received by it.

```python
import requests

def test_example(url, test_double_handler):
    test_double_handler.response_code = 404
    test_double_handler.message = "Not Found"
    response = requests.get(url + "/path")
    assert response.status_code == 404
    assert test_double_handler.latest_request_type == "GET"
    assert test_double_handler.latest_request_path == "/path"

```