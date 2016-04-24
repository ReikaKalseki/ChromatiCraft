package Reika.ChromatiCraft.ModInterface.Bees;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Block.Dye.BlockDyeLeaf;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray.MultiKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Interfaces.BlockCheck;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;


public class ProductChecks {

	public static abstract class ProductCondition {

		public abstract boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh);

		public abstract String getDescription();

	}

	static class AreaBlockCheck extends ProductCondition {

		private final BlockCheck check;

		AreaBlockCheck(BlockCheck bk) {
			check = bk;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return this.check(world, x, y, z, r, r2);
		}

		public boolean check(World world, int x, int y, int z, int r, int vr) {
			int d = 2;
			boolean last = false;
			for (int i = -r; i <= r; i += d) {
				for (int k = -r; k <= r; k += d) {
					for (int h = -vr; h <= vr; h += d) {
						int dx = x+i;
						int dy = y+h;
						int dz = z+k;
						if (check.matchInWorld(world, dx, dy, dz)) {
							return true;
						}
					}
				}
			}
			return false;
		}

		@Override
		public String getDescription() {
			return check.asItemStack().getDisplayName();
		}

	}

	static class ProgressionCheck extends ProductCondition {

		private final ProgressStage progress;

		ProgressionCheck(ProgressStage p) {
			progress = p;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			return ep != null && progress.isPlayerAtStage(ep);
		}

		@Override
		public String getDescription() {
			return "Progression '"+progress.getTitle()+"'";
		}

	}

	static class CrystalPlantCheck extends ProductCondition {

		private final CrystalElement color;

		CrystalPlantCheck(CrystalElement e) {
			color = e;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return ReikaWorldHelper.findNearBlock(world, x, y, z, r2, ChromaBlocks.PLANT.getBlockInstance(), color.ordinal());
		}

		@Override
		public String getDescription() {
			return color.displayName+" Crystal Bloom";
		}
	}

	static class FlowerCheck extends ProductCondition {

		private final CrystalElement color;

		FlowerCheck(CrystalElement e) {
			color = e;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return ReikaWorldHelper.findNearBlock(world, x, y, z, r2, ChromaBlocks.DYEFLOWER.getBlockInstance(), color.ordinal());
		}

		@Override
		public String getDescription() {
			return color.displayName+" Dye Flowers";
		}
	}

	static class LeafCheck extends ProductCondition {

		private final CrystalElement color;

		LeafCheck(CrystalElement e) {
			color = e;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return this.findLeaf(world, x, y, z, r, r2);
		}

		private boolean findLeaf(World world, int x, int y, int z, int r, int vr) {
			int d = 2;
			boolean last = false;
			for (int i = -r; i <= r; i += d) {
				for (int k = -r; k <= r; k += d) {
					for (int h = -vr; h <= vr; h += d) {
						int dx = x+i;
						int dy = y+h;
						int dz = z+k;
						Block b = world.getBlock(dx, dy, dz);
						if (b instanceof BlockDyeLeaf && world.getBlockMetadata(dx, dy, dz) == color.ordinal()) {
							if (last)
								return true;
							else
								last = true;
						}
						else
							last = false;
					}
				}
			}
			return false;
		}

		@Override
		public String getDescription() {
			return color.displayName+" Dye Leaves";
		}

	}

	static class ChargedShardCheck extends ProductCondition {

		private final CrystalElement color;

		private final AreaBlockCheck crystal;
		private final AreaBlockCheck chroma;
		private final LeafCheck leaf;

		ChargedShardCheck(CrystalElement e) {
			color = e;
			leaf = new LeafCheck(e);
			chroma = new AreaBlockCheck(new BlockKey(ChromaBlocks.CHROMA.getBlockInstance(), 0));
			MultiKey crys = new MultiKey();
			crys.add(new BlockKey(ChromaBlocks.CRYSTAL.getBlockInstance(), 0));
			crys.add(new BlockKey(ChromaBlocks.SUPER.getBlockInstance(), 0));
			crystal = new AreaBlockCheck(crys);
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			return crystal.check(world, x, y, z, ibg, ibh) && chroma.check(world, x, y, z, ibg, ibh) && leaf.check(world, x, y, z, ibg, ibh);
		}

		@Override
		public String getDescription() {
			return color.displayName+" Crystal and Leaves and Liquid Chroma";
		}

	}

	static class RainbowTreeCheck extends ProductCondition {

		RainbowTreeCheck() {

		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return this.findLeaf(world, x, y, z, r, r2);
		}

		private boolean findLeaf(World world, int x, int y, int z, int r, int vr) {
			int d = 2;
			boolean last = false;
			for (int i = -r; i <= r; i += d) {
				for (int k = -r; k <= r; k += d) {
					for (int h = -vr; h <= vr; h += d) {
						int dx = x+i;
						int dy = y+h;
						int dz = z+k;
						Block b = world.getBlock(dx, dy, dz);
						if (b == ChromaBlocks.RAINBOWLEAF.getBlockInstance() && world.getBlockMetadata(dx, dy, dz) == 0) {
							if (last)
								return true;
							else
								last = true;
						}
						else
							last = false;
					}
				}
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "Rainbow Leaves";
		}

	}
}
