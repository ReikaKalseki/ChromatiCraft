/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
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
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Progression.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Interfaces.Entity.ChunkLoadingEntity;
import Reika.DragonAPI.Interfaces.Item.SpriteRenderCallback;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public abstract class ItemPoweredChromaTool extends ItemChromaTool implements SpriteRenderCallback {

	private static final int CHARGE_RANGE = 32;

	public ItemPoweredChromaTool(int index) {
		super(index);
		//this.setMaxDamage(getChargeStates()-1);
	}


	protected abstract CrystalElement getColor();
	public abstract int getMaxCharge();
	public abstract int getChargeStates();

	protected abstract boolean isActivated(EntityPlayer e, ItemStack is, boolean held);
	protected abstract int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is);
	protected abstract boolean doTick(ItemStack is, World world, EntityPlayer e, boolean held);

	protected int getChargeRate(ItemStack is, int base) {
		return base;
	}

	protected int getChargeState(float frac) {
		return Math.round((this.getChargeStates()-1)*frac);
	}

	@Override
	public final void getSubItems(Item i, CreativeTabs c, List li) {
		li.add(this.getChargedItem(0));
		li.add(this.getChargedItem(this.getMaxCharge()));
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		li.add(String.format("Energy: %.2f%s", 100F*this.getCharge(is)/this.getMaxCharge(), "%"));
	}

	@Override
	public final void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (e instanceof EntityPlayer) {
			if (this.isActivated((EntityPlayer)e, is, held)) {
				if (this.getCharge(is) > 0) {
					if (this.doTick(is, world, (EntityPlayer)e, held))
						this.removeCharge(is, this.getChargeConsumptionRate((EntityPlayer)e, world, is));
				}
			}
		}
	}

	@Override
	public final int getEntityLifespan(ItemStack is, World world) {
		return Integer.MAX_VALUE;
	}

	@Override
	public final boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public final Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityChargingTool(world, (EntityItem)location, itemstack);
	}

	@Override
	public final boolean onEntityItemUpdate(EntityItem ei) {
		int charge = this.getCharge(ei.getEntityItem());
		if (!ei.worldObj.isRemote) {
			if (charge < this.getMaxCharge()) {
				WorldLocation loc = new WorldLocation(ei);
				TemporaryCrystalReceiver r = new TemporaryCrystalReceiver(loc, 0, CHARGE_RANGE, 0.0625, ResearchLevel.ENDGAME);
				CrystalElement e = this.getColor();
				r.addColorRestriction(e);
				ItemStack is = ei.getEntityItem();
				int amt = this.getChargeRate(is);
				//CrystalSource s = CrystalNetworker.instance.findSourceWithX(r, e, amt, range, true);
				CrystalSource s = CrystalNetworker.instance.getNearestTileOfType(r, CrystalSource.class, CHARGE_RANGE);
				if (s != null && s.isConductingElement(e)) {
					s.drain(e, amt*4);
					if (s instanceof TileEntityCrystalPylon) {
						amt *= 1.25; //25% boost
						if (((TileEntityCrystalPylon)s).isEnhanced())
							amt *= 1.6; //net 2x
					}
					if (loc.getBlock() == ChromaBlocks.CHROMA.getBlockInstance() && loc.getBlockMetadata() == 0)
						amt *= 1.25;
					if (TileEntityAuraPoint.isPointWithin(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord, 256))
						amt *= 2;
					this.addCharge(is, amt);
					ReikaPacketHelper.sendEntitySyncPacket(DragonAPIInit.packetChannel, ei, 32);
					//ReikaJavaLibrary.pConsole(this.getCharge(is)+" (+"+this.getChargeRate(is)+", f="+(this.getCharge(is)/(float)MAX_CHARGE));
				}
			}
		}
		else {
			if (charge > 0) {
				this.doChargeFX(ei, charge);
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	private void doChargeFX(EntityItem ei, int charge) {
		if (charge == this.getMaxCharge()) {
			double ang = 360*Math.sin(ei.age/50D);
			for (int i = 0; i < 360; i += 60) {
				double v = 0.125+0.0625*Math.sin(ei.age/250D);
				double vx = v*Math.cos(Math.toRadians(ang+i));
				double vz = v*Math.sin(Math.toRadians(ang+i));
				EntityBlurFX fx = new EntityCCBlurFX(ei.worldObj, ei.posX, (int)ei.posY+0.25, ei.posZ, vx, 0, vz).setLife(40).setRapidExpand().setScale(1.5F);
				fx.setColor(this.getColor().getColor());
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
		else {
			double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			double[] vel = ReikaPhysicsHelper.polarToCartesian(v, itemRand.nextDouble()*360, itemRand.nextDouble()*360);
			float s = 1+itemRand.nextFloat();
			EntityBlurFX fx = new EntityCCBlurFX(ei.worldObj, ei.posX, (int)ei.posY+0.25, ei.posZ, vel[0], vel[1], vel[2]).setLife(40).setScale(s).setColliding();
			fx.setColor(this.getColor().getColor());
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private int getChargeRate(ItemStack is) {
		int get = this.getCharge(is);
		int base = (int)(5*Math.min(20, 1+100*ReikaMathLibrary.cosInterpolation(0, this.getMaxCharge(), get)));
		return this.getChargeRate(is, base);
	}

	public final ItemStack getChargedItem(int charge) {
		ItemStack is = new ItemStack(this);
		this.addCharge(is, charge);
		return is;
	}

	public final int addCharge(ItemStack is, int amt) {
		int get = this.getCharge(is);
		amt = Math.min(amt, this.getMaxCharge()-get);
		this.setCharge(is, get+amt);
		return amt;
	}

	public final int removeCharge(ItemStack is, int amt) {
		int get = this.getCharge(is);
		amt = Math.min(get, amt);
		this.setCharge(is, get-amt);
		return amt;
	}

	private void setCharge(ItemStack is, int amt) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		int max = this.getMaxCharge();
		is.stackTagCompound.setInteger("charge", MathHelper.clamp_int(amt, 0, max));
		if (this.hasChargeStates())
			is.setItemDamage(this.getChargeState(this.getCharge(is)/(float)max));
	}

	public final int getCharge(ItemStack is) {
		return is.stackTagCompound != null ? is.stackTagCompound.getInteger("charge") : 0;
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		return super.getItemSpriteIndex(item)+item.getItemDamage();
	}

	protected void renderExtraIcons(RenderItem ri, ItemStack is, ItemRenderType type) {

	}

	protected boolean hasChargeStates() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type) {
		if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("tooltip"))
			return false;
		this.renderExtraIcons(ri, is, type);
		if (type == ItemRenderType.INVENTORY && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			ReikaTextureHelper.bindTerrainTexture();
			CrystalElement e = this.getColor();
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
		return false;
	}

	@Override
	public final boolean doPreGLTransforms(ItemStack is, ItemRenderType type) {
		return true;
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
