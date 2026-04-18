#!/bin/sh
#
# Gradle start up script for UN*X
#

# Attempt to set APP_HOME
APP_HOME=$(cd "$(dirname "$0")" && pwd)
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Default JVM options
DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD=maximum

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support
cygwin=false
msys=false
darwin=false
nonstop=false
case "$(uname)" in
  CYGWIN* ) cygwin=true ;;
  Darwin* ) darwin=true ;;
  MSYS* | MINGW* ) msys=true ;;
  NONSTOP* ) nonstop=true ;;
esac

JAVA_HOME="${JAVA_HOME:-}"
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
elif ! command -v java > /dev/null 2>&1; then
    die "ERROR: JAVA_HOME is not set and no 'java' command could be found."
else
    JAVACMD=java
fi

# Check existence of Gradle Wrapper jar
if [ ! -f "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" ]; then
    die "ERROR: Gradle wrapper jar not found. Run: gradle wrapper"
fi

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain "$@"
