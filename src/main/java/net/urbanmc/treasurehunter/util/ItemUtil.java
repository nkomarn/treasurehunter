package net.urbanmc.treasurehunter.util;

import net.urbanmc.treasurehunter.TreasureHunter;
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
import java.util.logging.Logger;

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
		final Logger logger = TreasureHunter.getInstance().getLogger();

		String[] split = name.split(" ");

		if(SpecialItemParser.isSpecialItem(split[0], data))
			return SpecialItemParser.handleSpecialItems(split, data);

		Material mat = Material.getMaterial(split[0].toUpperCase());

		if (mat == null) {
			logger.severe("Error loading material for " + name);
			return null;
		}

		ItemStack is = new ItemStack(mat);

		ItemMeta meta = is.getItemMeta();

		for (int i = 1; i < split.length; ++i) {
			String fullArgument = split[i];
			// Get split property
			int splitCharIndex = fullArgument.indexOf(':');

			if (splitCharIndex == -1 || splitCharIndex == (fullArgument.length() - 1)) {
				// Log unknown argument
				logger.warning("Unknown argument encountered: \"" + fullArgument + "\" while parsing item " + name);
				continue;
			}

			String property = fullArgument.substring(0, splitCharIndex);
			String value = fullArgument.substring(splitCharIndex + 1);

			switch (property.toLowerCase()) {
				case "name":
					String displayName = ChatColor.translateAlternateColorCodes('&', value.replace("_", " "));
					meta.setDisplayName(ChatColor.RESET + displayName);
					break;

				case "lore":
					String lore = ChatColor.translateAlternateColorCodes('&', value.replace("_", " "));

					List<String> loreList;

					if (!meta.hasLore()) {
						loreList = new ArrayList<>();
					} else {
						loreList = meta.getLore();
					}

					loreList.add(lore);

					meta.setLore(loreList);
					break;

				case "amount":
					int amount = Integer.parseInt(value);
					is.setAmount(amount);
					break;

				case"enchant": {
					String[] enchantSplit = value.split("/");

					Enchantment ench = getEnchantment(enchantSplit[0]);

					if (ench == null) {
						logger.severe("Error loading enchant \"" + enchantSplit[0] + "\" for " + name);
						continue;
					}

					int level = enchantSplit.length == 1 ? 1 : Integer.parseInt(enchantSplit[1]);

					meta.addEnchant(ench, level, true);
					break;
				}

				case "book": {
					String[] enchantSplit = value.split("/");

					if (!(meta instanceof EnchantmentStorageMeta))
						continue;

					EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) meta;

					Enchantment ench = getEnchantment(enchantSplit[0]);

					if (ench == null) {
						logger.warning("Cannot parse enchantment book \"" + enchantSplit[0] + "\" for item " + mat.name());
						continue;
					}

					int level = enchantSplit.length == 1 ? 1 : Integer.parseInt(enchantSplit[1]);

					bookMeta.addStoredEnchant(ench, level, true);
					break;
				}

				case "effect":
					String[] effectSplit = value.split("/");

					PotionEffectType effectType = PotionEffectType.getByName(effectSplit[0].toUpperCase());

					if (effectType == null) {
						logger.warning("Cannot parse potion effect \"" + effectSplit[0] + "\" for item " + mat.name());
						continue;
					}

					int level = effectSplit.length < 2 ? 1 : Integer.parseInt(effectSplit[1]);
					int duration = effectSplit.length < 3 ? 1 : Integer.parseInt(effectSplit[2]);

					PotionMeta potionMeta = (PotionMeta) meta;

					potionMeta.addCustomEffect(new PotionEffect(effectType, duration, level), true);
					break;

				case "durability":
					short durability = Short.parseShort(value);
					is.setDurability(durability);
					break;

				case "color":
					handleColor(meta, value);
					break;
				default:
					// Log unknown argument
					// Skip empty properties
					if (!property.isEmpty())
						logger.warning("Unknown property encountered: \"" + property + "\" while parsing item " + name);
					break;
			}
		}

		is.setItemMeta(meta);

		return is;
	}

	private static void handleColor(ItemMeta meta, String colorString) {
		if (meta instanceof LeatherArmorMeta) {
			Color color;

			if (colorString.charAt(0) == '#')
				color = colorFromHex(colorString.substring(1));
			else
				color = fromColorName(colorString);

			((LeatherArmorMeta) meta).setColor(color);
		}
		else {
			TreasureHunter.getInstance().getLogger().warning("Cannot apply leather armor color to material type!");
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
			TreasureHunter.getInstance().getLogger().warning("Could not parse hexadecimal rgb value for " + hex + "!");
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

		TreasureHunter.getInstance().getLogger().warning("Error parsing leather armor color for color name: " + colorName);
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
