/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Items.Tools.ItemPendant;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PlayerElementBuffer {

	public static final PlayerElementBuffer instance = new PlayerElementBuffer();

	private final CountMap<UUID> recentUpgrades = new CountMap();

	private static final String NBT_TAG = "CrystalBuffer";

	private PlayerElementBuffer() {

	}

	public float getAndDecrUpgradeTick(EntityPlayer ep) {
		UUID id = ep.getUniqueID();
		int get = recentUpgrades.get(id);
		if (get > 0)
			recentUpgrades.increment(id, -1);
		return get/2000F;
	}

	private NBTTagCompound getTag(EntityPlayer ep) {
		NBTTagCompound tag = ep.getEntityData().getCompoundTag(NBT_TAG);
		ep.getEntityData().setTag(NBT_TAG, tag);
		return tag;
	}

	public ElementTagCompound getPlayerBuffer(EntityPlayer ep) {
		NBTTagCompound tag = this.getTag(ep);
		return ElementTagCompound.createFromNBT(tag);
	}

	public int getPlayerContent(EntityPlayer ep, CrystalElement e) {
		NBTTagCompound tag = this.getTag(ep);
		return tag.getInteger(e.name());
	}

	public boolean playerHas(EntityPlayer ep, CrystalElement e, int amt) {
		return ep.capabilities.isCreativeMode || this.getPlayerContent(ep, e) >= amt;
	}

	public boolean playerHas(EntityPlayer player, ElementTagCompound tag) {
		for (CrystalElement e : tag.elementSet()) {
			int amt = tag.getValue(e);
			if (!this.playerHas(player, e, amt))
				return false;
		}
		return true;
	}

	public boolean addToPlayer(EntityPlayer ep, CrystalElement e, int amt) {
		NBTTagCompound tag = this.getTag(ep);
		int has = tag.getInteger(e.name());
		int val = Math.min(has+amt, this.getElementCap(ep));
		tag.setInteger(e.name(), val);
		//this.checkUpgrade(ep, true);
		this.setElementCap(ep, this.calcElementCap(ep), true);
		return val > has;
	}

	private void setToPlayer(EntityPlayer ep, CrystalElement e, int amt) {
		NBTTagCompound tag = this.getTag(ep);
		tag.setInteger(e.name(), amt);
	}

	public boolean addToPlayer(EntityPlayer ep, ElementTagCompound tag) {
		boolean flag = false;
		for (CrystalElement e : tag.elementSet()) {
			flag |= this.addToPlayer(ep, e, tag.getValue(e));
		}
		return flag;
	}

	public void removeFromPlayer(EntityPlayer ep, CrystalElement e, int amt) {
		if (ep.capabilities.isCreativeMode)
			return;

		if (ItemPendant.isEnhancedKuroPendantActive(ep))
			amt = Math.max(1, (int)(amt*0.5F));
		else if (ItemPendant.isKuroPendantActive(ep))
			amt = Math.max(1, (int)(amt*0.8F));

		NBTTagCompound tag = this.getTag(ep);
		int has = tag.getInteger(e.name());
		tag.setInteger(e.name(), Math.max(0, has-amt));
		this.checkAndWarnPlayer(ep, e, has);
	}

	private void checkAndWarnPlayer(EntityPlayer ep, CrystalElement e, int prev) {
		float f1 = prev/(float)this.getElementCap(ep);
		float f2 = this.getPlayerContent(ep, e)/(float)this.getElementCap(ep);
		//ReikaJavaLibrary.pConsole(f1+">"+f2+" @ "+e, Side.SERVER);
		this.warnPlayer(ep, e, f1, f2);
	}

	private void warnPlayer(EntityPlayer ep, CrystalElement e, float f1, float f2) {
		if (f2 < f1) {
			int s1 = -1;
			int s2 = -1;
			if (f1 < 0.015625) {
				s1 = 0;
			}
			else if (f1 < 0.03125) {
				s1 = 1;
			}
			else if (f1 < 0.0625) {
				s1 = 2;
			}
			if (f2 < 0.015625) {
				s2 = 0;
			}
			else if (f2 < 0.03125) {
				s2 = 1;
			}
			else if (f2 < 0.0625) {
				s2 = 2;
			}
			//ReikaJavaLibrary.pConsole(s1+","+s2, Side.SERVER);
			if (s1 != s2) {
				ChromaSounds snd = null;
				switch (s2) {
					case 0:
						snd = ChromaSounds.BUFFERWARNING;
						break;
					case 1:
						snd = ChromaSounds.BUFFERWARNING_LOW;
						break;
					case 2:
						snd = ChromaSounds.BUFFERWARNING_EMPTY;
						break;
				}
				if (snd != null)
					snd.playSound(ep, 1, (float)CrystalMusicManager.instance.getDingPitchScale(e));
			}
		}
	}

	public void removeFromPlayer(EntityPlayer player, ElementTagCompound tag) {
		for (CrystalElement e : tag.elementSet()) {
			int amt = tag.getValue(e);
			this.removeFromPlayer(player, e, amt);
		}
	}

	public int getElementCap(EntityPlayer ep) {
		NBTTagCompound tag = this.getTag(ep);
		return Math.max(24, tag.getInteger("cap"));
	}

	public int getChargeSpeed(EntityPlayer ep) {
		return (int)Math.pow(this.getElementCap(ep)/24, 0.667);
	}

	public double getPlayerFraction(EntityPlayer ep, CrystalElement e) {
		return (double)this.getPlayerContent(ep, e)/this.getElementCap(ep);
	}
	/*
	public boolean upgradeCap(EntityPlayer ep) {
		return this.setElementCap(ep, this.getElementCap(ep)*4, true);
	}
	 */
	public boolean setElementCap(EntityPlayer ep, int cap, boolean notify) {
		NBTTagCompound tag = this.getTag(ep);
		int prev = this.getElementCap(ep);
		int val = Math.min(cap, this.getPlayerMaximumCap(ep));
		tag.setInteger("cap", val);
		boolean flag = val > prev;
		if (flag) {
			if (cap%2 == 0)
				ChromaSounds.CRAFTDONE.playSound(ep.worldObj, ep.posX, ep.posY, ep.posZ, 0.1F, 0.5F);
			recentUpgrades.set(ep.getUniqueID(), 2000);
			//if (ep instanceof EntityPlayerMP)
			//	this.sendUpgradePacket((EntityPlayerMP)ep);
		}
		if (ep instanceof EntityPlayerMP) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.BUFFERSET.ordinal(), (EntityPlayerMP)ep, val);
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
		}
		return flag;
	}

	private int calcElementCap(EntityPlayer ep) {
		int amt = this.getPlayerTotalEnergy(ep);
		return Math.max(this.getPlayerBuffer(ep).getMaximumValue(), MathHelper.clamp_int((int)(Math.min(amt*0.8, 4*Math.pow(amt, 0.75))), 24, this.getPlayerMaximumCap(ep)));
	}

	public int getPlayerMaximumCap(EntityPlayer ep) {
		return ProgressStage.CTM.isPlayerAtStage(ep) ? 2400000 : 400000;
	}

	public int getChargeInefficiency(EntityPlayer ep) {
		return ProgressStage.CTM.isPlayerAtStage(ep) ? 1 : ProgressStage.DIMENSION.isPlayerAtStage(ep) ? 2 : 4;
	}
	/*
	private void sendUpgradePacket(EntityPlayerMP ep) {
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.BUFFERINC.ordinal(), ep, 0);
	}
	 */
	@SideOnly(Side.CLIENT)
	public void setPlayerCapOnClient(EntityPlayer ep, int cap) {
		this.setElementCap(ep, cap, false);
	}
	/*
	@SideOnly(Side.CLIENT)
	public void upgradePlayerOnClient(EntityPlayer ep) {
		recentUpgrades.set(ep.getUniqueID(), 2000);
	}
	 */
	public boolean canPlayerAccept(EntityPlayer ep, CrystalElement e, int amt) {
		return this.getPlayerContent(ep, e)+amt <= this.getElementCap(ep);
	}

	public boolean isMaxed(EntityPlayer player, CrystalElement e) {
		return this.getPlayerContent(player, e) == this.getElementCap(player);
	}

	public boolean isMaxedWithin(EntityPlayer player, CrystalElement e, float frac) {
		return this.getPlayerContent(player, e) >= this.getElementCap(player)*(1-frac);
	}
	/*
	public boolean checkUpgrade(EntityPlayer player, boolean doUpgrade) {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if (!this.isMaxedWithin(player, e, 0.1F))
				return false;
		}
		return doUpgrade ? this.upgradeCap(player) : true;
	}
	 */
	public boolean hasElement(EntityPlayer ep, CrystalElement e) {
		return this.getPlayerContent(ep, e) > 0;
	}

	public int getPlayerTotalEnergy(EntityPlayer ep) {
		int sum = 0;
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			sum += this.getPlayerContent(ep, e);
		}
		return sum;
	}

	public void copyTo(EntityPlayer from, EntityPlayer to) {
		NBTTagCompound data = this.getTag(from);
		to.getEntityData().setTag(NBT_TAG, data);
	}

	public static class PlayerEnergyCommand extends DragonCommandBase {

		@Override
		public void processCommand(ICommandSender ics, String[] args) {
			EntityPlayer sender = this.getCommandSenderAsPlayer(ics);
			EntityPlayer tg = args.length == 2 ? sender : sender.worldObj.getPlayerEntityByName(args[0]);
			String es = args[args.length-2].toUpperCase();
			CrystalElement[] en = es.equals("ALL") ? CrystalElement.elements : new CrystalElement[]{CrystalElement.valueOf(es)};
			int amt = Integer.parseInt(args[args.length-1]);
			for (CrystalElement e : en)
				instance.setToPlayer(tg, e, amt);
			if (tg instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)tg);
		}

		@Override
		public String getCommandString() {
			return "chromabuffer";
		}

		@Override
		protected boolean isAdminOnly() {
			return true;
		}

	}

}
