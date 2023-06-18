package Reika.ChromatiCraft.Items.Tools.Powered;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Magic.Interfaces.ChargingPoint;
import Reika.ChromatiCraft.Registry.CrystalElement;

//TODO Incomplete item
public class ItemBodyLight extends ItemPoweredChromaTool {

	public ItemBodyLight(int index) {
		super(index);
	}

	@Override
	public float getPlayerChargeCoefficient(EntityPlayer ep, ChargingPoint te, ItemStack is) {
		return 0.33F;
	}

	@Override
	public CrystalElement getColor(ItemStack is) {
		return CrystalElement.BLUE;
	}

	@Override
	public int getMaxCharge() {
		return 0;
	}

	@Override
	public int getChargeStates() {
		return 0;
	}

	@Override
	public boolean isActivated(EntityPlayer e, ItemStack is, boolean held) {
		return false;
	}

	@Override
	public int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is) {
		return 0;
	}

	@Override
	protected boolean doTick(ItemStack is, World world, EntityPlayer e, boolean held) {
		return false;
	}

}
