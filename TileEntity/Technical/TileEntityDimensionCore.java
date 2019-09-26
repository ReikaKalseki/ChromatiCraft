/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Technical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
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
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.PlayerBreakHook;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Structure core, does FX and things
public class TileEntityDimensionCore extends TileEntityLocusPoint implements NBTTile, PlayerBreakHook {

	private CrystalElement color = CrystalElement.WHITE;
	private UUID uid = null;
	private DimensionStructureType structure = null;
	private boolean triggered = false;

	private final HashSet<UUID> sentPlayers = new HashSet();
	private final HashSet<UUID> playerWhitelist = new HashSet();

	private static final EnumMap<CrystalElement, Coordinate> locations = new EnumMap(CrystalElement.class);
	private static final EnumMap<CrystalElement, HashSet<CrystalElement>> beams = new EnumMap(CrystalElement.class);
	private static final ArrayList<ArrayList<ImmutablePair<CrystalElement, Integer>>>[] melody = new ArrayList[2];

	private boolean primed = false;

	static {
		addColor(CrystalElement.BLACK, 5, 11, 18);
		addColor(CrystalElement.RED, 8, 11, 14);
		addColor(CrystalElement.GREEN, 14, 11, 8);
		addColor(CrystalElement.BROWN, 18, 11, 5);
		addColor(CrystalElement.BLUE, 24, 11, 5);
		addColor(CrystalElement.PURPLE, 28, 11, 8);
		addColor(CrystalElement.CYAN, 34, 11, 14);
		addColor(CrystalElement.LIGHTGRAY, 37, 11, 18);
		addColor(CrystalElement.GRAY, 37, 11, 24);
		addColor(CrystalElement.PINK, 34, 11, 28);
		addColor(CrystalElement.LIME, 28, 11, 34);
		addColor(CrystalElement.YELLOW, 24, 11, 37);
		addColor(CrystalElement.LIGHTBLUE, 18, 11, 37);
		addColor(CrystalElement.MAGENTA, 14, 11, 34);
		addColor(CrystalElement.ORANGE, 8, 11, 28);
		addColor(CrystalElement.WHITE, 5, 11, 24);

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

		melody[0] = new ArrayList();
		addMelodyNote(0, MusicKey.G4);
		addMelodyNote(0, MusicKey.A4);
		addMelodyNote(0, MusicKey.B4);
		addMelodyNote(0, MusicKey.D5);
		addMelodyNote(0, MusicKey.C5);
		addMelodyNote(0, MusicKey.C5);
		addMelodyNote(0, MusicKey.E5);
		addMelodyNote(0, MusicKey.D5);
		addMelodyNote(0, MusicKey.D5);
		addMelodyNote(0, MusicKey.G5);
		addMelodyNote(0, MusicKey.Fs5);
		addMelodyNote(0, MusicKey.G5);
		addMelodyNote(0, MusicKey.D5);
		addMelodyNote(0, MusicKey.B4);
		addMelodyNote(0, MusicKey.G4);
		addMelodyNote(0, MusicKey.A4);
		addMelodyNote(0, MusicKey.B4);
		addMelodyNote(0, MusicKey.C5);
		addMelodyNote(0, MusicKey.D5);
		addMelodyNote(0, MusicKey.E5);
		addMelodyNote(0, MusicKey.D5);
		addMelodyNote(0, MusicKey.C5);
		addMelodyNote(0, MusicKey.B4);
		addMelodyNote(0, MusicKey.A4);
		addMelodyNote(0, MusicKey.B4);
		addMelodyNote(0, MusicKey.G4);
		addMelodyNote(0, MusicKey.Fs4);
		addMelodyNote(0, MusicKey.G4);
		addMelodyNote(0, MusicKey.A4);
		addMelodyNote(0, MusicKey.D5);
		addMelodyNote(0, MusicKey.Fs5);
		addMelodyNote(0, MusicKey.A5);
		addMelodyNote(0, MusicKey.C6);
		addMelodyNote(0, MusicKey.B5);
		addMelodyNote(0, MusicKey.A5);
		addMelodyNote(0, MusicKey.B5);
		addMelodyNote(0, MusicKey.G5);
		addMelodyNote(0, MusicKey.A5);
		addMelodyNote(0, MusicKey.B5);
		addMelodyNote(0, MusicKey.D6);
		addMelodyNote(0, MusicKey.C6);
		addMelodyNote(0, MusicKey.C6);
		addMelodyNote(0, MusicKey.E6);
		addMelodyNote(0, MusicKey.D5);
		addMelodyNote(0, MusicKey.D6);
		addMelodyNote(0, MusicKey.D6);
		addMelodyNote(0, MusicKey.G5);
		addMelodyNote(0, MusicKey.Fs5);
		addMelodyNote(0, MusicKey.G5);
		addMelodyNote(0, MusicKey.D6);
		addMelodyNote(0, MusicKey.B5);
		addMelodyNote(0, MusicKey.G5);
		addMelodyNote(0, MusicKey.A5);
		addMelodyNote(0, MusicKey.B5);
		addMelodyNote(0, MusicKey.E5);
		addMelodyNote(0, MusicKey.D6);
		addMelodyNote(0, MusicKey.C6);
		addMelodyNote(0, MusicKey.B5);
		addMelodyNote(0, MusicKey.A5);
		addMelodyNote(0, MusicKey.G5);
		addMelodyNote(0, MusicKey.D5);
		addMelodyNote(0, MusicKey.G5);
		addMelodyNote(0, MusicKey.Fs5);
		addMelodyNote(0, MusicKey.G5);
		addMelodyNote(0, MusicKey.G5);
		addMelodyNote(0, MusicKey.G5);
		addMelodyNote(0, MusicKey.G5);

		melody[1] = new ArrayList();
		addMelodyNote(1, MusicKey.C5);
		addMelodyNote(1, MusicKey.E5);
		addMelodyNote(1, MusicKey.G5);
		addMelodyNote(1, MusicKey.C6);
		addMelodyNote(1, MusicKey.G4);
		addMelodyNote(1, MusicKey.B4);
		addMelodyNote(1, MusicKey.D5);
		addMelodyNote(1, MusicKey.G5);
		addMelodyNote(1, MusicKey.A4);
		addMelodyNote(1, MusicKey.C5);
		addMelodyNote(1, MusicKey.E5);
		addMelodyNote(1, MusicKey.A5);
		addMelodyNote(1, MusicKey.E4);
		addMelodyNote(1, MusicKey.G4);
		addMelodyNote(1, MusicKey.B4);
		addMelodyNote(1, MusicKey.G5);
		addMelodyNote(1, MusicKey.F4);
		addMelodyNote(1, MusicKey.A4);
		addMelodyNote(1, MusicKey.C5);
		addMelodyNote(1, MusicKey.F5);
		addMelodyNote(1, MusicKey.C4);
		addMelodyNote(1, MusicKey.E4);
		addMelodyNote(1, MusicKey.G4);
		addMelodyNote(1, MusicKey.C5);
		addMelodyNote(1, MusicKey.F4);
		addMelodyNote(1, MusicKey.A4);
		addMelodyNote(1, MusicKey.C5);
		addMelodyNote(1, MusicKey.F5);
		addMelodyNote(1, MusicKey.G4);
		addMelodyNote(1, MusicKey.B4);
		addMelodyNote(1, MusicKey.D5);
		addMelodyNote(1, MusicKey.G5);
	}

	private static void addMelodyNote(int track, MusicKey key) {
		Collection<CrystalElement> c = CrystalMusicManager.instance.getColorsWithKey(key);
		if (c.isEmpty())
			throw new RegistrationException(ChromatiCraft.instance, "No such color for note "+key);
		ArrayList<ImmutablePair<CrystalElement, Integer>> li = new ArrayList();
		for (CrystalElement e : c) {
			int idx = CrystalMusicManager.instance.getIntervalFor(e, key);
			if (idx == -1) {
				throw new RegistrationException(ChromatiCraft.instance, "No such index for note "+key+" for color "+e);
			}
			li.add(new ImmutablePair(e, idx));
		}
		//ReikaJavaLibrary.pConsole("Generating "+e+":"+idx+" for "+track+" / "+key);
		melody[track].add(li);
	}

	private static void addColor(CrystalElement e, int x, int y, int z) {
		locations.put(e, new Coordinate(x-21, y-5, z-21)); //base offset of controller is (21, 5, 21)
	}

	private Collection<CrystalElement> getColorBeams() {
		return beams.get(color);
	}

	public void setStructure(StructurePair p) {
		structure = p.generator.getType();
		uid = p.generator.id;
		color = p.color;
		this.syncAllData(false);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (world.isRemote) {
			if (primed) {
				this.spawnConnectFX(world, x, y, z);
			}
			else if (this.hasStructure()) {
				this.structureControlFX(world, x, y, z);
			}
		}
		else {
			if (this.hasStructure()) {
				this.doScanForEntry(world, x, y, z);
				if (!triggered) {
					this.doStructureCalculation(world, x, y, z);
				}
			}
		}
	}

	public void prime(boolean set) {
		primed = set;
		this.syncAllData(false);
	}

	private DimensionStructureGenerator getStructure() {
		return structure != null ? structure.getGenerator(uid) : null;
	}

	public boolean hasStructure() {
		return this.getStructure() != null;
	}

	private void doScanForEntry(World world, int x, int y, int z) {
		AxisAlignedBB box = this.getStructureEntryBox();
		//ReikaJavaLibrary.pConsole(box);
		for (EntityPlayerMP ep : ((List<EntityPlayerMP>)world.getEntitiesWithinAABB(EntityPlayerMP.class, box))) {
			UUID uid = ep.getUniqueID();
			if (!sentPlayers.contains(uid)) {
				if (ChromaDimensionManager.addPlayerToStructure(ep, this.getStructure()))
					sentPlayers.add(uid);
			}
		}
	}

	private AxisAlignedBB getStructureEntryBox() {
		DimensionStructureGenerator gen = this.getStructure();
		int x = gen.getEntryPosX();
		int y = gen.getPosY();
		int z = gen.getEntryPosZ();
		int r = 8;
		return AxisAlignedBB.getBoundingBox(x-r, y, z-r, x+r+1, ReikaWorldHelper.getTopNonAirBlock(worldObj, x, z, true)+9, z+r+1);
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
			case ANTFARM:
				break;
			case LASER:
				break;
			case PINBALL:
				break;
			case GRAVITY:
				break;
			case BRIDGE:
				break;
			case LIGHTPANEL:
				break;
			case TESSELLATION:
				break;
			case WATER:
				break;
			case RAYBLEND:
				break;
			case PISTONTAPE:
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	private void structureControlFX(World world, int x, int y, int z) {

	}

	@SideOnly(Side.CLIENT)
	private void spawnConnectFX(World world, int x, int y, int z) {
		int sp = 8;
		long tick = world.getTotalWorldTime(); //this.getTicksExisted();
		if (tick%sp == 0) {
			ArrayList<ArrayList<ImmutablePair<CrystalElement, Integer>>> song = melody[(ChunkProviderChroma.getMonumentGenerator().hashCode() ^ Minecraft.getMinecraft().hashCode())%melody.length];
			ArrayList<ImmutablePair<CrystalElement, Integer>> li = song.get((int)((tick/sp)%song.size()));
			ImmutablePair<CrystalElement, Integer> p = li.get(rand.nextInt(li.size()));
			if (p.left == color) {
				Coordinate cc = this.getCenter();
				TileEntity tile = cc.getTileEntity(world);
				if (tile instanceof TileEntityStructControl) {
					TileEntityStructControl ts = (TileEntityStructControl)tile;
					if (ts.isMonument()) {
						float mult = this.getSoundPitch(p.right);
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
						this.createBeamLine(world, x, y, z, cc, color);

						ReikaSoundHelper.playClientSound(ChromaSounds.ORB, x, y, z, 1, mult, false);
						ReikaSoundHelper.playClientSound(ChromaSounds.DING, x, y, z, 0.3F, mult);

						int n = 8+rand.nextInt(8);
						for (int i = 0; i < n; i++) {
							double px = x+rand.nextDouble();
							double py = y+rand.nextDouble();
							double pz = z+rand.nextDouble();
							int l = 40;
							float g = (float)ReikaRandomHelper.getRandomPlusMinus(0.03125, 0.0150625);
							float s = 2*(float)ReikaRandomHelper.getRandomPlusMinus(1.25, 0.5);
							EntityFX fx = new EntityLaserFX(color, world, px, py, pz, 0, 0, 0).setGravity(g).setScale(s);
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}

						for (int i = 0; i < n; i++) {
							double px = x+rand.nextDouble();
							double py = y+rand.nextDouble();
							double pz = z+rand.nextDouble();
							int l = 80;
							float g = (float)ReikaRandomHelper.getRandomPlusMinus(0.03125, 0.0150625);
							float s = 2*(float)ReikaRandomHelper.getRandomPlusMinus(1.25, 0.5);
							EntityFloatingSeedsFX fx = new EntityFloatingSeedsFX(world, px, py, pz, 0, -90);
							fx = (EntityFloatingSeedsFX)fx.setGravity(g).setScale(s).setLife(l).setColor(color.getColor());
							fx.angleVelocity *= 3;
							fx.freedom *= 5;
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void createBeamLine(World world, int x, int y, int z, Coordinate c, CrystalElement e) {
		createBeamLine(world, x, y, z, c.xCoord, c.yCoord, c.zCoord, color, e);
	}

	@SideOnly(Side.CLIENT)
	public static void createBeamLine(World world, int x1, int y1, int z1, int x2, int y2, int z2, CrystalElement e1, CrystalElement e2) {
		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		double pd = 0.25; //0.125
		for (double p = 0; p <= dd; p += pd) {
			double f = p/dd;
			//double v = 0.0625;
			//double vx = dx/dd*v;
			//double vy = dy/dd*v;
			//double vz = dz/dd*v;
			float s = 1+1.5F*(float)Math.sin(f*Math.PI);//+MathHelper.sin((this.getTicksExisted()+color.ordinal()*12)/32F);
			//2.5F+2*rand.nextFloat()+(rand.nextFloat()*2)*(rand.nextFloat()*3);
			int l = 20;//(int)(17*dd);
			double px = x1+0.5+f*dx;
			double py = y1+0.5+f*dy;
			double pz = z1+0.5+f*dz;

			int clr = ReikaColorAPI.mixColors(e1.getColor(), e2.getColor(), 1-(float)f);
			EntityFX fx = new EntityBlurFX(world, px, py, pz).setLife(l).setNoSlowdown().setScale(s).setColor(clr);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private float getSoundPitch(int p) {
		float mult = 0;
		switch(p) {
			case 0:
				mult = (float)CrystalMusicManager.instance.getDingPitchScale(color);
				break;
			case 1:
				mult = (float)CrystalMusicManager.instance.getThird(color);
				break;
			case 2:
				mult = (float)CrystalMusicManager.instance.getFifth(color);
				break;
			case 3:
				mult = (float)CrystalMusicManager.instance.getOctave(color);
				break;
		}
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

		if (this.getPlacer() == null && !this.hasStructure() && !world.isRemote) {
			ChromatiCraft.logger.logError(this+" was never given a structure!? Color = "+color+", UID="+uid);
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

		if (NBT.hasKey("uid"))
			uid = UUID.fromString(NBT.getString("uid"));

		playerWhitelist .clear();
		NBTTagList li = NBT.getTagList("whitelist", NBTTypes.STRING.ID);
		for (Object o : li.tagList) {
			String sg = ((NBTTagString)o).func_150285_a_();
			playerWhitelist.add(UUID.fromString(sg));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("struct", structure != null ? structure.ordinal() : -1);
		if (uid != null)
			NBT.setString("uid", uid.toString());

		NBTTagList li = new NBTTagList();
		for (UUID id : playerWhitelist) {
			li.appendTag(new NBTTagString(id.toString()));
		}
		NBT.setTag("whitelist", li);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		color = CrystalElement.elements[NBT.getInteger("color")];

		primed = NBT.getBoolean("prime");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("color", color.ordinal());

		NBT.setBoolean("prime", primed);
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		super.getTagsToWriteToStack(NBT);
		NBT.setInteger("color", color.ordinal());
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		super.setDataFromItemStackTag(is);
		color = is.stackTagCompound != null ? CrystalElement.elements[is.stackTagCompound.getInteger("color")] : CrystalElement.WHITE;
	}

	public boolean isBreakable(EntityPlayer ep) {
		if (ep == null)
			return false;
		if (ReikaPlayerAPI.isFake(ep))
			return false;
		if (!worldObj.isRemote && !ep.capabilities.isCreativeMode && this.hasStructure()) {
			if (!this.getStructure().shouldAllowCoreMining(worldObj, ep)) {
				return false;
			}
		}
		if (!playerWhitelist.isEmpty() && !playerWhitelist.contains(ep.getUniqueID()))
			return false;
		return true;
	}

	@Override
	public boolean breakByPlayer(EntityPlayer ep) {
		if (worldObj.isRemote) {
			if (this.hasStructure()) {
				ChromaDimensionManager.removePlayerFromStructure(ep);
			}
			return true;
		}
		if (ep.capabilities.isCreativeMode) {
			if (this.hasStructure())
				this.openStructure();
			return true;
		}
		if (ep.getDistance(xCoord+0.5, yCoord+0.5, zCoord+0.5) > 5)
			return false;
		if (this.hasStructure()) {
			/*
			if (structure.hasPlayerCompleted(ep)) {
				return false;
			}
			else {
				structure.markPlayerCompleted(ep);

				this.openStructure();
			}
			 */

			if (!ep.capabilities.isCreativeMode && !this.getStructure().shouldAllowCoreMining(worldObj, ep)) {
				return false;
			}

			//if (ProgressionManager.instance.hasPlayerCompletedStructureColor(ep, color)) {
			//	return false;
			//}
			//else {
			DimensionStructureGenerator gen = this.getStructure();
			ProgressionManager.instance.markPlayerCompletedStructureColor(ep, gen, color, true, true);
			ChromaDimensionManager.removePlayerFromStructure(ep);
			this.openStructure();
			//}
		}
		return true;
	}

	private void openStructure() {
		DimensionStructureGenerator gen = this.getStructure();
		Set<Coordinate> set = gen.getBreakableSpots();
		for (Coordinate c2 : set) {
			//Coordinate c2 = c.offset(-gen.getPosX(), -gen.getPosY(), -gen.getPosZ()).offset(xCoord, yCoord, zCoord);
			Block b = c2.getBlock(worldObj);
			BlockKey b2 = b instanceof BlockStructureShield ? new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CRACKS.metadata%8) : new BlockKey(Blocks.air);
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

	public static Coordinate getLocation(CrystalElement e) {
		return locations.get(e);
	}

	void setColor(CrystalElement e) {
		color = e;
	}

	public void whitelistPlayer(EntityPlayer ep) {
		playerWhitelist.add(ep.getUniqueID());
	}

}
