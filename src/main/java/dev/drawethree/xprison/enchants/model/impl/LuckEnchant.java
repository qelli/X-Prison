package dev.drawethree.xprison.enchants.model.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import dev.drawethree.xprison.utils.player.PlayerUtils;
import me.lucko.helper.Schedulers;

public class LuckEnchant extends XPrisonEnchantment {

    private static double MULTIPLIER;
    private static final Set<UUID> LUCKY_PLAYERS = new HashSet<>();
    private double chance;
    private String onActivateMessage;
    private String onDeactivateMessage;
    private boolean disableMessages;

    public LuckEnchant(XPrisonEnchants instance) {
        super(instance, 24);
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants."+id+".Chance");
        this.onActivateMessage = plugin.getEnchantsConfig().getYamlConfig().getString("enchants."+id+".OnActivate");
        this.onDeactivateMessage = plugin.getEnchantsConfig().getYamlConfig().getString("enchants."+id+".OnDeactivate");
        this.disableMessages = plugin.getEnchantsConfig().getYamlConfig().getBoolean("enchants."+id+".DisableMessages");
        MULTIPLIER = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants."+id+".Multiplier");
    }

    public static boolean isPlayerLucky(Player p) {
        return LUCKY_PLAYERS.contains(p.getUniqueId());
    }

    public static double getMultiplier() {
        return MULTIPLIER;
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {}

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {}

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {
        if (isPlayerLucky(e.getPlayer())) {
            return;
        }

        double chance = getChanceToTrigger(enchantLevel);

        if (chance < ThreadLocalRandom.current().nextDouble(100)) {
            return;
        }

        LUCKY_PLAYERS.add(e.getPlayer().getUniqueId());
        if (!this.disableMessages) {
            PlayerUtils.sendMessage(e.getPlayer(), this.onActivateMessage);
        }

        Schedulers.sync().runLater(() -> {
            if (e.getPlayer().isOnline() && !this.disableMessages) {
                PlayerUtils.sendMessage(e.getPlayer(), this.onDeactivateMessage);
            }
            LUCKY_PLAYERS.remove(e.getPlayer().getUniqueId());
        }, 5, TimeUnit.MINUTES);
    }

    @Override
    public double getChanceToTrigger(int enchantLevel) {
        return chance * enchantLevel;
    }

    @Override
    public void reload() {
        super.reload();
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants."+id+".Chance");
        this.onActivateMessage = plugin.getEnchantsConfig().getYamlConfig().getString("enchants."+id+".OnActivate");
        this.onDeactivateMessage = plugin.getEnchantsConfig().getYamlConfig().getString("enchants."+id+".OnDeactivate");
        this.disableMessages = plugin.getEnchantsConfig().getYamlConfig().getBoolean("enchants."+id+".DisableMessages");
        MULTIPLIER = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants."+id+".Multiplier");
        LUCKY_PLAYERS.clear();
    }

    @Override
    public String getAuthor() {
        return "blithe_kitsune";
    }
    
}
