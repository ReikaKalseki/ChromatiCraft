package Reika.ChromatiCraft.ModInterface;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCCFloatingSeedsFX;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.ProportionedBlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.ProportionedBlockBox.CubeEdge;
import Reika.DragonAPI.Interfaces.TileEntity.AdjacentUpdateWatcher;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Interfaces.TileEntity.ConditionalUnbreakability;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import buildcraft.api.tiles.ITileAreaProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value = "buildcraft.api.tiles.ITileAreaProvider")
public class TileEntityFloatingLandmark extends TileEntityChromaticBase implements ITileAreaProvider, BreakAction, AdjacentUpdateWatcher, ConditionalUnbreakability {

	private static final int RANGE = 256;

	private ProportionedBlockBox area;
	private boolean isPrimary = true;
	private Coordinate primary;
	private boolean anchored;

	private HashSet<Coordinate> connections = new HashSet();

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LANDMARK;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			this.doBlockParticles(world, x, y, z);
			if (area == null || area.volume.getVolume() == 1) {
				this.doAxisParticles(world, x, y, z);
			}
			else if (isPrimary) {
				this.doAreaParticles(world, x, y, z);
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

	@SideOnly(Side.CLIENT)
	private void doAxisParticles(World world, int x, int y, int z) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		ForgeDirection dir = dirs[this.getTicksExisted()%6];
		for (int d = 1; d < RANGE; d += 4) {
			int dx = x+dir.offsetX*d;
			int dy = y+dir.offsetY*d;
			int dz = z+dir.offsetZ*d;
			if (dy >= 0 && dy <= 255 && ep.getDistanceSq(dx+0.5, dy+0.5, dz+0.5) <= 256 && world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
				double v = 0.25;
				double vx = dir.offsetX*v;
				double vy = dir.offsetY*v;
				double vz = dir.offsetZ*v;
				EntityCCBlurFX fx = new EntityCCBlurFX(world, dx+0.5, dy+0.5, dz+0.5, vx, vy, vz);
				int c = ReikaColorAPI.getModifiedHue(0xff0000, d*360/256);
				fx.setRapidExpand().setAlphaFading().setLife(20).forceIgnoreLimits().setScale(1.4F).setColor(c);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		int dx = x+dir.offsetX*RANGE;
		int dy = MathHelper.clamp_int(y+dir.offsetY*RANGE, 0, 255);
		int dz = z+dir.offsetZ*RANGE;
		if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
			EntityCCBlurFX fx = new EntityCCBlurFX(world, dx+0.5, dy+0.5, dz+0.5);
			fx.setIcon(ChromaIcons.BIGFLARE).setRapidExpand().setAlphaFading().setLife(20).forceIgnoreLimits().setScale(3);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doBlockParticles(World world, int x, int y, int z) {
		double r = 0.0625;
		double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, r);
		double dy = ReikaRandomHelper.getRandomPlusMinus(y+0.5, r);
		double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, r);
		double a1 = rand.nextDouble()*360;
		double a2 = rand.nextDouble()*360;
		EntityCCFloatingSeedsFX fx = new EntityCCFloatingSeedsFX(world, dx, dy, dz, a1, a2);
		fx.particleVelocity *= ReikaRandomHelper.getRandomBetween(0.25, 0.5);
		fx.angleVelocity *= 4;
		fx.freedom *= 1.6;
		fx.tolerance *= 1.2;
		int l = ReikaRandomHelper.getRandomBetween(25, 80);
		float s = (float)ReikaRandomHelper.getRandomBetween(3.5, 4.5);
		int c = ReikaColorAPI.mixColors(0xffffff, 0x000000, (float)ReikaRandomHelper.getRandomBetween(0.25, 0.75));
		if (isPrimary)
			c = ReikaColorAPI.mixColors(0x00ffff, 0x0000ff, (float)ReikaRandomHelper.getRandomBetween(0.25, 0.75));
		fx.setIcon(ChromaIcons.FADE_GENTLE).setColor(c).setRapidExpand().setAlphaFading().setScale(s).setLife(l);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private void findArea(World world, int x, int y, int z) {
		isPrimary = true;
		area = new ProportionedBlockBox(BlockBox.block(this));
		if (world.isRemote)
			return;
		connections.add(new Coordinate(this));
		TileEntityFloatingLandmark te = this.findParent(world, x, y, z);
		if (te != null && te.area != null) {
			this.slaveTo(te);
		}
		this.syncAllData(false);
	}

	private void slaveTo(TileEntityFloatingLandmark te) {
		isPrimary = false;
		primary = new Coordinate(te);
		te.addLink(this);
		te.isPrimary = true;
		area = te.area;
	}

	private void addLink(TileEntityFloatingLandmark te) {
		area = new ProportionedBlockBox(area.volume.addCoordinate(te.xCoord, te.yCoord, te.zCoord));
		connections.addAll(te.connections);
		connections.add(new Coordinate(te));
		te.connections = connections;
		/*
		for (Coordinate c : new HashSet<Coordinate>(connections)) {
			TileEntity te2 = c.getTileEntity(worldObj);
			if (te2 instanceof TileEntityFloatingLandmark) {
				TileEntityFloatingLandmark t2 = (TileEntityFloatingLandmark)te2;
				t2.connections.clear();
				t2.connections.addAll(connections);
			}
		}*/
		this.syncAllData(false);
	}

	public void reset(boolean propagate) {
		area = null;
		isPrimary = true;
		if (propagate) {
			for (Coordinate c : new HashSet<Coordinate>(connections)) {
				TileEntity te = c.getTileEntity(worldObj);
				if (te instanceof TileEntityFloatingLandmark) {
					((TileEntityFloatingLandmark)te).reset(false);
				}
			}
		}
		connections.clear();
		primary = null;
		this.syncAllData(false);
	}

	@SideOnly(Side.CLIENT)
	private void doAreaParticles(World world, int x, int y, int z) {
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
			EntityCCBlurFX fx = new EntityCCBlurFX(world, dx, dy, dz, vx, vy, vz);
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
			fx.setIcon(ico).setColor(c).setLife(l).setScale(s);
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
		ChromaSounds.USE.playSoundAtBlock(this);
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
		return this.isAnchored() && new Coordinate(x, y, z).getTaxicabDistanceTo(new Coordinate(this)) == 1;
	}

	public void breakBlock() {
		if (worldObj.isRemote)
			return;
		this.reset(true);
	}

	@Override
	public void onAdjacentUpdate(World world, int x, int y, int z, Block b) {
		if (!this.isAnchored() && !ReikaWorldHelper.checkForAdjSolidBlock(world, x, y, z)) {
			ChromaSounds.RIFT.playSoundAtBlock(this);
			this.delete();
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, this.getTile().getCraftedProduct());
		}
	}

	@Override
	public boolean isUnbreakable(EntityPlayer ep) {
		return this.isAnchored();
	}

}
