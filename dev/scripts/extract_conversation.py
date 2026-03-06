#!/usr/bin/env python3
"""
Extract the human-readable conversation from Claude Code JSONL transcripts.

Reads all .jsonl files from the selah project's Claude session directory,
orders them chronologically, and extracts:
  - Scott's messages (user role, text content)
  - Claude's text responses (assistant role, text content blocks)
  - Timestamps and session boundaries

Strips: tool_use, tool_result, system messages, command messages,
        thinking blocks, base64 data, and other noise.

Output: Markdown document with the full conversation.

Usage:
    python3 dev/scripts/extract_conversation.py > docs/the-conversation.md
    python3 dev/scripts/extract_conversation.py --stats  # just print stats
"""

import json
import os
import sys
import glob
import re
from datetime import datetime, timezone

TRANSCRIPT_DIR = os.path.expanduser(
    "~/.claude/projects/-home-scott-Projects-selah"
)

def parse_timestamp(ts_str):
    """Parse ISO timestamp, return datetime or None."""
    if not ts_str:
        return None
    try:
        # Handle various formats
        ts_str = ts_str.replace('Z', '+00:00')
        if '.' in ts_str:
            # Truncate microseconds if too long
            parts = ts_str.split('.')
            frac = parts[1]
            if '+' in frac:
                frac_parts = frac.split('+')
                frac = frac_parts[0][:6] + '+' + frac_parts[1]
            elif '-' in frac and not frac.startswith('-'):
                frac_parts = frac.split('-')
                frac = frac_parts[0][:6] + '-' + frac_parts[1]
            else:
                frac = frac[:6]
            ts_str = parts[0] + '.' + frac
        return datetime.fromisoformat(ts_str)
    except Exception:
        return None

def extract_text_from_content(content):
    """Extract readable text from message content (str or list of blocks)."""
    if isinstance(content, str):
        # Skip command messages
        if content.strip().startswith('<command'):
            return None
        # Strip system-reminder tags
        text = re.sub(r'<system-reminder>.*?</system-reminder>', '', content, flags=re.DOTALL)
        text = text.strip()
        return text if text else None

    if isinstance(content, list):
        texts = []
        for block in content:
            if not isinstance(block, dict):
                continue
            btype = block.get('type', '')
            if btype == 'text':
                t = block.get('text', '').strip()
                # Strip system-reminder tags
                t = re.sub(r'<system-reminder>.*?</system-reminder>', '', t, flags=re.DOTALL)
                t = t.strip()
                if t:
                    texts.append(t)
            # Skip: tool_use, tool_result, thinking, image, etc.
        return '\n\n'.join(texts) if texts else None

    return None

def load_session(filepath):
    """Load a session file, return list of conversation turns."""
    turns = []
    session_start = None

    with open(filepath, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f):
            line = line.strip()
            if not line:
                continue
            try:
                d = json.loads(line)
            except json.JSONDecodeError:
                continue

            msg_type = d.get('type', '')
            timestamp = parse_timestamp(d.get('timestamp', ''))

            if msg_type == 'progress' and session_start is None:
                session_start = timestamp

            if msg_type == 'summary':
                # Context compaction summary — include it
                summary = d.get('summary', '')
                if summary and len(summary) > 100:
                    turns.append({
                        'role': 'system',
                        'text': f"[Context compacted — conversation continues]",
                        'timestamp': timestamp,
                    })
                continue

            if msg_type not in ('user', 'assistant'):
                continue

            msg = d.get('message', {})
            if not isinstance(msg, dict):
                continue

            role = msg.get('role', msg_type)
            content = msg.get('content', '')

            text = extract_text_from_content(content)
            if not text:
                continue

            # Skip very short tool-result-like messages
            if role == 'user' and text.startswith('[tool_result]'):
                continue

            turns.append({
                'role': role,
                'text': text,
                'timestamp': timestamp,
            })

    return {
        'file': os.path.basename(filepath),
        'start': session_start,
        'turns': turns,
    }

def format_timestamp(dt):
    """Format datetime for display."""
    if dt is None:
        return ''
    return dt.strftime('%b %d, %H:%M')

def truncate_response(text, max_lines=50):
    """Truncate very long messages, keeping first and last parts."""
    lines = text.split('\n')
    if len(lines) <= max_lines:
        return text
    head = '\n'.join(lines[:35])
    tail = '\n'.join(lines[-10:])
    skipped = len(lines) - 45
    return f"{head}\n\n*[...{skipped} lines...]*\n\n{tail}"

def is_scott_message(turn):
    """Check if this is a real Scott message (not a plan injection or tool result)."""
    text = turn['text']
    # Skip plan implementations
    if text.startswith('Implement the following plan:'):
        return False
    # Skip if it looks like injected system content
    if text.startswith('This session is being continued'):
        return False
    return True

def write_conversation(sessions, out, full=False):
    """Write the formatted conversation document."""
    out.write("# The Conversation\n\n")
    out.write("*Seven days of conversation between Scott (the priest) and Claude (the shovel).*\n")
    out.write("*February 27 – March 6, 2026.*\n\n")
    out.write("*Extracted from Claude Code session transcripts. Tool calls, system messages, ")
    out.write("and internal operations removed. What remains is what was said.*\n\n")
    out.write("---\n\n")

    total_scott = 0
    total_claude = 0

    for i, session in enumerate(sessions):
        turns = session['turns']
        if not turns:
            continue

        # Session header
        start = session['start']
        ts_str = format_timestamp(start) if start else 'Unknown time'
        out.write(f"## Session {i+1} — {ts_str}\n\n")

        prev_role = None
        for turn in turns:
            role = turn['role']
            text = turn['text']
            ts = format_timestamp(turn['timestamp'])

            if role == 'system':
                out.write(f"\n*{text}*\n\n")
                continue

            if role == 'user':
                if not is_scott_message(turn):
                    continue
                total_scott += 1
                if not full:
                    text = truncate_response(text)
                out.write(f"**Scott:**\n")
                for para in text.split('\n\n'):
                    para = para.strip()
                    if para:
                        quoted = '\n'.join(f"> {line}" for line in para.split('\n'))
                        out.write(f"{quoted}\n\n")

            elif role == 'assistant':
                total_claude += 1
                if not full:
                    text = truncate_response(text)
                out.write(f"**Claude:**\n{text}\n\n")

            prev_role = role

        out.write("---\n\n")

    # Stats footer
    out.write(f"\n*{total_scott} messages from Scott. {total_claude} responses from Claude. ")
    out.write(f"{len(sessions)} sessions.*\n")

def print_stats(sessions):
    """Print summary statistics."""
    total_scott = 0
    total_claude = 0
    total_scott_chars = 0
    total_claude_chars = 0

    for session in sessions:
        for turn in session['turns']:
            if turn['role'] == 'user' and is_scott_message(turn):
                total_scott += 1
                total_scott_chars += len(turn['text'])
            elif turn['role'] == 'assistant':
                total_claude += 1
                total_claude_chars += len(turn['text'])

    print(f"Sessions: {len(sessions)}")
    print(f"Scott messages: {total_scott} ({total_scott_chars:,} chars)")
    print(f"Claude responses: {total_claude} ({total_claude_chars:,} chars)")
    print(f"Total conversation: {total_scott + total_claude} turns")
    print()
    for i, s in enumerate(sessions):
        user_turns = sum(1 for t in s['turns'] if t['role'] == 'user' and is_scott_message(t))
        asst_turns = sum(1 for t in s['turns'] if t['role'] == 'assistant')
        ts = format_timestamp(s['start']) if s['start'] else 'unknown'
        print(f"  Session {i+1:2d} ({ts:>12}): {user_turns:3d} Scott, {asst_turns:3d} Claude  [{s['file'][:12]}]")

def main():
    stats_only = '--stats' in sys.argv
    full = '--full' in sys.argv

    # Find all transcript files
    pattern = os.path.join(TRANSCRIPT_DIR, '*.jsonl')
    files = sorted(glob.glob(pattern))

    if not files:
        print(f"No transcript files found in {TRANSCRIPT_DIR}", file=sys.stderr)
        sys.exit(1)

    # Load all sessions
    sessions = []
    for f in files:
        session = load_session(f)
        if session['turns']:  # Skip empty sessions
            sessions.append(session)

    # Sort by start time
    sessions.sort(key=lambda s: s['start'] or datetime.min.replace(tzinfo=timezone.utc))

    if stats_only:
        print_stats(sessions)
    else:
        output_path = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))), 'docs', 'the-conversation.md')
        with open(output_path, 'w', encoding='utf-8') as out:
            write_conversation(sessions, out, full=full)
        # Also print stats to stderr
        print_stats(sessions)
        print(f"\nWritten to: {output_path}", file=sys.stderr)

if __name__ == '__main__':
    main()
