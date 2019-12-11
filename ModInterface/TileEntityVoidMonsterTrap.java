package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.Linkable;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityLumenWire;
import Reika.ChromatiCraft.TileEntity.TileEntityLumenWire.WireWatcher;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Collections.ThreadSafeSet;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.VoidMonster.API.MonsterAPI;
import Reika.VoidMonster.Entity.EntityVoidMonster;


public class TileEntityVoidMonsterTrap extends ChargedCrystalPowered implements MultiBlockChromaTile, WireWatcher, LocationCached, Linkable {

	private static final ElementTagCompound required = new ElementTagCompound();

	private static final Collection<WorldLocation> cache = new ThreadSafeSet();

	private static final int RING_DURATION = 400;

	private Collection<Coordinate> wires;
	private ArrayList<Coordinate> wireSeek = new ArrayList();
	private ArrayList<Coordinate> explosionSeek = new ArrayList();

	private float flashFactor = 0;
	private float shaderRotation = 0;
	private float shaderRotationSpeed = 0;

	private int outerRingActivation = 0;
	private int innerRingActivation = 0;

	private int ritualTick;
	private boolean hasStructure;
	private WorldLocation link;

	static {
		required.addTag(CrystalElement.BLACK, 5);
		required.addTag(CrystalElement.PINK, 20);
		required.addTag(CrystalElement.LIGHTGRAY, 4);
		required.addTag(CrystalElement.GRAY, 1);
		required.addTag(CrystalElement.WHITE, 10);
		required.addTag(CrystalElement.MAGENTA, 2);
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
		return innerRingActivation > 0;
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
	}

	@Override
	public void breakBlock() {
		WorldLocation loc = new WorldLocation(this);
		cache.remove(loc);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
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
				if (ReikaItemHelper.matchStacks(inv[0], ChromaStacks.voidDust)) {
					if (this.hasEnergy(required)) {
						if (this.isActive()) {

						}
						else {
							if (this.canAttractMonster()) {
								this.attractMonster(world, x, y, z);
							}
						}
						this.useEnergy(required);
						if (rand.nextInt(this.isActive() ? 20 : 60) == 0)
							ReikaInventoryHelper.decrStack(1, inv);
					}
				}
			}
		}
	}

	@ModDependent(ModList.VOIDMONSTER)
	private void attractMonster(World world, int x, int y, int z) {
		if (world.isRemote)
			return;
		EntityVoidMonster e = (EntityVoidMonster)MonsterAPI.getNearestMonster(world, x+0.5, y+0.5, z+0.5);
		e.moveTowards(x+0.5, this.isNether() ? y-0.5 : y+0.5, z+0.5, 1.5);
	}

	@ModDependent(ModList.VOIDMONSTER)
	private void triggerTeleport() {
		Entity e = MonsterAPI.getNearestMonster(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5);
		MonsterTeleporter tel = new MonsterTeleporter((WorldServer)worldObj, link, 24, 60, 6);
		ReikaEntityHelper.transferEntityToDimension(e, link.dimensionID, tel);
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
		return ritualTick > 0;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 8;
	}

	@Override
	public float getCostModifier() {
		return this.isActive() ? 2 : 1;
	}

	@Override
	public boolean usesColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	protected boolean canExtractOtherItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	protected boolean isItemValidForOtherSlot(int slot, ItemStack is) {
		return slot == 1 && ReikaItemHelper.matchStacks(is, ChromaStacks.voidDust);
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		return required.copy();
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
	public void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("struct");
		ritualTick = NBT.getInteger("rtick");
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);
		NBT.setInteger("rtick", ritualTick);
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

	public static void handleTNTTrigger(World world, Entity e) {
		Iterator<WorldLocation> it = cache.iterator();
		while (it.hasNext()) {
			WorldLocation loc = it.next();
			if (loc.dimensionID == world.provider.dimensionId) {
				TileEntity te = loc.getTileEntity(world);
				if (te instanceof TileEntityVoidMonsterTrap) {
					if (((TileEntityVoidMonsterTrap)te).handleTNTTrigger(e))
						return;
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
	}

	private boolean handleTNTTrigger(Entity e) {
		if (this.isNether() && hasStructure && link != null && ModList.VOIDMONSTER.isLoaded()) {
			Coordinate c = new Coordinate(e);
			if (explosionSeek.contains(c)) {
				explosionSeek.remove(c);
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

	@Override
	public void reset() {
		link = null;
	}

	@Override
	public void resetOther() {
		TileEntity te = link.getTileEntity();
		if (te instanceof Linkable) {
			((Linkable)te).reset();
		}
	}

	@Override
	public boolean connectTo(World world, int x, int y, int z) {
		if (ChromaTiles.getTile(world, x, y, z) == this.getTile()) {
			TileEntityVoidMonsterTrap te = (TileEntityVoidMonsterTrap)world.getTileEntity(x, y, z);
			te.link = new WorldLocation(this);
			link = new WorldLocation(te);
			return true;
		}
		return false;
	}

}
