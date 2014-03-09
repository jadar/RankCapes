RankCapes
===============

RankCapes is a Bukkit plugin and Minecraft client mod combo that allows server owners to give their players custom capes based on permissions. RankCapes is easy to setup and use. It supports regular, animated, and HD capes! 

##Branches
The code on this repository is sorted by branches. The different version of the mods have their own braches, master and bleeding.

- Master - stable, error free (to the author's knowledge) code.
- Bleeding - latest, bleeding edge code which is unstable and may contain errors.

The master branch of the repository contains subtrees of the master code branches.

##Platforms
The RankCapes project has 2 parts. Client and server.

**Server**  
[Bukkit](../../tree/bukkit) - [bleeding](../../tree/bukkit-bleeding)  

**Client**  
[Forge](../../tree/forge) - [bleeding](../../tree/forge-bleeding)

##Usage
For full usage and setup, see [the wiki](../../wiki).

##Compilation
To compile the **plugin**.

1. Open a command line window in the root of the repository
2. Run `./gradlew build`.

To compile the **mod**.

1. Install Gradle
2. Run `./gradlew setupCIWorkspace build`
