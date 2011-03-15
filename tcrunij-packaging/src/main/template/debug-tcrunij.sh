#!/bin/bash

path="$0"

while [ -L "$path" ]; do
    dir=`dirname "$path"`
    path=`ls -l "$path" | sed -e 's/.* -> //'`
    cd "$dir"
done

dir=`dirname "$path"`
file=`basename "$path"`
cdir=`cd "$dir" && pwd -P`
cd "$cdir"

echo "TCRunIJ will halt until debugger is attached on port 8000."
export JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,address=8000"

./tcrunij.sh "$@"
