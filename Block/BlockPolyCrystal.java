package Reika.ChromatiCraft.Block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Render.PolyCrystal;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockPolyCrystal extends Block {

	public BlockPolyCrystal(Material mat) {
		super(mat);
		this.setHardness(2);
		this.setResistance(6000);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 0;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TilePolyCrystal();
	}

	@Override
	public int getRenderType() {
		return -1;
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
	public void breakBlock(World world, int x, int y, int z, Block old, int oldmeta) {
		super.breakBlock(world, x, y, z, old, oldmeta);

		BlockArray blocks = new BlockArray();
		blocks.recursiveAddWithBounds(world, x, y, z, this, x-32, y-32, z-32, x+32, y+32, z+32);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			blocks.recursiveAddWithBounds(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, this, x-32, y-32, z-32, x+32, y+32, z+32);
		}
		PolyCrystal crystal = new PolyCrystal();
		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			int dx = c.xCoord;
			int dy = c.yCoord;
			int dz = c.zCoord;
			crystal.addBlock(dx, dy, dz);
			TilePolyCrystal te = (TilePolyCrystal)world.getTileEntity(dx, dy, dz);
			te.crystal = crystal;
			te.doRender = i == 0;
			world.markBlockForUpdate(dx, dy, dz);
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		BlockArray blocks = new BlockArray();
		blocks.recursiveAddWithBounds(world, x, y, z, this, x-32, y-32, z-32, x+32, y+32, z+32);
		PolyCrystal crystal = new PolyCrystal();
		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			int dx = c.xCoord;
			int dy = c.yCoord;
			int dz = c.zCoord;
			crystal.addBlock(dx, dy, dz);
			TilePolyCrystal te = (TilePolyCrystal)world.getTileEntity(dx, dy, dz);
			te.crystal = crystal;
			te.doRender = i == 0;
			world.markBlockForUpdate(dx, dy, dz);
		}
	}

	public static class TilePolyCrystal extends TileEntity {

		private PolyCrystal crystal = new PolyCrystal();
		private boolean doRender = false;

		@Override
		public boolean canUpdate() {
			return false;
		}

		@SideOnly(Side.CLIENT)
		public void renderCrystal(Tessellator v5, float ptick) {
			if (doRender)
				crystal.render(v5, ptick);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			crystal.writeToNBT("blocks", NBT);
			NBT.setBoolean("render", doRender);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			crystal.readFromNBT("blocks", NBT);
			doRender = NBT.getBoolean("render");
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return pass == 1;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public AxisAlignedBB getRenderBoundingBox() {
			return crystal.asAABB();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public double getMaxRenderDistanceSquared()  {
			return super.getMaxRenderDistanceSquared()*4;
		}

	}

}
