/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import thaumcraft.api.aspects.Aspect;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.PortalRecipe;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Block.BlockEnderTNT.TileEntityEnderTNT;
import Reika.ChromatiCraft.Block.BlockHeatLamp.TileEntityHeatLamp;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.BlockRangeLamp.TileEntityRangedLamp;
import Reika.ChromatiCraft.Block.Crystal.BlockPowerTree;
import Reika.ChromatiCraft.Container.ContainerBookPages;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Entity.EntityChainGunShot;
import Reika.ChromatiCraft.Entity.EntitySplashGunShot;
import Reika.ChromatiCraft.Entity.EntityVacuum;
import Reika.ChromatiCraft.Items.ItemInfoFragment;
import Reika.ChromatiCraft.Items.Tools.ItemAuraPouch;
import Reika.ChromatiCraft.Items.Tools.ItemBulkMover;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemFlightWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand.TransitionMode;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.ModInterface.CrystalWand;
import Reika.ChromatiCraft.ModInterface.NodeReceiverWrapper;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectFormer;
import Reika.ChromatiCraft.ModInterface.TileEntityMEDistributor;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomePainter;
import Reika.ChromatiCraft.TileEntity.TileEntityFarmer;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityRFDistributor;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityTeleportationPump;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityInventoryTicker;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntitySpawnerReprogrammer;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.IPacketHandler;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromatiPackets implements IPacketHandler {

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
				ReikaSoundHelper.playClientSound(s, sx, sy, sz, v, p);
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
				int value = inputStream.readInt();
				ReikaPacketHelper.updateTileEntityData(world, x, y, z, name, value);
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
			case BUFFERINC:
				PlayerElementBuffer.instance.upgradePlayerOnClient(ep);
				break;
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
				int mode = data[0];
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
			case TNT:
				((TileEntityEnderTNT)tile).setTarget(ep, data[0], data[1], data[2], data[3]);
				break;
			case BOOKINVSCROLL:
				((ContainerBookPages)ep.openContainer).scroll(data[0] > 0);
				break;
			case TICKER:
				((TileEntityInventoryTicker)tile).ticks = data[0];
				break;
			case PYLONCLEAR:
				PylonGenerator.instance.clearDimension(data[0]);
				break;
			case SHARDBOOST: {
				Entity e = world.getEntityByID(data[0]);
				if (e instanceof EntityItem) {
					ChromaFX.doShardBoostingFX((EntityItem)e);
				}
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
				ChromaResearchManager.instance.givePlayerFragment(ep, r);
				break;
			}
			case LEAFBREAK:
				BlockPowerTree.breakEffectsClient(world, x, y, z, CrystalElement.elements[data[0]]);
				break;
			case GIVEPROGRESS: {
				ProgressStage p = ProgressStage.list[data[0]];
				ProgressionManager.instance.setPlayerStageClient(ep, p, data[1] > 0);
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
				ItemStack is = ItemInfoFragment.getItem(r);
				ItemChromaBook book = (ItemChromaBook)ep.getCurrentEquippedItem().getItem();
				ArrayList<ItemStack> li = book.getItemList(ep.getCurrentEquippedItem());
				li.add(is);
				book.setItems(ep.getCurrentEquippedItem(), li);
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
				this.doGluonClientside(world, data);
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
				this.doGrowthParticles(world, data);
				break;
			case PROGRESSNOTE:
				ChromaResearchManager.instance.notifyPlayerOfProgression(ep, ChromaResearchManager.instance.getProgressForID(data[0]));
				break;
			case PORTALRECIPE:
				PortalRecipe.onClientSideRandomTick((TileEntityCastingTable)tile, data[0], data[1], data[2], data[3]);
				break;
			case HEATLAMP:
				((TileEntityHeatLamp)tile).temperature = data[0];
				break;
			case WANDCHARGE:
				CrystalWand.updateWandClient(ep, data);
				break;
			case BULKITEM:
				ItemStack is = new ItemStack(Item.getItemById(data[0]), 1, data[1]);
				ItemBulkMover.setStoredItem(ep.getCurrentEquippedItem(), is);
				break;
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
	private void doGrowthParticles(World world, int[] data) {
		int x = data[0];
		int y = data[1];
		int z = data[2];
		double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.0625);
		double vy = ReikaRandomHelper.getRandomPlusMinus(0.1875, 0.0625);
		double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.0625);
		EntityFX fx = new EntityBlurFX(world, x+0.5, y+0.125, z+0.5, vx, vy, vz).setColor(0, 192, 0).setScale(1).setLife(20).setGravity(0.25F);
		fx.noClip = true;
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@SideOnly(Side.CLIENT)
	private void doGluonClientside(World world, int[] data) {
		EntityBallLightning src = (EntityBallLightning)world.getEntityByID(data[0]);
		EntityBallLightning tgt = (EntityBallLightning)world.getEntityByID(data[1]);
		if (src == null || tgt == null) {
			//ChromatiCraft.logger.debug("Null ball lightning to receive effect???");
			return;
		}
		Vec3 vec = ReikaVectorHelper.getVec2Pt(src.posX, src.posY, src.posZ, tgt.posX, tgt.posY, tgt.posZ);
		double lenv = vec.lengthVector();
		for (float i = 0; i <= lenv; i += 0.125) {
			double f = i/lenv;
			double ddx = src.posX-vec.xCoord*f;
			double ddy = src.posY-vec.yCoord*f;
			double ddz = src.posZ-vec.zCoord*f;
			int c = ReikaColorAPI.mixColors(tgt.getRenderColor(), src.getRenderColor(), (float)f);
			int r = ReikaColorAPI.getRed(c);
			int g = ReikaColorAPI.getGreen(c);
			int b = ReikaColorAPI.getBlue(c);
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityBlurFX(world, ddx, ddy, ddz).setColor(r, g, b).setLife(8));
		}
		src.doBoltClient(tgt);
		tgt.doBoltClient(src);
	}

}
