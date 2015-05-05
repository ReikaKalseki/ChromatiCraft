/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Auxiliary.OceanStructure;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.LootChestAccessEvent;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.BreakAction;
import Reika.DragonAPI.Interfaces.HitAction;
import Reika.DragonAPI.Interfaces.InertIInv;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityStructControl extends InventoriedChromaticBase implements BreakAction, HitAction, InertIInv {

	private Structures struct;
	private FilledBlockArray blocks;
	private CrystalElement color;
	private final EnumMap<CrystalElement, Coordinate> crystals = new EnumMap(CrystalElement.class);
	private boolean triggered = false;
	private int trapTick = 0;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.STRUCTCONTROL;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote && struct != null && world.getClosestPlayer(x+0.5, y+0.5, z+0.5, 12) != null)
			this.spawnParticles(world, x, y, z);

		if (!triggered && struct != null) {
			List<EntityPlayer> li = world.playerEntities;
			for (EntityPlayer ep : li) {
				if (ep.boundingBox.intersectsWith(this.getBox(x, y, z))) {
					this.onPlayerProximity(world, x, y, z, ep);
				}
			}
		}
		//triggered = false;
		if (struct == Structures.OCEAN) {
			if (trapTick > 0) {
				trapTick--;
			}
			else {
				this.resetOceanTrap();
			}
		}
	}

	@Override
	public void onHit(World world, int x, int y, int z, EntityPlayer ep) {
		this.trigger(x, y, z, ep);
	}

	private void trigger(int x, int y, int z, EntityPlayer ep) {
		if (struct != null) {
			switch(struct) {
			case CAVERN:
				break;
			case BURROW:
				break;
			case OCEAN:
				if ((y == yCoord || y == yCoord-1) && Math.abs(x-xCoord) <= 3 && Math.abs(z-zCoord) <= 3)
					this.triggerOceanTrap(ep);
				break;
			default:
				break;
			}
		}
	}

	private void triggerOceanTrap(EntityPlayer ep) {
		BlockArray arr = OceanStructure.getPitCover(xCoord, yCoord, zCoord);
		for (int i = 0; i < arr.getSize(); i++) {
			int[] xyz = arr.getNthBlock(i);
			int dx = xyz[0];
			int dy = xyz[1];
			int dz = xyz[2];
			worldObj.setBlock(dx, dy, dz, Blocks.air);
		}
		ChromaSounds.TRAP.playSound(ep, 1, 1);
		trapTick = 40;
		this.disableJetpack(ep);
		//ep.addPotionEffect(new PotionEffect(Potion.poison.id, 600, 2));
	}

	private void disableJetpack(EntityPlayer ep) {
		ItemStack chest = ep.getCurrentArmor(2);
		if (chest != null) {
			//no idea how to do this
		}
	}

	private void resetOceanTrap() {
		BlockArray arr = OceanStructure.getPitCover(xCoord, yCoord, zCoord);
		for (int i = 0; i < arr.getSize(); i++) {
			int[] xyz = arr.getNthBlock(i);
			int dx = xyz[0];
			int dy = xyz[1];
			int dz = xyz[2];
			worldObj.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
		}
	}

	private AxisAlignedBB getBox(int x, int y, int z) {
		AxisAlignedBB aabb = ReikaAABBHelper.getBlockAABB(x, y, z);
		switch(struct) {
		case CAVERN:
			return aabb.expand(3, 1, 3);
		case BURROW:
			return aabb.offset(2, 1, 0).expand(0.5, 0.5, 0.5);
		case OCEAN:
			return aabb.offset(0, 2, 0).expand(1, 1, 1);
		default:
			return aabb;
		}
	}

	private void onPlayerProximity(World world, int x, int y, int z, EntityPlayer ep) {
		switch(struct) {
		case CAVERN:
			world.setBlock(x+7, y, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 8, 3);
			world.setBlock(x+7, y-1, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 8, 3);
			ChromaSounds.TRAP.playSound(ep, 1, 1);
			break;
		case BURROW:
			world.setBlockMetadataWithNotify(x+2, y+1, z, BlockStructureShield.BlockType.CRACK.metadata, 3);
			ReikaSoundHelper.playBreakSound(world, x+2, y+1, z, Blocks.stone);
			if (world.isRemote)
				ReikaRenderHelper.spawnDropParticles(world, x+2, y+1, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 9);
			break;
		case OCEAN:
			BlockArray blocks = OceanStructure.getCovers(x, y, z);
			for (int i = 0; i < blocks.getSize(); i++) {
				int[] xyz = blocks.getNthBlock(i);
				int dx = xyz[0];
				int dy = xyz[1];
				int dz = xyz[2];
				world.setBlockMetadataWithNotify(dx, dy, dz, BlockType.CRACKS.metadata, 3);
			}
			int r = 1;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					ReikaSoundHelper.playBreakSound(world, x+i, y+1, z+k, Blocks.stone);
					if (world.isRemote) {
						ReikaRenderHelper.spawnDropParticles(world, x+i, y+3, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 9);
					}
				}
			}
			ChromaSounds.TRAP.playSound(ep, 1, 1);
			break;
		default:
			break;
		}
		this.openUpperChests();
		this.getProgressStage().stepPlayerTo(ep);
		triggered = true;
	}

	private void openUpperChests() {
		switch(struct) {
		case CAVERN:
			if (blocks != null) {
				for (int i = 0; i < blocks.getSize(); i++) {
					int[] xyz = blocks.getNthBlock(i);
					int x = xyz[0];
					int y = xyz[1];
					int z = xyz[2];
					if (y > yCoord && worldObj.getBlock(x, y, z) == ChromaBlocks.LOOTCHEST.getBlockInstance()) {
						worldObj.setBlockMetadataWithNotify(x, y, z, worldObj.getBlockMetadata(x, y, z)%8, 3);
						ReikaSoundHelper.playBreakSound(worldObj, x, y, z, Blocks.stone);
					}
				}
			}
			worldObj.setBlockMetadataWithNotify(xCoord+7, yCoord, zCoord, worldObj.getBlockMetadata(xCoord+7, yCoord, zCoord)%8, 3);
			worldObj.setBlockMetadataWithNotify(xCoord+7, yCoord-1, zCoord, worldObj.getBlockMetadata(xCoord+7, yCoord-1, zCoord)%8, 3);
			break;
		case BURROW:
			if (blocks != null) {
				for (int i = 0; i < blocks.getSize(); i++) {
					int[] xyz = blocks.getNthBlock(i);
					int x = xyz[0]+5;
					int y = xyz[1]+8;
					int z = xyz[2]+2;
					if (y > yCoord && worldObj.getBlock(x, y, z) == ChromaBlocks.LOOTCHEST.getBlockInstance()) {
						worldObj.setBlockMetadataWithNotify(x, y, z, worldObj.getBlockMetadata(x, y, z)%8, 3);
						ReikaSoundHelper.playBreakSound(worldObj, x, y, z, Blocks.stone);
					}
				}
			}
			break;
		case OCEAN:
			if (blocks != null) {
				for (int i = 0; i < blocks.getSize(); i++) {
					int[] xyz = blocks.getNthBlock(i);
					int x = xyz[0];//-3;
					int y = xyz[1];//-5;
					int z = xyz[2];//-3;
					if (y > yCoord && worldObj.getBlock(x, y, z) == ChromaBlocks.LOOTCHEST.getBlockInstance()) {
						worldObj.setBlockMetadataWithNotify(x, y, z, worldObj.getBlockMetadata(x, y, z)%8, 3);
						ReikaSoundHelper.playBreakSound(worldObj, x, y, z, Blocks.stone);
					}
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onFirstTick(World world, int x, int y, int z) {
		if (struct != null) {
			this.calcCrystals(world, x, y, z);
			this.regenerate();
		}
		LootChestWatcher.instance.cache(this);
		this.syncAllData(true);
	}

	private void regenerate() {
		if (struct != null) {
			FilledBlockArray copy = (FilledBlockArray)blocks.copy();
			if (struct == Structures.BURROW) {
				copy.offset(5, 8, 2);
			}
			copy.placeExcept(new Coordinate(this));
			if (struct == Structures.OCEAN) {
				BlockLootChest.setMaxReach(worldObj, xCoord-2, yCoord-1, zCoord, 2);
				BlockLootChest.setMaxReach(worldObj, xCoord, yCoord-1, zCoord-1, 2);
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		switch(struct) {
		case CAVERN:
			for (int i = -1; i <= 1; i += 2) {
				if (!crystals.isEmpty()) {
					CrystalElement e = ReikaJavaLibrary.getRandomListEntry((ArrayList<CrystalElement>)new ArrayList(crystals.keySet()));
					Coordinate c = crystals.get(e);
					double dd = ReikaMathLibrary.py3d(c.xCoord, c.yCoord, c.zCoord);
					double v = 0.2;
					double vx = -c.xCoord/dd*v;
					double vy = -c.yCoord/dd*v+0.15*i;
					double vz = -c.zCoord/dd*v;
					//ReikaJavaLibrary.pConsole(vx, e == CrystalElement.BROWN && x == 24 && z == 383);
					c = c.offset(x, y, z);
					EntityCenterBlurFX fx = new EntityCenterBlurFX(e, world, c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5, vx, vy, vz);
					fx.setGravity(0.1F*i).setLife(60);
					//fx.noClip = false;
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
			break;
		case BURROW:
			if (world.getBlock(x, y-2, z) == ChromaBlocks.LAMP.getBlockInstance()) {
				double dx = x+0.5;
				double dz = z+0.5;
				double d = 0.4;
				switch(rand.nextInt(5)) {
				case 0:
					break;
				case 1:
					dx += d;
					break;
				case 2:
					dx -= d;
					break;
				case 3:
					dz += d;
					break;
				case 4:
					dz -= d;
					break;
				}
				EntityCenterBlurFX fx = new EntityCenterBlurFX(color, world, dx, y-2+0.5, dz, 0, 0, 0).setGravity(-0.05F);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			break;
		case OCEAN:
			break;
		default:
			break;
		}
	}

	public void generate(Structures s, CrystalElement e) {
		if (!s.isNatural())
			throw new IllegalArgumentException("You cannot generate a structure control in the wrong structure!");
		struct = s;
		color = e;
		WeightedRandomChestContent[] loot = ChestGenHooks.getItems(ChestGenHooks.STRONGHOLD_LIBRARY, rand);
		WeightedRandomChestContent.generateChestContents(rand, loot, this, ChestGenHooks.getCount(ChestGenHooks.STRONGHOLD_LIBRARY, rand));
		int n = 1+rand.nextInt(4)*(1+rand.nextInt(2));
		//ReikaJavaLibrary.pConsole("genning "+n+" fragments @ "+xCoord+", "+zCoord);
		for (int i = 0; i < n; i++) {
			ReikaInventoryHelper.addToIInv(ChromaItems.FRAGMENT.getItemInstance(), this);
		}
		switch(struct) {
		case CAVERN:
			ReikaInventoryHelper.addToIInv(ChromaStacks.cavernLoot, this);
			break;
		case BURROW:
			ReikaInventoryHelper.addToIInv(ChromaStacks.burrowLoot, this);
			break;
		case OCEAN:
			ReikaInventoryHelper.addToIInv(ChromaStacks.oceanLoot, this);
			break;
		default:
			break;
		}
	}

	private void calcCrystals(World world, int x, int y, int z) {
		switch(struct) {
		case CAVERN:
			blocks = ChromaStructures.getCavernStructure(world, x, y, z);
			break;
		case BURROW:
			blocks = ChromaStructures.getBurrowStructure(world, x, y, z, color);
			break;
		case OCEAN:
			blocks = ChromaStructures.getOceanStructure(world, x, y, z);
			break;
		default:
			break;
		}
		if (blocks != null) {
			for (int i = 0; i < blocks.getSize(); i++) {
				int[] xyz = blocks.getNthBlock(i);
				if (worldObj.getBlock(xyz[0], xyz[1], xyz[2]) == ChromaBlocks.CRYSTAL.getBlockInstance()) {
					Coordinate c = new Coordinate(xyz[0]-xCoord, xyz[1]-yCoord, xyz[2]-zCoord);
					CrystalElement e = CrystalElement.elements[worldObj.getBlockMetadata(xyz[0], xyz[1], xyz[2])];
					crystals.put(e, c);
					//ReikaJavaLibrary.pConsole(c+":"+world.isRemote, e == CrystalElement.RED && x == 24 && z == 383);
				}
			}
		}
	}

	@Override
	public void breakBlock() {
		if (struct != null) {
			switch(struct) {
			case CAVERN:
				if (blocks != null) {
					for (int i = 0; i < blocks.getSize(); i++) {
						int[] xyz = blocks.getNthBlock(i);
						int x = xyz[0];
						int y = xyz[1];
						int z = xyz[2];
						if (worldObj.getBlock(x, y, z) == ChromaBlocks.STRUCTSHIELD.getBlockInstance())
							worldObj.setBlockMetadataWithNotify(x, y, z, worldObj.getBlockMetadata(x, y, z)%8, 3);
						else if (worldObj.getBlock(x, y, z) == ChromaBlocks.LOOTCHEST.getBlockInstance()) {
							worldObj.setBlockMetadataWithNotify(x, y, z, worldObj.getBlockMetadata(x, y, z)%8, 3);
							ReikaSoundHelper.playBreakSound(worldObj, x, y, z, Blocks.stone);
						}
					}
				}
				worldObj.setBlockMetadataWithNotify(xCoord+7, yCoord, zCoord, worldObj.getBlockMetadata(xCoord+7, yCoord, zCoord)%8, 3);
				worldObj.setBlockMetadataWithNotify(xCoord+7, yCoord-1, zCoord, worldObj.getBlockMetadata(xCoord+7, yCoord-1, zCoord)%8, 3);
				break;
			case BURROW:
				if (blocks != null) {
					for (int i = 0; i < blocks.getSize(); i++) {
						int[] xyz = blocks.getNthBlock(i);
						int x = xyz[0]+5;
						int y = xyz[1]+8;
						int z = xyz[2]+2;
						if (worldObj.getBlock(x, y, z) == ChromaBlocks.STRUCTSHIELD.getBlockInstance())
							worldObj.setBlockMetadataWithNotify(x, y, z, worldObj.getBlockMetadata(x, y, z)%8, 3);
						else if (worldObj.getBlock(x, y, z) == ChromaBlocks.LOOTCHEST.getBlockInstance()) {
							worldObj.setBlockMetadataWithNotify(x, y, z, worldObj.getBlockMetadata(x, y, z)%8, 3);
							ReikaSoundHelper.playBreakSound(worldObj, x, y, z, Blocks.stone);
						}
					}
				}
				break;
			case OCEAN:
				if (blocks != null) {
					for (int i = 0; i < blocks.getSize(); i++) {
						int[] xyz = blocks.getNthBlock(i);
						int x = xyz[0];//-3;
						int y = xyz[1];//-5;
						int z = xyz[2];//-3;
						if (worldObj.getBlock(x, y, z) == ChromaBlocks.STRUCTSHIELD.getBlockInstance())
							worldObj.setBlockMetadataWithNotify(x, y, z, worldObj.getBlockMetadata(x, y, z)%8, 3);
						else if (worldObj.getBlock(x, y, z) == ChromaBlocks.LOOTCHEST.getBlockInstance()) {
							worldObj.setBlockMetadataWithNotify(x, y, z, worldObj.getBlockMetadata(x, y, z)%8, 3);
							ReikaSoundHelper.playBreakSound(worldObj, x, y, z, Blocks.stone);
						}
					}
				}
				break;
			default:
				break;
			}
		}
		LootChestWatcher.instance.remove(this);
	}

	public int getBrightness() {
		return struct == Structures.BURROW ? 15 : 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		if (struct != null)
			NBT.setString("struct", struct.name());
		NBT.setInteger("color", this.getColor().ordinal());
		NBT.setBoolean("trigger", triggered);

		NBT.setInteger("ttick", trapTick);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		if (NBT.hasKey("struct"))
			struct = Structures.valueOf(NBT.getString("struct"));
		triggered = NBT.getBoolean("trigger");
		color = CrystalElement.elements[NBT.getInteger("color")];

		trapTick = NBT.getInteger("ttick");
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 27;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	public CrystalElement getColor() {
		return color != null ? color : CrystalElement.WHITE;
	}

	@SideOnly(Side.CLIENT)
	public boolean isVisible() {
		return true;//ProgressionManager.instance.isPlayerAtStage(Minecraft.getMinecraft().thePlayer, this.getProgressStage());
	}

	public boolean isBreakable() {
		return true;
	}

	private ProgressStage getProgressStage() {
		switch(struct) {
		case CAVERN:
			return ProgressStage.CAVERN;
		case BURROW:
			return ProgressStage.BURROW;
		case OCEAN:
			return ProgressStage.OCEAN;
		default:
			return null;
		}
	}

	public static class LootChestWatcher {

		public static final LootChestWatcher instance = new LootChestWatcher();

		private final MultiMap<WorldChunk, WorldLocation> cache = new MultiMap();

		private LootChestWatcher() {

		}

		private void cache(TileEntityStructControl te) {
			WorldLocation loc = new WorldLocation(te);
			WorldChunk wc = new WorldChunk(te.worldObj, te.worldObj.getChunkFromBlockCoords(te.xCoord, te.zCoord));
			cache.addValue(wc, loc);
		}

		private void remove(TileEntityStructControl te) {
			WorldLocation loc = new WorldLocation(te);
			WorldChunk wc = new WorldChunk(te.worldObj, te.worldObj.getChunkFromBlockCoords(te.xCoord, te.zCoord));
			cache.remove(wc, loc);
		}

		@SubscribeEvent
		public void onAccess(LootChestAccessEvent evt) {
			int x = evt.x;
			int z = evt.z;
			int r = 2;
			for (int i = -r; i <= r; i++) {
				for (int j = -r; j <= r; j++) {
					int dx = x+i*16;
					int dz = z+i*16;
					WorldChunk wc = new WorldChunk(evt.world, evt.world.getChunkFromBlockCoords(dx, dz));
					for (WorldLocation loc : cache.get(wc)) {
						TileEntity tile = evt.world.getTileEntity(loc.xCoord, loc.yCoord, loc.zCoord);
						if (tile instanceof TileEntityStructControl) {
							TileEntityStructControl te = (TileEntityStructControl)tile;
							te.trigger(evt.x, evt.y, evt.z, evt.player);
						}
					}
				}
			}
		}

	}

	static {
		MinecraftForge.EVENT_BUS.register(LootChestWatcher.instance);
	}

}
