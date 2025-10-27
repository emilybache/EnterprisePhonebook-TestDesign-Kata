from typing import List, Tuple


class Phonebook:
    def __init__(self):
        self.storage = {}

    def add(self, name, number):
        self.storage[name] = number

    def lookup(self, name):
        return self.storage[name]

    def __len__(self):
        return len(self.storage)

    def find_clashes(self, number_to_check) -> List[Tuple[str, str]]:
        entries = []
        for name, number in self.storage.items():
            if number.startswith(number_to_check) or number_to_check.startswith(number):
                entries.append((name, number))
        return entries
