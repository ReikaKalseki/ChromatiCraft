/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import Reika.ChromatiCraft.Base.ChromaPotion;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class PotionLumenRegen extends ChromaPotion {

	private final PlayerMap<LumenCache> buffers = new PlayerMap();

	public PotionLumenRegen(int id) {
		super(id, false, 0xffffff, 3);
	}

	@Override
	public String getName() {
		return StatCollector.translateToLocal("chromapotion.lumenregen");
	}

	@Override
	public int getLiquidColor() {
		return CrystalElement.getBlendedColor(Math.abs((int)System.currentTimeMillis()), 50);
	}

	@Override
	public void performEffect(EntityLivingBase elb, int level) {
		if (elb instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)elb;
			LumenCache c = buffers.get(ep);
			if (c == null || ep.worldObj.getTotalWorldTime()-c.timestamp > 2500) {
				c = new LumenCache(ep);
				buffers.put(ep, c);
			}

			if (!c.energy.isEmpty()) {
				CrystalElement e = ReikaJavaLibrary.getRandomCollectionEntry(ep.getRNG(), c.energy.elementSet());
				int diff = c.energy.getValue(e)-PlayerElementBuffer.instance.getPlayerContent(ep, e);
				if (diff > 0) {
					int amt = Math.max(1, diff/32);
					PlayerElementBuffer.instance.addToPlayer(ep, e, amt, false);
				}
			}
		}
	}

	@Override
	public boolean isReady(int tick, int level) {
		return true;
	}

	private static class LumenCache {

		private final ElementTagCompound energy;
		private final long timestamp;

		private LumenCache(EntityPlayer ep) {
			timestamp = ep.worldObj.getTotalWorldTime();
			energy = PlayerElementBuffer.instance.getPlayerBuffer(ep).copy();
		}

		private LumenCache(EntityPlayer ep, LumenCache prev) {
			timestamp = ep.worldObj.getTotalWorldTime();
			energy = PlayerElementBuffer.instance.getPlayerBuffer(ep).copy().maximizeWith(prev.energy);
		}

	}

}
