
from http.server import BaseHTTPRequestHandler

import pytest

from authorizer import SecurityClearanceAuthorizer

@pytest.mark.slow
def test_authorize(url, spy_handler):
    authorizer = SecurityClearanceAuthorizer(url)
    spy_handler.response_code = 200

    result = authorizer.authorize()

    assert result
    assert spy_handler.latest_request_type == "GET"

@pytest.mark.slow
def test_not_authorize(url, spy_handler):
    authorizer = SecurityClearanceAuthorizer(url)
    spy_handler.response_code = 404
    spy_handler.message = "Not Found"

    result = authorizer.authorize()

    assert not result
    assert spy_handler.latest_request_type == "GET"

