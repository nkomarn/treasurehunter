package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import net.urbanmc.treasurehunter.runnable.SpawnTask;
import org.bukkit.command.CommandSender;

public class SpawnSub extends SubCommand{

    private TreasureHunter plugin;

    public SpawnSub(TreasureHunter plugin) {
        super("spawn", Permission.SPAWN_SUB, false, false);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(TreasureChestManager.getInstance().getCurrentChest() != null) {
            sendPropMessage(sender, "command.spawn.already-spawned");
            return;
        }

        SpawnTask.getInstance().cancel();
        SpawnTask.getInstance().runTask(plugin);

        //Todo Basically this command is for admins to force spawn a treasure chest. I guess you could parse some arguments about chest type but mainly just force spawning a chest.


    }
}
