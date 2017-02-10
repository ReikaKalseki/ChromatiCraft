package Reika.ChromatiCraft.Block.Worldgen;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Items.ItemUnknownArtefact.ArtefactTypes;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class BlockUnknownArtefact extends Block {

	public BlockUnknownArtefact(Material mat) {
		super(mat);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		return true;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ep) {

	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return super.getDrops(world, x, y, z, metadata, fortune);
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return false;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest) {
		this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		return world.setBlockToAir(x, y, z);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		if (EnchantmentHelper.getSilkTouchModifier(ep)) {
			ReikaItemHelper.dropItem(world, x+world.rand.nextDouble(), y+world.rand.nextDouble(), z+world.rand.nextDouble(), ChromaItems.ARTEFACT.getStackOfMetadata(ArtefactTypes.ARTIFACT.ordinal()));
			ProgressStage.ARTEFACT.stepPlayerTo(ep);
		}
		else {
			int n = 1+world.rand.nextInt(4);
			for (int i = 0; i < n; i++) {
				ReikaItemHelper.dropItem(world, x+world.rand.nextDouble(), y+world.rand.nextDouble(), z+world.rand.nextDouble(), ChromaItems.ARTEFACT.getStackOfMetadata(ArtefactTypes.FRAGMENT.ordinal()));
			}
		}
	}

}
