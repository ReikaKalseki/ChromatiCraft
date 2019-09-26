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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.DynamicRepeater;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBallLightningFX;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.TileEntityPersonalCharger;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityWeakRepeater extends TileEntityCrystalRepeater implements DynamicRepeater {

	//public static final int MAX_LUMENS_MIN = 30000;
	//public static final int MAX_LUMENS_MAX = 80000;

	//private int originalUse;
	//private int remainingUse;

	private CrystalElement overloadColor;
	private int eolTicks;

	public static final int WEAK_RANGE = 16;
	public static final int WEAK_RECEIVE_RANGE = 24;

	private boolean ruptured;

	public TileEntityWeakRepeater() {
		//originalUse = ReikaRandomHelper.getRandomBetween(MAX_LUMENS_MIN, MAX_LUMENS_MAX);
		//remainingUse = originalUse;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.WEAKREPEATER;
	}

	public boolean isRuptured() {
		return ruptured;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (this.isRuptured())
			return;

		if (world.isRemote) {
			this.doLifespanParticles(world, x, y, z);
		}

		if (eolTicks > 0) {
			eolTicks++;
			world.setBlock(x, y+1, z, Blocks.fire);
			this.doDestroyFX(world, x, y, z);
		}
	}

	private void doDestroyFX(World world, int x, int y, int z) {
		if (eolTicks > 320) {
			RepeaterFailures r = RepeaterFailures.failureModes.getRandomEntry();
			r.doEffect(world, x, y, z, this);
			ProgressStage.BLOWREPEATER.stepPlayerTo(this.getPlacer());
			if (world.isRemote) {
				this.doDestroyFXClient(world, x, y, z);
			}
		}
		else {
			if (ReikaRandomHelper.doWithChance(Math.pow(eolTicks/320D, 0.5))) {
				if (world.isRemote)
					this.doDestroyingFXClient(world, x, y, z);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doDestroyingFXClient(World world, int x, int y, int z) {
		double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 1);
		double dy = ReikaRandomHelper.getRandomPlusMinus(y+0.5, 1);
		double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 1);
		float s = 2+rand.nextFloat()*5;
		CrystalElement e = CrystalElement.elements[(this.getTicksExisted()/16)%16];
		int l = 10+rand.nextInt(10);
		EntityFX fx = new EntityBlurFX(world, dx, dy, dz).setRapidExpand().setColor(e.getColor()).setScale(s).setLife(l).setIcon(ChromaIcons.TURBO);
		EntityFX fxb = new EntityBlurFX(world, dx, dy, dz).setRapidExpand().setColor(e.getColor()).setScale(s/1.125F).setLife(l).setIcon(ChromaIcons.TURBO);
		EntityFX fx2 = new EntityBlurFX(world, dx, dy, dz).setRapidExpand().setColor(0x000000).setScale(s/2.5F).setBasicBlend().setIcon(ChromaIcons.TRANSFADE).setLife(l);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		Minecraft.getMinecraft().effectRenderer.addEffect(fxb);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
	}

	@SideOnly(Side.CLIENT)
	private void doDestroyFXClient(World world, int x, int y, int z) {
		int n = 32+rand.nextInt(64);
		for (int i = 0; i < n; i++) {
			double phi = rand.nextDouble()*360;
			double theta = rand.nextDouble()*360;
			double v = ReikaRandomHelper.getRandomPlusMinus(0.25, 0.125);
			double[] vel = ReikaPhysicsHelper.polarToCartesian(v, theta, phi);
			int c1 = overloadColor.getColor();
			int c2 = ReikaColorAPI.mixColors(c1, 0xffffff, 0.25F);
			int c3 = ReikaColorAPI.mixColors(c1, 0x000000, 0.25F);
			double dx = x+rand.nextDouble();
			double dy = y+rand.nextDouble();
			double dz = z+rand.nextDouble();
			float s = 5+2.5F*rand.nextFloat();
			EntityFX fx = new EntityBlurFX(world, dx, dy, dz, vel[0], vel[1], vel[2]).setRapidExpand().setColor(c1).setScale(s);
			EntityFX fx1 = new EntityBlurFX(world, dx, dy, dz, vel[0], vel[1], vel[2]).setRapidExpand().setColor(c2).setScale(s*0.5F).lockTo(fx);
			EntityFX fx2 = new EntityBlurFX(world, dx, dy, dz, vel[0], vel[1], vel[2]).setRapidExpand().setColor(c3).setScale(s*0.25F).setIcon(ChromaIcons.TRANSFADE).setBasicBlend().lockTo(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
		ReikaSoundHelper.playClientSound(ChromaSounds.POWERDOWN, x+0.5, y+0.5, z+0.5, 1, 1, false);
	}

	@SideOnly(Side.CLIENT)
	private void doLifespanParticles(World world, int x, int y, int z) {
		if (eolTicks > 0) {
			double frac = eolTicks/320D;
			double f = 0.8*(1-Math.pow(frac, 1/6D));
			CrystalElement e = CrystalElement.elements[(this.getTicksExisted()/16)%16];
			if (eolTicks == 0 && ReikaRandomHelper.doWithChance(f)) {
				EntityBallLightningFX fx = new EntityBallLightningFX(world, x+0.5, y+0.5, z+0.5, e);
				fx.setVelocity(0.125, rand.nextInt(360), rand.nextInt(45));
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			if (frac < 0.5 && ReikaRandomHelper.doWithChance(0.125*Math.pow(f, 2))) {
				double dr = ReikaRandomHelper.getRandomPlusMinus(8D, 2D);
				double ex = ReikaRandomHelper.getRandomPlusMinus(x+0.5, dr);
				double ez = ReikaRandomHelper.getRandomPlusMinus(z+0.5, dr);
				double ey = ReikaRandomHelper.getRandomBetween(y+0.05, y+6);
				LightningBolt b = new LightningBolt(new DecimalPosition(x+0.5, y+0.5, z+0.5), new DecimalPosition(ex, ey, ez), 4);
				b.variance = 0.375;
				b.update();
				int l = 20+rand.nextInt(20);
				for (int i = 0; i < b.nsteps; i++) {
					DecimalPosition pos1 = b.getPosition(i);
					DecimalPosition pos2 = b.getPosition(i+1);
					for (double r = 0; r <= 1; r += 0.03125) {
						float s = 2F;
						int clr = e.getColor();
						double dx = pos1.xCoord+r*(pos2.xCoord-pos1.xCoord);
						double dy = pos1.yCoord+r*(pos2.yCoord-pos1.yCoord);
						double dz = pos1.zCoord+r*(pos2.zCoord-pos1.zCoord);
						EntityFX fx = new EntityBlurFX(world, dx, dy, dz).setScale(s).setColor(clr).setLife(l).setRapidExpand();
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						EntityFX fx2 = new EntityBlurFX(world, dx, dy, dz).setScale(s/2F).setColor(0xffffff).setLife(l).setRapidExpand();
						Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
					}
				}
				ReikaSoundHelper.playClientSound(ChromaSounds.DISCHARGE, x+0.5, y+0.5, z+0.5, 0.125F, CrystalMusicManager.instance.getRandomScaledDing(e), true);
			}
		}
	}

	private void destroy(CrystalElement e) {
		if (eolTicks > 0)
			return;
		overloadColor = e;
		eolTicks = 1+rand.nextInt(40);
		worldObj.setBlock(xCoord, yCoord+1, zCoord, Blocks.fire);
		ChromaSounds.REPEATERSURGE_WEAK.playSoundAtBlock(this, 1, 1.1035F);
		this.syncAllData(false);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		//remainingUse = NBT.getInteger("remaining");

		eolTicks = NBT.getInteger("eol");
		overloadColor = CrystalElement.elements[NBT.getInteger("overload")];

		ruptured = NBT.getBoolean("rupture");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		//NBT.setInteger("remaining", remainingUse);

		NBT.setInteger("eol", eolTicks);
		if (overloadColor != null) {
			NBT.setInteger("overload", overloadColor.ordinal());
		}

		NBT.setBoolean("rupture", ruptured);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		//originalUse = NBT.getInteger("lifespan");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		//NBT.setInteger("lifespan", originalUse);
	}

	@Override
	public void onTransfer(CrystalSource src, CrystalReceiver r, CrystalElement e, int amt) {
		/*
		if (remainingUse > 0) {
			remainingUse -= amt;
			if (remainingUse <= 0) {
				this.endOfLife(e);
			}
		}
		 */
		if (!this.canSafelySupply(r) && rand.nextInt(8) == 0)
			this.destroy(e);
	}

	private boolean canSafelySupply(CrystalReceiver r) {
		return r instanceof TileEntityRelaySource || r instanceof TileEntityRitualTable || r instanceof TileEntityPersonalCharger;
	}

	@Override
	public int getSendRange() {
		return WEAK_RANGE;
	}

	@Override
	public int getReceiveRange() {
		return WEAK_RECEIVE_RANGE;
	}

	@Override
	protected boolean checkForStructure() {
		ForgeDirection dir = facing;
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		Block b = world.getBlock(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
		int meta = world.getBlockMetadata(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
		ReikaTreeHelper tree = ReikaTreeHelper.getTree(b, meta);
		ModWoodList mod = ModWoodList.getModWood(b, meta);
		return tree != null || (mod != null && this.isValidWood(mod) &&  (mod.canBePlacedSideways() || dir.offsetY != 0));
	}

	public static boolean isValidWood(ModWoodList mod) {
		switch(mod) {
			case BLOODWOOD:
			case BAMBOO:
			case SLIME:
			case TAINTED:
				return false;
			default:
				return true;
		}
	}

	@Override
	public boolean canConduct() {
		return super.canConduct() && !this.isRuptured();
	}

	@Override
	public CrystalElement getActiveColor() {
		return null;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		super.getTagsToWriteToStack(NBT);

		//NBT.setInteger("total", originalUse);
		//NBT.setInteger("remain", remainingUse);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		super.setDataFromItemStackTag(is);

		//remainingUse = is.stackTagCompound != null && is.stackTagCompound.hasKey("remain") ? is.stackTagCompound.getInteger("remain") : remainingUse;
		//originalUse = is.stackTagCompound != null && is.stackTagCompound.hasKey("total") ? is.stackTagCompound.getInteger("total") : originalUse;
	}

	@Override
	protected boolean shouldDrop() {
		return eolTicks == 0 && !this.isRuptured();//this.hasRemainingLife();
	}

	@Override
	public int maxThroughput() {
		return 120;//Math.min(remainingUse, 120);
	}

	@Override
	public int getSignalDegradation() {
		return 250;
	}

	@Override
	public double getIncomingBeamRadius() {
		return 0.125;
	}

	@Override
	public double getOutgoingBeamRadius() {
		return 0.125;
	}

	@Override
	public boolean canTransmitTo(CrystalReceiver r) {
		return super.canTransmitTo(r);// && r.getResearchTier().ordinal() <= this.getResearchTier().ordinal();
	}

	/*
	@Override
	public ResearchLevel getResearchTier() {
		return ResearchLevel.ENERGYEXPLORE;
	}
	 */

	@Override
	public float getFailureWeight(CrystalElement e) {
		return 30;
	}

	@Override
	public int getModifiedThoughput(int basethru, CrystalSource src, CrystalReceiver r) {
		return !this.canSafelySupply(r) ? 0 : basethru;
	}

	private static enum RepeaterFailures {

		EXPLOSION(50),
		BURN(20),
		RUPTURE(40);

		private static final WeightedRandom<RepeaterFailures> failureModes = new WeightedRandom();

		public final int weight;

		private RepeaterFailures(int w) {
			weight = w;
		}

		private void doEffect(World world, int x, int y, int z, TileEntityWeakRepeater te) {
			switch(this) {
				case BURN:
					te.delete();
					for (int i = 0; i < 6; i++) {
						ReikaWorldHelper.ignite(world, x+te.dirs[i].offsetX, y+te.dirs[i].offsetY, z+te.dirs[i].offsetZ);
					}
					world.setBlock(x, y, z, ChromaBlocks.CHROMA.getBlockInstance());
					ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz", 2, 0.5F);
					break;
				case EXPLOSION:
					te.delete();
					for (int i = 0; i < 6; i++) {
						ReikaWorldHelper.ignite(world, x+te.dirs[i].offsetX, y+te.dirs[i].offsetY, z+te.dirs[i].offsetZ);
					}
					world.newExplosion(null, x+0.5, y+0.5, z+0.5, 2, true, true);
					break;
				case RUPTURE:
					ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz", 2, 0.5F);
					for (int m = 0; m < 40; m++) {
						ReikaParticleHelper.LAVA.spawnAt(world, x+rand.nextDouble(), y+rand.nextDouble()*1.5, z+rand.nextDouble());
					}
					world.setBlock(x, y+1, z, Blocks.air);
					world.newExplosion(null, x+0.5, y+0.5, z+0.5, 2, false, false);
					te.ruptured = true;
					te.triggerBlockUpdate();
					break;
			}
		}

		static {
			RepeaterFailures[] list = values();
			for (int i = 0; i < list.length; i++) {
				failureModes.addEntry(list[i], list[i].weight);
			}
		}

	}

}
