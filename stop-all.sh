#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$ROOT_DIR/logs"

if [ ! -d "$LOG_DIR" ]; then
  echo "No logs directory found at $LOG_DIR; nothing to stop."
  exit 0
fi

echo "Stopping services using PID files in $LOG_DIR..."

shopt -s nullglob
PIDS=("$LOG_DIR"/*.pid)
if [ ${#PIDS[@]} -eq 0 ]; then
  echo "No PID files found in $LOG_DIR. Nothing to stop."
  exit 0
fi

for pidfile in "${PIDS[@]}"; do
  svc=$(basename "$pidfile" .pid)
  pid=$(cat "$pidfile" 2>/dev/null || true)
  if [ -z "$pid" ]; then
    echo "PID file $pidfile is empty; removing." && rm -f "$pidfile" && continue
  fi

  if kill -0 "$pid" 2>/dev/null; then
    echo "Stopping $svc (PID $pid)..."
    kill "$pid" && sleep 1
    if kill -0 "$pid" 2>/dev/null; then
      echo "PID $pid still running; attempting SIGKILL..."
      kill -9 "$pid" || true
    fi
    echo "$svc stopped. Removing $pidfile"
    rm -f "$pidfile"
  else
    echo "Process $pid for $svc not found; removing stale PID file $pidfile"
    rm -f "$pidfile"
  fi
done

echo "Done stopping services. Check logs in $LOG_DIR for details."
