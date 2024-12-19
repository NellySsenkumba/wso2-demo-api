#!/bin/bash

# Resolve the directory of the actual script, even if called via a symbolic link
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

# Change to the script's directory
cd "$SCRIPT_DIR" || { echo "Failed to change directory to script location. Exiting."; exit 1; }

# Check if "--build" or "-B" is present in the arguments
for arg in "$@"; do
  if [ "$arg" == "--build" ] || [ "$arg" == "-B" ]; then
    echo "Rebuilding project..."
    mvn clean install -DskipTests > /dev/null 2>&1
    if [ $? -ne 0 ]; then
      echo "Failed to build the project. Exiting."
      exit 1
    fi
    echo "Project Built Successfully."
    break
  fi
  if [ "$arg" == "--stop" ]; then
    if [ -f ./pid.file ]; then
      kill -9 $(cat ./pid.file) > /dev/null 2>&1
      rm ./pid.file
      echo "Application stopped successfully."
      exit 0
    else
      echo "Application is not running."
      exit 1
    fi
  fi
done

# Stop the existing process if pid.file exists
if [ -f ./pid.file ]; then
  kill -9 $(cat ./pid.file) > /dev/null 2>&1
  rm ./pid.file
fi

# Start the application and save the PID
nohup java -jar ./target/wso2-demo-api-1.0.0.jar >> ./logs.log 2>&1 &
echo $! > ./pid.file
echo "Application started with PID: $(cat ./pid.file) and running on port 8080"
exit 0
