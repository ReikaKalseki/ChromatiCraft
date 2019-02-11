/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import Reika.ChromatiCraft.API.Event.CastingEvent;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FocusAcceleratable;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.VariableTexture;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalReceiver;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.ChromatiCraft.Render.Particle.EntityGlobeFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal;
import Reika.ChromatiCraft.World.IWG.PylonGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Interfaces.TileEntity.TriggerableAction;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;

public class TileEntityCastingTable extends InventoriedCrystalReceiver implements NBTTile, BreakAction, TriggerableAction, OwnedTile,
OperationInterval, MultiBlockChromaTile, FocusAcceleratable, VariableTexture {

	private CastingRecipe activeRecipe = null;
	private int craftingTick = 0;
	private int craftSoundTimer = 20000;
	private int craftingAmount;

	private EntityPlayer craftingPlayer;

	public boolean hasStructure = false;
	public boolean hasStructure2 = false;
	public boolean hasPylonConnections = false;
	private int tableXP;
	private RecipeType tier = RecipeType.CRAFTING;

	private boolean isEnhanced;

	private final HashSet<KeyedItemStack> completedRecipes = new HashSet();
	private final ItemHashMap<Integer> craftedItems = new ItemHashMap();

	public RecipeType getTier() {
		return tier;
	}

	public boolean isAtLeast(RecipeType type) {
		if (!this.getTier().isAtLeast(type))
			return false;
		switch(type) {
			case CRAFTING:
				return true;
			case TEMPLE:
				return hasStructure;
			case MULTIBLOCK:
				return hasStructure2;
			case PYLON:
				return hasPylonConnections;
		}
		return false;
	}

	private void setTier(RecipeType lvl) {
		if (lvl != tier) {
			tier = lvl;
			ChromaSounds.UPGRADE.playSoundAtBlock(this);
			if (worldObj.isRemote)
				this.particleBurst();
			this.validateStructure();
		}
	}

	public CastingRecipe getActiveRecipe() {
		return activeRecipe;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TABLE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (!world.isRemote && this.getTicksExisted() == 1) {
			this.evaluateRecipeAndRequest();
		}
		if (craftingTick > 0) {
			this.onCraftingTick(world, x, y, z);
		}

		if (isEnhanced && hasPylonConnections) {
			if (world.isRemote) {
				this.doEnhancedParticles(world, x, y, z);
			}
		}

		//ChromaStructures.getCastingLevelThree(world, x, y-1, z).place();

		if (DragonAPICore.debugtest) {
			this.addXP(800000);
			isEnhanced = false;

			for (CastingRecipe cr : RecipesCastingTable.instance.getAllRecipes()) {
				completedRecipes.add(new KeyedItemStack(cr.getOutput()));
			}
			this.markDirty();
			this.syncAllData(true);
		}

		//if (world.isRemote)
		//	this.spawnIdleParticles(world, x, y, z);

		/*
		Collection<CastingRecipe> li = RecipesCastingTable.instance.getAllRecipes();
		for (CastingRecipe cr : li) {
			if (cr instanceof TempleCastingRecipe) {
				TempleCastingRecipe t = (TempleCastingRecipe)cr;
				Map<Coordinate, CrystalElement> map = t.getRunes().getRunes();
				for (Coordinate c : map.keySet()) {
					Coordinate c2 = c.offset(x, y, z);
					CrystalElement e = map.get(c);
					c2.setBlock(world, ChromaBlocks.RUNE.getBlockInstance(), e.ordinal());
				}
			}
		}
		 */

		/*
		if (DragonAPICore.debugtest) {
			for (CastingRecipe c : RecipesCastingTable.instance.getAllRecipesMaking(ChromaStacks.crystalCore)) {
				if (c instanceof MultiBlockCastingRecipe) {
					MultiBlockCastingRecipe rc = (MultiBlockCastingRecipe)c;
					Map<List<Integer>, ItemMatch> map = rc.getAuxItems();
					for (int dx = x-4; dx <= x+4; dx += 2) {
						for (int dz = z-4; dz <= z+4; dz += 2) {
							int dy = Math.abs(dx) <= 2 && Math.abs(dz) <= 2 ? y : y+1;
							TileEntityItemStand te = (TileEntityItemStand)world.getTileEntity(dx, dy, dz);
							if (te != null) {
								te.setInventorySlotContents(0, null);
								te.syncAllData(true);
							}
						}
					}
					for (List<Integer> li : map.keySet()) {
						ItemMatch m = map.get(li);
						int dx = x+li.get(0);
						int dz = z+li.get(1);
						int dy = Math.abs(li.get(0)) <= 2 && Math.abs(li.get(1)) <= 2 ? y : y+1;
						TileEntityItemStand te = (TileEntityItemStand)world.getTileEntity(dx, dy, dz);
						ItemStack is = ReikaItemHelper.getSizedItemStack(ReikaJavaLibrary.getRandomCollectionEntry(rand, m.getItemList()).getItemStack(), 64);
						te.setInventorySlotContents(0, is);
						te.markDirty();
						te.syncAllData(true);
					}
					inv[4] = rc.getMainInput();
					break;
				}
			}
		}
		 */

		//ReikaJavaLibrary.pConsole(hasStructure, Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	private void doEnhancedParticles(World world, int x, int y, int z) {
		EntityFloatingSeedsFX fx = new EntityFloatingSeedsFX(world, x+0.5, y+0.5, z+0.5, rand.nextDouble()*360, ReikaRandomHelper.getRandomPlusMinus(0, 45D));
		fx.angleVelocity *= 16;
		fx.freedom *= 0.25;
		fx.tolerance *= 2;
		fx.particleVelocity *= 1.25;
		fx.setRapidExpand().setScale(1.5F);
		fx.setIcon(ChromaIcons.HOLE);
		//fx.setColor(CrystalElement.getBlendedColor(this.getTicksExisted(), 15));
		fx.setColor(ReikaColorAPI.getModifiedHue(0xff0000, (this.getTicksExisted()*3)%360));
		//fx.setColliding();
		fx.setGravity(-0.03125F*8);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	/*
	@SideOnly(Side.CLIENT)
	private void spawnIdleParticles(World world, int x, int y, int z) {
		CrystalElement e = CrystalElement.randomElement();
		EngravedRuneFX fx = new EngravedRuneFX(world, x, y, z, e, ForgeDirection.UP);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}
	 */
	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.validateStructure();
		craftingTick = 0;
	}

	public int getCraftingTick() {
		return craftingTick;
	}

	private void killCrafting() {
		craftingTick = 0;

		this.setStandLock(false);

		craftSoundTimer = 20000;
		//make something bad happen
	}

	private void onCraftingTick(World world, int x, int y, int z) {
		if (activeRecipe == null)
			return;
		if (world.isRemote) {
			this.spawnCraftingParticles(world, x, y, z);
		}

		craftSoundTimer++;
		ChromaSounds sound = activeRecipe.getSoundOverride(this, craftSoundTimer);
		if (sound != null) {
			sound.playSoundAtBlock(this, 2, 1);
			craftSoundTimer = 0;
		}
		else if (craftSoundTimer >= this.getSoundLength() && activeRecipe.getDuration() > 20) {
			craftSoundTimer = 0;
			ChromaSounds s = isEnhanced ? ChromaSounds.CRAFTING_BOOST : ChromaSounds.CRAFTING;
			s.playSoundAtBlock(this);
		}
		if (rand.nextInt(12) == 0) {
			float[] fa = activeRecipe.getHarmonics();
			if (fa != null) {
				for (float f : fa) {
					if (f != 1) {
						if (rand.nextInt(50) == 0) {
							ChromaSounds.CASTHARMONIC.playSoundAtBlock(this, 1, f);
						}
					}
				}
				if (rand.nextInt(25) == 0) {
					ChromaSounds.CASTHARMONIC.playSoundAtBlock(this, 1, 1);
				}
			}
		}
		//ReikaJavaLibrary.pConsole(craftingTick, Side.SERVER);
		activeRecipe.onRecipeTick(this);
		if (activeRecipe instanceof PylonCastingRecipe) {
			ElementTagCompound req = this.getRequiredEnergy();
			if (!energy.containsAtLeast(req)) {
				if (this.getCooldown() == 0 && checkTimer.checkCap()) {
					this.requestEnergyDifference(req);
				}
				return;
			}
		}
		craftingTick--;
		if (craftingTick <= 0) {
			if (world.isRemote) {
				activeRecipe.onCrafted(this, craftingPlayer, 1);
			}
			else {
				this.craft();
			}
			//craftingTick = activeRecipe.getDuration();
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnCraftingParticles(World world, int x, int y, int z) {
		if (this.getTier().isAtLeast(RecipeType.TEMPLE) && hasStructure) {
			BlockArray blocks = this.getStructureAccentLocations();

			for (int i = 0; i < blocks.getSize(); i++) {
				Coordinate c = blocks.getNthBlock(i);

				int dx = c.xCoord;
				int dy = c.yCoord;
				int dz = c.zCoord;
				double dd = ReikaMathLibrary.py3d(dx-x, dy-y, dz-z);
				double dr = rand.nextDouble();
				double px = 0.5+dr*(dx-x)+x;
				double py = 1+dr*(dy-y)+y;
				double pz = 0.5+dr*(dz-z)+z;
				//double v = 0;//.125;
				//double vx = v*(x-px)/dd;
				//double vy = v*(y-py)/dd;
				//double vz = v*(z-pz)/dd;
				Block b = world.getBlock(dx, dy, dz);
				CrystalElement e = CrystalElement.elements[(this.getTicksExisted()/20)%16];
				EntityLaserFX fx = new EntityLaserFX(e, world, px, py, pz).setScale(2);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		if (this.getTier().isAtLeast(RecipeType.MULTIBLOCK) && hasStructure2) {
			double a = 60*Math.sin(Math.toRadians((this.getTicksExisted()*4)%360));
			for (int i = 0; i < 360; i += 60) {
				double ang = Math.toRadians(a+i);
				double r = 2;
				double rx = x+0.5+r*Math.cos(ang);
				double ry = y;
				double rz = z+0.5+r*Math.sin(ang);
				double v = 0.0625;
				double vx = v*(x+0.5-rx);
				double vy = 0.0125+v*(y+0.5-ry);
				double vz = v*(z+0.5-rz);
				EntityGlobeFX fx = new EntityGlobeFX(world, rx, ry, rz, vx, vy, vz);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		if (this.getTier().isAtLeast(RecipeType.PYLON) && hasPylonConnections && activeRecipe instanceof PylonCastingRecipe) {
			BlockArray blocks = this.getStructureRuneLocations(((PylonCastingRecipe)activeRecipe).getRequiredAura());
			int mod = 17-blocks.getSize();
			if (blocks.getSize() > 0 && this.getTicksExisted()%mod == 0) {
				Coordinate c = blocks.getNthBlock(this.getTicksExisted()%blocks.getSize());
				int dx = c.xCoord;
				int dy = c.yCoord;
				int dz = c.zCoord;
				Block b = world.getBlock(dx, dy, dz);
				if (b == ChromaBlocks.RUNE.getBlockInstance()) {
					int meta = world.getBlockMetadata(dx, dy, dz);
					CrystalElement e = CrystalElement.elements[meta];
					double dd = ReikaMathLibrary.py3d(dx-x, dy-y, dz-z);
					double v = 0.125;
					double vx = v*(x-dx)/dd;
					double vy = v*(y-dy)/dd;
					double vz = v*(z-dz)/dd;
					int t = dd < 9 ? 70 : 80;
					EntityRuneFX fx = new EntityRuneFX(world, dx+0.5, dy+0.5, dz+0.5, vx, vy, vz, e).setLife(t).setScale(2);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}
	}

	public BlockArray getStructureAccentLocations() {
		BlockArray blocks = new BlockArray();
		blocks.addBlockCoordinate(xCoord-6, yCoord+5, zCoord);
		blocks.addBlockCoordinate(xCoord+6, yCoord+5, zCoord);

		blocks.addBlockCoordinate(xCoord, yCoord+5, zCoord-6);
		blocks.addBlockCoordinate(xCoord, yCoord+5, zCoord+6);

		blocks.addBlockCoordinate(xCoord+6, yCoord+4, zCoord+6);
		blocks.addBlockCoordinate(xCoord+6, yCoord+4, zCoord-6);
		blocks.addBlockCoordinate(xCoord-6, yCoord+4, zCoord-6);
		blocks.addBlockCoordinate(xCoord-6, yCoord+4, zCoord+6);
		return blocks;
	}

	public BlockArray getStructureRuneLocations(ElementTagCompound elements) {
		BlockArray blocks = new BlockArray();
		HashSet<BlockKey> li = new HashSet();
		for (CrystalElement e : elements.elementSet()) {
			li.add(new BlockKey(ChromaBlocks.RUNE.getBlockInstance(), e.ordinal()));
		}
		blocks.addBlockCoordinateIf(worldObj, xCoord-8, yCoord+2, zCoord+2, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord-8, yCoord+2, zCoord-2, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord-8, yCoord+2, zCoord+6, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord-8, yCoord+2, zCoord-6, li);

		blocks.addBlockCoordinateIf(worldObj, xCoord+8, yCoord+2, zCoord+2, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord+8, yCoord+2, zCoord-2, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord+8, yCoord+2, zCoord+6, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord+8, yCoord+2, zCoord-6, li);

		blocks.addBlockCoordinateIf(worldObj, xCoord+2, yCoord+2, zCoord-8, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord-2, yCoord+2, zCoord-8, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord+6, yCoord+2, zCoord-8, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord-6, yCoord+2, zCoord-8, li);

		blocks.addBlockCoordinateIf(worldObj, xCoord+2, yCoord+2, zCoord+8, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord-2, yCoord+2, zCoord+8, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord+6, yCoord+2, zCoord+8, li);
		blocks.addBlockCoordinateIf(worldObj, xCoord-6, yCoord+2, zCoord+8, li);
		return blocks;
	}

	public int getSoundLength() {
		switch(this.getTier()) {
			case CRAFTING:
				return 1;
			case TEMPLE:
				return 1;
			case MULTIBLOCK:
				//return 1;
			case PYLON:
				return 152;
			default:
				return 1;
		}
	}

	public void validateStructure() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord-1;
		int z = zCoord;
		FilledBlockArray b = ChromaStructures.getCastingLevelOne(world, x, y, z);
		FilledBlockArray b2 = ChromaStructures.getCastingLevelTwo(world, x, y, z);
		FilledBlockArray b3 = ChromaStructures.getCastingLevelThree(world, x, y, z);
		if (this.getTier().isAtLeast(RecipeType.PYLON)) {
			if (b3.matchInWorld()) {
				hasStructure = hasStructure2 = hasPylonConnections = true;
			}
			else if (b2.matchInWorld()) {
				hasStructure = hasStructure2 = true;
				hasPylonConnections = false;
			}
			else if (b.matchInWorld()) {
				hasStructure = true;
				hasStructure2 = hasPylonConnections = false;
			}
			else {
				hasStructure = hasStructure2 = hasPylonConnections = false;
			}
		}
		else if (this.getTier().isAtLeast(RecipeType.MULTIBLOCK)) {
			if (b2.matchInWorld()) {
				hasStructure = hasStructure2 = true;
			}
			else if (b.matchInWorld()) {
				hasStructure = true;
				hasStructure2 = false;
			}
			else {
				hasStructure = hasStructure2 = false;
			}
			hasPylonConnections = false;
		}
		else if (this.getTier().isAtLeast(RecipeType.TEMPLE)) {
			if (b.matchInWorld()) {
				hasStructure = true;
			}
			else {
				hasStructure = false;
			}
			hasStructure2 = hasPylonConnections = false;
		}
		else {
			hasStructure = hasStructure2 = hasPylonConnections = false;
		}

		if (hasStructure2)
			ProgressStage.MULTIBLOCK.stepPlayerTo(this.getPlacer());

		if (activeRecipe != null && !this.getValidRecipeTypes().contains(activeRecipe.type)) {
			if (craftingTick > 0) {
				this.killCrafting();
			}
			activeRecipe = null;
		}
		this.syncAllData(true);
	}

	public boolean triggerCrafting(EntityPlayer ep) {
		if (activeRecipe != null && craftingTick == 0) {
			if (activeRecipe.canRunRecipe(ep) && this.isOwnedByPlayer(ep)) {
				craftingPlayer = ep;
				if (worldObj.isRemote)
					return true;
				this.syncAllData(true);
				ChromaSounds.CAST.playSoundAtBlock(this);

				this.setStandLock(true);

				if (activeRecipe.canBeStacked()) {
					craftingAmount = 1;
				}
				else {
					craftingAmount = inv[4].stackSize;
					if (activeRecipe instanceof MultiBlockCastingRecipe) {
						MultiBlockCastingRecipe mult = (MultiBlockCastingRecipe)activeRecipe;
						HashMap<WorldLocation, ItemMatch> map = mult.getOtherInputs(worldObj, xCoord, yCoord, zCoord);
						for (WorldLocation loc : map.keySet()) {
							TileEntityItemStand te = (TileEntityItemStand)loc.getTileEntity(worldObj);//loc.getTileEntity();
							if (te != null) {
								craftingAmount = Math.min(craftingAmount, te.getStackInSlot(0).stackSize);
							}
						}
					}
				}

				this.setRecipeTickDuration(activeRecipe);
				if (activeRecipe instanceof PylonCastingRecipe) {
					this.requestEnergyDifference(this.getRequiredEnergy());
				}
				return true;
			}
		}
		ChromaSounds.ERROR.playSoundAtBlock(this);
		return false;
	}

	public ElementTagCompound getRequiredEnergy() {
		if (activeRecipe instanceof PylonCastingRecipe) {
			ElementTagCompound tag = ((PylonCastingRecipe)activeRecipe).getRequiredAura();
			return tag.scale(craftingAmount);
		}
		return null;
	}

	private void setRecipeTickDuration(CastingRecipe r) {
		int t = r.getDuration();
		if (isEnhanced)
			t = Math.max(t/r.getEnhancedTableAccelerationFactor(), Math.min(t, 20));
		if (r.canBeStacked())
			t *= r.getRecipeStackedTimeFactor(this, craftingAmount);
		if (t > 20 && r instanceof MultiBlockCastingRecipe) {
			t = Math.max(20, (int)(t/TileEntityFocusCrystal.getSummedFocusFactor(this)));
		}
		craftingTick = t;
	}

	public boolean isReadyToCraft() {
		return craftingTick == 0 && inv[9] == null;
	}

	/*
	private boolean getRecipeRequirements() {
		if (activeRecipe instanceof PylonRecipe) {
			ElementTagCompound req = ((PylonRecipe)activeRecipe).getRequiredAura();
			return energy.containsAtLeast(req);
		}
		else {
			return true;
		}
	}*/

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		this.readRecipes(NBT);

		if (NBT.hasKey("crafter") && worldObj != null && craftingPlayer == null) {
			UUID uid = UUID.fromString(NBT.getString("crafter"));
			craftingPlayer = worldObj.func_152378_a(uid);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		this.writeRecipes(NBT);

		if (craftingPlayer != null)
			NBT.setString("crafter", craftingPlayer.getUniqueID().toString());
	}

	private void writeRecipes(NBTTagCompound NBT) {
		NBTTagList li = new NBTTagList();
		for (KeyedItemStack is : completedRecipes) {
			NBTTagCompound tag = new NBTTagCompound();
			is.getItemStack().writeToNBT(tag);
			li.appendTag(tag);
		}
		NBT.setTag("recipes", li);

		li = new NBTTagList();
		for (ItemStack is : craftedItems.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			is.writeToNBT(tag);
			tag.setInteger("total", craftedItems.get(is));
			li.appendTag(tag);
		}
		NBT.setTag("counts", li);
	}

	private void readRecipes(NBTTagCompound NBT) {
		completedRecipes.clear();
		NBTTagList li = NBT.getTagList("recipes", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			ItemStack is = ItemStack.loadItemStackFromNBT(tag);
			completedRecipes.add(new KeyedItemStack(is));
		}

		craftedItems.clear();
		li = NBT.getTagList("counts", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			ItemStack is = ItemStack.loadItemStackFromNBT(tag);
			int amt = tag.getInteger("total");
			craftedItems.put(is, amt);
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasPylonConnections = NBT.getBoolean("pylons");
		hasStructure = NBT.getBoolean("struct");
		hasStructure2 = NBT.getBoolean("struct2");

		tableXP = NBT.getInteger("xp");
		tier = RecipeType.typeList[NBT.getInteger("tier")];

		craftingTick = NBT.getInteger("craft");

		isEnhanced = NBT.getBoolean("enhance");

		craftingAmount = NBT.getInteger("crafting");

		if (NBT.hasKey("crafter") && this.isInWorld())
			craftingPlayer = worldObj.func_152378_a(UUID.fromString(NBT.getString("crafter")));
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);
		NBT.setBoolean("struct2", hasStructure2);
		NBT.setBoolean("pylons", hasPylonConnections);

		NBT.setInteger("tier", tier.ordinal());
		NBT.setInteger("xp", tableXP);

		NBT.setInteger("craft", craftingTick);

		NBT.setBoolean("enhance", isEnhanced);

		NBT.setInteger("crafting", craftingAmount);

		if (craftingPlayer != null) {
			NBT.setString("crafter", craftingPlayer.getUniqueID().toString());
		}
	}

	private void craft() {
		CastingRecipe recipe = activeRecipe;
		CastingRecipe cachedRecipe = recipe;
		//ReikaJavaLibrary.pConsole(recipe, Side.SERVER);
		int count = 0;
		boolean repeat = false;
		NBTTagCompound NBTin = null;
		int xpToAdd = 0;
		while (activeRecipe == recipe && (count+(activeRecipe.getOutput().getMaxStackSize() == 1 ? 0 : 1))*activeRecipe.getOutput().stackSize < activeRecipe.getOutput().getMaxStackSize()) {
			if (inv[4] != null)
				NBTin = recipe.getOutputTag(craftingPlayer, inv[4].stackTagCompound);
			xpToAdd += (int)(activeRecipe.getExperience()*this.getXPModifier(activeRecipe));
			if (activeRecipe instanceof MultiBlockCastingRecipe) {
				MultiBlockCastingRecipe mult = (MultiBlockCastingRecipe)activeRecipe;
				HashMap<WorldLocation, ItemMatch> map = mult.getOtherInputs(worldObj, xCoord, yCoord, zCoord);
				for (WorldLocation loc : map.keySet()) {
					TileEntityItemStand te = (TileEntityItemStand)loc.getTileEntity(worldObj);
					//ReikaJavaLibrary.pConsole(te+":"+te.getStackInSlot(0), Side.SERVER);
					if (te != null) {
						//ReikaJavaLibrary.pConsole(loc+" @ "+te.getStackInSlot(0), Side.SERVER);
						ItemStack is = te.getStackInSlot(0);
						if (FluidContainerRegistry.isFilledContainer(is)) {
							is = FluidContainerRegistry.drainFluidContainer(is);
							te.setInventorySlotContents(0, is.copy());
						}
						else
							ReikaInventoryHelper.decrStack(0, te, 1);
						te.syncAllData(true);
					}
				}
				//ReikaJavaLibrary.pConsole("count="+(count+1)+", decr'ing stands");
			}
			for (int i = 0; i < 9; i++) {
				if (i == 4) {
					ItemStack ret = recipe.getCentralLeftover(inv[i]);
					if (ret != null) {
						inv[i] = ret;
						continue;
					}
				}
				if (inv[i] != null) {
					ItemStack container = inv[i].getItem().getContainerItem(inv[i]);
					if (container == null) {
						int amt = 1;
						if (recipe instanceof MultiBlockCastingRecipe)
							amt = ((MultiBlockCastingRecipe)recipe).getRequiredCentralItemCount();
						ReikaInventoryHelper.decrStack(i, this, amt);
					}
					else {
						container = container.copy();
						container.stackSize = 1;
						if (inv[i].stackSize == 1) {
							inv[i] = container;
						}
						else {
							ReikaInventoryHelper.decrStack(i, inv);
							ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+1.25, zCoord+0.5, container);
						}
					}
				}
			}
			count += 1;
			if (activeRecipe instanceof PylonCastingRecipe) {
				energy.subtract(((PylonCastingRecipe)activeRecipe).getRequiredAura());
			}
			activeRecipe = recipe;
			recipe = this.getValidRecipe();
			if (!activeRecipe.canBeStacked() && recipe == activeRecipe) {
				this.setRecipeTickDuration(activeRecipe);
				ChromaSounds.CAST.playSoundAtBlock(this);
				repeat = true;
				break;
			}
		}
		if (count > 0) {
			CastingRecipe temp = activeRecipe;
			activeRecipe.onCrafted(this, craftingPlayer, count); //this resets the recipe
			activeRecipe = temp;
			ProgressStage.CASTING.stepPlayerTo(craftingPlayer);
			if (activeRecipe instanceof PylonCastingRecipe) {
				ProgressStage.LINK.stepPlayerTo(craftingPlayer);
			}
		}
		ItemStack out = activeRecipe.getOutput();
		while (count > 0) {
			//ReikaJavaLibrary.pConsole("count="+count+", adding "+activeRecipe.getOutput().stackSize+" output");
			//ReikaJavaLibrary.pConsole("tags "+(inv[9] != null ? inv[9].stackTagCompound : "null")+", "+out.stackTagCompound);
			//ReikaJavaLibrary.pConsole("pre "+inv[9]);
			count--;

			ItemStack toadd = ReikaItemHelper.getSizedItemStack(out, activeRecipe.getOutput().stackSize);
			NBTTagCompound NBTout = NBTin != null ? (NBTTagCompound)NBTin.copy() : null;
			if (NBTout != null) {
				ReikaNBTHelper.combineNBT(NBTout, toadd.stackTagCompound);
				toadd.stackTagCompound = (NBTTagCompound)NBTout.copy();
			}
			toadd.stackTagCompound = activeRecipe.handleNBTResult(this, craftingPlayer, NBTin, toadd.stackTagCompound);
			activeRecipe.setOwner(toadd, craftingPlayer);
			ReikaInventoryHelper.addOrSetStack(toadd, inv, 9);
			//ReikaJavaLibrary.pConsole("post "+inv[9]);

			this.addCrafted(out, 1);
			if (inv[9] != null) {
				MinecraftForge.EVENT_BUS.post(new CastingEvent(this, activeRecipe, craftingPlayer, inv[9].copy()));
				int push = inv[9].stackSize;
				for (int i = 0; i < 6; i++) {
					TileEntity te = this.getAdjacentTileEntity(dirs[i]);
					if (te instanceof IInventory) {
						int amt = Math.min(inv[9].getMaxStackSize(), push);
						boolean flag = false;
						do {
							if (ReikaInventoryHelper.addToIInv(ReikaItemHelper.getSizedItemStack(inv[9], amt), (IInventory)te)) {
								flag = true;
								ReikaInventoryHelper.decrStack(9, this, amt);
								push -= amt;
							}
						}
						while (flag && inv[9] != null);
						if (inv[9] == null)
							break;
					}
				}
			}
		}
		this.addXP(xpToAdd);
		if (inv[9] != null)
			repeat = false;
		RecipesCastingTable.setPlayerHasCrafted(craftingPlayer, cachedRecipe.type);
		if (!repeat) {
			activeRecipe = null;
			craftSoundTimer = 20000;
			craftingTick = 0;
			craftingPlayer = null;
		}
		ChromaSounds.CRAFTDONE.playSoundAtBlock(this);
		if (worldObj.isRemote)
			this.particleBurst();

		this.setStandLock(false);
	}

	private void setStandLock(boolean lock) {
		for (TileEntityItemStand te : this.getOtherStands().values()) {
			te.lock(lock);
		}
	}

	private float getXPModifier(CastingRecipe recipe) {
		Integer get = craftedItems.get(recipe.getOutput());
		int max = recipe.getPenaltyThreshold();
		if (get != null && get.intValue() >= max) {
			float mult = recipe.getPenaltyMultiplier();
			float fac = (float)Math.pow(mult, get.intValue()-max);
			return fac;
		}
		return 1;
	}

	private void addCrafted(ItemStack is, int count) {
		Integer get = craftedItems.get(is);
		int has = get != null ? get.intValue() : 0;
		craftedItems.put(is, has+count);
	}

	public void breakBlock() {
		HashMap<List<Integer>, TileEntityItemStand> tiles = this.getOtherStands();
		for (TileEntityItemStand te : tiles.values()) {
			te.setTable(null);
		}
		tiles.clear();
	}

	@SideOnly(Side.CLIENT)
	private void particleBurst() {
		for (int i = 0; i < 128; i++) {
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.125);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			EntitySparkleFX fx = new EntitySparkleFX(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, vx, vy, vz).setScale(1.5F);
			fx.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private void addXP(int experience) {
		if (!worldObj.isRemote) {
			tableXP += experience;
			if (tableXP >= tier.levelUp) {
				this.setTier(tier.next());
			}
			if (tableXP > 1000000) {
				EntityPlayer ep = this.getPlacer();
				if (ep != null && !ReikaPlayerAPI.isFake(ep)) {
					if (ProgressStage.CTM.isPlayerAtStage(ep)) {
						isEnhanced = true;
					}
				}
			}
			this.syncAllData(false);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public int getXP() {
		return tableXP;
	}

	private void spawnParticles(World world, int x, int y, int z) {

	}

	@Override
	public void markDirty() {
		super.markDirty();

		CastingRecipe r = this.getValidRecipe();
		if (inv[9] != null)
			r = null;
		this.changeRecipe(r);
	}

	private void changeRecipe(CastingRecipe r) {
		if (r == null || r != activeRecipe || r.type != RecipeType.PYLON) {
			CrystalNetworker.instance.breakPaths(this);
			if (r == null || r != activeRecipe) {
				this.killCrafting();
			}
		}/*
		else if (r != activeRecipe) {
			ElementTagCompound tag = ((PylonRecipe)r).getRequiredAura();
			tag.subtract(energy);
			for (CrystalElement e : tag.elementSet()) {
				this.requestEnergy(e, tag.getValue(e));
			}
		}*/
		activeRecipe = r;
	}

	private CastingRecipe getValidRecipe() {
		CastingRecipe r = RecipesCastingTable.instance.getRecipe(this, this.getValidRecipeTypes());
		if (worldObj.provider.dimensionId != 0) {
			if (r instanceof TempleCastingRecipe && !PylonGenerator.instance.canGenerateIn(worldObj))
				r = null;
		}
		//ReikaJavaLibrary.pConsole(r);
		if (r instanceof MultiBlockCastingRecipe) {
			MultiBlockCastingRecipe m = (MultiBlockCastingRecipe)r;
			HashMap<List<Integer>, TileEntityItemStand> map = this.getOtherStands();
			for (List<Integer> key : map.keySet()) {
				int i = key.get(0);
				int k = key.get(1);
				int dx = xCoord+i;
				int dz = zCoord+k;
				int dy = yCoord+(Math.abs(i) != 4 && Math.abs(k) != 4 ? 0 : 1);
				TileEntityItemStand te = (TileEntityItemStand)worldObj.getTileEntity(dx, dy, dz);
				te.setTable(this);
			}
		}
		return r;
	}

	private ArrayList<RecipeType> getValidRecipeTypes() {
		ArrayList<RecipeType> li = new ArrayList();
		li.add(RecipeType.CRAFTING);
		if (tier.isAtLeast(RecipeType.TEMPLE) && hasStructure) {
			li.add(RecipeType.TEMPLE);
			if (tier.isAtLeast(RecipeType.MULTIBLOCK) && hasStructure2) {
				li.add(RecipeType.MULTIBLOCK);
				if (tier.isAtLeast(RecipeType.PYLON) && hasPylonConnections)
					li.add(RecipeType.PYLON);
			}
		}
		return li;
	}

	private void evaluateRecipeAndRequest() {
		CastingRecipe r = this.getValidRecipe();
		if (r != null && r != activeRecipe && r instanceof PylonCastingRecipe) {
			ElementTagCompound tag = this.getRequiredEnergy();
			this.requestEnergyDifference(tag);
		}
		activeRecipe = r;
	}

	public HashMap<List<Integer>, TileEntityItemStand> getOtherStands() {
		HashMap<List<Integer>, TileEntityItemStand> li = new HashMap();
		for (int i = -4; i <= 4; i += 2) {
			for (int k = -4; k <= 4; k += 2) {
				int dx = xCoord+i;
				int dz = zCoord+k;
				int dy = yCoord+(Math.abs(i) != 4 && Math.abs(k) != 4 ? 0 : 1);
				ChromaTiles c = ChromaTiles.getTile(worldObj, dx, dy, dz);
				if (c == ChromaTiles.STAND) {
					TileEntityItemStand te = (TileEntityItemStand)worldObj.getTileEntity(dx, dy, dz);
					li.put(Arrays.asList(i, k), te);
				}
			}
		}
		return li;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getSizeInventory() {
		return 10;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return slot != 9;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return slot == 9;
	}

	@Override
	public void onPathBroken(CrystalFlow p, FlowFail f) {
		//this.killCrafting();
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return isEnhanced ? 1000 : Math.min(1000, Math.max(100, 100*(tableXP/RecipeType.MULTIBLOCK.levelUp-1)));
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getReceiveRange() {
		return 24;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return Integer.MAX_VALUE;//250000;
	}

	public boolean isCrafting() {
		return activeRecipe != null;
	}

	public ArrayList<CrystalTarget> getTargets() {
		ArrayList<CrystalTarget> li = new ArrayList();
		return li;
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		super.getTagsToWriteToStack(NBT);
		NBT.setInteger("lvl", this.getTier().ordinal());
		NBT.setInteger("xp", tableXP);
		NBT.setBoolean("enhance", isEnhanced);

		this.writeRecipes(NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		super.setDataFromItemStackTag(is);
		if (ChromaItems.PLACER.matchWith(is)) {
			if (is.getItemDamage() == this.getTile().ordinal()) {
				if (is.stackTagCompound != null) {
					int lvl = is.stackTagCompound.getInteger("lvl");
					tier = RecipeType.typeList[lvl];
					tableXP = is.stackTagCompound.getInteger("xp");
					isEnhanced = is.stackTagCompound.getBoolean("enhance");
				}
			}
		}

		if (is.stackTagCompound != null)
			this.readRecipes(is.stackTagCompound);
	}

	@Override
	public ElementTagCompound getRequestedTotal() {
		return craftingTick > 0 && activeRecipe instanceof PylonCastingRecipe ? this.getRequiredEnergy() : null;
	}

	public BlockArray getBlocks() {
		switch(tier) {
			case CRAFTING:
				return null;
			case TEMPLE:
				return ChromaStructures.getCastingLevelOne(worldObj, xCoord, yCoord-1, zCoord);
			case MULTIBLOCK:
				return ChromaStructures.getCastingLevelTwo(worldObj, xCoord, yCoord-1, zCoord);
			case PYLON:
				return ChromaStructures.getCastingLevelThree(worldObj, xCoord, yCoord-1, zCoord);
			default:
				return null;
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(12, 6, 12);
	}

	@Override
	public boolean trigger() {
		return this.getPlacer() != null && !ReikaPlayerAPI.isFake(this.getPlacer()) && this.getPlacer().ticksExisted >= 20 && this.triggerCrafting(this.getPlacer());
	}

	public void giveRecipe(EntityPlayer ep, CastingRecipe cr) {
		completedRecipes.add(new KeyedItemStack(cr.getOutput()));
		this.markDirty();
		this.syncAllData(true);
	}

	public HashSet<CastingRecipe> getCompletedRecipes() {
		HashSet<CastingRecipe> set = new HashSet();
		for (KeyedItemStack is : completedRecipes) {
			set.addAll(RecipesCastingTable.instance.getAllRecipesMaking(is.getItemStack()));
		}
		return set;
	}

	public boolean hasRecipeBeenUsed(CastingRecipe cr) {
		return this.getCompletedRecipes().contains(cr);
	}

	@Override
	public int getIconState(int side) {
		return isEnhanced ? 1 : 0;
	}

	@Override
	public boolean onlyAllowOwnersToUse() {
		return true;
	}

	@Override
	public float getOperationFraction() {
		if (activeRecipe == null)
			return 0;
		float denom = activeRecipe.getDuration();
		if (craftingAmount > 1 && activeRecipe.canBeStacked())
			denom /= activeRecipe.getRecipeStackedTimeFactor(this, craftingAmount);
		if (isEnhanced)
			denom /= activeRecipe.getEnhancedTableAccelerationFactor();
		if (denom > 20 && activeRecipe instanceof MultiBlockCastingRecipe) {
			denom = Math.max(20, denom/TileEntityFocusCrystal.getSummedFocusFactor(this));
		}
		float f = 1F-craftingTick/denom;
		return f;
	}

	@Override
	public OperationState getState() {
		if (activeRecipe == null)
			return OperationState.INVALID;
		if (activeRecipe instanceof PylonCastingRecipe)
			return energy.containsAtLeast(this.getRequiredEnergy()) ? OperationState.RUNNING : OperationState.PENDING;
		else
			return OperationState.RUNNING;
	}

	public void dumpAllStands() {
		if (tier.isAtLeast(RecipeType.MULTIBLOCK)) {
			for (TileEntityItemStand te : this.getOtherStands().values()) {
				te.dropSlot();
				ChromaSounds.ITEMSTAND.playSoundAtBlock(te);
				te.syncAllData(true);
			}
		}
	}

}
