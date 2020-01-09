#!/usr/bin/env bash
BASEDIR=$(readlink -f $(dirname $0))
echo "BASEDIR $BASEDIR"

function findJDK12 {
    if [ -e "/home/user/apps/jdk-12" ] ; then
        export JAVA_HOME=/home/user/apps/jdk-12
        JAVAC=$JAVA_HOME/bin/javac
    else
        echo "jdk 12 not found"
        exit 1
    fi
}

findJDK12

# build filelist
find $BASEDIR/src -name '*.java' | grep -v '/src/test/' | grep -v 'module-info.java' > filelist.txt

mkdir -p $BASEDIR/target/classes-8/
$JAVAC --release 8 -d $BASEDIR/target/classes-8/ @filelist.txt
