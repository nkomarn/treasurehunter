package net.urbanmc.treasurehunter.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ItemUtil {

	private static ItemStack getItem(String name, FileConfiguration data) {
		String[] split = name.split(" ");

		if(SpecialItemParser.isSpecialItem(split[0], data))
			return SpecialItemParser.handleSpecialItems(split, data);

		Material mat = Material.getMaterial(split[0].toUpperCase());

		if (mat == null) {
			Bukkit.getLogger().log(Level.SEVERE, "[TreasureHunter] Error loading material for " + name);
			return null;
		}

		ItemStack is = new ItemStack(mat);

		ItemMeta meta = is.getItemMeta();

		for (String arg : split) {
			if (arg.startsWith("name:")) {
				String displayName = ChatColor.translateAlternateColorCodes('&', arg.substring(5).replace("_", " "));
				meta.setDisplayName(ChatColor.RESET + displayName);
			}
			else if (arg.startsWith("lore:")) {
				String lore = ChatColor.translateAlternateColorCodes('&', arg.substring(5).replace("_", " "));

				List<String> loreList;

				if (!meta.hasLore()) {
					loreList = new ArrayList<>();
				} else {
					loreList = meta.getLore();
				}

				loreList.add(lore);

				meta.setLore(loreList);
			}
			else if (arg.startsWith("amount:")) {
				int amount = Integer.parseInt(arg.substring(7));
				is.setAmount(amount);
			}
			else if (arg.startsWith("enchant:")) {
				String enchant = arg.substring(8);
				String[] enchantSplit = enchant.split("/");

				Enchantment ench = getEnchantment(enchantSplit[0]);

				if (ench == null) {
					Bukkit.getLogger().log(Level.SEVERE, "[TreasureHunter] Error loading enchant " + enchantSplit[0] + " for " + name);
					continue;
				}

				int level = enchantSplit.length == 1 ? 1 : Integer.parseInt(enchantSplit[1]);

				meta.addEnchant(ench, level, true);
			}
			else if (arg.startsWith("book:")) {
				String enchant = arg.substring(5);
				String[] enchantSplit = enchant.split("/");

				if (!(meta instanceof EnchantmentStorageMeta))
					continue;

				EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) meta;

				Enchantment ench = getEnchantment(enchantSplit[0]);

				if (ench == null) {
					Bukkit.getLogger().warning("Cannot parse enchantment book " + enchantSplit[0] + " for item " + mat.name());
					continue;
				}

				int level = enchantSplit.length == 1 ? 1 : Integer.parseInt(enchantSplit[1]);

				bookMeta.addStoredEnchant(ench, level, true);
			}
			else if (arg.startsWith("effect:")) {
				String effect = arg.substring(7);
				String[] effectSplit = effect.split("/");

				PotionEffectType effectType = PotionEffectType.getByName(effectSplit[0].toUpperCase());

				if (effectType == null) {
					Bukkit.getLogger().warning("Cannot parse potion effect " + effectSplit[0] + " for item " + mat.name());
					continue;
				}

				int level = effectSplit.length < 2 ? 1 : Integer.parseInt(effectSplit[1]);
				int duration = effectSplit.length < 3 ? 1 : Integer.parseInt(effectSplit[2]);

				PotionMeta potionMeta = (PotionMeta) meta;

				potionMeta.addCustomEffect(new PotionEffect(effectType, duration, level), true);
			}
			else if (arg.startsWith("durability:")) {
				short durability = Short.parseShort(arg.substring(11));
				is.setDurability(durability);
			}
			else {
				// Log unknown argument
				Bukkit.getLogger().warning("[TreasureHunter] Unknown argument encountered: " + arg + " while parsing item " + name);
			}
		}

		is.setItemMeta(meta);

		return is;
	}

	private static Enchantment getEnchantment(String enchantmentName) {
		enchantmentName = convertEnchants(enchantmentName.toLowerCase());

		Enchantment ench = Enchantment.getByName(enchantmentName.toUpperCase());

		if (ench == null)
			ench = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentName.toLowerCase()));

		return ench;
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


	public static List<ItemStack> getItemList(List<String> list, FileConfiguration data) {
		List<ItemStack> items = new ArrayList<>();

		for (String s : list) {
			ItemStack stack = null;

			try {
				stack = getItem(s, data);
			} catch (Exception e) {
				Bukkit.getLogger().warning("[TreasureHunter] Exception thrown while parsing item line " + s);
				e.printStackTrace();
			}

			if (stack != null)
				items.add(stack);
		}

		return items;
	}

}
