/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Transport;

import java.util.HashSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.API.Interfaces.RangeUpgradeable;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityCrystalTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

@Deprecated
public class TileEntityFluidNode extends TileEntityChromaticBase implements RangeUpgradeable {

	public static final int SCAN_RADIUS_XZ = 16;

	private int scanRange;

	private final StepTimer cacheTimer = new StepTimer(40);

	private final HashSet<WorldLocation> inputs = new HashSet();
	private final HashSet<Fluid> fluids = new HashSet();

	private Fluid selectedFluids;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		cacheTimer.update();

		if (cacheTimer.checkCap() || this.getTicksExisted() == 0) {
			this.scanAndCache(world, x, y, z);
		}
	}

	protected final void addInput(WorldLocation loc) {
		inputs.add(loc);
	}

	private void scanAndCache(World world, int x, int y, int z) {
		int r = scanRange;
		int r2 = r/2;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int j = -r2; j <= 0; j++) {
					TileEntity te = world.getTileEntity(x+i, y+j, z+k);
					if (te instanceof TileEntityCrystalTank) {
						TileEntityCrystalTank ct = (TileEntityCrystalTank)te;
						if (ct.getFluid() != null) {
							this.addInput(new WorldLocation(te));
							fluids.add(ct.getFluid());
						}
					}
				}
			}
		}
		scanRange = SCAN_RADIUS_XZ;
	}

	public final void upgradeRange(double r) {
		scanRange = (int)(SCAN_RADIUS_XZ*r);
	}

	public final int getRange() {
		return scanRange;
	}

	@Override
	public ChromaTiles getTile() {
		return null;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBTTagList li = new NBTTagList();
		for (Fluid f : fluids) {
			li.appendTag(new NBTTagString(f.getName()));
		}
		NBT.setTag("fluids", li);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		fluids.clear();
		NBTTagList li = NBT.getTagList("fluids", NBTTypes.STRING.ID);
		for (Object o : li.tagList) {
			NBTTagString s = (NBTTagString)o;
			Fluid f = FluidRegistry.getFluid(s.func_150285_a_());
			fluids.add(f);
		}
	}

}
