- [中文](README.md)
## Description
This is a simple client-side mod that allows you to kiss entities. (Spawning heart particles and playing sound effects)

The trigger condition is to hold down the sneak key and right-click on the entity. Pressing the key can also trigger the effect (default **F7**)
![kiss the player](https://cdn.modrinth.com/data/cached_images/ddbde5d1bdc6d5772e3338dcabd0d0286a290ce0.png)
## Server-side Support
When the server also installs this mod or plugin, the players who installed the mod can see each other's effects.
## Commands
```/kissmod-rightclick```  Toggles whether to enable sneak right-click triggering.

```/kissmod-rightclick true```  Enable

```/kissmod-rightclick false```  Disable

```/kissmod-proxlib```  Toggles whether to enable proximity communication.

```/kissmod-proxlib true```  Enable

```/kissmod-proxlib false```  Disable
## Configuration
When [Mod Menu](https://modrinth.com/mod/modmenu) and [Cloth Config API](https://modrinth.com/mod/cloth-config) are installed, the configuration screen can be accessed via the mod menu to modify:
### General Settings
- Whether to trigger with sneak right-click;
- Whether to show kisses to others;
- Whether to see kisses from others;
- Cooldown between triggers when holding down;
- Whether to enable detailed logging;
- Whether to enable proximity communication **(when enabled, nearby players can see your kisses without requiring the mod or plugin to be installed on the server);**
  > **Warning: May be mistaken for anomalies by anti-cheat systems, risk of being kicked or banned, the author is not responsible for this.**
- Whether the list is a whitelist;
- List of server addresses for proximity communication.
### Sound Settings
- Whether to play sound effects;
- Sound volume;
- Sound pitch.
### Particle Settings
- The number of heart particles;
- Particle spawn center XYZ offset;
- Maximum random XYZ offset for particle spawning.
## More supported versions (based on version 0.5 KissMod)
Here is a transplant mod https://github.com/Xiaoyu-2009/kiss-mod-transplant

Unofficial https://modrinth.com/mod/kiss-mod-unofficial
## Open source repository link for the plugin
https://github.com/chicken-awa/kiss-mod-Plugin
