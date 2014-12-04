package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
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
	private static HashMap<String, StructuredBlockArray> opaque = new HashMap();
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

	private void makeStructureCache(World world, int x, int y, int z, EntityPlayer ep) {
		StructuredBlockArray all = new StructuredBlockArray(world);
		StructuredBlockArray opq = new StructuredBlockArray(world);
		int r = 5;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (dy >= 0) {
						Block b = world.getBlock(dx, dy, dz);
						if (b != Blocks.air && b != Blocks.dirt && b != Blocks.grass && b != Blocks.bedrock) {
							all.addBlockCoordinate(dx, dy, dz);
							if (b.isOpaqueCube())
								opq.addBlockCoordinate(dx, dy, dz);
						}
					}
				}
			}
		}
		all.offset(-x, -y, -z);
		opq.offset(-x, -y, -z);
		structures.put(ep.getCommandSenderName(), all);
		opaque.put(ep.getCommandSenderName(), opq);
	}

	private void copyStructure(World world, int x, int y, int z, int s, EntityPlayer ep) {
		lock.add(ep.getCommandSenderName());
		triggerPlacement(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[s], ep);
		this.drainPlayer(ep);
	}

	private static void triggerPlacement(World world, int x, int y, int z, ForgeDirection dir, EntityPlayer ep) {
		//ticker.players.put(ep.getCommandSenderName(), new Coordinate(x, y, z).offset(dir, 1));
		StructuredBlockArray struct = structures.get(ep.getCommandSenderName());
		StructuredBlockArray opq = opaque.get(ep.getCommandSenderName());
		ArrayList<PositionedBlock> ls = new ArrayList();
		ArrayList<PositionedBlock> lo = new ArrayList();
		for (int i = 0; i < struct.getSize(); i++) {
			int[] xyz = struct.getNthBlock(i);
			int dx = xyz[0]+x+dir.offsetX;
			int dy = xyz[1]+y+dir.offsetY;
			int dz = xyz[2]+z+dir.offsetZ;
			ls.add(new PositionedBlock(dx, dy, dz, struct.getBlockAt(xyz[0], xyz[1], xyz[2]), struct.getMetaAt(xyz[0], xyz[1], xyz[2])));
		}

		for (int i = 0; i < opq.getSize(); i++) {
			int[] xyz = opq.getNthBlock(i);
			int dx = xyz[0]+x+dir.offsetX;
			int dy = xyz[1]+y+dir.offsetY;
			int dz = xyz[2]+z+dir.offsetZ;
			lo.add(new PositionedBlock(dx, dy, dz, opq.getBlockAt(xyz[0], xyz[1], xyz[2]), opq.getMetaAt(xyz[0], xyz[1], xyz[2])));
		}

		Collections.shuffle(ls);
		Collections.shuffle(lo);

		ArrayList sum = new ArrayList();
		sum.addAll(lo);
		sum.addAll(ls);

		ticker.placing.put(ep.getCommandSenderName(), new OperationList(sum));
	}

	private static void finishPlacement(String s) {
		lock.remove(s);
		structures.remove(s);
		opaque.remove(s);
	}

	public static StructuredBlockArray getStructureFor(EntityPlayer ep) {
		StructuredBlockArray a = structures.get(ep.getCommandSenderName());
		return !lock.contains(ep.getCommandSenderName()) && a != null ? (StructuredBlockArray)a.copy() : null;
	}

	public static class PlacementTicker implements TickHandler {

		//private HashMap<String, Coordinate> players = new HashMap();
		private HashMap<String, OperationList> placing = new HashMap();

		private PlacementTicker() {

		}

		@Override
		public void tick(TickType type, Object... tickData) {
			World world = (World)tickData[0];
			Collection<String> end = new ArrayList();
			for (String s : placing.keySet()) {/*
				Coordinate offset = players.get(s);
				StructuredBlockArray opq = opaque.get(s);
				StructuredBlockArray struct = structures.get(s);
				if (opq != null) {
					int[] xyz = opq.getRandomBlock();
					Block b = opq.getBlockAt(xyz[0], xyz[1], xyz[2]);
					if (b == Blocks.air)
						b = Blocks.brick_block;
					int meta = opq.getMetaAt(xyz[0], xyz[1], xyz[2]);
					if (!world.isRemote)
						offset.offset(xyz[0], xyz[1], xyz[2]).setBlock(world, b, meta);
					ReikaSoundHelper.playPlaceSound(world, xyz[0], xyz[1], xyz[2], b);
					//ReikaJavaLibrary.pConsole("placed "+b.getLocalizedName()+" @ "+offset.offset(xyz[0], xyz[1], xyz[2]));
					opq.remove(xyz[0], xyz[1], xyz[2]);
					struct.remove(xyz[0], xyz[1], xyz[2]);
					if (opq.isEmpty()) {
						opaque.remove(s);
						//ReikaJavaLibrary.pConsole("last placed "+b.getLocalizedName()+" @ "+offset.offset(xyz[0], xyz[1], xyz[2]));
					}
				}
				else {
					int[] xyz = struct.getRandomBlock();
					Block b = struct.getBlockAt(xyz[0], xyz[1], xyz[2]);
					int meta = struct.getMetaAt(xyz[0], xyz[1], xyz[2]);
					if (!world.isRemote)
						offset.offset(xyz[0], xyz[1], xyz[2]).setBlock(world, b, meta);
					ReikaSoundHelper.playPlaceSound(world, xyz[0], xyz[1], xyz[2], b);
					//ReikaJavaLibrary.pConsole("placed "+b.getLocalizedName()+" @ "+offset.offset(xyz[0], xyz[1], xyz[2]));
					struct.remove(xyz[0], xyz[1], xyz[2]);
					if (struct.isEmpty()) {
						//ReikaJavaLibrary.pConsole("last placed "+b.getLocalizedName()+" @ "+offset.offset(xyz[0], xyz[1], xyz[2]));
						structures.remove(s);
						finishPlacement(s);
						end.add(s);
					}
				}
				//ReikaJavaLibrary.pConsole((opq != null ? opq.getSize() : 0)+"+"+(struct != null ? struct.getSize() : 0));
			 */
				OperationList o = placing.get(s);
				ArrayList<PositionedBlock> li = o.list;
				for (int i = 0; i < 4; i++) {
					PositionedBlock b = li.get(o.index);
					b.place(world);
				}
				o.index++;
				if (o.isDone()) {
					end.add(s);
					finishPlacement(s);
				}
				//ReikaJavaLibrary.pConsole(li.size()+": last placed "+b);
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
			if (!world.isRemote)
				coord.setBlock(world, block, meta);
			ReikaSoundHelper.playPlaceSound(world, coord.xCoord, coord.yCoord, coord.zCoord, block);
		}

		@Override
		public String toString() {
			return "["+block.getLocalizedName()+"x"+block.hashCode()+"] > "+Block.getIdFromBlock(block)+":"+meta+" @ "+coord.toString();
		}

	}

	private static class OperationList {

		private final ArrayList<PositionedBlock> list;
		private int index = 0;

		private OperationList(ArrayList<PositionedBlock> li) {
			list = new ArrayList(li);
		}

		public boolean isDone() {
			return index >= list.size();
		}

	}

}
