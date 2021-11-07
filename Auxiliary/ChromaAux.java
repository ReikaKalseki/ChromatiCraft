/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.Fluid;

import Reika.ChromatiCraft.ChromaGuiHandler;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Block.BlockSelectiveGlass;
import Reika.ChromatiCraft.Block.Dimension.BlockLightedLeaf;
import Reika.ChromatiCraft.Block.Worldgen.BlockCliffStone;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Entity.EntityGlowCloud;
import Reika.ChromatiCraft.Entity.EntityLaserPulse;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.MonumentCompletionRitual;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.ToolChargingSystem;
import Reika.ChromatiCraft.Magic.Interfaces.ChargingPoint;
import Reika.ChromatiCraft.Magic.Interfaces.PoweredItem;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.TargetData;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager.ProgressElement;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaEntities;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager;
import Reika.ChromatiCraft.World.IWG.PylonGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry.BlockSetData;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry.BlockSetWatcher;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry.IWGWatcher;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry.InterceptionException;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.Entity.CustomProjectile;
import Reika.DragonAPI.Interfaces.Item.ActivatedInventoryItem;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaSpawnerHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaChunkHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BloodMagicHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ChiselBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerBlockHandler;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;

public class ChromaAux {

	public static final Color[] sideColors = {Color.CYAN, Color.BLUE, Color.YELLOW, Color.BLACK, new Color(255, 120, 0), Color.MAGENTA};
	public static final String[] sideColorNames = {"CYAN", "BLUE", "YELLOW", "BLACK", "ORANGE", "MAGENTA"};

	//private static final GenerationInterceptWorld cliffRelayWorld = new GenerationInterceptWorld();
	//private static final GenerationInterceptWorld rainbowRelayWorld = new GenerationInterceptWorld();

	private static HashMap<String, ArrayList<CrystalElement>> fluidRunes = new HashMap();
	private static final MultiMap<TargetData, CrystalElement> beamColorMixes = new MultiMap();

	static {

	}

	public static final IWGWatcher slimeIslandBlocker = new IWGWatcher() {

		@Override
		public boolean canIWGRun(IWorldGenerator gen, Random random, int cx, int cz, World world, IChunkProvider generator, IChunkProvider loader) {
			if (ReikaChunkHelper.chunkContainsBiomeType(world, cx, cz, BiomeGlowingCliffs.class)) {
				return BiomeGlowingCliffs.canRunGenerator(gen);
			}
			return true;
		}

	};

	public static final InterceptionException dimensionException = new InterceptionException() {

		@Override
		public boolean doesExceptionApply(World world, int x, int y, int z, Block set, int meta) {
			return world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue();
		}

	};

	public static final BlockSetWatcher populationWatcher = new BlockSetWatcher() {

		@Override
		public void onChunkGeneration(World world, Map<Coordinate, BlockSetData> set) {
			for (Coordinate c : set.keySet()) {
				BiomeGenBase b = c.getBiome(world);
				if (ChromatiCraft.isRainbowForest(b)) {
					BlockSetData dat = set.get(c);
					if (dat.newBlock == ThaumItemHelper.BlockEntry.TOTEM.getBlock())
						dat.revert(world);
					else if (dat.newBlock == Blocks.mob_spawner) {
						TileEntityMobSpawner tm = (TileEntityMobSpawner)dat.getTileEntity(world);
						MobSpawnerBaseLogic lgc = tm.func_145881_a();
						lgc.activatingRangeFromPlayer = lgc.getEntityNameToSpawn().equals("CaveSpider") && lgc.getSpawnerY() < 55 ? 12 : 6;
						lgc.minSpawnDelay *= 2;
						lgc.maxSpawnDelay *= 4;
					}
					else if (ChromaTiles.getTileFromIDandMetadata(dat.newBlock, dat.newMetadata) != ChromaTiles.PYLON && InterfaceCache.NODE.instanceOf(dat.getTileEntity(world))) {
						TileEntity te = dat.getTileEntity(world);
						INode n = (INode)te;
						n.setNodeType(NodeType.NORMAL);
						n.setNodeModifier(NodeModifier.BRIGHT);
						if (te.worldObj.rand.nextInt(4) == 0) {
							float f = 2+te.worldObj.rand.nextFloat()*4;
							AspectList al = n.getAspects();
							for (Aspect a : new HashSet<Aspect>(al.aspects.keySet())) {
								al.aspects.put(a, (int)(f*al.getAmount(a)));
							}
						}
					}
				}
				else if (BiomeGlowingCliffs.isGlowingCliffs(b)) {
					BlockSetData dat = set.get(c);
					if (ModList.CHISEL.isLoaded() && dat.oldBlock == ChromaBlocks.CLIFFSTONE.getBlockInstance() && ChiselBlockHandler.isWorldgenBlock(dat.newBlock, dat.newMetadata)) {
						dat.revert(world);
					}
					else if (dat.newBlock == Blocks.mob_spawner) {
						TileEntityMobSpawner tm = (TileEntityMobSpawner)dat.getTileEntity(world);
						if (tm == null)
							continue;
						if (ReikaSpawnerHelper.getMobSpawnerMobName(tm).toLowerCase(Locale.ENGLISH).contains("wisp")) {
							ReikaSpawnerHelper.setMobSpawnerMob(tm, ChromaEntities.GLOWCLOUD.entityName);
						}
					}
					else if (ModList.TINKERER.isLoaded() && TinkerBlockHandler.getInstance().isSlimeIslandBlock(dat.newBlock, dat.newMetadata)) {
						dat.revert(world);
					}
					else if (ChromaTiles.getTileFromIDandMetadata(dat.newBlock, dat.newMetadata) != ChromaTiles.PYLON && InterfaceCache.NODE.instanceOf(dat.getTileEntity(world))) {
						TileEntity te = dat.getTileEntity(world);
						INode n = (INode)te;
						n.setNodeType(NodeType.NORMAL);
						n.setNodeModifier(NodeModifier.BRIGHT);
						if (te.worldObj.rand.nextInt(4) == 0) {
							float f = 1+te.worldObj.rand.nextFloat()*2;
							AspectList al = n.getAspects();
							for (Aspect a : new HashSet<Aspect>(al.aspects.keySet())) {
								al.aspects.put(a, (int)(f*al.getAmount(a)));
							}
						}
					}
				}
			}
		}

	};

	public static float getRFTransferEfficiency(World world, int x, int y, int z) {
		return ChromaOptions.getRFEfficiency();
	}

	public static final boolean hasGui(World world, int x, int y, int z, EntityPlayer ep) {
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			Object GUI = ChromaGuiHandler.instance.getClientGuiElement(0, ep, world, x, y, z);
			if (GUI != null)
				return true;
		}
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			Object GUI = ChromaGuiHandler.instance.getServerGuiElement(0, ep, world, x, y, z);
			if (GUI != null)
				return true;
		}
		return false;
	}

	public static int get4SidedMetadataFromPlayerLook(EntityLivingBase ep) {
		int i = MathHelper.floor_double((ep.rotationYaw * 4F) / 360F + 0.5D);
		while (i > 3)
			i -= 4;
		while (i < 0)
			i += 4;
		switch (i) {
			case 0:
				return 2;
			case 1:
				return 1;
			case 2:
				return 3;
			case 3:
				return 0;
		}
		return -1;
	}

	public static int get6SidedMetadataFromPlayerLook(EntityLivingBase ep) {
		if (MathHelper.abs(ep.rotationPitch) < 60) {
			int i = MathHelper.floor_double((ep.rotationYaw * 4F) / 360F + 0.5D);
			while (i > 3)
				i -= 4;
			while (i < 0)
				i += 4;
			switch (i) {
				case 0:
					return 2;
				case 1:
					return 1;
				case 2:
					return 3;
				case 3:
					return 0;
			}
		}
		else { //Looking up/down
			if (ep.rotationPitch > 0)
				return 4; //set to up
			else
				return 5; //set to down
		}
		return -1;
	}

	public static int get2SidedMetadataFromPlayerLook(EntityLivingBase ep) {
		int i = MathHelper.floor_double((ep.rotationYaw * 4F) / 360F + 0.5D);
		while (i > 3)
			i -= 4;
		while (i < 0)
			i += 4;

		switch (i) {
			case 0:
				return 0;
			case 1:
				return 1;
			case 2:
				return 0;
			case 3:
				return 1;
		}
		return -1;
	}

	public static boolean shouldSetFlipped(World world, int x, int y, int z) {
		boolean softBelow = ReikaWorldHelper.softBlocks(world, x, y-1, z);
		boolean softAbove = ReikaWorldHelper.softBlocks(world, x, y+1, z);
		if (!softAbove && softBelow) {
			return true;
		}
		return false;
	}

	public static String getMessage(String tag) {
		return StatCollector.translateToLocal("message."+tag);
	}

	public static void writeMessage(String tag) {
		ReikaChatHelper.writeString(getMessage(tag));
	}

	public static void spawnInteractionBallLightning(World world, int x, int y, int z, CrystalElement e) {
		if (!world.isRemote && ChromaOptions.BALLLIGHTNING.getState()) {
			int dx = ReikaRandomHelper.getRandomPlusMinus(x, 16);
			int dz = ReikaRandomHelper.getRandomPlusMinus(z, 16);
			double dy = world.getTopSolidOrLiquidBlock(dx, dz)+ReikaRandomHelper.getSafeRandomInt(8);//ReikaRandomHelper.getRandomPlusMinus(y+0.5, 16);
			world.spawnEntityInWorld(new EntityBallLightning(world, e, dx+0.5, dy+0.5, dz+0.5).setNoDrops());
		}
	}

	public static boolean requiresSpecialSpawnEnforcement(EntityLiving e) {
		String name = e.getClass().getName().toLowerCase(Locale.ENGLISH);
		return name.contains("lycanite");
	}

	public static void doPylonAttack(CrystalElement color, EntityLivingBase e, float amt, boolean taperNew) {
		doPylonAttack(color, e, amt, taperNew, 0);
	}

	public static void doPylonAttack(CrystalElement color, EntityLivingBase e, float amt, boolean taperNew, int looting) {

		final float originalAmt = amt;

		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			ProgressStage.SHOCK.stepPlayerTo(ep);
			//DO NOT UNCOMMENT, AS ALLOWS DISCOVERY OF ALL COLORS BEFORE PREREQ//ProgressionManager.instance.setPlayerDiscoveredColor(ep, color, true);
			if (ModList.BLOODMAGIC.isLoaded()) {
				int drain = 5000;
				if (BloodMagicHandler.getInstance().isPlayerWearingFullBoundArmor(ep)) {
					amt *= 10; //counter the 90% reduction
					drain = 50000;
				}
				SoulNetworkHandler.syphonFromNetwork(ep.getCommandSenderName(), drain);
			}

			if (taperNew) {
				if (e.ticksExisted < 600) {
					amt = 1; //1/2 heart for first 30s
				}
				else if (e.ticksExisted <= 1000) {
					amt = 1+(e.ticksExisted-600)/100; //increase by 1/2 heart every 5 seconds, up to 2.5 hearts at 50 seconds
				}
			}
		}

		float last = e.getHealth();

		e.attackEntityFrom(ChromatiCraft.pylonDamage[color == null ? 16 : color.ordinal()], amt);

		if (e.getHealth() > Math.max(0, last-originalAmt)) {
			if (originalAmt >= last) { //kill
				e.setHealth(0.1F);
				e.attackEntityFrom(ChromatiCraft.pylonDamage[color == null ? 16 : color.ordinal()], Float.MAX_VALUE);
			}
			else
				e.setHealth(last-originalAmt);
		}
	}

	public static float getIslandBias(float originalBias, float dx, float dz) {
		float dist = MathHelper.sqrt_double(dx*dx+dz*dz);
		return 50+50*MathHelper.sin(dist*0.0625F); //is 100 at spawn
	}

	public static MultiMap<TargetData, CrystalElement> getBeamColorMixes(Collection<CrystalTarget> c) {
		beamColorMixes.clear();
		for (CrystalTarget t : c) {
			beamColorMixes.addValue(new TargetData(t), t.color);
		}
		return beamColorMixes;
	}

	public static void changePylonColor(World world, TileEntityCrystalPylon te, CrystalElement e) {
		try {
			ChunkCoordIntPair ch = new Coordinate(te).asChunkPair();
			world.getChunkProvider().provideChunk(ch.chunkXPos, ch.chunkZPos);
			TileEntityCrystalPylon old = te;
			te = (TileEntityCrystalPylon)world.getTileEntity(te.xCoord, te.yCoord, te.zCoord);
			CrystalNetworker.instance.removeTile(old);
			PylonGenerator.instance.removeCachedPylon(old);
			te.setColor(e);
			BlockArray runes = te.getRuneLocations(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
			for (int i = 0; i < runes.getSize(); i++) {
				Coordinate c = runes.getNthBlock(i);
				if (c.getBlock(te.worldObj) == ChromaBlocks.RUNE.getBlockInstance())
					te.worldObj.setBlockMetadataWithNotify(c.xCoord, c.yCoord, c.zCoord, te.getColor().ordinal(), 3);
			}
			CrystalNetworker.instance.addTile(te);
			PylonGenerator.instance.cachePylon(te);
		}
		catch (Exception ex) {
			ChromatiCraft.logger.logError("Could not change pylon color @ "+te);
			ex.printStackTrace();
		}
	}

	public static void notifyServerPlayersExcept(EntityPlayer ep, ProgressElement p) {
		String sg = EnumChatFormatting.GOLD+ep.getCommandSenderName()+EnumChatFormatting.RESET+" has learned something new: "+p.getFormatting()+p.getTitle();
		WorldServer[] ws = DimensionManager.getWorlds();
		for (int i = 0; i < ws.length; i++) {
			for (EntityPlayer ep2 : ((List<EntityPlayer>)ws[i].playerEntities)) {
				if (ep2 != ep)
					ReikaChatHelper.sendChatToPlayer(ep2, sg);
			}
		}
	}

	public static void notifyServerPlayers(EntityPlayer ep, ProgressElement p) {
		String sg = EnumChatFormatting.GOLD+ep.getCommandSenderName()+EnumChatFormatting.RESET+" has learned something new: "+p.getFormatting()+p.getTitle();
		WorldServer[] ws = DimensionManager.getWorlds();
		for (int i = 0; i < ws.length; i++) {
			for (EntityPlayer ep2 : ((List<EntityPlayer>)ws[i].playerEntities)) {
				if (ep2 != ep) {
					ReikaChatHelper.sendChatToPlayer(ep2, sg);
				}
				else {
					ReikaChatHelper.sendChatToPlayer(ep2, "You have learned something new: "+p.getFormatting()+p.getTitle());
				}
			}
		}
	}
	/*
	public static int overrideLightValue(IBlockAccess world, int x, int y, int z, int val) {
		world = Minecraft.getMinecraft().theWorld;
		int base = defaultLightBrightness((World)world, x, y, z, val);
		double dist = EntityFlyingLight.getClosestLight((World)world, x, y, z);
		//dist = Math.min(dist, Minecraft.getMinecraft().thePlayer.getDistance(x+0.5, y+0.5, z+0.5));
		int l = 0;
		int m = 5;
		if (dist < m) {
			l = 15;
		}
		else if (dist < 15+m) {
			l = 15+m-(int)dist;
		}
		//ReikaJavaLibrary.pConsole(Integer.toHexString(base)+"; "+l, dist < Double.POSITIVE_INFINITY);
		return ReikaMathLibrary.bitRound(base, 20) | (l << 4);
	}

	private static int defaultLightBrightness(World world, int x, int y, int z, int val) {
		int sky = world.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z);
		int block = Math.max(val, world.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z));
		return sky << 20 | block << 4;
	}
	 */

	@Deprecated
	public static void permuteRunes(HashMap<Coordinate, CrystalElement> map, World world, EntityPlayer ep) {

	}

	public static CrystalElement getRune(Fluid fluid) {
		ArrayList<CrystalElement> li = fluidRunes.get(fluid.getName());
		if (li == null) {
			li = new ArrayList();
			fluidRunes.put(fluid.getName(), li);
			li.add(CrystalElement.CYAN);
			if (fluid.getTemperature() > 900) //900K
				li.add(CrystalElement.ORANGE);
			if (fluid.getTemperature() < 270)
				li.add(CrystalElement.WHITE);
			if (fluid.isGaseous())
				li.add(CrystalElement.LIME);
			if (fluid.getLuminosity() > 0)
				li.add(CrystalElement.BLUE);
			if (fluid.getDensity() > 4000)
				li.add(CrystalElement.RED);
			String n = fluid.getName().toLowerCase(Locale.ENGLISH);
			if (n.contains("oil"))
				li.add(CrystalElement.BROWN);
			if (n.contains("fuel"))
				li.add(CrystalElement.YELLOW);
			if (n.contains("xp") || fluid == ChromatiCraft.chroma)
				li.add(CrystalElement.PURPLE);
			if (n.contains("bio") || n.contains("honey") || n.contains("seed"))
				li.add(CrystalElement.GREEN);
		}
		return li.get(ReikaRandomHelper.getSafeRandomInt(li.size()));
	}

	public static boolean chargePlayerFromPylon(EntityPlayer player, ChargingPoint te, CrystalElement e, int tick) {
		if (te.canConduct() && te.allowCharging(player, e) && allowPlayerChargingAt(player, te, e)) {
			int add = Math.max(1, (int)(PlayerElementBuffer.instance.getChargeSpeed(player)*te.getChargeRateMultiplier(player, e)));
			int n = PlayerElementBuffer.instance.getChargeInefficiency(player);
			int drain = add*n;
			int energy = te.getEnergy(e);
			if (drain > energy) {
				drain = energy;
				add = drain/n;
			}
			if (add > 0 && PlayerElementBuffer.instance.canPlayerAccept(player, e, add)) {
				te.onUsedBy(player, e);
				if (PlayerElementBuffer.instance.addToPlayer(player, e, add, true))
					te.drain(e, drain);
				ProgressStage.CHARGE.stepPlayerTo(player);
				if (te instanceof TileEntityCrystalPylon)
					ProgressionManager.instance.setPlayerDiscoveredColor(player, ((TileEntityCrystalPylon)te).getColor(), true, true);
				if (player.worldObj.isRemote) {
					//this.spawnParticles(player, e);
					ChromaFX.createPylonChargeBeam(te, player, (tick%20)/20D, e);
				}
				else {
					chargePlayerTools(player, te, e);
				}
				return true;
			}
		}
		return false;
	}

	public static void chargePlayerTools(EntityPlayer player, ChargingPoint te, CrystalElement e) {
		int slot = DragonAPICore.rand.nextInt(player.inventory.mainInventory.length);
		ItemStack at = player.inventory.mainInventory[slot];
		if (at != null) {
			if (at.getItem() instanceof ActivatedInventoryItem) {
				int size = ((ActivatedInventoryItem)at.getItem()).getInventorySize(at);
				slot = DragonAPICore.rand.nextInt(size);
				if (((ActivatedInventoryItem)at.getItem()).isSlotActive(at, slot)) {
					ItemStack in = ((ActivatedInventoryItem)at.getItem()).getItem(at, slot);
					if (in != null && in.getItem() instanceof PoweredItem) {
						PoweredItem pi = (PoweredItem)in.getItem();
						if (pi.getColor(in) == e && pi.canChargeWhilePlayerCharges()) {
							int rate = Math.max(1, (int)(te.getToolChargingPower(player, e)*ToolChargingSystem.instance.getChargeRate(in)));
							ToolChargingSystem.instance.addCharge(in, rate);
						}
					}
				}
			}
			else if (at.getItem() instanceof PoweredItem) {
				PoweredItem pi = (PoweredItem)at.getItem();
				if (pi.getColor(at) == e && pi.canChargeWhilePlayerCharges()) {
					int rate = Math.max(1, (int)(te.getToolChargingPower(player, e)*ToolChargingSystem.instance.getChargeRate(at)));
					ToolChargingSystem.instance.addCharge(at, rate);
				}
			}
		}
	}

	private static boolean allowPlayerChargingAt(EntityPlayer player, ChargingPoint te, CrystalElement e) {
		return true;
	}

	public static List<AxisAlignedBB> interceptEntityCollision(World world, Entity e, AxisAlignedBB box) {
		if (e instanceof EntityPlayer && world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {

		}
		if (e instanceof EntityPlayer && Chromabilities.ORECLIP.enabledOn((EntityPlayer)e)) {
			return AbilityHelper.instance.getNoclipBlockBoxes((EntityPlayer)e, box);
		}
		else {
			return getSurrogateCollidingAABBs(world, e, box);//e.worldObj.getCollidingBoundingBoxes(e, box);
		}
	}

	public static List<AxisAlignedBB> getSurrogateCollidingAABBs(World world, Entity ep, AxisAlignedBB box) {
		ArrayList<AxisAlignedBB> li = new ArrayList();

		int i = MathHelper.floor_double(box.minX);
		int j = MathHelper.floor_double(box.maxX + 1.0D);
		int k = MathHelper.floor_double(box.minY);
		int l = MathHelper.floor_double(box.maxY + 1.0D);
		int i1 = MathHelper.floor_double(box.minZ);
		int j1 = MathHelper.floor_double(box.maxZ + 1.0D);

		for (int x = i; x < j; ++x) {
			for (int z = i1; z < j1; ++z) {
				if (world.blockExists(x, 64, z)) {
					for (int y = k - 1; y < l; ++y) {
						Block block;

						if (x >= -30000000 && x < 30000000 && z >= -30000000 && z < 30000000) {
							block = world.getBlock(x, y, z);
						}
						else {
							block = Blocks.stone;
						}

						boolean flag = true;
						if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue() && ChromaDimensionManager.isBlockedAir(world, x, y, z, block, ep)) {
							Blocks.stone.addCollisionBoxesToList(world, x, y, z, box, li, ep);
							ChromaDimensionManager.onPlayerBlockedFromBiome(world, x, y, z, ep);
							flag = false;
						}
						else if (ep instanceof EntityGlowCloud) {
							if (EntityGlowCloud.isBlockNonColliding(world, x, y, z, block)) {
								flag = false;
							}
						}
						else if (ep instanceof EntityItem) {
							if (((EntityItem)ep).getEntityItem().getItem() == ChromaItems.FERTILITYSEED.getItemInstance()) {
								if (block == ChromaBlocks.DYELEAF.getBlockInstance() || block == ChromaBlocks.DECAY.getBlockInstance() || block == ChromaBlocks.GLOWLEAF.getBlockInstance())
									flag = false;
								else if (block.getMaterial() == Material.leaves || block instanceof BlockLeavesBase || block.isLeaves(world, x, y, z))
									flag = false;
							}
						}
						if (flag) {
							block.addCollisionBoxesToList(world, x, y, z, box, li, ep); //necessary for vanilla and mods like TreeClimbing
						}
					}
				}
			}
		}

		double d0 = 0.25D;
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(ep, box.expand(d0, d0, d0));

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

	public static boolean applyNoclipPhase(EntityPlayer ep) {
		return ep.noClip || Chromabilities.ORECLIP.enabledOn(ep);
	}

	public static AxisAlignedBB getInterceptedCollisionBox(Entity e, World world, int x, int y, int z, AxisAlignedBB def) {
		Block b = world.getBlock(x, y, z);
		if (b == ChromaBlocks.SELECTIVEGLASS.getBlockInstance() && (e instanceof IProjectile || e instanceof EntityFireball || e instanceof CustomProjectile)) {
			if (BlockSelectiveGlass.canEntityPass(world, x, y, z, e)) {
				return null;
			}
		}
		else if ((e instanceof IProjectile || e instanceof EntityFireball || e instanceof CustomProjectile) && AbilityHelper.instance.canProjectilePenetrateBlock(world, x, y, z, b, e)) {
			return null;
		}
		if (e instanceof EntityLaserPulse && b == ChromaBlocks.LASEREFFECT.getBlockInstance()) {
			return null;
		}
		return def;
	}
	/*
	public static MovingObjectPosition getInterceptedRaytrace(Entity e, Vec3 vec1, Vec3 vec2) {
		return getInterceptedRaytrace(e, vec1, vec2, false, false, false);
	}
	 */
	public static MovingObjectPosition getInterceptedRaytrace(Entity e, Vec3 vec1, Vec3 vec2, boolean b1, boolean b2, boolean b3, MovingObjectPosition def) {
		if (e instanceof IProjectile || e instanceof EntityFireball || e instanceof CustomProjectile) {
			if (AbilityHelper.instance.canProjectilePenetrateBlocks(e)) {
				return AbilityHelper.instance.getProjectileRayTrace(e, vec1, vec2, b1, b2, b3);
			}
		}
		if (e instanceof EntityLaserPulse) {
			return null;
		}
		return def;
	}

	public static TileEntityLootChest generateLootChest(World world, int x, int y, int z, int m, Random rand, String s, int bonus) {
		if (y < 0 || y > 256) {
			ChromatiCraft.logger.logError("Tried to generate a loot chest outside the map!");
			return null;
		}
		world.setBlock(x, y, z, ChromaBlocks.LOOTCHEST.getBlockInstance(), m, 3);
		TileEntityLootChest te = (TileEntityLootChest)world.getTileEntity(x, y, z);
		te.populateChest(s, null, bonus, rand);
		return te;
	}

	public static int groundOpacity(IBlockAccess iba, int x, int y, int z, Block b) {
		if (iba instanceof World) {
			World w = (World)iba;
			if (!(ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.isMystAge(w)) && w.getWorldInfo().getTerrainType() == WorldType.FLAT && ReikaObfuscationHelper.isDeObfEnvironment())
				return b.getLightOpacity();
			if (!ReikaWorldHelper.isChunkPastNoiseGen(w, x >> 4, z >> 4)) {
				return b.getLightOpacity();
			}
		}
		Block b2 = b;
		while(y >= 0 && (b2 == b || b2 == Blocks.dirt || ReikaBlockHelper.isLiquid(b2))) {
			y--;
			b2 = iba.getBlock(x, y, z);
		}
		return b2 == ChromaBlocks.CLIFFSTONE.getBlockInstance() && BlockCliffStone.isTransparent(iba, x, y, z) ? 0 : b.getLightOpacity();
	}

	@SideOnly(Side.CLIENT)
	public static void onIconLoad(TextureAtlasSprite tex) {
		if (tex.getIconName().startsWith("chromaticraft:dimgen/glowleaf")) {
			BlockLightedLeaf.setAnimationData(tex);
		}
		//else if (tex.getIconName().startsWith("chromaticraft:dimgen/glowcave/layer_1")) {
		//	BlockDimensionDeco.setGlowCaveAnimationData(tex);
		//}
	}

	public static void dischargeIntoPlayer(double x, double y, double z, Random rand, EntityLivingBase e, CrystalElement color, float power, float beamSize) {
		if (e.worldObj.isRemote)
			return;
		ChromaAux.doPylonAttack(color, e, e.getHealth()/4F*Math.min(1, 2*power), false);
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.FIREDUMPSHOCK.ordinal(), e.worldObj, (int)x, (int)y, (int)z, 64, color.ordinal(), e.getEntityId(), Float.floatToRawIntBits(beamSize));
		ReikaEntityHelper.knockbackEntityFromPos(x, /*y*/e.posY, z, e, 1.5*Math.min(power*4, 1));
		e.motionY += 0.125+rand.nextDouble()*0.0625;
		CrystalPotionController.instance.applyEffectFromColor((int)(100*MathHelper.clamp_float(power, 0.5F, 18)), (int)(power/2), e, color, false, false, true);
	}

	@SideOnly(Side.CLIENT)
	public static boolean interceptClientChunkUpdates(ChunkProviderClient p) {
		return MonumentCompletionRitual.areRitualsRunning() ? false : p.unloadQueuedChunks(); //contrary to name all this does is update lighting/sky height
	}

	public static void logTileCacheError(World world, WorldLocation loc, TileEntity te, ChromaTiles tile) {
		ChromatiCraft.logger.logError("Incorrect tile ("+te+") @ "+loc+" (with "+loc.getBlockKey(world)+") in "+tile.getName()+" cache!?");
		if (loc.getBlock(world) == tile.getBlock() && loc.getBlockMetadata(world) == tile.getBlockMetadata()) {
			ChromatiCraft.logger.logError("Correct block and meta but no TileEntity!?!?");
		}
	}
	/*
	@SideOnly(Side.CLIENT)
	public static Object getPlayerCoordForF3(Object val) {
		if (val instanceof Number && Minecraft.getMinecraft().theWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			DimensionStructureGenerator struct = ChromaDimensionManager.getStructurePlayerIsIn(Minecraft.getMinecraft().thePlayer);
			if (struct != null && struct.getType() == DimensionStructureType.NONEUCLID)
				val = Minecraft.getMinecraft().theWorld.rand.nextDouble()*10000;
		}
		return val instanceof Number ? ((Number)val).doubleValue() : val;
	}*/
}
