package Reika.ChromatiCraft.Auxiliary.Render;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemBlockChangingWand;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class BlockChangeCache {

	private Coordinate overlayOrigin;
	private BlockKey overlayBlock;
	private BlockArray overlay;
	private boolean isSneak;
	private Item overlayItem;

	public void updateAndVerifyCache(World world, int x, int y, int z, BlockKey bk, EntityPlayer ep, ItemStack is) {
		Coordinate loc = new Coordinate(x, y, z);
		boolean sp = ep.isSneaking();

		if (!loc.equals(overlayOrigin) || !bk.equals(overlayBlock) || (isSneak != sp) || (is.getItem() != overlayItem)) {
			overlay = null;
		}
		overlayBlock = bk;
		overlayOrigin = loc;
		isSneak = sp;
		overlayItem = is.getItem();
	}

	public void clearCache() {
		overlay = null;
	}

	public BlockArray getCachedOverlay(World world, int x, int y, int z, Block id, int meta, EntityPlayer ep, ItemStack is) {
		if (overlay == null) {
			ItemBlockChangingWand i = (ItemBlockChangingWand)is.getItem();
			overlay = new BlockArray();
			overlay.maxDepth = i.getDepth(ep)-1;
			i.getSpreadBlocks(world, x, y, z, overlay, ep, is);
		}
		return overlay;
	}

}
