import argparse
import contextlib
import urllib.request
import json
import os
import stat
import shutil

releases_url = "https://api.github.com/repos/alex9849/pi-cocktail-maker/releases"
installation_candidate_intermediate_name = "cocktailmaker_update.jar"
update_delta_script_name = "update_linux_delta.sh"
database_file_name = "cocktailmaker-data.db"

def download_file(url, filename):
    with open(filename, 'wb') as out_file:
        with contextlib.closing(urllib.request.urlopen(url)) as fp:
            block_size = 1024 * 8
            while True:
                block = fp.read(block_size)
                if not block:
                    break
                out_file.write(block)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="CocktailMaker updater")
    parser.add_argument("-c", "--current_version", help="The software's current version to update from.")
    parser.add_argument("-f", "--file_name", help="The software's file name.")
    args = parser.parse_args()
    if args.current_version is None:
        print("No starting version given!")
        exit(1)

    if args.file_name is None:
        print("No file name given!")
        exit(1)

    if not os.path.exists(args.file_name):
        print("specified file doesn't exist!")
        exit(1)

    releases = None
    with urllib.request.urlopen(releases_url) as request:
        releases = json.loads(request.read().decode())

    os.system("service cocktailmaker stop")

    # Backup database
    if os.path.exists(f"./db-backup/{args.current_version}"):
        os.remove(f"./db-backup/{args.current_version}")
    os.makedirs(f"./db-backup/{args.current_version}")

    for x in os.listdir():
        if x.startswith(database_file_name):
            shutil.copy(x, f"./db-backup/{args.current_version}")

    # Free required paths
    if os.path.exists(installation_candidate_intermediate_name):
        os.remove(installation_candidate_intermediate_name)

    if os.path.exists(update_delta_script_name):
        os.remove(update_delta_script_name)

    # Filter irrelevant releases find installation candidate
    # Irrelevant are:
    # - all releases before and including current_version
    # - pre releases
    # - drafts
    relevant_releases = []
    installation_candidate_url = None
    current_version_found = False
    for release in reversed(releases):
        if not current_version_found and args.current_version != release["tag_name"]:
            continue
        elif not current_version_found:
            current_version_found = True
            continue

        if release['prerelease'] or release['draft']:
            continue
        relevant_releases.append(release)
        for asset in release["assets"]:
            if asset["name"] == "server.jar":
                installation_candidate_url = asset["browser_download_url"]
                break

    if not current_version_found:
        print("Current version not found!")
        exit(1)

    if installation_candidate_url == None:
        print("Couldn't find installation candidate!")
        exit(1)

    for release in relevant_releases:
        for asset in release["assets"]:
            if asset["name"] != update_delta_script_name:
                continue

            download_file(asset["browser_download_url"], update_delta_script_name)
            st = os.stat(update_delta_script_name)
            os.chmod(update_delta_script_name, st.st_mode | stat.S_IEXEC)
            os.system(f'./{update_delta_script_name}')
            os.remove(update_delta_script_name)

    # Download current server.jar and store it in cocktailmaker_update.jar
    download_file(installation_candidate_url, installation_candidate_intermediate_name)


    if os.path.exists(args.file_name):
        os.remove(args.file_name)

    os.rename(installation_candidate_intermediate_name, args.file_name)
    st = os.stat(args.file_name)
    os.chmod(args.file_name, st.st_mode | stat.S_IEXEC)
    os.system("systemctl daemon-reload")
    os.system("service cocktailmaker start")

