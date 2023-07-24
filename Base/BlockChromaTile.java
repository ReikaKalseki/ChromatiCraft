/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.GuardianStoneManager;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaExtractable;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaPowered;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Auxiliary.Interfaces.VariableTexture;
import Reika.ChromatiCraft.Base.TileEntity.FluidEmitterChromaticBase;
import Reika.ChromatiCraft.Base.TileEntity.FluidIOChromaticBase;
import Reika.ChromatiCraft.Base.TileEntity.FluidReceiverChromaticBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityMassStorage;
import Reika.ChromatiCraft.GUI.Book.GuiMachineDescription;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.TileEntityAspectJar;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityDataNode;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCrystalLaser;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityMultiBuilder;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityChromaLamp;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityGuardianStone;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityAccelerator;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal.CrystalTier;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFunctionRelay;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityCrystalMusic;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityParticleSpawner;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityGlowFire;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityCrystalTank;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityDimensionCore;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRift;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTransportWindow;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.BlockTileEnum;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.Item.MusicDataItem;
import Reika.DragonAPI.Interfaces.TileEntity.AdjacentUpdateWatcher;
import Reika.DragonAPI.Interfaces.TileEntity.ConditionalUnbreakability;
import Reika.DragonAPI.Interfaces.TileEntity.HitAction;
import Reika.DragonAPI.Interfaces.TileEntity.RedstoneTile;
import Reika.DragonAPI.Interfaces.TileEntity.SidePlacedTile;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BotaniaHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.DartItemHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockChromaTile extends BlockTileEnum<TileEntityChromaticBase, ChromaTiles> implements IWailaDataProvider {

	private static final Random par5Random = new Random();

	private static final HashMap<Coordinate, Long> lastNoTileWarning = new HashMap();

	private final IIcon[][][] icons = new IIcon[16][6][2];

	public BlockChromaTile(Material par2Material) {
		super(par2Material);
		this.setCreativeTab(null);
		blockHardness = 5;
		blockResistance = 40;//10;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		if (c == null) {
			long time = System.currentTimeMillis();
			Coordinate loc = new Coordinate(x, y, z);
			Long get = lastNoTileWarning.get(loc);
			if (get == null || time-get.longValue() > 2000) {
				ChromatiCraft.logger.logError("No tile at location "+loc+" ["+loc.getBlock(world)+":"+loc.getBlockMetadata(world)+"] !?");
				lastNoTileWarning.put(loc, time);
			}
			return super.isSideSolid(world, x, y, z, dir);
		}
		switch(c) {
			case TABLE:
				return dir != ForgeDirection.UP;
			default:
				return super.isSideSolid(world, x, y, z, dir);
		}
	}

	@Override
	public float getExplosionResistance(Entity e, World world, int x, int y, int z, double eX, double eY, double eZ) {
		ChromaTiles t = ChromaTiles.getTile(world, x, y, z);
		if (t == ChromaTiles.TABLE || t == ChromaTiles.DATANODE || t == ChromaTiles.EXPLOSIONSHIELD)
			return Float.MAX_VALUE;
		if (t == ChromaTiles.LANDMARK)
			return 2.5F;
		return super.getExplosionResistance(e, world, x, y, z, eX, eY, eZ);
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		TileEntityBase te = (TileEntityBase)world.getTileEntity(x, y, z);
		if (te instanceof OwnedTile) {
			OwnedTile o = (OwnedTile)te;
			if (o.onlyAllowOwnersToMine() && !o.isOwnedByPlayer(ep))
				return -1;
		}
		if (te instanceof ConditionalUnbreakability) {
			if (((ConditionalUnbreakability)te).isUnbreakable(ep))
				return -1;
		}
		if (te instanceof SneakPop) {
			if (!((SneakPop)te).allowMining(ep))
				return -1;
		}
		if (te instanceof TileEntityDataNode)
			return -1;
		return super.getPlayerRelativeBlockHardness(ep, world, x, y, z);
	}

	@Override
	public final boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public final TileEntityChromaticBase createTileEntity(World world, int meta) {
		return (TileEntityChromaticBase)ChromaTiles.createTEFromIDAndMetadata(this, meta);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		int idx = 0;
		if (GuiMachineDescription.runningRender && ChromaTiles.getTileFromIDandMetadata(this, meta) == ChromaTiles.INJECTOR)
			idx = 1;
		return icons[meta][s][idx];
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		int meta = iba.getBlockMetadata(x, y, z);
		TileEntityChromaticBase te = (TileEntityChromaticBase)iba.getTileEntity(x, y, z);
		return icons[meta][s][te instanceof VariableTexture ? ((VariableTexture)te).getIconState(s) : 0];
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ep) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof HitAction) {
			((HitAction)te).onHit(world, x, y, z, ep);
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		if (c == ChromaTiles.LASER)
			return 15;
		if (c == ChromaTiles.LAMP)
			return 15;
		if (c == ChromaTiles.TANK) {
			TileEntityCrystalTank te = (TileEntityCrystalTank)world.getTileEntity(x, y, z);
			return te.getCurrentFluid() != null ? te.getCurrentFluid().getLuminosity() : 0;
		}
		if (c == ChromaTiles.ASPECTJAR) {
			TileEntityAspectJar te = (TileEntityAspectJar)world.getTileEntity(x, y, z);
			return te.hasAspects() ? 8 : 0;
		}
		if (c == ChromaTiles.STAND) {
			TileEntityItemStand te = (TileEntityItemStand)world.getTileEntity(x, y, z);
			return te.getItem() != null ? 6 : 0;
		}
		if (c == ChromaTiles.PYLONTURBO)
			return 15;
		return 0;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		ArrayList<ChromaTiles> tiles = ChromaTiles.getTilesForBlock(this);
		for (int i = 0; i < tiles.size(); i++) {
			ChromaTiles c = tiles.get(i);
			if (c.hasBlockRender()) {
				for (int k = 0; k < 6; k++) {
					String s = k == 0 ? "bottom" : k == 1 ? "top" : "side";
					String path = c.name().toLowerCase(Locale.ENGLISH)+"_"+s;
					icons[c.getBlockMetadata()][k][0] = ico.registerIcon("chromaticraft:tile/"+path);
					if (c.hasTextureVariants()) {
						icons[c.getBlockMetadata()][k][1] = ico.registerIcon("chromaticraft:tile/"+path+"_variant");
					}
				}
			}
		}
	}

	@Override
	public final boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		super.onNeighborBlockChange(world, x, y, z, b);
		ChromaTiles t = ChromaTiles.getTile(world, x, y, z);
		TileEntity te = world.getTileEntity(x, y, z);
		if (t == ChromaTiles.WINDOW) {
			((TileEntityTransportWindow)te).validateStructure();
		}
		else if (t.isSidePlaced()) {
			boolean flag = ((SidePlacedTile)te).checkLocationValidity();
			if (!flag) {
				((SidePlacedTile)te).drop();
			}
		}
		if (te instanceof AdjacentUpdateWatcher) {
			((AdjacentUpdateWatcher)te).onAdjacentUpdate(world, x, y, z, b);
		}
		if (te instanceof MultiBlockChromaTile) {
			((MultiBlockChromaTile)te).validateStructure();
		}
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion e) {
		ChromaTiles t = ChromaTiles.getTile(world, x, y, z);
		if (t == ChromaTiles.LANDMARK) {
			ReikaItemHelper.dropItem(world, x+par5Random.nextDouble(), y+par5Random.nextDouble(), z+par5Random.nextDouble(), t.getCraftedProduct());
		}
		super.onBlockExploded(world, x, y, z, e);
	}

	@Override
	public boolean canDropFromExplosion(Explosion e) {
		return false;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int s) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		TileEntityChromaticBase te = (TileEntityChromaticBase)world.getTileEntity(x, y, z);
		if (c.suppliesRedstone()) {
			return ((RedstoneTile)te).getStrongPower(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[s].getOpposite());
		}
		return 0;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int s) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		TileEntityChromaticBase te = (TileEntityChromaticBase)world.getTileEntity(x, y, z);
		if (c.suppliesRedstone()) {
			return ((RedstoneTile)te).getWeakPower(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[s].getOpposite());
		}
		return 0;
	}

	@Override
	public final boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int side, float par7, float par8, float par9) {
		super.onBlockActivated(world, x, y, z, ep, side, par7, par8, par9);
		if (ChromatiCraft.instance.isLocked())
			return false;
		if (ReikaPlayerAPI.isFakeOrNotInteractable(ep, x+0.5, y+0.5, z+0.5, 8))
			return false;
		if (ReikaRandomHelper.doWithChance(2))
			ChromaAux.spawnInteractionBallLightning(world, x, y, z, CrystalElement.randomElement());
		world.markBlockForUpdate(x, y, z);
		TileEntity te = world.getTileEntity(x, y, z);
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		ItemStack is = ep.getCurrentEquippedItem();

		if (ModList.DARTCRAFT.isLoaded() && DartItemHandler.getInstance().isWrench(is)) {
			ep.setCurrentItemOrArmor(0, null);
			ep.playSound("random.break", 1, 1);
			ep.attackEntityFrom(DamageSource.inWall, 2);
			ReikaChatHelper.write("Your tool has shattered into a dozen pieces.");
			return true;
		}
		if (ep.isSneaking() && !m.hasSneakActions()) {
			return false;
		}
		if (is != null && ChromaItems.isRegistered(is) && ChromaItems.getEntry(is).overridesRightClick(is)) {
			return false;
		}
		if (is != null && ReikaItemHelper.matchStackWithBlock(is, ChromaBlocks.ROUTERNODE.getBlockInstance()))
			return false;
		if (is != null && ModList.THAUMCRAFT.isLoaded() && is.getItem() == ThaumItemHelper.ItemEntry.WAND.getItem().getItem() && ReikaThaumHelper.getWandFocus(is) == ChromaItems.MANIPFOCUS.getItemInstance())
			return false;

		if (m == ChromaTiles.STAND && ep.isSneaking() && is == null) {
			if (!world.isRemote)
				((TileEntityItemStand)te).spreadItemWith(ep, is);
			return true;
		}

		if (m == ChromaTiles.TABLE && is != null && ModList.BOTANIA.isLoaded() && is.getItem() == BotaniaHandler.getInstance().wandID) {
			if (world.isRemote) {
				int[] colors = BotaniaHandler.getInstance().getWandColors(is);
				ReikaDyeHelper dye1 = ReikaDyeHelper.dyes[15-colors[0]]; //compensate for reversed color order
				ReikaDyeHelper dye2 = ReikaDyeHelper.dyes[15-colors[1]];
				((TileEntityCastingTable)te).onClickedWithBotaniaWand(dye1, dye2);
			}
			return true;
		}

		if (m == ChromaTiles.TABLE && ep.isSneaking() && is == null) {
			if (!world.isRemote)
				((TileEntityCastingTable)te).dumpAllStands();
			return true;
		}

		if (te instanceof ItemOnRightClick) {
			ItemStack ret = ((ItemOnRightClick)te).onRightClickWith(is, ep);
			((TileEntityBase)te).syncAllData(true);
			ep.setCurrentItemOrArmor(0, ret);
			return true;
		}

		if (ChromaItems.BUCKET.matchWith(is) && is.getItemDamage() == 0 && is.stackSize == 1 && te instanceof ChromaPowered) {
			if (((ChromaPowered)te).addChroma(1000)) {
				if (!ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
				return true;
			}
		}

		if (is != null && is.getItem() == Items.bucket && is.stackSize == 1 && te instanceof ChromaExtractable) {
			if (((ChromaExtractable)te).getChromaLevel() >= 1000) {
				((ChromaExtractable)te).removeLiquid(1000);
				if (!ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, ChromaItems.BUCKET.getStackOfMetadata(0));
				return true;
			}
		}

		if (ModList.THAUMCRAFT.isLoaded() && te instanceof TileEntityAspectJar && is != null) {
			TileEntityAspectJar jar = (TileEntityAspectJar)te;
			if (is.getItem() instanceof IEssentiaContainerItem && is.stackSize == 1) {
				IEssentiaContainerItem ieci = (IEssentiaContainerItem)is.getItem();
				AspectList al = ieci.getAspects(is);
				if (al != null && al.size() > 0) {
					Aspect a = al.getAspects()[0];
					int left = jar.addToContainer(a, al.getAmount(a));
					int added = al.getAmount(a)-left;
					ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "game.neutral.swim", 0.6F, (float)ReikaRandomHelper.getRandomPlusMinus(1, 1F));
					if (!ep.capabilities.isCreativeMode) {
						al.remove(a, added);
						ieci.setAspects(is, al);
						if (ieci == ThaumItemHelper.ItemEntry.PHIAL.getItem().getItem())
							is.setItemDamage(al.size() == 0 ? 0 : 1);
					}
					return true;
				}
				else {
					Aspect a = jar.getFirstAspect();
					if (a != null && jar.takeFromContainer(a, 8)) {
						al = new AspectList();
						al.add(a, 8);
						ieci.setAspects(is, al);
						if (ieci == ThaumItemHelper.ItemEntry.PHIAL.getItem().getItem())
							is.setItemDamage(al.size() == 0 ? 0 : 1);
						ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "game.neutral.swim", 0.6F, (float)ReikaRandomHelper.getRandomPlusMinus(1, 1F));
						return true;
					}
				}
			}
			else if (ReikaItemHelper.matchStacks(is, ChromaStacks.glowChunk)) {
				if (jar.upgradeForDirectDrain()) {
					if (!ep.capabilities.isCreativeMode)
						is.stackSize--;
				}
			}
		}

		if (m == ChromaTiles.MUSIC && is != null && is.getItem() instanceof MusicDataItem) {
			TileEntityCrystalMusic mus = (TileEntityCrystalMusic)te;
			mus.setTrack(((MusicDataItem)is.getItem()).getMusic(is));
			return true;
		}

		if (m == ChromaTiles.MULTIBUILDER) {
			TileEntityMultiBuilder tm = (TileEntityMultiBuilder)te;
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
			if (dir.offsetY != 0) {
				tm.cycleShape();
			}
			else {
				dir = dir.getOpposite();
				if (ep.isSneaking()) {
					tm.contractArea(dir);
				}
				else {
					tm.expandArea(dir);
				}
			}
			return true;
		}

		if (m == ChromaTiles.RITUAL) {
			TileEntityRitualTable tr = (TileEntityRitualTable)te;
			if (tr.isOwnedByPlayer(ep)) {
				tr.initEnhancementCheck(ep);
			}
		}

		if (ChromaItems.SHARD.matchWith(is) && is.getItemDamage() >= 16 && m == ChromaTiles.LAMP) {
			if (((TileEntityChromaLamp)te).addColor(CrystalElement.elements[is.getItemDamage()%16])) {
				if (!ep.capabilities.isCreativeMode)
					is.stackSize--;
			}
		}

		if (te instanceof TileEntityMassStorage) {
			TileEntityMassStorage ts = (TileEntityMassStorage)te;
			if (is != null && ts.isItemValidForSlot(0, is)) {
				ts.setInventorySlotContents(0, is.copy());
				ep.setCurrentItemOrArmor(0, null);
				ReikaSoundHelper.playSoundAtBlock(te, "random.pop", 1, 0.7F);
				return true;
			}
			else if (is == null) {
				ItemStack take = ts.removeLastItem();
				if (take != null) {
					ep.setCurrentItemOrArmor(0, take);
					ReikaSoundHelper.playSoundAtBlock(te, "random.pop");
					return true;
				}
			}
		}

		if (m == ChromaTiles.PARTICLES && is != null) {
			TileEntityParticleSpawner tp = (TileEntityParticleSpawner)te;
			if (is.getItem() == Items.book) {
				if (is.stackTagCompound != null && is.stackTagCompound.hasKey("particleprogram")) {
					tp.readCopyableData(is.stackTagCompound.getCompoundTag("particleprogram"));
					world.markBlockForUpdate(x, y, z);
					ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.stone);
				}
				else {
					is.stackTagCompound = new NBTTagCompound();
					NBTTagCompound tag = new NBTTagCompound();
					tp.writeCopyableData(tag);
					is.stackTagCompound.setTag("particleprogram", tag);
				}
			}
			else {
				FluidStack fs = ReikaFluidHelper.getFluidForItem(is);
				if (fs != null) {
					tp.markAsFluid(fs.getFluid());
					return true;
				}
			}
		}

		/*
		if (ChromaItems.SHARD.matchWith(is) && (m == ChromaTiles.WEAKREPEATER || m == ChromaTiles.SKYPEATER)) {
			CrystalRepeater tw = (CrystalRepeater)te;
			if (!world.isRemote) {
				CrystalElement e = CrystalElement.elements[is.getItemDamage()%16];
				if (CrystalNetworker.instance.checkConnectivity(e, tw)) {
					ChromaSounds.CAST.playSoundAtBlock(world, x, y, z);
					int rd = e.getRed();
					int gn = e.getGreen();
					int bl = e.getBlue();
					ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.COLOREDPARTICLE.ordinal(), te, 64, rd, gn, bl, 32, 8);
					//shows -1? ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.NUMBERPARTICLE.ordinal(), te, 64, tw.getSignalDepth(e));
				}
				else {
					ChromaSounds.ERROR.playSoundAtBlock(world, x, y, z);
				}
			}
			return true;
		}*/

		/*
		if (m == ChromaTiles.TURRET && ChromaItems.SHARD.matchWith(is) && is.getItemDamage() < 16) {
			TileEntityLumenTurret tile = (TileEntityLumenTurret)te;
			boolean flag = false;
			if (!tile.hasPassiveUpgrade && is.getItemDamage() == CrystalElement.GREEN.ordinal()) {
				tile.hasPassiveUpgrade = true;
				flag = true;
			}
			if (!tile.hasPlayerUpgrade && is.getItemDamage() == CrystalElement.LIGHTGRAY.ordinal()) {
				tile.hasPlayerUpgrade = true;
				flag = true;
			}
			if (flag) {

				return true;
			}
		}
		 */

		if (is != null && is.stackSize == 1 && m == ChromaTiles.TANK) {
			TileEntityCrystalTank tile = (TileEntityCrystalTank)te;
			FluidStack fs = ReikaFluidHelper.getFluidForItem(is);
			if (fs != null) {
				int drain = tile.fill(null, fs, false);
				if (drain == fs.amount) {
					tile.fill(null, fs, true);
					if (!ep.capabilities.isCreativeMode) {
						ItemStack is2 = FluidContainerRegistry.drainFluidContainer(is);
						ep.setCurrentItemOrArmor(0, is2);
					}
				}
				return true;
			}
			else if (FluidContainerRegistry.isEmptyContainer(is)) {
				FluidStack rem = tile.drain(null, tile.getCurrentFluidLevel(), false);
				if (rem != null) {
					ItemStack fill = FluidContainerRegistry.fillFluidContainer(rem, is);
					if (fill != null) {
						FluidStack removed = ReikaFluidHelper.getFluidForItem(fill);
						tile.drain(null, removed.amount, true);
						if (!ep.capabilities.isCreativeMode) {
							ep.setCurrentItemOrArmor(0, fill);
						}
					}
				}
				return true;
			}
		}

		if (ChromaItems.LENS.matchWith(is) && is.stackSize == 1 && te instanceof TileEntityCrystalLaser) {
			ItemStack ret = ((TileEntityCrystalLaser)te).swapLens(is);
			ep.setCurrentItemOrArmor(0, ret);
			return true;
		}

		if (m != ChromaTiles.TELEPORT) {
			if (te != null && ChromaAux.hasGui(world, x, y, z, ep) && ((TileEntityBase)te).isPlayerAccessible(ep)) {
				ep.openGui(ChromatiCraft.instance, ChromaGuis.TILE.ordinal(), world, x, y, z);
				return true;
			}
		}

		if (m == ChromaTiles.RITUAL) //to prevent the block-place issue
			return true;

		((TileEntityBase)te).syncAllData(true);
		return false;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(target.blockX, target.blockY, target.blockZ);
		ChromaTiles m = ChromaTiles.getTileFromIDandMetadata(this, meta);
		if (m == null)
			return null;
		TileEntity tile = world.getTileEntity(target.blockX, target.blockY, target.blockZ);
		ItemStack core = m.getCraftedProduct();/*
		if (core != null && m.isEnchantable()) {
			HashMap<Enchantment, Integer> ench = ((EnchantableMachine)tile).getEnchantments();
			ReikaEnchantmentHelper.applyEnchantments(core, ench);
		}*/
		if (core != null && m.hasNBTVariants()) {
			NBTTile nb = (NBTTile)tile;
			NBTTagCompound nbt = new NBTTagCompound();
			nb.getTagsToWriteToStack(nbt);
			core.stackTagCompound = nbt.hasNoTags() ? null : (NBTTagCompound)nbt.copy();
		}
		return core;
	}

	@Override
	public final boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
	{
		return false;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest)
	{
		if (this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, 0);
		return world.setBlockToAir(x, y, z);
	}

	private boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		if (ep.capabilities.isCreativeMode)
			return false;
		return true;
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta)
	{
		if (!this.canHarvest(world, ep, x, y, z))
			return;
		TileEntity te = world.getTileEntity(x, y, z);
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		if (m != null) {
			ItemStack is = m.getCraftedProduct();
			ArrayList li = new ArrayList();
			/*
			if (is != null && m.isEnchantable()) {
				HashMap<Enchantment,Integer> map = ((EnchantableMachine)te).getEnchantments();
				ReikaEnchantmentHelper.applyEnchantments(is, map);
			}*/
			if (is != null && m.hasNBTVariants()) {
				NBTTagCompound nbt = new NBTTagCompound();
				((NBTTile)te).getTagsToWriteToStack(nbt);
				is.stackTagCompound = (NBTTagCompound)(!nbt.hasNoTags() ? nbt.copy() : null);
			}
			if (m == ChromaTiles.GLOWFIRE) {
				float f = ((TileEntityGlowFire)te).isSmothered();
				if (f > 0 && ReikaRandomHelper.doWithChance(f))
					is = ChromaStacks.transformCore.copy();
			}
			if (m == ChromaTiles.PYLONLINK)
				is = ChromaBlocks.PYLONSTRUCT.getStackOf();
			if (m == ChromaTiles.PYLONTURBO && ((TileEntityChromaticBase)te).getOwners(false).isEmpty()) //fix for ones in accidental worldgen
				is = null;
			if (is != null) {
				li = ReikaJavaLibrary.makeListFrom(is);
			}
			if (m == ChromaTiles.ASPECTJAR && ((TileEntityAspectJar)te).hasDirectDrainUpgrade())
				li.add(ChromaStacks.glowChunk.copy());
			ReikaItemHelper.dropItems(world, x+par5Random.nextDouble(), y+par5Random.nextDouble(), z+par5Random.nextDouble(), li);
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		TileEntity te = world.getTileEntity(x, y, z);
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		if (m != null) {
			ItemStack is = m.getCraftedProduct();
			/*
			if (is != null && m.isEnchantable()) {
				HashMap<Enchantment,Integer> map = ((EnchantableMachine)te).getEnchantments();
				ReikaEnchantmentHelper.applyEnchantments(is, map);
			}*/
			if (is != null && m.hasNBTVariants()) {
				NBTTagCompound nbt = new NBTTagCompound();
				((NBTTile)te).getTagsToWriteToStack(nbt);
				is.stackTagCompound = (NBTTagCompound)(!nbt.hasNoTags() ? nbt.copy() : null);
			}
			if (is != null)
				li = ReikaJavaLibrary.makeListFrom(is);
		}
		return li;
	}

	@Override
	public final void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityItemCollector) {
			((TileEntityItemCollector)te).canIntake = false;
			ReikaWorldHelper.splitAndSpawnXP(world, x+0.5, y+0.5, z+0.5, ((TileEntityItemCollector)te).getExperience());
		}
		if (te instanceof TileEntityRift) {
			((TileEntityRift)te).resetOther();
		}
		if (te instanceof TileEntityGuardianStone) {
			GuardianStoneManager.instance.removeAreasForStone((TileEntityGuardianStone)te);
		}
		//if (te instanceof FiberIO) {
		//	((FiberIO)te).onBroken();
		//}
		//if (te instanceof TileEntityFiberOptic) {
		//	((TileEntityFiberOptic)te).removeFromNetwork();
		//}
		if (te instanceof CrystalNetworkTile) {
			((CrystalNetworkTile)te).removeFromCache();
		}
		if (te instanceof CrystalReceiver) {
			CrystalNetworker.instance.breakPaths((CrystalReceiver)te);
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public float getEnchantPowerBonus(World world, int x, int y, int z) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		if (c == ChromaTiles.FUNCTIONRELAY) {
			return ((TileEntityFunctionRelay)world.getTileEntity(x, y, z)).getEnchantPowerInRange(world, x, y, z);
		}
		return super.getEnchantPowerBonus(world, x, y, z);
	}

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;//ChromaTiles.getTileFromIDandMetadata(this, accessor.getMetadata()).getCraftedProduct();
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			currenttip.add(EnumChatFormatting.WHITE+this.getPickBlock(mov, world, x, y, z).getDisplayName());
		}*/
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		//if (/*LegacyWailaHelper.cacheAndReturn(acc)*/!currenttip.isEmpty())
		//	return currenttip;
		TileEntityChromaticBase te = (TileEntityChromaticBase)acc.getTileEntity();
		if (te.getTile() != ChromaTiles.MUSIC) //to prevent lag, and has nothing to sync anywyas
			te.syncAllData(te.getTile() == ChromaTiles.CHROMACRAFTER);
		if (te instanceof TileEntityRift) {
			WorldLocation loc = ((TileEntityRift)te).getLinkTarget();
			if (loc != null) {
				currenttip.add("Linked to "+loc);
			}
			else {
				currenttip.add("Unlinked");
			}
			return currenttip;
		}
		if (te instanceof TileEntityAccelerator) {
			currenttip.add(String.format("Time Acceleration Factor: %dx", 1+((TileEntityAccelerator)te).getAccel()));
		}
		if (te instanceof TileEntityFocusCrystal) {
			CrystalTier ct = ((TileEntityFocusCrystal)te).getTier();
			if (ct == CrystalTier.TURBOCHARGED)
				currenttip.add("Turbocharged");
		}
		if (te instanceof TileEntityDimensionCore) {
			CrystalElement e = ((TileEntityDimensionCore)te).getColor();
			currenttip.add("Color: "+e.displayName);
		}
		if (te instanceof TileEntityMassStorage) {
			TileEntityMassStorage ts = (TileEntityMassStorage)te;
			//currenttip.add(ts.getFilter().displayName());
			Map<KeyedItemStack, Integer> map = ts.getItemTypes();
			for (KeyedItemStack ks : map.keySet()) {
				int has = map.get(ks);
				currenttip.add(ks.getDisplayName()+" x"+has);
			}
		}
		if (te instanceof TileEntityCrystalTank) {
			TileEntityCrystalTank tank = (TileEntityCrystalTank)te;
			int amt = tank.getCurrentFluidLevel();
			int capacity = tank.getCapacity();
			Fluid f = tank.getCurrentFluid();
			if (amt > 0 && f != null) {
				currenttip.add(String.format("Tank: %dmB/%dmB of %s", amt, capacity, f.getLocalizedName()));
			}
			else {
				currenttip.add(String.format("Tank: Empty (Capacity %dmB)", capacity));
			}
		}
		else if (te instanceof FluidIOChromaticBase) {
			FluidIOChromaticBase liq = (FluidIOChromaticBase)te;
			Fluid in = liq.getFluidInInput();
			Fluid out = liq.getFluidInOutput();
			int amtin = liq.getInputLevel();
			int amtout = liq.getOutputLevel();
			String input = in != null ? String.format("%d/%d mB of %s", amtin, liq.getCapacity(), in.getLocalizedName()) : "Empty";
			String output = out != null ? String.format("%d/%d mB of %s", amtout, liq.getCapacity(), out.getLocalizedName()) : "Empty";
			currenttip.add("Input Tank: "+input);
			currenttip.add("Output Tank: "+output);
		}
		else if (te instanceof FluidReceiverChromaticBase) {
			FluidReceiverChromaticBase liq = (FluidReceiverChromaticBase)te;
			Fluid in = liq.getContainedFluid();
			int amt = liq.getLevel();
			String input = in != null ? String.format("%d/%d mB of %s", amt, liq.getCapacity(), in.getLocalizedName()) : "Empty";
			currenttip.add("Tank: "+input);
		}
		else if (te instanceof FluidEmitterChromaticBase) {
			FluidEmitterChromaticBase liq = (FluidEmitterChromaticBase)te;
			Fluid in = liq.getContainedFluid();
			int amt = liq.getLevel();
			String input = in != null ? String.format("%d/%d mB of %s", amt, liq.getCapacity(), in.getLocalizedName()) : "Empty";
			currenttip.add("Tank: "+input);
		}
		else if (te instanceof IFluidHandler) {
			FluidTankInfo[] tanks = ((IFluidHandler)te).getTankInfo(ForgeDirection.UP);
			if (tanks != null) {
				for (int i = 0; i < tanks.length; i++) {
					FluidTankInfo info = tanks[i];
					FluidStack fs = info.fluid;
					String input = fs != null ? String.format("%d/%d mB of %s", fs.amount, info.capacity, fs.getFluid().getLocalizedName(fs)) : "Empty";
					currenttip.add("Tank "+i+": "+input);
				}
			}
		}
		/*
		if (te instanceof LumenTile) {
			LumenTile lt = (LumenTile)te;
			ElementTagCompound tag = lt.getEnergy();
			currenttip.add("Stored Energy:");
			//StringBuilder sb = new StringBuilder();
			for (CrystalElement e : tag.elementSet()) {
				int amt = tag.getValue(e);
				int max = lt.getMaxStorage(e);
				currenttip.add(String.format("   %s%s: %d/%d", e.getChatColorString(), e.displayName, amt, max));
				//sb.append(String.format("%s%.0f%s%s%s", e.getChatColorString(), ReikaMathLibrary.getThousandBase(amt), ReikaEngLibrary.getSIPrefix(amt), EnumChatFormatting.RESET.toString(), "/"));
			}
			//sb.deleteCharAt(sb.length()-1);
			//currenttip.add(sb.toString());
		}*/
		/*
		if (te.getMachine().isEnchantable()) {
			if (((EnchantableMachine)te).hasEnchantments()) {
				currenttip.add("Enchantments: ");
				ArrayList<Enchantment> li = ((EnchantableMachine)te).getValidEnchantments();
				for (int i = 0; i < li.size(); i++) {
					Enchantment e = li.get(i);
					int level = ((EnchantableMachine)te).getEnchantment(e);
					if (level > 0)
						currenttip.add("  "+EnumChatFormatting.LIGHT_PURPLE.toString()+e.getTranslatedName(level));
				}
			}
		}*/
		if (te instanceof NBTTile) {
			NBTTile nb = (NBTTile)te;
			nb.addTooltipInfo(currenttip, acc.getPlayer().isSneaking());
		}
		ReikaJavaLibrary.removeDuplicates(currenttip);
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		String s1 = EnumChatFormatting.ITALIC.toString();
		String s2 = EnumChatFormatting.BLUE.toString();
		currenttip.add(s2+s1+"ChromatiCraft");*/
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

	@Override
	public final ChromaTiles getMapping(IBlockAccess world, int x, int y, int z) {
		return ChromaTiles.getTile(world, x, y, z);
	}

	@Override
	public ChromaTiles getMapping(int meta) {
		return ChromaTiles.getTileFromIDandMetadata(this, meta);
	}

}
