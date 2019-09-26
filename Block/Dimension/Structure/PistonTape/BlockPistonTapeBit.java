package Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Interfaces.LaserPulseEffect;
import Reika.ChromatiCraft.Base.BlockDimensionStructure;
import Reika.ChromatiCraft.Entity.EntityLaserPulse;
import Reika.DragonAPI.Instantiable.RGBColorData;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class BlockPistonTapeBit extends BlockDimensionStructure implements SemiUnbreakable, LaserPulseEffect {

	private final IIcon[] icons = new IIcon[2];

	public BlockPistonTapeBit(Material mat) {
		super(mat);
		this.setHardness(0);
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
	public IIcon getIcon(int s, int meta) {
		return meta < 6 ? icons[meta%2] : icons[1];
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public int getLightOpacity(IBlockAccess iba, int x, int y, int z) {
		return iba.getBlockMetadata(x, y, z)%2 == 1 ? 127 : 0;
	}

	@Override
	public int getMobilityFlag() {
		return 0;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/bit_zero");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/bit_one");
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return getColor(iba, x, y, z).getRenderColor();
	}

	@Override
	public boolean onRightClicked(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		int meta = world.getBlockMetadata(x, y, z);
		if (ep.isSneaking()) {
			meta = (meta+2) | 1;
			if (meta >= 7)
				meta = 1;
		}
		else {
			meta = ReikaMathLibrary.toggleBit(meta, 0);
		}
		world.setBlockMetadataWithNotify(x, y, z, meta, 3);
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.click", 1, meta%2 == 1 ? 0.875F : 0.75F);
		return true;
	}

	private static Entity currentCollisionEntity;

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return currentCollisionEntity instanceof EntityPlayer ? super.getCollisionBoundingBoxFromPool(world, x, y, z): null;
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List li, Entity e) {
		currentCollisionEntity = e;
		super.addCollisionBoxesToList(world, x, y, z, mask, li, e);
		currentCollisionEntity = null;
	}

	public boolean onImpact(World world, int x, int y, int z, EntityLaserPulse e) {
		e.color.intersect(getColor(world, x, y, z));
		return false;//e.color.isBlack();
	}

	public static RGBColorData getColor(IBlockAccess iba, int x, int y, int z) {
		int meta = iba.getBlockMetadata(x, y, z);
		if (meta < 6 && meta%2 == 0)
			return new RGBColorData(true, true, true);
		boolean r = true;
		boolean g = true;
		boolean b = true;
		switch(meta/2) {
			case 0:
				b = false;
				break;
			case 1:
				r = false;
				break;
			case 2:
				g = false;
				break;
			default:
				return RGBColorData.black();
		}
		return new RGBColorData(r, g, b);
	}

	public static int getMetaFor(RGBColorData c, boolean active) {
		if (c == null)
			return 0;
		if (c.isWhite())
			return 0;
		if (c.isPrimary() || c.isBlack())
			return 8; //error
		int base = 0;
		if (c.red && c.green) {
			base = 0;
		}
		else if (c.green && c.blue) {
			base = 2;
		}
		else if (c.red && c.blue) {
			base = 4;
		}
		return active ? base+1 : base;
	}

	@Override
	public boolean isUnbreakable(World world, int x, int y, int z, int meta) {
		return true;
	}

}
