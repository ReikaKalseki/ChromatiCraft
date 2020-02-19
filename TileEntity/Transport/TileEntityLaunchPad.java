package Reika.ChromatiCraft.TileEntity.Transport;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.TimerMap;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityLaunchPad extends TileEntityChromaticBase implements MultiBlockChromaTile {

	public static final int CHARGE_DURATION = 20;

	private int charge;
	private boolean structure;
	private boolean enhanced;

	private TimerMap<Entity> currentLaunches = new TimerMap();

	public float getChargeFraction() {
		return charge/(float)CHARGE_DURATION;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LAUNCHPAD;
	}

	public boolean hasStructure() {
		return structure;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote && this.hasStructure()) {
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x-0.75, y+0.9375, z-0.75, x+1.75, y+2.5, z+1.75);
			List<Entity> li = world.getEntitiesWithinAABB(Entity.class, box);
			for (Entity e : li) {
				e.fallDistance = 0;
			}

			currentLaunches.tick();
			for (Entity e : currentLaunches.keySet()) {
				this.flingEntity(e, false);
			}

			boolean flag = charge > 0;
			box = AxisAlignedBB.getBoundingBox(x-1, y+0.9375, z-1, x+2, y+1.0625, z+2);
			li = world.getEntitiesWithinAABB(Entity.class, box);
			Iterator<Entity> it = li.iterator();
			while (it.hasNext()) {
				Entity e = it.next();
				if (e instanceof EntityItem || e instanceof EntityXPOrb)
					it.remove();
			}
			if (li.size() == 1 && li.get(0).isSneaking())
				li.clear();
			if (!li.isEmpty()) {
				if (charge == CHARGE_DURATION) {
					for (Entity e : li) {
						this.flingEntity(e, true);
					}
					ChromaSounds.KILLAURA.playSoundAtBlockNoAttenuation(this, 1, 0.5F, 90);
					ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.LAUNCHFIRE.ordinal(), this, 90);
				}
				else {
					charge++;
					ChromaSounds.KILLAURA_CHARGE.playSoundAtBlock(this, 1, 0.5F+this.getChargeFraction()*0.5F);
				}
			}
			else if (charge > 0) {
				charge = Math.max(0, charge-2);
			}
			if (flag || charge > 0) {
				this.syncAllData(false);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void doFX(World world, int x, int y, int z) {
		for (int i = 0; i < 90; i++) {
			double dx = ReikaRandomHelper.getRandomBetween(x-1D, x+2);
			double dz = ReikaRandomHelper.getRandomBetween(z-1D, z+2);
			double dy = y+1.0625;
			double v = ReikaRandomHelper.getRandomBetween(2D, 5);
			EntityBlurFX fx = new EntityBlurFX(world, dx, dy, dz, 0, v, 0);
			int l = ReikaRandomHelper.getRandomBetween(10, 60);
			float s = (float)ReikaRandomHelper.getRandomBetween(0.5, 1.2);
			EntityBlurFX fx2 = new EntityBlurFX(world, dx, dy, dz, 0, v, 0);
			fx.setLife(l).setScale(s).setColor(0x00ff00).setAlphaFading().setRapidExpand().setIcon(ChromaIcons.CENTER).setColliding();
			fx2.setLife(l).setScale(s*0.4F).setColor(0xffffff).setAlphaFading().setRapidExpand().setIcon(ChromaIcons.CENTER).setColliding().lockTo(fx2);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	private void flingEntity(Entity e, boolean cache) {
		float v = this.getSpeedFactor();
		if (e instanceof EntityPlayer) {
			e.motionY = Math.max(e.motionY, (3.5+rand.nextDouble()*0.5)*v);
			e.velocityChanged = true;
		}
		else {
			e.motionY = 1.8*v;
			e.velocityChanged = true;
		}
		e.fallDistance = -100;

		if (cache && this.isEnhanced()) {
			currentLaunches.put(e, ReikaRandomHelper.getRandomBetween(12, 18));
		}
	}

	private float getSpeedFactor() {
		float base = this.isEnhanced() ? 2.5F : 1;
		return base*Math.max(0.25F, ChromaOptions.LAUNCHPOWER.getFloat());
	}

	public boolean isEnhanced() {
		return enhanced;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.checkForStructure(world, x, y, z);
	}

	private void checkForStructure(World world, int x, int y, int z) {
		ChromaStructureBase struct = ChromaStructures.LAUNCHPAD.getStructure();
		struct.resetToDefaults();
		enhanced = false;
		structure = struct.getArray(world, x, y, z).matchInWorld();
		if (structure) {
			enhanced = this.checkForEnhanced(world, x, y, z);
		}
	}

	private boolean checkForEnhanced(World world, int x, int y, int z) {
		for (int i = -2; i <= 2; i++) {
			for (int k = -2; k <= 2; k++) {
				int dx = x+i;
				int dz = z+k;
				if (Math.abs(i) != 2 && Math.abs(k) != 2)
					continue;
				Block b = world.getBlock(dx, y, dz);
				Fluid f = world.getBlockMetadata(dx, y, dz) == 0 ? FluidRegistry.lookupFluidForBlock(b) : null;
				if (f == null || !f.getName().equals("ender")) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", structure);
		NBT.setBoolean("enhanced", enhanced);
		NBT.setInteger("charge", charge);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		structure = NBT.getBoolean("struct");
		enhanced = NBT.getBoolean("enhanced");
		charge = NBT.getInteger("charge");
	}

	@Override
	public void validateStructure() {
		this.checkForStructure(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public ChromaStructures getPrimaryStructure() {
		return ChromaStructures.LAUNCHPAD;
	}

	@Override
	public Coordinate getStructureOffset() {
		return null;
	}

	@Override
	public boolean canStructureBeInspected() {
		return false;
	}

}
