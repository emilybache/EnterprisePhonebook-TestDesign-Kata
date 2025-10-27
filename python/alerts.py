import http.client
import json
from urllib.parse import urlparse

import requests


class InconsistentPhonebookAlerter:
    def __init__(self, url):
        # BUG: should be self.url = url + "/alert"
        self.url = url
        location = urlparse(self.url)
        self.alert_path = location.path
        self.alert_location = location.netloc

    def send_alert(self, event):
        data = event.to_data()
        # BUG: should be "data=json.dumps(data).encode()"
        response = requests.put(self.url, data=data)
        if not response.status_code == 200:
            raise RuntimeError(f"could not report alert to url {self.url}")

