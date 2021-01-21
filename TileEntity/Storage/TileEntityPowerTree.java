/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityCrystalBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityWirelessPowered;
import Reika.ChromatiCraft.Block.Crystal.BlockPowerTree.TileEntityPowerTreeAux;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.CrystalTarget.TickingCrystalTarget;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalBattery;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.WirelessSource;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ProgressionCatchupHandling;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityPowerTree extends CrystalReceiverBase implements CrystalBattery, OwnedTile, WirelessSource, MultiBlockChromaTile {

	private static final EnumMap<CrystalElement, BlockVector> origins = new EnumMap(CrystalElement.class);
	private static final EnumMap<CrystalElement, Integer> yOffsets = new EnumMap(CrystalElement.class);
	private static final EnumMap<CrystalElement, ArrayList<Coordinate>> locations = new EnumMap(CrystalElement.class);
	private static final ArrayList<Integer> directions = new ArrayList();

	private final EnumMap<CrystalElement, Integer> growth = new EnumMap(CrystalElement.class);
	private final EnumMap<CrystalElement, Integer> steps = new EnumMap(CrystalElement.class);
	private final EnumMap<CrystalElement, Integer> boost = new EnumMap(CrystalElement.class);

	private ArrayList<CrystalTarget> targets = new ArrayList(); //need to reset some way
	private ArrayList<TickingCrystalTarget> tickingTargets = new ArrayList();

	private boolean hasMultiblock = false;

	private boolean enhanced = false;
	private boolean hadEnhancedProgress = false;

	private boolean canSendEnergy = true;

	public static final int BASE = 1000;
	public static final int RATIO = 4000;
	public static final int POWER = 3;
	public static final int POWER_TURBO = 4;

	public static final int BOOST_LENGTH = 5400; //4m 30s

	static {
		addOrigin(CrystalElement.WHITE, 	new BlockVector(ForgeDirection.NORTH, 1, -3, -2));
		addOrigin(CrystalElement.BLACK, 	new BlockVector(ForgeDirection.NORTH, 1, -9, -2));
		addOrigin(CrystalElement.RED, 		new BlockVector(ForgeDirection.EAST, 2, -9, 0));
		addOrigin(CrystalElement.GREEN, 	new BlockVector(ForgeDirection.SOUTH, 0, -5, 1));
		addOrigin(CrystalElement.BROWN, 	new BlockVector(ForgeDirection.WEST, -1, -7, -1));
		addOrigin(CrystalElement.BLUE, 		new BlockVector(ForgeDirection.EAST, 2, -3, 0));
		addOrigin(CrystalElement.PURPLE, 	new BlockVector(ForgeDirection.EAST, 2, -5, 0));
		addOrigin(CrystalElement.CYAN, 		new BlockVector(ForgeDirection.SOUTH, 0, -3, 1));
		addOrigin(CrystalElement.LIGHTGRAY, new BlockVector(ForgeDirection.NORTH, 1, -5, -2));
		addOrigin(CrystalElement.GRAY, 		new BlockVector(ForgeDirection.NORTH, 1, -7, -2));
		addOrigin(CrystalElement.PINK, 		new BlockVector(ForgeDirection.WEST, -1, -5, -1));
		addOrigin(CrystalElement.LIME, 		new BlockVector(ForgeDirection.SOUTH, 0, -7, 1));
		addOrigin(CrystalElement.YELLOW, 	new BlockVector(ForgeDirection.SOUTH, 0, -9, 1));
		addOrigin(CrystalElement.LIGHTBLUE, new BlockVector(ForgeDirection.WEST, -1, -3, -1));
		addOrigin(CrystalElement.MAGENTA, 	new BlockVector(ForgeDirection.EAST, 2, -7, 0));
		addOrigin(CrystalElement.ORANGE, 	new BlockVector(ForgeDirection.WEST, -1, -9, -1));

		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(90); //grows rightward
		directions.add(0);
		directions.add(90);
		directions.add(0);
		directions.add(0);
		directions.add(90); //grows rightward

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];

			BlockVector bv = origins.get(e);
			ForgeDirection dir = bv.direction;
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
			int x = bv.xCoord;
			int y = bv.yCoord;
			int z = bv.zCoord;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x -= left.offsetX;
			y -= left.offsetY;
			z -= left.offsetZ;
			y++;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x = bv.xCoord;
			y = bv.yCoord;
			z = bv.zCoord;
			x += dir.offsetX;
			y += dir.offsetY;
			z += dir.offsetZ;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x -= left.offsetX;
			y -= left.offsetY;
			z -= left.offsetZ;
			y++;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x = bv.xCoord;
			y = bv.yCoord;
			z = bv.zCoord;
			x -= left.offsetX;
			y -= left.offsetY;
			z -= left.offsetZ;
			addLeaf(e, x, y, z);

			x += dir.offsetX;
			y += dir.offsetY;
			z += dir.offsetZ;
			addLeaf(e, x, y, z);

			x -= dir.offsetX;
			y -= dir.offsetY;
			z -= dir.offsetZ;
			y++;
			addLeaf(e, x, y, z);

			x = bv.xCoord;
			y = bv.yCoord;
			z = bv.zCoord;
			x += dir.offsetX*2;
			y += dir.offsetY*2;
			z += dir.offsetZ*2;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x = bv.xCoord;
			y = bv.yCoord;
			z = bv.zCoord;
			x -= left.offsetX*2;
			y -= left.offsetY*2;
			z -= left.offsetZ*2;
			addLeaf(e, x, y, z);
		}
	}

	public TileEntityPowerTree() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			growth.put(e, 0);
		}
	}

	private static void addOrigin(CrystalElement e, BlockVector vec) {
		origins.put(e, vec);
		yOffsets.put(e, vec.yCoord);
	}

	private static void addLeaf(CrystalElement e, int x, int y, int z) {
		ArrayList<Coordinate> li = locations.get(e);
		if (li == null) {
			li = new ArrayList();
			locations.put(e, li);
		}
		li.add(new Coordinate(x, y, z));
	}

	/** Returns relative to TILE location */
	public static Coordinate getLeafLocation(CrystalElement e, int index) {
		return locations.get(e).get(index);
	}

	public static int maxLeafCount(CrystalElement e) {
		return locations.get(e).size();
	}

	public static ForgeDirection getDirection(CrystalElement e) {
		return origins.get(e).direction;
	}

	public static int getYOffset(CrystalElement e) {
		return yOffsets.get(e);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (world.isRemote) {
			if (!targets.isEmpty()) {
				//this.spawnBeamParticles(world, x, y, z);
				ChromaFX.drawLeyLineParticles(world, x, y, z, this.getOutgoingBeamRadius(), targets);
			}
			this.doParticles(world, x, y, z);
		}

		//ChromaStructures.getTreeStructure(world, x, y, z).place();

		if (this.canConduct()) {
			if (!world.isRemote) {
				for (int i = 0; i < 16; i++) {
					CrystalElement e = CrystalElement.elements[i];
					boolean acc = this.growAndTickBoost(e);
					int base = acc ? 30 : 150;
					if (rand.nextInt(base*16) == 0) {
						this.grow(e);
					}
				}

				if (DragonAPICore.debugtest) {
					for (int i = 0; i < 64; i++)
						this.grow(CrystalElement.randomElement());
				}

				if (rand.nextInt(100) == 0) {
					for (int i = 0; i < CrystalElement.elements.length; i++) {
						CrystalElement e = CrystalElement.elements[i];
						if (this.getRemainingSpace(e) > 0) {
							this.requestEnergy(e, this.getRemainingSpace(e));
						}
					}
				}

				if (this.getTicksExisted()%40 == 0)
					this.checkHasSendFocus();
			}
			else {
				ProgressionCatchupHandling.instance.attemptSync(this, 18, ProgressStage.POWERTREE);
			}

			for (EntityPlayer ep : this.getOwners(false)) {
				if (Chromabilities.RECHARGE.enabledOn(ep)) {
					if (TileEntityAuraPoint.hasAuraPoints(ep) || ep.getDistanceSq(x+0.5, y+0.5, z+0.5) <= 2048) {
						CrystalElement e = CrystalElement.elements[(this.getTicksExisted()/16)%16];
						if (this.getEnergy(e) > 0) {
							int cap = PlayerElementBuffer.instance.getElementCap(ep);
							int space = cap-PlayerElementBuffer.instance.getPlayerContent(ep, e);
							if (space > 0) {
								int amt = Math.min(this.getEnergy(e), Math.min(space, Math.max(cap/24/8, 1+rand.nextInt(Math.max(1, space/4*4)))));
								//ReikaJavaLibrary.pConsole(e+":"+amt);
								ChromaAux.chargePlayerFromPylon(ep, this, e, amt);
							}
						}
					}
				}
			}
		}

		this.tickTargets();
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		if (canSendEnergy) {
			double dx = x+rand.nextDouble()*2;
			double dy = y+0.9375+rand.nextDouble()*0.125;
			double dz = z+rand.nextDouble()*2-1;
			EntityCCBlurFX fx = new EntityCCBlurFX(world, dx, dy, dz);
			int c = CrystalElement.getBlendedColor(this.getTicksExisted(), 40);
			float g = -(float)ReikaRandomHelper.getRandomBetween(0.03125, 0.0625);
			float s = (float)ReikaRandomHelper.getRandomBetween(1.5, 3);
			fx.setIcon(ChromaIcons.FLARE).setColor(c).setGravity(g).setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			EntityCCBlurFX fx2 = new EntityCCBlurFX(world, dx, dy, dz);
			fx2.setIcon(ChromaIcons.FADE_GENTLE).setColor(ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.7F)).setGravity(g).setScale(s).lockTo(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	private void tickTargets() {
		if (!tickingTargets.isEmpty()) {
			Iterator<TickingCrystalTarget> it = tickingTargets.iterator();
			while (it.hasNext()) {
				TickingCrystalTarget t = it.next();
				if (t.tick()) {
					it.remove();
					targets.remove(t);
					this.syncAllData(true);
				}
			}
		}
	}

	private boolean growAndTickBoost(CrystalElement e) {
		Integer get = boost.get(e);
		if (get != null && get.intValue() > 0) {
			get--;
			boost.put(e, get);
			return true;
		}
		else if (rand.nextInt(20) == 0 && this.findBoost(e)) {
			boost.put(e, BOOST_LENGTH);
			return true;
		}
		return false;
	}

	private boolean findBoost(CrystalElement e) {
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord-1, xCoord+2, yCoord+1, zCoord+1).expand(7, 7, 7).offset(0, -6, 0);
		List<EntityItem> li = worldObj.getEntitiesWithinAABB(EntityItem.class, box);
		for (EntityItem ei : li) {
			if (!ei.isDead && ei.delayBeforeCanPickup == 0) {
				ItemStack is = ei.getEntityItem();
				if (is.stackSize > 0 && ReikaItemHelper.matchStacks(is, ChromaItems.ELEMENTAL.getStackOf(e))) {
					is.stackSize--;
					if (is.stackSize > 0)
						ei.setEntityItemStack(is);
					else
						ei.setDead();
					return true;
				}
			}
		}
		return false;
	}

	/*
	@SideOnly(Side.CLIENT)
	private void spawnBeamParticles(World world, int x, int y, int z) {
		int p = Minecraft.getMinecraft().gameSettings.particleSetting;
		if (rand.nextInt(1+p*2) == 0) {
			for (CrystalTarget tg : targets) {
				double dx = tg.location.xCoord+tg.offsetX-x;
				double dy = tg.location.yCoord+tg.offsetY-y;
				double dz = tg.location.zCoord+tg.offsetZ-z;
				double dd = ReikaMathLibrary.py3d(dx, dy, dz);
				double dr = rand.nextDouble();
				double px = dx*dr+x+0.5;
				double py = dy*dr+y+0.5;
				double pz = dz*dr+z+0.5;
				EntityLaserFX fx = new EntityLaserFX(tg.color, world, px, py, pz).setScale(15);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}
	 */
	@Override
	public void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		if (!world.isRemote)
			this.validateStructure();
		targets.clear();
	}

	public void validateStructure() {
		ChromaStructures.TREE.getStructure().resetToDefaults();
		FilledBlockArray f = ChromaStructures.TREE.getArray(worldObj, xCoord, yCoord, zCoord);
		boolean flag = f.matchInWorld();
		if (!flag && hasMultiblock) {
			this.onBreakMultiblock();
		}
		hasMultiblock = flag;
		EntityPlayer ep = this.getPlacer();
		if (ep != null && !ReikaPlayerAPI.isFake(ep)) {
			hadEnhancedProgress = ProgressStage.CTM.isPlayerAtStage(ep);
		}
		enhanced = hasMultiblock && hadEnhancedProgress && ChromaStructures.TREE_BOOSTED.getArray(worldObj, xCoord, yCoord, zCoord).matchInWorld();
		this.checkHasSendFocus();
		for (int i = 0; i < 16; i++)
			this.clamp(CrystalElement.elements[i]);
		this.syncAllData(true);
	}

	private void checkHasSendFocus() {
		ChromaStructures.TREE_SENDER.getStructure().resetToDefaults();
		canSendEnergy = hasMultiblock && ChromaStructures.TREE_SENDER.getArray(worldObj, xCoord, yCoord, zCoord).matchInWorld();
	}

	public boolean hasMultiBlock() {
		return hasMultiblock;
	}

	private void onBreakMultiblock() {
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			BlockVector c = origins.get(e);
			worldObj.setBlock(xCoord+c.xCoord, yCoord+c.yCoord, zCoord+c.zCoord, Blocks.air);
		}
		energy.clear();
	}

	private void grow(CrystalElement e) {
		int stage = growth.get(e);
		ArrayList<Coordinate> li = locations.get(e);
		//ReikaJavaLibrary.pConsole(e+" : "+this.getEnergy(e)+" > "+(this.getEnergy(e)*100L)+" > "+(this.getEnergy(e)*100L/this.getMaxStorage(e)));
		if (this.getEnergy(e)*100L/this.getMaxStorage(e) >= 95) {
			//ReikaJavaLibrary.pConsole(e+":"+growth.get(e)+">"+this.getMaxStorage(e));
			if (stage < li.size()) {
				Coordinate c = li.get(stage).offset(xCoord, yCoord, zCoord);
				Block b = c.getBlock(worldObj);
				//ReikaJavaLibrary.pConsole(e+": "+stage+" > "+b);
				if (b == ChromaBlocks.POWERTREE.getBlockInstance()) {
					TileEntityPowerTreeAux te = (TileEntityPowerTreeAux)c.getTileEntity(worldObj);
					if (te.grow()) {
						steps.put(e, te.getGrowth());
					}
					else {
						stage++;
						growth.put(e, stage);
						steps.put(e, 0);
					}
				}
				else if (ReikaWorldHelper.softBlocks(worldObj, c.xCoord, c.yCoord, c.zCoord)) {
					c.setBlock(worldObj, ChromaBlocks.POWERTREE.getBlockInstance(), e.ordinal());
					TileEntityPowerTreeAux te = (TileEntityPowerTreeAux)c.getTileEntity(worldObj);
					int dir = stage < directions.size() ? directions.get(stage) : -1;
					if (dir >= 0) {
						ForgeDirection direc = origins.get(e).direction;
						if (dir == 90) {
							direc = ReikaDirectionHelper.getRightBy90(direc);
						}
						else if (dir == -90) {
							direc = ReikaDirectionHelper.getLeftBy90(direc);
						}
						else if (dir == 180) {
							direc = direc.getOpposite();
						}
						te.setDirection(direc);
					}
					te.setOrigin(this);
					steps.put(e, 0);
				}
			}
			else {
				steps.put(e, 0);
			}
		}
		else if (stage > 0 ? energy.getValue(e) <= this.getStorageForStep(e, stage-1) : energy.getValue(e) <= 1000) {
			if (stage < li.size()) {
				Coordinate c = li.get(stage).offset(xCoord, yCoord, zCoord);
				Block b = c.getBlock(worldObj);
				//ReikaJavaLibrary.pConsole(e+": "+stage+" > "+b);
				if (b == ChromaBlocks.POWERTREE.getBlockInstance()) {
					TileEntityPowerTreeAux te = (TileEntityPowerTreeAux)c.getTileEntity(worldObj);
					if (te.ungrow()) {
						steps.put(e, te.getGrowth());
					}
					else {
						c.setBlock(worldObj, Blocks.air);
						if (stage > 0)
							stage--;
						growth.put(e, stage);
						steps.put(e, 0);
					}
				}
			}
			else {
				if (stage > 0)
					stage--;
				growth.put(e, stage);
			}
		}
		this.syncAllData(true);
		HashSet<CrystalElement> set = new HashSet();
		for (int i = 0; i < 16; i++) {
			CrystalElement e2 = CrystalElement.elements[i];
			if (growth.get(e2) > locations.get(e2).size()/2) {
				set.add(e2);
			}
		}
		if (set.size() >= 12 && set.contains(CrystalElement.BLACK) && set.contains(CrystalElement.YELLOW) && set.contains(CrystalElement.PURPLE) && set.contains(CrystalElement.LIGHTBLUE)) {
			ProgressStage.POWERTREE.stepPlayerTo(this.getPlacer());
		}
	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null && energy.getValue(e) > 0;
	}

	@Override
	public int maxThroughput() {
		return this.isEnhanced() ? 90000 : 12000;
	}

	@Override
	public boolean canConduct() {
		return hasMultiblock && this.isOnTop() && worldObj.canBlockSeeTheSky(xCoord, yCoord+1, zCoord);
	}

	private boolean isOnTop() {
		return worldObj.getBlock(xCoord, yCoord+1, zCoord).isAir(worldObj, xCoord, yCoord+1, zCoord);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		if (!this.canConduct())
			return 0;
		int s = growth.get(e);
		return this.getStorageForStep(e, s);
	}

	private int getStorageForStep(CrystalElement e, int step) {
		return BASE+ReikaMathLibrary.intpow2(step, (this.isEnhanced() ? POWER_TURBO : POWER))*RATIO;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.POWERTREE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	/*
	@Override
	public int getTransmissionStrength() {
		return this.isEnhanced() ? 1000 : 100;
	}
	 */

	public boolean isEnhanced() {
		return enhanced;
	}

	@Override
	public boolean drain(CrystalElement e, int amt) {
		if (energy.containsAtLeast(e, amt)) {
			this.drainEnergy(e, amt);
			this.grow(e);
			return true;
		}
		return false;
	}

	@Override
	public int getSendRange() {
		return this.getReceiveRange();
	}

	@Override
	public final void addTarget(WorldLocation loc, CrystalElement e, double dx, double dy, double dz, double w) {
		CrystalTarget tg = new CrystalTarget(this, loc, e, dx, dy, dz, w);
		if (!worldObj.isRemote) {
			if (!targets.contains(tg))
				targets.add(tg);
			this.onTargetChanged();
		}
	}

	@Override
	public final void addSelfTickingTarget(WorldLocation loc, CrystalElement e, double dx, double dy, double dz, double w, int duration) {
		TickingCrystalTarget tg = new TickingCrystalTarget(this, loc, e, dx, dy, dz, w, duration);
		if (!worldObj.isRemote) {
			if (!targets.contains(tg)) {
				targets.add(tg);
				tickingTargets.add(tg);
			}
			this.onTargetChanged();
		}
	}

	public final void removeTarget(WorldLocation loc, CrystalElement e) {
		if (!worldObj.isRemote) {
			//ReikaJavaLibrary.pConsole(this+":"+targets.size()+":"+targets);
			targets.remove(new CrystalTarget(this, loc, e, 0));
			this.onTargetChanged();
			//ReikaJavaLibrary.pConsole(this+":"+targets.size()+":"+targets);
		}
	}

	public final void clearTargets(boolean unload) {
		if (!worldObj.isRemote) {
			targets.clear();
			if (!unload)
				this.onTargetChanged();
		}
	}

	private void onTargetChanged() {
		this.syncAllData(true);
	}

	@Override
	public boolean needsLineOfSightToReceiver(CrystalReceiver r) {
		return true;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasMultiblock = NBT.getBoolean("multi");

		enhanced = NBT.getBoolean("boosted");
		canSendEnergy = NBT.getBoolean("send");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("multi", hasMultiblock);

		NBT.setBoolean("boosted", enhanced);
		NBT.setBoolean("send", canSendEnergy);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			steps.put(e, NBT.getInteger("step"+i));
			growth.put(e, NBT.getInteger("grow"+i));
			boost.put(e, NBT.getInteger("boost"+i));
		}

		targets = new ArrayList();
		int num = NBT.getInteger("targetcount");
		for (int i = 0; i < num; i++) {
			CrystalTarget tg = CrystalTarget.readFromNBT("target"+i, NBT);
			if (tg != null)
				targets.add(tg);
		}

		hadEnhancedProgress = NBT.getBoolean("progress");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			NBT.setInteger("step"+i, steps.containsKey(e) ? steps.get(e) : 0);
			NBT.setInteger("grow"+i, growth.containsKey(e) ? growth.get(e) : 0);
			NBT.setInteger("boost"+i, boost.containsKey(e) ? boost.get(e) : 0);
		}

		NBT.setInteger("targetcount", targets.size());
		for (int i = 0; i < targets.size(); i++)
			targets.get(i).writeToNBT("target"+i, NBT);

		NBT.setBoolean("progress", hadEnhancedProgress);
	}

	public void onBreakLeaf(World world, int x, int y, int z, CrystalElement e) {
		Coordinate c = new Coordinate(x-xCoord, y-yCoord, z-zCoord);
		ArrayList<Coordinate> li = locations.get(e);
		int index = li.indexOf(c);
		if (index >= 0) {
			growth.put(e, Math.min(growth.get(e), index));
			steps.put(e, 0);
			this.clamp(e);
			for (int i = index+1; i < li.size(); i++) {
				Coordinate c2 = li.get(i).offset(xCoord, yCoord, zCoord);
				ArrayList<ItemStack> items = c2.getBlock(world).getDrops(world, c2.xCoord, c2.yCoord, c2.zCoord, c2.getBlockMetadata(world), 0);
				ReikaItemHelper.dropItems(world, c2.xCoord+rand.nextDouble(), c2.yCoord+rand.nextDouble(), c2.zCoord+rand.nextDouble(), items);
				c2.setBlock(world, Blocks.air);
				ReikaSoundHelper.playBreakSound(world, c2.xCoord, c2.yCoord, c2.zCoord, Blocks.glass);
			}
		}
		else {

		}
	}

	public Collection<CrystalTarget> getTargets() {
		return Collections.unmodifiableCollection(targets);
	}

	@Override
	public boolean canSupply(CrystalReceiver te, CrystalElement e) {
		if (!canSendEnergy)
			return false;
		return !(te instanceof TileEntityPowerTree) && this.getEnergy(e) >= 60000;
	}

	@Override
	public boolean canTransmitTo(CrystalReceiver te) {
		return true;
	}

	@Override
	public void onUsedBy(EntityPlayer ep, CrystalElement e) {
		World world = worldObj;
		Coordinate c = locations.get(e).get(rand.nextInt(locations.get(e).size()));
		c = c.offset(xCoord, yCoord, zCoord);
		int x = c.xCoord;
		int y = c.yCoord;
		int z = c.zCoord;
		int r = e.getRed();
		int g = e.getGreen();
		int b = e.getBlue();
		ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.COLOREDPARTICLE.ordinal(), world, x, y, z, 64, r, g, b, 32, 8);
		int mod = (int)(world.getTotalWorldTime()%16);
		if (mod == 0) {
			ChromaSounds.DING.playSoundAtBlock(world, x, y, z, 1, (float)CrystalMusicManager.instance.getDingPitchScale(e));
		}
		else if (mod == 4) {
			ChromaSounds.DING.playSoundAtBlock(world, x, y, z, 1, (float)CrystalMusicManager.instance.getThird(e));
		}
		else if (mod == 8) {
			ChromaSounds.DING.playSoundAtBlock(world, x, y, z, 1, (float)CrystalMusicManager.instance.getFifth(e));
		}
		else if (mod == 12) {
			ChromaSounds.DING.playSoundAtBlock(world, x, y, z, 1, (float)CrystalMusicManager.instance.getOctave(e));
		}
	}

	@Override
	public boolean playerCanUse(EntityPlayer ep) {
		return this.isOwnedByPlayer(ep);
	}

	@Override
	public boolean allowCharging(EntityPlayer ep, CrystalElement e) {
		return this.playerCanUse(ep);
	}

	@Override
	public float getChargeRateMultiplier(EntityPlayer ep, CrystalElement e) {
		return 0.85F;
	}

	@Override
	public CrystalElement getDeliveredColor(EntityPlayer ep, World world, int clickX, int clickY, int clickZ) {
		return CrystalElement.elements[world.getBlockMetadata(clickX, clickY, clickZ)];
	}

	@Override
	public Coordinate getChargeParticleOrigin(EntityPlayer ep, CrystalElement e) {
		ArrayList<Coordinate> li = locations.get(e);
		int step = growth.get(e);
		return step == 0 ? new Coordinate(this) : li.get(Math.min(step-1, rand.nextInt(li.size()))).offset(xCoord, yCoord, zCoord);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if (this.hasMultiBlock()) {
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord-1, xCoord+2, yCoord+1, zCoord+1);
			if (this.isEnhanced()) {
				box = box.expand(7, 7, 7).offset(0, -6, 0);
			}
			return box;
		}
		else {
			return super.getRenderBoundingBox();
		}
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return super.getMaxRenderDistanceSquared()*4;
	}

	@Override
	public boolean canTransmitTo(TileEntityWirelessPowered te) {
		return this.getDistanceSqTo(te.xCoord, te.yCoord, te.zCoord) <= 1024;// && PylonFinder.lineOfSight(worldObj, xCoord, yCoord, zCoord, te.xCoord, te.yCoord, te.zCoord);
	}

	@Override
	public int request(CrystalElement e, int amt, int x, int y, int z) {
		int has = energy.getValue(e);
		amt = Math.min(amt, has);
		if (amt > 0) {
			this.drain(e, amt);
			return amt;
		}
		return 0;
	}

	@Override
	public int getPathPriority() {
		return 500;
	}

	@Override
	public ChromaStructures getPrimaryStructure() {
		return ChromaStructures.TREE;
	}

	@Override
	public Coordinate getStructureOffset() {
		return null;
	}

	public boolean canStructureBeInspected() {
		return true;
	}

	@Override
	public double getMaximumBeamRadius() {
		return TileEntityCrystalBase.DEFAULT_BEAM_RADIUS;
	}

}
