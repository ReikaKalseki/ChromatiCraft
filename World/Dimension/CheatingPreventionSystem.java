package Reika.ChromatiCraft.World.Dimension;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CheatingPreventionSystem {

	public static final CheatingPreventionSystem instance = new CheatingPreventionSystem();

	private final HashSet<BlockKey> bannedBlocks = new HashSet();
	private final HashMap<KeyedItemStack, BanReaction> bannedItems = new HashMap();

	private CheatingPreventionSystem() {
		if (ModList.ENDERIO.isLoaded()) {
			Block b = GameRegistry.findBlock(ModList.ENDERIO.modLabel, "blockTravelAnchor");
			this.banBlock(b, BanReaction.DELETEONUSE);

			b = GameRegistry.findBlock(ModList.ENDERIO.modLabel, "blockTelePad");
			this.banBlock(b, BanReaction.DELETEONUSE);

			Item i = GameRegistry.findItem(ModList.ENDERIO.modLabel, "itemTravelStaff");
			if (i != null)
				this.banItem(i, BanReaction.DELETEONUSE);
		}

		Item i = GameRegistry.findItem("GraviSuite", "vajra");
		if (i != null)
			this.banItem(i, BanReaction.PREVENTUSE);

		if (ModList.THAUMICTINKER.isLoaded()) {
			Block b = GameRegistry.findBlock(ModList.THAUMICTINKER.modLabel, "warpGate");
			this.banBlock(b, BanReaction.DELETEONUSE);
		}

		if (ModList.DRACONICEVO.isLoaded()) {
			i = GameRegistry.findItem(ModList.DRACONICEVO.modLabel, "teleporterMKI");
			if (i != null)
				this.banItem(i, BanReaction.DROPONUSE);
			i = GameRegistry.findItem(ModList.DRACONICEVO.modLabel, "teleporterMKII");
			if (i != null)
				this.banItem(i, BanReaction.DELETEONUSE);
		}

		if (ModList.BOTANIA.isLoaded()) {
			i = GameRegistry.findItem(ModList.BOTANIA.modLabel, "flugelEye");
			if (i != null)
				this.banItem(i, BanReaction.PREVENTUSE);
		}

		i = GameRegistry.findItem("NotEnoughWands", "MovingWand");
		if (i != null)
			this.banItem(i, BanReaction.PREVENTUSE);

		i = GameRegistry.findItem("NotEnoughWands", "DisplacementWand");
		if (i != null)
			this.banItem(i, BanReaction.PREVENTUSE);
	}

	private void banBlock(Block b) {
		this.banBlock(b, null);
	}

	private void banBlock(Block b, BanReaction r) {
		if (b == null)
			return;
		BlockKey bk = new BlockKey(b);
		bannedBlocks.add(bk);
		if (r == null)
			r = BanReaction.DELETEONUSE;
		Item i = Item.getItemFromBlock(b);
		if (i != null)
			bannedItems.put(new KeyedItemStack(i).setIgnoreMetadata(!bk.hasMetadata()).setSimpleHash(true), r);
	}

	private void banItem(Item i, BanReaction r) {
		bannedItems.put(new KeyedItemStack(i).setIgnoreNBT(true).setSimpleHash(true), r);
	}

	private void banItem(ItemStack is, BanReaction r) {
		bannedItems.put(new KeyedItemStack(is).setIgnoreNBT(true).setSimpleHash(true), r);
	}

	public boolean isBannedDimensionBlock(Block b, int meta) {
		return bannedBlocks.contains(new BlockKey(b, meta));
	}

	@SubscribeEvent
	public void handleRightClicks(PlayerInteractEvent evt) {
		if (evt.action == Action.RIGHT_CLICK_AIR || evt.action == Action.RIGHT_CLICK_BLOCK) {
			if (evt.entityPlayer.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
				ItemStack is = evt.entityPlayer.getCurrentEquippedItem();
				BanReaction r = this.getReaction(is);
				if (r != null && r.reactsToUse()) {
					r.perform(evt.entityPlayer, is, -1);
					evt.setCanceled(true);
				}
			}
		}
	}

	public void preJoin(EntityPlayer ep) {
		this.checkInventory(ep, BanReaction.PREVENTBRING);
	}

	public void postJoin(EntityPlayer ep) {
		this.checkInventory(ep, BanReaction.DELETEONENTRY);
	}

	private void checkInventory(EntityPlayer ep, BanReaction br) {
		for (int i = 0; i < 5; i++) {
			ItemStack is = ep.getEquipmentInSlot(i);
			BanReaction r = this.getReaction(is);
			if (r == br) {
				r.perform(ep, is, -i);
			}
		}
		for (int i = 0; i < 36; i++) {
			ItemStack is = ep.inventory.mainInventory[i];
			BanReaction r = this.getReaction(is);
			if (r == br) {
				r.perform(ep, is, -i);
			}
		}
	}

	public void tick(EntityPlayer ep) {
		if (ep.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			ItemStack held = ep.getCurrentEquippedItem();
			if (held != null) {
				BanReaction r = this.getReaction(held);
				if (r != null && r.reactsToTick()) {
					r.perform(ep, held, -1);
				}
			}
		}
	}

	private BanReaction getReaction(ItemStack is) {
		return is == null || is.getItem() == null ? null : bannedItems.get(new KeyedItemStack(is).setSimpleHash(true));
	}

	public void punishCheatingPlayer(EntityPlayer ep) {
		ReikaSoundHelper.playSoundAtEntity(ep.worldObj, ep, "random.explode", 1, 1);
		ReikaSoundHelper.playSoundAtEntity(ep.worldObj, ep, "random.explode", 1, 0.5F);
		ReikaParticleHelper.EXPLODE.spawnAt(ep);
		ep.attackEntityFrom(DamageSource.generic, ReikaRandomHelper.getRandomBetween(5, 10));
		Vec3 v = ep.getLookVec();
		ReikaEntityHelper.knockbackEntityFromPos(ep.posX+v.xCoord, ep.posY+v.yCoord-1.5, ep.posZ+v.zCoord, ep, 2.5);
		ep.velocityChanged = true;
		ep.fallDistance += 10;
		//}

	}

	private static enum BanReaction {
		PREVENTUSE,
		PREVENTBRING,
		DROPONUSE,
		DELETEONUSE,
		DELETEONHOLD,
		DELETEONENTRY,
		;

		private void perform(EntityPlayer ep, ItemStack is, int slot) {
			switch(this) {
				case PREVENTUSE:
					ChromaSounds.ERROR.playSound(ep);
					break;
				case PREVENTBRING:
				case DELETEONENTRY:
					if (this == PREVENTBRING) {
						EntityItem ei = ReikaItemHelper.dropItem(ep, is);
						ei.lifespan = Integer.MAX_VALUE;
					}
					if (slot < 0)
						ep.setCurrentItemOrArmor(-slot, is);
					else
						ep.inventory.setInventorySlotContents(slot, null);
					break;
				case DROPONUSE:
				case DELETEONUSE:
				case DELETEONHOLD:
					instance.punishCheatingPlayer(ep);
					if (this == DROPONUSE)
						ReikaItemHelper.dropItem(ep, is);
					ep.setCurrentItemOrArmor(0, null);
					break;
			}
			if (this.givesProgress())
				ProgressStage.STRUCTCHEAT.stepPlayerTo(ep);
		}

		public boolean reactsToUse() {
			switch(this) {
				case PREVENTUSE:
				case DROPONUSE:
				case DELETEONUSE:
					return true;
				default:
					return false;
			}
		}

		public boolean reactsToTick() {
			switch(this) {
				case DELETEONHOLD:
					return true;
				default:
					return false;
			}
		}

		private boolean givesProgress() {
			return this.ordinal() >= DROPONUSE.ordinal();
		}
	}

}
