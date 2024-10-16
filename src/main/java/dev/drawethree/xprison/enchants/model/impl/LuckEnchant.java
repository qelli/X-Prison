package dev.drawethree.xprison.enchants.model.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import dev.drawethree.xprison.tokens.XPrisonTokens;

public class LuckEnchant extends XPrisonEnchantment {

    private static final Set<UUID> LUCKY_PLAYERS = new HashSet<>();
    private double chance;
    private String onActivateMessage;
    private String onDeactivateMessage;
    private boolean disableMessages;
    private double multiplier;

    public LuckEnchant(XPrisonEnchants instance) {
        super(instance, 24);
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants."+id+".Chance");
        this.onActivateMessage = plugin.getEnchantsConfig().getYamlConfig().getString("enchants."+id+".OnActivate");
        this.onDeactivateMessage = plugin.getEnchantsConfig().getYamlConfig().getString("enchants."+id+".OnDeactivate");
        this.multiplier = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants."+id+".Multiplier");
        this.disableMessages = plugin.getEnchantsConfig().getYamlConfig().getBoolean("enchants."+id+".DisableMessages");
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {}

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {}

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {
        if (!this.plugin.getCore().isModuleEnabled(XPrisonTokens.MODULE_NAME)) {
            return;
        }
        if (isPlayerLucky(e.getPlayer())) {
            return;
        }
        double chance = getChanceToTrigger(enchantLevel);
        if (chance < ThreadLocalRandom.current().nextDouble(100)) {
            return;
        }
        setPlayerLuck(e.getPlayer(), true);
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
        this.multiplier = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants."+id+".Multiplier");
        this.disableMessages = plugin.getEnchantsConfig().getYamlConfig().getBoolean("enchants."+id+".DisableMessages");
        LUCKY_PLAYERS.clear();
    }

    @Override
    public String getAuthor() {
        return "blithe_kitsune";
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    public void setPlayerLuck(Player p, boolean isLucky) {
        if (isLucky) {
            LUCKY_PLAYERS.add(p.getUniqueId());
            if (!this.disableMessages) {
                p.sendMessage(this.onActivateMessage);
            }
        } else {
            if(isPlayerLucky(p)) {
                if (!this.disableMessages) {
                    p.sendMessage(this.onDeactivateMessage);
                }
                LUCKY_PLAYERS.remove(p.getUniqueId());
            }
        }
    }

    public boolean isPlayerLucky(Player p) {
        return LUCKY_PLAYERS.contains(p.getUniqueId());
    }
    
}
