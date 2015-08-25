package Reika.ChromatiCraft.TileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityPylonTurboCharger extends TileEntityChromaticBase implements BreakAction {

	public static final int RITUAL_LENGTH = 1200;

	private int ritualTick = 0;
	private Location location;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PYLONTURBO;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!worldObj.isRemote) {
			if (ritualTick > 0) {
				this.doRitualTick(world, x, y, z);
				ritualTick--;
				if (ritualTick == 0) {
					this.completeRitual(world, x, y, z);
				}
			}
		}
		else {
			this.doParticles(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		double px = x+0.5;
		double py = y+0.4375;
		double pz = z+0.5;

		px = ReikaRandomHelper.getRandomPlusMinus(px, 0.03125);
		py = ReikaRandomHelper.getRandomPlusMinus(py, 0.03125);
		pz = ReikaRandomHelper.getRandomPlusMinus(pz, 0.03125);

		int l = 5+rand.nextInt(35);
		float s = (float)ReikaRandomHelper.getRandomPlusMinus(1, 0.5);

		int r = 192+rand.nextInt(64);
		int g = 192+rand.nextInt(64);
		int b = 192+rand.nextInt(64);

		EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(r, g, b).setScale(s).setGravity(0).setLife(l);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.findLocation(world, x, y, z);
	}

	private void findLocation(World world, int x, int y, int z) {
		for (int i = 0; i < Location.list.length; i++) {
			Location loc = Location.list[i];
			Coordinate c = loc.position.negate().offset(x, y, z);
			TileEntity te = c.getTileEntity(world);
			if (te instanceof TileEntityPylonTurboCharger) {
				if (((TileEntityPylonTurboCharger)te).getPylon(world, te.xCoord, te.yCoord, te.zCoord) != null) {
					location = loc;
					return;
				}
			}
		}
	}

	private void doRitualTick(World world, int x, int y, int z) {
		TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
		if (te == null) {
			this.failRitual(world, x, y, z);
		}
		else {

		}
	}

	private void completeRitual(World world, int x, int y, int z) {
		this.doCompleteParticles();
		ritualTick = 0;
		this.syncAllData(true);
	}

	private void failRitual(World world, int x, int y, int z) {
		ritualTick = 0;
		ChromaSounds.DISCHARGE.playSoundAtBlock(this);
		TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
		if (te != null) {
			te.drain(te.getColor(), te.getEnergy(te.getColor())*4/5);
		}
		this.doFailParticles(te != null);
		this.syncAllData(true);
	}

	private void doCompleteParticles() {
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PYLONTURBOCOMPLETE.ordinal(), this, 64);
	}

	private void doEvent(EventType type) {
		switch(type) {
			case FLASH:
				break;
			case BEAM:
				break;
		}

		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PYLONTURBOEVENT.ordinal(), this, 64, type.ordinal());
	}

	private void doFailParticles(boolean hasPylon) {
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PYLONTURBOFAIL.ordinal(), this, 64, hasPylon ? 1 : 0);
	}

	@SideOnly(Side.CLIENT)
	public void doFailParticlesClient(boolean hasPylon) {

	}

	@SideOnly(Side.CLIENT)
	public void doCompleteParticlesClient() {

	}

	@SideOnly(Side.CLIENT)
	public void doEventClient(int type) {
		EventType evt = EventType.list[type];
	}

	public int getTick() {
		return ritualTick;
	}

	public Location getLocation() {
		return location;
	}

	private TileEntityCrystalPylon getPylon(World world, int x, int y, int z) {
		int d = 8;
		for (int i = 1; i < d; i++) {
			int dy = y+i;
			if (!world.getBlock(x, dy, z).isAir(world, x, dy, z))
				return null;
		}
		TileEntity tile = world.getTileEntity(x, y+d, z);
		return tile instanceof TileEntityCrystalPylon ? (TileEntityCrystalPylon)tile : null;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean trigger() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		if (this.checkPylon(world, x, y, z)) {
			boolean hasAuxiliaries = this.checkForArrangement(world, x, y, z);
			if (hasAuxiliaries) {
				this.startRitual(world, x, y, z);
				return true;
			}
		}
		return false;
	}

	private boolean checkPylon(World world, int x, int y, int z) {
		TileEntityCrystalPylon te = this.getPylon(world, x, y, z);
		if (te != null) {
			return !te.isEnhanced() && te.getEnergy(te.getColor()) >= (TileEntityCrystalPylon.MAX_ENERGY*3/4) && te.canConduct();
		}
		return false;
	}

	private boolean checkForArrangement(World world, int x, int y, int z) {
		for (int i = 0; i < Location.list.length; i++) {
			Location loc = Location.list[i];
			Coordinate c = loc.position.offset(x, y, z);
			ChromaTiles t = ChromaTiles.getTileFromIDandMetadata(c.getBlock(world), c.getBlockMetadata(world));
			if (t != ChromaTiles.PYLONTURBO)
				return false;
			TileEntityPylonTurboCharger te = (TileEntityPylonTurboCharger)c.getTileEntity(world);
			if (!te.canFunction(world, c.xCoord, c.yCoord, c.zCoord))
				return false;
		}
		return true;
	}

	private boolean canFunction(World world, int x, int y, int z) {
		if (world.getBlock(x, y-1, z) == ChromaBlocks.PYLONSTRUCT.getBlockInstance() && world.getBlockMetadata(x, y-1, z) == 5) {
			if (world.getBlock(x, y-2, z) == ChromaBlocks.PYLONSTRUCT.getBlockInstance() && world.getBlockMetadata(x, y-2, z) == 2) {
				if (world.canBlockSeeTheSky(x, y+1, z)) {
					for (int i = -1; i <= 1; i++) {
						for (int k = -1; k <= 1; k++) {
							int dx = x+i;
							int dz = z+k;
							int dy = y+1;
							if (!world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz))
								return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	private void startRitual(World world, int x, int y, int z) {
		ritualTick = RITUAL_LENGTH;
		this.syncAllData(true);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("rtick", ritualTick);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		ritualTick = NBT.getInteger("rtick");
	}

	@Override
	public void breakBlock() {
		this.failRitual(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(12, 12, 12);
	}

	public static enum EventType {
		FLASH(),
		BEAM();

		private static final EventType[] list = values();
	}

	public static enum Location {

		N(0, -7),
		NE(5, -5),
		E(7, 0),
		SE(5, 5),
		S(0, 7),
		SW(-5, 5),
		W(-7, 0),
		NW(-5, -5);

		public final Coordinate position;

		private static final Location[] list = values();

		private Location(int x, int z) {
			position = new Coordinate(x, 2, z);
		}

		public Location getNext() {
			return this.ordinal() == list.length-1 ? list[0] : list[this.ordinal()+1];
		}

		public Coordinate getRelativePylonLocation() {
			return position.negate().offset(0, 8, 0);
		}
	}

}
