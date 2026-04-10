@echo off
rem Maven Wrapper script for Windows
rem Downloads Maven if not present and runs it

setlocal

set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6"
set "MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

if exist "%MVN_CMD%" goto runMaven

echo Downloading Maven...
set "DIST_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip"
set "TMPFILE=%TEMP%\maven-wrapper.zip"

powershell -Command "Invoke-WebRequest -Uri '%DIST_URL%' -OutFile '%TMPFILE%'"
powershell -Command "Expand-Archive -Path '%TMPFILE%' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force"
del "%TMPFILE%" 2>nul

:runMaven
"%MVN_CMD%" %*
