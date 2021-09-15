package Reika.ChromatiCraft.Auxiliary.APIImpl;

import java.lang.reflect.Field;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.ChromatiAPI;
import Reika.ChromatiCraft.API.ProgressionAPI;
import Reika.ChromatiCraft.API.RuneAPI;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.AbilityRituals;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.DragonAPI.Exception.RegistrationException;

public class CCAPICore extends ChromatiAPI {

	private boolean init;

	@Override
	protected void initalize() {
		try {
			abilities = AbilityHelper.instance;
			recipes = RecipesCastingTable.instance;
			rituals = AbilityRituals.instance;

			research = ProgressionAPI.instance;
			buffers = PlayerElementBuffer.instance;

			aura = new AuraLocusAPIImpl();
			adjacency = new AdjacencyUpgradeAPIImpl();

			items = ItemElementCalculator.instance;
			potions = CrystalPotionController.instance;

			runes = RuneAPI.instance;
			trees = new DyeTreeAPIImpl();
		}
		catch (Throwable e) {
			if (e instanceof LinkageError) {
				ChromatiCraft.logger.logError("Could not initialize API: "+e.toString());
				e.printStackTrace();
			}
			else {
				throw new RegistrationException(ChromatiCraft.instance, "Could not initialize API core", e);
			}
		}

		init = true;
	}

	@Override
	protected boolean initialized() {
		return init;
	}

	public static void load() {
		try {
			Field f = ChromatiAPI.class.getDeclaredField("core");
			f.setAccessible(true);
			f.set(null, new CCAPICore());
		}
		catch (Exception e) {
			throw new RegistrationException(ChromatiCraft.instance, "Could not construct API core", e);
		}
	}

}
