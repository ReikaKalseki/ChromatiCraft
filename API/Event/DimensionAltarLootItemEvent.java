package Reika.ChromatiCraft.API.Event;

import Reika.DragonAPI.Instantiable.Event.TileEntityEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

@Cancelable
public class DimensionAltarLootItemEvent extends TileEntityEvent {

	private final ItemStack item;

	public DimensionAltarLootItemEvent(TileEntity te, ItemStack is) {
		super(te);
		item = is;
	}

	public ItemStack getItem() {
		return item.copy();
	}

}
