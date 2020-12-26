/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Worldgen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenHills;
import net.minecraft.world.biome.BiomeGenSwamp;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LoadRegistry;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PlantDropManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaISBRH;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCCFloatingSeedsFX;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Effects.EntityFloatingSeedsFX;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockDecoFlower extends Block implements IShearable, LoadRegistry {

	private final IIcon[] icons = new IIcon[Flowers.list.length];

	public BlockDecoFlower() {
		super(Material.plants);
		stepSound = soundTypeGrass;
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
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
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		return world.rand.nextInt(Flowers.list[meta].getDropChance()) == 0 ? ReikaJavaLibrary.makeListFrom(Flowers.list[meta].getDrop()) : new ArrayList();
	}

	@Override
	public int getRenderType() {
		return ChromaISBRH.flower.getRenderID();
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < icons.length; i++) {
			icons[i] = ico.registerIcon("chromaticraft:plant/flower_"+i);
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta];
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		Flowers f = Flowers.list[iba.getBlockMetadata(x, y, z)];
		return f.isBiomeColored() ? iba.getBiomeGenForCoords(x, z).getBiomeGrassColor(x, y, z) : 0xffffff;
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		Flowers f = Flowers.list[iba.getBlockMetadata(x, y, z)];
		return f.getLightValue(iba, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		super.onNeighborBlockChange(world, x, y, z, b);
		this.checkAndDropBlock(world, x, y, z);
	}

	private boolean checkAndDropBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return Flowers.list[world.getBlockMetadata(x, y, z)].canPlantAt(world, x, y, z);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z)/* && this.canBlockStay(world, x, y, z)*/;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		Flowers f = Flowers.list[world.getBlockMetadata(x, y, z)];
		switch(f) {
			case ENDERFLOWER:
				this.setBlockBounds(0, 0, 0, 1, 0.875F, 1);
				break;
			case RESOCLOVER:
				this.setBlockBounds(0, 0, 0, 1, 0.0625F, 1);
				break;
			case SANOBLOOM:
				this.setBlockBounds(0, 0, 0, 1, 0.5F, 1);
			case GLOWDAISY:
				this.setBlockBounds(0, 0, 0, 1, 0.625F, 1);
				break;
			case FLOWIVY:
				float nx = 1-0.0625F;
				float nz = 1-0.0625F;
				float px = 0.0625F;
				float pz = 0.0625F;
				if (isIvySolid(world, x+1, y, z, ForgeDirection.WEST)) {
					px = 1;
					nz = 0;
					pz = 1;
				}
				if (isIvySolid(world, x-1, y, z, ForgeDirection.EAST)) {
					nx = 0;
					nz = 0;
					pz = 1;
				}
				if (isIvySolid(world, x, y, z+1, ForgeDirection.NORTH)) {
					pz = 1;
					nx = 0;
					px = 1;
				}
				if (isIvySolid(world, x, y, z-1, ForgeDirection.SOUTH)) {
					nz = 0;
					nx = 0;
					px = 1;
				}

				this.setBlockBounds(nx, 0, nz, px, 1, pz);
				break;
			case GLOWROOT:
				this.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
				break;
			default:
				this.setBlockBounds(0, 0, 0, 1, 1, 1);
				break;
		}
	}

	private static boolean isIvySolid(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
		Block b = world.getBlock(x, y, z);
		return (b.getMaterial() == Material.rock || ReikaBlockHelper.isFacade(b)) && b.isSideSolid(world, x, y, z, dir);
	}

	@Override
	public int getRenderColor(int meta) {
		Flowers f = Flowers.list[meta];
		return f.isBiomeColored() ? ColorizerGrass.getGrassColor(0.75F, 1) : 0xffffff;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (!this.checkAndDropBlock(world, x, y, z)) {
			int meta = world.getBlockMetadata(x, y, z);
			Flowers f = Flowers.list[meta];
			Coordinate c = f.grow(world, x, y, z, this, rand);
			if (c != null) {
				c.setBlock(world, this, meta);
				ReikaSoundHelper.playBreakSound(world, x, y, z, this, 1, 1);
				ReikaSoundHelper.playBreakSound(world, c.xCoord, c.yCoord, c.zCoord, this, 1, 1);
				ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), world, x, y, z, 32, Block.getIdFromBlock(this), meta);
				ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), world, c.xCoord, c.yCoord, c.zCoord, 64, Block.getIdFromBlock(this), meta);
			}
			else {
				f.tick(world, x, y, z, rand);
			}
		}
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		Flowers.list[world.getBlockMetadata(x, y, z)].doParticles(world, x, y, z, r);
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		int meta = world.getBlockMetadata(x, y, z);
		Flowers f = Flowers.list[meta];
		ItemStack is = new ItemStack(this, 1, meta);
		if (world instanceof World) { //Why is this IBA? This is NOT a renderer!
			if (f == Flowers.VOIDREED) {
				int dy = y+1;
				while (world.getBlock(x, dy, z) == this && world.getBlockMetadata(x, dy, z) == meta) {
					dy++;
				}
				for (int i = dy-1; i > y; i--) {
					((World)world).setBlock(x, i, z, Blocks.air);
					ReikaItemHelper.dropItem((World)world, x+0.5, i+0.5, z+0.5, is);
				}
			}
		}
		return ReikaJavaLibrary.makeListFrom(is.copy());
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (is != null && is.getItem() instanceof ItemShears) {
			return;
		}
		super.harvestBlock(world, ep, x, y, z, meta);
	}

	@Override
	public int damageDropped(int dmg) {
		return dmg;
	}

	@Override
	public void onLoad() {
		for (int i = 0; i < Flowers.list.length; i++) {
			Flowers.list[i].registerDrops(this);
		}
	}

	public static enum Flowers {
		ENDERFLOWER(),
		LUMALILY(),
		RESOCLOVER(),
		SANOBLOOM(),
		VOIDREED(),
		FLOWIVY(),
		GLOWDAISY(),
		GLOWROOT();

		public static final Flowers[] list = values();

		//private final ItemStack drop;

		private Flowers(/*ItemStack is*/) {
			//drop = is;
		}

		public int getLightValue(IBlockAccess iba, int x, int y, int z) {
			switch(this) {
				case GLOWDAISY:
					return ReikaWorldHelper.isAdjacentToCrop(iba, x, y, z) ? 12 : 10;
				case GLOWROOT:
					return 6;
				default:
					return 0;
			}
		}

		private void registerDrops(BlockDecoFlower b) {
			PlantDropManager.instance.registerDrops(b, this.ordinal(), this.getDrop());
		}

		public boolean canGenerateIn(BiomeGenBase b) {
			switch(this) {
				case ENDERFLOWER:
				case RESOCLOVER:
					return b == ChromatiCraft.enderforest;
				case LUMALILY:
					return ReikaBiomeHelper.isSnowBiome(b) && !(b instanceof BiomeGenHills) && b.topBlock == Blocks.grass;
				case SANOBLOOM:
					return BiomeDictionary.isBiomeOfType(b, Type.JUNGLE);
				case VOIDREED:
					return b.biomeID == ThaumIDHandler.Biomes.EERIE.getID() || b instanceof BiomeGenSwamp || b.getClass().getName().contains("BiomeGenBOPSwamp") || b.getClass().getName().contains("BiomeGenLushSwamp");
				case FLOWIVY:
					return b instanceof BiomeGenHills || (b.rootHeight >= 1 && b.topBlock == Blocks.grass && !BiomeGlowingCliffs.isGlowingCliffs(b)/* && ReikaBiomeHelper.getBiomeTemp(world, b) < 40*/);
				case GLOWDAISY:
				case GLOWROOT:
					return BiomeGlowingCliffs.isGlowingCliffs(b);
			}
			return false;
		}

		public boolean isBiomeColored() {
			/*
			switch(this) {
				case RESOCLOVER:
				case VOIDREED:
					return false;
				default:
					return true;
			}
			 */
			switch(this) {
				case FLOWIVY:
					return true;
				default:
					return false;
			}
		}

		public void tick(World world, int x, int y, int z, Random rand) {
			switch(this) {
				case ENDERFLOWER:
					break;
				case FLOWIVY:
					break;
				case GLOWDAISY:
					break;
				case GLOWROOT:
					if (rand.nextInt(4) == 0) {
						if (world.checkChunksExist(x-16, y, z-16, x+16, y, z+16)) {
							int meta = 0;
							switch(rand.nextInt(20)) {
								case 20:
									meta = 5;
									break;
								case 19:
								case 18:
									meta = 4;
									break;
								case 17:
								case 16:
								case 15:
									meta = 3;
									break;
								case 14:
								case 13:
								case 12:
									meta = 2;
									break;
								case 11:
								case 10:
								case 9:
									meta = 1;
									break;
							}
							ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, ChromaItems.FERTILITYSEED.getStackOfMetadata(meta));
						}
						break;
					}
				case LUMALILY:
					break;
				case RESOCLOVER:
					break;
				case SANOBLOOM:
					break;
				case VOIDREED:
					break;
			}
		}

		public Coordinate grow(World world, int x, int y, int z, Block b, Random rand) {
			boolean active = this.onActiveGrass(world, x, y, z);
			switch(this) {
				case ENDERFLOWER:
				case SANOBLOOM: {
					int n = active ? 8 : 32;
					if (rand.nextInt(n) > 0)
						return null;
					if (this == ENDERFLOWER && rand.nextInt(12) > 0)
						return null;
					if (!active) {
						for (int i = 2; i < 6; i++) {
							ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
							int dx = x+dir.offsetX;
							int dz = z+dir.offsetZ;
							if (this.matchAt(world, dx, y, dz))
								return null;
						}
					}
					int rx = ReikaRandomHelper.getRandomPlusMinus(x, 4);
					int rz = ReikaRandomHelper.getRandomPlusMinus(z, 4);
					int ry = y;
					int oy = y;
					while (world.getBlock(rx, ry-1, rz).isAir(world, rx, ry-1, rz) && ry > 0)
						ry--;
					if (Math.abs(oy-ry) <= 12 && world.getBlock(rx, ry, rz).isAir(world, rx, ry, rz) && this.canPlantAt(world, rx, ry, rz)) {
						return new Coordinate(rx, ry, rz);
					}
					return null;
				}
				case LUMALILY:
				case RESOCLOVER:
				case GLOWDAISY: {
					int n = active ? 5 : 20;
					if (this == LUMALILY)
						n *= 4;
					if (this == GLOWDAISY)
						n *= 12;
					int c = 0;
					if (!active) {
						for (int i = 2; i < 6; i++) {
							ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
							int dx = x+dir.offsetX;
							int dz = z+dir.offsetZ;
							if (this.matchAt(world, dx, y, dz))
								c++;
						}
						if (c >= 2)
							return null;
						else if (c == 1 && rand.nextBoolean())
							return null;
					}
					if (rand.nextInt(n) == 0) {
						ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[2+rand.nextInt(4)];
						int dx = x+dir.offsetX;
						int dz = z+dir.offsetZ;
						Block idb = world.getBlock(dx, y-1, dz);
						if (world.getBlock(dx, y, dz).isAir(world, dx, y, dz) && (idb == Blocks.grass || idb == Blocks.dirt)) {
							if (this == GLOWDAISY && world.getSavedLightValue(EnumSkyBlock.Sky, dx, y, dz) > 8)
								return null;
							//ReikaJavaLibrary.pConsole("Spreading "+this+" to "+new Coordinate(dx, y, dz));
							return new Coordinate(dx, y, dz);
						}
					}
					return null;
				}
				case FLOWIVY: {
					if (rand.nextInt(3) == 0) {
						ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)];
						if (dir == ForgeDirection.UP && rand.nextBoolean())
							dir = ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)];
						if (rand.nextInt(4) == 0)
							dir = ForgeDirection.DOWN;
						int dx = x+dir.offsetX;
						int dy = y+dir.offsetY;
						int dz = z+dir.offsetZ;
						if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz) && this.canPlantAt(world, dx, dy, dz)) {
							return new Coordinate(dx, dy, dz);
						}
					}
					return null;
				}
				case VOIDREED: {
					int n = this.isChromaPool(world, x, y, z) ? 20 : 40;
					if (active)
						n = n*3/4;
					if (rand.nextInt(n) == 0) {
						if (world.getBlock(x, y+1, z).isAir(world, x, y+1, z)) {
							int h = 1;
							int y2 = y-1;
							while (this.matchAt(world, x, y2, z)) {
								y2--;
								h++;
							}
							if (h < rand.nextInt(7)) { //slower as taller
								return new Coordinate(x, y+1, z);
							}
						}
					}
					return null;
				}
				case GLOWROOT: {
					if (rand.nextInt(36) == 0) {
						if (world.getBlock(x, y-1, z).isAir(world, x, y-1, z) && this.canPlantAt(world, x, y-1, z) && world.getBlock(x, y-2, z).isAir(world, x, y-2, z)) {
							return new Coordinate(x, y-1, z);
						}
					}
					return null;
				}
			}
			return null;
		}

		public boolean canPlantAt(World world, int x, int y, int z) {
			//ReikaJavaLibrary.pConsole("Testing plantability of "+this+" at "+new Coordinate(x, y, z));
			switch(this) {
				case FLOWIVY: {
					for (int i = 2; i < 6; i++) {
						ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
						Block b = world.getBlock(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
						if (b.isOpaqueCube() && b.getMaterial() == Material.rock)
							return true;
						if (isIvySolid(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, dir.getOpposite())) {
							return true;
						}
					}
					//return this.matchAt(world, x, y+1, z) || (ReikaPlantHelper.VINES.canPlantAt(world, x, y, z)/* && world.getBlock(x, y-1, z).isAir(world, x, y-1, z)*/);//world.getBlock(x, y+1, z).getMaterial().isSolid();
					return false;
				}
				case VOIDREED:
					return this.matchAt(world, x, y-1, z) || this.isChromaPool(world, x, y, z) || ReikaPlantHelper.SUGARCANE.canPlantAt(world, x, y, z);
				case ENDERFLOWER:
				case LUMALILY:
				case RESOCLOVER:
				case GLOWDAISY: {
					Block b = world.getBlock(x, y-1, z);
					return b == Blocks.dirt || b == Blocks.grass;
				}
				case SANOBLOOM:
					return world.getBlock(x, y-1, z) == Blocks.leaves && world.getBlockMetadata(x, y-1, z)%4 == 3;
				case GLOWROOT: {
					return (this.matchAt(world, x, y+1, z) || ReikaBlockHelper.isNaturalStone(world, x, y+1, z) || ReikaBlockHelper.isDirtType(world.getBlock(x, y+1, z), world.getBlockMetadata(x, y+1, z))) && !this.isRootTooLong(world, x, y, z);
				}
			}
			return false;
		}

		private boolean isRootTooLong(World world, int x, int y, int z) {
			while (this.matchAt(world, x, y-1, z)) {
				y--;
			}
			int l = 1;
			int dy = y;
			while (this.matchAt(world, x, dy+1, z)) {
				dy++;
				l++;
			}
			if (l < 4)
				return false;
			dy = y;
			int sp = l;
			while (world.getBlock(x, dy-1, z).isAir(world, x, dy-1, z)) {
				dy--;
				sp++;
			}
			//ReikaJavaLibrary.pConsole(y+">"+l+">"+sp+">"+sp*0.33, Side.SERVER);
			return l > sp*0.33;
		}

		private boolean isChromaPool(World world, int x, int y, int z) {
			Block idbelow = world.getBlock(x, y-1, z);
			int metabelow = world.getBlockMetadata(x, y-1, z);
			Material matbelow = ReikaWorldHelper.getMaterial(world, x, y-1, z);
			if (idbelow != Blocks.sand && !ReikaBlockHelper.isDirtType(idbelow, metabelow))
				return false;
			ForgeDirection liq = ReikaWorldHelper.checkForAdjBlock(world, x, y-1, z, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			return liq != null && liq.offsetY == 0;
		}

		private boolean onActiveGrass(World world, int x, int y, int z) {
			while (world.getBlock(x, y, z) == ChromaBlocks.DECOFLOWER.getBlockInstance())
				y--;
			if (!this.isValidGrass(world, x, y, z))
				return false;
			Block b = world.getBlock(x, y-1, z);
			return b instanceof IFluidBlock && ((IFluidBlock)b).getFluid() == FluidRegistry.getFluid("ender");
		}

		private boolean isValidGrass(World world, int x, int y, int z) {
			Block b = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			return (b == Blocks.grass && meta == 0) || (ReikaTreeHelper.JUNGLE.isTreeLeaf(b, meta) && ReikaTreeHelper.isNaturalLeaf(world, x, y, z));
		}

		private boolean matchAt(World world, int x, int y, int z) {
			return world.getBlock(x, y, z) == ChromaBlocks.DECOFLOWER.getBlockInstance() && world.getBlockMetadata(x, y, z) == this.ordinal();
		}

		@SideOnly(Side.CLIENT)
		public void doParticles(World world, int x, int y, int z, Random r) {
			switch(this) {
				case ENDERFLOWER: {
					double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 1.25);
					double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 1.25);
					double dy = y+r.nextDouble();
					int l = 20+r.nextInt(60);
					float s = 1.5F+r.nextFloat()*2;
					float f = (float)ReikaRandomHelper.getRandomPlusMinus(0.75, 0.125);
					int c1 = 0x0000ff;//0x60ffa0;
					int c2 = 0x20ffa0;//0x20ffff;
					EntityBlurFX fx1 = new EntityCCBlurFX(world, dx, dy, dz).setLife(l).setScale(s*f).setColor(c1);
					EntityBlurFX fx2 = new EntityCCBlurFX(world, dx, dy, dz).setLife(l).setScale(s).setColor(c2);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
					break;
				}
				case FLOWIVY: {
					boolean flag = (x+y+z)%6 == 0 && Minecraft.getMinecraft().theWorld.rand.nextBoolean();
					if (!flag) {
						HashSet<ForgeDirection> solid = new HashSet();
						for (int i = 0; i < 6; i++) {
							ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
							if (world.getBlock(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ).getMaterial() == Material.rock) {
								solid.add(dir);
								solid.add(dir.getOpposite());
							}
						}
						if (solid.size() < 6) {
							for (int i = 0; i < 6; i++) {
								ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
								if (!solid.contains(dir)) {
									if (!this.matchAt(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ)) {
										flag = true;
										break;
									}
								}
							}
						}
					}
					if (flag) {
						double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.5);
						double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.5);
						double dy = y+0.5+r.nextFloat()*0.25F;
						int l = 60+r.nextInt(120);
						float s = 1+r.nextFloat()*1.5F;
						int c = ReikaColorAPI.mixColors(0x00ff00, 0xa0ffa0, r.nextFloat());
						EntityFloatingSeedsFX fx = (EntityFloatingSeedsFX)new EntityCCFloatingSeedsFX(world, dx, dy, dz, 0, -90, ChromaIcons.CENTER).setLife(l).setScale(s).setColor(c);
						fx.angleVelocity = 0.25;
						fx.freedom = 70;
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				}
				case LUMALILY: {
					double dx = x+0.5;
					double dz = z+0.5;
					double dy = y+0.75;
					int l = 30+r.nextInt(30);
					float s = 2+r.nextFloat()*2;
					int c = ReikaColorAPI.mixColors(0x0000ff, 0xffffff, 0.5F+(float)(0.5*Math.sin(System.currentTimeMillis()/1000D)));
					EntityFloatingSeedsFX fx = (EntityFloatingSeedsFX)new EntityCCFloatingSeedsFX(world, dx, dy, dz, 0, 90, ChromaIcons.CENTER).setLife(l).setScale(s).setColor(c).setGravity(-0.125F).setRapidExpand();
					fx.angleVelocity = 0.5;
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					break;
				}
				case RESOCLOVER: {
					int n = 1+r.nextInt(2);
					for (int i = 0; i < n; i++) {
						double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.5);
						double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.5);
						double dy = y+0.0625*r.nextFloat();
						int l = 5;
						float s = 0.75F;
						int c = ReikaColorAPI.mixColors(0x0000ff, 0x00ff00, r.nextFloat());
						EntityBlurFX fx = new EntityCCBlurFX(world, dx, dy, dz).setIcon(ChromaIcons.BIGFLARE).setLife(l).setScale(s).setColor(c).setRapidExpand();
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				}
				case SANOBLOOM: {
					double dx = x+0.5;
					double dz = z+0.5;
					double dy = y+0.125;
					int l = 60+r.nextInt(80);
					float s = 2+r.nextFloat()*2;
					double[] v = ReikaPhysicsHelper.polarToCartesian(0.0625, r.nextDouble()*70, r.nextDouble()*360);
					EntityBlurFX fx = new EntityCCBlurFX(world, dx, dy, dz, v[0], v[1], v[2]).setLife(l).setScale(s).setColor(0xff0000).setRapidExpand().setNoSlowdown();
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					break;
				}
				case VOIDREED: {
					float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
					int l = 10+r.nextInt(30);
					float s = 1+r.nextFloat()*0.5F;
					int c1 = 0xdf3fff;
					int c2 = 0x202020;
					double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.25);
					double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.25);
					double dy = y+r.nextDouble();
					float f = (float)ReikaRandomHelper.getRandomPlusMinus(0.6, 0.2);
					ChromaIcons ico = ChromaIcons.TRANSFADE;
					EntityFX fx1 = new EntityCCFloatingSeedsFX(world, dx, dy, dz, 0, 90, ico).setScale(s).setLife(l).setColor(c1).setGravity(g).setBasicBlend();
					EntityFX fx2 = new EntityCCFloatingSeedsFX(world, dx, dy, dz, 0, 90, ico).setScale(s*f).setLife(l).setColor(c2).setGravity(g).setBasicBlend().lockTo(fx1);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
					break;
				}
				case GLOWDAISY: {
					double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.625);
					double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.625);
					double dy = y+0.125;
					int c = ReikaColorAPI.mixColors(0x22aaff, 0x0000ff, world.rand.nextFloat());
					ChromaIcons ico = ChromaIcons.FADE;
					EntityFX fx1 = new EntityCCFloatingSeedsFX(world, dx, dy, dz, 0, 90, ico).setColor(c).setRapidExpand().setScale(1.5F);
					EntityFX fx2 = new EntityCCFloatingSeedsFX(world, dx, dy, dz, 0, 90, ico).setScale(0.875F).setRapidExpand().setColor(0xffffff).lockTo(fx1);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
					break;
				}
				case GLOWROOT: {
					if (r.nextInt(3) == 0) {
						if (this.matchAt(world, x, y-1, z) && y%8 != 0)
							return;
						double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.25);
						double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.25);
						double dy = ReikaRandomHelper.getRandomPlusMinus(y+0.5, 0.5);
						int c = 0xffffff;
						int rc = r.nextInt(3);
						switch(rc) {
							case 0:
								c = 0xC89CF4;
								break;
							case 1:
								c = 0xF29BF2;
								break;
						}
						EntityFX fx = new EntityCCBlurFX(world, dx, dy, dz).setColor(c).setAlphaFading().setRapidExpand().setLife(120).setGravity(0.25F).setColliding();
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
				}
			}
		}

		public int getGenerationChance() {
			switch(this) {
				case VOIDREED:
					return 4;
				case ENDERFLOWER:
					return 4;
				case RESOCLOVER:
				case LUMALILY:
					return 2;
				case SANOBLOOM:
					return 6;
				case GLOWDAISY:
					return 2;
				case GLOWROOT:
					return 1;
				default:
					return 1;
			}
		}

		@SideOnly(Side.CLIENT)
		public void render(IBlockAccess world, int x, int y, int z, Block b, RenderBlocks rb, Tessellator v5) {
			IIcon ico = ((BlockDecoFlower)b).icons[this.ordinal()];
			v5.setColorOpaque_I(b.colorMultiplier(world, x, y, z));
			v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z));
			switch(this) {
				case VOIDREED:
				case LUMALILY:
				case SANOBLOOM:
					ReikaRenderHelper.renderCrossTex(world, x, y, z, ico, v5, rb, 1);
					break;
				case RESOCLOVER: {
					double d = 0.03125+0.03125/2*Math.sin(x+y+z);
					ReikaRenderHelper.renderFlatInnerTextureOnSide(world, x, y, z, ico, v5, rb, ForgeDirection.DOWN, d, false);
					break;
				}
				case FLOWIVY: {
					double d = 0.03125+0.03125/2*Math.sin(x+y+z);
					for (int i = 1; i < 6; i++) {
						ReikaRenderHelper.renderFlatInnerTextureOnSide(world, x, y, z, ico, v5, rb, ForgeDirection.VALID_DIRECTIONS[i], 0.03125, true);
					}
					break;
				}
				case ENDERFLOWER:
					ReikaRenderHelper.renderCropTypeTex(world, x, y, z, ico, v5, rb, 0.1875, 1);
					//top of flower ReikaRenderHelper.renderFlatInnerTextureOnSide(world, x, y, z, ico, v5, rb, ForgeDirection.DOWN, 0.75, false);
					break;
				case GLOWDAISY:
					ReikaRenderHelper.renderCropTypeTex(world, x, y, z, ico, v5, rb, 0.25, 1);
					break;
				case GLOWROOT:
					ReikaRenderHelper.renderCropTypeTex(world, x, y, z, ico, v5, rb, 0.1875, 1);
					break;
			}
		}

		public ItemStack getDrop() {
			switch(this) {
				case ENDERFLOWER: return ChromaStacks.teleDust.copy();
				case LUMALILY: return ChromaStacks.icyDust.copy();
				case RESOCLOVER: return ChromaStacks.energyPowder.copy();
				case SANOBLOOM: return ChromaStacks.etherBerries.copy();
				case VOIDREED: return ChromaStacks.voidDust.copy();
				case FLOWIVY: return ChromaStacks.livingEssence.copy();
				case GLOWDAISY: return new ItemStack(Items.glowstone_dust);
				case GLOWROOT: return new ItemStack(Items.glowstone_dust);
			}
			return null;
		}

		/** 1/N */
		public int getDropChance() {
			switch(this) {
				case ENDERFLOWER:
				case LUMALILY:
				case RESOCLOVER:
				case SANOBLOOM:
				case VOIDREED:
				case FLOWIVY: return 1;
				case GLOWDAISY: return 2;
				case GLOWROOT: return 4;
			}
			return 1;
		}

		public int getColor() {
			switch(this) {
				case ENDERFLOWER: return 0xFF00DC;
				case LUMALILY: return 0x84B5D9;
				case RESOCLOVER: return 0xE67FFF;
				case SANOBLOOM: return 0xFF3F3F;
				case VOIDREED: return 0x605366;
				case FLOWIVY: return 0x66BA6D;
				case GLOWDAISY: return 0x51B6FF;
				case GLOWROOT: return 0xC28FF5;
			}
			return 0;
		}
	}
}
