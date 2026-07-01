@echo off
title MultisellCreator
rem Run from the folder this launcher lives in.
cd /d "%~dp0"

rem Prefer JAVA_HOME, otherwise fall back to javaw.exe on PATH.
if defined JAVA_HOME (
	set "JAVAW=%JAVA_HOME%\bin\javaw.exe"
) else (
	set "JAVAW=javaw.exe"
)

rem javaw.exe = no console window for a GUI app.
start "" "%JAVAW%" -Dfile.encoding=UTF-8 -jar "MultisellCreator.jar"
