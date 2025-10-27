import threading
from http.server import ThreadingHTTPServer, BaseHTTPRequestHandler

import pytest


class SpyHandler(BaseHTTPRequestHandler):
    response_code = 200
    message = "OK"
    latest_request_type = None
    latest_request_body = None

    @staticmethod
    def reset():
        SpyHandler.response_code = 200
        SpyHandler.message = "OK"
        SpyHandler.latest_request_type = None
        SpyHandler.latest_request_body = None

    def do_GET(self):
        SpyHandler.latest_request_type = "GET"
        if SpyHandler.response_code == 200:
            self.send_response(200, message=SpyHandler.message)
            self.end_headers()
        else:
            self.send_error(SpyHandler.response_code, message=SpyHandler.message)

    def do_PUT(self):
        SpyHandler.latest_request_type = "PUT"
        length = int(self.headers['content-length'])
        SpyHandler.latest_request_body = self.rfile.read(length)
        if SpyHandler.response_code == 200:
            self.send_response(200, message=SpyHandler.message)
            self.end_headers()
        else:
            self.send_error(SpyHandler.response_code, message=SpyHandler.message)


@pytest.fixture
def spy_handler():
    yield SpyHandler
    SpyHandler.reset()


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
def http_server(host, port, spy_handler):
    httpd = ThreadingHTTPServer((host, port), spy_handler)
    server_thread = threading.Thread(target=lambda: httpd.serve_forever(poll_interval=0.01))
    server_thread.start()

    yield httpd

    httpd.shutdown()
    httpd.server_close()
    server_thread.join()
