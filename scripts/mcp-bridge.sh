#!/usr/bin/env bash
# MCP stdio-to-socket bridge
# Connects Claude Code (stdio) to Selah MCP socket server
PORT="${SELAH_MCP_PORT:-7889}"
HOST="${SELAH_MCP_HOST:-localhost}"
exec nc "$HOST" "$PORT"
