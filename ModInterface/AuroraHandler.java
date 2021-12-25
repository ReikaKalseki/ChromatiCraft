package Reika.ChromatiCraft.ModInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.BasicModEntry;
import Reika.DragonAPI.Instantiable.IO.PacketTarget.DimensionTarget;
import Reika.DragonAPI.Interfaces.Registry.ModEntry;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AuroraHandler {

	public static final AuroraHandler instance = new AuroraHandler();

	private Class network;

	private Class aurora;
	private Class handler;
	private Class renderer;

	private Class dimData;

	private Method getDataForDim;
	private Field auroraeServer;

	private Field aurorae;
	private Field current;

	private Field baseColor;
	private Field fadeColor;
	private Method die;

	private Class data;

	private Class colors;
	private Class presets;

	private Class color;

	private Constructor dataCtr;

	private Constructor colorPairCtr;
	private Constructor colorCtr;

	private Constructor auroraCtr;

	private Method sendAurora;

	private Method randomColor;
	private Method randomPreset;

	private Method render;

	private Field colorList;

	private AuroraHandler() {
		ModEntry mod = new BasicModEntry("dsurround");
		if (mod.isLoaded()) {
			try {
				network = Class.forName("org.blockartistry.mod.DynSurround.network.Network");

				data = Class.forName("org.blockartistry.mod.DynSurround.data.AuroraData");

				dimData = Class.forName("org.blockartistry.mod.DynSurround.data.DimensionEffectData");

				colors = Class.forName("org.blockartistry.mod.DynSurround.data.ColorPair");
				presets = Class.forName("org.blockartistry.mod.DynSurround.data.AuroraPreset");

				color = Class.forName("org.blockartistry.mod.DynSurround.util.Color");

				dataCtr = data.getDeclaredConstructor(int.class, int.class, int.class, long.class, int.class, int.class);
				dataCtr.setAccessible(true);

				colorPairCtr = colors.getDeclaredConstructor(color, color);
				colorPairCtr.setAccessible(true);

				colorCtr = color.getDeclaredConstructor(int.class, int.class, int.class);
				colorCtr.setAccessible(true);

				randomColor = colors.getDeclaredMethod("randomId");
				randomPreset = presets.getDeclaredMethod("randomId");

				getDataForDim = dimData.getDeclaredMethod("get", World.class);
				getDataForDim.setAccessible(true);

				auroraeServer = dimData.getDeclaredField("auroras");
				auroraeServer.setAccessible(true);

				colorList = colors.getDeclaredField("PAIRS");
				colorList.setAccessible(true);

				sendAurora = network.getDeclaredMethod("sendAurora", data, int.class);

				if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT || DragonAPICore.isSinglePlayer()) {
					aurora = Class.forName("org.blockartistry.mod.DynSurround.client.aurora.Aurora");
					handler = Class.forName("org.blockartistry.mod.DynSurround.client.AuroraEffectHandler");
					renderer = Class.forName("org.blockartistry.mod.DynSurround.client.aurora.AuroraRenderer");

					aurorae = handler.getDeclaredField("auroras");
					aurorae.setAccessible(true);
					current = handler.getDeclaredField("currentAurora");
					current.setAccessible(true);

					baseColor = aurora.getDeclaredField("baseColor");
					baseColor.setAccessible(true);
					fadeColor = aurora.getDeclaredField("fadeColor");
					fadeColor.setAccessible(true);
					die = aurora.getDeclaredMethod("die");
					die.setAccessible(true);

					render = renderer.getDeclaredMethod("renderAurora", float.class, aurora);
					render.setAccessible(true);

					auroraCtr = aurora.getDeclaredConstructor(data);
					auroraCtr.setAccessible(true);
				}

				for (int i = 0; i < 16; i++) {
					int c = CrystalElement.elements[i].getColor();
					//this.addColorPair(c, c);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				ChromatiCraft.logger.logError("Could not load DynSurround aurora hooks!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(mod, e);
			}
		}
	}

	public void sendClear(World world) throws Exception {
		Set s = this.getServersideAurorae(world);
		s.clear();
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.AURORACLEAR.ordinal(), new DimensionTarget(world));
	}

	public void sendRecolor(World world) throws Exception {
		//NOOP
	}

	public void sendNewAurora(World world, Object data) throws Exception {
		if (data != null)
			sendAurora.invoke(null, data, world.provider.dimensionId);
	}

	public void addColorPair(int base, int fade) throws Exception {
		List li = (List)colorList.get(null);
		li.add(this.createColorPair(base, fade));
	}

	@SideOnly(Side.CLIENT)
	public void clear() throws Exception {
		/*
		Set s = (Set)aurorae.get(null);
		s.clear();
		current.set(null, null);
		 */
		this.killAurora(current.get(null));
	}

	public Object addAurora(World world, int x, int z) throws Exception {
		return this.addAurora(world, x, z, this.getRandomColorID(), this.getRandomPresetID());
	}

	public Object addAurora(World world, int x, int z, int colors, int preset) throws Exception {
		Object data = this.constructData(world, x, z, colors, preset);
		Set s = this.getServersideAurorae(world);
		s.add(data);
		return data;
	}

	@SideOnly(Side.CLIENT)
	public Object getCurrentAurora() throws Exception {
		return current.get(null);
	}

	public Set getServersideAurorae(World world) throws Exception {
		Object dimData = getDataForDim.invoke(null, world);
		return (Set)auroraeServer.get(dimData);
	}

	@SideOnly(Side.CLIENT)
	public void colorizeAurora(Object aurora, int base, int fade) throws Exception {
		baseColor.set(aurora, this.createColor(base));
		fadeColor.set(aurora, this.createColor(fade));
	}

	@SideOnly(Side.CLIENT)
	public void killAurora(Object aurora) throws Exception {
		if (aurora != null) {
			die.invoke(aurora);
		}
	}

	private Object createColorPair(int rgbb, int rgbf) throws Exception {
		return colorPairCtr.newInstance(this.createColor(rgbb), this.createColor(rgbf));
	}

	private Object createColor(int rgb) throws Exception {
		int red = ReikaColorAPI.getRed(rgb);
		int green = ReikaColorAPI.getGreen(rgb);
		int blue = ReikaColorAPI.getBlue(rgb);
		return colorCtr.newInstance(red, green, blue);
	}

	public int getRandomColorID() throws Exception {
		return (int)randomColor.invoke(null);
	}

	public int getRandomPresetID() throws Exception {
		return (int)randomPreset.invoke(null);
	}

	public Object constructData(World world, int x, int z, int colors, int preset) throws Exception {
		int dim = world.provider.dimensionId;
		long seed = world.getWorldTime();
		return dataCtr.newInstance(dim, x, z, seed, colors, preset);
	}

	@SideOnly(Side.CLIENT)
	public Object createAurora(Object data) throws Exception {
		return auroraCtr.newInstance(data);
	}

	@SideOnly(Side.CLIENT)
	public void renderAurora(Object aurora) throws Exception {
		render.invoke(null, ReikaRenderHelper.getPartialTickTime(), aurora);
	}

}
