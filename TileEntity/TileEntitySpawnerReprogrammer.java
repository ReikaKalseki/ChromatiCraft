/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import Reika.ChromatiCraft.API.ProgrammableSpawner;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Extras.ItemSpawner;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaSpawnerHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import java.util.ArrayList;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEntitySpawnerReprogrammer extends InventoriedChromaticBase {

	private String selectedMob;
	private static final ArrayList<String> disallowedMobs = new ArrayList();

	private StepTimer progress = new StepTimer(180);
	public int progressTimer;

	static {
		addDisallowedMob(EntityWither.class);
		addDisallowedMob(EntityDragon.class);
		addDisallowedMob(EntityGiantZombie.class);
		addDisallowedMob(EntityLiving.class);
		addDisallowedMob(EntityMob.class);
		//addDisallowedMob(EntityIronGolem.class);

		addDisallowedMob("Void Monster");

		addDisallowedMob("TaintedCow");
		addDisallowedMob("TaintedSheep");
		addDisallowedMob("TaintedCreeper");
		addDisallowedMob("TaintSpider");
		addDisallowedMob("TaintedChicken");
		addDisallowedMob("TaintedPig");
		addDisallowedMob("TaintedVillager");
		addDisallowedMob("TaintVillager");
		addDisallowedMob("Thaumcraft.TaintacleTiny");
		addDisallowedMob("Thaumcraft.TaintSpore");
		addDisallowedMob("Thaumcraft.Golem");
		addDisallowedMob("Thaumcraft.TaintSwarmer");
		addDisallowedMob("Thaumcraft.TravelingTrunk");
		addDisallowedMob("Thaumcraft.TaintedVillager");
		addDisallowedMob("Thaumcraft.TaintSpider");
		addDisallowedMob("Thaumcraft.TaintedChicken");
		addDisallowedMob("Thaumcraft.TaintedSheep");
		addDisallowedMob("Thaumcraft.TaintedCow");
		addDisallowedMob("Thaumcraft.TaintedPig");
		addDisallowedMob("Thaumcraft.TaintedCreeper");

		addDisallowedMob("TwilightForest.Hydra");
		addDisallowedMob("TwilightForest.Naga");
		addDisallowedMob("TwilightForest.HydraHead");
		addDisallowedMob("TwilightForest.Lich Minion");
		//addDisallowedMob("TwilightForest.Questing Ram");
		addDisallowedMob("TwilightForest.Knight Phantom");
		addDisallowedMob("TwilightForest.Twilight Lich");
		addDisallowedMob("TwilightForest.Tower Boss");
		addDisallowedMob("TwilightForest.Loyal Zombie");
		addDisallowedMob("TwilightForest.Minoshroom");
		addDisallowedMob("TwilightForest.Boggard");

		addDisallowedMob("OpenBlocks.Luggage");

		addDisallowedMob("Linkbook");

		addDisallowedMob("Robit");
	}

	private static void addDisallowedMob(String name) {
		disallowedMobs.add(name);
	}

	private static void addDisallowedMob(Class <?extends EntityLiving> name) {
		addDisallowedMob((String)EntityList.classToStringMapping.get(name));
	}

	public static boolean isMobAllowed(String mob) {
		return !ReikaEntityHelper.isTameHostile(mob) && ReikaEntityHelper.isLivingMob(mob, false) && !disallowedMobs.contains(mob);
	}

	public static boolean isMobAllowed(Class<? extends EntityLiving> mob) {
		return isMobAllowed((String)EntityList.classToStringMapping.get(mob));
	}

	public String getSelectedMob() {
		return selectedMob;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.canConvert()) {
			progress.update();
			if (progress.checkCap()) {
				this.programSpawner();
			}
		}
		else {
			progress.reset();
		}
		progressTimer = progress.getTick();
	}

	public int getProgressScaled(int a) {
		return progressTimer * a / progress.getCap();
	}

	private boolean canConvert() {
		return this.isValidSpawner(inv[0]) && inv[1] == null;
	}

	private void programSpawner() {
		ItemStack is = ReikaItemHelper.getSizedItemStack(inv[0], 1);
		ReikaInventoryHelper.decrStack(0, inv);
		if (ReikaItemHelper.matchStackWithBlock(is, Blocks.mob_spawner)) {
			if (ReikaEntityHelper.hasID(selectedMob)) {
				int id = ReikaEntityHelper.mobNameToID(selectedMob);
				is.setItemDamage(id);
			}
		}
		else if (is.getItem() instanceof ItemSpawner) {
			ReikaSpawnerHelper.setSpawnerItemNBT(is, selectedMob, true);
		}
		else if (is.getItem() instanceof ProgrammableSpawner) {
			((ProgrammableSpawner)is.getItem()).setSpawnerType(is, this.getMobClass(selectedMob));
		}
		inv[1] = is;
	}

	private Class<? extends EntityLiving> getMobClass(String name) {
		return (Class)EntityList.stringToClassMapping.get(name);
	}

	public void setMobType(String type) {
		selectedMob = type;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return i == 1;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return i == 0 && this.isValidSpawner(itemstack);
	}

	private boolean isValidSpawner(ItemStack is) {
		if (is == null)
			return false;
		return ReikaItemHelper.matchStackWithBlock(is, Blocks.mob_spawner) || is.getItem() instanceof ItemSpawner || is.getItem() instanceof ProgrammableSpawner;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.REPROGRAMMER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		selectedMob = NBT.getString("mob");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (selectedMob != null && !selectedMob.isEmpty()) {
			NBT.setString("mob", selectedMob);
		}
	}

	public boolean hasSpawner() {
		return this.isValidSpawner(inv[0]);
	}

}
