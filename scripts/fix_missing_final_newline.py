#!/usr/bin/env python3
"""
Append a final newline to files that do not end with one.

By default, only processes .java and .json files.
Works well on Windows and preserves the file's existing newline style.

Examples:
    python scripts/fix_missing_final_newline.py src
    python scripts/fix_missing_final_newline.py . --git-only
    python scripts/fix_missing_final_newline.py . --extensions .md .gradle --check
"""

from __future__ import annotations

import argparse
from pathlib import Path
import subprocess
import sys


DEFAULT_EXTENSIONS = {".java", ".json"}


def get_git_uncommitted_files() -> set[Path]:
    """Get all uncommitted files (staged, unstaged, untracked) using git."""
    try:
        # Get staged and unstaged changes (excluding deleted)
        cmd_diff = ["git", "--no-pager", "diff", "HEAD", "--name-only", "--diff-filter=d"]
        output_diff = subprocess.check_output(cmd_diff, text=True, stderr=subprocess.DEVNULL)

        # Get untracked files
        cmd_ls = ["git", "--no-pager", "ls-files", "--others", "--exclude-standard"]
        output_ls = subprocess.check_output(cmd_ls, text=True, stderr=subprocess.DEVNULL)

        files = set()
        for line in (output_diff + output_ls).splitlines():
            if line.strip():
                # Git returns paths relative to the repo root.
                # We resolve them to absolute paths for easier matching.
                files.add(Path(line.strip()).absolute())
        return files
    except (subprocess.CalledProcessError, FileNotFoundError):
        return set()


def is_binary_file(data: bytes) -> bool:
    return b"\x00" in data


def detect_newline_bytes(data: bytes) -> bytes:
    if b"\r\n" in data:
        return b"\r\n"
    return b"\n"


def should_process(path: Path, extensions: set[str]) -> bool:
    if not path.is_file():
        return False
    return path.suffix.lower() in extensions


def iter_files(targets: list[Path], extensions: set[str], git_files: set[Path] | None):
    for target in targets:
        if target.is_file():
            if should_process(target, extensions):
                if git_files is None or target.absolute() in git_files:
                    yield target
            continue

        for path in target.rglob("*"):
            if should_process(path, extensions):
                if git_files is None or path.absolute() in git_files:
                    yield path


def fix_file(path: Path, check_only: bool) -> bool:
    data = path.read_bytes()

    if is_binary_file(data):
        return False

    if data.endswith(b"\n"):
        return False

    newline = detect_newline_bytes(data)

    if not check_only:
        path.write_bytes(data + newline)

    return True


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Append a final newline to text files that do not end with one."
    )
    parser.add_argument(
        "paths",
        nargs="+",
        type=Path,
        help="Files or directories to scan.",
    )
    parser.add_argument(
        "--extensions",
        nargs="*",
        help="Optional file extensions to include, e.g. .java .md .py",
    )
    parser.add_argument(
        "--check",
        action="store_true",
        help="Report files that need fixing without modifying them.",
    )
    parser.add_argument(
        "--git-only",
        action="store_true",
        help="Only process files with uncommitted changes (staged, unstaged, or untracked).",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()

    if args.extensions:
        extensions = {
            ext if ext.startswith(".") else f".{ext}"
            for ext in (value.lower() for value in args.extensions)
        }
    else:
        extensions = DEFAULT_EXTENSIONS

    git_files = get_git_uncommitted_files() if args.git_only else None

    changed_files: list[Path] = []

    for path in iter_files(args.paths, extensions, git_files):
        try:
            if fix_file(path, args.check):
                changed_files.append(path)
        except OSError as exc:
            print(f"ERROR: {path} -> {exc}", file=sys.stderr)

    if changed_files:
        action = "Needs final newline" if args.check else "Fixed"
        for path in changed_files:
            print(f"{action}: {path}")
        return 1 if args.check else 0

    print("All checked files already end with a newline.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
