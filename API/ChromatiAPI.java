package Reika.ChromatiCraft.API;


public abstract class ChromatiAPI {

	protected AbilityAPI abilities;
	protected CastingAPI recipes;
	protected RitualAPI rituals;

	protected ProgressionAPI research;
	protected PlayerBufferAPI buffers;

	protected AuraLocusAPI aura;
	protected AdjacencyUpgradeAPI adjacency;

	protected ItemElementAPI items;
	protected CrystalPotionAPI potions;

	protected RuneAPI runes;
	protected DyeTreeAPI trees;

	private static ChromatiAPI core;

	public static ChromatiAPI getAPI() {
		if (!core.initialized())
			core.initalize();
		return core;
	}

	public final AbilityAPI abilities() {return abilities;}
	public final CastingAPI recipes() {return recipes;}
	public final RitualAPI rituals() {return rituals;}

	public final ProgressionAPI research() {return research;}
	public final PlayerBufferAPI buffers() {return buffers;}

	public final AuraLocusAPI aura() {return aura;}
	public final AdjacencyUpgradeAPI adjacency() {return adjacency;}

	public final ItemElementAPI items() {return items;}
	public final CrystalPotionAPI potions() {return potions;}

	public final RuneAPI runes() {return runes;}
	public final DyeTreeAPI trees() {return trees;}

	protected abstract void initalize();
	protected abstract boolean initialized();


}
