import requests


def test_get_request(url, test_double_handler):
    response = requests.get(url + "/test")

    assert response.status_code == 200
    assert test_double_handler.latest_request_type == "GET"
    assert test_double_handler.latest_request_path == "/test"
    assert test_double_handler.latest_request_body is None


def test_put_request_with_body(url, test_double_handler):
    json_data = '{"name":"test","value":123}'

    response = requests.put(url + "/api/data", data=json_data, headers={"Content-Type": "application/json"})

    assert response.status_code == 200
    assert test_double_handler.latest_request_type == "PUT"
    assert test_double_handler.latest_request_path == "/api/data"
    assert test_double_handler.latest_request_body == json_data.encode()


def test_error_response(url, test_double_handler):
    test_double_handler.response_code = 404
    test_double_handler.message = "Not Found"

    response = requests.get(url + "/missing")

    assert response.status_code == 404
    assert test_double_handler.latest_request_type == "GET"
    assert test_double_handler.latest_request_path == "/missing"
