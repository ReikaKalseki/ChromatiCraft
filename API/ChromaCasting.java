package Reika.ChromatiCraft.API;

import net.minecraft.item.ItemStack;

public interface ChromaCasting {

	/** Self-explanatory */
	public ItemStack getOutput();

	/** Recipe duration in ticks. */
	public int getDuration();

	/** 3x3 ItemStack array for rendering display. */
	public ItemStack[] getArrayForDisplay();

}
