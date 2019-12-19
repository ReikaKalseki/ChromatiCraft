package Reika.ChromatiCraft.Auxiliary.Render;

import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;

import Reika.ChromatiCraft.ModInterface.VoidMonsterDestructionRitual;
import Reika.ChromatiCraft.ModInterface.VoidMonsterDestructionRitual.Effects;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry;
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
		float f = 0;
		if (VoidMonsterDestructionRitual.ritualsActive()) {
			for (Effects e : Effects.getTerrainShaders()) {
				float f2 = e.getShader().getIntensity();
				f = Math.max(f, f2);
				if (f2 > 0) {
					ChromaShaders.VOIDRITUAL$WORLD.getShader().applyShaderData(e.getShader());
				}
			}
		}
		while (lists.remaining() > 0) {
			int id = lists.get();
			if (f > 0) {
				ChromaShaders.VOIDRITUAL$WORLD.setIntensity(f);
				ChromaShaders.VOIDRITUAL$WORLD.getShader().updateEnabled();
				Coordinate c = instance.getRenderChunkForList(id);
				//ReikaJavaLibrary.pConsole("Running GL list # "+id+" which maps to chunk "+c);
				if (c != null) {
					ChromaShaders.VOIDRITUAL$WORLD.getShader().setField("chunkX", c.xCoord);
					ChromaShaders.VOIDRITUAL$WORLD.getShader().setField("chunkY", c.yCoord);
					ChromaShaders.VOIDRITUAL$WORLD.getShader().setField("chunkZ", c.zCoord);
				}
				ChromaShaders.VOIDRITUAL$WORLD.getShader().setTextureUnit("bgl_LightMapTexture", OpenGlHelper.lightmapTexUnit);
				ShaderRegistry.runShader(ChromaShaders.VOIDRITUAL$WORLD.getShader());
			}
			GL11.glCallList(id);
			if (f > 0)
				ShaderRegistry.completeShader();
		}
	}

}
