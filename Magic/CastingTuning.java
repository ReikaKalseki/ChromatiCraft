package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.FanDirections;

public class CastingTuning {

	public static final CastingTuning instance = new CastingTuning();

	private final HashMap<FanDirections, Coordinate> tuningKeys = new HashMap();
	private final PlayerMap<TuningKey> data = new PlayerMap();
	private final Random rand = new Random();

	private CastingTuning() {
		for (FanDirections dir : FanDirections.list) {
			if (!dir.isCardinal()) {
				Coordinate c = new Coordinate(dir.directionX*3, 0, dir.directionZ*3);
				tuningKeys.put(dir, c);
			}
		}
	}

	public TuningKey getTuningKey(EntityPlayer ep) {
		TuningKey ret = data.get(ep);
		if (ret == null) {
			ret = this.calculateTuningKey(ep);
			data.put(ep, ret);
		}
		return ret;
	}

	private TuningKey calculateTuningKey(EntityPlayer ep) {
		TuningKey tk = new TuningKey(ep);
		long seed = ep.getUniqueID().getLeastSignificantBits() ^ ep.getUniqueID().getLeastSignificantBits();
		if (ep.capabilities.isCreativeMode)
			seed ^= ep.getUniqueID().hashCode();
		this.seed(seed);
		int n = 12;//8+rand.nextInt(5);
		int i = 0;
		ArrayList<Coordinate> li = new ArrayList(tuningKeys.values());
		Collections.sort(li);
		Collections.shuffle(li, rand);
		for (Coordinate c : li) {
			tk.runes.put(c, CrystalElement.elements[rand.nextInt(16)]);
			i++;
			if (i >= n)
				break;
		}
		return tk;
	}

	private void seed(long seed) {
		rand.setSeed(seed);
		rand.nextBoolean();
		rand.nextBoolean();
	}

	public Collection<Coordinate> getTuningKeyLocations() {
		return Collections.unmodifiableCollection(tuningKeys.values());
	}

	public static class TuningKey {

		private final HashMap<Coordinate, CrystalElement> runes = new HashMap();

		public final UUID uid;

		private TuningKey(EntityPlayer ep) {
			uid = ep.getUniqueID();
		}

		public Map<Coordinate, CrystalElement> getRunes() {
			return Collections.unmodifiableMap(runes);
		}

		public boolean check(TileEntityCastingTable te) {
			return runes.equals(te.getCurrentTuningMap());
		}

		public HashMap<FanDirections, CrystalElement> getCompass() {
			HashMap<FanDirections, CrystalElement> ret = new HashMap();
			for (Entry<FanDirections, Coordinate> e : instance.tuningKeys.entrySet()) {
				ret.put(e.getKey(), runes.get(e.getValue()));
			}
			return ret;
		}

	}
}
