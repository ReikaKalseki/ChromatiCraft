package Reika.ChromatiCraft.Magic.Artefact.Effects;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Magic.Artefact.UABombingEffect;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class DropItemsEffect extends UABombingEffect.BlockEffect {

	@Override
	public void trigger(IInventory inv, TileEntity te) {
		ArrayList<Integer> slots = ReikaJavaLibrary.makeIntListFromArray(ReikaArrayHelper.getLinearArray(inv.getSizeInventory()));
		Collections.shuffle(slots);
		for (int slot : slots) {
			ItemStack in = inv.getStackInSlot(slot);
			if (in != null && in.getItem() != ChromaItems.ARTEFACT.getItemInstance()) {
				double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);
				double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);
				double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);
				EntityItem ei = ReikaItemHelper.dropItem(te.worldObj, te.xCoord+0.5, te.yCoord+0.75, te.zCoord+0.5, in);
				ei.motionX = vx;
				ei.motionY = vy;
				ei.motionZ = vz;
				ei.velocityChanged = true;
				inv.setInventorySlotContents(slot, null);
				break;
			}
		}
	}

}
