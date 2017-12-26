package net.urbanmc.treasurehunter.util;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {
	private static int[] xpTotalToReachLevel;

	private static ItemStack getItem(String name) {
		String[] split = name.split(" ");

		if(split[0].equalsIgnoreCase("bank_note") ||
				split[0].equalsIgnoreCase("xpbottle") ||
				split[0].equalsIgnoreCase("mcmmo_voucher"))
			return handleSpecialItems(split[0],split[1],split[2]);


		ItemStack is = new ItemStack(Material.getMaterial(split[0].toUpperCase()));

		ItemMeta meta = is.getItemMeta();

		for (String arg : split) {
			if (arg.startsWith("name:")) {
				String displayName = ChatColor.translateAlternateColorCodes('&', arg.substring(5).replace("_", " "));
				meta.setDisplayName(ChatColor.RESET + displayName);

				continue;
			}

			if (arg.startsWith("lore:")) {
				String lore = ChatColor.translateAlternateColorCodes('&', arg.substring(5).replace("_", " "));

				List<String> loreList;

				if (meta.getLore() == null) {
					loreList = new ArrayList<>();
				} else {
					loreList = meta.getLore();
				}

				loreList.add(lore);

				meta.setLore(loreList);

				continue;
			}

			if (arg.startsWith("amount:")) {
				int amount = Integer.parseInt(arg.substring(7));
				is.setAmount(amount);

				continue;
			}

			if (arg.startsWith("enchant:")) {
				String enchant = arg.substring(8);
				String[] enchantSplit = enchant.split("/");

				enchantSplit[0] = convertEnchants(enchantSplit[0]);

				Enchantment ench = Enchantment.getByName(enchantSplit[0].toUpperCase());
				int level = enchantSplit.length == 1 ? 1 : Integer.parseInt(enchantSplit[1]);

				meta.addEnchant(ench, level, true);

				continue;
			}

			if (arg.startsWith("book:")) {
				String enchant = arg.substring(5);
				String[] enchantSplit = enchant.split("/");

				if (!(meta instanceof EnchantmentStorageMeta))
					continue;

				EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) meta;

				enchantSplit[0] = convertEnchants(enchantSplit[0]);

				Enchantment ench = Enchantment.getByName(enchantSplit[0].toUpperCase());
				int level = enchantSplit.length == 1 ? 1 : Integer.parseInt(enchantSplit[1]);

				bookMeta.addStoredEnchant(ench, level, true);
				
				continue;
			}

			if (arg.startsWith("effect:")) {
				String effect = arg.substring(7);
				String[] effectSplit = effect.split("/");

				PotionEffectType effectType = PotionEffectType.getByName(effectSplit[0].toUpperCase());

				int level = effectSplit.length < 2 ? 1 : Integer.parseInt(effectSplit[1]);
				int duration = effectSplit.length < 3 ? 1 : Integer.parseInt(effectSplit[2]);

				PotionMeta potionMeta = (PotionMeta) meta;

				potionMeta.addCustomEffect(new PotionEffect(effectType, duration, level), true);
				continue;
			}

			if (arg.startsWith("durability:")) {
				short durability = Short.parseShort(arg.substring(11));
				is.setDurability(durability);
			}
		}

		is.setItemMeta(meta);

		return is;
	}

	private static String convertEnchants(String s) {
		switch (s) {
			case "sharpness":
				return "damage_all";
			case "infinity":
				return "arrow_infinite";
			case "power":
				return "arrow_damage";
			case "punch":
				return "arrow_knockback";
			case "looting":
				return "loot_bonus_mobs";
			case "protection":
				return "protection_environmental";
		}
		return s;
	}

	private static ItemStack handleSpecialItems(String item, String... args) {
		switch(item) {
			case "bank_note":
				return generateBankNote(args[0]);

			case "xpbottle":
				return generateXPBottle(args[0],args[1]);

			case "mcmmo_voucher":
				return generateMCMMOVoucher(args[0]);
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


	private static ItemStack generateXPBottle(String level, String amount) {
		ItemStack item = new ItemStack(Material.EXP_BOTTLE, Integer.getInteger(amount.substring(7)));

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(new StringBuilder().append(ChatColor.AQUA).append("").append(ChatColor.BOLD).append("XP Bottle").toString());

		List lore = new ArrayList();

		level = level.substring(6);

		lore.add(new StringBuilder().append(ChatColor.LIGHT_PURPLE).append("Signer ").append(ChatColor.WHITE).append("Server").toString());
		lore.add(new StringBuilder().append(ChatColor.LIGHT_PURPLE).append("XP ").append(ChatColor.WHITE).append(getXpForLevel(Integer.getInteger(level))).toString());
		lore.add(new StringBuilder().append(ChatColor.LIGHT_PURPLE).append("Level ").append(ChatColor.WHITE).append("0 -> ").append(level).toString());

		meta.setLore(lore);

		item.setItemMeta(meta);

		return item;
	}

	private static int getXpForLevel(int level)
	{
		Validate.isTrue((level >= 0) && (level <= 100000), "Invalid level " + level + "(must be in range 0.." + 100000 + ")");

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

	public static List<ItemStack> getItemList(List<String> list) {
		List<ItemStack> items = new ArrayList<>();

		for (String s : list) {
			items.add(getItem(s));
		}

		return items;
	}
}
