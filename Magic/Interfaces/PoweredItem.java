package Reika.ChromatiCraft.Magic.Interfaces;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.Item.SpriteRenderCallback;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface PoweredItem extends SpriteRenderCallback {

	public abstract CrystalElement getColor(ItemStack is);
	public abstract int getMaxCharge();
	public abstract int getChargeStates();
	public abstract int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is);

	public int getChargeRate(ItemStack is, int base);
	public boolean hasChargeStates();
	public int getChargeState(float frac);

	public boolean canChargeWhilePlayerCharges();

	@SideOnly(Side.CLIENT)
	public abstract void doChargeFX(EntityItem ei, int charge);

}
