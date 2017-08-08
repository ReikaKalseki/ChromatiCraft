/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Bridge;

import java.util.HashSet;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.World.Dimension.Structure.BridgeGenerator;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;


public class BlockDynamicBridge extends BlockDimensionStructureTile {

	private final IIcon[] icons = new IIcon[2];

	public BlockDynamicBridge(Material mat) {
		super(mat);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 1 ? ReikaAABBHelper.getBlockAABB(x, y, z) : null;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/bridge_off");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/bridge_on");
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int s) {
		return iba.getBlock(x, y, z) != this;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileDynamicBridge();
	}

	public static class TileDynamicBridge extends StructureBlockTile<BridgeGenerator> {

		private final HashSet<UUID> keySet = new HashSet();
		private final HashSet<UUID> disallowSet = new HashSet();

		public void checkState() {
			BridgeGenerator gen = this.getGenerator();
			boolean flag = true;
			if (gen != null) {
				for (UUID uid : keySet) {
					if (!gen.isKeyActive(uid)) {
						flag = false;
						break;
					}
				}
				for (UUID uid : disallowSet) {
					if (gen.isKeyActive(uid)) {
						flag = false;
						break;
					}
				}
			}
			this.setActive(flag);
		}

		public void setActive(boolean flag) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, flag ? 1 : 0, 3);
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.BRIDGE;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBTTagList li = new NBTTagList();
			for (UUID uid : keySet) {
				li.appendTag(new NBTTagString(uid.toString()));
			}
			NBT.setTag("keys", li);

			li = new NBTTagList();
			for (UUID uid : disallowSet) {
				li.appendTag(new NBTTagString(uid.toString()));
			}
			NBT.setTag("disallow", li);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			keySet.clear();
			disallowSet.clear();
			NBTTagList li = NBT.getTagList("keys", NBTTypes.STRING.ID);
			for (Object o : li.tagList) {
				NBTTagString b = (NBTTagString)o;
				String s = b.func_150285_a_();
				UUID uid = UUID.fromString(s);
				keySet.add(uid);
			}

			li = NBT.getTagList("disallow", NBTTypes.STRING.ID);
			for (Object o : li.tagList) {
				NBTTagString b = (NBTTagString)o;
				String s = b.func_150285_a_();
				UUID uid = UUID.fromString(s);
				disallowSet.add(uid);
			}
		}

	}

}
