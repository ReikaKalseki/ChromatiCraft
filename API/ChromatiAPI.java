package Reika.ChromatiCraft.API;


public abstract class ChromatiAPI {

	private static ChromatiAPI core;

	public static ChromatiAPI getAPI() {
		return core;
	}

	public abstract AbilityAPI abilities();
	public abstract CastingAPI recipes();
	public abstract RitualAPI rituals();

	public abstract ProgressionAPI research();
	public abstract PlayerBufferAPI buffers();

	public abstract AuraLocusAPI aura();
	public abstract AdjacencyUpgradeAPI adjacency();

	public abstract ItemElementAPI items();
	public abstract CrystalPotionAPI potions();

	public abstract RuneAPI runes();
	public abstract DyeTreeAPI trees();

	public abstract WorldgenAPI worldgen();


}
