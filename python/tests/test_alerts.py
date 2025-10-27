import json

import pytest

import alerts
from enterprise import InconsistentPhonebookEvent


def test_event_to_json_data():
    event = InconsistentPhonebookEvent(10, "Ted", "1234", ("Bob", "1234"))
    data = event.to_data()
    assert data == {'size': 10,
                    'new_entry': "Ted cannot be added with number 1234",
                    'clash': "Bob with number 1234"}

@pytest.mark.slow
def test_generate_event(spy_handler, url):
    alerter = alerts.InconsistentPhonebookAlerter(url)
    event = InconsistentPhonebookEvent(10, "Ted", "1234", ("Bob", "1234"))

    alerter.send_alert(event)

    assert spy_handler.latest_request_type == "PUT"
    assert spy_handler.latest_request_body is not None
    json_str = spy_handler.latest_request_body.decode('utf-8')
    assert json.loads(json_str) == event.to_data()
