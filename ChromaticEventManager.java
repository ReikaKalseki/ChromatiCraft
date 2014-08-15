/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Block.Dye.BlockDyeSapling;
import Reika.ChromatiCraft.Block.Dye.BlockRainbowSapling;
import Reika.ChromatiCraft.Items.Tools.ItemInventoryLinker;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.BiomeRainbowForest;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ChromaticEventManager {

	public static final ChromaticEventManager instance = new ChromaticEventManager();

	private final Random rand = new Random();

	private ChromaticEventManager() {

	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void sendLinkedItems(EntityItemPickupEvent ev) {
		EntityPlayer ep = ev.entityPlayer;
		EntityItem e = ev.item;
		ItemStack picked = e.getEntityItem();
		for (int i = 0; i < ep.inventory.mainInventory.length; i++) {
			ItemStack in = ep.inventory.mainInventory[i];
			if (in != null && in.getItem() == ChromaItems.LINK.getItemInstance()) {
				ItemInventoryLinker iil = (ItemInventoryLinker)in.getItem();
				if (iil.linksItem(in, picked)) {
					if (iil.processItem(ep.worldObj, in, picked)) {
						e.playSound("random.pop", 0.5F, 1);
						e.setDead();
						ev.setCanceled(true);
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onBucketUse(FillBucketEvent event) {
		World world = event.world;
		MovingObjectPosition pos = event.target;
		int x = pos.blockX;
		int y = pos.blockY;
		int z = pos.blockZ;
		Block b = world.getBlock(x, y, z);
		if (b == ChromaBlocks.CHROMA.getBlockInstance()) {
			world.setBlockToAir(x, y, z);
			event.setResult(Result.ALLOW);
			event.result = ChromaItems.BUCKET.getStackOf();
			//event.entityPlayer.setCurrentItemOrArmor(0, event.result);
		}
		else if (b == ChromaBlocks.ENDER.getBlockInstance()) {
			world.setBlockToAir(x, y, z);
			event.setResult(Result.ALLOW);
			event.result = ChromaItems.BUCKET.getStackOfMetadata(1);
			//event.entityPlayer.setCurrentItemOrArmor(0, event.result);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void extraXP(AttackEntityEvent ev) {
		EntityPlayer ep = ev.entityPlayer;
		Entity tg = ev.target;
		if (tg instanceof EntityLivingBase) {
			EntityLivingBase elb = (EntityLivingBase)tg;
			for (int i = 0; i < ep.inventory.mainInventory.length; i++) {
				ItemStack is = ep.inventory.mainInventory[i];
				if (is != null) {
					if (is.getItem() == ChromaItems.PENDANT3.getItemInstance()) {
						CrystalBlock.applyEffectFromColor(100, 3, elb, ReikaDyeHelper.getColorFromDamage(is.getItemDamage()));
					}
					else if (is.getItem() == ChromaItems.PENDANT.getItemInstance()) {
						CrystalBlock.applyEffectFromColor(100, 1, elb, ReikaDyeHelper.getColorFromDamage(is.getItemDamage()));
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void extraXP(LivingDropsEvent ev) {
		EntityLivingBase e = ev.entityLiving;
		DamageSource src = ev.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			int meta = ReikaDyeHelper.PURPLE.ordinal();
			int val = e instanceof EntityPlayer ? 25 : e instanceof EntityLiving ? ((EntityLiving)e).experienceValue : 5;
			if (val == 0)
				val = 5;
			if (e instanceof EntityDragon)
				val = 10000;
			if (ReikaInventoryHelper.checkForItemStack(ChromaItems.PENDANT3.getStackOfMetadata(meta), ep.inventory, false)) {
				for (int i = 0; i < 3; i++) {
					double px = e.posX;
					double pz = e.posZ;
					EntityXPOrb xp = new EntityXPOrb(e.worldObj, px, e.posY, pz, val);
					if (!e.worldObj.isRemote)
						e.worldObj.spawnEntityInWorld(xp);
				}
			}
			else if (ReikaInventoryHelper.checkForItemStack(ChromaItems.PENDANT.getStackOfMetadata(meta), ep.inventory, false)) {
				double px = e.posX;
				double pz = e.posZ;
				EntityXPOrb xp = new EntityXPOrb(e.worldObj, px, e.posY, pz, val);
				if (!e.worldObj.isRemote)
					e.worldObj.spawnEntityInWorld(xp);
			}
		}
	}

	@SubscribeEvent
	public void bonemealEvent (BonemealEvent event)
	{
		if (!event.world.isRemote)  {
			if (event.block == ChromaBlocks.DYESAPLING.getBlockInstance()) {
				BlockDyeSapling sap = (BlockDyeSapling)event.block;
				World world = event.world;
				int x = event.x;
				int y = event.y;
				int z = event.z;
				if (sap.canGrowAt(world, x, y, z, true)) {
					sap.func_149878_d(world, x, y, z, rand);
					event.setResult(Event.Result.ALLOW);
				}
			}
			else if (event.block == ChromaBlocks.RAINBOWSAPLING.getBlockInstance()) {
				BlockRainbowSapling sap = (BlockRainbowSapling)event.block;
				World world = event.world;
				int x = event.x;
				int y = event.y;
				int z = event.z;
				if (sap.canGrowAt(world, x, y, z)) {
					sap.func_149878_d(world, x, y, z, rand);
					event.setResult(Event.Result.ALLOW);
				}
			}
		}
	}

	@SubscribeEvent
	public void colorSheep(LivingSpawnEvent ev) {
		World world = ev.world;
		if (world.isRemote)
			return;
		int x = (int)Math.floor(ev.x);
		int y = (int)Math.floor(ev.y);
		int z = (int)Math.floor(ev.z);
		EntityLivingBase e = ev.entityLiving;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ChromatiCraft.isRainbowForest(b)) {
			if (e instanceof EntitySheep) {
				EntitySheep es = (EntitySheep)e;
				es.setFleeceColor(rand.nextInt(16));
			}
		}
	}

	/** Not functional due to BlockLeaves being the <i>only</i> block not to fire the event */
	@SubscribeEvent
	public void addLeafColors(HarvestDropsEvent evt) {
		World world = evt.world;
		if (ChromaOptions.NORMAL.getState())
			return;
		int x = evt.x;
		int y = evt.y;
		int z = evt.z;
		ArrayList<ItemStack> li = evt.drops;
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.leaves || b == Blocks.leaves2) {
			int meta = rand.nextInt(16);
			ItemStack sapling = new ItemStack(ChromaBlocks.DYESAPLING.getBlockInstance(), 1, meta);
			if (ReikaRandomHelper.doWithChance(0.04)) { //4% chance per leaf block
				li.add(sapling);
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void controlSlimes(CheckSpawn ev) {
		World world = ev.world;
		if (world.isRemote)
			return;
		int x = (int)Math.floor(ev.x);
		int y = (int)Math.floor(ev.y);
		int z = (int)Math.floor(ev.z);
		EntityLivingBase e = ev.entityLiving;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ChromatiCraft.isRainbowForest(b)) {
			ev.setResult(BiomeRainbowForest.isMobAllowed(e) ? Result.DEFAULT : Result.DENY);
		}
		//ReikaJavaLibrary.pConsole(b.biomeName+":"+e.getCommandSenderName()+":"+ReikaEntityHelper.isHostile(e)+":"+ev.getResult());
	}

	@SubscribeEvent
	public void killSpawns(LivingSpawnEvent ev) {
		World world = ev.world;
		if (world.isRemote)
			return;
		int x = (int)Math.floor(ev.x);
		int y = (int)Math.floor(ev.y);
		int z = (int)Math.floor(ev.z);
		EntityLivingBase e = ev.entityLiving;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ChromatiCraft.isRainbowForest(b)) {
			if (!BiomeRainbowForest.isMobAllowed(e)) {
				e.setDead();
			}
		}
	}

	@SubscribeEvent
	public void biomeDrops(LivingDropsEvent ev) {
		EntityLivingBase e = ev.entityLiving;
		ArrayList<EntityItem> drops = ev.drops;
		int x = (int)Math.floor(e.posX);
		int y = (int)Math.floor(e.posY);
		int z = (int)Math.floor(e.posZ);
		World world = e.worldObj;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);

		if (ChromatiCraft.isRainbowForest(b)) {
			if (e instanceof EntitySlime) {
				int dmg = e.getEntityId()%16;
				int size = 1+rand.nextInt(3);
				ItemStack dye = new ItemStack(Items.dye, size, dmg);
				ReikaItemHelper.dropItem(world, e.posX, e.posY, e.posZ, dye);
			}

			int spawn = ChromaOptions.ANIMALSPAWN.getValue();
			int def = ChromaOptions.ANIMALSPAWN.getDefaultValue();
			if (spawn < def) {
				int mult = def-spawn;
				if (e instanceof EntityAnimal) {
					for (int i = 0; i < mult; i++) {
						for (int k = 0; k < drops.size(); k++)
							ReikaItemHelper.dropItem(world, e.posX, e.posY, e.posZ, drops.get(k).getEntityItem());
					}
				}
			}
		}
	}



}