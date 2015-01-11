#!/bin/sh
cd ..
mvn clean package
cd build
ssh lacambra.de mkdir /home/alacambra/uploader
scp ../target/uploader.war build.sh Dockerfile lacambra.de:/home/alacambra/uploader
ssh lacambra.de cd /home/alacambra/uploader
ssh -t lacambra.de sudo /home/alacambra/uploader/build.sh