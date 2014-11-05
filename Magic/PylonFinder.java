/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;

public class PylonFinder {

	private final LinkedList<WorldLocation> nodes = new LinkedList();
	private final CrystalNetworker net;
	private static final RayTracer tracer;

	private final int stepRange;
	private final World world;
	private final int targetX;
	private final int targetY;
	private final int targetZ;
	private final CrystalElement element;

	PylonFinder(CrystalElement e, World world, int x, int y, int z, int r) {
		element = e;
		this.world = world;
		targetX = x;
		targetY = y;
		targetZ = z;
		stepRange = r;
		net = CrystalNetworker.instance;
	}

	CrystalPath findPylon() {
		this.findFrom(targetX, targetY, targetZ);
		return this.isComplete() ? new CrystalPath(element, nodes) : null;
	}

	CrystalFlow findPylon(CrystalReceiver r, int amount) {
		this.findFrom(targetX, targetY, targetZ);
		return this.isComplete() ? new CrystalFlow(r, element, amount, nodes) : null;
	}

	public boolean isComplete() {
		return nodes.size() >= 2 && nodes.getLast().getTileEntity() instanceof CrystalSource;
	}

	private void findFrom(int x, int y, int z) {
		if (nodes.contains(new WorldLocation(world, x, y, z))) {
			return;
		}
		nodes.add(new WorldLocation(world, x, y, z));
		ArrayList<CrystalTransmitter> li = net.getTransmittersWithinDofXYZ(world, x, y, z, stepRange, element);
		for (CrystalTransmitter te : li) {
			if (/*!te.needsLineOfSight() || */this.lineOfSight(world, x, y, z, te)) {
				if (te instanceof CrystalSource) {
					nodes.add(new WorldLocation(world, te.getX(), te.getY(), te.getZ()));
					return;
				}
				else if (te instanceof CrystalRepeater) {
					this.findFrom(te.getX(), te.getY(), te.getZ());
				}
			}
		}
		if (!this.isComplete())
			nodes.removeLast();
	}

	private boolean lineOfSight(World world, int x, int y, int z, CrystalNetworkTile te) {
		return lineOfSight(world, x, y, z, te.getX(), te.getY(), te.getZ());
	}

	public static boolean lineOfSight(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		RayTracer ray = tracer.setOrigins(x1, y1, z1, x2, y2, z2);
		return ray.isClearLineOfSight(world);
	}

	static {
		tracer = new RayTracer(0, 0, 0, 0, 0, 0);
		tracer.setInternalOffsets(0.5, 0.5, 0.5);
		tracer.softBlocksOnly = true;/*
		tracer.addOpaqueBlock(Blocks.standing_sign);
		tracer.addOpaqueBlock(Blocks.reeds);
		tracer.addOpaqueBlock(Blocks.carpet);
		tracer.addOpaqueBlock(Blocks.tallgrass);
		tracer.addOpaqueBlock(Blocks.deadbush);
		tracer.addOpaqueBlock(Blocks.rail);
		tracer.addOpaqueBlock(Blocks.web);
		tracer.addOpaqueBlock(Blocks.torch);
		tracer.addOpaqueBlock(Blocks.redstone_torch);
		tracer.addOpaqueBlock(Blocks.unlit_redstone_torch);
		tracer.addOpaqueBlock(Blocks.powered_comparator);
		tracer.addOpaqueBlock(Blocks.unpowered_comparator);
		tracer.addOpaqueBlock(Blocks.powered_repeater);
		tracer.addOpaqueBlock(Blocks.unpowered_repeater);
		tracer.addOpaqueBlock(Blocks.wheat);
		tracer.addOpaqueBlock(Blocks.carrots);
		tracer.addOpaqueBlock(Blocks.potatoes);*/
	}

}
