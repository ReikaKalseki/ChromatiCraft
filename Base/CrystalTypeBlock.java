package Reika.ChromatiCraft.Base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public abstract class CrystalTypeBlock extends Block {

	public CrystalTypeBlock(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		stepSound = new SoundType("stone", 0, 0);
	}

	@Override
	public final void onBlockAdded(World world, int x, int y, int z) {
		ding(world, x, y, z);
	}

	@Override
	public final void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		ding(world, x, y, z, CrystalElement.elements[meta]);
	}

	@Override
	public final void onEntityWalking(World world, int x, int y, int z, Entity ent) {
		ding(world, x, y, z);
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ep) {
		ding(world, x, y, z);
	}

	public static void ding(World world, int x, int y, int z) {
		ding(world, x, y, z, CrystalElement.elements[world.getBlockMetadata(x, y, z)]);
	}

	protected static void ding(World world, int x, int y, int z, CrystalElement e, float pitch) {
		ChromaSounds.DING.playSoundAtBlock(world, x, y, z, (float)ReikaRandomHelper.getRandomPlusMinus(1, 0.2), pitch);
	}

	public static void ding(World world, int x, int y, int z, CrystalElement e) {
		ding(world, x, y, z, e, getRandomPitch(e));
	}

	private static float getRandomPitch(CrystalElement e) { //Generates a major or minor chord
		return CrystalMusicManager.instance.getRandomScaledDing(e);
	}

	@Override
	public final int getLightValue(IBlockAccess iba, int x, int y, int z) {
		int color = CrystalElement.elements[iba.getBlockMetadata(x, y, z)].getColor();
		int l = this.getBrightness(iba, x, y, z);
		return ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(color, l) : l;
	}

	public abstract int getBrightness(IBlockAccess iba, int x, int y, int z);

	public final CrystalElement getCrystalElement(IBlockAccess iba, int x, int y, int z) {
		return CrystalElement.elements[iba.getBlockMetadata(x, y, z)];
	}

}
