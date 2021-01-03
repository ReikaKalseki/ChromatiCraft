/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.OrePings.OrePingDelegate;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Event.Client.EntityRenderingLoopEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class OreOverlayRenderer {

	public static final OreOverlayRenderer instance = new OreOverlayRenderer();

	private final HashMap<BlockKey, OrePingDelegate> blocks = new HashMap();

	private final HashMap<WorldLocation, OreRender> coords = new HashMap();
	private final HashMap<String, Long> lastSoundTick = new HashMap();

	private static final int DURATION_PING = 720;//360;
	private static final int DURATION_SCAN = 90;

	private static final int PING_RANGE = 48;//40;//32;

	private OreOverlayRenderer() {
		this.loadOres();
	}

	public void loadOres() {
		for (ReikaOreHelper ore : ReikaOreHelper.oreList) {
			blocks.put(new BlockKey(ore.getOreBlockInstance()), new OreBlock(ore));
		}
		blocks.put(new BlockKey(Blocks.lit_redstone_ore), blocks.get(new BlockKey(Blocks.redstone_ore)));
		for (ModOreList ore : ModOreList.oreList) {
			OreBlock ob = new OreBlock(ore);
			for (ItemStack is : ore.getAllOreBlocks())
				blocks.put(new BlockKey(is), ob);
		}
		ChromatiCraft.logger.log("Initialized ore map with "+blocks.size()+" entries: "+blocks.keySet());
	}

	public OrePingDelegate getForBlock(Block b, int meta) {
		return blocks.get(new BlockKey(b, meta));
	}

	public OrePingDelegate getOreTypeByData(ItemStack is) {
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("oreType"))
			return null;
		ItemStack ore = ItemStack.loadItemStackFromNBT(is.stackTagCompound.getCompoundTag("oreType"));
		if (ore == null)
			return null;
		return blocks.get(new BlockKey(ore));
	}

	public static void addBlockDelegate(Block b, int meta, OrePingDelegate delegate) {
		BlockKey bk = new BlockKey(b, meta);
		if (instance.blocks.containsKey(bk))
			throw new IllegalArgumentException("Block already present!");
		instance.blocks.put(bk, delegate);
	}

	public void addCoordinate(World world, int x, int y, int z, Block id, int meta, boolean ping) {
		if (world.isRemote) {
			coords.put(new WorldLocation(world, x, y, z), new OreRender(id, meta, ping));
		}
		else {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.OREPINGLOC.ordinal(), world, x, y, z, new PacketTarget.RadiusTarget(world, x, y, z, 90), Block.getIdFromBlock(id), meta, ping ? 1 : 0);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void tick(ClientTickEvent evt) {
		Iterator<Entry<WorldLocation, OreRender>> it = coords.entrySet().iterator();
		while (it.hasNext()) {
			Entry<WorldLocation, OreRender> e = it.next();
			OreRender key = e.getValue();
			key.life--;
			if (key.life <= 0) {
				it.remove();
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void render(EntityRenderingLoopEvent evt) {
		if (!coords.isEmpty()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			//BlendMode.ADDITIVEDARK.apply();
			BlendMode.DEFAULT.apply();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			ReikaTextureHelper.bindTerrainTexture();
			int dim = Minecraft.getMinecraft().theWorld.provider.dimensionId;
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			for (WorldLocation loc : coords.keySet()) {
				if (loc.dimensionID == dim) {
					this.renderPoint(loc, coords.get(loc), ep);
				}
			}
			GL11.glPopAttrib();
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderPoint(WorldLocation loc, OreRender val, EntityPlayer ep) {
		GL11.glPushMatrix();

		RenderManager rm = RenderManager.instance;
		GL11.glTranslated(loc.xCoord+0.5-rm.renderPosX, loc.yCoord+0.5-rm.renderPosY, loc.zCoord+0.5-rm.renderPosZ);

		GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ChromaIcons.FADE_BASICBLEND.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.setBrightness(240);

		float f1 = val.getOpacity();
		float f = Math.min(1, f1+0.375F);
		double s = 1.5*Math.sqrt(f);
		//int c = ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, f1);
		int c = val.getColor();
		v5.setColorRGBA_I(c, (int)(f1*255));
		v5.addVertexWithUV(-s, s, 0, u, dv);
		v5.addVertexWithUV(+s, s, 0, du, dv);
		v5.addVertexWithUV(+s, -s, 0, du, v);
		v5.addVertexWithUV(-s, -s, 0, u, v);
		v5.draw();
		GL11.glPopMatrix();
	}

	public void startScan(World world, int x, int y, int z, EntityPlayer ep) {
		ProgressiveBreaker b = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(world, x, y, z, 80);
		b.call = new OreScanCallback();
		this.addBreaker(b, ep);
	}

	public void startPing(World world, int x, int y, int z, EntityPlayer ep) {
		int r = PING_RANGE;//8;
		ProgressiveBreaker b = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(world, x, y, z, r);
		b.isOmni = true;
		b.breakAir = true;
		this.addBreaker(b, ep);
		b.tickRate = 1;
		b.call = new OrePingCallback();
		ChromaSounds.NETWORKOPT.playSound(ep, 1, 0.5F);
	}

	private void addBreaker(ProgressiveBreaker b, EntityPlayer ep) {
		b.pathTracking = true;
		b.doBreak = false;
		b.player = ep;
	}

	private void playSoundWithCooldown(SoundEnum s, EntityPlayer ep, float vol, float pitch) {
		Long last = lastSoundTick.get(s.getName());
		if (last != null && ep.worldObj.getTotalWorldTime() <= last.longValue())
			return;
		lastSoundTick.put(s.getName(), ep.worldObj.getTotalWorldTime());
		s.playSound(ep, vol, pitch);
	}

	private abstract static class OreOverlayCallback implements BreakerCallback {

		@Override
		public final boolean canBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
			return true;
		}

		@Override
		public final void onPreBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {

		}

		@Override
		public final void onFinish(ProgressiveBreaker b) {

		}
		@Override
		public final void onPostBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
			OrePingDelegate opd = instance.blocks.get(new BlockKey(id, meta));
			if (opd != null) {
				instance.addCoordinate(world, x, y, z, id, meta, this instanceof OrePingCallback);
				this.onAdd(world, x, y, z, b, id, meta);
			}
		}

		protected abstract void onAdd(World world, int x, int y, int z, ProgressiveBreaker b, Block id, int meta);

	}

	private static class OreScanCallback extends OreOverlayCallback {

		private int blocksFound;

		@Override
		protected void onAdd(World world, int x, int y, int z, ProgressiveBreaker b, Block id, int meta) {
			instance.playSoundWithCooldown(ChromaSounds.BUFFERWARNING, b.player, 1, 0.5F);
			blocksFound++;
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.NUMBERPARTICLE.ordinal(), world, x, y, z, new PacketTarget.RadiusTarget(world, x, y, z, 90), blocksFound);
		}

	}

	private static class OrePingCallback extends OreOverlayCallback {

		@Override
		protected void onAdd(World world, int x, int y, int z, ProgressiveBreaker b, Block id, int meta) {
			BlockArray arr = new BlockArray();
			b.tickRate = 5;
			arr.recursiveAddWithMetadata(world, x, y, z, id, meta);
			instance.playSoundWithCooldown(ChromaSounds.BOUNCE, b.player, 0.5F, 1F);
			for (Coordinate c : arr.keySet()) {
				b.exclude(c);
				instance.addCoordinate(world, c.xCoord, c.yCoord, c.zCoord, id, meta, true);
			}
		}

	}

	private static class OreRender {

		public final Block block;
		public final int metadata;
		private final int lifetime;

		private int life;

		private final int color;

		private OreRender(Block b, int meta, boolean ping) {
			block = b;
			metadata = meta;

			BlockKey bk = new BlockKey(b, meta);
			OrePingDelegate del = instance.blocks.get(bk);
			if (del == null) {
				ChromatiCraft.logger.logError("Block "+bk+" has null oreping delegate?!");
			}
			color = del != null ? del.getColor() : 0xD47EFF;

			lifetime = ping ? DURATION_PING : DURATION_SCAN;
			life = lifetime;
		}

		public int getColor() {
			return color;
		}

		public float getOpacity() {
			return (float)life/lifetime;
		}

	}

	private static class OreBlock implements OrePingDelegate {

		private final OreType ore;

		private OreBlock(OreType ore) {
			this.ore = ore;
		}

		@Override
		public boolean match(Block b, int meta) {
			if (ore instanceof ReikaOreHelper) {
				return ReikaOreHelper.getFromVanillaOre(b) == ore;
			}
			else if (ore instanceof ModOreList) {
				return ModOreList.getModOreFromOre(b, meta) == ore;
			}
			else {
				return false;
			}
		}

		@Override
		public int getColor() {
			return ore.getDisplayColor();
		}

		@Override
		public boolean isVisible(EntityPlayer ep) {
			return true;
		}

		@Override
		public IIcon getIcon() {
			ItemStack is = ore.getFirstOreBlock();
			return Block.getBlockFromItem(is.getItem()).getIcon(0, is.getItemDamage());
		}

		@Override
		public ItemStack getPrimary() {
			return ore.getFirstOreBlock();
		}

	}

}
