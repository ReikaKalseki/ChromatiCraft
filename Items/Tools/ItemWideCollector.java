package Reika.ChromatiCraft.Items.Tools;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemWithItemFilter;
import Reika.ChromatiCraft.Items.OwnableItem;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemInserter;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;


public class ItemWideCollector extends ItemWithItemFilter {

	public ItemWideCollector(int index) {
		super(index);
	}

	@Override
	public boolean isCurrentlyEnabled(EntityPlayer ep, ItemStack tool) {
		return true;
	}

	@Override
	public boolean canBeReversed(EntityPlayer ep, ItemStack tool) {
		return false;
	}

	@Override
	public String getActionName(EntityPlayer ep, ItemStack tool) {
		return "Collecting";
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (!world.isRemote && e instanceof EntityPlayer && is.stackTagCompound != null && world.getTotalWorldTime()%4 == 0) {
			EntityPlayer ep = (EntityPlayer)e;
			AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(ep, 8);
			List<EntityItem> li = world.getEntitiesWithinAABB(EntityItem.class, box);
			for (EntityItem ei : li) {
				if (ei.isDead || ei.delayBeforeCanPickup > 0 || ei.getEntityData().getBoolean(TileEntityItemInserter.DROP_TAG))
					continue;
				ItemStack in = ei.getEntityItem();
				if (in == null || in.stackSize <= 0)
					continue;
				if (in.getItem() instanceof OwnableItem && !((OwnableItem)in.getItem()).isCollectableBy(ei, ep)) {
					continue;
				}
				if (!this.matchesItem(ep, is, in))
					continue;
				ei.onCollideWithPlayer(ep);
			}
		}
	}

}
