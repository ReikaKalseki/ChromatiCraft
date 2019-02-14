/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Ability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.AbilityRituals;
import Reika.ChromatiCraft.Items.Tools.ItemEfficiencyCrystal;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.ModInterface.TileEntityLifeEmitter;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.ScaledDirection;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Event.PlayerHasItemEvent;
import Reika.DragonAPI.Instantiable.Event.PlayerPlaceBlockEvent;
import Reika.DragonAPI.Instantiable.Event.PostItemUseEvent;
import Reika.DragonAPI.Instantiable.Event.RawKeyPressEvent;
import Reika.DragonAPI.Instantiable.Event.RemovePlayerItemEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.SlotEvent.AddToSlotEvent;
import Reika.DragonAPI.Instantiable.Event.Client.ClientLoginEvent;
import Reika.DragonAPI.Instantiable.Event.Client.SinglePlayerLogoutEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.ItemHandlers.BloodMagicHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ChiselBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.RailcraftHandler;
import Reika.VoidMonster.Entity.EntityVoidMonster;
import WayofTime.alchemicalWizardry.api.event.ItemDrainNetworkEvent;
import WayofTime.alchemicalWizardry.api.event.PlayerDrainNetworkEvent;
import WayofTime.alchemicalWizardry.api.event.SacrificeKnifeUsedEvent;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;



public class AbilityHelper {

	public static final int[] REACH_SCALE = {8, 12, 16, 32, 64, 128, 192};

	//Client Side
	public int playerReach = -1;

	private final PlayerMap<WorldLocation> playerClicks = new PlayerMap();
	private final PlayerMap<Boolean> isDrawingBox = new PlayerMap();
	public final PlayerMap<ScaledDirection> shifts = new PlayerMap();

	private final PlayerMap<LossCache> lossCache = new PlayerMap();

	final PlayerMap<InventoryArray> inventories = new PlayerMap();

	final PlayerMap<HashMap<String, WarpPoint>> teleports = new PlayerMap();

	private final MultiMap<Ability, ProgressStage> progressMap = new MultiMap(new HashSetFactory());
	private final MultiMap<Ability, CrystalElement> structureMap = new MultiMap(new HashSetFactory());

	private final HashMap<Ability, ElementTagCompound> tagMap = new HashMap();

	private final HashMap<Class, AbilityXRays> xRayMap = new HashMap();

	private final PlayerMap<PlayerPath> playerPaths = new PlayerMap();

	private final HashSet<UUID> noClippingMagnet = new HashSet();

	private final PlayerMap<Long> dashTime = new PlayerMap();

	private final HashSet<UUID> doubleJumps = new HashSet();

	private final PlayerMap<Collection<LightningBolt>> playerBolts = new PlayerMap();

	public static final AbilityHelper instance = new AbilityHelper();

	//private int savedAOSetting;

	public boolean isNoClipEnabled;

	private static final Random rand = new Random();

	private final PlayerMap<ItemStack> refillItem = new PlayerMap();
	private static final String AE_LOC_TAG = "AELoc";

	public static final int LYING_DURATION = 1200;

	private AbilityHelper() {
		List<Ability> li = Chromabilities.getAbilities();
		for (Ability c : li) {
			ElementTagCompound tag = AbilityRituals.instance.getAura(c);
			tagMap.put(c, tag);
		}

		progressMap.addValue(Chromabilities.FIREBALL, ProgressStage.NETHER);
		progressMap.addValue(Chromabilities.PYLON, ProgressStage.SHOCK);
		progressMap.addValue(Chromabilities.DEATHPROOF, ProgressStage.DIE);
		progressMap.addValue(Chromabilities.TELEPORT, ProgressStage.STRUCTCOMPLETE);
		progressMap.addValue(Chromabilities.SPAWNERSEE, ProgressStage.DIMENSION);
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
		progressMap.addValue(Chromabilities.RECHARGE, ProgressStage.STRUCTCOMPLETE);
		progressMap.addValue(Chromabilities.GROWAURA, ProgressStage.RAINBOWLEAF);
		progressMap.addValue(Chromabilities.MEINV, ProgressStage.DIMENSION);
		progressMap.addValue(Chromabilities.MOBSEEK, ProgressStage.STRUCTCOMPLETE);
		if (ModList.FORESTRY.isLoaded())
			progressMap.addValue(Chromabilities.BEEALYZE, ProgressStage.HIVE);
		progressMap.addValue(Chromabilities.BEEALYZE, ProgressStage.LINK);
		progressMap.addValue(Chromabilities.NUKER, ProgressStage.STRUCTCOMPLETE);

		for (Ability a : progressMap.keySet()) {
			if (progressMap.get(a).contains(ProgressStage.STRUCTCOMPLETE)) {
				for (CrystalElement e : AbilityRituals.instance.getAura(a).elementSet())
					structureMap.addValue(a, e);
			}
		}

		for (AbilityXRays x : AbilityXRays.values()) {
			xRayMap.put(x.objectClass, x);
		}
	}

	public void register() {
		PlayerHandler.instance.registerTracker(LoginApplier.instance);
	}

	@SideOnly(Side.CLIENT)
	public AbilityXRays getAbilityXRay(Object te) {
		return xRayMap.get(te.getClass());
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
			AbilityCalls.shiftArea((WorldServer)ep.worldObj, box, dir.direction, dir.distance, (EntityPlayerMP)ep);
		}
	}

	@SubscribeEvent
	public void clickBlock(PlayerInteractEvent evt) {
		if (evt.action == Action.RIGHT_CLICK_BLOCK) {
			this.addPlayerClick(evt.entityPlayer, evt.world, evt.x, evt.y, evt.z);
		}
	}

	@SubscribeEvent
	public void superbuild(PlayerPlaceBlockEvent evt) {
		if (!evt.world.isRemote && Chromabilities.SUPERBUILD.enabledOn(evt.player) && KeyWatcher.instance.isKeyDown(evt.player, ChromatiCraft.config.getSuperbuildKey())) {
			AbilityCalls.superbuild(evt.world, evt.xCoord, evt.yCoord, evt.zCoord, evt.side, evt.block, evt.metadata, evt.getItem(), evt.player);
			//Chromabilities.SUPERBUILD.setToPlayer(evt.player, false);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void middleMouseUse(MouseEvent e) {
		if (e.buttonstate && e.button == 2 && !ChromaOptions.KEYBINDABILITY.getState()) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.thePlayer.capabilities.isCreativeMode) {
				MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlockClient(Minecraft.getMinecraft().playerController.getBlockReachDistance(), false);
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

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerLogin(ClientLoginEvent evt) {
		if (evt.newLogin) {
			LoginApplier.instance.onPlayerLogin(evt.player);
		}
	}

	@SubscribeEvent
	public void disableNoClip(SinglePlayerLogoutEvent evt) {
		isNoClipEnabled = false;
	}

	@SubscribeEvent
	public void disableNoClip(ClientDisconnectionFromServerEvent evt) {
		isNoClipEnabled = false;
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

	public Collection<WarpPoint> getTeleportLocations(EntityPlayer ep) {
		WarpPointData.readFromNBT(ep);
		HashMap<String, WarpPoint> c = teleports.get(ep);
		return c != null ? Collections.unmodifiableCollection(c.values()) : new HashSet();
	}

	public void addWarpPoint(String s, EntityPlayer ep) {
		this.addWarpPoint(s, ep, new WorldLocation(ep));
	}

	public void addWarpPoint(String s, EntityPlayer ep, WorldLocation loc) {
		WarpPointData.readFromNBT(ep);
		HashMap<String, WarpPoint> c = teleports.get(ep);
		if (c == null) {
			c = new HashMap();
			teleports.put(ep, c);
		}
		c.put(s, new WarpPoint(s, loc));
		WarpPointData.writeToNBT(ep);
		//WarpPointData.initWarpData(ep.worldObj).setDirty(true);
	}

	public void gotoWarpPoint(String s, EntityPlayerMP ep) {
		WarpPointData.readFromNBT(ep);
		HashMap<String, WarpPoint> c = teleports.get(ep);
		if (c != null) {
			WarpPoint wp = c.get(s);
			this.gotoWarpPoint(wp, ep);
		}
	}

	public void gotoMapWarpPoint(WorldLocation loc, EntityPlayerMP ep) {
		new WarpPoint("", loc).teleportPlayerTo(ep);
	}

	public void gotoWarpPoint(WarpPoint wp, EntityPlayerMP ep) {
		if (wp != null && wp.canTeleportPlayer(ep)) {
			wp.teleportPlayerTo(ep);
		}
	}

	public void removeWarpPoint(String s, EntityPlayer ep) {
		WarpPointData.readFromNBT(ep);
		HashMap<String, WarpPoint> c = teleports.get(ep);
		if (c != null) {
			c.remove(s);
			if (c.isEmpty()) {

			}
		}
		WarpPointData.writeToNBT(ep);
		//WarpPointData.initWarpData(ep.worldObj).setDirty(true);
	}

	public boolean playerCanWarpTo(EntityPlayer ep, WorldLocation loc) {
		HashMap<String, WarpPoint> c = teleports.get(ep);
		return c != null && c.values().contains(new WarpPoint("", ep));
	}

	@SideOnly(Side.CLIENT)
	public void copyVoxelMapWaypoints() {
		for (WarpPoint p : WarpPointData.loadMiniMaps()) {
			this.addWarpPoint(p.label, Minecraft.getMinecraft().thePlayer, p.location);
			ReikaPacketHelper.sendStringIntPacket(ChromatiCraft.packetChannel, ChromaPackets.SENDTELEPORT.ordinal(), PacketTarget.server, p.label, p.location.dimensionID, p.location.xCoord, p.location.yCoord, p.location.zCoord);
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
		Collection<CrystalElement> li2 = structureMap.get(c);
		for (CrystalElement e : li2) {
			if (!ProgressionManager.instance.hasPlayerCompletedStructureColor(ep, e))
				return false;
		}
		return true;
	}

	public Collection<ProgressStage> getProgressFor(Ability c) {
		return Collections.unmodifiableCollection(progressMap.get(c));
	}

	public Collection<CrystalElement> getRequiredStructuresFor(Ability c) {
		return Collections.unmodifiableCollection(structureMap.get(c));
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
		if (evt.entityPlayer.worldObj.isRemote)
			return;
		ItemStack is = evt.entityPlayer.getCurrentEquippedItem();
		if (Chromabilities.MEINV.enabledOn(evt.entityPlayer) && is != null && is.stackSize == is.getMaxStackSize() && is.stackSize > 1) {
			refillItem.put(evt.entityPlayer, is.copy());
		}
		else {
			refillItem.remove(evt.entityPlayer);
		}
	}

	@SubscribeEvent
	public void refillUsedStack(PostItemUseEvent evt) {
		if (evt.entityPlayer.worldObj.isRemote)
			return;
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
		return me != null ? (int)me.removeItem(ReikaItemHelper.getSizedItemStack(is, amt), false, true) : 0;
	}

	private MESystemReader getMESystem(EntityPlayer ep) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep).getCompoundTag(AE_LOC_TAG);
		if (nbt == null || nbt.hasNoTags())
			return null;
		WorldLocation c = WorldLocation.readTag(nbt);
		if (c == null) {
			return null;
		}
		TileEntity te = c.getTileEntity();
		if (te instanceof IGridHost) {
			IGridHost ih = (IGridHost)te;
			IGridNode n = ih.getGridNode(ForgeDirection.VALID_DIRECTIONS[nbt.getInteger("dir")]);
			MESystemReader me = new MESystemReader(n, ep);
			return me;
		}
		return null;
	}

	public void saveMESystemLocation(EntityPlayer ep, TileEntity te, int s) {
		NBTTagCompound NBT = new NBTTagCompound();
		WorldLocation c = new WorldLocation(te);
		c.writeToNBT(NBT);
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

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void rerouteStackToMESystem(EntityItemPickupEvent evt) {
		if (evt.entityPlayer.worldObj.isRemote || evt.item.isDead || evt.isCanceled())
			return;
		if (Chromabilities.MEINV.enabledOn(evt.entityPlayer)) {
			ItemStack is = evt.item.getEntityItem();
			int n = Loader.isModLoaded("dualhotbar") ? 18 : 9;
			for (int i = 0; i < n; i++) {
				ItemStack in = evt.entityPlayer.inventory.mainInventory[i];
				if (ReikaItemHelper.matchStacks(is, in) && in.stackSize == in.getMaxStackSize()) {
					MESystemReader me = this.getMESystem(evt.entityPlayer);
					if (me != null) {
						int left = (int)me.addItem(is, false);
						if (left > 0) {
							is.stackSize = left;
						}
						else {
							evt.item.setDead();
							evt.setCanceled(true);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void delegateStackCheck(PlayerHasItemEvent evt) {
		if (evt.entityPlayer.worldObj.isRemote)
			return;
		if (Chromabilities.MEINV.enabledOn(evt.entityPlayer)) {
			MESystemReader me = this.getMESystem(evt.entityPlayer);
			if (me != null) {
				if (me.getItemCount(evt.getItem(), true) > 0) {
					evt.setResult(Result.ALLOW);
				}
			}
		}
	}

	@SubscribeEvent
	public void delegateStackDecr(RemovePlayerItemEvent evt) {
		if (evt.entityPlayer.worldObj.isRemote)
			return;
		if (Chromabilities.MEINV.enabledOn(evt.entityPlayer)) {
			MESystemReader me = this.getMESystem(evt.entityPlayer);
			if (me != null) {
				int rem = (int)me.removeItem(evt.getItem(), false, true);
				if (rem == evt.getItem().stackSize) {
					evt.setCanceled(true);
				}
			}
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
		ReikaSoundHelper.playClientSound(ChromaSounds.NOCLIPOFF, Minecraft.getMinecraft().thePlayer, 1, 1);
	}

	@SideOnly(Side.CLIENT)
	private void clientNoClipEnable() {
		//savedAOSetting = Minecraft.getMinecraft().gameSettings.ambientOcclusion;
		//Minecraft.getMinecraft().gameSettings.ambientOcclusion = 0;
		ReikaRenderHelper.rerenderAllChunks();
		ReikaSoundHelper.playClientSound(ChromaSounds.NOCLIPON, Minecraft.getMinecraft().thePlayer, 1, 1);
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
		if (b == Blocks.stained_hardened_clay || b == Blocks.hardened_clay)
			return false;
		if (b == Blocks.snow || b == Blocks.snow_layer || b == Blocks.mycelium)
			return false;
		if (b == Blocks.bedrock)
			return y == 0 || (y >= 128 && world.provider.dimensionId == -1);
		if (ReikaBlockHelper.isLeaf(b, meta) || ReikaBlockHelper.isWood(b, meta))
			return b instanceof IPlantable;
		if (RailcraftHandler.Blocks.QUARRIED.match(b, meta) || RailcraftHandler.Blocks.ABYSSAL.match(b, meta))
			return false;
		if (ChiselBlockHandler.isWorldgenBlock(b, meta))
			return false;
		if (b == ChromaBlocks.CLIFFSTONE.getBlockInstance())
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

	@SubscribeEvent
	public void analyzeGenes(EntityItemPickupEvent evt) {
		if (Chromabilities.BEEALYZE.enabledOn(evt.entityPlayer)) {
			ItemStack is = evt.item.getEntityItem();
			if (is != null)
				this.analyzeGenes(is);
		}
	}

	@SubscribeEvent
	public void analyzeGenes(AddToSlotEvent evt) {
		IInventory ii = evt.inventory;
		if (ii instanceof InventoryPlayer) {
			if (Chromabilities.BEEALYZE.enabledOn(((InventoryPlayer)ii).player)) {
				ItemStack is = ii.getStackInSlot(evt.slotID);
				if (is != null)
					this.analyzeGenes(is);
			}
		}
	}

	@SubscribeEvent
	//@ModDependent(ModList.AGRICRAFT)
	public void analyzeGenes(PlayerInteractEvent evt) {
		if (evt.action == Action.RIGHT_CLICK_BLOCK || evt.action == Action.LEFT_CLICK_BLOCK) {
			if (Chromabilities.BEEALYZE.enabledOn(evt.entityPlayer)) {
				TileEntity te = evt.world.getTileEntity(evt.x, evt.y, evt.z);
				if (te instanceof IInventory) {
					IInventory ii = (IInventory)te;
					for (int i = 0; i < ii.getSizeInventory(); i++) {
						ItemStack is = ii.getStackInSlot(i);
						if (is != null) {
							this.analyzeGenes(is);
						}
					}
				}
			}
		}
	}

	/*
	@SubscribeEvent
	@ModDependent(ModList.FORESTRY)
	public void analyzeBees(PlayerInteractEvent evt) {
		if (evt.action == Action.RIGHT_CLICK_BLOCK || evt.action == Action.LEFT_CLICK_BLOCK) {
			if (Chromabilities.BEEALYZE.enabledOn(evt.entityPlayer)) {
				TileEntity te = evt.world.getTileEntity(evt.x, evt.y, evt.z);
				if (te instanceof IBeeHousing) {
					IBeeHousing ibh = (IBeeHousing)te;
					IBeeHousingInventory ii = ibh.getBeeInventory();
					ReikaBeeHelper.analyzeBee(ii.getQueen());
					ReikaBeeHelper.analyzeBee(ii.getDrone());
					if (te instanceof IInventory) {
						IInventory ii2 = (IInventory)te;
						for (int i = 0; i < ii2.getSizeInventory(); i++) {
							ReikaBeeHelper.analyzeBee(ii2.getStackInSlot(i));
						}
					}
				}
				else if (te instanceof IBeeHousingInventory) {
					IBeeHousingInventory ii = (IBeeHousingInventory)te;
					ReikaBeeHelper.analyzeBee(ii.getQueen());
					ReikaBeeHelper.analyzeBee(ii.getDrone());
					if (te instanceof IInventory) {
						IInventory ii2 = (IInventory)te;
						for (int i = 0; i < ii2.getSizeInventory(); i++) {
							ReikaBeeHelper.analyzeBee(ii2.getStackInSlot(i));
						}
					}
				}
			}
		}
	}
	 */

	public void analyzeGenes(ItemStack is) {
		if (ModList.FORESTRY.isLoaded())
			;//ReikaBeeHelper.analyzeBee(is); use tooltips
		if (ModList.AGRICRAFT.isLoaded()) {
			if (is.stackTagCompound != null) {
				if (is.stackTagCompound.hasKey("gain") || is.stackTagCompound.hasKey("growth") || is.stackTagCompound.hasKey("strength")) {
					is.stackTagCompound.setBoolean("analyzed", true);
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	@ModDependent(ModList.FORESTRY)
	public void showBeeGenes(ItemTooltipEvent evt) {
		if (ModList.FORESTRY.isLoaded() && ReikaBeeHelper.isBee(evt.itemStack)) {
			if (Chromabilities.BEEALYZE.enabledOn(evt.entityPlayer)) {
				Iterator<String> it = evt.toolTip.iterator();
				boolean primed = false;
				while (it.hasNext()) {
					String s = it.next();
					if (s.contains("Pristine Stock") || s.contains("Ignoble Stock"))
						primed = true;
					if (s.contains("<Unknown genome>"))
						primed = true;
					if (s.contains("Forestry"))
						primed = false;
					if (primed)
						it.remove();
				}
				if (GuiScreen.isCtrlKeyDown() || GuiScreen.isShiftKeyDown()) {
					EnumBeeType type = ReikaBeeHelper.getBeeRoot().getType(evt.itemStack);
					if (GuiScreen.isShiftKeyDown()) {
						ArrayList<String> li = ReikaBeeHelper.getGenesAsStringList(evt.itemStack);
						if (type == EnumBeeType.QUEEN || type == EnumBeeType.PRINCESS)
							evt.toolTip.add(evt.toolTip.size(), EnumChatFormatting.YELLOW.toString()+EnumChatFormatting.ITALIC.toString()+(ReikaBeeHelper.isPristine(evt.itemStack) ? "Pristine Stock" : "Ignoble Stock"));
						evt.toolTip.addAll(evt.toolTip.size(), li);
					}
					if (GuiScreen.isCtrlKeyDown()) {
						if (type == EnumBeeType.QUEEN) {
							IBee bee = ReikaBeeHelper.getBee(evt.itemStack);
							IBeeGenome ibg = bee.getMate();
							ArrayList<String> li = ReikaBeeHelper.getGenesAsStringList(ibg);
							li.add(0, EnumChatFormatting.GOLD+"Mate:");
							evt.toolTip.addAll(evt.toolTip.size(), li);
						}
					}
				}
				else {
					evt.toolTip.add(evt.toolTip.size(), "Hold "+EnumChatFormatting.GREEN+"LSHIFT"+EnumChatFormatting.RESET+" to show genes");
					EnumBeeType type = ReikaBeeHelper.getBeeRoot().getType(evt.itemStack);
					if (type == EnumBeeType.QUEEN) {
						evt.toolTip.add(evt.toolTip.size(), "Hold "+EnumChatFormatting.GREEN+"LCTRL"+EnumChatFormatting.RESET+" to show mate");
					}
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	@ModDependent(ModList.FORESTRY)
	public void showTreeGenes(ItemTooltipEvent evt) {
		if (ModList.FORESTRY.isLoaded() && ReikaBeeHelper.isTree(evt.itemStack)) {
			if (Chromabilities.BEEALYZE.enabledOn(evt.entityPlayer)) {
				Iterator<String> it = evt.toolTip.iterator();
				boolean primed = false;
				while (it.hasNext()) {
					String s = it.next();
					if (s.contains("Pristine Stock") || s.contains("Ignoble Stock"))
						primed = true;
					if (s.contains("<Unknown genome>"))
						primed = true;
					if (s.contains("Forestry"))
						primed = false;
					if (primed)
						it.remove();
				}
				if (GuiScreen.isShiftKeyDown()) {
					ArrayList<String> li = ReikaBeeHelper.getGenesAsStringList(evt.itemStack);
					evt.toolTip.addAll(evt.toolTip.size(), li);
				}
				else {
					evt.toolTip.add(evt.toolTip.size(), "Hold "+EnumChatFormatting.GREEN+"LSHIFT"+EnumChatFormatting.RESET+" to show genes");
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean canRenderEntityXRay(Entity e) {
		if (ModList.VOIDMONSTER.isLoaded() && e instanceof EntityVoidMonster)
			return false;
		if (e == Minecraft.getMinecraft().thePlayer)
			return false;
		int x = MathHelper.floor_double(e.posX+TileEntityRendererDispatcher.staticPlayerX);
		int y = MathHelper.floor_double(e.posY+TileEntityRendererDispatcher.staticPlayerY);
		int z = MathHelper.floor_double(e.posZ+TileEntityRendererDispatcher.staticPlayerZ);
		if (e.worldObj.getBlock(x, y, z) == Blocks.mob_spawner)
			return false;
		return e instanceof EntityLivingBase && e.worldObj != null;
	}

	@SubscribeEvent
	@ModDependent(ModList.BLOODMAGIC)
	public void interceptSoulNet(PlayerDrainNetworkEvent evt) {
		EntityPlayer ep = evt.player;
		if (Chromabilities.LIFEPOINT.enabledOn(ep)) {
			float amt = evt.drainAmount;
			ElementTagCompound tag1 = TileEntityLifeEmitter.getLumensPerHundredLP().scale(amt/100F);
			ElementTagCompound tag2 = PlayerElementBuffer.instance.getPlayerBuffer(ep);
			tag2.intersectWith(tag1);
			float ratio = tag2.getSmallestRatio(tag1);
			if (ratio >= 1) {
				PlayerElementBuffer.instance.removeFromPlayer(ep, tag1);
				evt.drainAmount = 0;
				if (evt instanceof ItemDrainNetworkEvent) {
					ItemDrainNetworkEvent ev = (ItemDrainNetworkEvent)evt;
					ev.damageAmount = 0;
					ev.shouldDamage = false;
				}
				else
					evt.setCanceled(true);
			}
			else if (ratio > 0) {
				ElementTagCompound rem = tag1.copy();
				rem.scale(ratio);
				float rat = 1-ratio;
				PlayerElementBuffer.instance.removeFromPlayer(ep, rem);
				evt.drainAmount *= rat;
				if (evt instanceof ItemDrainNetworkEvent) {
					ItemDrainNetworkEvent ev = (ItemDrainNetworkEvent)evt;
					ev.damageAmount *= rat;
					if (rat < 0.25)
						ev.shouldDamage = false;
				}
			}
			else {

			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.BLOODMAGIC)
	public void onUseSacrificeOrb(SacrificeKnifeUsedEvent evt) {
		EntityPlayer ep = evt.player;
		if (Chromabilities.LIFEPOINT.enabledOn(ep)) {
			ElementTagCompound tag = AbilityHelper.instance.getUsageElementsFor(Chromabilities.LIFEPOINT, ep);
			tag.maximizeWith(TileEntityLifeEmitter.getLumensPerHundredLP());
			if (PlayerElementBuffer.instance.playerHas(ep, tag)) {
				Chromabilities.LIFEPOINT.trigger(ep, 0);
				evt.shouldDrainHealth = false;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.BLOODMAGIC)
	public void onUseBloodOrb(PlayerInteractEvent evt) {
		if (evt.action == Action.RIGHT_CLICK_AIR) {
			EntityPlayer ep = evt.entityPlayer;
			ItemStack is = ep.getCurrentEquippedItem();
			if (is != null) {
				if (BloodMagicHandler.getInstance().isBloodOrb(is.getItem())) {
					if (Chromabilities.LIFEPOINT.enabledOn(ep)) {
						ElementTagCompound tag = AbilityHelper.instance.getUsageElementsFor(Chromabilities.LIFEPOINT, ep);
						tag.maximizeWith(TileEntityLifeEmitter.getLumensPerHundredLP());
						if (PlayerElementBuffer.instance.playerHas(ep, tag)) {
							Chromabilities.LIFEPOINT.trigger(ep, 2);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void applyAOE(LivingAttackEvent evt) {
		DamageSource src = evt.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			if (Chromabilities.COMMUNICATE.enabledOn(ep)) {
				EntityLivingBase mob = evt.entityLiving;
				if (mob instanceof EntityCreature && ((EntityCreature)mob).getEntityToAttack() != ep) {
					ep.getEntityData().setLong("lastCommunicateLie", ep.worldObj.getTotalWorldTime());
					AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(ep, 16).expand(16, 0, 16);
					List<EntityCreature> li = ep.worldObj.getEntitiesWithinAABB(ReikaEntityHelper.getEntityCategoryClass(mob), box);
					HashSet<Class> played = new HashSet();
					for (EntityCreature ec : li) {
						ec.setAttackTarget(ep);
						if (!played.contains(ec.getClass())) {
							played.add(ec.getClass());
							ReikaEntityHelper.playAggroSound(ec);
						}
					}
				}
			}
		}

	}

	public boolean canReachBoostSelect(World world, int x, int y, int z, EntityPlayer ep) {
		if (ChromaTiles.getTile(world, x, y, z) == ChromaTiles.PYLON)
			return false;
		Block b = world.getBlock(x, y, z);
		if (b instanceof BlockTieredResource) {
			return ((BlockTieredResource)b).isPlayerSufficientTier(world, x, y, z, ep);
		}
		return true;
	}

	public double getBoostedHealth(EntityPlayer ep) {
		AttributeModifier mod = ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(Chromabilities.HEALTH_UUID);
		double boost = mod != null ? mod.getAmount() : 0;
		return boost;
	}

	public void syncHealth(EntityPlayerMP player) {
		double health = this.getBoostedHealth(player);
		int[] i = ReikaJavaLibrary.splitDoubleToInts(health);
		ChromatiCraft.logger.log("Syncing health boost from server, boost="+health);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.HEALTHSYNC.ordinal(), player, i[0], i[1]);
	}

	@SideOnly(Side.CLIENT)
	public void setHealthClient(EntityPlayer ep, double attr) {
		ChromatiCraft.logger.log("Receiving health boost sync from server, boost="+attr);
		ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier(Chromabilities.HEALTH_UUID, "Chroma", attr, 2));
	}

	public void copyHealthBoost(EntityPlayer original, EntityPlayer ep) {
		ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier(Chromabilities.HEALTH_UUID, "Chroma", this.getBoostedHealth(original), 2));
	}

	public Collection<LightningBolt> getCollectionBeamsForPlayer(EntityPlayer ep) {
		Collection<LightningBolt> c = playerBolts.get(ep);
		if (c == null || c.isEmpty()) {
			if (c == null) {
				c = new ArrayList();
				playerBolts.put(ep, c);
			}
			c.clear();
			DecimalPosition p1 = new DecimalPosition(0, -0.75, 0);
			int n = 3;
			double r = 3;
			for (int i = 0; i < n; i++) {
				double a1 = Math.toRadians(i*360D/n);
				double a2 = Math.toRadians((i+0.5)*360/n);
				double x1 = r*Math.cos(a1);
				double z1 = r*Math.sin(a1);
				double x2 = r*Math.cos(a2);
				double z2 = r*Math.sin(a2);
				LightningBolt b1 = new LightningBolt(p1, new DecimalPosition(x1, -1.6, z1), 3);
				LightningBolt b2 = new LightningBolt(p1, new DecimalPosition(x2, 0.5, z2), 3);
				b1.velocity = b2.velocity = b1.velocity/32D;
				//b1.variance = b2.variance = b1.variance*2D;
				c.add(b1);
				c.add(b2);
			}
		}
		return Collections.unmodifiableCollection(c);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderChestCollectionFirstPerson(TickEvent.RenderTickEvent evt) {
		if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			if (ep != null && Chromabilities.CHESTCLEAR.enabledOn(ep)) { //ep != null is 'not main menu'
				GL11.glPushMatrix();
				GL11.glTranslated(0, 0, 0);
				this.renderChestCollectionFX(ep, evt.renderTickTime);
				GL11.glPopMatrix();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderChestCollectionFX(EntityPlayer ep, float ptick) {
		if (ep.worldObj == null)
			return;

		if (!Minecraft.getMinecraft().isGamePaused() && ep.ticksExisted%32 == 0)
			ReikaSoundHelper.playClientSound(ChromaSounds.RADIANCE, ep, 0.20F, 1F, false);

		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if (gui instanceof GuiContainerCreative || gui instanceof GuiInventory)
			return;

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glPushMatrix();
		double sc = 1.25+0.5*Math.sin(ep.ticksExisted/60D);
		GL11.glScaled(sc, sc, sc);
		GL11.glRotated(ep.ticksExisted*2.4, 0, 1, 0);
		Collection<LightningBolt> c = AbilityHelper.instance.getCollectionBeamsForPlayer(ep);
		//ReikaJavaLibrary.pConsole(c.size());
		int clr = 0xB6FF00;
		for (LightningBolt b : c) {
			ChromaFX.renderBolt(b, ptick, 192, 0.1875, 6, clr);
			b.update();
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();

		if (!Minecraft.getMinecraft().isGamePaused() && ep.getRNG().nextInt(8) == 0) {
			double px = ReikaRandomHelper.getRandomPlusMinus(ep.posX, 0.5);
			double pz = ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 0.5);
			double py = ep.posY-1.62+rand.nextDouble()*1.8;
			double v = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
			float s = (float)ReikaRandomHelper.getRandomBetween(0.75, 1.5);
			EntityFX fx = new EntityBlurFX(ep.worldObj, px, py, pz, 0, v, 0).setColor(clr).setGravity(0).setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

}
