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
import Reika.DragonAPI.DragonAPICore;
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
				int n = dir.isOctagonal() ? 6 : 3;
				Coordinate c = new Coordinate(dir.directionX*n, 0, dir.directionZ*n);
				tuningKeys.put(dir, c);
			}
		}
	}

	public TuningKey getTuningKey(EntityPlayer ep) {
		return this.getTuningKey(ep.getUniqueID());
	}

	public TuningKey getTuningKey(UUID uid) {
		TuningKey ret = data.directGet(uid);
		if (ret == null) {
			ret = this.calculateTuningKey(uid);
			data.directPut(uid, ret);
		}
		return ret;
	}

	private TuningKey calculateTuningKey(UUID ep) {
		TuningKey tk = new TuningKey(ep);
		if (ep.equals(DragonAPICore.Reika_UUID)) {
			tk.runes.put(tuningKeys.get(FanDirections.WNW), CrystalElement.RED);
			tk.runes.put(tuningKeys.get(FanDirections.NW), CrystalElement.BLACK);
			tk.runes.put(tuningKeys.get(FanDirections.NNW), CrystalElement.BLUE);

			tk.runes.put(tuningKeys.get(FanDirections.NNE), CrystalElement.BLACK);
			tk.runes.put(tuningKeys.get(FanDirections.NE), CrystalElement.LIME);
			tk.runes.put(tuningKeys.get(FanDirections.ENE), CrystalElement.YELLOW);

			tk.runes.put(tuningKeys.get(FanDirections.ESE), CrystalElement.WHITE);
			tk.runes.put(tuningKeys.get(FanDirections.SE), CrystalElement.LIGHTBLUE);
			tk.runes.put(tuningKeys.get(FanDirections.SSE), CrystalElement.BLACK);

			tk.runes.put(tuningKeys.get(FanDirections.SSW), CrystalElement.MAGENTA);
			tk.runes.put(tuningKeys.get(FanDirections.SW), CrystalElement.PURPLE);
			tk.runes.put(tuningKeys.get(FanDirections.WSW), CrystalElement.RED);
		}
		else {
			long seed = ep.getLeastSignificantBits() ^ ep.getLeastSignificantBits();
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

		public static final String ICON_SHEET = "Textures/cast_tuning_icons.png";
		public static final int ICON_COLS = 4;
		public static final int ICON_ROWS = 4;

		private final HashMap<Coordinate, CrystalElement> runes = new HashMap();

		public final UUID uid;
		public final int iconIndex;

		private TuningKey(UUID uid) {
			this.uid = uid;
			int s = ICON_COLS*ICON_ROWS;
			iconIndex = ((uid.hashCode()%s)+s)%s;
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
