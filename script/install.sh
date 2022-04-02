#!/bin/bash

WORKING_DIR="$(pwd)"
BRANCH=develop

if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root"
   exit 1
fi

echo "The script will install the cocktailmaker application into the current folder:"
echo "${WORKING_DIR}"
echo "Press [ENTER] to continue or ctrl + c to abort!"

read -s -N 1 -t 1 key
while [[ $key != $'\x0a' ]]; do
  echo "Press [ENTER] to continue or ctrl + c to abort!"
  read -s -N 1 -t 1 key
done

# Install dependencies
echo "Installing dependencies..."
sudo apt update
sudo apt -yq install default-jdk python python3-pip pigpio python-pigpio python3-pigpio wget alsa-utils python3-pip
sudo curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo apt install python3-pip -y
sudo pip3 install docker-compose
echo "Dependencies installed!"

echo "Downloading application"
wget https://github.com/alex9849/pi-cocktail-maker/releases/latest/download/server.jar -O cocktailmaker.jar
wget https://raw.githubusercontent.com/alex9849/pi-cocktail-maker/${BRANCH}/script/files/application.properties -O application.properties
wget https://raw.githubusercontent.com/alex9849/pi-cocktail-maker/${BRANCH}/script/files/docker-compose.yml -O docker-compose.yml
docker-compose up -d
sudo ln -s "${WORKING_DIR}"/cocktailmaker.jar /etc/init.d/cocktailmaker
sudo chmod +x /etc/init.d/cocktailmaker
sudo systemctl daemon-reload
sudo service cocktailmaker start
sudo update-rc.d cocktailmaker defaults
sudo rm -- "$0"
