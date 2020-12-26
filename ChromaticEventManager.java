/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.xcompwiz.mystcraft.api.event.LinkEvent;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager.ForceChunkEvent;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;

import Reika.ChromatiCraft.API.Interfaces.CustomEnderDragon;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ChromaTeleporter;
import Reika.ChromatiCraft.Auxiliary.FocusCrystalTrade;
import Reika.ChromatiCraft.Auxiliary.LumenTurretDamage;
import Reika.ChromatiCraft.Auxiliary.PylonDamage;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes.PoolRecipe;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityCrystalBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityLocusPoint;
import Reika.ChromatiCraft.Block.BlockActiveChroma;
import Reika.ChromatiCraft.Block.BlockActiveChroma.TileEntityChroma;
import Reika.ChromatiCraft.Block.BlockFakeSky;
import Reika.ChromatiCraft.Block.Dye.BlockDyeSapling;
import Reika.ChromatiCraft.Block.Dye.BlockRainbowSapling;
import Reika.ChromatiCraft.Block.Worldgen.BlockCliffStone.Variants;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Entity.EntityChromaEnderCrystal;
import Reika.ChromatiCraft.Entity.EntityGlowCloud;
import Reika.ChromatiCraft.Items.ItemFertilitySeed;
import Reika.ChromatiCraft.Items.ItemInfoFragment;
import Reika.ChromatiCraft.Items.Tools.ItemFloatstoneBoots;
import Reika.ChromatiCraft.Items.Tools.ItemInventoryLinker;
import Reika.ChromatiCraft.Items.Tools.Powered.ItemPurifyCrystal;
import Reika.ChromatiCraft.Items.Tools.Powered.ItemSpawnerBypass;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.WarpNetwork;
import Reika.ChromatiCraft.Magic.Artefact.UABombingEffects;
import Reika.ChromatiCraft.Magic.Artefact.UATrades.UATrade;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentAggroMask;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentBossKill;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentDataKeeper;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentPhasingSequence;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentUseRepair;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentWeaponAOE;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.ModInterface.MystPages;
import Reika.ChromatiCraft.ModInterface.Bees.ChromaBeeHelpers;
import Reika.ChromatiCraft.ModInterface.Bees.EfficientFlowerCache;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.ChromaAspectManager;
import Reika.ChromatiCraft.ModInterface.VoidRitual.TileEntityVoidMonsterTrap;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterDestructionRitual;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterDestructionRitual.VoidMonsterRitualDamage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaEnchants;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.TileEntity.TileEntityDataNode;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAIShutdown;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityMultiBuilder;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityChromaLamp;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityCloakingTower;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityCrystalBeacon;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityExplosionShield;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityProtectionUpgrade;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalBroadcaster;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityWirelessSource;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityHeatLily;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityAuraInfuser;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs.GlowingTreeGen;
import Reika.ChromatiCraft.World.BiomeRainbowForest;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionTicker;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.ChromatiCraft.World.Dimension.WorldProviderChroma;
import Reika.ChromatiCraft.World.Dimension.Structure.BridgeGenerator;
import Reika.CritterPet.Interfaces.TamedMob;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ClassDependent;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.AbstractSearch.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.AbstractSearch.TerminationCondition;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BreadthFirstSearch;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Event.AttackAggroEvent;
import Reika.DragonAPI.Instantiable.Event.BlockConsumedByFireEvent;
import Reika.DragonAPI.Instantiable.Event.BlockSpreadEvent;
import Reika.DragonAPI.Instantiable.Event.BlockSpreadEvent.BlockDeathEvent;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.Event.BlockTillEvent;
import Reika.DragonAPI.Instantiable.Event.CanSeeSkyEvent;
import Reika.DragonAPI.Instantiable.Event.ChunkPopulationEvent;
import Reika.DragonAPI.Instantiable.Event.EnderAttackTPEvent;
import Reika.DragonAPI.Instantiable.Event.EntityCollisionEvents.CollisionBoxEvent;
import Reika.DragonAPI.Instantiable.Event.EntityCollisionEvents.RaytraceEvent;
import Reika.DragonAPI.Instantiable.Event.EntitySpawnerCheckEvent;
import Reika.DragonAPI.Instantiable.Event.FarmlandTrampleEvent;
import Reika.DragonAPI.Instantiable.Event.FireSpreadEvent;
import Reika.DragonAPI.Instantiable.Event.GenLayerBeachEvent;
import Reika.DragonAPI.Instantiable.Event.GenLayerRiverEvent;
import Reika.DragonAPI.Instantiable.Event.GetPlayerLookEvent;
import Reika.DragonAPI.Instantiable.Event.GrassSustainCropEvent;
import Reika.DragonAPI.Instantiable.Event.HarvestLevelEvent;
import Reika.DragonAPI.Instantiable.Event.IceFreezeEvent;
import Reika.DragonAPI.Instantiable.Event.ItemStackUpdateEvent;
import Reika.DragonAPI.Instantiable.Event.ItemUpdateEvent;
import Reika.DragonAPI.Instantiable.Event.LavaSpawnFireEvent;
import Reika.DragonAPI.Instantiable.Event.LeafDecayEvent;
import Reika.DragonAPI.Instantiable.Event.MobTargetingEvent;
import Reika.DragonAPI.Instantiable.Event.PigZombieAggroSpreadEvent;
import Reika.DragonAPI.Instantiable.Event.PlayerKeepInventoryEvent;
import Reika.DragonAPI.Instantiable.Event.PlayerPlaceBlockEvent;
import Reika.DragonAPI.Instantiable.Event.PlayerSprintEvent;
import Reika.DragonAPI.Instantiable.Event.SetBlockEvent;
import Reika.DragonAPI.Instantiable.Event.SlotEvent.AddToSlotEvent;
import Reika.DragonAPI.Instantiable.Event.SlotEvent.ClickSlotEvent;
import Reika.DragonAPI.Instantiable.Event.SlotEvent.RemoveFromSlotEvent;
import Reika.DragonAPI.Instantiable.Event.SpawnerCheckPlayerEvent;
import Reika.DragonAPI.Instantiable.Event.TileEntityMoveEvent;
import Reika.DragonAPI.Instantiable.Event.VillagerTradeEvent;
import Reika.DragonAPI.Instantiable.Event.Client.SinglePlayerLogoutEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Interfaces.Entity.ClampedDamage;
import Reika.DragonAPI.Interfaces.Item.ActivatedInventoryItem;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.IC2RubberLogHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.ToolPartType;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import Reika.VoidMonster.API.PlayerLookAtVoidMonsterEvent;
import Reika.VoidMonster.API.VoidMonsterEatLightEvent;
import Reika.VoidMonster.Entity.EntityVoidMonster;

import WayofTime.alchemicalWizardry.api.event.TeleposeEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import forestry.api.multiblock.IAlvearyComponent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.monster.EntityWisp;

public class ChromaticEventManager {

	public static final ChromaticEventManager instance = new ChromaticEventManager();

	private final Random rand = new Random();

	private boolean applyingAOE;
	private boolean applyingPhasing;

	public EntityPlayer collectItemPlayer;

	private final HashSet<Coordinate> playerBreakCache = new HashSet();

	private ChromaticEventManager() {

	}

	@SubscribeEvent
	public void rightClickLexicon(ClickSlotEvent evt) {
		if (evt.buttonID == 1 && ChromaItems.HELP.matchWith(evt.getItem())) {
			//evt.player.openGui(ChromatiCraft.instance, ChromaGuis.BOOKEMPTIES.ordinal(), evt.player.worldObj, 0, 0, 0);
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void stopBiggerOakDecay(LeafDecayEvent evt) {
		if (evt.world instanceof World) {
			BiomeGenBase b = ((World)evt.world).getBiomeGenForCoords(evt.xCoord, evt.zCoord);
			if (ChromatiCraft.isEnderForest(b) || BiomeGlowingCliffs.isGlowingCliffs(b)) {
				if (evt.world.getBlock(evt.xCoord, evt.yCoord, evt.zCoord) == ReikaTreeHelper.OAK.getLogID()) {
					if (!this.canBiomeOakDecay((World)evt.world, evt.xCoord, evt.yCoord, evt.zCoord)) {
						evt.setResult(Result.DENY);
					}
				}
			}
		}
	}

	protected boolean canBiomeOakDecay(World world, final int x, final int y, final int z) {
		TerminationCondition t = new TerminationCondition(){

			@Override
			public boolean isValidTerminus(World world, int dx, int dy, int dz) {
				Block b = world.getBlock(dx, dy, dz);
				return b.isWood(world, x, y, z) && ReikaTreeHelper.getTree(b, world.getBlockMetadata(dx, dy, dz)) == ReikaTreeHelper.OAK;
			}
		};

		PropagationCondition c = new PropagationCondition(){

			@Override
			public boolean isValidLocation(World world, int dx, int dy, int dz, Coordinate from) {
				return ReikaTreeHelper.getTree(world.getBlock(dx, dy, dz), world.getBlockMetadata(dx, dy, dz)) == ReikaTreeHelper.OAK;
			}

		};

		BreadthFirstSearch s = new BreadthFirstSearch(x, y, z);
		s.limit = BlockBox.block(x, y, z).expand(7, 15, 7);
		s.depthLimit = 32;
		s.complete(world, c, t);
		return s.getResult().isEmpty();
	}

	/*
	@SubscribeEvent
	public void noLaunchpadFallDamage(LivingFallEvent ev) {
		Coordinate c = new Coordinate(ev.entityLiving.posX, ev.entityLiving.posY-0.5, ev.entityLiving.posZ);
		ChromaTiles te = ChromaTiles.getTile(ev.entityLiving.worldObj, c.xCoord, c.yCoord, c.zCoord);
		boolean flag1 = te == ChromaTiles.LAUNCHPAD;
		boolean flag2 = c.getBlock(ev.entityLiving.worldObj) == ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		if (flag1 || flag2) {
			ev.setCanceled(true);
			ev.distance = 0;
		}
	}*/

	@ModDependent(ModList.VOIDMONSTER)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void makeRitualMonsterNotAttackable(LivingHurtEvent evt) {
		if (VoidMonsterDestructionRitual.isFocusOfActiveRitual(evt.entity)) {
			if (!(evt.source instanceof VoidMonsterRitualDamage)) {
				evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.IC2)
	public void handleRubberLogPlacement(PlayerPlaceBlockEvent evt) {
		ItemStack held = evt.player.getCurrentEquippedItem();
		if (held != null && IC2RubberLogHandler.getInstance().isCrop(evt.block, held.getItemDamage()) && IC2RubberLogHandler.getInstance().isRipeCrop(evt.block, held.getItemDamage())) {
			evt.setCanceled(true);
			Block b = IC2RubberLogHandler.getInstance().logBlock;
			ForgeDirection dir = ReikaDirectionHelper.getFromLookDirection(evt.player, false);
			//ReikaJavaLibrary.pConsole(dir);
			int meta = IC2RubberLogHandler.getInstance().getMeta(dir.getOpposite());
			evt.world.setBlock(evt.xCoord, evt.yCoord, evt.zCoord, b, meta, 3);
			ReikaSoundHelper.playPlaceSound(evt.world, evt.xCoord, evt.yCoord, evt.zCoord, b);
			held.stackSize--;
			if (held.stackSize <= 0)
				held = null;
			evt.player.setCurrentItemOrArmor(0, held);
		}
	}

	@SubscribeEvent
	public void applyIdentityRetention(BlockEvent.BreakEvent evt) {
		if (EnchantmentDataKeeper.handleBreak(evt.world, evt.x, evt.y, evt.z, evt.block, evt.blockMetadata, evt.getPlayer())) {
			evt.setCanceled(true);
			evt.world.setBlock(evt.x, evt.y, evt.z, Blocks.air);
		}
	}

	@SubscribeEvent
	public void noDimWarpMobs(EntityJoinWorldEvent evt) {
		if (evt.world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			if (ChromaDimensionManager.isDisallowedEntity(evt.entity)) {
				evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.VOIDMONSTER)
	public void triggerVoidMonsterTeleport(ExplosionEvent.Start evt) {
		if (evt.explosion.exploder instanceof EntityTNTPrimed && !evt.world.isRemote) {
			if (TileEntityVoidMonsterTrap.handleTNTTrigger(evt.world, evt.explosion.exploder))
				evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.VOIDMONSTER)
	public void preventVoidMonsterRedstoneCrystalTorchEat(VoidMonsterEatLightEvent evt) {
		if (this.isCrystallineRedstoneTorch(evt.world, evt.xCoord, evt.yCoord, evt.zCoord, evt.block, evt.metadata)) {
			evt.setCanceled(true);
		}
	}

	public boolean isCrystallineRedstoneTorch(IBlockAccess world, int x, int y, int z, Block b, int meta) {
		if (b == Blocks.redstone_torch || b == Blocks.unlit_redstone_torch) {
			if (meta == 5) {
				Block b2 = world.getBlock(x, y-1, z);
				if (b2 == ChromaBlocks.PYLONSTRUCT.getBlockInstance() || b2 instanceof BlockStructureShield) {
					return true;
				}
			}
		}
		return false;
	}

	@SubscribeEvent
	public void overrideCollision(CollisionBoxEvent evt) {
		evt.box = ChromaAux.getInterceptedCollisionBox(evt.entity, evt.world, evt.xCoord, evt.yCoord, evt.zCoord, evt.box);
	}

	@SubscribeEvent
	public void overrideRaytrace(RaytraceEvent evt) {
		evt.result = ChromaAux.getInterceptedRaytrace(evt.entity, evt.pos1, evt.pos2, evt.flag1, evt.flag2, evt.flag3, evt.result);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void delegateEnchantGui(PlayerInteractEvent evt) {
		if (evt.entityPlayer.isSneaking())
			return;
		if (ChromaItems.BUCKET.matchWith(evt.entityPlayer.getCurrentEquippedItem()))
			return;
		if (evt.entityPlayer.getCurrentEquippedItem() != null && evt.entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock)
			return;
		if (evt.action == Action.RIGHT_CLICK_BLOCK) {
			if (ChromaTiles.getTile(evt.world, evt.x, evt.y-1, evt.z) == ChromaTiles.ENCHANTER) {
				if (((TileEntityAutoEnchanter)evt.world.getTileEntity(evt.x, evt.y-1, evt.z)).isAssisted()) {
					ChromaTiles.ENCHANTER.getBlock().onBlockActivated(evt.world, evt.x, evt.y-1, evt.z, evt.entityPlayer, evt.face, 0, 0, 0);
					evt.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void lockT2EnderEyes(EntityItemPickupEvent evt) {
		if (evt.item.getEntityItem().getItem() == ChromaItems.ENDEREYE.getItemInstance()) {
			NBTTagCompound tag = evt.item.getEntityItem().stackTagCompound;
			if (tag != null && tag.hasKey("owner")) {
				UUID uid = UUID.fromString(tag.getString("owner"));
				if (!uid.equals(evt.entityPlayer.getPersistentID()))
					evt.setCanceled(true);
			}
		}
	}
	/*
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void clampGlowCloudCount(WorldEvent.PotentialSpawns evt) {

	}
	 */
	@SubscribeEvent
	public void toggleSpawners(SpawnerCheckPlayerEvent evt) {
		if (ItemSpawnerBypass.isActive(evt.player)) {
			RayTracer rt = RayTracer.getVisualLOS();
			rt.addTransparentBlock(Blocks.mob_spawner);
			rt.addTransparentBlock(Blocks.web);
			rt.setOrigins(evt.spawner.getSpawnerX()+0.5, evt.spawner.getSpawnerY()+0.5, evt.spawner.getSpawnerZ()+0.5, evt.player.posX, evt.player.posY, evt.player.posZ);
			if (evt.player.getDistanceSq(evt.spawner.getSpawnerX()+0.5, evt.spawner.getSpawnerY()+0.5, evt.spawner.getSpawnerZ()+0.5) <= evt.spawner.activatingRangeFromPlayer*evt.spawner.activatingRangeFromPlayer)
				evt.player.getEntityData().setLong("spawnerpass", evt.player.worldObj.getTotalWorldTime());
			if (!rt.isClearLineOfSight(evt.player.worldObj)) {
				evt.setResult(Result.DENY);
			}
		}
	}

	@ModDependent(ModList.FORESTRY)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void resyncAlvearies(PlayerInteractEvent evt) {
		if (evt.action == Action.RIGHT_CLICK_BLOCK) {
			TileEntity te = evt.world.getTileEntity(evt.x, evt.y, evt.z);
			if (te instanceof IAlvearyComponent && !(te instanceof TileEntityLumenAlveary)) {
				IAlvearyComponent iae = (IAlvearyComponent)te;
				try {
					TileEntityLumenAlveary te2 = ChromaBeeHelpers.getLumenAlvearyController(iae.getMultiblockLogic().getController(), evt.world, iae.getCoordinates());
					if (te2 != null) {
						EfficientFlowerCache eff = te2.getFlowerCache();
						if (eff != null) {
							eff.forceUpdate(te2);
						}
						te2.syncAllData(true);
					}
				}
				catch (AbstractMethodError e) {
					String s = "Cannot fetch multiblock logic for alveary part "+iae+"; it is using an old verison of the API! This is a bug in its mod!";
					ChromatiCraft.logger.log(s);
					ReikaChatHelper.write("Error processing "+iae+"; it is using an outdated API. This is a bug in that mod.");
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void growGlowcliffsTrees(PlayerInteractEvent evt) {
		if (evt.action == Action.RIGHT_CLICK_BLOCK && !evt.world.isRemote) {
			if (BiomeGlowingCliffs.isGlowingCliffs(evt.world.getBiomeGenForCoords(evt.x, evt.z))) {
				if (evt.world.getBlock(evt.x, evt.y, evt.z) == Blocks.sapling && evt.world.getBlockMetadata(evt.x, evt.y, evt.z)%8 == 0) {
					ItemStack is = evt.entityPlayer.getCurrentEquippedItem();
					if (is != null && is.getItem() == Items.glowstone_dust && rand.nextInt(3) == 0) {
						evt.world.setBlock(evt.x, evt.y, evt.z, Blocks.air);
						WorldGenAbstractTree tree = ChromatiCraft.glowingcliffs.getUndergroundTreeGen(rand, true, 12); //default chance is 40
						((GlowingTreeGen)tree).setGlowChance(10);
						tree.setScale(1.0D, 1.0D, 1.0D);
						int n = 8;
						boolean flag = tree.generate(evt.world, rand, evt.x, evt.y, evt.z);
						while (!flag && n <= 8) {
							flag = tree.generate(evt.world, rand, evt.x, evt.y, evt.z);
							n++;
						}
						if (flag) {
							tree.func_150524_b(evt.world, rand, evt.x, evt.y, evt.z);
						}
						is.stackSize--;
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void interceptChunkPopulation(ChunkPopulationEvent evt) {
		if (evt.world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			((WorldProviderChroma)evt.world.provider).getChunkGenerator().onPopulationHook(evt.generator, evt.loader, evt.chunkX, evt.chunkZ);
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void updateFakeSkyBlocks(SetBlockEvent.Post evt) {
		if (!evt.isWorldgen)
			BlockFakeSky.updateColumn(evt.world, evt.xCoord, evt.yCoord, evt.zCoord);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void applyFakeSkyBlocks(CanSeeSkyEvent evt) {
		if (BlockFakeSky.isForcedSky(evt.world, evt.xCoord, evt.yCoord, evt.zCoord))
			evt.setResult(Result.ALLOW);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void removeMetaAlloy(BlockEvent.BreakEvent evt) {
		if (!evt.world.isRemote && evt.world.provider.dimensionId == 0 && evt.block == ChromaBlocks.METAALLOYLAMP.getBlockInstance()) {
			TileEntityDataNode.removeMetaAlloy(evt.world, evt.x, evt.y, evt.z);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void preventSomeGrassBreakInDimension(BreakSpeed evt) {
		if (evt.entityPlayer.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			if (WorldProviderChroma.isUnbreakableTerrain(evt.entityPlayer.worldObj, evt.x, evt.y, evt.z)) {
				evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void preventSomeGrassBreakInDimension(ExplosionEvent.Detonate evt) {
		if (evt.world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			Iterator<ChunkPosition> it = evt.explosion.affectedBlockPositions.iterator();
			while (it.hasNext()) {
				ChunkPosition p = it.next();
				if (WorldProviderChroma.isUnbreakableTerrain(evt.world, p.chunkPosX, p.chunkPosY, p.chunkPosZ)) {
					it.remove();
				}
			}
		}
	}

	/*
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void voxelBuild(PlayerPlaceBlockEvent evt) {
		if (Chromabilities.VOXELPLACE.enabledOn(evt.player) && !evt.block.hasTileEntity(evt.metadata)) {
			double r = 3.5;
			for (int i = MathHelper.floor_double(-r); i <= MathHelper.ceiling_double_int(r); i++) {
				for (int j = MathHelper.floor_double(-r); j <= MathHelper.ceiling_double_int(r); j++) {
					for (int k = MathHelper.floor_double(-r); k <= MathHelper.ceiling_double_int(r); k++) {
						if (ReikaMathLibrary.py3d(i, j, k) <= r) {
							int dx = evt.xCoord+i;
							int dy = evt.yCoord+j;
							int dz = evt.zCoord+k;
							ItemStack is = evt.player.getCurrentEquippedItem();
							if (is != null && is.stackSize > 0 && evt.world.getBlock(dx, dy, dz).isAir(evt.world, dx, dy, dz)) {
								evt.world.setBlock(dx, dy, dz, evt.block, evt.metadata, 3);
								if (!evt.player.capabilities.isCreativeMode)
									is.stackSize--;
							}
						}
					}
				}
			}
		}
	}*/

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void multiBuild(PlayerPlaceBlockEvent evt) {
		TileEntityMultiBuilder.placeBlock(evt.world, evt.xCoord, evt.yCoord, evt.zCoord, evt.block, evt.metadata, evt.player, evt.player.getCurrentEquippedItem());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void multiBuild(BlockEvent.BreakEvent evt) {
		TileEntityMultiBuilder.breakBlock(evt.world, evt.x, evt.y, evt.z, evt.block, evt.blockMetadata, evt.getPlayer());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void clearCachedTiles(SinglePlayerLogoutEvent evt) {
		TileEntityItemCollector.clearCache();
		TileEntityLocusPoint.clearCache();
		TileEntityLampController.clearCache();
		TileEntityChromaLamp.clearCache();
		TileEntityCloakingTower.clearCache();
		TileEntityCrystalBeacon.clearCache();
		TileEntityMultiBuilder.clearCache();
		TileEntityExplosionShield.clearCache();
		TileEntityWirelessSource.clearCache();
		TileEntityVoidMonsterTrap.clearCache();
		TileEntityAuraInfuser.clearCache();
		BlockFakeSky.clearCache();
		LoreManager.instance.clearOnLogout();
		WarpNetwork.instance.clear();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void clearCachedTiles(ClientDisconnectionFromServerEvent evt) {
		TileEntityItemCollector.clearCache();
		TileEntityLocusPoint.clearCache();
		TileEntityLampController.clearCache();
		TileEntityChromaLamp.clearCache();
		TileEntityCloakingTower.clearCache();
		TileEntityCrystalBeacon.clearCache();
		TileEntityMultiBuilder.clearCache();
		TileEntityExplosionShield.clearCache();
		TileEntityVoidMonsterTrap.clearCache();
		TileEntityAuraInfuser.clearCache();
		BlockFakeSky.clearCache();
		LoreManager.instance.clearOnLogout();
		WarpNetwork.instance.clear();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void keepLumaFogsNatural(LivingSpawnEvent.SpecialSpawn evt) {
		if (evt.entityLiving instanceof EntityGlowCloud) {
			evt.setCanceled(true); //prevent onSpawnWithEgg call
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void preventFertilitySeedReuse(EntityItemPickupEvent evt) {
		if (evt.item.getEntityItem().getItem() == ChromaItems.FERTILITYSEED.getItemInstance()) {
			if (evt.item.age >= ItemFertilitySeed.INITIAL_DELAY)
				evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void reloadBroacastAirCache(SetBlockEvent.Post evt) {
		if (!evt.isWorldgen)
			TileEntityCrystalBroadcaster.updateAirCaches(evt.world, evt.xCoord, evt.yCoord, evt.zCoord);
	}

	/*
	@SubscribeEvent
	public void preventCliffShadows(LightCalculationEvent evt) {
		//if (BiomeGlowingCliffs.isGlowingCliffs(evt.world.getBiomeGenForCoords(evt.x, evt.z))) {
		//	evt.setCanceled(true);
		//}
		ImmutablePair<Integer, Integer> val = GlowingCliffsAuxGenerator.TEMP_ISLAND_CACHE.get(new Coordinate(evt.x, 0, evt.z));
		if (val != null) {
			if (evt.y > /*val.left*//*evt.world.getTopSolidOrLiquidBlock(evt.x, evt.z) && evt.y < val.right) {
				//evt.setCanceled(true);
			}
		}
	}
			 */

	@SubscribeEvent
	public void allowCliffGrassCrops(GrassSustainCropEvent evt) {
		if (BiomeGlowingCliffs.isGlowingCliffs(evt.world.getBiomeGenForCoords(evt.xCoord, evt.zCoord))) {
			evt.setResult(Result.ALLOW);
		}
	}

	@SubscribeEvent
	public void createCliffFarmland(BlockTillEvent evt) {
		if (BiomeGlowingCliffs.isGlowingCliffs(evt.world.getBiomeGenForCoords(evt.x, evt.z))) {
			//ReikaJavaLibrary.pConsole(evt.x+","+evt.y+","+evt.z+": "+evt.world.getBlock(evt.x, evt.y+1, evt.z).isOpaqueCube());
			evt.tilledBlock = ChromaBlocks.CLIFFSTONE.getBlockInstance();
			evt.tilledMeta = Variants.FARMLAND.getMeta(false, false);
		}
	}

	@SubscribeEvent
	public void preventCliffStackedGrass(BlockDeathEvent evt) {
		if (evt.getClass() != BlockDeathEvent.class)
			return;
		if (evt.world.getBlock(evt.xCoord, evt.yCoord+1, evt.zCoord).isOpaqueCube()) {
			if (BiomeGlowingCliffs.isGlowingCliffs(evt.world.getBiomeGenForCoords(evt.xCoord, evt.zCoord)) || evt.world.getBlock(evt.xCoord, evt.yCoord-1, evt.zCoord) == ChromaBlocks.CLIFFSTONE.getBlockInstance()) {
				//ReikaJavaLibrary.pConsole(evt.x+","+evt.y+","+evt.z+": "+evt.world.getBlock(evt.x, evt.y+1, evt.z).isOpaqueCube());
				evt.setResult(Result.ALLOW);
			}
		}
	}

	@SubscribeEvent
	public void preventCliffStackedGrass(BlockSpreadEvent evt) {
		if (evt.getClass() != BlockSpreadEvent.class)
			return;
		if (evt.world.getBlock(evt.xCoord, evt.yCoord+1, evt.zCoord).isOpaqueCube()) {
			if (BiomeGlowingCliffs.isGlowingCliffs(evt.world.getBiomeGenForCoords(evt.xCoord, evt.zCoord)) || evt.world.getBlock(evt.xCoord, evt.yCoord-1, evt.zCoord) == ChromaBlocks.CLIFFSTONE.getBlockInstance()) {
				//ReikaJavaLibrary.pConsole(evt.x+","+evt.y+","+evt.z+": "+evt.world.getBlock(evt.x, evt.y+1, evt.z).isOpaqueCube());
				evt.setResult(Result.DENY);
			}
		}
	}

	@SubscribeEvent
	public void preventCliffFire(LavaSpawnFireEvent evt) {
		if (BiomeGlowingCliffs.isGlowingCliffs(evt.world.getBiomeGenForCoords(evt.x, evt.z))) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void preventCliffFire(FireSpreadEvent evt) {
		if (BiomeGlowingCliffs.isGlowingCliffs(evt.world.getBiomeGenForCoords(evt.xCoord, evt.zCoord))) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void preventCliffBeaches(GenLayerBeachEvent evt) {
		if (evt.originalBiomeID.biomeID == ExtraChromaIDs.LUMINOUSCLIFFS.getValue()) {
			evt.beachIDToPlace = ExtraChromaIDs.LUMINOUSEDGE.getValue();
		}
	}

	@SubscribeEvent
	public void preventCliffRivers(GenLayerRiverEvent evt) {
		if (evt.originalBiomeID == ExtraChromaIDs.LUMINOUSCLIFFS.getValue() || evt.originalBiomeID == ExtraChromaIDs.LUMINOUSEDGE.getValue()) {
			evt.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void blendCliffEdgesAndShapeCliffs(ChunkProviderEvent.ReplaceBiomeBlocks evt) {
		if (evt.world != null && evt.blockArray != null) {
			BiomeGlowingCliffs.blendTerrainEdgesAndGenCliffs(evt.world, evt.chunkX, evt.chunkZ, evt.blockArray, evt.metaArray);
		}
	}

	@SubscribeEvent
	public void changeLightSpawnCurve(LivingSpawnEvent.CheckSpawn evt) {
		int x = MathHelper.floor_double(evt.x);
		int z = MathHelper.floor_double(evt.z);
		if (BiomeGlowingCliffs.isGlowingCliffs(evt.world.getBiomeGenForCoords(x, z))) {
			if (evt.entityLiving instanceof EntityCreeper)
				evt.setResult(Result.DENY);
			else if (evt.entityLiving instanceof EntitySkeleton || evt.entityLiving instanceof EntitySpider || evt.entityLiving instanceof EntityZombie) {
				int y = MathHelper.floor_double(evt.entityLiving.boundingBox.minY);
				float block = evt.world.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
				float sky = evt.world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z)*Math.max(0, 1-Math.min(evt.world.skylightSubtracted, 8)/8F);
				if (sky > 4)
					evt.setResult(Result.DENY);
				float c = block*1.25F+sky*2F;
				if (c >= 7 || rand.nextInt(7) < c)
					evt.setResult(Result.DENY);
			}
		}
	}

	@SubscribeEvent
	public void preventCliffCreepers(LivingSpawnEvent evt) {
		if (BiomeGlowingCliffs.isGlowingCliffs(evt.world.getBiomeGenForCoords(MathHelper.floor_double(evt.x), MathHelper.floor_double(evt.z)))) {
			if (evt.entityLiving instanceof EntityCreeper)
				evt.entityLiving.setDead();
		}
	}

	@SubscribeEvent
	public void preventCliffsFreeze(IceFreezeEvent evt) {
		if (BiomeGlowingCliffs.isGlowingCliffs(evt.world.getBiomeGenForCoords(evt.xCoord, evt.zCoord))) {
			evt.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void buyUnknownArtefact(VillagerTradeEvent evt) {
		if (evt.trade instanceof UATrade) {
			if (ReikaRandomHelper.doWithChance(UABombingEffects.TRADE_BOMBING_CHANCE))
				UABombingEffects.instance.trigger((Entity)evt.villager);
			if (evt.villager instanceof EntityVillager) {
				EntityVillager ev = (EntityVillager)evt.villager;
				ev.setRevengeTarget(evt.entityPlayer);
			}
		}
	}

	@SubscribeEvent
	public void buyFocusCrystals(VillagerTradeEvent evt) {
		//ReikaJavaLibrary.pConsole(evt.trade);
		if (evt.trade instanceof FocusCrystalTrade) {
			ProgressStage.FOCUSCRYSTAL.stepPlayerTo(evt.entityPlayer);
		}
	}

	@SubscribeEvent
	public void onAddArmor(AddToSlotEvent evt) {
		int id = evt.slotID;
		if (evt.inventory instanceof InventoryPlayer && evt.slotID == 36) { //foot armor
			ItemStack pre = evt.getPreviousItem();
			if (pre != null && ItemFloatstoneBoots.isFloatBoots(pre)) {
				ItemStack is = evt.getItem();
				if (is == null || !ItemFloatstoneBoots.isFloatBoots(is)) {
					if (!((InventoryPlayer)evt.inventory).player.capabilities.isCreativeMode) {
						((InventoryPlayer)evt.inventory).player.capabilities.allowFlying = false;
						((InventoryPlayer)evt.inventory).player.capabilities.isFlying = false;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onRemoveArmor(RemoveFromSlotEvent evt) {
		int id = evt.slotID;
		if (evt.slotID == 36) { //foot armor
			ItemStack is = evt.getItem();
			if (is != null && ItemFloatstoneBoots.isFloatBoots(is)) {
				if (!evt.player.capabilities.isCreativeMode) {
					evt.player.capabilities.allowFlying = false;
					evt.player.capabilities.isFlying = false;
				}
			}
		}
	}

	@SubscribeEvent
	public void noFloatstoneTrample(FarmlandTrampleEvent ev) {
		if (ev.entity instanceof EntityLivingBase) {
			ItemStack boots = ((EntityLivingBase)ev.entity).getEquipmentInSlot(1);
			if (boots != null && ItemFloatstoneBoots.isFloatBoots(boots)) {
				ev.setResult(Result.DENY);
			}
		}
	}

	@SubscribeEvent
	public void noFloatstoneFall(LivingFallEvent ev) {
		ItemStack boots = ev.entityLiving.getEquipmentInSlot(1);
		if (boots != null && ItemFloatstoneBoots.isFloatBoots(boots)) {
			ev.setCanceled(true);
			ev.distance = 0;
		}
	}

	@SubscribeEvent
	public void sinkingEnchantment(LivingUpdateEvent ev) {
		ItemStack boots = ev.entityLiving.getEquipmentInSlot(1);
		if (boots != null) {
			if (ReikaEnchantmentHelper.hasEnchantment(ChromaEnchants.FASTSINK.getEnchantment(), boots)) {
				if (!ev.entityLiving.onGround && ev.entityLiving.handleWaterMovement() && ev.entityLiving.motionY < 0) {
					ev.entityLiving.motionY -= 0.0625;
					ev.entityLiving.velocityChanged = true;
				}
			}
		}
	}

	@SubscribeEvent
	public void boostHarvestLevel(HarvestLevelEvent ev) {
		int level = ChromaEnchants.HARVESTLEVEL.getLevel(ev.getItem());
		if (level > 0) {
			ev.harvestLevel += level;
		}
	}

	@SubscribeEvent
	public void autoCollectDirect(EntityJoinWorldEvent evt) {
		if (collectItemPlayer != null && evt.entity instanceof EntityItem && !ReikaInventoryHelper.isFull(collectItemPlayer.inventory)) {
			EntityItem ei = (EntityItem)evt.entity;
			if (!MinecraftForge.EVENT_BUS.post(new EntityItemPickupEvent(collectItemPlayer, ei)))
				ReikaPlayerAPI.addOrDropItem(ei.getEntityItem(), collectItemPlayer);
			collectItemPlayer = null;
		}
	}

	@SubscribeEvent
	public void autoCollectPre(BlockEvent.BreakEvent evt) {
		playerBreakCache.add(new Coordinate(evt.x, evt.y, evt.z));
	}

	@SubscribeEvent
	public void autoCollectPost(HarvestDropsEvent evt) {
		EntityPlayer ep = evt.harvester;
		if (ep != null && !ReikaPlayerAPI.isFake(ep) && playerBreakCache.contains(new Coordinate(evt.x, evt.y, evt.z))) {
			ItemStack tool = ep.getCurrentEquippedItem();
			int level = ChromaEnchants.AUTOCOLLECT.getLevel(tool);
			if (level > 0) {
				for (ItemStack is : evt.drops) {
					if (!MinecraftForge.EVENT_BUS.post(new EntityItemPickupEvent(ep, new EntityItem(ep.worldObj, ep.posX, ep.posY, ep.posZ, is))))
						ReikaPlayerAPI.addOrDropItem(is, ep);
				}
				ReikaSoundHelper.playSoundAtEntity(ep.worldObj, ep, "random.pop", 0.25F+0.25F*rand.nextFloat(), ((rand.nextFloat()-rand.nextFloat())*0.7F+1)*2);
				evt.drops.clear();
			}
		}
		playerBreakCache.clear();
	}

	@SubscribeEvent
	public void rareLootBoost(LivingDropsEvent ev) {
		if (ev.source.getEntity() instanceof EntityLivingBase) {
			EntityLivingBase src = (EntityLivingBase)ev.source.getEntity();
			ItemStack is = src.getHeldItem();
			if (is != null) {
				int level = ChromaEnchants.RARELOOT.getLevel(is);
				if (level > 0) {
					EntityLivingBase e = ev.entityLiving;
					ArrayList<EntityItem> li = ev.drops;
					e.captureDrops = true;
					int sum = 100+100*level*level;
					ReikaObfuscationHelper.invoke("dropEquipment", e, true, sum);
					if (rand.nextInt(sum) >= 100) {
						ReikaObfuscationHelper.invoke("dropRareDrop", e, 1);
						if (rand.nextBoolean()) {
							ReikaEntityHelper.dropHead(e);
						}
					}
					e.captureDrops = false;
				}
			}
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.TINKERER)
	public void chromastoneTools(ItemStackUpdateEvent evt) {
		if (evt.held && !(evt.holder instanceof EntityPlayerMP && (ReikaPlayerAPI.isFake((EntityPlayer)evt.holder) || ((EntityPlayerMP)evt.holder).theItemInWorldManager.isDestroyingBlock))) {
			if (InterfaceCache.TINKERTOOL.instanceOf(evt.item.getItem())) {
				NBTTagCompound tags = evt.item.getTagCompound().getCompoundTag("InfiTool");
				if (tags == null)
					return;
				boolean allMats = true;
				if (!tags.getBoolean("Broken")) {
					for (int i = 0; i < ToolPartType.types.length; i++) {
						int mat = TinkerToolHandler.getInstance().getToolMaterial(evt.item, ToolPartType.types[i]);
						if (mat == ExtraChromaIDs.CHROMAMATID.getValue()) {
							if (rand.nextInt(4) == 0) {
								int dmg = tags.getInteger("Damage");
								if (dmg > 0) {
									dmg--;
								}
								tags.setInteger("Damage", dmg);
							}
						}
						else {
							allMats = false;
						}
					}
					if (allMats) {
						int[] arr = new int[]{450, 5};
						tags.setTag("Lapis", new NBTTagIntArray(arr));
						ReikaEnchantmentHelper.addEnchantment(evt.item.stackTagCompound, Enchantment.fortune, 5, false);
						ReikaEnchantmentHelper.addEnchantment(evt.item.stackTagCompound, Enchantment.looting, 5, false);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void bridgeBlockInteract(PlayerInteractEvent evt) {
		if (ChromaDimensionManager.getStructurePlayerIsIn(evt.entityPlayer) instanceof BridgeGenerator) {
			if (evt.action == Action.RIGHT_CLICK_BLOCK) {
				if (evt.world.getBlock(evt.x, evt.y, evt.z) == ChromaBlocks.BRIDGECONTROL.getBlockInstance()) {
					return;
				}
			}
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void dioramaSpawners(EntitySpawnerCheckEvent evt) {
		if (evt.entityLiving instanceof EntityGhast && evt.logic.getSpawnerY() > 129) {
			if (evt.entityLiving.worldObj.getCollidingBoundingBoxes(evt.entityLiving, evt.entityLiving.boundingBox).isEmpty())
				evt.setResult(Result.ALLOW);
		}
	}

	@SubscribeEvent
	public void banDimensionSpells(EntityJoinWorldEvent evt) {
		if (evt.world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			Entity e = evt.entity;
			String n = e.getClass().getName();
			if (n.equals("am2.entities.EntitySpellProjectile") || n.equals("am2.entities.EntitySpellEffect")) {
				ChromaSounds.FAIL.playSound(e, 1, 1);
				ReikaPacketHelper.sendPositionPacket(ChromatiCraft.packetChannel, ChromaPackets.SPELLFAIL.ordinal(), e, new PacketTarget.RadiusTarget(e, 32));
				evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void banDimensionBlocks(PlayerPlaceBlockEvent evt) {
		if (evt.world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			Block b = evt.block;
			int meta = evt.metadata;
			if (ChromaDimensionManager.isBannedDimensionBlock(b, meta)) {
				evt.setCanceled(true);
				ChromaDimensionManager.punishCheatingPlayer(evt.player);
			}
		}
	}

	@SubscribeEvent //fallback
	public void banDimensionBlocks(SetBlockEvent.Post evt) {
		if (evt.world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue() && !evt.isWorldgen) {
			Block b = evt.getBlock();
			int meta = evt.getMetadata();
			if (ChromaDimensionManager.isBannedDimensionBlock(b, meta)) {
				//ArrayList<ItemStack> li = b.getDrops(evt.world, evt.xCoord, evt.yCoord, evt.zCoord, meta, 0);
				evt.world.setBlock(evt.xCoord, evt.yCoord, evt.zCoord, Blocks.air);
				//ReikaItemHelper.dropItems(evt.world, evt.xCoord+0.5, evt.yCoord+0.5, evt.zCoord+0.5, li);
				ReikaSoundHelper.playSoundAtBlock(evt.world, evt.xCoord, evt.yCoord, evt.zCoord, "random.explode");
				ReikaParticleHelper.EXPLODE.spawnAroundBlock(evt.world, evt.xCoord, evt.yCoord, evt.zCoord, 2);
				List<EntityPlayer> li = evt.world.getEntitiesWithinAABB(EntityPlayer.class, ReikaAABBHelper.getBlockAABB(evt.xCoord, evt.yCoord, evt.zCoord).expand(6, 4.5, 6));
				for (EntityPlayer ep : li) {
					ChromaDimensionManager.punishCheatingPlayer(ep);
				}
			}
		}
	}

	@SubscribeEvent
	public void showLiedParticles(LivingUpdateEvent evt) {
		if (evt.entityLiving instanceof EntityCreature && !evt.entityLiving.worldObj.isRemote) {
			EntityCreature ec = (EntityCreature)evt.entityLiving;
			Entity tgt = ec.getEntityToAttack();
			if (tgt instanceof EntityPlayer) {
				EntityPlayer ep = (EntityPlayer)tgt;
				if (Chromabilities.COMMUNICATE.enabledOn(ep)) {
					if (!AbilityHelper.instance.isPeaceActive(ep)) {
						if (rand.nextInt(6) == 0)
							ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.PARTICLE.ordinal(), ec.worldObj, MathHelper.floor_double(ec.posX), (int)ec.posY+1, MathHelper.floor_double(ec.posZ), new PacketTarget.RadiusTarget(ec, 24), ReikaJavaLibrary.makeListFrom(ReikaParticleHelper.ANGRY.ordinal(), 1));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void noTargeting(LivingSetAttackTargetEvent evt) {
		if (!ReikaEntityHelper.tameMobTargeting) {
			if (evt.target instanceof EntityPlayer) {
				EntityPlayer ep = (EntityPlayer)evt.target;
				if (evt.entityLiving instanceof EntityLiving) {
					if (this.isPlayerNotTargetable(ep, evt.entityLiving.posX, evt.entityLiving.posY, evt.entityLiving.posZ)) {
						//evt.setCanceled(true);
						((EntityLiving)evt.entityLiving).setAttackTarget(null);
					}
				}
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void cloakPlayers(MobTargetingEvent.Pre evt) {
		if (evt.world.isRemote)
			return;
		if (this.isPlayerNotTargetable(evt.player, evt.x, evt.y, evt.z)) {
			evt.setResult(Result.DENY);
		}
	}

	private boolean isPlayerNotTargetable(EntityPlayer ep, double x, double y, double z) {
		if (Chromabilities.COMMUNICATE.enabledOn(ep)) {
			//evt.setCanceled(true);
			return AbilityHelper.instance.isPeaceActive(ep);
		}
		else if (TileEntityCloakingTower.isPlayerCloaked(ep)) {
			if (ep.getDistanceSq(x, y, z) >= 4) {
				//evt.setCanceled(true);
				return true;
			}
		}
		return false;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void applyCorruptedAura(BlockTickEvent evt) {
		if (ModList.MYSTCRAFT.isLoaded() && MystPages.Pages.CORRUPTED.existsInWorld(evt.world) && ReikaRandomHelper.doWithChance(10)) {
			BiomeGenBase b = evt.world.getBiomeGenForCoords(evt.xCoord, evt.zCoord);
			if (ChromatiCraft.isRainbowForest(b)) {
				BiomeGenBase b2 = BiomeGenBase.desert;
				if (ModList.THAUMCRAFT.isLoaded()) {
					int id = ThaumIDHandler.Biomes.TAINT.getID();
					if (id >= 0)
						b2 = BiomeGenBase.biomeList[id];
				}
				ReikaWorldHelper.setBiomeForXZ(evt.world, evt.xCoord, evt.zCoord, b2);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void applyNoclipDanagePrevention(LivingAttackEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer && Chromabilities.ORECLIP.enabledOn((EntityPlayer)evt.entityLiving)) {
			if (evt.source == DamageSource.inWall || evt.source == DamageSource.cactus || evt.source == DamageSource.inFire)
				evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void applyNoclipRaytrace(GetPlayerLookEvent evt) {
		if (Chromabilities.ORECLIP.enabledOn(evt.entityPlayer)) {
			MovingObjectPosition hit = AbilityHelper.instance.doOreClipRayTrace(evt.entityPlayer.worldObj, evt.playerVec, evt.auxVec, false);
			evt.newLook = hit != null && hit.typeOfHit == MovingObjectType.BLOCK ? hit : new MovingObjectPosition(0, 0, 0, 0, Vec3.createVectorHelper(0, 0, 0), false);//new MovingObjectPosition(ep);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void applyNoclipFallProtection(LivingFallEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer && Chromabilities.ORECLIP.enabledOn((EntityPlayer)evt.entityLiving)) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void applyDimensionFallProtection(LivingFallEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer && evt.entityLiving.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			if (evt.entityLiving.ticksExisted < 1200) {
				evt.setCanceled(true);
			}
			else {
				evt.distance = Math.min(8, (Math.min(evt.distance, Math.max(1, 0.5F*MathHelper.sqrt_float(evt.distance)))));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void protectDimDeath(LivingDeathEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer && evt.entityLiving.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			evt.entityLiving.setHealth(1);
			evt.setCanceled(true);
			ReikaEntityHelper.transferEntityToDimension(evt.entityLiving, 0, new ChromaTeleporter(0));
			ElementTagCompound tag = PlayerElementBuffer.instance.getPlayerBuffer((EntityPlayer)evt.entityLiving);
			for (CrystalElement e : new ArrayList<CrystalElement>(tag.elementSet())) {
				float f = (float)ReikaRandomHelper.getRandomBetween(0.4, 0.9);
				tag.setTag(e, Math.max(1, (int)(tag.getValue(e)*f)));
			}
			PlayerElementBuffer.instance.removeFromPlayer((EntityPlayer)evt.entityLiving, tag);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void applyKeepInv(PlayerKeepInventoryEvent evt) {
		//String tag = "cc_keepinv";
		//NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(evt.entityPlayer);
		if (Chromabilities.KEEPINV.enabledOn(evt.entityPlayer)) {
			//nbt.setBoolean(tag, true);
			evt.setResult(Result.ALLOW);
		}
		/*
		else if (nbt.getBoolean(tag)) {
			//nbt.setBoolean(tag, false);
			//evt.setResult(Result.ALLOW);
		}
		 */
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void stopPearlGlitch(EnderTeleportEvent evt) {
		if (evt.entityLiving.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
			evt.setCanceled(true);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void applyAggroMask(PigZombieAggroSpreadEvent evt) {
		DamageSource src = evt.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			EntityLivingBase mob = evt.entityLiving;
			ItemStack weapon = ep.getCurrentEquippedItem();
			if (weapon != null) {
				int level = ChromaEnchants.AGGROMASK.getLevel(weapon);
				if (level > 0 && EnchantmentAggroMask.hidePigmanSpreadDamage(level)) {
					evt.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void applyAggroMask(AttackAggroEvent evt) {
		DamageSource src = evt.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			EntityLivingBase mob = evt.entityLiving;
			ItemStack weapon = ep.getCurrentEquippedItem();
			if (weapon != null) {
				int level = ChromaEnchants.AGGROMASK.getLevel(weapon);
				if (level > 0 && EnchantmentAggroMask.hideDirectDamage(level)) {
					evt.setResult(Result.DENY);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void applyEnderLock(EnderAttackTPEvent evt) {
		DamageSource src = evt.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			EntityLivingBase mob = evt.entityLiving;
			ItemStack weapon = ep.getCurrentEquippedItem();
			if (weapon != null) {
				int level = ChromaEnchants.ENDERLOCK.getLevel(weapon);
				if (level > 0) {
					evt.setResult(Result.DENY);
				}
			}
		}
	}

	@SubscribeEvent
	public void applyUseRepair(LivingHurtEvent evt) {
		DamageSource src = evt.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			EntityLivingBase mob = evt.entityLiving;
			ItemStack weapon = ep.getCurrentEquippedItem();
			if (weapon != null) {
				int level = ChromaEnchants.USEREPAIR.getLevel(weapon);
				if (level > 0) {
					int rep = EnchantmentUseRepair.getRepairedDurability(weapon, level, evt.ammount);
					weapon.setItemDamage(weapon.getItemDamage()-rep);
					evt.ammount *= Math.pow(0.9875, rep);
				}
			}
		}
	}

	@SubscribeEvent
	public void applyBossKill(LivingAttackEvent evt) {
		DamageSource src = evt.source;
		if (evt.entity.worldObj.isRemote)
			return;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			EntityLivingBase mob = evt.entityLiving;
			ItemStack weapon = ep.getCurrentEquippedItem();
			if (weapon != null) {
				int level = ChromaEnchants.BOSSKILL.getLevel(weapon);
				if (level > 0) {
					float dmg = EnchantmentBossKill.getDamageDealt(mob, level);
					if (mob instanceof ClampedDamage) {
						dmg = Math.min(((ClampedDamage)mob).getDamageCap(src, dmg), dmg);
					}
					if (dmg > 0) {
						if (dmg >= mob.getHealth()) {
							mob.setHealth(0);
							mob.onDeath(src);
						}
						else {
							ChromaAux.doPylonAttack(null, mob, dmg, false);
						}
					}
					else {
						weapon.damageItem(4, ep);
						ReikaSoundHelper.playSoundAtEntity(ep.worldObj, ep, "random.break");
						if (mob instanceof EntityCreeper) {
							ReikaEntityHelper.chargeCreeper((EntityCreeper)mob);
						}
					}
					evt.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void applyAOE(LivingAttackEvent evt) {
		if (applyingAOE)
			return;
		DamageSource src = evt.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			EntityLivingBase mob = evt.entityLiving;
			ItemStack weapon = ep.getCurrentEquippedItem();
			if (weapon != null) {
				int level = ChromaEnchants.WEAPONAOE.getLevel(weapon);
				if (level > 0) {
					applyingAOE = true;
					double r = EnchantmentWeaponAOE.getRadius(level);
					AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(evt.entityLiving, r);
					Class<? extends EntityLivingBase> cat = ReikaEntityHelper.getEntityCategoryClass(mob);
					List<EntityLivingBase> li = mob.worldObj.getEntitiesWithinAABB(cat, box);
					for (EntityLivingBase e : li) {
						if (e != mob && e != ep) {
							Class<? extends EntityLivingBase> cat2 = ReikaEntityHelper.getEntityCategoryClass(e);
							if (cat2 == cat) {
								double d = e.getDistanceToEntity(mob);
								double f = EnchantmentWeaponAOE.getDamageFactor(level, d, r);
								if (f > 0) {
									float dmg2 = (float)(f*evt.ammount);
									if (e instanceof EntityLiving && dmg2 >= e.getHealth() && Chromabilities.RANGEDBOOST.enabledOn(ep)) {
										int xp = ((EntityLiving)e).experienceValue;
										if (ReikaInventoryHelper.checkForItemStack(ChromaItems.PENDANT3.getStackOf(CrystalElement.PURPLE), ep.inventory, false)) {
											xp *= 4;
										}
										else if (ReikaInventoryHelper.checkForItemStack(ChromaItems.PENDANT.getStackOf(CrystalElement.PURPLE), ep.inventory, false)) {
											xp *= 2;
										}
										((EntityLiving)e).experienceValue = 0;
										ep.addExperience(xp);
									}
									e.attackEntityFrom(src, dmg2);
								}
							}
						}
					}
					applyingAOE = false;
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.VOIDMONSTER)
	public void voidMonsterAttackDrops(LivingHurtEvent evt) {
		if (evt.entityLiving instanceof EntityVoidMonster) {
			if (evt.source.getEntity() instanceof EntityPlayer) {
				EntityPlayer ep = (EntityPlayer)evt.source.getEntity();
				float num = evt.ammount/40F;
				while (num > 1) {
					ReikaItemHelper.dropItem(evt.entityLiving, ChromaStacks.voidmonsterEssence);
					num--;
				}
				if (num > 0)
					if (ReikaRandomHelper.doWithChance(num))
						ReikaItemHelper.dropItem(evt.entityLiving, ChromaStacks.voidmonsterEssence);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.VOIDMONSTER)
	public void voidMonsterSeeProgress(LivingHurtEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer) {
			if (evt.source.getEntity() instanceof EntityVoidMonster) {
				ProgressStage.VOIDMONSTER.stepPlayerTo((EntityPlayer)evt.entityLiving);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.VOIDMONSTER)
	public void voidMonsterSeeProgress(PlayerLookAtVoidMonsterEvent evt) {
		ProgressStage.VOIDMONSTER.stepPlayerTo(evt.entityPlayer);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.VOIDMONSTER)
	public void voidMonsterDeathProgress(LivingDeathEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer) {
			if (evt.source.getEntity() instanceof EntityVoidMonster) {
				ProgressStage.VOIDMONSTERDIE.stepPlayerTo((EntityPlayer)evt.entityLiving);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void reopenStructureOnDeath(LivingDeathEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer) {
			int x = MathHelper.floor_double(evt.entityLiving.posX);
			int y = MathHelper.floor_double(evt.entityLiving.posY);
			int z = MathHelper.floor_double(evt.entityLiving.posZ);
			int r = 12;
			int rh = 6;
			for (int i = -r; i <= r; i++) {
				for (int j = -rh; j <= rh; j++) {
					for (int k = -r; k <= r; k++) {
						int dx = x+i;
						int dy = y+j;
						int dz = z+k;
						if (ChromaTiles.getTile(evt.entityLiving.worldObj, dx, dy, dz) == ChromaTiles.STRUCTCONTROL) {
							TileEntityStructControl te = (TileEntityStructControl)evt.entityLiving.worldObj.getTileEntity(dx, dy, dz);
							if (te.isTriggerPlayer((EntityPlayer)evt.entityLiving)) {
								te.reopenStructure();
							}
						}
					}
				}
			}
		}
	}

	/*
	@SubscribeEvent
	public void punishDamagedForests(BlockTickEvent evt) {
		int r = 2;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int x = evt.xCoord+i;
				int z = evt.zCoord+k;
				if (!evt.world.isRemote && evt.world.checkChunksExist(x, evt.yCoord, z, x, evt.yCoord, z)) {
					BiomeGenBase b = evt.world.getBiomeGenForCoords(x, z);
					if (b == ChromatiCraft.rainbowforest) {
						if (BiomeRainbowForest.isDamaged(evt.world, x, z)) {
							int y = evt.world.getTopSolidOrLiquidBlock(x, z)-1;
							while (ReikaBlockHelper.isLiquid(evt.world.getBlock(x, y, z)))
								y++;
							while (y >= 0 && evt.world.getBlock(x, y, z) == Blocks.air)
								y--;
							ReikaWorldHelper.erodeBlock(evt.world, x, y, z);
							int dx = ReikaRandomHelper.getRandomPlusMinus(x, 4);
							int dy = ReikaRandomHelper.getRandomPlusMinus(evt.yCoord, 4);
							int dz = ReikaRandomHelper.getRandomPlusMinus(z, 4);
							//evt.world.scheduleBlockUpdate(dx, dx, dz, evt.world.getBlock(dx, dy, dz), 20);
						}
					}
				}
			}
		}
	}
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void clearLumenTurretDrops(LivingDropsEvent evt) {
		if (!(evt.entityLiving instanceof EntityPlayer)) {
			if (evt.source instanceof LumenTurretDamage) {
				if (evt.entityLiving instanceof EntityLiving)
					((EntityLiving)evt.entityLiving).experienceValue = 0;
				evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void forceConstantMiningSpeed(BreakSpeed evt) {
		ItemStack is = evt.entityPlayer.getCurrentEquippedItem();
		int lvl = ChromaEnchants.MINETIME.getLevel(is);
		if (lvl > 0) {
			float h = evt.block.getBlockHardness(evt.entityPlayer.worldObj, evt.x, evt.y, evt.z);
			if (h > 0) {
				float f = (float)Math.pow(lvl/(float)ChromaEnchants.MINETIME.getEnchantment().getMaxLevel(), 1.5);
				float best = 2*h; //force same speed on everything, even superhard blocks
				evt.newSpeed = best*f+evt.originalSpeed*(1-f);
				evt.setCanceled(false);
			}
		}
	}

	/*
	@SubscribeEvent
	public void triggerDoubleJump(RawKeyPressEvent evt) {
		if (evt.key == Key.JUMP) {
			if (!evt.player.onGround && evt.player.jumpTicks == 0) {
				ItemStack boot = evt.player.getEquipmentInSlot(1);
				if (boot != null && boot.stackTagCompound != null && boot.stackTagCompound.getBoolean("Chroma_Double_Jump")) {
					if (AbilityHelper.instance.tryAndDoDoubleJump(evt.player)) {
						ReikaJavaLibrary.pConsole("fire_"+FMLCommonHandler.instance().getEffectiveSide());
						evt.player.fallDistance = 0;
						evt.player.jump();
						evt.player.velocityChanged = true;
					}
				}
			}
		}
	}*/

	@SubscribeEvent
	public void resetDoubleJump(LivingFallEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer) {
			//ReikaJavaLibrary.pConsole("fall:"+evt.entityLiving.fallDistance, Side.SERVER);
			AbilityHelper.instance.resetDoubleJump((EntityPlayer)evt.entityLiving);
		}
	}
	/*
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void processDoubleJump(LivingJumpEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer) {
			AbilityHelper.instance.registerJumpTime((EntityPlayer)evt.entityLiving);
		}
	}
	/*
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void processDoubleJump(LivingJumpEvent evt) {
		ReikaJavaLibrary.pConsole(evt.entityLiving.worldObj.isRemote);
		if (evt.entityLiving instanceof EntityPlayer) {
			if (AbilityHelper.instance.isDoubleJumping((EntityPlayer)evt.entityLiving)) {
				evt.entityLiving.fallDistance = 0;
				evt.entityLiving.motionY = Math.max(evt.entityLiving.motionY, 0.5);
				ReikaJavaLibrary.pConsole("jump:"+evt.entityLiving.fallDistance, Side.SERVER);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void triggerDoubleJump(JumpCheckEventClient evt) {
		if (evt.entityLiving instanceof EntityPlayer) {
			if (!evt.entityLiving.onGround && evt.jumpTick == 0) {
				ItemStack boot = evt.entityLiving.getEquipmentInSlot(1);
				if (boot != null && boot.stackTagCompound != null && boot.stackTagCompound.getBoolean("Chroma_Double_Jump")) {
					if (AbilityHelper.instance.tryAndDoDoubleJump((EntityPlayer)evt.entityLiving)) {
						ReikaJavaLibrary.pConsole("Client_fire");
						evt.entityLiving.fallDistance = 0;
						evt.setResult(Result.ALLOW);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void triggerDoubleJump(JumpCheckEvent evt) {
		if (!evt.entityPlayer.worldObj.isRemote) {
			if (!evt.entityPlayer.onGround && KeyWatcher.instance.isKeyDown(evt.entityPlayer, Key.JUMP)) {
				ItemStack boot = evt.entityPlayer.getEquipmentInSlot(1);
				if (boot != null && boot.stackTagCompound != null && boot.stackTagCompound.getBoolean("Chroma_Double_Jump")) {
					if (AbilityHelper.instance.tryAndDoDoubleJump(evt.entityPlayer)) {
						ReikaJavaLibrary.pConsole("server_fire");
						evt.entityPlayer.fallDistance = 0;
						evt.setResult(Result.ALLOW);
					}
				}
			}
		}
	}*/

	@SubscribeEvent
	public void triggerLumenDash(PlayerSprintEvent evt) {
		if (Chromabilities.DASH.enabledOn(evt.entityPlayer) && AbilityHelper.instance.getPlayerDashCooldown(evt.entityPlayer) == 0) {
			AbilityHelper.instance.doLumenDash(evt.entityPlayer);
		}
	}

	@SubscribeEvent
	public void negateFlyMiningSlowdown(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed evt) {
		if (!evt.entityPlayer.onGround) {
			if (!Chromabilities.REACH.enabledOn(evt.entityPlayer)) {
				ItemStack is = evt.entityPlayer.getCurrentEquippedItem();
				if (is != null && ReikaEnchantmentHelper.hasEnchantment(ChromaEnchants.AIRMINER.getEnchantment(), is)) {
					evt.newSpeed *= 5;
				}
			}
		}
	}

	@SubscribeEvent
	public void keepReachMiningFast(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed evt) {
		if (evt.entityPlayer.isInsideOfMaterial(Material.water) || !evt.entityPlayer.onGround) {
			if (Chromabilities.REACH.enabledOn(evt.entityPlayer)) {
				evt.newSpeed *= 5;
			}
		}
	}

	@SubscribeEvent
	public void stopEndCountingEndAsDeath(net.minecraftforge.event.entity.player.PlayerEvent.Clone evt) {
		if (!evt.wasDeath) {
			PlayerElementBuffer.instance.copyTo(evt.original, evt.entityPlayer);
			Chromabilities.copyTo(evt.original, evt.entityPlayer);
			AbilityHelper.instance.copyHealthBoost(evt.original, evt.entityPlayer);
		}
	}

	@SubscribeEvent
	public void preventDimLoading(ForceChunkEvent evt) {
		Ticket t = evt.ticket;
		if (t.world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			NBTTagCompound tag = t.getModData();
			if (tag.hasKey("tileX") && tag.hasKey("tileY") && tag.hasKey("tileZ")) {
				WorldLocation loc = new WorldLocation(t.world, tag.getInteger("tileX"), tag.getInteger("tileY"), tag.getInteger("tileZ"));
				Block b = loc.getBlock(t.world);
				if (b == ChromaBlocks.CHUNKLOADER.getBlockInstance()) {
					return;
				}
			}
			ChromatiCraft.logger.log("Discarding force-loaded chunk request: "+t.getModId()+":"+t.getModData()+":"+t.getChunkList());
			ChromaDimensionTicker.instance.scheduleTicketUnload(t);
		}
	}

	@SubscribeEvent
	public void killBallLightning(EntityJoinWorldEvent evt) {
		if (evt.entity instanceof EntityBallLightning && !ChromaOptions.BALLLIGHTNING.getState()) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void markHostile(AttackEntityEvent evt) {
		if (evt.target instanceof EntityPlayer) {
			for (TileEntityAuraPoint te : TileEntityAuraPoint.getPoints(evt.entityPlayer)) {
				te.markHostile((EntityPlayer)evt.target);
			}
		}
	}

	@SubscribeEvent
	public void floatstonePads(LivingFallEvent evt) {

	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void enforceMagnetism(EntityItemPickupEvent evt) {
		if (evt.item.getEntityData().hasKey("cc_magnetized")) {
			String s = evt.item.getEntityData().getString("cc_magnetized");
			UUID uid = UUID.fromString(s);
			if (!evt.entityPlayer.getUniqueID().equals(uid))
				evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void enforceMagnetism(PlayerPickupXpEvent evt) {
		if (evt.orb.getEntityData().hasKey("cc_magnetized")) {
			String s = evt.orb.getEntityData().getString("cc_magnetized");
			UUID uid = UUID.fromString(s);
			if (!evt.entityPlayer.getUniqueID().equals(uid))
				evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void preventMovingKeyThroughFence(EntityItemPickupEvent evt) {
		if (ReikaItemHelper.matchStackWithBlock(evt.item.getEntityItem(), ChromaBlocks.LOCKKEY.getBlockInstance())) {
			int x = MathHelper.floor_double(evt.entityPlayer.posX);
			int y = MathHelper.floor_double(evt.entityPlayer.posY);
			int z = MathHelper.floor_double(evt.entityPlayer.posZ);
			Block b = ChromaBlocks.LOCKFENCE.getBlockInstance();
			boolean flag =
					evt.entityPlayer.worldObj.getBlock(x, y, z-1) == b ||
					evt.entityPlayer.worldObj.getBlock(x, y, z) == b ||
					evt.entityPlayer.worldObj.getBlock(x, y, z+1) == b ||
					evt.entityPlayer.worldObj.getBlock(x-1, y, z-1) == b ||
					evt.entityPlayer.worldObj.getBlock(x-1, y, z) == b ||
					evt.entityPlayer.worldObj.getBlock(x-1, y, z+1) == b ||
					evt.entityPlayer.worldObj.getBlock(x+1, y, z-1) == b ||
					evt.entityPlayer.worldObj.getBlock(x+1, y, z) == b ||
					evt.entityPlayer.worldObj.getBlock(x+1, y, z+1) == b
					;
			if (flag) {
				evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void preventStealingDataCrystals(EntityItemPickupEvent evt) {
		ItemStack is = evt.item.getEntityItem();
		if (ChromaItems.DATACRYSTAL.matchWith(is) && is.stackTagCompound != null && is.stackTagCompound.hasKey("owner")) {
			if (!evt.entityPlayer.getUniqueID().equals(UUID.fromString(is.stackTagCompound.getString("owner")))) {
				evt.setCanceled(true);
			}
			else {
				is.stackTagCompound = null;
				evt.item.setEntityItemStack(is);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void preventStructureMining(BlockEvent.BreakEvent evt) {
		if (!evt.getPlayer().capabilities.isCreativeMode) {
			if (evt.block == ChromaBlocks.PYLON.getBlockInstance()) {
				if (evt.blockMetadata == ChromaTiles.PYLON.getBlockMetadata())
					evt.setCanceled(true);
				else if (evt.blockMetadata == ChromaTiles.REPEATER.getBlockMetadata() || evt.blockMetadata == ChromaTiles.COMPOUND.getBlockMetadata()) {
					TileEntityCrystalRepeater te = (TileEntityCrystalRepeater)evt.world.getTileEntity(evt.x, evt.y, evt.z);
					if (!te.getPlacerUUID().equals(evt.getPlayer().getUniqueID()))
						evt.setCanceled(true);
				}
			}
			else if (evt.block instanceof SemiUnbreakable) {
				if (((SemiUnbreakable)evt.block).isUnbreakable(evt.world, evt.x, evt.y, evt.z, evt.blockMetadata))
					evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.MYSTCRAFT)
	public void burnRainbowLeaves(BlockConsumedByFireEvent evt) {
		if (evt.world.getBlock(evt.xCoord, evt.yCoord, evt.zCoord) == ChromaBlocks.RAINBOWLEAF.getBlockInstance()) {
			ReikaMystcraftHelper.addInstabilityForAge(evt.world, (short)4);
		}
	}

	@SubscribeEvent
	public void preventPrematureJoin(WorldEvent.Load evt) throws InterruptedException {
		if (evt.world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			int i = 0;
			int time = 0;
			int d = 5; //ms
			while (!ChunkProviderChroma.areGeneratorsReady()) {
				Thread.sleep(d);
				time += d;
				i++;
				if (i < 10 || (i < 20 && i%5 == 0) || (i%10 == 0)) {
					String delay = "Total delay so far: "+time+" ms";
					String s = "Pausing main server thread, since it tried to load the CC dimension, which is not yet ready. "+delay;
					ChromatiCraft.logger.log(s);
				}
				if (i > 100) {
					d = 2500;
				}
				else if (i > 75) {
					d = 1000;
				}
				else if (i > 50) {
					d = 500;
				}
				else if (i > 20) {
					d = 250;
				}
				else if (i > 10) {
					d = 100;
				}
				else if (i > 5) {
					d = 50;
				}
				else if (i > 2) {
					d = 20;
				}
				else {
					d = 10;
				}
			}
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.MYSTCRAFT)
	public void noDimensionLinking(LinkEvent.LinkEventAllow evt) {
		if (evt.origin != null && evt.origin.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
			evt.setCanceled(true);
		if (evt.destination != null && evt.destination.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
			evt.setCanceled(true);
	}

	@SubscribeEvent
	public void deleteEnd(WorldEvent.Unload evt) {
		if (ChromaOptions.DELEND.getState() && !ModList.ENDEREXPANSION.isLoaded()) {
			if (evt.world.provider.dimensionId == 1 && !evt.world.isRemote) {
				String path = DimensionManager.getCurrentSaveRootDirectory().getAbsolutePath().replaceAll("\\\\", "/").replaceAll("/\\./", "/");
				File dim = new File(path+"/DIM1");
				if (dim.exists() && dim.isDirectory()) {
					ChromatiCraft.logger.log("Deleting unloaded end.");
					ReikaFileReader.deleteFolderWithContents(dim, 100);
				}
			}
		}
	}


	@SubscribeEvent
	public void teleportPlayerOut(LivingHurtEvent evt) {
		if (evt.source == DamageSource.outOfWorld && evt.entityLiving instanceof EntityPlayer) {
			if (evt.entityLiving.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
				double y = evt.entityLiving.posY;
				if (y < -1024) {
					ReikaEntityHelper.transferEntityToDimension(evt.entityLiving, 0, new ChromaTeleporter(0));
				}
				evt.ammount = 0;
				evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void resetDimension(WorldEvent.Unload evt) {
		if (evt.world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue() && !evt.world.isRemote) {
			ChromaDimensionManager.resetDimension(evt.world);
		}
	}

	@SubscribeEvent
	public void resetDimension(PlayerEvent.PlayerChangedDimensionEvent evt) {
		if (evt.fromDim == ExtraChromaIDs.DIMID.getValue())
			ChromaDimensionManager.checkChromaDimensionUnload();
	}

	@SubscribeEvent
	public void resetDimension(PlayerEvent.PlayerLoggedOutEvent evt) {
		//if (evt.player.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
		ChromaDimensionManager.checkChromaDimensionUnload();
	}

	@SubscribeEvent
	public void harvestSpawner(BlockEvent.BreakEvent evt) {
		if (evt.block == Blocks.mob_spawner) {
			ProgressStage.BREAKSPAWNER.stepPlayerTo(evt.getPlayer());
		}
	}

	@SubscribeEvent
	public void doPoolRecipes(ItemUpdateEvent evt) {
		EntityItem ei = evt.entityItem;
		boolean rng = rand.nextInt(5) == 0;
		if (rng || ei.getEntityData().getBoolean("chromaalloy")) {
			//ReikaJavaLibrary.pConsole(ei+" : "+ReikaItemHelper.getDropper(ei));
			if (PoolRecipes.instance.canAlloyItem(ei)) {
				PoolRecipe out = PoolRecipes.instance.getPoolRecipe(ei);
				if (out != null) {
					if (!ei.getEntityData().getBoolean("chromaalloy")) {
						out.initialize(ei);
						ei.getEntityData().setBoolean("chromaalloy", true);
					}
					out.doFX(ei);
					int min = out.getMinDuration();
					if (ei.worldObj.isRemote) {
						ChromaFX.poolRecipeParticles(ei);
					}
					else if (rng && ei.ticksExisted >= min && ei.ticksExisted > 20) {
						int x = MathHelper.floor_double(ei.posX);
						int y = MathHelper.floor_double(ei.posY);
						int z = MathHelper.floor_double(ei.posZ);
						TileEntityChroma te = (TileEntityChroma)ei.worldObj.getTileEntity(x, y, z);
						int ether = te.getEtherCount();
						int n = BlockActiveChroma.getSpeedMultiplier(ether);
						if (rand.nextInt(20/n) == 0 && (ei.ticksExisted-min >= 600/n || rand.nextInt((600-ei.ticksExisted+min)/n) == 0)) {
							te.clear();
							PoolRecipes.instance.makePoolRecipe(ei, out, ether, x, y, z);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void applyDamagePhasing(LivingAttackEvent evt) {
		DamageSource src = evt.source;
		if (applyingPhasing)
			return;
		if (!src.isDamageAbsolute()) {
			Entity e = src.getEntity();
			if (e instanceof EntityLivingBase) {
				EntityLivingBase elb = (EntityLivingBase)e;
				ItemStack is = elb.getHeldItem();
				if (is != null && ReikaEnchantmentHelper.hasEnchantment(ChromaEnchants.PHASING.getEnchantment(), is)) {
					applyingPhasing = true;
					int lvl = ChromaEnchants.PHASING.getLevel(is);
					float pierce = EnchantmentPhasingSequence.getPenetratingDamage(evt.ammount, lvl);
					float leftover = EnchantmentPhasingSequence.getSpilloverDamage(evt.ammount, lvl);
					evt.entityLiving.attackEntityFrom(src, leftover);
					/*
					DamageSource src2 = elb instanceof EntityPlayer ? DamageSource.causePlayerDamage((EntityPlayer)elb) : DamageSource.causeMobDamage(elb);
					src2.setDamageIsAbsolute().setDamageBypassesArmor();
					evt.entityLiving.attackEntityFrom(src2, pierce);
					 */
					ChromaAux.doPylonAttack(null, evt.entityLiving, pierce, false);
					applyingPhasing = false;
				}
			}
		}
	}

	@SubscribeEvent
	public void stealHealth(LivingAttackEvent evt) {
		DamageSource src = evt.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			if (Chromabilities.LEECH.enabledOn(ep)) {
				ep.heal(evt.ammount*0.1F);
			}
		}
	}

	@SubscribeEvent
	public void applyBoostForRangedAttack(LivingDropsEvent evt) {
		DamageSource src = evt.source;
		if (src.isProjectile() && src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			if (Chromabilities.RANGEDBOOST.enabledOn(ep)) {
				int looting = (int)(12*ep.getHealth()/ep.getMaxHealth());
				ArrayList li = new ArrayList(evt.entityLiving.capturedDrops);
				evt.entityLiving.capturedDrops.clear();
				boolean cap = evt.entityLiving.captureDrops;
				evt.entityLiving.captureDrops = true;
				Entity e = evt.entityLiving;
				ReikaObfuscationHelper.invoke("dropFewItems", e, true, looting);
				ReikaObfuscationHelper.invoke("dropEquipment", e, true, looting);
				int rem = rand.nextInt(200) - looting*2;
				if (rem <= 5)
					ReikaObfuscationHelper.invoke("dropRareDrop", e, 1);
				for (EntityItem ei : evt.entityLiving.capturedDrops) {
					if (!MinecraftForge.EVENT_BUS.post(new EntityItemPickupEvent(ep, ei))) {
						ItemStack is = ei.getEntityItem();
						if (ReikaInventoryHelper.addToIInv(is, ep.inventory)) {

						}
						else {
							evt.entityLiving.worldObj.spawnEntityInWorld(ei);
						}
					}
				}
				evt.entityLiving.capturedDrops.clear();
				evt.entityLiving.captureDrops = cap;
				evt.entityLiving.capturedDrops.addAll(li);
				Iterator<EntityItem> it = evt.drops.iterator();
				while (it.hasNext()) {
					EntityItem ei = it.next();
					if (!MinecraftForge.EVENT_BUS.post(new EntityItemPickupEvent(ep, ei))) {
						ItemStack is = ei.getEntityItem();
						if (ReikaInventoryHelper.addToIInv(is, ep.inventory)) {
							it.remove();
						}
					}
				}
				if (evt.entityLiving instanceof EntityLiving) {
					ep.addExperience(((EntityLiving)evt.entityLiving).experienceValue);
					((EntityLiving)evt.entityLiving).experienceValue = 0;
				}
			}
		}
	}

	@SubscribeEvent
	public void cancelFramez(TileEntityMoveEvent evt) {
		if (!this.isMovable(evt.tile)) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.THAUMCRAFT)
	public void wispEnergy(LivingDropsEvent evt) {
		if (evt.entityLiving instanceof EntityWisp) {
			EntityWisp e = (EntityWisp)evt.entityLiving;
			if (evt.source.getEntity() instanceof EntityPlayer) {
				EntityPlayer ep = (EntityPlayer)evt.source.getEntity();
				if (!ReikaPlayerAPI.isFake(ep)) {
					if (ProgressStage.CHARGE.isPlayerAtStage(ep)) {
						String type = e.getType();
						Aspect a = Aspect.getAspect(type);
						if (a != null) {
							int s = 4+rand.nextInt(8);
							ElementTagCompound tag = ChromaAspectManager.instance.getElementCost(a, 1+rand.nextInt(2)).scale(s);
							PlayerElementBuffer.instance.addToPlayer(ep, tag, true);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.VOIDMONSTER)
	public void voidMonsterBonus(LivingDropsEvent evt) {
		if (evt.source instanceof PylonDamage && evt.entityLiving.getClass().getSimpleName().equals("EntityVoidMonster")) {
			for (int i = 0; i < 4+rand.nextInt(4); i++) {
				ReikaItemHelper.dropItem(evt.entityLiving, ChromaItems.FRAGMENT.getStackOf());
			}
			try {
				for (int i = 0; i < 2; i++)
					ReikaObfuscationHelper.invoke("dropFewItems", evt.entityLiving, false, 0);
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not perform pylon-void monster bonus drops interaction!");
				e.printStackTrace();
			}
			ChromaSounds.POWERDOWN.playSound(evt.entityLiving, 2, 1);
			ChromaSounds.POWERDOWN.playSound(evt.entityLiving, 2, 0.5F);
			ReikaParticleHelper.EXPLODE.spawnAt(evt.entityLiving);
		}
	}

	@SubscribeEvent
	public void deathProgress(LivingDeathEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)evt.entityLiving;
			if (PlayerElementBuffer.instance.getPlayerTotalEnergy(ep) >= 90000)
				ProgressStage.DIE.stepPlayerTo(ep);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.BLOODMAGIC)
	@ClassDependent("WayofTime.alchemicalWizardry.api.event.TeleposeEvent")
	public void noTelepose(TeleposeEvent evt) {
		if (!this.isMovable(evt.initialBlock) || !this.isMovable(evt.finalBlock))
			evt.setCanceled(true);
		if (!this.isMovable(evt.getInitialTile()) || !this.isMovable(evt.getFinalTile()))
			evt.setCanceled(true);
		if (evt.initialWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue() || evt.finalWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			AxisAlignedBB box1 = ReikaAABBHelper.getBlockAABB(evt.initialX, evt.initialY, evt.initialZ).expand(3, 3, 3).offset(0, 4, 0);
			AxisAlignedBB box2 = ReikaAABBHelper.getBlockAABB(evt.finalX, evt.finalY, evt.finalZ).expand(3, 3, 3).offset(0, 4, 0);
			List<Entity> li = evt.initialWorld.getEntitiesWithinAABB(Entity.class, box1);
			List<Entity> li2 = evt.finalWorld.getEntitiesWithinAABB(Entity.class, box2);
			if (!li.isEmpty() || !li2.isEmpty())
				evt.setCanceled(true);
		}
	}

	private boolean isMovable(Block b) {
		if (b instanceof BlockStructureShield)
			return false;
		ChromaBlocks r = ChromaBlocks.getEntryByID(b);
		return r == null || !r.isDimensionStructureBlock();
	}

	private boolean isMovable(TileEntity te) {
		return !(te instanceof TileEntityCrystalBase);
	}

	@SubscribeEvent
	public void respawnDragon(WorldEvent.Load evt) {
		if (evt.world.provider.dimensionId == 1 && !evt.world.isRemote) {
			if (ChromaOptions.REDRAGON.getState()) {
				EntityDragon ed = new EntityDragon(evt.world);
				ed.setLocationAndAngles(0.0D, 128.0D, 0.0D, evt.world.rand.nextFloat() * 360.0F, 0.0F);
				evt.world.spawnEntityInWorld(ed);
			}
		}
	}

	@SubscribeEvent
	public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent evt) {
		if (evt.toDim == -1) {
			ProgressStage.NETHER.stepPlayerTo(evt.player);
		}
		else if (evt.toDim == 1) {
			ProgressStage.END.stepPlayerTo(evt.player);
		}
		else if (evt.toDim == ReikaTwilightHelper.getDimensionID()) {
			ProgressStage.TWILIGHT.stepPlayerTo(evt.player);
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	@ModDependent(ModList.IC2)
	public void explosionProtectionByRedCore(ic2.api.event.ExplosionEvent evt) {
		if (!TileEntityProtectionUpgrade.canExplode(evt.world, evt.x, evt.y, evt.z, evt.power, true)) {
			evt.world.playSoundEffect(evt.x, evt.y, evt.z, "random.fizz", 2, 0.5F);
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void explosionProtectionByRedCore(ExplosionEvent.Start evt) {
		if (!TileEntityProtectionUpgrade.canExplode(evt.world, evt.explosion.explosionX, evt.explosion.explosionY, evt.explosion.explosionZ, evt.explosion.explosionSize, false)) {
			evt.world.playSoundEffect(evt.explosion.explosionX, evt.explosion.explosionY, evt.explosion.explosionZ, "random.fizz", 2, 0.5F);
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void explosionProtection(ExplosionEvent.Detonate evt) {
		TileEntityExplosionShield.dampenExplosion(evt.world, evt.explosion);
	}

	@ModDependent(ModList.CRITTERPET)
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void rangedPetInvincibility(LivingAttackEvent evt) {
		if (evt.ammount > 0 && TileEntityCrystalBeacon.isDamageBlockable(evt.source)) {
			if (evt.entityLiving instanceof TamedMob) {
				if (TileEntityCrystalBeacon.isEntityInvincible((EntityLiving)evt.entityLiving, ((TamedMob)evt.entityLiving).getMobOwner(), evt.ammount)) {
					evt.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void rangedInvincibility(LivingAttackEvent evt) {
		if (evt.ammount > 0 && TileEntityCrystalBeacon.isDamageBlockable(evt.source)) {
			if (evt.entityLiving instanceof EntityPlayer && !((EntityPlayer)evt.entityLiving).capabilities.isCreativeMode) {
				if (TileEntityCrystalBeacon.isPlayerInvincible((EntityPlayer)evt.entityLiving, evt.ammount)) {
					evt.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void lampSpawnLimits(CheckSpawn evt) {
		if (ReikaEntityHelper.isHostile(evt.entityLiving)) {
			if (TileEntityChromaLamp.findLampFromXYZ(evt.world, evt.x, evt.z)) {
				evt.setResult(Result.DENY);
			}
		}
	}

	@SubscribeEvent
	public void checkRainbowForest(LivingUpdateEvent evt) {
		if (evt.entityLiving.ticksExisted > 600 && evt.entityLiving.ticksExisted%32 == 0 && evt.entityLiving instanceof EntityPlayer) {
			int x = MathHelper.floor_double(evt.entityLiving.posX);
			int z = MathHelper.floor_double(evt.entityLiving.posZ);
			if (evt.entityLiving.worldObj.getBiomeGenForCoords(x, z) instanceof BiomeRainbowForest) {
				ProgressStage.RAINBOWFOREST.stepPlayerTo((EntityPlayer)evt.entityLiving);
			}
		}
	}

	@SubscribeEvent
	public void stopSomeTicks(LivingUpdateEvent evt) {
		if (TileEntityAIShutdown.stopUpdate(evt.entityLiving)) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void preSpawnItemXP(EntityJoinWorldEvent evt) {
		if (!evt.world.isRemote) {
			Entity e = evt.entity;
			if (e instanceof EntityItem || e instanceof EntityXPOrb) {
				if (TileEntityItemCollector.absorbItem(evt.world, e)) {
					evt.setCanceled(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public void preventLilyFreeze(IceFreezeEvent evt) {
		if (TileEntityHeatLily.stopFreeze(evt.world, evt.xCoord, evt.yCoord, evt.zCoord)) {
			evt.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void preventDimensionIce(IceFreezeEvent evt) {
		if (evt.world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			evt.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void convertCrystals(EntityJoinWorldEvent evt) {
		if (!evt.world.isRemote) {
			if (evt.entity.getClass() == EntityEnderCrystal.class) {
				EntityChromaEnderCrystal cry = new EntityChromaEnderCrystal(evt.world, (EntityEnderCrystal)evt.entity);
				evt.entity.setDead();
				cry.worldObj.spawnEntityInWorld(cry);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void sendLinkedItems(EntityItemPickupEvent ev) {
		this.fillFragments(ev);
		EntityPlayer ep = ev.entityPlayer;
		this.parseInventoryForLinking(ev, ep.inventory.mainInventory, null);
	}

	private void parseInventoryForLinking(EntityItemPickupEvent ev, ItemStack[] inv, ItemStack active) {
		EntityItem e = ev.item;
		if (e.isDead || ev.isCanceled())
			return;
		ItemStack picked = e.getEntityItem();
		for (int i = 0; i < inv.length; i++) {
			if (active == null || ((ActivatedInventoryItem)active.getItem()).isSlotActive(active, i)) {
				ItemStack in = inv[i];
				if (in != null && in.getItem() == ChromaItems.LINK.getItemInstance()) {
					if (((ItemInventoryLinker)ChromaItems.LINK.getItemInstance()).matchesItem(ev.entityPlayer, in, picked)) {
						if (ItemInventoryLinker.processItem(ev.entityPlayer.worldObj, in, picked)) {
							e.playSound("random.pop", 0.5F, 1);
							e.setDead();
							ev.setCanceled(true);
							return;
						}
					}
				}
				else if (in != null && in.getItem() instanceof ActivatedInventoryItem) {
					this.parseInventoryForLinking(ev, ((ActivatedInventoryItem)in.getItem()).getInventory(in), in);
				}
			}
		}
	}

	@SubscribeEvent
	public void onBucketUse(FillBucketEvent event) {
		World world = event.world;
		MovingObjectPosition pos = event.target;
		int x = pos.blockX;
		int y = pos.blockY;
		int z = pos.blockZ;
		Block b = world.getBlock(x, y, z);
		if (b == ChromaBlocks.CHROMA.getBlockInstance()) {
			event.result = ChromaItems.BUCKET.getStackOf();
			world.setBlockToAir(x, y, z);
			event.setResult(Result.ALLOW);
			//event.entityPlayer.setCurrentItemOrArmor(0, event.result);
		}/*
		else if (b == ChromaBlocks.ACTIVECHROMA.getBlockInstance()) {
			TileEntityChroma te = (TileEntityChroma)world.getTileEntity(x, y, z);
			event.result = ChromaItems.BUCKET.getStackOfMetadata(2+te.getElement().ordinal());
			event.result.stackTagCompound = new NBTTagCompound();
			event.result.stackTagCompound.setInteger("berry", te.getBerryCount());
			world.setBlockToAir(x, y, z);
			event.setResult(Result.ALLOW);
			//event.entityPlayer.setCurrentItemOrArmor(0, event.result);
		}*/
		else if (b == ChromaBlocks.ENDER.getBlockInstance()) {
			world.setBlockToAir(x, y, z);
			event.setResult(Result.ALLOW);
			event.result = ChromaItems.BUCKET.getStackOfMetadata(1);
			//event.entityPlayer.setCurrentItemOrArmor(0, event.result);
		}
		else if (b == ChromaBlocks.LUMA.getBlockInstance()) {
			world.setBlockToAir(x, y, z);
			event.setResult(Result.ALLOW);
			event.result = ChromaItems.BUCKET.getStackOfMetadata(3);
			ProgressStage.LUMA.stepPlayerTo(event.entityPlayer);
			//event.entityPlayer.setCurrentItemOrArmor(0, event.result);
		}
		else if (b == ChromaBlocks.MOLTENLUMEN.getBlockInstance()) {
			world.setBlockToAir(x, y, z);
			event.setResult(Result.ALLOW);
			event.result = ChromaItems.BUCKET.getStackOfMetadata(4);
			//event.entityPlayer.setCurrentItemOrArmor(0, event.result);
		}
		else if (b == ChromaBlocks.EVERFLUID.getBlockInstance()) { //Not bucketable
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void carryPotionEffect(AttackEntityEvent ev) {
		EntityPlayer ep = ev.entityPlayer;
		Entity tg = ev.target;
		if (tg instanceof EntityLivingBase && !(ItemPurifyCrystal.isActive(ep) && tg instanceof EntityLivingBase && ReikaEntityHelper.isHostile((EntityLivingBase)tg))) {
			EntityLivingBase elb = (EntityLivingBase)tg;
			this.parseInventoryForPendantCarry(ev, elb, ep.inventory.mainInventory, null);
		}
	}

	private void parseInventoryForPendantCarry(AttackEntityEvent ev, EntityLivingBase elb, ItemStack[] inv, ItemStack active) {
		for (int i = 0; i < inv.length; i++) {
			if (active == null || ((ActivatedInventoryItem)active.getItem()).isSlotActive(active, i)) {
				ItemStack is = inv[i];
				if (is != null) {
					if (is.getItem() == ChromaItems.PENDANT3.getItemInstance()) {
						CrystalPotionController.applyEffectFromColor(100, 1, elb, CrystalElement.elements[is.getItemDamage()], true);
					}
					else if (is.getItem() == ChromaItems.PENDANT.getItemInstance()) {
						CrystalPotionController.applyEffectFromColor(100, 0, elb, CrystalElement.elements[is.getItemDamage()], true);
					}
					else if (is.getItem() instanceof ActivatedInventoryItem) {
						this.parseInventoryForPendantCarry(ev, elb, ((ActivatedInventoryItem)is.getItem()).getInventory(is), is);
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void triggerBossProgress(LivingDeathEvent ev) {
		if (ev.source.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)ev.source.getEntity();
			if (ep != null && ep.worldObj.provider.dimensionId == ev.entityLiving.worldObj.provider.dimensionId && ep.getDistanceSqToEntity(ev.entityLiving) <= 384*384) {
				if (ev.entityLiving instanceof EntityDragon || ev.entityLiving instanceof CustomEnderDragon || ev.entityLiving.getClass().getName().equals("chylex.hee.entity.boss.EntityBossDragon")) {
					ProgressStage.KILLDRAGON.stepPlayerTo(ep);
				}
				else if (ev.entityLiving instanceof EntityWither) {
					ProgressStage.KILLWITHER.stepPlayerTo(ep);
				}

				if (ReikaEntityHelper.isHostile(ev.entityLiving)) {
					ProgressStage.KILLMOB.stepPlayerTo(ep);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void extraXP(LivingDropsEvent ev) {
		EntityLivingBase e = ev.entityLiving;
		DamageSource src = ev.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			int meta = CrystalElement.PURPLE.ordinal();
			int val = e instanceof EntityPlayer ? 25 : e instanceof EntityLiving ? ((EntityLiving)e).experienceValue : 5;
			if (val == 0)
				val = 5;
			if (e instanceof EntityDragon)
				val = 10000;
			if (CrystalPotionController.shouldBeHostile(e, e.worldObj)) {
				if (e instanceof EntityLiving)
					((EntityLiving)e).experienceValue = 0;
				else if (e instanceof EntityPlayer) {
					ReikaPlayerAPI.clearExperience((EntityPlayer)e);
				}
			}
			else {
				int orbs = 0;
				if (ReikaInventoryHelper.checkForItemStack(ChromaItems.PENDANT3.getStackOfMetadata(meta), ep.inventory, false)) {
					orbs = 3;
				}
				else if (ReikaInventoryHelper.checkForItemStack(ChromaItems.PENDANT.getStackOfMetadata(meta), ep.inventory, false)) {
					orbs = 1;
				}

				if (orbs > 0) {
					if (Chromabilities.RANGEDBOOST.enabledOn(ep)) {
						ep.addExperience(val*orbs);
					}
					else {
						for (int i = 0; i < orbs; i++) {
							double px = ReikaRandomHelper.getRandomPlusMinus(e.posX, 0.125);
							double pz = ReikaRandomHelper.getRandomPlusMinus(e.posZ, 0.125);
							EntityXPOrb xp = new EntityXPOrb(e.worldObj, px, e.posY, pz, val);
							if (!e.worldObj.isRemote)
								e.worldObj.spawnEntityInWorld(xp);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void bonemealEvent (BonemealEvent event)
	{
		if (!event.world.isRemote)  {
			if (event.block == ChromaBlocks.DYESAPLING.getBlockInstance()) {
				BlockDyeSapling sap = (BlockDyeSapling)event.block;
				World world = event.world;
				int x = event.x;
				int y = event.y;
				int z = event.z;
				if (sap.canGrowAt(world, x, y, z, true)) {
					sap.func_149878_d(world, x, y, z, rand);
					event.setResult(Event.Result.ALLOW);
				}
			}
			else if (event.block == ChromaBlocks.RAINBOWSAPLING.getBlockInstance()) {
				BlockRainbowSapling sap = (BlockRainbowSapling)event.block;
				World world = event.world;
				int x = event.x;
				int y = event.y;
				int z = event.z;
				if (sap.canGrowLargeRainbowTreeAt(world, x, y, z)) {
					sap.func_149878_d(world, x, y, z, rand);
					event.setResult(Event.Result.ALLOW);
				}
			}
		}
	}

	@SubscribeEvent
	public void colorSheep(LivingSpawnEvent ev) {
		if (ev.getClass() != LivingSpawnEvent.class) //do not allow subclasses
			return;
		World world = ev.world;
		if (world.isRemote)
			return;
		int x = (int)Math.floor(ev.x);
		int y = (int)Math.floor(ev.y);
		int z = (int)Math.floor(ev.z);
		EntityLivingBase e = ev.entityLiving;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ChromatiCraft.isRainbowForest(b)) {
			if (e instanceof EntitySheep) {
				EntitySheep es = (EntitySheep)e;
				es.setFleeceColor(rand.nextInt(16));
			}
		}
	}

	/** Not functional due to BlockLeaves being the <i>only</i> block not to fire the event */
	@SubscribeEvent
	public void addLeafColors(HarvestDropsEvent evt) {/*
		World world = evt.world;
		int x = evt.x;
		int y = evt.y;
		int z = evt.z;
		ArrayList<ItemStack> li = evt.drops;
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.leaves || b == Blocks.leaves2) {
			int meta = rand.nextInt(16);
			ItemStack sapling = new ItemStack(ChromaBlocks.DYESAPLING.getBlockInstance(), 1, meta);
			if (ReikaRandomHelper.doWithChance(0.04)) { //4% chance per leaf block
				li.add(sapling);
			}
		}*/
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void controlSlimes(CheckSpawn ev) {
		World world = ev.world;
		if (world.isRemote)
			return;
		int x = (int)Math.floor(ev.x);
		int y = (int)Math.floor(ev.y);
		int z = (int)Math.floor(ev.z);
		EntityLivingBase e = ev.entityLiving;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ChromatiCraft.isRainbowForest(b)) {
			if (!BiomeRainbowForest.isMobAllowed(e)) {
				ev.setResult(Result.DENY);
				e.setDead();
			}
			else {
				if (e instanceof EntitySlime) { //ignore the slime chunk and /10 rules
					EntitySlime es = (EntitySlime)e;
					if (es.posY < 40) {
						if (world.checkNoEntityCollision(e.boundingBox)) {
							if (world.getCollidingBoundingBoxes(e, e.boundingBox).isEmpty() && !world.isAnyLiquid(e.boundingBox)) {
								if (ev.getResult() != Result.DENY)
									ev.setResult(Result.ALLOW);
							}
						}
					}
				}
			}
		}
		//ReikaJavaLibrary.pConsole(b.biomeName+":"+e.getCommandSenderName()+":"+ReikaEntityHelper.isHostile(e)+":"+ev.getResult());
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void killSpawns(LivingSpawnEvent ev) {
		World world = ev.world;
		if (world.isRemote)
			return;
		int x = (int)Math.floor(ev.x);
		int y = (int)Math.floor(ev.y);
		int z = (int)Math.floor(ev.z);
		EntityLivingBase e = ev.entityLiving;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ChromatiCraft.isRainbowForest(b)) {
			if (!BiomeRainbowForest.isMobAllowed(e)) {
				e.setDead();
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST, receiveCanceled = true)
	public void killSpawns(EntityJoinWorldEvent ev) {
		World world = ev.world;
		if (world.isRemote)
			return;
		if (ev.entity instanceof EntityLiving) {
			int x = (int)Math.floor(ev.entity.posX);
			int y = (int)Math.floor(ev.entity.posY);
			int z = (int)Math.floor(ev.entity.posZ);
			EntityLiving e = (EntityLiving)ev.entity;
			BiomeGenBase b = world.getBiomeGenForCoords(x, z);
			if (ChromatiCraft.isRainbowForest(b)) {
				if (!BiomeRainbowForest.isMobAllowed(e) && ChromaAux.requiresSpecialSpawnEnforcement(e)) {
					//e.setDead();
					ev.setCanceled(true);
				}
			}
		}
	}
	/*
	@ModDependent(ModList.LYCANITE)
	@SubscribeEvent(priority=EventPriority.LOWEST, receiveCanceled = true)
	public void specialEnforce(LivingUpdateEvent ev) {
		if (ev.entityLiving.ticksExisted < 5) {
			World world = ev.entityLiving.worldObj;
			if (world.isRemote)
				return;
			int x = (int)Math.floor(ev.entityLiving.posX);
			int y = (int)Math.floor(ev.entityLiving.posY);
			int z = (int)Math.floor(ev.entityLiving.posZ);
			EntityLivingBase e = ev.entityLiving;
			BiomeGenBase b = world.getBiomeGenForCoords(x, z);
			if (ChromatiCraft.isRainbowForest(b)) {
				if (!BiomeRainbowForest.isMobAllowed(e)) {
					e.setDead();
				}
			}
		}
	}
	 */

	@SubscribeEvent
	@ModDependent(ModList.THAUMCRAFT)
	public void friendlyWisps(LivingUpdateEvent ev) {
		if (!ChromaOptions.HOSTILEFOREST.getState()) {
			if (ev.entityLiving instanceof EntityWisp) {
				World world = ev.entityLiving.worldObj;
				if (world.isRemote)
					return;
				int x = (int)Math.floor(ev.entityLiving.posX);
				int y = (int)Math.floor(ev.entityLiving.posY);
				int z = (int)Math.floor(ev.entityLiving.posZ);
				EntityLivingBase e = ev.entityLiving;
				BiomeGenBase b = world.getBiomeGenForCoords(x, z);
				if (ChromatiCraft.isRainbowForest(b)) {
					ReikaThaumHelper.setWispHostility((EntityWisp)ev.entityLiving, null);
				}
			}
		}
	}

	/*
	@SubscribeEvent
	public void pylonDrops(LivingDropsEvent ev) {
		if (ev.source instanceof PylonDamage) {
			EntityLivingBase e = ev.entityLiving;
			int n = 0;
			if (e instanceof EntityPlayer) {
				NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT((EntityPlayer)e);
				String tag = "pylondeath";
				long time = nbt.getLong(tag);
				long cur = e.worldObj.getTotalWorldTime();
				if (cur-time > 24000) {
					n = 1;
					if (rand.nextInt(8) == 0)
						n = 2;
				}
				nbt.setLong(tag, cur);
			}
			else if (e instanceof EntityVillager || e instanceof EntityWitch)
				n = rand.nextInt(8) == 0 ? 1 : 0;
			else if (e instanceof EntityEnderman)
				;//n = rand.nextInt(32) == 0 ? 1 : 0;
			if (n > 0) {
				World world = e.worldObj;
				for (int i = 0; i < n; i++) {
					ReikaItemHelper.dropItem(e, ChromaItems.FRAGMENT.getStackOf());
				}
			}
		}
	}
	 */

	//@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void fillFragments(EntityItemPickupEvent ev) {
		ItemStack is = ev.item.getEntityItem();
		if (ChromaItems.FRAGMENT.matchWith(is) && ItemInfoFragment.isBlank(is) && !ev.entityPlayer.capabilities.isCreativeMode) {
			ItemInfoFragment.programShardAndGiveData(is, ev.entityPlayer);
		}
	}

	@SubscribeEvent
	public void biomeDrops(LivingDropsEvent ev) {
		EntityLivingBase e = ev.entityLiving;
		ArrayList<EntityItem> drops = ev.drops;
		int x = (int)Math.floor(e.posX);
		int y = (int)Math.floor(e.posY);
		int z = (int)Math.floor(e.posZ);
		World world = e.worldObj;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ChromatiCraft.isRainbowForest(b)) {
			if (e instanceof EntitySlime) {
				int dmg = e.getEntityId()%16;
				int size = 1+rand.nextInt(3);
				ItemStack dye = new ItemStack(Items.dye, size, dmg);
				ReikaItemHelper.dropItem(world, e.posX, e.posY, e.posZ, dye);
			}

			int spawn = ChromaOptions.ANIMALSPAWN.getValue();
			int def = ChromaOptions.ANIMALSPAWN.getDefaultValue();
			if (spawn < def) {
				int mult = def-spawn;
				if (e instanceof EntityAnimal) {
					for (int i = 0; i < mult; i++) {
						for (EntityItem is : drops)
							ReikaItemHelper.dropItem(world, e.posX, e.posY, e.posZ, is.getEntityItem());
					}
				}
			}
		}
	}



}
