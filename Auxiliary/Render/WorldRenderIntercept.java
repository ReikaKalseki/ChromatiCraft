package Reika.ChromatiCraft.Auxiliary.Render;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.MonumentCompletionRitual;
import Reika.ChromatiCraft.Magic.Artefact.ArtefactSpawner;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterDestructionRitual;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterRitualClientEffects;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterRitualClientEffects.EffectVisual;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.WorldShaderSystem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.map.hash.THashMap;

@SideOnly(Side.CLIENT)
public class WorldRenderIntercept implements WorldShaderSystem {

	public static final WorldRenderIntercept instance = new WorldRenderIntercept();

	private static final int DEFAULT_RADIUS = 10;
	private static final int DEFAULT_CAPACITY_COMPONENT = 2*DEFAULT_RADIUS+1;

	private final THashMap<Integer, WorldRenderer> listMap = new THashMap(16*DEFAULT_CAPACITY_COMPONENT*DEFAULT_CAPACITY_COMPONENT, 0.98F);

	private WorldRenderIntercept() {
		ShaderRegistry.registerWorldShaderSystem(this);
	}

	public DragonAPIMod getMod() {
		return ChromatiCraft.instance;
	}

	public void mapChunkRenderList(int id, WorldRenderer wr) {
		listMap.put(id, wr);
	}

	public boolean apply(IntBuffer lists) {
		if (MonumentCompletionRitual.areRitualsRunning() || ModList.VOIDMONSTER.isLoaded()) {
			instance.runIntercept(lists);
			return true;
		}
		return false;
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
		else {
			s = ChromaShaders.UAZONE;
			ChromaShaders.UAZONE.refresh();
			if (ArtefactSpawner.isShaderActive(Minecraft.getMinecraft().theWorld))
				ChromaShaders.UAZONE.rampUpIntensity(0.04F, 1.05F);
			ChromaShaders.UAZONE.lingerTime = 0;
			ChromaShaders.UAZONE.rampDownAmount = 0.004F;
			ChromaShaders.UAZONE.rampDownFactor = 0.997F;
			f = ChromaShaders.UAZONE.getIntensity();
		}
		while (lists.remaining() > 0) {
			int id = lists.get();
			if (f > 0) {
				s.setIntensity(f);
				s.getShader().updateEnabled();
				WorldRenderer c = listMap.get(id);
				//ReikaJavaLibrary.pConsole("Running GL list # "+id+" which maps to chunk "+c);
				if (c != null) {
					s.getShader().setField("chunkX", c.posX);
					s.getShader().setField("chunkY", c.posY);
					s.getShader().setField("chunkZ", c.posZ);
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
