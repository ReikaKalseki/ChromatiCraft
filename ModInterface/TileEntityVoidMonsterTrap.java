package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityLumenWire;
import Reika.ChromatiCraft.TileEntity.TileEntityLumenWire.WireWatcher;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class TileEntityVoidMonsterTrap extends ChargedCrystalPowered implements MultiBlockChromaTile, WireWatcher {

	private static final ElementTagCompound required = new ElementTagCompound();

	private static final int RING_DURATION = 400;

	private Collection<Coordinate> wires;
	private ArrayList<Coordinate> wireSeek = new ArrayList();

	private float flashFactor = 0;
	private float shaderRotation = 0;
	private float shaderRotationSpeed = 0;

	private int outerRingActivation = 0;
	private int innerRingActivation = 0;

	private int ritualTick;
	private boolean hasStructure;

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
		return worldObj.provider.dimensionId == -1;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		wires = VoidMonsterRitualStructure.getWireLocations();
		wireSeek.addAll(wires);
		this.validateStructure();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
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

						}
					}
					this.useEnergy(required);
					if (rand.nextInt(this.isActive() ? 20 : 60) == 0)
						ReikaInventoryHelper.decrStack(1, inv);
				}
			}
		}
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

}
