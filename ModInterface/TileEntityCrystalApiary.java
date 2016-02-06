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

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ASM.APIStripper.Strippable;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;

@Strippable(value={"forestry.api.apiculture.IBeeHousing"})
public class TileEntityCrystalApiary extends TileEntityChromaticBase implements IBeeHousing {

	@Override
	public ChunkCoordinates getCoordinates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IErrorLogic getErrorLogic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumTemperature getTemperature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumHumidity getHumidity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBlockLightValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canBlockSeeTheSky() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public World getWorld() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BiomeGenBase getBiome() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameProfile getOwner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec3 getBeeFXCoordinates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChromaTiles getTile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		// TODO Auto-generated method stub

	}

}
