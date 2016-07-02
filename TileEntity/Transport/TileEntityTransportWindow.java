/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Transport;

import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerTimer;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTransportWindow extends TileEntityChromaticBase implements BreakAction {

	private static final HashSet<BlockKey> acceptedFrames = new HashSet();

	private boolean hasStructure;
	private final WindowTimer cooldowns = new WindowTimer();
	private WorldLocation target;
	private WorldLocation source;

	public boolean renderBackPane = true;
	public boolean renderTexture = true;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.WINDOW;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			if (hasStructure) {
				TileEntityTransportWindow te = this.getTarget();
				if (te != null) {
					ForgeDirection dir = this.getFacing().getOpposite();
					AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).offset(dir.offsetX, dir.offsetY, dir.offsetZ);
					List<EntityPlayer> li = world.getEntitiesWithinAABB(EntityPlayer.class, box);
					for (EntityPlayer ep : li) {
						if (this.canTeleport(dir, ep)) {
							this.teleport(ep, te);
						}
					}
				}
			}
			else {
				cooldowns.clear();
			}

			//ReikaJavaLibrary.pConsole(cooldowns);
			cooldowns.tick(world);
		}
		else {
			this.doParticles(world, x, y, z, meta);
		}
	}

	private void teleport(EntityPlayer ep, TileEntityTransportWindow te) {
		//ReikaJavaLibrary.pConsole("Teleported "+ep);
		te.cooldowns.put(ep, 60);
		cooldowns.put(ep, 60);

		/*
		ForgeDirection dir = te.getFacing().getOpposite();
		//ep.rotationYaw = 180-ep.rotationYaw;
		//ep.rotationYawHead = 180-ep.rotationYawHead;
		ep.rotationYaw = (float)te.getTargetPhi();
		ep.rotationYawHead = ep.rotationYaw;
		ep.setPositionAndUpdate(te.xCoord+0.5+dir.offsetX*0.9, ep.posY+te.yCoord-yCoord+0.01, te.zCoord+0.5+dir.offsetZ*0.9);
		 */
		ReikaEntityHelper.seamlessTeleport(ep, xCoord, yCoord, zCoord, te.xCoord, te.yCoord, te.zCoord, this.getFacing().getOpposite(), te.getFacing().getOpposite());
	}

	private TileEntityTransportWindow getTarget() {
		TileEntity te = target != null ? target.getTileEntity() : null;
		return te instanceof TileEntityTransportWindow ? (TileEntityTransportWindow)te : null;
	}

	private TileEntityTransportWindow getSource() {
		TileEntity te = source != null ? source.getTileEntity() : null;
		return te instanceof TileEntityTransportWindow ? (TileEntityTransportWindow)te : null;
	}

	public void linkTo(TileEntityTransportWindow te) {
		target = new WorldLocation(te);
		te.source = new WorldLocation(this);

		this.syncAllData(true);
		te.syncAllData(true);

		//ReikaJavaLibrary.pConsole("Linking "+this+" to "+te);
	}

	public WorldLocation getTargetLocation() {
		return target;
	}

	public WorldLocation getSourceLocation() {
		return source;
	}

	@Override
	public void breakBlock() {
		this.reset();
	}

	public void reset() {
		TileEntityTransportWindow te = this.getSource();
		if (te != null) {
			te.target = null;
			te.syncAllData(true);
		}

		te = this.getTarget();
		if (te != null) {
			te.source = null;
			te.syncAllData(true);
		}
		target = null;

		this.syncAllData(true);
	}

	@Override
	public void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.validateStructure();
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public ForgeDirection getFacing() {
		switch(this.getBlockMetadata()) {
			case 0:
				return ForgeDirection.EAST;
			case 1:
				return ForgeDirection.WEST;
			case 2:
				return ForgeDirection.SOUTH;
			case 3:
				return ForgeDirection.NORTH;
			default:
				return ForgeDirection.UNKNOWN;
		}
	}

	public void setFacing(ForgeDirection dir) {
		switch(dir) {
			case EAST:
				this.setBlockMetadata(0);
				break;
			case WEST:
				this.setBlockMetadata(1);
				break;
			case SOUTH:
				this.setBlockMetadata(2);
				break;
			case NORTH:
				this.setBlockMetadata(3);
				break;
			default:
				break;
		}
	}

	public boolean doRender() {
		return hasStructure && renderBackPane;
	}

	private boolean canTeleport(ForgeDirection dir, EntityPlayer ep) {
		if (cooldowns.containsKey(ep))
			return false;
		return this.canTeleportPosition(dir, ep);
	}

	private boolean canTeleportPosition(ForgeDirection dir, EntityPlayer ep) {
		double dx = Math.abs(ep.posX-xCoord-0.5);
		double dy = Math.abs(ep.posY+1.62-yCoord-0.5);
		double dz = Math.abs(ep.posZ-zCoord-0.5);
		double md = 0.82;
		double mw = 0.05;
		//ReikaJavaLibrary.pConsole(String.format("%.2f, %.2f, %.2f", dx, dy, dz));
		boolean dist = dx < (dir.offsetX == 0 ? mw : md) && dy < 0.15 && dz < (dir.offsetZ == 0 ? mw : md);
		if (!dist)
			return false;
		double phi = ep.rotationYawHead;
		while (phi < 0)
			phi += 360;
		while (phi >= 360)
			phi -= 360;
		double phit = this.getTargetPhi();
		//ReikaJavaLibrary.pConsole(phi+" : "+phit);
		return (ReikaMathLibrary.approxr(phi, phit, 5) || ReikaMathLibrary.approxr(phi, phit+360, 5));
	}

	private double getTargetPhi() {
		switch(this.getBlockMetadata()) {
			case 0:
				return 270;
			case 1:
				return 90;
			case 2:
				return 0;
			case 3:
				return 180;
		}
		return 0;
	}

	public void validateStructure() {
		hasStructure = this.checkStructure(worldObj, xCoord, yCoord, zCoord);
		this.syncAllData(true);
	}

	private boolean checkStructure(World world, int x, int y, int z) {
		if (!acceptedFrames.contains(BlockKey.getAt(world, x, y+1, z)) || !acceptedFrames.contains(BlockKey.getAt(world, x, y-1, z)))
			return false;
		if (this.getFacing().offsetX != 0) {
			for (int i = -1; i <= 1; i++) {
				if (!acceptedFrames.contains(BlockKey.getAt(world, x, y+i, z-1)) || !acceptedFrames.contains(BlockKey.getAt(world, x, y+i, z+1)))
					return false;
			}
		}
		else {
			for (int i = -1; i <= 1; i++) {
				if (!acceptedFrames.contains(BlockKey.getAt(world, x-1, y+i, z)) || !acceptedFrames.contains(BlockKey.getAt(world, x+1, y+i, z)))
					return false;
			}
		}
		return true;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);

		if (target != null)
			target.writeToNBT("tgt", NBT);
		if (source != null)
			source.writeToNBT("src", NBT);

		NBT.setBoolean("back", renderBackPane);
		NBT.setBoolean("tex", renderTexture);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("struct");

		source = WorldLocation.readFromNBT("src", NBT);
		target = WorldLocation.readFromNBT("tgt", NBT);

		renderBackPane = NBT.getBoolean("back");
		renderTexture = NBT.getBoolean("tex");
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return ChromaTiles.getTileFromIDandMetadata(oldBlock, oldMeta) != ChromaTiles.getTileFromIDandMetadata(newBlock, newMeta);
	}

	static {
		acceptedFrames.add(new BlockKey(Blocks.bedrock));
		acceptedFrames.add(new BlockKey(Blocks.obsidian));
		acceptedFrames.add(new BlockKey(ChromaBlocks.PYLONSTRUCT.getBlockInstance()));
		acceptedFrames.add(new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.ordinal()));
		acceptedFrames.add(new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata));
		acceptedFrames.add(new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.ordinal()));
		acceptedFrames.add(new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata));
		acceptedFrames.add(new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.ordinal()));
		acceptedFrames.add(new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata));
		acceptedFrames.add(new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.MOSS.ordinal()));
		acceptedFrames.add(new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.MOSS.metadata));
		acceptedFrames.add(new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.ordinal()));
		acceptedFrames.add(new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata));
		acceptedFrames.add(new BlockKey(ChromaBlocks.SPECIALSHIELD.getBlockInstance()));
	}

	private class WindowTimer extends PlayerTimer {

		@Override
		protected boolean shouldTickPlayer(EntityPlayer ep) {
			return TileEntityTransportWindow.this.canTeleportPosition(TileEntityTransportWindow.this.getFacing(), ep);
		}

	}

}
