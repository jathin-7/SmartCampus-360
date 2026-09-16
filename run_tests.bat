@echo off
REM ========================================================================
REM SmartCampus 360 - Automated Test Suite Runner
REM Candidate: YATHAM JATHINDRA REDDY | Reg No: 25BAI10611 | CSE2006
REM ========================================================================

echo Compiling test files...
if not exist "bin" mkdir bin
javac -d bin src\com\vityarthi\smartcampus\exception\*.java src\com\vityarthi\smartcampus\model\*.java src\com\vityarthi\smartcampus\util\*.java src\com\vityarthi\smartcampus\concurrency\*.java src\com\vityarthi\smartcampus\repository\*.java src\com\vityarthi\smartcampus\service\*.java src\com\vityarthi\smartcampus\test\*.java src\com\vityarthi\smartcampus\Main.java

echo Running validation test suite...
java -cp bin com.vityarthi.smartcampus.test.SystemTestSuite
