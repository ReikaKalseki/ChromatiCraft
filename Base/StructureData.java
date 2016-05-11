/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockStructureDataStorage.TileEntityStructureDataStorage;

public abstract class StructureData {

	protected final DimensionStructureGenerator generator;

	protected StructureData(DimensionStructureGenerator gen) {
		generator = gen;
	}

	public final DimensionStructureType getType() {
		return generator.getType();
	}

	public abstract void load(HashMap<String, Object> map);

	public void onInteract(World world, int x, int y, int z, EntityPlayer ep, int s, HashMap<String, Object> extraData) {

	}

	public void onTileTick(TileEntityStructureDataStorage te) {

	}

	public final UUID getUUID() {
		return generator.id;
	}

}
