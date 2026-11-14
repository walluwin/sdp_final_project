@echo off
rem Build and run. Usage: run.bat          (interactive menu)
rem                       run.bat --demo   (scripted demo of all patterns)
if not exist out mkdir out
dir /s /b src\main\java\*.java > out\sources.txt
javac -encoding UTF-8 --release 17 -d out\classes @out\sources.txt || exit /b 1
java -cp out\classes com.trading.Main %*
