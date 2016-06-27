/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.FluidRegistry;
import thaumcraft.api.aspects.Aspect;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.MonumentCompletionRitual;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Event.DimensionPingEvent;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.PortalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.RepeaterTurboRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.PylonTurboRecipe;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaOverlays;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityWirelessPowered;
import Reika.ChromatiCraft.Block.BlockActiveChroma.TileEntityChroma;
import Reika.ChromatiCraft.Block.BlockEnderTNT.TileEntityEnderTNT;
import Reika.ChromatiCraft.Block.BlockHeatLamp.TileEntityHeatLamp;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.BlockRangeLamp.TileEntityRangedLamp;
import Reika.ChromatiCraft.Block.Crystal.BlockPowerTree;
import Reika.ChromatiCraft.Block.Dimension.Structure.Music.BlockMusicMemory.TileMusicMemory;
import Reika.ChromatiCraft.Container.ContainerBookPages;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Entity.EntityChainGunShot;
import Reika.ChromatiCraft.Entity.EntityDimensionFlare;
import Reika.ChromatiCraft.Entity.EntityMeteorShot;
import Reika.ChromatiCraft.Entity.EntitySplashGunShot;
import Reika.ChromatiCraft.Entity.EntityThrownGem;
import Reika.ChromatiCraft.Entity.EntityVacuum;
import Reika.ChromatiCraft.Items.Tools.ItemAuraPouch;
import Reika.ChromatiCraft.Items.Tools.ItemBulkMover;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemFlightWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand.TransitionMode;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.ModInterface.CrystalWand;
import Reika.ChromatiCraft.ModInterface.EssentiaNetwork.EssentiaPath;
import Reika.ChromatiCraft.ModInterface.NodeReceiverWrapper;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectFormer;
import Reika.ChromatiCraft.ModInterface.TileEntityMEDistributor;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomePainter;
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
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCobbleGen;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityGlowFire;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityInventoryTicker;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntitySpawnerReprogrammer;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityFluidDistributor;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRFDistributor;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager;
import Reika.ChromatiCraft.World.Dimension.OuterRegionsEvents;
import Reika.ChromatiCraft.World.Dimension.SkyRiverManagerClient;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.PacketHandler;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.DataPacket;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

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
						data = new int[len];
						for (int i = 0; i < len; i++)
							data[i] = inputStream.readInt();
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
				case SPAWNERPROGRAM:
					TileEntitySpawnerReprogrammer prog = (TileEntitySpawnerReprogrammer)tile;
					prog.setMobType(stringdata);
					break;
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
						Chromabilities.triggerAbility(ep, c, data[1]);
					break;
				}
				case PYLONATTACK:
					if (tile instanceof TileEntityCrystalPylon)
						((TileEntityCrystalPylon)tile).particleAttack(data[0], data[1], data[2], data[3], data[4], data[5]);
					break;
				case ABILITYCHOOSE:
					((TileEntityRitualTable)tile).setChosenAbility(Chromabilities.getAbilityByInt(data[0]));
					break;
				case BUFFERSET:
					PlayerElementBuffer.instance.setPlayerCapOnClient(ep, data[0]);
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
					Chromabilities.setHealthClient(ep, data[0]);
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
					PylonGenerator.instance.cachePylonLocation(world, data[0], data[1], data[2], CrystalElement.elements[data[3]]);
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
					AbilityHelper.instance.gotoWarpPoint(stringdata, ep);
					break;
				case DELTELEPORT:
					AbilityHelper.instance.removeWarpPoint(stringdata, ep);
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
					((TileEntityCastingAuto)tile).receiveUpdatePacket(data);
					break;
				case AUTORECIPE:
					CastingRecipe cr = data[0] >= 0 ? RecipesCastingTable.instance.getRecipeByID(data[0]) : null;
					((TileEntityCastingAuto)tile).setRecipe(cr, data[1]);
					break;
				case AUTOCANCEL:
					((TileEntityCastingAuto)tile).cancelCrafting();
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
					((TileEntityAuraPoint)tile).doGrowFX(data[0], data[1], data[2]);
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
				case SPLASHGUNEND:
					EntitySplashGunShot.doDamagingParticles(data[0]);
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
				case MUSICDEMO:
					((TileEntityCrystalMusic)tile).loadDemo();
					break;
				case PYLONTURBOSTART:
					((TileEntityPylonTurboCharger)tile).doStartFXClient(world, x, y, z);
					break;
				case PYLONTURBOCOMPLETE:
					((TileEntityPylonTurboCharger)tile).doCompleteParticlesClient(world, x, y, z);
					break;
				case PYLONTURBOEVENT:
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
					((TileEntityCrystalRepeater)tile).triggerConnectionRender();
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
						((TileEntityItemCollector)tile).increaseRange();
					else
						((TileEntityItemCollector)tile).decreaseRange();
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
			}
		}
		catch (NullPointerException e) {
			ChromatiCraft.logger.logError("TileEntity at "+x+", "+y+", "+z+" was deleted before its packet "+pack+" could be received!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
