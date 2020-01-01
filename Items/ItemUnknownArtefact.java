/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.ItemChromaMulti;
import Reika.ChromatiCraft.Magic.Artefact.UABombingEffects;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Interfaces.Item.AnimatedSpritesheet;
import Reika.DragonAPI.Interfaces.Item.MetadataSpecificTrade;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemUnknownArtefact extends ItemChromaMulti implements AnimatedSpritesheet, MetadataSpecificTrade {

	//only directly obtainable when silk touched
	//effects: hurt entities when in inv (slowly); too many in chest -> UA bombing
	// (nearby RF storages empty, chest may drop items (except other UAs), maybe low-power explosions
	//can be sold to villagers for a LOT

	private static final int RENDER_TEXT_MIN_WORD_LENGTH = 2;
	private static final int RENDER_TEXT_MAX_WORD_LENGTH = 8;
	private static final int RENDER_TEXT_LENGTH = 24;

	private static final int STEP_SPEED = 100*2;

	private static final Random rand = new Random();

	private static String renderText;
	private static String renderTextNext;
	private static int panLocation;
	private static long stepTime;

	private static final Interpolation chanceTable = new Interpolation(false);

	static {
		chanceTable.addPoint(2, 0);
		chanceTable.addPoint(3, 0.01);
		chanceTable.addPoint(4, 0.25);
		chanceTable.addPoint(12, 0.5);
		chanceTable.addPoint(16, 0.75);
		chanceTable.addPoint(24, 0.975);
		chanceTable.addPoint(32, 0.995);
		chanceTable.addPoint(64, 1.0);
	}

	public ItemUnknownArtefact(int tex) {
		super(tex);
		this.setMaxStackSize(1);
	}

	public static enum ArtefactTypes {
		ARTIFACT(),
		FRAGMENT(),
		//PROBE(),
		;

		public static final ArtefactTypes[] list = values();

		public boolean emitsLight() {
			return this == ARTIFACT;
		}

		public boolean damagesEntities() {
			return this == ARTIFACT;
		}

		public boolean triggersUABombing() {
			return this == ARTIFACT;
		}

		public boolean doesFX() {
			return this == ARTIFACT;
		}
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		//ei.age = 0;
		ei.lifespan = Integer.MAX_VALUE;
		ItemStack is = ei.getEntityItem();
		if (ei.worldObj.isRemote) {
			if (ArtefactTypes.list[is.getItemDamage()].doesFX())
				this.doItemFX(ei.worldObj, ei, is);
		}
		else {
			int x = MathHelper.floor_double(ei.posX);
			int y = MathHelper.floor_double(ei.posY);
			int z = MathHelper.floor_double(ei.posZ);
			/*
			if (ArtefactTypes.list[is.getItemDamage()].emitsLight()) {
				if (ei.worldObj.getBlock(x, y, z).isAir(ei.worldObj, x, y, z)) {
					ei.worldObj.setBlock(x, y, z, ChromaBlocks.LIGHT.getBlockInstance(), Flags.DECAY.getFlag(), 3);
				}
			}
			 */
			if (ArtefactTypes.list[is.getItemDamage()].triggersUABombing()) {
				//this.UABombing(ei);
			}
		}
		//orient to 0,0? or maybe make orient to something special
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (is.getItemDamage() != ArtefactTypes.ARTIFACT.ordinal())
			return false;
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava) {
			if (side == 0)
				--y;
			if (side == 1)
				++y;
			if (side == 2)
				--z;
			if (side == 3)
				++z;
			if (side == 4)
				--x;
			if (side == 5)
				++x;
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava)
				return false;
		}
		if (y <= 0 || y >= 255)
			return false;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
		List inblock = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		if (inblock.size() > 0)
			return false;
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else {
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, ChromaBlocks.ARTEFACT.getBlockInstance(), 1, 3);
		}

		ReikaSoundHelper.playPlaceSound(world, x, y, z, ChromaBlocks.ARTEFACT.getBlockInstance());
		return true;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean verbose) {
		if (System.currentTimeMillis()-stepTime >= STEP_SPEED) {
			this.cycleRenderText();
			stepTime = System.currentTimeMillis();
		}
		li.add(this.getRenderText());
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (ArtefactTypes.list[is.getItemDamage()].damagesEntities() && rand.nextInt(1000) == 0) {
			if (rand.nextBoolean()) {
				e.attackEntityFrom(DamageSource.outOfWorld, 1);
			}
			else {
				if (e instanceof EntityLivingBase) {
					ReikaEntityHelper.damageArmor((EntityLivingBase)e, 5);
					e.attackEntityFrom(DamageSource.outOfWorld, 0.01F);
				}
			}
		}
		if (ArtefactTypes.list[is.getItemDamage()].triggersUABombing() && rand.nextInt(200) == 0) {
			this.UABombing(e);
		}
		if (world.isRemote && ArtefactTypes.list[is.getItemDamage()].doesFX()) {
			this.doHeldFX(world, e, is);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doHeldFX(World world, Entity e, ItemStack is) {

	}

	@SideOnly(Side.CLIENT)
	private void doItemFX(World world, Entity e, ItemStack is) {
		doUA_FX(world, e.posX, e.posY, e.posZ, false);
	}

	@SideOnly(Side.CLIENT)
	public static void doUA_FX(World world, double posX, double posY, double posZ, boolean renderThroughGround) {
		if (rand.nextBoolean()) {
			double px = ReikaRandomHelper.getRandomPlusMinus(posX, 1);
			double py = ReikaRandomHelper.getRandomPlusMinus(posY+0.25, 1);
			double pz = ReikaRandomHelper.getRandomPlusMinus(posZ, 1);
			int l = ReikaRandomHelper.getRandomBetween(10, 40);
			double s = 6+rand.nextDouble()*9;
			int c = ReikaColorAPI.mixColors(0x003010, 0x000030, rand.nextFloat());
			EntityBlurFX fx = new EntityBlurFX(world, px, py, pz);
			fx.setColor(c).setLife(l).setScale((float)s).setAlphaFading().setIcon(ChromaIcons.FADE_CLOUD);
			if (renderThroughGround)
				fx.setNoDepthTest();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		int n = 1+rand.nextInt(2);
		for (int i = 0; i < n; i++) {
			double px = ReikaRandomHelper.getRandomPlusMinus(posX, 2D);
			double py = ReikaRandomHelper.getRandomPlusMinus(posY, 2D);
			double pz = ReikaRandomHelper.getRandomPlusMinus(posZ, 2D);
			int l = ReikaRandomHelper.getRandomBetween(3, 8);
			double maxv = 0.125/l;
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, maxv);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, maxv);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, maxv);
			EntityBlurFX fx = new EntityBlurFX(world, px, py, pz, vx, vy, vz);
			fx.setColor(0xffffff).setLife(l).setRapidExpand().setIcon(ChromaIcons.FADE_STAR);
			if (renderThroughGround)
				fx.setNoDepthTest();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world) {
		return Integer.MAX_VALUE;
	}

	public static void onAddedToInventory(IInventory inv, ItemStack is) {
		if (ArtefactTypes.list[is.getItemDamage()].triggersUABombing()) {
			int amt = ReikaInventoryHelper.countItem(ChromaItems.ARTEFACT.getItemInstance(), ArtefactTypes.ARTIFACT.ordinal(), inv);
			double ch = chanceTable.getValue(amt);
			if (ch > 0 && ReikaRandomHelper.doWithChance(ch)) {
				UABombing(inv);
			}
		}
	}

	private static void UABombing(IInventory inv) {
		TileEntity te = (TileEntity)inv;
		//UABombingEffects.instance.trigger(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
		UABombingEffects.instance.trigger(te, inv);
	}

	private static void UABombing(Entity e) {
		//UABombingEffects.instance.trigger(e.worldObj, MathHelper.floor_double(e.posX), MathHelper.floor_double(e.posY), MathHelper.floor_double(e.posZ));
		UABombingEffects.instance.trigger(e);
	}

	private static String getRenderText() {
		return ChromaFontRenderer.FontType.OBFUSCATED.id+renderTextNext.substring(0, panLocation)+renderText.substring(panLocation);
	}

	private static void cycleRenderText() {
		if (renderText == null) {
			renderText = generateNewRenderText();
		}
		if (renderTextNext == null) {
			renderTextNext = generateNewRenderText();
		}

		panLocation++;
		if (panLocation >= RENDER_TEXT_LENGTH) {
			panLocation = 0;
			renderText = renderTextNext;
			renderTextNext = generateNewRenderText();
		}
	}

	private static String generateNewRenderText() {
		StringBuilder sb = new StringBuilder();
		while (sb.length() < RENDER_TEXT_LENGTH) {
			int len = Math.min(RENDER_TEXT_LENGTH-sb.length(), ReikaRandomHelper.getRandomBetween(RENDER_TEXT_MIN_WORD_LENGTH, RENDER_TEXT_MAX_WORD_LENGTH));
			while (len > 0 && sb.length() < RENDER_TEXT_LENGTH) {
				char c = (char)ReikaRandomHelper.getRandomBetween('/', '~');
				sb.append(String.valueOf(c));
			}
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	@Override
	public boolean useAnimatedRender(ItemStack is) {
		return true;
	}

	@Override
	public int getFrameCount(ItemStack is) {
		return 16;
	}

	@Override
	public int getBaseRow(ItemStack is) {
		return 6+is.getItemDamage();
	}

	@Override
	public int getColumn(ItemStack is) {
		return 0;
	}

	@Override
	public int getFrameOffset(ItemStack is) {
		return ((System.identityHashCode(is)))%this.getFrameCount(is);
	}

	@Override
	public int getFrameSpeed() {
		return 4;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ChromatiCraft/Textures/Items/miscanim.png";
	}

	@Override
	public boolean verticalFrames() {
		return false;
	}

}
