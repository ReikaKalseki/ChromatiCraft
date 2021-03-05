package Reika.ChromatiCraft.Items.Tools.Powered;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class ItemBodyLight extends ItemPoweredChromaTool {

	public ItemBodyLight(int index) {
		super(index);
	}

	@Override
	public boolean canChargeWhilePlayerCharges() {
		return true;
	}

	@Override
	public CrystalElement getColor(ItemStack is) {
		return CrystalElement.BLUE;
	}

	@Override
	public int getMaxCharge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getChargeStates() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isActivated(EntityPlayer e, ItemStack is, boolean held) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected boolean doTick(ItemStack is, World world, EntityPlayer e, boolean held) {
		// TODO Auto-generated method stub
		return false;
	}

}
