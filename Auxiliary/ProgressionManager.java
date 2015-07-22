/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.block.Block;
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
import net.minecraftforge.common.util.FakePlayer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.CrystalElementProxy;
import Reika.ChromatiCraft.API.ResearchFetcher;
import Reika.ChromatiCraft.API.ResearchFetcher.ProgressRegistry;
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
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ProgressionManager implements ProgressRegistry {

	public static final ProgressionManager instance = new ProgressionManager();

	private static final String NBT_TAG = "Chroma_Progression";
	private static final String NBT_TAG2 = "Chroma_Element_Discovery";

	private final SequenceMap<ProgressStage> progressMap = new SequenceMap();

	private final MultiMap<String, ProgressStage> playerMap = new MultiMap(new MultiMap.HashSetFactory());

	private final EnumMap<CrystalElement, ColorDiscovery> colorDiscoveries = new EnumMap(CrystalElement.class);

	private final EnumMap<ProgressStage, ChromaResearch> auxiliaryReference = new EnumMap(ProgressStage.class);

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
		RAINBOWLEAF(ChromaBlocks.RAINBOWLEAF.getStackOf()), //harvest a rainbow leaf
		CHROMA(ChromaTiles.COLLECTOR.getCraftedProduct()), //step in liquid chroma
		STONES(ChromaStacks.elementUnit), //craft all elemental stones together
		SHOCK(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(5)), //get hit by a pylon
		HIVE(ChromaBlocks.HIVE.getBlockInstance(), ModList.FORESTRY.isLoaded()),
		NETHER(Blocks.portal), //go to the nether
		END(Blocks.end_portal_frame), //go to the end
		TWILIGHT(ModWoodList.CANOPY.getItem(), ModList.TWILIGHT.isLoaded()), //Go to the twilight forest
		BEDROCK(Blocks.bedrock), //Find bedrock
		CAVERN(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.CLOAK.metadata)), //Cavern structure
		BURROW(Blocks.chest), //Burrow structure
		OCEAN(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.metadata)), //Ocean floor structure
		DIE(Items.skull), //die and lose energy
		ALLCOLORS(ChromaItems.ELEMENTAL.getStackOf(CrystalElement.CYAN)), //find all colors
		REPEATER(ChromaTiles.REPEATER.getCraftedProduct()), //craft any repeater type
		RAINBOWFOREST(ChromaBlocks.RAINBOWSAPLING.getBlockInstance()),
		DIMENSION(ChromaBlocks.PORTAL.getBlockInstance()),
		CTM(ChromaBlocks.SUPER.getStackOfMetadata(CrystalElement.YELLOW.ordinal())), //icon is a placeholder
		STORAGE(ChromaItems.STORAGE.getStackOf()),
		BALLLIGHTNING(ChromaStacks.auraDust),
		POWERCRYSTAL(ChromaTiles.CRYSTAL.getCraftedProduct()),
		BREAKSPAWNER(Blocks.mob_spawner),
		KILLDRAGON(Blocks.dragon_egg),
		KILLWITHER(Items.nether_star),
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

		@SideOnly(Side.CLIENT)
		public String getTitle() {
			return this.getTitleString();
		}

		@SideOnly(Side.CLIENT)
		public String getShortDesc() {
			return this.getRevealedString();
		}

		@SideOnly(Side.CLIENT)
		public String getTitleString() {
			//StatCollector.translateToLocal("chromaprog."+this.name().toLowerCase());
			return ChromaDescriptions.getProgressText(this).title;
		}

		@SideOnly(Side.CLIENT)
		public String getHintString() {
			//StatCollector.translateToLocal("chromaprog."+this.name().toLowerCase());
			return ChromaDescriptions.getProgressText(this).hint;
		}

		@SideOnly(Side.CLIENT)
		public String getRevealedString() {
			//StatCollector.translateToLocal("chromaprog.reveal."+this.name().toLowerCase());
			return ChromaDescriptions.getProgressText(this).reveal;
		}

		@SideOnly(Side.CLIENT)
		public ItemStack getIcon() {
			return icon.copy();
		}
	}

	private ProgressionManager() {
		ResearchFetcher.progressManager = this;

		this.load();

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			ColorDiscovery c = new ColorDiscovery(e);
			colorDiscoveries.put(e, c);
			ChromaResearchManager.instance.register(c);
		}
	}

	public void reload() {
		progressMap.clear();
		this.load();
	}

	private void load() {
		progressMap.addParent(ProgressStage.CASTING,	ProgressStage.CRYSTALS);

		progressMap.addParent(ProgressStage.RUNEUSE,	ProgressStage.CASTING);

		progressMap.addParent(ProgressStage.MULTIBLOCK,	ProgressStage.RUNEUSE);

		progressMap.addParent(ProgressStage.LINK, 		ProgressStage.PYLON);
		progressMap.addParent(ProgressStage.LINK, 		ProgressStage.REPEATER);

		progressMap.addParent(ProgressStage.CHARGE, 	ProgressStage.PYLON);
		progressMap.addParent(ProgressStage.CHARGE, 	ProgressStage.CRYSTALS);

		progressMap.addParent(ProgressStage.ABILITY, 	ProgressStage.CHARGE);
		progressMap.addParent(ProgressStage.ABILITY, 	ProgressStage.LINK);

		progressMap.addParent(ProgressStage.STONES, 	ProgressStage.MULTIBLOCK);

		progressMap.addParent(ProgressStage.SHOCK, 		ProgressStage.PYLON);

		progressMap.addParent(ProgressStage.CHROMA, 	ProgressStage.MULTIBLOCK);

		progressMap.addParent(ProgressStage.NETHER, 	ProgressStage.BEDROCK);

		progressMap.addParent(ProgressStage.END, 		ProgressStage.NETHER);

		progressMap.addParent(ProgressStage.ALLCOLORS,	ProgressStage.PYLON);

		progressMap.addParent(ProgressStage.REPEATER, 	ProgressStage.MULTIBLOCK);

		progressMap.addParent(ProgressStage.STORAGE, 	ProgressStage.MULTIBLOCK);

		progressMap.addParent(ProgressStage.RUNEUSE,	ProgressStage.ALLCOLORS);

		progressMap.addParent(ProgressStage.POWERCRYSTAL, ProgressStage.LINK);
		progressMap.addParent(ProgressStage.POWERCRYSTAL, ProgressStage.STORAGE);
		progressMap.addParent(ProgressStage.POWERCRYSTAL, ProgressStage.CHARGE);

		progressMap.addParent(ProgressStage.DIE,		ProgressStage.CHARGE);

		progressMap.addParent(ProgressStage.KILLDRAGON,	ProgressStage.END);

		progressMap.addParent(ProgressStage.KILLWITHER,	ProgressStage.NETHER);

		progressMap.addParent(ProgressStage.DIMENSION,	ProgressStage.ALLCOLORS);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.END);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.POWERCRYSTAL);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.RAINBOWFOREST);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.CAVERN);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.BURROW);
		progressMap.addParent(ProgressStage.DIMENSION, 	ProgressStage.OCEAN);

		progressMap.addParent(ProgressStage.CTM,		ProgressStage.DIMENSION);

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
		return progressMap.getTopology();
	}

	private Collection<ProgressStage> getPlayerData(EntityPlayer ep) {
		//if (playerMap.isEmpty()) {
		//	this.loadFromNBT(ep);
		//}
		return this.loadFromNBT(ep);//playerMap.get(ep.getCommandSenderName());
	}

	private Collection<ProgressStage> loadFromNBT(EntityPlayer ep) {
		NBTTagList li = this.getNBTList(ep);
		Collection<ProgressStage> c = new ArrayList();
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
		if (!nbt.hasKey(NBT_TAG))
			nbt.setTag(NBT_TAG, new NBTTagList());
		NBTTagList li = nbt.getTagList(NBT_TAG, NBTTypes.STRING.ID);
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

	private boolean stepPlayerTo(EntityPlayer ep, ProgressStage s) {
		if (ep == null) {
			ChromatiCraft.logger.logError("Tried to give progress '"+s+"' to null player???");
			return false;
		}
		if (!this.canStepPlayerTo(ep, s))
			return false;
		this.setPlayerStage(ep, s, true);
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

	public boolean setPlayerStage(EntityPlayer ep, int val, boolean set) {
		if (ep instanceof FakePlayer)
			return false;
		if (val < 0 || val >= ProgressStage.values().length)
			return false;
		this.setPlayerStage(ep, ProgressStage.values()[val], set);
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void setPlayerStageClient(EntityPlayer ep, ProgressStage s, boolean set) {
		this.setPlayerStage(ep, s, set, true);
	}

	public void setPlayerStage(EntityPlayer ep, ProgressStage s, boolean set) {
		this.setPlayerStage(ep, s, set, false);
	}

	private void setPlayerStage(EntityPlayer ep, ProgressStage s, boolean set, boolean allowClient) {
		if (ep instanceof FakePlayer)
			return;
		if (ep.worldObj.isRemote && !allowClient)
			return;
		if (ep instanceof EntityPlayerMP)
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.GIVEPROGRESS.ordinal(), (EntityPlayerMP)ep, s.ordinal(), set ? 1 : 0);
		NBTTagList li = this.getNBTList(ep);
		NBTBase tag = new NBTTagString(s.name());
		boolean flag = false;
		if (set) {
			if (!li.tagList.contains(tag)) {
				flag = true;
				li.appendTag(tag);
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
			ReikaPlayerAPI.getDeathPersistentNBT(ep).setTag(NBT_TAG, li);
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			if (set) {
				playerMap.addValue(ep.getCommandSenderName(), s);

				ChromaResearchManager.instance.notifyPlayerOfProgression(ep, s);
				this.giveAuxiliaryResearch(ep, s);
			}
			else {
				playerMap.remove(ep.getCommandSenderName(), s);
			}
			this.updateChunks(ep);
		}
	}

	public void resetPlayerProgression(EntityPlayer ep) {
		NBTTagList li = this.getNBTList(ep);
		li.tagList.clear();
		Collection<ProgressStage> c = playerMap.remove(ep.getCommandSenderName());
		if (ep instanceof EntityPlayerMP) {
			EntityPlayerMP emp = (EntityPlayerMP)ep;
			for (ProgressStage p : c) {
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.GIVEPROGRESS.ordinal(), emp, p.ordinal(), 0);
			}
		}
		ReikaPlayerAPI.getDeathPersistentNBT(ep).setTag(NBT_TAG, li);
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			this.setPlayerDiscoveredColor(ep, CrystalElement.elements[i], false);
		}
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
		this.updateChunks(ep);
	}

	public void maxPlayerProgression(EntityPlayer ep) {
		for (int i = 0; i < ProgressStage.list.length; i++) {
			if (ProgressStage.list[i].active)
				this.setPlayerStage(ep, ProgressStage.list[i], true);
		}
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			this.setPlayerDiscoveredColor(ep, CrystalElement.elements[i], true);
		}
		for (int i = 0; i < RecipeType.typeList.length; i++) {
			RecipeType r = RecipeType.typeList[i];
			RecipesCastingTable.setPlayerHasCrafted(ep, r);
		}
	}

	public void setPlayerDiscoveredColor(EntityPlayer ep, CrystalElement e, boolean disc) {
		//ReikaJavaLibrary.pConsole(this.getPlayerData(ep));
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound tag = nbt.getCompoundTag(NBT_TAG2);
		boolean had = tag.getBoolean(e.name());
		tag.setBoolean(e.name(), disc);
		if (!had) {
			nbt.setTag(NBT_TAG2, tag);
			if (disc)
				this.checkPlayerColors(ep);
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			this.updateChunks(ep);
			if (disc)
				ChromaResearchManager.instance.notifyPlayerOfProgression(ep, colorDiscoveries.get(e));
		}
	}

	private void checkPlayerColors(EntityPlayer ep) {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			if (!this.hasPlayerDiscoveredColor(ep, CrystalElement.elements[i]))
				return;
		}
		ProgressStage.ALLCOLORS.stepPlayerTo(ep);
	}

	private void updateChunks(EntityPlayer ep) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			ReikaRenderHelper.rerenderAllChunks();
		else
			ReikaPacketHelper.sendUpdatePacket(DragonAPIInit.packetChannel, PacketIDs.RERENDER.ordinal(), ep.worldObj, 0, 0, 0);
	}

	public boolean hasPlayerDiscoveredColor(EntityPlayer ep, CrystalElement e) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep).getCompoundTag(NBT_TAG2);
		return nbt.getBoolean(e.name());
	}

	public Collection<CrystalElement> getColorsFor(EntityPlayer ep) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep).getCompoundTag(NBT_TAG2);
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
		@SideOnly(Side.CLIENT)
		public String getTitle() {
			return color.displayName;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public String getShortDesc() {
			return "A new form of crystal energy";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getIcon() {
			return ChromaBlocks.RUNE.getStackOfMetadata(color.ordinal());
		}

	}

	public void giveAuxiliaryResearch(EntityPlayer ep, ProgressStage p) {
		if (ChromaOptions.EASYFRAG.getState()) {
			ChromaResearch r = auxiliaryReference.get(p);
			if (r != null)
				ChromaResearchManager.instance.givePlayerFragment(ep, r);
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

	@Override
	public boolean playerDiscoveredElement(EntityPlayer ep, CrystalElementProxy e) {
		return this.hasPlayerDiscoveredColor(ep, CrystalElement.getFromAPI(e));
	}

}
