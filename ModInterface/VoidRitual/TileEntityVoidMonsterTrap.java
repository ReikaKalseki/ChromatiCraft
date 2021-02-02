package Reika.ChromatiCraft.ModInterface.VoidRitual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.Linkable;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalReceiver;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.TileEntity.TileEntityLumenWire;
import Reika.ChromatiCraft.TileEntity.TileEntityLumenWire.WireWatcher;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Instantiable.Data.Collections.ThreadSafeSet;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Math.VariableEndpointSpline;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Interfaces.TileEntity.ChunkLoadingTile;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.VoidMonster.API.MonsterAPI;
import Reika.VoidMonster.Entity.EntityVoidMonster;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityVoidMonsterTrap extends InventoriedCrystalReceiver implements MultiBlockChromaTile, WireWatcher, LocationCached,
Linkable, ChunkLoadingTile, BreakAction, InertIInv {

	private static final ElementTagCompound required = new ElementTagCompound();

	private static final Collection<WorldLocation> cache = new ThreadSafeSet();

	private static final int RING_DURATION = 400;

	private Collection<Coordinate> wires;
	private ArrayList<Coordinate> wireSeek = new ArrayList();
	private ArrayList<Coordinate> explosionSeek = new ArrayList();
	private ConcurrentHashMap<Integer, VoidMonsterTether> tethers = new ConcurrentHashMap();

	private float flashFactor = 0;
	private float shaderRotation = 0;
	private float shaderRotationSpeed = 0;

	private int outerRingActivation = 0;
	private int innerRingActivation = 0;

	private boolean hasStructure;
	private WorldLocation link;
	private VoidMonsterDestructionRitual ritual;

	static {
		required.addTag(CrystalElement.BLACK, 5);
		required.addTag(CrystalElement.PINK, 20);
		required.addTag(CrystalElement.LIGHTGRAY, 4);
		required.addTag(CrystalElement.GRAY, 1);
		required.addTag(CrystalElement.WHITE, 10);
		required.addTag(CrystalElement.MAGENTA, 2);
	}

	public static ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	public void activateOuterRing() {
		outerRingActivation = RING_DURATION;
	}

	public void activateInnerRing() {
		if (outerRingActivation > 0) {
			outerRingActivation = RING_DURATION;
			innerRingActivation = RING_DURATION;
		}
	}

	public void validateStructure() {
		ChromaStructures s = this.getPrimaryStructure();
		s.getStructure().resetToDefaults();
		hasStructure = s.getArray(worldObj, xCoord, yCoord, zCoord).matchInWorld();
	}

	public boolean canAttractMonster() {
		return hasStructure && (link != null || !this.isNether()) && !this.isActive();
	}

	public boolean isNether() {
		return worldObj != null && worldObj.provider.dimensionId == -1;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (this.isNether()) {
			explosionSeek.addAll(VoidMonsterNetherStructure.getTNTLocations());
		}
		else {
			wires = VoidMonsterRitualStructure.getWireLocations();
			wireSeek.addAll(wires);
		}
		WorldLocation loc = new WorldLocation(this);
		if (!cache.contains(loc))
			cache.add(loc);
		this.validateStructure();
		ChunkManager.instance.loadChunks(this);
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		ChunkManager.instance.unloadChunks(this);
	}

	@Override
	public Collection<ChunkCoordIntPair> getChunksToLoad() {
		return ChunkManager.instance.getChunkSquare(xCoord, yCoord, 2);
	}

	@Override
	public void breakBlock() {
		WorldLocation loc = new WorldLocation(this);
		cache.remove(loc);
		if (ritual != null) {
			ritual.onPrematureTermination();
			ritual = null;
		}
		this.resetOther();
		this.reset();
	}

	private void checkAndRequest() {
		for (CrystalElement e : required.elementSet()) {
			int amt = this.getRemainingSpace(e);
			if (amt > 0) {
				boolean ret = this.requestEnergy(e, amt);
			}
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote && this.hasStructure() && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}

		if (!world.isRemote && !hasStructure && this.getTicksExisted() < 5) {
			this.validateStructure();
		}

		if (!world.isRemote) {
			if (inv[0] == null || inv[0].stackSize < this.getInventoryStackLimit()) {
				this.searchForItems(world, x, y, z);
			}
			if (explosionSeek.size() < 4) {
				hasStructure = false;
				explosionSeek.clear();
				explosionSeek.addAll(VoidMonsterNetherStructure.getTNTLocations());
			}
			if (this.isNether()) {
				if (this.canAttractMonster()) {
					this.attractMonster(world, x, y, z);
				}
			}
			else {
				if (!wireSeek.isEmpty()) {
					int idx = rand.nextInt(wireSeek.size());
					Coordinate c = wireSeek.get(idx);
					if (c.getBlock(world) == ChromaTiles.LUMENWIRE.getBlock() && c.getBlockMetadata(world) == ChromaTiles.LUMENWIRE.getBlockMetadata()) {
						TileEntityLumenWire te = (TileEntityLumenWire)c.getTileEntity(world);
						te.addWatcher(this);
					}
				}
				EntityLiving e = this.getMonster(world, x, y, z);
				if (e != null) {
					if (ReikaItemHelper.matchStacks(inv[0], ChromaStacks.voidmonsterEssence)) {
						if (energy.containsAtLeast(required)) {
							if (this.isActive()) {
								if (ritual.tick()) {
									ritual.onCompletion();
									ritual = null;
								}
							}
							else {
								if (this.canAttractMonster()) {
									double dist = this.attractMonster(world, x, y, z);
									if (dist < 1) {
										this.activate(world, x, y, z, e);
									}
								}
							}
							this.drainEnergy(required);
							if (rand.nextInt(this.isActive() ? 20 : 60) == 0)
								ReikaInventoryHelper.decrStack(0, inv);
						}
					}
				}
			}
		}
		else {
			for (VoidMonsterTether t : tethers.values()) {
				if (t.tick(world)) {
					this.removeTether(t, world);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doEatFX(World world, int x, int y, int z) {
		for (int i = 0; i < 20; i++) {
			int l = 20+rand.nextInt(60);
			float s = 1.5F+rand.nextFloat()*2.5F;
			int c1 = 0xdf3fff;
			int c2 = 0x202020;
			double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.25);
			double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.25);
			double dy = y+rand.nextDouble();
			float f = (float)ReikaRandomHelper.getRandomPlusMinus(0.6, 0.2);
			double vel = ReikaRandomHelper.getRandomBetween(0.0625, 0.25);
			double[] v = ReikaPhysicsHelper.polarToCartesian(vel, rand.nextDouble()*360, rand.nextDouble()*360);
			EntityFX fx1 = new EntityCCBlurFX(world, dx, dy, dz, v[0], v[1], v[2]).setIcon(ChromaIcons.TRANSFADE).setScale(s).setLife(l).setColor(c1).setBasicBlend();
			EntityFX fx2 = new EntityCCBlurFX(world, dx, dy, dz, v[0], v[1], v[2]).setIcon(ChromaIcons.TRANSFADE).setScale(s*f).setLife(l).setColor(c2).setBasicBlend().lockTo(fx1);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	private void searchForItems(World world, int x, int y, int z) {
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(this).expand(8, 5, 8);
		List<EntityItem> li = world.selectEntitiesWithinAABB(EntityItem.class, box, new ReikaEntityHelper.SpecificItemSelector(ChromaStacks.voidmonsterEssence));
		double v = 2.5;
		for (EntityItem ent : li) {
			if (ent.ticksExisted > 5) {
				//Vec3 i2vac = ReikaVectorHelper.getVec2Pt(ent.posX, ent.posY, ent.posZ, x+0.5, y+0.5, z+0.5);
				//if (ReikaWorldHelper.canBlockSee(world, x, y, z, ent.posX, ent.posY, ent.posZ, this.getRange()+2)) {
				double dx = (x+0.5 - ent.posX);
				double dy = (y+0.5 - ent.posY);
				double dz = (z+0.5 - ent.posZ);
				double ddt = ReikaMathLibrary.py3d(dx, dy, dz);
				if (ddt < 0.5) {
					ItemStack is = ent.getEntityItem();
					ItemStack is2 = ReikaItemHelper.getSizedItemStack(is, 1);
					if (ReikaInventoryHelper.addOrSetStack(is2, inv, 0)) {
						is.stackSize--;
						if (is.stackSize <= 0) {
							ent.setDead();
						}
						else {
							ent.setEntityItemStack(is);
						}
						ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.VOIDTRAPEAT.ordinal(), this, 32);
						ChromaSounds.BOUNCE.playSoundAtBlock(this, 1, 2F);
					}
				}
				else {
					double vx = v*dx/ddt;
					double vy = v*dy/ddt;
					double vz = v*dz/ddt;
					double vmax = 0.0625;
					double f = Math.min(1, ent.ticksExisted/80D);
					vx = MathHelper.clamp_double(vx, -vmax, vmax);
					vy = MathHelper.clamp_double(vy, -vmax, vmax);
					vz = MathHelper.clamp_double(vz, -vmax, vmax);
					ent.motionX = ent.motionX*(1-f)+vx*f;
					ent.motionY = ent.motionY*(1-f)+vy*f;
					ent.motionZ = ent.motionZ*(1-f)+vz*f;
					if (ent.posY < y)
						ent.motionY += 0.125;
					if (!world.isRemote)
						ent.velocityChanged = true;
				}
			}
		}
	}

	private EntityLiving getMonster(World world, int x, int y, int z) {
		return ritual != null ? ritual.getEntity() : MonsterAPI.getNearestMonster(world, x+0.5, y+0.5, z+0.5);
	}

	private void activate(World world, int x, int y, int z, EntityLiving e) {
		ritual = new VoidMonsterDestructionRitual(this, e);
		ReikaJavaLibrary.pConsole("Activating ritual with "+this+" "+e);
	}

	@ModDependent(ModList.VOIDMONSTER)
	private double attractMonster(World world, int x, int y, int z) {
		EntityVoidMonster e = (EntityVoidMonster)this.getMonster(world, x, y, z);
		if (e == null)
			return Double.POSITIVE_INFINITY;
		VoidMonsterTether t = this.getOrCreateTether(e);
		double dist = e.getDistanceSq(x+0.5, y+0.5, z+0.5);
		if (this.isNether()) {
			if (dist > 256) {
				this.removeTether(t, world);
				return dist;
			}
			t.setDistance(e.moveTowards(x+0.5, y-0.5, z+0.5, Math.min(1, 20/dist)));
			return t.distance;
		}
		else {
			if (dist > 64) {
				this.removeTether(t, world);
				return dist;
			}
			double s = 1.5;//innerRingActivation > 0 ? 1.5 : 0.25;
			t.setDistance(e.moveTowards(x+0.5, y-0.5, z+0.5, s*Math.min(1, 20/dist)));
			return t.distance;
		}
	}

	private void removeTether(VoidMonsterTether t, World world) {
		tethers.remove(t.ID);
		Entity e = t.getEntity(world);
		if (e != null)
			;//world.newExplosion(e, e.posX, e.posY, e.posZ, 4, true, false);
	}

	private VoidMonsterTether getOrCreateTether(Entity e) {
		VoidMonsterTether t = tethers.get(e.getEntityId());
		if (t == null) {
			t = new VoidMonsterTether(this, e);
			tethers.put(t.ID, t);
			this.syncAllData(true);
		}
		return t;
	}

	public Collection<VoidMonsterTether> getTethers() {
		return Collections.unmodifiableCollection(tethers.values());
	}

	@ModDependent(ModList.VOIDMONSTER)
	private void triggerTeleport() {
		EntityVoidMonster e = (EntityVoidMonster)MonsterAPI.getNearestMonster(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5);
		if (e == null)
			return;
		e.forcePersist = true;
		e.forceSpawn = true;
		MonsterTeleporter tel = new MonsterTeleporter((WorldServer)link.getWorld(), link, 24, 60, 6);
		ReikaEntityHelper.transferEntityToDimension(e, link.dimensionID, tel);
		worldObj.newExplosion(null, xCoord+0.5, yCoord-0.5, zCoord+0.5, 9, true, true);
		this.delete();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (world != null && world.isRemote) {
			if (flashFactor > 0) {
				flashFactor = Math.max(flashFactor*0.92F-0.09F, 0);
			}
			else if (rand.nextInt(70) == 0) {
				flashFactor = 2;
			}

			if (shaderRotationSpeed > 0) {
				boolean flag = shaderRotation < 0;
				shaderRotation += shaderRotationSpeed;
				if (shaderRotation >= 1) {
					shaderRotation = -1;
				}
				else if (flag && shaderRotation >= 0) {
					shaderRotation = 0;
					shaderRotationSpeed = 0;
				}
			}
			else if (rand.nextInt(40) == 0) {
				shaderRotationSpeed = 0.0625F+rand.nextFloat()*0.0625F;
			}
		}
	}

	public float getFlashBrightness() {
		if (flashFactor <= 0 || flashFactor > 2)
			return 0;
		return flashFactor <= 1 ? flashFactor : 2-flashFactor;
	}

	public float getShaderRotation() {
		return shaderRotation;
	}

	public boolean isActive() {
		return ritual != null;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return ChromaStacks.voidmonsterEssence.getMaxStackSize();
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.VOIDTRAP;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(this).expand(2, 2, 2);
	}

	@Override
	public void onToggle(Coordinate wire, boolean active) {
		if (wire.yCoord == yCoord-1) {
			this.activateInnerRing();
		}
		else {
			this.activateOuterRing();
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("struct");

		NBTTagList li = NBT.getTagList("tethers", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			VoidMonsterTether t = VoidMonsterTether.readFromNBT((NBTTagCompound)o);
			VoidMonsterTether has = tethers.get(t.ID);
			if (has == null)
				tethers.put(t.ID, t);
			else
				has.copyFrom(t);
		}

		link = NBT.hasKey("link") ? WorldLocation.readFromNBT("link", NBT) : null;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);

		NBTTagList li = new NBTTagList();
		for (Entry<Integer, VoidMonsterTether> e : tethers.entrySet()) {
			li.appendTag(e.getValue().writeToNBT());
		}
		NBT.setTag("tethers", li);

		if (link != null) {
			link.writeToNBT("link", NBT);
		}
	}

	@Override
	public int getPacketDelay() {
		int base = super.getPacketDelay();
		return tethers.isEmpty() ? base : base/2;
	}

	public boolean hasStructure() {
		return hasStructure;
	}

	@Override
	public ChromaStructures getPrimaryStructure() {
		return this.isNether() ? ChromaStructures.NETHERTRAP : ChromaStructures.VOIDRITUAL;
	}

	@Override
	public Coordinate getStructureOffset() {
		return null;
	}

	@Override
	public boolean canStructureBeInspected() {
		return true;
	}

	public static boolean handleTNTTrigger(World world, Entity e) {
		Iterator<WorldLocation> it = cache.iterator();
		while (it.hasNext()) {
			WorldLocation loc = it.next();
			if (loc.dimensionID == world.provider.dimensionId) {
				TileEntity te = loc.getTileEntity(world);
				if (te instanceof TileEntityVoidMonsterTrap) {
					if (((TileEntityVoidMonsterTrap)te).handleTNTTrigger(e))
						return true;
				}
				else {
					it.remove();
					ChromatiCraft.logger.logError("Incorrect tile ("+te+") @ "+loc+" (with "+loc.getBlockKey(world)+") in Void Trap cache!?");
					if (loc.getBlock(world) == ChromaTiles.VOIDTRAP.getBlock() && loc.getBlockMetadata(world) == ChromaTiles.VOIDTRAP.getBlockMetadata()) {
						ChromatiCraft.logger.logError("Void Trap block and meta but no TileEntity!?!?");
					}
				}
			}
		}
		return false;
	}

	private boolean handleTNTTrigger(Entity e) {
		//ReikaJavaLibrary.pConsole(e+" and "+link+" and "+this.hasStructure, yCoord == 133);
		if (this.isNether() && (hasStructure || explosionSeek.size() < 4) && link != null && ModList.VOIDMONSTER.isLoaded()) {
			Coordinate c = new Coordinate(e).offset(-xCoord+2, -yCoord+6, -zCoord+2);
			//ReikaJavaLibrary.pConsole(c+" of "+explosionSeek.size()+":"+explosionSeek+" for "+e);
			if (explosionSeek.contains(c)) {
				explosionSeek.remove(c);
				hasStructure = false;
				if (explosionSeek.isEmpty()) {
					this.triggerTeleport();
				}
				return true;
			}
		}
		return false;
	}

	public static void clearCache() {
		cache.clear();
	}

	@Override
	public void reset() {
		link = null;
	}

	@Override
	public void resetOther() {
		if (link == null)
			return;
		TileEntity te = link.getTileEntity();
		if (te instanceof Linkable) {
			((Linkable)te).reset();
		}
	}

	@Override
	public boolean tryConnect(World world, int x, int y, int z) {
		if (ChromaTiles.getTile(world, x, y, z) == this.getTile()) {
			TileEntityVoidMonsterTrap te = (TileEntityVoidMonsterTrap)world.getTileEntity(x, y, z);
			te.link = new WorldLocation(this);
			link = new WorldLocation(te);
			return true;
		}
		return false;
	}

	@Override
	public boolean isEmitting() {
		return true;
	}

	@Override
	public WorldLocation getConnection() {
		return link;
	}

	@Override
	public boolean hasValidConnection() {
		return link != null && link.getTileEntity() instanceof TileEntityVoidMonsterTrap;
	}

	@Override
	public int getReceiveRange() {
		return 24;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int maxThroughput() {
		return 100;
	}

	@Override
	public boolean canConduct() {
		return hasStructure;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return required.getValue(e)*600;
	}

	private static class MonsterTeleporter extends Teleporter {

		private final WorldLocation target;
		private final double minFuzzRadius;
		private final double maxFuzzRadius;
		private final double maxFuzzY;

		private final World world;

		public MonsterTeleporter(WorldServer world, WorldLocation loc, double r, double y) {
			this(world, loc, r, r, y);
		}

		public MonsterTeleporter(WorldServer world, WorldLocation loc, double r1, double r2, double y) {
			super(world);
			target = loc;
			this.world = world;
			minFuzzRadius = r1;
			maxFuzzRadius = r2;
			maxFuzzY = y;
		}

		@Override
		public void placeInPortal(Entity e, double x, double y, double z, float facing) {
			double r = ReikaRandomHelper.getRandomBetween(minFuzzRadius, maxFuzzRadius);
			double ang = Math.toRadians(rand.nextDouble()*360);
			double dx = r*Math.cos(ang);
			double dz = r*Math.sin(ang);
			double dy = ReikaRandomHelper.getRandomPlusMinus(0, maxFuzzY);
			double dr = 1.5;
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(dx-dr, dy-dr, dz-dr, dx+dr, dy+dr, dz+dr);
			while (!world.getCollidingBoundingBoxes(e, box).isEmpty()) {
				r = ReikaRandomHelper.getRandomBetween(minFuzzRadius, maxFuzzRadius);
				ang = Math.toRadians(rand.nextDouble()*360);
				dx = r*Math.cos(ang);
				dz = r*Math.sin(ang);
				dy = ReikaRandomHelper.getRandomPlusMinus(0, maxFuzzY);
			}
			e.setLocationAndAngles(target.xCoord+0.5+dx, target.yCoord+0.5+dy, target.zCoord+0.5+dz, e.rotationYaw, e.rotationPitch);
			this.placeInExistingPortal(e, x, y, z, facing);
		}

		@Override
		public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float facing) {
			return true;
		}

		private void makeReturnPortal(World world, int x, int y, int z) {

		}

		@Override
		public boolean makePortal(Entity e) {
			return false;
		}

	}

	public static class VoidMonsterTether {

		public final int ID;
		public final Coordinate location;

		private double distance;
		private final VariableEndpointSpline[] splines = new VariableEndpointSpline[3];

		VoidMonsterTether(TileEntity te, Entity e) {
			this(new Coordinate(te), e.getEntityId(), new DecimalPosition(e));
		}

		private VoidMonsterTether(Coordinate c, int id, DecimalPosition p2) {
			location = c;
			ID = id;
			for (int i = 0; i < splines.length; i++) {
				splines[i] = new VariableEndpointSpline(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5, p2.xCoord, p2.yCoord, p2.zCoord, SplineType.CENTRIPETAL, 6, 1.2, 0.125);
			}
		}

		public boolean tick(World world) {
			Entity e = this.getEntity(world);
			if (e == null)
				return true;
			for (int i = 0; i < splines.length; i++) {
				splines[i].setEndpoint(e.posX, e.posY+e.height/2, e.posZ);
				splines[i].tick();
			}
			return false;
		}

		public void copyFrom(VoidMonsterTether t) {
			distance = t.distance;
		}

		public Collection<VariableEndpointSpline> getSplines() {
			return Arrays.asList(splines);
		}

		private void setDistance(double d) {
			distance = d;
		}

		public double getDistance() {
			return distance;
		}

		public Entity getEntity(World world) {
			return world.getEntityByID(ID);
		}

		private NBTTagCompound writeToNBT() {
			NBTTagCompound ret = new NBTTagCompound();
			ret.setInteger("id", ID);
			ret.setDouble("dist", distance);
			location.writeToTag(ret);
			return ret;
		}

		private static VoidMonsterTether readFromNBT(NBTTagCompound tag) {
			VoidMonsterTether ret = new VoidMonsterTether(Coordinate.readTag(tag), tag.getInteger("id"), new DecimalPosition(0, 0, 0));
			ret.distance = tag.getDouble("dist");
			return ret;
		}

	}

}
