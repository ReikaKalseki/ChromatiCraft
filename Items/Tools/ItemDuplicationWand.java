package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.TickRegistry;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Instantiable.Data.StructuredBlockArray;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

public class ItemDuplicationWand extends ItemWandBase {

	private static HashMap<String, StructuredBlockArray> structures = new HashMap();
	private static Collection<String> lock = new ArrayList();

	private static final PlacementTicker ticker = new PlacementTicker();

	public ItemDuplicationWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.BLACK, 4);
		this.addEnergyCost(CrystalElement.PURPLE, 4);
		this.addEnergyCost(CrystalElement.LIGHTBLUE, 2);
		TickRegistry.instance.registerTickHandler(ticker, Side.SERVER);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (lock.contains(ep.getCommandSenderName()))
			return false;
		if (this.sufficientEnergy(ep)) {
			if (ep.isSneaking())
				this.makeStructureCache(world, x, y, z, ep);
			else if (structures.containsKey(ep.getCommandSenderName()))
				this.copyStructure(world, x, y, z, s, ep);
			return true;
		}
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		this.finishPlacement(ep.getCommandSenderName());
		return is;
	}

	private void makeStructureCache(World world, int x, int y, int z, EntityPlayer ep) {
		StructuredBlockArray all = new StructuredBlockArray(world);
		int r = 5;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (dy >= 0) {
						Block b = world.getBlock(dx, dy, dz);
						all.addBlockCoordinate(dx, dy, dz);
					}
				}
			}
		}
		all.offset(-x, -y, -z);
		structures.put(ep.getCommandSenderName(), all);
	}

	private void copyStructure(World world, int x, int y, int z, int s, EntityPlayer ep) {
		lock.add(ep.getCommandSenderName());
		triggerPlacement(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[s], ep);
		this.drainPlayer(ep, 1+structures.get(ep.getCommandSenderName()).getSize()/16F);
	}

	private static void triggerPlacement(World world, int x, int y, int z, ForgeDirection dir, EntityPlayer ep) {
		StructuredBlockArray struct = structures.get(ep.getCommandSenderName());
		ArrayList<PositionedBlock> ls = new ArrayList();
		for (int i = 0; i < struct.getSize(); i++) {
			int[] xyz = struct.getNthBlock(i);
			int dx = xyz[0]+x+dir.offsetX;
			int dy = xyz[1]+y+dir.offsetY;
			int dz = xyz[2]+z+dir.offsetZ;
			ls.add(new PositionedBlock(dx, dy, dz, struct.getBlockAt(xyz[0], xyz[1], xyz[2]), struct.getMetaAt(xyz[0], xyz[1], xyz[2])));
		}

		ticker.placing.put(ep.getCommandSenderName(), new OperationList(world, ls));
	}

	private static void finishPlacement(String s) {
		lock.remove(s);
		structures.remove(s);
	}

	public static StructuredBlockArray getStructureFor(EntityPlayer ep) {
		StructuredBlockArray a = structures.get(ep.getCommandSenderName());
		return !lock.contains(ep.getCommandSenderName()) && a != null ? (StructuredBlockArray)a.copy() : null;
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
					Block bf = b.coord.getBlock(world);
					int mf = b.coord.getBlockMetadata(world);
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
		public TickType getType() {
			return TickType.WORLD;
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
			if (block == Blocks.air || block instanceof BlockAir)
				ReikaSoundHelper.playBreakSound(world, coord.xCoord, coord.yCoord, coord.zCoord, coord.getBlock(world));
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
			if (b instanceof BlockFarmland || b instanceof BlockSlab || b == Blocks.glass || b instanceof BlockLeavesBase)
				return 1;
			if (b instanceof BlockBush || b.getMaterial() == Material.cactus || b.getMaterial() == Material.plants)
				return 2;
			if (b.getMaterial() == Material.circuits || b.getMaterial() == Material.carpet || b.getMaterial() == Material.portal)
				return 2;
			if (b.getMaterial() == Material.vine || b.getMaterial() == Material.web || b.getMaterial() == Material.snow)
				return 2;
			if (b == Blocks.torch || b == Blocks.redstone_torch || b == Blocks.unlit_redstone_torch)
				return 2;
			if (b instanceof BlockLiquid || b instanceof BlockFluidBase || b.getMaterial() == Material.water || b.getMaterial() == Material.lava)
				return 3;
			if (b == Blocks.air || b.getMaterial() == Material.air || b instanceof BlockAir)
				return 4;
			return 0;
		}

	}

}
