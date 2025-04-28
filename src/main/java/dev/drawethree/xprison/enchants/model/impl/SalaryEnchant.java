package dev.drawethree.xprison.enchants.model.impl;

import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public final class SalaryEnchant extends XPrisonEnchantment {

    private double chance;
    private String amountToGiveExpression;

    public SalaryEnchant(XPrisonEnchants instance) {
        super(instance, 12);
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Chance");
        this.amountToGiveExpression = plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".Amount-To-Give");
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {
        double chance = getChanceToTrigger(e.getPlayer(), enchantLevel);
        if (chance < ThreadLocalRandom.current().nextDouble(100)) {
            return;
        }

        long playerPrestige = this.plugin.getCore().getPrestiges().getPrestigeManager().getPlayerPrestige(e.getPlayer()).getId();
        double randAmount = createExpression(enchantLevel, playerPrestige).evaluate();

        plugin.getCore().getEconomy().depositPlayer(e.getPlayer(), randAmount);

        if (this.plugin.isAutoSellModuleEnabled()) {
            plugin.getCore().getAutoSell().getManager().addToCurrentEarnings(e.getPlayer(), randAmount);
        }

    }

    @Override
    public double getChanceToTrigger(int enchantLevel) {
        return this.chance * enchantLevel;
    }

    @Override
    public void reload() {
        super.reload();
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Chance");
        this.amountToGiveExpression = plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".Amount-To-Give");
    }

    private Expression createExpression(int level, long prestige) {
        return new ExpressionBuilder(this.amountToGiveExpression)
                .variables("level")
                .variables("prestige")
                .build()
                .setVariable("level", level)
                .setVariable("prestige", prestige);
    }

    @Override
    public String getAuthor() {
        return "Drawethree";
    }
}
