package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.ModInteract.DeepInteract.ItemCustomFocus;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;

public class ItemAbilityFocus extends ItemCustomFocus {

	public ItemAbilityFocus() {
		super(ChromatiCraft.tabChromaTools);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return "chromaticraft:ability-focus";
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public AspectList getVisCost(ItemStack focusStack) {
		Ability ab = this.getAbility(focusStack);
		if (ab == null) {
			return new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1).add(Aspect.FIRE, 1).add(Aspect.AIR, 1).add(Aspect.WATER, 1).add(Aspect.EARTH, 1);
		}
		else {
			ElementTagCompound tag = AbilityHelper.instance.getUsageElementsFor(ab, null);
			AspectList al = new AspectList();
			for (CrystalElement e : tag.elementSet()) {
				for (Aspect a : ChromaAspectManager.instance.getAspects(e, true)) {
					AspectList al2 = ReikaThaumHelper.decompose(a);
					for (Aspect a2 : al2.aspects.keySet()) {
						al.add(a2, (int)(tag.getValue(e)*al2.getAmount(a2)*20*this.getCostScale(focusStack)));
					}
				}
			}
			return al;
		}
	}

	@Override
	public ItemStack onFocusRightClick(ItemStack wand, World world, EntityPlayer ep, MovingObjectPosition mov) {
		ItemStack focus = ReikaThaumHelper.getWandFocusStack(wand);
		if (focus == null || focus.stackTagCompound == null)
			return null;
		Ability a = Chromabilities.getAbility(focus.stackTagCompound.getString("ability"));
		if (a != null && Chromabilities.playerHasAbility(ep, a) && Chromabilities.canPlayerExecuteAt(ep, a)) {
			int pwr = focus.stackTagCompound.getInteger("power");
			pwr = Math.min(pwr, MathHelper.ceiling_float_int(a.getMaxPower()*this.getUpgradeLevel(focus, FocusUpgradeType.potency)/5F));
			Chromabilities.triggerAbility(ep, a, pwr, this.getCostScale(focus));
			ThaumcraftApi.internalMethods.consumeVisFromWand(wand, ep, this.getVisCost(focus), true, false);
		}
		else {
			ChromaSounds.ERROR.playSound(ep);
		}
		return null;
	}

	private float getCostScale(ItemStack focus) {
		return Math.max(0.5F, 1.5F-0.25F*this.getUpgradeLevel(focus, FocusUpgradeType.frugal));
	}

	@Override
	public boolean canApplyUpgrade(ItemStack focusstack, EntityPlayer player, FocusUpgradeType type, int rank) {
		return /*type == FocusUpgradeType.frugal ? rank <= 4 : */true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.ABILITYFOCUS.ordinal(), world, 0, 0, 0);
		return is;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		Ability a = this.getAbility(is);
		if (a != null) {
			li.add("Selected Ability: "+a.getDisplayName());
			li.add("Ability Power: "+is.stackTagCompound.getInteger("power"));
		}
		else {
			li.add("No ability selected.");
		}
		super.addInformation(is, ep, li, vb);
	}

	private Ability getAbility(ItemStack is) {
		return is.stackTagCompound == null ? null : Chromabilities.getAbility(is.stackTagCompound.getString("ability"));
	}

	@Override
	public EnumRarity getRarity(ItemStack focusstack) {
		return EnumRarity.rare;
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
		return ReikaColorAPI.getModifiedHue(0x22aaff, (int)(245+35*Math.sin(System.currentTimeMillis()/250D)));
	}

	/**
	 * Does the focus have ornamentation like the focus of the nine hells. Ornamentation is a standard icon rendered in a cross around the focus
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getOrnament(ItemStack focusstack) {
		return Items.glowstone_dust.itemIcon;//ChromaIcons.SUNFLARE.getIcon();
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
		return 500;
	}

	@Override
	protected String getID() {
		return "ability";
	}

	@Override
	public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focusstack, int rank) {
		return new FocusUpgradeType[] {FocusUpgradeType.frugal, FocusUpgradeType.potency};
	}

}
