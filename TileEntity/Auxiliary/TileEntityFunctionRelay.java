/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.ItemSpecificEffectDescription.ItemListEffectDescription;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiStackDisplay;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityFunctionRelay extends TileEntityChromaticBase {

	private static final TreeMap<String, RelayedEffectDescription> specialEffects = new TreeMap();

	private final StepTimer scanTimer = new StepTimer(50);

	private final ArrayList<Coordinate> activeCoords = new ArrayList();

	public static Collection<RelayedEffectDescription> getEffects() {
		return Collections.unmodifiableCollection(specialEffects.values());
	}

	public static void setSpecialEffect(String desc, ChromaTiles t) {
		setSpecialEffect(desc, t.getCraftedProduct());
	}

	public static void setSpecialEffect(String desc, ItemStack is) {
		setSpecialEffect(desc, new GuiStackDisplay(is));
	}

	public static void setSpecialEffect(String desc, GuiItemDisplay d) {
		RelayedEffectDescription eff = specialEffects.get(desc);
		if (eff == null) {
			eff = new RelayedEffectDescription(desc);
			specialEffects.put(desc, eff);
		}
		eff.addDisplays(d);
	}

	public static void setDefaultEffects() {
		setSpecialEffect("Expands bookshelf search", new ItemStack(Blocks.enchanting_table));
		String s = "Relays harvest AoE";
		setSpecialEffect(s, ChromaTiles.FARMER);
		setSpecialEffect(s, ChromaTiles.HARVESTPLANT);
		s = "Relays area of effect";
		setSpecialEffect(s, ChromaTiles.CROPSPEED);
		setSpecialEffect(s, ChromaTiles.REVERTER);
		setSpecialEffect("Expands fluid search", ChromaTiles.COBBLEGEN);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FUNCTIONRELAY;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		scanTimer.update();
		if (scanTimer.checkCap()) {
			this.doScan(world, x, y, z);
		}
		if (world.isRemote) {
			//this.doParticles(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		int l = 3;//360;
		double theta = 0;//this.getTicksExisted()%360;
		double phi = 0;//rand.nextDouble()*360;
		double a = (this.getTicksExisted()*0.73)%360D;
		double a2 = (0.135*this.getTicksExisted())%360D;
		for (double d = a; d < 360+a; d += 15) {
			for (double d2 = -90+a2; d2 <= 90+a2; d2 += 15) {
				double r = 1.125+0.125*Math.sin(this.getTicksExisted()/8D+d/(3*Math.PI-0.01)+d2/(4*Math.PI-0.01));
				double[] xyz = ReikaPhysicsHelper.polarToCartesian(r, theta+d2, phi+d);
				double px = x+0.5+xyz[0];
				double py = y+0.5+xyz[1];
				double pz = z+0.5+xyz[2];
				EntityBlurFX fx = new EntityCCBlurFX(world, px, py, pz).setLife(l).setAlphaFading();
				ColorBlendList cbl = new ColorBlendList(l*4/*/4F*/, 0xff00ff, 0xffff00, 0xff00ff, 0x00ffff);
				fx.setColor(cbl.getColor(this.getTicksExisted()+Math.abs((int)(d*d2/512D))));
				//fx.setPositionController(new PulsingSpherePositionController(l*4, x+0.5, y+0.5, z+0.5, 1, 1.25, theta, phi)).setColorController(new BlendListColorController(cbl));
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.doScan(world, x, y, z);
	}

	private void doScan(World world, int x, int y, int z) {
		activeCoords.clear();
		for (int i = -6; i <= 6; i++) {
			for (int k = -6; k <= 6; k++) {
				if (Math.abs(i)+Math.abs(k) <= 9) {
					int dx = x+i;
					int dz = z+k;
					for (int j = -6; j <= 2; j++) {
						int dy = y+j;
						Block b = world.getBlock(dx, dy, dz);
						if (!b.isAir(world, dx, dy, dz)) {
							int meta = world.getBlockMetadata(dx, dy, dz);
							activeCoords.add(new Coordinate(dx, dy, dz));
						}
					}
				}
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);
	}

	public Coordinate getRandomCoordinate() {
		return activeCoords.isEmpty() ? null : activeCoords.get(rand.nextInt(activeCoords.size()));
	}

	public Collection<Coordinate> getCoordinates() {
		return Collections.unmodifiableCollection(activeCoords);
	}

	public float getEnchantPowerInRange(World world, int x, int y, int z) {
		/*
		int i = 0;
		int j;
		float power = 0;

		for (j = -1; j <= 1; ++j) {
			for (int k = -1; k <= 1; ++k) {
				if ((j != 0 || k != 0) && world.isAirBlock(x+k, y, z+j) && world.isAirBlock(x+k, y+1, z+j)) {
					power += ForgeHooks.getEnchantPower(world, x+k*2, y,    z+j*2);
					power += ForgeHooks.getEnchantPower(world, x+k*2, y+1, 	z+j*2);

					if (k != 0 && j != 0) {
						power += ForgeHooks.getEnchantPower(world, x+k*2, y,    z+j    	);
						power += ForgeHooks.getEnchantPower(world, x+k*2, y+1, 	z+j    	);
						power += ForgeHooks.getEnchantPower(world, x+k,   y,  	z+j*2	);
						power += ForgeHooks.getEnchantPower(world, x+k,   y+1,	z+j*2	);
					}
				}
			}
		}
		return power;
		 */
		float power = 0;
		for (int j = -1; j <= 1; j++) {
			for (int i = -5; i <= 5; i++) {
				for (int k = -5; k <= 5; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (ChromaTiles.getTile(world, dx, dy, dz) != ChromaTiles.FUNCTIONRELAY)
						power += ForgeHooks.getEnchantPower(world, dx, dy, dz);
				}
			}
		}
		return power;
	}

	public static class RelayedEffectDescription extends ItemListEffectDescription {

		public RelayedEffectDescription(String s) {
			super(s);
		}

	}

}
