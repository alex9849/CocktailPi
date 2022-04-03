#!/bin/bash

WORKING_DIR="$(pwd)"
BRANCH=master

if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root"
   exit 1
fi

echo "The script will install the cocktailmaker application into the current folder:"
echo "${WORKING_DIR}"
echo "Press [ENTER] to continue or ctrl + c to abort!"

read -r -n1 key
while [[ $key != '' ]]; do
  echo "Press [ENTER] to continue or ctrl + c to abort!"
  read -r -n1 key
done

# Install dependencies
echo "Installing dependencies..."
sudo apt update && apt -yq upgrade
sudo apt -yq install default-jdk python python3-pip pigpio python-pigpio python3-pigpio wget alsa-utils python3-pip
echo "Dependencies installed!"

echo "Downloading application"
wget https://github.com/alex9849/pi-cocktail-maker/releases/latest/download/server.jar -O cocktailmaker.jar
wget https://raw.githubusercontent.com/alex9849/pi-cocktail-maker/${BRANCH}/script/files/application.properties -O application.properties
echo "Creating service"
sudo ln -s "${WORKING_DIR}"/cocktailmaker.jar /etc/init.d/cocktailmaker
sudo chmod +x /etc/init.d/cocktailmaker
sudo systemctl daemon-reload
echo "Adding cocktailmaker to autostart"
sudo update-rc.d cocktailmaker defaults

echo ""
echo "Script finished! You can now have to fill in the postgres database details into the config file (application.properties)"
echo "By default the postgres database in accessible on localhost only. The webinterface should be reachable on port 80!"
echo "Logs can be found in /var/log/cocktailmaker.log"
echo ""
echo "You can start and stop the application by using:"
echo "service cocktailmaker (start|stop|restart|status)"
echo ""
echo "The application startup will fail if no postgres database has been specified in the application.properties"
sudo rm -- "$0"
