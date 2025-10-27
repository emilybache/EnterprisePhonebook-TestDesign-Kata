import dataclasses
from typing import Tuple


@dataclasses.dataclass
class InconsistentPhonebookEvent:
    size: int
    new_name: str
    new_number: str
    problem : Tuple[str, str]

    def to_data(self):
        return {
            'size': self.size,
            'new_entry': f"{self.new_name} cannot be added with number {self.new_number}",
            'clash': f"{self.problem[0]} with number {self.problem[1]}",
        }
