/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.structure.MapGenStronghold;

import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;


public class ItemStructureMap extends ItemChromaTool {

	public ItemStructureMap(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (ep.isSneaking()) {
			is.setItemDamage((is.getItemDamage()+1)%StructureSearch.list.length);
		}
		else {

		}
		return is;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {

	}

	public static enum StructureSearch {

		STRONGHOLD(),
		TOWERS();

		private static final StructureSearch[] list = values();

		public Collection<Coordinate> getLocations(WorldServer world) {
			Collection<Coordinate> li = new ArrayList();
			switch(this) {
				case STRONGHOLD:
					MapGenStronghold mg = ((ChunkProviderGenerate)world.theChunkProviderServer.currentChunkProvider).strongholdGenerator;
					List li0 = (List)ReikaObfuscationHelper.invoke("getCoordList", mg);
					if (li0 != null) {
						for (Object o : li) {
							li.add(new Coordinate((ChunkPosition)o));
						}
					}
					break;
				case TOWERS:
					for (Towers t : Towers.towerList) {
						Coordinate c = t.getGeneratedLocation();
						if (c == null)
							c = new Coordinate(t.getRootPosition().chunkXPos, 64, t.getRootPosition().chunkZPos);
						li.add(c);
					}
			}
			return li;
		}

	}

}
