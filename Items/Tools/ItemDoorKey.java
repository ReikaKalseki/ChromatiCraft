package Reika.ChromatiCraft.Items.Tools;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.BlockChromaDoor.TileEntityChromaDoor;
import Reika.ChromatiCraft.Registry.ChromaBlocks;


public class ItemDoorKey extends ItemChromaTool {

	public ItemDoorKey(int index) {
		super(index);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		Block bk = world.getBlock(x, y, z);
		if (bk == ChromaBlocks.DOOR.getBlockInstance()) {
			TileEntityChromaDoor te = (TileEntityChromaDoor)world.getTileEntity(x, y, z);
			if (!world.isRemote) {
				UUID uid = this.getUID(is);
				if (ep.isSneaking() && te.isOwner(ep)) {
					te.bindUUID(uid);
				}
				else if (te.canOpen(ep, uid)) {
					te.open();
					if (BlockChromaDoor.consumeKey(world, x, y, z))
						ep.setCurrentItemOrArmor(0, null);
				}
				else {

				}
			}
			return true;
		}
		return false;
	}

	public UUID getUID(ItemStack is) {
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("uid")) {
			UUID uid = UUID.randomUUID();
			this.setID(is, uid);
			return uid;
		}
		return UUID.fromString(is.stackTagCompound.getString("uid"));
	}

	public void setID(ItemStack is, UUID uid) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setString("uid", uid.toString());
	}

}
