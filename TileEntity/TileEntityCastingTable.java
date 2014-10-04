/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalReceiver;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityGlobeFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockMap.BlockKey;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCastingTable extends InventoriedCrystalReceiver implements NBTTile {

	private CastingRecipe activeRecipe = null;
	private int craftingTick = 0;
	private int craftSoundTimer = 20000;

	public boolean hasStructure = false;
	public boolean hasStructure2 = false;
	public boolean hasPylonConnections = false;
	private int tableXP;
	private RecipeType tier = RecipeType.CRAFTING;

	private RecipeType getTier() {
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
			this.particleBurst();
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
		if (this.getTicksExisted() == 0) {
			this.validateStructure(null, world, x, y-1, z);
			craftingTick = 0;
		}
		if (!world.isRemote && this.getTicksExisted() == 1) {
			this.evaluateRecipeAndRequest();
		}
		if (craftingTick > 0) {
			this.onCraftingTick(world, x, y, z);
		}

		//ReikaJavaLibrary.pConsole(energy, Side.SERVER);
	}

	private void killCrafting() {
		craftingTick = 0;
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
		if (craftSoundTimer >= this.getSoundLength() && activeRecipe != null && activeRecipe.getDuration() > 20) {
			craftSoundTimer = 0;
			ChromaSounds.CRAFTING.playSoundAtBlock(this);
		}
		//ReikaJavaLibrary.pConsole(craftingTick, Side.SERVER);
		if (activeRecipe instanceof PylonRecipe) {
			ElementTagCompound req = ((PylonRecipe)activeRecipe).getRequiredAura();
			if (!energy.containsAtLeast(req)) {
				if (this.getCooldown() == 0 && checkTimer.checkCap()) {
					this.requestEnergyDifference(req);
				}
				return;
			}
		}
		craftingTick--;
		if (craftingTick == 0) {
			this.craft();
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnCraftingParticles(World world, int x, int y, int z) {
		if (this.getTier().isAtLeast(RecipeType.TEMPLE) && hasStructure) {
			BlockArray blocks = this.getStructureAccentLocations();

			for (int i = 0; i < blocks.getSize(); i++) {
				int[] xyz = blocks.getNthBlock(i);

				int dx = xyz[0];
				int dy = xyz[1];
				int dz = xyz[2];
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
				double dd = ReikaMathLibrary.py3d(rx, ry, rz);
				double v = 64;
				double vx = v*(x+0.5-rx)/dd;
				double vy = 0.0125+v*(y+0.5-ry)/dd;
				double vz = v*(z+0.5-rz)/dd;
				EntityGlobeFX fx = new EntityGlobeFX(world, rx, ry, rz, vx, vy, vz);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		if (this.getTier().isAtLeast(RecipeType.PYLON) && hasPylonConnections && activeRecipe instanceof PylonRecipe) {
			BlockArray blocks = this.getStructureRuneLocations(((PylonRecipe)activeRecipe).getRequiredAura());
			int mod = 17-blocks.getSize();
			if (this.getTicksExisted()%mod == 0) {
				int[] xyz = blocks.getNthBlock(this.getTicksExisted()%blocks.getSize());
				int dx = xyz[0];
				int dy = xyz[1];
				int dz = xyz[2];
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
		ArrayList<BlockKey> li = new ArrayList();
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

	private int getSoundLength() {
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

	public void validateStructure(StructuredBlockArray blocks, World world, int x, int y, int z) {
		FilledBlockArray b = ChromaStructures.getCastingLevelOne(world, x, y, z);
		FilledBlockArray b2 = ChromaStructures.getCastingLevelTwo(world, x, y, z);
		FilledBlockArray b3 = ChromaStructures.getCastingLevelThree(world, x, y, z);
		if ((b.matchInWorld() || b2.matchInWorld() || b3.matchInWorld()) && this.getTier().isAtLeast(RecipeType.TEMPLE)) {
			hasStructure = true;
			if ((b2.matchInWorld() || b3.matchInWorld()) && this.getTier().isAtLeast(RecipeType.MULTIBLOCK)) {
				hasStructure2 = true;
				if (b3.matchInWorld() && this.getTier().isAtLeast(RecipeType.PYLON)) {
					hasPylonConnections = true;
				}
				else {
					hasPylonConnections = false;
				}
			}
			else {
				hasPylonConnections = false;
				hasStructure2 = false;
			}
		}
		else {
			hasPylonConnections = false;
			hasStructure = false;
			hasStructure2 = false;
		}

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
			if (activeRecipe.canRunRecipe(ep) && this.isPlacer(ep)) {
				ChromaSounds.CAST.playSoundAtBlock(this);
				craftingTick = activeRecipe.getDuration();
				if (activeRecipe instanceof PylonRecipe) {
					ElementTagCompound tag = ((PylonRecipe)activeRecipe).getRequiredAura();
					tag.subtract(energy);
					for (CrystalElement e : tag.elementSet()) {
						this.requestEnergy(e, tag.getValue(e));
					}
				}
				return true;
			}
		}
		return false;
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
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasPylonConnections = NBT.getBoolean("pylons");
		hasStructure = NBT.getBoolean("struct");
		hasStructure2 = NBT.getBoolean("struct2");

		tableXP = NBT.getInteger("xp");
		tier = RecipeType.typeList[NBT.getInteger("tier")];

		craftingTick = NBT.getInteger("craft");
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
	}

	private void craft() {
		CastingRecipe recipe = activeRecipe;
		int count = 0;
		while (activeRecipe == recipe && count < activeRecipe.getOutput().getMaxStackSize()) {
			this.addXP(activeRecipe.getExperience());
			if (activeRecipe instanceof MultiBlockCastingRecipe) {
				MultiBlockCastingRecipe mult = ((MultiBlockCastingRecipe)activeRecipe);
				HashMap<WorldLocation, ItemStack> map = mult.getOtherInputs(worldObj, xCoord, yCoord, zCoord);
				for (WorldLocation loc : map.keySet()) {
					TileEntityItemStand te = (TileEntityItemStand)loc.getTileEntity();
					//ReikaJavaLibrary.pConsole(te+":"+te.getStackInSlot(0), Side.SERVER);
					te.setInventorySlotContents(0, null);
					te.syncAllData(true);
				}
			}
			for (int i = 0; i < 9; i++) {
				if (inv[i] != null)
					ReikaInventoryHelper.decrStack(i, inv);
			}
			count += activeRecipe.getOutput().stackSize;
			if (activeRecipe instanceof PylonRecipe) {
				energy.subtract(((PylonRecipe)activeRecipe).getRequiredAura());
			}
			recipe = this.getValidRecipe();
		}
		inv[9] = ReikaItemHelper.getSizedItemStack(activeRecipe.getOutput(), count);
		activeRecipe = null;
		craftSoundTimer = 20000;
		craftingTick = 0;
		ChromaSounds.CRAFTDONE.playSoundAtBlock(this);
		if (worldObj.isRemote)
			this.particleBurst();
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
		}
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
		if (r != null && r != activeRecipe && r instanceof PylonRecipe) {
			ElementTagCompound tag = ((PylonRecipe)r).getRequiredAura();
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
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public void onPathBroken() {

	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return 50;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	public int getMaxStorage() {
		return 200000;
	}

	public boolean isCrafting() {
		return activeRecipe != null;
	}

	public ArrayList<CrystalTarget> getTargets() {
		ArrayList<CrystalTarget> li = new ArrayList();
		return li;
	}

	@Override
	public NBTTagCompound getTagsToWriteToStack() {
		NBTTagCompound NBT = new NBTTagCompound();
		NBT.setInteger("lvl", this.getTier().ordinal());
		return NBT;
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		if (ChromaItems.PLACER.matchWith(is)) {
			if (is.getItemDamage() == this.getTile().ordinal()) {
				if (is.stackTagCompound != null) {
					int lvl = is.stackTagCompound.getInteger("lvl");
					tier = RecipeType.typeList[lvl];
				}
			}
		}
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		return null;
	}

}
