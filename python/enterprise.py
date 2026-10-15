from alert_data import InconsistentPhonebookEvent
from phonebook import Phonebook


class EnterprisePhonebook:
    def __init__(self, phonebook: Phonebook, authorizer, alerter):
        self.phonebook = phonebook
        self.authorizer = authorizer
        self.alerter = alerter

    def lookup(self, name):
        authorized = self.authorizer.authorize()
        # BUG: should be "if not authorized:"
        if authorized:
            raise RuntimeError("unauthorized lookup")
        number = self.phonebook.lookup(name)
        return number

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
            self.alerter.send_alert(event)

    def __len__(self):
        return len(self.phonebook)


