/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.InscriptionRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.InscriptionRecipes.InscriptionRecipe;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class ItemDataCrystal extends ItemChromaTool {

	public ItemDataCrystal(int index) {
		super(index);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (is.stackTagCompound != null && world.getTotalWorldTime()-is.stackTagCompound.getLong("last") >= 10) {
			int get = is.stackTagCompound.getInteger("carve");
			if (get > 0) {
				is.stackTagCompound.setInteger("carve", get-1);
				if (get == 1)
					is.stackTagCompound = null;
			}
		}
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		/*
		if (LoreManager.instance.hasPlayerCompletedBoard(ep) && world.getBlock(x, y, z) == ChromaBlocks.LOREREADER.getBlockInstance() && ChromaStructures.getLoreReaderStructure(world, x, y, z).matchInWorld()) {
			((TileEntityLoreReader)world.getTileEntity(x, y, z)).addCrystal();
			ep.setCurrentItemOrArmor(0, null);
			return true;
		}
		 */
		if (!ProgressStage.TOWER.isPlayerAtStage(ep)) {
			is.stackTagCompound = null;
			return true;
		}
		InscriptionRecipe ir = InscriptionRecipes.instance.getInscriptionRecipe(world, x, y, z);
		if (ir == null) {
			is.stackTagCompound = null;
			return true;
		}
		if (is.stackTagCompound != null && is.stackTagCompound.hasKey("recipe") && ir.referenceIndex != is.stackTagCompound.getInteger("recipe")) {
			is.stackTagCompound = null;
			return true;
		}
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("recipe", ir.referenceIndex);
		is.stackTagCompound.setLong("last", world.getTotalWorldTime());
		Coordinate loc = Coordinate.readFromNBT("loc", is.stackTagCompound);
		if (loc != null && loc.equals(x, y, z)) {
			if (!world.isRemote) {
				int tick = is.stackTagCompound.getInteger("carve")+1;
				if (tick >= ir.duration) {
					ir.place(world, x, y, z, ep);
					ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.INSCRIBE.ordinal(), world, x, y, z, 128, ir.referenceIndex);
					is.stackTagCompound = null;
				}
				else {
					is.stackTagCompound.setInteger("carve", tick);
					if (tick%5 == 0)
						ChromaSounds.INSCRIBE.playSoundAtBlock(world, x, y, z);
				}
			}
			else {
				ReikaRenderHelper.spawnDropParticles(world, x, y, z, ir.input.blockID, ir.input.metadata);
			}
		}
		else {
			new Coordinate(x, y, z).writeToNBT("loc", is.stackTagCompound);
			is.stackTagCompound.setInteger("carve", 0);
			ChromaSounds.INSCRIBE.playSoundAtBlock(world, x, y, z);
		}
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.LOREKEY.ordinal(), world, 0, 0, 0);
		return super.onItemRightClick(is, world, ep);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity e, ItemStack is) {
		EntityDataCrystal ei = new EntityDataCrystal(world, e.posX, e.posY, e.posZ, is);
		ei.motionX = e.motionX;
		ei.motionY = e.motionY;
		ei.motionZ = e.motionZ;
		ei.delayBeforeCanPickup = 40;
		EntityPlayer ep = ReikaItemHelper.getDropper((EntityItem)e);
		if (ep != null)
			ReikaItemHelper.setDropper(ei, ep);
		return ei;
	}

	public static class EntityDataCrystal extends EntityItem {

		public EntityDataCrystal(World world) {
			super(world);
		}

		public EntityDataCrystal(World world, double posX, double posY, double posZ, ItemStack is) {
			super(world, posX, posY, posZ, is);
		}

		@Override
		public boolean isEntityInvulnerable() {
			return true;
		}

		@Override
		public boolean attackEntityFrom(DamageSource src, float dmg) {
			return false;
		}

		@Override
		public void setAgeToCreativeDespawnTime() {

		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			age = 0;
			hoverStart = 0;
			rotationYaw = 0;
			if (posY < 0) {
				posY = 0;
				motionY = Math.max(motionY, 0);
			}
			if (motionY < 0 && motionY < -0.125) {
				motionY = -0.125;
			}
			velocityChanged = true;
		}

		@Override
		public float getShadowSize() {
			return 0;
		}

		@Override
		public boolean isInRangeToRenderDist(double distsq) {
			return true;
		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return pass == 1;
		}

	}

	@Override
	public boolean showDurabilityBar(ItemStack is) {
		return is.stackTagCompound != null;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack is) {
		return 1-(double)is.stackTagCompound.getInteger("carve")/InscriptionRecipes.instance.getRecipeByID(is.stackTagCompound.getInteger("recipe")).duration;
	}

}
