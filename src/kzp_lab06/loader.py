import csv
from pathlib import Path
from .errors import DataError

REQUIRED_FIELDS = {"subject", "teacher", "day", "period", "room"}

def load_records(path: Path) -> list[dict[str, object]]:
    """Читає CSV-файл розкладу, перетворює поля та перевіряє записи."""
    try:
        with path.open("r", encoding="utf-8", newline="") as source:
            reader = csv.DictReader(source)
            if reader.fieldnames is None or set(reader.fieldnames) != REQUIRED_FIELDS:
                raise DataError("Некоректний заголовок CSV. Очікуються поля: subject, teacher, day, period, room")
            
            records = []
            for row_number, row in enumerate(reader, start=2):
                try:
                    subject = row["subject"].strip()
                    teacher = row["teacher"].strip()
                    day = row["day"].strip()
                    period = int(row["period"])
                    room = int(row["room"])
                except (AttributeError, KeyError, TypeError, ValueError) as error:
                    raise DataError(f"Рядок {row_number}: некоректний формат поля") from error
                
                if not subject or not teacher or not day:
                    raise DataError(f"Рядок {row_number}: текстове поле не може бути порожнім")
                if period <= 0 or room <= 0:
                    raise DataError(f"Рядок {row_number}: period та room мають бути додатними числами")
                
                records.append({
                    "subject": subject,
                    "teacher": teacher,
                    "day": day,
                    "period": period,
                    "room": room,
                })
            return records
    except OSError as error:
        raise DataError(f"Не вдалося прочитати файл: {path}") from error