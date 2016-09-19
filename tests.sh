#!/usr/bin/env bash
RED='\033[0;31m'
GREEN='\033[0;32m'
BROWN_ORANGE='\033[0;33m'
NC='\033[0m' # No Color

allTestsDir="$(pwd)/src/test/resources/"
courseTests="${allTestsDir}minijava-test-files/"
myTests="${allTestsDir}minijava-dev-tests/"

# Ensure test directories exist
function ensureDirExists {
    if [ $# -ne 1 ]; then
        echo "Missing directory argument."
        exit -2
    fi
    dir=$1
    if [ ! -d ${dir} ]; then
        echo "${dir} doesn't exist. Either create it or update the variables in this script."
        exit -1
    fi
}
ensureDirExists ${allTestsDir}
ensureDirExists ${courseTests}
ensureDirExists ${myTests}

# Ensure jar exists
targetDir="$(pwd)/target"
jar="${targetDir}/minijava-semantical-analyzer-1.0.jar"

if [ ! -f ${jar} ]; then
    echo "${jar} is missing. Run \'mvn package\'."
    exit -1;
fi

echo -e "${BROWN_ORANGE}${0#$(pwd)/} assumes the convention that java source files which contain an error, \
have \"-error\" in the filename.${NC}"

# Testing
shopt -s nullglob  # Causes the array to be empty if none mach.
courseTestFiles=(${allTestsDir}/minijava-*/*.java)

totalNumberOfTests=${#courseTestFiles[@]}
i=1

for file in "${courseTestFiles[@]}"
do
    java -jar ${jar} ${file} > /dev/null 2>&1
    result=$?
    testFilename=${file#${courseTests}}

    if [[ ${testFilename} == *"-error"* ]]; then
        if [ ${result} -ne 1 ]; then
            echo -e "\n${RED}\tSemantical check for ${testFilename} should have failed!${NC}"
        fi
    fi

    printf "\r$i/${totalNumberOfTests}: ${testFilename}"
    ((i++))
done
printf "${GREEN}\rTests completed.${NC}"
