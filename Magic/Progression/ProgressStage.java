package Reika.ChromatiCraft.Magic.Progression;

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Items.ItemUnknownArtefact.ArtefactTypes;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager.ProgressElement;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum ProgressStage implements ProgressElement, ProgressAccess {

	CASTING(		Shareability.SELFONLY,	Reloadability.NEVER,	ChromaTiles.TABLE), //Do a recipe
	CRYSTALS(		Shareability.SELFONLY,	Reloadability.NEVER,	ChromaBlocks.CRYSTAL.getStackOfMetadata(CrystalElement.RED.ordinal())), //Found a crystal
	DYETREE(		Shareability.SELFONLY,	Reloadability.NEVER,	ChromaBlocks.DYELEAF.getStackOfMetadata(CrystalElement.YELLOW.ordinal())), //Harvest a dye tree
	MULTIBLOCK(		Shareability.PROXIMITY,	Reloadability.TRIGGER,	ChromaTiles.STAND), //Assembled a multiblock
	RUNEUSE(		Shareability.PROXIMITY,	Reloadability.TRIGGER,	ChromaBlocks.RUNE.getStackOfMetadata(CrystalElement.ORANGE.ordinal())), //Placed runes
	PYLON(			Shareability.SELFONLY,	Reloadability.NEVER,	ChromaTiles.PYLON), //Found pylon
	LINK(			Shareability.PROXIMITY,	Reloadability.NEVER,	ChromaTiles.COMPOUND), //Made a network connection/high-tier crafting
	CHARGE(			Shareability.SELFONLY,	Reloadability.NEVER,	ChromaItems.TOOL.getStackOf()), //charge from a pylon
	ABILITY(		Shareability.SELFONLY,	Reloadability.NEVER,	ChromaTiles.RITUAL), //use an ability
	RAINBOWLEAF(	Shareability.PROXIMITY,	Reloadability.ALWAYS,	ChromaBlocks.RAINBOWLEAF.getStackOfMetadata(3)), //harvest a rainbow leaf
	MAKECHROMA(		Shareability.PROXIMITY,	Reloadability.ALWAYS,	ChromaTiles.COLLECTOR),
	SHARDCHARGE(	Shareability.PROXIMITY,	Reloadability.NEVER,	ChromaStacks.chargedRedShard),
	ALLOY(			Shareability.PROXIMITY,	Reloadability.NEVER,	ChromaStacks.chromaIngot),
	INFUSE(			Shareability.PROXIMITY,	Reloadability.NEVER,	ChromaTiles.INFUSER),
	CHROMA(			Shareability.SELFONLY,	Reloadability.NEVER,	ChromaBlocks.CHROMA.getBlockInstance()), //step in liquid chroma
	//STONES(		Shareability.SELFONLY,	ChromaStacks.elementUnit), //craft all elemental stones together
	SHOCK(			Shareability.SELFONLY,	Reloadability.NEVER,	ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(5)), //get hit by a pylon
	HIVE(			Shareability.ALWAYS,	Reloadability.ALWAYS,	new ItemStack(ChromaBlocks.HIVE.getBlockInstance()), ModList.FORESTRY.isLoaded()),
	NETHER(			Shareability.SELFONLY,	Reloadability.NEVER,	Blocks.portal), //go to the nether
	END(			Shareability.SELFONLY,	Reloadability.NEVER,	Blocks.end_portal_frame), //go to the end
	TWILIGHT(		Shareability.SELFONLY,	Reloadability.NEVER,	ModList.TWILIGHT.isLoaded() ? ModWoodList.CANOPY.getItem().asItemStack() : null, ModList.TWILIGHT.isLoaded()), //Go to the twilight forest
	BEDROCK(		Shareability.PROXIMITY,	Reloadability.ALWAYS,	Blocks.bedrock), //Find bedrock
	CAVERN(			Shareability.ALWAYS,	Reloadability.ALWAYS,	ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.CLOAK.metadata)), //Cavern structure
	BURROW(			Shareability.ALWAYS,	Reloadability.ALWAYS,	ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.MOSS.metadata)), //Burrow structure
	OCEAN(			Shareability.ALWAYS,	Reloadability.ALWAYS,	ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.metadata)), //Ocean floor structure
	DESERTSTRUCT(	Shareability.ALWAYS,	Reloadability.ALWAYS,	ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.COBBLE.metadata)),
	SNOWSTRUCT(		Shareability.ALWAYS,	Reloadability.ALWAYS,	ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.LIGHT.metadata)),
	BIOMESTRUCT(	Shareability.ALWAYS,	Reloadability.ALWAYS,	ChromaBlocks.COLORLOCK.getStackOf()),
	DIE(			Shareability.SELFONLY,	Reloadability.NEVER,	Items.skull), //die and lose energy
	ALLCOLORS(		Shareability.SELFONLY,	Reloadability.NEVER,	ChromaItems.ELEMENTAL.getStackOf(CrystalElement.CYAN)), //find all colors
	REPEATER(		Shareability.ALWAYS,	Reloadability.TRIGGER,	ChromaTiles.REPEATER), //craft any repeater type
	RAINBOWFOREST(	Shareability.PROXIMITY,	Reloadability.ALWAYS,	ChromaBlocks.RAINBOWSAPLING.getBlockInstance()),
	DIMENSION(		Shareability.SELFONLY,	Reloadability.NEVER,	ChromaBlocks.PORTAL.getBlockInstance()),
	CTM(			Shareability.SELFONLY,	Reloadability.NEVER,	ChromaTiles.AURAPOINT),
	STORAGE(		Shareability.ALWAYS,	Reloadability.TRIGGER,	ChromaItems.STORAGE.getStackOf()),
	CHARGECRYSTAL(	Shareability.ALWAYS,	Reloadability.ALWAYS,	ChromaTiles.CHARGER),
	BALLLIGHTNING(	Shareability.PROXIMITY,	Reloadability.ALWAYS,	ChromaStacks.auraDust),
	POWERCRYSTAL(	Shareability.PROXIMITY,	Reloadability.TRIGGER,	ChromaTiles.CRYSTAL),
	POWERTREE(		Shareability.PROXIMITY,	Reloadability.TRIGGER,	ChromaBlocks.POWERTREE.getStackOfMetadata(CrystalElement.YELLOW.ordinal())),
	TURBOCHARGE(	Shareability.PROXIMITY,	Reloadability.NEVER,	ChromaTiles.PYLONTURBO),
	FINDSPAWNER(	Shareability.PROXIMITY,	Reloadability.NEVER,	new ItemStack(Blocks.mob_spawner)),
	BREAKSPAWNER(	Shareability.ALWAYS,	Reloadability.ALWAYS,	new ItemStack(Items.spawn_egg, 1, (int)EntityList.classToIDMapping.get(EntitySpider.class))),
	KILLDRAGON(		Shareability.PROXIMITY,	Reloadability.ALWAYS,	new ItemStack(Blocks.dragon_egg)),
	KILLWITHER(		Shareability.PROXIMITY,	Reloadability.ALWAYS,	new ItemStack(Items.nether_star)),
	KILLMOB(		Shareability.SELFONLY,	Reloadability.NEVER,	new ItemStack(Items.skull, 1, 4)),
	ALLCORES(		Shareability.SELFONLY,	Reloadability.NEVER,	ChromaTiles.DIMENSIONCORE.getCraftedNBTProduct("color", CrystalElement.RED.ordinal())),
	USEENERGY(		Shareability.PROXIMITY,	Reloadability.NEVER,	ChromaTiles.WEAKREPEATER),
	BLOWREPEATER(	Shareability.PROXIMITY,	Reloadability.ALWAYS,	ChromaStacks.crystalPowder),
	STRUCTCOMPLETE(	Shareability.SELFONLY,	Reloadability.NEVER,	ChromaBlocks.DIMDATA.getStackOf()),
	NETHERROOF(		Shareability.SELFONLY,	Reloadability.NEVER,	Blocks.netherrack),
	NETHERSTRUCT(	Shareability.PROXIMITY,	Reloadability.ALWAYS,	new ItemStack(Blocks.nether_brick)),
	VILLAGECASTING(	Shareability.PROXIMITY,	Reloadability.ALWAYS,	new ItemStack(Blocks.cobblestone)),
	FOCUSCRYSTAL(	Shareability.ALWAYS,	Reloadability.TRIGGER,	new ItemStack(Items.emerald)),
	ANYSTRUCT(		Shareability.SELFONLY,	Reloadability.NEVER,	ChromaTiles.STRUCTCONTROL),
	ARTEFACT(		Shareability.SELFONLY,	Reloadability.NEVER,	ChromaItems.ARTEFACT.getStackOfMetadata(ArtefactTypes.FRAGMENT.ordinal())),
	TOWER(			Shareability.SELFONLY,	Reloadability.NEVER,	ChromaTiles.DATANODE),
	STRUCTCHEAT(	Shareability.SELFONLY,	Reloadability.NEVER,	Blocks.tnt), //optional, just to rub it in
	VOIDMONSTER(	Shareability.PROXIMITY,	Reloadability.NEVER,	(ItemStack)null, ModList.VOIDMONSTER.isLoaded()),
	VOIDMONSTERDIE(	Shareability.PROXIMITY,	Reloadability.ALWAYS,	(ItemStack)null, ModList.VOIDMONSTER.isLoaded()),
	LUMA(			Shareability.SELFONLY,	Reloadability.NEVER,	ChromaBlocks.LUMA.getBlockInstance()),
	WARPNODE(		Shareability.SELFONLY,	Reloadability.NEVER,	ChromaBlocks.WARPNODE.getBlockInstance()),
	BYPASSWEAK(		Shareability.ALWAYS,	Reloadability.ALWAYS,	(ItemStack)null),
	TUNECAST(		Shareability.SELFONLY,	Reloadability.TRIGGER,	(ItemStack)null),
	PYLONLINK(		Shareability.SELFONLY,	Reloadability.TRIGGER,	ChromaTiles.PYLONLINK.getCraftedProduct()),
	RELAYS(			Shareability.PROXIMITY, Reloadability.TRIGGER,	ChromaTiles.RELAYSOURCE.getCraftedProduct()),
	ENERGYIDEA(		Shareability.SELFONLY,	Reloadability.NEVER,	(ItemStack)null),
	NODE(			Shareability.PROXIMITY,	Reloadability.NEVER,	ModList.THAUMCRAFT.isLoaded() ? ThaumItemHelper.BlockEntry.NODE.getItem() : null, ModList.THAUMCRAFT.isLoaded()),
	POTION(			Shareability.SELFONLY,	Reloadability.NEVER,	ReikaPotionHelper.getPotionItem(Potion.regeneration, true, true, false)),
	NEVER(			Shareability.SELFONLY,	Reloadability.NEVER,	(ItemStack)null, false), //used as a no-trigger placeholder
	;

	private final ItemStack icon;
	public final boolean active;
	public final Shareability shareLevel;
	public final Reloadability reloadLevel;

	public static final ProgressStage[] list = values();

	private ProgressStage(Shareability s, Reloadability r, Block b, boolean... cond) {
		this(s, r, new ItemStack(b), cond);
	}

	private ProgressStage(Shareability s, Reloadability r, Item b, boolean... cond) {
		this(s, r, new ItemStack(b), cond);
	}

	private ProgressStage(Shareability s, Reloadability r, ChromaTiles t, boolean... cond) {
		this(s, r, t.getCraftedProduct(), cond);
	}

	private ProgressStage(Shareability s, Reloadability r, ItemStack is, boolean... cond) {
		icon = is;
		boolean flag = true;
		for (int i = 0; i < cond.length; i++)
			flag = flag && cond[i];
		active = flag;
		ChromaResearchManager.instance.register(this);
		shareLevel = s;
		reloadLevel = r;
	}

	public boolean stepPlayerTo(EntityPlayer ep) {
		return ProgressionManager.instance.stepPlayerTo(ep, this, true, true);
	}

	public boolean stepPlayerTo(EntityPlayer ep, boolean syncToCoop) {
		return ProgressionManager.instance.stepPlayerTo(ep, this, true, syncToCoop);
	}

	public boolean isPlayerAtStage(EntityPlayer ep) {
		return ProgressionManager.instance.isPlayerAtStage(ep, this);
	}

	public boolean isPlayerAtStage(World world, UUID id) {
		EntityPlayer ep = world.func_152378_a(id);
		if (ep == null && world instanceof WorldServer) {
			ep = ReikaPlayerAPI.getFakePlayerByNameAndUUID((WorldServer)world, "Progress Backup", id);
		}
		return ep != null && ProgressionManager.instance.isPlayerAtStage(ep, this);
	}

	public boolean playerHasPrerequisites(EntityPlayer ep) {
		return ProgressionManager.instance.playerHasPrerequisites(ep, this);
	}

	public boolean isOneStepAway(EntityPlayer ep) {
		return ProgressionManager.instance.isOneStepAway(ep, this);
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
		if (this == ANYSTRUCT) { //item render does not work
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y, ChromaIcons.SPINFLARE.getIcon(), 16, 16); //render directly
			GL11.glPopAttrib();
		}
		else if (this == VOIDMONSTER || this == VOIDMONSTERDIE) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaGuiAPI.instance.renderStatic(x-1, y-1, x+16, y+16);
			if (this == VOIDMONSTERDIE) {
				ReikaGuiAPI.instance.drawItemStack(ri, fr, new ItemStack(Items.skull), x, y);
			}
			GL11.glPopAttrib();
		}
		else if (this == WARPNODE) {
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/warpnode-small.png");
			int idx = (int)(System.currentTimeMillis()/20%64);
			double u = idx%8/8D;
			double v = idx/8/8D;
			double du = u+1/8D;
			double dv = v+1/8D;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			int d = 2;
			int w = 16;
			int h = 16;
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			v5.addVertexWithUV((x + 0 - d), (y + h + d), 0, u, dv);
			v5.addVertexWithUV((x + w + d), (y + h + d), 0, du, dv);
			v5.addVertexWithUV((x + w + d), (y + 0 - d), 0, du, v);
			v5.addVertexWithUV((x + 0 - d), (y + 0 - d), 0, u, v);
			v5.draw();
			GL11.glPopAttrib();
		}
		else if (this == BYPASSWEAK) {
			ReikaGuiAPI.instance.drawItemStack(ri, fr, ChromaTiles.WEAKREPEATER.getCraftedProduct(), x, y);
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			boolean has = this.isPlayerAtStage(Minecraft.getMinecraft().thePlayer);
			if (has)
				BlendMode.DEFAULT.apply();
			else
				BlendMode.ADDITIVEDARK.apply();
			ChromaIcons ico = has ? ChromaIcons.X : ChromaIcons.QUESTION;
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x+2, y+2, ico.getIcon(), 12, 12); //render directly
			GL11.glPopAttrib();
		}
		else if (this == TUNECAST) {
			ChromaResearch.CASTTUNING.renderIcon(ri, fr, x, y);
			double s = 0.6;
			GL11.glPushMatrix();
			GL11.glScaled(s, s, s);
			ReikaGuiAPI.instance.drawItemStack(ri, fr, ChromaTiles.TABLE.getCraftedProduct(), (int)(x/s+12), (int)(y/s+12));
			GL11.glPopMatrix();
		}
		else if (this == ENERGYIDEA) {
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/infoicons.png");
			int idx = 32;
			double u = idx%16/16D;
			double v = idx/16/16D;
			double du = u+1/16D;
			double dv = v+1/16D;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVE2.apply();
			int d = 2;
			int w = 16;
			int h = 16;
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			v5.addVertexWithUV((x + 0 - d), (y + h + d), 0, u, dv);
			v5.addVertexWithUV((x + w + d), (y + h + d), 0, du, dv);
			v5.addVertexWithUV((x + w + d), (y + 0 - d), 0, du, v);
			v5.addVertexWithUV((x + 0 - d), (y + 0 - d), 0, u, v);
			v5.draw();
			GL11.glPopAttrib();
		}
		else {
			ReikaGuiAPI.instance.drawItemStack(ri, fr, icon, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderIconInWorld(Tessellator v5, double s, int x, int y, float f) {
		if (this == ANYSTRUCT) { //item render does not work
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y, ChromaIcons.SPINFLARE.getIcon(), 16, 16); //render directly
			IIcon ico = ChromaIcons.SPINFLARE.getIcon();
			v5.startDrawingQuads();
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, f));
			v5.addVertexWithUV(-s, s, 0, ico.getMinU(), ico.getMaxV());
			v5.addVertexWithUV(s, s, 0, ico.getMaxU(), ico.getMaxV());
			v5.addVertexWithUV(s, -s, 0, ico.getMaxU(), ico.getMinV());
			v5.addVertexWithUV(-s, -s, 0, ico.getMinU(), ico.getMinV());
			v5.draw();
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		else if (this == VOIDMONSTER) {
			//not possible
		}
		else if (this == WARPNODE) {
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/warpnode-small.png");
			int idx = (int)(System.currentTimeMillis()/20%64);
			double u = idx%8/8D;
			double v = idx/8/8D;
			double du = u+1/8D;
			double dv = v+1/8D;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			double w = 1*s;
			double h = 1*s;
			v5.startDrawingQuads();
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, f));
			v5.addVertexWithUV((x - w), (y + h), 0, u, dv);
			v5.addVertexWithUV((x + w), (y + h), 0, du, dv);
			v5.addVertexWithUV((x + w), (y - h), 0, du, v);
			v5.addVertexWithUV((x - w), (y - h), 0, u, v);
			v5.draw();
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		else if (this == ENERGYIDEA) {
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/infoicons.png");
			int idx = 32;
			double u = idx%16/16D;
			double v = idx/16/16D;
			double du = u+1/16D;
			double dv = v+1/16D;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glAlphaFunc(GL11.GL_GREATER, 1/32F);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			double w = 1*s;
			double h = 1*s;
			v5.startDrawingQuads();
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, f));
			v5.addVertexWithUV((x - w), (y - h), 0, u, dv);
			v5.addVertexWithUV((x + w), (y - h), 0, du, dv);
			v5.addVertexWithUV((x + w), (y + h), 0, du, v);
			v5.addVertexWithUV((x - w), (y + h), 0, u, v);
			v5.draw();
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		else {
			GL11.glPopMatrix();
			ItemStack is = icon;
			if (this == BYPASSWEAK) {
				is = ChromaTiles.WEAKREPEATER.getCraftedProduct();
			}
			else if (this == TUNECAST) {

			}
			ChromaItems r = ChromaItems.getEntry(is);
			boolean block = (r != null && r.isPlacer()) || Block.getBlockFromItem(is.getItem()) != Blocks.air;
			GL11.glColor4f(1, 1, 1, f);
			GL11.glDepthMask(block);
			InertItem ei = new InertItem(Minecraft.getMinecraft().theWorld, is);
			ei.age = 0;
			ei.hoverStart = MathHelper.sin(System.currentTimeMillis()/100F);
			ei.rotationYaw = 0;
			ReikaRenderHelper.disableEntityLighting();
			RenderItem.renderInFrame = true;
			this.setItemRBTintField(false);
			RenderManager.instance.renderEntityWithPosYaw(ei, 0, 0, 0, 0, 0/*tick*/);
			this.setItemRBTintField(true);
			RenderItem.renderInFrame = false;
			ReikaRenderHelper.enableEntityLighting();
		}
	}

	private void setItemRBTintField(boolean flag) {
		RenderItem ri = (RenderItem)RenderManager.instance.entityRenderMap.get(EntityItem.class);
		ri.renderBlocksRi.useInventoryTint = flag;
	}

	public boolean alwaysRenderFullBright() {
		return this == BYPASSWEAK;
	}

	public boolean isGatedAfter(ProgressStage p) {
		return ProgressionManager.instance.getRecursiveParents(this).contains(p);
	}

	@Override
	public String getFormatting() {
		return EnumChatFormatting.UNDERLINE.toString();
	}

	@Override
	public boolean giveToPlayer(EntityPlayer ep, boolean notify) {
		return ProgressionManager.instance.stepPlayerTo(ep, this, notify, true);
	}

	public void forceOnPlayer(EntityPlayer ep, boolean notify) {
		//instance.setPlayerStage(ep, this, true, notify);
	}

	public Shareability getShareability() {
		return shareLevel;
	}

	public boolean isGating(ResearchLevel level) {
		return ProgressionManager.instance.isProgressionGating(this, level);
	}

	@Override
	public boolean playerHas(EntityPlayer ep) {
		return this.isPlayerAtStage(ep);
	}

	public static enum Shareability {
		SELFONLY(),
		PROXIMITY(),
		ALWAYS();

		public boolean canShareTo(EntityPlayer from, EntityPlayer to) {
			switch(this) {
				case ALWAYS:
					return true;
				case PROXIMITY:
					return to.getDistanceSqToEntity(from) <= 576;
				case SELFONLY:
					return false;
			}
			return false;
		}
	}

	public static enum Reloadability {
		NEVER(),
		TRIGGER(),
		ALWAYS();
	}
}
