# What is MCI?
The MCI system, or the **M**od **C**ompatibility **I**nformation system (originally just called the "defines" system) is the system this save editor uses for mod compatibility.
It allows to redefine game related values that may vary from one edition to another (i.e. map names in CS and CS+).

# Current Status
Deprecated. In the original CaveSaveEdit it was meant to be a scripting extension to the app,
but now it serves only as a static configuration system.

# How does MCI work?
The MCI system uses JSON to define and declare values.
An example for an MCI configuration file can be found [here](src/main/resources/default.json).
Every MCI file should contain the following fields. 
*Note: CaveSaveEdit Pro always uses double resolution images, take this in consideration when dealing with positions and sizes!*

## Metadata
- `name:String` - MCI file's title
- `author:String` - MCI file's author(s)

## Game information
- `exeName:String` - Game's executable name
- `armsImageYStart:Number` - Starting Y position for weapon icons in ArmsImage
- `armsImageSize:Number` - Size of weapon icons in ArmsImage
- `fps:Number` - FPS. Used for calculating the "Seconds Played" field
- `graphicsResolution:Number` - Game's graphics density
- `profileClass:String` - Profile class name to use to load profiles. Valid values are:
`com.leo.cse.backend.profile.model.NormalProfile` - For vanilla CS profiles,
`com.leo.cse.backend.profile.model.PlusProfile` - For Cave Story+ profiles.
- `saveEvent:Number` - The event used for saving the profile. Used for the Save Points dialog

## Information arrays
To add an empty space to any of these arrays, add a `null`.
- `specials:Array<String>` - Deprecated. A list of special support features to enable. Valid values are:  
`MimHack` for the <MIM hack,  
`VarHack` for the <VAR hack (cannot be enabled with <MIM or <BUY),  
`PhysVarHack` for the <PHY hack (depends on <VAR),  
`BuyHack` for the <BUY hack.
If no special support is required, `null` can also be returned.
*Note: these special features are deprecated. They may not work properly*
- `mapNames:Array<String>` - A list of map names. Used when an executable isn't loaded
- `songNames:Array<String>` - A list of song names
- `equipNames:Array<String>` - A list of equip names
- `weaponNames:Array<String>` - A list of weapon names
- `itemNames:Array<String>` - A list of item names
- `warpNames:Array<String>` - A list of warp names
- `warpLocNames:Array<String>` - A list of warp location names
- `flagDescriptions:Array<String>` - A list of flag descriptions
- `challengeNames:Array<String>` - A list of Cave Story+ challenge names
- `saveFlagID:Number` - The ID of the "game was saved" flag. This flag ID will not be modifiable

## Entity extras
- `entityFrames:Object` - Key-value data structure that maps entity type to it's frame rectangle. `x` and `y` are the top-left corner's position, and `width` and `height` are the bottom-right corner's position.
- `entityOffsets:Object` - Key-value data structure that maps entity type to it's position offset in pixels
