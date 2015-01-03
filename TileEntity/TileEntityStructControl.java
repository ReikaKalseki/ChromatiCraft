package Reika.ChromatiCraft.TileEntity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Interfaces.BreakAction;
import Reika.DragonAPI.Interfaces.InertIInv;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityStructControl extends InventoriedChromaticBase implements BreakAction, InertIInv {

	private Structures struct;
	private FilledBlockArray blocks;
	private CrystalElement color;
	private final EnumMap<CrystalElement, Coordinate> crystals = new EnumMap(CrystalElement.class);
	private boolean triggered = false;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.STRUCTCONTROL;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);

		if (!triggered) {
			List<EntityPlayer> li = world.playerEntities;
			for (EntityPlayer ep : li) {
				if (ep.boundingBox.intersectsWith(ReikaAABBHelper.getBlockAABB(x, y, z).expand(2, 1, 2))) {
					this.onPlayerProximity(world, x, y, z, ep);
				}
			}
		}
		//triggered = false;
	}

	private void onPlayerProximity(World world, int x, int y, int z, EntityPlayer ep) {
		switch(struct) {
		case CAVERN:
			world.setBlock(x+7, y, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 8, 3);
			world.setBlock(x+7, y-1, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 8, 3);
			ChromaSounds.TRAP.playSound(ep, 2, 1);
			ChromaSounds.TRAP.playSound(ep, 2, 0.5F);
			ProgressStage.CAVERN.stepPlayerTo(ep);
			break;
		case BURROW:
			ProgressStage.BURROW.stepPlayerTo(ep);
			break;
		case OCEAN:
			ProgressStage.OCEAN.stepPlayerTo(ep);
			break;
		default:
			break;
		}
		triggered = true;
	}

	@Override
	public void onFirstTick(World world, int x, int y, int z) {
		this.calcCrystals(world, x, y, z);
		this.syncAllData(true);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		//double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.2);
		//double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.1);
		//double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.2);
		//EntityCenterBlurFX fx = new EntityCenterBlurFX(CrystalElement.WHITE, world, x+0.5, y+0.5, z+0.5, vx, vy, vz);
		for (int i = -1; i <= 1; i += 2) {
			if (!crystals.isEmpty()) {
				CrystalElement e = ReikaJavaLibrary.getRandomListEntry(new ArrayList(crystals.keySet()));
				Coordinate c = crystals.get(e);
				double dd = ReikaMathLibrary.py3d(c.xCoord, c.yCoord, c.zCoord);
				double v = 0.2;
				double vx = -c.xCoord/dd*v;
				double vy = -c.yCoord/dd*v+0.15*i;
				double vz = -c.zCoord/dd*v;
				//ReikaJavaLibrary.pConsole(vx, e == CrystalElement.BROWN && x == 24 && z == 383);
				c = c.offset(x, y, z);
				EntityCenterBlurFX fx = new EntityCenterBlurFX(e, world, c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5, vx, vy, vz);
				fx.setGravity(0.1F*i).setLife(60);
				//fx.noClip = false;
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	public void generate(Structures s, CrystalElement e) {
		if (!s.isNatural())
			throw new IllegalArgumentException("You cannot generate a structure control in the wrong structure!");
		struct = s;
		color = e;
		WeightedRandomChestContent[] loot = ChestGenHooks.getItems(ChestGenHooks.STRONGHOLD_LIBRARY, rand);
		WeightedRandomChestContent.generateChestContents(rand, loot, this, ChestGenHooks.getCount(ChestGenHooks.STRONGHOLD_LIBRARY, rand));
		int n = 1+rand.nextInt(4)*(1+rand.nextInt(2));
		for (int i = 0; i < n; i++) {
			ReikaInventoryHelper.addToIInv(ChromaItems.FRAGMENT.getItemInstance(), this);
		}
		ReikaInventoryHelper.addToIInv(ChromaStacks.cavernLoot, this);
	}

	private void calcCrystals(World world, int x, int y, int z) {
		switch(struct) {
		case CAVERN:
			blocks = ChromaStructures.getCavernStructure(world, x, y, z);
			break;
		case BURROW:
			blocks = ChromaStructures.getBurrowStructure(world, x, y, z);
			break;
		case OCEAN:
			blocks = ChromaStructures.getOceanStructure(world, x, y, z);
			break;
		default:
			break;
		}
		if (blocks != null) {
			for (int i = 0; i < blocks.getSize(); i++) {
				int[] xyz = blocks.getNthBlock(i);
				if (worldObj.getBlock(xyz[0], xyz[1], xyz[2]) == ChromaBlocks.CRYSTAL.getBlockInstance()) {
					Coordinate c = new Coordinate(xyz[0]-xCoord, xyz[1]-yCoord, xyz[2]-zCoord);
					CrystalElement e = CrystalElement.elements[worldObj.getBlockMetadata(xyz[0], xyz[1], xyz[2])];
					crystals.put(e, c);
					//ReikaJavaLibrary.pConsole(c+":"+world.isRemote, e == CrystalElement.RED && x == 24 && z == 383);
				}
			}
		}
	}

	@Override
	public void breakBlock() {
		switch(struct) {
		case CAVERN:
			if (blocks != null) {
				for (int i = 0; i < blocks.getSize(); i++) {
					int[] xyz = blocks.getNthBlock(i);
					if (worldObj.getBlock(xyz[0], xyz[1], xyz[2]) == ChromaBlocks.STRUCTSHIELD.getBlockInstance())
						worldObj.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], worldObj.getBlockMetadata(xyz[0], xyz[1], xyz[2])%8, 3);
				}
			}
			worldObj.setBlockMetadataWithNotify(xCoord+7, yCoord, zCoord, worldObj.getBlockMetadata(xCoord+7, yCoord, zCoord)%8, 3);
			worldObj.setBlockMetadataWithNotify(xCoord+7, yCoord-1, zCoord, worldObj.getBlockMetadata(xCoord+7, yCoord-1, zCoord)%8, 3);
			break;
		default:
			break;
		}
	}

	public int getBrightness() {
		return 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setString("struct", struct.name());
		NBT.setBoolean("trigger", triggered);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		if (NBT.hasKey("struct"))
			struct = Structures.valueOf(NBT.getString("struct"));
		triggered = NBT.getBoolean("trigger");
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 27;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

}
