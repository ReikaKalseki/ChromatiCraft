package Reika.ChromatiCraft.API;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;
import Reika.ChromatiCraft.API.Interfaces.AdjacencyCheckHandler;
import Reika.ChromatiCraft.API.Interfaces.CustomAcceleration;
import Reika.ChromatiCraft.API.Interfaces.CustomHealing.CustomBlockHealing;
import Reika.ChromatiCraft.API.Interfaces.CustomHealing.CustomTileHealing;
import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade;
import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade.RangeUpgradeable;

public interface AdjacencyUpgradeAPI {

	/** Fetch a {@link AdjacencyCheckHandler}, for a given color. Supply what the effect will do, and the item versions of the blocks it applies to. */
	public AdjacencyCheckHandler createCheckHandler(CrystalElementProxy color, String desc, ItemStack... items);

	public double getFactor(CrystalElementProxy e, int tier);

	public void addCustomAcceleration(Class<? extends TileEntity> c, CustomAcceleration a);

	public void addCustomRangeBoost(Class<? extends TileEntity> c, CustomRangeUpgrade a);
	/** The ItemStacks here are the item forms of the relevant machines, used the same way as the "relevant items" on the full handlers */
	public void addBasicRangeBoost(Class<? extends RangeUpgradeable> c, ItemStack... items);

	public void addCustomHealing(Block b, CustomBlockHealing h);
	public void addCustomHealing(Block b, int meta, CustomBlockHealing h);
	public void addCustomHealing(Class<? extends TileEntity> c, CustomTileHealing h);


	/** Use this to blacklist your TileEntity class from being accelerated with the TileEntity acclerator.
	 * You must specify a reason (from the {@link BlacklistReason} enum) which will be put into the loading log.
	 * Arguments: TileEntity class, Reason.
	 * Sample log message:<br>
	 * <i> CHROMATICRAFT:
	 * "TileEntity "Miner" has been blacklisted from the TileEntity Accelerator, because the creator finds it unbalanced or overpowered."
	 * </i>*/
	public void addAcceleratorBlacklist(Class<? extends TileEntity> cl, String name, ItemStack item, BlacklistReason r);

	public void addAcceleratorBlacklist(Class<? extends TileEntity> cl, ItemStack item, BlacklistReason r);

	public static enum BlacklistReason {
		BUGS("it will cause bugs or other errors."),
		CRASH("it would cause a crash."),
		BALANCE("the creator finds it unbalanced or overpowered."),
		EXPLOIT("it creates an exploit."),
		OPINION("the creator wishes it to be disabled.");

		public final String message;

		private BlacklistReason(String msg) {
			message = msg;
		}
	}
}
