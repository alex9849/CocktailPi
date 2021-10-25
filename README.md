# CocktailMaker

The “Cocktail-Maker” is not only a piece of software. 
It’s a cocktail-mixing-machine, that works with a Raspberry-Pi, 
that controls multiple pumps, which have different ingredients assigned. 
The Cocktail-Maker provides a UI, that can be accessed via web browser. 
Admins can create other users and assign them to multiple roles with 
different permissions. Users can create own cocktails and if the 
Cocktail-Maker has all the needed ingredients, they can order them. 
Cocktails can be categorized and shared with other users.

New recipes can be created in the UI. The user can add ingredients to 
different production steps. Ingredients that are in the same 
production step get bottled at the same time. 
The order in which ingredients get bottled can be changed via drag & 
drop.

#Demo
A demo can be found here: https://cocktailmaker-demo.liggesmeyer.net/
User: Admin  
Password: 123456  

# Build the hardware

In order to use this machine in a useful way, you need to build your own hardware.
In simple words produces the Cocktail-Maker-Software different recipes by 
controlling a relay board, that opens or closes the electronic circuit 
for multiple pumps, which pump the different liquids into the glass.
The user can add new pumps in the webinterface, where he has to specify a GPIO-Pin
for every pump and how long that pumps needs to pump exactly on centiliter in 
milliseconds. You can build your machine how you like it. The only important thing
is that you need to be able to apply that concept to your setup.

###This is the hardware that I've used for my machine:
 * 1x RaspberryPi: https://amzn.to/3metGLm (needs to be able to run docker for a simpler setup)
 * 1x Relay-Board: https://amzn.to/3bcaNSR
 * 8x Dosing pumps: https://amzn.to/3nui0TN
 * 8x Diodes (reverse current diodes)
 * 1x Power supply: https://bit.ly/2ZJPIcP
 * Silicone hose: https://amzn.to/3Efum9f
 * Cables

# Installation

For easy deployment, this application runs in Docker.
A PostgreSQL-Database is required.
Run this code on a fresh raspbian installation as root user to install docker:
````bash
apt update && apt -y upgrade
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
apt install python3-pip -y
sudo pip3 install docker-compose
````

Create a file named `docker-compose.yml` in your /root folder and paste the following content:
````yaml
version: '3.3'

services:
  app:
    container_name: Cocktailmaker
    restart: always
    privileged: true
    image: alex9849/pi-cocktailmaker:latest-pi
    depends_on:
      - db
    ports:
      - 80:8080
    environment:
      DB_HOST: 'db'
      DB_PORT: 5432
      DB_DATABASE: 'cocktailmaker'
      DB_USER: 'postgres'
      DB_PASSWORD: 'Chang3M3OnPr0duct1on'
      JWT_SECRET: 'SecretSecretKey8375324'
  db:
    image: postgres:latest
    restart: always
    environment:
      POSTGRES_PASSWORD: 'Chang3M3OnPr0duct1on'
      POSTGRES_DB: 'cocktailmaker'
    volumes:
      - type: volume
        source: cocktail-db-data
        target: /var/lib/postgresql/data
        volume:
          nocopy: true

volumes:
  cocktail-db-data:
````
Don't forget to change the JWT_SECRET! If you want to enable https for the 
webinterface you may be able for do that by tunneling the app-traffic 
though a reverse-proxy.

Start the application by executing the following command as root user in the same
directory as your docker-compose.yml
``docker-compose up -d``
Now the latest version of the application gets downloaded and started. This may take a while.
After startup you should be able to reach your application on the port 80!
The default user is named `admin` and uses the password ``123456``.

Next you need to add your pumps. Go to 'Administration' -> 'Pumps' 
and click 'Add pump'. Fill in the form shown for. Notice
that the field 'WiringPi-Pin' is not the GPIO-Pin. 
Since there are different RaspberryPi-Boards you need 
to find out the right WiringPi-Pin number for your 
given GPIO-Pins. This link might be helpful: https://de.pinout.xyz/pinout/wiringpi
Alternatively you may install the WiringPi-Library on your Raspberry.
The command 'gpio readall' also prints the WiringPi-Pin-Numbers for your board.