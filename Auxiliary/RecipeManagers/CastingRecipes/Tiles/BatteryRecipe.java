/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BatteryRecipe extends PylonCastingRecipe {

	public static final int NOTE_SPEED = 8;

	private final MusicKey[] melody = {
			MusicKey.C4, MusicKey.E4, MusicKey.G4, MusicKey.C5, MusicKey.G4, MusicKey.E4, MusicKey.C4, MusicKey.E4,
			MusicKey.G4, MusicKey.B4, MusicKey.D5, MusicKey.B4, MusicKey.G4, MusicKey.D4, MusicKey.G4, MusicKey.B4,
			MusicKey.D5, MusicKey.A4, MusicKey.F4, MusicKey.A4, MusicKey.D5, MusicKey.F5, MusicKey.D5, MusicKey.A4,
			MusicKey.F4, MusicKey.C4, MusicKey.F5, MusicKey.A4, MusicKey.C5, MusicKey.A4, MusicKey.F4, MusicKey.A4,
			MusicKey.C5, MusicKey.E5, MusicKey.C5, MusicKey.A4, MusicKey.E4, MusicKey.A4, MusicKey.C5, MusicKey.A4,
			MusicKey.E4, MusicKey.G4, MusicKey.B4, MusicKey.G4, MusicKey.E4, MusicKey.G4, MusicKey.B4, MusicKey.G4,
	};

	private final MusicKey[] chords = {
			MusicKey.C4, MusicKey.G4, MusicKey.D4, MusicKey.F4, MusicKey.A4, MusicKey.E4,
	};

	public BatteryRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 0, 2);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 0, -2);

		this.addAuxItem(ChromaStacks.focusDust, 2, 0);
		this.addAuxItem(ChromaStacks.focusDust, -2, 0);

		this.addAuxItem(ChromaStacks.avolite, -4, 0);
		this.addAuxItem(ChromaStacks.avolite, 4, 0);
		this.addAuxItem(ChromaStacks.avolite, 0, 4);
		this.addAuxItem(ChromaStacks.avolite, 0, -4);

		this.addAuxItem(ChromaStacks.avolite, -2, 2);
		this.addAuxItem(ChromaStacks.avolite, -2, -2);
		this.addAuxItem(ChromaStacks.avolite, 2, -2);
		this.addAuxItem(ChromaStacks.avolite, 2, 2);

		this.addAuxItem(ChromaStacks.chargedWhiteShard, -4, 4);
		this.addAuxItem(ChromaStacks.chargedYellowShard, -4, -4);
		this.addAuxItem(ChromaStacks.chargedBlueShard, 4, -4);
		this.addAuxItem(ChromaStacks.chargedBlackShard, 4, 4);

		this.addAuxItem(ChromaStacks.beaconDust, -4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, -4, -2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -2);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, -4);

		this.addAuraRequirement(CrystalElement.BLACK, 80000);
		this.addAuraRequirement(CrystalElement.YELLOW, 80000);
		this.addAuraRequirement(CrystalElement.BLUE, 30000);
		this.addAuraRequirement(CrystalElement.PURPLE, 8000);
	}

	@Override
	public int getDuration() {
		return 16*super.getDuration();
	}

	@Override
	public void onRecipeTick(TileEntityCastingTable te) {
		int tick = te.getCraftingTick();
		if (te.worldObj.isRemote) {
			this.doFX(te, tick);
		}
		else {

		}
	}

	@SideOnly(Side.CLIENT)
	private void doFX(TileEntityCastingTable te, int tick) {
		int t = this.getDuration()-tick; //goes up
		if (t%NOTE_SPEED == 0) {
			MusicKey m = melody[t/NOTE_SPEED%melody.length];
			this.playKey(te, m, ChromaSounds.DING);
		}

		if (t%(NOTE_SPEED*8) == 0) {
			MusicKey m = chords[t/(NOTE_SPEED*8)%chords.length];
			this.playKey(te, m, ChromaSounds.ORB);
		}
	}

	@SideOnly(Side.CLIENT)
	private void playKey(TileEntityCastingTable te, MusicKey m, ChromaSounds s) {
		float f = 0;
		for (CrystalElement e : CrystalMusicManager.instance.getColorsWithKey(m)) {
			int idx = CrystalMusicManager.instance.getIntervalFor(e, m);
			f = CrystalMusicManager.instance.getScaledDing(e, idx);
			this.spawnParticles(te, e, idx);
		}

		ReikaSoundHelper.playClientSound(s, te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, 1, f);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(TileEntityCastingTable te, CrystalElement e, int idx) {
		double ang = Math.toRadians(22.5*e.ordinal());
		double c = Math.cos(ang);
		double s = Math.sin(ang);
		double r = 6;
		for (double d = 0; d < r; d += 0.125) {
			double dl = r/2-Math.abs(d-r/2);
			double dc = ReikaMathLibrary.cosInterpolation(0, r, d);
			double dx = te.xCoord+0.5+c*d;
			double dz = te.zCoord+0.5+s*d;
			double vy = dc*0.0625*1.5*(0.75+idx/2D);
			double dy = te.yCoord+0.5;
			float g = 0.0625F*(float)dc;
			EntityFX fx = new EntityBlurFX(te.worldObj, dx, dy, dz, 0, vy, 0).setLife(60).setGravity(g).setColor(e.getColor()).setScale(1.5F);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

}
