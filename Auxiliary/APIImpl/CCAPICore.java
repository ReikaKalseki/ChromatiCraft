package Reika.ChromatiCraft.Auxiliary.APIImpl;

import java.lang.reflect.Field;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI;
import Reika.ChromatiCraft.API.AdjacencyUpgradeAPI;
import Reika.ChromatiCraft.API.AuraLocusAPI;
import Reika.ChromatiCraft.API.CastingAPI;
import Reika.ChromatiCraft.API.ChromatiAPI;
import Reika.ChromatiCraft.API.CrystalPotionAPI;
import Reika.ChromatiCraft.API.DyeTreeAPI;
import Reika.ChromatiCraft.API.ItemElementAPI;
import Reika.ChromatiCraft.API.PlayerBufferAPI;
import Reika.ChromatiCraft.API.ProgressionAPI;
import Reika.ChromatiCraft.API.RitualAPI;
import Reika.ChromatiCraft.API.RuneAPI;
import Reika.ChromatiCraft.API.WorldgenAPI;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.AbilityRituals;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.DragonAPI.Exception.RegistrationException;

public class CCAPICore extends ChromatiAPI implements WorldgenAPI {

	private final AuraLocusAPIImpl aura = new AuraLocusAPIImpl();
	private final AdjacencyUpgradeAPIImpl adjacency = new AdjacencyUpgradeAPIImpl();
	private final DyeTreeAPIImpl trees = new DyeTreeAPIImpl();

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

	@Override
	public AbilityAPI abilities() {
		return AbilityHelper.instance;
	}

	@Override
	public CastingAPI recipes() {
		return RecipesCastingTable.instance;
	}

	@Override
	public RitualAPI rituals() {
		return AbilityRituals.instance;
	}

	@Override
	public ProgressionAPI research() {
		return ProgressionAPI.instance;
	}

	@Override
	public PlayerBufferAPI buffers() {
		return PlayerElementBuffer.instance;
	}

	@Override
	public AuraLocusAPI aura() {
		return aura;
	}

	@Override
	public AdjacencyUpgradeAPI adjacency() {
		return adjacency;
	}

	@Override
	public ItemElementAPI items() {
		return ItemElementCalculator.instance;
	}

	@Override
	public CrystalPotionAPI potions() {
		return CrystalPotionController.instance;
	}

	@Override
	public RuneAPI runes() {
		return RuneAPI.instance;
	}

	@Override
	public DyeTreeAPI trees() {
		return trees;
	}

	@Override
	public WorldgenAPI worldgen() {
		return this;
	}

	@Override
	public float getEndIslandBias(World world, int chunkX, int chunkZ) {
		int f = chunkX*2;
		int f1 = chunkZ*2;
		float orig = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * 8.0F;
		return ChromaAux.getIslandBias(world, orig, f, f1, f, f1);
	}

}
