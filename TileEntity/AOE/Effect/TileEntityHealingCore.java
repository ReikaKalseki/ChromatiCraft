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
import net.minecraft.block.BlockAnvil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.Repairable;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;


public class TileEntityHealingCore extends TileEntityAdjacencyUpgrade {

	private static final HashMap<Class, RepairInterface> interactions = new HashMap();

	@Override
	protected boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		int tier = this.getTier();
		Block b = world.getBlock(dx, dy, dz);
		if (b instanceof Repairable) {
			((Repairable)b).repair(world, dx, dy, dz, tier);
		}
		else if (b instanceof BlockAnvil) {
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (meta > 0 && rand.nextInt(200) < 1+tier*8) {
				world.setBlockMetadataWithNotify(dx, dy, dz, meta-4, 3);
			}
		}

		TileEntity te = this.getAdjacentTileEntity(dir);
		if (te instanceof Repairable) {
			((Repairable)te).repair(world, dx, dy, dz, tier);
		}
		else if (te instanceof IInventory) {
			IInventory ii = (IInventory)te;
			int slot = rand.nextInt(ii.getSizeInventory());
			ItemStack is = ii.getStackInSlot(slot);
			if (this.canRepair(is)) {
				this.repair(is);
			}
		}
		if (te != null) {
			RepairInterface s = this.getInterface(te);
			if (s != NoInterface.instance) {
				try {
					s.tick(te, this.getTier());
				}
				catch (Exception ex) {
					ChromatiCraft.logger.logError("Could not tick repair interface "+s+" for "+te+" @ "+this);
					this.writeError(ex);
				}
			}
		}
		return true;
	}

	private RepairInterface getInterface(TileEntity te) {
		Class c = te.getClass();
		Class c2 = c;
		RepairInterface e = interactions.get(c2);
		while (e == null && c2 != TileEntity.class) {
			c2 = c2.getSuperclass();
			e = interactions.get(c2);
		}
		if (e == null)
			e = NoInterface.instance;
		interactions.put(c, e);
		return e;
	}

	private boolean canRepair(ItemStack is) {
		if (is == null)
			return false;
		if (is.getItem().isRepairable() && is.getItemDamage() > 0)
			return true;
		return false;
	}

	private void repair(ItemStack is) {
		is.setItemDamage(is.getItemDamage()-this.getTier());
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.MAGENTA;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private static abstract class RepairInterface {

		protected RepairInterface() {
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

		protected abstract void tick(TileEntity te, int tier) throws Exception;

		protected abstract void init() throws Exception;

		protected abstract ModList getMod();

		protected abstract String[] getClasses();

		protected TileEntity getActingTileEntity(TileEntity te) throws Exception {
			return te;
		}
	}

	private static class NoInterface extends RepairInterface { //Used for null

		private static final NoInterface instance = new NoInterface();

		@Override
		protected void tick(TileEntity te, int tier) throws Exception {

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

	private static abstract class FieldSetRepairInterface extends RepairInterface {

		@Override
		protected final void tick(TileEntity te, int tier) throws Exception {
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

	private static class DecalcificationInterface extends FieldSetRepairInterface {

		private Field calcification;

		@Override
		protected void init() throws Exception {
			Class c = Class.forName("ic2.core.block.machine.tileentity.TileEntitySteamGenerator");
			calcification = c.getDeclaredField("calcification");
			calcification.setAccessible(true);
		}

		@Override
		protected ModList getMod() {
			return ModList.IC2;
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"ic2.core.block.machine.tileentity.TileEntitySteamGenerator"};
		}

		@Override
		protected Field getSetField(TileEntity te) throws Exception {
			return calcification;
		}

		@Override
		protected double getReplacedValue(TileEntity te, int tier, Number original) throws Exception {
			return calcification.getInt(te)*this.getReductionFactor(tier);
		}

		private double getReductionFactor(int tier) {
			return Math.pow(0.95, 1+tier);
		}

	}

}
