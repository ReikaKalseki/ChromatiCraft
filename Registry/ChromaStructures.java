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

import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.Random;

import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Structure.BoostedLumenTreeStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.BoostedRelayStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.BroadcasterStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.CastingL1Structure;
import Reika.ChromatiCraft.Auxiliary.Structure.CastingL2Structure;
import Reika.ChromatiCraft.Auxiliary.Structure.CastingL3Structure;
import Reika.ChromatiCraft.Auxiliary.Structure.CloakingTowerStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.CompoundRepeaterStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.GateStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.InfusionStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.LumenTreeStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.MeteorTowerStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.NetworkOptimizerStructureV2;
import Reika.ChromatiCraft.Auxiliary.Structure.PersonalChargerStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.PlayerInfusionStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.PortalStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.ProgressionLinkerStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.ProtectionBeaconStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.PylonTurboStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.RepeaterStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.RitualStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.TreeSendFocusStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.WeakRepeaterStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.WirelessPedestalL2Structure;
import Reika.ChromatiCraft.Auxiliary.Structure.WirelessPedestalStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.BurrowStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.CavernStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.DataTowerStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.DesertStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.OceanStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.PylonStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.SnowStructure;
import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Base.ColoredStructureBase;
import Reika.ChromatiCraft.ModInterface.VoidMonsterRitualStructure;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Interfaces.Registry.StructureEnum;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum ChromaStructures implements StructureEnum<ChromaStructureBase> {

	PYLON(PylonStructure.class),
	CASTING1(CastingL1Structure.class),
	CASTING2(CastingL2Structure.class),
	CASTING3(CastingL3Structure.class),
	RITUAL(RitualStructure.class,	false),
	RITUAL2(RitualStructure.class,	true),
	INFUSION(InfusionStructure.class),
	PLAYERINFUSION(PlayerInfusionStructure.class),
	TREE(LumenTreeStructure.class),
	TREE_SENDER(TreeSendFocusStructure.class),
	TREE_BOOSTED(BoostedLumenTreeStructure.class),
	REPEATER(RepeaterStructure.class),
	COMPOUND(CompoundRepeaterStructure.class),
	CAVERN(CavernStructure.class),
	BURROW(BurrowStructure.class),
	OCEAN(OceanStructure.class),
	DESERT(DesertStructure.class),
	SNOWSTRUCT(SnowStructure.class),
	PORTAL(PortalStructure.class),
	PERSONAL(PersonalChargerStructure.class),
	BROADCAST(BroadcasterStructure.class),
	CLOAKTOWER(CloakingTowerStructure.class),
	PROTECT(ProtectionBeaconStructure.class),
	WEAKREPEATER(WeakRepeaterStructure.class),
	METEOR1(MeteorTowerStructure.class,	0),
	METEOR2(MeteorTowerStructure.class,	1),
	METEOR3(MeteorTowerStructure.class,	2),
	TELEGATE(GateStructure.class),
	RELAY(BoostedRelayStructure.class),
	PYLONBROADCAST(PylonBroadcastStructure.class),
	PYLONTURBO(PylonTurboStructure.class),
	DATANODE(DataTowerStructure.class),
	WIRELESSPEDESTAL(WirelessPedestalStructure.class),
	WIRELESSPEDESTAL2(WirelessPedestalL2Structure.class),
	PROGRESSLINK(ProgressionLinkerStructure.class),
	OPTIMIZER(NetworkOptimizerStructureV2.class),
	VOIDRITUAL(VoidMonsterRitualStructure.class);

	public final boolean requiresColor;
	private final Class<? extends ChromaStructureBase> structureClass;
	private final ChromaStructureBase structureInstance;

	public static final ChromaStructures[] structureList = values();

	private ChromaStructures(Class<? extends ChromaStructureBase> type, Object... data) {
		structureClass = type;
		requiresColor = ColoredStructureBase.class.isAssignableFrom(structureClass);
		try {
			structureInstance = this.instantiate(data);
		}
		catch (Exception e) {
			throw new RegistrationException(ChromatiCraft.instance, "Could not instantiate structure type "+this+"!", e);
		}
	}

	private ChromaStructureBase instantiate(Object... data) throws Exception {
		if (structureClass == MeteorTowerStructure.class) {
			Constructor c = structureClass.getConstructor(int.class);
			return (ChromaStructureBase)c.newInstance(data[0]);
		}
		else if (structureClass == RitualStructure.class) {
			Constructor c = structureClass.getConstructor(boolean.class);
			return (ChromaStructureBase)c.newInstance(data[0]);
		}
		return structureClass.newInstance();
	}

	public synchronized FilledBlockArray getArray(World world, int x, int y, int z) {
		return this.getStructure().getArray(world, x, y, z);
	}

	public synchronized FilledBlockArray getArray(World world, int x, int y, int z, Random r) {
		ChromaStructureBase s = this.getStructure();
		s.setRand(r);
		FilledBlockArray ret = s.getArray(world, x, y, z);
		s.resetToDefaults();
		return ret;
	}

	public synchronized FilledBlockArray getArray(World world, int x, int y, int z, CrystalElement e) {
		if (e == null)
			return this.getArray(world, x, y, z);
		return ((ColoredStructureBase)this.getStructure()).getArray(world, x, y, z, e);
	}

	@SideOnly(Side.CLIENT)
	public synchronized FilledBlockArray getStructureForDisplay() {
		ChromaStructureBase s = this.getStructure();
		s.setRand(DragonAPICore.rand);
		return this.getStructure().getStructureForDisplay();
	}

	public String getDisplayName() {
		return StatCollector.translateToLocal("chromastruct."+this.name().toLowerCase(Locale.ENGLISH));
	}

	public boolean isNatural() {
		switch(this) {
			case PYLON:
			case CAVERN:
			case BURROW:
			case OCEAN:
			case DESERT:
			case SNOWSTRUCT:
				return true;
			default:
				return false;
		}
	}

	@Override
	public ChromaStructureBase getStructure() {
		return structureInstance;
	}

}
