/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Wands;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.SpriteRenderCallback;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCaptureWand extends ItemWandBase implements SpriteRenderCallback {

	private final ElementTagCompound perTick;
	private static final String NBT_TAG = "mob";

	public ItemCaptureWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.PINK, 10);
		this.addEnergyCost(CrystalElement.LIME, 20);
		this.addEnergyCost(CrystalElement.LIGHTGRAY, 50);

		perTick = this.getEnergy(0.1F);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (held && hasMob(is, world)) {
			EntityPlayer ep = (EntityPlayer)e;
			if (!ep.capabilities.isCreativeMode) {
				if (PlayerElementBuffer.instance.playerHas(ep, perTick))
					PlayerElementBuffer.instance.removeFromPlayer(ep, perTick);
				else {
					this.releaseMob(is, ep, world, MathHelper.floor_double(ep.posX), MathHelper.floor_double(ep.posY), MathHelper.floor_double(ep.posZ));
					ChromaSounds.ERROR.playSound(ep);
				}
			}
		}
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (this.hasMob(is, world)) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.releaseMob(is, ep, world, dx, dy, dz))
				return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is) {
		return this.hasMob(is, Minecraft.getMinecraft().theWorld);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (this.hasMob(is, ep.worldObj)) {
			EntityLiving e = this.getMob(is, ep.worldObj);
			li.add(String.format("Contains a %s.", EntityList.getEntityString(e)));
			li.add(String.format("Health: %.0f hearts", e.getHealth()/2));
		}
		else {
			li.add("Contains no mob.");
		}
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack is, EntityPlayer ep, EntityLivingBase elb) {
		if (!hasMob(is, ep.worldObj) && elb instanceof EntityLiving) {
			this.captureMob(is, ep, (EntityLiving)elb);
			ep.setCurrentItemOrArmor(0, is);
			return true;
		}
		return false;
	}

	private void captureMob(ItemStack is, EntityPlayer ep, EntityLiving el) {
		this.setMob(is, el);
		this.drainPlayer(ep);
		el.setDead();
	}

	private boolean releaseMob(ItemStack is, EntityPlayer ep, World world, int x, int y, int z) {
		EntityLiving e = this.getMob(is, world);
		if (e != null) {
			this.drainPlayer(ep);
			e.setPosition(x+0.5, y, z+0.5);
			if (!world.isRemote)
				world.spawnEntityInWorld(e);
			is.stackTagCompound.removeTag(NBT_TAG);
			return true;
		}
		return false;
	}

	private static void setMob(ItemStack is, EntityLiving e) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setString("name", EntityList.getEntityString(e));
		NBTTagCompound dat = new NBTTagCompound();
		e.writeToNBT(dat);
		nbt.setTag("data", dat);

		is.stackTagCompound.setTag(NBT_TAG, nbt);
	}

	private static EntityLiving getMob(ItemStack is, World world) {
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey(NBT_TAG))
			return null;
		NBTTagCompound mob = is.stackTagCompound.getCompoundTag(NBT_TAG);
		String n = mob.getString("name");
		EntityLiving e = (EntityLiving)EntityList.createEntityByName(n, world);
		if (e == null)
			return null;
		NBTTagCompound dat = mob.getCompoundTag("data");
		e.readFromNBT(dat);
		return e;
	}

	private static boolean hasMob(ItemStack is, World world) {
		return is.stackTagCompound != null && is.stackTagCompound.hasKey(NBT_TAG);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type) {
		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED) {
			EntityLiving e = this.getMob(is, Minecraft.getMinecraft().theWorld);
			if (e != null) {
				GL11.glPushMatrix();
				//GL11.glScaled(0.2, 0.2, 0.2);
				GL11.glRotated(30, 0, 1, 0);
				GL11.glRotated(-45, 1, 0, 0);
				//GL11.glTranslated(0, 0, 0);
				GL11.glTranslated(type == ItemRenderType.EQUIPPED_FIRST_PERSON ? 1.5 : 1, type == ItemRenderType.EQUIPPED_FIRST_PERSON ? 0.25 : 1, 1);
				//GL11.glTranslated(EntityRenderDispatcher., y, z);
				EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
				Render r = ReikaEntityHelper.getEntityRenderer(e.getClass());
				r.doRender(e, 0, 0, 0, 0, 0);
				GL11.glPopMatrix();
			}
		}
		return false;
	}

}
