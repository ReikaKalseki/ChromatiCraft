package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.ModInteract.DeepInteract.ItemCustomFocus;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BotaniaHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;

public class ItemManipulatorFocus extends ItemCustomFocus {

	public ItemManipulatorFocus() {
		super(ChromatiCraft.tabChromaTools);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return "chromaticraft:manip-focus";
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public AspectList getVisCost(ItemStack focusStack) {
		return new AspectList().add(Aspect.ORDER, 1);
	}

	@Override
	public ItemStack onFocusRightClick(ItemStack wand, World world, EntityPlayer player, MovingObjectPosition mov) {
		if (mov == null) {
			player.setItemInUse(wand, wand.getItem().getMaxItemUseDuration(wand));
		}
		else {
			ChromaItems.TOOL.getItemInstance().onItemUse(ChromaItems.TOOL.getStackOf(), player, world, mov.blockX, mov.blockY, mov.blockZ, mov.sideHit, (float)mov.hitVec.xCoord, (float)mov.hitVec.yCoord, (float)mov.hitVec.zCoord);
			if (this.canMultiTool(wand) && ModList.BOTANIA.isLoaded()) {
				Item petals = BotaniaHandler.getInstance().wandID;
				petals.onItemUse(new ItemStack(petals), player, world, mov.blockX, mov.blockY, mov.blockZ, mov.sideHit, (float)mov.hitVec.xCoord, (float)mov.hitVec.yCoord, (float)mov.hitVec.zCoord);
			}
		}
		return null;
	}

	private boolean canMultiTool(ItemStack wand) {
		ItemStack focus = ReikaThaumHelper.getWandFocusStack(wand);
		return focus != null && this.getUpgradeLevel(focus, FocusUpgradeType.enlarge) > 0;
	}

	@Override
	public void onUsingFocusTick(ItemStack wandstack, EntityPlayer player, int count) {
		ChromaItems.TOOL.getItemInstance().onUsingTick(ChromaItems.TOOL.getStackOf(), player, count);
	}

	@Override
	public EnumRarity getRarity(ItemStack focusstack) {
		return EnumRarity.uncommon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int par1) {
		return itemIcon;
	}

	/**
	 * What color will the focus orb be rendered on the held wand
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public int getFocusColor(ItemStack focusstack) {
		return CrystalElement.getBlendedColor(Minecraft.getMinecraft().thePlayer.ticksExisted, 12);
	}

	/**
	 * Does the focus have ornamentation like the focus of the nine hells. Ornamentation is a standard icon rendered in a cross around the focus
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getOrnament(ItemStack focusstack) {
		return null;//ChromaIcons.SUNFLARE.getIcon();
	}

	/**
	 * An icon to be rendered inside the focus itself
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getFocusDepthLayerIcon(ItemStack focusstack) {
		return ChromaIcons.LATTICEITEM.getIcon();
	}

	@Override
	public int getActivationCooldown(ItemStack focusstack) {
		return 50;
	}

	@Override
	protected String getID() {
		return "manipulator";
	}

	@Override
	public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focusstack, int rank) {
		return new FocusUpgradeType[] {FocusUpgradeType.enlarge};
	}

}
