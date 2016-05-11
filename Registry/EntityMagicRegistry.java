/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.HashMap;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import Reika.ChromatiCraft.Magic.ElementTagCompound;

public class EntityMagicRegistry {

	public static final EntityMagicRegistry instance = new EntityMagicRegistry();

	private final HashMap<Class <? extends EntityLiving>, ElementTagCompound> data = new HashMap();

	private EntityMagicRegistry() {
		this.addTag(EntityCreeper.class, CrystalElement.BLACK, 5);
		this.addTag(EntitySpider.class, CrystalElement.BLACK, 5);
		this.addTag(EntityZombie.class, CrystalElement.BLACK, 5);
		this.addTag(EntitySkeleton.class, CrystalElement.BLACK, 5);
	}

	private void addTag(Class <? extends EntityLiving> cl, CrystalElement color, int value) {
		data.get(cl).setTag(color, value);
	}

}
