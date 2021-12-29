/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ASM.DependentMethodStripper.ClassDependent;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockSpiral;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Interfaces.Block.MachineRegistryBlock;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

import buildcraft.api.core.IAreaProvider;


public class TileEntityAreaBreaker extends ChargedCrystalPowered implements BreakAction, ItemOnRightClick {

	public static final int MAX_ACTIVE_BREAKERS = 4;
	public static final float TICKS_PER_HARDNESS = 5F;

	public static final int MAX_RANGE = 16;

	private final BlockSpiral[] area = new BlockSpiral[MAX_RANGE];
	private final HashMap<Coordinate, ImmutablePair<Integer, Integer>> breakLocs = new HashMap();
	private final MultiMap<Coordinate, LightningBolt> bolts = new MultiMap();

	private int newloctick = 0;
	private int activeIndex = 0;
	private BreakShape shape = BreakShape.CUBOID;
	private BlockBox areaOverride = null;;
	private int range = MAX_RANGE;

	private static final ElementTagCompound required = new ElementTagCompound();

	static {
		required.addTag(CrystalElement.YELLOW, 50);
		required.addTag(CrystalElement.LIGHTGRAY, 20);
		required.addTag(CrystalElement.PURPLE, 10);
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.AREABREAKER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.isRunning()) {
			EntityPlayer ep = this.getPlacer();
			if (newloctick == 0) {
				if (breakLocs.size() < Math.max(1, Math.min(MAX_ACTIVE_BREAKERS, area[activeIndex].getSize()/16))) {
					Coordinate c = area[activeIndex].getNextAndMoveOn();
					Block b = c != null ? c.getBlock(world) : Blocks.air;
					while (!area[activeIndex].isEmpty() && !this.isCoordValid(c, b, world, x, y, z)) {
						c = area[activeIndex].getNextAndMoveOn();
						b = c != null ? c.getBlock(world) : Blocks.air;
					}
					if (this.isCoordValid(c, b, world, x, y, z)) {
						newloctick = 4;
						float h = b.getBlockHardness(world, c.xCoord, c.yCoord, c.zCoord);
						if (h >= 0) {
							int t = Math.max((int)(TICKS_PER_HARDNESS*h), 4);
							//ReikaJavaLibrary.pConsole(b+">"+h+">"+t);
							breakLocs.put(c, new ImmutablePair(t, t));
							this.createBolts(c);
						}
					}
					if (activeIndex < area.length-1 && area[activeIndex].isEmpty()) {
						activeIndex++;
					}
				}
			}
			else {
				newloctick--;
			}
			Map<Coordinate, ImmutablePair<Integer, Integer>> put = new HashMap();
			for (Coordinate c : breakLocs.keySet()) {
				if (this.hasEnergy(required)) {
					ImmutablePair<Integer, Integer> get = breakLocs.get(c);
					c.destroyBlockPartially(world, 10-10D*get.left/get.right);
					if (get.left > 1) {
						put.put(c, new ImmutablePair(get.left-1, get.right));
						this.useEnergy(required.copy().scale(this.getEnergyCostScale()*0.25F));
						Collection<LightningBolt> li = bolts.get(c);
						for (LightningBolt b : li)
							b.update();
					}
					else {
						this.useEnergy(required.copy().scale(this.getEnergyCostScale()));
						this.breakBlock(world, c.xCoord, c.yCoord, c.zCoord, ep);
						bolts.remove(c);
					}
				}
			}
			breakLocs.clear();
			breakLocs.putAll(put);
		}
	}

	private boolean isRunning() {
		return this.hasRedstoneSignal();
	}

	private boolean isCoordValid(Coordinate c, Block b, World world, int x, int y, int z) {
		return c != null && this.shouldTryBreaking(world, c.xCoord, c.yCoord, c.zCoord, b) && !c.equals(x, y, z) && c.getTileEntity(world) == null && (areaOverride != null ? areaOverride.isBlockInside(c) : shape.isBlockInShape(c.xCoord-x, c.yCoord-y, c.zCoord-z, range));
	}

	private boolean shouldTryBreaking(World world, int x, int y, int z, Block b) {
		return !b.isAir(world, x, y, z) && !ReikaBlockHelper.isLiquid(b);
	}

	private void createBolts(Coordinate c) {
		for (int i = 0; i < 3; i++) {
			LightningBolt b = new LightningBolt(new DecimalPosition(this), new DecimalPosition(c), 8);
			b.setVariance(0.25);
			b.setVelocity(0.0625);
			bolts.addValue(c, b);
		}
	}

	public Map<Coordinate, ImmutablePair<Integer, Integer>> getBreakLocs() {
		return Collections.unmodifiableMap(breakLocs);
	}

	public Collection<LightningBolt> getBolts(Coordinate c) {
		return bolts.get(c);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.getCoordsFromIAP(world, x, y, z);
		this.initArea();
	}

	private void initArea() {
		for (int i = 0; i < area.length; i++) {
			area[i] = new BlockSpiral(xCoord, yCoord+i, zCoord, range).setRightHanded().setGridSize(1).calculate();
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private void breakBlock(World world, int x, int y, int z, EntityPlayer ep) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (world.isRemote) {
			ReikaRenderHelper.spawnDropParticles(world, x, y, z, b, meta);
		}
		else {
			this.dropItems(world, x, y, z, b, meta, ep);
		}
		ReikaSoundHelper.playBreakSound(world, x, y, z, b);
		world.setBlock(x, y, z, Blocks.air);
	}

	private void dropItems(World world, int x, int y, int z, Block b, int meta, EntityPlayer ep) {
		Collection<ItemStack> items = null;
		if (b instanceof BlockTieredResource) {
			BlockTieredResource bt = (BlockTieredResource)b;
			boolean harvest = ep != null && bt.isPlayerSufficientTier(world, x, y, z, ep);
			items = harvest ? bt.getHarvestResources(world, x, y, z, 0, ep) : bt.getNoHarvestResources(world, x, y, z, 0, ep);
		}
		else if (b instanceof MachineRegistryBlock) {
			items = ReikaJavaLibrary.makeListFrom(((MachineRegistryBlock)b).getMachine(world, x, y, z).getCraftedProduct());
		}
		else {
			items = ReikaJavaLibrary.makeListFrom(ReikaBlockHelper.getSilkTouch(world, x, y, z, b, meta, this.getPlacer(), false));
		}
		if (items != null) {
			for (ItemStack is : items) {
				ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
			}
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		//breakLocs.clear();
		//breakLocs.putAll(ReikaNBTHelper.readMapFromNBT(NBT, "locs"));

		shape = BreakShape.list[NBT.getInteger("shape")];
		if (NBT.hasKey("override")) {
			NBTTagCompound tag = NBT.getCompoundTag("override");
			areaOverride = BlockBox.readFromNBT(tag);
		}
		range = NBT.getInteger("range");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		//ReikaNBTHelper.writeMapToNBT(NBT, "locs", breakLocs);

		if (areaOverride != null) {
			NBTTagCompound tag = new NBTTagCompound();
			areaOverride.writeToNBT(tag);
			NBT.setTag("override", tag);
		}
		NBT.setInteger("shape", shape.ordinal());
		NBT.setInteger("range", range);
	}

	@Override
	public void breakBlock() {
		for (Coordinate c : breakLocs.keySet()) {
			c.destroyBlockPartially(worldObj, -1);
		}
	}

	public BreakShape getShape() {
		return shape;
	}

	public void cycleShape() {
		this.initArea();
		shape = shape.next();
		ChromaSounds.USE.playSoundAtBlock(this);
	}

	public void incRange() {
		range++;
		if (range > MAX_RANGE)
			range = 1;
		this.initArea();
		ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.NUMBERPARTICLE.ordinal(), this, 512, range);
		ChromaSounds.USE.playSoundAtBlock(this);
	}

	public int getRange() {
		return range;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public float getCostModifier() {
		return 1;
	}

	@Override
	public boolean usesColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	protected boolean canExtractOtherItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	protected boolean isItemValidForOtherSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public ItemStack onRightClickWith(ItemStack item, EntityPlayer ep) {
		if (item == null) {
			ItemStack is = inv[0];
			inv[0] = null;
			return is;
		}
		else if (this.isItemValidForSlot(0, item)) {
			inv[0] = item;
			return null;
		}
		return item;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.isRunning() ? ReikaAABBHelper.getBlockAABB(this).expand(MAX_RANGE, MAX_RANGE, MAX_RANGE) : super.getRenderBoundingBox();
	}

	private void getCoordsFromIAP(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			TileEntity te = world.getTileEntity(dx, dy, dz);
			if (InterfaceCache.AREAPROVIDER.instanceOf(te)) {
				this.readIAP(te);
				this.syncAllData(false);
				return;
			}
		}
	}

	@ClassDependent("buildcraft.api.core.IAreaProvider")
	private void readIAP(TileEntity te) {
		IAreaProvider iap = (IAreaProvider)te;
		areaOverride = BlockBox.getFromIAP(iap);
		range = areaOverride.getLongestEdge()+2;
		iap.removeFromWorld();
	}

	public static enum BreakShape {

		CUBOID(),
		HEMISPHERE(),
		CYLINDER(),
		DIAMONDPRISM(),
		PYRAMID(),
		HEXAGON(),
		OCTAGON();

		private static final BreakShape[] list = values();

		public boolean isBlockInShape(int dx, int dy, int dz, int r) {
			r = this.getRangeForHeight(dy, r);
			switch(this) {
				case CUBOID:
					return Math.abs(dx) <= r && Math.abs(dy) <= r && Math.abs(dz) <= r;
				case HEMISPHERE:
					return ReikaMathLibrary.py3d(dx, dy, dz) <= r+0.5;
				case CYLINDER:
					return ReikaMathLibrary.py3d(dx, 0, dz) <= r+0.5;
				case PYRAMID:
					return new Coordinate(dx, dy, dz).getTaxicabDistanceTo(new Coordinate(0, 0, 0)) <= r;
				case DIAMONDPRISM:
					return new Coordinate(dx, 0, dz).getTaxicabDistanceTo(new Coordinate(0, 0, 0)) <= r;
				case HEXAGON:
					return ReikaMathLibrary.isPointInsidePolygon(dx, dz, 6, r);
				case OCTAGON:
					return ReikaMathLibrary.isPointInsidePolygon(dx, dz, 8, r);
			}
			return false;
		}

		public BreakShape next() {
			return list[(this.ordinal()+1)%list.length];
		}

		private int getRangeForHeight(int i, int r) {
			if (this == DIAMONDPRISM || this == CYLINDER)
				return r;
			if (this == PYRAMID)
				return r-i;
			return i <= 3 ? r : r+3-(i < 8 ? i : 8+(i-8)*2);
		}

		public void renderPreview(Tessellator v5, double s) {
			switch(this) {
				case CUBOID:
					v5.addVertex(-s, -s, -s);
					v5.addVertex(s, -s, -s);

					v5.addVertex(s, -s, -s);
					v5.addVertex(s, -s, s);

					v5.addVertex(s, -s, s);
					v5.addVertex(-s, -s, s);

					v5.addVertex(-s, -s, s);
					v5.addVertex(-s, -s, -s);

					v5.addVertex(-s, s, -s);
					v5.addVertex(s, s, -s);

					v5.addVertex(s, s, -s);
					v5.addVertex(s, s, s);

					v5.addVertex(s, s, s);
					v5.addVertex(-s, s, s);

					v5.addVertex(-s, s, s);
					v5.addVertex(-s, s, -s);

					v5.addVertex(-s, -s, -s);
					v5.addVertex(-s, s, -s);

					v5.addVertex(s, -s, -s);
					v5.addVertex(s, s, -s);

					v5.addVertex(-s, -s, s);
					v5.addVertex(-s, s, s);

					v5.addVertex(s, -s, s);
					v5.addVertex(s, s, s);
					break;
				case HEMISPHERE:
					for (double s2 = 0; s2 <= s; s2 += s/8) {
						for (double a = 0; a <= 350; a += 10) {
							double a1 = Math.toRadians(a);
							double a2 = Math.toRadians(a+10);
							double r = 0.75*Math.sqrt(s-s2);
							double x1 = r*Math.cos(a1);
							double z1 = r*Math.sin(a1);
							double x2 = r*Math.cos(a2);
							double z2 = r*Math.sin(a2);
							v5.addVertex(x1, s2, z1);
							v5.addVertex(x2, s2, z2);
						}
					}
					break;
				case CYLINDER:
					for (double a = 0; a <= 350; a += 10) {
						double a1 = Math.toRadians(a);
						double a2 = Math.toRadians(a+10);
						double x1 = s*Math.cos(a1);
						double z1 = s*Math.sin(a1);
						double x2 = s*Math.cos(a2);
						double z2 = s*Math.sin(a2);

						v5.addVertex(x1, -s, z1);
						v5.addVertex(x2, -s, z2);

						v5.addVertex(x1, s, z1);
						v5.addVertex(x2, s, z2);

						v5.addVertex(x1, -s, z1);
						v5.addVertex(0, -s, 0);

						v5.addVertex(x1, s, z1);
						v5.addVertex(0, s, 0);

						v5.addVertex(x1, -s, z1);
						v5.addVertex(x1, s, z1);
					}
					break;
				case DIAMONDPRISM:
					v5.addVertex(s, -s, 0);
					v5.addVertex(0, -s, s);

					v5.addVertex(0, -s, s);
					v5.addVertex(-s, -s, 0);

					v5.addVertex(-s, -s, 0);
					v5.addVertex(0, -s, -s);

					v5.addVertex(0, -s, -s);
					v5.addVertex(s, -s, 0);

					v5.addVertex(s, s, 0);
					v5.addVertex(0, s, s);

					v5.addVertex(0, s, s);
					v5.addVertex(-s, s, 0);

					v5.addVertex(-s, s, 0);
					v5.addVertex(0, s, -s);

					v5.addVertex(0, s, -s);
					v5.addVertex(s, s, 0);

					v5.addVertex(s, -s, 0);
					v5.addVertex(s, s, 0);

					v5.addVertex(0, -s, s);
					v5.addVertex(0, s, s);

					v5.addVertex(-s, -s, 0);
					v5.addVertex(-s, s, 0);

					v5.addVertex(0, -s, -s);
					v5.addVertex(0, s, -s);
					break;
				case PYRAMID:
					v5.addVertex(0, -s, s);
					v5.addVertex(0, -s, -s);

					v5.addVertex(-s, -s, 0);
					v5.addVertex(s, -s, 0);

					v5.addVertex(s, -s, 0);
					v5.addVertex(0, s, 0);

					v5.addVertex(-s, -s, 0);
					v5.addVertex(0, s, 0);

					v5.addVertex(0, -s, s);
					v5.addVertex(0, s, 0);

					v5.addVertex(0, -s, -s);
					v5.addVertex(0, s, 0);

					v5.addVertex(s, -s, 0);
					v5.addVertex(0, -s, s);

					v5.addVertex(0, -s, s);
					v5.addVertex(-s, -s, 0);

					v5.addVertex(-s, -s, 0);
					v5.addVertex(0, -s, -s);

					v5.addVertex(0, -s, -s);
					v5.addVertex(s, -s, 0);
					break;
				case HEXAGON:
					for (int i = 0; i < 6; i++) {
						double a = Math.toRadians(30+i*60);
						double a2 = Math.toRadians(30+(i+1)*60);
						double dx = s*Math.cos(a);
						double dz = s*Math.sin(a);
						double dx2 = s*Math.cos(a2);
						double dz2 = s*Math.sin(a2);
						v5.addVertex(dx, -s, dz);
						v5.addVertex(dx, s, dz);

						v5.addVertex(dx, -s, dz);
						v5.addVertex(dx2, -s, dz2);

						v5.addVertex(dx, s, dz);
						v5.addVertex(dx2, s, dz2);
					}
					break;
				case OCTAGON:
					for (int i = 0; i < 8; i++) {
						double a = Math.toRadians(22.5+i*45);
						double a2 = Math.toRadians(22.5+(i+1)*45);
						double dx = s*Math.cos(a);
						double dz = s*Math.sin(a);
						double dx2 = s*Math.cos(a2);
						double dz2 = s*Math.sin(a2);
						v5.addVertex(dx, -s, dz);
						v5.addVertex(dx, s, dz);

						v5.addVertex(dx, -s, dz);
						v5.addVertex(dx2, -s, dz2);

						v5.addVertex(dx, s, dz);
						v5.addVertex(dx2, s, dz2);
					}
					break;
				default:
					break;
			}
		}

	}

}
