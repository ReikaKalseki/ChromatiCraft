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
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.WorldEvent;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FiberIO;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FiberSource;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityFiberOptic;
import Reika.ChromatiCraft.TileEntity.TileEntityFiberReceiver;
import Reika.ChromatiCraft.TileEntity.TileEntityFiberTransmitter;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FiberNetwork {

	private final ArrayList<TileEntityFiberOptic> wires = new ArrayList();
	private final EnumMap<CrystalElement, Collection<TileEntityFiberTransmitter>> sinks = new EnumMap(CrystalElement.class);
	private final EnumMap<CrystalElement, Collection<TileEntityFiberReceiver>> sources = new EnumMap(CrystalElement.class);
	//private final Collection<FiberPath> paths = new ArrayList();

	public FiberNetwork() {
		MinecraftForge.EVENT_BUS.register(this);
		//TickRegistry.instance.registerTickHandler(this, Side.SERVER);
	}

	@SubscribeEvent
	public void unload(WorldEvent.Unload evt) {
		this.clear(true);
	}

	public void addBlock(TileEntityFiberOptic te) {
		if (!wires.contains(te))
			wires.add(te);
	}

	public void removeBlock(TileEntityFiberOptic te) {
		wires.remove(te);
		this.rebuild();
	}

	public void removeTerminus(FiberIO te) {
		CrystalElement e = te.getColor();
		Collection<TileEntityFiberTransmitter> sink = sinks.get(e);
		if (sink != null)
			sink.remove(te);
		Collection<TileEntityFiberReceiver> source = sources.get(e);
		if (source != null)
			source.remove(te);
		if (te instanceof TileEntityFiberTransmitter) {
			Collection<TileEntityFiberReceiver> c = sources.get(e);
			if (c != null) {
				for (TileEntityFiberReceiver r : c) {
					r.removeTerminus((TileEntityFiberTransmitter)te);
				}
			}
		}
		else if (te instanceof TileEntityFiberReceiver) {
			Collection<TileEntityFiberTransmitter> c = sinks.get(e);
			if (c != null) {
				for (TileEntityFiberTransmitter r : c) {
					((TileEntityFiberReceiver)te).removeTerminus(r);
				}
			}
		}
	}

	private void rebuild() {
		ArrayList<TileEntityFiberOptic> li = new ArrayList();
		for (TileEntityFiberOptic te : wires) {
			li.add(te);
		}
		this.clear(true);

		for (TileEntityFiberOptic te : li) {
			te.findAndJoinNetwork(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
		}
	}

	public void merge(FiberNetwork n) {
		if (n != this) {
			//ArrayList<TileEntity> li = new ArrayList();
			for (TileEntityFiberOptic wire : n.wires) {
				wire.setNetwork(this);
				//li.add(wire);
			}
			for (Collection<TileEntityFiberTransmitter> c : n.sinks.values()) {
				//if (!li.contains(emitter)) {
				if (c != null) {
					for (TileEntityFiberTransmitter emitter : c) {
						emitter.setNetwork(this);
						this.addEmitter(emitter, emitter.getColor());
					}
				}
				//	li.add(emitter);
				//}
			}
			for (Collection<TileEntityFiberReceiver> c : n.sources.values()) {
				//if (!li.contains(source)) {
				if (c != null) {
					for (TileEntityFiberReceiver source : c) {
						source.setNetwork(this);
						this.addReceiver(source, source.getColor());
					}
				}
				//	li.add(source);
				//}
			}
			n.clear(false);
		}
		//this.updateWires();

		try {
			MinecraftForge.EVENT_BUS.unregister(this);
		}
		catch (Exception e) { //randomly??
			//e.printStackTrace();
		}
	}

	public void addIO(FiberIO te, CrystalElement e) {
		if (te instanceof TileEntityFiberTransmitter) {
			this.addEmitter((TileEntityFiberTransmitter)te, e);
		}
		else if (te instanceof TileEntityFiberReceiver) {
			this.addReceiver((TileEntityFiberReceiver)te, e);
		}
	}

	private void addEmitter(TileEntityFiberTransmitter te, CrystalElement e) {
		if (e != null) {
			Collection<TileEntityFiberTransmitter> c = sinks.get(e);
			if (c == null) {
				c = new ArrayList();
				sinks.put(e, c);
			}
			c.add(te);
		}
		te.setNetwork(this);
		Collection<TileEntityFiberReceiver> c = sources.get(e);
		//ReikaJavaLibrary.pConsole("added emitter, "+c+" from "+sources);
		if (c != null) {
			for (TileEntityFiberReceiver r : c) {
				r.addTerminus(te);
			}
		}
		//ReikaJavaLibrary.pConsole(te, Side.SERVER);
	}

	private void addReceiver(TileEntityFiberReceiver te, CrystalElement e) {
		if (e != null) {
			Collection<TileEntityFiberReceiver> c = sources.get(e);
			if (c == null) {
				c = new ArrayList();
				sources.put(e, c);
			}
			c.add(te);
		}
		te.setNetwork(this);
		Collection<TileEntityFiberTransmitter> c = sinks.get(e);
		//ReikaJavaLibrary.pConsole("added receiver, "+c+" from "+sinks);
		if (c != null) {
			for (TileEntityFiberTransmitter r : c) {
				te.addTerminus(r);
			}
		}
		//ReikaJavaLibrary.pConsole(te, Side.SERVER);
	}

	private void clear(boolean clearTiles) {
		if (clearTiles) {
			for (TileEntityFiberOptic te : wires) {
				te.resetNetwork();
			}
			for (Collection<TileEntityFiberTransmitter> c : sinks.values()) {
				for (TileEntityFiberTransmitter emitter : c) {
					emitter.setNetwork(null);
				}
			}
			for (Collection<TileEntityFiberReceiver> c : sources.values()) {
				for (TileEntityFiberReceiver source : c) {
					source.setNetwork(null);
				}
			}
		}

		wires.clear();
		sinks.clear();
		sources.clear();
		//paths.clear();
	}

	public void killChannel(CrystalElement e) {/*
		Iterator<FiberPath> it = paths.iterator();
		while (it.hasNext()) {
			FiberPath p = it.next();
			if (p.color == e) {
				it.remove();
				p.breakPath();
			}
		}*/
	}

	public int distribute(FiberSource s, CrystalElement e, int energy) {
		int dist = 0;
		Collection<TileEntityFiberTransmitter> c = sinks.get(e);
		if (c != null) {
			for (TileEntityFiberTransmitter te : c) {
				int add = te.dumpEnergy(e, energy-dist);
				if (add > 0) {
					s.onTransmitTo(te, e, energy);
					dist += add;
					if (dist >= energy)
						break;
				}
			}
		}
		return dist;
	}

	public void onTileChangeColor(FiberIO te, CrystalElement newcolor) {
		this.removeTerminus(te);
		this.addIO(te, newcolor);
	}

	@Override
	public String toString() {
		return this.hashCode()+": "+sources.toString()+" > "+sinks.toString();
	}

	public FiberPath getPathBetween(TileEntityFiberReceiver fr, TileEntityFiberTransmitter ft) {
		LinkedList<TileEntityChromaticBase> li = new LinkedList();
		li.addLast(fr);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			TileEntity te = fr.getAdjacentTileEntity(dir);
			if (te == ft) {
				li.addLast((TileEntityChromaticBase)te);
				break;
			}
			else if (te instanceof TileEntityFiberOptic) {
				this.recurse(fr, ft, (TileEntityFiberOptic)te, li);
			}
			if (this.isComplete(li, fr, ft))
				break;
		}
		//ReikaJavaLibrary.pConsole(li, Side.SERVER);
		return this.isComplete(li, fr, ft) ? new FiberPath(fr.getColor(), li) : null;
	}

	private boolean isComplete(LinkedList<TileEntityChromaticBase> li, TileEntityFiberReceiver fr, TileEntityFiberTransmitter ft) {
		return li.getFirst() == fr && li.getLast() == ft;
	}

	private void recurse(TileEntityFiberReceiver fr, TileEntityFiberTransmitter ft, TileEntityFiberOptic te, LinkedList<TileEntityChromaticBase> li) {
		if (li.contains(te))
			return;
		if (this.isComplete(li, fr, ft))
			return;
		li.addLast(te);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			TileEntity tile = te.getAdjacentTileEntity(dir);
			if (tile == ft) {
				li.addLast((TileEntityChromaticBase)tile);
				return;
			}
			else if (tile instanceof TileEntityFiberOptic) {
				this.recurse(fr, ft, (TileEntityFiberOptic)tile, li);
			}
			if (this.isComplete(li, fr, ft))
				return;
		}
		li.removeLast();
	}

}
