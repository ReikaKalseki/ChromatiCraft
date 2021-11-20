package Reika.ChromatiCraft.TileEntity.Recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Magic.ElementBufferCapacityBoost;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityPlayerInfuser extends TileEntityAuraInfuser {

	private AxisAlignedBB targetBox;

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		double d = 0.0625;
		targetBox = AxisAlignedBB.getBoundingBox(x-d, y+0.375-d, z-d, x+1+d, y+0.75+d, z+1+d);
	}

	public AxisAlignedBB getTargetBox() {
		return targetBox.copy();
	}

	@Override
	public int getInventoryStackLimit() {
		return 8;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PLAYERINFUSER;
	}

	@Override
	protected ChromaStructures getStructure() {
		return ChromaStructures.PLAYERINFUSION;
	}

	@Override
	protected void collectFocusCrystalLocations(FilledBlockArray arr) {
		for (Coordinate c : arr.keySet()) {
			if (arr.getBlockAt(c.xCoord, c.yCoord, c.zCoord) == ChromaBlocks.PYLONSTRUCT.getBlockInstance()) {
				if (arr.getMetaAt(c.xCoord, c.yCoord, c.zCoord) == StoneTypes.STABILIZER.ordinal()) {
					Coordinate c2 = c.offset(0, 1, 0);
					focusCrystalSpots.add(c2);
				}
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return this.getEffect(is) != null;
	}

	@Override
	protected boolean isReady() {
		if (inv[0] == null || inv[0].stackSize < 8)
			return false;
		if (targetBox == null || !targetBox.intersectsWith(this.getCraftingPlayer().boundingBox))
			return false;
		ElementBufferCapacityBoost e = this.getSelectedEffect();
		return e != null && ElementBufferCapacityBoost.getAvailableBoosts(this.getCraftingPlayer()).contains(e);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void spawnParticles(World world, int x, int y, int z) {
		int n = Math.max(1, rand.nextInt(4)-Minecraft.getMinecraft().gameSettings.particleSetting);
		for (int i = 0; i < n; i++) {
			Coordinate p = ReikaJavaLibrary.getRandomCollectionEntry(rand, this.getChromaLocations());
			double r = 1.85;
			double px = p.xCoord+rand.nextDouble();
			double pz = p.zCoord+rand.nextDouble();
			double vy = ReikaRandomHelper.getRandomBetween(0.125, 0.4);
			float s = 1.5F;
			float g = (float)(vy*1.2D);
			double vx = -vy*(px-x-0.5)/6;
			double vz = -vy*(pz-z-0.5)/6;
			EntityChromaFluidFX fx = new EntityChromaFluidFX(CrystalElement.WHITE, world, px, p.yCoord+0.5, pz, vx, vy, vz).setScale(s).setGravity(g);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void doAmbientParticles(World world, int x, int y, int z) {
		Coordinate p = ReikaJavaLibrary.getRandomCollectionEntry(rand, this.getChromaLocations());
		double px = p.xCoord+rand.nextDouble();
		double pz = p.zCoord+rand.nextDouble();
		float s = 1.75F;
		double vy = ReikaRandomHelper.getRandomBetween(0.0625, 0.375);
		float g = (float)Math.max(ReikaRandomHelper.getRandomBetween(0.125, 0.25), vy);
		EntityChromaFluidFX fx = new EntityChromaFluidFX(CrystalElement.WHITE, world, px, p.yCoord+0.5, pz, 0, vy, 0).setScale(s).setGravity(g);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private ElementBufferCapacityBoost getEffect(ItemStack is) {
		for (ElementBufferCapacityBoost e : ElementBufferCapacityBoost.list) {
			ItemStack is2 = e.getIngredient();
			if (is2 != null) {
				if (ReikaItemHelper.matchStacks(is, is2))
					return e;
			}
		}
		return null;
	}

	@Override
	protected void onCraft() {
		this.getSelectedEffect().give(this.getCraftingPlayer());
		inv[0] = null;
	}

	public ElementBufferCapacityBoost getSelectedEffect() {
		return inv[0] != null ? this.getEffect(inv[0]) : null;
	}

	@Override
	protected void onCraftingTick(World world, int x, int y, int z) {
		EntityPlayer ep = this.getCraftingPlayer();
		AxisAlignedBB box = ep.boundingBox;
		double cx = (box.maxX+box.minX)/2;
		double cy = box.minY;
		double cz = (box.maxZ+box.minZ)/2;
		double cx2 = (targetBox.maxX+targetBox.minX)/2;
		double cy2 = (targetBox.maxY+targetBox.minY)/2;
		double cz2 = (targetBox.maxZ+targetBox.minZ)/2;
		double dx = cx2-cx;
		double dy = cy2-cy;
		double dz = cz2-cz;
		double v = 0.25;
		ep.motionX += dx*dx*v*Math.signum(dx);
		ep.motionY += dy*dy*v*1.5*Math.signum(dy);
		ep.motionZ += dz*dz*v*Math.signum(dz);
		//ep.velocityChanged = true;
	}

	@Override
	public boolean hasWork() {
		return this.getState() == OperationState.RUNNING;
	}

}
