package Reika.ChromatiCraft.Items.Tools.Powered;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModInteract.ItemHandlers.TwilightForestHandler;


public class ItemSpawnerBypass extends ItemPoweredChromaTool {

	public ItemSpawnerBypass(int index) {
		super(index);
	}

	@Override
	public CrystalElement getColor(ItemStack is) {
		return CrystalElement.LIGHTGRAY;
	}

	@Override
	public int getMaxCharge() {
		return 72000*1; //1h, 5 min in TF
	}

	@Override
	public int getChargeStates() {
		return 1;
	}

	@Override
	protected boolean isActivated(EntityPlayer e, ItemStack is, boolean held) {
		return true;
	}

	@Override
	public int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is) {
		return world.provider.dimensionId == TwilightForestHandler.getInstance().dimensionID ? 12 : 1;
	}

	@Override
	protected boolean doTick(ItemStack is, World world, EntityPlayer e, boolean held) {
		e.getEntityData().setLong("spawnertoggle", world.getTotalWorldTime());
		return isWorking(e);
	}

	public static boolean isActive(EntityPlayer ep) {
		return ep.worldObj.getTotalWorldTime()-ep.getEntityData().getLong("spawnertoggle") < 5;
	}

	public static boolean isWorking(EntityPlayer ep) {
		return ep.worldObj.getTotalWorldTime()-ep.getEntityData().getLong("spawnerpass") < 20;
	}

}
