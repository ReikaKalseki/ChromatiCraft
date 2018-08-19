/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.CrystalPath;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledSoundEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget.PlayerTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;


public class ItemBottleneckFinder extends ItemChromaTool {

	public ItemBottleneckFinder(int index) {
		super(index);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float af, float bf, float cf) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof CrystalReceiver) {
				boolean flag = false;
				CrystalReceiver r = (CrystalReceiver)te;
				for (CrystalElement e : CrystalElement.elements) {
					if (r.isConductingElement(e)) {
						CrystalPath p = CrystalNetworker.instance.getConnectivity(e, r);
						if (p != null) {
							flag = true;
							p.blink(20*10, r);
							TreeMap<Integer, Collection<WorldLocation>> throughputLimits = new TreeMap();
							TreeMap<Integer, Collection<WorldLocation>> lossLevels = new TreeMap();
							for (CrystalNetworkTile cr : p.getTileList()) {
								int thru = cr.maxThroughput();
								WorldLocation loc = new WorldLocation(cr.getWorld(), cr.getX(), cr.getY(), cr.getZ());
								Collection<WorldLocation> c = throughputLimits.get(thru);
								if (c == null) {
									c = new ArrayList();
									throughputLimits.put(thru, c);
								}
								c.add(loc);
								if (cr instanceof CrystalRepeater) {
									int loss = ((CrystalRepeater)cr).getSignalDegradation();
									if (loss > 0) {
										c = lossLevels.get(loss);
										if (c == null) {
											c = new ArrayList();
											lossLevels.put(loss, c);
										}
										c.add(loc);
									}
								}
								//throughputLimits.addValue(, cr);
								//cr.triggerBottleneckDisplay(20*30);
							}
							HashSet<WorldLocation> sentLoss = new HashSet();
							HashSet<WorldLocation> sentThrough = new HashSet();
							for (int i = 0; i < WarningLevels.list.length; i++) {
								WarningLevels w = WarningLevels.list[i];
								Collection<WorldLocation> worstThrough = throughputLimits.isEmpty() ? null : throughputLimits.remove(throughputLimits.firstKey());
								Collection<WorldLocation> worstLoss = lossLevels.isEmpty() ? null : lossLevels.remove(lossLevels.lastKey());
								if (worstThrough != null) {
									for (WorldLocation loc : worstThrough) {
										if (!sentThrough.contains(loc)) {
											triggerWarning((EntityPlayerMP)ep, loc, e, w, true);
											sentThrough.add(loc);
										}
									}
								}
								if (worstLoss != null) {
									for (WorldLocation loc : worstLoss) {
										if (!sentLoss.contains(loc)) {
											triggerWarning((EntityPlayerMP)ep, loc, e, w, false);
											sentLoss.add(loc);
										}
									}
								}
							}
						}
					}
				}
				if (flag) {
					MusicKey[] mk = {MusicKey.C4, MusicKey.C5, MusicKey.E5, MusicKey.G5, MusicKey.C6};
					for (int i = 0; i < 5; i++) {
						float f = (float)mk[i].getRatio(MusicKey.C5);
						//ChromaSounds.REPEATERRING.playSoundAtBlock(te, 1, f);
						ScheduledSoundEvent evt = new ScheduledSoundEvent(ChromaSounds.REPEATERRING, ep, 1, f);
						evt.attenuate = false;
						TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(evt), 1+i*13);
					}
				}
				else {
					ChromaSounds.ERROR.playSoundAtBlock(te);
				}
				return true;
			}
		}
		return false;
	}

	private static void triggerWarning(EntityPlayerMP ep, WorldLocation loc, CrystalElement e, WarningLevels w, boolean isThroughput) {
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.BOTTLENECK.ordinal(), new PlayerTarget(ep), loc.dimensionID, loc.xCoord, loc.yCoord, loc.zCoord, w.ordinal(), isThroughput ? 1 : 0, e.ordinal());
	}

	public static enum WarningLevels {
		FIRST(),
		SECOND(),
		THIRD();

		public static final WarningLevels[] list = values();

		public IIcon getIcon() {
			return ChromaIcons.BLACKHOLE.getIcon();
		}
	}

}
