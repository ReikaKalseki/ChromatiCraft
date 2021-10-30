package Reika.ChromatiCraft.Items.Tools.Powered;

import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ColoredMultiBlockChromaTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Auxiliary.Render.ProbeInfoOverlayRenderer;
import Reika.ChromatiCraft.Auxiliary.Render.StructureErrorOverlays;
import Reika.ChromatiCraft.Auxiliary.Structure.RitualStructure;
import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Magic.ToolChargingSystem;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCrystalProbe extends ItemPoweredChromaTool {

	public static final int CHARGE_TIME = 50;

	public ItemCrystalProbe(int index) {
		super(index);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack is, World world, EntityPlayer ep, int count) {
		count = MathHelper.clamp_int(this.getMaxItemUseDuration(is)-count, 0, CHARGE_TIME);
		//ReikaChatHelper.write(power+"  ->  "+charge);
		//ReikaJavaLibrary.pConsole(count);
		if (count >= CHARGE_TIME)
			this.fire(is, world, ep);
		ep.setItemInUse(null, 0);
	}

	private void fire(ItemStack is, World world, EntityPlayer ep) {
		if (world.isRemote)
			return;
		MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 5, false);
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			int s = mov.sideHit;
			Inspections i = Inspections.list[this.getActionType(is)];
			if (i != null && i.isValid(world, x, y, z) && ToolChargingSystem.instance.getCharge(is) >= i.energyCost) {
				ChromaSounds.LOREHEX.playSound(ep, 1, 1);
				if (i.doEffect(world, x, y, z, s, ep, is)) {
					ToolChargingSystem.instance.removeCharge(is, i.energyCost);
				}
			}
		}
	}

	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer ep) {
		return is;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack is) {
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack is) {
		return EnumAction.block;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (ep.isSneaking()) {
			int type = this.getActionType(is);
			type = (type+1)%Inspections.list.length;
			is.stackTagCompound.setInteger("type", type);
		}
		else if (!this.handleUseAllowance(ep) && this.isActivated(ep, is, true))
			ep.setItemInUse(is, this.getMaxItemUseDuration(is));
		return is;
	}

	private int getActionType(ItemStack is) {
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
		}
		int type = is.stackTagCompound != null ? is.stackTagCompound.getInteger("type") : 0;
		return type;
	}

	@Override
	public void onUsingTick(ItemStack is, EntityPlayer ep, int count) {
		count = this.getMaxItemUseDuration(is)-count;
		count = MathHelper.clamp_int(count, 0, CHARGE_TIME);
		if (ep.worldObj.isRemote) {
			//this.doChargingParticles(ep, count);
		}
		else {
			ChromaSounds.USE.playSound(ep, 0.25F+2F*count/CHARGE_TIME, MathHelper.clamp_float(0.5F, 2F*count/CHARGE_TIME, 2F));
		}
	}

	@Override
	public boolean hasEffect(ItemStack is) {
		return ToolChargingSystem.instance.getCharge(is)/(float)this.getMaxCharge() > 0.8;
	}

	@Override
	public CrystalElement getColor(ItemStack is) {
		return CrystalElement.BLUE;
	}

	@Override
	public int getMaxCharge() {
		return 12000;
	}

	@Override
	public int getChargeStates() {
		return 4;
	}

	@Override
	public int getChargeState(float frac) {
		return frac >= Inspections.getMostExpensiveOperation().energyCost/(float)this.getMaxCharge() ? Math.max(1, super.getChargeState(frac)) : 0;
	}

	@Override
	public boolean isActivated(EntityPlayer e, ItemStack is, boolean held) {
		return ToolChargingSystem.instance.getCharge(is) >= this.getRequiredCharge(is);
	}

	private int getRequiredCharge(ItemStack is) {
		return Inspections.list[this.getActionType(is)].energyCost;
	}

	@Override
	public int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is) {
		return 0;
	}

	@Override
	protected boolean doTick(ItemStack is, World world, EntityPlayer e, boolean held) {
		return false;
	}
	/*
	private static CrystalElement getTuning(ItemStack is) {
		if (is.stackTagCompound != null && is.stackTagCompound.hasKey("tuned")) {
			return CrystalElement.elements[is.stackTagCompound.getInteger("tuned")];
		}
		else {
			return null;
		}
	}
	 */

	@Override
	@SideOnly(Side.CLIENT)
	protected void renderExtraIcons(RenderItem ri, ItemStack is, ItemRenderType type) {
		if (type == ItemRenderType.INVENTORY && GuiScreen.isCtrlKeyDown()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			GL11.glDisable(GL11.GL_LIGHTING);
			double sc = 1;
			GL11.glScaled(sc, sc, sc);
			GL11.glTranslated(0.5/sc, -0.5/sc, 0);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/infoicons.png");
			int d = 1+this.getActionType(is);
			if (d == 1 && (System.currentTimeMillis()/500)%2 == 0)
				d = 0;
			double u = 0.0625*d;
			double v = 0.125;
			double s = 0.0625;
			Tessellator v5 = Tessellator.instance;
			double z = 50;
			v5.startDrawingQuads();
			v5.addVertexWithUV(0, 0, z, u, v+s);
			v5.addVertexWithUV(1, 0, z, u+s, v+s);
			v5.addVertexWithUV(1, 1, z, u+s, v);
			v5.addVertexWithUV(0, 1, z, u, v);
			v5.draw();
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
	}

	@Override
	public String getNotes(int subpage) {
		StringBuilder sb = new StringBuilder();
		sb.append("Action Types:\n");
		for (Inspections i : Inspections.list) {
			sb.append("  ");
			sb.append(i.displayName());
			sb.append("\n");
		}
		sb.append("\n");
		sb.append(super.getNotes(subpage));
		return sb.toString();
	}

	private static enum Inspections {
		REPEATER_CONNECTIVITY(60, CrystalReceiver.class),
		STRUCTURE_CHECK(1000, MultiBlockChromaTile.class);

		public final int energyCost;
		private final Class trigger;

		private static final Inspections[] list = values();

		private Inspections(int energy, Class check) {
			energyCost = energy;
			trigger = check;
		}

		private boolean doEffect(World world, int x, int y, int z, int s, EntityPlayer ep, ItemStack is) {
			switch(this) {
				case REPEATER_CONNECTIVITY:
					for (CrystalElement e : CrystalElement.elements) {
						CrystalReceiver te = (CrystalReceiver)world.getTileEntity(x, y, z);
						boolean can = te.isConductingElement(e);
						boolean flag = can && CrystalNetworker.instance.checkConnectivity(e, te);
						ProbeInfoOverlayRenderer.instance.markConnectivity(ep, e, flag, can);
					}
					return true;
				case STRUCTURE_CHECK:
					MultiBlockChromaTile te = (MultiBlockChromaTile)world.getTileEntity(x, y, z);
					if (te.canStructureBeInspected()) {
						ChromaStructures str = te.getPrimaryStructure();
						if (str != null) {
							Coordinate c = te.getStructureOffset();
							if (c == null)
								c = new Coordinate(0, 0, 0);
							str.getStructure().resetToDefaults();
							switch(str) {
								case RITUAL:
									((RitualStructure)str.getStructure()).initializeEnhance(true, false);
									break;
								default:
									break;
							}
							CrystalElement e = str.requiresColor ? ((ColoredMultiBlockChromaTile)te).getColor() : null;
							FilledBlockArray arr = str.requiresColor ? str.getArray(world, x, y, z, e) : str.getArray(world, x+c.xCoord, y+c.yCoord, z+c.zCoord);
							if (!arr.matchInWorld(StructureErrorOverlays.instance)) {

							}
							return true;
						}
					}
					return false;
				default:
					return false;
			}
		}

		public String displayName() {
			return WordUtils.capitalize(this.name(), ' ', '_');
		}

		private boolean isValid(World world, int x, int y, int z) {
			TileEntity te = world.getTileEntity(x, y, z);
			return te != null && trigger.isAssignableFrom(te.getClass());
		}

		public static Inspections getMostExpensiveOperation() {
			Inspections ret = null;
			for (Inspections i : values()) {
				if (ret == null || ret.energyCost < i.energyCost)
					ret = i;
			}
			return ret;
		}
	}

}
