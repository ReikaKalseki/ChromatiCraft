package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;

@Strippable("thaumcraft.api.wands.ItemFocusBasic")
public class ItemManipulatorFocus extends ItemFocusBasic {

	public ItemManipulatorFocus(int idx) {
		this.setCreativeTab(ChromatiCraft.tabChromaTools);
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public AspectList getVisCost(ItemStack focusStack) {
		return new AspectList().add(Aspect.ORDER, 1);
	}

	@Override
	public ItemStack onFocusRightClick(ItemStack wand, World world, EntityPlayer player, MovingObjectPosition mov) {
		if (mov == null)
			return null;
		ChromaItems.TOOL.getItemInstance().onItemUse(ChromaItems.TOOL.getStackOf(), player, world, mov.blockX, mov.blockY, mov.blockZ, mov.sideHit, (float)mov.hitVec.xCoord, (float)mov.hitVec.yCoord, (float)mov.hitVec.zCoord);
		return null;
	}

}
