/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectJar;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalConsole;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityGuardianStone;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityDimensionCore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Interfaces.TileEntity.SidePlacedTile;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import com.bioxx.tfc.api.Enums.EnumItemReach;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;
import com.bioxx.tfc.api.Interfaces.ISize;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value = {"com.bioxx.tfc.api.Interfaces.ISize"})
public class ItemChromaPlacer extends Item implements ISize {

	public ItemChromaPlacer(int tex) {
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		maxStackSize = 64;
		this.setCreativeTab(ChromatiCraft.instance.isLocked() ? null : ChromatiCraft.tabChroma);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean select) {
		if (select && !world.isRemote) {
			if (ChromaTiles.TEList[is.getItemDamage()].isRepeater()) {
				if (world.getTotalWorldTime()%40 == 0) {
					int r = 64;
					int x = MathHelper.floor_double(e.posX);
					int y = MathHelper.floor_double(e.posY);
					int z = MathHelper.floor_double(e.posZ);
					for (CrystalNetworkTile te : CrystalNetworker.instance.getNearTilesOfType(world, x, y, z, TileEntityCrystalRepeater.class, r)) {
						((TileEntityCrystalRepeater)te).triggerConnectionRender();
					}
				}
			}
		}
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava) {
			if (side == 0)
				--y;
			if (side == 1)
				++y;
			if (side == 2)
				--z;
			if (side == 3)
				++z;
			if (side == 4)
				--x;
			if (side == 5)
				++x;
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava)
				return false;
		}
		if (!this.checkValidBounds(is, ep, world, x, y, z))
			return false;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z,	 x+1, y+1, z+1);
		List inblock = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		if (inblock.size() > 0)
			return false;
		ChromaTiles m = ChromaTiles.TEList[is.getItemDamage()];
		if (m == ChromaTiles.HEATLILY)
			return false;
		if (m.getBlock() == ChromaBlocks.DECOPLANT.getBlockInstance()) {
			if (m == ChromaTiles.COBBLEGEN && (!world.getBlock(x, y+1, z).isOpaqueCube() && ChromaTiles.getTile(world, x, y+1, z) != ChromaTiles.PLANTACCEL)) {
				return false;
			}
			else if (m == ChromaTiles.PLANTACCEL) {
				if (ChromaTiles.getTile(world, x, y-1, z) == m || ChromaTiles.getTile(world, x, y+1, z) == m) {

				}
				else if (world.getBlock(x, y-1, z).isOpaqueCube() || world.getBlock(x, y+1, z).isOpaqueCube()) {

				}
				else {
					return false;
				}
			}
			else if (m == ChromaTiles.CROPSPEED && world.getBlock(x, y-1, z) != Blocks.farmland && ChromaTiles.getTile(world, x, y-1, z) != ChromaTiles.PLANTACCEL)
				return false;
			else if ((m != ChromaTiles.COBBLEGEN && m != ChromaTiles.CROPSPEED) && (!world.getBlock(x, y-1, z).isOpaqueCube() && ChromaTiles.getTile(world, x, y-1, z) != ChromaTiles.PLANTACCEL)) {
				return false;
			}
		}
		if (m == ChromaTiles.ADJACENCY) { //prevent place of old item
			return false;
		}
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		if (!m.allowFakePlacer() && ReikaPlayerAPI.isFake(ep))
			return false;
		else {
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, m.getBlock(), m.getBlockMetadata(), 3);
		}
		Material mat = m.getBlock().getMaterial();
		Block b = m.getBlock();
		if (b == ChromaBlocks.TILECRYSTAL.getBlockInstance() || b == ChromaBlocks.TILECRYSTALNONCUBE.getBlockInstance()) {
			ReikaSoundHelper.playPlaceSound(world, x, y, z, m.getBlock());
		}
		else {
			if (mat == Material.iron) {
				ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.iron_block);
			}
			else if (mat == Material.rock) {
				ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.stone);
			}
			else if (mat == Material.plants) {
				ReikaSoundHelper.playBreakSound(world, x, y, z, Blocks.grass);
			}
			else if (mat == Material.glass) {
				ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.glass);
			}
			else {
				ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.wool);
			}
		}
		TileEntityChromaticBase te = (TileEntityChromaticBase)world.getTileEntity(x, y, z);
		te.setPlacer(ep);
		if (m.canBeVertical())
			te.setBlockMetadata(ChromaAux.get6SidedMetadataFromPlayerLook(ep));
		else
			te.setBlockMetadata(ChromaAux.get4SidedMetadataFromPlayerLook(ep));
		if (m.isSidePlaced()) {
			((SidePlacedTile)te).placeOnSide(side);
		}
		if (m == ChromaTiles.AURAPOINT) {
			((TileEntityAuraPoint)te).savePoint();
		}
		if (m == ChromaTiles.DIMENSIONCORE) {
			((TileEntityDimensionCore)te).prime(true);
		}
		if (m == ChromaTiles.CONSOLE) {
			((TileEntityCrystalConsole)te).placedDir = ReikaEntityHelper.getDirectionFromEntityLook(ep, false).getOpposite();
		}
		if (te instanceof NBTTile && is.stackTagCompound != null) {
			((NBTTile)te).setDataFromItemStackTag(is);
		}
		if (m.isRotateableRepeater()) {
			if (!ep.isSneaking()) {
				((TileEntityCrystalRepeater)te).findFirstValidSide();
			}
		}
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		ChromaTiles m = ChromaTiles.TEList[is.getItemDamage()];
		if (m == ChromaTiles.HEATLILY) {
			MovingObjectPosition mov = this.getMovingObjectPositionFromPlayer(world, ep, true);
			if (mov != null && mov.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				int x = mov.blockX;
				int y = mov.blockY;
				int z = mov.blockZ;

				if (!world.canMineBlock(ep, x, y, z))
					return is;
				if (!ep.canPlayerEdit(x, y, z, mov.sideHit, is))
					return is;

				if (world.getBlock(x, y, z).getMaterial() == Material.water && world.getBlockMetadata(x, y, z) == 0) {
					if (world.isAirBlock(x, y+1, z)) {
						world.setBlock(x, y + 1, z, m.getBlock(), m.getBlockMetadata(), 3);
						if (!ep.capabilities.isCreativeMode)
							--is.stackSize;
					}
				}
			}
		}

		return is;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles c = ChromaTiles.TEList[i];
			if (c.isAvailableInCreativeInventory()) {
				ItemStack item = new ItemStack(par1, 1, i);
				if (c == ChromaTiles.METEOR) {
					for (int k = 0; k < 3; k++) {
						ItemStack item2 = item.copy();
						item2.stackTagCompound = new NBTTagCompound();
						item2.stackTagCompound.setInteger("tier", k);
						par3List.add(item2);
					}
				}
				else if (c == ChromaTiles.DIMENSIONCORE) {
					for (int k = 0; k < 16; k++) {
						ItemStack item2 = item.copy();
						item2.stackTagCompound = new NBTTagCompound();
						item2.stackTagCompound.setInteger("color", k);
						par3List.add(item2);
					}
				}
				else if (c.isRepeater() && c != ChromaTiles.WEAKREPEATER) {
					par3List.add(item);
					ItemStack item2 = item.copy();
					item2.stackTagCompound = new NBTTagCompound();
					item2.stackTagCompound.setBoolean("boosted", true);
					par3List.add(item2);
				}
				else {
					par3List.add(item);
				}
			}
		}
	}

	protected boolean checkValidBounds(ItemStack is, EntityPlayer ep, World world, int x, int y, int z) {
		return y > 0 && y < world.provider.getHeight()-1;
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public final String getUnlocalizedName(ItemStack is)
	{
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + String.valueOf(d);
	}

	@Override
	public final void registerIcons(IIconRegister ico) {}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		ChromaTiles r = ChromaTiles.TEList[is.getItemDamage()];
		if (r == ChromaTiles.GUARDIAN) {
			li.add(String.format("Protects a radius-%d area", TileEntityGuardianStone.RANGE));
		}
		if (r == ChromaTiles.ADJACENCY) {
			li.add(EnumChatFormatting.GOLD+"This item is deprecated! Craft it into the new version!");
		}
		if (r == ChromaTiles.ASPECTJAR && is.stackTagCompound != null && ModList.THAUMCRAFT.isLoaded()) {
			li.addAll(TileEntityAspectJar.parseNBT(is.stackTagCompound));
		}
		if (r.isRepeater() && is.stackTagCompound != null && is.stackTagCompound.getBoolean("boosted")) {
			li.add(EnumChatFormatting.GOLD+"Turbocharged");
		}
		if (r == ChromaTiles.DIMENSIONCORE && is.stackTagCompound != null) {
			CrystalElement e = CrystalElement.elements[is.stackTagCompound.getInteger("color")];
			li.add("Color: "+e.displayName);
		}
		if (is.stackTagCompound != null && is.stackTagCompound.hasKey("energy") && r.isLumenTile()) {
			ElementTagCompound tag = ElementTagCompound.createFromNBT(is.stackTagCompound.getCompoundTag("energy"));
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			for (int i = 0; i < 16; i++) {
				CrystalElement e = CrystalElement.elements[i];
				int amt = tag.getValue(e);
				String val = String.format("%d%s", Math.round(ReikaMathLibrary.getThousandBase(amt)), ReikaEngLibrary.getSIPrefix(amt));
				String s = e.getChatColorString()+val+EnumChatFormatting.RESET.toString();
				sb.append(s);
				if (i < 15) {
					sb.append("/");
				}
			}
			sb.append("}");
			li.add(sb.toString());
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		ChromaItems ir = ChromaItems.getEntry(is);
		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
	}

	@Override
	@ModDependent(ModList.TFC)
	public EnumSize getSize(ItemStack is) {
		return EnumSize.LARGE;
	}

	@Override
	@ModDependent(ModList.TFC)
	public EnumWeight getWeight(ItemStack is) {
		return EnumWeight.HEAVY;
	}

	@Override
	@ModDependent(ModList.TFC)
	public EnumItemReach getReach(ItemStack is) {
		return EnumItemReach.MEDIUM;
	}

	@Override
	@ModDependent(ModList.TFC)
	public boolean canStack() {
		return true;
	}


}
