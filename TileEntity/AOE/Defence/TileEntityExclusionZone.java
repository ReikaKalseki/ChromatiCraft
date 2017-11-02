/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Defence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.Perimeter;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;


public class TileEntityExclusionZone extends TileEntityChromaticBase {

	private final Perimeter boundary = new Perimeter().disallowVertical();
	private final Collection<AxisAlignedBB> boxes = new ArrayList();

	private ExclusionModes mode;

	@Override
	public ChromaTiles getTile() {
		return null;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.getTicksExisted()%4 == 0) {
			IEntitySelector sel = this.getEntitySelection();
			for (AxisAlignedBB box : boxes) {
				this.checkBox(world, box, sel);
			}
		}
	}

	private IEntitySelector getEntitySelection() {
		return ReikaEntityHelper.hostileOrPlayerSelector;
	}

	private void checkBox(World world, AxisAlignedBB box, IEntitySelector sel) {
		List<Entity> li = world.selectEntitiesWithinAABB(EntityLivingBase.class, box, sel);
		if (!li.isEmpty()) {
			if (li.size() > 1 || li.get(0) != this.getPlacer())
				this.trigger();
		}
	}

	private void trigger() {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public void setMode(ExclusionModes m) {
		mode = m;
		this.syncAllData(false);
	}

	public void reset() {
		boundary.clear();
		boxes.clear();
	}

	public void calculate() {
		boxes.addAll(boundary.getAreaAABBs());
	}

	public static enum ExclusionModes {
		ALL(ReikaEntityHelper.hostileOrPlayerSelector),
		PLAYERS(ReikaEntityHelper.playerSelector),
		MOBS(ReikaEntityHelper.hostileSelector);

		private final IEntitySelector selector;

		private ExclusionModes(IEntitySelector sel) {
			selector = sel;
		}
	}

}
