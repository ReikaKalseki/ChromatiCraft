/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import java.util.UUID;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Node;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.API.Event.LumenWireToggleEvent;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CustomHitbox;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Rendering.FXCollection;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Interfaces.TileEntity.RedstoneTile;
import Reika.DragonAPI.Interfaces.TileEntity.SidePlacedTile;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityLumenWire extends TileEntityChromaticBase implements BreakAction, SidePlacedTile, CustomHitbox, RedstoneTile {

	private ForgeDirection facing;

	private StepTimer checkTimer = new StepTimer(5);

	private Coordinate connection;
	private UUID connectionUID;

	private int activeTick = 0;

	private CheckType check = CheckType.OWNER;

	public static final int MAX_LENGTH = 6;
	public static final int ACTIVATION_LENGTH = 30;

	@SideOnly(Side.CLIENT)
	public FXCollection particles;

	public TileEntityLumenWire() {
		if (this.getSide() == Side.CLIENT)
			particles = new FXCollection();
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LUMENWIRE;
	}

	public AxisAlignedBB getHitbox() {
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(xCoord+0.5, yCoord+0.5, zCoord+0.5, xCoord+0.5, yCoord+0.5, zCoord+0.5);
		switch(this.getFacing().getOpposite()) {
			case DOWN:
				box.minY = yCoord;
				box.maxY = yCoord+0.25;
				box.minX = xCoord+0.25;
				box.maxX = xCoord+0.75;
				box.minZ = zCoord+0.25;
				box.maxZ = zCoord+0.75;
				break;
			case UP:
				box.maxY = yCoord+1;
				box.minY = yCoord+0.75;
				box.minX = xCoord+0.25;
				box.maxX = xCoord+0.75;
				box.minZ = zCoord+0.25;
				box.maxZ = zCoord+0.75;
				break;
			case EAST:
				box.maxX = xCoord+1;
				box.minX = xCoord+0.75;
				box.minY = yCoord+0.25;
				box.maxY = yCoord+0.75;
				box.minZ = zCoord+0.25;
				box.maxZ = zCoord+0.75;
				break;
			case WEST:
				box.minX = xCoord;
				box.maxX = xCoord+0.25;
				box.minY = yCoord+0.25;
				box.maxY = yCoord+0.75;
				box.minZ = zCoord+0.25;
				box.maxZ = zCoord+0.75;
				break;
			case NORTH:
				box.minZ = zCoord;
				box.maxZ = zCoord+0.25;
				box.minY = yCoord+0.25;
				box.maxY = yCoord+0.75;
				box.minX = xCoord+0.25;
				box.maxX = xCoord+0.75;
				break;
			case SOUTH:
				box.maxZ = zCoord+1;
				box.minZ = zCoord+0.75;
				box.minY = yCoord+0.25;
				box.maxY = yCoord+0.75;
				box.minX = xCoord+0.25;
				box.maxX = xCoord+0.75;
				break;
			default:
				break;
		}
		return box;
	}

	public void cycleMode() {
		ChromaSounds.USE.playSoundAtBlock(this);
		check = check.next();
		if (connection != null) {
			((TileEntityLumenWire)connection.getTileEntity(worldObj)).check = check;
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.checkConnection(world, x, y, z);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			checkTimer.update();
			if (checkTimer.checkCap()) {
				this.checkConnection(world, x, y, z);
			}
		}
		else {
			particles.update();
		}

		if (connection != null) {
			if (activeTick > 0) {
				if (activeTick == 1) {
					this.toggle(world, x, y, z, false);
				}
				activeTick--;
			}

			if (world.isRemote) {
				this.doConnectionParticles(world, x, y, z);
				if (this.isActive()) {
					this.doActiveParticles(world, x, y, z);
				}
			}
			else {
				if (this.testBounds(world, x, y, z)) {
					this.toggle(world, x, y, z, true);
				}
			}
		}
		else {
			activeTick = 0;
		}
	}

	private void toggle(World world, int x, int y, int z, boolean on) {
		boolean change = on != this.isActive();
		if (change)
			ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.click", 1F, on ? 0.75F : 0.675F);
		if (on)
			activeTick = ACTIVATION_LENGTH;
		if (change) {
			this.causeUpdates(world, x, y, z);
			TileEntityLumenWire te = (TileEntityLumenWire)connection.getTileEntity(world);
			if (te != null)
				te.causeUpdates(world, connection.xCoord, connection.yCoord, connection.zCoord);
			if (ModList.OPENCOMPUTERS.isLoaded())
				this.sendOCActivation(world, x, y, z, on);
			MinecraftForge.EVENT_BUS.post(new LumenWireToggleEvent(world, x, y, z, connectionUID, on));
			this.syncAllData(false);
		}
	}

	private void causeUpdates(World world, int x, int y, int z) {
		ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);
		ReikaWorldHelper.causeAdjacentUpdates(world, x-this.getFacing().offsetX, y-this.getFacing().offsetY, z-this.getFacing().offsetZ);
	}

	@ModDependent(ModList.OPENCOMPUTERS)
	private void sendOCActivation(World world, int x, int y, int z, boolean on) {
		if (!world.isRemote) {
			String tag = "lumen_wire";
			Node n = this.node();
			Network.newPacket(n != null ? n.address() : "NULL", null, 500, new Object[]{tag, connectionUID.toString(), on});
			if (n != null)
				n.sendToReachable("computer.signal", tag, connectionUID.toString(), on);
		}
	}

	private boolean testBounds(World world, int x, int y, int z) {
		AxisAlignedBB box = this.getCheckBox(world, x, y, z);
		return check.check(this, world, box);
	}

	private AxisAlignedBB getCheckBox(World world, int x, int y, int z) {
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
		if (connection == null)
			return box;
		box.minX = Math.min(box.minX, connection.xCoord);
		box.minY = Math.min(box.minY, connection.yCoord);
		box.minZ = Math.min(box.minZ, connection.zCoord);
		box.maxX = Math.max(box.maxX, connection.xCoord+1);
		box.maxY = Math.max(box.maxY, connection.yCoord+1);
		box.maxZ = Math.max(box.maxZ, connection.zCoord+1);
		return box;
	}

	@SideOnly(Side.CLIENT)
	private void doConnectionParticles(World world, int x, int y, int z) {
		double dp = Minecraft.getMinecraft().thePlayer.getDistanceSq(x+0.5, y+0.5, z+0.5);
		int n = 1+rand.nextInt(3);
		float ds = 0;
		if (dp > 16384) {
			if (rand.nextInt(4) > 0)
				return;
		}
		else if (dp > 4096) { //64
			if (rand.nextInt(2) == 0)
				return;
			n = n/3;
			ds = 4;
		}
		else if (dp > 1024) { //32
			if (rand.nextInt(3) == 0)
				return;
			n = n/2;
			ds = 1.5F;
		}
		else if (dp > 256) { //16
			if (rand.nextInt(4) == 0)
				return;
			n = n*3/2;
			ds = 0.5F;
		}
		double in = 0.25;
		double h = 0.5;
		double inx = this.getFacing().offsetX == 0 ? 0.5 : in;
		double iny = this.getFacing().offsetY == 0 ? h : in;
		double inz = this.getFacing().offsetZ == 0 ? 0.5 : in;
		for (int i = 0; i < n; i++) {
			double d = rand.nextDouble();
			double dx = x+inx+d*(connection.xCoord+1-inx*2-x);
			double dy = y+iny+d*(connection.yCoord+1-iny*2-y);
			double dz = z+inz+d*(connection.zCoord+1-inz*2-z);
			int l = 10+rand.nextInt(10);
			float s = 1.5F+rand.nextFloat()*1F+ds;
			int p = rand.nextInt(4);
			/*
			EntityBlurFX fx = new EntityBlurFX(world, dx, dy, dz).setLife(l).setColor(check.renderColor).setScale(s);
			EntityBlurFX fx2 = new EntityBlurFX(world, dx, dy, dz).setLife(l).setColor(0xffffff).setScale(s/2);
			if (rand.nextBoolean()) {
				fx.setRapidExpand();
				fx2.setRapidExpand();
			}
			switch(p) {
				case 1:
					fx.setIcon(ChromaIcons.FLARE);
					fx2.setIcon(ChromaIcons.FLARE);
					break;
				case 2:
					fx.setIcon(ChromaIcons.CENTER);
					fx2.setIcon(ChromaIcons.CENTER);
					break;
				case 3:
					fx.setIcon(ChromaIcons.BIGFLARE);
					fx2.setIcon(ChromaIcons.BIGFLARE);
					break;
			}
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
			 */
			ChromaIcons ico = ChromaIcons.FADE;
			switch(p) {
				case 1:
					ico = ChromaIcons.FLARE;
					break;
				case 2:
					ico = ChromaIcons.CENTER;
					break;
				case 3:
					ico = ChromaIcons.BIGFLARE;
					break;
			}
			particles.addEffect(dx-x, dy-y, dz-z, ico.getIcon(), l, s, check.renderColor);
			particles.addEffect(dx-x, dy-y, dz-z, ico.getIcon(), l, s/2F, 0xffffff);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doActiveParticles(World world, int x, int y, int z) {
		double dp = Minecraft.getMinecraft().thePlayer.getDistanceSq(x+0.5, y+0.5, z+0.5);
		int n = 1+rand.nextInt(3);
		if (dp > 16384) {
			if (rand.nextInt(4) > 0)
				return;
		}
		else if (dp > 4096) { //64
			if (rand.nextInt(2) == 0)
				return;
			n = n/3;
		}
		else if (dp > 1024) { //32
			if (rand.nextInt(3) == 0)
				return;
			n = n/2;
		}
		else if (dp > 256) { //16
			if (rand.nextInt(4) == 0)
				return;
			n = n*3/2;
		}
		if (n <= 0)
			return;
		double in = 0.25;
		double h = 0.5;
		double inx = this.getFacing().offsetX == 0 ? 0.5 : in;
		double iny = this.getFacing().offsetY == 0 ? h : in;
		double inz = this.getFacing().offsetZ == 0 ? 0.5 : in;
		double d = rand.nextDouble();
		double dx = x+inx+d*(connection.xCoord+1-inx*2-x);
		double dy = y+iny+d*(connection.yCoord+1-iny*2-y);
		double dz = z+inz+d*(connection.zCoord+1-inz*2-z);
		int l = 10+rand.nextInt(10);
		float s = (float)(2.5+4*(0.5-Math.abs(0.5-d)));
		int p = rand.nextInt(4);
		int hue = ReikaRandomHelper.getRandomPlusMinus(ReikaColorAPI.getHue(check.renderColor), 60);
		int c = ReikaColorAPI.getModifiedHue(check.renderColor, hue);
		EntityBlurFX fx = new EntityBlurFX(world, dx, dy, dz).setLife(l).setColor(c).setScale(s).setIcon(ChromaIcons.ROSES_WHITE);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	public boolean isActive() {
		return activeTick > 0;
	}

	private void checkConnection(World world, int x, int y, int z) {
		this.disconnect(true);
		for (int i = 1; i <= MAX_LENGTH; i++) {
			int dx = x+this.getFacing().offsetX*i;
			int dy = y+this.getFacing().offsetY*i;
			int dz = z+this.getFacing().offsetZ*i;
			ChromaTiles t = ChromaTiles.getTile(world, dx, dy, dz);
			if (t == this.getTile()) {
				TileEntityLumenWire te = (TileEntityLumenWire)world.getTileEntity(dx, dy, dz);
				if (te.getFacing() == this.getFacing().getOpposite())
					this.connect(te, true);
			}
			else if (!world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz))
				return;
		}
	}

	private void connect(TileEntityLumenWire te, boolean callOther) {
		Coordinate old = connection;
		this.disconnect(true);
		connection = new Coordinate(te);
		if (!connection.equals(old)) {
			connectionUID = callOther ? UUID.randomUUID() : te.connectionUID;
			this.markDirty();
			this.syncAllData(false);
			if (callOther)
				te.connect(this, false);
		}
	}

	private void disconnect(boolean callOther) {
		if (connection != null && callOther) {
			TileEntityLumenWire te = (TileEntityLumenWire)connection.getTileEntity(worldObj);
			if (te != null)
				te.disconnect(false);
		}
		connection = null;
		connectionUID = null;
		this.markDirty();
		this.syncAllData(false);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		connection = NBT.hasKey("loc") ? Coordinate.readFromNBT("loc", NBT) : null;

		activeTick = NBT.getInteger("activet");

		facing = dirs[NBT.getInteger("dir")];

		check = CheckType.list[NBT.getInteger("ctype")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (connection != null)
			connection.writeToNBT("loc", NBT);

		NBT.setInteger("activet", activeTick);

		NBT.setInteger("dir", this.getFacing().ordinal());

		NBT.setInteger("ctype", check.ordinal());
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.UP;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void breakBlock() {
		this.disconnect(true);
	}

	@Override
	public void placeOnSide(int s) {
		facing = ForgeDirection.VALID_DIRECTIONS[s];
	}

	public static enum CheckType {
		ANY(0xffffff),
		OWNER(0x22aaff),
		PLAYER(0x0000ff),
		MOB(0xff0000),
		PASSIVE(0x00ff00),
		LIVING(0x00ffff),
		ITEM(0xffff00),
		PROJECTILE(0x00ffff);

		public final int renderColor;

		private static final CheckType[] list = values();

		private CheckType(int c) {
			renderColor = c;
		}

		private boolean check(TileEntityLumenWire te, World world, AxisAlignedBB box) {
			switch(this) {
				case ANY:
					return !world.getEntitiesWithinAABB(Entity.class, box).isEmpty();
				case LIVING:
					return !world.getEntitiesWithinAABB(EntityLivingBase.class, box).isEmpty();
				case ITEM:
					return !world.getEntitiesWithinAABBExcludingEntity(null, box, ReikaEntityHelper.itemOrXPSelector).isEmpty();
				case MOB:
					return !world.getEntitiesWithinAABB(EntityMob.class, box).isEmpty();
				case OWNER:
					for (EntityPlayer ep : te.getOwners(false)) {
						if (ep.boundingBox.intersectsWith(box))
							return true;
					}
					return false;
				case PASSIVE:
					return !world.getEntitiesWithinAABB(EntityAnimal.class, box).isEmpty();
				case PLAYER:
					return !world.getEntitiesWithinAABB(EntityPlayer.class, box).isEmpty();
				case PROJECTILE:
					return !world.getEntitiesWithinAABB(IProjectile.class, box).isEmpty();
				default:
					return false;
			}
		}

		private CheckType next() {
			return this.ordinal() == list.length-1 ? list[0] : list[this.ordinal()+1];
		}
	}

	@Override
	public boolean checkLocationValidity() {
		WorldLocation loc = this.getAdjacentLocation(this.getFacing().getOpposite());
		return loc.getBlock().isSideSolid(worldObj, loc.xCoord, loc.yCoord, loc.zCoord, this.getFacing());
	}

	public void drop() {
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getTile().getCraftedProduct());
		this.delete();
	}

	@Override
	public int getStrongPower(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return activeTick > 1 ? 15 : 0;
	}

	@Override
	public int getWeakPower(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return this.getStrongPower(world, x, y, z, side);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.getCheckBox(worldObj, xCoord, yCoord, zCoord);
	}

}
