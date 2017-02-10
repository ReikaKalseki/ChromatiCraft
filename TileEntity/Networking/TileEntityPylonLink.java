/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityPylonLink extends TileEntityChromaticBase implements LocationCached {

	private static final MultiMap<UUID, PylonNode> links = new MultiMap(new HashSetFactory());

	private PylonNode connection;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PYLONLINK;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote && connection != null) {
			this.doFX(world, x, y, z);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.link();
	}

	private void link() {
		if (placerUUID == null)
			return;
		TileEntityCrystalPylon te = this.getPylon();

		if (te != null) {
			connection = new PylonNode(this, te);
			links.addValue(placerUUID, connection);
			te.link(this);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doFX(World world, int x, int y, int z) {
		int c = connection.color.getColor();

		double t = this.getTicksExisted()*1.5D;
		int n = 6;
		double da = 360D/n;
		double r = 0.4375;
		for (double a = 0; a < 360; a += da) {
			double ang = Math.toRadians(a+t);
			double px = x+0.5+r*Math.cos(ang);
			double pz = z+0.5+r*Math.sin(ang);
			double py = y+1.05;
			float g = -(float)ReikaRandomHelper.getRandomBetween(0.03125/4, 0.03125);
			float s = (float)ReikaRandomHelper.getRandomBetween(0.375, 0.5);
			EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(c).setGravity(g).setScale(s).setRapidExpand().setAlphaFading();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		t = -this.getTicksExisted()*3.5D;
		n = 3;
		da = 360D/n;
		r = 0.1875;
		for (double a = 0; a < 360; a += da) {
			double ang = Math.toRadians(a+t);
			double px = x+0.5+r*Math.cos(ang);
			double pz = z+0.5+r*Math.sin(ang);
			double py = y+1.05;
			float g = -(float)ReikaRandomHelper.getRandomBetween(0.03125/4, 0.03125);
			float s = (float)ReikaRandomHelper.getRandomBetween(0.375, 0.5);
			EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(c).setGravity(g).setScale(s).setRapidExpand().setAlphaFading();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		for (int i = 1; i <= 4; i++) {
			double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.03125);
			double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.03125);
			double py = y+1+4.5+4.5*Math.sin(this.getTicksExisted()/64D*(1+i/8D)+i*5);
			EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(c).setScale(1.25F).setRapidExpand().setAlphaFading().setLife(12);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public Collection<WorldLocation> getLinkedPylons() {
		if (placerUUID == null || connection == null)
			return new ArrayList();
		ArrayList<WorldLocation> li = new ArrayList();
		Collection<PylonNode> c = links.get(placerUUID);
		for (PylonNode pn : c) {
			if (pn.color == connection.color)
				li.add(pn.pylon);
		}
		return li;
	}

	public TileEntityCrystalPylon getPylon() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		int d = 9;
		for (int i = 1; i < d; i++) {
			int dy = y+i;
			if (!world.getBlock(x, dy, z).isAir(world, x, dy, z) && !(i == 1 && ChromaTiles.getTile(world, x, dy, z) == ChromaTiles.PYLONTURBO))
				return null;
		}
		TileEntity tile = world.getTileEntity(x, y+d, z);
		return tile instanceof TileEntityCrystalPylon ? (TileEntityCrystalPylon)tile : null;
	}

	@Override
	public void breakBlock() {
		this.unlink();
	}

	private void unlink() {
		if (placerUUID == null || connection == null)
			return;
		WorldLocation loc = new WorldLocation(this);
		TileEntity te = connection.pylon.getTileEntity();
		if (te instanceof TileEntityCrystalPylon) {
			((TileEntityCrystalPylon)te).link(null);
		}
		links.remove(placerUUID, connection);
		connection = null;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		if (NBT.hasKey("link"))
			connection = PylonNode.readFromNBT("link", NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (connection != null)
			connection.writeToNBT("link", NBT);
	}

	private static class PylonNode {

		private final WorldLocation tile;
		private final WorldLocation pylon;
		private final CrystalElement color;

		private PylonNode(TileEntityPylonLink te, TileEntityCrystalPylon p) {
			this(new WorldLocation(te), new WorldLocation(p), p.getColor());
		}

		private PylonNode(WorldLocation loc, WorldLocation py, CrystalElement e) {
			tile = loc;
			pylon = py;
			color = e;
		}

		private static PylonNode readFromNBT(String tag, NBTTagCompound NBT) {
			NBTTagCompound c = NBT.getCompoundTag(tag);
			WorldLocation loc = WorldLocation.readFromNBT("loc", c);
			WorldLocation py = WorldLocation.readFromNBT("pylon", c);
			CrystalElement e = CrystalElement.elements[c.getInteger("color")];
			return new PylonNode(loc, py, e);
		}

		private void writeToNBT(String tag, NBTTagCompound NBT) {
			NBTTagCompound c = new NBTTagCompound();
			tile.writeToNBT("loc", c);
			pylon.writeToNBT("pylon", c);
			c.setInteger("color", color.ordinal());
			NBT.setTag(tag, c);
		}

		@Override
		public int hashCode() {
			return tile.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof PylonNode) {
				PylonNode pn = (PylonNode)o;
				return pn.tile.equals(tile) && pn.pylon.equals(pylon) && pn.color == color;
			}
			return false;
		}

	}

}
