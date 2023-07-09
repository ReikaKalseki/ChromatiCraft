/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator.StructureGenStatus;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Interfaces.Item.SpriteRenderCallback;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemStructureMap extends ItemChromaTool implements SpriteRenderCallback {

	private static final String TAG_NAME = "mapdata";

	private static final PlayerMap<StructureSearch> activeSearches = new PlayerMap();

	public ItemStructureMap(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (!world.isRemote) {
			if (ep.isSneaking()) {
				if (activeSearches.containsKey(ep)) {
					ReikaChatHelper.sendChatToPlayer(ep, "Another scan is in progress.");
				}
				else if (is.stackTagCompound != null && (is.stackTagCompound.hasKey(TAG_NAME) || is.stackTagCompound.getBoolean("scanning") || is.stackTagCompound.getBoolean("searcher"))) {
					ReikaChatHelper.sendChatToPlayer(ep, "This map is already scanning the world.");
				}
				else {
					StructureSearchType s = StructureSearchType.list[is.getItemDamage()];
					int x0 = MathHelper.floor_double(ep.posX);
					int z0 = MathHelper.floor_double(ep.posZ);
					if (is.stackTagCompound == null)
						is.stackTagCompound = new NBTTagCompound();
					is.stackTagCompound.setBoolean("scanning", true);
					is.stackTagCompound.setString("searcher", ep.getUniqueID().toString());
					activeSearches.put(ep, s.beginSearch((WorldServer)world, x0, z0));
				}
			}
			else {
				boolean flag = true;
				while (flag) {
					is.setItemDamage((is.getItemDamage()+1)%StructureSearchType.list.length);
					flag = StructureSearchType.list[is.getItemDamage()].skip();
				}
			}
		}
		return is;
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (!world.isRemote && held && is.stackTagCompound != null && is.stackTagCompound.getBoolean("scanning") && is.stackTagCompound.hasKey("searcher")) {
			UUID uid = UUID.fromString(is.stackTagCompound.getString("searcher"));
			StructureSearch s = activeSearches.directGet(uid);
			if (s == null) {
				activeSearches.directRemove(uid);
			}
			else {
				StructureMapData data = s.tick((WorldServer)world);
				if (data != null) {
					is.stackTagCompound.setTag(TAG_NAME, data.writeToNBT());
					is.stackTagCompound.removeTag("scanning");
					activeSearches.directRemove(uid);
					ChromaSounds.CRAFTDONE.playSound(e);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		li.add("Seeking "+StructureSearchType.list[is.getItemDamage()].displayName());
		if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("scanning")) {
			li.add(String.format("Map is scanning: %.1f%% complete.", activeSearches.directGet(UUID.fromString(is.stackTagCompound.getString("searcher"))).getProgress()));
		}
		else if (is.stackTagCompound != null && is.stackTagCompound.hasKey(TAG_NAME)) {
			if (GuiScreen.isCtrlKeyDown()) {
				StructureMapData map = StructureMapData.readFromNBT(is.stackTagCompound.getCompoundTag(TAG_NAME));
				map.addTooltip(li);
			}
			else {
				li.add("Map is populated.");
			}
		}
		else {
			li.add("Map is empty.");
		}
	}

	public boolean doPreGLTransforms(ItemStack is, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static void renderMap(ItemStack item, float ptick, EntityPlayer ep) {
		if (item.stackTagCompound == null || !item.stackTagCompound.hasKey(TAG_NAME))
			return;
		StructureMapData data = StructureMapData.readFromNBT(item.stackTagCompound.getCompoundTag(TAG_NAME));
		ReikaRenderHelper.disableEntityLighting();
		ReikaRenderHelper.disableLighting();
		Tessellator v5 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(1, 1, 1, 1);
		BlendMode.DEFAULT.apply();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		StructureSearchType type = StructureSearchType.list[item.getItemDamage()];
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/StructureMap/overlay_typed.png");
		double ou = (type.ordinal()%4)/4D;
		double ov = (type.ordinal()/4)/4D;
		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
		byte b0 = 7;
		double rz = -0.01;
		/*
		int px = MathHelper.floor_double(ep.posX+(ep.lastTickPosX-ep.posX)*ptick);
		int pz = MathHelper.floor_double(ep.posZ+(ep.lastTickPosZ-ep.posZ)*ptick);
		 */
		int px = data.originX;
		int pz = data.originZ;
		int max = type.getSearchRange();
		if (max == -1) {
			px = data.bounds.getCenterX();
			pz = data.bounds.getCenterZ();
			Coordinate far = data.bounds.getFarthestPointFrom(px, data.bounds.getCenterY(), pz);
			max = Math.max(Math.abs(far.xCoord-px), Math.abs(far.zCoord-pz));
		}
		v5.addVertexWithUV(0 - b0, 128 + b0, rz, ou, ov+0.25);
		v5.addVertexWithUV(128 + b0, 128 + b0, rz, ou+0.25, ov+0.25);
		v5.addVertexWithUV(128 + b0, 0 - b0, rz, ou+0.25, ov);
		v5.addVertexWithUV(0 - b0, 0 - b0, rz, ou, ov);
		v5.draw();
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		int d = 12;
		//GL11.glPointSize(2);
		GL11.glDepthMask(false);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/StructureMap/marker.png");
		//v5.startDrawing(GL11.GL_POINTS);
		v5.startDrawingQuads();
		v5.setBrightness(240);
		double sc = 2;
		for (Entry<Coordinate, StructurePresence> e : data.map.entrySet()) {
			Coordinate loc = e.getKey();
			StructurePresence s = e.getValue();
			int x = (int)Math.round((loc.xCoord-px)*40D/max+64);
			int z = (int)Math.round((loc.zCoord-pz)*40D/max+64);
			v5.setColorOpaque_I(s.renderColor);
			//v5.addVertex(x, z, rz*2);
			v5.addVertexWithUV(x-sc, z+sc, rz*2, 0, 1);
			v5.addVertexWithUV(x+sc, z+sc, rz*2, 1, 1);
			v5.addVertexWithUV(x+sc, z-sc, rz*2, 1, 0);
			v5.addVertexWithUV(x-sc, z-sc, rz*2, 0, 0);
		}
		v5.draw();
	}

	public static enum StructureSearchType {

		STRONGHOLD(),
		TOWERS(),
		CAVERN(ChromaStructures.CAVERN),
		BURROW(ChromaStructures.BURROW),
		OCEAN(ChromaStructures.OCEAN),
		DESERT(ChromaStructures.DESERT),
		SNOWSTRUCT(ChromaStructures.SNOWSTRUCT),
		MINESHAFT(),
		BIOME(ChromaStructures.BIOMEFRAG),
		;

		private static final StructureSearchType[] list = values();

		private final ChromaStructures structure;

		private StructureSearchType() {
			this(null);
		}

		private StructureSearch beginSearch(WorldServer world, int x0, int z0) {
			return new StructureSearch(this, x0, z0);
		}

		private boolean skip() {
			return this == MINESHAFT;
		}

		public int getSearchRange() {
			if (structure != null) {
				return 128*16;
			}
			switch(this) {
				case STRONGHOLD:
				case TOWERS:
					return -1;
				default:
					return 1;
			}
		}

		private StructureSearchType(ChromaStructures s) {
			structure = s;
		}

		public String displayName() {
			if (this == TOWERS)
				return ChromaFontRenderer.FontType.OBFUSCATED.id+"GlowingTowers";
			if (structure != null) {
				return structure.getDisplayName();
			}
			return ReikaStringParser.capFirstChar(this.name());
		}
	}

	private static class StructureSearch {

		private static final int RANGE = 128;

		private final StructureSearchType type;
		private final int originX;
		private final int originZ;

		private int scanI = -RANGE;
		private int scanK = -RANGE;

		private int scannedChunks = 0;

		private final HashMap<Coordinate, StructurePresence> foundStructures = new HashMap();

		private StructureSearch(StructureSearchType t, int x, int z) {
			type = t;
			originX = x;
			originZ = z;
		}

		private boolean updateScanChunk() {
			boolean flag1 = false;
			scanI++;
			if (scanI > RANGE) {
				scanI = -RANGE;
				flag1 = true;
			}
			if (flag1) {
				scanK++;
				if (scanK > RANGE) {
					scanK = -RANGE;
					return true;
				}
			}
			//ReikaJavaLibrary.pConsole(scanI+","+scanK);
			return false;
		}

		public float getProgress() {
			int r = RANGE*2+1;
			return scannedChunks*100F/(r*r);
		}

		/** Return coords are absolute, not relative */
		private StructureMapData tick(WorldServer world) {
			int cc = 0;
			BlockBox bounds = BlockBox.nothing();
			if (type.structure != null) {
				for (int i = 0; i < 24; i++) {
					int rx = originX+scanI*16;
					int rz = originZ+scanK*16;
					StructureGenStatus at = DungeonGenerator.instance.getGenStatus(type.structure, world, rx, rz);
					if (at.hasStructure()) {
						Coordinate c = new Coordinate(rx, 0, rz);
						boolean cf = at.isFinalized();
						if (cf)
							cc++;
						bounds = bounds.addCoordinate(c.xCoord, c.yCoord, c.zCoord);
						foundStructures.put(c, cf ? StructurePresence.CONFIRMED : StructurePresence.EXPECTED);
					}
					scannedChunks++;
					if (this.updateScanChunk())
						return new StructureMapData(foundStructures, originX, originZ, cc, bounds);
				}
				return null;
			}
			else {
				switch(type) {
					case STRONGHOLD:
						MapGenStronghold mg = ((ChunkProviderGenerate)world.theChunkProviderServer.currentChunkProvider).strongholdGenerator;
						Field f = ReikaReflectionHelper.getProtectedInheritedField(mg, FMLForgePlugin.RUNTIME_DEOBF ? "field_75039_c" : "worldObj");
						f.setAccessible(true);
						try {
							f.set(mg, world);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						ReikaObfuscationHelper.invoke("canSpawnStructureAtCoords", mg, 0, 0); //populate the map
						List li0 = (List)ReikaObfuscationHelper.invoke("getCoordList", mg);
						if (li0 != null) {
							for (Object o : li0) {
								Coordinate c = new Coordinate((ChunkPosition)o).to2D();
								bounds = bounds.addCoordinate(c.xCoord, c.yCoord, c.zCoord);
								foundStructures.put(c, StructurePresence.EXPECTED);
							}
						}
						for (StructureStart ss : ((Map<Long, StructureStart>)mg.structureMap).values()) {
							if (ss.isSizeableStructure()) {
								StructureBoundingBox sbb = ss.getBoundingBox();
								if (sbb != null) {
									Coordinate c = new Coordinate(sbb.getCenterX(), 0, sbb.getCenterZ());
									bounds = bounds.addCoordinate(c.xCoord, c.yCoord, c.zCoord);
									foundStructures.put(c, StructurePresence.CONFIRMED);
									cc++;
								}
							}
						}
						break;
					case TOWERS:
						for (Towers t : Towers.towerList) {
							Coordinate c = t.getGeneratedLocation();
							if (c == null)
								c = new Coordinate(t.getRootPosition().chunkXPos, 0, t.getRootPosition().chunkZPos);
							bounds = bounds.addCoordinate(c.xCoord, c.yCoord, c.zCoord);
							boolean cf = ReikaWorldHelper.isChunkGenerated(world, c.xCoord, c.zCoord);
							if (cf)
								cc++;
							foundStructures.put(c, cf ? StructurePresence.CONFIRMED : StructurePresence.EXPECTED);
						}
						break;
					default:
						break;
				}
				return new StructureMapData(foundStructures, originX, originZ, cc, bounds);
			}
		}

	}

	private static class StructureMapData {

		private final HashMap<Coordinate, StructurePresence> map;
		private final int originX;
		private final int originZ;
		private final BlockBox bounds;
		private final int confirmedCount;

		private StructureMapData(HashMap<Coordinate, StructurePresence> data, int x, int z, int cc, BlockBox box) {
			map = data;
			originX = x;
			originZ = z;
			confirmedCount = cc;
			bounds = box;
		}

		public void addTooltip(List li) {
			li.add("Confirmed "+confirmedCount+"/"+map.size()+" locations.");
		}

		public static StructureMapData readFromNBT(NBTTagCompound data) {
			HashMap<Coordinate, StructurePresence> values = new HashMap();
			NBTTagList li = data.getTagList("mapdata", NBTTypes.COMPOUND.ordinal());
			int cc = 0;
			for (Object o : li.tagList) {
				NBTTagCompound tag = (NBTTagCompound)o;
				Coordinate loc = Coordinate.readFromNBT("loc", tag);
				StructurePresence s = StructurePresence.valueOf(tag.getString("state"));
				if (s == StructurePresence.CONFIRMED)
					cc++;
				values.put(loc, s);
			}

			NBTTagCompound meta = data.getCompoundTag("metadata");
			return new StructureMapData(values, meta.getInteger("rootX"), meta.getInteger("rootZ"), cc, BlockBox.readFromNBT(meta));
		}

		private NBTTagCompound writeToNBT() {
			NBTTagCompound ret = new NBTTagCompound();
			NBTTagList tag = new NBTTagList();
			for (Entry<Coordinate, StructurePresence> e : map.entrySet()) {
				NBTTagCompound val = new NBTTagCompound();
				e.getKey().writeToNBT("loc", val);
				val.setString("state", e.getValue().name());
				tag.appendTag(val);
			}

			NBTTagCompound meta = new NBTTagCompound();
			bounds.writeToNBT(meta);
			meta.setInteger("rootX", originX);
			meta.setInteger("rootZ", originZ);

			ret.setTag("mapdata", tag);
			ret.setTag("metadata", meta);

			return ret;
		}

	}

	private static enum StructurePresence {
		EXPECTED(0xffff00),
		CONFIRMED(0x00ff00),
		FAILED(0xff0000);

		public final int renderColor;

		private StructurePresence(int c) {
			renderColor = c;
		}
	}

}
