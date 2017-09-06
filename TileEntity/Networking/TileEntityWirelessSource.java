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
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityWirelessPowered;
import Reika.ChromatiCraft.Magic.Interfaces.WirelessSource;
import Reika.ChromatiCraft.Magic.Network.PylonFinder;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.GlowTendril;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.ParticleController.AttractiveMotionController;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityWirelessSource extends CrystalReceiverBase implements WirelessSource, LocationCached, MultiBlockChromaTile {

	public static final int TRANSMIT_RANGE = 18;

	public static final double LOSS_PER_LUMEN = 0.2;
	public static final int OCCLUSION_FACTOR = 20;

	private boolean broadcasting = false;
	private boolean enhancedBroadcasting = false;

	@SideOnly(Side.CLIENT)
	public GlowTendril tendril;

	public TileEntityWirelessSource() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			this.loadTendril();
	}

	@SideOnly(Side.CLIENT)
	private void loadTendril() {
		tendril = new GlowTendril(1.25, 6);
	}

	private static final HashSet<WorldLocation> cache = new HashSet();

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			if (world != null) { //tick 6x speed since using world, not render tick
				for (int i = 0; i < 5; i++)
					tendril.update();
			}
			tendril.update();
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (world.isRemote) {
			this.doParticles(world, x, y, z);
		}
		if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}
		if (!world.isRemote && this.isBeacon()) {
			this.broadcastEnergy(world);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		if (!world.isRemote)
			cache.add(new WorldLocation(this));

		this.validateStructure();
	}

	@Override
	public void validateStructure() {
		broadcasting = !worldObj.isRemote && ChromaStructures.getWirelessPedestalStructure(worldObj, xCoord, yCoord, zCoord).matchInWorld();
		enhancedBroadcasting = broadcasting && ChromaStructures.getWirelessPedestalStructure2(worldObj, xCoord, yCoord, zCoord).matchInWorld();
	}

	private void broadcastEnergy(World world) {
		ArrayList<WorldLocation> li = new ArrayList(cache);
		for (int i = 0; i < 16; i++) {
			Collections.shuffle(li);
			for (WorldLocation loc : li) {
				//if (loc.dimensionID == world.provider.dimensionId) { //world specific means tile accelerators in other worlds cannot work
				TileEntity te = loc.getTileEntity(world);
				if (te instanceof TileEntityWirelessSource) {
					if (this.longRangeTransfer((TileEntityWirelessSource)te, CrystalElement.randomElement()))
						break;
				}
				//}
			}
		}
	}

	private boolean longRangeTransfer(TileEntityWirelessSource te, CrystalElement e) {
		if (te.isBeacon())
			return false;
		if (placerUUID != null) {
			if (!placerUUID.equals(te.placerUUID))
				return false;
		}
		int amt = Math.min(this.getEnergy(e), te.getRemainingSpace(e));
		if (amt > 0) {
			amt = Math.min(amt, Math.max(25, amt/8));
			this.drainEnergy(e, amt);
			te.receiveElement(e, amt/(te.worldObj.provider.dimensionId != worldObj.provider.dimensionId ? 4 : 2)); //50% loss + another 50% if cross-dimension
			//ReikaJavaLibrary.pConsole("Moved "+amt+" of "+e+" to "+te+" > "+te.energy);
			te.syncAllData(false);
			return true;
		}
		return false;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		broadcasting = NBT.getBoolean("broadcast");
		enhancedBroadcasting = NBT.getBoolean("broadcast2");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("broadcast", broadcasting);
		NBT.setBoolean("broadcast2", enhancedBroadcasting);
	}

	private void checkAndRequest() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int space = this.getRemainingSpace(e);
			if (space > (this.isBeacon() ? 0 : this.getEnergy(e))) { //50% full, unless beacon, in which case all
				this.requestEnergy(e, space);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		if (rand.nextInt(1+Minecraft.getMinecraft().gameSettings.particleSetting) == 0) {
			int c = CrystalElement.getBlendedColor(this.getTicksExisted()+15, 25);
			double v = ReikaRandomHelper.getRandomBetween(0.03125, 0.125);
			double[] vel = ReikaPhysicsHelper.polarToCartesian(v, rand.nextDouble()*360, rand.nextDouble()*360);
			float g = rand.nextBoolean() ? 0.0625F : -0.0625F;
			EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.5, z+0.5, vel[0], vel[1], vel[2]).setColor(c).setRapidExpand().setGravity(g).setColliding();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		if (broadcasting) {
			int c = CrystalElement.getBlendedColor(this.getTicksExisted()+43, 25);
			double d = (this.getTicksExisted()%80/80D)*6-2.5;
			double px = x-2.5;
			double pz = z+d;
			AttractiveMotionController am = new AttractiveMotionController(this, 0.03125/24, 0.1875, 0.9875);
			EntityBlurFX fx = new EntityBlurFX(world, px, y-3, pz).setColor(c).setAlphaFading().setRapidExpand().setGravity(-0.0625F).setMotionController(am).setLife(80);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);

			px = x+3.5;
			pz = z+1-d;
			fx = new EntityBlurFX(world, px, y-3, pz).setColor(c).setAlphaFading().setRapidExpand().setGravity(-0.0625F).setMotionController(am).setLife(80);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);

			pz = z+3.5;
			px = x+d;
			fx = new EntityBlurFX(world, px, y-3, pz).setColor(c).setAlphaFading().setRapidExpand().setGravity(-0.0625F).setMotionController(am).setLife(80);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);

			pz = z-2.5;
			px = x+1-d;
			fx = new EntityBlurFX(world, px, y-3, pz).setColor(c).setAlphaFading().setRapidExpand().setGravity(-0.0625F).setMotionController(am).setLife(80);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public int getReceiveRange() {
		return 24;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return this.isBeacon() ? enhancedBroadcasting ? 24000 : 9000 : 1000;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return this.isBeacon() ? enhancedBroadcasting ? 720000 : 360000 : 120000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.WIRELESS;
	}

	@Override
	public boolean canTransmitTo(TileEntityWirelessPowered te) {
		return te.getDistanceFrom(xCoord+0.5, yCoord+0.5, zCoord+0.5) <= TRANSMIT_RANGE*TRANSMIT_RANGE;
	}

	@Override
	public int request(CrystalElement e, int amt, int x, int y, int z) {
		int has = this.getEnergy(e);
		int ret = Math.min(amt, has);
		int rem = ret;
		ret /= 1+LOSS_PER_LUMEN;
		if (!PylonFinder.lineOfSight(worldObj, xCoord, yCoord, zCoord, x, y, z))
			ret /= OCCLUSION_FACTOR;
		energy.subtract(e, rem);
		return ret;
	}

	@Override
	public void breakBlock() {
		cache.remove(new WorldLocation(this));
	}

	public boolean isBeacon() {
		return broadcasting;
	}

	@SideOnly(Side.CLIENT)
	public int getRenderColor(float ptick) {
		int t = this.isInWorld() && !StructureRenderer.isRenderingTiles() ? this.getTicksExisted() : Math.abs((int)(System.currentTimeMillis()/50));
		return CrystalElement.getBlendedColor(t, 25);
	}

}
