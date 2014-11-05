package Reika.ChromatiCraft.TileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalRepeater;
import Reika.ChromatiCraft.Magic.CrystalSource;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCompoundRepeater extends CrystalTransmitterBase implements CrystalRepeater {

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (world.isRemote && this.canConduct())
			this.particles(world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	private void particles(World world, int x, int y, int z) {
		if (this.getTicksExisted()%32 == 0) {
			double px = x+0.5;//rand.nextDouble();
			double py = y+0.5;//0.25+y+rand.nextDouble();
			double pz = z+0.5;//rand.nextDouble();
			CrystalElement e = CrystalElement.elements[(this.getTicksExisted()/32)%16];
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityRuneFX(world, px, py, pz, 0, 0, 0, e).setScale(5).setFading());
		}
	}

	@Override
	public void receiveElement(CrystalElement e, int amt) {

	}

	@Override
	public void onPathBroken() {

	}

	@Override
	public int getReceiveRange() {
		return 24;
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset( CrystalElement e) {
		return null;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return this.canConduct();
	}

	@Override
	public int maxThroughput() {
		return 1000;
	}

	@Override
	public boolean canConduct() {
		for (int i = -3; i <= 1; i++) {
			if (i != 0) {
				Block b = worldObj.getBlock(xCoord, yCoord+i, zCoord);
				int meta = worldObj.getBlockMetadata(xCoord, yCoord+i, zCoord);
				int m2 = i == -2 ? 2 : 0;
				if (b != ChromaBlocks.PYLONSTRUCT.getBlockInstance() || meta != m2) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int getSendRange() {
		return 16;
	}

	@Override
	public int getSignalDegradation() {
		return 5;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.COMPOUND;
	}

	@Override
	public boolean needsLineOfSight() {
		return true;
	}

	public boolean checkConnectivity() {
		CrystalElement c = this.getActiveColor();
		return c != null && CrystalNetworker.instance.checkConnectivity(c, worldObj, xCoord, yCoord, zCoord, this.getReceiveRange());
	}

	public CrystalElement getActiveColor() {
		return this.canConduct() ? CrystalElement.elements[worldObj.getBlockMetadata(xCoord, yCoord-1, zCoord)] : null;
	}

	public CrystalSource getEnergySource() {
		CrystalElement e = this.getActiveColor();
		return e != null ? CrystalNetworker.instance.getConnectivity(e, worldObj, xCoord, yCoord, zCoord, this.getReceiveRange()) : null;
	}

	public void onRelayPlayerCharge(EntityPlayer player, TileEntityCrystalPylon p) {
		if (!worldObj.isRemote) {
			if (!player.capabilities.isCreativeMode && !Chromabilities.PYLON.enabledOn(player) && rand.nextInt(20) == 0)
				p.attackEntityByProxy(player, this);
			CrystalNetworker.instance.makeRequest(this, this.getActiveColor(), 15000, this.getReceiveRange());
		}
	}

}
