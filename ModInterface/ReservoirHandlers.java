package Reika.ChromatiCraft.ModInterface;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes.PoolRecipe;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.RotaryCraft.API.ReservoirAPI.TankHandler;

public class ReservoirHandlers {

	private static final Random rand = new Random();

	private static abstract class ChromaRecipeHandlerBase implements TankHandler {

		@Override
		public final int onTick(TileEntity te, Fluid stored, int amt) {
			if (amt >= 1000 && stored == FluidRegistry.getFluid("chroma")) {
				return this.onTick(te);
			}
			else {
				return 0;
			}
		}

		protected abstract int onTick(TileEntity te);

	}

	public static class ShardBoostingHandler extends ChromaRecipeHandlerBase {

		@Override
		protected int onTick(TileEntity te) {
			return 0;
		}

	}

	public static class PoolRecipeHandler extends ChromaRecipeHandlerBase {

		@Override
		protected int onTick(TileEntity te) {
			if (rand.nextInt(3) == 0) {
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(te.xCoord, te.yCoord, te.zCoord);
				List<EntityItem> li = te.worldObj.getEntitiesWithinAABB(EntityItem.class, box);
				for (EntityItem ei : li) {
					PoolRecipe pr = PoolRecipes.instance.getPoolRecipe(ei, li, false);
					if (pr != null) {
						if (ei.worldObj.isRemote) {
							ChromaFX.poolRecipeParticles(ei);
						}
						else if (ei.ticksExisted > 20 && rand.nextInt(20) == 0 && (ei.ticksExisted >= 600 || rand.nextInt(600-ei.ticksExisted) == 0)) {
							PoolRecipes.instance.makePoolRecipe(ei, pr);
						}
						break;
					}
				}
			}
			return 0;
		}

	}

}
