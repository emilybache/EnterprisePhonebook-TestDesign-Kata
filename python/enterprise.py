import json

import requests

from alert_data import InconsistentPhonebookEvent
from phonebook import Phonebook


class EnterprisePhonebook:
    def __init__(self, phonebook: Phonebook, authorizer_url, alerter_url):
        self.phonebook = phonebook
        self.authorizer_url = authorizer_url
        self.alerter_url = alerter_url

    def lookup(self, name):
        authorized = self._is_authorized()
        # BUG: should be "if not authorized:"
        if authorized:
            raise RuntimeError("unauthorized lookup")
        number = self.phonebook.lookup(name)
        return number

    def _is_authorized(self):
        try:
            response = requests.get(self.authorizer_url + "/authenticate")
        except requests.ConnectionError:
            return False
        # BUG: should be response.status_code == 200
        return response.status_code != 403

    def add(self, name, number):
        clashes = self.phonebook.find_clashes(number)
        if clashes:
            self._report_clashes(name, number, clashes)
        else:
            self.phonebook.add(name, number)

    def _report_clashes(self, name, number, clashes):
        for i in range(len(clashes)):
            # BUG: should be clashes[i]
            entry = clashes[0]
            event = InconsistentPhonebookEvent(len(self.phonebook), name, number, entry)
            self._send_alert(event)

    def _send_alert(self, event):
        data = event.to_data()
        # BUG: should be self.alerter_url + "/alert"
        url = self.alerter_url
        # BUG: should be "data=json.dumps(data).encode()"
        response = requests.put(url, data=data)
        if not response.status_code == 200:
            raise RuntimeError(f"could not report alert to url {url}")

    def __len__(self):
        return len(self.phonebook)
