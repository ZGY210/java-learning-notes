@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion
cd /d "%~dp0"

rem ============================================================
rem  newday.cmd -- scaffold today's journal entry
rem
rem  Usage:  newday.cmd              (today's date)
rem          newday.cmd 2026-10-16   (explicit date)
rem
rem  NOTE: this file is ASCII-only ON PURPOSE.
rem  cmd.exe reads .cmd files with the system ANSI codepage (GBK on
rem  Chinese Windows) unless there is a BOM, so Chinese comments would
rem  turn into garbage. Chinese content belongs in the .md templates.
rem ============================================================

if "%~1"=="" (
    for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd"') do set "TODAY=%%i"
) else (
    set "TODAY=%~1"
)

set "JF=journal\%TODAY%.md"

echo ==========================================
echo   newday  -  %TODAY%
echo ==========================================
echo.

if not exist "journal" (
    echo [FAIL] run this from the repo root - journal\ not found.
    exit /b 1
)

if exist "%JF%" (
    echo [skip]  %JF% already exists.
) else (
    if not exist "journal\_template.md" (
        echo [FAIL] journal\_template.md is missing.
        exit /b 1
    )
    copy "journal\_template.md" "%JF%" >nul
    if errorlevel 1 (
        echo [FAIL] could not create %JF%
        exit /b 1
    )
    echo [ok]    created %JF%
)

echo.
echo Tonight's checklist:
echo    1. write the three lines in %JF%
echo    2. notes\      - at least one note (copy notes\_template.md)
echo    3. leetcode\   - at least one solution (copy leetcode\_模板.md)
echo.
echo Then run:
echo    verify.cmd
echo    git add -A
echo    git commit -m "feat: ..."
echo    git push
echo.
exit /b 0
