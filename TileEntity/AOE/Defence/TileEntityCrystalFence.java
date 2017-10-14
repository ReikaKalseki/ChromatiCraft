/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Defence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityRelayPowered;
import Reika.ChromatiCraft.Block.BlockCrystalFence.CrystalFenceAuxTile;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Perimeter;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.KeySignature;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.Note;

//make very expensive, but aux cheaper
public class TileEntityCrystalFence extends TileEntityRelayPowered implements OwnedTile {

	private final Perimeter fence = new Perimeter().disallowVertical();

	private ForgeDirection outputFace = ForgeDirection.DOWN;

	private HashMap<Integer, Integer> active = new HashMap();

	private boolean controller = true;
	private int mainCount = 1;

	public static final int RANGE = 16;
	public static final int MAX_STEPS = 32;

	private final StepTimer calcTimer = new StepTimer(100);

	private static final int FADE_STEP = 32;
	private static final int FADE_LENGTH = FADE_STEP*4;
	private static final int FADE_START = 512;

	private int chargingTick;

	private boolean[] colorFade;

	private static final ElementTagCompound required = new ElementTagCompound();

	static {
		required.addTag(CrystalElement.RED, 100);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FENCE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		//calcTimer.update();
		//if (calcTimer.checkCap()) {
		//	this.calcFence();
		//}

		if (!world.isRemote && !fence.isEmpty()) {
			if (chargingTick > 0) {
				chargingTick--;
				int t = 5;
				if (this.getTicksExisted()%t == 0) {
					int i = (this.getTicksExisted()/t)%fence.segmentCount();
					this.triggerSegment(i, false);
					Note n = KeySignature.C.getScale().get(i%7);
					ChromaSounds.DING.playSoundAtBlock(this, 1, (float)MusicKey.getByIndex(MusicKey.C5.ordinal()+n.ordinal()+(i/7)*12).getRatio(MusicKey.C5));
				}
			}
			else {
				if (controller && fence.isClosed() && energy.containsAtLeast(required)) {
					this.affectEntities();
				}
			}
		}

		for (Integer key : active.keySet()) {
			if (key != null) {
				int val = active.get(key);
				if (val > 0)
					val -= FADE_STEP;
				active.put(key, val);
			}
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.calcFence();
	}

	public void calcFence() {
		fence.clear();
		fence.addPoint(xCoord, yCoord, zCoord);
		this.findFrom(xCoord, zCoord, outputFace, 0);
		colorFade = new boolean[fence.segmentCount()];
		chargingTick = 600;
	}

	public Perimeter getFence() {
		return fence;
	}

	public boolean isValid() {
		return controller && fence.segmentCount() >= 1 && fence.isClosed();
	}

	private void findFrom(int x, int z, ForgeDirection dir, int step) {
		for (int i = 1; i < RANGE; i++) {
			int dx = x+dir.offsetX*i;
			int dz = z+dir.offsetZ*i;
			TileEntity te = worldObj.getTileEntity(dx, yCoord, dz);
			if (this.isValid()) {
				return;
			}
			else if (step > MAX_STEPS) {
				fence.clear();
				return;
			}
			else if (te == this) {
				fence.addPoint(dx, yCoord, dz);
				controller = true;
				return;
			}
			else if (te instanceof TileEntityCrystalFence) {
				TileEntityCrystalFence fen = (TileEntityCrystalFence)te;
				fen.controller = false;
				mainCount++;
				fence.addPoint(dx, yCoord, dz);
				this.findFrom(dx, dz, fen.outputFace, step+1);
				return;
			}
			else if (te instanceof CrystalFenceAuxTile) {
				CrystalFenceAuxTile fen = (CrystalFenceAuxTile)te;
				if (fen.getInput() == dir.getOpposite()) {
					fence.addPoint(dx, yCoord, dz);
					this.findFrom(dx, dz, fen.getOutput(), step+1);
				}
				else {
					fence.clear();
				}
				return;
			}
		}
	}

	private void affectEntities() {
		ArrayList<AxisAlignedBB> li = fence.getAABBs();
		for (int i = 0; i < li.size(); i++) {
			AxisAlignedBB aabb = li.get(i);
			aabb.minY = yCoord-this.getFenceDepth();
			aabb.maxY = yCoord+1+this.getFenceHeight();
			List<EntityLivingBase> ents = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
			for (EntityLivingBase e : ents) {
				boolean att = true;
				if (e instanceof EntityPlayer) {
					EntityPlayer ep = (EntityPlayer)e;
					if (ep == this.getPlacer())
						att = false;
					else if (ReikaPlayerAPI.isReika(ep))
						att = false;
				}
				if (att) {
					e.attackEntityFrom(DamageSource.cactus, 2+2*mainCount);
					//e.knockBack(null, 0, 0, 0);
					double dx = e.posX-xCoord-0.5;
					double dz = e.posZ-zCoord-0.5;
					double dd = ReikaMathLibrary.py3d(dx, 0, dz);
					e.motionX = dx/dd;
					e.motionY = 0.5;
					e.motionZ = dz/dd;
					e.velocityChanged = true;
					ChromaSounds.DISCHARGE.playSound(e, 1, 1.4F);

					this.triggerSegment(i, true);

					this.drainEnergy(required);
				}
			}
		}
	}

	public void triggerSegment(int i, boolean fadeColor) {
		if (worldObj.isRemote) {
			active.put(i, FADE_START+FADE_LENGTH);
			if (colorFade.length > i)
				colorFade[i] = fadeColor;
		}
		else {
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.FENCETRIGGER.ordinal(), this, 64, i, fadeColor ? 1 : 0);
		}
	}

	public int getSegmentAlpha(int i) {
		Integer get = active.get(i);
		if (get == null)
			return 0;
		if (get.intValue() > FADE_START) {
			return FADE_START-((get.intValue()-FADE_START)*(FADE_START/FADE_LENGTH));
		}
		else {
			return get.intValue();
		}
	}

	public boolean colorFade(int i) {
		return false;
	}

	public int getFenceHeight() {
		return 2;
	}

	public int getFenceDepth() {
		return 0;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setBoolean("enable", controller);
		NBT.setInteger("main", mainCount);

		NBT.setInteger("face", outputFace.ordinal());

		NBT.setInteger("charge", chargingTick);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		controller = NBT.getBoolean("enable");
		mainCount = NBT.getInteger("main");

		outputFace = dirs[NBT.getInteger("face")];

		chargingTick = NBT.getInteger("charge");
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return controller && required.contains(e);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 20000;
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return true;
	}

	public void setFacing(ForgeDirection dir) {
		outputFace = dir;
		this.syncAllData(false);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public boolean renderModelsInPass1() {
		return true;
	}

}
