#!/bin/sh
# Maven Wrapper script for Unix/Linux/macOS
# Downloads Maven if not present and runs it

set -e

MAVEN_WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"

if [ -f "$MAVEN_WRAPPER_PROPERTIES" ]; then
    DIST_URL=$(grep 'distributionUrl' "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2-)
else
    DIST_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip"
fi

MAVEN_HOME="${HOME}/.m2/wrapper/dists/apache-maven-3.9.6"
MVN_CMD="${MAVEN_HOME}/bin/mvn"

if [ ! -f "$MVN_CMD" ]; then
    echo "Downloading Maven from ${DIST_URL}..."
    mkdir -p "${MAVEN_HOME}"
    TMPFILE=$(mktemp /tmp/maven-XXXXXX.zip)
    if command -v curl > /dev/null 2>&1; then
        curl -fsSL -o "$TMPFILE" "$DIST_URL"
    elif command -v wget > /dev/null 2>&1; then
        wget -q -O "$TMPFILE" "$DIST_URL"
    else
        echo "Error: curl or wget is required to download Maven" >&2
        exit 1
    fi
    unzip -q -o "$TMPFILE" -d "${HOME}/.m2/wrapper/dists/"
    # The zip extracts to apache-maven-3.9.6/ inside the target dir
    if [ ! -f "$MVN_CMD" ]; then
        # Handle nested directory from zip
        EXTRACTED_DIR=$(find "${HOME}/.m2/wrapper/dists/" -maxdepth 1 -name 'apache-maven-*' -type d | head -1)
        if [ -n "$EXTRACTED_DIR" ] && [ "$EXTRACTED_DIR" != "$MAVEN_HOME" ]; then
            rm -rf "$MAVEN_HOME"
            mv "$EXTRACTED_DIR" "$MAVEN_HOME"
        fi
    fi
    rm -f "$TMPFILE"
    chmod +x "$MVN_CMD"
fi

exec "$MVN_CMD" "$@"
