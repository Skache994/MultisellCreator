' MultisellCreator launcher.
' Started by wscript.exe, so there is no console window and no black flash.
Option Explicit

Dim shell, fso, scriptDir, javaHome, javaw, command
Set shell = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")

' Run from the folder this launcher lives in (so it finds the jar next to it).
scriptDir = fso.GetParentFolderName(WScript.ScriptFullName)
shell.CurrentDirectory = scriptDir

' Prefer JAVA_HOME\bin\javaw.exe, otherwise fall back to javaw.exe on PATH.
javaHome = shell.Environment("Process").Item("JAVA_HOME")
If javaHome <> "" Then
	javaw = """" & javaHome & "\bin\javaw.exe"""
Else
	javaw = "javaw.exe"
End If

' javaw.exe = no console; window style 0 = launch hidden. The Swing window still shows.
command = javaw & " -Dfile.encoding=UTF-8 -jar ""MultisellCreator.jar"""
shell.Run command, 0, False
