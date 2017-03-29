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

import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Magic.Interfaces.ConnectivityAction;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Network.CrystalPath;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCompoundRepeater extends TileEntityCrystalRepeater implements ConnectivityAction {

	private final EnumMap<CrystalElement, Integer> depth = new EnumMap(CrystalElement.class);

	private boolean connectedToPylon = false;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (world.isRemote && this.canConduct())
			this.particles(world, x, y, z);
		//ReikaJavaLibrary.pConsole(colorTimer+":"+this.getSide()+">"+this.getActiveColor());
	}


	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		/* Not performant
		Collection<TileEntityCrystalPylon> c = CrystalNetworker.instance.getAllNearbyPylons(this, this.getReceiveRange());
		for (TileEntityCrystalPylon te : c) {
			if (te.canConduct() && PylonFinder.lineOfSight(world, x, y, z, te.xCoord, te.yCoord, te.zCoord)) {
				connectedToPylon = true;
				break;
			}
		}
		 */
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);
	}

	@SideOnly(Side.CLIENT)
	private void particles(World world, int x, int y, int z) {
		if (this.getColorCycleTick()%32 == 5) {
			double px = x+0.5;//rand.nextDouble();
			double py = y+0.5;//0.25+y+rand.nextDouble();
			double pz = z+0.5;//rand.nextDouble();
			CrystalElement e = this.getParticleColor(); //compensate for particle delay
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityRuneFX(world, px, py, pz, 0, 0, 0, e).setScale(5).setFading());
		}
	}

	private CrystalElement getParticleColor() {
		return this.getRenderColorWithOffset(64);
	}

	public CrystalElement getRenderColorWithOffset(int i) {
		return CrystalElement.elements[((this.getColorCycleTick()+i)/32)%16];
	}

	public int getColorCycleTick() {
		return (int)((worldObj.getTotalWorldTime()+xCoord/8D%16+zCoord/8D%16)%512D);//this.getTicksExisted();
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null && this.canConduct();
	}

	@Override
	public int maxThroughput() {
		return this.isTurbocharged() ? (this.isEnhancedStructure() ? 20000 : 12000) : 1000;
	}

	@Override
	public int getSignalDegradation() {
		return this.isTurbocharged() ? (this.isEnhancedStructure() ? 10 : 20) : 100;
	}

	@Override
	protected boolean checkForStructure() {
		for (int i = 1; i <= 5; i++) {
			int dx = xCoord+facing.offsetX*i;
			int dy = yCoord+facing.offsetY*i;
			int dz = zCoord+facing.offsetZ*i;
			Block b = worldObj.getBlock(dx, dy, dz);
			int meta = worldObj.getBlockMetadata(dx, dy, dz);
			int m2 = i == 3 ? 13 : i == 1 || i == 5 ? 12 : this.getColumnBeam();
			int m2b = m2 == this.getColumnBeam() && this.isTurbocharged() ? StoneTypes.list[m2].getGlowingVariant().ordinal() : m2;
			if (b != ChromaBlocks.PYLONSTRUCT.getBlockInstance() || (meta != m2 && meta != m2b)) {
				return false;
			}
		}
		return true;
	}

	private int getColumnBeam() {
		return facing.offsetY == 0 ? 1 : 2;
	}

	@Override
	protected boolean checkEnhancedStructure() {
		for (int i = 2; i <= 4; i += 2) {
			int dx = xCoord+facing.offsetX*i;
			int dy = yCoord+facing.offsetY*i;
			int dz = zCoord+facing.offsetZ*i;
			Block b = worldObj.getBlock(dx, dy, dz);
			int meta = worldObj.getBlockMetadata(dx, dy, dz);
			if (b != ChromaBlocks.PYLONSTRUCT.getBlockInstance() || meta != StoneTypes.list[this.getColumnBeam()].getGlowingVariant().ordinal()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.COMPOUND;
	}
	/*
	@Override
	public boolean checkConnectivity() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if (CrystalNetworker.instance.checkConnectivity(e, worldObj, xCoord, yCoord, zCoord, this.getReceiveRange()))
				return true;
		}
		return false;
	}
	 */
	@Override
	public CrystalElement getActiveColor() {
		return CrystalElement.elements[(this.getRenderColorWithOffset(7).ordinal()+2)%16];
	}
	/*
	@Override
	public CrystalSource getEnergySource(CrystalElement e) {
		return e != null ? CrystalNetworker.instance.getConnectivity(e, worldObj, xCoord, yCoord, zCoord, this.getReceiveRange()) : null;
	}*/

	/*
	@Override
	public void onRelayPlayerCharge(EntityPlayer player, TileEntityCrystalPylon p) {
		if (!worldObj.isRemote) {
			if (!player.capabilities.isCreativeMode && !Chromabilities.PYLON.enabledOn(player) && rand.nextInt(20) == 0)
				p.attackEntityByProxy(player, this);
			CrystalNetworker.instance.makeRequest(this, p.getColor(), 15000, this.getReceiveRange());
		}
	}
	 */

	@Override
	public void setSignalDepth(CrystalElement e, int d) {
		depth.put(e, d);
	}

	@Override
	public int getSignalDepth(CrystalElement e) {
		Integer d = depth.get(e);
		return d != null ? d.intValue() : -1;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			String s = "depth_"+e.ordinal();
			if (NBT.hasKey(s))
				depth.put(e, NBT.getInteger(s));
		}

		connectedToPylon = NBT.getBoolean("pylon");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		for (CrystalElement e : depth.keySet())
			NBT.setInteger("depth_"+e.ordinal(), depth.get(e));

		NBT.setBoolean("pylon", connectedToPylon);
	}

	@Override
	public float getFailureWeight(CrystalElement e) {
		return 1.25F;
	}

	@Override
	public int getPathPriority() {
		return -10;
	}

	@Override
	public void notifySendingTo(CrystalPath p, CrystalReceiver r) {

	}

	@Override
	public void notifyReceivingFrom(CrystalPath p, CrystalTransmitter t) {
		if (t instanceof TileEntityCrystalPylon) {
			p.addBaseAttenuation(1000); //== 50 multis
			connectedToPylon = true;
		}
		else {
			connectedToPylon = false;
		}
	}

	public boolean connectedToPylon() {
		return connectedToPylon;
	}

}
