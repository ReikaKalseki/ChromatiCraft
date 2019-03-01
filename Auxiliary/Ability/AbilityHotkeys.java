package Reika.ChromatiCraft.Auxiliary.Ability;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.GUI.GuiAbilitySelect;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AbilityHotkeys {

	private static final String NBT_TAG = "AbilityHotkey";

	public static int SLOTS = 4;

	public static final KeyBinding[] keys = new KeyBinding[4];
	private static final boolean[] lastPress = new boolean[4];

	public static void tick(EntityPlayer ep, Ability sel, int data) { //null 'sel' to make it a general tick
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] != null) {
				boolean flag = keys[i].getIsKeyPressed();
				if (flag && !lastPress[i]) {
					if (sel != null)
						cacheAbility(ep, sel, data, i);
					else
						fireHotkeyForPlayer(ep, i);
				}
				lastPress[i] = flag;
			}
		}
	}

	public static void fireHotkeyForPlayer(EntityPlayer ep, int idx) {
		CachedAbilitySelection[] arr = getHotkeysForPlayer(ep);
		if (arr != null && arr[idx] != null) {
			GuiAbilitySelect.selectAbility(ep, arr[idx].ability, arr[idx].data);
		}
	}

	public static CachedAbilitySelection getCachedHotkey(EntityPlayer ep, int idx) {
		CachedAbilitySelection[] arr = getHotkeysForPlayer(ep);
		return arr != null && arr[idx] != null ? arr[idx] : null;
	}

	private static CachedAbilitySelection[] getHotkeysForPlayer(EntityPlayer ep) {
		NBTTagCompound tag = getNBT(ep);
		if (tag == null || tag.hasNoTags())
			return null;
		CachedAbilitySelection[] ret = new CachedAbilitySelection[SLOTS];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = CachedAbilitySelection.readFromNBT(tag.getCompoundTag("slot"+i));
		}
		return ret;
	}

	public static void cacheAbility(EntityPlayer ep, Ability a, int data, int slot) {
		NBTTagCompound li = getNBT(ep);
		CachedAbilitySelection cab = new CachedAbilitySelection(a, data);
		NBTTagCompound tag = cab.writeToNBT();
		li.setTag("slot"+slot, tag);
		ReikaPlayerAPI.syncCustomDataFromClient(ep);

		ReikaChatHelper.sendChatToPlayer(ep, "Saved ability "+a.getDisplayName()+" to ability hotkey slot "+slot+" with power level "+data);
	}

	private static NBTTagCompound getNBT(EntityPlayer ep) {
		NBTTagCompound tag = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound nbt = tag.getCompoundTag(NBT_TAG);
		tag.setTag(NBT_TAG, nbt);
		return nbt;
	}

	public static class CachedAbilitySelection {

		public final Ability ability;
		public final int data;

		private CachedAbilitySelection(Ability a, int dat) {
			if (a == null)
				throw new IllegalArgumentException("Null ability!");
			ability = a;
			data = dat;
		}

		public NBTTagCompound writeToNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("ability", ability.getID());
			tag.setInteger("data", data);
			return tag;
		}

		public static CachedAbilitySelection readFromNBT(NBTTagCompound tag) {
			Ability a = Chromabilities.getAbility(tag.getString("ability"));
			return a != null ? new CachedAbilitySelection(a, tag.getInteger("data")) : null;
		}

	}

}
