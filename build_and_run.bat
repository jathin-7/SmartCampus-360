@echo off
REM ========================================================================
REM SmartCampus 360 - Build and Execution Script
REM Candidate: YATHAM JATHINDRA REDDY | Reg No: 25BAI10611 | CSE2006
REM ========================================================================

echo [1/2] Compiling SmartCampus Java source files...
if not exist "bin" mkdir bin
javac -d bin src\com\vityarthi\smartcampus\exception\*.java src\com\vityarthi\smartcampus\model\*.java src\com\vityarthi\smartcampus\util\*.java src\com\vityarthi\smartcampus\concurrency\*.java src\com\vityarthi\smartcampus\repository\*.java src\com\vityarthi\smartcampus\service\*.java src\com\vityarthi\smartcampus\test\*.java src\com\vityarthi\smartcampus\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Launching SmartCampus 360 Application...
java -cp bin com.vityarthi.smartcampus.Main %*
