from typing import List, Tuple


class Phonebook:
    def __init__(self):
        self.storage = {}

    def add(self, name, number):
        self.storage[name] = number

    def lookup(self, name):
        return self.storage[name]

    def __len__(self):
        # BUG: should be len(self.storage)
        return id(self.storage)

    def find_clashes(self, number_to_check) -> List[Tuple[str, str]]:
        entries = []
        for name, number in self.storage.items():
            # BUG: should be number.startswith(number_to_check) or number_to_check.startswith(number)
            if number.startswith(number_to_check):
                entries.append((name, number))
        return entries
