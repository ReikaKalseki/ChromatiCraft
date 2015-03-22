/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Recipe;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.AbilityRituals;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalReceiver;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityGlobeFX;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Interfaces.BreakAction;
import Reika.DragonAPI.Interfaces.TriggerableAction;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityRitualTable extends InventoriedCrystalReceiver implements /*GuiController, */BreakAction, TriggerableAction {

	private boolean hasStructure = false;
	private int abilityTick = 0;
	private Ability ability;
	private int abilitySoundTick = 2000;
	private int tickNoPlayer = 0;

	@Override
	public void onPathBroken(CrystalElement e) {
		//this.killRitual();
	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (abilityTick > 0) {
			this.onRitualTick(world, x, y, z);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		AbilityRituals.addTable(this);
		this.validateMultiblock(null, world, x, y-2, z);
	}

	private void onRitualTick(World world, int x, int y, int z) {

		ElementTagCompound tag = AbilityRituals.instance.getAura(ability);
		if (this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.requestEnergyDifference(tag);
		}

		EntityPlayer ep = world.getPlayerEntityByName(placer);
		if (ep == null) {
			tickNoPlayer++;
			if (tickNoPlayer > 200) { //make something bad happen
				abilitySoundTick = 20000;
				abilityTick = 0;
			}
			return;
		}
		tickNoPlayer = 0;
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).offset(0, 2, 0).expand(0, 1, 0);
		boolean canTick = energy.containsAtLeast(tag) && ep.boundingBox.intersectsWith(box);
		//ReikaJavaLibrary.pConsole(abilityTick+":"+inbox+" from "+ep.boundingBox+" & "+box, Side.SERVER);
		//ReikaJavaLibrary.pConsole(energy+"/"+tag, Side.SERVER);
		//ReikaJavaLibrary.pConsole(box.maxX > ep.boundingBox.minX && box.minX < ep.boundingBox.maxX ? (box.maxY > ep.boundingBox.minY && box.minY < ep.boundingBox.maxY ? box.maxZ > ep.boundingBox.minZ && box.minZ < ep.boundingBox.maxZ : false) : false);

		abilitySoundTick++;
		if (abilitySoundTick >= 490) {
			abilitySoundTick = 0;
			ChromaSounds.ABILITY.playSoundAtBlock(this);
		}

		if (world.isRemote) {
			this.spawnParticles(world, x, y, z, canTick, tag);
		}

		if (canTick && !world.isRemote) {
			double dx = ep.posX-x-0.5;
			double dy = ep.posY-y-2.25-0.75*Math.sin(Math.toRadians(2*this.getTicksExisted()));
			double dz = ep.posZ-z-0.5;
			double dd = ReikaMathLibrary.py3d(dx, dy, dz);
			double v = 0.1875;
			double sx = dx > 0 ? 1 : -1;
			double sy = dy > 0 ? 1 : -1;
			double sz = dz > 0 ? 1 : -1;
			double vx = -v*sx*(dx*dx)/dd;
			double vy = -v*sy*(dy*dy)/dd;
			double vz = -v*sz*(dz*dz)/dd;
			ep.motionX = vx;
			ep.motionY = vy;
			ep.motionZ = vz;
			ep.velocityChanged = true;
			ep.fallDistance = 0;
			if (ep instanceof EntityPlayerMP) {
				((EntityPlayerMP)ep).playerNetServerHandler.floatingTickCount = 0;
			}
		}
		if (canTick)
			abilityTick--;
		if (abilityTick <= 0) {
			this.giveAbility(ep);
			energy.subtract(tag);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z, boolean canTick, ElementTagCompound tag) {
		int a = (2*this.getTicksExisted())%360;
		for (int i = 0; i < 360; i += 120) {
			double ang = Math.toRadians(a+i);
			double r = 0.5;
			double rx = x+0.5+r*Math.cos(ang);
			double ry = y;
			double rz = z+0.5+r*Math.sin(ang);
			double v = 0.04;
			double vx = v*Math.cos(ang);
			double vz = v*Math.sin(ang);
			EntityGlobeFX fx = new EntityGlobeFX(world, rx, y, rz, vx, 0.1875, vz).setScale(1.5F);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		tag.intersectWithMinimum(energy);
		int n = tag.tagCount();
		if (n > 0) {
			a = (8*this.getTicksExisted())%360;
			Iterator<CrystalElement> it = tag.elementSet().iterator();
			for (int i = 0; i < 360; i += 360/n) {
				double ang = Math.toRadians(a+i);
				double r = 0.25;
				double rx = x+0.5+r*Math.cos(ang);
				double ry = y;
				double rz = z+0.5+r*Math.sin(ang);
				double v = 0.0125;
				double vx = v*Math.cos(ang);
				double vz = v*Math.sin(ang);
				CrystalElement e = it.next();
				EntityGlobeFX fx = new EntityGlobeFX(e, world, rx, y, rz, vx, 0.1875, vz).setScale(1.5F);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		if (canTick) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.thePlayer.getCommandSenderName().equals(this.getPlacerName())) {
				mc.gameSettings.thirdPersonView = 2;
				mc.thePlayer.rotationYaw = this.getTicksExisted()%360;
				mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw-35;
				mc.thePlayer.rotationPitch = 0;
				mc.gameSettings.hideGUI = true;
			}
		}
	}

	private void giveAbility(EntityPlayer ep) {
		Chromabilities.give(ep, ability);
		abilitySoundTick = 2000;
		ability = null;
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
		if (worldObj.isRemote) {
			GameSettings gs = Minecraft.getMinecraft().gameSettings;
			gs.thirdPersonView = 0;
			gs.hideGUI = false;
		}
	}

	public void validateMultiblock(StructuredBlockArray blocks, World world, int x, int y, int z) {
		FilledBlockArray array = ChromaStructures.getRitualStructure(world, x, y, z);
		hasStructure = array.matchInWorld();
		if (!hasStructure && abilityTick > 0) {
			this.killRitual();
		}
		this.syncAllData(true);
	}

	private void killRitual() {
		//make something bad happen
		abilitySoundTick = 2000;
		abilityTick = 0;
		ability = null;
		if (worldObj.isRemote) {
			GameSettings gs = Minecraft.getMinecraft().gameSettings;
			gs.thirdPersonView = 0;
			gs.hideGUI = false;
		}
	}

	public boolean triggerRitual(EntityPlayer ep) {
		if (hasStructure && abilityTick == 0 && ability != null && AbilityRituals.instance.hasRitual(ability) && this.isPlacer(ep)) {
			if (worldObj.isRemote)
				return true;
			ElementTagCompound tag = AbilityRituals.instance.getAura(ability);
			this.requestEnergyDifference(tag);
			abilityTick = AbilityRituals.instance.getDuration(ability);
			ChromaSounds.USE.playSoundAtBlock(this);
			return true;
		}
		ChromaSounds.ERROR.playSoundAtBlock(this);
		return false;
	}

	public void setChosenAbility(Ability c) {
		this.killRitual();
		ability = c;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);
		NBT.setInteger("atick", abilityTick);
		NBT.setString("ability", ability != null ? ability.getID() : "null");
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("struct");
		abilityTick = NBT.getInteger("atick");
		String a = NBT.getString("ability");
		ability = a != null && !a.isEmpty() && !a.equals("null") ? Chromabilities.getAbility(a) : null;
	}

	@Override
	public int maxThroughput() {
		return 200;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return AbilityRituals.instance.getMaxAbilityCost()*3/2;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.RITUAL;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public ElementTagCompound getRequestedTotal() {
		return ability != null && abilityTick > 0 ? AbilityHelper.instance.getElementsFor(ability) : null;
	}

	public boolean isActive() {
		return abilityTick > 0;
	}

	@Override
	public void breakBlock() {
		AbilityRituals.removeTable(this);
	}

	public boolean isPlayerUsing(EntityPlayer ep) {
		ElementTagCompound tag = AbilityRituals.instance.getAura(ability);
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).offset(0, 2, 0).expand(0, 1, 0);
		return energy.containsAtLeast(tag) && ep.boundingBox.intersectsWith(box);
	}

	@Override
	public boolean trigger() {
		return this.getPlacer() != null && this.triggerRitual(this.getPlacer());
	}

}
