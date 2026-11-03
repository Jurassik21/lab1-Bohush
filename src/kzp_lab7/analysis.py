from collections.abc import Iterable
from .models import Lesson, RoomNeeded, ScheduleSummary

def filter_by_day(records: Iterable[Lesson], target_day: str) -> list[Lesson]:
    return [r for r in records if r.day.lower() == target_day.lower()]

def teacher_workload(records: list[Lesson]) -> dict[str, int]:
    teachers = {r.teacher for r in records}
    return {teacher: sum(1 for r in records if r.teacher == teacher) for teacher in sorted(teachers)}

def unique_rooms(records: list[Lesson]) -> list[int]:
    return sorted({r.room for r in records}, reverse=True)

def total_room_need(records: list[RoomNeeded]) -> int:
    return sum(r.room_need() for r in records)

def make_summary(records: list[Lesson], target_day: str = "Понеділок") -> ScheduleSummary:
    filtered = filter_by_day(records, target_day)
    periods = [r.period for r in filtered]
    
    return ScheduleSummary(
        day=target_day,
        count=len(filtered),
        first_period=min(periods) if periods else 0,
        last_period=max(periods) if periods else 0,
        rooms=unique_rooms(filtered),
        teacher_counts=teacher_workload(filtered),
        total_room_need=total_room_need(filtered)
    )