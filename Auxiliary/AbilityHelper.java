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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.AbilityRituals;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler.PlayerTracker;
import Reika.DragonAPI.Instantiable.BasicInventory;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.ScaledDirection;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Instantiable.Event.RawKeyPressEvent;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public class AbilityHelper {

	//Client Side
	public int playerReach = -1;

	private final PlayerMap<WorldLocation> playerClicks = new PlayerMap();
	private final PlayerMap<Boolean> isDrawingBox = new PlayerMap();
	public final PlayerMap<ScaledDirection> shifts = new PlayerMap();

	private final PlayerMap<LossCache> lossCache = new PlayerMap();

	private final PlayerMap<Integer> healthCache = new PlayerMap();

	private final PlayerMap<InventoryArray> inventories = new PlayerMap();

	private final MultiMap<Ability, ProgressStage> progressMap = new MultiMap();

	private final HashMap<Ability, ElementTagCompound> tagMap = new HashMap();

	public static final AbilityHelper instance = new AbilityHelper();

	private static final Random rand = new Random();

	private AbilityHelper() {
		List<Ability> li = Chromabilities.getAbilities();
		for (Ability c : li) {
			ElementTagCompound tag = AbilityRituals.instance.getAura(c);
			tagMap.put(c, tag);
		}

		progressMap.addValue(Chromabilities.FIREBALL, ProgressStage.NETHER);
		progressMap.addValue(Chromabilities.PYLON, ProgressStage.SHOCK);
		progressMap.addValue(Chromabilities.DEATHPROOF, ProgressStage.DIE);
	}

	public void boostHealth(EntityPlayer ep, int attr) {
		healthCache.put(ep, attr);
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
			Chromabilities.shiftArea((WorldServer)ep.worldObj, box, dir.direction, dir.distance, (EntityPlayerMP)ep);
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

	@SubscribeEvent
	public void saveSomeEnergy(LivingDeathEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)evt.entityLiving;
			if (Chromabilities.DEATHPROOF.enabledOn(ep)) {
				lossCache.put(ep, new LossCache(ep));
			}
		}
	}

	@SubscribeEvent
	public void useCache(PlayerEvent.PlayerRespawnEvent evt) {
		LossCache c = lossCache.remove(evt.player);
		if (c != null) {
			c.applyToPlayer(evt.player);
		}
	}

	private static class LossCache {

		private final Collection<Ability> savedAbilities = new ArrayList();
		private final EnumMap<CrystalElement, Integer> savedEnergy = new EnumMap(CrystalElement.class);

		private static final double INIT_CHANCE = 10;
		private static final double INC_CHANCE = 5;
		private static final double MAX_CHANCE = 80;

		private static final String NBT_TAG = "bufferDeathChance";

		private LossCache(EntityPlayer ep) {
			NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
			double chance = nbt.getDouble(NBT_TAG);
			if (!nbt.hasKey(NBT_TAG)) {
				chance = INIT_CHANCE;
			}
			else {
				chance += INC_CHANCE;
				if (chance > MAX_CHANCE)
					chance = MAX_CHANCE;
			}
			nbt.setDouble(NBT_TAG, chance);
			for (Ability a : Chromabilities.getAvailableFrom(ep)) {
				if (ReikaRandomHelper.doWithChance(chance)) {
					savedAbilities.add(a);
				}
			}
			for (int i = 0; i < CrystalElement.elements.length; i++) {
				CrystalElement e = CrystalElement.elements[i];
				int max = PlayerElementBuffer.instance.getPlayerContent(ep, e);
				double ratio = 1D/(2+rand.nextInt(3));
				double nrat = 1D-ratio;
				int random = (int)(max*nrat);
				int save = (int)(max*ratio)+rand.nextInt(random);
				save *= chance/100D;
				savedEnergy.put(e, save);
			}
		}

		private void applyToPlayer(EntityPlayer player) {
			for (CrystalElement e : savedEnergy.keySet()) {
				PlayerElementBuffer.instance.addToPlayer(player, e, savedEnergy.get(e));
			}
			for (Ability a : savedAbilities) {
				Chromabilities.give(player, a);
			}
		}

	}

	public static class LoginApplier implements PlayerTracker {

		public static final LoginApplier instance = new LoginApplier();

		private LoginApplier() {

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
			if (!player.worldObj.isRemote && Chromabilities.HEALTH.enabledOn(player)) {
				Integer get = AbilityHelper.instance.healthCache.get(player);
				int health = get != null ? get.intValue() : 0;
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.HEALTHSYNC.ordinal(), (EntityPlayerMP)player, health);
			}
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

	@SubscribeEvent
	public void scrollInventories(RawKeyPressEvent evt) {
		if (evt.key == Key.PGDN || evt.key == Key.PGUP) {
			boolean up = evt.key == Key.PGUP;
			this.cycleInventory(evt.player, up);
			ReikaJavaLibrary.pConsole(inventories.get(evt.player));
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.INVCYCLE.ordinal(), (EntityPlayerMP)evt.player, up ? 1 : 0);
		}
	}

	@SideOnly(Side.CLIENT)
	public void cycleInventoryClient(EntityPlayer ep, boolean up) {
		this.cycleInventory(ep, up);
	}

	private void cycleInventory(EntityPlayer ep, boolean up) {
		InventoryArray inv = inventories.get(ep);
		if (inv != null) {
			inv.shift(ep, up);
			InventoryArrayData.initArrayData(ep.worldObj).setDirty(true);
		}
	}

	public void addInventoryPage(EntityPlayer ep) {
		InventoryArray inv = inventories.get(ep);
		if (inv == null) {
			inv = new InventoryArray();
			inventories.put(ep, inv);
		}
		inv.addPage();
		InventoryArrayData.initArrayData(ep.worldObj).setDirty(true);
	}

	public void editSavedInventory(EntityPlayer ep, IInventory data, int page) {
		InventoryArrayData.initArrayData(ep.worldObj).setDirty(true);
	}

	public static class InventoryArrayData extends WorldSavedData {

		public static final String TAG = "CHROMAINVARRAY";

		public InventoryArrayData() {
			super(TAG);
		}

		public InventoryArrayData(String s) {
			super(s);
		}

		private static InventoryArrayData initArrayData(World world) {
			InventoryArrayData data = (InventoryArrayData)world.loadItemData(InventoryArrayData.class, TAG);
			if (data == null) {
				data = new InventoryArrayData();
				world.setItemData(TAG, data);
			}
			return data;
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			for (String s : instance.inventories.keySet()) {
				NBTTagList tag = nbt.getTagList(s, NBTTypes.COMPOUND.ID);
				instance.inventories.get(s).readFromNBT(tag);
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound nbt) {
			for (String s : instance.inventories.keySet()) {
				NBTTagList tag = new NBTTagList();
				instance.inventories.get(s).writeToNBT(tag);
				nbt.setTag(s, tag);
			}
		}

	}

	private static class InventoryArray {

		private LinkedList<Inventory> inventories = new LinkedList();

		private InventoryArray() {

		}

		public void addPage() {
			inventories.addLast(new Inventory());
		}

		private void shift(EntityPlayer ep, boolean up) {
			if (inventories.isEmpty())
				return;

			Inventory inv = new Inventory();
			inv.populate(ep);

			if (up) {
				Inventory i = inventories.getFirst();
				inventories.removeFirst();
				i.load(ep);

				inventories.addLast(inv);
			}
			else {
				Inventory i = inventories.getLast();
				inventories.removeLast();
				i.load(ep);

				inventories.addFirst(inv);
			}
		}

		private void writeToNBT(NBTTagList NBT) {
			for (Inventory i : inventories) {
				NBTTagCompound tag = new NBTTagCompound();
				i.writeToNBT(tag);
				NBT.appendTag(tag);
			}
		}

		private void readFromNBT(NBTTagList NBT) {
			for (Object tag : NBT.tagList) {
				Inventory i = new Inventory();
				i.readFromNBT((NBTTagCompound)tag);
				inventories.addLast(i);
			}
		}

		@Override
		public String toString() {
			return inventories.size()+": "+inventories.toString();
		}

		//private void addPage() {
		//	inventories.addLast(new Inventory(inventories.size()));
		//}

	}

	private static class Inventory extends BasicInventory {

		private static final int SIZE = 36;

		public Inventory() {
			super("Ability Page", SIZE);
		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			return true;
		}

		private void writeToNBT(NBTTagCompound NBT) {
			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 0; i < inv.length; i++) {
				if (inv[i] != null) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setByte("Slot", (byte)i);
					inv[i].writeToNBT(nbttagcompound);
					nbttaglist.appendTag(nbttagcompound);
				}
			}

			NBT.setTag("Items", nbttaglist);
		}

		private void readFromNBT(NBTTagCompound NBT) {
			NBTTagList nbttaglist = NBT.getTagList("Items", NBTTypes.COMPOUND.ID);
			inv = new ItemStack[SIZE];

			for (int i = 0; i < nbttaglist.tagCount(); i++)
			{
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				byte byte0 = nbttagcompound.getByte("Slot");

				if (byte0 >= 0 && byte0 < inv.length) {
					inv[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
				}
			}
		}

		private void load(EntityPlayer ep) {
			for (int i = 0; i < SIZE; i++) {
				ep.inventory.mainInventory[i] = inv[i];
			}
		}

		private void populate(EntityPlayer ep) {
			for (int i = 0; i < SIZE; i++) {
				inv[i] = ep.inventory.mainInventory[i];
			}
		}

		@Override
		public String toString() {
			return Arrays.toString(inv);
		}

	}

	public ElementTagCompound getElementsFor(Ability a) {
		return tagMap.get(a).copy();
	}

	public ElementTagCompound getUsageElementsFor(Ability c) {
		return tagMap.get(c).copy().scale(0.0008F); //was 0.0002F
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
