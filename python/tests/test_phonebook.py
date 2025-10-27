import pytest

from phonebook import Phonebook


@pytest.fixture
def phonebook():
    "Provides an empty Phonebook"
    return Phonebook()


def test_lookup_missing_name(phonebook):
    with pytest.raises(KeyError):
        phonebook.lookup("Bob")


def test_look_up_by_name(phonebook):
    phonebook.add("Bob", "1234")
    result = phonebook.lookup("Bob")
    assert result == "1234"


def test_look_up_by_name_longer_number(phonebook):
    phonebook.add("Ann", "12345678")
    result = phonebook.lookup("Ann")
    assert result == "12345678"


def test_lookup_by_name(phonebook):
    phonebook.add("Bob", "12345")
    number = phonebook.lookup("Bob")
    assert number == "12345"


def test_no_inconsistent_entries(phonebook):
    phonebook.add("Bob", "12345")
    phonebook.add("Sid", "12346")
    assert phonebook.find_clashes("12347") == []


def test_inconsistent_entries(phonebook):
    phonebook.add("Bob", "12345")
    phonebook.add("Sid", "12346")
    assert phonebook.find_clashes("12345") == [("Bob", "12345")]


def test_several_inconsistent_entries(phonebook):
    phonebook.add("Bob", "12345")
    phonebook.add("Sid", "12346")
    assert phonebook.find_clashes("1234") == [
        ("Bob", "12345"),
        ("Sid", "12346"),
    ]
