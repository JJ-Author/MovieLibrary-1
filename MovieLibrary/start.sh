#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
LIB="/lib"
BIN="/bin:"
LIBF=$DIR$LIB
BINF=$DIR$BIN
STAR="/*"
LIBS=$LIBF$STAR
CP=$BINF$LIBS
echo $LIBF
echo $BINF
echo $CP
#java -Djava.library.path=./lib -cp ./bin:./lib/* jffsss.launch.MovieLibraryLauncher --WorkingDirectory=$DIR
java -Djava.library.path=$LIBF -cp /home/hadoop/Desktop/git/MovieLibrary/MovieLibrary/bin:/home/hadoop/Desktop/git/MovieLibrary/MovieLibrary/lib/* jffsss.launch.MovieLibraryLauncher --WorkingDirectory=$DIR
