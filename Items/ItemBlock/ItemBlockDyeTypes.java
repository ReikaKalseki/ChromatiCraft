package Reika.ChromatiCraft.Items.ItemBlock;

import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockDyeTypes extends ItemBlock {

	public ItemBlockDyeTypes(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	public void getSubItems(Item id, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++)
			par3List.add(new ItemStack(id, 1, i));
	}

	@Override
	public int getMetadata(int dmg)
	{
		return dmg;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int par2)
	{
		if (ChromaBlocks.DYE.match(is) || ChromaBlocks.DYELEAF.match(is) || ChromaBlocks.DECAY.match(is))
			return super.getColorFromItemStack(is, par2);

		return ReikaDyeHelper.getColorFromDamage(is.getItemDamage()).getJavaColor().brighter().getRGB();
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ChromaBlocks.getEntryByID(field_150939_a).getMultiValuedName(is.getItemDamage());
	}

}
