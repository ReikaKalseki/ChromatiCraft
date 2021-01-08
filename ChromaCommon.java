/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.IO.DynamicSoundLoader;
import Reika.DragonAPI.Instantiable.IO.RemoteSourcedAsset.RemoteSourcedAssetRepository;

public class ChromaCommon {

	public static int armor;

	public static final RemoteSourcedAssetRepository dynamicAssets = new RemoteSourcedAssetRepository(ChromatiCraft.instance, ChromatiCraft.class, "https://raw.githubusercontent.com/ReikaKalseki/ChromatiCraft/master", "Reika/ChromatiCraft/AssetDL");
	public static final DynamicSoundLoader soundLoader = new DynamicSoundLoader(ChromaSounds.class, dynamicAssets);

	public void registerRenderers()
	{
		//unused server side. -- see ClientProxy for implementation
	}

	public void addArmorRenders() {}

	public World getClientWorld() {
		return null;
	}

	public void registerRenderInformation() {

	}

	public void initAssetLoaders() {

	}

	public void registerSounds() {

	}

	public void registerKeys() {

	}

	public void addDonatorRender() {

	}

}
