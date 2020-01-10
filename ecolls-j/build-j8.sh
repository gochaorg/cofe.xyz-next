#!/usr/bin/env bash
BASEDIR=$(readlink -f $(dirname $0))
echo "BASEDIR $BASEDIR"

function findJDK {
    if [ -e "/home/user/apps/jdk-12" ] ; then
        export JAVA_HOME=/home/user/apps/jdk-12
        JAVAC=$JAVA_HOME/bin/javac
    else
        echo "jdk 12 not found"
        exit 1
    fi
}

findJDK

# build filelist
find $BASEDIR/src -name '*.java' | grep -v '/src/test/' | grep -v 'module-info.java' > filelist.txt

function compile {
    local DEST
    DEST=$BASEDIR/target/classes-$1/

    mkdir -p $DEST

    echo "compile for java $1 to $DEST"
    $JAVAC --release $1 -d $DEST @filelist.txt
}

compile 8
#compile 7
