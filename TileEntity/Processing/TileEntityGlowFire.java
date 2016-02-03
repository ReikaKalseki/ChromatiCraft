package Reika.ChromatiCraft.TileEntity.Processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Magic.ElementMixer;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.LumenTile;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ItemElementCalculator;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityGlowFire extends InventoriedChromaticBase implements LumenTile, InertIInv, ItemOnRightClick, BreakAction {

	public static final int MAX_BRANCHES = 6;

	public static final String DROP_TAG = "GlowFire";

	private final ElementTagCompound energy = new ElementTagCompound();

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

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.GLOWFIRE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			this.doParticles(world, x, y, z);
		}
		else {
			this.consumeItems(world, x, y, z);
		}
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
					ReikaJavaLibrary.pConsole("Insufficient "+e+", have "+energy.getValue(e)+", need "+cost.getValue(e));
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
		ReikaJavaLibrary.pConsole(in+" costs "+cost+", rem "+remove);
		energy.subtract(remove);
		return true;
	}

	private static ElementTagCompound getCompositionCost(CrystalElement e, int amt) {
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
			tag.setTag(in, ReikaMathLibrary.intpow2(amt, tag.getValue(e)));
		}
		return tag;
	}

	private void consumeItems(World world, int x, int y, int z) {
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).contract(0.2, 0.2, 0.2);
		List<EntityItem> li = world.getEntitiesWithinAABB(EntityItem.class, box);
		for (EntityItem ei : li) {
			if (!ei.getEntityData().getBoolean(DROP_TAG)) {
				ItemStack is = ei.getEntityItem();
				ElementTagCompound tag = this.consumeItem(is);
				if (tag != null) {
					is.stackSize--;
					if (is.stackSize <= 0)
						ei.setDead();
					energy.addTag(tag);
					ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.FIRECONSUMEITEM.ordinal(), this, 32, tag.keySetAsBits());
				}
			}
		}
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
		ElementTagCompound tag = FabricationRecipes.recipes().getItemCost(is);
		tag = tag != null ? ItemElementCalculator.instance.getValueForItem(is).copy().power(4).scale(0.0625F)/*tag.copy().scale(0.01F)*/ : null;
		return tag != null && !tag.isEmpty() ? tag : null;
	}

	public static ElementTagCompound getDecompositionValue(ItemStack is) {
		ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(is);
		if (tag == null || tag.isEmpty())
			return null;
		return tag.copy();
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
			energy.clear();
			if (inv[0] != null)
				this.dropItem(inv[0]);
			inv[0] = null;
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
		EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.5, z+0.5, v[0], v[1], v[2]).setColor(c).setScale(s).setLife(l);
		if (rand.nextBoolean())
			fx.setColliding();
		if (primary)
			fx.setRapidExpand();
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private void retargetSecondary(int i) {
		secondaryAngleThetaVel[i] = ReikaRandomHelper.getRandomPlusMinus(0, 2);//ReikaRandomHelper.getRandomPlusMinus(0, 90);
		secondaryAnglePhiVel[i] = ReikaRandomHelper.getRandomPlusMinus(0, 2);//rand.nextDouble()*360;
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
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		energy.writeToNBT("energy", NBT);
	}

	@Override
	public void breakBlock() {
		this.empty();
	}

}
