package net.urbanmc.treasurehunter.util;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpecialItemParser {

    private static int[] xpTotalToReachLevel;

    public static boolean isSpecialItem(String itemName) {
        if (Arrays.asList(
                "bank_note",
                "xpbottle",
                "mcmmo_voucher",
                "colorbook"
        ).contains(itemName.toLowerCase())) return true;

        return false;
    }

    public static ItemStack handleSpecialItems(String[] args) {
        //Args[0] = Name; Args[1] = Amount
        switch(args[0]) {
            case "bank_note":
                return generateBankNote(args[1]);

            case "xpbottle":
                return generateXPBottle(args[1],args[2]);

            case "mcmmo_voucher":
                return generateMCMMOVoucher(args[1]);

            case "colorbook":
                return generateColorBook();

        }
        return null;
    }


    private static ItemStack generateBankNote(String amount) {
        ItemStack item = new ItemStack(Material.PAPER);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Bank Note");

        List lore = new ArrayList();

        double value = Double.valueOf(amount.substring(7));

        lore.add(ChatColor.LIGHT_PURPLE + "Signer " + ChatColor.WHITE + "Server");
        lore.add(ChatColor.LIGHT_PURPLE + "Value " + ChatColor.WHITE + "$" + value);

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }


    private static ItemStack generateMCMMOVoucher(String credits) {
        ItemStack item = new ItemStack(Material.PAPER);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "MCMMO Voucher");

        List lore = new ArrayList();

        int creds = Integer.valueOf(credits.substring(8));
        lore.add(ChatColor.LIGHT_PURPLE + "Credits: " + ChatColor.WHITE + creds);

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack generateColorBook() {
        ItemStack cbook = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = cbook.getItemMeta();
        meta.setDisplayName(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', "&4C&6o&el&2o&1r &8Book"));
        List l = new ArrayList();

        l.add("Do /colorname (name)");
        l.add("to give the book a colored name!");

        meta.setLore(l);

        cbook.setItemMeta(meta);

        return cbook;
    }


    private static ItemStack generateXPBottle(String level, String amount) {
        ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE, Integer.valueOf(amount.substring(7)));

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(new StringBuilder().append(ChatColor.AQUA).append("").append(ChatColor.BOLD).append("XP Bottle").toString());

        List lore = new ArrayList();

        level = level.substring(6);

        lore.add(new StringBuilder().append(ChatColor.LIGHT_PURPLE).append("Signer ").append(ChatColor.WHITE).append("Server").toString());
        lore.add(new StringBuilder().append(ChatColor.LIGHT_PURPLE).append("XP ").append(ChatColor.WHITE).append(getXpForLevel(Integer.valueOf(level))).toString());
        lore.add(new StringBuilder().append(ChatColor.LIGHT_PURPLE).append("Level ").append(ChatColor.WHITE).append("0 -> ").append(level).toString());

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    private static int getXpForLevel(int level)
    {
        Validate.isTrue((level >= 0) && (level <= 100000), "Invalid level " + level + "(must be in range 0.." + 100000 + ")");

        if(xpTotalToReachLevel == null) {
            initLookupTables(level * 2);
        }

        if (level >= xpTotalToReachLevel.length) {
            initLookupTables(level * 2);
        }
        return xpTotalToReachLevel[level];
    }

    private static void initLookupTables(int maxLevel)
    {
        xpTotalToReachLevel = new int[maxLevel];

        for (int i = 0; i < xpTotalToReachLevel.length; i++)
            xpTotalToReachLevel[i] = (i >= 16 ? (int)(2.5D * i * i - 40.5D * i + 360.0D) : i >= 32 ? (int)(4.5D * i * i - 162.5D * i + 2220.0D) : i * i + 6 * i);
    }

}
