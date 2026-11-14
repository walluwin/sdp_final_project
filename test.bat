@echo off
rem Build main + test sources and run the pattern self-test.
if not exist out mkdir out
dir /s /b src\*.java > out\all-sources.txt
javac -encoding UTF-8 --release 17 -d out\test-classes @out\all-sources.txt || exit /b 1
java -cp out\test-classes com.trading.PatternSelfTest
