#!/bin/sh

WORKING_DIR="$(pwd)"
BRANCH=develop
# Install dependencies
apt update
apt -yq install default-jdk python python3-pip pigpio python-pigpio python3-pigpio wget alsa-utils python3-pip
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
apt install python3-pip -y
sudo pip3 install docker-compose

wget https://github.com/alex9849/pi-cocktail-maker/releases/latest/download/server.jar -O cocktailmaker.jar
wget https://raw.githubusercontent.com/alex9849/pi-cocktail-maker/${BRANCH}/script/files/application.properties -O application.properties
wget https://raw.githubusercontent.com/alex9849/pi-cocktail-maker/${BRANCH}/script/files/docker-compose.yml -O docker-compose.yml
docker-compose up -d
ln -s "${WORKING_DIR}"/cocktailmaker.jar /etc/init.d/cocktailmaker
chmod +x /etc/init.d/cocktailmaker
systemctl daemon-reload
service cocktailmaker start
update-rc.d cocktailmaker defaults
