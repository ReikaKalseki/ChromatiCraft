/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Acquisition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.API.MinerBlock;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Instantiable.Data.ItemHashMap;
import Reika.DragonAPI.Interfaces.SpecialOreBlock;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMiner extends ChargedCrystalPowered {

	private boolean digging = false;
	private boolean digReady = false;

	private int range = 128;

	private int readX = 0;
	private int readY = 0;
	private int readZ = 0;

	private double particleX;
	private double particleY;
	private double particleVX;
	private double particleVY;

	public int progress;

	private static int TICKSTEP = 2048;
	private int index;

	private static final ElementTagCompound required = new ElementTagCompound();

	private final ArrayList<Coordinate> coords = new ArrayList();
	private final ItemHashMap<Integer> found = new ItemHashMap(); //pre-unified for display

	static {
		required.addTag(CrystalElement.YELLOW, 50);
		required.addTag(CrystalElement.LIME, 30);
		required.addTag(CrystalElement.GRAY, 20);
		required.addTag(CrystalElement.BROWN, 40);
	}

	public static ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.MINER;
	}

	public int getReadX() {
		return readX;
	}

	public int getReadY() {
		return readY;
	}

	public int getReadZ() {
		return readZ;
	}

	public int getRange() {
		return range;
	}

	@Override
	public void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		digging = digReady = false;
		readY = 0;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			int n = this.hasSpeed() ? 4 : 1;
			for (int k = 0; k < n; k++) {
				if (digging && !coords.isEmpty()) {
					if (this.hasEnergy(required)) {
						Coordinate c = coords.get(index);
						int dx = c.xCoord;
						int dy = c.yCoord;
						int dz = c.zCoord;
						Block id = this.parseBlock(world.getBlock(x, y, z));
						int meta2 = world.getBlockMetadata(dx, dy, dz);
						//ReikaJavaLibrary.pConsole(readX+":"+dx+", "+dy+", "+readZ+":"+dz+" > "+ores.getSize(), Side.SERVER);
						this.removeFound(world, dx, dy, dz, id, meta2);
						if (id instanceof SpecialOreBlock) {
							this.dropSpecialOreBlock(world, x, y, z, dx, dy, dz, (SpecialOreBlock)id, meta2);
						}
						else if (ReikaBlockHelper.isOre(id, meta2)) {
							//ores.addBlockCoordinate(dx, dy, dz);
							this.dropBlock(world, x, y, z, dx, dy, dz, id, meta2);
						}
						else if (this.isTieredResource(world, dx, dy, dz, id, meta2)) {
							this.dropTieredResource(world, x, y, z, dx, dy, dz, id, meta2);
						}
						else if (id instanceof MinerBlock) {
							this.dropMineableBlock(world, x, y, z, dx, dy, dz, id, meta2);
						}
						this.useEnergy(required.copy().scale(this.hasEfficiency() ? 0.25F : 1));
						index++;
						if (index >= coords.size()) {
							digging = false;
							digReady = false;
							coords.clear();
							found.clear();
						}
					}
				}
				else if (!digReady) {
					for (int i = 0; i < TICKSTEP; i++) {
						int dx = x+readX;
						int dy = readY;
						int dz = z+readZ;
						ReikaWorldHelper.forceGenAndPopulate(world, dx, dy, dz, meta);
						Block id = this.parseBlock(world.getBlock(dx, dy, dz));
						int meta2 = world.getBlockMetadata(dx, dy, dz);
						//ReikaJavaLibrary.pConsole(readX+":"+dx+", "+dy+", "+readZ+":"+dz+" > "+ores.getSize(), Side.SERVER);
						boolean add = false;
						if (ReikaBlockHelper.isOre(id, meta2)) {
							//ores.addBlockCoordinate(dx, dy, dz);
							add = coords.add(new Coordinate(dx, dy, dz));
						}
						else if (this.isTieredResource(world, dx, dy, dz, id, meta2)) {
							add = coords.add(new Coordinate(dx, dy, dz));
						}
						else if (id instanceof MinerBlock && ((MinerBlock)id).isMineable(meta2)) {
							add = coords.add(new Coordinate(dx, dy, dz));
						}
						else if (this.shouldMine(id, meta2)) {
							add = coords.add(new Coordinate(dx, dy, dz));
						}
						if (add)
							this.addFound(world, dx, dy, dz, id, meta2);
						this.updateReadPosition();
						if (readY >= worldObj.getActualHeight()) {
							digReady = true;
							readX = 0;
							readY = 0;
							readZ = 0;
						}
					}
				}
			}
			progress = readY;
		}
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);
	}

	private Block parseBlock(Block b) {
		if (b == Blocks.lit_redstone_ore)
			return Blocks.redstone_ore;
		return b;
	}

	private boolean shouldMine(Block id, int meta2) {
		if (id == Blocks.glowstone)
			return true;
		if (id == Blocks.mob_spawner) //need an item
			;//return true;
		return false;
	}

	public float getDigCompletion() {
		return this.isReady() ? 1 : (float)progress/worldObj.getActualHeight();
	}

	public boolean isReady() {
		return digReady;
	}

	private void removeFound(World world, int x, int y, int z, Block b, int meta) {
		ItemStack is = new ItemStack(b, 1, meta);
		if (b instanceof SpecialOreBlock) {
			is = ((SpecialOreBlock)b).getDisplayItem(world, x, y, z);
		}
		else if (ReikaBlockHelper.isOre(b, meta)) {
			ReikaOreHelper ore = ReikaOreHelper.getEntryByOreDict(is);
			ModOreList mod = ModOreList.getModOreFromOre(is);
			if (ore != null) {
				is = ore.getOreBlock();
			}
			else if (mod != null && mod != ModOreList.CERTUSQUARTZ) {
				is = mod.getFirstOreBlock();
			}
		}
		Integer i = found.get(is);
		if (i == null)
			i = 0;
		if (i > 1)
			found.put(is, i-1);
		else
			found.remove(is);
	}

	private void addFound(World world, int x, int y, int z, Block b, int meta) {
		ItemStack is = new ItemStack(b, 1, meta);
		if (b instanceof SpecialOreBlock) {
			is = ((SpecialOreBlock)b).getDisplayItem(world, x, y, z);
		}
		else if (ReikaBlockHelper.isOre(b, meta)) {
			ReikaOreHelper ore = ReikaOreHelper.getEntryByOreDict(is);
			ModOreList mod = ModOreList.getModOreFromOre(is);
			if (ore != null) {
				is = ore.getOreBlock();
			}
			else if (mod != null && mod != ModOreList.CERTUSQUARTZ) {
				is = mod.getFirstOreBlock();
			}
		}
		Integer i = found.get(is);
		if (i == null)
			i = 0;
		found.put(is, i+1);
	}

	public List<ItemStack> getFound() {
		return Collections.unmodifiableList(found.sortedKeyset());
	}

	public int getNumberFound(ItemStack is) {
		Integer i = found.get(is);
		return i != null ? i.intValue() : 0;
	}

	private boolean isTieredResource(World world, int x, int y, int z, Block id, int meta) {
		EntityPlayer ep = this.getPlacer();
		return ep != null && id instanceof BlockTieredResource && ((BlockTieredResource)id).isPlayerSufficientTier(world, x, y, z, ep);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		double px = x+particleX;
		double py = y+particleY;
		double pz = z;

		int color = CrystalElement.getBlendedColor(this.getTicksExisted(), 40);
		int red = ReikaColorAPI.getRedFromInteger(color);
		int green = ReikaColorAPI.getGreenFromInteger(color);
		int blue = ReikaColorAPI.getBlueFromInteger(color);

		EntityBlurFX fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x+1-particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		pz = z+1;
		px = x+1-particleX;
		py = y+particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x+particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x;
		pz = z+particleX;
		py = y+particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		pz = z+1-particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x+1;
		pz = z+1-particleX;
		py = y+particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		pz = z+particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		double d = 0.05;
		particleX += particleVX;
		particleY += particleVY;
		particleX = MathHelper.clamp_double(particleX, 0, 1);
		particleY = MathHelper.clamp_double(particleY, 0, 1);

		if (particleX == 1 && particleY == 0) {
			particleVX = 0;
			particleVY = d;
		}
		if (particleY == 1 && particleY == 1) {
			particleVX = -d;
			particleVY = 0;
		}
		if (particleX == 0 && particleY == 1) {
			particleVX = 0;
			particleVY = -d;
		}
		if (particleX == 0 && particleY == 0) {
			particleVX = d;
			particleVY = 0;
		}
	}

	private void dropSpecialOreBlock(World world, int x, int y, int z, int dx, int dy, int dz, SpecialOreBlock id, int meta2) {
		if (this.silkTouch()) {
			this.dropItems(world, x, y, z, ReikaJavaLibrary.makeListFrom(id.getSilkTouchVersion(world, dx, dy, dz)));
		}
		else {
			this.dropItems(world, x, y, z, id.getDrops(world, dx, dy, dz, 0));
		}
		ReikaWorldHelper.setBlock(world, dx, dy, dz, id.getReplacementBlock(world, dx, dy, dz));
	}

	private void dropBlock(World world, int x, int y, int z, int dx, int dy, int dz, Block id, int meta2) {
		if (this.silkTouch()) {
			this.dropItems(world, x, y, z, ReikaJavaLibrary.makeListFrom(new ItemStack(id, 1, meta2)));
		}
		else {
			this.dropItems(world, x, y, z, id.getDrops(world, dx, dy, dz, meta2, 0));
		}
		world.setBlock(dx, dy, dz, Blocks.stone);
	}

	private void dropTieredResource(World world, int x, int y, int z, int dx,int dy, int dz, Block id, int meta2) {
		Collection<ItemStack> li = ((BlockTieredResource)id).getHarvestResources(world, dx, dy, dz, 10, this.getPlacer());
		this.dropItems(world, x, y, z, li);
		world.setBlock(dx, dy, dz, id.getMaterial().isSolid() ? Blocks.stone : Blocks.air);
	}

	private void dropMineableBlock(World world, int x, int y, int z, int dx, int dy, int dz, Block id, int meta2) {
		if (this.silkTouch()) {
			this.dropItems(world, x, y, z, ReikaJavaLibrary.makeListFrom(new ItemStack(id, 1, meta2)));
		}
		else {
			this.dropItems(world, x, y, z, ((MinerBlock)id).getHarvestItems(world, dx, dy, dz, meta2, 0));
		}
		world.setBlock(dx, dy, dz, Blocks.stone);
	}

	private boolean silkTouch() {
		return ReikaItemHelper.matchStacks(inv[1], ChromaStacks.silkUpgrade);
	}

	private boolean hasSpeed() {
		return ReikaItemHelper.matchStacks(inv[2], ChromaStacks.speedUpgrade);
	}

	private boolean hasEfficiency() {
		return ReikaItemHelper.matchStacks(inv[3], ChromaStacks.efficiencyUpgrade);
	}

	private void dropItems(World world, int x, int y, int z, Collection<ItemStack> li) {
		for (ItemStack is : li) {
			boolean flag = true;
			for (int i = 0; i < 6 && flag; i++) {
				TileEntity te = this.getAdjacentTileEntity(dirs[i]);
				if (te instanceof IInventory) {
					if (ReikaInventoryHelper.addToIInv(is, (IInventory)te))
						flag = false;
				}
			}
			if (flag)
				ReikaItemHelper.dropItem(world, x+0.5, y+1.5, z+0.5, is);
		}
	}

	private void updateReadPosition() {
		boolean flag1 = false;
		boolean flag2 = false;
		readX++;
		if (readX > range) {
			readX = -range;
			flag1 = true;
		}
		if (flag1) {
			readZ++;
			//ReikaJavaLibrary.pConsole(readY+" > "+readZ+":"+range+" > "+ores.getSize(), Side.SERVER);
			if (readZ > range) {
				readZ = -range;
				flag2 = true;
			}
			if (flag2) {
				readY++;
			}
		}
	}

	public boolean triggerDigging() {
		if (this.isReady()) {
			digging = true;
			ChromaSounds.USE.playSoundAtBlock(this);
			return true;
		}
		ChromaSounds.ERROR.playSoundAtBlock(this);
		return false;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		//NBTTagList li = NBT.getTagList("coords", NBTTypes.COMPOUND.ID);
		//for (Object o : li.tagList) {
		//	NBTTagCompound tag = (NBTTagCompound)o;
		//	Coordinate c = Coordinate.readTag(tag);
		//	coords.add(c);
		//}

		readX = NBT.getInteger("rx");
		readY = NBT.getInteger("ry");
		readZ = NBT.getInteger("rz");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		//NBTTagList li = new NBTTagList();
		//for (Coordinate c : coords) {
		//	NBTTagCompound tag = c.writeToTag();
		//	li.appendTag(tag);
		//}
		//NBT.setTag("coords", li);
		NBT.setInteger("rx", readX);
		NBT.setInteger("ry", readY);
		NBT.setInteger("rz", readZ);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		digging = NBT.getBoolean("dig");
		digReady = NBT.getBoolean("dig2");
		index = NBT.getInteger("index");

		NBTTagList li = NBT.getTagList("count", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			int id = tag.getInteger("id");
			int meta = tag.getInteger("meta");
			int count = tag.getInteger("count");
			ItemStack is = new ItemStack(Item.getItemById(id), 1, meta);
			found.put(is, count);
		}
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("dig", digging);
		NBT.setBoolean("dig2", digReady);
		NBT.setInteger("index", index);

		NBTTagList li = new NBTTagList();
		for (ItemStack is : found.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("id", Item.getIdFromItem(is.getItem()));
			tag.setInteger("meta", is.getItemDamage());
			tag.setInteger("count", found.get(is));
			li.appendTag(tag);
		}
		NBT.setTag("count", li);
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		switch(slot) {
		case 0:
			return ChromaItems.STORAGE.matchWith(is);
		default:
			return false;
		}
	}

}
