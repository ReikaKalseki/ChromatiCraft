package Reika.ChromatiCraft.Block.Worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ProgressionTrigger;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockChromaMud extends Block implements ProgressionTrigger {

	public BlockChromaMud(Material m) {
		super(m);

		this.setCreativeTab(ChromatiCraft.tabChromaGen);

		blockHardness = Blocks.farmland.blockHardness;
		blockResistance = Blocks.farmland.blockResistance;
		//stepSound = Blocks.farmland.stepSound;
		slipperiness = Blocks.soul_sand.slipperiness;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
		this.setLightOpacity(4);

		this.setStepSound(new SoundType("", 1, 0.5F) {
			@Override
			public String getBreakSound() {
				return Blocks.dirt.stepSound.getBreakSound();
			}

			@Override
			public String getStepResourcePath() {
				return "mob.slime.attack";
			}

			@Override
			public String func_150496_b() { //place sound
				return Blocks.dirt.stepSound.func_150496_b();//"mob.slime.small";
			}
		});

		this.setTickRandomly(true);
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
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:mud");
	}

	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity e, float dist) {

	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune)  {
		return Blocks.dirt.getItemDropped(0, rand, fortune);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return Item.getItemFromBlock(Blocks.dirt);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		//grow chroma plants
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		e.motionX *= 0.67D;
		e.motionZ *= 0.67D;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return ReikaAABBHelper.getBlockAABB(x, y, z).contract(0, 0.0625, 0).offset(0, -0.0625, 0);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		super.onNeighborBlockChange(world, x, y, z, b);
		if (world.getBlock(x, y+1, z).getMaterial().isSolid())
			world.setBlock(x, y, z, Blocks.dirt);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s == 1 ? blockIcon : Blocks.dirt.blockIcon;
	}

	@Override
	public ProgressStage[] getTriggers(EntityPlayer ep, World world, int x, int y, int z) {
		return new ProgressStage[] {ProgressStage.MUDHINT};
	}

}
