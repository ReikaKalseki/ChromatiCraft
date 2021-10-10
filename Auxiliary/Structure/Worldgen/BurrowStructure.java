package Reika.ChromatiCraft.Auxiliary.Structure.Worldgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.FragmentStructureBase;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.BlockHeatLamp.TileEntityHeatLamp;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Items.Tools.ItemDoorKey;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.ItemDrop;
import Reika.DragonAPI.Instantiable.ItemDrop.OreDrop;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Interfaces.Registry.OreType.OreRarity;
import Reika.DragonAPI.Interfaces.Registry.TreeType;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;


public class BurrowStructure extends FragmentStructureBase {

	private static WeightedRandom<OreType> furnaceOres = new WeightedRandom();
	private static WeightedRandom<ItemDrop> lootItems = new WeightedRandom();

	private UUID doorID;

	public static void buildLootCache() {
		furnaceOres.addEntry(ReikaOreHelper.IRON, 40);
		furnaceOres.addEntry(ReikaOreHelper.GOLD, 15);
		addOreIf(ModOreList.COPPER, 50);
		addOreIf(ModOreList.TIN, 50);
		addOreIf(ModOreList.LEAD, 10);
		addOreIf(ModOreList.NICKEL, 10);
		addOreIf(ModOreList.SILVER, 15);
		addOreIf(ModOreList.ALUMINUM, 20);
		addOreIf(ModOreList.PLATINUM, 2);

		lootItems.addEntry(new ItemDrop(Blocks.iron_ore, 12, 32), 35);
		lootItems.addEntry(new ItemDrop(Blocks.gold_ore, 8, 24), 15);

		lootItems.addEntry(new OreDrop("oreCopper", 16, 40), 40);
		lootItems.addEntry(new OreDrop("oreTin", 16, 40), 40);
		lootItems.addEntry(new OreDrop("oreSilver", 12, 24), 20);
		lootItems.addEntry(new OreDrop("oreNickel", 12, 24), 15);
		lootItems.addEntry(new OreDrop("oreLead", 12, 24), 20);
		//lootItems.addEntry(new OreDrop("oreAluminium", 12, 24), 30);

		lootItems.addEntry(new ItemDrop(Items.diamond, 1, 4), 5);
		lootItems.addEntry(new ItemDrop(Items.diamond, 3, 12), 2);

		lootItems.addEntry(new ItemDrop(ReikaItemHelper.lapisDye, 2, 6), 20);
		lootItems.addEntry(new ItemDrop(ReikaItemHelper.lapisDye, 12, 30), 5);

		lootItems.addEntry(new ItemDrop(Items.redstone, 4, 64), 40);

		lootItems.addEntry(new ItemDrop(Items.coal, 16, 64), 40);
		lootItems.addEntry(new ItemDrop(Items.coal, 4, 24), 60);

		lootItems.addEntry(new ItemDrop(Items.gold_ingot, 4, 16), 25);
		lootItems.addEntry(new ItemDrop(Items.gold_ingot, 16, 40), 10);

		lootItems.addEntry(new ItemDrop(Items.iron_ingot, 16, 40), 60);
		lootItems.addEntry(new ItemDrop(Items.iron_ingot, 32, 64), 20);

		lootItems.addEntry(new OreDrop("ingotNickel", 16, 48), 40);
		lootItems.addEntry(new OreDrop("ingotLead", 16, 48), 40);
		lootItems.addEntry(new OreDrop("ingotSilver", 16, 48), 50);

		lootItems.addEntry(new ItemDrop(Items.flint, 12, 32), 40);
		lootItems.addEntry(new ItemDrop(Items.clay_ball, 30, 60), 40);

		lootItems.addEntry(new ItemDrop(Items.slime_ball, 10, 20), 15);
		lootItems.addEntry(new ItemDrop(Items.bone, 5, 15), 40);
		lootItems.addEntry(new ItemDrop(Items.rotten_flesh, 5, 15), 50);
		lootItems.addEntry(new ItemDrop(Items.string, 10, 30), 50);
		lootItems.addEntry(new ItemDrop(Items.gunpowder, 5, 20), 30);
		lootItems.addEntry(new ItemDrop(Items.leather, 10, 25), 30);
		lootItems.addEntry(new ItemDrop(Items.feather, 5, 15), 25);
		lootItems.addEntry(new ItemDrop(ReikaItemHelper.inksac, 5, 15), 30);
		lootItems.addEntry(new ItemDrop(Items.ender_pearl, 4, 12), 10);

		lootItems.addEntry(new ItemDrop(Items.wheat, 18, 30), 35);
		lootItems.addEntry(new ItemDrop(Items.carrot, 18, 30), 35);
		lootItems.addEntry(new ItemDrop(Items.potato, 18, 30), 35);
		lootItems.addEntry(new ItemDrop(Items.apple, 18, 30), 35);
		lootItems.addEntry(new ItemDrop(Items.porkchop, 8, 16), 20);
		lootItems.addEntry(new ItemDrop(Items.beef, 8, 16), 20);
		lootItems.addEntry(new ItemDrop(Items.fish, 8, 16), 20);
		lootItems.addEntry(new ItemDrop(Items.chicken, 8, 16), 20);

		lootItems.addEntry(new ItemDrop(Items.reeds, 1, 6), 20);

		for (int i = 0; i < 16; i++) {
			lootItems.addEntry(new ItemDrop(ChromaItems.SHARD.getStackOfMetadata(i), 2, 8), 5);
			lootItems.addEntry(new ItemDrop(ChromaBlocks.CRYSTAL.getStackOfMetadata(i), 2, 8), 1);
		}
		lootItems.addEntry(new ItemDrop(ChromaStacks.auraDust, 6, 30), 5);

		lootItems.addEntry(new ItemDrop(Items.lava_bucket, 1, 1), 10);

		lootItems.addEntry(new ItemDrop(Items.quartz, 12, 32), 15);
		lootItems.addEntry(new ItemDrop(Items.glowstone_dust, 4, 12), 25);
		lootItems.addEntry(new ItemDrop(Items.glowstone_dust, 16, 32), 5);
		lootItems.addEntry(new ItemDrop(Items.blaze_powder, 4, 8), 15);
		if (ModList.THAUMCRAFT.isLoaded()) {
			lootItems.addEntry(new ItemDrop(Items.blaze_powder, 12, 24), 10);
		}

		lootItems.addEntry(new ItemDrop(Blocks.torch, 4, 20), 50);
		lootItems.addEntry(new ItemDrop(Blocks.planks, 24, 64), 50);
		lootItems.addEntry(new ItemDrop(Blocks.sand, 24, 64), 50);

		lootItems.addEntry(new ItemDrop(Blocks.obsidian, 4, 8), 10);
		lootItems.addEntry(new ItemDrop(Blocks.obsidian, 8, 16), 5);

		lootItems.addEntry(new ItemDrop(Blocks.mossy_cobblestone, 16, 32), 15);

		lootItems.addEntry(new ItemDrop(Blocks.cobblestone, 32, 64), 100);
		lootItems.addEntry(new ItemDrop(Blocks.dirt, 32, 64), 100);
		lootItems.addEntry(new ItemDrop(Blocks.gravel, 32, 64), 40);

		lootItems.addEntry(new ItemDrop(ReikaItemHelper.redDye, 4, 16), 20);
		lootItems.addEntry(new ItemDrop(ReikaItemHelper.yellowDye, 4, 16), 20);
		lootItems.addEntry(new ItemDrop(ReikaItemHelper.cactusDye, 4, 16), 20);
		lootItems.addEntry(new ItemDrop(Items.paper, 2, 8), 15);
	}

	private static void addOreIf(ModOreList ore, int wt) {
		if (ore.existsInGame())
			furnaceOres.addEntry(ore, wt);
	}

	private final TileCallback furnaceCall = new TileCallback() {

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			world.setBlock(x, y+1, z, ChromaBlocks.HEATLAMP.getBlockInstance(), ForgeDirection.UP.ordinal(), 2);
			((TileEntityHeatLamp)world.getTileEntity(x, y+1, z)).temperature = ReikaRandomHelper.getRandomBetween(50, 160);
			//BlockFurnace.updateFurnaceBlockState(true, world, x, y, z);
			TileEntityFurnace tf = (TileEntityFurnace)te;
			if (tf == null) {
				tf = new TileEntityFurnace();
				world.setTileEntity(x, y, z, tf);
			}
			OreType ore = furnaceOres.getRandomEntry();
			ItemStack is = ReikaJavaLibrary.getRandomCollectionEntry(world.rand, ore.getAllOreBlocks());
			is = is.copy();
			int max = is.getMaxStackSize();
			if (ore.getRarity() == OreRarity.RARE)
				max = Math.min(max, 8);
			else if (ore.getRarity() == OreRarity.SCARCE)
				max = Math.min(max, 24);
			else if (ore.getRarity() == OreRarity.SCATTERED)
				max = Math.min(max, 40);
			is.stackSize = ReikaRandomHelper.getRandomBetween(4, max);
			tf.setInventorySlotContents(0, is);
			world.setBlockMetadataWithNotify(x, y, z, 3, 3);
		}

	};

	private final TileCallback doorCall = new TileCallback() {

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (doorID != null) {
				if (te == null) {
					te = new BlockChromaDoor.TileEntityChromaDoor();
					world.setTileEntity(x, y, z, te);
				}
				((BlockChromaDoor.TileEntityChromaDoor)te).bindUUID(null, doorID, 0);
			}
			else {
				throw new IllegalStateException("Burrow has no chest ID!");
			}
		}

	};

	private final TileCallback keyChestCall = new TileCallback() {

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (doorID != null) {
				if (te == null) {
					te = new TileEntityChest();
					world.setTileEntity(x, y, z, te);
				}
				((TileEntityChest)te).setInventorySlotContents(world.rand.nextInt(27), this.getDoorKey());
			}
			else {
				throw new IllegalStateException("Burrow has no chest ID!");
			}
		}

		private ItemStack getDoorKey() {
			ItemStack ret = ChromaItems.KEY.getStackOf();
			((ItemDoorKey)ret.getItem()).setID(ret, doorID);
			return ret;
		}

	};

	private final TileCallback lootCall = new TileCallback() {

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity tile) {
			TileEntityLootChest te = (TileEntityLootChest)tile;
			if (te == null) {
				te = new TileEntityLootChest();
				world.setTileEntity(x, y, z, te);
			}
			ReikaInventoryHelper.clearInventory(te);

			int filled = ReikaRandomHelper.getRandomBetween(13, 20); //was 9 & 27, then 18 & 27, then 8 & 16, then 12 & 18
			ArrayList<ItemStack> add = new ArrayList();
			for (int i = 0; i < filled; i++) {
				ItemStack is = lootItems.getRandomEntry().getItem();
				while (is == null) {
					is = lootItems.getRandomEntry().getItem();
				}
				add.add(is);
			}
			BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
			if (ReikaBiomeHelper.isSnowBiome(biome) && world.rand.nextInt(4) == 0) {
				add.add(new ItemStack(Blocks.ice, ReikaRandomHelper.getRandomBetween(12, 32), 0));
			}
			if (world.rand.nextInt(3) == 0) {
				TreeType tree = ReikaBiomeHelper.getDominantTreeType(biome);
				if (tree != null) {
					if (tree.getSaplingID() != null) {
						ItemStack sapling = new ItemStack(tree.getSaplingID(), ReikaRandomHelper.getRandomBetween(1, 6), tree.getSaplingMeta());
						add.add(sapling);
					}
				}
			}
			add = ReikaItemHelper.collateItemList(add);
			Collections.sort(add, ReikaItemHelper.comparator);
			int slot = 0;
			boolean items = false;
			for (int i = 0; i < add.size(); i++) {
				ItemStack stack = add.get(i);
				if (Block.getBlockFromItem(stack.getItem()) == Blocks.air) {
					if (!items)
						slot = 27;
					items = true;
				}
				te.setInventorySlotContents(slot, stack);
				slot++;
			}
		}

		private ItemStack getDoorKey() {
			ItemStack ret = ChromaItems.KEY.getStackOf();
			((ItemDoorKey)ret.getItem()).setID(ret, doorID);
			return ret;
		}

	};

	@Override
	protected void preCallbacks(World world, Random rand) {
		doorID = UUID.randomUUID();
	}

	@Override
	public Coordinate getControllerRelativeLocation() {
		return new Coordinate(-5, -8, -2);
	}

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		y -= 11;
		x -= 8;
		z -= 5;

		//Cracking block
		array.setBlock(x+5, y+4, z+3, shield, BlockType.STONE.metadata);

		array.setBlock(x+0, y+2, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+0, y+3, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+0, y+5, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+0, y+5, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+0, y+6, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+0, y+6, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+0, y+6, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+0, y+7, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+1, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+1, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+1, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+2, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+2, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+3, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+3, z+3, shield, BlockType.CRACK.metadata);
		array.setBlock(x+1, y+3, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+4, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+4, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+4, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+5, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+5, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+5, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+5, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+5, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+6, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+6, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+6, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+6, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+7, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+7, z+3, shield, BlockType.CRACK.metadata);
		array.setBlock(x+1, y+7, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+7, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+8, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+0, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+0, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+0, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+1, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+1, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+2, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+2, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+3, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+3, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+4, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+4, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+4, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+4, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+4, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+5, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+5, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+5, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+5, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+6, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+6, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+6, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+6, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+7, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+7, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+8, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+8, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+8, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+0, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+0, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+0, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+1, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+1, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+2, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+2, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+3, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+3, z+1, shield, BlockType.CRACK.metadata);
		array.setBlock(x+3, y+3, z+5, shield, BlockType.CRACK.metadata);
		array.setBlock(x+3, y+3, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+4, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+4, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+4, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+4, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+4, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+5, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+5, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+6, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+6, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+7, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+7, z+1, shield, BlockType.CRACK.metadata);
		array.setBlock(x+3, y+7, z+5, shield, BlockType.CRACK.metadata);
		array.setBlock(x+3, y+7, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+8, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+8, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+8, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+8, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+8, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+0, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+0, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+0, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+1, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+1, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+3, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+3, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+4, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+4, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+4, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+4, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+4, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+5, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+5, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+5, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+5, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+6, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+6, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+6, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+6, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+7, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+7, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+8, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+8, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+8, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+1, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+1, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+1, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+1, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+1, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+2, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+2, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+3, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+3, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+4, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+4, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+5, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+5, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+6, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+6, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+7, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+7, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+8, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+8, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+2, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+2, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+2, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+3, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+3, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+3, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+4, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+5, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+5, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+5, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+6, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+6, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+7, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+7, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+8, z+1, Blocks.stone);
		array.setBlock(x+6, y+8, z+5, Blocks.stone);
		array.setBlock(x+7, y+6, z+1, Blocks.stone);
		array.setBlock(x+7, y+6, z+2, Blocks.stone);
		array.setBlock(x+7, y+6, z+3, Blocks.stone);
		array.setBlock(x+7, y+6, z+4, Blocks.stone);
		array.setBlock(x+7, y+7, z+1, Blocks.stone);
		array.setBlock(x+7, y+7, z+5, Blocks.stone);
		array.setBlock(x+7, y+8, z+1, Blocks.stone);
		array.setBlock(x+7, y+8, z+5, Blocks.stone);
		array.setBlock(x+8, y+7, z+2, Blocks.stone);
		array.setBlock(x+8, y+7, z+3, Blocks.stone);
		array.setBlock(x+8, y+7, z+4, Blocks.stone);

		//Covering
		array.setBlock(x+7, y+10, z+5, Blocks.grass);
		array.setBlock(x+7, y+11, z+2, Blocks.grass);
		array.setBlock(x+7, y+11, z+3, Blocks.grass);
		array.setBlock(x+7, y+11, z+4, Blocks.grass);
		array.setBlock(x+8, y+8, z+2, Blocks.dirt);
		array.setBlock(x+8, y+8, z+3, Blocks.dirt);
		array.setBlock(x+8, y+8, z+4, Blocks.dirt);
		array.setBlock(x+8, y+9, z+2, Blocks.dirt);
		array.setBlock(x+8, y+9, z+3, Blocks.grass);
		array.setBlock(x+8, y+9, z+4, Blocks.dirt);
		array.setBlock(x+8, y+10, z+1, Blocks.grass);
		array.setBlock(x+8, y+10, z+2, Blocks.grass);
		array.setBlock(x+8, y+10, z+4, Blocks.grass);
		array.setBlock(x+8, y+10, z+5, Blocks.grass);
		array.setBlock(x+9, y+10, z+1, Blocks.grass);
		array.setBlock(x+9, y+10, z+2, Blocks.grass);
		array.setBlock(x+9, y+10, z+3, Blocks.grass);
		array.setBlock(x+9, y+10, z+4, Blocks.grass);
		array.setBlock(x+9, y+10, z+5, Blocks.grass);
		array.setBlock(x+7, y+9, z+1, Blocks.dirt);
		array.setBlock(x+7, y+9, z+5, Blocks.dirt);
		array.setBlock(x+7, y+10, z+1, Blocks.dirt);
		array.setBlock(x+6, y+9, z+1, Blocks.dirt);
		array.setBlock(x+6, y+9, z+5, Blocks.dirt);
		array.setBlock(x+6, y+10, z+2, Blocks.dirt);
		array.setBlock(x+6, y+10, z+3, Blocks.dirt);
		array.setBlock(x+6, y+10, z+4, Blocks.dirt);
		array.setBlock(x+6, y+11, z+2, Blocks.grass);
		array.setBlock(x+6, y+11, z+3, Blocks.grass);
		array.setBlock(x+6, y+11, z+4, Blocks.grass);
		array.setBlock(x+5, y+9, z+1, Blocks.dirt);
		array.setBlock(x+5, y+9, z+5, Blocks.dirt);
		array.setBlock(x+5, y+10, z+2, Blocks.dirt);
		array.setBlock(x+5, y+10, z+3, Blocks.dirt);
		array.setBlock(x+5, y+10, z+4, Blocks.dirt);
		array.setBlock(x+5, y+11, z+2, Blocks.grass);
		array.setBlock(x+5, y+11, z+3, Blocks.grass);
		array.setBlock(x+5, y+11, z+4, Blocks.grass);
		array.setBlock(x+4, y+9, z+2, Blocks.dirt);
		array.setBlock(x+4, y+9, z+3, Blocks.dirt);
		array.setBlock(x+4, y+9, z+4, Blocks.dirt);

		//ReikaJavaLibrary.pConsole("Running burrow lamp "+this.getCurrentColor()+" on "+Thread.currentThread().getName());
		array.setBlock(x+3, y+1, z+3, ChromaBlocks.LAMP.getBlockInstance(), this.getCurrentColor().ordinal());
		array.setBlock(x+3, y+5, z+3, Blocks.torch, 5);

		//Chests
		this.addLootChest(array, x+3, y+6, z+1, ForgeDirection.SOUTH);
		this.addLootChest(array, x+1, y+6, z+3, ForgeDirection.EAST);
		this.addLootChest(array, x+3, y+6, z+5, ForgeDirection.NORTH);
		this.addLootChest(array, x+3, y+2, z+1, ForgeDirection.SOUTH);
		this.addLootChest(array, x+1, y+2, z+3, ForgeDirection.EAST);
		this.addLootChest(array, x+3, y+2, z+5, ForgeDirection.NORTH);

		//Air
		array.setBlock(x+2, y+1, z+2, Blocks.air);
		array.setBlock(x+2, y+1, z+3, Blocks.air);
		array.setBlock(x+2, y+1, z+4, Blocks.air);
		array.setBlock(x+2, y+2, z+2, Blocks.air);
		array.setBlock(x+2, y+2, z+3, Blocks.air);
		array.setBlock(x+2, y+2, z+4, Blocks.air);
		array.setBlock(x+2, y+3, z+2, Blocks.air);
		array.setBlock(x+2, y+3, z+3, Blocks.air);
		array.setBlock(x+2, y+3, z+4, Blocks.air);
		array.setBlock(x+2, y+5, z+2, Blocks.air);
		array.setBlock(x+2, y+5, z+3, Blocks.air);
		array.setBlock(x+2, y+5, z+4, Blocks.air);
		array.setBlock(x+2, y+6, z+2, Blocks.air);
		array.setBlock(x+2, y+6, z+3, Blocks.air);
		array.setBlock(x+2, y+6, z+4, Blocks.air);
		array.setBlock(x+2, y+7, z+2, Blocks.air);
		array.setBlock(x+2, y+7, z+3, Blocks.air);
		array.setBlock(x+2, y+7, z+4, Blocks.air);
		array.setBlock(x+3, y+1, z+2, Blocks.air);
		array.setBlock(x+3, y+1, z+4, Blocks.air);
		array.setBlock(x+3, y+2, z+2, Blocks.air);
		array.setBlock(x+3, y+2, z+3, Blocks.air);
		array.setBlock(x+3, y+2, z+4, Blocks.air);
		array.setBlock(x+3, y+3, z+2, Blocks.air);
		array.setBlock(x+3, y+3, z+3, Blocks.air);
		array.setBlock(x+3, y+3, z+4, Blocks.air);
		array.setBlock(x+3, y+5, z+2, Blocks.air);
		array.setBlock(x+3, y+5, z+4, Blocks.air);
		array.setBlock(x+3, y+6, z+2, Blocks.air);
		array.setBlock(x+3, y+6, z+3, Blocks.air);
		array.setBlock(x+3, y+6, z+4, Blocks.air);
		array.setBlock(x+3, y+7, z+2, Blocks.air);
		array.setBlock(x+3, y+7, z+3, Blocks.air);
		array.setBlock(x+3, y+7, z+4, Blocks.air);
		array.setBlock(x+4, y+1, z+2, Blocks.air);
		array.setBlock(x+4, y+1, z+3, Blocks.air);
		array.setBlock(x+4, y+1, z+4, Blocks.air);
		array.setBlock(x+4, y+2, z+2, Blocks.air);
		array.setBlock(x+4, y+2, z+3, Blocks.air);
		array.setBlock(x+4, y+2, z+4, Blocks.air);
		array.setBlock(x+4, y+3, z+2, Blocks.air);
		array.setBlock(x+4, y+3, z+3, Blocks.air);
		array.setBlock(x+4, y+3, z+4, Blocks.air);
		array.setBlock(x+4, y+5, z+2, Blocks.air);
		array.setBlock(x+4, y+5, z+3, Blocks.air);
		array.setBlock(x+4, y+5, z+4, Blocks.air);
		array.setBlock(x+4, y+6, z+2, Blocks.air);
		array.setBlock(x+4, y+6, z+3, Blocks.air);
		array.setBlock(x+4, y+6, z+4, Blocks.air);
		array.setBlock(x+4, y+7, z+2, Blocks.air);
		array.setBlock(x+4, y+7, z+3, Blocks.air);
		array.setBlock(x+4, y+7, z+4, Blocks.air);
		array.setBlock(x+5, y+2, z+2, Blocks.air);
		array.setBlock(x+5, y+2, z+3, Blocks.air);
		array.setBlock(x+5, y+2, z+4, Blocks.air);
		array.setBlock(x+5, y+3, z+2, Blocks.air);
		array.setBlock(x+5, y+3, z+3, Blocks.air);
		array.setBlock(x+5, y+3, z+4, Blocks.air);
		array.setBlock(x+5, y+5, z+2, Blocks.air);
		array.setBlock(x+5, y+5, z+3, Blocks.air);
		array.setBlock(x+5, y+5, z+4, Blocks.air);
		array.setBlock(x+5, y+6, z+2, Blocks.air);
		array.setBlock(x+5, y+6, z+3, Blocks.air);
		array.setBlock(x+5, y+6, z+4, Blocks.air);
		array.setBlock(x+5, y+7, z+2, Blocks.air);
		array.setBlock(x+5, y+7, z+3, Blocks.air);
		array.setBlock(x+5, y+7, z+4, Blocks.air);
		array.setBlock(x+5, y+8, z+2, Blocks.air);
		array.setBlock(x+5, y+8, z+3, Blocks.air);
		array.setBlock(x+5, y+8, z+4, Blocks.air);
		array.setBlock(x+5, y+9, z+2, Blocks.air);
		array.setBlock(x+5, y+9, z+3, Blocks.air);
		array.setBlock(x+5, y+9, z+4, Blocks.air);
		array.setBlock(x+6, y+6, z+2, Blocks.air);
		array.setBlock(x+6, y+6, z+3, Blocks.air);
		array.setBlock(x+6, y+6, z+4, Blocks.air);
		array.setBlock(x+6, y+7, z+2, Blocks.air);
		array.setBlock(x+6, y+7, z+3, Blocks.air);
		array.setBlock(x+6, y+7, z+4, Blocks.air);
		array.setBlock(x+6, y+8, z+2, Blocks.air);
		array.setBlock(x+6, y+8, z+3, Blocks.air);
		array.setBlock(x+6, y+8, z+4, Blocks.air);
		array.setBlock(x+6, y+9, z+2, Blocks.air);
		array.setBlock(x+6, y+9, z+3, Blocks.air);
		array.setBlock(x+6, y+9, z+4, Blocks.air);
		array.setBlock(x+7, y+7, z+2, Blocks.air);
		array.setBlock(x+7, y+7, z+3, Blocks.air);
		array.setBlock(x+7, y+7, z+4, Blocks.air);
		array.setBlock(x+7, y+8, z+2, Blocks.air);
		array.setBlock(x+7, y+8, z+3, Blocks.air);
		array.setBlock(x+7, y+8, z+4, Blocks.air);
		array.setBlock(x+7, y+9, z+2, Blocks.air);
		array.setBlock(x+7, y+9, z+3, Blocks.air);
		array.setBlock(x+7, y+9, z+4, Blocks.air);
		array.setBlock(x+7, y+10, z+2, Blocks.air);
		array.setBlock(x+7, y+10, z+3, Blocks.air);
		array.setBlock(x+7, y+10, z+4, Blocks.air);

		//Water pit, if cannot stop it genning under lakes
		//array.setBlock(x+7, y+5, z+3, Blocks.air);
		//array.setBlock(x+7, y+6, z+3, Blocks.air);

		//Entry Blocks
		array.setBlock(x+8, y+10, z+3, Blocks.air);
		array.setBlock(x+8, y+11, z+3, Blocks.air);
		array.setBlock(x+8, y+11, z+2, Blocks.air);
		array.setBlock(x+8, y+11, z+4, Blocks.air);

		return array;
	}

	public FilledBlockArray getFurnaceRoom(World world, int x, int y, int z) {
		x -= 5;
		y -= 11;
		z -= 6;

		FilledBlockArray array = new FilledBlockArray(world);

		array.setBlock(x+1, y+2, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+2, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+2, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+3, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+3, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+3, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+4, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+4, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+4, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+1, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+1, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+1, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+2, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+3, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+4, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+4, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+4, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+5, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+5, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+5, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+1, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+1, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+1, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+2, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+2, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+2, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+3, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+3, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+3, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+4, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+4, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+4, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+5, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+5, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+5, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+5, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+3, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+3, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+4, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+4, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+5, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+5, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+5, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+5, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+2, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+2, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+2, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+2, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+2, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+3, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+3, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+4, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+4, z+10, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+5, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+5, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+5, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+5, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+3, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+3, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+3, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+3, z+9, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+4, z+6, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+4, z+7, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+4, z+8, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+4, z+9, shield, BlockType.STONE.metadata);

		array.setBlock(x+2, y+2, z+6, shield, BlockType.CRACK.metadata); //cracks
		array.setBlock(x+2, y+3, z+6, shield, BlockType.CRACK.metadata);

		this.placeFurnace(array, x+4, y+3, z+6);
		this.placeFurnace(array, x+5, y+3, z+6);

		array.setBlock(x+2, y+2, z+8, Blocks.torch, 5);
		array.setBlock(x+5, y+3, z+8, Blocks.torch, 5);

		array.setBlock(x+5, y+3, z+7, Blocks.air);
		array.setBlock(x+5, y+3, z+9, Blocks.air);
		array.setBlock(x+5, y+4, z+7, Blocks.air);
		array.setBlock(x+5, y+4, z+8, Blocks.air);
		array.setBlock(x+5, y+4, z+9, Blocks.air);
		array.setBlock(x+2, y+2, z+7, Blocks.air);
		array.setBlock(x+2, y+2, z+9, Blocks.air);
		array.setBlock(x+2, y+3, z+7, Blocks.air);
		array.setBlock(x+2, y+3, z+8, Blocks.air);
		array.setBlock(x+2, y+3, z+9, Blocks.air);
		array.setBlock(x+2, y+4, z+8, Blocks.air);
		array.setBlock(x+2, y+4, z+9, Blocks.air);
		array.setBlock(x+3, y+2, z+8, Blocks.air);
		array.setBlock(x+3, y+2, z+9, Blocks.air);
		array.setBlock(x+3, y+3, z+8, Blocks.air);
		array.setBlock(x+3, y+3, z+9, Blocks.air);
		array.setBlock(x+3, y+4, z+8, Blocks.air);
		array.setBlock(x+3, y+4, z+9, Blocks.air);
		array.setBlock(x+4, y+3, z+7, Blocks.air);
		array.setBlock(x+4, y+3, z+8, Blocks.air);
		array.setBlock(x+4, y+3, z+9, Blocks.air);
		array.setBlock(x+4, y+4, z+7, Blocks.air);
		array.setBlock(x+4, y+4, z+8, Blocks.air);
		array.setBlock(x+4, y+4, z+9, Blocks.air);

		return array;
	}

	public FilledBlockArray getLootRoom(World world, int x, int y, int z) {

		x -= 5;
		y -= 11;
		z -= 6;

		FilledBlockArray array = new FilledBlockArray(world);

		array.setBlock(x+1, y+2, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+1, y+3, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+1, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+1, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+2, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+3, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+4, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+2, y+4, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+1, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+1, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+1, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+1, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+2, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+2, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+3, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+3, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+4, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+4, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+3, y+4, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+0, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+0, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+0, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+0, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+1, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+1, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+1, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+1, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+2, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+3, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+3, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+3, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+3, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+4, y+4, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+0, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+0, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+0, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+0, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+1, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+1, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+2, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+2, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+3, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+3, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+3, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+3, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+5, y+4, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+0, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+0, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+0, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+0, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+1, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+1, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+2, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+2, z+5, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+3, z+0, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+3, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+3, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+3, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+6, y+4, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+1, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+1, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+1, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+1, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+2, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+2, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+2, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+2, z+4, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+3, z+1, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+3, z+2, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+3, z+3, shield, BlockType.STONE.metadata);
		array.setBlock(x+7, y+3, z+4, shield, BlockType.STONE.metadata);

		array.setBlock(x+2, y+2, z+2, shield, BlockType.CRACK.metadata); //cracks
		array.setBlock(x+2, y+3, z+2, shield, BlockType.CRACK.metadata);

		array.setBlock(x+3, y+4, z+6, shield, BlockType.CRACK.metadata); //crack to expose chest

		this.placeDoor(array, x+5, y+1, z+2); //door
		this.placeDoor(array, x+5, y+2, z+2);
		this.placeDoor(array, x+6, y+1, z+2);
		this.placeDoor(array, x+6, y+2, z+2);

		array.setBlock(x+3, y+4, z+5, Blocks.chest, 3); //key holder
		if (this.isWorldgen())
			this.addCallback(x+3, y+4, z+5, keyChestCall);

		array.setBlock(x+6, y+1, z+4, getChestGen(), 2); //chests
		array.setBlock(x+5, y+1, z+4, getChestGen(), 2);
		if (this.isWorldgen()) {
			this.addCallback(x+6, y+1, z+4, lootCall);
			this.addCallback(x+5, y+1, z+4, lootCall);
		}

		array.setBlock(x+3, y+2, z+1, Blocks.torch, 5);
		array.setBlock(x+6, y+1, z+3, Blocks.torch, 5);

		array.setBlock(x+2, y+2, z+1, Blocks.air);
		array.setBlock(x+3, y+3, z+1, Blocks.air);
		array.setBlock(x+2, y+3, z+1, Blocks.air);
		array.setBlock(x+4, y+1, z+1, Blocks.air);
		array.setBlock(x+4, y+2, z+1, Blocks.air);
		array.setBlock(x+4, y+3, z+1, Blocks.air);
		array.setBlock(x+5, y+1, z+1, Blocks.air);
		array.setBlock(x+5, y+1, z+3, Blocks.air);
		array.setBlock(x+5, y+2, z+1, Blocks.air);
		array.setBlock(x+5, y+2, z+3, Blocks.air);
		array.setBlock(x+5, y+2, z+4, Blocks.air);
		array.setBlock(x+5, y+3, z+1, Blocks.air);
		array.setBlock(x+6, y+1, z+1, Blocks.air);
		array.setBlock(x+6, y+2, z+1, Blocks.air);
		array.setBlock(x+6, y+2, z+3, Blocks.air);
		array.setBlock(x+6, y+2, z+4, Blocks.air);
		array.setBlock(x+6, y+3, z+1, Blocks.air);

		return array;
	}

	private void placeFurnace(FilledBlockArray array, int x, int y, int z) {
		array.setBlock(x, y, z, Blocks.lit_furnace, 3);
		if (this.isWorldgen())
			this.addCallback(x, y, z, furnaceCall);
	}

	private void placeDoor(FilledBlockArray array, int x, int y, int z) {
		array.setBlock(x, y, z, ChromaBlocks.DOOR.getBlockInstance(), BlockChromaDoor.getMetadata(false, false, true, true));
		if (this.isWorldgen())
			this.addCallback(x, y, z, doorCall);
	}

	@Override
	public int getStructureVersion() {
		return 0;
	}

	@Override
	public void resetToDefaults() {
		super.resetToDefaults();
		doorID = null;
	}

	@Override
	public int getChestYield(Coordinate c, TileEntityLootChest te, FilledBlockArray arr, Random r) {
		return c.yCoord <= arr.getMinY()+4 ? 0 : -1;
	}

	@Override
	public String getChestLootTable(Coordinate c, TileEntityLootChest te, FilledBlockArray arr, Random r) {
		return ChestGenHooks.BONUS_CHEST;
	}

	@Override
	public int modifyLootCount(TileEntityLootChest tileEntityLootChest, String s, int bonus, Random r, int count) {
		return (int)(count*(0.4+bonus*0.2F));
	}

	@Override
	public float getFragmentChance(TileEntityLootChest te, String s, int bonus, Random r) {
		return 0.667F+bonus*0.208F;
	}

	@Override
	public int getFragmentCount(TileEntityLootChest te, String s, int bonus, Random r) {
		return r.nextInt(3) == 0 ? 2 : 1;
	}

}
