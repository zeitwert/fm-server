@echo off
REM Build all module JARs using Maven
echo Building all module JARs...
call mvnw.cmd clean install -DskipTests -pl fm-common,fm-dddrive,fm-domain -am
if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build successful! All JARs have been created.
) else (
    echo.
    echo Build failed! Check the errors above.
    exit /b %ERRORLEVEL%
)
