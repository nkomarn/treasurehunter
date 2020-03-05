package net.urbanmc.treasurehunter.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ItemUtil {

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

		for (int i = 1; i < split.length; ++i) {
			String arg = split[i];

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
			else if (arg.startsWith("color:")) {
				handleColor(is, arg.substring(6));
			}
			// Log unknown argument
			Bukkit.getLogger().warning("[TreasureHunter] Unknown argument encountered: " + arg + " while parsing item " + name);
		}

		is.setItemMeta(meta);

		return is;
	}

	private static void handleColor(ItemStack is, String colorString) {
		ItemMeta meta = is.getItemMeta();
		if (is.getItemMeta() instanceof LeatherArmorMeta) {
			Color color;

			if (colorString.charAt(0) == '#')
				color = colorFromHex(colorString.substring(1));
			else
				color = fromColorName(colorString);

			((LeatherArmorMeta) meta).setColor(color);
			is.setItemMeta(meta);
		}
		else {
			Bukkit.getLogger().warning("[TreasureHunter] Cannot apply leather armor color to material type: "
					+ is.getType().name() + "!");
		}
	}

	private static Color colorFromHex(String hex) {
		try {
			return Color.fromRGB(
					Integer.valueOf(hex.substring(1, 3), 16),
					Integer.valueOf(hex.substring(3, 5), 16),
					Integer.valueOf(hex.substring(5, 7), 16)
			);
		} catch (NumberFormatException ex) {
			Bukkit.getLogger().warning("[TreasureHunter] Could not parse hexadecimal rgb value for " + hex + "!");
			return Color.WHITE;
		}
	}

	private static Color fromColorName(String colorName) {
		switch (colorName.toLowerCase()) {
			case "aqua":
				return Color.AQUA;
			case "fuchsia":
				return Color.FUCHSIA;
			case "black":
				return Color.BLACK;
			case "blue":
				return Color.BLUE;
			case "gray":
				return Color.GRAY;
			case "green":
				return Color.GREEN;
			case "lime":
				return Color.LIME;
			case "maroon":
				return Color.MAROON;
			case "navy":
				return Color.NAVY;
			case "olive":
				return Color.OLIVE;
			case "yellow":
				return Color.YELLOW;
			case "orange":
				return Color.ORANGE;
			case "purple":
				return Color.PURPLE;
			case "red":
				return Color.RED;
			case "silver":
				return Color.SILVER;
			case "teal":
				return Color.TEAL;
			case "white":
				return Color.WHITE;
		}

		Bukkit.getLogger().warning("[TreasureHunter] Error parsing leather armor color for color name: " + colorName);
		return Color.WHITE;
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

}
