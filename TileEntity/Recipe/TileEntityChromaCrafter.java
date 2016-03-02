package Reika.ChromatiCraft.TileEntity.Recipe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityChromaCrafter extends InventoriedRelayPowered implements IFluidHandler, ItemOnRightClick, OwnedTile, InertIInv, IPipeConnection {

	public static final int CRAFTING_DURATION = 300;

	private static final ElementTagCompound required = new ElementTagCompound();

	static {
		required.addTag(CrystalElement.BLACK, 500);
		required.addTag(CrystalElement.GRAY, 2500);
		required.addTag(CrystalElement.PURPLE, 1000);
		required.addTag(CrystalElement.CYAN, 200);
		required.addTag(CrystalElement.BROWN, 200);
	}

	private int recipeTick = 0;

	private final HybridTank tank = new HybridTank("chromacraft", 3000);

	private EntityPlayer craftingPlayer;

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("recipe", recipeTick);
		tank.writeToNBT(NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		recipeTick = NBT.getInteger("recipe");
		tank.readFromNBT(NBT);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (recipeTick > 0) {
			this.onRecipeTick(world, x, y, z);
		}
	}

	private void onRecipeTick(World world, int x, int y, int z) {
		if (recipeTick == 0) {
			this.craft();
		}
		else {
			recipeTick--;
			if (world.isRemote)
				this.recipeParticles(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void recipeParticles(World world, int x, int y, int z) {

	}

	private void craft() {
		recipeTick = 0;
	}

	@Override
	public boolean canExtractItem(int side, ItemStack is, int slot) {
		return slot == 3;
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		switch(slot) {
			case 0:
				return (ChromaItems.SHARD.matchWith(is) && is.getItemDamage() < 16) || !PoolRecipes.instance.getRecipesForItem(is).isEmpty();
			case 1:
				return ChromaItems.BERRY.matchWith(is) || (inv[0] != null && PoolRecipes.instance.isCompatibleWith(inv[0]));
			case 2:
				return ReikaItemHelper.matchStacks(is, ChromaStacks.etherBerries);
		}
		return false;
	}

	@Override
	public void markDirty() {
		super.markDirty();

		if (this.checkForRecipe()) {
			recipeTick = CRAFTING_DURATION;
		}
	}

	private boolean checkForRecipe() {
		if (craftingPlayer == null)
			return false;
		int flags = this.getRecipeFlags();
		if (flags == 0)
			return false;
		for (int i = 0; i < RecipeFlags.list.length; i++) {
			RecipeFlags r = RecipeFlags.list[i];
			if ((flags & r.flag) != 0) {
				if (!r.canRunRecipe(craftingPlayer))
					return false;
			}
		}
		return true;
	}

	private int getRecipeFlags() {
		int flag = 0;
		if (inv[0] == null)
			return 0;
		if (ChromaItems.SHARD.matchWith(inv[0]) && inv[0].getItemDamage() < 16)
			flag = flag | RecipeFlags.SHARDCHARGE.flag;
		if (!PoolRecipes.instance.getRecipesForItem(inv[0]).isEmpty())
			flag = flag | RecipeFlags.ALLOYS.flag;
		if (ReikaItemHelper.matchStacks(inv[2], ChromaStacks.etherBerries))
			flag = flag | RecipeFlags.ENHANCED.flag;
		return flag;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.canFill(from, resource.getFluid()) ? tank.fill(resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from.offsetY == 0 && fluid == FluidRegistry.getFluid("chroma");
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 10000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CHROMACRAFTER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return dir.offsetY == 0;
	}

	@Override
	protected ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	@ModDependent(ModList.BCTRANSPORT)
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		return ConnectOverride.DISCONNECT;
	}

	@Override
	public ItemStack onRightClickWith(ItemStack item, EntityPlayer ep) {
		for (int i = 0; i < inv.length-1; i++) {
			if (item != null && !this.isItemValidForSlot(i, item))
				continue;
			if (inv[i] != null)
				ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, inv[0]);
			inv[i] = item != null ? item.copy() : null;
			craftingPlayer = ep;
			this.markDirty();
			return null;
		}
		return item;
	}

	private static enum RecipeFlags {
		SHARDCHARGE(),
		ALLOYS(),
		ENHANCED(),
		ACCELERATED();

		private final int flag;

		private static final RecipeFlags[] list = values();

		private RecipeFlags() {
			flag = 1 << this.ordinal();
		}

		public boolean canRunRecipe(EntityPlayer ep) {
			switch(this) {
				case SHARDCHARGE:
					return ProgressStage.SHARDCHARGE.isPlayerAtStage(ep);
				case ALLOYS:
					return ProgressStage.ALLOY.isPlayerAtStage(ep);
				case ENHANCED:
					return ProgressStage.POWERCRYSTAL.playerHasPrerequisites(ep);
				case ACCELERATED:
					return ProgressStage.DIMENSION.isPlayerAtStage(ep);
			}
			return false;
		}
	}

}
