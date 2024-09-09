import re

LANGUAGE_CODES = [
    "en",
    "es",
    "de",
    "fr",
    "pt",
    "pt-PT",
    "it",
    "fi",
    "el",
    "ht",
    "haw",
    "iw",
    "hi",
    "hu",
    "ja",
    "ko",
    "la",
    "lb",
    "mt",
    "no",
    "fa",
    "pl",
    "ro",
    "ru",
    "sa",
    "sr",
    "sl",
    "sk",
    "tr",
    "vi",
]


def redact_api_key(text: str) -> str:
    api_key_pattern = r"(sk-[a-zA-Z0-9\-]{8})([a-zA-Z0-9\-]*)([a-zA-Z0-9]{4})"

    def redact(match):
        return f"{match.group(1)}{'*' * 5}{match.group(3)}"

    return re.sub(api_key_pattern, redact, text)
