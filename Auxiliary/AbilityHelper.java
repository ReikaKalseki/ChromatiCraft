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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.AbilityRituals;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler.PlayerTracker;
import Reika.DragonAPI.Instantiable.BasicInventory;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.ScaledDirection;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Instantiable.Event.RawKeyPressEvent;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
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

	private final PlayerMap<HashMap<String, WarpPoint>> teleports = new PlayerMap();

	private final MultiMap<Ability, ProgressStage> progressMap = new MultiMap();

	private final HashMap<Ability, ElementTagCompound> tagMap = new HashMap();

	private final HashMap<Class, TileXRays> xRayMap = new HashMap();

	private final PlayerMap<PlayerPath> playerPaths = new PlayerMap();

	private final HashSet<UUID> noClippingMagnet = new HashSet();

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
		progressMap.addValue(Chromabilities.TELEPORT, ProgressStage.CTM);
		progressMap.addValue(Chromabilities.SPAWNERSEE, ProgressStage.BREAKSPAWNER);

		for (TileXRays x : TileXRays.values()) {
			xRayMap.put(x.tileClass, x);
		}
	}

	@SideOnly(Side.CLIENT)
	public TileXRays getTileEntityXRay(TileEntity te) {
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
			if (ep != null) {
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
			ep.setPositionAndUpdate(location.xCoord+0.5, location.yCoord+0.25, location.zCoord+0.5);
			ep.playSound("mob.endermen.portal", 1, 1);
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
		}

	}

	public ElementTagCompound getElementsFor(Ability a) {
		return tagMap.get(a).copy();
	}

	public ElementTagCompound getUsageElementsFor(Ability c) {
		return tagMap.get(c).copy().scale(0.0008F); //was 0.0008F //was 0.0002F
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

	public static enum TileXRays {
		SPAWNERS(TileEntityMobSpawner.class, Blocks.mob_spawner, 0x224466),
		CHESTS(TileEntityChest.class, 0xC17C32);

		public final Class tileClass;
		private final Block texture;
		public final int highlightColor;

		private TileXRays(Class t, int c) {
			this(t, null, c);
		}

		public IIcon getTexture() {
			return texture != null ? texture.blockIcon : null;
		}

		private TileXRays(Class t, Block tex, int c) {
			texture = tex;
			tileClass = t;
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

}
