/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Entity.EntityVacuum;
import Reika.ChromatiCraft.Render.ISBRH.SelectiveGlassRenderer;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.Block.ConnectedTextureGlass;
import Reika.DragonAPI.ModRegistry.InterfaceCache;


public class BlockSelectiveGlass extends Block implements ConnectedTextureGlass {

	private final ArrayList<Integer> allDirs = new ArrayList();
	private final IIcon[] edges = new IIcon[10];

	//public static Entity collidingEntity;

	private static final HashMap<Coordinate, Entity> collidingEntities = new HashMap();
	private static long lastEntityCull = -1;

	public BlockSelectiveGlass(Material mat) {
		super(mat);

		for (int i = 1; i < 10; i++) {
			allDirs.add(i);
		}

		this.setResistance(7.5F);
		this.setHardness(1);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	public static void addEntityCheckPos(Coordinate c, Entity e) {
		long t = e.worldObj.getTotalWorldTime();
		if (t-lastEntityCull > 100) {
			collidingEntities.clear();
			lastEntityCull = t;
		}
		collidingEntities.put(c, e);
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
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		SelectiveGlassRenderer.renderPass = pass;
		return pass <= 1;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.selectiveRender;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		AxisAlignedBB ret = null;
		Coordinate c = new Coordinate(x, y, z);
		Entity e = collidingEntities.get(c);
		if (e == null || !this.canEntityPass(world, x, y, z, e)) {
			ret = super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
		collidingEntities.remove(c);
		return ret;
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB box, List li, Entity e) {
		if (this.canEntityPass(world, x, y, z, e)) {

		}
		else {
			//collide = true;
			super.addCollisionBoxesToList(world, x, y, z, box, li, e);
			//li.add(ReikaAABBHelper.getBlockAABB(x, y, z));
		}
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 vec1, Vec3 vec2) {
		Coordinate c = new Coordinate(x, y, z);
		Entity e = collidingEntities.get(c);
		if (e != null && this.canEntityPass(world, x, y, z, e)) {
			return null;
		}
		collidingEntities.remove(c);
		return super.collisionRayTrace(world, x, y, z, vec1, vec2);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (!this.canEntityPass(world, x, y, z, e)) {
			//e.setDead();
		}
	}

	private boolean canEntityPass(World world, int x, int y, int z, Entity e) {
		if (e instanceof EntityThrowable) {
			return ((EntityThrowable)e).getThrower() instanceof EntityPlayer;
		}
		else if (e instanceof EntityFireball) {
			return ((EntityFireball)e).shootingEntity instanceof EntityPlayer;
		}
		else if (e instanceof EntityArrow) {
			return ((EntityArrow)e).shootingEntity instanceof EntityPlayer;
		}
		else if (e instanceof EntityVacuum) {
			return true;
		}
		else if (ModList.BLOODMAGIC.isLoaded() && InterfaceCache.SPELLSHOT.instanceOf(e)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
		return iba.getBlock(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ) != this; //NOT translated by side
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {

		for (int i = 0; i < 10; i++) {
			edges[i] = ico.registerIcon("chromaticraft:glass/glass_"+i);
		}
	}

	public ArrayList<Integer> getEdgesForFace(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		ArrayList<Integer> li = new ArrayList();
		li.addAll(allDirs);

		li.remove(new Integer(5)); //glass tex

		if (face.offsetX != 0) { //test YZ
			//sides; removed if have adjacent on side
			if (world.getBlock(x, y, z+1) == this)
				li.remove(new Integer(2));
			if (world.getBlock(x, y, z-1) == this)
				li.remove(new Integer(8));
			if (world.getBlock(x, y+1, z) == this)
				li.remove(new Integer(4));
			if (world.getBlock(x, y-1, z) == this)
				li.remove(new Integer(6));

			//Corners; only removed if have adjacent on side AND corner
			if (world.getBlock(x, y+1, z+1) == this && !li.contains(4) && !li.contains(2))
				li.remove(new Integer(1));
			if (world.getBlock(x, y-1, z-1) == this && !li.contains(6) && !li.contains(8))
				li.remove(new Integer(9));
			if (world.getBlock(x, y+1, z-1) == this && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
			if (world.getBlock(x, y-1, z+1) == this && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
		}
		if (face.offsetY != 0) { //test XZ
			//sides; removed if have adjacent on side
			if (world.getBlock(x, y, z+1) == this)
				li.remove(new Integer(2));
			if (world.getBlock(x, y, z-1) == this)
				li.remove(new Integer(8));
			if (world.getBlock(x+1, y, z) == this)
				li.remove(new Integer(4));
			if (world.getBlock(x-1, y, z) == this)
				li.remove(new Integer(6));

			//Corners; only removed if have adjacent on side AND corner
			if (world.getBlock(x+1, y, z+1) == this && !li.contains(4) && !li.contains(2))
				li.remove(new Integer(1));
			if (world.getBlock(x-1, y, z-1) == this && !li.contains(6) && !li.contains(8))
				li.remove(new Integer(9));
			if (world.getBlock(x+1, y, z-1) == this && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
			if (world.getBlock(x-1, y, z+1) == this && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
		}
		if (face.offsetZ != 0) { //test XY
			//sides; removed if have adjacent on side
			if (world.getBlock(x, y+1, z) == this)
				li.remove(new Integer(4));
			if (world.getBlock(x, y-1, z) == this)
				li.remove(new Integer(6));
			if (world.getBlock(x+1, y, z) == this)
				li.remove(new Integer(2));
			if (world.getBlock(x-1, y, z) == this)
				li.remove(new Integer(8));

			//Corners; only removed if have adjacent on side AND corner
			if (world.getBlock(x+1, y+1, z) == this && !li.contains(2) && !li.contains(4))
				li.remove(new Integer(1));
			if (world.getBlock(x-1, y-1, z) == this && !li.contains(8) && !li.contains(6))
				li.remove(new Integer(9));
			if (world.getBlock(x+1, y-1, z) == this && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
			if (world.getBlock(x-1, y+1, z) == this && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
		}
		return li;
	}

	public IIcon getIconForEdge(IBlockAccess world, int x, int y, int z, int edge) {
		return edges[edge];
	}

	public IIcon getIconForEdge(int itemMeta, int edge) {
		return edges[edge];
	}

	@Override
	public boolean renderCentralTextureForItem(int meta) {
		return true;
	}

}
