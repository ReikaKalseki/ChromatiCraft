/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import Reika.ChromatiCraft.Auxiliary.Interfaces.VariableTexture;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Magic.PylonLinkNetwork;
import Reika.ChromatiCraft.Magic.PylonLinkNetwork.PylonNode;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityPylonLink extends TileEntityChromaticBase implements LocationCached, VariableTexture {

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
	public int getIconState(int side) {
		return side == 1 && connection != null ? 1 : 0;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.link();
	}

	public void link() {
		if (placerUUID == null)
			return;
		TileEntityCrystalPylon te = this.getPylon();

		if (te != null) {
			connection = PylonLinkNetwork.instance.addLocation(this, te);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doFX(World world, int x, int y, int z) {
		int c = connection.getColor().getColor();

		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		double dd = ep.getDistanceSq(x+0.5, y+0.5, z+0.5);

		if (dd >= 256 && rand.nextInt(3) == 0)
			return;
		else if (dd >= 1024 && rand.nextInt(2) == 0)
			return;
		else if (dd >= 4096 && rand.nextInt(3) > 0)
			return;

		double t = this.getTicksExisted()*1.5D;
		int n = dd >= 256 ? 3 : 6;
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
		return PylonLinkNetwork.instance.getLinkedPylons(worldObj, placerUUID, connection.getColor());
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
		PylonLinkNetwork.instance.removeLocation(worldObj, connection);
		connection = null;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		if (NBT.hasKey("link"))
			connection = PylonNode.fromSync(NBT.getCompoundTag("link"));
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (connection != null) {
			NBTTagCompound tag = new NBTTagCompound();
			connection.sync(tag);
			NBT.setTag("link", tag);
		}
	}

	public UUID getUUID() {
		return placerUUID;
	}

}
