/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.MinerBlock;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.ChunkLoadingTile;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;


public class BlockDimensionChunkloader extends Block implements MinerBlock {

	private final IIcon[][] textures = new IIcon[2][2];

	public BlockDimensionChunkloader(Material mat) {
		super(mat);

		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setHardness(0.75F);
		this.setResistance(6000);
		this.setLightLevel(1);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		//blockIcon = ico.registerIcon("chromaticraft:dimgen/chunkloader");

		textures[0][0] = ico.registerIcon("chromaticraft:dimgen/chunkloader-a");
		textures[0][1] = ico.registerIcon("chromaticraft:dimgen/chunkloader-b");
		textures[1][0] = ico.registerIcon("chromaticraft:dimgen/chunkloader-c");
		textures[1][1] = ico.registerIcon("chromaticraft:dimgen/chunkloader-d");
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		int a = 0;
		int b = 0;
		switch(ForgeDirection.VALID_DIRECTIONS[s]) {
			case UP:
				a = x%2;
				b = z%2;
				break;
			case DOWN:
				a = x%2;
				b = z%2;
				break;
			case EAST:
				a = z%2;
				b = y%2;
				break;
			case WEST:
				a = z%2;
				b = y%2;
				break;
			case NORTH:
				a = x%2;
				b = y%2;
				break;
			case SOUTH:
				a = x%2;
				b = y%2;
				break;
			default:
				break;
		}
		a = (a+2)%2;
		b = (b+2)%2;
		return textures[a][b];
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return textures[0][0];
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 1;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return meta == 1 ? new TileEntityDimensionChunkloader() : null;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		ReikaParticleHelper p = rand.nextInt(3) == 0 ? ReikaParticleHelper.WITCH : ReikaParticleHelper.AMBIENTMOBSPELL;
		p.spawnAroundBlockWithOutset(world, x, y, z, 1, 0.03125);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityDimensionChunkloader) {
				((TileEntityDimensionChunkloader)te).placer = e.getUniqueID();
				ChunkManager.instance.loadChunks((TileEntityDimensionChunkloader)te);
			}
			else {
				String s = "Missing TileEntity for CC dimension chunkloader @ DIM"+world.provider.dimensionId+" "+x+", "+y+", "+z+" ("+te+")";
				ChromatiCraft.logger.logError(s);
				if (e instanceof EntityPlayer) {
					ReikaChatHelper.sendChatToPlayer((EntityPlayer)e, s);
				}
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		if (this.hasTileEntity(meta))
			((TileEntityDimensionChunkloader)world.getTileEntity(x, y, z)).breakBlock();
		super.breakBlock(world, x, y, z, b, meta);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return meta == 1 ? null : super.getItemDropped(meta, rand, fortune);
	}

	public static class TileEntityDimensionChunkloader extends TileEntity implements ChunkLoadingTile {

		private static final int TIMER = 1200; //60s

		private UUID placer;
		private int deletionTimer;

		@Override
		public void updateEntity() {
			if (placer == null)
				this.delete("No placer");
			if (deletionTimer > 0) {
				deletionTimer--;
				if (deletionTimer <= 0)
					this.delete("Timer Elapsed");
			}
			EntityPlayer ep = placer != null ? worldObj.func_152378_a(placer) : null;
			if (ep != null) {
				if (deletionTimer > 0) {
					this.delete("Reentry");
				}
				else if (ep.posY < -250)
					this.delete("Intentional Exit");
			}
			else if (deletionTimer == 0) {
				ChromatiCraft.logger.log("Activating chunkloader crystal "+new WorldLocation(this)+" placed by "+placer);
				deletionTimer = TIMER;
			}
		}

		private void delete(String reason) {
			ChromatiCraft.logger.log("Deleting chunkloader crystal placed by "+placer+": "+reason);
			ChunkManager.instance.unloadChunks(this);
			worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air);
		}

		@Override
		public void breakBlock() {
			ChunkManager.instance.unloadChunks(this);
		}

		@Override
		public Collection<ChunkCoordIntPair> getChunksToLoad() {
			return ReikaJavaLibrary.makeListFrom(new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4));
		}

	}

	@Override
	public boolean isMineable(int meta) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getHarvestItems(World world, int x, int y, int z, int meta, int fortune) {
		return this.getDrops(world, x, y, z, meta, fortune);
	}

	@Override
	public MineralCategory getCategory() {
		return MineralCategory.MISC_UNDERGROUND_VALUABLE;
	}

	@Override
	public Block getReplacedBlock(World world, int x, int y, int z) {
		return Blocks.air;
	}

}
