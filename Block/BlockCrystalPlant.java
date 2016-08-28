/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant.Modifier;
import Reika.DragonAPI.Instantiable.ParticleController.SpiralMotionController;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrystalPlant extends Block {

	private IIcon colorIcon;
	private IIcon colorIcon2;
	private IIcon fastIcon;
	private IIcon fastIcon2;
	private IIcon center;

	private static final Random rand = new Random();

	public BlockCrystalPlant(Material mat) {
		super(mat);
		this.setTickRandomly(true);
		this.setLightOpacity(0);
		this.setHardness(0);
		this.setResistance(1F);
		this.setStepSound(soundTypeGrass);
		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
		this.setBlockBounds(0, 0, 0, 0.9375F, 0.9375F, 0.9375F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z) && super.canPlaceBlockAt(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block par5)
	{
		super.onNeighborBlockChange(world, x, y, z, par5);
		if (!this.canBlockStay(world, x, y, z)) {
			this.die(world, x, y, z);
		}
	}

	private void die(World world, int x, int y, int z) {
		this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		world.setBlockToAir(x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
		if (te.isPure()) {
			double rad = ReikaRandomHelper.getRandomPlusMinus(0.375, 0.125);
			double a = Math.toRadians(r.nextDouble()*360);
			double px = x+0.5+rad*Math.cos(a);
			double pz = z+0.5+rad*Math.sin(a);
			EntityBlurFX fx = new EntityBlurFX(world, px, y+r.nextDouble(), pz);
			fx.setIcon(ChromaIcons.BIGFLARE).setLife(5);
			fx.setColor(ReikaColorAPI.mixColors(te.getColor().getColor(), 0xffffff, 0.8F));
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);

			if (te.canHarvest()) {
				for (int i = 0; i < 4; i++) {
					float s = (float)ReikaRandomHelper.getRandomBetween(0.25, 1);
					fx = new EntityBlurFX(world, x+0.5, y+1.03125+0.1875*r.nextDouble(), z+0.5).setLife(20).setRapidExpand().setScale(s);
					fx.setColor(ReikaColorAPI.mixColors(te.getColor().getColor(), 0xffffff, 0.5F)).setIcon(ChromaIcons.FADE_CLOUD);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}

			if (te.is(Modifier.BOOSTED)) {
				px = x+r.nextDouble();
				pz = z+r.nextDouble();
				fx = new EntityBlurFX(world, px, y+r.nextDouble(), pz).setColor(te.getColor().getColor());
				fx.setGravity(-0.03125F);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				float fs = (float)ReikaRandomHelper.getRandomBetween(0.125, 0.75);
				EntityFX fx2 = new EntityBlurFX(world, px, fx.posY, pz).setColor(0xffffff).lockTo(fx).setScale(fs);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
			}
			if (te.is(Modifier.PRIMAL)) {
				rad = 0.25;
				a = System.currentTimeMillis()/40D;
				px = x+0.5;
				pz = z+0.5;
				double v = 0;//0.03125;
				double vx = v*Math.cos(a);
				double vz = v*Math.sin(a);
				fx = new EntityBlurFX(world, px, y+0.9375, pz, vx, 0, vz).setColor(te.getColor().getColor()).setRapidExpand();
				SpiralMotionController m = new SpiralMotionController(x+0.5, z+0.5, 15, 0.015, rad, 0, a);
				fx.setMotionController(m).setPositionController(m).setLife(100);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				float fs = (float)ReikaRandomHelper.getRandomBetween(0.125, 0.75);
				EntityFX fx2 = new EntityBlurFX(world, px, fx.posY, pz).setColor(0xffffff).lockTo(fx).setScale(fs).setRapidExpand().setLife(100);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
			}
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		if (this.canBlockStay(world, x, y, z)) {
			if (random.nextInt(12) == 0) {
				TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
				te.grow();
			}
		}
		else {
			this.die(world, x, y, z);
		}
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		TileEntityCrystalPlant te = (TileEntityCrystalPlant)iba.getTileEntity(x, y, z);
		return te.emitsLight() ? te.isPure() ? 15 : 12 : 0;
	}

	@Override
	public int getRenderType() {
		return 1; //cross tex, render the "plant" part here
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityCrystalPlant();
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
	public int getRenderColor(int meta) {
		return super.getRenderColor(meta);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return ChromaItems.SEED.getItemInstance();
	}

	@Override
	public int damageDropped(int meta) {
		return meta%16+16;
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {/*
		int l = 0;
		int i1 = 0;
		int j1 = 0;

		for (int k1 = -1; k1 <= 1; ++k1)
		{
			for (int l1 = -1; l1 <= 1; ++l1)
			{
				int i2 = iba.getBiomeGenForCoords(x + l1, z + k1).getBiomeGrassColor();
				l += (i2 & 16711680) >> 16;
			i1 += (i2 & 65280) >> 8;
			j1 += i2 & 255;
			}
		}

		return (l / 9 & 255) << 16 | (i1 / 9 & 255) << 8 | j1 / 9 & 255;*/
		return super.colorMultiplier(iba, x, y, z);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		//return Minecraft.getMinecraft().gameSettings.fancyGraphics ? colorIcon : fastIcon;
		return fastIcon;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int s) {
		TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
		return te.is(Modifier.PRIMAL) ? fastIcon2 : fastIcon;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
		if (te.canHarvest()) {
			te.harvest(true);
		}
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		fastIcon = ico.registerIcon("chromaticraft:plant");
		fastIcon2 = ico.registerIcon("chromaticraft:plant_primal");
		colorIcon = ico.registerIcon("chromaticraft:plant_gray");
		colorIcon2 = ico.registerIcon("chromaticraft:plant_gray_primal");
		center = ico.registerIcon("chromaticraft:crystal/bloom");
	}

	public IIcon getBulbIcon(CrystalElement color) {
		return center;//bulb[color.ordinal()];
	}
	/*
	@Override
	public boolean isReadyToHarvest(World world, int x, int y, int z) {
		if (world.getBlock(x, y-1, z) == Blocks.grass)
			;//return false;
		TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
		return te.canHarvest();
	}

	@Override
	public void setPostHarvest(World world, int x, int y, int z) {
		TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
		te.harvest();
	}

	@Override
	public ArrayList<ItemStack> getHarvestProducts(World world, int x, int y, int z) {
		if (ChromaOptions.CRYSTALFARM.getState() && ReikaRandomHelper.doWithChance(0.01)) {
			ArrayList li = new ArrayList();
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(world.getBlockMetadata(x, y, z));
			li.add(shard);
			return li;
		}
		return null;
	}

	@Override
	public float getHarvestingSpeed() {
		return 0.33F;
	}*/

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer ep)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return new ItemStack(ChromaBlocks.PLANT.getBlockInstance(), 1, meta);
	}

}
