/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.TemporaryCrystalReceiver;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Orbit;
import Reika.DragonAPI.Instantiable.OrbitMotionController;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemPurifyCrystal extends ItemChromaTool {

	private static final String TAG = "last_purify";
	public static final int CHARGE_STATES = 4;
	public static final int MAX_CHARGE = 216000*12; //3h in the Nether, 36h in the overworld

	public ItemPurifyCrystal(int index) {
		super(index);
		//this.setMaxDamage(CHARGE_STATES-1);
	}

	@Override
	public void getSubItems(Item i, CreativeTabs c, List li) {
		li.add(this.getChargedItem(0));
		li.add(this.getChargedItem(MAX_CHARGE));
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		li.add(String.format("Energy: %.2f%s", 100F*this.getCharge(is)/MAX_CHARGE, "%"));
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (e instanceof EntityPlayer) {
			//if (CrystalPotionController.isWorldHostile(world)) {
			if (this.getCharge(is) > 0) {
				e.getEntityData().setLong(TAG, world.getTotalWorldTime());
				removeCharge(is, CrystalPotionController.isWorldHostile(world) ? 12 : 1);
				if (world.isRemote) {
					this.doHeldFX(is, e);
				}
			}
			//}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doHeldFX(ItemStack is, Entity e) {
		if (itemRand.nextInt(2*4) == 0) {
			double r = 2.5;
			double ec = 0;//0.4+0.4*Math.sin(Math.toRadians(e.ticksExisted));
			double i = e.ticksExisted*4;//.ticksExisted%2 == 0 ? 60 : -60;
			double raan = 0;//(e.ticksExisted%2 == 0 ? 180 : 0)+RenderManager.instance.playerViewY;
			Orbit o = new Orbit(r, ec, i, raan, 0, 0);
			OrbitMotionController p = new OrbitMotionController(o, e.posX, e.posY, e.posZ).trackEntity(e);
			p.thetaSpeed = ReikaRandomHelper.getRandomBetween(1.5, 5);
			int l = 60+itemRand.nextInt(120);
			float s = (float)ReikaRandomHelper.getRandomPlusMinus(1, 0.25);
			EntityFX fx = new EntityBlurFX(e.worldObj, e.posX, e.posY, e.posZ).setPositionController(p).setLife(l).setIcon(ChromaIcons.CENTER).setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public int getEntityLifespan(ItemStack is, World world) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		int charge = this.getCharge(ei.getEntityItem());
		if (!ei.worldObj.isRemote) {
			if (charge < MAX_CHARGE) {
				int range = 32;
				WorldLocation loc = new WorldLocation(ei);
				CrystalReceiver r = new TemporaryCrystalReceiver(loc, 0, range, 0.0625, ResearchLevel.ENDGAME);
				ItemStack is = ei.getEntityItem();
				int amt = this.getChargeRate(is);
				CrystalElement e = CrystalElement.WHITE;
				//CrystalSource s = CrystalNetworker.instance.findSourceWithX(r, e, amt, range, true);
				CrystalSource s = CrystalNetworker.instance.getNearestTileOfType(r, CrystalSource.class, range);
				if (s != null) {
					s.drain(e, amt*4);
					if (s instanceof TileEntityCrystalPylon) {
						amt *= 1.25; //25% boost
						if (((TileEntityCrystalPylon)s).isEnhanced())
							amt *= 1.5;
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
		if (charge == MAX_CHARGE) {
			double ang = 360*Math.sin(ei.age/50D);
			for (int i = 0; i < 360; i += 60) {
				double v = 0.125+0.0625*Math.sin(ei.age/250D);
				double vx = v*Math.cos(Math.toRadians(ang+i));
				double vz = v*Math.sin(Math.toRadians(ang+i));
				EntityFX fx = new EntityBlurFX(ei.worldObj, ei.posX, (int)ei.posY+0.25, ei.posZ, vx, 0, vz).setLife(40).setRapidExpand().setScale(1.5F);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
		else {
			double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			double[] vel = ReikaPhysicsHelper.polarToCartesian(v, itemRand.nextDouble()*360, itemRand.nextDouble()*360);
			float s = 1+itemRand.nextFloat();
			EntityFX fx = new EntityBlurFX(ei.worldObj, ei.posX, (int)ei.posY+0.25, ei.posZ, vel[0], vel[1], vel[2]).setLife(40).setScale(s).setColliding();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public static boolean isActive(EntityPlayer ep) {
		return ep.worldObj.getTotalWorldTime()-ep.getEntityData().getLong(TAG) < 20;
	}

	private static int getChargeRate(ItemStack is) {
		int get = getCharge(is);
		return (int)(5*Math.min(20, 1+100*ReikaMathLibrary.cosInterpolation(0, MAX_CHARGE, get)));
	}

	public ItemStack getChargedItem(int charge) {
		ItemStack is = new ItemStack(this);
		this.addCharge(is, charge);
		return is;
	}

	public static int addCharge(ItemStack is, int amt) {
		int get = getCharge(is);
		amt = Math.min(amt, MAX_CHARGE-get);
		setCharge(is, get+amt);
		return amt;
	}

	public static int removeCharge(ItemStack is, int amt) {
		int get = getCharge(is);
		amt = Math.min(get, amt);
		setCharge(is, get-amt);
		return amt;
	}

	private static void setCharge(ItemStack is, int amt) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("charge", MathHelper.clamp_int(amt, 0, MAX_CHARGE));
		is.setItemDamage(Math.round((CHARGE_STATES-1)*getCharge(is)/(float)MAX_CHARGE));
	}

	public static int getCharge(ItemStack is) {
		return is.stackTagCompound != null ? is.stackTagCompound.getInteger("charge") : 0;
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		return super.getItemSpriteIndex(item)+item.getItemDamage();
	}

}
