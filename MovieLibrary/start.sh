#!/bin/bash
P='"'
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DIRF=$P$DIR$P
LIB="/lib"
BIN="/bin:"
LIBF=$DIR$LIB
BINF=$DIR$BIN
STAR="/*"
LIBS=$LIBF$STAR
CP=$BINF$LIBS
echo $DIRF
echo $LIBF
echo $BINF
echo $CP
#java -Djava.library.path=./lib -cp ./bin:./lib/* jffsss.launch.MovieLibraryLauncher --WorkingDirectory=$DIR
cd $DIR
java -Djava.library.path=./lib -cp ./bin:./lib/* jffsss.launch.MovieLibraryLauncher --WorkingDirectory=$DIRF
#/usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java -Djava.library.path=$LIBF -cp $CP jffsss.launch.MovieLibraryLauncher --WorkingDirectory=$DIR --FileExtensions="mkv,iso,avi,mp4,mpg,mpeg,mov,divx,xvid,iso,ts"
