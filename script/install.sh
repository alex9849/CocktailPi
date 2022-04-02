#!/bin/bash

WORKING_DIR="$(pwd)"
BRANCH=develop

if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root"
   exit 1
fi

echo "The script will install the cocktailmaker application into the current folder:"
echo "${WORKING_DIR}"
echo ""
echo "Press [ENTER] to continue or ctrl + c to abort!"
echo ""

read -r -n1 key
while [[ $key != '' ]]; do
  echo ""
  echo "Press [ENTER] to continue or ctrl + c to abort!"
  echo ""
  read -r -n1 key
done

# Install dependencies
echo "Installing dependencies..."
sudo apt update && apt -yq upgrade
sudo apt -yq install default-jdk python python3-pip pigpio python-pigpio python3-pigpio wget alsa-utils python3-pip
if ! command -v docker &> /dev/null
then
    echo "Docker not be found! Installing..."
    sudo curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo rm get-docker.sh
fi

sudo apt install python3-pip -y
sudo pip3 install docker-compose
echo "Dependencies installed!"

echo "Downloading application"
wget https://github.com/alex9849/pi-cocktail-maker/releases/latest/download/server.jar -O cocktailmaker.jar
wget https://raw.githubusercontent.com/alex9849/pi-cocktail-maker/${BRANCH}/script/files/application.properties -O application.properties
wget https://raw.githubusercontent.com/alex9849/pi-cocktail-maker/${BRANCH}/script/files/docker-compose.yml -O docker-compose.yml
docker-compose up -d
echo "Creating service"
sudo ln -s "${WORKING_DIR}"/cocktailmaker.jar /etc/init.d/cocktailmaker
sudo chmod +x /etc/init.d/cocktailmaker
sudo systemctl daemon-reload
echo "Adding cocktailmaker to autostart"
sudo update-rc.d cocktailmaker defaults
echo "Starting cocktailmaker"
sudo service cocktailmaker start

echo "Script finished! You can now configure the created database (docker-compose.yml) and the application (application.properties)"
echo "By default the database in accessible on localhost only. The webinterface should be reachable on port 80!"
echo "Logs can be found in /var/log/cocktailmaker.log"
echo "You can start and stop the application by using \"service cocktailmaker (start|stop|restart|status)\""
sudo rm -- "$0"
