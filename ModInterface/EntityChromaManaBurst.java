package Reika.ChromatiCraft.ModInterface;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ModInterface.TileEntityManaBooster.ManaPath;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Base.ParticleEntity;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;



public class EntityChromaManaBurst extends ParticleEntity implements IEntityAdditionalSpawnData {

	private boolean isAccelerated;
	private ManaPath path;

	private boolean hitBooster;
	private int pathTick = 0;
	private int boosterEntryTick = 0;
	private int boosterCollectionTick = 0;
	private int boosterExitTick = 0;
	private boolean hitTarget;
	private int targetTick;

	private int amount;
	private int startingAmount;
	private int targetAmount;

	public EntityChromaManaBurst(World world, int amt, ManaPath path, boolean accel) {
		super(world, MathHelper.floor_double(path.pathToBooster.get(0).xCoord), MathHelper.floor_double(path.pathToBooster.get(0).yCoord), MathHelper.floor_double(path.pathToBooster.get(0).zCoord));
		this.setPosition(path.pathToBooster.get(0));
		this.setStartingAmount(amt);
		this.path = path;
		isAccelerated = accel;
		this.extractMana();
	}

	public EntityChromaManaBurst(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		dataWatcher.addObject(24, 0);
		dataWatcher.addObject(25, 0);

		dataWatcher.addObject(26, 0F);
		dataWatcher.addObject(27, 0F);
		dataWatcher.addObject(28, 0F);
	}

	private void setStartingAmount(int amt) {
		startingAmount = amt;
		this.setAmount(amt);
		targetAmount = amt*TileEntityManaBooster.BOOST_FACTOR;
		dataWatcher.updateObject(25, startingAmount);
	}

	private void setAmount(int amt) {
		amount = amt;
		dataWatcher.updateObject(24, amount);
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		super.writeSpawnData(buf);

		buf.writeInt(startingAmount);
		buf.writeBoolean(isAccelerated);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		super.readSpawnData(buf);

		this.setStartingAmount(buf.readInt());
		isAccelerated = buf.readBoolean();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);

		amount = tag.getInteger("mana");
		startingAmount = tag.getInteger("starting");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);

		tag.setInteger("mana", amount);
		tag.setInteger("starting", startingAmount);
	}

	@Override
	protected void onTick() {
		if (!worldObj.isRemote) {
			if (path != null) {
				if (hitTarget) {
					targetTick++;
					if (targetTick > 5)
						this.setDead();
				}
				else {
					if (hitBooster) {
						if (amount < targetAmount) {
							//ReikaJavaLibrary.pConsole(amount+" of "+targetAmount);
							this.setAmount(amount+Math.max(1, (targetAmount-amount)/(isAccelerated ? 12 : 24)));

							/*
						double dx = posX-path.boosterCenter.xCoord;
						double dy = posY-path.boosterCenter.yCoord;
						double dz = posZ-path.boosterCenter.zCoord;
						double v = 0;//0.0625;
						motionX -= dx*v;//+ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
						motionY -= dy*v;//+ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
						motionZ -= dz*v;//+ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
							 */

							if (boosterEntryTick > 0) {
								DecimalPosition p = this.getBoosterOrbitPosition();
								this.setPosition(p);
								boosterEntryTick++;
							}
							else {
								DecimalPosition p = this.getBoosterEntryPosition();
								if (boosterCollectionTick >= 6) {
									this.setPosition(p);
									boosterEntryTick++;
								}
								else {
									boosterCollectionTick++;
									p = p.interpolate(path.boosterEntry, p, boosterCollectionTick/6D);
									this.setPosition(p);
								}
							}

							/*
						double[] angs = ReikaPhysicsHelper.cartesianToPolar(posX-path.boosterCenter.xCoord, posY-path.boosterCenter.yCoord, posZ-path.boosterCenter.zCoord);
						double d = 0.75;//angs[0]+(0.75-angs[0])*0.03125;
						double ang1 = angs[1]+2.2*2;//(ticksExisted*2.2+this.getEntityId())%360;
						double ang2 = -angs[2]+1.3*2;//(ticksExisted*1.3-this.getEntityId()*1.6)%360;
						double[] xyz = ReikaPhysicsHelper.polarToCartesian(d, ang1, ang2);
						DecimalPosition p = new DecimalPosition(path.boosterCenter.offset(xyz[0], xyz[1]-0.375, xyz[2]));
						this.setPosition(p);
							 */

							//this.setPosition(path.boosterCenter);
						}
						else {
							DecimalPosition p = path.pathToPool.get(pathTick);
							if (boosterEntryTick == -1 || boosterExitTick >= 6) {
								boosterEntryTick = -1;
								this.setPosition(p);
								pathTick++;
								if (pathTick == path.pathToBooster.size()) {
									this.dumpMana();
								}
							}
							else {
								boosterExitTick++;
								p = p.interpolate(this.getBoosterOrbitPosition(), path.boosterExit, boosterExitTick/6D);
								this.setPosition(p);
							}
						}
					}
					else {
						DecimalPosition p = path.pathToBooster.get(pathTick);
						this.setPosition(p);
						pathTick++;
						if (pathTick == path.pathToBooster.size()) {
							hitBooster = true;
							pathTick = 0;
							//this.setPosition(path.boosterCenter);
							motionX = motionY = motionZ = 0;
						}
					}
				}
				velocityChanged = true;

				dataWatcher.updateObject(26, (float)posX);
				dataWatcher.updateObject(27, (float)posY);
				dataWatcher.updateObject(28, (float)posZ);
			}
			else {
				ReikaJavaLibrary.pConsole("Clearing pulse, no path");
				this.setDead();
			}
		}
		else {
			amount = dataWatcher.getWatchableObjectInt(24);

			posX = dataWatcher.getWatchableObjectFloat(26);
			posY = dataWatcher.getWatchableObjectFloat(27);
			posZ = dataWatcher.getWatchableObjectFloat(28);
		}
	}

	private DecimalPosition getBoosterEntryPosition() {
		double ang1 = (0*2.2+this.getEntityId())%360;
		double ang2 = (0*1.3-this.getEntityId()*1.6)%360;
		double[] xyz = ReikaPhysicsHelper.polarToCartesian(0.75, ang1, ang2);
		return new DecimalPosition(path.boosterCenter.offset(xyz[0], xyz[1], xyz[2]));
	}

	private DecimalPosition getBoosterOrbitPosition() {
		double ang1 = (boosterEntryTick*2.2*4+this.getEntityId())%360;
		double ang2 = (boosterEntryTick*1.3*4-this.getEntityId()*1.6)%360;
		double[] xyz = ReikaPhysicsHelper.polarToCartesian(0.75, ang1, ang2);
		return new DecimalPosition(path.boosterCenter.offset(xyz[0], xyz[1], xyz[2]));
	}

	private void setPosition(DecimalPosition p) {
		double v = 0.125;
		motionX = (p.xCoord-posX)*v;
		motionY = (p.yCoord-posY)*v;
		motionZ = (p.zCoord-posZ)*v;
		posX = p.xCoord;
		posY = p.yCoord;
		posZ = p.zCoord;
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
	}

	public int getMana() {
		return amount;
	}

	public float getRenderSize() {
		float base = dataWatcher.getWatchableObjectInt(25);
		float basefrac = (float)Math.pow(base/16F, 0.25)/1.25F; //scales from 0.4 @ 1 mana to 0.8 @ 16 mana to 1.6 @ 256 mana
		float frac = this.getMana()/base/3F; //scales from 1 to 2 as it grows
		return frac*basefrac;
	}

	private void extractMana() {
		TileEntityManaBooster.receiveMana(worldObj, path.manaSource, startingAmount, true);
		ChromaSounds.FIRE.playSound(this, 0.1875F, 0.75F+rand.nextFloat()*0.5F);
	}

	private void dumpMana() {
		TileEntityManaBooster.dumpMana(worldObj, path.manaTarget, amount, true);
		//TileEntityManaBooster.receiveMana(worldObj, path.manaSource, startingAmount, true);
		ChromaSounds.GUISEL.playSound(this, 2F, 0.5F);
		this.setPosition(new DecimalPosition(path.manaTarget));
		hitTarget = true;
	}

	@Override
	public double getRenderRangeSquared() {
		return 4096;
	}

	@Override
	public double getHitboxSize() {
		return 0.125;
	}

	@Override
	public boolean despawnOverTime() {
		return false;
	}

	@Override
	public boolean despawnOverDistance() {
		return false;
	}

	@Override
	public boolean canInteractWithSpawnLocation() {
		return false;
	}

	@Override
	public double getSpeed() {
		return 0;
	}

	@Override
	protected boolean dieOnNoVelocity() {
		return false;
	}

	@Override
	protected boolean onEnterBlock(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public void applyEntityCollision(Entity e) {

	}
}
