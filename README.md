# CaveSaveEdit Pro 5.0
Save files editor for Cave Story and Cave Story+

Improved and refactored fork of CaveSaveEdit by [@Leo40Story](https://github.com/Leo40Git)

## What's new in Pro Version
- completely refactored
- config is stored in a local `prefs.cfg` file
- new appearance
- new Cave Story+ tab
- supports game resources from various game editions and clones
- Cave Story to Cave Story+ and vice versa converter 
- bug fixes

# How to Install

There are two ways to install the application.

1. Download installer for your platform
    - Windows: [CaveSaveEditPro-v5.0.0-Installer-win.exe](https://github.com/msalv/CaveSaveEditPro/releases/download/v5.0.0/CaveSaveEditPro-v5.0.0-Installer-win.exe)
    - macOS: [CaveSaveEditPro-v5.0.0-mac.dmg](https://github.com/msalv/CaveSaveEditPro/releases/download/v5.0.0/CaveSaveEditPro-v5.0.0-mac.dmg)

2. Download JAR-file for any platfrom
    - Make sure that [Java](https://www.java.com/en/download/) is installed using command line: `java --version`
    - Download JAR-file: [CaveSaveEdit.jar](https://github.com/msalv/CaveSaveEditPro/releases/download/v5.0.0/CaveSaveEdit.jar)
    - Run `java -jar CaveSaveEdit.jar` to launch the application

# How to Compile

1. Run `./gradlew jar` to build CaveSaveEdit.jar file.
2. CaveSaveEdit.jar would compile to `./build/libs` directory.
3. Run `java -jar CaveSaveEdit.jar` to launch the application.

# How to Use

### 1. Open Profile.dat
Save game file is usually located in the same directory as your Cave Story executable file.   
It is called ‘Profile.dat’

If you have a Steam version of Cave Story+ installed then open Steam and go to your Library.<br/>
Right-click the game and select ‘Properties’ from the context menu.<br/>
In the ‘Properties’ window, go to the ‘Local Files’ tab and click ‘Browse Local Files’.

To open a Profile.dat go to *File _→_ Open...* menu.

### 2. Load Game Resources (optional)
Also you can load resources in order to see game sprites and maps.

Select one of these files according to your version of the game:
- Cave Story (freeware) — `Doukutsu.exe`
- Cave Story+ — `stage.tbl`
- NXEngine/NXEngine-evo — `stage.dat`
- Doukutsu-rs/CSE2E — `mrmap.bin`

One of these files should be located in the game folder or in the ‘data’ subfolder.

You can load them via *File _→_ Load Game Resources...* menu.

# Troubleshooting

If you’re having issues with the game text (e. g. map names), you can try to change its encoding in the *Settings*.

# Notes
This editor only works with vanilla profiles, meaning that any mod with custom profiles will **not** work.

# Credits

App icon designed by [Pancakes](https://vk.com/pancakes_art)
<br/>UI icons by [Gofox](https://www.flaticon.com/authors/gofox)
<br/>Arcadepix font by [Reekee of Dimenzioned](https://www.dafont.com/reekee-of-dimenzioned.d1065)
<br/>NDS BIOS font by [Aaron D. Chand](https://www.dafont.com/aaron-d-chand.d6569)
<br/>Based on Kapow's profile [specs](https://www.cavestory.org/guides/profile.txt)

Honorable Mentions of the Original Version by [@Leo40Git](https://github.com/Leo40Git):  
[Noxid](https://www.cavestory.org/forums/members/noxid.863) [@taedixon](https://github.com/taedixon), [gamemanj](https://www.cavestory.org/forums/members/gamemanj.7022) [@20kdc](https://github.com/20kdc), [zxin](https://www.cavestory.org/forums/members/zxin.7232) [@zxinmine](https://github.com/zxinmine), [Carrotlord](https://www.cavestory.org/forums/members/carrotlord.1111)
