package Reika.ChromatiCraft.ModInterface;

import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.ProportionedBlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.ProportionedBlockBox.CubeEdge;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import buildcraft.api.tiles.ITileAreaProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value = "buildcraft.api.tiles.ITileAreaProvider")
public class TileEntityFloatingLandmark extends TileEntityChromaticBase implements ITileAreaProvider, BreakAction {

	private static final int RANGE = 256;

	private ProportionedBlockBox area;
	private boolean isPrimary = true;
	private Coordinate primary;
	private boolean anchored;

	private final HashSet<Coordinate> connections = new HashSet();

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
			if (area == null)
				this.findArea(world, x, y, z);
			if (isPrimary) {
				/*
				for (int dx = area.volume.minX; dx < area.volume.maxX; dx++) {
					for (int dy = area.volume.minY; dy < area.volume.maxY; dy++) {
						for (int dz = area.volume.minZ; dz < area.volume.maxZ; dz++) {
							if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz))
								world.setBlock(dx, dy, dz, Blocks.glass);
						}
					}
				}*/
			}
		}
	}

	private void findArea(World world, int x, int y, int z) {
		area = new ProportionedBlockBox(BlockBox.block(this));
		if (world.isRemote)
			return;
		TileEntityFloatingLandmark te = this.findParent(world, x, y, z);
		if (te != null) {
			this.slaveTo(te);
		}
		this.syncAllData(false);
	}

	private void slaveTo(TileEntityFloatingLandmark te) {
		isPrimary = false;
		primary = new Coordinate(te);
		te.addLink(this);
		area = te.area;
	}

	private void addLink(TileEntityFloatingLandmark te) {
		area = new ProportionedBlockBox(area.volume.addCoordinate(te.xCoord, te.yCoord, te.zCoord));
		connections.add(new Coordinate(te));
		this.syncAllData(false);
	}

	public void reset() {
		area = null;
		if (isPrimary) {
			for (Coordinate c : connections) {
				TileEntity te = c.getTileEntity(worldObj);
				if (te instanceof TileEntityFloatingLandmark) {
					((TileEntityFloatingLandmark)te).reset();
				}
			}
			connections.clear();
		}
		else {
			isPrimary = true;
			primary = null;
		}
		this.syncAllData(false);
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		int n = 18*Math.min(40, Math.max(1, area.volume.getSurfaceArea()/20));
		for (int i = 0; i < n; i++) {
			double dx = 0;
			double dy = 0;
			double dz = 0;
			if (rand.nextInt(5) < 2) {
				CubeEdge edge = area.getRandomEdge();
				dx = ReikaRandomHelper.getRandomBetween(edge.root.xCoord, (double)edge.root.xCoord+(edge.length+0)*edge.axis.offsetX);
				dy = ReikaRandomHelper.getRandomBetween(edge.root.yCoord, (double)edge.root.yCoord+(edge.length+0)*edge.axis.offsetY);
				dz = ReikaRandomHelper.getRandomBetween(edge.root.zCoord, (double)edge.root.zCoord+(edge.length+0)*edge.axis.offsetZ);
				if (edge.isPositiveX) {
					dx++;
				}
				if (edge.isPositiveY) {
					dy++;
				}
				if (edge.isPositiveZ) {
					dz++;
				}
			}
			else {
				dx = ReikaRandomHelper.getRandomBetween(area.volume.minX, (double)area.volume.maxX);
				dy = ReikaRandomHelper.getRandomBetween(area.volume.minY, (double)area.volume.maxY);
				dz = ReikaRandomHelper.getRandomBetween(area.volume.minZ, (double)area.volume.maxZ);
				switch(area.getRandomFace()) {
					case WEST:
						dx = area.volume.minX;
						break;
					case EAST:
						dx = area.volume.maxX;
						break;
					case DOWN:
						dy = area.volume.minY;
						break;
					case UP:
						dy = area.volume.maxY;
						break;
					case NORTH:
						dz = area.volume.minZ;
						break;
					case SOUTH:
						dz = area.volume.maxZ;
						break;
					default:
						break;
				}
			}
			int ix = MathHelper.floor_double(dx);
			int iy = MathHelper.floor_double(dy);
			int iz = MathHelper.floor_double(dz);
			boolean valid = world.getBlock(ix, iy, iz).isAir(world, ix, iy, iz) || ChromaTiles.getTile(world, ix, iy, iz) == this.getTile();
			if (!valid)
				continue;
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125/8D);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125/8D);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125/8D);
			EntityBlurFX fx = new EntityBlurFX(world, dx, dy, dz, vx, vy, vz);
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
		this.findArea(world, x, y, z);
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

		ReikaNBTHelper.writeCollectionToNBT(connections, NBT, "links", Coordinate.nbtHandler);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		isPrimary = NBT.getBoolean("primary");
		if (NBT.hasKey("minx"))
			area = new ProportionedBlockBox(BlockBox.readFromNBT(NBT));
		if (NBT.hasKey("root"))
			primary = Coordinate.readFromNBT("root", NBT);
		anchored = NBT.getBoolean("anchor");

		ReikaNBTHelper.readCollectionFromNBT(connections, NBT, "links", Coordinate.nbtHandler);
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
