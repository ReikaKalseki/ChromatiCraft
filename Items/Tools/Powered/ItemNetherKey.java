package Reika.ChromatiCraft.Items.Tools.Powered;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class ItemNetherKey extends ItemPoweredChromaTool {

	private static final String NBT_TAG = "netherKeyActive";

	public ItemNetherKey(int index) {
		super(index);
	}

	@Override
	public CrystalElement getColor(ItemStack is) {
		return CrystalElement.ORANGE;
	}

	@Override
	public int getMaxCharge() {
		return 24000;
	}

	@Override
	public int getChargeStates() {
		return 2;
	}

	@Override
	public int getChargeState(float frac) {
		return frac > 0 ? 1 : 0;
	}

	@Override
	protected boolean isActivated(EntityPlayer e, ItemStack is, boolean held) {
		return e.worldObj.provider.dimensionId == -1;
	}

	@Override
	public int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is) {
		return 1;
	}

	@Override
	protected boolean doTick(ItemStack is, World world, EntityPlayer e, boolean held) {
		e.getEntityData().setLong(NBT_TAG, world.getTotalWorldTime());
		return true;
	}

	public static boolean isPlayerTagged(EntityPlayer ep) {
		long has = ep.getEntityData().getLong(NBT_TAG);
		return ep.worldObj.getTotalWorldTime()-has <= 20;
	}

}
