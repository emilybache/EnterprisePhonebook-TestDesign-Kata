from alert_data import InconsistentPhonebookEvent
from phonebook import Phonebook


class EnterprisePhonebook:
    def __init__(self, phonebook: Phonebook, authorizer, alerter):
        self.phonebook = phonebook
        self.authorizer = authorizer
        self.alerter = alerter

    def lookup(self, name):
        self._authorize()
        number = self.phonebook.lookup(name)
        return number

    def add(self, name, number):
        self._authorize()
        clashes = self.phonebook.find_clashes(number)
        if clashes:
            self._report_clashes(name, number, clashes)
        else:
            self.phonebook.add(name, number)

    def _authorize(self):
        authorized = self.authorizer.authorize()
        if not authorized:
            raise RuntimeError("Not authorized")

    def _report_clashes(self, name, number, clashes):
        for i in range(len(clashes)):
            entry = clashes[i]
            event = InconsistentPhonebookEvent(len(self.phonebook), name, number, entry)
            self.alerter.send_alert(event)

    def __len__(self):
        return len(self.phonebook)


