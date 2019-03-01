/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import climateControl.api.BiomeSettings;
import climateControl.api.ClimateControlRules;
import climateControl.utils.Mutable;
import climateControl.utils.Settings;

public class ChromaClimateControl extends BiomeSettings {

	public static final String biomeCategory = "ReikasBiome";
	public static final String reikasCategory = "ReikasSettings";
	public final Category reikasSettings = new Category(reikasCategory);

	public final Element enderForest = new Element("Ender Forest", 47, "MEDIUM");
	public final Element rainbowForest = new Element("Rainbow Forest", 48, "MEDIUM");
	public final Element glowcliffs = new Element("Luminous Cliffs", 49, "MEDIUM");

	static final String biomesOnName = "ReikasBiomesOn";

	public final Mutable<Boolean> biomesFromConfig = climateControlCategory.booleanSetting(biomesOnName, "", false);

	static final String configName = "Reikas";
	public final Mutable<Boolean> biomesInNewWorlds = climateControlCategory.booleanSetting(this.startBiomesName(configName), "Use biome in new worlds and dimensions", true);

	public ChromaClimateControl() {
		super(biomeCategory);
		enderForest.biomeIncidences().set(3);
		rainbowForest.biomeIncidences().set(3);
		glowcliffs.biomeIncidences().set(1);
	}

	public File configFile(File source) {
		File directory = source.getParentFile();
		File highlands = new File(directory, "Highlands");
		File generalConfig = new File(highlands, "General.cfg");
		return generalConfig;
	}

	@Override
	public void setNativeBiomeIDs(File configDirectory) {
		try {
			UpdatedChromaticraftSettings nativeSettings = this.nativeIDs(configDirectory);
			enderForest.biomeID().set(nativeSettings.EnderForestID.value());
			rainbowForest.biomeID().set(nativeSettings.RainbowForestID.value());
			glowcliffs.biomeID().set(nativeSettings.GlowCliffsID.value());
		}
		catch (NoClassDefFoundError e) {
			// no highlands
		}
	}

	@Override
	public void setRules(ClimateControlRules rules) {

	}

	@Override
	public void onNewWorld() {
		biomesFromConfig.set(biomesInNewWorlds);
	}

	@Override
	public boolean biomesAreActive() {
		return biomesFromConfig.value();
	}

	private UpdatedChromaticraftSettings nativeIDs(File configDirectory) {
		UpdatedChromaticraftSettings result = new UpdatedChromaticraftSettings();
		File reikaDirectory = new File(configDirectory, "Reika");
		File configFile = new File(reikaDirectory, "ChromatiCraft.cfg");
		result.readFrom(new Configuration(configFile));
		return result;
	}
}

class UpdatedChromaticraftSettings extends Settings {

	public static final String biomeIDName = "biome ids";
	public final Category biomeIDs = new Category(biomeIDName);

	Mutable<Integer> EnderForestID = biomeIDs.intSetting("Ender Forest Biome ID", 47);
	Mutable<Integer> RainbowForestID = biomeIDs.intSetting("Rainbow Forest Biome ID", 48);
	Mutable<Integer> GlowCliffsID = biomeIDs.intSetting("Luminous Cliffs Biome ID", 49);

}
