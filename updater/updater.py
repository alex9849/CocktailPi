import json

import requests
import urllib

if __name__ == "__main__":
    response = requests.get("https://api.github.com/repos/alex9849/pi-cocktail-maker/releases").json()
