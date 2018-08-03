/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant.Modifier;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityAuraInfuser;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray.MultiKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.BlockCheck;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;


public class ProductChecks {

	public static abstract class ProductCondition {

		public abstract boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh);

		public abstract String getDescription();

	}

	static class IridescentShardCheck extends ProductCondition {

		//private final AreaBlockCheck tileCheck;

		IridescentShardCheck() {
			//new AreaBlockCheck(new BlockKey(ChromaBlocks.CHROMA.getBlockInstance(), 0), 1);
			//tileCheck = new AreaBlockCheck(new BlockKey(ChromaTiles.INFUSER), 1, 1);
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;
			UUID id = ibh.getOwner().getId();
			if (id == null)
				return false;
			if (!ProgressStage.ALLOY.isPlayerAtStage(world, id))
				return false;
			TileEntityAuraInfuser te = this.check(world, x, y, z, r2, r2);
			return te != null && te.hasStructure() && te.isOwnedByPlayer(id);
		}

		private TileEntityAuraInfuser check(World world, int x, int y, int z, int r, int vr) {
			boolean last = false;
			for (int i = -r; i <= r; i += 1) {
				for (int k = -r; k <= r; k += 1) {
					for (int h = -vr; h <= vr; h += 1) {
						int dx = x+i;
						int dy = y+h;
						int dz = z+k;
						if (ChromaTiles.getTile(world, dx, dy, dz) == ChromaTiles.INFUSER) {
							return (TileEntityAuraInfuser)world.getTileEntity(dx, dy, dz);
						}
					}
				}
			}
			return null;
		}

		@Override
		public String getDescription() {
			return "An operational infusion ring";
		}


	}

	static class AuraLocusCheck extends ProductCondition {

		AuraLocusCheck() {

		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			EntityPlayer ep = ChromaBeeHelpers.getOwner(ibh);
			if (ep == null)
				return false;
			TileEntityAuraPoint te = TileEntityAuraPoint.getPoint(ep);
			if (te == null)
				return false;
			int[] r = ChromaBeeHelpers.getSearchRange(ibg, ibh);
			return Math.abs(te.xCoord-x) <= r[0] && Math.abs(te.zCoord-z) <= r[0] && Math.abs(te.yCoord-y) <= r[1];
		}

		@Override
		public String getDescription() {
			return "A nearby Aura Locus";
		}
	}

	static class AreaBlockCheck extends ProductCondition {

		private final BlockCheck check;
		private final int stepSize;
		private final int stepSizeY;

		private final HashMap<WorldLocation, Coordinate> successfulChecks = new HashMap();
		private static final WeightedRandom<Double> rangeRandom = new WeightedRandom();
		private static final int SEARCH_LOCS = 16;

		static {
			rangeRandom.addEntry(-1D, 1D);
			rangeRandom.addEntry(1D, 4D);
			rangeRandom.addEntry(0.75D, 6D);
			rangeRandom.addEntry(0.5D, 8D);
			rangeRandom.addEntry(0.25D, 16D);
			rangeRandom.addEntry(0.125D, 32D);
		}

		AreaBlockCheck(BlockCheck bk, int s) {
			this(bk, s, s);
		}

		AreaBlockCheck(BlockCheck bk, int s, int sy) {
			check = bk;
			stepSize = s;
			stepSizeY = sy;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			int[] r = ChromaBeeHelpers.getSearchRange(ibg, ibh);
			WorldLocation loc = ChromaBeeHelpers.getLocation(ibh);
			Coordinate c = successfulChecks.get(loc);
			if (c != null && !this.validate(world, loc, c, r[0], r[1]))
				c = null;
			if (c == null) {
				Coordinate find = this.check(world, x, y, z, r[0], r[1]);
				if (find != null) {
					successfulChecks.put(loc, find);
					c = find;
				}
			}
			return c != null;
		}

		private boolean validate(World world, WorldLocation loc, Coordinate c, int r, int vr) {
			if (!c.isWithinSquare(new Coordinate(loc), r, vr, r))
				return false;
			if (!check.matchInWorld(world, c.xCoord, c.yCoord, c.zCoord))
				return false;
			return true;
		}

		private Coordinate check(World world, int x, int y, int z, int r, int vr) {
			double f = rangeRandom.getRandomEntry();
			if (f == -1) {
				for (int i = -r; i <= r; i += stepSize) {
					for (int k = -r; k <= r; k += stepSize) {
						for (int h = -vr; h <= vr; h += stepSizeY) {
							int dx = x+i;
							int dy = y+h;
							int dz = z+k;
							if (check.matchInWorld(world, dx, dy, dz)) {
								return new Coordinate(dx, dy, dz);
							}
						}
					}
				}
			}
			else {
				int dr = (int)(f*r);
				int dvr = (int)(f*vr);
				for (int i = 0; i < SEARCH_LOCS; i++) {
					int dx = ReikaRandomHelper.getRandomPlusMinus(x, dr);
					int dy = ReikaRandomHelper.getRandomPlusMinus(y, dvr);
					int dz = ReikaRandomHelper.getRandomPlusMinus(z, dr);
					if (check.matchInWorld(world, dx, dy, dz)) {
						return new Coordinate(dx, dy, dz);
					}
				}
			}
			return null;
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
			return progress.isPlayerAtStage(world, ibh.getOwner().getId());
		}

		@Override
		public String getDescription() {
			return "Progression '"+progress.getTitle()+"'";
		}

	}

	static class CrystalPlantCheck extends ProductCondition {

		private final CrystalElement color;
		private final AreaBlockCheck check;

		CrystalPlantCheck(CrystalElement e) {
			color = e;
			check = new AreaBlockCheck(new BlockKey(ChromaBlocks.PLANT.getBlockInstance(), color.ordinal()), 1);
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			if (check.check(world, x, y, z, ibg, ibh)) {
				if (world.rand.nextInt(30) == 0) {
					WorldLocation loc = ChromaBeeHelpers.getLocation(ibh);
					Coordinate plant = check.successfulChecks.get(loc);
					TileEntityCrystalPlant te = (TileEntityCrystalPlant)plant.getTileEntity(world);
					te.setState(Modifier.BOOSTED);
					if (world.rand.nextInt(30) == 0) {
						te.setState(Modifier.PRIMAL);
					}
				}
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return color.displayName+" Crystal Bloom";
		}
	}

	static class FlowerCheck extends ProductCondition {

		private final CrystalElement color;
		private final AreaBlockCheck check;

		FlowerCheck(CrystalElement e) {
			color = e;
			check = new AreaBlockCheck(new BlockKey(ChromaBlocks.DYEFLOWER.getBlockInstance(), color.ordinal()), 1);
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			return check.check(world, x, y, z, ibg, ibh);
		}

		@Override
		public String getDescription() {
			return color.displayName+" Dye Flowers";
		}
	}

	static class LeafCheck extends ProductCondition {

		private final CrystalElement color;
		private final AreaBlockCheck check;

		LeafCheck(CrystalElement e) {
			color = e;
			MultiKey mk = new MultiKey();
			mk.add(new BlockKey(ChromaBlocks.DECAY.getBlockInstance(), color.ordinal()));
			//mk.add(new BlockKey(ChromaBlocks.DYELEAF.getBlockInstance(), color.ordinal()));
			check = new AreaBlockCheck(mk, 2, 2);
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			return check.check(world, x, y, z, ibg, ibh);
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
		private final ProgressionCheck progress;

		ChargedShardCheck(CrystalElement e) {
			color = e;
			leaf = new LeafCheck(e);
			chroma = new AreaBlockCheck(new BlockKey(ChromaBlocks.CHROMA.getBlockInstance(), 0), 1);
			MultiKey crys = new MultiKey();
			crys.add(new BlockKey(ChromaBlocks.CRYSTAL.getBlockInstance(), color.ordinal()));
			crys.add(new BlockKey(ChromaBlocks.SUPER.getBlockInstance(), color.ordinal()));
			crystal = new AreaBlockCheck(crys, 1);
			progress = new ProgressionCheck(ProgressStage.SHARDCHARGE);
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			return progress.check(world, x, y, z, ibg, ibh) && crystal.check(world, x, y, z, ibg, ibh) && chroma.check(world, x, y, z, ibg, ibh) && leaf.check(world, x, y, z, ibg, ibh);
		}

		@Override
		public String getDescription() {
			return "Liquid Chroma, "+color.displayName+" Tree and Crystal";
		}

	}

	static class RainbowTreeCheck extends ProductCondition {

		private final AreaBlockCheck check;

		RainbowTreeCheck() {
			check = new AreaBlockCheck(new BlockKey(ChromaBlocks.RAINBOWLEAF.getBlockInstance(), 0), 3, 2);
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			return check.check(world, x, y, z, ibg, ibh);
		}

		@Override
		public String getDescription() {
			return "Rainbow Leaves";
		}

	}
}
