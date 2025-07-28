@echo off
REM ------------------------------------------
REM Spring Boot + Docker Deployment Script
REM ------------------------------------------

REM Step 1: Build Spring Boot JAR
echo 🚧 Building Spring Boot application...
call mvn clean install

IF %ERRORLEVEL% NEQ 0 (
    echo ❌ Maven build failed. Fix the errors and try again.
    pause
    exit /b
)

REM Step 2: Build Docker Image
echo 🐳 Building Docker image: venky3540/java-application
docker build -t venky3540/java-application .

IF %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker build failed. Check your Dockerfile and project.
    pause
    exit /b
)

REM Step 3: Docker Login (will prompt if not already logged in)
echo 🔐 Logging in to Docker Hub...
docker login

IF %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker login failed.
    pause
    exit /b
)

REM Step 4: Push Docker Image
echo 🚀 Pushing Docker image to Docker Hub...
docker push venky3540/java-application

IF %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker push failed. Check your internet or credentials.
    pause
    exit /b
)

REM Step 5: Reminder to deploy on Render
echo.
echo ✅ Image pushed successfully!
echo 🌐 Go to https://dashboard.render.com
echo 🔁 Select your backend service and click "Manual Deploy"
echo.

pause
