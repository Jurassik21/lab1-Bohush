from collections.abc import Iterable

def filter_by_day(records: Iterable[dict[str, object]], target_day: str) -> list[dict[str, object]]:
    """Відбирає заняття за заданим днем тижня."""
    return [r for r in records if str(r["day"]).lower() == target_day.lower()]

def teacher_workload(records: list[dict[str, object]]) -> dict[str, int]:
    """Обчислює кількість занять за викладачами у стабільному порядку."""
    teachers = {str(r["teacher"]) for r in records}
    return {
        teacher: sum(1 for r in records if r["teacher"] == teacher)
        for teacher in sorted(teachers)
    }

def unique_rooms(records: list[dict[str, object]]) -> set[int]:
    """Повертає множину унікальних аудиторій."""
    return {int(r["room"]) for r in records}

def make_summary(records: list[dict[str, object]], target_day: str = "Понеділок") -> dict[str, object]:
    """Формує підсумок для заданого дня згідно з вимогами Варіанта 3."""
    filtered = filter_by_day(records, target_day)
    
    periods = [int(r["period"]) for r in filtered]
    first_period = min(periods) if periods else 0
    last_period = max(periods) if periods else 0

    return {
        "day": target_day,
        "count": len(filtered),
        "teacher_counts": teacher_workload(filtered),
        "rooms": unique_rooms(filtered),
        "first_period": first_period,
        "last_period": last_period,
    }