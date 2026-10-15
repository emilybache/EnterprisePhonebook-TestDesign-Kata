import http.client
from urllib.parse import urlparse

import requests


class SecurityClearanceAuthorizer:
    def __init__(self, url):
        self.url = url
        location = urlparse(self.url)
        self.resource = location.path
        self.location = location.netloc

    def authorize(self):
        try:
            request = requests.get(self.url + "/authenticate")
        except requests.ConnectionError:
            return False
        # BUG: should be request.status_code == 200
        return request.status_code != 403


