package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum HoldingChecks {

	MANIPULATOR(),
	FOCUSCRYSTAL(),
	REPEATER();

	private float fade;
	private long lastUpdate;

	@SideOnly(Side.CLIENT)
	public boolean isClientHolding() {
		return this.isHolding(Minecraft.getMinecraft().thePlayer);
	}

	public boolean isHolding(EntityPlayer ep) {
		return this.match(ep.getCurrentEquippedItem());
	}

	@SideOnly(Side.CLIENT)
	public float getFade() {
		long t = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
		if (t != lastUpdate) {
			lastUpdate = t;
			if (this.isClientHolding()) {
				fade = Math.min(1, fade+0.125F);
			}
			else {
				fade = Math.max(0, fade-0.03125F);
			}
		}
		return fade;
	}

	private boolean match(ItemStack is) {
		switch(this) {
			case MANIPULATOR:
				return ChromaItems.TOOL.matchWith(is);
			case FOCUSCRYSTAL:
				return ChromaItems.PLACER.matchWith(is) && is.getItemDamage() == ChromaTiles.FOCUSCRYSTAL.ordinal();
			case REPEATER:
				return ChromaItems.PLACER.matchWith(is) && ChromaTiles.TEList[is.getItemDamage()].isRepeater();
			default:
				return false;
		}
	}

}
