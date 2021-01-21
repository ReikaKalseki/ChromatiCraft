package Reika.ChromatiCraft.Magic.CastingTuning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.FanDirections;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CastingTuningManager {

	public static final CastingTuningManager instance = new CastingTuningManager();

	public static final String NBT_KEY = "CASTTUNING";

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
		return this.getTuningKey(ep.worldObj, ep.getUniqueID());
	}

	public TuningKey getTuningKey(World world, UUID uid) {
		TuningKey ret = data.directGet(uid);
		if (ret == null) {
			ret = this.calculateTuningKey(world, uid);
			data.directPut(uid, ret);
		}
		return ret;
	}

	private TuningKey calculateTuningKey(World world, UUID ep) {
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
			long seed = this.generateTuningSeed(world, ep);
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

	private long generateTuningSeed(World world, UUID ep) {
		if (world.isRemote) {
			return this.fetchClientSeed();
		}
		else {
			long seed = ep.getMostSignificantBits() ^ ep.getLeastSignificantBits();
			seed = seed ^ ReikaWorldHelper.getCurrentWorldID(world).worldCreationTime;
			return seed;
		}
	}

	@SideOnly(Side.CLIENT)
	private long fetchClientSeed() {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		NBTTagCompound tag = ChromaResearchManager.instance.getRootNBTTag(ep);
		boolean flag = false;
		if (!tag.hasKey(NBT_KEY))
			flag = true;
		NBTTagCompound data = tag.getCompoundTag(NBT_KEY);
		if (!data.hasKey("rootSeed"))
			flag = true;
		if (flag) {
			ReikaPlayerAPI.requestCustomDataSyncFromClient(ep);
		}
		return data.getLong("rootSeed");
	}

	private void seed(long seed) {
		rand.setSeed(seed);
		rand.nextBoolean();
		rand.nextBoolean();
	}

	public Collection<Coordinate> getTuningKeyLocations() {
		return Collections.unmodifiableCollection(tuningKeys.values());
	}

	public void calculateAndCacheKey(EntityPlayer player) {
		TuningKey tk = this.getTuningKey(player);
		NBTTagCompound tag = ChromaResearchManager.instance.getRootNBTTag(player);
		NBTTagCompound data = tag.getCompoundTag(NBT_KEY);
		data.setLong("rootSeed", this.generateTuningSeed(player.worldObj, player.getUniqueID()));
		tag.setTag(NBT_KEY, data);
	}
}
