#!/bin/bash
sudo docker build --tag=uploader /home/alacambra/uploader
sudo docker kill uploader
sudo docker kill nginx-hub
sudo docker rm uploader
sudo docker rm nginx-hub
sudo docker run --name uploader -v /home/alacambra/uploader/files:/opt/uploader/files -itd uploader
#sudo docker run --name uploader -itd uploader
sudo docker run -d -p 80:80 \
  --name nginx-hub --link nginx-www:www \
  --link cookinghelper:cookinghelper \
  --link uploader:uploader \
  alacambra/nginx:hub nginx
