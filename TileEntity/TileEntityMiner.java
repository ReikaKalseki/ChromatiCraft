package Reika.ChromatiCraft.TileEntity;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMiner extends TileEntityChromaticBase {

	private boolean digging;

	private int range = 512;

	private int readX = 0;
	private int readY = 0;
	private int readZ = 0;

	private static int TICKSTEP = 256;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.MINER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			for (int i = 0; i < TICKSTEP*8 && digging; i++) {
				int dx = x+readX;
				int dy = readY;
				int dz = z+readZ;
				ReikaWorldHelper.forceGenAndPopulate(world, dx, dy, dz, meta);
				Block id = world.getBlock(dx, dy, dz);
				int meta2 = world.getBlockMetadata(dx, dy, dz);
				//ReikaJavaLibrary.pConsole(readX+":"+dx+", "+dy+", "+readZ+":"+dz+" > "+ores.getSize(), Side.SERVER);
				if (ReikaBlockHelper.isOre(id, meta2)) {
					//ores.addBlockCoordinate(dx, dy, dz);
					this.dropBlock(world, x, y, z, dx, dy, dz, id, meta2);
				}
				this.updateReadPosition();
			}
		}
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		double angle = (System.currentTimeMillis()/15D)%360;
		double d = 0.05;
		double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, d);
		double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, d);
		double py = ReikaRandomHelper.getRandomPlusMinus(y+1.5+0.5*(1+Math.sin(Math.toRadians(angle))), d);
		CrystalElement c = CrystalElement.randomElement();//CrystalElement.elements[(this.getTicksExisted()/16)%16];
		EntityBlurFX fx = new EntityBlurFX(c, world, px, py, pz, 0, 0, 0).setScale(2F).setLife(10).setIcon(ChromaIcons.CENTER);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private void dropBlock(World world, int x, int y, int z, int dx, int dy, int dz, Block id, int meta2) {
		ArrayList<ItemStack> li = id.getDrops(world, dx, dy, dz, meta2, 0);
		for (ItemStack is : li) {
			boolean flag = true;
			for (int i = 0; i < 6 && flag; i++) {
				TileEntity te = this.getAdjacentTileEntity(dirs[i]);
				if (te instanceof IInventory) {
					if (ReikaInventoryHelper.addToIInv(is, (IInventory)te))
						flag = false;
				}
			}
			if (flag)
				ReikaItemHelper.dropItem(world, x+0.5, y+1.5, z+0.5, is);
		}
		world.setBlock(dx, dy, dz, Blocks.stone);
	}

	private void updateReadPosition() {
		boolean flag1 = false;
		boolean flag2 = false;
		readX++;
		if (readX > range) {
			readX = -range;
			flag1 = true;
		}
		if (flag1) {
			readZ++;
			//ReikaJavaLibrary.pConsole(readY+" > "+readZ+":"+range+" > "+ores.getSize(), Side.SERVER);
			if (readZ > range) {
				readZ = -range;
				flag2 = true;
			}
			if (flag2) {
				readY++;
			}
		}
		if (readY >= worldObj.getActualHeight())
			digging = false;
	}

	public void triggerCalculation() {
		digging = true;
		readX = -range;
		readY = 1;
		readZ = -range;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
