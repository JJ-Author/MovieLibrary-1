#!/bin/bash
P='"'
O='"'
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DIRF=$P$DIR$O
eval cd $DIRF
eval java -Djava.library.path=./lib -cp ./bin:./lib/* jffsss.launch.MovieLibraryLauncher --WorkingDirectory=$DIRF --FileExtensions="mkv,iso,avi,mp4,mpg,mpeg,mov,divx,xvid,iso,ts"
#eval /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java -Djava.library.path=./lib -cp ./bin:./lib/* jffsss.launch.MovieLibraryLauncher --WorkingDirectory=$DIRF --FileExtensions="mkv,iso,avi,mp4,mpg,mpeg,mov,divx,xvid,iso,ts"
