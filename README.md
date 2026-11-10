# Amethyst Launcher

A lightweight Android TV home-screen prototype for Xiaomi TV A Pro.

## Included
- Android HOME / LEANBACK launcher activity
- D-pad focusable app tiles
- App discovery through Leanback and launcher intents
- Quick Settings and Wi-Fi Settings shortcuts
- Date/time and network connectivity indicator

## Build without a computer
1. Upload this project to a GitHub repository using Safari (GitHub website).
2. Open the repository's **Actions** tab.
3. Select **Build Amethyst Launcher** and tap **Run workflow**.
4. Wait for the workflow to finish.
5. Open the successful run, download `Amethyst-Launcher-debug`, and extract it.
6. On the TV, enable installation from unknown sources for the file manager / downloader you use.
7. Transfer the APK to the TV (USB drive, local network transfer, or a trusted downloader) and install it.

The APK is unsigned beyond Android's standard debug signing. It is suitable for testing, not Play Store distribution.

## Make it the home screen
Install and open the app once. Press Home on the remote. If Android shows a home-app chooser, select Amethyst Launcher and choose Always. If the TV firmware does not show a chooser, use Android's Home app settings if available.

## Safety
Do not disable or uninstall the stock Xiaomi / Google launcher until Amethyst has been tested and you have confirmed how to restore the stock launcher. This is an early prototype; app visibility and Settings shortcuts can vary by firmware.
