#!/bin/bash

#
# Script running Gradle "check" task on project if committing to main, develop or release branches.
#

BRANCH_NAME=$(git rev-parse --abbrev-ref HEAD)

if [[ "$BRANCH_NAME" != "main" && "$BRANCH_NAME" != "develop" && "$BRANCH_NAME" != "release/"* ]]; then
    exit 0
fi

echo "Running Gradle 'check' task on branch: $BRANCH_NAME..."
./gradlew check

if [ $? -ne 0 ]; then
    echo -e "\033[31m"
    echo -e "----------------------------------------"
    echo -e "COMMIT ABORTED. Please solve reported issues before retrying."
    echo -e "----------------------------------------\033[0m"
    exit 1
fi

exit 0
