#!/bin/bash

while true; do
  read -p "Enter default zoom level (positive number, may be float): " ZOOM

  # Check if input is a positive number (integer or float)
  if [[ "$ZOOM" =~ ^[+]?[0-9]+([.][0-9]+)?$ ]] && awk "BEGIN { exit ($ZOOM > 0 ? 0 : 1) }"; then
    break
  else
    echo "Invalid input. Please enter a positive number (e.g., 0.5, 1.0, 3.2, 5)."
  fi
done

WAYFIRE_CMD="wayfire -c ~/.config/wayfire.ini"
WAYFIRE_EXEC=$(basename $(echo "$WAYFIRE_CMD" | awk '{print $1}'))
FILE="/home/pi/.config/chromium-profile/Default/Preferences"
NEW_JSON="{\"partition\": {\"default_zoom_level\": {\"x\": $ZOOM}}}"

PID=$(pgrep -x "$WAYFIRE_EXEC")

if [ -n "$PID" ]; then
  kill "$PID"
  while pgrep -x "$WAYFIRE_EXEC" > /dev/null; do
    sleep 0.5
  done
fi

mkdir -p "$(dirname "$FILE")"

if [ ! -f "$FILE" ]; then
  # File does not exist — create with NEW_JSON
  echo "$NEW_JSON" > "$FILE"
else
  # File exists — merge NEW_JSON into it
  TMP_FILE=$(mktemp)
  echo "$NEW_JSON" | jq -s '.[0] * .[1]' "$FILE" - > "$TMP_FILE" && mv "$TMP_FILE" "$FILE"
fi

nohup wayfire -c ~/.config/wayfire.ini > /dev/null 2>&1 & disown
