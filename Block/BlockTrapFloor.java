package Reika.ChromatiCraft.Block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.Block.CollisionDelegate;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;


@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockTrapFloor extends Block implements CollisionDelegate, SemiUnbreakable, IWailaDataProvider {

	public BlockTrapFloor(Material mat) {
		super(mat);

		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(1F);
		//this.setLightOpacity(1);
		this.setResistance(0);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:trapfloor");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		BlockKey bk = this.getDisguise(meta);
		return bk.blockID == this ? blockIcon : bk.blockID.getIcon(s, bk.metadata);
	}

	private BlockKey getDisguise(int meta) {
		switch(meta) {
			case 0:
			default:
				return new BlockKey(this);
			case 1:
				return new BlockKey(Blocks.stonebrick, 0);
			case 2:
				return new BlockKey(Blocks.planks, 0);
			case 3:
				return new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (HoldingChecks.MANIPULATOR.isHolding(ep)) {
			int meta = 1+(world.getBlockMetadata(x, y, z))%4;
			world.setBlockMetadataWithNotify(x, y, z, meta, 3);
			ReikaSoundHelper.playBreakSound(world, x, y, z, Blocks.stone);
		}
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+0.875, z+1);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		this.setBlockBounds(0, 0, 0, 1, 1, 1);
		if (e instanceof EntityLivingBase) {
			Block b = world.getBlock(x, y-1, z);
			b.onEntityCollidedWithBlock(world, x, y-1, z, e);
			if (b == Blocks.lava || b == Blocks.flowing_lava) {
				e.setOnFireFromLava();
			}
			else if (b == Blocks.water || b == Blocks.flowing_water) {
				e.extinguish();
			}
		}
	}

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity e) {
		super.onEntityWalking(world, x, y, z, e);
		Block b = world.getBlock(x, y-1, z);
		b.onEntityWalking(world, x, y-1, z, e);
		//b.onEntityCollidedWithBlock(world, x, y-1, z, e);
	}

	@Override
	public boolean isBurning(IBlockAccess world, int x, int y, int z) {
		Block b = world.getBlock(x, y-1, z);
		if (b == Blocks.lava || b == Blocks.flowing_lava || b == Blocks.fire)
			return true;
		return b.isBurning(world, x, y-1, z);
	}

	@Override
	public Coordinate getDelegatedCollision(World world, int x, int y, int z) {
		return new Coordinate(x, y-1, z);
	}

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler config) {
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			BlockKey id = this.getDisguise(acc.getMetadata());
			//if (b == Blocks.grass)
			//	b = Blocks.dirt;
			return id.asItemStack();
		}
		return null;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@ModDependent(ModList.WAILA)
	public final List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		String s1 = EnumChatFormatting.ITALIC.toString();
		String s2 = EnumChatFormatting.BLUE.toString();
		String mod = "NULL";
		MovingObjectPosition pos = acc.getPosition();
		if (pos != null) {
			BlockKey id = this.getDisguise(acc.getMetadata());
			if (ReikaItemHelper.isVanillaBlock(id.blockID))
				mod = "Minecraft";
			else {
				UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(id.blockID);
				if (uid != null) {
					mod = Loader.instance().getIndexedModList().get(uid.modId).getName();
				}
			}
			//currenttip.add(s2+s1+mod);
		}
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		return this.isUnbreakable(world, x, y, z, world.getBlockMetadata(x, y, z)) ? -1 : super.getBlockHardness(world, x, y, z);
	}

	@Override
	public boolean isUnbreakable(World world, int x, int y, int z, int meta) {
		int dy = y-1;
		Block b = world.getBlock(x, dy, z);
		while (dy > y-5 && b != ChromaBlocks.STRUCTSHIELD.getBlockInstance()) {
			dy--;
			b = world.getBlock(x, dy, z);
		}
		return b == ChromaBlocks.STRUCTSHIELD.getBlockInstance() && ((BlockStructureShield)b).isUnbreakable(world, x, dy, z, world.getBlockMetadata(x, dy, z));//meta >= 8;
	}

}
