package Reika.ChromatiCraft.Block;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockFakeSky extends Block {

	private static final SkyCache skyCache = new SkyCache();

	public BlockFakeSky(Material mat) {
		super(mat);

		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(0.5F);
		this.setLightOpacity(0);
		this.setResistance(600000);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:blank");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.theWorld != null && mc.thePlayer != null) {
			return ReikaColorAPI.fromVec3(mc.theWorld.getSkyColor(mc.thePlayer, ReikaRenderHelper.getPartialTickTime()));
		}
		return 0x55d0ff;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return this.getRenderColor(iba.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 1;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return meta == 1 ? new TileEntityFakeSkyController() : null;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		world.setBlockMetadataWithNotify(x, y, z, 1, 3);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldb, int oldmeta) {
		super.breakBlock(world, x, y, z, oldb, oldmeta);

		skyCache.remove(new WorldLocation(world, x, y, z));

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			BlockArray arr = new BlockArray();
			arr.recursiveAddWithBounds(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, this, x-64, y-12, z-64, x+64, y+12, z+64);
			for (Coordinate c : arr.keySet()) {
				c.setBlockMetadata(world, 0);
			}
			if (arr.isEmpty())
				continue;
			arr.getNthBlock(0).setBlockMetadata(world, 1);
		}

		world.markBlocksDirtyVertical(x, z, 0, y);
	}

	public static void updateColumn(World world, int x, int y, int z) {
		skyCache.updateColumn(world, x, y, z);
	}

	public static boolean isForcedSky(World world, int x, int y, int z) {
		return skyCache.isForcedSky(world, x, y, z);
	}

	public static void clearCache() {
		skyCache.data.clear();
		skyCache.skySections.clear();
	}

	@SideOnly(Side.CLIENT)
	public static void doSkyRendering() {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		Collection<BlockArray> c = skyCache.getSkySections(ep.worldObj);
		if (c != null) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();

			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

			Vec3 clr = ep.worldObj.getSkyColor(ep, ReikaRenderHelper.getPartialTickTime());

			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			v5.setColorOpaque_F((float)clr.xCoord, (float)clr.yCoord, (float)clr.zCoord);
			double o = 0.0075;//*ep.getDistance((arr.getMaxX()+arr.getMinX())/2, (arr.getMaxY()+arr.getMinY())/2, (arr.getMaxZ()+arr.getMinZ())/2);
			for (BlockArray arr : c) {
				v5.addVertex(arr.getMinX()-o, arr.getMinY()-o, arr.getMinZ()-o);
				v5.addVertex(arr.getMaxX()+1+o, arr.getMinY()-o, arr.getMinZ()-o);
				v5.addVertex(arr.getMaxX()+1+o, arr.getMinY()-o, arr.getMaxZ()+1+o);
				v5.addVertex(arr.getMinX()-o, arr.getMinY()-o, arr.getMaxZ()+1+o);

				v5.addVertex(arr.getMinX()-o, arr.getMaxY()+1+o, arr.getMaxZ()+1+o);
				v5.addVertex(arr.getMaxX()+1+o, arr.getMaxY()+1+o, arr.getMaxZ()+1+o);
				v5.addVertex(arr.getMaxX()+1+o, arr.getMaxY()+1+o, arr.getMinZ()-o);
				v5.addVertex(arr.getMinX()-o, arr.getMaxY()+1+o, arr.getMinZ()-o);

				v5.addVertex(arr.getMinX()-o, arr.getMinY()-o, arr.getMaxZ()+1+o);
				v5.addVertex(arr.getMinX()-o, arr.getMaxY()+1+o, arr.getMaxZ()+1+o);
				v5.addVertex(arr.getMinX()-o, arr.getMaxY()+1+o, arr.getMinZ()-o);
				v5.addVertex(arr.getMinX()-o, arr.getMinY()-o, arr.getMinZ()-o);

				v5.addVertex(arr.getMaxX()+1+o, arr.getMinY()-o, arr.getMinZ()-o);
				v5.addVertex(arr.getMaxX()+1+o, arr.getMaxY()+1+o, arr.getMinZ()-o);
				v5.addVertex(arr.getMaxX()+1+o, arr.getMaxY()+1+o, arr.getMaxZ()+1+o);
				v5.addVertex(arr.getMaxX()+1+o, arr.getMinY()-o, arr.getMaxZ()+1+o);

				v5.addVertex(arr.getMinX()-o, arr.getMaxY()+1+o, arr.getMinZ()-o);
				v5.addVertex(arr.getMaxX()+1+o, arr.getMaxY()+1+o, arr.getMinZ()-o);
				v5.addVertex(arr.getMaxX()+1+o, arr.getMinY()-o, arr.getMinZ()-o);
				v5.addVertex(arr.getMinX()-o, arr.getMinY()-o, arr.getMinZ()-o);

				v5.addVertex(arr.getMinX()-o, arr.getMinY()-o, arr.getMaxZ()+1+o);
				v5.addVertex(arr.getMaxX()+1+o, arr.getMinY()-o, arr.getMaxZ()+1+o);
				v5.addVertex(arr.getMaxX()+1+o, arr.getMaxY()+1+o, arr.getMaxZ()+1+o);
				v5.addVertex(arr.getMinX()-o, arr.getMaxY()+1+o, arr.getMaxZ()+1+o);
			}
			v5.draw();
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
	}

	public static class TileEntityFakeSkyController extends TileEntity {

		private boolean cached = false;

		@Override
		public void updateEntity() {
			if (!cached) {
				this.addToCache();
				cached = true;
			}
		}

		private void addToCache() {
			BlockArray arr = new BlockArray();
			arr.recursiveAddWithBounds(worldObj, xCoord, yCoord, zCoord, this.getBlockType(), xCoord-64, yCoord-12, zCoord-64, xCoord+64, yCoord+12, zCoord+64);
			for (Coordinate c : arr.keySet()) {
				if (!c.equals(xCoord, yCoord, zCoord))
					c.setBlockMetadata(worldObj, 0);
				skyCache.add(c, worldObj);
			}
		}

	}

	private static class SkyCache {

		private final MultiMap<WorldLocation, SkyShunt> data = new MultiMap().setNullEmpty();
		private MultiMap<Integer, BlockArray> skySections = new MultiMap().setNullEmpty();

		public boolean isForcedSky(World world, int x, int y, int z) {
			WorldLocation c = new WorldLocation(world, x, 0, z);
			Collection<SkyShunt> li = data.get(c);
			if (li == null)
				return false;
			for (SkyShunt s : li) {
				if (s.includesY(y)) {
					return true;
				}
			}
			return false;
		}

		public void updateColumn(World world, int x, int y, int z) {
			WorldLocation c = new WorldLocation(world, x, 0, z);
			Collection<SkyShunt> li = data.get(c);
			if (li == null)
				return;
			for (SkyShunt s : li) {
				if (s.includesY(y)) {
					s.update(world);
				}
			}
		}

		private void remove(WorldLocation c) {
			Collection<SkyShunt> li = data.get(c.to2D());
			if (li != null) {
				Iterator<SkyShunt> it = li.iterator();
				while (it.hasNext()) {
					SkyShunt s = it.next();
					if (s.skyBlock.equals(new Coordinate(c))) {
						it.remove();
						break;
					}
				}
				if (li.isEmpty()) {
					data.remove(c.to2D());
				}
			}
			this.recalcBlocks();
		}

		private void add(Coordinate c, World world) {
			data.addValue(new WorldLocation(world, c.to2D()), new SkyShunt(c, world));
			this.recalcBlocks();
		}

		private void recalcBlocks() {
			MultiMap<Integer, BlockArray> replSky = new MultiMap().setNullEmpty();
			HashSet<WorldLocation> set = new HashSet();
			for (Collection<SkyShunt> c : data.values()) {
				for (SkyShunt s : c) {
					set.add(new WorldLocation(s.dimensionID, s.skyBlock));
				}
			}
			while (!set.isEmpty()) {
				WorldLocation c = ReikaJavaLibrary.getRandomCollectionEntry(DragonAPICore.rand, set);
				BlockArray arr = new BlockArray();
				arr.recursiveAdd(new CacheAccess(c.dimensionID, set), c.xCoord, c.yCoord, c.zCoord, ChromaBlocks.FAKESKY.getBlockInstance());
				for (Coordinate loc : arr.keySet()) {
					set.remove(new WorldLocation(c.dimensionID, loc));
				}
				for (BlockArray arr2 : arr.splitToRectangles())
					replSky.addValue(c.dimensionID, arr2);
			}
			skySections = replSky;
		}

		private Collection<BlockArray> getSkySections(World world) {
			return skySections.get(world.provider.dimensionId);
		}

		private class CacheAccess implements IBlockAccess {

			private final int dimensionID;
			private HashSet<WorldLocation> set;

			private CacheAccess(int dim, HashSet<WorldLocation> set) {
				dimensionID = dim;
				this.set = set;
			}

			@Override
			public Block getBlock(int x, int y, int z) {
				return set.contains(new WorldLocation(dimensionID, x, y, z)) ? ChromaBlocks.FAKESKY.getBlockInstance() : Blocks.air;
			}

			@Override
			public TileEntity getTileEntity(int x, int y, int z) {return null;}
			@Override
			@SideOnly(Side.CLIENT)
			public int getLightBrightnessForSkyBlocks(int x, int y, int z, int l) {return 0;}
			@Override
			public int getBlockMetadata(int x, int y, int z) {return 0;}
			@Override
			public int isBlockProvidingPowerTo(int x, int y, int z, int s) {return 0;}
			@Override
			public boolean isAirBlock(int x, int y, int z) {return false;}
			@Override
			@SideOnly(Side.CLIENT)
			public BiomeGenBase getBiomeGenForCoords(int x, int z) {return BiomeGenBase.ocean;}
			@Override
			@SideOnly(Side.CLIENT)
			public int getHeight() {return 0;}
			@Override
			@SideOnly(Side.CLIENT)
			public boolean extendedLevelsInChunkCache() {return false;}
			@Override
			public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {return false;}
		}

	}

	private static class SkyShunt {

		private final int dimensionID;
		private final Coordinate skyBlock;
		private int minY;

		private SkyShunt(Coordinate c, World world) {
			skyBlock = c;
			dimensionID = world.provider.dimensionId;
			this.calcMinY(world);
		}

		public boolean includesY(int y) {
			return y >= minY && y < skyBlock.yCoord;
		}

		private void calcMinY(World world) {
			int y = skyBlock.yCoord-1;
			while (world.getBlock(skyBlock.xCoord, y, skyBlock.zCoord).getLightOpacity(world, skyBlock.xCoord, y, skyBlock.zCoord) == 0) {
				y--;
			}
			minY = y+1;
		}

		private void update(World world) {
			this.calcMinY(world);
		}

		@Override
		public int hashCode() {
			return skyBlock.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof SkyShunt && ((SkyShunt)o).skyBlock.equals(skyBlock);
		}

	}

}
