package Reika.ChromatiCraft.ModInterface;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.ProportionedBlockBox;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import buildcraft.api.tiles.ITileAreaProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value = "buildcraft.api.tiles.ITileAreaProvider")
public class TileEntityFloatingLandmark extends TileEntityChromaticBase implements ITileAreaProvider {

	private static final int RANGE = 256;

	private ProportionedBlockBox area;
	private boolean isPrimary = true;
	private Coordinate primary;
	private boolean anchored;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LANDMARK;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			if (isPrimary) {
				this.doParticles(world, x, y, z);
			}
		}
		else {
			/*
			if (isPrimary) {
				for (int dx = area.minX; dx < area.maxX; dx++) {
					for (int dy = area.minY; dy < area.maxY; dy++) {
						for (int dz = area.minZ; dz < area.maxZ; dz++) {
							if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz))
								world.setBlock(dx, dy, dz, Blocks.glass);
						}
					}
				}
			}*/
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		int n = 4*Math.min(40, Math.max(1, area.getVolume()/20));
		for (int i = 0; i < n; i++) {
			double dx = 0;
			double dy = 0;
			double dz = 0;
			boolean edge = rand.nextInt(5) < 2;
			if (edge) {
				switch(rand.nextInt(12)) {
					case 0:
						dx = area.minX;
						dz = area.minZ;
						dy = ReikaRandomHelper.getRandomBetween(area.minY, (double)area.maxY);
						break;
					case 1:
						dx = area.maxX;
						dz = area.minZ;
						dy = ReikaRandomHelper.getRandomBetween(area.minY, (double)area.maxY);
						break;
					case 2:
						dx = area.minX;
						dz = area.maxZ;
						dy = ReikaRandomHelper.getRandomBetween(area.minY, (double)area.maxY);
						break;
					case 3:
						dx = area.maxX;
						dz = area.maxZ;
						dy = ReikaRandomHelper.getRandomBetween(area.minY, (double)area.maxY);
						break;
					case 4:
						dx = ReikaRandomHelper.getRandomBetween(area.minX, (double)area.maxX);
						dz = area.minZ;
						dy = area.minY;
						break;
					case 5:
						dx = ReikaRandomHelper.getRandomBetween(area.minX, (double)area.maxX);
						dz = area.minZ;
						dy = area.maxY;
						break;
					case 6:
						dx = ReikaRandomHelper.getRandomBetween(area.minX, (double)area.maxX);
						dz = area.maxZ;
						dy = area.minY;
						break;
					case 7:
						dx = ReikaRandomHelper.getRandomBetween(area.minX, (double)area.maxX);
						dz = area.maxZ;
						dy = area.maxY;
						break;
					case 8:
						dx = area.minX;
						dz = ReikaRandomHelper.getRandomBetween(area.minZ, (double)area.maxZ);
						dy = area.minY;
						break;
					case 9:
						dx = area.maxX;
						dz = ReikaRandomHelper.getRandomBetween(area.minZ, (double)area.maxZ);
						dy = area.minY;
						break;
					case 10:
						dx = area.minX;
						dz = ReikaRandomHelper.getRandomBetween(area.minZ, (double)area.maxZ);
						dy = area.maxY;
						break;
					case 11:
						dx = area.maxX;
						dz = ReikaRandomHelper.getRandomBetween(area.minZ, (double)area.maxZ);
						dy = area.maxY;
						break;
				}
			}
			else {
				dx = ReikaRandomHelper.getRandomBetween(area.minX, (double)area.maxX);
				dy = ReikaRandomHelper.getRandomBetween(area.minY, (double)area.maxY);
				dz = ReikaRandomHelper.getRandomBetween(area.minZ, (double)area.maxZ);
				switch(rand.nextInt(6)) {
					case 0:
						dx = area.minX;
						break;
					case 1:
						dx = area.maxX;
						break;
					case 2:
						dy = area.minY;
						break;
					case 3:
						dy = area.maxY;
						break;
					case 4:
						dz = area.minZ;
						break;
					case 5:
						dz = area.maxZ;
						break;
				}
			}
			/*
			boolean valid = world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz) || ChromaTiles.getTile(world, dx, dy, dz) == this.getTile();
if (!valid)
	continue;
			 */
			//double px = dx+rand.nextDouble();
			//double py = dy+rand.nextDouble();
			//double pz = dz+rand.nextDouble();
			EntityBlurFX fx = new EntityBlurFX(world, dx, dy, dz);
			int hue = (int)((this.getTicksExisted()*2+dx*3+dy*1+dz*2)%360);
			int c = ReikaColorAPI.getModifiedHue(0xff0000, hue);
			int l = ReikaRandomHelper.getRandomBetween(10, 40);
			float s = (float)ReikaRandomHelper.getRandomBetween(0.5, 1);
			ChromaIcons ico = ChromaIcons.FADE;
			switch(rand.nextInt(6)) {
				case 1:
					ico = ChromaIcons.NODE2;
					break;
				case 2:
					ico = ChromaIcons.BIGFLARE;
					break;
				case 3:
					ico = ChromaIcons.SPARKLEPARTICLE;
					break;
				case 4:
					ico = ChromaIcons.FLARE;
					break;
				case 5:
					ico = ChromaIcons.RINGFLARE;
					break;
			}
			fx.setColor(c).setLife(l).setScale(s).setIcon(ico);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		area = new ProportionedBlockBox(BlockBox.block(this));
		if (world.isRemote)
			return;
		TileEntityFloatingLandmark te = this.findParent(world, x, y, z);
		if (te != null) {
			isPrimary = false;
			primary = new Coordinate(te);
			te.area = new ProportionedBlockBox(te.area.volume.addBlock(xCoord, yCoord, zCoord));
			area = te.area;
			this.syncAllData(false);
		}
	}

	private TileEntityFloatingLandmark findParent(World world, int x, int y, int z) {
		for (int d = 1; d < RANGE; d++) {
			for (int i = 0; i < 6; i++) {
				int dx = x+dirs[i].offsetX*d;
				int dy = y+dirs[i].offsetY*d;
				int dz = z+dirs[i].offsetZ*d;
				if (ChromaTiles.getTile(world, dx, dy, dz) == this.getTile()) {
					TileEntityFloatingLandmark te = (TileEntityFloatingLandmark)world.getTileEntity(dx, dy, dz);
					return te.getPrimary();
				}
			}
		}
		return null;
	}

	public TileEntityFloatingLandmark getPrimary() {
		if (isPrimary)
			return this;
		TileEntity te = primary != null ? primary.getTileEntity(worldObj) : null;
		return te instanceof TileEntityFloatingLandmark ? (TileEntityFloatingLandmark)te : null;
	}

	@Override
	public int getUpdatePacketRadius() {
		return RANGE;
	}

	public boolean isAnchored() {
		return anchored;
	}

	public void anchor() {
		anchored = true;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("primary", isPrimary);
		if (area != null)
			area.volume.writeToNBT(NBT);
		if (primary != null)
			primary.writeToNBT("root", NBT);
		NBT.setBoolean("anchor", anchored);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		isPrimary = NBT.getBoolean("primary");
		if (NBT.hasKey("minX"))
			area = new ProportionedBlockBox(BlockBox.readFromNBT(NBT));
		if (NBT.hasKey("root"))
			primary = Coordinate.readFromNBT("root", NBT);
		anchored = NBT.getBoolean("anchor");
	}

	@Override
	public int xMin() {
		return area.volume.minX;
	}

	@Override
	public int yMin() {
		return area.volume.minY;
	}

	@Override
	public int zMin() {
		return area.volume.minZ;
	}

	@Override
	public int xMax() {
		return area.volume.maxX-1;
	}

	@Override
	public int yMax() {
		return area.volume.maxY-1;
	}

	@Override
	public int zMax() {
		return area.volume.maxZ-1;
	}

	@Override
	public void removeFromWorld() {

	}

	@Override
	public boolean isValidFromLocation(int x, int y, int z) {
		return new Coordinate(x, y, z).getTaxicabDistanceTo(new Coordinate(this)) == 1;
	}

}
