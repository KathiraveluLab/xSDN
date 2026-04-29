#!/bin/bash
# xSDN Setup Script
# Builds the project and extracts the core JAR for easy execution.

echo "[xSDN] Starting build..."
mvn clean install

echo "[xSDN] Extracting distribution..."
if [ -f distribution/target/xsdn-1.0-SNAPSHOT.tar.gz ]; then
    tar -xzvf distribution/target/xsdn-1.0-SNAPSHOT.tar.gz
    
    echo "[xSDN] Moving core JAR to root..."
    if [ -f xsdn-1.0-SNAPSHOT/jars/xsdn-1.0-SNAPSHOT.jar ]; then
        mv xsdn-1.0-SNAPSHOT/jars/xsdn-1.0-SNAPSHOT.jar .
        echo "[xSDN] Setup complete. xsdn-1.0-SNAPSHOT.jar is now in the root directory."
    else
        echo "[ERROR] Could not find xsdn-1.0-SNAPSHOT.jar in the extracted directory."
    fi
else
    echo "[ERROR] Build failed or distribution tarball not found."
fi
