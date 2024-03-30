package dev.drawethree.xprison.utils.economy;

import dev.drawethree.xprison.XPrison;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyUtils {

    private static final Economy ECONOMY = XPrison.getInstance().getEconomy();

    private static final Map<UUID, Double> DEPOSIT_CACHE = new ConcurrentHashMap<>();

    public static void init() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(XPrison.getInstance(), () -> {
            final Iterator<Map.Entry<UUID, Double>> iterator = DEPOSIT_CACHE.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<UUID, Double> entry = iterator.next();
                iterator.remove();
                final OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
                ECONOMY.depositPlayer(player, entry.getValue());
            }
        }, 20L, 400L);
    }

    public static void deposit(Player player, double amount) {
        DEPOSIT_CACHE.merge(player.getUniqueId(), amount, Double::sum);
    }

    public static EconomyResponse withdraw(Player player, double amount) {
        return ECONOMY.withdrawPlayer(player, amount);
    }
}
