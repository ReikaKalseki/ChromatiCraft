/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityAccelerator;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityBlockShuffler;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityBlockTicker;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityDamageBoost;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityEfficiencyUpgrade;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityEnergyIncrease;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityHealingCore;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityHeatRelay;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityOreCreator;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityPerformanceBoost;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityPlayerDelegate;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityProtectionUpgrade;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityRangeBoost;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityStabilityCore;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityTankCapacityUpgrade;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public enum AdjacencyUpgrades {

	BLACK(0x000000, 0x505050, TileEntityEfficiencyUpgrade.class),
	RED(0xff0000, 0x800000, TileEntityProtectionUpgrade.class),
	GREEN(0x00C90E, 0x008206, TileEntityBlockTicker.class),
	BROWN(0xA06139, 0x56341F, TileEntityOreCreator.class),
	BLUE(0x0000FF, 0x0065FF),
	PURPLE(0xBE00EA, 0x7400EA, TileEntityPerformanceBoost.class),
	CYAN(0x009F8C, 0x009FF0, TileEntityTankCapacityUpgrade.class),
	LIGHTGRAY(0x808080, 0xC0C0C0, TileEntityPlayerDelegate.class),
	GRAY(0x404040, 0x808080, TileEntityBlockShuffler.class),
	PINK(0xff4040, 0xffcfcf, TileEntityDamageBoost.class),
	LIME(0x00ff00, 0x00ffff, TileEntityRangeBoost.class),
	YELLOW(0xffff00, 0xffffff, TileEntityEnergyIncrease.class),
	LIGHTBLUE(0x0000ff, 0x00ffff, TileEntityAccelerator.class),
	MAGENTA(0xFF61DC, 0xD900DC, TileEntityHealingCore.class),
	ORANGE(0xffcf00, 0xff4000, TileEntityHeatRelay.class),
	WHITE(0xffffff, 0xffffff, TileEntityStabilityCore.class);

	public final int color1;
	public final int color2;

	private final Class tileClass;

	private TileEntityAdjacencyUpgrade renderInstance;

	public static final AdjacencyUpgrades[] upgrades = values();

	private AdjacencyUpgrades(int c1, int c2) {
		this(c1, c2, null);
	}

	private AdjacencyUpgrades(int c1, int c2, Class c) {
		tileClass = c;
		color1 = c1;
		color2 = c2;
	}

	@SideOnly(Side.CLIENT)
	public TileEntityAdjacencyUpgrade createTEInstanceForRender() {
		if (renderInstance == null) {
			renderInstance = this.createTileEntity();
		}
		return renderInstance;
	}

	public boolean isImplemented() {
		return tileClass != null;
	}

	public Class getTileClass() {
		return tileClass;
	}

	public TileEntityAdjacencyUpgrade createTileEntity() {
		if (!this.isImplemented())
			return null;
		try {
			return (TileEntityAdjacencyUpgrade)tileClass.newInstance();
		}
		catch (Exception e) {
			throw new RegistrationException(ChromatiCraft.instance, "Could not instantiate an adjacency upgrade TileEntity "+this, e);
		}
	}

	public String getName() {
		return StatCollector.translateToLocal("adjacency."+this.name().toLowerCase());
	}

	public String getDesc(int tier) {
		switch(this) {
			case BLACK:
				return String.format("Reduces lumen consumption by %sx", ReikaStringParser.getAutoDecimal(1-this.getFactor(tier)));
			case RED:
				return String.format("Protects against explosions up to %sx TNT", ReikaStringParser.getAutoDecimal(this.getFactor(tier)/4D));
			case GREEN:
				return String.format("Ticks blocks around it, %d times per 5 seconds", (int)(this.getFactor(tier)*50));
			case BROWN:
				return String.format("Transmutes stone into mineral, %s per second, up to rarity '%s'", ReikaStringParser.getAutoDecimal(this.getFactor(tier)*20), TileEntityOreCreator.getMaxSpawnableRarity(tier));
			case BLUE:
				break;
			case PURPLE:
				return String.format("Improves the performance of adjacent constructs, Tier %d", tier);
			case CYAN:
				return String.format("Increases fluid capacity %sx", ReikaStringParser.getAutoDecimal(this.getFactor(tier)));
			case GRAY:
				return String.format("Shuffles blocks around it %s times a second", ReikaStringParser.getAutoDecimal(this.getFactor(tier)));
			case LIGHTGRAY:
				return String.format("Fakes player interaction, Tier %d", tier);
			case PINK:
				return String.format("Increases construct damage %sx", ReikaStringParser.getAutoDecimal(this.getFactor(tier)));
			case LIME:
				return String.format("Increases construct range %sx", ReikaStringParser.getAutoDecimal(this.getFactor(tier)));
			case YELLOW:
				return String.format("Multiplies energy output %sx", ReikaStringParser.getAutoDecimal(this.getFactor(tier)));
			case LIGHTBLUE:
				return String.format("Accelerates time by %dx for TileEntities adjacent to it.", (int)this.getFactor(tier));
			case MAGENTA:
				return String.format("Repairs adjacent blocks, Tier %d", tier);
			case ORANGE:
				return String.format("Exchanges and balances heat, %s%s per tick", ReikaStringParser.getAutoDecimal(this.getFactor(tier)), "%");
			case WHITE:
				return String.format("Stabilizes adjacent constructs, Tier %d", tier);
		}
		return "Does nothing...yet";
	}

	public double getFactor(int tier) {
		switch(this) {
			case BLACK:
				return TileEntityEfficiencyUpgrade.getCostFactor(tier);
			case RED:
				return TileEntityProtectionUpgrade.getMaxProtectedPower(tier);
			case GREEN:
				return (TileEntityBlockTicker.getTicksPerTick(tier));
			case BROWN:
				return TileEntityOreCreator.getOreChance(tier);
			case BLUE:
				break;
			case PURPLE:
				break;
			case CYAN:
				return TileEntityTankCapacityUpgrade.getCapacityFactor(tier);
			case GRAY:
				return TileEntityBlockShuffler.getOperationsPerSecond(tier);
			case LIGHTGRAY:
				break;
			case PINK:
				return TileEntityDamageBoost.getFactor(tier);
			case LIME:
				return TileEntityRangeBoost.getFactor(tier);
			case YELLOW:
				return 1+TileEntityEnergyIncrease.getFactor(tier);
			case LIGHTBLUE:
				return 1+TileEntityAccelerator.getAccelFromTier(tier);
			case MAGENTA:
				break;
			case ORANGE:
				return 100*TileEntityHeatRelay.getFactor(tier);
			case WHITE:
				break;
		}
		return 0;
	}

	public ItemStack getStackOfTier(int tier) {
		ItemStack is = ChromaItems.ADJACENCY.getStackOfMetadata(this.ordinal());
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("tier", tier);
		return is;
	}

}
