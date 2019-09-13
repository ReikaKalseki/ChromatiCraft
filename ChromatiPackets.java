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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.google.common.base.Strings;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Auxiliary.MonumentCompletionRitual;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityCalls;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.Event.DimensionPingEvent;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CastingAutomationBlock;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.InscriptionRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.PortalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.RepeaterTurboRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.PylonTurboRecipe;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaOverlays;
import Reika.ChromatiCraft.Auxiliary.Render.ProbeInfoOverlayRenderer;
import Reika.ChromatiCraft.Auxiliary.Render.StructureErrorOverlays;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityWirelessPowered;
import Reika.ChromatiCraft.Block.BlockActiveChroma.TileEntityChroma;
import Reika.ChromatiCraft.Block.BlockEnderTNT.TileEntityEnderTNT;
import Reika.ChromatiCraft.Block.BlockHeatLamp.TileEntityHeatLamp;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.BlockRouterNode.TileEntityRouterNode;
import Reika.ChromatiCraft.Block.Crystal.BlockPowerTree;
import Reika.ChromatiCraft.Block.Decoration.BlockRangedLamp.TileEntityRangedLamp;
import Reika.ChromatiCraft.Block.Dimension.Structure.Music.BlockMusicMemory.TileMusicMemory;
import Reika.ChromatiCraft.Block.Relay.BlockRelayFilter.TileEntityRelayFilter;
import Reika.ChromatiCraft.Block.Worldgen.BlockUnknownArtefact;
import Reika.ChromatiCraft.Container.ContainerBookPages;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Entity.EntityChainGunShot;
import Reika.ChromatiCraft.Entity.EntityDimensionFlare;
import Reika.ChromatiCraft.Entity.EntityEnderEyeT2;
import Reika.ChromatiCraft.Entity.EntityGlowCloud;
import Reika.ChromatiCraft.Entity.EntityMeteorShot;
import Reika.ChromatiCraft.Entity.EntitySplashGunShot;
import Reika.ChromatiCraft.Entity.EntityThrownGem;
import Reika.ChromatiCraft.Entity.EntityVacuum;
import Reika.ChromatiCraft.GUI.Tile.GuiTeleportGate;
import Reika.ChromatiCraft.Items.ItemFertilitySeed;
import Reika.ChromatiCraft.Items.ItemUnknownArtefact;
import Reika.ChromatiCraft.Items.Tools.ItemAuraPouch;
import Reika.ChromatiCraft.Items.Tools.ItemBulkMover;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.ChromatiCraft.Items.Tools.Powered.ItemStructureFinder;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemFlightWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand.TransitionMode;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.ChromatiCraft.Magic.Network.PylonLinkNetwork;
import Reika.ChromatiCraft.ModInterface.RFWeb;
import Reika.ChromatiCraft.ModInterface.VoidMonsterDestructionRitual;
import Reika.ChromatiCraft.ModInterface.AE.TileEntityMEDistributor;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.CrystalWand;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.EssentiaNetwork.EssentiaPath;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.NodeReceiverWrapper;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.NodeRecharger;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.TileEntityAspectFormer;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomePainter;
import Reika.ChromatiCraft.TileEntity.TileEntityDataNode;
import Reika.ChromatiCraft.TileEntity.TileEntityFarmer;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCaveLighter;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemInserter;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityCrystalFence;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityLumenTurret;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityOreCreator;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityTeleportationPump;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityChromaCrystal;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityCrystalCharger;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityPylonTurboCharger;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityCrystalMusic;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityParticleSpawner;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalBroadcaster;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityNetworkOptimizer;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCobbleGen;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityGlowFire;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityInventoryTicker;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntitySpawnerReprogrammer;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityFluidDistributor;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityFluidRelay;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRFDistributor;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRouterHub;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTeleportGate;
import Reika.ChromatiCraft.World.Dimension.BiomeDistributor;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager;
import Reika.ChromatiCraft.World.Dimension.OuterRegionsEvents;
import Reika.ChromatiCraft.World.Dimension.SkyRiverManagerClient;
import Reika.ChromatiCraft.World.Dimension.StructureCalculator;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlend.RayBlendPuzzle;
import Reika.ChromatiCraft.World.IWG.PylonGenerator;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.PacketHandler;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.DataPacket;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

public class ChromatiPackets implements PacketHandler {

	protected ChromaPackets pack;

	public void handleData(PacketObj packet, World world, EntityPlayer ep) {
		DataInputStream inputStream = packet.getDataIn();
		int control = Integer.MIN_VALUE;
		int len;
		int[] data = new int[0];
		int x = 0;
		int y = 0;
		int z = 0;
		double dx = 0;
		double dy = 0;
		double dz = 0;
		String stringdata = null;
		NBTTagCompound NBT = null;
		UUID id = null;
		//System.out.print(packet.length);
		try {
			PacketTypes packetType = packet.getType();
			switch(packetType) {
				case FULLSOUND:
					break;
				case SOUND:
					control = inputStream.readInt();
					ChromaSounds s = ChromaSounds.soundList[control];
					double sx = inputStream.readDouble();
					double sy = inputStream.readDouble();
					double sz = inputStream.readDouble();
					float v = inputStream.readFloat();
					float p = inputStream.readFloat();
					boolean att = inputStream.readBoolean();
					ReikaSoundHelper.playClientSound(s, sx, sy, sz, v, p, att);
					return;
				case STRING:
					stringdata = packet.readString();
					control = inputStream.readInt();
					pack = ChromaPackets.getPacket(control);
					break;
				case DATA:
					control = inputStream.readInt();
					pack = ChromaPackets.getPacket(control);
					len = pack.numInts;
					if (pack.hasData()) {
						if (pack.variableData()) {
							ArrayList<Integer> li = new ArrayList();
							while (inputStream.available() >= 4+4*3) {
								li.add(inputStream.readInt());
							}
							data = ReikaArrayHelper.intListToArray(li);
						}
						else {
							data = new int[len];
							for (int i = 0; i < len; i++)
								data[i] = inputStream.readInt();
						}
					}
					break;
				case POS:
					control = inputStream.readInt();
					pack = ChromaPackets.getPacket(control);
					dx = inputStream.readDouble();
					dy = inputStream.readDouble();
					dz = inputStream.readDouble();
					len = pack.numInts;
					if (pack.hasData()) {
						data = new int[len];
						for (int i = 0; i < len; i++)
							data[i] = inputStream.readInt();
					}
					break;
				case UPDATE:
					control = inputStream.readInt();
					pack = ChromaPackets.getPacket(control);
					break;
				case FLOAT:
					break;
				case SYNC:
					String name = packet.readString();
					x = inputStream.readInt();
					y = inputStream.readInt();
					z = inputStream.readInt();
					ReikaPacketHelper.updateTileEntityData(world, x, y, z, name, inputStream);
					return;
				case TANK:
					String tank = packet.readString();
					x = inputStream.readInt();
					y = inputStream.readInt();
					z = inputStream.readInt();
					int level = inputStream.readInt();
					ReikaPacketHelper.updateTileEntityTankData(world, x, y, z, tank, level);
					return;
				case RAW:
					control = inputStream.readInt();
					pack = ChromaPackets.getPacket(control);
					len = 1;
					data = new int[len];
					for (int i = 0; i < len; i++)
						data[i] = inputStream.readInt();
					break;
				case PREFIXED:
					control = inputStream.readInt();
					pack = ChromaPackets.getPacket(control);
					len = inputStream.readInt();
					data = new int[len];
					for (int i = 0; i < len; i++)
						data[i] = inputStream.readInt();
					break;
				case NBT:
					control = inputStream.readInt();
					pack = ChromaPackets.getPacket(control);
					NBT = ((DataPacket)packet).asNBT();
					break;
				case STRINGINT:
				case STRINGINTLOC:
					stringdata = packet.readString();
					control = inputStream.readInt();
					pack = ChromaPackets.getPacket(control);
					data = new int[pack.numInts];
					for (int i = 0; i < data.length; i++)
						data[i] = inputStream.readInt();
					break;
				case UUID:
					control = inputStream.readInt();
					pack = ChromaPackets.getPacket(control);
					long l1 = inputStream.readLong(); //most
					long l2 = inputStream.readLong(); //least
					id = new UUID(l1, l2);
					break;
			}
			if (packetType.hasCoordinates()) {
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
			}
		}
		catch (IOException e) {
			ChromatiCraft.logger.logError("Error handling "+pack+" packet:");
			e.printStackTrace();
			return;
		}
		TileEntity tile = world.getTileEntity(x, y, z);
		try {
			switch (pack) {
				case ENCHANTER: {
					Enchantment e = Enchantment.enchantmentsList[data[0]];
					boolean incr = data[1] > 0;
					TileEntityAutoEnchanter ench = (TileEntityAutoEnchanter)tile;
					if (incr) {
						ench.incrementEnchantment(e);
					}
					else {
						ench.decrementEnchantment(e);
					}
					break;
				}
				case ENCHANTERRESET: {
					TileEntityAutoEnchanter ench = (TileEntityAutoEnchanter)tile;
					ench.clearEnchantments();
					break;
				}
				case SPAWNERPROGRAM: {
					TileEntitySpawnerReprogrammer prog = (TileEntitySpawnerReprogrammer)tile;
					prog.setMobType(stringdata);
					break;
				}
				case SPAWNERDATA: {
					TileEntitySpawnerReprogrammer prog = (TileEntitySpawnerReprogrammer)tile;
					prog.setData(data[0], data[1], data[2], data[3], data[4], data[5]);
					break;
				}
				case CRYSTALEFFECT: {
					Block b = world.getBlock(x, y, z);
					if (b instanceof CrystalBlock) {
						CrystalBlock cb = (CrystalBlock)b;
						cb.updateEffects(world, x, y, z);
					}
					break;
				}
				case PLANTUPDATE:
					((TileEntityCrystalPlant)tile).updateLight();
					break;
				case ABILITY: {
					Ability c = Chromabilities.getAbilityByInt(data[0]);
					if (Chromabilities.playerHasAbility(ep, c))
						Chromabilities.triggerAbility(ep, c, data[1], true);
					break;
				}
				case ABILITYSEND: {
					Entity player = world.getEntityByID(data[0]);
					if (player instanceof EntityPlayer) {
						Ability c = Chromabilities.getAbilityByInt(data[1]);
						Chromabilities.triggerAbility((EntityPlayer)player, c, data[2], false);
					}
					break;
				}
				case PYLONATTACK:
					if (tile instanceof TileEntityCrystalPylon)
						((TileEntityCrystalPylon)tile).particleAttack(data[0], data[1], data[2], data[3], data[4], data[5]);
					break;
				case PYLONATTACKRECEIVE:
					ChromaOverlays.instance.triggerPylonEffect(CrystalElement.elements[data[0]]);
					break;
				case ABILITYCHOOSE:
					((TileEntityRitualTable)tile).setChosenAbility(Chromabilities.getAbilityByInt(data[0]));
					break;
				case BUFFERSET:
					PlayerElementBuffer.instance.setPlayerCapOnClient(ep, data[0], data[1] > 0);
					break;
					/*
				case BUFFERINC:
					PlayerElementBuffer.instance.upgradePlayerOnClient(ep);
					break;
					 */
				case TELEPUMP:
					((TileEntityTeleportationPump)tile).setTargetedFluid(data[0]);
					break;
					//case TRANSMIT:
					//((TileEntityFiberTransmitter)tile).transmitParticle(ForgeDirection.VALID_DIRECTIONS[data[0]], data[1], CrystalElement.elements[data[2]]);
					//break;
				case ASPECT:
					((TileEntityAspectFormer)tile).selectAspect(stringdata);
					break;
				case LAMPCHANNEL:
					((TileEntityRangedLamp)tile).setChannel(data[0]);
					break;
				case LAMPCONTROL:
					switch(data[0]) {
						case 0:
							((TileEntityLampController)tile).setChannel(data[1]);
							break;
						case 1:
							((TileEntityLampController)tile).incrementMode();
							break;
						case 2:
							((TileEntityLampController)tile).toggleState();
							break;
					}
					break;
				case LAMPINVERT:
					((TileEntityRangedLamp)tile).invert();
					break;
				case TNT:
					((TileEntityEnderTNT)tile).setTarget(ep, data[0], data[1], data[2], data[3]);
					break;
				case BOOKINVSCROLL:
					((ContainerBookPages)ep.openContainer).scroll(data[0] > 0);
					break;
				case TICKER:
					((TileEntityInventoryTicker)tile).ticks = data[0];
					break;
					//case PYLONCLEAR:
					//	PylonGenerator.instance.clearDimension(data[0]);
					//	break;
				case SHARDBOOST: {
					Entity e = world.getEntityByID(data[0]);
					if (e instanceof EntityItem) {
						ChromaFX.spawnShardBoostedEffects((EntityItem)e);
					}
					TileEntityChroma te = (TileEntityChroma)world.getTileEntity(x, y, z);
					if (te != null)
						te.clear();
					break;
				}/*
			case FRAGPROGRAM: {
				ChromaResearch r = ChromaResearch.researchList[data[0]];
				int slot = data[1];
				ItemStack is = ep.inventory.mainInventory[slot];
				ItemInfoFragment.programShardAndGiveData(is, ep);
				break;
			}*/
				case GIVERESEARCH: {
					ChromaResearch r = ChromaResearch.researchList[data[0]];
					ChromaResearchManager.instance.givePlayerFragment(ep, r, true);
					break;
				}
				case LEAFBREAK:
					BlockPowerTree.breakEffectsClient(world, x, y, z, CrystalElement.elements[data[0]]);
					break;
				case GIVEPROGRESS: {
					ProgressStage p = ProgressStage.list[data[0]];
					ProgressionManager.instance.setPlayerStageClient(ep, p, data[1] > 0, true);
					break;
				}
				case HEALTHSYNC:
					AbilityHelper.instance.setHealthClient(ep, ReikaJavaLibrary.buildDoubleFromInts(data[0], data[1]));
					break;
				case INVCYCLE:
					AbilityHelper.instance.cycleInventoryClient(ep, data[0] > 0);
					break;
				case RELAYCONNECT: {
					int num = (data.length-1);
					ArrayList<Coordinate> li = new ArrayList();
					for (int i = 0; i < num; i += 3) {
						Coordinate c = new Coordinate(data[i+0], data[i+1], data[i+2]);
						li.add(c);
					}
					CrystalElement e = CrystalElement.elements[data[data.length-1]];
					ChromaFX.spawnRelayParticle(e, li);
					break;
				}
				case RERESEARCH: {
					ChromaResearch r = ChromaResearch.researchList[data[0]];
					ReikaInventoryHelper.findAndDecrStack(Items.paper, -1, ep.inventory.mainInventory);
					ReikaInventoryHelper.findAndDecrStack(ReikaItemHelper.inksac, ep.inventory.mainInventory);
					ItemChromaBook book = (ItemChromaBook)ep.getCurrentEquippedItem().getItem();
					ArrayList<ChromaResearch> li = book.getItemList(ep.getCurrentEquippedItem());
					li.add(r);
					book.setItems(ep.getCurrentEquippedItem(), li);
					book.recoverFragment(ep, r);
					break;
				}
				case BIOMEPAINT: {
					BiomeGenBase b = data[2] >= 0 ? BiomeGenBase.biomeList[data[2]] : ReikaWorldHelper.getNaturalGennedBiomeAt(world, data[0], data[1]);
					((TileEntityBiomePainter)tile).changeBiomeAt(data[0], data[1], b);
					break;
				}
				case LIGHTNINGDIE: {
					EntityBallLightning.receiveDeathParticles(world, dx, dy, dz, data[0]);
					break;
				}
				case CLOUDDIE: {
					Entity e = world.getEntityByID(data[0]);
					if (e instanceof EntityGlowCloud)
						((EntityGlowCloud)e).doDeathParticles();
					break;
				}
				case CLOUDATTACK: {
					Entity e = world.getEntityByID(data[0]);
					if (e instanceof EntityGlowCloud)
						((EntityGlowCloud)e).doAttackFX();
					break;
				}
				case GLUON: {
					ChromaFX.doGluonClientside(world, data[0], data[1]);
					break;
				}
				case AURAPOUCH: {
					ItemStack is = ep.getCurrentEquippedItem();
					if (ChromaItems.AURAPOUCH.matchWith(is)) {
						ItemAuraPouch iap = (ItemAuraPouch)is.getItem();
						iap.setSlotActive(is, data[0], data[1] > 0);
					}
					break;
				}
				case FARMERHARVEST: {
					((TileEntityFarmer)tile).doParticles(data[0], data[1], data[2]);
					break;
				}
				case PYLONCACHE: {
					ArrayList<Coordinate> li = new ArrayList();
					UUID uid = null;
					if (data[6] == -1 && data[7] == -1 && data[8] == -1 && data[9] == -1) {

					}
					else {
						long least = ReikaJavaLibrary.buildLong(data[6], data[7]);
						long most = ReikaJavaLibrary.buildLong(data[8], data[9]);
						uid = new UUID(most, least);
					}
					for (int i = 10; i < data.length; i += 3) {
						int cx = data[i];
						int cy = data[i+1];
						int cz = data[i+2];
						Coordinate c = new Coordinate(cx, cy, cz);
						li.add(c);
					}
					PylonGenerator.instance.cachePylonLocation(world, data[0], data[1], data[2], CrystalElement.elements[data[3]], li, data[4] > 0, uid, data[5] > 0);
					break;
				}
				case PYLONLINKCACHE: {
					PylonLinkNetwork.instance.load(NBT);
					break;
				}
				case PYLONCACHECLEAR: {
					PylonGenerator.instance.clearDimension(data[0]);
					break;
				}
				case TRANSITIONWAND: {
					((ItemTransitionWand)ep.getCurrentEquippedItem().getItem()).setMode(ep.getCurrentEquippedItem(), TransitionMode.list[data[0]]);
					break;
				}
				case NEWTELEPORT:
					AbilityHelper.instance.addWarpPoint(stringdata, ep);
					break;
				case TELEPORT:
					AbilityHelper.instance.gotoWarpPoint(stringdata, (EntityPlayerMP)ep);
					break;
				case MAPTELEPORT:
					AbilityHelper.instance.gotoMapWarpPoint(new WorldLocation(data[0], data[1], data[2], data[3]), (EntityPlayerMP)ep);
					break;
				case DELTELEPORT:
					AbilityHelper.instance.removeWarpPoint(stringdata, ep);
					break;
				case SENDTELEPORT:
					AbilityHelper.instance.addWarpPoint(stringdata, ep, new WorldLocation(data[0], data[1], data[2], data[3]));
					break;
				case GROWTH:
					ChromaFX.doGrowthWandParticles(world, data[0], data[1], data[2]);
					break;
				case PROGRESSNOTE:
					ChromaResearchManager.instance.notifyPlayerOfProgression(ep, ChromaResearchManager.instance.getProgressForID(data[0]));
					break;
				case PORTALRECIPE:
					PortalRecipe.onClientSideRandomTick((TileEntityCastingTable)tile, data[0]);
					break;
				case PYLONTURBORECIPE:
					PylonTurboRecipe.onClientSideRandomTick((TileEntityCastingTable)tile, data[0]);
					break;
				case REPEATERTURBORECIPE:
					RepeaterTurboRecipe.onClientSideRandomTick((TileEntityCastingTable)tile, data[0]);
					break;
				case HEATLAMP:
					((TileEntityHeatLamp)tile).temperature = data[0];
					break;
				case WANDCHARGE:
					CrystalWand.updateWandClient(ep, data);
					break;
				case BULKITEM: {
					ItemStack is = new ItemStack(Item.getItemById(data[0]), 1, data[1]);
					ItemBulkMover.setStoredItem(ep.getCurrentEquippedItem(), is);
					break;
				}
				case BULKNUMBER:
					ItemBulkMover.setNumberToCarry(ep.getCurrentEquippedItem(), data[0]);
					break;
				case CASTAUTOUPDATE:
					((CastingAutomationBlock)tile).getAutomationHandler().receiveUpdatePacket(world, data);
					break;
				case AUTORECIPE:
					CastingRecipe cr = !Strings.isNullOrEmpty(stringdata) ? RecipesCastingTable.instance.getRecipeByStringID(stringdata) : null;
					((CastingAutomationBlock)tile).getAutomationHandler().setRecipe(cr, data[0], data[1] > 0);
					break;
				case AUTOCANCEL:
					((CastingAutomationBlock)tile).getAutomationHandler().cancelCrafting();
					break;
				case CHAINGUNHURT:
					EntityChainGunShot.doDamagingParticles(data[0]);
					break;
				case CHAINGUNEND:
					EntityChainGunShot.doDestructionParticles(data[0]);
					break;
				case METRANSFER:
					((TileEntityMEDistributor)tile).spawnTransferParticles(world, x, y, z, data[0], data[1]);
					break;
				case MEDISTRIBTHRESH:
					((TileEntityMEDistributor)tile).setThreshold(data[0], data[1]);
					break;
				case MEDISTRIBFUZZY:
					((TileEntityMEDistributor)tile).toggleFuzzy(data[0]);
					break;
				case HOVERWAND: {
					ItemFlightWand.setMode(ep.getCurrentEquippedItem(), HoverType.list[data[0]]);
					break;
				}
				case AURATTACK:
					((TileEntityAuraPoint)tile).doAttackFX(world.getEntityByID(data[0]));
					break;
				case AURAHEAL:
					((TileEntityAuraPoint)tile).doHealFX(world.getEntityByID(data[0]));
					break;
				case AURAGROW:
					TileEntityAuraPoint.doGrowFX(world, x, y, z);
					break;
				case DESTROYNODE:
					NodeReceiverWrapper.triggerDestroyFX(world, x, y, z);
					break;
				case HURTNODE:
					NodeReceiverWrapper.triggerDamageFX(world, x, y, z);
					break;
				case CHARGINGNODE:
					NodeReceiverWrapper.triggerChargingFX(world, x, y, z);
					break;
				case HEALNODE:
					NodeReceiverWrapper.triggerHealFX(world, x, y, z);
					break;
				case NEWASPECTNODE:
					NodeReceiverWrapper.triggerNewAspectFX(world, x, y, z, Aspect.getAspect(stringdata));
					break;
				case SPLASHGUNATTACK:
					EntitySplashGunShot.doAttackParticles(data[0], data[1]);
					break;
				case SPLASHGUNEND:
					EntitySplashGunShot.doDestroyParticles(data[0]);
					break;
				case VACUUMGUNEND:
					EntityVacuum.doDestroyParticles(data[0]);
					break;
				case RFSEND:
					((TileEntityRFDistributor)tile).sendRFToClient(data[0], data[1], data[2], data[3]);
					break;
				case DIMPING:
					DimensionPingEvent.addPing(data[0], data[1], data[2]);
					break;
				case STRUCTUREENTRY:
					DimensionStructureType type = DimensionStructureType.types[data[0]];
					ChromaDimensionManager.addPlayerToStructureClient(ep, type);
					ChromaOverlays.instance.addStructureText(type);
					break;
				case CRYSTALMUS:
					((TileEntityCrystalMusic)tile).doParticles(world, data[0], data[1], data[2], CrystalElement.elements[data[3]]);
					break;
				case CRYSTALMUSERROR:
					((TileEntityCrystalMusic)tile).doErrorParticles(world, x, y, z);
					break;
				case MUSICCLEAR:
					((TileEntityCrystalMusic)tile).clearMusic();
					break;
				case MUSICCLEARCHANNEL:
					((TileEntityCrystalMusic)tile).clearChannel(data[0]);
					break;
				case MUSICNOTE:
					((TileEntityCrystalMusic)tile).addNote(data[0], MusicKey.getByIndex(data[1]), data[2], data[3] > 0);
					break;
				case FIXEDMUSICNOTE:
					((TileEntityCrystalMusic)tile).addNote(data[4], data[0], MusicKey.getByIndex(data[1]), data[2], data[3] > 0);
					break;
				case MUSICBKSP:
					((TileEntityCrystalMusic)tile).backspace(data[0]);
					break;
				case MUSICDEMO:
					((TileEntityCrystalMusic)tile).loadDemo();
					break;
					/*
				case MUSICDISC: {
					ItemStack is = ep.getCurrentEquippedItem();
					is.stackTagCompound = new NBTTagCompound();
					is.stackTagCompound.setString("file", stringdata);
					break;
				}
					 */
				case PYLONTURBOSTART:
					((TileEntityPylonTurboCharger)tile).doStartFXClient(world, x, y, z);
					break;
				case PYLONTURBOCOMPLETE:
					((TileEntityPylonTurboCharger)tile).doCompleteParticlesClient(world, x, y, z);
					break;
				case PYLONTURBOEVENT:
					if (tile != null)
						((TileEntityPylonTurboCharger)tile).doEventClient(world, x, y, z, data[0], data[1]);
					break;
				case PYLONTURBOFAIL:
					((TileEntityPylonTurboCharger)tile).doFailParticlesClient(world, x, y, z, data[0] > 0);
					break;
				case MUSICPLAY:
					((TileMusicMemory)tile).playKeyClient(MusicKey.getByIndex(data[0]));
					break;
				case TURRETATTACK:
					((TileEntityLumenTurret)tile).doAttackParticles(data[0]);
					break;
				case MONUMENTSTART:
					((TileEntityStructControl)tile).triggerMonumentClient(ep);
					break;
				case MONUMENTCOMPLETE:
					MonumentCompletionRitual.completeMonumentClient(world, data[0], data[1], data[2]);
					break;
				case RESETMONUMENT:
					MonumentCompletionRitual.resetSettings(world, data[0], data[1], data[2]);
					break;
				case MONUMENTEVENT:
					MonumentCompletionRitual.triggerMonumentEventClient(world, data[0], data[1], data[2], data[3], data[4], data[5]);
					break;
				case MONUMENTEND:
					((TileEntityStructControl)tile).endMonumentRitual();
					break;
				case DASH:
					ChromaFX.doDashParticles(world, (EntityPlayer)world.getEntityByID(data[0]), data[0] != ep.getEntityId());
					break;
				case FENCETRIGGER:
					((TileEntityCrystalFence)tile).triggerSegment(data[0], data[1] > 0);
					break;
				case MINERJAM:
					((TileEntityMiner)tile).doWarningParticles(world, x, y, z);
					break;
				case REPEATERCONN:
					((TileEntityCrystalRepeater)tile).refreshConnectionRender();
					break;
				case CHARGERTOGGLE:
					((TileEntityCrystalCharger)tile).toggle(CrystalElement.elements[data[0]]);
					break;
				case BOOKNOTE: {
					ItemChromaBook.addNoteText(ep.getCurrentEquippedItem(), stringdata);
					break;
				}
				case BOOKNOTESRESET: {
					ItemChromaBook.clearNoteTexts(ep.getCurrentEquippedItem());
					break;
				}
				case REPEATERSURGE: {
					TileEntityCrystalRepeater.overloadClient(world, x, y, z, CrystalElement.elements[data[0]]);
					break;
				}
				case FIREDUMP: {
					TileEntityGlowFire.emptyClientFX(world, x, y, z, data[0]);
					break;
				}
				case FIREDUMPSHOCK: {
					Entity e = world.getEntityByID(data[1]);
					if (e instanceof EntityPlayer)
						TileEntityGlowFire.dischargeIntoPlayerFX(world, x, y, z, CrystalElement.elements[data[0]], (EntityPlayer)e);
					break;
				}
				case FIRECONSUMEITEM: {
					TileEntityGlowFire.consumeItemFX(world, x, y, z, data[0]);
					break;
				}
				case ESSENTIAPARTICLE: {
					EssentiaPath.sendParticle(world, data[0], data[1], data[2], data[3], data[4], data[5], stringdata, data[6]);
					break;
				}
				case INSERTERMODE: {
					((TileEntityItemInserter)tile).setInsertionType(data[0], ((TileEntityItemInserter)tile).getInsertionType(data[0]).next());
					break;
				}
				case INSERTERCLEAR: {
					((TileEntityItemInserter)tile).removeCoordinate(data[0]);
					break;
				}
				case INSERTERCONNECTION: {
					((TileEntityItemInserter)tile).toggleConnection(data[0], data[1]);
					break;
				}
				case INSERTERACTION: {
					((TileEntityItemInserter)tile).sendItemClientside(data[0], data[1], data[2], data[3], data[4]);
					break;
				}
				case COBBLEGENEND: {
					((TileEntityCobbleGen)tile).endCraftingFX(world, x, y, z, data[0], data[1] > 0);
					break;
				}
				case LIGHTERACT: {
					((TileEntityCaveLighter)tile).doLightedParticles(data[0], data[1], data[2]);
					break;
				}
				case LIGHTERDELTAY: {
					((TileEntityCaveLighter)tile).doDeltaYParticles(data[0]);
					break;
				}
				case LIGHTEREND: {
					((TileEntityCaveLighter)tile).doCompletionParticles();
					break;
				}
				case POWERCRYSDESTROY: {
					((TileEntityChromaCrystal)tile).doDestroyParticles(world, x, y, z);
					break;
				}
				case PARTICLESPAWNER: {
					Coordinate c = Coordinate.readFromNBT("loc", NBT);
					((TileEntityParticleSpawner)c.getTileEntity(world)).particles.readFromNBT(NBT);
					break;
				}
				/*
				case PYLONJAR: {
					((TileEntityChromaCrystal)tile).doDestroyParticles(world, x, y, z);
					break;
				}
				 */
				case PYLONCRYSTALBREAK: {
					((TileEntityCrystalPylon)tile).doPowerCrystalBreakFX(world, x, y, z);
					break;
				}
				case WIRELESS: {
					((TileEntityWirelessPowered)tile).doEnergyRequestClient(world, x, y, z, data[0], data[1], data[2], CrystalElement.elements[data[3]], data[4]);
					break;
				}
				case METEORIMPACT: {
					EntityMeteorShot.doClientImpact(world, data[0]);
					break;
				}
				case ORECREATE: {
					TileEntityOreCreator.doOreCreationFX(world, x, y, z, data[0], data[1]);
					break;
				}
				case THROWNGEM: {
					Entity e = world.getEntityByID(data[0]);
					if (e instanceof EntityThrownGem) {
						((EntityThrownGem)e).doImpactFX(CrystalElement.elements[data[1]]);
					}
					break;
				}
				case FLAREMSG: {
					ChromaOverlays.instance.addFlareMessage(stringdata);
					break;
				}
				case FLAREATTACK: {
					Entity e = world.getEntityByID(data[0]);
					if (e instanceof EntityDimensionFlare) {
						OuterRegionsEvents.instance.doRejectAttack((EntityDimensionFlare)e, ep);
					}
					break;
				}
				case ASPECTMODE:
					((TileEntityAspectFormer)tile).stepMode();
					break;
				case FLUIDSEND:
					((TileEntityFluidDistributor)tile).sendFluidToClient(data[0], data[1], data[2], FluidRegistry.getFluid(data[3]), data[4]);
					break;
				case COLLECTORRANGE:
					if (data[0] > 0)
						((TileEntityItemCollector)tile).increaseRange(data[1]);
					else
						((TileEntityItemCollector)tile).decreaseRange(data[1]);
					break;
				case LEAVEDIM:
					ChromaDimensionManager.resetDimensionClient();
					break;
				case DIMSOUND:
					if (ChromaOptions.RECEIVEDIMSOUND.getState())
						ReikaSoundHelper.playClientSound(ChromaSounds.GOTODIM, ep, 0.75F, 1);
					break;
				case SKYRIVER_SYNC:
					SkyRiverManagerClient.handleRayUpdatePacket(NBT);
					break;
				case SKYRIVER_STATE:
					SkyRiverManagerClient.handleClientState(data[0]);
					break;
				case ACTIVEGATE:
					((TileEntityTeleportGate)tile).activateClientside(world, x, y, z);
					break;
				case GATECACHE:
					TileEntityTeleportGate.loadCacheFromNBT(NBT);
					break;
				case TRIGGERTELEPORT:
					TileEntityTeleportGate.startTriggerTeleport(new WorldLocation(data[0], data[1], data[2], data[3]), new WorldLocation(data[4], data[5], data[6], data[7]), (EntityPlayerMP)ep);
					break;
				case TELEPORTCONFIRM:
					this.handleTeleconfirm(data[0], ep);
					break;
				case BIOMELOCS:
					BiomeDistributor.fillFromPacket(NBT.getTagList("data", NBTTypes.BYTE.ID));
					break;
				case RELAYPRESSUREBASE:
					((TileEntityFluidRelay)tile).changeBasePressure(data[0]);
					break;
				case RELAYPRESSUREVAR:
					((TileEntityFluidRelay)tile).changeFunctionPressure(data[0]);
					break;
				case RELAYCLEAR:
					((TileEntityFluidRelay)tile).clearFilters();
					break;
				case RELAYCOPY:
					((TileEntityFluidRelay)tile).copyFilters();
					break;
				case RELAYAUTO:
					((TileEntityFluidRelay)tile).autoFilter = !((TileEntityFluidRelay)tile).autoFilter;
					break;
				case RELAYFLUID:
					((TileEntityFluidRelay)tile).sendFluidParticles(world, x, y, z, FluidRegistry.getFluid(data[0]));
					break;
				case RELAYFILTER:
					((TileEntityRelayFilter)tile).setFlag(CrystalElement.elements[data[0]], data[1] > 0);
					break;
				case ROUTERFILTERFLAG:
					((TileEntityRouterNode)tile).isBlacklist = data[0] > 0;
					((TileEntityRouterNode)tile).update();
					break;
				case ROUTERLINK:
					((TileEntityRouterHub)tile).addHighlight(new Coordinate(data[0], data[1], data[2]));
					break;
				case STRUCTFIND: {
					ItemStructureFinder.doHeldFX(ep, dx, dy, dz, Structures.structureList[data[0]], data[1] > 0);
					break;
				}
				case DATASCAN:
					TileEntityDataNode.doScanFX(world, x, y, z);
					break;
				case LORENOTE:
					LoreManager.instance.addLoreNote(ep, Towers.towerList[data[0]]);
					break;
				case LOREPUZZLECOMPLETE:
					LoreManager.instance.completeBoard(ep);
					break;
				case INSCRIBE:
					InscriptionRecipes.instance.getRecipeByID(data[0]).doFX(world, x, y, z);
					break;
				case TOWERLOC:
					LoreManager.instance.readTowersFromServer(NBT);
					break;
					/*
				case DIGARTEFACT:
					ArtefactSpawner.instance.checkPlayerBreakClient(world, data[0], data[1], data[2]);
					break;
				case ARTEFACTCONFIRM:
					ArtefactSpawner.instance.confirmUA(world, data[0], data[1], data[2]);
					break;
					 */
				case ARTEFACTCLICK:
					BlockUnknownArtefact.doInteractFX(world, x, y, z);
					break;
				case FERTILITYSEED:
					ItemFertilitySeed.doFertilizeFX(world, x, y, z);
					break;
				case NUKERLOC: {
					Entity e = world.getEntityByID(data[3]);
					if (e instanceof EntityPlayer)
						AbilityCalls.doNukerFX(world, data[0], data[1], data[2], (EntityPlayer)e);
					break;
				}
				case BURNERINV:
					if (data[0] > 0) {
						ep.closeScreen();
						ep.openGui(ChromatiCraft.instance, ChromaGuis.BURNERINV.ordinal(), world, x, y, z);
					}
					else {
						ep.openContainer.onContainerClosed(ep);
						ep.openContainer = ep.inventoryContainer;
					}
					break;
				case BROADCASTLINK:
					((TileEntityCrystalBroadcaster)tile).onConnectedParticles(CrystalElement.elements[data[0]]);
					break;
				case SPELLFAIL:
					ChromaClientEventController.doDimensionSpellFailParticles(world, dx, dy, dz);
					break;
				case UAFX:
					ItemUnknownArtefact.doUA_FX(world, dx, dy, dz, true);
					break;
				case STRUCTPASSNOTE:
					ProgressionManager.instance.addStructurePasswordNote(ep, data[0]);
					break;
				case MINERCATEGORY:
					((TileEntityMiner)tile).setCategory(data[0]);
					break;
				case BOTTLENECK:
					ChromaOverlays.instance.addBottleneckWarning(data[0], data[1], data[2], data[3], data[4], data[5] > 0, CrystalElement.elements[data[6]]);
					break;
				case VOIDMONSTERRITUAL:
					VoidMonsterDestructionRitual.handlePacket(data[0], data[1]);
					break;
				case ALVEARYEFFECT:
					((TileEntityLumenAlveary)tile).setEffectSelectionState(data[0], data[1] > 0);
					break;
				case SUPERBUILD:
					AbilityCalls.doSuperbuildFX(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[data[0]]);
					break;
				case OPTIMIZE:
					TileEntityNetworkOptimizer.runOptimizationStepFX(world, x, y, z, data[0]);
					break;
				case ENDEREYESYNC: {
					Entity e = world.getEntityByID(data[0]);
					if (e instanceof EntityEnderEyeT2) {
						((EntityEnderEyeT2)e).doSync(data[1]);
					}
					break;
				}
				case NODERECEIVERSYNC:
					WorldLocation loc = WorldLocation.readFromNBT("location", NBT);
					NodeRecharger.instance.updateClient(loc, NBT);
					break;
				case RFWEBSEND:
					RFWeb.doSendParticle(world, x, y, z, data[0], data[1], data[2], data[3]);
					break;
				case STRUCTSEED:
					long s = ReikaJavaLibrary.buildLong(data[0], data[1]);
					StructureCalculator.assignSeed(s);
					break;
				case RAYBLENDPING:
					RayBlendPuzzle.spawnPingParticle(world, CrystalElement.elements[data[0]], x, y, z);
					break;
				case RAYBLENDMIX:
					RayBlendPuzzle.CrystalMix.doParticle(world, dx, dy, dz, CrystalElement.elements[data[0]], data[1] > 0);
					break;
				case CONNECTIVITY:
					ProbeInfoOverlayRenderer.instance.markConnectivity(ep, CrystalElement.elements[data[0]], data[1] > 0, data[2] > 0);
					break;
				case STRUCTUREERROR:
					StructureErrorOverlays.instance.onBlockFailure(world, x, y, z, new BlockKey(Block.getBlockById(data[0]), data[1]));
					break;
			}
		}
		catch (NullPointerException e) {
			ChromatiCraft.logger.logError("TileEntity at "+x+", "+y+", "+z+" was deleted before its packet "+pack+" could be received!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	private void handleTeleconfirm(int data, EntityPlayer ep) {
		((GuiTeleportGate)Minecraft.getMinecraft().currentScreen).handleTriggerConfirm(data > 0);
	}

}
