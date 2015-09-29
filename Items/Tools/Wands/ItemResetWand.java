package Reika.ChromatiCraft.Items.Tools.Wands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import Reika.ChromatiCraft.Auxiliary.ChunkResetter;
import Reika.ChromatiCraft.Base.ItemWandBase;

//Make look like SimCity 4 obliterate
public class ItemResetWand extends ItemWandBase {

	public ItemResetWand(int index) {
		super(index);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		int r = 2;
		if (world instanceof WorldServer) {
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					ChunkResetter.instance.addChunk((WorldServer)world, world.getChunkFromBlockCoords(x+i*16, z+k*16));
				}
			}
		}
		return true;
	}

}
