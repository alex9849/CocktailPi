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

# Installation

For easy deployment, this application runs in Docker.
A PostgreSQL-Database is required.
Environment-Variables:
  - ``DB_HOST`` Database-Host
  - ``DB_PORT`` Database-Port
  - ``DB_DATABASE`` Database
  - ``DB_USER`` Database-User
  - ``DB_PASSWORD`` Database-Password
  - ``JWT_SECRET`` A random String, that gets used to encrypt the JSON-Web-Tokens

The webinterface starts on port 8080.