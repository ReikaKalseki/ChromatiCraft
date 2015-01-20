/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;

public class TileEntityAspectJar extends TileEntityChromaticBase implements IAspectContainer, IEssentiaTransport {

	public static final int CAPACITY = 240000;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ASPECTJAR;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public AspectList getAspects() {
		return null;
	}

	@Override
	public void setAspects(AspectList aspects) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean doesContainerAccept(Aspect tag) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int addToContainer(Aspect tag, int amount) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean takeFromContainer(Aspect tag, int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@Deprecated
	public boolean takeFromContainer(AspectList ot) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@Deprecated
	public boolean doesContainerContain(AspectList ot) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int containerContains(Aspect tag) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConnectable(ForgeDirection face) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canInputFrom(ForgeDirection face) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canOutputTo(ForgeDirection face) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public Aspect getSuctionType(ForgeDirection face) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSuctionAmount(ForgeDirection face) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Aspect getEssentiaType(ForgeDirection face) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEssentiaAmount(ForgeDirection face) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinimumSuction() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean renderExtendedTube() {
		// TODO Auto-generated method stub
		return false;
	}

}
