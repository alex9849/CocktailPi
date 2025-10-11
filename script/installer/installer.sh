#!/bin/bash

langsel=$1


# 1 = CocktailPi
# 2 = CocktailPi + Touchscreen without keyboard
# 3 = CocktailPi + Touchscreen + Keyboard
modsel=$2


function color {
    if [ "$1" = "c" ]; then
        txt_color=6
    fi  
    if [ "$1" = "g" ]; then
        txt_color=2
    fi
    if [ "$1" = "r" ]; then
        txt_color=1
    fi
    if [ "$1" = "y" ]; then
        txt_color=3
    fi
    if [ "$2" = "n" ]; then
        echo -n "$(tput setaf $txt_color)$3"
    else
        echo "$(tput setaf $txt_color)$3"   
    fi
    tput sgr0
}

function select_lang {
    clear
    echo "CocktailPi Installer"
    echo ""
    echo "(1) German"
    echo "(2) English"
    echo "(3) Exit"
    echo ""

    if [ "$langsel" = "" ]; then
        echo "Bitte waehlen Sie ihre Sprache."
        echo -n "Please select your language: "
    else
        color r x "Bitte geben Sie entweder 1,2 oder 3 ein!"
        color r n "Please enter only 1,2 or 3: "
    fi

    read -n 1 langsel
	langselsize=${#langsel} 
	if [ "$langselsize" = "0" ]; then
	    langsel=99
        clear
        select_lang
	fi

    for i in $langsel; do
    case "$i" in
        '1')
            clear
        ;;
        '2')
            clear
        ;;
        '3')
            clear
            exit 0
        ;;
        *)
            langsel=99
            clear
            select_lang
        ;;
    esac
    done
}


function select_mode {
    clear
    if [ "$langsel" = "1" ]; then
        echo "Installations Auswahl"
        echo ""
        echo "Wählen sie 1, 2 oder 3 um die entsprechende Installation durchzuführen."
        echo ""
        echo "(1) CocktailPi"
        echo "(2) CocktailPi + Touchscreen ohne Bildschirmtastatur"
        echo "(3) CocktailPi + Touchscreen mit Bildschirmtastatur"
        echo "(4) Konfiguration: Größe der Touchscreen UI ändern"
        echo ""
        echo "(0) Exit"
    else
        echo "Installation selection"
        echo ""
        echo "Choose 1 or 2 or 3 to carry out the corresponding installation."
        echo ""
        echo "(1) CocktailPi"
        echo "(2) CocktailPi + Touchscreen without on-screen keyboard"
        echo "(3) CocktailPi + Touchscreen with on-screen keyboard"
        echo "(4) Configuration: Change size of Touchscreen UI"
        echo ""
        echo "(0) Exit"
    fi
    echo ""

    if [ "$langsel" = "1" ]; then
        if [ "$modsel" = "" ]; then
            echo -n "Bitte geben Sie ihre Auswahl an: "
        else
            color r n "Bitte geben Sie entweder 1,2,3,4 oder 0 ein: "
        fi
    else
        if [ "$modsel" = "" ]; then
            echo -n "Please enter your selection: "
        else
            color r n "Please enter either 1,2,3,4 or 0: "
        fi
    fi

    read -n 1 modsel
	  modselsize=${#modsel}
	  if [ "$modselsize" = "0" ]; then
	      modsel=99
        clear
        select_mode
	fi

    for i in $modsel; do
    case "$i" in
        '1')
            clear
        ;;
        '2')
            clear
        ;;
        '3')
            clear
        ;;
        '4')
            clear
        ;;
        '0')
            clear
            exit 0
        ;;
        *)
            modsel=99
            clear
            select_mode
        ;;
    esac
    done
}

function select_confirm {
    clear
    echo -e "$1"
    echo ""
    if [ "$langsel" = "1" ]; then
        echo "(1) Bestätigen - Weiter"
    else
        echo "(1) Confirm - Continue"
    fi
    echo ""

    if [ "$langsel" = "1" ]; then
        if [ "$confirmsel" = "" ]; then
            echo -n "Bitte geben Sie ihre Auswahl an: "
        else
            color r n "Bitte bestätigen Sie mit 1: "
        fi
    else
        if [ "$confirmsel" = "" ]; then
            echo -n "Please enter your selection: "
        else
            color r n "Please confirm with 1: "
        fi
    fi

    read -n 1 confirmsel
	  confirmselsize=${#confirmsel}
  	if [ "$confirmselsize" = "0" ]; then
	      confirmsel=99
        clear
        select_confirm "$1"
	  fi

    for i in $confirmsel; do
    case "$i" in
        '1')
            clear
        ;;
        *)
            confirmsel=99
            clear
            select_confirm "$1"
        ;;
    esac
    done
}

function select_confirm_exit {
    clear
    echo -e "$1"
    echo ""
    if [ "$langsel" = "1" ]; then
        echo "(1) Bestätigen - Weiter"
		echo "(2) Beenden"
    else
        echo "(1) Confirm - Continue"
		echo "(2) Exit"
    fi
    echo ""

    if [ "$langsel" = "1" ]; then
        if [ "$confirmsel" = "" ]; then
            echo -n "Bitte geben Sie ihre Auswahl an: "
        else
            color r n "Bitte geben Sie entweder 1 oder 2 ein: "
        fi
    else
        if [ "$confirmsel" = "" ]; then
            echo -n "Please enter your selection: "
        else
            color r n "Please enter either 1 or 2: "
        fi
    fi

    read -n 1 confirmsel
	confirmselsize=${#confirmsel} 
	if [ "$confirmselsize" = "0" ]; then
	    confirmsel=99
        clear
        select_confirm_exit "$1"
	fi

    for i in $confirmsel; do
    case "$i" in
        '1')
            clear
        ;;
		'2')
            clear
            exit 0
        ;;
        *)
            confirmsel=99
            clear
            select_confirm_exit "$1"
        ;;
    esac
    done
}


if [ ! -n "$langsel" ]; then
    select_lang
fi

if [ "$(id -u)" != "0" ]; then
    clear
    if [ "$langsel" = "1" ]; then
        color r x "Sie benötigen root Rechte. Wechseln sie zum root-Benutzer mit \"sudo -i\""
    else
        color r x "You need root privileges. Switch to the roor-User using \"sudo -i\""
    fi
    exit 1
fi


if [ ! -n "$modsel" ]; then
    select_mode
fi

if [ "$modsel" = "4" ]; then
  if ! [ -d "/home/pi/.config/chromium-profile/" ]; then
    if [ "$langsel" = "1" ]; then
        color r x "Touchscreen UI ist nicht installiert. Bitte installieren Sie diese zuerst und starten Sie dann das Skript neu!"
    else
        color r x "Touchscreen UI is not installed. Please install it first and restart the script afterwards!"
    fi
    exit 1
  fi

  while true; do
    if [ "$langsel" = "1" ]; then
        read -p "Geben Sie den Skalierungsfaktor an (positive ganze Zahl, oder Kommazahl): " zoomsel
    else
        read -p "Enter default scaling level (positive number, may be floating point number): " zoomsel
    fi
    # Check if input is a positive number (integer or float)
    if [[ "$zoomsel" =~ ^[+]?[0-9]+([.][0-9]+)?$ ]] && awk "BEGIN { exit ($zoomsel > 0 ? 0 : 1) }"; then
      break
    else
      if [ "$langsel" = "1" ]; then
          color r x "Ungültige Eingabe. Bitte geben Sie eine positive Zahl (z.B. 0.5, 1.0, 3.2, 5) an."
      else
          color r x "Invalid input. Please enter a positive number (e.g., 0.5, 1.0, 3.2, 5)."
      fi
    fi
  done
  WAYFIRE_CMD="wayfire -c ~/.config/wayfire.ini"
  WAYFIRE_EXEC=$(basename $(echo "$WAYFIRE_CMD" | awk '{print $1}'))
  FILE="/home/pi/.config/chromium-profile/Default/Preferences"
  NEW_JSON="{\"partition\": {\"default_zoom_level\": {\"x\": $zoomsel}}}"

  pkill -f wayfire

  sudo -u pi mkdir -p "$(dirname "$FILE")"
  if [ ! -f "$FILE" ]; then
    # File does not exist — create with NEW_JSON
    sudo -u pi echo "$NEW_JSON" > "$FILE"
  else
    # File exists — merge NEW_JSON into it
    TMP_FILE='TempPreferences'
    touch "$TMP_FILE"
    echo "$NEW_JSON" | sudo -u pi jq -s '.[0] * .[1]' "$FILE" - > "$TMP_FILE"
    mv -f "$TMP_FILE" "$FILE"
    chown pi:pi "$FILE"
    chmod 600 "$FILE"

  fi
  PI_ID=$(id -u pi)
  sudo -u pi XDG_RUNTIME_DIR=/run/user/$PI_ID \
    nohup wayfire -c /home/pi/.config/wayfire.ini > /dev/null 2>&1 < /dev/null & disown

  exit 0
fi

if [ "$modsel" = "3" ]; then
    clear
	confirmsel=""
	if [ "$langsel" = "1" ]; then
        select_confirm_exit "Sie haben ausgewählt, dass der Touchscreen mit Bildschirmtastatur installiert werden soll. Hierzu muss zwingend bereits während der Installation ein Bildschirm an dem Raspberry Pi angeschlossen sein. Bitte bestätigen Sie, dass ein Bildschirm an den Raspberry Pi angeschlossen ist."
	else
        select_confirm_exit "You have selected that the touchscreen and an on-screen keyboard should be installed. To do this, a screen must be connected to the Raspberry Pi during installation. Please confirm that a screen is connected to the Raspberry Pi."
	fi
fi

is_ssh=""
if [[ $(who am i) =~ \([0-9a-z\:\.]+\)$ ]]; then
    is_ssh=1
else
    is_ssh=0;
fi

users=($(cat /etc/passwd | grep "/bin/bash" | sed 's/:.*//'))
pi_user_found=0
for user in "${users[@]}"; do
    if [ "$user" = "pi" ]; then
      pi_user_found=1
      break
    fi
done

if [ "$pi_user_found" == "0" ]; then
  clear
  	if [ "$langsel" = "1" ]; then
        echo "Der \"pi\"-Benutzer konnte nicht gefunden werden!"
        echo "Bitte flashen Sie Ihr Betriebssystem erneut und setzen Sie den Standardbenutzer auf \"pi\", bevor Sie den Flashvorgang starten!"
  	else
        echo "The \"pi\"-user could not be found!"
        echo "Please re-flash your operating system and set the default user to \"pi\" before starting the flashing process!"
  	fi
  exit 1
fi

wget -q --spider http://google.com
if [ $? -ne 0 ]; then
    clear
    if [ "$langsel" = "1" ]; then
        echo "Keine Internetverbindung! Bitte verbinde den Raspberry Pi mit dem Internet und starte das Skript erneut!"
    else
        echo "No internet connection! Please connect the Raspberry Pi to the internet and restart the script!"
    fi
    exit 1
fi

if [ "$langsel" = "1" ]; then
    echo "Update system..."
else
    echo "Updating system..."
fi
sleep 2
apt-get update && sudo apt-get -y upgrade

clear
serviceRunning=$(systemctl is-active cocktailpi)
if [ "$serviceRunning" != "inactive" ]; then
    if [ "$langsel" = "1" ]; then
        echo "Stoppe laufende CocktailPi Instanz..."
    else
        echo "Stopping running CocktailPi instance..."
    fi
    service cocktailpi stop
fi

clear
if [ "$langsel" = "1" ]; then
        echo "Installiere benötigte Software..."
    else
        echo "Installing dependencies..."
    fi
sleep 2
apt install --no-install-recommends -y default-jdk i2c-tools python3-full python3-dev python3-pip pigpio wget libjna-java alsa-utils nano curl

clear
if [ "$langsel" = "1" ]; then
    echo "Installiere CocktailPi nach /root/cocktailpi"
else
    echo "Installing CocktailPi into /root/cocktailpi"
fi

mkdir -p /root/cocktailpi
wget -q --show-progress https://github.com/alex9849/CocktailPi/releases/latest/download/server.jar -O /root/cocktailpi/cocktailpi.jar
if [ -f /etc/init.d/cocktailpi ]; then
    unlink /etc/init.d/cocktailpi
fi
ln -s /root/cocktailpi/cocktailpi.jar /etc/init.d/cocktailpi
chmod +x /etc/init.d/cocktailpi

clear
if [ "$langsel" = "1" ]; then
    echo "Füge CocktailPi zu Autostart hinzu..."
else
    echo "Adding CocktailPi to the autostart..."
fi
systemctl daemon-reload
update-rc.d cocktailpi defaults

if [ "$langsel" = "1" ]; then
    echo "Starte CocktailPi Service..."
else
    echo "Starting CocktailPi service..."
fi
service cocktailpi start


if [ "$modsel" = "1" ]; then
    clear
	if [ "$langsel" = "1" ]; then
	    echo "CocktailPi ist installiert! Das Webinterface sollte unter der IP-Adresse Ihres Raspberry Pi über Ihr Heimnetzwerk erreichbar sein!"
        echo "Bitte beachten Sie, dass es je nach verwendetem Raspberry Pi einige Zeit dauern kann, bis die Software vollständig im Hintergrund gestartet ist und die Website erreichbar ist."
    else
	    echo "CocktailPi has been installed! The webinterface should be reachable under the IP-Address of your Raspberry Pi through your home."
        echo "Please note that depending on which Raspberry Pi you use it can take some time till the software completely booted in the background and the website becomes reachable."
    fi
    exit 0
fi

clear
if [ "$langsel" = "1" ]; then
    echo "Installiere touchscreen software..."
else
    echo "Installing touchscreen dependencies..."
fi

if [ -d /home/pi/wait-for-app-html ]; then
    rm -r /home/pi/wait-for-app-html
fi

sudo -u pi wget -q --show-progress https://github.com/alex9849/CocktailPi/releases/latest/download/wait-for-app.tar -O /home/pi/wait-for-app.tar

sudo -u pi tar -xf /home/pi/wait-for-app.tar -C /home/pi/
sudo -u pi rm /home/pi/wait-for-app.tar

if [ "$langsel" = "1" ]; then
    echo "Bitte warten..."
else
    echo "Please wait..."
fi
raspi-config nonint do_boot_behaviour B2

apt install --no-install-recommends -y chromium rpi-chromium-mods
apt install --no-install-recommends -y wayfire seatd xdg-user-dirs jq

raspi-config nonint do_wayland W2

if ! cat /home/pi/.bashrc | grep -q "wayfire -c ~/.config/wayfire.ini"; then
    echo "if [ \"\$(tty)\" = \"/dev/tty1\" ]; then" >> /home/pi/.bashrc
    echo "    wayfire -c ~/.config/wayfire.ini" >> /home/pi/.bashrc
    echo "fi" >> /home/pi/.bashrc
fi

sudo -u pi mkdir -p /home/pi/.config

if [ "$modsel" = "3" ]; then
    pkill -f wayfire
    confirmsel=""
    if [ "$is_ssh" = "1" ]; then
        if [ "$langsel" = "1" ]; then
            select_confirm "Um fortfahren zu können muss ein Bildschirm an dem Raspberry Pi angeschlossen sein. Stellen Sie sicher, dass ein Bildschirm angeschlossen ist."
        else
            select_confirm "To continue, a screen must be connected to the Raspberry Pi. Make sure that a screen is connected."
        fi
    else
        if [ "$langsel" = "1" ]; then
            select_confirm "Im nächsten Schritt wird sich ein Browser auf ihrem Bildschirm öffnen und eine Chrome-Erweiterung anzeigen (Bildschirmtastatur), welche Sie installieren müssen. Sobald Sie auf \"Bestätigen\" drücken haben Sie 100 Sekunden Zeit um die Erweiterung zu installieren, bevor das setup den Browser schließt und die Installation fortsetzt."
        else
            select_confirm "In the next step, a browser will open on your screen and display a Chrome extension (on-screen keyboard), which you must install (Add to chrome). As soon as you press \"Confirm\", you have 100 seconds to install the extension before the setup closes the browser and continues the installation."
        fi
    fi
    if [ -f /home/pi/.config/wayfire.ini ]; then
        rm -r /home/pi/.config/wayfire.ini
    fi
    sudo -u pi touch /home/pi/.config/wayfire.ini
    echo "[core]" >> /home/pi/.config/wayfire.ini
    echo "plugins = \\" >> /home/pi/.config/wayfire.ini
    echo "        autostart" >> /home/pi/.config/wayfire.ini
    echo "" >> /home/pi/.config/wayfire.ini
    echo "[autostart]" >> /home/pi/.config/wayfire.ini
    echo "chromium = chromium https://chromewebstore.google.com/detail/chrome-simple-keyboard-a/cjabmkimbcmhhepelfhjhbhonnapiipj --kiosk --noerrdialogs --enable-extensions --disable-component-update --check-for-update-interval=31536000 --disable-infobars --no-first-run --ozone-platform=wayland --enable-features=OverlayScrollbar --disable-features=OverscrollHistoryNavigation --start-maximized --user-data-dir=/home/pi/.config/chromium-profile" >> /home/pi/.config/wayfire.ini
    echo "screensaver = false" >> /home/pi/.config/wayfire.ini
    echo "dpms = false" >> /home/pi/.config/wayfire.ini

    confirmsel=""
    PI_ID=$(id -u pi)
    sudo -u pi XDG_RUNTIME_DIR=/run/user/$PI_ID \
      nohup wayfire -c /home/pi/.config/wayfire.ini > /dev/null 2>&1 < /dev/null & disown
    if [ "$is_ssh" = "1" ]; then
        if [ "$langsel" = "1" ]; then
            select_confirm "Auf dem Bildschirm sollte sich jetzt der Chrome Webstore öffnen. Fügen Sie die angezeigte Erweiterung zu Chrome hinzu. Kehren Sie nach dem hinzufügen hierher zurück und setzen Sie das Skript mit 1 fort."
        else
            select_confirm "The Chrome Webstore should now open on the screen. Add the displayed extension to Chrome. After adding, return here and continue the script with 1."
        fi
        for i in {1..20}
        do
            echo "Waiting $((20-$i)) seconds..."
            sleep 1
        done
    else
        sleep 100
    fi

    pkill -f wayfire

fi

if [ -f /home/pi/.config/wayfire.ini ]; then
    rm -r /home/pi/.config/wayfire.ini
fi
sudo -u pi touch /home/pi/.config/wayfire.ini

echo "[core]" >> /home/pi/.config/wayfire.ini
echo "plugins = \\" >> /home/pi/.config/wayfire.ini
echo "        autostart" >> /home/pi/.config/wayfire.ini
echo "" >> /home/pi/.config/wayfire.ini
echo "[autostart]" >> /home/pi/.config/wayfire.ini
echo "chromium = chromium /home/pi/wait-for-app-html/index.html --kiosk --noerrdialogs --enable-extensions --disable-component-update --check-for-update-interval=31536000 --disable-infobars --no-first-run --ozone-platform=wayland --enable-features=OverlayScrollbar --disable-features=OverscrollHistoryNavigation --start-maximized --user-data-dir=/home/pi/.config/chromium-profile" >> /home/pi/.config/wayfire.ini
echo "screensaver = false" >> /home/pi/.config/wayfire.ini
echo "dpms = false" >> /home/pi/.config/wayfire.ini


clear
service cocktailpi start
if [ "$modsel" = "3" ] || [ "$modsel" = "2" ]; then
  PI_ID=$(id -u pi)
  sudo -u pi XDG_RUNTIME_DIR=/run/user/$PI_ID \
    nohup wayfire -c /home/pi/.config/wayfire.ini > /dev/null 2>&1 < /dev/null & disown
fi

if [ "$langsel" = "1" ]; then
    echo "CocktailPi wurde installiert und startet jetzt im Hintergrund."
    echo "Es kann einige Zeit dauer, bis sich die Software für den ersten start initialisiert hat."
  	echo ""
  	echo "Wenn dir das Projekt gefällt, kannst du es gerne auf GitHub \"starren\" ;)"
else
    echo "CocktailPi has been installed and is currently starting in the background."
    echo "It can take a while, till the software initialized itself for the first start."
	  echo ""
	  echo "If you like the project, please consider to \"star\" it on GitHub ;)"
fi
