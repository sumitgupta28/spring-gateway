#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$ROOT_DIR/logs"
mkdir -p "$LOG_DIR"

echo "Making gradlew executable..."
chmod +x "$ROOT_DIR/gradlew" || true

start_service() {
  local name="$1";
  local task="$2";
  local logfile="$LOG_DIR/${name}.log";
  local pidfile="$LOG_DIR/${name}.pid";

  echo "Starting $name ($task)..."
  nohup "$ROOT_DIR/gradlew" "$task" --no-daemon > "$logfile" 2>&1 &
  local pid=$!
  echo "$pid" > "$pidfile"
  echo "$name started with PID $pid (log: $logfile)"
}

# Start services (order: product -> cart -> gateway)
start_service product-service :product-service:bootRun
sleep 1
start_service shopping-cart-service :shopping-cart-service:bootRun
sleep 1
start_service spring-cloud-gateway :spring-cloud-gateway:bootRun

echo "\nAll services started in background. Logs and PIDs are in: $LOG_DIR"

cat <<EOF
To follow logs in another terminal:
  tail -f "$LOG_DIR"/*.log
To stop services run the generated stop-all.sh script (or kill PIDs in $LOG_DIR/*.pid)
EOF

