package Reika.ChromatiCraft.Magic.Artefact;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;


public abstract class UABombingEffect {

	public static abstract class EntityEffect extends UABombingEffect {

		public abstract void trigger(Entity e);

	}

	public static abstract class BlockEffect extends UABombingEffect {

		public abstract void trigger(IInventory inv, TileEntity te);

	}

}
