package Reika.ChromatiCraft.Magic;

import java.util.Collection;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.TemporaryCrystalReceiver;
import Reika.ChromatiCraft.Items.Tools.ItemEfficiencyCrystal;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.PoweredItem;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Progression.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Interfaces.Entity.ChunkLoadingEntity;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ToolChargingSystem {

	public static final ToolChargingSystem instance = new ToolChargingSystem();

	private static final int CHARGE_RANGE = 32;

	private ToolChargingSystem() {

	}

	public int getChargeRate(ItemStack is) {
		int get = this.getCharge(is);
		int base = (int)(5*Math.min(20, 1+100*ReikaMathLibrary.cosInterpolation(0, this.getItem(is).getMaxCharge(), get)));
		return this.getItem(is).getChargeRate(is, base);
	}

	public <I extends Item & PoweredItem> ItemStack getChargedItem(I item, int charge) {
		ItemStack is = new ItemStack(item);
		this.addCharge(is, charge);
		return is;
	}

	public int addCharge(ItemStack is, int amt) {
		int get = this.getCharge(is);
		amt = Math.min(amt, this.getItem(is).getMaxCharge()-get);
		this.setCharge(is, get+amt);
		return amt;
	}

	public int removeCharge(ItemStack is, int amt, EntityPlayer ep) {
		if (ItemEfficiencyCrystal.isActive(ep))
			amt = Math.max(1, amt/3);
		int get = this.getCharge(is);
		amt = Math.min(get, amt);
		this.setCharge(is, get-amt);
		return amt;
	}

	private void setCharge(ItemStack is, int amt) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		int max = this.getItem(is).getMaxCharge();
		is.stackTagCompound.setInteger("charge", MathHelper.clamp_int(amt, 0, max));
		if (this.getItem(is).hasChargeStates())
			is.setItemDamage(this.getItem(is).getChargeState(this.getCharge(is)/(float)max));
	}

	public void tryChargeFromPlayer(ItemStack is, EntityPlayer ep) {
		int extr = this.getItem(is).getPlayerBufferExtractionRate(is);
		if (extr > 0) {
			CrystalElement ec = this.getItem(is).getColor(is);
			int get = Math.min(extr, PlayerElementBuffer.instance.getPlayerContent(ep, ec));
			if (get > 0) {
				int scale = 40; //40x since lumens in items are worth a LOT less
				int add = this.addCharge(is, get*scale);
				if (add > 0)
					add = Math.max(1, add/scale);
				PlayerElementBuffer.instance.removeFromPlayer(ep, ec, add);
			}
		}
	}

	private PoweredItem getItem(ItemStack is) {
		return (PoweredItem)is.getItem();
	}

	public final int getCharge(ItemStack is) {
		return is.stackTagCompound != null ? is.stackTagCompound.getInteger("charge") : 0;
	}

	public boolean tickItem(EntityItem ei) {
		ItemStack is = ei.getEntityItem();
		PoweredItem pi = this.getItem(is);
		int charge = this.getCharge(is);
		if (!ei.worldObj.isRemote) {
			if (charge < pi.getMaxCharge()) {
				WorldLocation loc = new WorldLocation(ei);
				TemporaryCrystalReceiver r = new TemporaryCrystalReceiver(loc, 0, CHARGE_RANGE, 0.0625, ResearchLevel.ENDGAME);
				CrystalElement e = pi.getColor(is);
				r.addColorRestriction(e);
				int amt = this.getChargeRate(is);
				//CrystalSource s = CrystalNetworker.instance.findSourceWithX(r, e, amt, range, true);
				CrystalSource s = CrystalNetworker.instance.getNearestTileOfType(r, CrystalSource.class, CHARGE_RANGE);
				if (s != null && s.isConductingElement(e)) {
					float rate = s.getDroppedItemChargeRate(is);
					if (rate > 0) {
						amt *= rate;
						s.drain(e, amt*4);
						amt = this.scaleChargeRate(amt, ei, is, s, loc);
						this.addCharge(is, amt);
						ReikaPacketHelper.sendEntitySyncPacket(DragonAPIInit.packetChannel, ei, 32);
						//ReikaJavaLibrary.pConsole(this.getCharge(is)+" (+"+this.getChargeRate(is)+", f="+(this.getCharge(is)/(float)MAX_CHARGE));
					}
				}
				r.destroy();
			}
		}
		else {
			if (charge > 0) {
				pi.doChargeFX(ei, charge);
			}
		}
		return false;
	}

	private int scaleChargeRate(int amt, EntityItem ei, ItemStack is, CrystalSource s, WorldLocation loc) {
		if (s instanceof TileEntityCrystalPylon) {
			amt *= 1.25; //25% boost
			if (((TileEntityCrystalPylon)s).isEnhanced())
				amt *= 1.6; //net 2x
		}
		if (loc.getBlock() == ChromaBlocks.CHROMA.getBlockInstance() && loc.getBlockMetadata() == 0)
			amt *= 1.25;
		if (TileEntityAuraPoint.isPointWithin(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord, 256))
			amt *= 2;
		return amt;
	}

	@SideOnly(Side.CLIENT)
	public void renderItemAux(RenderItem ri, ItemStack is, ItemRenderType type) {
		if (type == ItemRenderType.INVENTORY && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			ReikaTextureHelper.bindTerrainTexture();
			CrystalElement e = this.getItem(is).getColor(is);
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			GL11.glDisable(GL11.GL_LIGHTING);
			double sc = 0.5;
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				sc = 1;
			GL11.glScaled(sc, sc, sc);
			GL11.glTranslated(0.5/sc, 0, 0);
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				GL11.glTranslated(0, -0.5, 0);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/infoicons.png");
			double u = 0.0625*6;
			double v = 0;
			double s = 0.0625;
			Tessellator v5 = Tessellator.instance;
			double z = 50;
			//float f = 1+(float)(0.5F*Math.sin(System.currentTimeMillis()/10000D));
			//int c = ReikaColorAPI.getColorWithBrightnessMultiplier(e.getColor(), f);
			int c = e.getColor();
			int c1 = ReikaColorAPI.mixColors(c, 0xffffff, 0.65F);
			int c2 = e == CrystalElement.BLACK ? c1 : ReikaColorAPI.mixColors(c, 0x000000, 0.75F);
			ColorBlendList cbl = new ColorBlendList(10, c, c2, c, c1);
			v5.startDrawingQuads();
			v5.addVertexWithUV(0, 0, z, u, v+s);
			v5.addVertexWithUV(1, 0, z, u+s, v+s);
			v5.addVertexWithUV(1, 1, z, u+s, v);
			v5.addVertexWithUV(0, 1, z, u, v);

			v5.setColorOpaque_I(cbl.getColor(System.currentTimeMillis()/40D));
			v5.addVertexWithUV(0, 0, z, u+s, v+s);
			v5.addVertexWithUV(1, 0, z, u+s*2, v+s);
			v5.addVertexWithUV(1, 1, z, u+s*2, v);
			v5.addVertexWithUV(0, 1, z, u+s, v);
			v5.draw();

			ReikaTextureHelper.bindTerrainTexture();
			IIcon ico = e.getFaceRune();
			u = ico.getMinU();
			double du = ico.getMaxU();
			v = ico.getMinV();
			double dv = ico.getMaxV();
			double x = 0.5/sc;
			v5.startDrawingQuads();
			v5.addVertexWithUV(0-x, 0, z, u, dv);
			v5.addVertexWithUV(1-x, 0, z, du, dv);
			v5.addVertexWithUV(1-x, 1, z, du, v);
			v5.addVertexWithUV(0-x, 1, z, u, v);
			v5.draw();

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
	}

	public static class EntityChargingTool extends EntityItem implements ChunkLoadingEntity {

		private boolean needsLoad = true;

		public EntityChargingTool(World world) {
			super(world);
		}

		public EntityChargingTool(World world, EntityItem e, ItemStack is) {
			super(world, e.posX, e.posY, e.posZ, is);
			delayBeforeCanPickup = e.delayBeforeCanPickup;
			motionX = e.motionX;
			motionY = e.motionY;
			motionZ = e.motionZ;
			velocityChanged = true;
		}

		public EntityChargingTool(World world, double posX, double posY, double posZ, ItemStack is) {
			super(world, posX, posY, posZ, is);
		}

		@Override
		public void setAgeToCreativeDespawnTime() {

		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			//ReikaJavaLibrary.pConsole("Ticking age "+age+" for "+this);
			//worldObj.newExplosion(this, posX, posY, posZ, 8, true, true);
			if (needsLoad) {
				ChunkManager.instance.loadChunks(this);
				needsLoad = false;
			}
		}

		@Override
		public boolean isInRangeToRenderDist(double distsq) {
			return true;
		}

		@Override
		public Collection<ChunkCoordIntPair> getChunksToLoad() {
			return ChunkManager.instance.getChunkSquare(MathHelper.floor_double(posX), MathHelper.floor_double(posZ), CHARGE_RANGE/16);
			//return ChunkManager.instance.getChunk(this);
		}

		@Override
		public void setDead() {
			this.onDestroy();
			super.setDead();
		}

		@Override
		public void onDestroy() {
			ChunkManager.instance.unloadChunks(this);
		}

	}

}
