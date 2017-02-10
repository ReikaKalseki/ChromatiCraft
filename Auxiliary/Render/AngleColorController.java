/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Render;

import net.minecraft.entity.Entity;
import Reika.DragonAPI.Interfaces.ColorController;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;


public class AngleColorController implements ColorController {

	public final double posX;
	public final double posZ;
	public final double originAngle;
	public final double originRadius;

	public final double angleVelocity;
	public final double verticalVelocity;
	public final double radiusVelocity;

	private double angle;
	private double radius;

	public AngleColorController(double x, double z, double a, double v, double r, double rv) {
		this(x, z, a, v, r, rv, 0);
	}

	public AngleColorController(double x, double z, double a, double v, double r, double rv, double o) {
		angleVelocity = a;
		verticalVelocity = v;
		originRadius = r;
		radius = originRadius;
		radiusVelocity = rv;
		posX = x;
		posZ = z;

		originAngle = o;
		angle = originAngle;
	}

	@Override
	public void update(Entity e) {
		angle += angleVelocity;
		radius += radiusVelocity;
	}

	@Override
	public int getColor(Entity e) {
		return ReikaColorAPI.getModifiedHue(0xff0000, (int)(angle));
	}

}
