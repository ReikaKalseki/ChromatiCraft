/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;

public class BlockStructureDataStorage extends BlockDimensionStructureTile {

	private final IIcon[] icons = new IIcon[2];

	public BlockStructureDataStorage(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityStructureDataStorage();
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/dimdata");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/dimdata_side");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return /*s <= 1 ? icons[0] : */icons[1];
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (!world.isRemote) {
			((TileEntityStructureDataStorage)world.getTileEntity(x, y, z)).onRightClick(ep, s);
		}
		return true;
	}

	public static class TileEntityStructureDataStorage extends TileEntity {

		private StructureData data;
		private HashMap<String, Object> extraData;

		public void loadData(DimensionStructureGenerator gen, HashMap<String, Object> map) {
			data = gen.createDataStorage();
			if (data != null)
				data.load(map);
			extraData = map;
		}

		protected void onRightClick(EntityPlayer ep, int s) {
			if (data != null)
				data.onInteract(worldObj, xCoord, yCoord, zCoord, ep, s, extraData);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			//extraData = new HashMap();
			NBTTagCompound dat = NBT.getCompoundTag("extra");
			/*
			for (Object o : dat.func_150296_c()) {
				String s = (String)o;
				extraData.put(s, ReikaNBTHelper.getValue(dat.getTag(s)));
			}
			 */
			extraData = dat != null && !dat.hasNoTags() ? (HashMap<String, Object>)ReikaNBTHelper.getValue(dat) : null;

			if (NBT.hasKey("uid")) {
				UUID uid = UUID.fromString(NBT.getString("uid"));
				this.loadData(DimensionStructureGenerator.getGeneratorByID(uid), extraData);
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (data != null)
				NBT.setString("uid", data.getUUID().toString());

			if (extraData != null && !extraData.isEmpty()) {
				/*
				NBTTagCompound dat = new NBTTagCompound();
				for (String s : extraData.keySet()) {
					dat.setTag(s, ReikaNBTHelper.getTagForObject(extraData.get(s)));
				}
				NBT.setTag("extra", dat);
				 */
				NBT.setTag("extra", ReikaNBTHelper.getTagForObject(extraData));
			}
		}

	}

}
