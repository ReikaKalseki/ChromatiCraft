package Reika.ChromatiCraft.Auxiliary.Render;

import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;

import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class WorldRenderIntercept {

	public static final WorldRenderIntercept instance = new WorldRenderIntercept();

	private HashMap<Integer, Coordinate> listMap = new HashMap();

	private WorldRenderIntercept() {

	}

	public void mapChunkRenderList(int id, WorldRenderer wr) {
		listMap.put(id, new Coordinate(wr));
	}

	public Coordinate getRenderChunkForList(int id) {
		return listMap.get(id);
	}

	public static void callGlLists(IntBuffer lists) {
		while (lists.remaining() > 0) {
			ChromaShaders.VOIDRITUAL$WAVE.setIntensity(1);
			ChromaShaders.VOIDRITUAL$WAVE.getShader().updateEnabled();
			int id = lists.get();
			Coordinate c = instance.getRenderChunkForList(id);
			//ReikaJavaLibrary.pConsole("Running GL list # "+id+" which maps to chunk "+c);
			if (c != null) {
				ChromaShaders.VOIDRITUAL$WAVE.getShader().setField("chunkX", c.xCoord);
				ChromaShaders.VOIDRITUAL$WAVE.getShader().setField("chunkY", c.yCoord);
				ChromaShaders.VOIDRITUAL$WAVE.getShader().setField("chunkZ", c.zCoord);
			}
			ChromaShaders.VOIDRITUAL$WAVE.getShader().setTextureUnit("bgl_LightMapTexture", OpenGlHelper.lightmapTexUnit);
			//ShaderRegistry.runShader(ChromaShaders.VOIDRITUAL$WAVE.getShader());
			GL11.glCallList(id);
			//ShaderRegistry.completeShader();
		}
	}

}
