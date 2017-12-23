#!/bin/sh

if [ ! -f /opt/mission-turtle/Convertor.jar ]; then
    echo "/opt/tmx-test: Missing mission-turtle/Convertor.jar file" 1>&2
    exit 1
fi

if [ "$#" -ne 1 ]; then
    echo "Usage: tmx-test <TMX level file>"
    exit 2
fi

java -cp /opt/mission-turtle/Convertor.jar turtle.editor.TMXTestLevel $1