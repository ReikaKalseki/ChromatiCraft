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

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ASM.InterfaceInjector.Injectable;
import Reika.DragonAPI.Interfaces.Item.MultisheetItem;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.IArmorApiarist;


@Injectable(value={"forestry.api.apiculture.IArmorApiarist"})
public class ItemFloatstoneBoots extends ItemArmor implements MultisheetItem, ISpecialArmor {

	private final int textureIndex;

	public ItemFloatstoneBoots(int tex, int render) {
		super(ChromatiCraft.FLOATSTONE, render, 3);
		textureIndex = tex;
		maxStackSize = 1;
		this.setNoRepair();
		this.setCreativeTab(this.isAvailableInCreativeMode() ? this.getCreativePage() : null);
	}

	private boolean isAvailableInCreativeMode() {
		if (ChromatiCraft.instance.isLocked())
			return false;
		return true;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer ep, ItemStack is) {
		ep.capabilities.allowFlying = true;
		if (world.isRemote) {
			this.addParticles(world, ep);
		}
		if (is.stackTagCompound != null) {
			if (is.stackTagCompound.hasKey("special")) {
				ItemStack load = ItemStack.loadItemStackFromNBT(is.stackTagCompound.getCompoundTag("special"));
				if (load != null) {
					load.getItem().onArmorTick(world, ep, load);
				}
			}
		}
	}

	public static ItemStack getSpecialItem(ItemStack is) {
		if (is.stackTagCompound != null) {
			if (is.stackTagCompound.hasKey("special")) {
				ItemStack load = ItemStack.loadItemStackFromNBT(is.stackTagCompound.getCompoundTag("special"));
				if (load != null) {
					return load.copy();
				}
			}
		}
		return null;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		ItemStack in = this.getSpecialItem(is);
		if (in != null) {
			li.add("Made from "+in.getDisplayName());
		}
	}

	@SideOnly(Side.CLIENT)
	public static void addParticles(World world, EntityPlayer ep) {
		if (ep.capabilities.isFlying) {
			int c = 0xE02739;
			int n = 4;
			for (int i = 0; i < n; i++) {
				double a = Math.toRadians(i*360D/n+world.getTotalWorldTime()*12D);
				double r = 0.25;
				double dx = r*Math.cos(a);
				double dz = r*Math.sin(a);
				double dy = 0.1875D*1.5;
				EntityFX fx = new EntityCCBlurFX(world, ep.posX+dx, ep.posY-1.62-dy, ep.posZ+dz, 0, 0.03125, 0).setColor(c).setLife(10);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}

			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
			EntityFX fx = new EntityCCBlurFX(world, ep.posX, ep.posY-1.62, ep.posZ, vx, -0.25, vz).setColor(c).setLife(15).setRapidExpand().setScale(1.5F);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void registerIcons(IIconRegister ico) {}

	@Override
	@SideOnly(Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack is) {
		return null;
	}

	@Override
	public final String getItemStackDisplayName(ItemStack is) {
		ChromaItems ir = ChromaItems.getEntry(is);
		String name = ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			//name = ModList.NEI.isLoaded() && DragonAPICore.hasGameLoaded() ? ObfuscatedNameHandler.registerName(name, is) : name;
			if (this.obfuscate(is)) {
				//name = EnumChatFormatting.OBFUSCATED.toString()+name;
				name = this.getObfuscatedName(name);
			}
		}
		return name;
	}

	@SideOnly(Side.CLIENT)
	private String getObfuscatedName(String name) {
		return ChromaFontRenderer.FontType.OBFUSCATED.id+name;
	}

	@SideOnly(Side.CLIENT)
	private boolean obfuscate(ItemStack is) {
		if (!DragonAPICore.hasGameLoaded())
			return false;
		return !ProgressStage.DIMENSION.isPlayerAtStage(Minecraft.getMinecraft().thePlayer);
	}

	@Override
	public int getItemSpriteIndex(ItemStack is) {
		return textureIndex;
	}

	@Override
	public Class getTextureReferenceClass() {
		return ChromatiCraft.class;
	}

	protected final CreativeTabs getCreativePage() {
		return ChromatiCraft.tabChromaTools;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ChromatiCraft/Textures/Items/items_tool.png";
	}

	@Override
	public String getSpritesheet(ItemStack is) {
		return this.getTexture(is);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final ModelBiped getArmorModel(EntityLivingBase elb, ItemStack is, int armorSlot)
	{
		return null;//ChromaClient.getArmorRenderer(ChromaItems.getEntry(is));
	}

	@Override
	public final String getArmorTexture(ItemStack is, Entity entity, int slot, String type) {
		ChromaItems item = ChromaItems.getEntry(is);
		String sg = ChromaClient.getArmorTextureAsset(item);
		return sg;
	}

	public static boolean isFloatBoots(ItemStack is) {
		return is.getItem() instanceof ItemFloatstoneBoots;
	}

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		ItemStack is = this.getSpecialItem(armor);
		if (is != null && is.getItem() instanceof ISpecialArmor)
			return ((ISpecialArmor)is.getItem()).getProperties(player, is, source, damage, slot);
		ItemStack isa = is != null ? is : armor.copy();
		ItemArmor ia = is != null ? (ItemArmor)is.getItem() : this;
		return new ArmorProperties(0, ia.damageReduceAmount / 25D, isa.getMaxDamage() + 1 - isa.getItemDamage()); //default
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		ItemStack is = this.getSpecialItem(armor);
		if (is != null && is.getItem() instanceof ISpecialArmor)
			return ((ISpecialArmor)is.getItem()).getArmorDisplay(player, is, slot);
		return is != null ? ((ItemArmor)is.getItem()).damageReduceAmount : 0;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		ItemStack is = this.getSpecialItem(stack);
		if (is != null && is.getItem() instanceof ISpecialArmor) {
			((ISpecialArmor)is.getItem()).damageArmor(entity, is, source, damage, slot);
		}
		else if (is != null) {
			is.damageItem(1, entity);
		}
	}

	//IArmorApiarist
	public boolean protectEntity(EntityLivingBase entity, ItemStack armor, String cause, boolean doProtect) {
		ItemStack head = entity.getEquipmentInSlot(4);
		return head != null && head.getItem() instanceof IArmorApiarist && ((IArmorApiarist)head.getItem()).protectEntity(entity, head, cause, doProtect);
	}

	@Deprecated
	public boolean protectPlayer(EntityPlayer player, ItemStack armor, String cause, boolean doProtect) {
		return this.protectEntity(player, armor, cause, doProtect);
	}

	@Override
	public final void setDamage(ItemStack stack, int damage) {

	}

}
