package Reika.ChromatiCraft.Auxiliary.Ability;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class GrowAuraEffect {

	public abstract void performEffect(EntityPlayer ep, int x, int y, int z, int power);
	public abstract boolean isEffectViable();
	public abstract String getGuiLabel();


	public static abstract class BlockBasedGrowAuraEffect extends GrowAuraEffect {

		public abstract int getNumberPerTick(EntityPlayer ep, int x, int y, int z, int power);
		public abstract int getXZRange();
		public abstract int getYRange();
		public abstract boolean isValid(World world, int x, int y, int z, Block b);
		protected abstract void performEffect(EntityPlayer ep, int x, int y, int z, int power, Block b);

		@Override
		public final void performEffect(EntityPlayer ep, int x, int y, int z, int power) {
			this.performEffect(ep, x, y, z, power, ep.worldObj.getBlock(x, y, z));
		}

	}
}
