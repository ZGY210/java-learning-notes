@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

rem ============================================================
rem  mvn-local.cmd  --  build this project without installing Maven
rem
rem  It reuses the Maven bundled with IntelliJ IDEA.
rem
rem  Usage:  mvn-local.cmd -v
rem          mvn-local.cmd -o compile
rem          mvn-local.cmd -o test
rem          mvn-local.cmd -o exec:java "-Dexec.mainClass=api.MoneyUtil"
rem
rem  All arguments are passed through to Maven unchanged.
rem  The  -o  flag means "offline": use it while the network is
rem  unreliable, it forces Maven to use only the local repository.
rem ============================================================

set "MVN_HOME="

rem --- 1) user config dir: %APPDATA%\JetBrains\IntelliJIdea20xx.x
for /d %%D in ("%APPDATA%\JetBrains\IntelliJIdea*") do (
    if exist "%%D\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
        set "MVN_HOME=%%D\plugins\maven-plugin\lib\maven3"
    )
)

rem --- 2) default install dir: %ProgramFiles%\JetBrains\IntelliJ*
if not defined MVN_HOME (
    for /d %%D in ("%ProgramFiles%\JetBrains\IntelliJ*") do (
        if exist "%%D\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
            set "MVN_HOME=%%D\plugins\maven-plugin\lib\maven3"
        )
    )
)

rem --- 3) custom install dirs (this machine: D:\IDEA\...)
if not defined MVN_HOME (
    for %%R in ("D:\IDEA" "D:\JetBrains" "C:\IDEA" "E:\IDEA") do (
        if exist "%%~R" (
            for /d %%D in ("%%~R\IntelliJ*") do (
                if exist "%%D\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
                    set "MVN_HOME=%%D\plugins\maven-plugin\lib\maven3"
                )
            )
        )
    )
)

rem --- 4) standalone Maven on this machine
if not defined MVN_HOME (
    for %%R in ("D:\JAVE\apache-maven-3.9.9" "D:\JAVE\apache-maven-3.9.11" "D:\apache-maven") do (
        if exist "%%~R\bin\mvn.cmd" set "MVN_HOME=%%~R"
    )
)

if not defined MVN_HOME (
    echo [mvn-local] ERROR: no Maven found.
    echo [mvn-local] Fix it in one of two ways:
    echo [mvn-local]   1^) make sure IDEA 2026.2 is installed, then open a NEW terminal and retry;
    echo [mvn-local]   2^) download standalone Maven from https://maven.apache.org/download.cgi
    echo [mvn-local]      unzip to D:\JAVE\apache-maven-3.9.9 and add its bin folder to PATH.
    exit /b 1
)

echo [mvn-local] using Maven at: %MVN_HOME%
call "%MVN_HOME%\bin\mvn.cmd" %*
exit /b %ERRORLEVEL%
