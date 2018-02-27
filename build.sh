#!/bin/bash

set -e

#################
#
# Show help and exit
#
#################
function show_help {
	echo "Usage: $0 [-d] [-s] [-q] [-z] [-h]"
	echo "-d - enable maven debugging"
	echo "-b - enable maven batch mode"
	echo "-h - show this help"
	echo "-s - skip unit test execution"
	echo "-q - execute integration test execution"
	echo "-z - skip docker image creation and deployment"
	echo ""
	echo "To passthrough additional arguments straight to maven, enter '--' followed by the extra arguments"
	exit 1
}

#
# This script should be executed from the directory
# it is located in. Try and stop alternatives
#
SCRIPT=`basename "$0"`

if [ ! -f $SCRIPT ]; then
  echo "This script must be executed from the same directory it is located in"
  exit 1
fi

#
# We must be in the same directory as the script so work
# out the root directory and find its absolute path
#
SCRIPT_DIR=`pwd`

echo "Script directory = $SCRIPT_DIR"

#
# Set root directory to be its parent since we are downloading
# lots of stuff and the relative path to the parent pom is
# ../build/parent/pom.xml
#
ROOT_DIR="$SCRIPT_DIR/.."

#
# By default debug is turned off
#
DEBUG=0

#
# Determine the command line options
#
while getopts ":dhsqz" opt;
do
  case $opt in
    b) BATCH=1 ;;
    d) DEBUG=1 ;;
    h) show_help ;;
    s) SKIP=1 ;;
    q) INT_TEST=1 ;;
    z) NO_DOCKER=1 ;;
  esac
done
shift "$(expr $OPTIND - 1)"

EXTRA_ARGS="$@"

#
# Source directory containing komodo codebase
# Should be the same directory as the build script location
#
SRC_DIR="${SCRIPT_DIR}"

#
# Maven repository to use.
# Ensure it only contains komodo related artifacts and
# does not clutter up user's existing $HOME/.m2 repository
#
#LOCAL_REPO="${ROOT_DIR}/m2-repository"
LOCAL_REPO="${HOME}/.m2/repository"

#
# Maven command
#
MVN="mvn clean install"
MVN_FLAGS=

#
# Turn on dedugging if required
#
if [ "${DEBUG}" == "1" ]; then
  echo "** Adding debugging maven parameters **"
  MVN_FLAGS="${MVN_FLAGS} -e -X -U"
fi

#
# Turn on batch if required
#
if [ "${BATCH}" == "1" ]; then
  echo "** Adding batch maven parameters **"
  MVN_FLAGS="${MVN_FLAGS} -B"
fi

#
# Skip tests
#
if [ "${SKIP}" == "1" ]; then
  echo "** Skipping all unit testing **"
  SKIP_FLAG="-DskipTests"
fi

if [ "${INT_TEST}" == "1" ]; then
  echo "** Execution of integration tests enabled **" 
  INTEGRATION_TEST_FLAG="-Parquillian"
fi

DOCKER_RELEASE="-Pdocker-release"
if [ "${NO_DOCKER}" == "1" ]; then
  echo "** Skipping docker image build and local registration using docker-release profile **"
  DOCKER_RELEASE=""
fi

#
# Maven options
# -D maven.repo.local : Assign the $LOCAL_REPO as the target repository
#
MVN_FLAGS="${MVN_FLAGS} -s settings.xml -Dmaven.repo.local=${LOCAL_REPO} ${SKIP_FLAG} ${INTEGRATION_TEST_FLAG} ${DOCKER_RELEASE}"

echo "==============="

# Build and test the komodo codebase
echo "Build and install the komodo plugins"
cd "${SRC_DIR}"
echo "Executing ${MVN} ${MVN_FLAGS} ${EXTRA_ARGS}"
${MVN} ${MVN_FLAGS} ${EXTRA_ARGS}
