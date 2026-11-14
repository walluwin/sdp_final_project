#!/usr/bin/env sh
# Build main + test sources and run the pattern self-test.
set -e
mkdir -p out
find src -name "*.java" > out/all-sources.txt
javac -encoding UTF-8 --release 17 -d out/test-classes @out/all-sources.txt
java -cp out/test-classes com.trading.PatternSelfTest
