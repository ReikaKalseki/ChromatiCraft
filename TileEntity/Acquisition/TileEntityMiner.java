/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.MinerBlock;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityRangeBoost;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Interfaces.Block.SpecialOreBlock;
import Reika.DragonAPI.Interfaces.TileEntity.ChunkLoadingTile;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMiner extends ChargedCrystalPowered implements OwnedTile, ChunkLoadingTile {

	public static final int MAXRANGE = 128;

	private boolean digging = false;
	private boolean digReady = false;

	private boolean finishedDigging = false;

	private int range = MAXRANGE;

	private int readX = 0;
	private int readY = 0;
	private int readZ = 0;

	private double particleX;
	private double particleY;
	private double particleVX;
	private double particleVY;

	public int progress;

	private boolean dropFlag = false;
	private long lastWarning;

	private boolean chunkloaded;

	private static final int TICKSTEP = 2048;
	private int index;
	private StepTimer miningTimer = new StepTimer(5);

	private static final ElementTagCompound required = new ElementTagCompound();

	private final ArrayList<Coordinate> coords = new ArrayList();
	private final ItemHashMap<Integer> found = new ItemHashMap(); //pre-unified for display

	private boolean silkTouch;

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
		this.updateRange();
		this.calcSilkTouch();
		digging = digReady = dropFlag = false;
		readY = 0;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			if (dropFlag) {
				if (this.getTicksExisted()%20 == 0) {
					this.doDropWarning(world, x, y, z);
				}
			}
			else {
				if (digging && !coords.isEmpty()) {
					//this.prepareChunkloading();
					this.calcSilkTouch();
					miningTimer.update();
					if (miningTimer.checkCap()) {
						if (this.hasEnergy(required)) {
							Coordinate c = coords.get(index);
							int dx = c.xCoord;
							int dy = c.yCoord;
							int dz = c.zCoord;
							Block id = this.parseBlock(world.getBlock(dx, dy, dz));
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
							else if (this.shouldMine(id, meta2)) {
								this.dropBlock(world, x, y, z, dx, dy, dz, id, meta2);
							}
							else if (this.isTieredResource(world, dx, dy, dz, id, meta2)) {
								this.dropTieredResource(world, x, y, z, dx, dy, dz, id, meta2);
							}
							else if (id instanceof MinerBlock) {
								this.dropMineableBlock(world, x, y, z, dx, dy, dz, id, meta2);
							}
							this.useEnergy(required.copy().scale(this.getEnergyCostScale()));
							//ReikaJavaLibrary.pConsole("Mining "+id+":"+meta2+" @ "+dx+","+dy+","+dz+"; index="+index);
							index++;
							if (index >= coords.size()) {
								this.finishDigging();
							}
						}
					}
				}
				else if (!digReady && !finishedDigging) {
					this.prepareChunkloading();
					for (int i = 0; i < TICKSTEP; i++) {
						int dx = x+readX;
						int dy = readY;
						int dz = z+readZ;
						ReikaWorldHelper.forceGenAndPopulate(world, dx, dz);
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
						if (add) {
							this.addFound(world, dx, dy, dz, id, meta2);
						}
						this.updateReadPosition();
						if (readY >= worldObj.getActualHeight()) {
							this.prepareDigging();
						}
					}
				}
				progress = readY;
			}
		}
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);
	}

	private void prepareChunkloading() {
		if (!chunkloaded) {
			chunkloaded = true;
			ChunkManager.instance.loadChunks(this);
		}
	}

	private void prepareDigging() {
		digReady = true;
		readX = 0;
		readY = 0;
		readZ = 0;
		index = 0;
		ChunkManager.instance.unloadChunks(this);
	}

	private void finishDigging() {
		digging = false;
		digReady = false;
		coords.clear();
		found.clear();
		finishedDigging = true;
		//ReikaJavaLibrary.pConsole(found);
		this.syncAllData(true);
		this.scheduleBlockUpdate(5);
		//ChunkManager.instance.unloadChunks(this);
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
		if (Item.getItemFromBlock(b) == null)
			return;
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
		//ReikaJavaLibrary.pConsole("Removed "+b+":"+meta+" from cache, now have "+found.get(is));
	}

	private void addFound(World world, int x, int y, int z, Block b, int meta) {
		if (b != null && Item.getItemFromBlock(b) == null) {
			ChromatiCraft.logger.logError("Block "+b+" has no item to drop when mined???");
			return;
		}
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
		//ReikaJavaLibrary.pConsole("Found "+b+":"+meta+" @ "+x+","+y+","+z+"; have "+(i+1));
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

		EntityBlurFX fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(color);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x+1-particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(color);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		pz = z+1;
		px = x+1-particleX;
		py = y+particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(color);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x+particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(color);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x;
		pz = z+particleX;
		py = y+particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(color);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		pz = z+1-particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(color);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x+1;
		pz = z+1-particleX;
		py = y+particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(color);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		pz = z+particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(color);
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
		if (silkTouch) {
			this.dropItems(world, x, y, z, ReikaJavaLibrary.makeListFrom(id.getSilkTouchVersion(world, dx, dy, dz)));
		}
		else {
			this.dropItems(world, x, y, z, id.getDrops(world, dx, dy, dz, 0));
		}
		ReikaWorldHelper.setBlock(world, dx, dy, dz, id.getReplacementBlock(world, dx, dy, dz));
	}

	private void dropBlock(World world, int x, int y, int z, int dx, int dy, int dz, Block id, int meta2) {
		if (silkTouch) {
			this.dropItems(world, x, y, z, ReikaJavaLibrary.makeListFrom(new ItemStack(id, 1, meta2)));
		}
		else {
			this.dropItems(world, x, y, z, id.getDrops(world, dx, dy, dz, meta2, 0));
		}
		this.getFillerBlock(world, dx, dy, dz, id, meta2).place(world, dx, dy, dz);
		ReikaSoundHelper.playBreakSound(world, dx, dy, dz, id);
		ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), world, dx, dy, dz, Block.getIdFromBlock(id), meta2);
	}

	private void dropTieredResource(World world, int x, int y, int z, int dx,int dy, int dz, Block id, int meta2) {
		Collection<ItemStack> li = ((BlockTieredResource)id).getHarvestResources(world, dx, dy, dz, 10, this.getPlacer());
		this.dropItems(world, x, y, z, li);
		this.getFillerBlock(world, dx, dy, dz, id, meta2).place(world, dx, dy, dz);
		ReikaSoundHelper.playBreakSound(world, dx, dy, dz, id);
		ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), world, dx, dy, dz, Block.getIdFromBlock(id), meta2);
	}

	private void dropMineableBlock(World world, int x, int y, int z, int dx, int dy, int dz, Block id, int meta2) {
		if (silkTouch) {
			this.dropItems(world, x, y, z, ReikaJavaLibrary.makeListFrom(new ItemStack(id, 1, meta2)));
		}
		else {
			this.dropItems(world, x, y, z, ((MinerBlock)id).getHarvestItems(world, dx, dy, dz, meta2, 0));
		}
		this.getFillerBlock(world, dx, dy, dz, id, meta2).place(world, dx, dy, dz);
		ReikaSoundHelper.playBreakSound(world, dx, dy, dz, id);
		ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), world, dx, dy, dz, Block.getIdFromBlock(id), meta2);
	}

	private BlockKey getFillerBlock(World world, int dx, int dy, int dz, Block id, int meta2) {
		if (!id.getMaterial().isSolid())
			return new BlockKey(Blocks.air);
		if (id == Blocks.glowstone)
			return new BlockKey(Blocks.air);
		if (id == Blocks.mob_spawner)
			return new BlockKey(Blocks.air);
		if (world.provider.dimensionId == -1)
			return new BlockKey(Blocks.netherrack);
		if (world.provider.dimensionId == 1)
			return new BlockKey(Blocks.end_stone);
		return new BlockKey(Blocks.stone);
	}

	public boolean hasCrystal() {
		return ChromaItems.STORAGE.matchWith(inv[0]);
	}

	private void calcSilkTouch() {
		Integer get = TileEntityAdjacencyUpgrade.getAdjacentUpgrades(this).get(CrystalElement.PURPLE);
		silkTouch = get != null && get.intValue() > 2;
	}

	@Override
	public void onAdjacentUpdate(World world, int x, int y, int z, Block b) {
		this.calcSilkTouch();
		this.updateRange();
		super.onAdjacentUpdate(world, x, y, z, b);
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
			if (flag) {
				ReikaItemHelper.dropItem(world, x+0.5, y+1.5, z+0.5, is);
				dropFlag = true;
				this.doDropWarning(world, x, y, z);
			}
		}
	}

	private void doDropWarning(World world, int x, int y, int z) {
		if (lastWarning != world.getTotalWorldTime()) {
			ChromaSounds.ERROR.playSoundAtBlock(this);
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.MINERJAM.ordinal(), this, 32);
		}
		lastWarning = world.getTotalWorldTime();
	}

	@SideOnly(Side.CLIENT)
	public void doWarningParticles(World world, int x, int y, int z) {
		int n = 2+rand.nextInt(6);
		for (int i = 0; i < n; i++) {
			double rx = x+rand.nextDouble();
			double ry = y+rand.nextDouble();
			double rz = z+rand.nextDouble();
			int l = 10+rand.nextInt(20);
			float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
			EntityFX fx = new EntityCenterBlurFX(world, rx, ry, rz).setGravity(g).setLife(l);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
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
			dropFlag = false;
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
		finishedDigging = NBT.getBoolean("finish");
		index = NBT.getInteger("index");

		dropFlag = NBT.getBoolean("dropped");

		silkTouch = NBT.getBoolean("silk");

		found.clear();
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
		NBT.setBoolean("finish", finishedDigging);
		NBT.setBoolean("silk", silkTouch);

		NBTTagList li = new NBTTagList();
		for (ItemStack is : found.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("id", Item.getIdFromItem(is.getItem()));
			tag.setInteger("meta", is.getItemDamage());
			tag.setInteger("count", found.get(is));
			li.appendTag(tag);
		}
		NBT.setTag("count", li);

		NBT.setBoolean("dropped", dropFlag);
	}

	@Override
	public int getSizeInventory() {
		return 1;
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

	@Override
	public void breakBlock() {
		ChunkManager.instance.unloadChunks(this);
	}

	@Override
	public Collection<ChunkCoordIntPair> getChunksToLoad() {
		return ChunkManager.getChunkSquare(xCoord, zCoord, range >> 4);
	}

	@Override
	public float getCostModifier() {
		float f = 1;
		if (silkTouch) {
			f *= 8;
		}
		return f;
	}

	@Override
	public boolean usesColor(CrystalElement e) {
		return required.contains(e);
	}

	private void updateRange() {
		int oldrange = range;
		double r = 1;
		Integer get = TileEntityAdjacencyUpgrade.getAdjacentUpgrades(this).get(CrystalElement.LIME);
		int val = get != null ? get.intValue() : 0;
		if (val > 0)
			r = TileEntityRangeBoost.getFactor(val-1);
		range = (int)(MAXRANGE*r);
		if (range != oldrange && !digging) {
			readX = 0;
			readY = 0;
			readZ = 0;
			digReady = false;
			found.clear();
			coords.clear();
		}
	}

	public boolean hasSilkTouch() {
		return silkTouch;
	}

}
