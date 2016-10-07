/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Decoration;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.DragonAPI.Instantiable.MusicScore;
import Reika.DragonAPI.Instantiable.MusicScore.Note;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.MIDIInterface;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;
import Reika.DragonAPI.Interfaces.TileEntity.TriggerableAction;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityCrystalMusic extends /*Inventoried*/TileEntityChromaticBase implements GuiController, TriggerableAction {

	private boolean isPlaying;
	private int playTick;

	private MusicScore track = new MusicScore(16);

	private static final MusicScore demoTrack = new MIDIInterface(ChromatiCraft.class, "Resources/music-demo.mid").fillToScore().scaleSpeed(11);

	static {
		ChromatiCraft.logger.log("Loaded demo track "+demoTrack);
	}

	private static final EnumMap<CrystalElement, Coordinate> colorPositions = new EnumMap(CrystalElement.class);

	static {
		colorPositions.put(CrystalElement.BLACK, 		new Coordinate(0, 0, -3));
		colorPositions.put(CrystalElement.RED, 			new Coordinate(1, 0, -3));
		colorPositions.put(CrystalElement.GREEN, 		new Coordinate(2, 0, -2));
		colorPositions.put(CrystalElement.BROWN, 		new Coordinate(3, 0, -1));
		colorPositions.put(CrystalElement.BLUE, 		new Coordinate(3, 0, 0));
		colorPositions.put(CrystalElement.PURPLE, 		new Coordinate(3, 0, 1));
		colorPositions.put(CrystalElement.CYAN, 		new Coordinate(2, 0, 2));
		colorPositions.put(CrystalElement.LIGHTGRAY, 	new Coordinate(1, 0, 3));
		colorPositions.put(CrystalElement.GRAY, 		new Coordinate(0, 0, 3));
		colorPositions.put(CrystalElement.PINK, 		new Coordinate(-1, 0, 3));
		colorPositions.put(CrystalElement.LIME, 		new Coordinate(-2, 0, 2));
		colorPositions.put(CrystalElement.YELLOW, 		new Coordinate(-3, 0, 1));
		colorPositions.put(CrystalElement.LIGHTBLUE, 	new Coordinate(-3, 0, 0));
		colorPositions.put(CrystalElement.MAGENTA, 		new Coordinate(-3, 0, -1));
		colorPositions.put(CrystalElement.ORANGE, 		new Coordinate(-2, 0, -2));
		colorPositions.put(CrystalElement.WHITE, 		new Coordinate(-1, 0, -3));
	}

	public void addNote(int channel, MusicKey key, int length, boolean rest) {
		track.addNote(this.getTime(channel), channel, key, rest ? -1 : 0, 100, length);
	}

	private int getTime(int channel) {
		int pos = track.getLatestPos(channel);
		Note n = track.getNote(channel, pos);
		return pos+(n != null ? n.length : 0);
	}

	public void clearChannel(int channel) {
		track.clearChannel(channel);
	}

	public void clearMusic() {
		track = new MusicScore(16);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.MUSIC;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote && track != null) {
			if (isPlaying) {
				this.play(world, x, y, z);
			}
		}
	}

	@Override
	protected void onPositiveRedstoneEdge() {
		this.triggerPlay();
	}

	public void triggerPlay() {
		if (isPlaying)
			return;
		playTick = 0;
		isPlaying = true;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (!world.isRemote) {
			//this.loadDemo();
		}
	}

	private void play(World world, int x, int y, int z) {
		ArrayList<Note> li = track.getNotes(playTick);

		//int maxplay = 3;
		//HashMap<Note, Integer> plays = new HashMap();

		for (Note n : li) {
			//Integer get = plays.get(n);
			//int val = get != null ? get.intValue() : 0;
			//if (val < maxplay) {
			if (n.voice != -1) { // -1 == rest
				if (this.playNote(world, x, y, z, n)) {
					//	plays.put(n, val+1);
				}
			}
			//}
		}

		playTick++;
		if (playTick > track.getLatestPos())
			isPlaying = false;
	}

	private boolean playNote(World world, int x, int y, int z, Note n) {
		Set<CrystalElement> set = CrystalMusicManager.instance.getColorsWithKey(n.key);
		boolean canPlay = false;

		if (!set.isEmpty()) {
			for (CrystalElement e : set) {
				canPlay |= this.playCrystal(world, x, y, z, e);
			}
			if (canPlay)
				;//this.generateParticles(world, x, y, z, ReikaJavaLibrary.getRandomCollectionEntry(set));
		}
		else {
			canPlay = true;
			this.generateParticles(world, x, y+2, z, CrystalElement.randomElement());
		}

		if (canPlay) {
			ChromaSounds.DING.playSoundAtBlock(this, n.volume/100F, (float)CrystalMusicManager.instance.getPitchFactor(n.key));
			return true;
		}
		else {
			ChromaSounds.ERROR.playSoundAtBlock(this);
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.CRYSTALMUSERROR.ordinal(), this, 24);
			return false;
		}
	}

	public boolean playCrystal(World world, int x, int y, int z, CrystalElement e) {
		Coordinate c = colorPositions.get(e).offset(xCoord, yCoord, zCoord);
		Block b = c.getBlock(world);
		if (b instanceof CrystalBlock && c.getBlockMetadata(world) == e.ordinal()) {
			this.generateParticles(world, c.xCoord, c.yCoord, c.zCoord, e);
			return true;
		}
		return false;
	}

	private void generateParticles(World world, int x, int y, int z, CrystalElement e) {
		if (world.isRemote) {
			this.doParticles(world, x, y, z, e);
		}
		else {
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.CRYSTALMUS.ordinal(), this, 24, x, y, z, e.ordinal());
		}
	}

	@SideOnly(Side.CLIENT)
	public void doErrorParticles(World world, int x, int y, int z) {
		int n = 12+rand.nextInt(12);
		for (int i = 0; i < n; i++) {
			double dx = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);
			double dy = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);
			double dz = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);

			int l = 5+rand.nextInt(15);
			double v = ReikaRandomHelper.getRandomPlusMinus(0.25, 0.125);
			int c = CrystalElement.randomElement().getColor();

			EntityFX fx = new EntityBlurFX(world, x+0.5+dx, y+0.5+dy, z+0.5+dz, -dx*v, -dy*v, -dz*v).setLife(l).setColor(c).setGravity(0);
			fx.noClip = false;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	public void doParticles(World world, int x, int y, int z, CrystalElement e) {
		int n = 3+rand.nextInt(6);
		for (int i = 0; i < n; i++) {
			double px = x+rand.nextDouble();
			double py = y+rand.nextDouble();
			double pz = z+rand.nextDouble();

			int l = 10+rand.nextInt(40);
			float g = -(float)ReikaRandomHelper.getRandomBetween(0.015125, 0.125);

			EntityFX fx = new EntityFloatingSeedsFX(world, px, py, pz, 0, 90).setLife(l).setColor(e.getColor()).setGravity(g);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		if (x != xCoord || y != yCoord || z != zCoord) {
			double dx = x-xCoord;
			double dy = y-yCoord;
			double dz = z-zCoord;
			double dd = ReikaMathLibrary.py3d(dx, dy, dz);
			for (double p = 0; p <= dd; p += 0.125) {
				double f = p/dd;
				float s = 1+1.5F*(float)Math.sin(f*Math.PI);
				int l = 10;//(int)(17*dd);
				double px = xCoord+0.5+f*dx;
				double py = yCoord+0.5+f*dy;
				double pz = zCoord+0.5+f*dz;

				int clr = e.getColor();
				EntityFX fx = new EntityBlurFX(world, px, py, pz).setLife(l).setNoSlowdown().setScale(s).setColor(clr);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public void loadDemo() {
		this.setTrack(demoTrack.copy());
		this.triggerPlay();
	}

	public void setTrack(MusicScore music) {
		isPlaying = false;
		track = music;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		if (track != null) {
			NBTTagCompound tag = new NBTTagCompound();
			track.writeToNBT(tag);
			NBT.setTag("track", tag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		if (NBT.hasKey("track")) {
			NBTTagCompound tag = NBT.getCompoundTag("track");
			track = MusicScore.readFromNBT(tag);
		}
	}

	@Override
	public boolean trigger() {
		if (track == null)
			return false;
		this.triggerPlay();
		return true;
	}

	/*
	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
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
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return is.getItem() instanceof MusicDataItem;
	}
	 */
}
