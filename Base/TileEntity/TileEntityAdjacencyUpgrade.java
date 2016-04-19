package Reika.ChromatiCraft.Base.TileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public abstract class TileEntityAdjacencyUpgrade extends TileEntityChromaticBase implements NBTTile, SneakPop {

	public static final int MAX_TIER = 8;

	private int tier;

	private boolean particles = true;
	private int soundtick = 0;

	public final int getTier() {
		return tier;
	}

	@Override
	public final void updateEntity(World world, int x, int y, int z, int meta) {

		if (!this.canRun(world, x, y, z)) {
			soundtick = 0;
			return;
		}

		if (particles && world.isRemote) {
			this.spawnParticles(world, x, y, z);
		}

		soundtick++;

		float f = 1+(1+this.getTier())/(float)MAX_TIER;
		int l = (int)(221/f);
		if (soundtick%l == 0)
			ChromaSounds.DRONE.playSoundAtBlock(world, x, y, z, 0.25F, f);

		if (this.ticksIndividually()) {
			long time = System.nanoTime();
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				if (!this.tickDirection(world, x, y, z, dir, time))
					break;
			}
		}
		else {
			this.doCollectiveTick(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		int p2 = Minecraft.getMinecraft().gameSettings.particleSetting;
		if (rand.nextInt(1+p2) == 0) {
			int p = this.getTier() > 0 ? (this.getTier()+1)/2 : rand.nextBoolean() ? 1 : 0;
			for (int i = 0; i < p; i++) {
				double dx = rand.nextDouble();
				double dy = rand.nextDouble();
				double dz = rand.nextDouble();
				double v = 0.125;
				double vx = v*(dx-0.5);
				double vy = v*(dy-0.5);
				double vz = v*(dz-0.5);
				//ReikaParticleHelper.FLAME.spawnAt(world, x-0.5+dx*2, y-0.5+dy*2, z-0.5+dz*2, v*(-1+dx*2), v*(-1+dy*2), v*(-1+dz*2));
				//Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySparkleFX(world, x+0.5, y+0.5, z+0.5, vx, vy, vz));
				//Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySparkleFX(world, x+0.5, y+0.5, z+0.5, vx, vy, vz));

				dx = x-2+dx*4;
				dy = y-2+dy*4;
				dz = z-2+dz*4;

				vx = -(dx-x)/8;
				vy = -(dy-y)/8;
				vz = -(dz-z)/8;
				Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySparkleFX(world, dx+0.5, dy+0.5, dz+0.5, vx, vy, vz));
			}
		}
	}

	protected abstract boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime);

	public boolean canRun(World world, int x, int y, int z) {
		return world.getBlockPowerInput(x, y, z) < 15;
	}

	protected boolean ticksIndividually() {
		return true;
	}

	protected void doCollectiveTick(World world, int x, int y, int z) {

	}

	public abstract CrystalElement getColor();

	public void setDataFromItemStackTag(ItemStack is) {
		if (ChromaItems.ADJACENCY.matchWith(is)) {
			if (is.stackTagCompound != null) {
				tier = is.stackTagCompound.getInteger("tier");
			}
		}
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		NBT.setInteger("tier", this.getTier());
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		tier = NBT.getInteger("tier");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("tier", tier);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setBoolean("particle", particles);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		particles = NBT.getBoolean("particle");
	}

	@Override
	public final void drop() {
		ItemStack is = ChromaItems.ADJACENCY.getStackOf(this.getColor());
		is.stackTagCompound = new NBTTagCompound();
		this.getTagsToWriteToStack(is.stackTagCompound);
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, is);
		this.delete();
	}

	public final boolean canDrop(EntityPlayer ep) {
		return ep.getUniqueID().equals(placerUUID);
	}

	@Override
	public final ChromaTiles getTile() {
		return ChromaTiles.ADJACENCY;
	}

}
