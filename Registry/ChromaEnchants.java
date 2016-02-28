package Reika.ChromatiCraft.Registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.StatCollector;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentAggroMask;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentEnderLock;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentUseRepair;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentWeaponAOE;
import Reika.DragonAPI.Interfaces.Registry.EnchantmentEnum;


public enum ChromaEnchants implements EnchantmentEnum {

	WEAPONAOE(EnchantmentWeaponAOE.class, ExtraChromaIDs.WEAPONAOEID),
	AGGROMASK(EnchantmentAggroMask.class, ExtraChromaIDs.AGGROMASKID),
	ENDERLOCK(EnchantmentEnderLock.class, ExtraChromaIDs.ENDERLOCKID),
	USEREPAIR(EnchantmentUseRepair.class, ExtraChromaIDs.USEREPAIRID);

	private final Class enchantmentClass;
	private final ExtraChromaIDs enchantmentID;

	public static final ChromaEnchants[] enchantmentList = values();

	private ChromaEnchants(Class<? extends Enchantment> c, ExtraChromaIDs id) {
		enchantmentClass = c;
		enchantmentID = id;
	}

	@Override
	public String getBasicName() {
		return StatCollector.translateToLocal(this.getUnlocalizedName());
	}

	@Override
	public boolean isDummiedOut() {
		return false;
	}

	@Override
	public Class getObjectClass() {
		return enchantmentClass;
	}

	@Override
	public String getUnlocalizedName() {
		return "chroma."+this.name().toLowerCase();
	}

	@Override
	public Enchantment getEnchantment() {
		return ChromatiCraft.enchants[this.ordinal()];
	}

	@Override
	public int getEnchantmentID() {
		return enchantmentID.getValue();
	}

}
