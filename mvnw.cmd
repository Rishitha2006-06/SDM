@REM ----------------------------------------------------------------------------
@REM Student Management System Maven Runner
@REM ----------------------------------------------------------------------------
@echo off
setlocal
if not defined JAVA_HOME (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
)
set "MAVEN_HOME=C:\Users\DELL\apache-maven-3.9.6"
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo [ERROR] Maven not found at %MAVEN_HOME%
    exit /b 1
)
call "%MAVEN_HOME%\bin\mvn.cmd" %*
endlocal
