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
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.ModRegistry.ModWoodList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum ProgressStage implements ProgressElement, ProgressAccess {

	CASTING(		Shareability.SELFONLY,	ChromaTiles.TABLE), //Do a recipe
	CRYSTALS(		Shareability.SELFONLY,	ChromaBlocks.CRYSTAL.getStackOfMetadata(CrystalElement.RED.ordinal())), //Found a crystal
	DYETREE(		Shareability.SELFONLY,	ChromaBlocks.DYELEAF.getStackOfMetadata(CrystalElement.YELLOW.ordinal())), //Harvest a dye tree
	MULTIBLOCK(		Shareability.PROXIMITY,	ChromaTiles.STAND), //Assembled a multiblock
	RUNEUSE(		Shareability.PROXIMITY,	ChromaBlocks.RUNE.getStackOfMetadata(CrystalElement.ORANGE.ordinal())), //Placed runes
	PYLON(			Shareability.SELFONLY,	ChromaTiles.PYLON), //Found pylon
	LINK(			Shareability.PROXIMITY,	ChromaTiles.COMPOUND), //Made a network connection/high-tier crafting
	CHARGE(			Shareability.SELFONLY,	ChromaItems.TOOL.getStackOf()), //charge from a pylon
	ABILITY(		Shareability.SELFONLY,	ChromaTiles.RITUAL), //use an ability
	RAINBOWLEAF(	Shareability.PROXIMITY,	ChromaBlocks.RAINBOWLEAF.getStackOfMetadata(3)), //harvest a rainbow leaf
	MAKECHROMA(		Shareability.PROXIMITY,	ChromaTiles.COLLECTOR),
	SHARDCHARGE(	Shareability.PROXIMITY,	ChromaStacks.chargedRedShard),
	ALLOY(			Shareability.PROXIMITY,	ChromaStacks.chromaIngot),
	INFUSE(			Shareability.PROXIMITY,	ChromaTiles.INFUSER),
	CHROMA(			Shareability.SELFONLY,	ChromaBlocks.CHROMA.getBlockInstance()), //step in liquid chroma
	//STONES(		Shareability.SELFONLY,	ChromaStacks.elementUnit), //craft all elemental stones together
	SHOCK(			Shareability.SELFONLY,	ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(5)), //get hit by a pylon
	HIVE(			Shareability.ALWAYS,	new ItemStack(ChromaBlocks.HIVE.getBlockInstance()), ModList.FORESTRY.isLoaded()),
	NETHER(			Shareability.SELFONLY,	Blocks.portal), //go to the nether
	END(			Shareability.SELFONLY,	Blocks.end_portal_frame), //go to the end
	TWILIGHT(		Shareability.SELFONLY,	ModList.TWILIGHT.isLoaded() ? ModWoodList.CANOPY.getItem() : null, ModList.TWILIGHT.isLoaded()), //Go to the twilight forest
	BEDROCK(		Shareability.SELFONLY,		Blocks.bedrock), //Find bedrock
	CAVERN(			Shareability.ALWAYS,	ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.CLOAK.metadata)), //Cavern structure
	BURROW(			Shareability.ALWAYS,	ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.MOSS.metadata)), //Burrow structure
	OCEAN(			Shareability.ALWAYS,	ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.metadata)), //Ocean floor structure
	DESERTSTRUCT(	Shareability.ALWAYS,	ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.COBBLE.metadata)),
	SNOWSTRUCT(		Shareability.ALWAYS,	ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.LIGHT.metadata)),
	DIE(			Shareability.SELFONLY,	Items.skull), //die and lose energy
	ALLCOLORS(		Shareability.SELFONLY,	ChromaItems.ELEMENTAL.getStackOf(CrystalElement.CYAN)), //find all colors
	REPEATER(		Shareability.ALWAYS,	ChromaTiles.REPEATER), //craft any repeater type
	RAINBOWFOREST(	Shareability.SELFONLY,	ChromaBlocks.RAINBOWSAPLING.getBlockInstance()),
	DIMENSION(		Shareability.SELFONLY,	ChromaBlocks.PORTAL.getBlockInstance()),
	CTM(			Shareability.SELFONLY,	ChromaTiles.AURAPOINT),
	STORAGE(		Shareability.ALWAYS,	ChromaItems.STORAGE.getStackOf()),
	CHARGECRYSTAL(	Shareability.ALWAYS,	ChromaTiles.CHARGER),
	BALLLIGHTNING(	Shareability.SELFONLY,	ChromaStacks.auraDust),
	POWERCRYSTAL(	Shareability.PROXIMITY,	ChromaTiles.CRYSTAL),
	POWERTREE(		Shareability.PROXIMITY,	ChromaBlocks.POWERTREE.getStackOfMetadata(CrystalElement.YELLOW.ordinal())),
	TURBOCHARGE(	Shareability.PROXIMITY,	ChromaTiles.PYLONTURBO),
	FINDSPAWNER(	Shareability.PROXIMITY,	new ItemStack(Blocks.mob_spawner)),
	BREAKSPAWNER(	Shareability.ALWAYS,	new ItemStack(Items.spawn_egg, 1, (int)EntityList.classToIDMapping.get(EntitySpider.class))),
	KILLDRAGON(		Shareability.PROXIMITY,	new ItemStack(Blocks.dragon_egg)),
	KILLWITHER(		Shareability.PROXIMITY,	new ItemStack(Items.nether_star)),
	KILLMOB(		Shareability.SELFONLY,	new ItemStack(Items.skull, 1, 4)),
	ALLCORES(		Shareability.SELFONLY,	ChromaTiles.DIMENSIONCORE.getCraftedNBTProduct("color", CrystalElement.RED.ordinal())),
	USEENERGY(		Shareability.PROXIMITY,	ChromaTiles.WEAKREPEATER),
	BLOWREPEATER(	Shareability.PROXIMITY,	ChromaStacks.crystalPowder),
	STRUCTCOMPLETE(	Shareability.SELFONLY,	ChromaBlocks.DIMDATA.getStackOf()),
	NETHERROOF(		Shareability.SELFONLY,	Blocks.netherrack),
	NETHERSTRUCT(	Shareability.PROXIMITY,	new ItemStack(Blocks.nether_brick)),
	VILLAGECASTING(	Shareability.PROXIMITY,	new ItemStack(Blocks.cobblestone)),
	FOCUSCRYSTAL(	Shareability.ALWAYS,	new ItemStack(Items.emerald)),
	ANYSTRUCT(		Shareability.SELFONLY,	ChromaTiles.STRUCTCONTROL),
	ARTEFACT(		Shareability.SELFONLY,	ChromaItems.ARTEFACT.getStackOfMetadata(ArtefactTypes.FRAGMENT.ordinal())),
	TOWER(			Shareability.SELFONLY,	ChromaTiles.DATANODE),
	STRUCTCHEAT(	Shareability.SELFONLY,	Blocks.tnt), //optional, just to rub it in
	VOIDMONSTER(	Shareability.PROXIMITY,	(ItemStack)null, ModList.VOIDMONSTER.isLoaded()),
	VOIDMONSTERDIE(	Shareability.SELFONLY,	(ItemStack)null, ModList.VOIDMONSTER.isLoaded()),
	LUMA(			Shareability.SELFONLY,	ChromaBlocks.LUMA.getBlockInstance()),
	WARPNODE(		Shareability.SELFONLY,	ChromaBlocks.WARPNODE.getBlockInstance()),
	BYPASSWEAK(		Shareability.SELFONLY,	(ItemStack)null),
	TUNECAST(		Shareability.SELFONLY,	(ItemStack)null),
	NEVER(			Shareability.SELFONLY,	(ItemStack)null, false), //used as a no-trigger placeholder
	;

	private final ItemStack icon;
	public final boolean active;
	public final Shareability shareLevel;

	public static final ProgressStage[] list = values();

	private ProgressStage(Shareability s, Block b, boolean... cond) {
		this(s, new ItemStack(b), cond);
	}

	private ProgressStage(Shareability s, Item b, boolean... cond) {
		this(s, new ItemStack(b), cond);
	}

	private ProgressStage(Shareability s, ChromaTiles t, boolean... cond) {
		this(s, t.getCraftedProduct(), cond);
	}

	private ProgressStage(Shareability s, ItemStack is, boolean... cond) {
		icon = is;
		boolean flag = true;
		for (int i = 0; i < cond.length; i++)
			flag = flag && cond[i];
		active = flag;
		ChromaResearchManager.instance.register(this);
		shareLevel = s;
	}

	public boolean stepPlayerTo(EntityPlayer ep) {
		return ProgressionManager.instance.stepPlayerTo(ep, this, true);
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
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV((x + 0 - d), (y + h + d), 0, u, dv);
			tessellator.addVertexWithUV((x + w + d), (y + h + d), 0, du, dv);
			tessellator.addVertexWithUV((x + w + d), (y + 0 - d), 0, du, v);
			tessellator.addVertexWithUV((x + 0 - d), (y + 0 - d), 0, u, v);
			tessellator.draw();
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
		else {
			ReikaGuiAPI.instance.drawItemStack(ri, fr, icon, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderIconInWorld(Tessellator v5, double s, int x, int y) {
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
			v5.setColorOpaque_I(0xffffff);
			v5.addVertexWithUV(-s, s, 0, ico.getMinU(), ico.getMaxV());
			v5.addVertexWithUV(s, s, 0, ico.getMaxU(), ico.getMaxV());
			v5.addVertexWithUV(s, -s, 0, ico.getMaxU(), ico.getMinV());
			v5.addVertexWithUV(-s, -s, 0, ico.getMinU(), ico.getMinV());
			v5.draw();
			GL11.glPopAttrib();
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
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV((x - w), (y + h), 0, u, dv);
			tessellator.addVertexWithUV((x + w), (y + h), 0, du, dv);
			tessellator.addVertexWithUV((x + w), (y - h), 0, du, v);
			tessellator.addVertexWithUV((x - w), (y - h), 0, u, v);
			tessellator.draw();
			GL11.glPopAttrib();
		}
		else {
			ItemStack is = icon;
			if (this == BYPASSWEAK) {
				is = ChromaTiles.WEAKREPEATER.getCraftedProduct();
			}
			else if (this == TUNECAST) {

			}
			InertItem ei = new InertItem(Minecraft.getMinecraft().theWorld, is);
			ei.age = 0;
			ei.hoverStart = MathHelper.sin(System.currentTimeMillis()/100F);
			ei.rotationYaw = 0;
			ReikaRenderHelper.disableEntityLighting();
			RenderItem.renderInFrame = true;
			RenderManager.instance.renderEntityWithPosYaw(ei, 0, 0, 0, 0, 0/*tick*/);
			RenderItem.renderInFrame = false;
			ReikaRenderHelper.enableEntityLighting();
		}
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
		return ProgressionManager.instance.stepPlayerTo(ep, this, notify);
	}

	public void forceOnPlayer(EntityPlayer ep, boolean notify) {
		//instance.setPlayerStage(ep, this, true, notify);
	}

	public Shareability getShareability() {
		return shareLevel;
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

	@Override
	public boolean playerHas(EntityPlayer ep) {
		return this.isPlayerAtStage(ep);
	}
}
