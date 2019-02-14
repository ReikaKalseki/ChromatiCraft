/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.List;

import Reika.ChromatiCraft.Auxiliary.TemporaryCrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;


public abstract class ItemPoweredChromaTool extends ItemChromaTool {

	public ItemPoweredChromaTool(int index) {
		super(index);
		//this.setMaxDamage(getChargeStates()-1);
	}


	protected abstract CrystalElement getColor();
	public abstract int getMaxCharge();
	public abstract int getChargeStates();

	protected abstract boolean isActivated(EntityPlayer e, boolean held);
	protected abstract int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is);
	protected abstract boolean doTick(ItemStack is, World world, EntityPlayer e, boolean held);

	protected int getChargeRate(ItemStack is, int base) {
		return base;
	}

	protected int getChargeState(float frac) {
		return Math.round((this.getChargeStates()-1)*frac);
	}

	@Override
	public final void getSubItems(Item i, CreativeTabs c, List li) {
		li.add(this.getChargedItem(0));
		li.add(this.getChargedItem(this.getMaxCharge()));
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		li.add(String.format("Energy: %.2f%s", 100F*this.getCharge(is)/this.getMaxCharge(), "%"));
	}

	@Override
	public final void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (e instanceof EntityPlayer) {
			if (this.isActivated((EntityPlayer)e, held)) {
				if (this.getCharge(is) > 0) {
					if (this.doTick(is, world, (EntityPlayer)e, held))
						this.removeCharge(is, this.getChargeConsumptionRate((EntityPlayer)e, world, is));
				}
			}
		}
	}

	@Override
	public final int getEntityLifespan(ItemStack is, World world) {
		return Integer.MAX_VALUE;
	}

	@Override
	public final boolean onEntityItemUpdate(EntityItem ei) {
		int charge = this.getCharge(ei.getEntityItem());
		if (!ei.worldObj.isRemote) {
			if (charge < this.getMaxCharge()) {
				int range = 32;
				WorldLocation loc = new WorldLocation(ei);
				CrystalReceiver r = new TemporaryCrystalReceiver(loc, 0, range, 0.0625, ResearchLevel.ENDGAME);
				ItemStack is = ei.getEntityItem();
				int amt = this.getChargeRate(is);
				CrystalElement e = this.getColor();
				//CrystalSource s = CrystalNetworker.instance.findSourceWithX(r, e, amt, range, true);
				CrystalSource s = CrystalNetworker.instance.getNearestTileOfType(r, CrystalSource.class, range);
				if (s != null) {
					s.drain(e, amt*4);
					if (s instanceof TileEntityCrystalPylon) {
						amt *= 1.25; //25% boost
						if (((TileEntityCrystalPylon)s).isEnhanced())
							amt *= 1.6; //net 2x
					}
					if (loc.getBlock() == ChromaBlocks.CHROMA.getBlockInstance() && loc.getBlockMetadata() == 0)
						amt *= 1.25;
					this.addCharge(is, amt);
					ReikaPacketHelper.sendEntitySyncPacket(DragonAPIInit.packetChannel, ei, 32);
					//ReikaJavaLibrary.pConsole(this.getCharge(is)+" (+"+this.getChargeRate(is)+", f="+(this.getCharge(is)/(float)MAX_CHARGE));
				}
			}
		}
		else {
			if (charge > 0) {
				this.doChargeFX(ei, charge);
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	private void doChargeFX(EntityItem ei, int charge) {
		if (charge == this.getMaxCharge()) {
			double ang = 360*Math.sin(ei.age/50D);
			for (int i = 0; i < 360; i += 60) {
				double v = 0.125+0.0625*Math.sin(ei.age/250D);
				double vx = v*Math.cos(Math.toRadians(ang+i));
				double vz = v*Math.sin(Math.toRadians(ang+i));
				EntityBlurFX fx = new EntityBlurFX(ei.worldObj, ei.posX, (int)ei.posY+0.25, ei.posZ, vx, 0, vz).setLife(40).setRapidExpand().setScale(1.5F);
				fx.setColor(this.getColor().getColor());
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
		else {
			double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			double[] vel = ReikaPhysicsHelper.polarToCartesian(v, itemRand.nextDouble()*360, itemRand.nextDouble()*360);
			float s = 1+itemRand.nextFloat();
			EntityBlurFX fx = new EntityBlurFX(ei.worldObj, ei.posX, (int)ei.posY+0.25, ei.posZ, vel[0], vel[1], vel[2]).setLife(40).setScale(s).setColliding();
			fx.setColor(this.getColor().getColor());
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private int getChargeRate(ItemStack is) {
		int get = this.getCharge(is);
		int base = (int)(5*Math.min(20, 1+100*ReikaMathLibrary.cosInterpolation(0, this.getMaxCharge(), get)));
		return this.getChargeRate(is, base);
	}

	public final ItemStack getChargedItem(int charge) {
		ItemStack is = new ItemStack(this);
		this.addCharge(is, charge);
		return is;
	}

	public final int addCharge(ItemStack is, int amt) {
		int get = this.getCharge(is);
		amt = Math.min(amt, this.getMaxCharge()-get);
		this.setCharge(is, get+amt);
		return amt;
	}

	public final int removeCharge(ItemStack is, int amt) {
		int get = this.getCharge(is);
		amt = Math.min(get, amt);
		this.setCharge(is, get-amt);
		return amt;
	}

	private void setCharge(ItemStack is, int amt) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		int max = this.getMaxCharge();
		is.stackTagCompound.setInteger("charge", MathHelper.clamp_int(amt, 0, max));
		is.setItemDamage(this.getChargeState(this.getCharge(is)/(float)max));
	}

	public final int getCharge(ItemStack is) {
		return is.stackTagCompound != null ? is.stackTagCompound.getInteger("charge") : 0;
	}

	@Override
	public final int getItemSpriteIndex(ItemStack item) {
		return super.getItemSpriteIndex(item)+item.getItemDamage();
	}

}
