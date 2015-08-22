/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.StructurePair;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityLocusPoint;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Magic.ElementMixer;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.PlayerBreakHook;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Structure core, does FX and things
public class TileEntityDimensionCore extends TileEntityLocusPoint implements NBTTile, PlayerBreakHook {

	private CrystalElement color = CrystalElement.WHITE;
	private DimensionStructureType structure = null;
	private boolean triggered = false;
	private int soundPitch;

	private HashSet<UUID> sentPlayers = new HashSet();

	private static final EnumMap<CrystalElement, Coordinate> locations = new EnumMap(CrystalElement.class);
	private static final EnumMap<CrystalElement, HashSet<CrystalElement>> beams = new EnumMap(CrystalElement.class);


	static {
		addColor(CrystalElement.BLACK, -3, -1, -3);
		addColor(CrystalElement.RED, 0, -2, -7);
		addColor(CrystalElement.GREEN, 5, -2, 5);
		addColor(CrystalElement.BROWN, 0, -1, -4);
		addColor(CrystalElement.BLUE, -7, -2, 0);
		addColor(CrystalElement.PURPLE, 7, -2, 0);
		addColor(CrystalElement.CYAN, -5, -2, 5);
		addColor(CrystalElement.LIGHTGRAY, 3, -1, -3);
		addColor(CrystalElement.GRAY, -3, -1, 3);
		addColor(CrystalElement.PINK, -4, -1, 0);
		addColor(CrystalElement.LIME, 5, -2, -5);
		addColor(CrystalElement.YELLOW, -5, -2, -5);
		addColor(CrystalElement.LIGHTBLUE, 0, -1, 4);
		addColor(CrystalElement.MAGENTA, 0, -2, 7);
		addColor(CrystalElement.ORANGE, 4, -1, 0);
		addColor(CrystalElement.WHITE, 3, -1, 3);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			HashSet<CrystalElement> m = new HashSet();
			Collection<CrystalElement> m2 = ElementMixer.instance.getMixablesWith(e);
			if (m2 != null) {
				m.addAll(m2);
			}
			m2 = ElementMixer.instance.getMixParents(e);
			if (m2 != null) {
				m.addAll(m2);
			}
			m2 = ElementMixer.instance.getChildrenOf(e);
			if (m2 != null) {
				m.addAll(m2);
			}
			beams.put(e, m);
		}
	}

	private static void addColor(CrystalElement e, int x, int y, int z) {
		locations.put(e, new Coordinate(x, y, z));
	}

	private Collection<CrystalElement> getColorBeams() {
		return beams.get(color);
	}

	public void setStructure(StructurePair p) {
		structure = p.generator;
		color = p.color;
		this.syncAllData(false);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (world.isRemote) {
			if (placer != null) {
				this.spawnConnectFX(world, x, y, z);
			}
			else if (structure != null) {
				this.structureControlFX();
			}
		}
		else {
			if (structure != null) {
				this.doScanForEntry(world, x, y, z);
				if (!triggered) {
					this.doStructureCalculation(world, x, y, z);
				}
			}
		}
	}

	private void doScanForEntry(World world, int x, int y, int z) {
		AxisAlignedBB box = this.getStructureEntryBox();
		//ReikaJavaLibrary.pConsole(box);
		for (EntityPlayerMP ep : ((List<EntityPlayerMP>)world.getEntitiesWithinAABB(EntityPlayerMP.class, box))) {
			UUID uid = ep.getUniqueID();
			if (!sentPlayers.contains(uid)) {
				sentPlayers.add(uid);
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.STRUCTUREENTRY.ordinal(), ep, structure.ordinal());
			}
		}
	}

	private AxisAlignedBB getStructureEntryBox() {
		int x = structure.getGenerator().getEntryPosX();
		int y = structure.getGenerator().getPosY();
		int z = structure.getGenerator().getEntryPosZ();
		int r = 8;
		return AxisAlignedBB.getBoundingBox(x-r, y, z-r, x+r+1, ReikaWorldHelper.getTopNonAirBlock(worldObj, x, z)+9, z+r+1);
	}

	private void doStructureCalculation(World world, int x, int y, int z) {
		switch(structure) {
			case ALTAR:
				break;
			case LOCKS:
				break;
			case SHIFTMAZE:
				break;
			case TDMAZE:
				AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x-1, y+2, z-1, x+2, y+4, z+2);
				List<EntityPlayer> li = world.getEntitiesWithinAABB(EntityPlayer.class, box);
				if (!li.isEmpty()) {
					EntityPlayer ep = li.get(0);
					triggered = true;
					boolean w = rand.nextBoolean();
					int dx = w ? rand.nextBoolean() ? -2 : 2 : rand.nextBoolean() ? 1 : -1;
					int dz = !w ? rand.nextBoolean() ? -2 : 2 : rand.nextBoolean() ? 1 : -1;
					int dx2 = Math.abs(dx) == 1 ? dx : (int)Math.signum(dx)*(Math.abs(dx)+1);
					int dz2 = Math.abs(dz) == 1 ? dz : (int)Math.signum(dz)*(Math.abs(dz)+1);
					world.setBlockMetadataWithNotify(x+dx, y+2, z+dz, BlockType.CRACKS.metadata, 3);
					world.setBlockMetadataWithNotify(x+dx, y+3, z+dz, BlockType.CRACKS.metadata, 3);
					world.setBlockMetadataWithNotify(x-dx2, y+1, z-dz2, BlockType.CRACKS.metadata, 3);
					world.setBlockMetadataWithNotify(x+dx, y, z+dz, BlockType.CRACKS.metadata, 3);
					world.setBlockMetadataWithNotify(x+dx, y-1, z+dz, BlockType.CRACKS.metadata, 3);
					ReikaSoundHelper.playBreakSound(world, x, y+3, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 2, 1);
					ReikaSoundHelper.playBreakSound(world, x, y+3, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 2, 1);
					ReikaSoundHelper.playBreakSound(world, x, y+3, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 2, 1);
				}
				break;
			case MUSIC:
				break;
			case NONEUCLID:
				break;
			case GOL:
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	private void structureControlFX() {

	}

	@SideOnly(Side.CLIENT)
	private void spawnConnectFX(World world, int x, int y, int z) {
		int sp = 8;
		if ((this.getTicksExisted()/sp)%16 == color.ordinal() && this.getTicksExisted()%sp == 0) {
			float mult = this.getSoundPitch();
			//CrystalMusicManager.instance.getRandomScaledDing(color);
			Collection<CrystalElement> m = this.getColorBeams();
			//if (rand.nextInt(m.size() >= 8 ? 1 : 8-m.size()) == 0) {
			//CrystalElement e = ReikaJavaLibrary.getRandomListEntry(m);
			boolean flag = false;

			for (CrystalElement e : m) {
				Coordinate c = this.getOtherColor(e);
				TileEntity te = c.getTileEntity(world);
				if (te instanceof TileEntityDimensionCore && ((TileEntityDimensionCore)te).getColor() == e) {
					this.createBeamLine(world, x, y, z, c, e);
					flag = true;
				}
				//}
			}
			Coordinate cc = this.getCenter();
			if (cc.getTileEntity(world) instanceof TileEntityStructControl) {
				this.createBeamLine(world, x, y, z, cc, color);
				flag = true;
			}

			if (flag) {
				ReikaSoundHelper.playClientSound(ChromaSounds.ORB, x, y, z, 1, mult);
				ReikaSoundHelper.playClientSound(ChromaSounds.DING, x, y, z, 0.3F, mult);

				int n = 8+rand.nextInt(8);
				for (int i = 0; i < n; i++) {
					double px = x+rand.nextDouble();
					double py = y+rand.nextDouble();
					double pz = z+rand.nextDouble();
					int l = 40;
					float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.03125, 0.0150625);
					float s = 2*(float)ReikaRandomHelper.getRandomPlusMinus(1.25, 0.5);
					EntityFX fx = new EntityLaserFX(color, world, px, py, pz, 0, 0, 0).setGravity(g).setScale(s);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void createBeamLine(World world, int x, int y, int z, Coordinate c, CrystalElement e) {
		double dx = c.xCoord-x;
		double dy = c.yCoord-y;
		double dz = c.zCoord-z;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		for (double p = 0; p <= dd; p += 0.125) {
			double f = p/dd;
			//double v = 0.0625;
			//double vx = dx/dd*v;
			//double vy = dy/dd*v;
			//double vz = dz/dd*v;
			float s = 1+1.5F*(float)Math.sin(f*Math.PI);//+MathHelper.sin((this.getTicksExisted()+color.ordinal()*12)/32F);
			//2.5F+2*rand.nextFloat()+(rand.nextFloat()*2)*(rand.nextFloat()*3);
			int l = 20;//(int)(17*dd);
			CrystalElement e1 = e;//rand.nextBoolean() ? e : color;
			double px = x+0.5+f*dx;
			double py = y+0.5+f*dy;
			double pz = z+0.5+f*dz;

			int clr = ReikaColorAPI.mixColors(color.getColor(), e.getColor(), 1-(float)f);
			EntityFX fx = new EntityBlurFX(world, px, py, pz).setLife(l).setNoSlowdown().setScale(s).setColor(clr);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private float getSoundPitch() {
		float mult = 0;
		switch(soundPitch) {
			case 0:
				mult = (float)CrystalMusicManager.instance.getDingPitchScale(color);
				break;
			case 1:
				mult = (float)CrystalMusicManager.instance.getFifth(color);
				break;
			case 2:
				mult = (float)CrystalMusicManager.instance.getOctave(color);
				break;
		}
		soundPitch = rand.nextBoolean() ? 0 : rand.nextInt(3);
		return mult;
	}

	private Coordinate getCenter() {
		Coordinate c = locations.get(color);
		return new Coordinate(xCoord-c.xCoord, yCoord-c.yCoord, zCoord-c.zCoord);
	}

	private Coordinate getOtherColor(CrystalElement e) {
		Coordinate c = locations.get(color);
		Coordinate c2 = locations.get(e);
		return new Coordinate(xCoord-c.xCoord+c2.xCoord, yCoord-c.yCoord+c2.yCoord, zCoord-c.zCoord+c2.zCoord);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);

		if (structure == null && !world.isRemote) {
			ChromatiCraft.logger.logError(this+" was never given a structure!? Color = "+color);
		}
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.DIMENSIONCORE;
	}

	@Override
	public int getRenderColor() {
		return color.getColor();
	}

	public CrystalElement getColor() {
		return color;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		int s = NBT.getInteger("struct");
		structure = s >= 0 ? DimensionStructureType.types[s] : null;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("struct", structure != null ? structure.ordinal() : -1);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		color = CrystalElement.elements[NBT.getInteger("color")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("color", color.ordinal());
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		NBT.setInteger("color", color.ordinal());
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		color = is.stackTagCompound != null ? CrystalElement.elements[is.stackTagCompound.getInteger("color")] : CrystalElement.WHITE;
	}

	@Override
	public boolean breakByPlayer(EntityPlayer ep) {
		if (worldObj.isRemote)
			return true;
		if (ep.capabilities.isCreativeMode) {
			if (structure != null)
				this.openStructure();
			return true;
		}
		if (ReikaPlayerAPI.isFake(ep))
			return false;
		if (ep.getDistance(xCoord+0.5, yCoord+0.5, zCoord+0.5) > 5)
			return false;
		if (structure != null) {
			if (structure.hasPlayerCompleted(ep)) {
				return false;
			}
			else {
				structure.markPlayerCompleted(ep);

				this.openStructure();
			}
		}
		return true;
	}

	private void openStructure() {
		DimensionStructureGenerator gen = structure.getGenerator();
		Set<Coordinate> set = gen.getBreakableSpots();
		for (Coordinate c2 : set) {
			//Coordinate c2 = c.offset(-gen.getPosX(), -gen.getPosY(), -gen.getPosZ()).offset(xCoord, yCoord, zCoord);
			Block b = c2.getBlock(worldObj);
			BlockKey b2 = b instanceof BlockStructureShield ? new BlockKey(b, BlockType.CRACKS.metadata%8) : new BlockKey(Blocks.air);
			c2.setBlock(worldObj, b2.blockID, b2.metadata);
			//ReikaJavaLibrary.pConsole(new Coordinate(this)+":"+c+">"+c2+":"+c2.getBlockKey(worldObj));
			//ReikaJavaLibrary.pConsole(new Coordinate(this)+" > "+c2+" % "+c2.getBlockKey(worldObj));
		}

		ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 2, 1);
		ReikaSoundHelper.playBreakSound(worldObj, xCoord+4, yCoord, zCoord, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 2, 1);
		ReikaSoundHelper.playBreakSound(worldObj, xCoord-4, yCoord, zCoord, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 2, 1);
		ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord+4, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 2, 1);
		ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord-4, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 2, 1);
		ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord+4, zCoord, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 2, 1);
		ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord-4, zCoord, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 2, 1);
	}

}
