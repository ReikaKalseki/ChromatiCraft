package Reika.ChromatiCraft.Render.ISBRH;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower;
import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;


public class DecoFlowerRenderer implements ISimpleBlockRenderingHandler {

	public int renderPass;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		BlockDecoFlower b = (BlockDecoFlower)block;
		Flowers f = Flowers.list[world.getBlockMetadata(x, y, z)];
		Tessellator.instance.setColorOpaque_I(0xffffff);
		f.render(world, x, y, z, block, renderer, Tessellator.instance);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return 0;
	}

}
