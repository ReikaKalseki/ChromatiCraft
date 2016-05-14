package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Recharger;
import Reika.ChromatiCraft.Auxiliary.Recharger.RechargeWaiter;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Entity.EntityLumaBurst;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.GravityPuzzleGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;


public class BlockGravityTile extends BlockContainer {

	public BlockGravityTile(Material mat) {
		super(mat);

		this.setResistance(60000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderBlockPass() {
		return 0;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		//LaserEffectorRenderer.renderPass = pass;
		return pass <= 0;
	}

	@Override
	public int getRenderType() {
		return 0;//ChromatiCraft.proxy.lasereffectRender;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		this.setBlockBounds(0, 0, 0, 1, 0.4F, 1);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		if (meta == GravityTiles.TARGET.ordinal())
			return new GravityTarget();
		return new GravityTile();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s].getOpposite();
		if (this.canMove(world, x, y, z, dir))
			this.move(world, x, y, z, dir);
		else if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
			GravityTile te = (GravityTile)world.getTileEntity(x, y, z);
			GravityTiles type = GravityTiles.list[world.getBlockMetadata(x, y, z)];
			ItemStack is = ep.getCurrentEquippedItem();
			if (ChromaItems.SHARD.matchWith(is)) {
				te.color = CrystalElement.elements[is.getItemDamage()%16];
			}
			else if (is != null && is.getItem() == Items.ender_pearl && te instanceof GravityTarget) {
				((GravityTarget)te).isDormant = false;
			}
			else if (!type.isOmniDirectional()) {
				te.facing = te.facing.getRotation(true);
			}
			world.markBlockForUpdate(x, y, z);
		}
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.click", 0.75F, 1.25F);
		return true;
	}

	private boolean canMove(World world, int x, int y, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY-1;
		int dz = z+dir.offsetZ;
		return world.getBlock(dx, dy, dz) instanceof BlockStructureShield && world.getBlockMetadata(dx, dy, dz)%8 == BlockType.CLOAK.ordinal();
	}

	private void move(World world, int x, int y, int z, ForgeDirection dir) {
		int meta = world.getBlockMetadata(x, y, z);
		GravityTile g1 = (GravityTile)world.getTileEntity(x, y, z);
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		world.setBlock(dx, dy, dz, this, meta, 3);
		GravityTile g2 = (GravityTile)world.getTileEntity(dx, dy, dz);
		g2.copyFrom(g1);
		world.setBlock(x, y, z, Blocks.air);
	}

	public static enum GravityTiles {

		EMITTER(0, 0),
		TARGET(0.75, 0.125),
		REROUTE(1, 0.5),
		SPLITTER(1.25, 0.5),
		TINTER(0, 0),
		ATTRACTOR(1.5, 0.25),
		REPULSOR(1.5, -0.25);

		public static final GravityTiles[] list = values();

		public final double gravityRange;
		public final double gravityStrength;

		private GravityTiles(double r, double g) {
			gravityRange = r;
			gravityStrength = g;
		}

		public boolean onPulse(World world, int x, int y, int z, EntityLumaBurst e) {
			GravityTile te = (GravityTile)world.getTileEntity(x, y, z);
			switch(this) {
				case EMITTER:
					return true;
				case REROUTE:
					te.fire(e.getColor());
					return true;
				case SPLITTER:
					return true;
				case TARGET:
					return e.getColor() == te.color && !((GravityTarget)te).isDormant;
				case TINTER:
					return false;
				case ATTRACTOR:
					return false;
				default:
					return false;
			}
		}

		public boolean isOmniDirectional() {
			return this == TINTER || this == ATTRACTOR;
		}

		public boolean isOmniColor() {
			return this != EMITTER && this != TARGET;
		}

	}

	public static class GravityTarget extends GravityTile implements RechargeWaiter {

		public static final int DORMANT_DURATION = 40;
		public Recharger timer;

		private int requiredBursts = 100;
		private int collectedBursts;
		private boolean isDormant;

		public float getFillFraction() {
			return (float)collectedBursts/requiredBursts;
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);

			collectedBursts = tag.getInteger("num");
			requiredBursts = tag.getInteger("req");
			isDormant = tag.getBoolean("dormant");
			if (timer != null)
				timer.readFromNBT(tag.getCompoundTag("timer"), this);
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);

			tag.setInteger("num", collectedBursts);
			tag.setInteger("req", requiredBursts);
			tag.setBoolean("dormant", isDormant);
			if (timer != null) {
				NBTTagCompound dat = new NBTTagCompound();
				timer.writeToNBT(dat);
				tag.setTag("timer", dat);
			}
		}

		@Override
		public void updateEntity() {
			super.updateEntity();
			if (timer != null) {
				timer.tick();
				renderAlpha = Math.max(0, renderAlpha-32);
			}
			else {
				renderAlpha = Math.min(255, renderAlpha+32);
			}
		}

		@Override
		protected void onImpact(GravityTiles type, Coordinate c, EntityLumaBurst e, double r, double d) {
			if (isDormant) {
				return;
			}
			if (d <= 0.625*0.625) {
				if (e.getColor() == color) {
					collectedBursts++;
					if (collectedBursts >= requiredBursts) {
						if (this.getFillFraction() > 1.5) {
							this.overload();
						}
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					}
				}
				else { //bounce
					ReikaPhysicsHelper.reflectEntitySpherical(xCoord+0.5, yCoord+0.5, zCoord+0.5, e);
				}
			}
			else {
				super.onImpact(type, c, e, r, d);
			}
		}

		private void overload() {
			timer = new Recharger(8, 16, this);
			isDormant = true;
			while (collectedBursts > 0) {
				EntityLumaBurst e = this.fire(color);
				e.setRandomDirection(true);
				collectedBursts -= 1;
				if (collectedBursts < 0)
					collectedBursts = 0;
			}
			ChromaSounds.POWERDOWN.playSoundAtBlock(this, 1, 0.67F);
		}

		@Override
		public void onSegmentComplete() {
			ChromaSounds.ITEMSTAND.playSoundAtBlock(this, 0.4F, 1.8F);
		}

		@Override
		public void onChargingComplete() {
			isDormant = false;
			ChromaSounds.ITEMSTAND.playSoundAtBlock(this, 0.8F, 2F);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			timer = null;
		}

	}

	public static class GravityTile extends StructureBlockTile<GravityPuzzleGenerator> {

		protected String level = "none";
		protected CrystalElement color = CrystalElement.WHITE;
		protected CubeDirections facing = CubeDirections.NORTH;
		public int renderAlpha = 255;

		@Override
		public void updateEntity() {
			if (worldObj.isRemote)
				;//return;
			GravityTiles type = this.getTileType();
			double r = type.gravityRange*2;
			if (r > 0) {
				Coordinate c = new Coordinate(this);
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(r, r, r);
				List<EntityLumaBurst> li = worldObj.getEntitiesWithinAABB(EntityLumaBurst.class, box);
				for (EntityLumaBurst e : li) {
					if (c.equals(e.getSpawnLocation()))
						e.resetSpawnTimer();
					double d = e.getDistanceSq(xCoord+0.5, yCoord+0.5, zCoord+0.5);
					this.onImpact(type, c, e, r, d);
				}
			}
			if (type == GravityTiles.EMITTER) {
				EntityLumaBurst e = this.fire(color);
			}
		}

		protected void onImpact(GravityTiles type, Coordinate c, EntityLumaBurst e, double r, double d) {
			if (d <= r*r) {
				if (this.canAttract(type, c, e)) {
					double dx = e.posX-xCoord-0.5;
					double dy = e.posY-yCoord-0.5;
					double dz = e.posZ-zCoord-0.5;
					double v = 0.03125/4;
					e.motionX -= dx*v/d;
					e.motionY -= dy*v/d;
					e.motionZ -= dz*v/d;
					e.velocityChanged = true;
				}
			}
		}

		private boolean canAttract(GravityTiles type, Coordinate c, EntityLumaBurst e) {
			if (!type.isOmniColor() && e.getColor() != color)
				return false;
			return e.isOutOfSpawnZone() || !c.equals(e.getSpawnLocation());
		}

		protected EntityLumaBurst fire(CrystalElement c) {
			EntityLumaBurst e = new EntityLumaBurst(worldObj, xCoord, yCoord, zCoord, facing, c);
			if (!worldObj.isRemote) {
				worldObj.spawnEntityInWorld(e);
			}
			return e;
		}

		public GravityTiles getTileType() {
			return GravityTiles.list[this.getBlockMetadata()];
		}

		protected void copyFrom(GravityTile g) {
			super.copyFrom(g);
			level = g.level;
			color = g.color;
			facing = g.facing;
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			color = CrystalElement.elements[tag.getInteger("color")];
			facing = CubeDirections.list[tag.getInteger("dir")];

			level = tag.getString("level");
			renderAlpha = 255;//tag.getInteger("alpha");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);


			tag.setInteger("color", color.ordinal());
			tag.setInteger("dir", facing.ordinal());

			tag.setString("level", level);
			tag.setInteger("alpha", renderAlpha);
		}

		public CubeDirections getFacing() {
			return facing;
		}

		public CrystalElement getColor() {
			return color;
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.GRAVITY;
		}

	}

}
