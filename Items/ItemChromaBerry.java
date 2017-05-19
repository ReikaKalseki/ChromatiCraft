/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Block.BlockActiveChroma.TileEntityChroma;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.RecentEventCounter;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemChromaBerry extends ItemCrystalBasic {

	private final PlayerMap<RecentEventCounter> eatingTimers = new PlayerMap();

	private static final int BERRY_EAT_LIFE = 600;
	private static final int BERRY_MIN_TRIGGER = 4;
	private static final int BERRY_CHANCE_PER_EAT = (100/((BERRY_EAT_LIFE/20)-BERRY_MIN_TRIGGER))*4;

	public ItemChromaBerry(int tex) {
		super(tex);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		int x = MathHelper.floor_double(ei.posX);
		int y = MathHelper.floor_double(ei.posY);
		int z = MathHelper.floor_double(ei.posZ);
		Block b = ei.worldObj.getBlock(x, y, z);
		if (b == ChromaBlocks.CHROMA.getBlockInstance()) {
			if (ei.worldObj.getBlockMetadata(x, y, z) == 0) {
				if (this.canCharge(ei)) {
					TileEntity te = ei.worldObj.getTileEntity(x, y, z);
					if (te instanceof TileEntityChroma) {
						TileEntityChroma tc = (TileEntityChroma)te;
						ItemStack is = ei.getEntityItem();
						int df = is.stackSize;
						//ReikaJavaLibrary.pConsole("pre "+is.stackSize, Side.SERVER);
						int amt = tc.activate(CrystalElement.elements[is.getItemDamage()], is.stackSize);
						//ReikaJavaLibrary.pConsole(amt+" from "+is.stackSize, Side.SERVER);
						if (!ei.worldObj.isRemote)
							is.stackSize -= amt;
						//ReikaJavaLibrary.pConsole(ei.age+":"+amt+", "+df+">"+is.stackSize, Side.SERVER);
						if (is.stackSize <= 0)
							ei.setDead();
						else
							ei.setEntityItemStack(is);
					}
				}
			}
		}
		return false;
	}

	private boolean canCharge(EntityItem ei) {
		EntityPlayer ep = ReikaItemHelper.getDropper(ei);
		if (ep != null) {
			if (ProgressStage.SHARDCHARGE.playerHasPrerequisites(ep)) {
				return true;
			}
		}
		return false;
	}

	/*
	@Override
	public boolean hasCustomEntity(ItemStack is) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack)
	{
		EntityChromaBerry ei = new EntityChromaBerry(world, location.posX, location.posY, location.posZ, itemstack);
		ei.motionX = location.motionX;
		ei.motionY = location.motionY;
		ei.motionZ = location.motionZ;
		ei.delayBeforeCanPickup = 10;
		return ei;
	}*/

	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer ep)
	{
		is.stackSize--;
		CrystalElement e = CrystalElement.elements[is.getItemDamage()%16];
		if (ReikaRandomHelper.doWithChance(20)) {
			if (PlayerElementBuffer.instance.hasElement(ep, e) && PlayerElementBuffer.instance.getPlayerFraction(ep, e) < 0.25) {
				PlayerElementBuffer.instance.addToPlayer(ep, e, 1, true);
			}
		}
		if (!ep.worldObj.isRemote) {
			int num = this.getOrCreateCounter(ep).addEntry(world.getTotalWorldTime(), BERRY_EAT_LIFE);
			double d = Math.min(1, ((num-BERRY_MIN_TRIGGER)*BERRY_CHANCE_PER_EAT)/100D);
			ReikaJavaLibrary.pConsole(num+" > "+d+" for "+BERRY_CHANCE_PER_EAT);
			if (num >= BERRY_MIN_TRIGGER && ReikaRandomHelper.doWithChance(d) || true) {
				PotionEffect get = ep.getActivePotionEffect(ChromatiCraft.lumarhea);
				int lvl = get != null ? get.getAmplifier()+1 : 0;
				int tmin = 50*(1+lvl);
				int tmax = 2*ReikaMathLibrary.intpow2(lvl+1, 6);
				int dur = tmax <= tmin ? tmin : ReikaRandomHelper.getRandomBetween(tmin, tmax);
				ep.addPotionEffect(new PotionEffect(ChromatiCraft.lumarhea.id, dur, lvl));
			}
		}
		return is;
	}

	private RecentEventCounter getOrCreateCounter(EntityPlayer ep) {
		RecentEventCounter r = eatingTimers.get(ep);
		if (r == null) {
			r = new RecentEventCounter();
			eatingTimers.put(ep, r);
		}
		return r;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack is)
	{
		return 64;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack p_77661_1_)
	{
		return EnumAction.eat;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep)
	{
		ep.setItemInUse(is, this.getMaxItemUseDuration(is));
		return is;
	}

}
