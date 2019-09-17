package Reika.ChromatiCraft.Magic.CastingTuning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.FanDirections;

public class CastingTuningManager {

	public static final CastingTuningManager instance = new CastingTuningManager();

	final HashMap<FanDirections, Coordinate> tuningKeys = new HashMap();
	private final PlayerMap<TuningKey> data = new PlayerMap();
	private final Random rand = new Random();

	private CastingTuningManager() {
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
			tk.putRune(tuningKeys.get(FanDirections.WNW), CrystalElement.RED);
			tk.putRune(tuningKeys.get(FanDirections.NW), CrystalElement.BLACK);
			tk.putRune(tuningKeys.get(FanDirections.NNW), CrystalElement.BLUE);

			tk.putRune(tuningKeys.get(FanDirections.NNE), CrystalElement.BLACK);
			tk.putRune(tuningKeys.get(FanDirections.NE), CrystalElement.LIME);
			tk.putRune(tuningKeys.get(FanDirections.ENE), CrystalElement.YELLOW);

			tk.putRune(tuningKeys.get(FanDirections.ESE), CrystalElement.WHITE);
			tk.putRune(tuningKeys.get(FanDirections.SE), CrystalElement.LIGHTBLUE);
			tk.putRune(tuningKeys.get(FanDirections.SSE), CrystalElement.BLACK);

			tk.putRune(tuningKeys.get(FanDirections.SSW), CrystalElement.MAGENTA);
			tk.putRune(tuningKeys.get(FanDirections.SW), CrystalElement.PURPLE);
			tk.putRune(tuningKeys.get(FanDirections.WSW), CrystalElement.RED);
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
				tk.putRune(c, CrystalElement.elements[rand.nextInt(16)]);
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
}
