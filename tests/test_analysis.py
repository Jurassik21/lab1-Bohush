from kzp_lab06.analysis import teacher_workload, unique_rooms, make_summary

def test_teacher_workload_is_deterministic() -> None:
    """Перевіряє стабільний підрахунок занять за викладачами."""
    records = [
        {"subject": "Math", "teacher": "Петренко", "day": "Понеділок", "period": 1, "room": 201},
        {"subject": "Physics", "teacher": "Іваненко", "day": "Понеділок", "period": 2, "room": 101},
        {"subject": "Algebra", "teacher": "Петренко", "day": "Понеділок", "period": 3, "room": 201},
    ]
    assert teacher_workload(records) == {"Іваненко": 1, "Петренко": 2}

def test_unique_rooms() -> None:
    """Перевіряє формування множини аудиторій."""
    records = [
        {"subject": "Math", "teacher": "Петренко", "day": "Понеділок", "period": 1, "room": 201},
        {"subject": "Physics", "teacher": "Іваненко", "day": "Понеділок", "period": 2, "room": 101},
    ]
    assert unique_rooms(records) == {101, 201}

def test_make_summary_filters_by_day() -> None:
    """Перевіряє правильність відбору за днем."""
    records = [
        {"subject": "Math", "teacher": "Петренко", "day": "Понеділок", "period": 2, "room": 201},
        {"subject": "Chemistry", "teacher": "Сидор", "day": "Вівторок", "period": 1, "room": 305},
    ]
    summary = make_summary(records, target_day="Понеділок")
    assert summary["count"] == 1
    assert summary["first_period"] == 2
    assert summary["rooms"] == {201}