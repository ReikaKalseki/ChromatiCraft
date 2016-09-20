/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.CrystalElementProxy;
import Reika.ChromatiCraft.API.ResearchFetcher;
import Reika.ChromatiCraft.API.ResearchFetcher.ProgressRegistry;
import Reika.ChromatiCraft.API.Event.ProgressionEvent;
import Reika.ChromatiCraft.API.Event.ProgressionEvent.ResearchType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ProgressElement;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.SequenceMap;
import Reika.DragonAPI.Instantiable.Data.Maps.SequenceMap.Topology;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ProgressionManager implements ProgressRegistry {

	public static final ProgressionManager instance = new ProgressionManager();

	private static final String MAIN_NBT_TAG = "Chroma_Progression";
	private static final String COLOR_NBT_TAG = "Chroma_Element_Discovery";
	private static final String STRUCTURE_NBT_TAG = "Structure_Color_Completion";
	private static final String COOPERATE_NBT_TAG = "Chroma_Cooperation";

	private final SequenceMap<ProgressStage> progressMap = new SequenceMap();

	private final MultiMap<String, ProgressStage> playerMap = new MultiMap(new MultiMap.HashSetFactory());

	private final EnumMap<CrystalElement, ColorDiscovery> colorDiscoveries = new EnumMap(CrystalElement.class);
	private final EnumMap<CrystalElement, StructureComplete> structureFlags = new EnumMap(CrystalElement.class);

	private final EnumMap<ProgressStage, ChromaResearch> auxiliaryReference = new EnumMap(ProgressStage.class);

	//private final Comparator<EntityPlayer> playerProgressionComparator = new PlayerProgressionComparator();

	public static enum ProgressStage implements ProgressElement {

		CASTING(ChromaTiles.TABLE.getCraftedProduct()), //Do a recipe
		CRYSTALS(ChromaBlocks.CRYSTAL.getStackOfMetadata(CrystalElement.RED.ordinal())), //Found a crystal
		DYETREE(ChromaBlocks.DYELEAF.getStackOfMetadata(CrystalElement.YELLOW.ordinal())), //Harvest a dye tree
		MULTIBLOCK(ChromaTiles.STAND.getCraftedProduct()), //Assembled a multiblock
		RUNEUSE(ChromaBlocks.RUNE.getStackOfMetadata(CrystalElement.ORANGE.ordinal())), //Placed runes
		PYLON(ChromaTiles.PYLON.getCraftedProduct()), //Found pylon
		LINK(ChromaTiles.COMPOUND.getCraftedProduct()), //Made a network connection/high-tier crafting
		CHARGE(ChromaItems.TOOL.getStackOf()), //charge from a pylon
		ABILITY(ChromaTiles.RITUAL.getCraftedProduct()), //use an ability
		RAINBOWLEAF(ChromaBlocks.RAINBOWLEAF.getStackOfMetadata(3)), //harvest a rainbow leaf
		MAKECHROMA(ChromaTiles.COLLECTOR.getCraftedProduct()),
		SHARDCHARGE(ChromaStacks.chargedRedShard),
		ALLOY(ChromaStacks.chromaIngot),
		CHROMA(ChromaBlocks.CHROMA.getBlockInstance()), //step in liquid chroma
		STONES(ChromaStacks.elementUnit), //craft all elemental stones together
		SHOCK(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(5)), //get hit by a pylon
		HIVE(ChromaBlocks.HIVE.getBlockInstance(), ModList.FORESTRY.isLoaded()),
		NETHER(Blocks.portal), //go to the nether
		END(Blocks.end_portal_frame), //go to the end
		TWILIGHT(ModList.TWILIGHT.isLoaded() ? ModWoodList.CANOPY.getItem() : null, ModList.TWILIGHT.isLoaded()), //Go to the twilight forest
		BEDROCK(Blocks.bedrock), //Find bedrock
		CAVERN(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.CLOAK.metadata)), //Cavern structure
		BURROW(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.MOSS.metadata)), //Burrow structure
		OCEAN(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.metadata)), //Ocean floor structure
		DESERTSTRUCT(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.COBBLE.metadata)),
		DIE(Items.skull), //die and lose energy
		ALLCOLORS(ChromaItems.ELEMENTAL.getStackOf(CrystalElement.CYAN)), //find all colors
		REPEATER(ChromaTiles.REPEATER.getCraftedProduct()), //craft any repeater type
		RAINBOWFOREST(ChromaBlocks.RAINBOWSAPLING.getBlockInstance()),
		DIMENSION(ChromaBlocks.PORTAL.getBlockInstance()),
		CTM(ChromaTiles.AURAPOINT.getCraftedProduct()),
		STORAGE(ChromaItems.STORAGE.getStackOf()),
		BALLLIGHTNING(ChromaStacks.auraDust),
		POWERCRYSTAL(ChromaTiles.CRYSTAL.getCraftedProduct()),
		TURBOCHARGE(ChromaTiles.PYLONTURBO.getCraftedProduct()),
		BREAKSPAWNER(Blocks.mob_spawner),
		KILLDRAGON(Blocks.dragon_egg),
		KILLWITHER(Items.nether_star),
		KILLMOB(new ItemStack(Items.skull, 1, 4)),
		ALLCORES(ChromaTiles.DIMENSIONCORE.getCraftedNBTProduct("color", CrystalElement.RED.ordinal())),
		USEENERGY(ChromaTiles.WEAKREPEATER.getCraftedProduct()),
		STRUCTCOMPLETE(ChromaBlocks.DIMDATA.getStackOf()),
		NETHERROOF(Blocks.netherrack),
		NETHERSTRUCT(Blocks.nether_brick),
		VILLAGECASTING(Blocks.cobblestone),
		NEVER(Blocks.stone, false), //used as a no-trigger placeholder
		;

		private final ItemStack icon;
		public final boolean active;

		public static final ProgressStage[] list = values();

		private ProgressStage(Block b, boolean... cond) {
			this(new ItemStack(b), cond);
		}

		private ProgressStage(Item b, boolean... cond) {
			this(new ItemStack(b), cond);
		}

		private ProgressStage(ItemStack is, boolean... cond) {
			icon = is;
			boolean flag = true;
			for (int i = 0; i < cond.length; i++)
				flag = flag && cond[i];
			active = flag;
			ChromaResearchManager.instance.register(this);
		}

		public boolean stepPlayerTo(EntityPlayer ep) {
			return instance.stepPlayerTo(ep, this);
		}

		public boolean isPlayerAtStage(EntityPlayer ep) {
			return instance.isPlayerAtStage(ep, this);
		}

		public boolean playerHasPrerequisites(EntityPlayer ep) {
			return instance.playerHasPrerequisites(ep, this);
		}

		public boolean isOneStepAway(EntityPlayer ep) {
			return instance.isOneStepAway(ep, this);
		}

		//@SideOnly(Side.CLIENT)
		public String getTitle() {
			return this.getTitleString();
		}

		//@SideOnly(Side.CLIENT)
		public String getShortDesc() {
			return ChromaDescriptions.getProgressText(this).desc;
		}

		//@SideOnly(Side.CLIENT)
		public String getTitleString() {
			//StatCollector.translateToLocal("chromaprog."+this.name().toLowerCase());
			return ChromaDescriptions.getProgressText(this).title;
		}

		//@SideOnly(Side.CLIENT)
		public String getHintString() {
			//StatCollector.translateToLocal("chromaprog."+this.name().toLowerCase());
			return ChromaDescriptions.getProgressText(this).hint;
		}

		//@SideOnly(Side.CLIENT)
		public String getRevealedString() {
			//StatCollector.translateToLocal("chromaprog.reveal."+this.name().toLowerCase());
			return ChromaDescriptions.getProgressText(this).reveal;
		}

		@SideOnly(Side.CLIENT)
		public void renderIcon(RenderItem ri, FontRenderer fr, int x, int y) {
			ReikaGuiAPI.instance.drawItemStack(ri, fr, icon, x, y);
		}

		public boolean isGatedAfter(ProgressStage p) {
			return ProgressionManager.instance.progressMap.getRecursiveParents(this).contains(p);
		}

		@Override
		public String getFormatting() {
			return EnumChatFormatting.UNDERLINE.toString();
		}

		@Override
		public boolean giveToPlayer(EntityPlayer ep, boolean notify) {
			return this.stepPlayerTo(ep);
		}
	}

	private ProgressionManager() {
		ResearchFetcher.progressManager = this;

		this.load();

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			ColorDiscovery c = new ColorDiscovery(e);
			StructureComplete s = new StructureComplete(e);
			colorDiscoveries.put(e, c);
			structureFlags.put(e, s);
			ChromaResearchManager.instance.register(c);
			ChromaResearchManager.instance.register(s);
		}
	}

	public void reload() {
		progressMap.clear();
		this.load();
	}

	private void load() {
		progressMap.addParent(ProgressStage.CASTING,	ProgressStage.CRYSTALS);

		progressMap.addParent(ProgressStage.ALLCOLORS,	ProgressStage.PYLON);

		progressMap.addParent(ProgressStage.RUNEUSE,	ProgressStage.ALLCOLORS);
		progressMap.addParent(ProgressStage.RUNEUSE,	ProgressStage.CASTING);

		progressMap.addParent(ProgressStage.MULTIBLOCK,	ProgressStage.RUNEUSE);

		progressMap.addParent(ProgressStage.LINK, 		ProgressStage.PYLON);
		progressMap.addParent(ProgressStage.LINK, 		ProgressStage.REPEATER);

		progressMap.addParent(ProgressStage.USEENERGY, 	ProgressStage.PYLON);
		progressMap.addParent(ProgressStage.USEENERGY, 	ProgressStage.RUNEUSE);

		progressMap.addParent(ProgressStage.CHARGE, 	ProgressStage.PYLON);
		progressMap.addParent(ProgressStage.CHARGE, 	ProgressStage.CRYSTALS);

		progressMap.addParent(ProgressStage.ABILITY, 	ProgressStage.CHARGE);
		progressMap.addParent(ProgressStage.ABILITY, 	ProgressStage.LINK);

		progressMap.addParent(ProgressStage.STONES, 	ProgressStage.MULTIBLOCK);

		progressMap.addParent(ProgressStage.SHOCK, 		ProgressStage.PYLON);

		progressMap.addParent(ProgressStage.MAKECHROMA, ProgressStage.CASTING);

		progressMap.addParent(ProgressStage.SHARDCHARGE, ProgressStage.MAKECHROMA);
		progressMap.addParent(ProgressStage.SHARDCHARGE, ProgressStage.RUNEUSE);
		progressMap.addParent(ProgressStage.SHARDCHARGE, ProgressStage.DYETREE);

		progressMap.addParent(ProgressStage.CHROMA, 	ProgressStage.MAKECHROMA);
		//progressMap.addParent(ProgressStage.CHROMA, 	ProgressStage.RUNEUSE);
		//progressMap.addParent(ProgressStage.CHROMA, 	ProgressStage.MULTIBLOCK);

		progressMap.addParent(ProgressStage.ALLOY, 		ProgressStage.SHARDCHARGE);
		progressMap.addParent(ProgressStage.ALLOY, 		ProgressStage.MULTIBLOCK);
		progressMap.addParent(ProgressStage.ALLOY, 		ProgressStage.CHROMA);

		progressMap.addParent(ProgressStage.NETHER, 	ProgressStage.BEDROCK);
		progressMap.addParent(ProgressStage.NETHERROOF, ProgressStage.NETHER);
		progressMap.addParent(ProgressStage.NETHERSTRUCT, ProgressStage.NETHERROOF);

		progressMap.addParent(ProgressStage.END, 		ProgressStage.NETHER);

		progressMap.addParent(ProgressStage.REPEATER, 	ProgressStage.MULTIBLOCK);
		progressMap.addParent(ProgressStage.REPEATER, 	ProgressStage.USEENERGY);

		progressMap.addParent(ProgressStage.STORAGE, 	ProgressStage.MULTIBLOCK);

		progressMap.addParent(ProgressStage.POWERCRYSTAL, ProgressStage.LINK);
		progressMap.addParent(ProgressStage.POWERCRYSTAL, ProgressStage.STORAGE);
		progressMap.addParent(ProgressStage.POWERCRYSTAL, ProgressStage.CHARGE);
		progressMap.addParent(ProgressStage.POWERCRYSTAL, ProgressStage.ALLOY);

		progressMap.addParent(ProgressStage.DIE,		ProgressStage.CHARGE);

		progressMap.addParent(ProgressStage.KILLDRAGON,	ProgressStage.END);

		progressMap.addParent(ProgressStage.KILLWITHER,	ProgressStage.NETHER);

		progressMap.addParent(ProgressStage.KILLDRAGON,	ProgressStage.KILLMOB);
		progressMap.addParent(ProgressStage.KILLWITHER,	ProgressStage.KILLMOB);

		progressMap.addParent(ProgressStage.DIMENSION,	ProgressStage.ALLCOLORS);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.END);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.NETHERSTRUCT);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.POWERCRYSTAL);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.RAINBOWFOREST);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.CAVERN);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.BURROW);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.OCEAN);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.DESERTSTRUCT);

		progressMap.addParent(ProgressStage.TURBOCHARGE, ProgressStage.DIMENSION);
		progressMap.addParent(ProgressStage.TURBOCHARGE, ProgressStage.STRUCTCOMPLETE);

		progressMap.addParent(ProgressStage.STRUCTCOMPLETE,	ProgressStage.ABILITY);
		progressMap.addParent(ProgressStage.STRUCTCOMPLETE,	ProgressStage.DIMENSION);

		progressMap.addParent(ProgressStage.ALLCORES,	ProgressStage.STRUCTCOMPLETE);

		progressMap.addParent(ProgressStage.CTM,		ProgressStage.ALLCORES);

		for (int i = 0; i < ProgressStage.list.length; i++) {
			ProgressStage p = ProgressStage.list[i];
			if (p.active && !progressMap.hasElement(p) && !progressMap.hasElementAsChild(p)) {
				progressMap.addChildless(p);
			}
		}

		auxiliaryReference.put(ProgressStage.CRYSTALS, ChromaResearch.CRYSTALS);
		auxiliaryReference.put(ProgressStage.PYLON, ChromaResearch.PYLONS);
		auxiliaryReference.put(ProgressStage.BURROW, ChromaResearch.BURROW);
		auxiliaryReference.put(ProgressStage.CAVERN, ChromaResearch.CAVERN);
		auxiliaryReference.put(ProgressStage.OCEAN, ChromaResearch.OCEAN);
		auxiliaryReference.put(ProgressStage.RAINBOWLEAF, ChromaResearch.RAINBOWLEAVES);
		auxiliaryReference.put(ProgressStage.DYETREE, ChromaResearch.DYELEAVES);
		auxiliaryReference.put(ProgressStage.BALLLIGHTNING, ChromaResearch.BALLLIGHTNING);
		auxiliaryReference.put(ProgressStage.ALLCOLORS, ChromaResearch.RUNES);
	}

	public Topology getTopology() {
		return progressMap.getTopology();//.sort(new AlphabeticalProgressComparator());
	}

	private Collection<ProgressStage> getPlayerData(EntityPlayer ep) {
		//if (playerMap.isEmpty()) {
		//	this.loadFromNBT(ep);
		//}
		return this.loadFromNBT(ep);//playerMap.get(ep.getCommandSenderName());
	}

	private Collection<ProgressStage> loadFromNBT(EntityPlayer ep) {
		NBTTagList li = this.getNBTList(ep);
		Collection<ProgressStage> c = new HashSet();
		Iterator<NBTTagString> it = li.tagList.iterator();
		while (it.hasNext()) {
			String val = it.next().func_150285_a_();
			try {
				c.add(ProgressStage.valueOf(val));
			}
			catch (IllegalArgumentException e) {
				ChromatiCraft.logger.logError("Could not load progress stage from NBT String "+val);
			}
		}
		playerMap.put(ep.getCommandSenderName(), c);
		//this.verify(ep);
		return c;
	}

	private void verify(EntityPlayer ep) {
		boolean changed = false;
		String s = ep.getCommandSenderName();
		do {
			Collection<ProgressStage> c = playerMap.get(s);
			Iterator<ProgressStage> it = c.iterator();
			while (it.hasNext()) {
				ProgressStage p = it.next();
				if (!this.playerHasPrerequisites(ep, p)) {
					it.remove();
					changed = true;
					ChromatiCraft.logger.logError("Player "+s+" had progress element "+p+" without its prereqs! Removing!");
				}
			}
		} while (changed);
	}

	private NBTTagList getNBTList(EntityPlayer ep) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		if (!nbt.hasKey(MAIN_NBT_TAG))
			nbt.setTag(MAIN_NBT_TAG, new NBTTagList());
		NBTTagList li = nbt.getTagList(MAIN_NBT_TAG, NBTTypes.STRING.ID);
		return li;
	}

	private boolean isPlayerAtStage(EntityPlayer ep, ProgressStage s) {
		return this.getPlayerData(ep).contains(s);
	}

	public Collection<ProgressStage> getStagesFor(EntityPlayer ep) {
		Collection<ProgressStage> c = this.getPlayerData(ep);
		return c != null ? Collections.unmodifiableCollection(c) : new ArrayList();
	}

	public Collection<ProgressStage> getStagesForFromNBT(EntityPlayer ep) {
		Collection<ProgressStage> c = this.getPlayerData(ep);
		return c != null ? Collections.unmodifiableCollection(c) : new ArrayList();
	}

	public Collection<UUID> getSlavedIDs(EntityPlayer ep) {
		Collection<UUID> c = new HashSet();
		for (NBTTagString s : ((List<NBTTagString>)this.getCooperatorList(ep).tagList)) {
			try {
				c.add(UUID.fromString(s.func_150285_a_()));
			}
			catch (IllegalArgumentException e) {
				ChromatiCraft.logger.logError("Could not load cooperator UUID "+s.func_150285_a_()+"' as a cooperator with "+ep.getCommandSenderName());
			}
		}
		return c;
	}

	public boolean linkProgression(EntityPlayer ep1, EntityPlayer ep2, boolean link) {
		if (!this.canLinkProgression(ep1, ep2))
			return false;
		NBTTagString s1 = new NBTTagString(ep2.getUniqueID().toString());
		NBTTagString s2 = new NBTTagString(ep1.getUniqueID().toString());
		NBTTagList li1 = this.getCooperatorList(ep1);
		NBTTagList li2 = this.getCooperatorList(ep2);
		if (link) {
			li1.appendTag(s1);
			li2.appendTag(s2);
		}
		else {
			li1.tagList.remove(s1);
			li2.tagList.remove(s2);
		}
		ReikaPlayerAPI.getDeathPersistentNBT(ep1).setTag(COOPERATE_NBT_TAG, li1);
		ReikaPlayerAPI.getDeathPersistentNBT(ep2).setTag(COOPERATE_NBT_TAG, li2);
		return true;
	}

	private boolean canLinkProgression(EntityPlayer ep1, EntityPlayer ep2) {
		if (ReikaPlayerAPI.isFake(ep1) || ReikaPlayerAPI.isFake(ep2))
			return false;
		return this.isProgressionEqual(ep1, ep2) && ChromaResearchManager.instance.getPlayerResearchLevel(ep1) == ChromaResearchManager.instance.getPlayerResearchLevel(ep2);//playerProgressionComparator.compare(ep1, ep2) == 0;
	}

	public boolean isProgressionEqual(EntityPlayer ep1, EntityPlayer ep2) {
		Collection<ProgressStage> c1 = this.getStagesFor(ep1);
		Collection<ProgressStage> c2 = this.getStagesFor(ep2);
		return c1.equals(c2);
	}

	private boolean playerCanReceiveProgressWith(EntityPlayer e, EntityPlayer ep) {
		return e.getDistanceToEntity(ep) <= 12;
	}

	private NBTTagList getCooperatorList(EntityPlayer ep) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		if (!nbt.hasKey(COOPERATE_NBT_TAG))
			nbt.setTag(COOPERATE_NBT_TAG, new NBTTagList());
		NBTTagList li = nbt.getTagList(COOPERATE_NBT_TAG, NBTTypes.STRING.ID);
		return li;
	}

	private boolean stepPlayerTo(EntityPlayer ep, ProgressStage s) {
		if (ep == null) {
			ChromatiCraft.logger.logError("Tried to give progress '"+s+"' to null player???");
			return false;
		}
		if (!this.canStepPlayerTo(ep, s))
			return false;
		this.setPlayerStage(ep, s, true, true);
		return true;
	}

	private boolean canStepPlayerTo(EntityPlayer ep, ProgressStage s) {
		if (ReikaPlayerAPI.isFake(ep))
			return false;
		if (this.isPlayerAtStage(ep, s))
			return false;
		if (!this.playerHasPrerequisites(ep, s))
			return false;
		return true;
	}

	public boolean playerHasPrerequisites(EntityPlayer ep, ProgressStage s) {
		Collection<ProgressStage> c = progressMap.getParents(s);
		if (c == null || c.isEmpty())
			return true;
		for (ProgressStage s2 : c) {
			if (!this.isPlayerAtStage(ep, s2))
				return false;
		}
		return true;
	}
	/*
	public boolean playerHasAnyPrereq(EntityPlayer ep, ProgressStage s) {

	}
	 */
	public Collection<ProgressStage> getPrereqs(ProgressStage s) {
		return Collections.unmodifiableCollection(progressMap.getParents(s));
	}

	public ProgressStage[] getPrereqsArray(ProgressStage s) {
		Collection<ProgressStage> c = progressMap.getParents(s);
		return c != null ? c.toArray(new ProgressStage[c.size()]) : new ProgressStage[0];
	}

	private boolean isOneStepAway(EntityPlayer ep, ProgressStage s) {
		if (this.isPlayerAtStage(ep, s))
			return false;
		Collection<ProgressStage> c = progressMap.getParents(s);
		if (c == null || c.isEmpty())
			return false;
		for (ProgressStage par : c) {
			if (this.isPlayerAtStage(ep, par))
				return false;
			Collection<ProgressStage> c2 = progressMap.getParents(par);
			for (ProgressStage par2 : c2) {
				if (!this.isPlayerAtStage(ep, par))
					return false;
			}
		}
		return true;
	}

	public boolean setPlayerStage(EntityPlayer ep, int val, boolean set, boolean notify) {
		if (ReikaPlayerAPI.isFake(ep))
			return false;
		if (val < 0 || val >= ProgressStage.values().length)
			return false;
		this.setPlayerStage(ep, ProgressStage.values()[val], set, notify);
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void setPlayerStageClient(EntityPlayer ep, ProgressStage s, boolean set, boolean notify) {
		this.setPlayerStage(ep, s, set, true, notify);
	}

	public void setPlayerStage(EntityPlayer ep, ProgressStage s, boolean set, boolean notify) {
		this.setPlayerStage(ep, s, set, false, notify);
	}

	private void setPlayerStage(EntityPlayer ep, ProgressStage s, boolean set, boolean allowClient, boolean notify) {
		//ReikaJavaLibrary.pConsole("Giving "+ep.getCommandSenderName()+" progress '"+s+"': "+set+"/"+allowClient+"/"+notify);
		//ReikaJavaLibrary.pConsole("NBT PRE: ");
		//for (String sg : ReikaNBTHelper.parseNBTAsLines(ReikaPlayerAPI.getDeathPersistentNBT(ep)))
		//	ReikaJavaLibrary.pConsole(sg);
		if (ReikaPlayerAPI.isFake(ep))
			return;
		if (ep.worldObj.isRemote && !allowClient)
			return;
		Collection<UUID> coop = this.getSlavedIDs(ep);
		Collection<EntityPlayer> players = new ArrayList();
		for (UUID u : coop) {
			EntityPlayer e = ep.worldObj.func_152378_a(u);
			if (e == null || ReikaPlayerAPI.isFake(e)) {
				return;
			}
			if (!this.playerCanReceiveProgressWith(e, ep)) {
				return;
			}
			players.add(e);
		}
		for (EntityPlayer e : players) {
			this.setPlayerStage(ep, s, set, allowClient, notify);
		}
		if (notify && ep instanceof EntityPlayerMP)
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.GIVEPROGRESS.ordinal(), (EntityPlayerMP)ep, s.ordinal(), set ? 1 : 0);
		NBTTagList li = this.getNBTList(ep);
		NBTBase tag = new NBTTagString(s.name());
		boolean flag = false;
		if (set) {
			if (!li.tagList.contains(tag)) {
				flag = true;
				li.appendTag(tag);
				Collection<ProgressStage> c = progressMap.getRecursiveParents(s);
				for (ProgressStage s2 : c) {
					NBTBase tag2 = new NBTTagString(s2.name());
					li.tagList.add(tag2);
				}
			}
		}
		else {
			if (li.tagList.contains(tag)) {
				flag = true;
				li.tagList.remove(tag);
				Collection<ProgressStage> c = progressMap.getRecursiveChildren(s);
				for (ProgressStage s2 : c) {
					NBTBase tag2 = new NBTTagString(s2.name());
					li.tagList.remove(tag2);
				}
			}
		}
		if (flag) {
			ReikaPlayerAPI.getDeathPersistentNBT(ep).setTag(MAIN_NBT_TAG, li);
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			if (set) {
				playerMap.addValue(ep.getCommandSenderName(), s);
				if (notify)
					ChromaResearchManager.instance.notifyPlayerOfProgression(ep, s);
				this.giveAuxiliaryResearch(ep, s);
				MinecraftForge.EVENT_BUS.post(new ProgressionEvent(ep, s.name(), ResearchType.PROGRESS));
			}
			else {
				playerMap.remove(ep.getCommandSenderName(), s);
			}
			if (notify)
				this.updateChunks(ep);
		}
		//ReikaJavaLibrary.pConsole("NBT POST: ");
		//for (String sg : ReikaNBTHelper.parseNBTAsLines(ReikaPlayerAPI.getDeathPersistentNBT(ep)))
		//	ReikaJavaLibrary.pConsole(sg);
	}

	public void resetPlayerProgression(EntityPlayer ep, boolean notify) {
		NBTTagList li = this.getNBTList(ep);
		li.tagList.clear();
		Collection<ProgressStage> c = playerMap.remove(ep.getCommandSenderName());
		if (notify) {
			if (ep instanceof EntityPlayerMP) {
				EntityPlayerMP emp = (EntityPlayerMP)ep;
				for (ProgressStage p : c) {
					ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.GIVEPROGRESS.ordinal(), emp, p.ordinal(), 0);
				}
			}
		}
		ReikaPlayerAPI.getDeathPersistentNBT(ep).setTag(MAIN_NBT_TAG, li);
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			this.setPlayerDiscoveredColor(ep, CrystalElement.elements[i], false, notify);
			this.markPlayerCompletedStructureColor(ep, CrystalElement.elements[i], false, notify);
		}
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
		if (notify)
			this.updateChunks(ep);
	}

	public void maxPlayerProgression(EntityPlayer ep, boolean notify) {
		for (int i = 0; i < ProgressStage.list.length; i++) {
			if (ProgressStage.list[i].active)
				this.setPlayerStage(ep, ProgressStage.list[i], true, notify);
		}
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			this.setPlayerDiscoveredColor(ep, CrystalElement.elements[i], true, notify);
			this.markPlayerCompletedStructureColor(ep, CrystalElement.elements[i], true, notify);
		}
		for (int i = 0; i < RecipeType.typeList.length; i++) {
			RecipeType r = RecipeType.typeList[i];
			RecipesCastingTable.setPlayerHasCrafted(ep, r);
		}
	}

	public boolean setPlayerDiscoveredColor(EntityPlayer ep, CrystalElement e, boolean disc, boolean notify) {
		//ReikaJavaLibrary.pConsole(this.getPlayerData(ep));
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound tag = nbt.getCompoundTag(COLOR_NBT_TAG);
		boolean had = tag.getBoolean(e.name());
		tag.setBoolean(e.name(), disc);
		if (had != disc) {
			nbt.setTag(COLOR_NBT_TAG, tag);
			if (disc)
				this.checkPlayerColors(ep);
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			if (notify)
				this.updateChunks(ep);
			if (disc && notify)
				ChromaResearchManager.instance.notifyPlayerOfProgression(ep, colorDiscoveries.get(e));
			if (disc)
				MinecraftForge.EVENT_BUS.post(new ProgressionEvent(ep, e.name(), ResearchType.COLOR));
			return true;
		}
		return false;
	}

	private void checkPlayerColors(EntityPlayer ep) {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			if (!this.hasPlayerDiscoveredColor(ep, CrystalElement.elements[i]))
				return;
		}
		ProgressStage.ALLCOLORS.stepPlayerTo(ep);
	}

	public void updateChunks(EntityPlayer ep) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			ReikaRenderHelper.rerenderAllChunksLazily();
		else
			ReikaPacketHelper.sendUpdatePacket(DragonAPIInit.packetChannel, PacketIDs.RERENDER.ordinal(), 0, 0, 0, new PacketTarget.PlayerTarget((EntityPlayerMP)ep));
	}

	public boolean hasPlayerDiscoveredColor(EntityPlayer ep, CrystalElement e) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep).getCompoundTag(COLOR_NBT_TAG);
		return nbt.getBoolean(e.name());
	}

	public Collection<CrystalElement> getColorsFor(EntityPlayer ep) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep).getCompoundTag(COLOR_NBT_TAG);
		Collection<CrystalElement> c = new ArrayList();
		for (Object o : nbt.func_150296_c()) {
			String tag = (String)o;
			if (nbt.getBoolean(tag))
				c.add(CrystalElement.valueOf(tag));
		}
		return c;
	}

	public static class ColorDiscovery implements ProgressElement {

		public final CrystalElement color;

		private ColorDiscovery(CrystalElement e) {
			color = e;
		}

		@Override
		//@SideOnly(Side.CLIENT)
		public String getTitle() {
			return color.displayName;
		}

		@Override
		//@SideOnly(Side.CLIENT)
		public String getShortDesc() {
			return "A new form of crystal energy";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void renderIcon(RenderItem ri, FontRenderer fr, int x, int y) {
			ReikaGuiAPI.instance.drawItemStack(ri, fr, ChromaBlocks.RUNE.getStackOfMetadata(color.ordinal()), x, y);
		}

		@Override
		public String toString() {
			return "Discover_"+color.name();
		}

		@Override
		public int hashCode() {
			return color.ordinal();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof ColorDiscovery && ((ColorDiscovery)o).color == color;
		}

		@Override
		public String getFormatting() {
			return color.getChatColorString();
		}

		@Override
		public boolean giveToPlayer(EntityPlayer ep, boolean notify) {
			return instance.setPlayerDiscoveredColor(ep, color, true, notify);
		}

	}

	public static class StructureComplete implements ProgressElement {

		public final CrystalElement color;

		private StructureComplete(CrystalElement e) {
			color = e;
		}

		@Override
		//@SideOnly(Side.CLIENT)
		public String getTitle() {
			return color.displayName+" Core";
		}

		@Override
		//@SideOnly(Side.CLIENT)
		public String getShortDesc() {
			return "Another piece of the puzzle";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void renderIcon(RenderItem ri, FontRenderer fr, int x, int y) {
			ItemStack is = ChromaTiles.DIMENSIONCORE.getCraftedProduct();
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setInteger("color", color.ordinal());
			ReikaGuiAPI.instance.drawItemStack(ri, fr, is, x, y);
		}

		@Override
		public String toString() {
			return "Structure_"+color.name();
		}

		@Override
		public int hashCode() {
			return color.ordinal();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof StructureComplete && ((StructureComplete)o).color == color;
		}

		@Override
		public String getFormatting() {
			return color.getChatColorString();
		}

		@Override
		public boolean giveToPlayer(EntityPlayer ep, boolean notify) {
			return instance.markPlayerCompletedStructureColor(ep, color, true, notify);
		}

	}

	public void giveAuxiliaryResearch(EntityPlayer ep, ProgressStage p) {
		if (ChromaOptions.EASYFRAG.getState()) {
			ChromaResearch r = auxiliaryReference.get(p);
			if (r != null)
				ChromaResearchManager.instance.givePlayerFragment(ep, r, true);
		}
	}

	@Override
	public boolean playerHasResearch(EntityPlayer ep, String key) {
		try {
			ProgressStage p = ProgressStage.valueOf(key.toUpperCase());
			return p.isPlayerAtStage(ep);
		}
		catch (IllegalArgumentException e) {
			ChromatiCraft.logger.logError("A mod tried to fetch an invalid progress stage '"+key+"'!");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public HashSet<String> getAllResearches() {
		HashSet<String> c = new HashSet();
		for (int i = 0; i < ProgressStage.list.length; i++) {
			c.add(ProgressStage.list[i].name());
		}
		return c;
	}

	@Override
	public HashSet<String> getPrerequisites(String key) {
		try {
			ProgressStage p = ProgressStage.valueOf(key.toUpperCase());
			Collection<ProgressStage> c = this.getPrereqs(p);
			HashSet<String> h = new HashSet();
			for (ProgressStage req : c) {
				h.add(req.name());
			}
			return h;
		}
		catch (IllegalArgumentException e) {
			ChromatiCraft.logger.logError("A mod tried to fetch the state of an invalid progress stage '"+key+"'!");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean canPlayerStepTo(EntityPlayer ep, String key) {
		try {
			ProgressStage p = ProgressStage.valueOf(key.toUpperCase());
			return this.canStepPlayerTo(ep, p);
		}
		catch (IllegalArgumentException e) {
			ChromatiCraft.logger.logError("A mod tried to fetch the state of an invalid progress stage '"+key+"'!");
			e.printStackTrace();
			return false;
		}
	}

	public boolean hasPlayerDiscoveredAGeneratedStructure(EntityPlayer ep) {
		return ProgressStage.BURROW.isPlayerAtStage(ep) || ProgressStage.CAVERN.isPlayerAtStage(ep) || ProgressStage.OCEAN.isPlayerAtStage(ep) || ProgressStage.DESERTSTRUCT.isPlayerAtStage(ep);
	}

	@Override
	public boolean playerDiscoveredElement(EntityPlayer ep, CrystalElementProxy e) {
		return this.hasPlayerDiscoveredColor(ep, CrystalElement.getFromAPI(e));
	}

	public boolean hasPlayerCompletedStructureColor(EntityPlayer ep, CrystalElement e) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep).getCompoundTag(STRUCTURE_NBT_TAG);
		return nbt.getBoolean(e.name());
	}

	public boolean markPlayerCompletedStructureColor(EntityPlayer ep, CrystalElement e, boolean set, boolean notify) {
		//ReikaJavaLibrary.pConsole(this.getPlayerData(ep));
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound tag = nbt.getCompoundTag(STRUCTURE_NBT_TAG);
		boolean had = tag.getBoolean(e.name());
		tag.setBoolean(e.name(), set);
		if (had != set) {
			nbt.setTag(STRUCTURE_NBT_TAG, tag);
			if (set) {
				ProgressStage.STRUCTCOMPLETE.stepPlayerTo(ep);
				this.checkPlayerStructures(ep);
			}
			else {
				this.setPlayerStage(ep, ProgressStage.ALLCORES, false, notify);
			}
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			if (notify)
				this.updateChunks(ep);
			if (set && notify)
				ChromaResearchManager.instance.notifyPlayerOfProgression(ep, structureFlags.get(e));
			if (set)
				MinecraftForge.EVENT_BUS.post(new ProgressionEvent(ep, e.name(), ResearchType.DIMSTRUCT));
			return true;
		}
		return false;
	}

	private void checkPlayerStructures(EntityPlayer ep) {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			if (!this.hasPlayerCompletedStructureColor(ep, CrystalElement.elements[i])) {
				return;
			}
		}
		ProgressStage.ALLCORES.stepPlayerTo(ep);
	}

	public Collection<CrystalElement> getStructuresFor(EntityPlayer ep) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep).getCompoundTag(STRUCTURE_NBT_TAG);
		Collection<CrystalElement> c = new ArrayList();
		for (Object o : nbt.func_150296_c()) {
			String tag = (String)o;
			if (nbt.getBoolean(tag))
				c.add(CrystalElement.valueOf(tag));
		}
		return c;
	}

	private static class AlphabeticProgressComparator implements Comparator<ProgressStage> {

		@Override
		public int compare(ProgressStage o1, ProgressStage o2) {
			return o1.getTitleString().compareToIgnoreCase(o2.getTitleString());
		}

	}

	/*
	private static class PlayerProgressionComparator implements Comparator<EntityPlayer> {

		@Override
		public int compare(EntityPlayer o1, EntityPlayer o2) {
			return this.getFlags(o1)-this.getFlags(o2);
		}

		private int getFlags(EntityPlayer ep) {
			int sum = 0;
			for (ProgressStage p : ProgressionManager.instance.getStagesFor(ep)) {
sum += (1 << 4*getDepth());
			}
			return sum;
		}

	}
	 */

}
