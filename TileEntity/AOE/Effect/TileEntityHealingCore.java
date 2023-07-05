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
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.CustomHealing.CustomBlockHealing;
import Reika.ChromatiCraft.API.Interfaces.CustomHealing.CustomTileHealing;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiStackDisplay;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MultiblockControllerFinder;

import cpw.mods.fml.common.registry.GameRegistry;


public class TileEntityHealingCore extends TileEntityAdjacencyUpgrade {

	private static final HashMap<BlockKey, BaseRepairInterface> blockInteractions = new HashMap();
	private static final HashMap<Class, BaseRepairInterface> interactions = new HashMap();

	private boolean[] warnedSides = new boolean[6];

	static {
		blockInteractions.put(new BlockKey(Blocks.anvil), new AnvilRepairInterface());

		new DecalcificationInterface();
		new RailTurbineInterface();
	}

	public static void addTileHandler(Class c, CustomTileHealing h) {
		interactions.put(c, new APIHealingTile(h));
	}

	public static void addBlockHandler(Block b, CustomBlockHealing h) {
		addBlockHandler(new BlockKey(b), h);
	}

	public static void addBlockHandler(Block b, int meta, CustomBlockHealing h) {
		addBlockHandler(new BlockKey(b, meta), h);
	}

	public static void addBlockHandler(BlockKey bk, CustomBlockHealing h) {
		blockInteractions.put(bk, new APIHealingBlock(h));
	}

	@Override
	protected EffectResult tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		int tier = this.getTier();
		EffectResult ret = EffectResult.CONTINUE;
		BaseRepairInterface bi = blockInteractions.get(BlockKey.getAt(world, dx, dy, dz));
		if (bi instanceof BlockRepairInterface && bi != NoInterface.instance && (bi.runOnClient() || !world.isRemote)) {
			try {
				((BlockRepairInterface)bi).tick(world, dx, dy, dz, tier);
			}
			catch (Exception ex) {
				ChromatiCraft.logger.logError("Could not tick repair interface "+bi+" for "+BlockKey.getAt(world, dx, dy, dz)+" @ "+this);
				this.writeError(ex);
			}
			return EffectResult.ACTION;
		}

		TileEntity te = this.getEffectiveTileOnSide(dir);
		if (te != null) {
			BaseRepairInterface s = this.getInterface(te);
			if (s instanceof TileRepairInterface && s != NoInterface.instance && (s.runOnClient() || !world.isRemote)) {
				try {
					int r = s.getTickRand(this.getTier());
					if (r <= 1 || rand.nextInt(r) == 0)
						((TileRepairInterface)s).tick(te, this.getTier());
				}
				catch (Exception ex) {
					ChromatiCraft.logger.logError("Could not tick repair interface "+s+" for "+te+" @ "+this);
					this.writeError(ex);
				}
				return EffectResult.ACTION;
			}
		}

		if (te instanceof IInventory) {
			IInventory ii = (IInventory)te;
			if (ii.getSizeInventory() == 0) {
				if (warnedSides[dir.ordinal()]) {

				}
				else {
					String sg = "Found an inventory '"+te+"' with zero size!?";
					ChromatiCraft.logger.log(sg);
					ReikaChatHelper.sendChatToPlayer(this.getPlacer(), sg);
					warnedSides[dir.ordinal()] = true;
				}
			}
			else {
				int slot = rand.nextInt(ii.getSizeInventory());
				ItemStack is = ii.getStackInSlot(slot);
				if (this.canRepair(is)) {
					this.repair(is);
					ret = EffectResult.ACTION;
				}
			}
		}
		return ret;
	}

	@Override
	protected void onAdjacentBlockUpdate() {
		warnedSides = new boolean[6];
	}

	private BaseRepairInterface getInterface(TileEntity te) {
		Class c = te.getClass();
		Class c2 = c;
		BaseRepairInterface e = interactions.get(c2);
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

	private static abstract class BaseRepairInterface extends SpecificAdjacencyEffect {

		protected BaseRepairInterface() {
			super(CrystalElement.MAGENTA);
		}

		protected abstract boolean runOnClient();

		protected abstract void init() throws Exception;

		public int getTickRand(int tier) {
			return 1;
		}

		@Override
		public final boolean isActive() {
			return this.getMod() == null || this.getMod().isLoaded();
		}

		protected abstract ModList getMod();

	}

	private static abstract class BlockRepairInterface extends BaseRepairInterface {

		protected abstract void tick(World world, int x, int y, int z, int tier) throws Exception;
	}

	private static class AnvilRepairInterface extends BlockRepairInterface {

		@Override
		protected void tick(World world, int x, int y, int z, int tier) throws Exception {
			int meta = world.getBlockMetadata(x, y, z);
			if (meta > 0 && rand.nextInt(200) < 1+tier*8) {
				world.setBlockMetadataWithNotify(x, y, z, meta-4, 3);
			}
		}

		@Override
		protected boolean runOnClient() {
			return false;
		}

		@Override
		protected void init() throws Exception {

		}

		@Override
		public String getDescription() {
			return "Repairs damage";
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
			li.add(new GuiStackDisplay(Blocks.anvil));
		}

		@Override
		protected ModList getMod() {
			return null;
		}

	}

	private static abstract class TileRepairInterface extends BaseRepairInterface {

		protected TileRepairInterface() {
			super();
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

		protected abstract String[] getClasses();

		protected TileEntity getActingTileEntity(TileEntity te) throws Exception {
			return te;
		}

		@Override
		public int getTickRand(int tier) {
			return 1;
		}
	}

	private static class NoInterface extends BaseRepairInterface { //Used for null

		private static final NoInterface instance = new NoInterface();

		@Override
		protected void init() throws Exception {

		}

		@Override
		protected boolean runOnClient() {
			return false;
		}

		@Override
		public String getDescription() {
			return "Does nothing";
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {

		}

		@Override
		protected ModList getMod() {
			return null;
		}

	}

	private static abstract class FieldSetRepairInterface extends TileRepairInterface {

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

	private static abstract class ItemSlotRepairInterface extends TileRepairInterface {

		@Override
		protected final void tick(TileEntity te, int tier) throws Exception {
			te = this.getActingTileEntity(te);
			IInventory ii = this.getInventory(te);
			if (ii != null) {
				for (int i = 0; i < ii.getSizeInventory(); i++) {
					if (this.repairSlot(i)) {
						ItemStack in = ii.getStackInSlot(0);
						if (in != null && this.shouldRepairItem(in)) {
							this.doRepairItem(in, tier);
						}
					}
				}
			}
		}

		public abstract boolean repairSlot(int slot);

		public abstract boolean shouldRepairItem(ItemStack is);

		public abstract void doRepairItem(ItemStack is, int tier);

		protected IInventory getInventory(TileEntity te) throws Exception {
			return te instanceof IInventory ? (IInventory)te : null;
		}

	}

	private static class RailTurbineInterface extends ItemSlotRepairInterface {

		private Field inventory; //ranges from 0 at 0.00% to 100k at 100% -> each 1000 is 1%

		@Override
		protected void init() throws Exception {
			Class c = Class.forName("mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine");
			inventory = c.getDeclaredField("inv");
			inventory.setAccessible(true);
		}

		@Override
		protected ModList getMod() {
			return ModList.RAILCRAFT;
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine"};
		}

		@Override
		protected IInventory getInventory(TileEntity te) throws Exception {
			return (IInventory)inventory.get(te);
		}

		@Override
		public int getTickRand(int tier) {
			return 100-tier*10;
		}

		@Override
		public boolean repairSlot(int slot) {
			return slot == 0;
		}

		@Override
		public boolean shouldRepairItem(ItemStack is) {
			return is.getItemDamage() > 0 && is.getItem() == GameRegistry.findItem(ModList.RAILCRAFT.modLabel, "part.turbine.rotor");
		}

		@Override
		public void doRepairItem(ItemStack is, int tier) {
			is.setItemDamage(Math.max(is.getItemDamage()-1, 0));
		}

		@Override
		protected TileEntity getActingTileEntity(TileEntity te) throws Exception {
			return MultiblockControllerFinder.instance.getController(te);
		}

		@Override
		protected boolean runOnClient() {
			return false;
		}

		@Override
		public String getDescription() {
			return "Repairs turbine rotors";
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
			li.add(new GuiStackDisplay("Railcraft:machine.alpha:1"));
		}

	}

	private static class DecalcificationInterface extends FieldSetRepairInterface {

		private Field calcification; //ranges from 0 at 0.00% to 100k at 100% -> each 1000 is 1%

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
			int current = calcification.getInt(te);
			double rem = current*this.getReductionFactor(tier);
			//ReikaJavaLibrary.pConsole(rem, Side.SERVER);
			if (rem >= 1 || ReikaRandomHelper.doWithChance(rem)) {
				int rem2 = (int)Math.max(1, rem);
				return Math.max(0, current-rem2);
			}
			else {
				return current;
			}
		}

		private double getReductionFactor(int tier) {
			return 1D-Math.pow(0.99997, 1+tier); //was 0.98, then 0.999, then 0.999997
		}

		@Override
		protected boolean runOnClient() {
			return false;
		}

		@Override
		public String getDescription() {
			return "Decalcification";
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
			li.add(new GuiStackDisplay("IC2:blockMachine3"));
		}

	}

	private static final class APIHealingBlock extends BlockRepairInterface {

		private final CustomBlockHealing effect;

		private APIHealingBlock(CustomBlockHealing acc) {
			effect = acc;
		}

		@Override
		public String getDescription() {
			return effect.getDescription();
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
			for (ItemStack is : effect.getItems())
				li.add(new GuiStackDisplay(is));
		}

		@Override
		protected boolean runOnClient() {
			return effect.runOnClient();
		}

		@Override
		protected void init() throws Exception {

		}

		@Override
		protected void tick(World world, int x, int y, int z, int tier) throws Exception {
			effect.tick(world, x, y, z, tier);
		}

		@Override
		protected ModList getMod() {
			return null;
		}

	}

	private static final class APIHealingTile extends TileRepairInterface {

		private final CustomTileHealing effect;

		private APIHealingTile(CustomTileHealing acc) {
			effect = acc;
		}

		@Override
		public String getDescription() {
			return effect.getDescription();
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
			for (ItemStack is : effect.getItems())
				li.add(new GuiStackDisplay(is));
		}

		@Override
		protected boolean runOnClient() {
			return effect.runOnClient();
		}

		@Override
		protected void init() throws Exception {

		}

		@Override
		protected void tick(TileEntity te, int tier) throws Exception {
			effect.tick(te, tier);
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

}
