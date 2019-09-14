package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;

public class CastingTuning {

	public static final CastingTuning instance = new CastingTuning();

	private final PlayerMap<TuningKey> data = new PlayerMap();
	private final Random rand = new Random();

	private CastingTuning() {

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
		ArrayList<Coordinate> li = new ArrayList(TileEntityCastingTable.getTuningKeys());
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

	}
}
