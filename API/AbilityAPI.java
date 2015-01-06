/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AbilityAPI {

	private static Class abilities;
	private static Method add;

	/** Call this to add abilities to the game. */
	public static void addAbility(Ability a) {
		try {
			add.invoke(null, a);
		}
		catch (Exception e) {
			System.out.println("Could not add ability "+a+"!");
			e.printStackTrace();
		}
	}

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

		/** For "action-type" abilities like lightning. Actually performs the ability. Args: Player, power level */
		public void trigger(EntityPlayer ep, int level);

		/** For "ambient-type" abilities like magnet mode. Performs the ability this tick. Args: Player */
		public void apply(EntityPlayer ep);

		/** Self-explanatory. */
		public String getDisplayName();

		/** The descriptive text that appears in the lexicon and on some GUIs. Be wary of excessively long descriptions overrunning the space. */
		public String getDescription();

		/** Called when the ability is removed (deactivated) on a player, either manually, from loss of charge, or from death. */
		public void onRemoveFromPlayer(EntityPlayer ep);

		/** Whether the player can attempt to obtain this ability yet. Return false if there is some unmet prerequisite. */
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

	}

	static {
		try {
			abilities = Class.forName("Reika.ChromatiCraft.Registry.Chromabilities");
			add = abilities.getMethod("addAbility", Ability.class);
		}
		catch (Exception e) {
			System.out.println("Failed to load the ability handler properly!");
			e.printStackTrace();
		}
	}

}
