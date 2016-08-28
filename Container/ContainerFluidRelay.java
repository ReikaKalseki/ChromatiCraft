package Reika.ChromatiCraft.Container;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityFluidRelay;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.Slot.GhostSlot;


public class ContainerFluidRelay extends CoreContainer {

	private final TileEntityFluidRelay relay;

	public ContainerFluidRelay(EntityPlayer player, TileEntityFluidRelay te) {
		super(player, te);

		relay = te;

		for (int i = 0; i < relay.getFluidTypes().length; i++) {
			int x = 14+22*i;
			int y = 17;
			this.addSlotToContainer(new GhostSlot(i, x, y));
		}
		this.addPlayerInventoryWithOffset(ep, 0, -16);
	}

	@Override
	public boolean allowShiftClicking(EntityPlayer player, int slot, ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack slotClick(int slot, int mouse, int action, EntityPlayer ep) {
		if (slot >= 0 && slot <= 7) {
			ItemStack held = ep.inventory.getItemStack();
			if (mouse == 0) {
				FluidStack fs = held != null ? FluidContainerRegistry.getFluidForFilledItem(held) : null;
				Fluid f = fs != null ? fs.getFluid() : null;
				if (held != null && held.getItem() instanceof ItemBlock) {
					Block b = ((ItemBlock)held.getItem()).field_150939_a;
					f = FluidRegistry.lookupFluidForBlock(b);
				}
				relay.setFluid(slot, f);
			}
			return held;
		}

		ItemStack is = super.slotClick(slot, mouse, action, ep);
		InventoryPlayer ip = ep.inventory;
		return is;
	}

}
