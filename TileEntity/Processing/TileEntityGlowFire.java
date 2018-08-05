/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Magic.ElementMixer;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Magic.Interfaces.LumenTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.Data.RunningAverage;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.DelayableSchedulableEvent;
import Reika.DragonAPI.Instantiable.Rendering.FXCollection;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityGlowFire extends InventoriedChromaticBase implements LumenTile, InertIInv, ItemOnRightClick, BreakAction {

	public static final int MAX_BRANCHES = 6;

	private static final float BASE_ITEM_FACTOR = 10;
	public static final String DROP_TAG = "GlowFire";

	private final ElementTagCompound energy = new ElementTagCompound();

	private RunningAverage averageIngredientValue = new RunningAverage();
	private RunningAverage averageOutputValue = new RunningAverage();
	private boolean smothered;

	//private ItemStack output;

	private double primaryAngleTheta = rand.nextDouble()*360;
	private double primaryAngleThetaVel = ReikaRandomHelper.getRandomPlusMinus(0, 90D);
	private double primaryAnglePhi = ReikaRandomHelper.getRandomPlusMinus(0, 3);
	private double primaryAnglePhiVel = ReikaRandomHelper.getRandomPlusMinus(0, 3);

	private double[] secondaryAngleTheta = new double[MAX_BRANCHES];
	private double[] secondaryAnglePhi = new double[MAX_BRANCHES];
	private double[] secondaryAngleThetaVel = new double[MAX_BRANCHES];
	private double[] secondaryAnglePhiVel = new double[MAX_BRANCHES];
	private int[] secondaryLife = new int[MAX_BRANCHES];

	@SideOnly(Side.CLIENT)
	public FXCollection particles;

	public TileEntityGlowFire() {
		if (this.getSide() == Side.CLIENT)
			particles = new FXCollection();
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.GLOWFIRE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			particles.update();
			if (!this.isSmothered() || rand.nextInt(4) == 0)
				this.doParticles(world, x, y, z);
		}
		else {
			this.consumeItems(world, x, y, z);

			this.doOverloadShocks();
		}
	}

	private void doOverloadShocks() {
		CrystalElement e = energy.asWeightedRandom().getRandomEntry();
		if (energy.getValue(e) >= this.getMaxStorage(e)) {
			float f = 0.03125F*0.75F*energy.getValue(e)/this.getMaxStorage(e);
			if (ReikaRandomHelper.doWithChance(f)) {
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(this).expand(6, 3, 6);
				List<EntityPlayer> li = worldObj.getEntitiesWithinAABB(EntityPlayer.class, box);
				for (EntityPlayer ep : li) {
					this.dischargeIntoPlayer(this, ep, e, 1);
				}
			}
		}
	}

	public boolean isSmothered() {
		return smothered;
	}

	public boolean craft() {
		if (inv[0] == null)
			return false;
		if (energy.isEmpty())
			return false;
		ItemStack in = inv[0];
		inv[0] = null;
		boolean flag = true;
		boolean flag2 = false;
		while(flag) {
			this.dropItem(in);
			flag = this.craftAndDrop(in);
			flag2 |= flag;
		}
		double over = averageOutputValue.getAverage()/averageIngredientValue.getAverage();
		if (averageIngredientValue.getAverage() > 0 && over >= 1) {
			if (ReikaRandomHelper.doWithChance(Math.min(0.25, over/16D))) {
				smothered = true;
			}
		}
		return flag2;
	}

	private boolean craftAndDrop(ItemStack in) {
		ElementTagCompound cost = this.getCost(in);
		ElementTagCompound remove = new ElementTagCompound();
		boolean[] flags = ReikaArrayHelper.getTrueArray(16);
		for (CrystalElement e : cost.elementSet()) {
			int req = cost.getValue(e);
			if (energy.getValue(e) >= req) {
				remove.addTag(e, req);
			}
			else {
				flags[e.ordinal()] = false;
			}
		}
		for (int i = 0; i < 16; i++) {
			if (!flags[i]) {
				CrystalElement e = CrystalElement.elements[i];
				if (e.isPrimary()) {
					//ReikaJavaLibrary.pConsole("Insufficient "+e+", have "+energy.getValue(e)+", need "+cost.getValue(e));
					return false;
				}
				ElementTagCompound combine = this.getCompositionCost(e, cost.getValue(e));
				for (CrystalElement e2 : combine.elementSet()) {
					int add = combine.getValue(e2);
					remove.addValueToColor(e2, add);
				}
			}
		}
		for (CrystalElement e : remove.elementSet()) {
			int val = remove.getValue(e);
			if (energy.getValue(e) < val)
				return false;
		}
		//ReikaJavaLibrary.pConsole(in+" costs "+cost+", rem "+remove);
		averageOutputValue.addValue(remove.getTotalEnergy());
		energy.subtract(remove);
		return true;
	}

	public static ElementTagCompound getCompositionCost(CrystalElement e, int amt) {
		ElementTagCompound tag = new ElementTagCompound();
		Collection<CrystalElement> parents = ElementMixer.instance.getMixParents(e);
		if (parents == null)
			return null;
		for (CrystalElement in : parents) {
			tag.addTag(in, 2);
		}
		while (!tag.isPrimaryOnly()) {
			Iterator<CrystalElement> it = tag.elementSet().iterator();
			while (it.hasNext()) {
				CrystalElement in = it.next();
				if (in.isPrimary()) {

				}
				else {
					int get = tag.removeTag(in);
					Collection<CrystalElement> parents2 = ElementMixer.instance.getMixParents(in);
					for (CrystalElement in2 : parents2) {
						tag.addTag(in2, get+1);
					}
				}
			}
		}
		for (CrystalElement in : tag.elementSet()) {
			tag.setTag(in, MathHelper.ceiling_double_int(Math.pow(amt, -0.75+tag.getValue(in))));
		}
		return tag;
	}

	private void consumeItems(World world, int x, int y, int z) {
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).contract(0.2, 0.2, 0.2);
		List<EntityItem> li = world.getEntitiesWithinAABB(EntityItem.class, box);
		for (EntityItem ei : li) {
			if (this.isConsumableItem(ei)) {
				ItemStack is = ei.getEntityItem();
				ElementTagCompound tag = this.consumeItem(is);
				if (tag != null) {
					is.stackSize--;
					if (is.stackSize <= 0)
						ei.setDead();
					energy.addTag(tag);
					averageIngredientValue.addValue(tag.getTotalEnergy());
					ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.FIRECONSUMEITEM.ordinal(), this, 32, tag.keySetAsBits());
				}
			}
		}
	}

	private boolean isConsumableItem(EntityItem ei) {
		if (ei.getEntityData().getBoolean(DROP_TAG))
			return false;
		EntityPlayer ep = ReikaItemHelper.getDropper(ei);
		if (ep == null || ReikaPlayerAPI.isFake(ep) || ep.getDistanceSqToEntity(ei) >= 64)
			return false;
		return true;
	}

	private ElementTagCompound consumeItem(ItemStack is) {
		ElementTagCompound tag = this.getDecompositionValue(is);
		if (tag != null) {
			for (CrystalElement e : tag.elementSet()) {
				if (energy.getValue(e)+tag.getValue(e) > this.getMaxStorage(e)) {
					return null;
				}
			}
		}
		return tag;
	}

	public static ElementTagCompound getCost(ItemStack is) {
		if (!FabricationRecipes.recipes().isItemFabricable(is))
			return null;
		if (ReikaBlockHelper.isOre(is))
			return null;
		ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(is).copy();
		//if (tag == null)
		//	tag = FabricationRecipes.recipes().getItemRecipe(is).getCost().copy().scale(1F/FabricationRecipes.FACTOR).power(1F/FabricationRecipes.POWER).scale(1F/FabricationRecipes.INITFACTOR);
		if (tag != null)
			tag = scaleCostTag(is, tag);
		return tag != null && !tag.isEmpty() ? tag : null;
	}

	public static ElementTagCompound getDecompositionValue(ItemStack is) {
		ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(is);
		if (tag == null || tag.isEmpty())
			return null;
		tag = tag.copy();
		tag = scaleDecompositionTag(is, tag);
		return tag;
	}

	private static ElementTagCompound scaleCostTag(ItemStack is, ElementTagCompound tag) {
		boolean sc = tag.getMaximumValue() == 1;
		tag.power(1.15).scale(1.5F);
		if (ChromaBlocks.CRYSTAL.match(is)) {
			tag.power(1.1);
			tag.scale(2);
		}
		tag.scale(BASE_ITEM_FACTOR);
		if (is.getItem() == Items.dye || ChromaItems.DYE.matchWith(is) || ReikaItemHelper.isOreNugget(is))
			tag.scale(0.25F);
		if (sc)
			tag.scale(1.2F);
		if (ChromaItems.SHARD.matchWith(is))
			tag.scale(is.getItemDamage() >= 16 ? 5 : 2);
		if (ReikaItemHelper.isInOreTag(is, "flower")) {
			addFlowerCrafting(is, tag);
		}
		return tag;
	}

	private static void addFlowerCrafting(ItemStack is, ElementTagCompound tag) {
		ItemStack out = ReikaRecipeHelper.getShapelessCraftResult(is);
		Collection<ReikaDyeHelper> c = ReikaDyeHelper.getColorsFromItem(is);
		if (c != null) {
			for (ReikaDyeHelper dye : c) {
				tag.addValueToColor(CrystalElement.elements[dye.ordinal()], 1);
			}
		}
	}

	private static ElementTagCompound scaleDecompositionTag(ItemStack is, ElementTagCompound tag) {
		if (is.getItem() != Items.dye && !ChromaItems.DYE.matchWith(is) && !ReikaItemHelper.isOreNugget(is))
			tag.scale(BASE_ITEM_FACTOR);
		if (ChromaBlocks.PYLONSTRUCT.match(is))
			tag.scale(2F/BASE_ITEM_FACTOR); //was 5F/BASE_ITEM_FACTOR
		if (ChromaItems.SHARD.matchWith(is))
			tag.scale(is.getItemDamage() >= 16 ? 3 : 2);
		return tag;
	}

	@SideOnly(Side.CLIENT)
	public static void consumeItemFX(World world, int x, int y, int z, int colors) {
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if ((colors & (1 << e.ordinal())) != 0) {
				int n = 1+rand.nextInt(5);
				for (int k = 0; k < n; k++) {
					double rx = x+rand.nextDouble();
					double ry = y+rand.nextDouble();
					double rz = z+rand.nextDouble();
					float s = 1+rand.nextFloat();
					int l = 12+rand.nextInt(18);
					EntityFX fx = new EntityBlurFX(world, rx, ry, rz).setColor(e.getColor()).setLife(l).setScale(s).setRapidExpand();
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}
	}

	public void empty() {
		if (!worldObj.isRemote) {
			ChromaSounds.RIFT.playSoundAtBlock(this);
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.FIREDUMP.ordinal(), this, 64, energy.keySetAsBits());
			if (inv[0] != null)
				this.dropItem(inv[0]);
			inv[0] = null;
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(this).expand(6, 3, 6);
			List<EntityPlayer> li = worldObj.getEntitiesWithinAABB(EntityPlayer.class, box);
			for (EntityPlayer ep : li) {
				int tick = 1+rand.nextInt(3);
				for (CrystalElement e : energy.elementSet()) {
					TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new GlowFireDischarge(this, ep, e, energy.getValue(e)/(float)this.getMaxStorage(e))), tick);
					tick += 1+rand.nextInt(10);
				}
			}
			energy.clear();
		}
	}

	//big particle burst
	@SideOnly(Side.CLIENT)
	public static void emptyClientFX(World world, int x, int y, int z, int colors) {
		ArrayList<Integer> li = new ArrayList();
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if ((colors & (1 << e.ordinal())) != 0) {
				li.add(e.getColor());
			}
		}
		if (li.isEmpty()) {
			li.add(0x2090ff);
		}
		int n = 20+rand.nextInt(90);
		for (int i = 0; i < n; i++) {
			EntityFX fx = new EntityFloatingSeedsFX(world, x+0.5, y+0.5, z+0.5, rand.nextDouble()*360, ReikaRandomHelper.getRandomPlusMinus(0, 10D)).setColor(li.get(rand.nextInt(li.size()))).setColliding();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {

		//if (this.getTicksExisted()%2 == 0)
		this.addParticle(world, x, y, z, primaryAnglePhi, primaryAngleTheta, true, 0);

		primaryAngleTheta += primaryAngleThetaVel;
		primaryAnglePhi += primaryAnglePhiVel;

		if (rand.nextInt(200) == 0) {
			primaryAngleThetaVel = ReikaRandomHelper.getRandomPlusMinus(0, 3);//ReikaRandomHelper.getRandomPlusMinus(0, 90);
			primaryAnglePhiVel = ReikaRandomHelper.getRandomPlusMinus(0, 3);//rand.nextDouble()*360;
		}

		for (int i = 0; i < MAX_BRANCHES; i++) {
			if (secondaryLife[i] > 0) {
				secondaryLife[i]--;

				//if (secondaryLife[i]%2 == 0)
				this.addParticle(world, x, y, z, secondaryAnglePhi[i], secondaryAngleTheta[i], false, secondaryLife[i]);

				secondaryAngleTheta[i] += secondaryAngleThetaVel[i];
				secondaryAnglePhi[i] += secondaryAnglePhiVel[i];

				if (rand.nextInt(20) == 0) {
					this.retargetSecondary(i);
				}
			}
			else if (rand.nextInt(40) == 0) {
				secondaryLife[i] = 30+rand.nextInt(270);
				this.retargetSecondary(i);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void addParticle(World world, int x, int y, int z, double phi, double theta, boolean primary, double timer) {
		int l = primary ? 10+rand.nextInt(30) : 10+rand.nextInt(10);
		float s = primary ? 2.2F : 1.25F;
		double[] v = ReikaPhysicsHelper.polarToCartesian(0.125/l*6D, theta, phi);
		int c = ReikaColorAPI.getModifiedHue(0x1070ff, (int)(215+70*Math.sin(timer/40D)));
		if (this.isSmothered()) {
			c = ReikaColorAPI.getModifiedHue(c, rand.nextInt(60));
			c = ReikaColorAPI.getModifiedSat(c, 0.5F);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.5F+0.25F*rand.nextFloat());
		}
		EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.5, z+0.5, v[0], v[1], v[2]).setColor(c).setScale(s).setLife(l);
		if (primary) {
			fx.setRapidExpand();
		}
		if (rand.nextBoolean()) {
			fx.setColliding();
			fx.forceIgnoreLimits();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		else {
			if (GuiScreen.isCtrlKeyDown())
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			else
				particles.addEffectWithVelocity(0.5, 0.5, 0.5, v[0], v[1], v[2], ChromaIcons.FADE.getIcon(), l, s, c, primary);
		}
	}

	private void retargetSecondary(int i) {
		secondaryAngleThetaVel[i] = ReikaRandomHelper.getRandomPlusMinus(0, 2);//ReikaRandomHelper.getRandomPlusMinus(0, 90);
		secondaryAnglePhiVel[i] = ReikaRandomHelper.getRandomPlusMinus(0, 2);//rand.nextDouble()*360;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(this).expand(2, 2, 2);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getEnergy(CrystalElement e) {
		return energy.getValue(e);
	}

	@Override
	public ElementTagCompound getEnergy() {
		return energy.copy();
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 10000;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack is, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public ItemStack onRightClickWith(ItemStack item, EntityPlayer ep) {
		if (inv[0] != null) {
			this.dropItem(inv[0]);
			inv[0] = null;
		}
		if (item != null && this.getCost(item) != null) {
			inv[0] = ReikaItemHelper.getSizedItemStack(item, 1);
			item.stackSize--;
			return item.stackSize > 0 ? item : null;
		}
		return item;
	}

	private void dropItem(ItemStack is) {
		EntityItem ei = ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, is);
		ei.getEntityData().setBoolean(DROP_TAG, true);
		worldObj.playSound(xCoord+0.5, yCoord+0.5, zCoord+0.5, "random.pop", 1, 0.8F, true);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		energy.readFromNBT("energy", NBT);
		smothered = NBT.getBoolean("smother");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		energy.writeToNBT("energy", NBT);
		NBT.setBoolean("smother", smothered);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		averageIngredientValue.readFromNBT("inputval", NBT);
		averageOutputValue.readFromNBT("outputval", NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		averageIngredientValue.writeToNBT("inputval", NBT);
		averageOutputValue.writeToNBT("outputval", NBT);
	}

	@Override
	public void breakBlock() {
		this.empty();
	}

	private static void dischargeIntoPlayer(TileEntityGlowFire tile, EntityPlayer player, CrystalElement color, float power) {
		dischargeIntoPlayer(tile.xCoord+0.5, tile.yCoord+0.125, tile.zCoord+0.5, player, color, power);
	}

	private static void dischargeIntoPlayer(double x, double y, double z, EntityPlayer player, CrystalElement color, float power) {
		if (player.worldObj.isRemote)
			return;
		ChromaAux.doPylonAttack(color, player, player.getHealth()/4F*Math.min(1, 2*power), false);
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.FIREDUMPSHOCK.ordinal(), player.worldObj, (int)x, (int)player.posY, (int)z, 64, color.ordinal(), player.getEntityId());
		ReikaEntityHelper.knockbackEntityFromPos(x, /*y*/player.posY, z, player, 1.5*Math.min(power*4, 1));
		player.motionY += 0.125+rand.nextDouble()*0.0625;
	}

	/** In the words of {@link SoundHandler} line 171, "IN YOU FACE!" */
	@SideOnly(Side.CLIENT)
	public static void dischargeIntoPlayerFX(World world, int x, int y, int z, CrystalElement e, EntityPlayer ep) {
		ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENTRAY, ep, 1, (float)CrystalMusicManager.instance.getDingPitchScale(e), false);
		int n = 4+rand.nextInt(4);
		LightningBolt b = new LightningBolt(new DecimalPosition(x+0.5, y+0.5, z+0.5), new DecimalPosition(ep).offset(0, -0.25, 0), n);
		b.variance *= 2;
		b.update();
		for (int i = 0; i < b.nsteps; i++) {
			DecimalPosition pos1 = b.getPosition(i);
			DecimalPosition pos2 = b.getPosition(i+1);
			for (double r = 0; r <= 1; r += 0.03125) {
				double f = i+r;
				float s = 1.75F;//(float)(1.25+1.75*f/(2D*b.nsteps));
				int l = 20;
				int a = (int)(2*f);
				DecimalPosition dd = DecimalPosition.interpolate(pos1, pos2, r);
				EntityFX fx = new EntityBlurFX(world, dd.xCoord, dd.yCoord, dd.zCoord).setScale(s).setColor(e.getColor()).setLife(l).setRapidExpand().freezeLife(a);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	private static class GlowFireDischarge implements DelayableSchedulableEvent {

		private final DecimalPosition tile;
		private final EntityPlayer player;
		private final CrystalElement color;
		private final float fraction;

		public GlowFireDischarge(TileEntityGlowFire te, EntityPlayer ep, CrystalElement e, float amt) {
			tile = new DecimalPosition(te);
			player = ep;
			color = e;
			fraction = amt;
		}

		@Override
		public void fire() {
			dischargeIntoPlayer(tile.xCoord, tile.yCoord, tile.zCoord, player, color, fraction);
		}

		@Override
		public boolean runOnSide(Side s) {
			return s == Side.SERVER;
		}

		@Override
		public boolean canTick() {
			return player != null && !ReikaPlayerAPI.isFake(player) && player.worldObj != null && ReikaPlayerAPI.getPlayerByNameAnyWorld(player.getCommandSenderName()) != null;
		}

	}

}
