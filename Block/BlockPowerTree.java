package Reika.ChromatiCraft.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityPowerTree;
import Reika.DragonAPI.ASM.APIStripper.Strippable;

@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockPowerTree extends Block implements IWailaDataProvider {

	private static final Random rand = new Random();

	public BlockPowerTree(Material mat) {
		super(mat);
		this.setHardness(20);
		this.setResistance(6000);
		this.setLightLevel(1);
		stepSound = soundTypeGlass;
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	public TileEntity createTileEntity(World world, int x, int y, int z) {
		return new TileEntityPowerTreeAux();
	}

	private TileEntityPowerTree getTile(World world, int x, int y, int z) {
		ForgeDirection dir = TileEntityPowerTree.getDirection(this.getColor(world, x, y, z));
		for (int i = 1; i < 32; i++) {
			int dx = x+dir.offsetX*i;
			int dy = y+dir.offsetY*i;
			int dz = z+dir.offsetZ*i;
			ChromaTiles c = ChromaTiles.getTile(world, dx, dy, dz);
			if (c == ChromaTiles.POWERTREE) {
				return (TileEntityPowerTree)world.getTileEntity(dx, dy, dz);
			}
			else if (world.getBlock(dx, dy, dz) != this)
				return null;
		}
		return null;
	}

	public CrystalElement getColor(IBlockAccess world, int x, int y, int z) {
		return CrystalElement.elements[world.getBlockMetadata(x, y, z)];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/powertree5");
	}

	@Override
	public int getRenderColor(int meta) {
		return CrystalElement.elements[meta].getColor();
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return this.getColor(iba, x, y, z).getColor();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block old, int oldmeta) {

		super.breakBlock(world, x, y, z, old, oldmeta);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public int getRenderType() {
		return 0;
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
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		int n = (1+fortune/2)*rand.nextInt(1+rand.nextInt(4)+2*fortune);
		CrystalElement e = CrystalElement.elements[meta];
		for (int i = 0; i < n; i++) {
			ItemStack is = rand.nextInt(3) == 0 ? ChromaStacks.getChargedShard(e) : ChromaStacks.getShard(e);
			li.add(is);
		}
		return li;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return new ItemStack(this, 1, meta);
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	public List<String> getWailaTail(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	public static class TileEntityPowerTreeAux extends TileEntity {

		private int growth = 0;

	}

}
