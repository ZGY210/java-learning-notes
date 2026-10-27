@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

rem ============================================================
rem  verify.cmd  --  one-click check: compile + run every self test
rem
rem  Usage:  double-click it, or run  verify.cmd  in a terminal.
rem
rem  Why this exists:
rem  every day must end with a green screen. If you cannot show a
rem  running result, the day does not count. Run this before every
rem  git commit.
rem
rem  It uses plain javac/java, so it works with no network and no
rem  Maven repository. Maven is only needed once you add JUnit or
rem  third-party libraries.
rem ============================================================

cd /d "%~dp0"

set "SRC=src\main\java"
set "TEST=src\test\java"
set "OUT=target\classes"

echo ==========================================
echo   p1-javase  verify
echo ==========================================
echo.

if exist "%OUT%" rmdir /s /q "%OUT%"
mkdir "%OUT%" 2>nul

echo [1/4] compiling ...
dir /s /b "%SRC%\*.java" > "%TEMP%\p1_sources.txt"
if exist "%TEST%" dir /s /b "%TEST%\*.java" >> "%TEMP%\p1_sources.txt"
javac -encoding UTF-8 --release 17 -d "%OUT%" @"%TEMP%\p1_sources.txt"
if errorlevel 1 (
    echo.
    echo [FAIL] compile failed. Fix the errors above first.
    exit /b 1
)
echo       compile OK
echo.

echo [2/4] MyArrayList self test ...
java -Dstdout.encoding=UTF-8 -cp "%OUT%" practice.mycollection.MyArrayListTest
if errorlevel 1 (
    echo.
    echo [FAIL] MyArrayListTest has failures.
    exit /b 1
)
echo.

echo [3/4] reference.DateUtil demo ...
java -Dstdout.encoding=UTF-8 -cp "%OUT%" reference.DateUtil
if errorlevel 1 goto :failed
echo.

echo [4/4] reference.MoneyUtil demo ...
java -Dstdout.encoding=UTF-8 -cp "%OUT%" reference.MoneyUtil
if errorlevel 1 goto :failed
echo.

echo ==========================================
echo   ALL GREEN - safe to commit
echo ==========================================
exit /b 0

:failed
echo.
echo [FAIL] one of the demos exited with an error.
exit /b 1
