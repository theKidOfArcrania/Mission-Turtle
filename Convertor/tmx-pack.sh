#!/bin/sh

if [ ! -f /opt/mission-turtle/Convertor.jar ]; then
    echo "/opt/tmx-test: Missing mission-turtle/Convertor.jar file" 1>&2
    exit 1
fi

if [ "$#" -gt 1 ]; then
    echo "Usage: tmx-pack [Level directory...]"
    echo "  Level directory will default to current directory."
    exit 2
elif [ "$#" -eq 1 ]; then
    java -cp /opt/mission-turtle/Convertor.jar turtle.editor.TMXPack $1
else
    java -cp /opt/mission-turtle/Convertor.jar turtle.editor.TMXPack `pwd`
fi
