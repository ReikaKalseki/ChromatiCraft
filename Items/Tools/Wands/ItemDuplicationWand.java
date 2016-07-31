/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Wands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.UnCopyableBlock;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaCropHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.RotaryCraft.Registry.BlockRegistry;
import Reika.RotaryCraft.Registry.ItemRegistry;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ItemDuplicationWand extends ItemWandBase {

	public static final int MAX_SIZE = Math.min(1000, ChromaOptions.COPYSIZE.getValue());

	//Do not switch to playermap; not compatible, and not persistent across loads anyways
	private static HashMap<String, StructuredBlockArray> structures = new HashMap();
	private HashMap<String, Boolean> region = new HashMap();
	private HashMap<String, Coordinate> centers = new HashMap();
	private HashMap<String, Coordinate> lastClick = new HashMap();
	private static Collection<String> lock = new ArrayList();

	private static final BlockMap<ItemStack> specialReqs = new BlockMap();

	private static final PlacementTicker ticker = new PlacementTicker();

	public ItemDuplicationWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.BLACK, 4);
		this.addEnergyCost(CrystalElement.PURPLE, 2);
		this.addEnergyCost(CrystalElement.LIGHTBLUE, 4);
		TickRegistry.instance.registerTickHandler(ticker);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		String sg = ep.getCommandSenderName();
		if (world.isRemote)
			return true;
		if (lock.contains(sg))
			return false;
		if (this.sufficientEnergy(ep)) {
			if (ep.isSneaking())
				this.addToStructureCache(world, x, y, z, sg);
			else {
				StructuredBlockArray struct = structures.get(sg);
				if (struct != null && !struct.isEmpty() && this.hasItems(struct, ep)) {
					this.copyStructure(world, x, y, z, s, sg);
					if (!ep.capabilities.isCreativeMode) {
						this.drainPlayer(ep, 1+struct.getSize()/16F);
						this.removeFromInventory(ep, struct);
					}
				}
			}
			return true;
		}
		return false;
	}

	private boolean hasItems(StructuredBlockArray blocks, EntityPlayer ep) {
		if (ep.capabilities.isCreativeMode)
			return true;
		ItemHashMap<Integer> items = blocks.getItems();
		this.performMappings(items);
		return ReikaInventoryHelper.inventoryContains(items, ep.inventory);
	}

	private void removeFromInventory(EntityPlayer ep, StructuredBlockArray blocks) {
		ItemHashMap<Integer> items = blocks.getItems();
		this.performMappings(items);
		//if (!ep.worldObj.isRemote && Chromabilities.MEINV.enabledOn(ep)) {
		//	AbilityHelper.instance.removeFromPlayerME(ep, items);
		//}
		//else {
		ReikaInventoryHelper.removeFromInventory(items, ep.inventory);
		//}
	}

	private void performMappings(ItemHashMap<Integer> items) {
		HashMap<BlockKey, Fluid> fluids = new HashMap();
		for (BlockKey bk : specialReqs.keySet()) {
			ItemStack is = bk.asItemStack();
			Integer get = items.get(is);
			if (get != null) {
				items.remove(is);
				ItemStack is2 = specialReqs.get(bk);
				items.put(is2, get);
			}
		}
		for (ItemStack is : items.keySet()) {
			Block b = Block.getBlockFromItem(is.getItem());
			if (b instanceof BlockFluidBase || b instanceof BlockLiquid) {
				Fluid f = FluidRegistry.lookupFluidForBlock(b);
				if (f != null) {
					fluids.put(new BlockKey(b, 0), f);
				}
			}
		}
		for (BlockKey bk : fluids.keySet()) {
			Fluid f = fluids.get(bk);
			ItemStack is = bk.asItemStack();
			Integer get = items.get(is);
			items.remove(is);
			if (!ReikaFluidHelper.isInfinite(f)) {
				ArrayList<ItemStack> fill = ReikaFluidHelper.getAllContainersFor(f);
				if (!fill.isEmpty()) {
					items.put(fill.get(0), get);
				}
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		this.finishPlacement(ep.getCommandSenderName());
		this.clearStructureCache(ep.getCommandSenderName());
		return is;
	}

	private void clearStructureCache(String s) {
		region.remove(s);
		centers.remove(s);
	}

	private void addToStructureCache(World world, int x, int y, int z, String s) {
		StructuredBlockArray all = structures.get(s);
		Coordinate cx = new Coordinate(x, y, z);
		boolean second = region.containsKey(s) && region.get(s);
		if (all == null) {
			all = new StructuredBlockArray(world);
			if (!second)
				centers.put(s, cx);
		}

		if (second) {
			Coordinate last = lastClick.get(s);
			StructuredBlockArray add = new StructuredBlockArray(world);
			int x1 = Math.min(last.xCoord, x);
			int x2 = Math.max(last.xCoord, x);
			int y1 = Math.min(last.yCoord, y);
			int y2 = Math.max(last.yCoord, y);
			int z1 = Math.min(last.zCoord, z);
			int z2 = Math.max(last.zCoord, z);
			int ct = 0;
			for (int dx = x1; dx <= x2; dx++) {
				for (int dy = y1; dy <= y2; dy++) {
					for (int dz = z1; dz <= z2; dz++) {
						if (dy >= 0) {
							Block b = world.getBlock(dx, dy, dz);
							int meta = world.getBlockMetadata(dx, dy, dz);
							if (b instanceof UnCopyableBlock && ((UnCopyableBlock)b).disallowCopy(meta)) {

							}
							else if (!ChromaOptions.COPYTILE.getState() && world.getTileEntity(dx, dy, dz) instanceof TileEntityBase) {

							}
							else if ((b instanceof BlockFluidBase || b instanceof BlockLiquid) && !ReikaFluidHelper.isInfinite(FluidRegistry.lookupFluidForBlock(b))) {

							}
							else {
								add.addBlockCoordinate(dx, dy, dz);
								ct++;
							}
						}
					}
				}
			}
			if (add.getSize() < MAX_SIZE || (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment())) {
				Coordinate c = centers.get(s);
				add.offset(-c.xCoord, -c.yCoord, -c.zCoord);
				add.copyTo(all);
				structures.put(s, all);
				ChromatiCraft.logger.debug("Added "+ct+" blocks to region, from "+cx+" to "+last+", for "+s+". Now has "+all.getSize()+" blocks.");
				region.put(s, false);
				lastClick.remove(s);
			}
		}
		else {
			ChromatiCraft.logger.debug("Started drawing subregion for "+s);
			lastClick.put(s, cx);
			region.put(s, true);
		}
	}

	private void copyStructure(World world, int x, int y, int z, int s, String sg) {
		lock.add(sg);
		triggerPlacement(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[s], sg);
	}

	private static void triggerPlacement(World world, int x, int y, int z, ForgeDirection dir, String s) {
		StructuredBlockArray struct = structures.get(s);
		ArrayList<PositionedBlock> ls = new ArrayList();
		for (int i = 0; i < struct.getSize(); i++) {
			Coordinate c = struct.getNthBlock(i);
			int dx = c.xCoord+x+dir.offsetX;
			int dy = c.yCoord+y+dir.offsetY;
			int dz = c.zCoord+z+dir.offsetZ;
			ls.add(new PositionedBlock(dx, dy, dz, struct.getBlockAt(c.xCoord, c.yCoord, c.zCoord), struct.getMetaAt(c.xCoord, c.yCoord, c.zCoord)));
		}

		ticker.placing.put(s, new OperationList(world, ls));
	}

	private static void finishPlacement(String s) {
		lock.remove(s);
		structures.remove(s);
		ticker.placing.remove(s);
	}

	public static StructuredBlockArray getStructureFor(EntityPlayer ep) {
		String s = ep.getCommandSenderName();
		StructuredBlockArray a = structures.get(s);
		return !lock.contains(s) && a != null ? (StructuredBlockArray)a.copy() : null;
	}

	public static void loadMappings() {
		addMapping(Blocks.farmland, Blocks.dirt);

		for (int i = 0; i < ReikaCropHelper.cropList.length; i++) {
			ReikaCropHelper crop = ReikaCropHelper.cropList[i];
			addMapping(crop.blockID, crop.getSeedItem());
		}

		for (int i = 0; i < 16; i++) {
			addMapping(ChromaBlocks.PLANT.getBlockInstance(), i, ChromaItems.SEED.getStackOfMetadata(i));
		}

		if (ModList.ROTARYCRAFT.isLoaded()) {
			addMapping(BlockRegistry.CANOLA.getBlockInstance(), ItemRegistry.CANOLA.getItemInstance());
		}

		addMapping(Blocks.reeds, Items.reeds);
		addMapping(Blocks.pumpkin_stem, Items.pumpkin_seeds);
		addMapping(Blocks.melon_stem, Items.melon_seeds);

		addMapping(Blocks.monster_egg, 0, Blocks.stone);
		addMapping(Blocks.monster_egg, 1, Blocks.cobblestone);
		addMapping(Blocks.monster_egg, 2, Blocks.stonebrick);
		addMapping(Blocks.monster_egg, 3, ReikaItemHelper.mossyBricks);
		addMapping(Blocks.monster_egg, 4, ReikaItemHelper.crackBricks);
		addMapping(Blocks.monster_egg, 5, ReikaItemHelper.circleBricks);

		addMapping(Blocks.lit_redstone_ore, Blocks.redstone_ore);
		addMapping(Blocks.unlit_redstone_torch, Blocks.redstone_torch);
		addMapping(Blocks.unpowered_repeater, Items.repeater);
		addMapping(Blocks.powered_repeater, Items.repeater);
		addMapping(Blocks.unpowered_comparator, Items.comparator);
		addMapping(Blocks.powered_comparator, Items.comparator);

		addMapping(Blocks.brewing_stand, Items.brewing_stand);
		addMapping(Blocks.cauldron, Items.cauldron);
		addMapping(Blocks.wall_sign, Items.sign);
		addMapping(Blocks.standing_sign, Items.sign);
		addMapping(Blocks.wooden_door, Items.wooden_door);
		addMapping(Blocks.iron_door, Items.iron_door);
		addMapping(Blocks.lit_furnace, Blocks.furnace);
		addMapping(Blocks.flower_pot, Items.flower_pot);
		addMapping(Blocks.tripwire, Items.string);

		for (int i = 0; i < 3; i++)
			addMapping(Blocks.skull, i, new ItemStack(Items.skull, 1, i));

	}

	private static void addMapping(Block b, int m, Block i) {
		addMapping(b, m, new ItemStack(i));
	}

	private static void addMapping(Block b, Block i) {
		addMapping(b, 0, new ItemStack(i));
	}

	private static void addMapping(Block b, int m, Item i) {
		addMapping(b, m, new ItemStack(i));
	}

	private static void addMapping(Block b, Item i) {
		addMapping(b, 0, new ItemStack(i));
	}

	private static void addMapping(Block b, ItemStack is) {
		addMapping(b, 0, is);
	}

	private static void addMapping(Block b, int m, ItemStack is) {
		specialReqs.put(b, m, is);
	}

	public static class PlacementTicker implements TickHandler {

		private HashMap<String, OperationList> placing = new HashMap();

		private PlacementTicker() {

		}

		@Override
		public void tick(TickType type, Object... tickData) {
			World world = (World)tickData[0];
			Collection<String> end = new ArrayList();
			for (String s : placing.keySet()) {
				OperationList o = placing.get(s);
				if (o.dimension == world.provider.dimensionId) {
					ArrayList<PositionedBlock> li = o.list;
					PositionedBlock b = li.get(o.index);
					//Block bf = b.coord.getBlock(world);
					//int mf = b.coord.getBlockMetadata(world);
					b.place(world);
					o.index++;
					if (o.isDone()) {
						end.add(s);
						finishPlacement(s);
					}
					//ReikaJavaLibrary.pConsole(o.index+"/"+li.size()+": last placed "+b+", overwriting "+Block.getIdFromBlock(bf)+":"+mf);
				}
			}
			for (String s : end)
				placing.remove(s);
		}

		@Override
		public EnumSet<TickType> getType() {
			return EnumSet.of(TickType.WORLD);
		}

		@Override
		public boolean canFire(Phase p) {
			return p == Phase.START;
		}

		@Override
		public String getLabel() {
			return "Duplicator";
		}

	}

	private static class PositionedBlock {

		private final Coordinate coord;
		private final Block block;
		private final int meta;

		private PositionedBlock(int x, int y, int z, Block b, int m) {
			this(new Coordinate(x, y, z), b, m);
		}

		private PositionedBlock(Coordinate c, Block b, int m) {
			coord = c;
			block = b;
			meta = m;
		}

		private void place(World world) {
			if (block == Blocks.air || block instanceof BlockAir) {
				Block b2 = coord.getBlock(world);
				if (b2 == Blocks.air) {
					ChromaSounds.ERROR.playSoundAtBlock(world, coord.xCoord, coord.yCoord, coord.zCoord, 0.25F, 2F);
				}
				else
					ReikaSoundHelper.playBreakSound(world, coord.xCoord, coord.yCoord, coord.zCoord, b2);
			}
			else
				ReikaSoundHelper.playPlaceSound(world, coord.xCoord, coord.yCoord, coord.zCoord, block);

			if (!world.isRemote)
				coord.setBlock(world, block, meta);
		}

		@Override
		public String toString() {
			return /*"["+block.getLocalizedName()+"x"+block.hashCode()+"] > "+*/Block.getIdFromBlock(block)+":"+meta+" @ "+coord.toString();
		}

	}

	private static class OperationList {

		private static final PrimacySorter sorter = new PrimacySorter();

		private final ArrayList<PositionedBlock> list;
		private int index = 0;
		private final int dimension;

		private OperationList(World world, ArrayList<PositionedBlock> li) {
			list = new ArrayList(li);
			dimension = world.provider.dimensionId;
			Collections.shuffle(list);
			Collections.sort(list, sorter);
		}

		public boolean isDone() {
			return index >= list.size();
		}

	}

	private static class PrimacySorter implements Comparator<PositionedBlock> {

		/** General order: <ol>
		<li>Opaque</li>
		<li>Farmland, Slabs, etc</li>
		<li>Popoff blocks like torches and crops</li>
		<li>Liquids</li>
		<li>Air</li>
		</ol> */
		@Override
		public int compare(PositionedBlock o1, PositionedBlock o2) {
			return getIndex(o1.block)-getIndex(o2.block);
		}

		private static int getIndex(Block b) {
			if (b.isOpaqueCube())
				return 0;
			if (b instanceof BlockFarmland || b instanceof BlockSlab || b instanceof BlockGlass)
				return 1;
			if (b instanceof BlockLeavesBase || b instanceof BlockStairs)
				return 1;
			if (b instanceof BlockBush || b.getMaterial() == Material.cactus || b.getMaterial() == Material.plants)
				return 2;
			if (b.getMaterial() == Material.circuits || b.getMaterial() == Material.carpet || b.getMaterial() == Material.portal)
				return 2;
			if (b.getMaterial() == Material.vine || b.getMaterial() == Material.web || b.getMaterial() == Material.snow)
				return 2;
			if (b == Blocks.torch || b == Blocks.redstone_torch || b == Blocks.unlit_redstone_torch || b instanceof BlockDoor)
				return 2;
			if (b instanceof BlockLiquid || b instanceof BlockFluidBase || b.getMaterial() == Material.water || b.getMaterial() == Material.lava)
				return 3;
			if (b == Blocks.air || b.getMaterial() == Material.air || b instanceof BlockAir)
				return 4;
			return 0;
		}

	}

}
