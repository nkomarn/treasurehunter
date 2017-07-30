package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import net.urbanmc.treasurehunter.object.TreasureChest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class StartSub extends SubCommand{

    private List<UUID> warned = new ArrayList<>();

    public static ItemStack compass;

    private TreasureHunter plugin;

    public StartSub(TreasureHunter plugin) {
        super("start", Permission.START_SUB, true);

        this.plugin = plugin;

        createCompass();
    }

    private void createCompass() {
        compass = new ItemStack(Material.COMPASS, 1);

        ItemMeta meta = compass.getItemMeta();

        meta.setDisplayName(Messages.getString("compass.name"));
        meta.setLore(Collections.singletonList(Messages.getString("compass.desc")));

        compass.setItemMeta(meta);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(TreasureChestManager.getInstance().getCurrentChest() == null) {
            sendPropMessage(sender,"command.no-chest");
            return;
        }

        TreasureChest chest = TreasureChestManager.getInstance().getCurrentChest();
        Player p = (Player) sender;

        if(chest.getCancelled().contains(p.getUniqueId())) {
            sendPropMessage(sender, "command.start.cancelled");
            return;
        }

        if(chest.getHunting().contains(p.getUniqueId())) {

            if(p.getInventory().contains(compass)) {
                sendPropMessage(sender, "command.start.hunting");
                return;
            }

            p.getInventory().addItem(compass.clone());

            return;
        }

        if(!warned.contains(p.getUniqueId())) {
            p.sendMessage(Messages.getString("command.start.warning", buildWarnMessage()));

            warned.add(p.getUniqueId());

            timeOut(p.getUniqueId());
            return;
        }

        warned.remove(p.getUniqueId());

        chest.getHunting().add(p.getUniqueId());

        sendPropMessage(sender, "command.start.start-hunt");

        p.getInventory().addItem(compass.clone());

        if(ConfigManager.getConfig().getBoolean("disable-fly"))
        p.setFlying(false);

        if(ConfigManager.getConfig().getBoolean("disable-god"))
        TreasureHunter.getEssentials().getUser(p).setGodModeEnabled(false);
    }

    private void timeOut(UUID p) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(warned.contains(p))
                warned.remove(p);
        }, 500);
    }

    private String buildWarnMessage() {
        StringBuilder blocked = new StringBuilder();

        if(ConfigManager.getConfig().getBoolean("disable-fly"))
            blocked.append("\n").append("- /fly");

        if(ConfigManager.getConfig().getBoolean("disable-god"))
            blocked.append("\n").append("- /god");

        if(!ConfigManager.getConfig().getStringList("blocked-commands").isEmpty())
            for(String command : ConfigManager.getConfig().getStringList("blocked-commands"))
            blocked.append("\n").append("- /").append(command);

        return blocked.toString();
    }
}
