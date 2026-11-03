import argparse
import sys
from pathlib import Path
from .analysis import make_summary
from .errors import DataError
from .loader import load_records
from .reporting import format_report, write_report

def _print_data_error(error: DataError) -> None:
    """Виводить помилку даних у стандартний потік помилок з UTF-8."""
    try:
        sys.stderr.reconfigure(encoding="utf-8")
    except (AttributeError, ValueError):
        pass
    print(f"Помилка даних: {error}", file=sys.stderr)

def main(argv: list[str] | None = None) -> int:
    """Обробляє аргументи CLI, завантажує дані, формує та записує звіт."""
    parser = argparse.ArgumentParser(description="Аналіз розкладу занять (Лабораторна №6)")
    parser.add_argument("input_csv", type=Path, help="Шлях до вхідного CSV-файлу")
    parser.add_argument("output_report", type=Path, help="Шлях до вихідного файлу звіту")
    parser.add_argument("--day", type=str, default="Понеділок", help="День тижня для відбору")
    
    args = parser.parse_args(argv)
    
    try:
        records = load_records(args.input_csv)
        summary = make_summary(records, target_day=args.day)
        write_report(args.output_report, format_report(summary))
    except DataError as error:
        _print_data_error(error)
        return 2
    return 0

if __name__ == "__main__":
    raise SystemExit(main())