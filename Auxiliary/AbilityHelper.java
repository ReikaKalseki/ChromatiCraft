/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.AbilityRituals;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler.PlayerTracker;
import Reika.DragonAPI.Instantiable.Data.BlockBox;
import Reika.DragonAPI.Instantiable.Data.MultiMap;
import Reika.DragonAPI.Instantiable.Data.ScaledDirection;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public class AbilityHelper {

	//Client Side
	public int playerReach = -1;

	private final HashMap<String, WorldLocation> playerClicks = new HashMap();
	private final HashMap<String, Boolean> isDrawingBox = new HashMap();
	public final HashMap<String, ScaledDirection> shifts = new HashMap();
	private final MultiMap<Ability, ProgressStage> progressMap = new MultiMap();

	private final HashMap<Ability, ElementTagCompound> tagMap = new HashMap();

	public static final AbilityHelper instance = new AbilityHelper();

	private AbilityHelper() {
		List<Ability> li = Chromabilities.getAbilities();
		for (Ability c : li) {
			ElementTagCompound tag = AbilityRituals.instance.getAura(c);
			tagMap.put(c, tag);
		}

		progressMap.addValue(Chromabilities.FIREBALL, ProgressStage.NETHER);
		progressMap.addValue(Chromabilities.PYLON, ProgressStage.SHOCK);
	}

	public void startDrawingBoxes(EntityPlayer ep) {
		isDrawingBox.put(ep.getCommandSenderName(), true);
	}

	public void stopDrawingBoxes(EntityPlayer ep) {
		isDrawingBox.put(ep.getCommandSenderName(), false);
		this.removePlayerClick(ep);
	}

	public void addPlayerClick(EntityPlayer ep, World world, int x, int y, int z) {
		if (!world.isRemote) {
			String s = ep.getCommandSenderName();
			if (isDrawingBox.containsKey(s) && isDrawingBox.get(s)) {
				WorldLocation loc = new WorldLocation(world, x, y, z);
				if (playerClicks.containsKey(s)) {
					WorldLocation loc2 = playerClicks.get(s);
					BlockBox b = new BlockBox(loc, loc2);
					this.playerMakeBox(ep, b);
					this.removePlayerClick(ep);
				}
				else
					playerClicks.put(s, loc);
			}
		}
	}

	public void removePlayerClick(EntityPlayer ep) {
		playerClicks.remove(ep.getCommandSenderName());
	}

	private void playerMakeBox(EntityPlayer ep, BlockBox box) {
		if (!ep.worldObj.isRemote && Chromabilities.SHIFT.enabledOn(ep)) {
			ScaledDirection dir = shifts.get(ep.getCommandSenderName());
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
		if (e.buttonstate && e.button == 2 && !ChromaOptions.KEYBINDABILITY.getState()) {
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
				Chromabilities.triggerAbility(ep, Chromabilities.REACH, 0);
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

	public ElementTagCompound getElementsFor(Ability a) {
		return tagMap.get(a).copy();
	}

	public ElementTagCompound getUsageElementsFor(Ability c) {
		return tagMap.get(c).copy().scale(0.0002F);
	}

	public boolean playerCanGetAbility(Chromabilities c, EntityPlayer ep) {
		Collection<ProgressStage> li = progressMap.get(c);
		if (li == null)
			return true;
		for (ProgressStage p : li) {
			if (!p.isPlayerAtStage(ep))
				return false;
		}
		return true;
	}

	public Collection<ProgressStage> getProgressFor(Chromabilities c) {
		return Collections.unmodifiableCollection(progressMap.get(c));
	}

}
