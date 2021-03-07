# TreasureHunter
A bukkit plugin that spawns pre-defined tiered chests with configurable loot at hourly intervals around a world. 

## Requires Timer Lib 
This plugin requires another custom plugin called `TimerLib`. `TimerLib` is an API plugin that provides events at precise intervals: every 5 minutes, hourly, daily. `TimerLib` also includes [Aikar's TaskChain](https://github.com/aikar/TaskChain) which other plugins can utilize. `TreasureHunter` requires both features to work.

## Chest Types
There are four chest types: common, rare, epic, and legendary. 

### Spawn Ranges
The spawn ranges for each chest can be editted in `spawns.yml`. 

### Items
The items within each chest can be configured in `items.yml`.

## Installing
The plugin can be installed via the releases page.

## Building
This is a standard maven project and can be built via `mvn clean package`. 

## Archived
This project is archived and will not recieve any features. Bugs may still be resolved, but there is no gurantee they will be resolved in a timely fashion. Pull-requests related to features / bugs are welcome.

# Developers
Elian, Silverwolfg11
