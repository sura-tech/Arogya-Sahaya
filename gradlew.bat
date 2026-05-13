@rem Gradle startup script for Windows
@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem  Gradle startup script for Windows
@rem ##########################################################################
setlocal
set APP_HOME=%~dp0
set CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar
set JAVA_EXE=%JAVA_HOME%/bin/java.exe
if not exist "%JAVA_EXE%" set JAVA_EXE=java
"%JAVA_EXE%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
endlocal
