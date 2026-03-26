#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$ROOT_DIR/logs"
mkdir -p "$LOG_DIR"

# Default simulation class (can be overridden by passing the full class name as first arg)
SIMULATION=${1:-com.sg.gatling.simulation.ProductSimulation}

echo "Making gradlew executable..."
chmod +x "$ROOT_DIR/gradlew" || true

echo "Building gatling-test fat jar..."
cd "$ROOT_DIR"
./gradlew :gatling-test:buildJar --no-daemon

# find the produced fat jar (shadowJar with classifier 'all')
JAR_FILE=$(ls -1 "$ROOT_DIR/gatling-test/build/libs"/gatling-test-*all*.jar 2>/dev/null | tail -n 1 || true)
if [ -z "$JAR_FILE" ]; then
  echo "ERROR: fat jar not found under gatling-test/build/libs" >&2
  exit 1
fi

TIMESTAMP=$(date +%Y%m%d%H%M%S)
RUN_LOG="$LOG_DIR/gatling-$TIMESTAMP.log"

echo "Running Gatling from jar: $JAR_FILE"
echo "Simulation: $SIMULATION"

echo "Logs will be written to: $RUN_LOG"

# Run the jar with the system property for the simulation and tee output to a log file
java -Dgatling.simulation="$SIMULATION" -jar "$JAR_FILE" 2>&1 | tee "$RUN_LOG"

echo "Gatling run finished. See report and logs."
