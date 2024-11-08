package dev.drawethree.xprison.enchants.model.impl;

import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import dev.drawethree.xprison.utils.player.PlayerUtils;
import me.lucko.helper.Schedulers;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class LuckEnchant extends XPrisonEnchantment {

    private static final Map<UUID, Double> LUCKY_PLAYERS = new HashMap<>();

    private double chance;
    private String onActivateMessage;
    private String onDeactivateMessage;
    private boolean disableMessages;
    private double multiplier;
    private long time;
    private TimeUnit unit;

    public LuckEnchant(XPrisonEnchants instance) {
        super(instance, 24);
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Chance");
        this.onActivateMessage = plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".OnActivate");
        this.onDeactivateMessage = plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".OnDeactivate");
        this.disableMessages = plugin.getEnchantsConfig().getYamlConfig().getBoolean("enchants." + id + ".DisableMessages");
        this.multiplier = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Multiplier", 1.0);
        final String[] split = plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".Time", "5 MINUTES").split(" ", 2);
        this.time = Long.parseLong(split[0].trim());
        this.unit = TimeUnit.valueOf(split[1].toUpperCase().trim());
    }

    public static double getMultiplier(Player p) {
        return LUCKY_PLAYERS.getOrDefault(p.getUniqueId(), 1.0);
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {
        if (LUCKY_PLAYERS.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }

        double chance = getChanceToTrigger(enchantLevel);

        if (chance < ThreadLocalRandom.current().nextDouble(100)) {
            return;
        }

        LUCKY_PLAYERS.put(e.getPlayer().getUniqueId(), this.multiplier);
        if (!this.disableMessages) {
            PlayerUtils.sendMessage(e.getPlayer(), this.onActivateMessage);
        }

        Schedulers.sync().runLater(() -> {
            LUCKY_PLAYERS.remove(e.getPlayer().getUniqueId());
            if (e.getPlayer().isOnline() && !this.disableMessages) {
                PlayerUtils.sendMessage(e.getPlayer(), this.onDeactivateMessage);
            }
        }, time, unit);
    }

    @Override
    public double getChanceToTrigger(int enchantLevel) {
        return chance * enchantLevel;
    }

    @Override
    public void reload() {
        super.reload();
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Chance");
        this.onActivateMessage = plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".OnActivate");
        this.onDeactivateMessage = plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".OnDeactivate");
        this.disableMessages = plugin.getEnchantsConfig().getYamlConfig().getBoolean("enchants." + id + ".DisableMessages");
        this.multiplier = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Multiplier", 1.0);
        final String[] split = plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".Time", "5 MINUTES").split(" ", 2);
        this.time = Long.parseLong(split[0].trim());
        this.unit = TimeUnit.valueOf(split[1].toUpperCase().trim());
        LUCKY_PLAYERS.clear();
    }

    @Override
    public String getAuthor() {
        return "blithe_kitsune";
    }
    
}
