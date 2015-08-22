package Reika.ChromatiCraft.World.Dimension;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler.PlayerTracker;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class DimensionBubble implements PlayerTracker {

	public static final DimensionBubble instance = new DimensionBubble();

	private DimensionBubble() {

	}

	@Override
	public void onPlayerLogin(EntityPlayer ep) {
		this.clearAreaForPlayer(ep);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {

	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player, int dimFrom, int dimTo) {

	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		this.clearAreaForPlayer(player);
	}

	private void clearAreaForPlayer(EntityPlayer ep) {
		if (ep.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			double r = 5;
			double rh = 3.5;

			int x = MathHelper.floor_double(ep.posX);
			int y = MathHelper.floor_double(ep.posY);
			int z = MathHelper.floor_double(ep.posZ);

			for (double i = -r; i <= r; i++) {
				for (double j = -rh; j <= rh; j++) {
					for (double k = -r; k <= r; k++) {
						int dx = MathHelper.floor_double(x+i);
						int dy = MathHelper.floor_double(y+j);
						int dz = MathHelper.floor_double(z+k);

						if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r, rh, r)) {
							if (this.clear(ep.worldObj, dx, dy, dz)) {
								if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r-1, rh-1, r-1)) {
									ep.worldObj.setBlock(dx, dy, dz, Blocks.air);
								}
								else {
									ep.worldObj.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata%8, 3);
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean clear(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b.isAir(world, x, y, z))
			return false;
		if (b instanceof BlockLiquid || b instanceof BlockFluidBase)
			return false;
		if (b instanceof BlockLeavesBase || b instanceof BlockLog)
			return false;
		if (b.getMaterial() == Material.plants)
			return false;
		if (b.blockHardness < 0)
			return false;
		if (b instanceof SemiUnbreakable)
			return !((SemiUnbreakable)b).isUnbreakable(world, x, y, z, world.getBlockMetadata(x, y, z));
		if (world.getTileEntity(x, y, z) != null)
			return false;
		return true;
	}

}
