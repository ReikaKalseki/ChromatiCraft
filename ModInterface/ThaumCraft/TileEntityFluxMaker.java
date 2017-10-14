/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Maps.TimerMap;
import Reika.DragonAPI.Instantiable.Effects.EntityFluidFX;
import Reika.DragonAPI.Interfaces.TileEntity.OpenTopTank;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityFluxMaker extends InventoriedRelayPowered implements OperationInterval {

	private static final ElementTagCompound required = new ElementTagCompound();

	private static final WeightedRandom<Fluid> fluidRand = new WeightedRandom();

	static {
		required.addTag(CrystalElement.LIGHTGRAY, 60);
		required.addTag(CrystalElement.BLACK, 60);

		addFluid("fluxgoo", 500);
		addFluid("fluxGas", 2000);
		addFluid("fluiddeath", 5);
	}

	private static void addFluid(String s, int wt) {
		Fluid f = FluidRegistry.getFluid(s);
		if (f != null)
			fluidRand.addEntry(f, wt);
	}

	public int processTimer;

	private final StepTimer timer = new StepTimer(10);
	private final HashMap<String, Integer> fluxCache = new HashMap();
	private final TimerMap<String> particleTimers = new TimerMap();

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FLUXMAKER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		particleTimers.tick();
		if (world.isRemote) {
			this.doParticles(world, x, y, z);
		}
		else {
			if (energy.containsAtLeast(required)) {
				Fluid f = fluidRand.getRandomEntry();
				if (f != null) {
					timer.update();
					processTimer = timer.getTick();
					if (timer.checkCap()) {
						int amt = 125;
						if (inv[0] != null && this.isItemValid(inv[0])) {
							ReikaInventoryHelper.decrStack(0, inv);
							amt = 1000;
						}
						if (this.spawnBlock(world, x, y, z, f, amt)) {
							energy.subtract(required);
							particleTimers.put(f.getName(), 5+timer.getCap());

							if (rand.nextInt(amt == 1000 ? 2 : 8) == 0) {
								ForgeDirection dir = dirs[1+rand.nextInt(5)];
								Block b = GameRegistry.findBlock(ModList.THAUMCRAFT.modLabel, "blockFluxGas");
								int dx = x+dir.offsetX;
								int dy = y+dir.offsetY;
								int dz = z+dir.offsetZ;
								if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
									world.setBlock(dx, dy, dz, b);
								}
							}
						}
					}
				}
				else {
					timer.reset();
				}
			}
			else {
				timer.reset();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		for (String s : particleTimers.keySet()) {
			Fluid f = FluidRegistry.getFluid(s);
			if (f != null) {
				for (int i = 0; i < 2; i++) {
					double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.025);
					double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.025);
					double vy = ReikaRandomHelper.getRandomBetween(0, 0.0625);
					EntityFluidFX fx = new EntityFluidFX(world, x+0.5, y+0.5, z+0.5, vx, vy, vz, f).setGravity(0.125F).setLife(30).setScale(0.5F);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
				if (rand.nextInt(32) == 0) {
					String snd = "";
					switch(rand.nextInt(4)) {
						case 0:
							snd = "thaumcraft:gore";
							break;
						case 1:
							snd = "liquid.lavapop";
							break;
						case 2:
							snd = "liquid.lava";
							break;
						case 3:
							snd = "game.neutral.swim";
							break;
					}
					if (!snd.isEmpty())
						ReikaSoundHelper.playClientSound(snd, x+0.5, y+0.5, z+0.5, 1, 0.5F+rand.nextFloat(), true);
				}
			}
			else {
				throw new IllegalStateException("Fluid '"+s+"' exists on the server but not the client! This is a mod installation error!");
			}
		}
	}

	private boolean spawnBlock(World world, int x, int y, int z, Fluid f, int amt) {
		TileEntity te = this.getAdjacentTileEntity(ForgeDirection.DOWN);
		if (te instanceof OpenTopTank) {
			OpenTopTank tk = (OpenTopTank)te;
			int add = tk.addLiquid(f, amt, false);
			if (add >= amt) {
				tk.addLiquid(f, amt, true);
				return true;
			}
			return false;
		}
		else if (te instanceof IFluidHandler) {
			IFluidHandler ifl = (IFluidHandler)te;
			FluidStack fs = new FluidStack(f, amt);
			int add = ifl.fill(ForgeDirection.UP, fs, false);
			if (add >= amt) {
				ifl.fill(ForgeDirection.UP, fs, true);
				return true;
			}
			return false;
		}
		else {
			boolean flag = false;
			String s = f.getName();
			Integer has = fluxCache.get(s);
			if (has == null)
				has = 0;
			has += amt;
			boolean flag2 = true;
			while (flag2 && has >= 1000) {
				for (int i = 0; i < 6 && flag2 && has >= 1000; i++) {
					int[] dirList = {0, 2, 3, 4, 5, 1};
					ForgeDirection dir = dirs[dirList[i]];
					int dx = x+dir.offsetX;
					int dy = y+dir.offsetY;
					int dz = z+dir.offsetZ;
					Block b = world.getBlock(dx, dy, dz);
					boolean added = false;
					if (b.isAir(world, dx, dy, dz)) {
						world.setBlock(dx, dy, dz, f.getBlock());
						added = true;
					}
					else if (b instanceof BlockFluidFinite) {
						BlockFluidFinite bf = (BlockFluidFinite)b;
						if (bf.getFluid() == f && bf.getQuantaPercentage(world, dx, dy, dz) < 1) {
							world.setBlockMetadataWithNotify(dx, dy, dz, world.getBlockMetadata(dx, dy, dz)+1, 3);
							added = true;
						}
					}
					if (added) {
						has -= 1000;
						flag = true;
						continue;
					}
					if (i == 5)
						flag2 = false;
				}
			}
			if (has > 0) {
				fluxCache.put(s, has);
			}
			else {
				fluxCache.remove(s);
			}
			return flag;
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return slot == 0 && this.isItemValid(is);
	}

	private boolean isItemValid(ItemStack is) {
		if (is.getItem() == GameRegistry.findItem(ModList.THAUMCRAFT.modLabel, "ItemCrystalEssence") && is.stackTagCompound != null) {
			AspectList al = new AspectList();
			al.readFromNBT(is.stackTagCompound);
			return al.size() == 1 && al.getAmount(Aspect.TAINT) > 0;
		}
		return false;
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	public static ElementTagCompound getTags() {
		return required.copy();
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 2400;
	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return this.isAcceptingColor(e);
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		processTimer = NBT.getInteger("time");
		HashMap<String, Integer> map = (HashMap<String, Integer>)ReikaNBTHelper.readMapFromNBT(NBT, "particles");
		particleTimers.clear();
		particleTimers.putAll(map);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("time", processTimer);
		ReikaNBTHelper.writeMapToNBT("particles", NBT, particleTimers.toMap());
	}

	public int getCookProgressScaled(int a) {
		return processTimer * a / timer.getCap();
	}

	@Override
	public float getOperationFraction() {
		return processTimer/(float)timer.getCap();
	}

	@Override
	public OperationState getState() {
		return energy.containsAtLeast(required) ? OperationState.RUNNING : OperationState.PENDING;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		ReikaNBTHelper.writeMapToNBT("fluids", NBT, fluxCache);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		HashMap<String, Integer> map = (HashMap<String, Integer>)ReikaNBTHelper.readMapFromNBT(NBT, "fluids");
		fluxCache.clear();
		fluxCache.putAll(map);
	}

}
