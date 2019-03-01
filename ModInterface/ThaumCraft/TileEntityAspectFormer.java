/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import com.google.common.base.Strings;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

public class TileEntityAspectFormer extends CrystalReceiverBase implements GuiController {

	private Aspect selected;

	private ForgeDirection facing = ForgeDirection.DOWN;

	private AspectMode mode = AspectMode.DEMAND;

	private ElementTagCompound currentRequest = new ElementTagCompound();

	private static int CAPACITY;

	public static void initCapacity() {
		int max = 0;
		if (ModList.THAUMCRAFT.isLoaded()) {
			for (Aspect a : Aspect.aspects.values()) {
				ElementTagCompound cost = getAspectCost(a);
				max = Math.max(max, cost.getMaximumValue());
				//ReikaJavaLibrary.pConsole(a.getName()+" > "+cost);
			}
		}
		CAPACITY = max*3/2;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		TileEntity te = this.getAdjacentTileEntity(facing);
		if (te instanceof IAspectContainer) {
			IAspectContainer iac = (IAspectContainer)te;
			mode.collectEnergy(this, world, x, y, z, iac);
			this.checkAndRequest();

			mode.generateAspects(this, world, x, y, z, iac);

			if (world.isRemote)
				this.spawnParticles(world, x, y, z, iac);
		}
	}

	public void stepMode() {
		mode = mode.next();
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z, IAspectContainer iac) {
		double h = 0.2875;
		Aspect a = null;
		switch(mode) {
			case DEMAND:
				a = selected;
				break;
			case SUSTAIN:
				AspectList al = iac.getAspects();
				if (al == null || al.aspects.isEmpty())
					break;
				ArrayList<Aspect> li = new ArrayList(al.aspects.keySet());
				a = li.get((this.getTicksExisted()/20)%li.size());
				break;
		}
		if (a == null)
			return;
		//ElementTagCompound tag = getAspectCost(a);
		//for (CrystalElement e : tag.elementSet()) {
		if (ReikaRandomHelper.doWithChance(15)) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.375);
			double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.375);
			EntityCenterBlurFX fx = new EntityCenterBlurFX(world, rx, y+h, rz, 0, 0, 0).setColor(a.getColor());
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		//}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		if (NBT.hasKey("aspect"))
			this.selectAspect(NBT.getString("aspect"));

		mode = AspectMode.list[NBT.getInteger("mode")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setString("aspect", selected != null ? selected.getTag() : "null");

		NBT.setInteger("mode", mode.ordinal());
	}

	public void selectAspect(String asp) {
		selected = Strings.isNullOrEmpty(asp) ? null : Aspect.aspects.get(asp);
	}

	public Aspect getAspectForRender() {
		switch(mode) {
			case DEMAND:
				return selected;
			case SUSTAIN:
				TileEntity te = this.getAdjacentTileEntity(facing);
				if (te instanceof IAspectContainer) {
					AspectList al = ((IAspectContainer)te).getAspects();
					if (al == null || al.aspects.isEmpty())
						return null;
					ArrayList<Aspect> li = new ArrayList(al.aspects.keySet());
					return li.get((this.getTicksExisted()/20)%li.size());
				}
				else {
					return null;
				}
		}
		return null;
	}

	public static ElementTagCompound getAspectCost(Aspect a) {
		return ChromaAspectManager.instance.getElementCost(a, 2).power(1.67).scale(20);
	}

	private void checkAndRequest(Aspect a) {
		if (a != null) {
			ElementTagCompound tag = this.getAspectCost(a).scale(64);
			tag.clamp(CAPACITY);
			currentRequest.add(tag);
		}
	}

	private void checkAndRequest() {
		for (CrystalElement e : currentRequest.elementSet()) {
			int max = Math.min(currentRequest.getValue(e), this.getMaxStorage(e));
			int diff = max-this.getEnergy(e);
			if (diff > 0) {
				this.requestEnergy(e, diff);
			}
		}
		currentRequest.clear();
	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		double d = 0.21875-0.5;
		double dx = d+(e.ordinal()/4)*(0.1875);
		double dz = d+(e.ordinal()%4)*(0.1875);
		//ReikaJavaLibrary.pConsole(dx+", "+dz);
		return new ImmutableTriple(dx, 0.4, dz);
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return true;
	}

	@Override
	public int maxThroughput() {
		return 250;
	}

	@Override
	public boolean canConduct() {
		return false;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return CAPACITY;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ASPECT;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord-1, zCoord, xCoord+1, yCoord+1, zCoord+2);
	}

	@Override
	public ElementTagCompound getRequestedTotal() {
		return selected != null ? this.getAspectCost(selected) : null;
	}

	@Override
	public double getIncomingBeamRadius() {
		return 0.25;
	}

	public AspectMode getMode() {
		return mode;
	}

	public static enum AspectMode {

		DEMAND(),
		SUSTAIN();

		private static AspectMode[] list = values();

		public AspectMode next() {
			return list[(this.ordinal()+1)%list.length];
		}

		private void collectEnergy(TileEntityAspectFormer te, World world, int x, int y, int z, IAspectContainer iac) {
			if (!world.isRemote && te.getCooldown() == 0 && te.checkTimer.checkCap()) {
				switch(this) {
					case DEMAND:
						if (te.selected != null)
							te.checkAndRequest(te.selected);
						break;
					case SUSTAIN:
						AspectList al = iac.getAspects();
						if (al != null) {
							for (Aspect a : al.aspects.keySet()) {
								te.checkAndRequest(a);
							}
						}
						break;
				}
			}
		}

		private void generateAspects(TileEntityAspectFormer te, World world, int x, int y, int z, IAspectContainer iac) {
			switch(this) {
				case DEMAND:
					if (te.selected != null)
						this.addAspect(te, te.selected, iac);
					break;
				case SUSTAIN:
					AspectList al = iac.getAspects();
					if (al != null) {
						for (Aspect a : al.aspects.keySet()) {
							this.addAspect(te, a, iac);
						}
					}
					break;
			}
		}

		private void addAspect(TileEntityAspectFormer te, Aspect a, IAspectContainer iac) {
			int amt = iac instanceof TileEntityAspectJar ? 100 : a.isPrimal() ? 4 : 1;
			ElementTagCompound tag = getAspectCost(a);
			int frac = (int)te.energy.divide(tag);
			amt = Math.min(amt, frac);
			if (amt > 0) {
				int left = iac.addToContainer(a, amt);
				int added = amt-left;
				if (added > 0) {
					te.drainEnergy(tag.scale(added));
				}
			}
		}

	}

}
