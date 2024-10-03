package dev.drawethree.xprison.enchants.command;

import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import dev.drawethree.xprison.enchants.repo.EnchantsRepository;
import dev.drawethree.xprison.utils.player.PlayerUtils;
import me.lucko.helper.Commands;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GivePickaxeCommand {

	private final XPrisonEnchants plugin;

	public GivePickaxeCommand(XPrisonEnchants plugin) {
		this.plugin = plugin;
	}

	public void register() {
		Commands.create()
				.assertOp()
				.handler(c -> {

					if (c.args().size() == 0) {
						PlayerUtils.sendMessage(c.sender(), "&c/givepickaxe <player> [<enchant1>=<level1>,<enchant2>=<level2>,...<enchantX>=<levelX>] [pickaxe_name] [level] [blocks]");
						return;
					}

					Player target = null;
					String input = null;
					String name = null;
					int level = -1;
					int blocks = -1;

					for (int index = 0; index < c.args().size(); index++) {
						final String arg = c.args().get(index);
						if (index == 0) {
							target = Bukkit.getPlayer(arg);
							continue;
						}
						if (index == 1 && arg.contains("=")) {
							input = arg;
							continue;
						}
						if (name == null && index <= 2 && !isNumber(arg)) {
							name = arg;
							continue;
						}
						if (level < 0 && index <= 3 && isNumber(arg)) {
							level = Integer.parseInt(arg);
							continue;
						}
						if (blocks < 0 && index <= 4 && isNumber(arg)) {
							blocks = Integer.parseInt(arg);
							continue;
						}

						if (input == null && arg.contains("=")) {
							input = arg;
						} else if (name == null) {
							name = arg;
						}
					}

					Map<XPrisonEnchantment, Integer> enchants = parseEnchantsFromInput(input);

					this.plugin.getEnchantsManager().givePickaxe(target, enchants, name, c.sender(), level, blocks);
				}).registerAndBind(this.plugin.getCore(), "givepickaxe");
	}

	private boolean isNumber(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}


	private Map<XPrisonEnchantment, Integer> parseEnchantsFromInput(String input) {
		Map<XPrisonEnchantment, Integer> enchants = new HashMap<>();
		if (input == null) {
			return enchants;
		}

		String[] split = input.split(",");
		for (String s : split) {
			String[] enchantData = s.split("=");

			try {
				XPrisonEnchantment enchantment = getEnchantsRepository().getEnchantByName(enchantData[0]);
				if (enchantment == null) {
					enchantment = getEnchantsRepository().getEnchantById(Integer.parseInt(enchantData[0]));
				}

				if (enchantment == null) {
					continue;
				}

				int enchantLevel = Integer.parseInt(enchantData[1]);
				enchants.put(enchantment, enchantLevel);
			} catch (Exception ignored) {

			}
		}
		return enchants;
	}

	private EnchantsRepository getEnchantsRepository() {
		return this.plugin.getEnchantsRepository();
	}
}
