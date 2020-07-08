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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator.StructureGenStatus;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.Item.SpriteRenderCallback;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemStructureMap extends ItemChromaTool implements SpriteRenderCallback {

	private static final String TAG_NAME = "mapdata";

	public ItemStructureMap(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (!world.isRemote) {
			if (is.stackTagCompound == null || !is.stackTagCompound.hasKey(TAG_NAME)) {

			}
			else if (ep.isSneaking()) {
				is.setItemDamage((is.getItemDamage()+1)%StructureSearch.list.length);
			}
			else {
				StructureSearch s = StructureSearch.list[is.getItemDamage()];
				int x0 = MathHelper.floor_double(ep.posX);
				int z0 = MathHelper.floor_double(ep.posZ);
				HashMap<Coordinate, StructurePresence> data = s.getLocations((WorldServer)world, x0, z0);
				if (is.stackTagCompound == null)
					is.stackTagCompound = new NBTTagCompound();
				this.writeDataToItem(data, is);
				is.stackTagCompound.setInteger("rootX", x0);
				is.stackTagCompound.setInteger("rootZ", z0);
			}
		}
		return is;
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (held && is.stackTagCompound != null && is.stackTagCompound.hasKey(TAG_NAME)) {
			NBTTagList tag = is.stackTagCompound.getTagList(TAG_NAME, NBTTypes.COMPOUND.ID);

		}
	}

	private void writeDataToItem(HashMap<Coordinate, StructurePresence> data, ItemStack is) {
		NBTTagList tag = new NBTTagList();
		for (Entry<Coordinate, StructurePresence> e : data.entrySet()) {
			NBTTagCompound val = new NBTTagCompound();
			e.getKey().writeToNBT("loc", val);
			val.setString("state", e.getValue().name());
			tag.appendTag(val);
		}
		is.stackTagCompound.setTag("mapdata", tag);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		li.add("Seeking "+StructureSearch.list[is.getItemDamage()].displayName());
		if (is.stackTagCompound != null && is.stackTagCompound.hasKey(TAG_NAME)) {
			if (GuiScreen.isCtrlKeyDown()) {
				NBTTagList tag = is.stackTagCompound.getTagList(TAG_NAME, NBTTypes.COMPOUND.ID);
				li.add("Map has found "+tag.tagCount()+" structures.");
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
		ReikaRenderHelper.disableEntityLighting();
		ReikaRenderHelper.disableLighting();
		Tessellator v5 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(1, 1, 1, 1);
		BlendMode.DEFAULT.apply();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/structmapoverlay.png");
		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
		byte b0 = 7;
		double rz = -0.01;
		/*
		int px = MathHelper.floor_double(ep.posX+(ep.lastTickPosX-ep.posX)*ptick);
		int pz = MathHelper.floor_double(ep.posZ+(ep.lastTickPosZ-ep.posZ)*ptick);
		 */
		int px = item.stackTagCompound.getInteger("rootX");
		int pz = item.stackTagCompound.getInteger("rootZ");
		StructureSearch type = StructureSearch.list[item.getItemDamage()];
		int max = type.getSearchRange();
		v5.addVertexWithUV(0 - b0, 128 + b0, rz, 0, 1);
		v5.addVertexWithUV(128 + b0, 128 + b0, rz, 1, 1);
		v5.addVertexWithUV(128 + b0, 0 - b0, rz, 1, 0);
		v5.addVertexWithUV(0 - b0, 0 - b0, rz, 0, 0);
		v5.draw();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		int d = 12;
		NBTTagList li = item.stackTagCompound.getTagList(TAG_NAME, NBTTypes.COMPOUND.ordinal());
		GL11.glPointSize(2);
		v5.startDrawing(GL11.GL_POINTS);
		v5.setBrightness(240);
		v5.setColorOpaque_I(0x000000);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			Coordinate loc = Coordinate.readFromNBT("loc", tag);
			StructurePresence s = StructurePresence.valueOf(tag.getString("state"));
			int x = (int)Math.round((loc.xCoord-px)*40D/max+64);
			int z = (int)Math.round((loc.zCoord-pz)*40D/max+64);
			v5.setColorOpaque_I(s.renderColor);
			v5.addVertex(x, z, rz*2);
		}
		v5.draw();
	}

	public static enum StructureSearch {

		STRONGHOLD(),
		TOWERS(),
		CAVERN(ChromaStructures.CAVERN),
		BURROW(ChromaStructures.BURROW),
		OCEAN(ChromaStructures.OCEAN),
		DESERT(ChromaStructures.DESERT),
		SNOWSTRUCT(ChromaStructures.SNOWSTRUCT);

		private static final StructureSearch[] list = values();

		private final ChromaStructures structure;

		private StructureSearch() {
			this(null);
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

		private StructureSearch(ChromaStructures s) {
			structure = s;
		}

		public String displayName() {
			if (structure != null) {
				return structure.getDisplayName();
			}
			return ReikaStringParser.capFirstChar(this.name());
		}

		/** Return coords are absolute, not relative */
		private HashMap<Coordinate, StructurePresence> getLocations(WorldServer world, int x0, int z0) {
			HashMap<Coordinate, StructurePresence> li = new HashMap();
			if (structure != null) {
				int range = 128;
				for (int i = -range; i <= range; i++) {
					for (int k = -range; k <= range; k++) {
						int rx = x0+i*16;
						int rz = z0+k*16;
						StructureGenStatus at = DungeonGenerator.instance.getGenStatus(structure, world, rx, rz);
						if (at.hasStructure()) {
							li.put(new Coordinate(rx, 0, rz), at.isFinalized() ? StructurePresence.CONFIRMED : StructurePresence.EXPECTED);
						}
					}
				}
			}
			else {
				switch(this) {
					case STRONGHOLD:
						MapGenStronghold mg = ((ChunkProviderGenerate)world.theChunkProviderServer.currentChunkProvider).strongholdGenerator;
						List li0 = (List)ReikaObfuscationHelper.invoke("getCoordList", mg);
						if (li0 != null) {
							for (Object o : li0) {
								li.put(new Coordinate((ChunkPosition)o).to2D(), StructurePresence.EXPECTED);
							}
						}
						for (StructureStart ss : ((Map<Long, StructureStart>)mg.structureMap).values()) {
							if (ss.isSizeableStructure()) {
								StructureBoundingBox sbb = ss.getBoundingBox();
								if (sbb != null) {
									li.put(new Coordinate(sbb.getCenterX(), 0, sbb.getCenterZ()), StructurePresence.CONFIRMED);
								}
							}
						}
						break;
					case TOWERS:
						for (Towers t : Towers.towerList) {
							Coordinate c = t.getGeneratedLocation();
							if (c == null)
								c = new Coordinate(t.getRootPosition().chunkXPos, 0, t.getRootPosition().chunkZPos);
							li.put(c, ReikaWorldHelper.isChunkGenerated(world, c.xCoord, c.zCoord) ? StructurePresence.CONFIRMED : StructurePresence.EXPECTED);
						}
						break;
					default:
						break;
				}
			}
			return li;
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
