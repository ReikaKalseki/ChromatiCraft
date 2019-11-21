package Reika.ChromatiCraft.TileEntity.Networking;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.CrystalPath;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityNetworkOptimizer extends CrystalReceiverBase implements MultiBlockChromaTile {

	public static final int REQUIRED_CHARGE = 12000;

	private final PathData[] data = new PathData[16];

	private int advanceDelay;
	private boolean isComplete;

	private boolean hasStructure;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (world.isRemote)
			return;
		if (!this.hasStructure())
			return;
		if (isComplete)
			return;
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if (energy.getValue(e) < REQUIRED_CHARGE) {
				if (this.getCooldown() == 0 && checkTimer.checkCap())
					this.requestEnergy(e, REQUIRED_CHARGE-energy.getValue(e));
				return;
			}
		}
		if (advanceDelay > 0) {
			advanceDelay--;
			return;
		}

		boolean flag = true;
		for (int i = 0; i < 16; i++) {
			if (data[i] != null) {
				flag = false;
				break;
			}
		}
		if (flag) {
			this.onOptimizationComplete(world, x, y, z);
			return;
		}

		int clr = (this.getTicksExisted()+System.identityHashCode(this))%16;
		//ReikaJavaLibrary.pConsole(clr+ " > "+(data[clr] != null));
		if (data[clr] != null) {
			if (this.runOptimizationStep(world, x, y, z, clr)) {
				data[clr] = null;
				//ReikaJavaLibrary.pConsole(CrystalElement.elements[clr]);
			}
		}
	}

	@Override
	protected int getCooldownLength() {
		return 100;
	}

	public void validateStructure() {
		hasStructure = ChromaStructures.OPTIMIZER.getArray(worldObj, xCoord, yCoord, zCoord).matchInWorld();
		//ReikaJavaLibrary.pConsole(hasStructure, Side.SERVER);
		this.syncAllData(false);
	}

	private boolean runOptimizationStep(World world, int x, int y, int z, int clr) {
		boolean ret = data[clr].optimizeStep();
		if (ret) {
			for (int i = 0; i < 24; i++) {
				ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.OPTIMIZE.ordinal(), this, 48, clr);
				ChromaSounds.CAST.playSoundAtBlock(world, x, y, z, 1, 0.5F);
			}
			advanceDelay = 60;
		}
		return ret;
	}

	@SideOnly(Side.CLIENT)
	public static void runOptimizationStepFX(World world, int x, int y, int z, int clr) {
		double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.0625);
		double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.0625);
		double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.0625);
		EntityBlurFX fx = new EntityBlurFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), vx, vy, vz);
		fx.setColor(CrystalElement.elements[clr].getColor()).setColliding();
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private void onOptimizationComplete(World world, int x, int y, int z) {
		isComplete = true;
		ChromaSounds.NETWORKOPT.playSoundAtBlockNoAttenuation(this, 1, 1, 256);
		this.syncAllData(true);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.validateStructure();
		for (int i = 0; i < 16; i++) {
			data[i] = new PathData();
			data[i].pathData.addAll(CrystalNetworker.instance.getAllCachedPaths(CrystalElement.elements[i]));
		}
	}

	@Override
	protected void onReceiveEnergy(CrystalElement e, int amt) {
		float f = CrystalMusicManager.instance.getScaledDing(e, 0);
		if (this.getTicksExisted()%(Math.max(1, (int)(3F/(f*f)))) == 0) {
			ChromaSounds.NETWORKOPTCHARGE.playSoundAtBlock(this, 1, f);
		}
	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return true;
	}

	@Override
	public int maxThroughput() {
		return 50;
	}

	@Override
	public boolean canConduct() {
		return this.hasStructure();
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return REQUIRED_CHARGE;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.OPTIMIZER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("struct");
		isComplete = NBT.getBoolean("complete");
		advanceDelay = NBT.getInteger("delay");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);
		NBT.setBoolean("complete", isComplete);
		NBT.setInteger("delay", advanceDelay);
	}

	public boolean hasStructure() {
		return hasStructure;
	}

	private static class PathData {

		private final ArrayList<CrystalPath> pathData = new ArrayList();

		public boolean optimizeStep() {
			if (pathData.isEmpty())
				return true;
			CrystalPath p = pathData.get(0);
			CrystalPath p2 = p.optimize(1);
			if (p2 != null) {
				pathData.set(0, p2);
			}
			if (p2.isOptimized()) {
				CrystalNetworker.instance.replacePath(p2);
				pathData.remove(0);
			}
			return pathData.isEmpty();
		}

	}

}
