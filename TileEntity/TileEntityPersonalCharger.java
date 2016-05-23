/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Magic.Interfaces.ChargingPoint;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityPersonalCharger extends CrystalReceiverBase implements ChargingPoint, OwnedTile {

	private CrystalElement color = CrystalElement.WHITE;
	private boolean hasMultiblock = false;

	public static final int CAPACITY = 60000;

	@Override
	protected int getCooldownLength() {
		return 800;
	}

	@Override
	public ResearchLevel getResearchTier() {
		return ResearchLevel.ENERGYEXPLORE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (this.canConduct()) {
			if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
				this.checkAndRequest();
			}

			if (world.isRemote) {
				this.doParticles(world, x, y, z);
			}

			if (this.playSound(world, x, y, z)) {
				float f = 0.75F;

				if (TileEntityCrystalPylon.TUNED_PYLONS)
					f *= CrystalMusicManager.instance.getDingPitchScale(color);

				if (this.getTicksExisted()%(int)(72/f) == 0) {
					ChromaSounds.POWER.playSoundAtBlock(this, 0.33F, f);
				}
			}
		}
	}

	private boolean playSound(World world, int x, int y, int z) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 1);
		double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 1);
		double py = ReikaRandomHelper.getRandomPlusMinus(y, 0.375);
		float g = rand.nextFloat()*0.25F;
		float s = 2F;
		EntityFX fx = new EntityBlurFX(color, world, px, py, pz, 0, 0, 0).setScale(s).setLife(100).setGravity(g);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		fx = new EntityBlurFX(world, px, py, pz, 0, 0, 0).setScale(s*0.5F).setLife(100).setGravity(g).setColor(0xffffff);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		double d = rand.nextDouble()*3;
		int l = 20+rand.nextInt(20);
		int n = rand.nextInt(4);
		py = y-5;
		switch(n) {
			case 0:
				px = x+0.5-2+d;
				pz = z+0.5-2;
				break;
			case 1:
				px = x+0.5-2+d;
				pz = z+0.5+2;
				break;
			case 2:
				px = x+0.5-2;
				pz = z+0.5-2+d;
				break;
			case 3:
				px = x+0.5+2;
				pz = z+0.5-2+d;
				break;
		}
		fx = new EntityRuneFX(world, px, py, pz, color).setScale(2).setGravity(-0.0625F).setLife(l);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);

		this.validateStructure(world, x, y, z);
	}

	public void validateStructure(World world, int x, int y, int z) {
		int m1 = world.getBlockMetadata(x-2, y-4, z-2);
		int m2 = world.getBlockMetadata(x+2, y-4, z-2);
		int m3 = world.getBlockMetadata(x-2, y-4, z+2);
		int m4 = world.getBlockMetadata(x+2, y-4, z+2);
		if (m1 == m2 && m1 == m3 && m1 == m4) {
			CrystalElement e = CrystalElement.elements[m1];
			FilledBlockArray arr = ChromaStructures.getPersonalStructure(world, x, y-6, z, e);
			boolean flag = arr.matchInWorld();
			if (flag != hasMultiblock) {
				if (flag) {
					ChromaSounds.CAST.playSoundAtBlock(this, 1, 0.5F);
					color = e;
				}
				else {
					ChromaSounds.POWERDOWN.playSoundAtBlock(this, 1, 0.5F);
					energy.clear();
					checkTimer.setTick(checkTimer.getCap());
				}
			}
			hasMultiblock = flag;
		}
		else {
			hasMultiblock = false;
		}
		this.syncAllData(true);
	}

	private void checkAndRequest() {
		if (this.getEnergy(color)/(double)CAPACITY < 0.75) { // < 75% full
			this.requestEnergy(color, this.getRemainingSpace(color));
		}
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return this.canConduct() && e == color;
	}

	@Override
	public int maxThroughput() {
		return 200;
	}

	@Override
	public boolean canConduct() {
		return color != null && hasMultiblock;
	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	public boolean allowCharging(EntityPlayer ep, CrystalElement e) {
		return true;//ep.getUniqueID().equals(this.getPlacerUUID());
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return e == color ? CAPACITY : 0;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PERSONAL;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public float getChargeRateMultiplier(EntityPlayer ep, CrystalElement e) {
		return 0.4F;
	}

	@Override
	public void onUsedBy(EntityPlayer ep, CrystalElement e) {

	}

	@Override
	public CrystalElement getDeliveredColor(EntityPlayer ep, World world, int clickX, int clickY, int clickZ) {
		return color;
	}

	@Override
	public boolean drain(CrystalElement e, int amt) {
		boolean flag = energy.contains(e);
		this.drainEnergy(e, amt);
		return flag;
	}

	public CrystalElement getColor() {
		return color;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		color = CrystalElement.elements[NBT.getInteger("color")];
		hasMultiblock = NBT.getBoolean("multi");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("color", color.ordinal());
		NBT.setBoolean("multi", hasMultiblock);
	}

	@SideOnly(Side.CLIENT)
	public int getRenderColor() {
		return this.getColor().getColor();
	}

	@Override
	public Coordinate getChargeParticleOrigin(EntityPlayer ep, CrystalElement e) {
		return new Coordinate(this);
	}

}
