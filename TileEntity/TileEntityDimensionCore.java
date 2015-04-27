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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityLocusPoint;
import Reika.ChromatiCraft.Magic.ElementMixer;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Structure core, does FX and things
public class TileEntityDimensionCore extends TileEntityLocusPoint implements NBTTile {

	private CrystalElement color = CrystalElement.WHITE;

	private static final EnumMap<CrystalElement, Coordinate> locations = new EnumMap(CrystalElement.class);

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
	}

	private static void addColor(CrystalElement e, int x, int y, int z) {
		locations.put(e, new Coordinate(x, y, z));
	}

	private int soundPitch;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (world.isRemote) {
			this.spawnConnectFX(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnConnectFX(World world, int x, int y, int z) {
		int sp = 8;
		if ((this.getTicksExisted()/sp)%16 == color.ordinal() && this.getTicksExisted()%sp == 0) {
			float mult = this.getSoundPitch();
			//CrystalMusicManager.instance.getRandomScaledDing(color);
			//ReikaJavaLibrary.pConsole(color+": "+mult);
			ReikaSoundHelper.playClientSound(ChromaSounds.ORB, x, y, z, 1, mult);
			ReikaSoundHelper.playClientSound(ChromaSounds.DING, x, y, z, 0.3F, mult);
			ArrayList<CrystalElement> m = new ArrayList();
			Collection<CrystalElement> m2 = ElementMixer.instance.getMixablesWith(color);
			if (m2 != null) {
				m.addAll(m2);
			}
			m2 = ElementMixer.instance.getMixParents(color);
			if (m2 != null) {
				m.addAll(m2);
			}
			m2 = ElementMixer.instance.getChildrenOf(color);
			if (m2 != null) {
				m.addAll(m2);
			}
			//ReikaJavaLibrary.pConsole(color+": "+m);

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

			//if (rand.nextInt(m.size() >= 8 ? 1 : 8-m.size()) == 0) {
			//CrystalElement e = ReikaJavaLibrary.getRandomListEntry(m);
			for (CrystalElement e : m) {
				Coordinate c = this.getOtherColor(e);
				TileEntity te = c.getTileEntity(world);
				if (te instanceof TileEntityDimensionCore && ((TileEntityDimensionCore)te).getColor() == e) {
					this.createBeamLine(world, x, y, z, c, e);
				}
				//}
			}
			this.createBeamLine(world, x, y, z, this.getCenter(), color);
		}
	}

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
			int r = ReikaColorAPI.getRed(clr);
			int g = ReikaColorAPI.getGreen(clr);
			int b = ReikaColorAPI.getBlue(clr);
			EntityFX fx = new EntityBlurFX(world, px, py, pz).setLife(l).setNoSlowdown().setScale(s).setColor(r, g, b);
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
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.DIMENSIONCORE;
	}

	public CrystalElement getColor() {
		return color;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

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

}
