/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface AbilityAPI {

	/** Call this to add abilities to the game. */
	public void addAbility(Ability a);

	/** Returns whether the player has completed the ritual to grant the power to use this. */
	public boolean playerHasAbility(EntityPlayer ep, Ability a);

	/** Implement this on your class to make it a custom ability. All instances of this class are expected to be singletons; enums are suggested. */
	public static interface Ability {

		/** The string ID of the ability. Must be unique, preferentially all in lowercase. */
		public String getID();

		/** The image file location relative to the class returned by getTextureReferenceClass. Args: Grayscaled yes/no (images are grayscaled
		 * if the ability is unavailable to the player). All images must be their own 50x50 png file. */
		@SideOnly(Side.CLIENT)
		public String getTexturePath(boolean gray);

		/** The class that getTexturePath defines the image file as relative to. Usually but not required to be the core mod class. */
		@SideOnly(Side.CLIENT)
		public Class getTextureReferenceClass();

		/** The maximum power level for this ability. Only used for trigger-type abilities. */
		public int getMaxPower();

		/** For "action-type" abilities like lightning. Actually performs the ability. Args: Player, power level. Return true if successfully executed. */
		public boolean trigger(EntityPlayer ep, int level);

		/** For "ambient-type" abilities like magnet mode. Performs the ability this tick. Args: Player */
		public void apply(EntityPlayer ep);

		/** Self-explanatory. */
		public String getDisplayName();

		/** The descriptive text that appears in the lexicon and on some GUIs. Be wary of excessively long descriptions overrunning the space. */
		public String getDescription();

		/** Called when the ability is removed (deactivated) on a player, either manually, from loss of charge, or from death. */
		public void onRemoveFromPlayer(EntityPlayer ep);

		/** Whether the player can attempt to obtain this ability FRAGMENT yet. Return false if there is some unmet prerequisite. */
		public boolean isAvailableToPlayer(EntityPlayer ep);

		/** Return true if the player can perform the given action. */
		public boolean canPlayerExecuteAt(EntityPlayer player);

		/** Is the ability ambient like magnet mode as opposed to trigger-based like lightning. */
		public boolean isTickBased();

		/** Only applicable for tick-based abilities. You can return null for anything else. */
		public Phase getTickPhase();

		/** Is the ability purely trigger based; some abilities have a trigger to set them up but have an ambient part as well (look to "SHIFT"
		 * for an example). */
		public boolean isPureEventDriven();

		/** Does the ability perform its actions clientside? */
		public boolean actOnClient();

		/** If the energy cost for this ability is per-tick as opposed to on-trigger or on-toggle. Usually true. */
		public boolean costsPerTick();

		/** Whether the ability is currently doing anything, irrespective of being enabled/disabled. Controls energy consumption and function. */
		public boolean isFunctioningOn(EntityPlayer ep);

	}

}
