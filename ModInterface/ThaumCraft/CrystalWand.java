/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.IWandRodOnUpdate;
import thaumcraft.api.wands.WandRod;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CrystalWand extends WandRod {

	private static final Random rand = new Random();

	private final IWandRodOnUpdate updater = new WandUpdater();
	private ColorBlendList colors;

	public CrystalWand() {
		super("CRYSTALWAND", 6000, ChromaStacks.crystalWand, 18, null, null);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			this.registerTexture();
		this.setGlowing(true);
	}

	@SideOnly(Side.CLIENT)
	private void registerTexture() {
		this.setTexture(DirectResourceManager.getResource("Reika/ChromatiCraft/Textures/Wands/crystalwand.png"));
		colors = new ColorBlendList(10, ChromaFX.getChromaColorTiles());
	}

	@Override
	public IWandRodOnUpdate getOnUpdate() {
		return updater;
	}

	@Override
	public ResourceLocation getTexture() {
		/*
		int c = colors != null ? colors.getColor(System.currentTimeMillis()/50D) : 0;
		float r = ReikaColorAPI.getRed(c)/255F;
		float g = ReikaColorAPI.getGreen(c)/255F;
		float b = ReikaColorAPI.getBlue(c)/255F;
		GL11.glColor4f(r, g, b, 1);
		 */
		return super.getTexture();
	}

	private static class WandUpdater implements IWandRodOnUpdate {

		@Override
		public void onUpdate(ItemStack is, EntityPlayer player) {

			if (ReikaPlayerAPI.isFake(player))
				return;
			int n = ReikaItemHelper.matchStacks(is, player.getCurrentEquippedItem()) ? 100 : 800;
			if (rand.nextInt(n) == 0) {

				AspectList al = ReikaThaumHelper.getVisInWand(is);

				ElementTagCompound tag = new ElementTagCompound();

				boolean flag = false;

				for (Aspect a : Aspect.getPrimalAspects()) {
					if (al.getAmount(a) > 0) {
						int sp = ReikaThaumHelper.getWandSpaceFor(is, a);
						if (sp > 0) {
							PlayerElementBuffer.instance.removeFromPlayer(player, ChromaAspectManager.instance.getElementCost(a, 1));
							ReikaThaumHelper.addVisToWand(is, a, Math.min(sp, 10));
							flag = true;
						}
					}
				}

				if (flag && player instanceof EntityPlayerMP) {
					int[] data = new int[16];
					for (int i = 0; i < CrystalElement.elements.length; i++) {
						CrystalElement e = CrystalElement.elements[i];
						data[i] = tag.getValue(e);
					}
					ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.WANDCHARGE.ordinal(), (EntityPlayerMP)player, data);
				}

			}
		}

	}

	@SideOnly(Side.CLIENT)
	public static void updateWandClient(EntityPlayer player, int[] data) {

		ElementTagCompound tag = new ElementTagCompound();
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			tag.addValueToColor(e, data[i]);
		}

		Vec3 vec = player.getLookVec();
		for (CrystalElement e : CrystalElement.elements) {
			double px = ReikaRandomHelper.getRandomPlusMinus(player.posX+vec.xCoord, 1);
			double py = ReikaRandomHelper.getRandomPlusMinus(player.posY+vec.yCoord, 0.5);
			double pz = ReikaRandomHelper.getRandomPlusMinus(player.posZ+vec.zCoord, 1);
			EntityFX fx = new EntityRuneFX(player.worldObj, px, py, pz, e).setLife(40).setScale(2).setGravity(0.1F);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		ReikaSoundHelper.playClientSound(ChromaSounds.CAST, player, 1, 1);
	}

}
