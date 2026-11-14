#!/usr/bin/env sh
# Build and run. Usage: ./run.sh  (interactive menu)   ./run.sh --demo  (scripted demo)
set -e
mkdir -p out
find src/main/java -name "*.java" > out/sources.txt
javac -encoding UTF-8 --release 17 -d out/classes @out/sources.txt
java -cp out/classes com.trading.Main "$@"
