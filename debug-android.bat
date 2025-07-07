@echo off
REM Android Debug Helper Script Template
REM Replace PACKAGE_NAME and MAIN_ACTIVITY with your app's values

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
    echo === Android Debug Helper - Help ===
    echo Usage: debug-android.bat [options]
    echo.
    echo Options:
    echo   --nobuild, -n    Skip build and install, just start the app in debug mode
    echo   --help, -h       Show this help message
    echo.
    exit /b 0
)

echo === Android Debug Helper ===
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

REM Start the app in debug mode
echo 3. Starting app in debug mode...
adb shell am start -D -n %PACKAGE_NAME%/%MAIN_ACTIVITY%
echo.

REM Wait a moment for the app to start
timeout /t 3 /nobreak > nul

REM Find the process ID using findstr (Windows equivalent of grep)
echo 4. Finding app process ID...
echo All processes containing '%PACKAGE_NAME%':
adb shell ps | findstr %PACKAGE_NAME%
echo.

REM Try to automatically extract the process ID
for /f "tokens=2" %%i in ('adb shell ps ^| findstr %PACKAGE_NAME%') do (
    set PROCESS_ID=%%i
    goto :found_process
)

:found_process
if defined PROCESS_ID (
    echo Found Process ID: %PROCESS_ID%
    echo.
    
    REM Set up port forwarding
    echo 5. Setting up port forwarding...
    adb forward tcp:8700 jdwp:%PROCESS_ID%
    echo Port forwarding set up: localhost:8700 -^> jdwp:%PROCESS_ID%
    echo.
    
    echo 6. Ready for debugging!
    echo    - Your app is waiting for debugger
    echo    - Use 'Debug Android Java ^(Remote^)' configuration in VS Code
    echo    - Connect to localhost:8700
    echo.
    echo To clean up port forwarding later, run:
    echo    adb forward --remove tcp:8700
) else (
    echo Could not automatically find process ID.
    echo Please look at the process list above and run manually:
    echo    adb forward tcp:8700 jdwp:PROCESS_ID
    echo Then use 'Debug Android Java ^(Remote^)' in VS Code
)

echo.
pause

