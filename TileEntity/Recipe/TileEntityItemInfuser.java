package Reika.ChromatiCraft.TileEntity.Recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityItemInfuser extends TileEntityAuraInfuser {

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.INFUSER;
	}

	@Override
	protected ChromaStructures getStructure() {
		return ChromaStructures.INFUSION;
	}

	@Override
	protected void collectFocusCrystalLocations(FilledBlockArray arr) {
		for (Coordinate c : arr.keySet()) {
			if (c.yCoord == yCoord-1 && c.getTaxicabDistanceTo(new Coordinate(this)) > 2) {
				if (arr.getBlockAt(c.xCoord, c.yCoord, c.zCoord) == ChromaBlocks.PYLONSTRUCT.getBlockInstance()) {
					Coordinate c2 = c.offset(0, 1, 0);
					focusCrystalSpots.add(c2);
				}
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return ReikaItemHelper.matchStacks(is, ChromaStacks.rawCrystal);
	}

	@Override
	protected boolean isReady() {
		return ReikaItemHelper.matchStacks(inv[0], ChromaStacks.rawCrystal);
	}

	@Override
	protected void onCraft() {
		int n = inv[0].stackSize;
		EntityPlayer ep = this.getCraftingPlayer();
		if (!ReikaPlayerAPI.isFake(ep) && Chromabilities.DOUBLECRAFT.enabledOn(ep))
			n *= 2;
		inv[0] = ReikaItemHelper.getSizedItemStack(ChromaStacks.iridCrystal, n);
		ProgressStage.INFUSE.stepPlayerTo(ep);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void spawnParticles(World world, int x, int y, int z) {
		double ang = Math.toRadians(this.getTicksExisted()*2%360);
		float fac = (float)Math.sin(Math.toRadians(this.getTicksExisted()*4));
		float s = 1.25F+0.25F*fac;
		for (int i = 0; i < 360; i += 60) {
			boolean tall = i%120 == 0;
			float g = tall ? 0.375F*(0.5F+0.5F*fac) : 0.375F;
			double a = ang+Math.toRadians(i);
			double r = 1.85;
			double v = tall ? 0.0425*(1+fac) : ReikaRandomHelper.getRandomPlusMinus(0.0425, 0.005);
			double px = x+0.5+r*Math.sin(a);
			double py = y-0.75;
			double pz = z+0.5+r*Math.cos(a);
			double vx = -v*(px-x-0.5);
			double vy = 0.3;
			double vz = -v*(pz-z-0.5);
			EntityChromaFluidFX fx = new EntityChromaFluidFX(CrystalElement.WHITE, world, px, py, pz, vx, vy, vz).setScale(s).setGravity(g);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public boolean hasWork() {
		return this.getState() == OperationState.RUNNING;
	}

}
