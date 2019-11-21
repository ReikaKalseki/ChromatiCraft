package Reika.ChromatiCraft.ModInterface;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class TileEntityVoidMonsterTrap extends ChargedCrystalPowered {

	private static final ElementTagCompound required = new ElementTagCompound();

	static {
		required.addTag(CrystalElement.BLACK, 5);
		required.addTag(CrystalElement.PINK, 20);
		required.addTag(CrystalElement.LIGHTGRAY, 4);
		required.addTag(CrystalElement.GRAY, 1);
		required.addTag(CrystalElement.WHITE, 10);
		required.addTag(CrystalElement.MAGENTA, 2);
	}

	private int ritualTick;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (ReikaItemHelper.matchStacks(inv[0], ChromaStacks.voidDust)) {
			if (this.hasEnergy(required)) {
				if (this.isActive()) {

				}
				else {

				}
				this.useEnergy(required);
				if (rand.nextInt(this.isActive() ? 20 : 60) == 0)
					ReikaInventoryHelper.decrStack(1, inv);
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean isActive() {
		return ritualTick > 0;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 8;
	}

	@Override
	public float getCostModifier() {
		return this.isActive() ? 2 : 1;
	}

	@Override
	public boolean usesColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	protected boolean canExtractOtherItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	protected boolean isItemValidForOtherSlot(int slot, ItemStack is) {
		return slot == 1 && ReikaItemHelper.matchStacks(is, ChromaStacks.voidDust);
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.VOIDTRAP;
	}

}
