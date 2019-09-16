package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.FanDirections;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

		private static final String ICON_SHEET = "Textures/cast_tuning_icons.png";
		private static final int ICON_COLS = 4;
		private static final int ICON_ROWS = 4;

		private final HashMap<Coordinate, CrystalElement> runes = new HashMap();

		public final UUID uid;
		public final int iconIndex;

		private final double textureU;
		private final double textureV;
		private final double textureDU;
		private final double textureDV;

		private TuningKey(UUID uid) {
			this.uid = uid;
			if (this.uid.equals(DragonAPICore.Reika_UUID)) {
				iconIndex = 12;
			}
			else {
				int s = ICON_COLS*ICON_ROWS-1;
				int val = ((uid.hashCode()%s)+s)%s;
				if (val >= 12)
					val++;
				iconIndex = val;
			}

			int col = iconIndex%ICON_COLS;
			int row = iconIndex/ICON_COLS;
			textureU = col/(float)ICON_COLS;
			textureV = row/(float)ICON_ROWS;
			textureDU = textureU+1F/ICON_COLS;
			textureDV = textureV+1F/ICON_ROWS;
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

		@SideOnly(Side.CLIENT)
		public void drawIconInGUI(Tessellator v5, double s, double z, int color) {
			this.initGL(v5, color);
			v5.addVertexWithUV(0, s, z, textureU, 	textureDV);
			v5.addVertexWithUV(s, s, z, textureDU, textureDV);
			v5.addVertexWithUV(s, 0, z, textureDU, textureV);
			v5.addVertexWithUV(0, 0, z, textureU, 	textureV);
			v5.draw();
			GL11.glPopAttrib();
		}

		@SideOnly(Side.CLIENT)
		public void drawIcon(Tessellator v5, double s, int color) {
			this.initGL(v5, color);
			v5.addVertexWithUV(-s, -s, 0, textureU, textureV);
			v5.addVertexWithUV(s, -s, 0, textureDU, textureV);
			v5.addVertexWithUV(s, s, 0, textureDU, textureDV);
			v5.addVertexWithUV(-s, s, 0, textureU, textureDV);
			v5.draw();
			GL11.glPopAttrib();
		}

		private void initGL(Tessellator v5, int color) {
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, ICON_SHEET);
			//GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDepthMask(false);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(color);
		}

	}
}
