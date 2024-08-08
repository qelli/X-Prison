package dev.drawethree.xprison.mines.migration.model.impl;

import com.koletar.jj.mineresetlite.Mine;
import com.koletar.jj.mineresetlite.MineResetLite;
import com.koletar.jj.mineresetlite.SerializableBlock;
import dev.drawethree.xprison.mines.migration.model.MinesMigration;
import dev.drawethree.xprison.mines.model.mine.BlockPalette;
import dev.drawethree.xprison.utils.compat.CompMaterial;
import me.lucko.helper.serialize.Point;
import me.lucko.helper.serialize.Position;
import me.lucko.helper.serialize.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;

public final class MineResetLiteMigration extends MinesMigration<Mine> {

	private final MineResetLite plugin;

	public MineResetLiteMigration() {
		super("MineResetLite");
		this.plugin = ((MineResetLite) Bukkit.getPluginManager().getPlugin(this.fromPlugin));
	}

	@Override
	protected List<Mine> getMinesToMigrate() {
		return this.plugin.mines;
	}

	@Override
	protected boolean preValidateMigration() {
		return this.plugin != null;
	}

	@Override
	protected dev.drawethree.xprison.mines.model.mine.Mine migrate(Mine mine) {
		dev.drawethree.xprison.mines.model.mine.Mine.Builder builder = new dev.drawethree.xprison.mines.model.mine.Mine.Builder();

		//Name
		String name = mine.getName();
		builder.name(name);

		final Map<String, Object> map = mine.serialize();

		//Region
		Position minPoint = Position.of((int) map.get("minX"), (int) map.get("minY"), (int) map.get("minZ"), mine.getWorld());
		Position maxPoint = Position.of((int) map.get("maxX"), (int) map.get("maxY"), (int) map.get("maxZ"), mine.getWorld());
		Region region = minPoint.regionWith(maxPoint);
		builder.region(region);

		//Teleport location
		if (map.containsKey("tpX") && map.containsKey("tpY") && map.containsKey("tpZ")) {
			final Location tpPos = new Location(mine.getWorld(), (int) map.get("tpX"), (int) map.get("tpY"), (int) map.get("tpZ"));
			if (map.containsKey("tpYaw")) {
				tpPos.setYaw((int) map.get("tpYaw"));
			}
			if (map.containsKey("tpPitch")) {
				tpPos.setPitch((int) map.get("tpPitch"));
			}
			Point teleportLocation = Point.of(tpPos);
			builder.teleportLocation(teleportLocation);
		}

		//Block palette
		BlockPalette blockPalette = new BlockPalette();

		for (Map.Entry<SerializableBlock, Double> entry : mine.getComposition().entrySet()) {
			double chance = entry.getValue();
			CompMaterial material = CompMaterial.fromString(entry.getKey().getBlockId().split(":")[0]);
			blockPalette.addToPalette(material, chance);
		}

		builder.blockPalette(blockPalette);
		return builder.build();
	}
}
