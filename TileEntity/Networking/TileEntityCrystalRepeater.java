/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalFuse;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.PylonFinder;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCrystalRepeater extends CrystalTransmitterBase implements CrystalRepeater, CrystalFuse, NBTTile, SneakPop, OwnedTile {

	protected ForgeDirection facing = ForgeDirection.DOWN;
	protected boolean hasMultiblock;
	private int depth = -1;
	private boolean isTurbo = false;
	private boolean enhancedStructure = false;

	private CrystalElement surgeColor;
	private int surgeTicks = 0;

	protected int connectionRenderTick = 0;

	private HashSet<WorldLocation> connectableTiles;

	public static final int RANGE = 32;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.REPEATER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (connectionRenderTick > 0) {
			connectionRenderTick--;
			if (connectionRenderTick == 0) {
				connectableTiles = null;
			}
		}

		if (world.isRemote && this.canConduct() && this.isTurbocharged() && this.isEnhancedStructure()) {
			this.doEnhancedStructureParticles(world, x, y, z);
		}

		if (surgeTicks > 0) {
			surgeTicks--;
			if (surgeTicks == 0) {
				this.doSurge();
			}
			if (world.isRemote) {
				this.doSurgingParticles(world, x, y, z);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doEnhancedStructureParticles(World world, int x, int y, int z) {
		double d = Minecraft.getMinecraft().thePlayer.getDistanceSq(x+0.5, y+0.5, z+0.5);
		if (d < 1024 && (d < 256 || ReikaRandomHelper.doWithChance(100*256/d))) {
			int c = this.getActiveColor().getColor();
			double v = ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
			ForgeDirection dir = facing.getOpposite();
			double vx = v*dir.offsetX;
			double vy = v*dir.offsetY;
			double vz = v*dir.offsetZ;
			double dx = x+rand.nextDouble();
			double dy = y+rand.nextDouble();
			double dz = z+rand.nextDouble();
			switch(dir) {
				case EAST:
					dx = x+1;
					break;
				case WEST:
					dx = x;
					break;
				case NORTH:
					dz = z;
					break;
				case SOUTH:
					dz = z+1;
					break;
				case UP:
					dy = y+1;
					break;
				case DOWN:
					dy = y;
					break;
				default:
					break;
			}
			float s = 1+rand.nextFloat();

			EntityFX fx = new EntityBlurFX(world, dx, dy, dz, vx, vy, vz).setColor(c).setNoSlowdown().setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			EntityFX fx2 = new EntityBlurFX(world, dx, dy, dz, vx, vy, vz).setColor(0xffffff).setNoSlowdown().setScale(s/2.5F).lockTo(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.validateStructure();
		//this.checkConnectivity();
	}

	@Override
	public int getSendRange() {
		return RANGE;
	}

	@Override
	public int getReceiveRange() {
		return RANGE;
	}

	@Override
	public int getSignalDegradation() {
		return this.isTurbocharged() ? 0 : 5;
	}

	@Override
	public boolean canConduct() {
		return hasMultiblock;
	}

	public final void validateStructure() {
		hasMultiblock = this.checkForStructure();
		enhancedStructure = hasMultiblock && this.isTurbocharged() && this.checkEnhancedStructure();
		if (!hasMultiblock) {
			CrystalNetworker.instance.breakPaths(this);
		}
		this.syncAllData(false);
	}

	protected boolean checkForStructure() {
		ForgeDirection dir = facing;
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		if (world.getBlock(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ) != ChromaBlocks.RUNE.getBlockInstance())
			return false;
		for (int i = 2; i < 4; i++) {
			int dx = x+dir.offsetX*i;
			int dy = y+dir.offsetY*i;
			int dz = z+dir.offsetZ*i;
			Block id = world.getBlock(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			int m2 = (i == 3 && this.isTurbocharged()) ? StoneTypes.RESORING.ordinal() : 0;
			if (id != ChromaBlocks.PYLONSTRUCT.getBlockInstance() || (meta != 0 && meta != m2))
				return false;
		}
		return true;
	}

	protected boolean checkEnhancedStructure() {
		int dx = xCoord+facing.offsetX*3;
		int dy = yCoord+facing.offsetY*3;
		int dz = zCoord+facing.offsetZ*3;
		return worldObj.getBlockMetadata(dx, dy, dz) == StoneTypes.RESORING.ordinal();
	}

	public void redirect(int side) {
		facing = dirs[side].getOpposite();
		this.validateStructure();
	}

	public boolean findFirstValidSide() {
		for (int i = 0; i < 6; i++) {
			facing = dirs[i];
			this.validateStructure();
			if (hasMultiblock)
				return true;
		}
		return false;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInteger("face")];
		hasMultiblock = NBT.getBoolean("multi");
		depth = NBT.getInteger("depth");
		isTurbo = NBT.getBoolean("turbo");
		enhancedStructure = NBT.getBoolean("enhance");

		surgeTicks = NBT.getInteger("surge");
		surgeColor = CrystalElement.elements[NBT.getInteger("surge_c")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (facing != null)
			NBT.setInteger("face", facing.ordinal());

		NBT.setBoolean("multi", hasMultiblock);
		NBT.setInteger("depth", depth);
		NBT.setBoolean("turbo", isTurbo);
		NBT.setBoolean("enhance", enhancedStructure);

		NBT.setInteger("surge", surgeTicks);
		if (surgeColor != null)
			NBT.setInteger("surge_c", surgeColor.ordinal());
	}

	public final boolean isTurbocharged() {
		return isTurbo;
	}

	public final boolean isEnhancedStructure() {
		return enhancedStructure;
	}

	@Override
	public int maxThroughput() {
		return this.isTurbocharged() ? (this.isEnhancedStructure() ? 12000 :  8000) : 1000;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null && e == this.getActiveColor();
	}

	@Override
	public final int receiveElement(CrystalElement e, int amt) {
		return 1;
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		return null;
	}

	public boolean checkConnectivity() {
		CrystalElement c = this.getActiveColor();
		return c != null && CrystalNetworker.instance.checkConnectivity(c, this);
	}

	public CrystalElement getActiveColor() {
		int dx = xCoord+facing.offsetX;
		int dy = yCoord+facing.offsetY;
		int dz = zCoord+facing.offsetZ;
		return this.canConduct() ? CrystalElement.elements[worldObj.getBlockMetadata(dx, dy, dz)] : null;
	}

	public CrystalSource getEnergySource() {
		CrystalElement e = this.getActiveColor();
		return e != null ? CrystalNetworker.instance.getConnectivity(e, this) : null;
	}

	public void onRelayPlayerCharge(EntityPlayer player, TileEntityCrystalPylon p) {
		if (!worldObj.isRemote) {
			if (!player.capabilities.isCreativeMode && !Chromabilities.PYLON.enabledOn(player) && rand.nextInt(60) == 0)
				p.attackEntityByProxy(player, this);
			CrystalNetworker.instance.makeRequest(this, p.getColor(), 100, this.getReceiveRange(), 1);
		}
	}

	@Override
	public final boolean needsLineOfSight() {
		return true;
	}

	@Override
	public int getSignalDepth(CrystalElement e) {
		return depth;
	}

	@Override
	public void setSignalDepth(CrystalElement e, int d) {
		if (e == this.getActiveColor())
			depth = d;
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		NBT.setBoolean("boosted", isTurbo);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		isTurbo = ReikaItemHelper.matchStacks(is, this.getTile().getCraftedProduct()) && is.stackTagCompound != null && is.stackTagCompound.getBoolean("boosted");
	}

	@Override
	public final void onPathCompleted(CrystalFlow p) {

	}

	@Override
	public final void onPathBroken(CrystalFlow p, FlowFail f) {

	}

	@Override
	public ResearchLevel getResearchTier() {
		return ResearchLevel.NETWORKING;
	}

	@Override
	public final void drop() {
		//ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getTile().getCraftedProduct());
		if (!this.shouldDrop())
			return;
		ItemStack is = this.getTile().getCraftedProduct();
		is.stackTagCompound = new NBTTagCompound();
		this.getTagsToWriteToStack(is.stackTagCompound);
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, is);
		this.delete();
	}

	protected boolean shouldDrop() {
		return true;
	}

	public final boolean canDrop(EntityPlayer ep) {
		return ep.getUniqueID().equals(placerUUID);
	}

	@Override
	public boolean canTransmitTo(CrystalReceiver r) {
		return true;
	}

	public void triggerConnectionRender() {
		if (worldObj.isRemote) {
			connectionRenderTick = 100;
			//connectableTiles = this.getConnectableTilesForRender();
		}
		else {
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.REPEATERCONN.ordinal(), this, 128);
		}
	}

	public int getConnectionRenderAlpha() {
		return connectionRenderTick > 0 ? (connectionRenderTick > 10 ? 255 : 25*connectionRenderTick) : 0;
	}

	public HashSet<WorldLocation> getRenderedConnectableTiles() {
		return connectableTiles;
	}

	@SideOnly(Side.CLIENT)
	/** Does not use the crystal network since is clientside. */
	private final HashSet<WorldLocation> getConnectableTilesForRender(float ptick) {
		HashSet<WorldLocation> c = new HashSet();
		int r = Math.max(this.getReceiveRange(), this.getSendRange());
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					boolean flag = false;
					TileEntity te = worldObj.getTileEntity(xCoord+i, yCoord+j, zCoord+k);
					if (te instanceof CrystalReceiver && ((CrystalNetworkTile)te).canConduct() && this.canTransmitTo((CrystalReceiver)te)) {
						flag = true;
					}
					if (te instanceof CrystalTransmitter && ((CrystalNetworkTile)te).canConduct() && ((CrystalTransmitter)te).canTransmitTo(this)) {
						flag = true;
					}
					if (flag) {
						if (this.getDistanceSqTo(te.xCoord, te.yCoord, te.zCoord) <= r*r) {
							if (!this.needsLineOfSight() || (PylonFinder.lineOfSight(worldObj, xCoord, yCoord, zCoord, te.xCoord, te.yCoord, te.zCoord))) {
								c.add(new WorldLocation(te));
							}
						}
					}
				}
			}
		}
		return c;
	}

	@Override
	public final AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB def = super.getRenderBoundingBox();
		if (def == INFINITE_EXTENT_AABB)
			return def;
		if (connectionRenderTick > 0) {
			return INFINITE_EXTENT_AABB;
		}
		else if (isTurbo) {
			return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(2, 2, 2);
		}
		else {
			return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(0.5, 0.5, 0.5);
		}
	}

	@Override
	public float getFailureWeight(CrystalElement e) {
		return 1.5F;
	}

	@Override
	public final void overload(CrystalElement e) {
		this.startSurge(e);
	}

	private void startSurge(CrystalElement e) {
		ChromaSounds.REPEATERSURGE.playSoundAtBlockNoAttenuation(this, 1, 1, 1024);
		surgeTicks = 55;
		surgeColor = e;
		this.syncAllData(false);
	}

	private void doSurge() {
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.REPEATERSURGE.ordinal(), this, 64, surgeColor.ordinal());
		int y = yCoord-1;
		Block b = worldObj.getBlock(xCoord, y, zCoord);
		while (y > 0 && b == ChromaBlocks.PYLONSTRUCT.getBlockInstance() || b == ChromaBlocks.RUNE.getBlockInstance()) {
			ReikaWorldHelper.dropAndDestroyBlockAt(worldObj, xCoord, y, zCoord, null, true);
			y--;
			b = worldObj.getBlock(xCoord, y, zCoord);
		}
		this.delete();
	}

	@SideOnly(Side.CLIENT)
	private void doSurgingParticles(World world, int x, int y, int z) {
		int n = 1+rand.nextInt(2);
		if (rand.nextInt(10) == 0)
			n = 24+rand.nextInt(24);
		double phi = rand.nextDouble()*360;
		double theta = 2+rand.nextDouble()*86;
		double dx = xCoord+0.5;
		double dy = yCoord+0.5;
		double dz = zCoord+0.5;
		for (int i = 0; i < n; i++) {
			double phi2 = ReikaRandomHelper.getRandomPlusMinus(phi, 2D);
			double theta2 = ReikaRandomHelper.getRandomPlusMinus(theta, 2D);
			double v = ReikaRandomHelper.getRandomBetween(0.25, 0.125);
			float s = 1.5F+rand.nextFloat()*2.5F;
			double[] vxyz = ReikaPhysicsHelper.polarToCartesian(v, theta2, phi2);
			int l = 20+rand.nextInt(80);
			EntityBlurFX fx = new EntityBlurFX(surgeColor, worldObj, dx, dy, dz, vxyz[0], vxyz[1], vxyz[2]).setLife(l).setScale(s).setRapidExpand().setNoSlowdown();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	public static final void overloadClient(World world, int x, int y, int z, CrystalElement e) {
		for (int i = 0; i < 256; i++) {
			double dx = x+rand.nextDouble();
			double dy = y+rand.nextDouble();
			double dz = z+rand.nextDouble();
			double v = 0.25;
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, v);
			float s = 1.5F+rand.nextFloat()*2.5F;
			float g = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			EntityBlurFX fx = new EntityBlurFX(e, world, dx, dy, dz, vx, vy, vz).setLife(200).setScale(s).setGravity(g).setRapidExpand().setNoSlowdown();
			switch(rand.nextInt(3)) {
				case 0:
					break;
				case 1:
					fx.setIcon(ChromaIcons.FLARE);
					break;
				case 2:
					fx.setIcon(ChromaIcons.BIGFLARE);
					break;
			}
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		ChromaSounds.POWERDOWN.playSoundAtBlock(world, x, y, z);
		ReikaSoundHelper.playBreakSound(world, x, y, z, Blocks.glass);
	}

	public void onTransfer(CrystalElement e, int amt) {

	}

}
