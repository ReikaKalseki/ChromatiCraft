/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import Reika.ChromatiCraft.Block.Dye.BlockRainbowLeaf;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityRainbowBeacon extends TileEntityBase {

	@Override
	public Block getTileEntityBlockID() {
		return ChromaBlocks.RAINBOWLEAF.getBlockInstance();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.getWorldTime()%200 == 0) {
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, this.getTreeBaseY(), z).expand(48, 48, 48);
			List<EntityPlayer> inbox = world.getEntitiesWithinAABB(EntityPlayer.class, box);
			for (int i = 0; i < inbox.size(); i++) {
				EntityPlayer ep = inbox.get(i);
				for (int k = 0; k < 18; k++) {
					double ex = ReikaRandomHelper.getRandomPlusMinus(ep.posX, 1);
					double ez = ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 1);
					double ey = ep.posY-1+rand.nextDouble();
					int ix = MathHelper.floor_double(ex);
					int iy = MathHelper.floor_double(ey);
					int iz = MathHelper.floor_double(ez);
					BlockRainbowLeaf brl = (BlockRainbowLeaf)ChromaBlocks.RAINBOWLEAF.getBlockInstance();
					int color = brl.colorMultiplier(world, ix, iy, iz);
					Color c = new Color(color);
					float r = c.getRed()/255F;
					float g = c.getGreen()/255F;
					float b = c.getBlue()/255F;
					ReikaParticleHelper.REDSTONE.spawnAt(world, ex, ey, ez, r, g, b);
				}
				if (ep.getMaxHealth() > ep.getHealth()) {
					ep.setHealth(ep.getHealth()+2);
					//ReikaJavaLibrary.pConsole(0+":"+ep.getHealth()+":"+ep.getMaxHealth());
				}
				else if (ep.getMaxHealth() < 60) {
					ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(this.modifier());
					//ReikaJavaLibrary.pConsole(1+":"+ep.getHealth()+":"+ep.getMaxHealth());
				}
			}
		}
	}

	private AttributeModifier modifier() {
		return new AttributeModifier("Rainbow Tree @ "+System.currentTimeMillis(), 2D, 0);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected String getTEName() {
		return "Rainbow Tree Beacon";
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	}

	private int getTreeBaseY() {
		return yCoord-26;
	}

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

}
