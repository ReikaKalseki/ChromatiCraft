package Reika.ChromatiCraft.Auxiliary.Render;

import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.MonumentCompletionRitual;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterDestructionRitual;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterRitualClientEffects;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterRitualClientEffects.EffectVisual;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.WorldShaderHandler;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WorldRenderIntercept implements WorldShaderHandler {

	public static final WorldRenderIntercept instance = new WorldRenderIntercept();

	private HashMap<Integer, Coordinate> listMap = new HashMap();

	private WorldRenderIntercept() {
		ShaderRegistry.registerWorldShaderSystem(this);
	}

	public DragonAPIMod getMod() {
		return ChromatiCraft.instance;
	}

	public void mapChunkRenderList(int id, WorldRenderer wr) {
		listMap.put(id, new Coordinate(wr));
	}

	public Coordinate getRenderChunkForList(int id) {
		return listMap.get(id);
	}

	public static void callGlLists(IntBuffer lists) {
		if (MonumentCompletionRitual.areRitualsRunning() || ModList.VOIDMONSTER.isLoaded()) {
			instance.runIntercept(lists);
		}
		else {
			GL11.glCallLists(lists);
		}
	}

	public void onPreWorldRender() {

	}

	public void onPostWorldRender() {
		listMap.clear();
	}

	//@ModDependent(ModList.VOIDMONSTER)
	private void runIntercept(IntBuffer lists) {
		float f = 0;
		ChromaShaders s = null;
		if (VoidMonsterDestructionRitual.ritualsActive()) {
			s = ChromaShaders.VOIDRITUAL$WORLD;
			for (EffectVisual e : VoidMonsterRitualClientEffects.EffectVisual.getTerrainShaders()) {
				float f2 = e.getShaderIntensity();
				f = Math.max(f, f2);
			}
		}
		else if (MonumentCompletionRitual.areRitualsRunning()) {
			f = 1;
			s = ChromaShaders.MONUMENT$CHORDS;
			MonumentCompletionRitual.addShaderData(s.getShader());
		}
		while (lists.remaining() > 0) {
			int id = lists.get();
			if (f > 0) {
				s.setIntensity(f);
				s.getShader().updateEnabled();
				Coordinate c = instance.getRenderChunkForList(id);
				//ReikaJavaLibrary.pConsole("Running GL list # "+id+" which maps to chunk "+c);
				if (c != null) {
					s.getShader().setField("chunkX", c.xCoord);
					s.getShader().setField("chunkY", c.yCoord);
					s.getShader().setField("chunkZ", c.zCoord);
				}
				s.getShader().setTextureUnit("bgl_LightMapTexture", OpenGlHelper.lightmapTexUnit);
				ShaderRegistry.runShader(s.getShader());
			}
			GL11.glCallList(id);
			if (f > 0)
				ShaderRegistry.completeShader();
		}
	}

}
