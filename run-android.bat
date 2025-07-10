@echo off
REM Android Run Helper Script (Non-Debug Mode)

REM === CONFIGURATION - EDIT THESE VALUES ===
set PACKAGE_NAME=com.example.myapplication
set MAIN_ACTIVITY=.MainActivity
REM ==========================================

REM Parse command line arguments
set SKIP_BUILD=0
set SHOW_HELP=0

:parse_args
if "%~1"=="" goto :continue
if /i "%~1"=="--nobuild" set SKIP_BUILD=1
if /i "%~1"=="-n" set SKIP_BUILD=1
if /i "%~1"=="--help" set SHOW_HELP=1
if /i "%~1"=="-h" set SHOW_HELP=1
shift
goto :parse_args

:continue
if %SHOW_HELP%==1 (
    echo === Android Run Helper - Help ===
    echo Usage: run-android.bat [options]
    echo.
    echo Options:
    echo   --nobuild, -n    Skip build and install, just start the app
    echo   --help, -h       Show this help message
    echo.
    exit /b 0
)

echo === Android Run Helper ===
echo Package: %PACKAGE_NAME%
echo Activity: %MAIN_ACTIVITY%
if %SKIP_BUILD%==1 echo Build: SKIPPED
echo.

REM Check if device is connected
echo 1. Checking connected devices...
adb devices
echo.

REM Build and install the app (unless skipped)
if %SKIP_BUILD%==0 (
    echo 2. Building and installing debug APK...
    call gradlew.bat clean assembleDebug installDebug
    echo Build and install completed.
    echo.
) else (
    echo 2. Build and install SKIPPED.
    echo.
)

REM Start the app normally (without debug flag)
echo 3. Starting app...
adb shell am start -n %PACKAGE_NAME%/%MAIN_ACTIVITY%
echo.

echo App launched successfully!
echo.
pause
