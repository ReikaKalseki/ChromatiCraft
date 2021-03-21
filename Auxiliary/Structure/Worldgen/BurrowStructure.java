package Reika.ChromatiCraft.Auxiliary.Structure.Worldgen;

import java.util.UUID;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.FragmentStructureBase;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.BlockHeatLamp.TileEntityHeatLamp;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Items.Tools.ItemDoorKey;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Interfaces.Registry.OreType.OreRarity;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;


public class BurrowStructure extends FragmentStructureBase {

	private static WeightedRandom<OreType> furnaceOres = new WeightedRandom();

	static {
		furnaceOres.addEntry(ReikaOreHelper.IRON, 40);
		furnaceOres.addEntry(ReikaOreHelper.GOLD, 15);
		addOreIf(ModOreList.COPPER, 50);
		addOreIf(ModOreList.TIN, 50);
		addOreIf(ModOreList.LEAD, 10);
		addOreIf(ModOreList.NICKEL, 10);
		addOreIf(ModOreList.SILVER, 15);
		addOreIf(ModOreList.ALUMINUM, 20);
		addOreIf(ModOreList.PLATINUM, 2);
	}

	private static void addOreIf(ModOreList ore, int wt) {
		if (ore.existsInGame())
			furnaceOres.addEntry(ore, wt);
	}

	private final TileCallback furnaceCall = new TileCallback() {

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			TileEntityFurnace tf = (TileEntityFurnace)te;
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

			//tf.furnaceCookTime = ReikaRandomHelper.getRandomBetween(100, 190);
			//freezeTile(tf);
		}

	};

	private final TileCallback heatLampCall = new TileCallback() {

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			((TileEntityHeatLamp)te).temperature = ReikaRandomHelper.getRandomBetween(TileEntityHeatLamp.MINTEMP, 199);
		}

	};

	private final TileCallback doorCall = new TileCallback() {

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (doorID == null)
				doorID = UUID.randomUUID();
			((BlockChromaDoor.TileEntityChromaDoor)te).bindUUID(null, doorID, 0);
		}

	};

	private final TileCallback keyChestCall = new TileCallback() {

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (doorID != null) {
				((TileEntityChest)te).setInventorySlotContents(world.rand.nextInt(27), this.getDoorKey());
			}
		}

		private ItemStack getDoorKey() {
			ItemStack ret = ChromaItems.KEY.getStackOf();
			((ItemDoorKey)ret.getItem()).setID(ret, doorID);
			return ret;
		}

	};

	private UUID doorID;

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
		array.setBlock(x+3, y+6, z+1, getChestGen(), getChestMeta(ForgeDirection.SOUTH));
		array.setBlock(x+1, y+6, z+3, getChestGen(), getChestMeta(ForgeDirection.EAST));
		array.setBlock(x+3, y+6, z+5, getChestGen(), getChestMeta(ForgeDirection.NORTH));
		array.setBlock(x+3, y+2, z+1, getChestGen(), getChestMeta(ForgeDirection.SOUTH));
		array.setBlock(x+1, y+2, z+3, getChestGen(), getChestMeta(ForgeDirection.EAST));
		array.setBlock(x+3, y+2, z+5, getChestGen(), getChestMeta(ForgeDirection.NORTH));

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
		y -= 3;
		z -= 4;

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

		this.placeHeatLamp(array, x+5, y+4, z+6); //heat lamps
		this.placeHeatLamp(array, x+4, y+4, z+6);

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
		y -= 3;
		z -= 4;

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
		this.addCallback(x+3, y+4, z+5, keyChestCall);

		array.setBlock(x+6, y+1, z+4, getChestGen(), 2); //chests
		array.setBlock(x+5, y+1, z+4, getChestGen(), 2);

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
		array.setBlock(x, y, z, Blocks.furnace, 3);
		this.addCallback(x, y, z, furnaceCall);
	}

	private void placeHeatLamp(FilledBlockArray array, int x, int y, int z) {
		array.setBlock(x, y, z, ChromaBlocks.HEATLAMP.getBlockInstance(), ForgeDirection.UP.ordinal());
		this.addCallback(x, y, z, heatLampCall);
	}

	private void placeDoor(FilledBlockArray array, int x, int y, int z) {
		array.setBlock(x, y, z, ChromaBlocks.DOOR.getBlockInstance(), BlockChromaDoor.getMetadata(false, false, true, true));
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

}
