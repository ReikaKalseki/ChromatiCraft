package Reika.ChromatiCraft.Items.Tools;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.Render.ProbeInfoOverlayRenderer;
import Reika.ChromatiCraft.Auxiliary.Render.StructureErrorOverlays;
import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public class ItemCrystalProbe extends ItemPoweredChromaTool {

	private static final HashMap<BlockKey, Inspections> lookup = new HashMap();
	private static final int MIN_CHARGE = Inspections.getMostExpensiveOperation().energyCost;
	public static final int CHARGE_TIME = 50;

	public ItemCrystalProbe(int index) {
		super(index);
	}

	/*
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		Inspections i = Inspections.getFor(world, x, y, z);
		if (i != null) {
			if (i.doEffect(world, x, y, z, s, ep, is)) {
				this.removeCharge(is, i.energyCost);
				return true;
			}
		}
		return false;
	}*/

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
			Inspections i = Inspections.getFor(world, x, y, z);
			if (i != null && this.getCharge(is) >= i.energyCost) {
				ChromaSounds.LOREHEX.playSound(ep, 1, 1);
				if (i.doEffect(world, x, y, z, s, ep, is)) {
					this.removeCharge(is, i.energyCost);
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
		if (!this.handleUseAllowance(ep) && this.isActivated(ep, is, true))
			ep.setItemInUse(is, this.getMaxItemUseDuration(is));
		return is;
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
		return this.getCharge(is)/(float)this.getMaxCharge() > 0.8;
	}

	@Override
	protected CrystalElement getColor() {
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
	protected int getChargeState(float frac) {
		return frac >= MIN_CHARGE/(float)this.getMaxCharge() ? Math.max(1, super.getChargeState(frac)) : 0;
	}

	@Override
	protected boolean isActivated(EntityPlayer e, ItemStack is, boolean held) {
		return this.getCharge(is) >= MIN_CHARGE;
	}

	@Override
	protected int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is) {
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
	private static enum Inspections {
		REPEATER_CONNECTIVITY(60, ChromaTiles.REPEATER, ChromaTiles.COMPOUND, ChromaTiles.BROADCAST, ChromaTiles.WEAKREPEATER, ChromaTiles.SKYPEATER),
		STRUCTURE_CHECK(1000, ChromaTiles.TABLE, ChromaTiles.RITUAL);

		public final int energyCost;
		private final HashSet<BlockKey> trigger = new HashSet();

		private Inspections(int energy) {
			energyCost = energy;
		}

		private Inspections(int energy, ChromaTiles... arr) {
			this(energy);
			for (ChromaTiles t : arr) {
				this.addBlock(new BlockKey(t.getBlock(), t.getBlockMetadata()));
			}
		}

		private Inspections(int energy, Block b) {
			this(energy);
			this.addBlock(new BlockKey(b));
		}

		private Inspections(int energy, Block b, int meta) {
			this(energy);
			this.addBlock(new BlockKey(b, meta));
		}

		private void addBlock(BlockKey b) {
			trigger.add(b);
			lookup.put(b, this);
		}

		private boolean doEffect(World world, int x, int y, int z, int s, EntityPlayer ep, ItemStack is) {
			switch(this) {
				case REPEATER_CONNECTIVITY:
					for (CrystalElement e : CrystalElement.elements) {
						CrystalRepeater te = (CrystalRepeater)world.getTileEntity(x, y, z);
						boolean can = te.isConductingElement(e);
						boolean flag = can && CrystalNetworker.instance.checkConnectivity(e, te);
						ProbeInfoOverlayRenderer.instance.markConnectivity(ep, e, flag, can);
					}
					return true;
				case STRUCTURE_CHECK:
					FilledBlockArray arr = null;
					switch(ChromaTiles.getTile(world, x, y, z)) {
						case TABLE:
							TileEntityCastingTable te = (TileEntityCastingTable)world.getTileEntity(x, y, z);
							switch(te.getTier()) {
								case CRAFTING:
									break;
								case TEMPLE:
									arr = ChromaStructures.getCastingLevelOne(world, x, y-1, z);
									break;
								case MULTIBLOCK:
									arr = ChromaStructures.getCastingLevelTwo(world, x, y-1, z);
									break;
								case PYLON:
									arr = ChromaStructures.getCastingLevelThree(world, x, y-1, z);
									break;
							}
							break;
						case RITUAL:
							arr = ChromaStructures.getRitualStructure(world, x, y, z, true, false);
							break;
						default:
							break;
					}
					if (arr != null) {
						if (!arr.matchInWorld(StructureErrorOverlays.instance)) {

						}
						return true;
					}
					else {
						return false;
					}
				default:
					return false;
			}
		}

		public static Inspections getMostExpensiveOperation() {
			Inspections ret = null;
			for (Inspections i : values()) {
				if (ret == null || ret.energyCost < i.energyCost)
					ret = i;
			}
			return ret;
		}

		public static Inspections getFor(World world, int x, int y, int z) {
			return lookup.get(BlockKey.getAt(world, x, y, z));
		}
	}

}
