from pathlib import Path

def format_report(summary: dict[str, object]) -> str:
    """Формує детермінований текстовий звіт для розкладу."""
    lines = [
        f"День: {summary['day']}",
        f"Занять у цей день: {summary['count']}",
        f"Перша пара: {summary['first_period']}",
        f"Остання пара: {summary['last_period']}",
        f"Унікальні аудиторії: {', '.join(map(str, sorted(summary['rooms'])))}",
        "Кількість занять за викладачами:",
    ]
    for teacher, count in sorted(summary["teacher_counts"].items()):
        lines.append(f"  - {teacher}: {count}")
    
    return "\n".join(lines) + "\n"

def write_report(path: Path, text: str) -> None:
    """Створює батьківський каталог і записує звіт у файл UTF-8."""
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="\n") as target:
        target.write(text)