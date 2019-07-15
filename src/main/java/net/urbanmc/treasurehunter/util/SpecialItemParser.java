package net.urbanmc.treasurehunter.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialItemParser {

    private static int[] xpTotalToReachLevel;

    public static boolean isSpecialItem(String itemName, FileConfiguration data) {
        return data.contains("customitems." + itemName);
    }

    public static ItemStack handleSpecialItems(String[] args, FileConfiguration data) {
        //Args[0] = Name;

        //State custom item is loading
        Bukkit.getLogger().info("[TreasureHunter] Loading custom item " + args[0]);

        //Hashmap will split up arguments based on "key:value"
        HashMap<String, String> keyValue = new HashMap<>();

        //Define a length variable so args size isn't recounted.
        int argsLength = args.length;

        //Split key:value and place them into a hashmap.
        for (int i = 1; i < argsLength; i++) {
            //Check if it's a key-value string
            if (!args[i].contains(":")) continue;

            String[] insideSplit = args[i].split(":");
            if (insideSplit.length != 2) continue;

            keyValue.put(insideSplit[0], insideSplit[1]);
        }

        //Parse the item
        return parseItem(args[0], keyValue, data);
    }


    private static ItemStack parseItem(String customName, HashMap<String,String> keyValue, FileConfiguration data) {
        String quickPath = "customitems." + customName + ".";

        if (!data.contains(quickPath + "material")) {
            Bukkit.getLogger().warning("No material information for custom item: " + customName);
            return new ItemStack(Material.AIR);
        }

        String materialString = replaceString(data.getString(quickPath + "material"), keyValue);

        Material material;
        try {
            material = Material.valueOf(materialString.toUpperCase());
        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().warning("Invalid material (" + materialString.toUpperCase() +  ") for custom item: " + customName);
            return new ItemStack(Material.AIR);
        }

        String amountString = replaceString(data.getString(quickPath + "amount", ""+ 1),
                keyValue);

        int amount = 1;

        try {
            amount = Integer.valueOf(amountString);
        } catch (NumberFormatException ex) {
            Bukkit.getLogger().warning("Invalid amount for custom item: " + customName);
            amount = 1;
        }

        ItemStack stack = new ItemStack(material, amount);

        ItemMeta meta = stack.getItemMeta();

        if (data.contains(quickPath + "name")) {
            String displayName = replaceString(data.getString(quickPath + "name"),
                    keyValue);

            displayName = ChatColor.translateAlternateColorCodes('&', displayName);

            meta.setDisplayName(displayName);
        }

        if (data.contains(quickPath + "lore")) {
            List<String> lore = data.getStringList(quickPath + "lore");

            lore = lore.stream().map(loreValue ->
                    ChatColor.translateAlternateColorCodes('&',replaceString(loreValue, keyValue)))
                    .collect(Collectors.toList());

            meta.setLore(lore);
        }

        stack.setItemMeta(meta);

        return stack;
    }

    private static String replaceString(String toReplace, HashMap<String, String> keyValue) {
        for (String key : keyValue.keySet()) {
            toReplace = toReplace.replaceAll("%" + key + "%", keyValue.get(key));
        }

        return toReplace;
    }

    /*

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

        MessageFormat.format("Hello {0}", "hi")
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
    } */

}
