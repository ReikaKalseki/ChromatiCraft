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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.AbilityRituals;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Items.Tools.ItemEfficiencyCrystal;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler.PlayerTracker;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.BasicInventory;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.ScaledDirection;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Instantiable.Event.PostItemUseEvent;
import Reika.DragonAPI.Instantiable.Event.RawKeyPressEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.SourceType;
import Reika.DragonAPI.ModInteract.ItemHandlers.ChiselBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.RailcraftHandler;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import cpw.mods.fml.common.FMLCommonHandler;
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

	private final PlayerMap<HashMap<String, WarpPoint>> teleports = new PlayerMap();

	private final MultiMap<Ability, ProgressStage> progressMap = new MultiMap(new HashSetFactory());

	private final HashMap<Ability, ElementTagCompound> tagMap = new HashMap();

	private final HashMap<Class, AbilityXRays> xRayMap = new HashMap();

	private final PlayerMap<PlayerPath> playerPaths = new PlayerMap();

	private final HashSet<UUID> noClippingMagnet = new HashSet();

	private final PlayerMap<Long> dashTime = new PlayerMap();

	private final HashSet<UUID> doubleJumps = new HashSet();

	public static final AbilityHelper instance = new AbilityHelper();

	//private int savedAOSetting;

	public boolean isNoClipEnabled;

	private static final Random rand = new Random();

	private final PlayerMap<ItemStack> refillItem = new PlayerMap();
	private static final String AE_LOC_TAG = "AELoc";

	private AbilityHelper() {
		List<Ability> li = Chromabilities.getAbilities();
		for (Ability c : li) {
			ElementTagCompound tag = AbilityRituals.instance.getAura(c);
			tagMap.put(c, tag);
		}

		progressMap.addValue(Chromabilities.FIREBALL, ProgressStage.NETHER);
		progressMap.addValue(Chromabilities.PYLON, ProgressStage.SHOCK);
		progressMap.addValue(Chromabilities.DEATHPROOF, ProgressStage.DIE);
		progressMap.addValue(Chromabilities.TELEPORT, ProgressStage.CTM);
		progressMap.addValue(Chromabilities.SPAWNERSEE, ProgressStage.CTM);
		progressMap.addValue(Chromabilities.SPAWNERSEE, ProgressStage.BREAKSPAWNER);
		progressMap.addValue(Chromabilities.DIMPING, ProgressStage.DIMENSION);
		progressMap.addValue(Chromabilities.COMMUNICATE, ProgressStage.KILLMOB);
		progressMap.addValue(Chromabilities.RANGEDBOOST, ProgressStage.KILLMOB);
		progressMap.addValue(Chromabilities.LEECH, ProgressStage.KILLMOB);
		progressMap.addValue(Chromabilities.LASER, ProgressStage.TURBOCHARGE);
		progressMap.addValue(Chromabilities.FIRERAIN, ProgressStage.NETHER);
		progressMap.addValue(Chromabilities.FIRERAIN, ProgressStage.CTM);
		progressMap.addValue(Chromabilities.KEEPINV, ProgressStage.DIMENSION);
		progressMap.addValue(Chromabilities.ORECLIP, ProgressStage.CTM);
		progressMap.addValue(Chromabilities.DOUBLECRAFT, ProgressStage.CTM);
		progressMap.addValue(Chromabilities.RECHARGE, ProgressStage.CTM);
		progressMap.addValue(Chromabilities.GROWAURA, ProgressStage.CTM);
		progressMap.addValue(Chromabilities.MEINV, ProgressStage.DIMENSION);
		progressMap.addValue(Chromabilities.MOBSEEK, ProgressStage.CTM);

		for (AbilityXRays x : AbilityXRays.values()) {
			xRayMap.put(x.objectClass, x);
		}
	}

	@SideOnly(Side.CLIENT)
	public AbilityXRays getAbilityXRay(Object te) {
		return xRayMap.get(te.getClass());
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
				LossCache lc = new LossCache(ep);
				lossCache.put(ep, lc);
			}
			else {
				NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
				nbt.removeTag(LossCache.NBT_TAG);
			}
		}
	}

	@SubscribeEvent
	public void useCache(PlayerEvent.PlayerRespawnEvent evt) {
		LossCache c = lossCache.remove(evt.player);
		if (c != null) {
			c.applyToPlayer(evt.player);
			Chromabilities.DEATHPROOF.setToPlayer(evt.player, true);
		}
	}

	private static class LossCache {

		private final Collection<Ability> savedAbilities = new ArrayList();
		private final ElementTagCompound savedEnergy = new ElementTagCompound();
		private int cap;

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
				if (a == Chromabilities.DEATHPROOF || ReikaRandomHelper.doWithChance(chance)) {
					savedAbilities.add(a);
				}
			}
			for (int i = 0; i < CrystalElement.elements.length; i++) {
				CrystalElement e = CrystalElement.elements[i];
				int max = PlayerElementBuffer.instance.getPlayerContent(ep, e);
				if (max > 0) {
					double ratio = 1D/(2+rand.nextInt(3));
					double nrat = 1D-ratio;
					int random = (int)(max*nrat);
					int save = (int)(max*ratio)+rand.nextInt(random);
					save *= chance/100D;
					savedEnergy.addTag(e, save);
				}
			}

			//int pcap = PlayerElementBuffer.instance.getElementCap(ep);
			//cap = Math.max(savedEnergy.getMaximumValue(), Math.max(24, ReikaRandomHelper.doWithChance(chance) ? pcap : pcap/4));
			cap = 24;
			int max = savedEnergy.getMaximumValue();
			while (cap < max)
				cap *= 4;
		}

		private void applyToPlayer(EntityPlayer player) {
			PlayerElementBuffer.instance.setElementCap(player, cap, false);
			for (CrystalElement e : savedEnergy.elementSet()) {
				PlayerElementBuffer.instance.addToPlayer(player, e, savedEnergy.getValue(e));
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
			WarpPointData.initWarpData(ep.worldObj).setDirty(true);
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
		if (!evt.player.worldObj.isRemote && Chromabilities.HOTBAR.enabledOn(evt.player)) {
			if (evt.key == Key.PGDN || evt.key == Key.PGUP) {
				boolean up = evt.key == Key.PGUP;
				this.cycleInventory(evt.player, up);
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.INVCYCLE.ordinal(), (EntityPlayerMP)evt.player, up ? 1 : 0);
			}
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
			for (UUID s : instance.inventories.keySet()) {
				NBTTagList tag = nbt.getTagList(s.toString(), NBTTypes.COMPOUND.ID);
				instance.inventories.get(s.toString()).readFromNBT(tag);
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound nbt) {
			for (UUID s : instance.inventories.keySet()) {
				NBTTagList tag = new NBTTagList();
				instance.inventories.get(s.toString()).writeToNBT(tag);
				nbt.setTag(s.toString(), tag);
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

	public Collection<WarpPoint> getTeleportLocations(EntityPlayer ep) {
		HashMap<String, WarpPoint> c = teleports.get(ep);
		return c != null ? Collections.unmodifiableCollection(c.values()) : new HashSet();
	}

	public void addWarpPoint(String s, EntityPlayer ep) {
		HashMap<String, WarpPoint> c = teleports.get(ep);
		if (c == null) {
			c = new HashMap();
			teleports.put(ep, c);
		}
		c.put(s, new WarpPoint(s, ep));
		WarpPointData.initWarpData(ep.worldObj).setDirty(true);
	}

	public void gotoWarpPoint(String s, EntityPlayer ep) {
		HashMap<String, WarpPoint> c = teleports.get(ep);
		if (c != null) {
			WarpPoint wp = c.get(s);
			if (wp != null && wp.canTeleportPlayer(ep)) {
				wp.teleportPlayerTo(ep);
			}
		}
	}

	public void removeWarpPoint(String s, EntityPlayer ep) {
		HashMap<String, WarpPoint> c = teleports.get(ep);
		if (c != null) {
			c.remove(s);
			if (c.isEmpty()) {

			}
		}
		WarpPointData.initWarpData(ep.worldObj).setDirty(true);
	}

	public boolean playerCanWarpTo(EntityPlayer ep, WorldLocation loc) {
		HashMap<String, WarpPoint> c = teleports.get(ep);
		return c != null && c.values().contains(new WarpPoint("", ep));
	}

	public static class WarpPoint {

		public final String label;
		public final WorldLocation location;

		private WarpPoint(String s, EntityPlayer ep) {
			this(s, new WorldLocation(ep));
		}

		private WarpPoint(String s, World world, int x, int y, int z) {
			this(s, new WorldLocation(world, x, y, z));
		}

		private WarpPoint(String s, WorldLocation loc) {
			label = s;
			location = loc;
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof WarpPoint && ((WarpPoint)o).location.equals(location);
		}

		@Override
		public int hashCode() {
			return ~location.hashCode();
		}

		@Override
		public String toString() {
			return label+" ("+location.toString()+")";
		}

		private void teleportPlayerTo(EntityPlayer ep) {
			ReikaEntityHelper.transferEntityToDimension(ep, location.dimensionID);
			ep.setPositionAndUpdate(location.xCoord+0.5, location.yCoord+0.25, location.zCoord+0.5);
			ep.playSound("mob.endermen.portal", 1, 1);
		}

		public boolean canTeleportPlayer(EntityPlayer ep) {
			int dim = ep.worldObj.provider.dimensionId;
			if (location.dimensionID == ExtraChromaIDs.DIMID.getValue() || dim == ExtraChromaIDs.DIMID.getValue())
				return dim == location.dimensionID;
			return true;
		}

	}

	public static class WarpPointData extends WorldSavedData {

		public static final String TAG = "CHROMAWARPPOINT";

		public WarpPointData() {
			super(TAG);
		}

		public WarpPointData(String s) {
			super(s);
		}

		private static WarpPointData initWarpData(World world) {
			WarpPointData data = (WarpPointData)world.loadItemData(WarpPointData.class, TAG);
			if (data == null) {
				data = new WarpPointData();
				world.setItemData(TAG, data);
			}
			return data;
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			instance.teleports.clear();
			NBTTagCompound data = nbt.getCompoundTag("warpdata");
			for (Object o : data.func_150296_c()) {
				String player = (String)o;
				NBTTagList points = data.getTagList(player, NBTTypes.COMPOUND.ID);
				HashMap<String, WarpPoint> map = new HashMap();
				instance.teleports.directPut(UUID.fromString(player), map);
				for (Object o2 : points.tagList) {
					NBTTagCompound pt = (NBTTagCompound)o2;
					String label = pt.getString("label");
					WorldLocation pos = WorldLocation.readFromNBT("pos", pt);
					if (pos != null) {
						map.put(label, new WarpPoint(label, pos));
					}
				}
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound nbt) {
			NBTTagCompound data = new NBTTagCompound();
			for (UUID uid : instance.teleports.keySet()) {
				NBTTagList points = new NBTTagList();
				HashMap<String, WarpPoint> map = instance.teleports.directGet(uid);
				for (String label : map.keySet()) {
					WarpPoint wp = map.get(label);
					NBTTagCompound pt = new NBTTagCompound();
					pt.setString("label", label);
					wp.location.writeToNBT("pos", pt);
					points.appendTag(pt);
				}
				data.setTag(uid.toString(), points);
			}
			nbt.setTag("warpdata", data);
		}

	}

	public ElementTagCompound getElementsFor(Ability a) {
		return tagMap.get(a).copy();
	}

	public ElementTagCompound getUsageElementsFor(Ability c, EntityPlayer ep) {
		ElementTagCompound ret = tagMap.get(c).copy().scale(0.0008F); //was 0.0008F //was 0.0002F
		if (c == Chromabilities.FIRERAIN)
			ret.scale(12.5F);
		else if (c == Chromabilities.ORECLIP)
			ret.scale(20);
		else if (c == Chromabilities.DOUBLECRAFT)
			ret.scale(7.5F);
		else if (this.getProgressFor(c).contains(ProgressStage.CTM))
			ret.scale(5);
		else if (this.getProgressFor(c).contains(ProgressStage.DIMENSION))
			ret.scale(2.5F);
		if (ItemEfficiencyCrystal.isActive(ep))
			ret.power(0.75);
		return ret;
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

	public Collection<ProgressStage> getProgressFor(Ability c) {
		return Collections.unmodifiableCollection(progressMap.get(c));
	}

	public static enum AbilityXRays {
		SPAWNERS(TileEntityMobSpawner.class, Blocks.mob_spawner, 0x224466),
		CHESTS(TileEntityChest.class, 0xC17C32),
		LOOTCHESTS(TileEntityLootChest.class, 0x303030),
		CHESTCARTS(EntityMinecartChest.class, 0xC17C32);

		public final Class objectClass;
		private final Block texture;
		public final int highlightColor;

		private AbilityXRays(Class t, int c) {
			this(t, null, c);
		}

		public IIcon getTexture() {
			return texture != null ? texture.blockIcon : null;
		}

		private AbilityXRays(Class t, Block tex, int c) {
			texture = tex;
			objectClass = t;
			highlightColor = c;
		}
	}

	@SubscribeEvent
	public void addPoint(LivingUpdateEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)evt.entityLiving;
			if (Chromabilities.BREADCRUMB.enabledOn(ep) && ep.worldObj.isRemote && ep.ticksExisted%2 == 0) {
				PlayerPath path = playerPaths.get(ep);
				if (path == null) {
					path = new PlayerPath();
					playerPaths.put(ep, path);
				}
				path.addPoint(ep);
			}
		}
	}

	public void setPathLength(EntityPlayer ep, int len) {
		PlayerPath path = playerPaths.get(ep);
		if (path == null) {
			path = new PlayerPath();
			playerPaths.put(ep, path);
		}
		path.setLength(len);
	}

	public void renderPath(EntityPlayer ep) {
		PlayerPath path = playerPaths.get(ep);
		if (path != null) {
			path.render();
		}
	}

	private static class PlayerPath {

		private int maxLength = 32;
		private boolean renderLock = false;
		private LinkedList<DecimalPosition> points = new LinkedList();

		private void addPoint(EntityPlayer ep) {
			this.addPoint(ep.posX, ep.posY-1.62, ep.posZ);
		}

		private void addPoint(double x, double y, double z) {
			if (maxLength > 0 && !renderLock) {
				DecimalPosition dec = new DecimalPosition(x, y, z);
				points.addLast(dec);
				if (points.size() > maxLength + 8) {
					points.removeFirst();
				}
			}
		}

		public void setLength(int len) {
			if (len == 0)
				points.clear();
			else if (len < points.size())
				points = new LinkedList(points.subList(points.size()-len, points.size()));
			maxLength = len;
		}

		@SideOnly(Side.CLIENT)
		public void render() {
			if (maxLength > 0) {
				renderLock = true;
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glPushMatrix();
				EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
				//GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_CULL_FACE);
				BlendMode.ADDITIVEDARK.apply();
				ReikaRenderHelper.disableEntityLighting();
				ReikaTextureHelper.bindTerrainTexture();
				GL11.glDisable(GL11.GL_LIGHTING);
				//Tessellator.instance.startDrawing(GL11.GL_LINE_STRIP);
				float f = 0;
				int i = 0;
				for (DecimalPosition d : points) {
					if (i < points.size()-8) {
						int c = f >= 0.5F ? ReikaColorAPI.mixColors(0xffffff, 0x00ffff, (f-0.5F)*2) : ReikaColorAPI.mixColors(0x00ffff, 0x0000ff, f*2);
						c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.5F);
						double s = 0.25;
						/*
						//Tessellator.instance.addVertex(d.xCoord, d.yCoord, d.zCoord);
						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord-s);
						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord-s);
						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord-s);
						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord-s);

						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord+s);
						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord+s);
						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord+s);
						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord+s);

						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord-s);
						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord+s);
						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord+s);
						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord-s);

						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord-s);
						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord+s);
						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord+s);
						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord-s);

						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord-s);
						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord-s);
						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord+s);
						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord+s);

						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord-s);
						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord-s);
						Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord+s);
						Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord+s);
						 */

						IIcon ico = ChromaIcons.FADE.getIcon();

						float u = ico.getMinU();
						float v = ico.getMinV();
						float du = ico.getMaxU();
						float dv = ico.getMaxV();

						GL11.glPushMatrix();

						GL11.glTranslated(d.xCoord-RenderManager.renderPosX, d.yCoord-RenderManager.renderPosY, d.zCoord-RenderManager.renderPosZ);
						//GL11.glTranslated(-271-RenderManager.renderPosX, 8-RenderManager.renderPosY, 157-RenderManager.renderPosZ);

						RenderManager rm = RenderManager.instance;
						GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
						GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
						Tessellator.instance.startDrawingQuads();

						Tessellator.instance.setColorOpaque_I(c);

						Tessellator.instance.addVertexWithUV(-s, -s, 0, u, v);
						Tessellator.instance.addVertexWithUV(s, -s, 0, du, v);
						Tessellator.instance.addVertexWithUV(s, s, 0, du, dv);
						Tessellator.instance.addVertexWithUV(-s, s, 0, u, dv);

						Tessellator.instance.draw();

						GL11.glPopMatrix();

						/*
						Tessellator.instance.addVertexWithUV(d.xCoord-s, d.yCoord, d.zCoord-s, u, v);
						Tessellator.instance.addVertexWithUV(d.xCoord+s, d.yCoord, d.zCoord-s, du, v);
						Tessellator.instance.addVertexWithUV(d.xCoord+s, d.yCoord, d.zCoord+s, du, dv);
						Tessellator.instance.addVertexWithUV(d.xCoord-s, d.yCoord, d.zCoord+s, u, dv);

						Tessellator.instance.addVertexWithUV(d.xCoord, d.yCoord-s, d.zCoord-s, u, v);
						Tessellator.instance.addVertexWithUV(d.xCoord, d.yCoord-s, d.zCoord+s, du, v);
						Tessellator.instance.addVertexWithUV(d.xCoord, d.yCoord+s, d.zCoord+s, du, dv);
						Tessellator.instance.addVertexWithUV(d.xCoord, d.yCoord+s, d.zCoord-s, u, dv);
						 */
					}
					f += 1F/(points.size()-8);
					i++;
				}
				ReikaRenderHelper.enableEntityLighting();
				BlendMode.DEFAULT.apply();
				GL11.glPopMatrix();
				GL11.glPopAttrib();
				renderLock = false;
			}
		}

	}

	public boolean isMagnetNoClip(EntityPlayer ep) {
		return noClippingMagnet.contains(ep.getUniqueID());
	}

	public void setNoClippingMagnet(EntityPlayer ep, boolean set) {
		if (set)
			noClippingMagnet.add(ep.getUniqueID());
		else
			noClippingMagnet.remove(ep.getUniqueID());
	}

	public int getPlayerDashCooldown(EntityPlayer ep) {
		Long ret = dashTime.get(ep);
		return ret == null ? 0 : Math.max(0, (int)(ret.longValue()+30-ep.worldObj.getTotalWorldTime()));
	}

	public void doLumenDash(EntityPlayer ep) {
		dashTime.put(ep, ep.worldObj.getTotalWorldTime());
		TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new ResetWalkSpeedEvent(ep)), 10);
		//ReikaPlayerAPI.setPlayerWalkSpeed(ep, 5);
		ep.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 8, 60, true));
		ep.addPotionEffect(new PotionEffect(Potion.jump.id, 8, 2, true));
		ep.stepHeight = 2.75F;
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.DASH.ordinal(), new PacketTarget.RadiusTarget(ep, 24), ep.getEntityId());
	}

	@SubscribeEvent
	public void saveUsedStack(PlayerInteractEvent evt) {
		ItemStack is = evt.entityPlayer.getCurrentEquippedItem();
		if (Chromabilities.MEINV.enabledOn(evt.entityPlayer) && is != null && is.stackSize == is.getMaxStackSize()) {
			refillItem.put(evt.entityPlayer, is.copy());
		}
		else {
			refillItem.remove(evt.entityPlayer);
		}
	}

	@SubscribeEvent
	public void refillUsedStack(PostItemUseEvent evt) {
		this.refillUsedStack(evt.entityPlayer, evt.getItem());
	}

	private void refillUsedStack(EntityPlayer ep, ItemStack is) {
		ItemStack was = refillItem.remove(ep);
		if (was != null) {
			int slot =  ep.inventory.currentItem;
			ItemStack now = is;
			if (ReikaItemHelper.matchStacks(was, now) && now.stackSize < now.getMaxStackSize()) {
				int amt = now.getMaxStackSize()-now.stackSize;
				int ret = this.requestItem(ep, was, amt);
				if (ret > 0) {
					now.stackSize += ret;
					ep.inventory.mainInventory[slot] = now.copy();
				}
			}
		}
	}

	@ModDependent(ModList.APPENG)
	private int requestItem(EntityPlayer ep, ItemStack is, int amt) {
		MESystemReader me = this.getMESystem(ep);
		return me != null ? (int)me.removeItem(ReikaItemHelper.getSizedItemStack(is, amt), false) : 0;
	}

	private MESystemReader getMESystem(EntityPlayer ep) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep).getCompoundTag(AE_LOC_TAG);
		if (nbt == null || nbt.hasNoTags())
			return null;
		Coordinate c = Coordinate.readTag(nbt);
		if (c == null) {
			return null;
		}
		TileEntity te = c.getTileEntity(ep.worldObj);
		if (te instanceof IGridHost) {
			IGridHost ih = (IGridHost)te;
			IGridNode n = ih.getGridNode(ForgeDirection.VALID_DIRECTIONS[nbt.getInteger("dir")]);
			MESystemReader me = new MESystemReader(n, SourceType.MACHINE);
			return me;
		}
		return null;
	}

	public void saveMESystemLocation(EntityPlayer ep, TileEntity te, int s) {
		NBTTagCompound NBT = new NBTTagCompound();
		Coordinate c = new Coordinate(te);
		c.writeToTag(NBT);
		NBT.setInteger("dir", s);
		ReikaPlayerAPI.getDeathPersistentNBT(ep).setTag(AE_LOC_TAG, NBT);
	}

	/*ME systems cannot be read clientside
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void useMEItemCount(ItemSizeTextEvent evt) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (Chromabilities.MEINV.enabledOn(ep)) {
			ItemStack is = evt.getItem();
			if (is.stackSize == 64 && is != ep.inventory.getItemStack()) {
				MESystemReader me = this.getMESystem(ep);
				if (me != null) {
					long amt = me.getItemCount(is);
					if (amt < 1000) {
						evt.newString = String.valueOf(amt);
					}
					else {
						double base = ReikaMathLibrary.getThousandBase(amt);
						int n = 2-(int)Math.log10(base);
						evt.newString = String.format("%."+n+"f%s", base, ReikaEngLibrary.getSIPrefix(amt));
					}
				}
			}
		}
	}
	 */

	private static class ResetWalkSpeedEvent implements ScheduledEvent {

		private final EntityPlayer player;
		//private final float walkSpeed;
		private final float prevHeight;

		private ResetWalkSpeedEvent(EntityPlayer ep) {
			player = ep;
			//walkSpeed = ep.capabilities.getWalkSpeed();
			prevHeight = ep.stepHeight;
		}

		@Override
		public void fire() {
			//ReikaPlayerAPI.setPlayerWalkSpeed(player, walkSpeed);
			player.stepHeight = prevHeight;
		}

	}

	public boolean tryAndDoDoubleJump(EntityPlayer ep) {
		UUID id = ep.getUniqueID();
		if (doubleJumps.contains(id))
			return false;
		doubleJumps.add(id);
		return true;
	}

	public boolean isDoubleJumping(EntityPlayer ep) {
		return doubleJumps.contains(ep.getUniqueID());
	}

	public void resetDoubleJump(EntityPlayer ep) {
		doubleJumps.remove(ep.getUniqueID());
	}

	public void onNoClipDisable(EntityPlayer ep) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			this.clientNoClipDisable();
	}

	public void onNoClipEnable(EntityPlayer ep) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			this.clientNoClipEnable();
	}

	@SideOnly(Side.CLIENT)
	private void clientNoClipDisable() {
		//Minecraft.getMinecraft().gameSettings.ambientOcclusion = savedAOSetting;
		//Minecraft.getMinecraft().thePlayer.removePotionEffect(Potion.nightVision.id);
		ReikaRenderHelper.rerenderAllChunks();
	}

	@SideOnly(Side.CLIENT)
	private void clientNoClipEnable() {
		//savedAOSetting = Minecraft.getMinecraft().gameSettings.ambientOcclusion;
		//Minecraft.getMinecraft().gameSettings.ambientOcclusion = 0;
		ReikaRenderHelper.rerenderAllChunks();
	}

	@SubscribeEvent
	public void resetNoclip(LivingDeathEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)evt.entityLiving;
			if (Chromabilities.ORECLIP.enabledOn(ep)) {
				Chromabilities.ORECLIP.setToPlayer(ep, false);
				isNoClipEnabled = false;
				this.onNoClipDisable(ep);
			}
		}
	}

	public boolean isBlockOreclippable(World world, int x, int y, int z, Block b, int meta) {
		if (b == Blocks.stone || b == Blocks.grass || b == Blocks.dirt || b == Blocks.sandstone || b == Blocks.sand || b == Blocks.gravel)
			return false;
		if (b == Blocks.netherrack || b == Blocks.end_stone)
			return false;
		if (b == Blocks.bedrock)
			return y == 0;
		if (ReikaBlockHelper.isLeaf(b, meta) || ReikaBlockHelper.isWood(b, meta))
			return false;
		if (RailcraftHandler.Blocks.QUARRIED.match(b, meta) || RailcraftHandler.Blocks.ABYSSAL.match(b, meta))
			return false;
		if (ChiselBlockHandler.isWorldgenBlock(b, meta))
			return false;
		return !b.isReplaceableOreGen(world, x, y, z, Blocks.stone);
	}

	public ArrayList<AxisAlignedBB> getNoclipBlockBoxes(EntityPlayer ep) {
		ArrayList<AxisAlignedBB> li = new ArrayList();

		AxisAlignedBB box = ep.boundingBox.addCoord(ep.motionX, ep.motionY, ep.motionZ);

		int i = MathHelper.floor_double(box.minX);
		int j = MathHelper.floor_double(box.maxX + 1.0D);
		int k = MathHelper.floor_double(box.minY);
		int l = MathHelper.floor_double(box.maxY + 1.0D);
		int i1 = MathHelper.floor_double(box.minZ);
		int j1 = MathHelper.floor_double(box.maxZ + 1.0D);

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = i1; l1 < j1; ++l1) {
				if (ep.worldObj.blockExists(k1, 64, l1)) {
					for (int i2 = k - 1; i2 < l; ++i2) {
						Block block;

						if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000) {
							block = ep.worldObj.getBlock(k1, i2, l1);
						}
						else {
							block = Blocks.stone;
						}

						if (this.isBlockOreclippable(ep.worldObj, k1, i2, l1, block, ep.worldObj.getBlockMetadata(k1, i2, l1)))
							block.addCollisionBoxesToList(ep.worldObj, k1, i2, l1, box, li, ep);
					}
				}
			}
		}

		double d0 = 0.25D;
		List<Entity> list = ep.worldObj.getEntitiesWithinAABBExcludingEntity(ep, box.expand(d0, d0, d0));

		for (Entity e : list) {
			AxisAlignedBB box2 = e.getBoundingBox();

			if (box2 != null && box2.intersectsWith(box)) {
				li.add(box2);
			}

			box2 = ep.getCollisionBox(e);

			if (box2 != null && box2.intersectsWith(box)) {
				li.add(box2);
			}
		}

		return li;
	}

	public MovingObjectPosition doOreClipRayTrace(World world, Vec3 vec1, Vec3 vec2, boolean liq) {
		if (!Double.isNaN(vec1.xCoord) && !Double.isNaN(vec1.yCoord) && !Double.isNaN(vec1.zCoord)) {
			if (!Double.isNaN(vec2.xCoord) && !Double.isNaN(vec2.yCoord) && !Double.isNaN(vec2.zCoord)) {

				int i = MathHelper.floor_double(vec2.xCoord);
				int j = MathHelper.floor_double(vec2.yCoord);
				int k = MathHelper.floor_double(vec2.zCoord);
				int l = MathHelper.floor_double(vec1.xCoord);
				int i1 = MathHelper.floor_double(vec1.yCoord);
				int j1 = MathHelper.floor_double(vec1.zCoord);

				Block block = world.getBlock(l, i1, j1);
				int k1 = world.getBlockMetadata(l, i1, j1);

				if (block.canCollideCheck(k1, liq) && AbilityHelper.instance.isBlockOreclippable(world, l, i1, j1, block, k1)) {
					MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, l, i1, j1, vec1, vec2);

					if (movingobjectposition != null) {
						return movingobjectposition;
					}
				}

				MovingObjectPosition movingobjectposition2 = null;
				k1 = 200;

				while (k1-- >= 0) {
					if (Double.isNaN(vec1.xCoord) || Double.isNaN(vec1.yCoord) || Double.isNaN(vec1.zCoord)) {
						return null;
					}

					if (l == i && i1 == j && j1 == k) {
						return null;
					}

					boolean flag6 = true;
					boolean flag3 = true;
					boolean flag4 = true;
					double d0 = 999.0D;
					double d1 = 999.0D;
					double d2 = 999.0D;

					if (i > l) {
						d0 = l + 1.0D;
					}
					else if (i < l) {
						d0 = l + 0.0D;
					}
					else {
						flag6 = false;
					}

					if (j > i1) {
						d1 = i1 + 1.0D;
					}
					else if (j < i1) {
						d1 = i1 + 0.0D;
					}
					else {
						flag3 = false;
					}

					if (k > j1) {
						d2 = j1 + 1.0D;
					}
					else if (k < j1) {
						d2 = j1 + 0.0D;
					}
					else {
						flag4 = false;
					}

					double d3 = 999.0D;
					double d4 = 999.0D;
					double d5 = 999.0D;
					double d6 = vec2.xCoord - vec1.xCoord;
					double d7 = vec2.yCoord - vec1.yCoord;
					double d8 = vec2.zCoord - vec1.zCoord;

					if (flag6) {
						d3 = (d0 - vec1.xCoord) / d6;
					}

					if (flag3) {
						d4 = (d1 - vec1.yCoord) / d7;
					}

					if (flag4) {
						d5 = (d2 - vec1.zCoord) / d8;
					}

					boolean flag5 = false;
					byte b0;

					if (d3 < d4 && d3 < d5) {
						if (i > l)
						{
							b0 = 4;
						}
						else
						{
							b0 = 5;
						}

						vec1.xCoord = d0;
						vec1.yCoord += d7 * d3;
						vec1.zCoord += d8 * d3;
					}
					else if (d4 < d5) {
						if (j > i1) {
							b0 = 0;
						}
						else {
							b0 = 1;
						}

						vec1.xCoord += d6 * d4;
						vec1.yCoord = d1;
						vec1.zCoord += d8 * d4;
					}
					else {
						if (k > j1)
						{
							b0 = 2;
						}
						else
						{
							b0 = 3;
						}

						vec1.xCoord += d6 * d5;
						vec1.yCoord += d7 * d5;
						vec1.zCoord = d2;
					}

					Vec3 vec32 = Vec3.createVectorHelper(vec1.xCoord, vec1.yCoord, vec1.zCoord);
					l = (int)(vec32.xCoord = MathHelper.floor_double(vec1.xCoord));

					if (b0 == 5) {
						--l;
						++vec32.xCoord;
					}

					i1 = (int)(vec32.yCoord = MathHelper.floor_double(vec1.yCoord));

					if (b0 == 1) {
						--i1;
						++vec32.yCoord;
					}

					j1 = (int)(vec32.zCoord = MathHelper.floor_double(vec1.zCoord));

					if (b0 == 3) {
						--j1;
						++vec32.zCoord;
					}

					Block block1 = world.getBlock(l, i1, j1);
					int l1 = world.getBlockMetadata(l, i1, j1);

					if (block1.canCollideCheck(l1, liq) && AbilityHelper.instance.isBlockOreclippable(world, l, i1, j1, block1, l1)) {
						MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, l, i1, j1, vec1, vec2);

						if (movingobjectposition1 != null) {
							return movingobjectposition1;
						}
					}
					else {
						movingobjectposition2 = new MovingObjectPosition(l, i1, j1, b0, vec1, false);
					}
				}

				return null;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}

	public boolean canProjectilePenetrateBlocks(Entity e) {
		return ReikaEntityHelper.getShootingEntity(e) instanceof EntityPlayer;
	}

	public boolean canProjectilePenetrateBlock(World world, int x, int y, int z, Block b, Entity e) {
		return this.canProjectilePenetrateBlocks(e) && !this.isBlockSolidToProjectiles(world, x, y, z, b, e);
	}

	private boolean isBlockSolidToProjectiles(World world, int x, int y, int z, Block b, Entity e) {
		if (b == ChromaBlocks.SELECTIVEGLASS.getBlockInstance())
			return false;
		if (ReikaBlockHelper.isLeaf(world, x, y, z) || ReikaBlockHelper.isWood(world, x, y, z))
			return !this.isEntityFiredByMobSeek(e);
		if (b == Blocks.red_mushroom_block || b == Blocks.brown_mushroom_block)
			return !this.isEntityFiredByMobSeek(e);
		if (b == Blocks.waterlily)
			return !this.isEntityFiredByMobSeek(e);
		if (b == Blocks.fence)
			return !this.isEntityFiredByMobSeek(e);
		if (b == Blocks.iron_bars)
			return !this.isEntityFiredByMobSeek(e);
		return true;
	}

	private boolean isEntityFiredByMobSeek(Entity e) {
		Entity s = ReikaEntityHelper.getShootingEntity(e);
		return s instanceof EntityPlayer && Chromabilities.MOBSEEK.enabledOn((EntityPlayer)s);
	}

	public MovingObjectPosition getProjectileRayTrace(Entity e, Vec3 vec1, Vec3 vec2, boolean b1, boolean b2, boolean b3) {
		World world = e.worldObj;
		if (!Double.isNaN(vec1.xCoord) && !Double.isNaN(vec1.yCoord) && !Double.isNaN(vec1.zCoord)) {
			if (!Double.isNaN(vec2.xCoord) && !Double.isNaN(vec2.yCoord) && !Double.isNaN(vec2.zCoord)) {
				int i = MathHelper.floor_double(vec2.xCoord);
				int j = MathHelper.floor_double(vec2.yCoord);
				int k = MathHelper.floor_double(vec2.zCoord);
				int l = MathHelper.floor_double(vec1.xCoord);
				int i1 = MathHelper.floor_double(vec1.yCoord);
				int j1 = MathHelper.floor_double(vec1.zCoord);
				Block block = world.getBlock(l, i1, j1);
				int k1 = world.getBlockMetadata(l, i1, j1);

				if ((!b2 || block.getCollisionBoundingBoxFromPool(world, l, i1, j1) != null) && block.canCollideCheck(k1, b1) && this.isBlockSolidToProjectiles(world, l, i1, j1, block, e)) {
					MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, l, i1, j1, vec1, vec2);

					if (movingobjectposition != null) {
						return movingobjectposition;
					}
				}

				MovingObjectPosition movingobjectposition2 = null;
				k1 = 200;

				while (k1-- >= 0)
				{
					if (Double.isNaN(vec1.xCoord) || Double.isNaN(vec1.yCoord) || Double.isNaN(vec1.zCoord))
					{
						return null;
					}

					if (l == i && i1 == j && j1 == k)
					{
						return b3 ? movingobjectposition2 : null;
					}

					boolean flag6 = true;
					boolean flag3 = true;
					boolean flag4 = true;
					double d0 = 999.0D;
					double d1 = 999.0D;
					double d2 = 999.0D;

					if (i > l)
					{
						d0 = l + 1.0D;
					}
					else if (i < l)
					{
						d0 = l + 0.0D;
					}
					else
					{
						flag6 = false;
					}

					if (j > i1)
					{
						d1 = i1 + 1.0D;
					}
					else if (j < i1)
					{
						d1 = i1 + 0.0D;
					}
					else
					{
						flag3 = false;
					}

					if (k > j1)
					{
						d2 = j1 + 1.0D;
					}
					else if (k < j1)
					{
						d2 = j1 + 0.0D;
					}
					else
					{
						flag4 = false;
					}

					double d3 = 999.0D;
					double d4 = 999.0D;
					double d5 = 999.0D;
					double d6 = vec2.xCoord - vec1.xCoord;
					double d7 = vec2.yCoord - vec1.yCoord;
					double d8 = vec2.zCoord - vec1.zCoord;

					if (flag6)
					{
						d3 = (d0 - vec1.xCoord) / d6;
					}

					if (flag3)
					{
						d4 = (d1 - vec1.yCoord) / d7;
					}

					if (flag4)
					{
						d5 = (d2 - vec1.zCoord) / d8;
					}

					boolean flag5 = false;
					byte b0;

					if (d3 < d4 && d3 < d5)
					{
						if (i > l)
						{
							b0 = 4;
						}
						else
						{
							b0 = 5;
						}

						vec1.xCoord = d0;
						vec1.yCoord += d7 * d3;
						vec1.zCoord += d8 * d3;
					}
					else if (d4 < d5)
					{
						if (j > i1)
						{
							b0 = 0;
						}
						else
						{
							b0 = 1;
						}

						vec1.xCoord += d6 * d4;
						vec1.yCoord = d1;
						vec1.zCoord += d8 * d4;
					}
					else
					{
						if (k > j1)
						{
							b0 = 2;
						}
						else
						{
							b0 = 3;
						}

						vec1.xCoord += d6 * d5;
						vec1.yCoord += d7 * d5;
						vec1.zCoord = d2;
					}

					Vec3 vec32 = Vec3.createVectorHelper(vec1.xCoord, vec1.yCoord, vec1.zCoord);
					l = (int)(vec32.xCoord = MathHelper.floor_double(vec1.xCoord));

					if (b0 == 5)
					{
						--l;
						++vec32.xCoord;
					}

					i1 = (int)(vec32.yCoord = MathHelper.floor_double(vec1.yCoord));

					if (b0 == 1)
					{
						--i1;
						++vec32.yCoord;
					}

					j1 = (int)(vec32.zCoord = MathHelper.floor_double(vec1.zCoord));

					if (b0 == 3)
					{
						--j1;
						++vec32.zCoord;
					}

					Block block1 = world.getBlock(l, i1, j1);
					int l1 = world.getBlockMetadata(l, i1, j1);

					if (!b2 || block1.getCollisionBoundingBoxFromPool(world, l, i1, j1) != null)
					{
						if (block1.canCollideCheck(l1, b1) && this.isBlockSolidToProjectiles(world, l, i1, j1, block1, e))
						{
							MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, l, i1, j1, vec1, vec2);

							if (movingobjectposition1 != null)
							{
								return movingobjectposition1;
							}
						}
						else
						{
							movingobjectposition2 = new MovingObjectPosition(l, i1, j1, b0, vec1, false);
						}
					}
				}

				return b3 ? movingobjectposition2 : null;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

}
