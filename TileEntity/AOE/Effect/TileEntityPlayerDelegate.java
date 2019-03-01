/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityCollector;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;

import thaumcraft.api.aspects.Aspect;


public class TileEntityPlayerDelegate extends TileEntityAdjacencyUpgrade {

	private static final HashMap<Class, DelegateInterface> interactions = new HashMap();

	static {
		new DeconstructionTableDelegateInterface();
	}

	@Override
	protected EffectResult tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		TileEntity te = this.getAdjacentTileEntity(dir);
		if (te != null) {
			DelegateInterface s = this.getInterface(te);
			if (s != NoInterface.instance) {
				try {
					s.tick(te, this.getTier(), this.getPlacer());
				}
				catch (Exception ex) {
					ChromatiCraft.logger.logError("Could not tick Delegate interface "+s+" for "+te+" @ "+this);
					this.writeError(ex);
				}
				return EffectResult.ACTION;
			}
		}
		else if (!world.isRemote) {
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			if (b instanceof CrystalBlock && rand.nextInt(80/(this.getTier()+1)) == 0) {
				CrystalBlock c = (CrystalBlock)b;
				CrystalElement e = CrystalElement.elements[world.getBlockMetadata(dx, dy, dz)];
				if (e == CrystalElement.PURPLE) {
					CrystalPotionController.applyEffectFromColor(c.getDuration(e), c.getPotionLevel(e), this.getPlacer(), e, false);
				}
				else if (c.shouldGiveEffects(e) && c.performEffect(e)) {
					c.updateEffects(world, dx, dy, dz);
				}
				return EffectResult.ACTION;
			}
		}
		return EffectResult.CONTINUE;
	}

	private DelegateInterface getInterface(TileEntity te) {
		Class c = te.getClass();
		Class c2 = c;
		DelegateInterface e = interactions.get(c2);
		while (e == null && c2 != TileEntity.class) {
			c2 = c2.getSuperclass();
			e = interactions.get(c2);
		}
		if (e == null)
			e = NoInterface.instance;
		interactions.put(c, e);
		return e;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.LIGHTGRAY;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private static abstract class DelegateInterface {

		protected DelegateInterface() {
			if (this.getMod() == null || this.getMod().isLoaded()) {
				try {
					String[] cs = this.getClasses();
					for (int i = 0; i < cs.length; i++) {
						Class c = Class.forName(cs[i]);
						interactions.put(c, this);
					}
					this.init();
					ChromatiCraft.logger.log("Loaded "+this+" for "+this.getMod());
				}
				catch (Exception e) {
					ChromatiCraft.logger.logError("Could not load "+this+" for "+this.getMod()+":");
					e.printStackTrace();
					ReflectiveFailureTracker.instance.logModReflectiveFailure(this.getMod(), e);
				}
			}
			else {
				ChromatiCraft.logger.log("Not loading "+this+" for "+this.getMod()+"; Mod not present.");
			}
		}

		protected abstract void tick(TileEntity te, int tier, EntityPlayer ep) throws Exception;

		protected abstract void init() throws Exception;

		protected abstract ModList getMod();

		protected abstract String[] getClasses();

		protected TileEntity getActingTileEntity(TileEntity te) throws Exception {
			return te;
		}
	}

	private static class NoInterface extends DelegateInterface { //Used for null

		private static final NoInterface instance = new NoInterface();

		@Override
		protected void tick(TileEntity te, int tier, EntityPlayer ep) throws Exception {

		}

		@Override
		protected void init() throws Exception {

		}

		@Override
		protected ModList getMod() {
			return null;
		}

		@Override
		protected String[] getClasses() {
			return new String[0];
		}

	}

	private static abstract class FieldSetDelegateInterface extends DelegateInterface {

		@Override
		protected final void tick(TileEntity te, int tier, EntityPlayer ep) throws Exception {
			te = this.getActingTileEntity(te);
			Field f = this.getSetField(te);
			Number get = (Number)f.get(te);
			double val = this.getReplacedValue(te, tier, get);
			Object set = null;
			if (get instanceof Integer || get.getClass() == int.class) {
				set = (int)val;
			}
			else if (get instanceof Double || get.getClass() == double.class) {
				set = (double)val;
			}
			else if (get instanceof Float || get.getClass() == float.class) {
				set = (float)val;
			}
			else if (get instanceof Short || get.getClass() == short.class) {
				set = (short)val;
			}
			else if (get instanceof Byte || get.getClass() == byte.class) {
				set = (byte)val;
			}
			f.set(te, set);
		}

		protected abstract double getReplacedValue(TileEntity te, int tier, Number original) throws Exception;

		protected abstract Field getSetField(TileEntity te) throws Exception;

	}

	private static class DeconstructionTableDelegateInterface extends DelegateInterface {

		private Field aspectOutput;

		@Override
		protected void tick(TileEntity te, int tier, EntityPlayer ep) throws Exception {
			Aspect a = (Aspect)aspectOutput.get(te);
			if (a != null) {
				ReikaThaumHelper.giveResearchPoint(a, (short)1, ep);
				aspectOutput.set(te, null);
			}
		}

		@Override
		protected void init() throws Exception {
			aspectOutput = Class.forName("thaumcraft.common.tiles.TileDeconstructionTable").getDeclaredField("aspect");
			aspectOutput.setAccessible(true);
		}

		@Override
		protected ModList getMod() {
			return ModList.THAUMCRAFT;
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"thaumcraft.common.tiles.TileDeconstructionTable"};
		}

	}

	private static class ChromaCollectorDelegateInterface extends DelegateInterface {

		@Override
		protected void tick(TileEntity te, int tier, EntityPlayer ep) throws Exception {
			TileEntityCollector tc = (TileEntityCollector)te;
			tc.tryIntakeXPFromPlayer(ep, false);
		}

		@Override
		protected void init() throws Exception {

		}

		@Override
		protected ModList getMod() {
			return ModList.CHROMATICRAFT;
		}

		@Override
		protected String[] getClasses() {
			return new String[]{TileEntityCollector.class.getName()};
		}

	}

}
