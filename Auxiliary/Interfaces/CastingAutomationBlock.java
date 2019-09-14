package Reika.ChromatiCraft.Auxiliary.Interfaces;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.CastingAutomationSystem;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;

import appeng.api.networking.security.IActionHost;

@Strippable(value={"appeng.api.networking.IActionHost"})
public interface CastingAutomationBlock extends GuiController, OwnedTile, IActionHost {

	public Collection<CastingRecipe> getAvailableRecipes();

	public TileEntityCastingTable getTable();

	public int getInjectionTickRate();
	public boolean isAbleToRun(TileEntityCastingTable te);

	public boolean canTriggerCrafting();
	public boolean canPlaceCentralItemForMultiRecipes();
	public boolean canRecursivelyRequest();

	public CastingAutomationSystem getAutomationHandler();

	public void consumeEnergy(CastingRecipe c, TileEntityCastingTable te, ItemStack is);
	public boolean canCraft(World world, int x, int y, int z, TileEntityCastingTable te);

	public TileEntity getItemPool();

}
