cd /d %~dp0
set var=%cd%
java -Djava.library.path=lib\ -cp "bin;lib\*" jffsss.launch.MovieLibraryLauncher --WorkingDirectory="%var%" --FileExtensions="mkv,iso,avi,mp4,mpg,mpeg,mov,divx,xvid,iso,ts"
