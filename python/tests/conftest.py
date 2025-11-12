import threading
from http.server import ThreadingHTTPServer, BaseHTTPRequestHandler

import pytest


class TestDoubleHandler(BaseHTTPRequestHandler):
    response_code = 200
    message = "OK"
    latest_request_type = None
    latest_request_path = None
    latest_request_body = None

    @staticmethod
    def reset():
        TestDoubleHandler.response_code = 200
        TestDoubleHandler.message = "OK"
        TestDoubleHandler.latest_request_type = None
        TestDoubleHandler.latest_request_body = None

    def do_GET(self):
        TestDoubleHandler.latest_request_type = "GET"
        TestDoubleHandler.latest_request_path = self.path
        if TestDoubleHandler.response_code == 200:
            self.send_response(200, message=TestDoubleHandler.message)
            self.end_headers()
        else:
            self.send_error(TestDoubleHandler.response_code, message=TestDoubleHandler.message)

    def do_PUT(self):
        TestDoubleHandler.latest_request_type = "PUT"
        length = int(self.headers['content-length'])
        TestDoubleHandler.latest_request_body = self.rfile.read(length)
        if TestDoubleHandler.response_code == 200:
            self.send_response(200, message=TestDoubleHandler.message)
            self.end_headers()
        else:
            self.send_error(TestDoubleHandler.response_code, message=TestDoubleHandler.message)


@pytest.fixture
def test_double_handler():
    yield TestDoubleHandler
    TestDoubleHandler.reset()


@pytest.fixture
def host():
    return '127.0.0.1'


@pytest.fixture
def port():
    return 9998

@pytest.fixture
def url(host, port):
    return f"http://{host}:{port}"

@pytest.fixture(autouse=True)
def http_server(host, port, test_double_handler):
    httpd = ThreadingHTTPServer((host, port), test_double_handler)
    server_thread = threading.Thread(target=lambda: httpd.serve_forever(poll_interval=0.01))
    server_thread.start()

    yield httpd

    httpd.shutdown()
    httpd.server_close()
    server_thread.join()
