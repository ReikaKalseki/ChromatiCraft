/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import java.util.HashSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.ChromaOverlays;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityPylonTurboCharger extends TileEntityChromaticBase implements BreakAction, OwnedTile {

	public static final int RITUAL_LENGTH = 2400;

	private int revTick = 0;
	private int ritualTick = 0;
	private Location location;

	private int jetTick = 0;
	private int groundTick;
	private int skyTick = 0;

	private boolean isComplete;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PYLONTURBO;
	}

	@Override
	public int getPacketDelay() {
		return revTick > 0 ? 1 : super.getPacketDelay();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			if (location == null && ritualTick > 0) {
				if (revTick > 0) {
					EntityPlayer ep = world.getClosestPlayer(x, y, z, 6);
					if (ep == null) //no players inside
						revTick--;
					if (revTick < 16) {
						Location loc = Location.list[revTick/2];
						Coordinate c = loc.position.offset(x, y, z);
						ChromaTiles t = ChromaTiles.getTileFromIDandMetadata(c.getBlock(world), c.getBlockMetadata(world));
						if (t != ChromaTiles.PYLONTURBO) {
							this.failRitual(world, x, y, z);
							return;
						}
						TileEntityPylonTurboCharger te = (TileEntityPylonTurboCharger)c.getTileEntity(world);
						te.ritualTick = ritualTick;
					}
					if (revTick == 0) {
						this.triggerStartFX(world, x, y, z);
					}
					ChromaSounds.ERROR.playSoundAtBlock(this, 1, 0.5F); //low buzz
				}
				else {
					this.doRitualTick(world, x, y, z);
					ritualTick--;
					if (ritualTick == 0) {
						this.completeRitual(world, x, y, z);
					}
				}
			}
		}
		else {
			this.doParticles(world, x, y, z);
		}

		if (world.isRemote && revTick > 0) {
			this.doRevEffects(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doRevEffects(World world, int x, int y, int z) {
		TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
		for (int i = 0; i < Location.list.length; i++) {
			Location loc = Location.list[i];
			Coordinate c = loc.position.offset(x, y, z);
			double px = c.xCoord+0.5;
			double py = c.yCoord+0.5;
			double pz = c.zCoord+0.5;

			double dx = px-xCoord-0.5;
			double dy = py-yCoord-0.5;
			double dz = pz-zCoord-0.5;

			double dd = ReikaMathLibrary.py3d(dx, dy, dz);

			double v = -ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);

			double vx = dx/dd*v;
			double vy = dy/dd*v;
			double vz = dz/dd*v;

			float s = (float)ReikaRandomHelper.getRandomBetween(6, 9);

			EntityFX fx = new EntityCenterBlurFX(te.getColor(), world, px, py, pz, vx, vy, vz).setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		double px = x+0.5;
		double py = y+0.4375;
		double pz = z+0.5;

		px = ReikaRandomHelper.getRandomPlusMinus(px, 0.03125);
		py = ReikaRandomHelper.getRandomPlusMinus(py, 0.03125);
		pz = ReikaRandomHelper.getRandomPlusMinus(pz, 0.03125);

		int l = 5+rand.nextInt(35);
		float s = (float)ReikaRandomHelper.getRandomPlusMinus(1, 0.5);

		int r = 192+rand.nextInt(64);
		int g = 192+rand.nextInt(64);
		int b = 192+rand.nextInt(64);

		EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(r, g, b).setScale(s).setGravity(0).setLife(l);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		ritualTick = 0;
		skyTick = 0;
		groundTick = 0;
		jetTick = 0;
		this.findLocation(world, x, y, z);
	}

	private void findLocation(World world, int x, int y, int z) {
		for (int i = 0; i < Location.list.length; i++) {
			Location loc = Location.list[i];
			Coordinate c = loc.position.negate().offset(x, y, z);
			TileEntity te = c.getTileEntity(world);
			if (te instanceof TileEntityPylonTurboCharger) {
				if (((TileEntityPylonTurboCharger)te).getPylon(world, te.xCoord, te.yCoord, te.zCoord) != null) {
					location = loc;
					return;
				}
			}
		}
	}

	private void doRitualTick(World world, int x, int y, int z) {
		TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
		if (te == null) {
			this.failRitual(world, x, y, z);
		}
		else {
			for (int i = 0; i < Location.list.length; i++) {
				Coordinate c = Location.list[i].position.offset(x, y, z);
				ChromaTiles t = ChromaTiles.getTileFromIDandMetadata(c.getBlock(world), c.getBlockMetadata(world));
				if (t != ChromaTiles.PYLONTURBO) {
					this.failRitual(world, x, y, z);
					return;
				}
				TileEntityPylonTurboCharger tile = (TileEntityPylonTurboCharger)c.getTileEntity(world);
				tile.ritualTick = ritualTick;
			}

			if ((RITUAL_LENGTH-ritualTick)%200 == 0) {
				ChromaSounds.PYLONBOOSTRITUAL.playSoundAtBlock(this, 0.75F, 1);
			}

			if (skyTick > 0) {
				this.doEvent(world, x, y, z, EventType.SKY);
				skyTick--;
			}
			if (groundTick > 0) {
				this.doEvent(world, x, y, z, EventType.PARTICLEBURST);
				groundTick--;
			}
			if (jetTick > 0) {
				this.doEvent(world, x, y, z, EventType.JETS);
				jetTick--;
			}

			if (this.triggerEffect(120, 1.5, 0)) {
				this.doEvent(world, x, y, z, EventType.FLASH);
			}
			if (this.triggerEffect(20, 0.25, RITUAL_LENGTH/4)) {
				this.doEvent(world, x, y, z, EventType.BEAM);
			}
			if (groundTick == 0 && this.triggerEffect(200, 0.75, RITUAL_LENGTH/2)) {
				groundTick = 5+rand.nextInt(10);
				this.doEvent(world, x, y, z, EventType.PARTICLEBURST);
			}
			if (skyTick == 0 && this.triggerEffect(100, 1.5, 0)) {
				skyTick = 20+rand.nextInt(80);
				this.doEvent(world, x, y, z, EventType.SKY);
			}
			if (jetTick == 0 && this.triggerEffect(80, 1, RITUAL_LENGTH/2)) {
				jetTick = 40+rand.nextInt(120);
				this.doEvent(world, x, y, z, EventType.JETS);
			}
			if (this.triggerEffect(240, 4, RITUAL_LENGTH/4)) {
				this.doEvent(world, x, y, z, EventType.LIGHTNING);
			}
			if (this.triggerEffect(160, 2, 0)) {
				this.doEvent(world, x, y, z, EventType.POTIONS);
			}
		}
	}

	private boolean triggerEffect(int min, double factor, int sub) {
		if (ritualTick < min || (RITUAL_LENGTH-ritualTick) < min)
			return false;
		return ritualTick%((int)(2*Math.max(min, min*factor))) == 0 || rand.nextInt(Math.max(min, (int)(ritualTick*factor)-sub)) == 0;
	}

	private void completeRitual(World world, int x, int y, int z) {
		this.doCompleteParticles();
		ritualTick = 0;
		skyTick = 0;
		jetTick = 0;
		ChromaSounds.PYLONTURBO.playSoundAtBlock(this, 2, 1);
		ChromaSounds.PYLONTURBO.playSoundAtBlock(this, 2, 1);
		TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
		te.enhance();

		for (int i = 0; i < Location.list.length; i++) {
			Coordinate c = Location.list[i].position.offset(x, y, z);
			ChromaTiles t = ChromaTiles.getTileFromIDandMetadata(c.getBlock(world), c.getBlockMetadata(world));
			if (t != ChromaTiles.PYLONTURBO) {
				this.failRitual(world, x, y, z);
				return;
			}
			TileEntityPylonTurboCharger tile = (TileEntityPylonTurboCharger)c.getTileEntity(world);
			tile.ritualTick = 0;
			tile.isComplete = true;
			tile.syncAllData(true);
		}
		isComplete = true;

		this.syncAllData(true);
	}

	private void failRitual(World world, int x, int y, int z) {
		TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
		if (te != null) {
			te.disenhance();
			te.drain(te.getColor(), te.getEnergy(te.getColor())*4/5);
		}
		this.doFailParticles(te != null);
		for (int i = 0; i < Location.list.length; i++) {
			Coordinate c = Location.list[i].position.offset(x, y, z);
			ChromaTiles t = ChromaTiles.getTileFromIDandMetadata(c.getBlock(world), c.getBlockMetadata(world));
			if (t != ChromaTiles.PYLONTURBO) {
				continue;
			}
			TileEntityPylonTurboCharger tile = (TileEntityPylonTurboCharger)c.getTileEntity(world);
			tile.ritualTick = 0;
		}
		ChromaSounds.DISCHARGE.playSoundAtBlockNoAttenuation(this, 1, 1);
		isComplete = false;
		ritualTick = 0;
		revTick = 0;
		skyTick = 0;
		jetTick = 0;
		groundTick = 0;
		worldObj.addWeatherEffect(new EntityLightningBolt(world, x+0.5, y+8.5, z+0.5));
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y+8, z).expand(24, 16, 24);
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase e : li) {
			if (e instanceof EntityPlayer) {
				if (((EntityPlayer)e).capabilities.isCreativeMode) {
					//screen rock is enough
				}
				else {
					float amt = Math.max(5, Math.min(e.getHealth()-4, e.getMaxHealth()*0.75F));
					ChromaAux.doPylonAttack(e, amt, false);
				}
			}
			else {
				e.attackEntityFrom(DamageSource.magic, 0); //only appear to hurt
			}
		}
		this.syncAllData(true);
	}

	private void doCompleteParticles() {
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PYLONTURBOCOMPLETE.ordinal(), this, 64);
	}

	private void doEvent(World world, int x, int y, int z, EventType type) {
		TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
		int dat = 0;
		switch(type) {
			case FLASH:
				//this.rockScreen(40);
				dat = te.getColor().ordinal();
				//ChromaSounds.PYLONFLASH.playSoundAtBlock(this);
				break;
			case BEAM:
				break;
			case JETS:
				if (jetTick%4 == 0)
					ChromaSounds.INFUSE.playSoundAtBlockNoAttenuation(this.getPylon(world, x, y, z), 0.5F, 0.5F);
				break;
			case PARTICLEBURST:
				//this.rockScreen(6);
				//ChromaSounds.CAST.playSoundAtBlockNoAttenuation(this.getPylon(world, x, y, z), 1, 1);
				break;
			case SKY:
				dat = skyTick;
				break;
			case LIGHTNING:
				//this.rockScreen(20);
				//ChromaSounds.DISCHARGE.playSoundAtBlockNoAttenuation(this, 1, 1);
				double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 8);
				double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 8);
				worldObj.addWeatherEffect(new EntityLightningBolt(world, dx, y, dz));
				break;
			case POTIONS:
				//this.rockScreen(16);
				//ChromaSounds.POWERDOWN.playSoundAtBlockNoAttenuation(this, 1, 1);
				AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x-12, y-2, z-12, x+1+12, y+1+24, z+1+12);
				List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
				for (EntityLivingBase e : li) {
					PotionEffect eff = CrystalPotionController.getEffectFromColor(te.getColor(), 200, 4);
					if (eff != null) {
						e.addPotionEffect(eff);
					}
				}
				break;
		}

		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PYLONTURBOEVENT.ordinal(), this, 64, type.ordinal(), dat);
	}

	private void doFailParticles(boolean hasPylon) {
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PYLONTURBOFAIL.ordinal(), this, 64, hasPylon ? 1 : 0);
	}

	@SideOnly(Side.CLIENT)
	public void doFailParticlesClient(World world, int x, int y, int z, boolean hasPylon) {
		this.rockScreenClient(15);
		int n = 12;
		int da = 360/n;
		double r = 16;
		double dd = 0.125;
		for (int a = 0; a < 360; a += da) {
			for (double d = 0.5; d <= r; d += dd) {
				double dx = x+0.5+d*Math.cos(Math.toRadians(a));
				double dz = z+0.5+d*Math.sin(Math.toRadians(a));
				double dy = y+0.5+8;
				float s = 5*(float)(1-(d/r));
				EntityFX fx = new EntitySparkleFX(world, dx, dy, dz, 0, 0, 0).setScale(s);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
		if (hasPylon) {
			TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
			n = 32+rand.nextInt(96);
			for (int i = 0; i < n; i++) {
				double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 4);
				double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 4);
				double py = ReikaRandomHelper.getRandomPlusMinus(y+0.5+8, 2);

				double dx = px-x-0.5;
				double dz = pz-z-0.5;
				double dy = py-y-0.5-8;

				dd = ReikaMathLibrary.py3d(dx, dy, dz);

				double v = -ReikaRandomHelper.getRandomPlusMinus(1, 0.25);
				double vx = v*dx/dd;
				double vy = v*dy/dd;
				double vz = v*dz/dd;

				int l = 10+rand.nextInt(10);
				float s = (float)ReikaRandomHelper.getRandomBetween(2, 7.5);
				EntityFX fx = new EntityBlurFX(world, px, py, pz, vx, vy, vz).fadeColors(0xffffff, te.getColor().getColor()).setScale(s).setLife(l).setRapidExpand();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void doCompleteParticlesClient(World world, int x, int y, int z) {
		double da = 5;
		for (double a1 = 0; a1 < 360; a1 += da) {
			for (double a2 = -90+da; a2 < 90-da; a2 += da) {
				double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
				double[] xyz = ReikaPhysicsHelper.polarToCartesian(v, a2, a1);
				float s = (float)ReikaRandomHelper.getRandomBetween(2D, 3D);
				int c1 = this.getPylon(world, x, y, z).getColor().getColor();
				int l = 30+rand.nextInt(20);
				EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.5+8, z+0.5, xyz[0], xyz[1], xyz[2]).setRapidExpand().setScale(s).setLife(l).setGravity(0).fadeColors(0xffffff, c1);
				IIcon ico = null;
				switch(rand.nextInt(4)) {
					case 0:
						ico = ChromaIcons.FADE.getIcon();
						break;
					case 1:
						ico = ChromaIcons.BIGFLARE.getIcon();
						break;
					case 2:
						ico = ChromaIcons.SPARKLEPARTICLE.getIcon();
						break;
					case 3:
						ico = ChromaIcons.CENTER.getIcon();
						break;
				}
				fx.setParticleIcon(ico);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		float s = (float)ReikaRandomHelper.getRandomBetween(2D, 3D);
		double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
		int c1 = this.getPylon(world, x, y, z).getColor().getColor();
		int l = 30+rand.nextInt(20);
		EntityFX fx = new EntityBlurFX(world, x+0.5, y+0.5+8, z+0.5, 0, v, 0).setRapidExpand().setScale(s).setLife(l).setGravity(0).fadeColors(0xffffff, c1);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		s = (float)ReikaRandomHelper.getRandomBetween(2D, 3D);
		l = 30+rand.nextInt(20);
		v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
		fx = new EntityBlurFX(world, x+0.5, y+0.5+8, z+0.5, 0, -v, 0).setRapidExpand().setScale(s).setLife(l).setGravity(0).fadeColors(0xffffff, c1);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@SideOnly(Side.CLIENT)
	public void doEventClient(World world, int x, int y, int z, int type, int data) {
		EventType evt = EventType.list[type];
		TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
		switch(evt) {
			case FLASH: {
				this.rockScreenClient(40);
				ReikaSoundHelper.playClientSound(ChromaSounds.PYLONFLASH, x+0.5, y+0.5, z+0.5, 1, 1, false); //play sound here to ensure sync
				ChromaOverlays.instance.triggerWashout(CrystalElement.elements[data]);
				for (int i = 0; i < 6; i++) {
					double v = ReikaRandomHelper.getRandomBetween(0.05, 0.1);
					int n = 16+rand.nextInt(32);
					double theta = rand.nextInt(360);
					double phi = rand.nextInt(360);
					for (int k = 0; k < n; k++) {
						double rtheta = ReikaRandomHelper.getRandomPlusMinus(theta, 15);
						double rphi = ReikaRandomHelper.getRandomPlusMinus(phi, 15);
						double[] xyz = ReikaPhysicsHelper.polarToCartesian(v, rtheta, rphi);
						float s = (float)ReikaRandomHelper.getRandomBetween(2D, 4D);
						int l = 40+rand.nextInt(120);
						int c1 = te.getColor().getColor();
						EntityFX fx = new EntityBlurFX(world, x+0.5, y+0.5+8, z+0.5, xyz[0], xyz[1], xyz[2]).setRapidExpand().setScale(s).setLife(l).setGravity(0).fadeColors(0xffffff, c1);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
				}
				break;
			}
			case PARTICLEBURST: {
				this.rockScreenClient(6);
				ReikaSoundHelper.playClientSound(ChromaSounds.CAST, x+0.5, y+0.5, z+0.5, 1, 1, false); //play sound here to ensure sync
				for (int a = 0; a < 360; a += 2) {
					int n = 1;//1+rand.nextInt(3);
					for (int i = 0; i < n; i++) {
						double r = 7;

						double dx = x+0.5+r*Math.cos(Math.toRadians(a));
						double dz = z+0.5+r*Math.sin(Math.toRadians(a));

						dx = ReikaRandomHelper.getRandomPlusMinus(dx, 0.125);
						dz = ReikaRandomHelper.getRandomPlusMinus(dz, 0.125);

						double dy = y;

						int c = te.getColor().getColor();
						float s = (float)ReikaRandomHelper.getRandomPlusMinus(3D, 1D);
						int l = 40+rand.nextInt(120);
						float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
						EntityFX fx = new EntityFloatingSeedsFX(world, dx, dy, dz, 0, 90).setLife(l).setScale(s).setColor(c).setGravity(g);
						((EntityFloatingSeedsFX)fx).freedom = 40;
						((EntityFloatingSeedsFX)fx).velocity = 1.25;
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
				}
				break;
			}
			case BEAM: {
				Location loc = Location.list[rand.nextInt(Location.list.length)];
				Coordinate c = loc.position.offset(x, y, z);
				int n = 6+rand.nextInt(6);

				double px = c.xCoord+0.5;
				double py = c.yCoord+0.5;
				double pz = c.zCoord+0.5;

				double dx = px-xCoord-0.5;
				double dy = py-yCoord-0.5;
				double dz = pz-zCoord-0.5;

				double dd = ReikaMathLibrary.py3d(dx, dy, dz);

				for (int i = 0; i < n; i++) {
					double v = ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);

					double vx = dx/dd*v;
					double vy = dy/dd*v;
					double vz = dz/dd*v;

					float s = (float)ReikaRandomHelper.getRandomBetween(3, 6);

					EntityFX fx = new EntityLaserFX(te.getColor(), world, px, py, pz, vx, vy, vz).setScale(s);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);

					fx = new EntityLaserFX(te.getColor(), world, px, py, pz, -vx, -vy, -vz).setScale(s);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
				break;
			}
			case JETS: {	//solid streams of sparkly particles from the pylon
				int n = 6;
				int da = 360/n;
				double a = Math.toRadians(this.getTicksExisted()%360);
				double v = ReikaRandomHelper.getRandomBetween(0.125, 0.25);
				if (rand.nextInt(4) == 0)
					v *= 2;
				for (int i = 0; i < n; i++) {
					double a1 = a+i*da;
					float s = (float)ReikaRandomHelper.getRandomBetween(1.5, 3);
					double th = 60*Math.sin(a+i*Math.PI/6D);
					int l = 40;
					EntityFX fx = new EntityFloatingSeedsFX(world, x+0.5, y+8.5, z+0.5, a1, th).setRapidExpand().setLife(l).setScale(s).fadeColors(0xffffff, te.getColor().getColor());
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
				break;
			}
			case SKY: {
				skyTick = data;
				//do in renderer
				break;
			}
			case LIGHTNING:
				this.rockScreenClient(20);
				ReikaSoundHelper.playClientSound(ChromaSounds.DISCHARGE, x+0.5, y+0.5, z+0.5, 1, 1, false); //play sound here to ensure sync
				break;
			case POTIONS:
				this.rockScreenClient(16);
				ReikaSoundHelper.playClientSound(ChromaSounds.POWERDOWN, x+0.5, y+0.5, z+0.5, 1, 1, false); //play sound here to ensure sync
				int r = 16;
				int n = 16+rand.nextInt(32);
				HashSet<Coordinate> coords = new HashSet();
				for (int i = 0; i < n; i++) {
					double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, r);
					double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, r);
					Coordinate c = new Coordinate(dx, y, dz);
					while(coords.contains(c)) {
						dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, r);
						dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, r);
						c = new Coordinate(dx, y, dz);
					}
					coords.add(c);

					int l = 10+rand.nextInt(50);
					float s = (float)ReikaRandomHelper.getRandomBetween(2, 6);
					float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
					EntityFX fx = new EntityRuneFX(world, dx, y-1-s/4F, dz, 0, 0.125, 0, te.getColor()).setGravity(g).setLife(l).setScale(s);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
				break;
		}
	}
	//For some reason screen rock only works if the player was recently damaged; that is the real reason for triggering this effect
	private void triggerStartFX(World world, int x, int y, int z) {
		for (int i = 0; i < Location.list.length; i++) {
			Location loc = Location.list[i];
			Coordinate c = loc.position.offset(x, y, z);
			world.addWeatherEffect(new EntityLightningBolt(world, c.xCoord, c.yCoord, c.zCoord));
		}

		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x-64, y-64, z-64, x+1+64, y+1+64, z+1+64);
		List<EntityPlayer> li = world.getEntitiesWithinAABB(EntityPlayer.class, box);
		for (EntityPlayer ep : li) {
			ep.attackEntityFrom(new DamageSource("fx").setDamageBypassesArmor().setDamageIsAbsolute().setDamageAllowedInCreativeMode(), 0.00001F);
		}

		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PYLONTURBOSTART.ordinal(), this, 64);
	}

	@SideOnly(Side.CLIENT)
	public void doStartFXClient(World world, int x, int y, int z) {
		ReikaSoundHelper.playClientSound(ChromaSounds.PYLONBOOSTSTART, x+0.5, y+0.5, z+0.5, 2, 1, false); //play sound here to ensure sync
		ReikaSoundHelper.playClientSound(ChromaSounds.PYLONBOOSTSTART, x+0.5, y+0.5, z+0.5, 2, 1, false); //play sound here to ensure sync
	}

	@SideOnly(Side.CLIENT)
	private void rockScreenClient(int ticks) {
		Minecraft.getMinecraft().renderViewEntity.hurtTime = Math.max(Minecraft.getMinecraft().renderViewEntity.hurtTime, ticks);
	}

	public int getSkyTick() {
		return skyTick;
	}

	public int getTick() {
		return ritualTick;
	}

	public Location getLocation() {
		return location;
	}

	public TileEntityCrystalPylon getPylon(World world, int x, int y, int z) {
		int d = 8;
		for (int i = 1; i < d; i++) {
			int dy = y+i;
			if (!world.getBlock(x, dy, z).isAir(world, x, dy, z))
				return null;
		}
		TileEntity tile = world.getTileEntity(x, y+d, z);
		return tile instanceof TileEntityCrystalPylon ? (TileEntityCrystalPylon)tile : null;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean trigger(EntityPlayer ep) {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		if (this.checkPylon(world, x, y, z)) {
			if (this.canPlayerTurbocharge(world, x, y, z, ep)) {
				boolean hasAuxiliaries = this.checkForArrangement(world, x, y, z);
				if (hasAuxiliaries) {
					this.startRitual(world, x, y, z);
					ChromaSounds.PYLONBOOSTSTART.playSoundAtBlock(this, 1, 1);
					return true;
				}
			}
		}
		ChromaSounds.ERROR.playSoundAtBlock(this);
		return false;
	}

	private boolean canPlayerTurbocharge(World world, int x, int y, int z, EntityPlayer ep) {
		TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
		return ProgressionManager.instance.hasPlayerCompletedStructureColor(ep, te.getColor());//ProgressStage.CTM.isPlayerAtStage(ep);
	}

	private boolean checkPylon(World world, int x, int y, int z) {
		TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
		if (te != null) {
			return !te.isEnhanced() && te.getEnergy(te.getColor()) >= (TileEntityCrystalPylon.MAX_ENERGY*3/4) && te.canConduct() && te.getBoosterCrystals(world, x, y+8, z).size() == 8;
		}
		return false;
	}

	private boolean checkForArrangement(World world, int x, int y, int z) {
		for (int i = 0; i < Location.list.length; i++) {
			Location loc = Location.list[i];
			Coordinate c = loc.position.offset(x, y, z);
			ChromaTiles t = ChromaTiles.getTileFromIDandMetadata(c.getBlock(world), c.getBlockMetadata(world));
			if (t != ChromaTiles.PYLONTURBO)
				return false;
			TileEntityPylonTurboCharger te = (TileEntityPylonTurboCharger)c.getTileEntity(world);
			if (!te.canFunction(world, c.xCoord, c.yCoord, c.zCoord))
				return false;
		}
		return true;
	}

	private boolean canFunction(World world, int x, int y, int z) {
		if (world.getBlock(x, y-1, z) == ChromaBlocks.PYLONSTRUCT.getBlockInstance() && world.getBlockMetadata(x, y-1, z) == 5) {
			if (world.getBlock(x, y-2, z) == ChromaBlocks.PYLONSTRUCT.getBlockInstance() && world.getBlockMetadata(x, y-2, z) == 2) {
				if (world.canBlockSeeTheSky(x, y+1, z)) {
					for (int i = -1; i <= 1; i++) {
						for (int k = -1; k <= 1; k++) {
							int dx = x+i;
							int dz = z+k;
							int dy = y+1;
							if (!world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz))
								return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	private void startRitual(World world, int x, int y, int z) {
		ritualTick = RITUAL_LENGTH;
		revTick = 40;

		for (int i = 0; i < Location.list.length; i++) {
			Location loc = Location.list[i];
			Coordinate c = loc.position.offset(x, y, z);
			ChromaTiles t = ChromaTiles.getTileFromIDandMetadata(c.getBlock(world), c.getBlockMetadata(world));
			if (t != ChromaTiles.PYLONTURBO) {
				this.failRitual(world, x, y, z);
				return;
			}
			TileEntityPylonTurboCharger tile = (TileEntityPylonTurboCharger)c.getTileEntity(world);
			tile.location = loc;
		}

		world.getWorldInfo().setRaining(false);

		this.syncAllData(true);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("rtick", ritualTick);
		NBT.setInteger("rvtick", revTick);

		NBT.setInteger("sky", skyTick);

		NBT.setBoolean("complete", isComplete);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		ritualTick = NBT.getInteger("rtick");
		revTick = NBT.getInteger("rvtick");

		skyTick = NBT.getInteger("sky");

		isComplete = NBT.getBoolean("complete");
	}

	@Override
	public void breakBlock() {
		if (isComplete || ritualTick > 0) {
			if (location != null) {
				Coordinate c = location.position.negate().offset(xCoord, yCoord, zCoord);
				TileEntity te = c.getTileEntity(worldObj);
				if (te instanceof TileEntityPylonTurboCharger) {
					((TileEntityPylonTurboCharger)te).failRitual(worldObj, te.xCoord, te.yCoord, te.zCoord);
				}
			}
			else {
				this.failRitual(worldObj, xCoord, yCoord, zCoord);
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(12, 12, 12);
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return 16384D;
	}

	public static enum EventType {
		FLASH(),
		BEAM(),
		SKY(),
		JETS(),
		PARTICLEBURST(),
		LIGHTNING(),
		POTIONS();

		private static final EventType[] list = values();
	}

	public static enum Location {

		N(0, -7),
		NE(5, -5),
		E(7, 0),
		SE(5, 5),
		S(0, 7),
		SW(-5, 5),
		W(-7, 0),
		NW(-5, -5);

		public final Coordinate position;

		public static final Location[] list = values();

		private Location(int x, int z) {
			position = new Coordinate(x, 2, z);
		}

		public Location getNext() {
			return this.ordinal() == list.length-1 ? list[0] : list[this.ordinal()+1];
		}

		public Coordinate getRelativePylonLocation() {
			return position.negate().offset(0, 8, 0);
		}
	}

}
