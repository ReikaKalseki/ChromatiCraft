/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.EnumMap;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.AbilityRituals;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Auxiliary.PlayerHandler.PlayerTracker;
import Reika.DragonAPI.Instantiable.Data.BlockBox;
import Reika.DragonAPI.Instantiable.Data.ScaledDirection;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public class AbilityHelper {

	//Client Side
	public int playerReach = -1;

	private final HashMap<EntityPlayer, WorldLocation> playerClicks = new HashMap();
	private final HashMap<EntityPlayer, Boolean> isDrawingBox = new HashMap();
	public final HashMap<EntityPlayer, ScaledDirection> shifts = new HashMap();

	private final EnumMap<Chromabilities, ElementTagCompound> tagMap = new EnumMap(Chromabilities.class);

	public static final AbilityHelper instance = new AbilityHelper();

	private AbilityHelper() {
		for (int i = 0; i < Chromabilities.abilities.length; i++) {
			Chromabilities c = Chromabilities.abilities[i];
			ElementTagCompound tag = AbilityRituals.instance.getAura(c);
			tagMap.put(c, tag);
		}
	}

	public void startDrawingBoxes(EntityPlayer ep) {
		isDrawingBox.put(ep, true);
	}

	public void stopDrawingBoxes(EntityPlayer ep) {
		isDrawingBox.put(ep, false);
		this.removePlayerClick(ep);
	}

	public void addPlayerClick(EntityPlayer ep, World world, int x, int y, int z) {
		if (!world.isRemote) {
			if (isDrawingBox.containsKey(ep) && isDrawingBox.get(ep)) {
				WorldLocation loc = new WorldLocation(world, x, y, z);
				if (playerClicks.containsKey(ep)) {
					WorldLocation loc2 = playerClicks.get(ep);
					BlockBox b = new BlockBox(loc, loc2);
					this.playerMakeBox(ep, b);
					this.removePlayerClick(ep);
				}
				else
					playerClicks.put(ep, loc);
			}
		}
	}

	public void removePlayerClick(EntityPlayer ep) {
		playerClicks.remove(ep);
	}

	private void playerMakeBox(EntityPlayer ep, BlockBox box) {
		if (!ep.worldObj.isRemote && Chromabilities.SHIFT.enabledOn(ep)) {
			ScaledDirection dir = shifts.get(ep);
			Chromabilities.shiftArea((WorldServer)ep.worldObj, box, dir.direction, dir.distance, ep);
		}
	}

	@SubscribeEvent
	public void clickBlock(PlayerInteractEvent evt) {
		if (evt.action == Action.RIGHT_CLICK_BLOCK) {
			this.addPlayerClick(evt.entityPlayer, evt.world, evt.x, evt.y, evt.z);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void middleMouseUse(MouseEvent e) {
		if (e.buttonstate && e.button == 2) { //what about people with no mousewheels? config option to use keybind?
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.thePlayer.capabilities.isCreativeMode) {
				MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlockClient(5, false);
				if (mov != null)
					return;
			}
			mc.thePlayer.openGui(ChromatiCraft.instance, ChromaGuis.ABILITY.ordinal(), mc.theWorld, 0, 0, 0);
		}
	}

	public static class ReachApplier implements PlayerTracker {

		public static final ReachApplier instance = new ReachApplier();

		private ReachApplier() {

		}

		@Override
		public void onPlayerLogin(EntityPlayer ep) {
			if (Chromabilities.REACH.enabledOn(ep)) {
				Chromabilities.REACH.trigger(ep, 0);
			}
		}

		@Override
		public void onPlayerLogout(EntityPlayer player) {

		}

		@Override
		public void onPlayerChangedDimension(EntityPlayer player, int dimFrom, int dimTo) {

		}

		@Override
		public void onPlayerRespawn(EntityPlayer player) {

		}

	}

	public static class PlayerExemptAITarget implements IEntitySelector {

		private final IEntitySelector base;

		public PlayerExemptAITarget(IEntitySelector ie)
		{
			base = ie;
		}

		@Override
		public boolean isEntityApplicable(Entity e) {
			if (base.isEntityApplicable(e)) {
				if (e instanceof EntityPlayer) {
					if (Chromabilities.COMMUNICATE.enabledOn((EntityPlayer)e)) {
						return false;
					}
					else {
						return true;
					}
				}
				else {
					return true;
				}
			}
			return false;
		}

	}

	public ElementTagCompound getElementsFor(Chromabilities c) {
		return tagMap.get(c).copy();
	}

	public ElementTagCompound getUsageElementsFor(Chromabilities c) {
		return tagMap.get(c).copy().scale(0.0002F);
	}

}
