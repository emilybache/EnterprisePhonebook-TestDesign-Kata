import unittest.mock
from io import StringIO
from textwrap import dedent

import pytest

from enterprise import EnterprisePhonebook
from alert_data import InconsistentPhonebookEvent
from tests.test_phonebook import phonebook


class StubAuthorizer:
    def __init__(self):
        self.should_authorize = True

    def authorize(self):
        return self.should_authorize


class SpyPhonebookAlerter:
    def __init__(self):
        self.events = []

    def send_alert(self, event):
        self.events.append(event)


@pytest.fixture
def stub_authorizer():
    return StubAuthorizer()


@pytest.fixture
def spy_alerter():
    alerter = SpyPhonebookAlerter()
    return alerter

@pytest.fixture
def dummy_logger():
    return unittest.mock.MagicMock()

@pytest.fixture
def enterprise_phonebook(phonebook, dummy_logger, stub_authorizer, spy_alerter):
    "Provides an empty EnterprisePhonebook"
    return EnterprisePhonebook(phonebook, stub_authorizer, spy_alerter)


def test_lookup_authorized_fixture(phonebook, enterprise_phonebook, stub_authorizer):
    phonebook.add("Bob", "1234")
    stub_authorizer.should_authorize = True
    assert enterprise_phonebook.lookup("Bob") == "1234"


def test_lookup_not_authorized_fixture(phonebook, enterprise_phonebook, stub_authorizer):
    phonebook.add("Bob", "1234")
    stub_authorizer.should_authorize = False
    with pytest.raises(RuntimeError):
        enterprise_phonebook.lookup("Bob")


def test_authorize(enterprise_phonebook, stub_authorizer):
    stub_authorizer.should_authorize = False
    with pytest.raises(RuntimeError):
        enterprise_phonebook.add("Bob", "12345")


def test_alert(enterprise_phonebook, spy_alerter):
    enterprise_phonebook.add("Bob", "12345")
    enterprise_phonebook.add("Sid", "12346")
    enterprise_phonebook.add("Ted", "1234")
    assert spy_alerter.events == [
        (InconsistentPhonebookEvent(2, "Ted", "1234", ("Bob", "12345"))),
        (InconsistentPhonebookEvent(2, "Ted", "1234", ("Sid", "12346"))),
    ]


def test_do_not_add_inconsistent_entry(enterprise_phonebook):
    enterprise_phonebook.add("Bob", "12345")
    enterprise_phonebook.add("Sid", "12346")
    enterprise_phonebook.add("Ted", "1234")
    assert enterprise_phonebook.phonebook.storage == {"Bob": "12345", "Sid": "12346"}

