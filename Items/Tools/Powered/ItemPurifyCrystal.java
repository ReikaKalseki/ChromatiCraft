/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Powered;

import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Orbit;
import Reika.DragonAPI.Instantiable.ParticleController.OrbitMotionController;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


public class ItemPurifyCrystal extends ItemPoweredChromaTool {

	private static final String TAG = "last_purify";
	public static final int CHARGE_STATES = 4;
	public static final int MAX_CHARGE = 216000*12; //3h in the Nether, 36h in the overworld

	public ItemPurifyCrystal(int index) {
		super(index);
	}

	@Override
	public boolean doTick(ItemStack is, World world, EntityPlayer e, boolean held) {
		e.getEntityData().setLong(TAG, world.getTotalWorldTime());
		if (world.isRemote) {
			this.doHeldFX(is, e);
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	private void doHeldFX(ItemStack is, Entity e) {
		if (itemRand.nextInt(2*4) == 0) {
			double r = 2.5;
			double ec = 0;//0.4+0.4*Math.sin(Math.toRadians(e.ticksExisted));
			double i = e.ticksExisted*4;//.ticksExisted%2 == 0 ? 60 : -60;
			double raan = 0;//(e.ticksExisted%2 == 0 ? 180 : 0)+RenderManager.instance.playerViewY;
			Orbit o = new Orbit(r, ec, i, raan, 0, 0);
			OrbitMotionController p = new OrbitMotionController(o, e.posX, e.posY, e.posZ).trackEntity(e);
			p.thetaSpeed = ReikaRandomHelper.getRandomBetween(1.5, 5);
			int l = 60+itemRand.nextInt(120);
			float s = (float)ReikaRandomHelper.getRandomPlusMinus(1, 0.25);
			EntityFX fx = new EntityBlurFX(e.worldObj, e.posX, e.posY, e.posZ).setPositionController(p).setLife(l).setIcon(ChromaIcons.CENTER).setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public static boolean isActive(EntityPlayer ep) {
		return ep.worldObj.getTotalWorldTime()-ep.getEntityData().getLong(TAG) < 20;
	}

	@Override
	protected boolean isActivated(EntityPlayer e, boolean held) {
		return true;
	}

	@Override
	protected CrystalElement getColor() {
		return CrystalElement.WHITE;
	}

	@Override
	public int getMaxCharge() {
		return MAX_CHARGE;
	}

	@Override
	public int getChargeStates() {
		return CHARGE_STATES;
	}

	@Override
	protected int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is) {
		return CrystalPotionController.isWorldHostile(world) ? 12 : 1;
	}

}
