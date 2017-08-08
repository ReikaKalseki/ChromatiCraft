/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Gravity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.Recharger;
import Reika.ChromatiCraft.Auxiliary.Recharger.RechargeWaiter;
import Reika.ChromatiCraft.Auxiliary.Interfaces.Linkable;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Entity.EntityLumaBurst;
import Reika.ChromatiCraft.Magic.ElementMixer;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.PulsingRadius;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.World.Dimension.Structure.GravityPuzzleGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockGravityTile extends BlockDimensionStructureTile {

	private static IIcon overlay;
	private static IIcon underlay;

	public BlockGravityTile(Material mat) {
		super(mat);

		//this.setLightLevel(1);
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
	public IIcon getIcon(int s, int meta) {
		return s == 1 ? blockIcon : Blocks.iron_block.getIcon(s, meta);
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return s == 1 ? underlay : Blocks.iron_block.getIcon(s, iba.getBlockMetadata(x, y, z));
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dimstruct/gravity_sum");
		underlay = ico.registerIcon("chromaticraft:dimstruct/gravity_underlay");
		overlay = ico.registerIcon("chromaticraft:dimstruct/gravity_overlay");
	}

	public static IIcon getOverlay() {
		return overlay;
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
		switch(GravityTiles.list[meta]) {
			case TARGET:
				return new GravityTarget();
			case RIFT:
				return new GravityWarp();
			case REROUTE:
			case SPLITTER:
				return new RerouteTile();
			case EMITTER:
				return new GravityEmitter();
			default:
				return new GravityTile();
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s].getOpposite();
		if (!tryMove(world, x, y, z, dir)) {
			if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
				GravityTile te = (GravityTile)world.getTileEntity(x, y, z);
				GravityTiles type = GravityTiles.list[world.getBlockMetadata(x, y, z)];
				ItemStack is = ep.getCurrentEquippedItem();
				if (ChromaItems.SHARD.matchWith(is)) {
					te.color = CrystalElement.elements[is.getItemDamage()%16];
				}
				else if (is != null && is.getItem() == Items.ender_pearl && te instanceof GravityTarget) {
					((GravityTarget)te).isDormant = false;
				}
				else if (is != null && is.getItem() == Items.glowstone_dust && te instanceof GravityTarget) {
					((GravityTarget)te).collectedBursts = 0;
				}
				else if (is != null && is.getItem() == Items.beef) {
					te.gravityFactor -= 0.03125;
				}
				else if (is != null && is.getItem() == Items.cooked_beef) {
					te.gravityFactor += 0.03125;
				}
				else if (is != null && is.getItem() == Items.porkchop) {
					te.gravityRangeFactor -= 0.03125;
					double r = te.gravityRangeFactor*te.getTileType().gravityRange;
					if (r > 0)
						te.gravityDisplay = new PulsingRadius(r, 0.125);
				}
				else if (is != null && is.getItem() == Items.cooked_porkchop) {
					te.gravityRangeFactor += 0.03125;
					double r = te.gravityRangeFactor*te.getTileType().gravityRange;
					if (r > 0)
						te.gravityDisplay = new PulsingRadius(r, 0.125);
				}
				else if (is != null && is.getItem() == Items.redstone && te instanceof GravityEmitter) {
					((GravityEmitter)te).fireDuration += 10;
				}
				else if (is != null && is.getItem() == Items.gunpowder && te instanceof GravityEmitter) {
					((GravityEmitter)te).fireDuration -= 10;
				}
				else if (ChromaItems.LINKTOOL.matchWith(is)) {
					//is.getItem().onItemUse(is, ep, world, x, y, z, 0, 0, 0, 0);
					return false;
				}
				else if (!type.isOmniDirectional()) {
					te.facing = te.facing.getRotation(true);
				}
				world.markBlockForUpdate(x, y, z);
			}
		}
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.click", 0.75F, 1.25F);
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		GravityTile te = (GravityTile)world.getTileEntity(x, y, z);
		if (te instanceof GravityEmitter) {
			if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
				((GravityEmitter)te).startFiring();
			}
		}
	}

	public static boolean tryMove(World world, int x, int y, int z, ForgeDirection dir) {
		if (canMove(world, x, y, z, dir)) {
			move(world, x, y, z, dir);
			return true;
		}
		return false;
	}

	private static boolean canMove(World world, int x, int y, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY-1;
		int dz = z+dir.offsetZ;
		return world.getBlock(dx, dy, dz) instanceof BlockStructureShield && world.getBlockMetadata(dx, dy, dz)%8 == BlockType.CLOAK.ordinal();
	}

	private static void move(World world, int x, int y, int z, ForgeDirection dir) {
		int meta = world.getBlockMetadata(x, y, z);
		GravityTile g1 = (GravityTile)world.getTileEntity(x, y, z);
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		world.setBlock(dx, dy, dz, ChromaBlocks.GRAVITY.getBlockInstance(), meta, 3);
		GravityTile g2 = (GravityTile)world.getTileEntity(dx, dy, dz);
		g2.copyFrom(g1);
		world.setBlock(x, y, z, Blocks.air);
	}

	public static enum GravityTiles {

		EMITTER(0, 0, ChromaIcons.FAN),
		TARGET(1, 0.125, null),
		REROUTE(1, 0.5, ChromaIcons.SIDEDFLOW),
		SPLITTER(1, 0.5, ChromaIcons.SIDEDFLOWBI),
		TINTER(0, 0, null),
		ATTRACTOR(1.5, 0.125, ChromaIcons.BLACKHOLE),
		REPULSOR(1.5, -0.125, ChromaIcons.CONCENTRIC2),
		RIFT(0, 0, ChromaIcons.HOLE);

		public static final GravityTiles[] list = values();

		public final double gravityRange;
		public final double gravityStrength;
		public final ChromaIcons icon;

		private GravityTiles(double r, double g, ChromaIcons ico) {
			gravityRange = r;
			gravityStrength = g;
			icon = ico;
		}

		public boolean onPulse(World world, int x, int y, int z, EntityLumaBurst e) {
			GravityTile te = (GravityTile)world.getTileEntity(x, y, z);
			switch(this) {
				case EMITTER:
					return true;
				case REROUTE:
				case SPLITTER:
					//te.fire(e.getColor());
					((RerouteTile)te).collect(e);
					return true;
				case TARGET:
					return e.getColor() == te.color && !((GravityTarget)te).isDormant;
				case TINTER:
					if (world.isRemote)
						return false;
					ForgeDirection dir = ReikaDirectionHelper.getImpactedSide(world, x, y, z, e);
					//ReikaJavaLibrary.pConsole(dir);
					if (dir != null) {
						int dx = Math.abs(dir.offsetX) != 0 ? -1 : 1;
						int dz = Math.abs(dir.offsetZ) != 0 ? -1 : 1;
						e.motionX *= dx;
						e.motionZ *= dz;
						e.velocityChanged = true;
						e.setColor(te.color);
						//Entity eb = te.fireCopy(e.getColor(), e);
						//eb.setPosition(e.posX, e.posY+2, e.posZ);
					}
					else {
						return true;
					}
					return false;//true;
				case ATTRACTOR:
				case REPULSOR:
					return false;
				case RIFT:
					te.onImpact(this, new Coordinate(te), e, 0, e.getDistanceSq(x+0.5, y+0.5, z+0.5));
					return true;
				default:
					return false;
			}
		}

		public boolean isOmniDirectional() {
			return this != EMITTER && this != REROUTE && this != SPLITTER;
		}

		public boolean isOmniColor() {
			return this != EMITTER && this != TARGET;
		}

	}

	public static class RerouteTile extends GravityTile {

		private final EnumMap<CrystalElement, Integer> collection = new EnumMap(CrystalElement.class);
		private int cooldown;

		@Override
		public void updateEntity() {
			super.updateEntity();

			if (cooldown > 0) {
				cooldown--;
				if (cooldown == 0) {
					for (CrystalElement e : collection.keySet()) {
						int num = collection.get(e);
						for (int i = 0; i < num; i++) {
							this.fire(e);
						}
					}
					collection.clear();
				}
			}
		}

		private void collect(EntityLumaBurst b) {
			ArrayList<CrystalElement> li = new ArrayList(collection.keySet());
			if (li.isEmpty()) {
				ReikaJavaLibrary.addToIntMap(collection, b.getColor(), 1);
			}
			else {
				for (CrystalElement e : li) {
					CrystalElement mix = ElementMixer.instance.getMix(e, b.getColor());
					if (mix != null) {
						ReikaJavaLibrary.subtractFromIntMap(collection, e, 1);
						ReikaJavaLibrary.addToIntMap(collection, mix, 1);
					}
					else {
						ReikaJavaLibrary.addToIntMap(collection, b.getColor(), 1);
					}
				}
			}
			cooldown = 1;
		}

	}

	public static class GravityWarp extends GravityTile implements Linkable {

		private Coordinate otherLocation;
		public LightningBolt linkRender;

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);

			otherLocation = Coordinate.readFromNBT("loc", tag);
			if (otherLocation != null) {
				DecimalPosition p2 = new DecimalPosition(otherLocation.offset(new Coordinate(this).negate())).offset(-0.5, -0.5, -0.5);
				linkRender = new LightningBolt(new DecimalPosition(0, 0, 0), p2, 8);
				linkRender.variance *= 0.375;
				linkRender.velocity *= 0.0625*1.5;
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);

			if (otherLocation != null)
				otherLocation.writeToNBT("loc", tag);
		}

		@Override
		protected void onImpact(GravityTiles type, Coordinate c, EntityLumaBurst e, double r, double d) {
			if (otherLocation != null && d <= 0.5*0.5 && !this.isSpawnLocked(c, e)) {
				((GravityWarp)otherLocation.getTileEntity(worldObj)).fireCopy(e.getColor(), e);
			}
			else {
				super.onImpact(type, c, e, r, d);
			}
		}

		public void linkTo(GravityWarp w) {
			otherLocation = new Coordinate(w);
			w.otherLocation = new Coordinate(this);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			w.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public void reset() {
			otherLocation = null;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public void resetOther() {
			if (otherLocation != null) {
				TileEntity te = otherLocation.getTileEntity(worldObj);
				if (te instanceof GravityWarp) {
					((GravityWarp)te).otherLocation = null;
					te.worldObj.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
				}
			}
		}

		public Coordinate getLink() {
			return otherLocation;
		}

		@Override
		public boolean connectTo(World world, int x, int y, int z) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof GravityWarp) {
				this.linkTo((GravityWarp)te);
				return true;
			}
			return false;
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
			if (tag.hasKey("timer")) {
				timer = new Recharger(8, 16, this);
				timer.readFromNBT(tag.getCompoundTag("timer"), this);
			}
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
						if (collectedBursts == requiredBursts)  {
							this.trigger(true);
						}
						if (this.getFillFraction() > 1.5 && !worldObj.isRemote) {
							this.overload();
						}
					}
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
				else { //bounce
					ReikaPhysicsHelper.reflectEntitySpherical(xCoord+0.5, yCoord+0.5, zCoord+0.5, e);
				}
			}
			else {
				super.onImpact(type, c, e, r, d);
			}
		}

		private void trigger(boolean set) {
			if (set) {
				if (worldObj.isRemote) {
					this.doTriggerClientside();
				}
				ChromaSounds.CAST.playSoundAtBlock(this);
			}
			else {
				ChromaSounds.POWERDOWN.playSoundAtBlock(this, 1, 0.67F);
			}
		}

		@SideOnly(Side.CLIENT)
		private void doTriggerClientside() {
			for (int i = 0; i < 16; i++) {
				double px = ReikaRandomHelper.getRandomPlusMinus(xCoord+0.5, 0.4375);
				double pz = ReikaRandomHelper.getRandomPlusMinus(zCoord+0.5, 0.4375);
				double py = yCoord+0.25;
				int l = ReikaRandomHelper.getRandomBetween(20, 40);
				float s = 1+worldObj.rand.nextFloat()*0.67F;
				float g = -(float)ReikaRandomHelper.getRandomBetween(0.03125, 0.125);
				EntityFX fx = new EntityBlurFX(worldObj, px, py, pz).setColor(color.getColor()).setGravity(g).setScale(s).setLife(l);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
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
			this.trigger(false);
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

	public static class GravityEmitter extends GravityTile {

		private int fireDuration = 180;
		private int firePower = 1;
		private int fireTimer;

		@Override
		public void updateEntity() {
			super.updateEntity();

			if (fireTimer > 0) {
				for (int i = 0; i < firePower; i++) {
					EntityLumaBurst e = this.fire(color);
				}
				fireTimer--;
			}
		}

		public void startFiring() {
			if (fireTimer == 0) {
				fireTimer = fireDuration;
			}
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);

			firePower = tag.getInteger("pwr");
			fireDuration = tag.getInteger("dur");
			fireTimer = tag.getInteger("timer");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);

			tag.setInteger("pwr", firePower);
			tag.setInteger("dur", fireDuration);
			tag.setInteger("timer", fireTimer);
		}
	}

	public static class GravityTile extends StructureBlockTile<GravityPuzzleGenerator> {

		protected String level = "none";
		protected CrystalElement color = CrystalElement.WHITE;
		protected CubeDirections facing = CubeDirections.NORTH;
		public int renderAlpha = 255;

		private double gravityFactor = 1;
		private double gravityRangeFactor = 1;

		public PulsingRadius gravityDisplay;

		@Override
		public void updateEntity() {
			GravityTiles type = this.getTileType();
			double r = type.gravityRange*gravityRangeFactor;
			if (gravityDisplay == null && r > 0 && worldObj.isRemote)
				gravityDisplay = new PulsingRadius(r, 0.125);
			if (worldObj.isRemote)
				;//return;
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
		}

		protected void onImpact(GravityTiles type, Coordinate c, EntityLumaBurst e, double r, double d) {
			if (d <= r*r) {
				if (this.canAttract(type, c, e)) {
					double dx = e.posX-xCoord-0.5;
					double dy = e.posY-yCoord-0.5;
					double dz = e.posZ-zCoord-0.5;
					double v = 0.03125*4*type.gravityStrength*gravityFactor;
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
			return !this.isSpawnLocked(c, e);
		}

		protected final boolean isSpawnLocked(Coordinate c, EntityLumaBurst e) {
			return !e.isOutOfSpawnZone() && c.equals(e.getSpawnLocation());
		}

		protected EntityLumaBurst fire(CrystalElement c) {
			CubeDirections dir = facing;
			if (this.getTileType() == GravityTiles.SPLITTER && worldObj.rand.nextBoolean())
				dir = dir.getOpposite();
			EntityLumaBurst e = new EntityLumaBurst(worldObj, xCoord, yCoord, zCoord, dir, c);
			if (!worldObj.isRemote) {
				worldObj.spawnEntityInWorld(e);
			}
			return e;
		}

		protected EntityLumaBurst fireFree(CrystalElement c, double ang) {
			EntityLumaBurst e = new EntityLumaBurst(worldObj, xCoord, yCoord, zCoord, ang, c);
			if (!worldObj.isRemote) {
				worldObj.spawnEntityInWorld(e);
			}
			return e;
		}

		protected EntityLumaBurst fireCopy(CrystalElement c, EntityLumaBurst e2) {
			EntityLumaBurst e = new EntityLumaBurst(worldObj, xCoord, yCoord, zCoord, 0, c);
			e.copyFrom(e2);
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

			if (tag.hasKey("gfactor"))
				gravityFactor = tag.getDouble("gfactor");
			if (tag.hasKey("rfactor"))
				gravityRangeFactor = tag.getDouble("rfactor");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);


			tag.setInteger("color", color.ordinal());
			tag.setInteger("dir", facing.ordinal());

			tag.setString("level", level);
			tag.setInteger("alpha", renderAlpha);

			tag.setDouble("gfactor", gravityFactor);
			tag.setDouble("rfactor", gravityRangeFactor);
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
