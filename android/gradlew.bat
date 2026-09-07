@rem Gradle startup script for Windows
@echo off
set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

set DEFAULT_JVM_OPTS="-Dfile.encoding=UTF-8"
set GRADLE_OPTS=%DEFAULT_JVM_OPTS%

"%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*