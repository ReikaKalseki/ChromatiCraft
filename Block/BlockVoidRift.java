package Reika.ChromatiCraft.Block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class BlockVoidRift extends Block {

	public BlockVoidRift(Material mat) {
		super(mat);
		this.setResistance(900000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.vriftRender;
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityVoidRift();
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {

	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	/*
	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		VoidRiftRenderer.renderPass = pass;
		return true;
	}
	 */
	public static class TileEntityVoidRift extends TileEntity {

		public static final int HEIGHT = 16;

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public void updateEntity() {

		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return pass <= 1;
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+HEIGHT, zCoord+1);
		}

		public CrystalElement getColor() {
			return CrystalElement.elements[this.getBlockMetadata()];
		}

	}

}
