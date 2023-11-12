import argparse
import requests
import os
import stat

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="CocktailMaker updater")
    parser.add_argument("-s", "--starting_version", help="The software's current version to update from.")
    parser.add_argument("-f", "--file_name", help="The software's file name.")
    args = parser.parse_args()
    if args.starting_version is None:
        print("No starting version given!")
        exit(1)

    if args.file_name is None:
        print("No file name given!")
        exit(1)

    if not os.path.exists(args.file_name):
        print("specified file doesn't exist!")
        exit(1)

    releases = requests.get("https://api.github.com/repos/alex9849/pi-cocktail-maker/releases").json()
    os.system("service cocktailmaker stop")


    # Download current server.jar and store it in cocktailmaker_update.jar
    file_url = None
    for asset in releases[0]["assets"]:
        if asset["name"] == "server.jar":
            file_url = asset["browser_download_url"]
            break

    if file_url == None:
        print("Couldn't download updated file!")
        exit(1)

    if os.path.exists("cocktailmaker_update.jar"):
        os.remove("cocktailmaker_update.jar")

    if os.path.exists("update_linux_delta.sh"):
        os.remove("update_linux_delta.sh")

    update_jar_request = requests.get(file_url)

    # Execute update scripts for all versions from current till recent
    starting_version_found = False
    for release in reversed(releases):
        if not starting_version_found and args.starting_version != release["tag_name"]:
            continue
        starting_version_found = True
        for asset in release["assets"]:
            if asset["name"] != "update_linux_delta.sh":
                continue

            update_delta_sh_request = requests.get(asset["browser_download_url"])
            with open('update_linux_delta.sh', 'wb') as f:
                f.write(update_delta_sh_request.content)
            st = os.stat('update_linux_delta.sh')
            os.chmod('update_linux_delta.sh', st.st_mode | stat.S_IEXEC)
            os.system('./update_linux_delta.sh')
            os.remove('update_linux_delta.sh')



    if not starting_version_found:
        print("Starting version not found!")
        exit(1)

    with open('cocktailmaker_update.jar', 'wb') as f:
        f.write(update_jar_request.content)

    if os.path.exists(args.file_name):
        os.remove(args.file_name)

    os.rename("cocktailmaker_update.jar", args.file_name)
    st = os.stat(args.file_name)
    os.chmod(args.file_name, st.st_mode | stat.S_IEXEC)
    os.system("systemctl daemon-reload")
    os.system("service cocktailmaker start")

