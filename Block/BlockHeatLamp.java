/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SidedBlock;
import Reika.ChromatiCraft.Base.BlockAttachableMini;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityHeatRelay;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ClassDependent;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.ItemSpecificEffectDescription;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiStackDisplay;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;
import Reika.DragonAPI.Interfaces.TileEntity.ThermalTile;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MultiblockControllerFinder;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.TinkerSmelteryHandler;
import Reika.DragonAPI.ModInteract.DeepInteract.TinkerSmelteryHandler.CastingBlockWrapper;
import Reika.DragonAPI.ModInteract.DeepInteract.TinkerSmelteryHandler.SmelteryWrapper;
import Reika.DragonAPI.ModInteract.Power.ReikaRailCraftHelper;
import Reika.DragonAPI.ModInteract.Power.ReikaRailCraftHelper.FireboxWrapper;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Interfaces.BasicTemperatureMachine;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;
import Reika.RotaryCraft.Registry.EngineType;
import Reika.RotaryCraft.Registry.GearboxTypes;
import Reika.RotaryCraft.Registry.MachineRegistry;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.tile.IHeatSource;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import tuhljin.automagy.api.essentia.IEssentiaDistillery;

public class BlockHeatLamp extends BlockAttachableMini {

	private IIcon coldIcon;

	public BlockHeatLamp(Material mat) {
		super(mat);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs c, List li) {
		li.add(ChromaBlocks.HEATLAMP.getStackOfMetadata(0));
		li.add(ChromaBlocks.HEATLAMP.getStackOfMetadata(8));
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ((BlockTieredOre)ChromaBlocks.TIEREDORE.getBlockInstance()).getGeodeIcon(6);
		coldIcon = ico.registerIcon("chromaticraft:coldlamp");
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return isCold(iba, x, y, z) ? coldIcon : super.getIcon(iba, x, y, z, s);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return meta >= 8 ? coldIcon : blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void createFX(World world, int x, int y, int z, double dx, double dy, double dz, Random r) {
		CrystalElement e = isCold(world, x, y, z) ? CrystalElement.WHITE : CrystalElement.ORANGE;
		EntityFX fx = new EntityCenterBlurFX(e, world, dx, dy, dz, 0, 0, 0).setScale(2+r.nextFloat()*2);
		if (r.nextInt(4) == 0) {
			fx = new EntityLaserFX(e, world, dx, dy, dz, 0, 0, 0).setScale(2+r.nextFloat()*2);
		}
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.TILE.ordinal(), world, x, y, z);
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityHeatLamp();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public int getColor(IBlockAccess iba, int x, int y, int z) {
		return this.isCold(iba, x, y, z) ? 0x90b0ff : 0xffaa00;
	}

	private static boolean isCold(IBlockAccess iba, int x, int y, int z) {
		return iba.getBlockMetadata(x, y, z) >= 8;
	}

	public static abstract class HeatLampEffect extends ItemSpecificEffectDescription {

		public abstract void tickTile(TileEntity te, TileEntityHeatLamp lamp);

		public boolean isActive() {
			return true;
		}

	}

	public static abstract class ModdedHeatLampEffect extends HeatLampEffect {

		public abstract ModList getMod();

		@Override
		public final boolean isActive() {
			return this.getMod().isLoaded();
		}

	}

	@Strippable(value = "ic2.api.energy.tile.IHeatSource")
	public static class TileEntityHeatLamp extends TileEntity implements GuiController, IHeatSource {

		public int temperature;
		public static final int MAXTEMP = 615;
		public static final int MAXTEMP_COLD = 15;
		public static final int MINTEMP = 20;
		public static final int MINTEMP_COLD = -60;

		private static final ArrayList<HeatLampEffect> heatEffects = new ArrayList();
		private static final ArrayList<HeatLampEffect> coldEffects = new ArrayList();

		static {
			heatEffects.add(new HeatLampEffect(){
				@Override
				public String getDescription(GuiItemDisplay i) {
					return "Heats to temperature";
				}

				@Override
				public void tickTile(TileEntity te, TileEntityHeatLamp lamp) {
					if (te instanceof ThermalTile) {
						ThermalTile tl = (ThermalTile)te;
						if (lamp.canHeat(tl, lamp.isCold())) {
							if (lamp.temperature > tl.getTemperature()) {
								tl.setTemperature(tl.getTemperature()+1);
								if (ModList.ROTARYCRAFT.isLoaded() && te instanceof BasicTemperatureMachine)
									((BasicTemperatureMachine)te).resetAmbientTemperatureTimer();
							}
						}
						else {
							((BlockHeatLamp)lamp.getBlockType()).drop(lamp.worldObj, lamp.xCoord, lamp.yCoord, lamp.zCoord);
						}
					}
				}

				@Override
				public List<GuiItemDisplay> getRelevantItems() {
					return ModList.ROTARYCRAFT.isLoaded() ? TileEntityHeatLamp.getAffectedRCMachines(false) : new ArrayList();
				}
			});
			coldEffects.add(new HeatLampEffect(){
				@Override
				public String getDescription(GuiItemDisplay i) {
					return "Cools to temperature";
				}

				@Override
				public void tickTile(TileEntity te, TileEntityHeatLamp lamp) {
					if (te instanceof ThermalTile) {
						ThermalTile tl = (ThermalTile)te;
						if (lamp.canHeat(tl, lamp.isCold())) {
							if (lamp.temperature < tl.getTemperature()) {
								tl.setTemperature(tl.getTemperature()-1);
								if (ModList.ROTARYCRAFT.isLoaded() && te instanceof BasicTemperatureMachine)
									((BasicTemperatureMachine)te).resetAmbientTemperatureTimer();
							}
						}
						else {
							((BlockHeatLamp)lamp.getBlockType()).drop(lamp.worldObj, lamp.xCoord, lamp.yCoord, lamp.zCoord);
						}
					}
				}

				@Override
				public List<GuiItemDisplay> getRelevantItems() {
					return ModList.ROTARYCRAFT.isLoaded() ? TileEntityHeatLamp.getAffectedRCMachines(true) : new ArrayList();
				}
			});
			heatEffects.add(new HeatLampEffect(){
				@Override
				public String getDescription(GuiItemDisplay i) {
					return "Supplants fuel needs (>200C)";
				}

				@Override
				public void tickTile(TileEntity te, TileEntityHeatLamp lamp) {
					if (te instanceof TileEntityFurnace) {
						TileEntityFurnace tf = (TileEntityFurnace)te;
						if (lamp.temperature >= 200) {
							double c = Math.min(1, 1.25*lamp.temperature/1000D);
							if (ReikaRandomHelper.doWithChance(c)) {
								if (tf.furnaceBurnTime == 0 && ReikaRandomHelper.doWithChance(c)) {
									tf.furnaceBurnTime = 20;
								}
								te.updateEntity();
							}
						}
						else {
							tf.furnaceCookTime = lamp.temperature;
							tf.furnaceBurnTime = 2;
						}
					}
				}

				@Override
				public List<GuiItemDisplay> getRelevantItems() {
					return Arrays.asList(new GuiStackDisplay(Blocks.furnace));
				}
			});
			heatEffects.add(new ModdedHeatLampEffect(){
				@Override
				public String getDescription(GuiItemDisplay i) {
					return "Keeps smelteries warm";
				}

				@Override
				public void tickTile(TileEntity te, TileEntityHeatLamp lamp) {
					if (TinkerSmelteryHandler.isSmelteryController(te)) {
						SmelteryWrapper s = new SmelteryWrapper(te);
						s.fuelLevel = 4000;
						s.meltPower = lamp.temperature*1200/MAXTEMP; //that puts max heat lamp at 1200
						s.write(te);
					}
				}

				@Override
				public ModList getMod() {
					return ModList.TINKERER;
				}

				@Override
				public List<GuiItemDisplay> getRelevantItems() {
					return Arrays.asList(new GuiStackDisplay("TConstruct:Smeltery"), new GuiStackDisplay("TConstruct:SmelteryNether"));
				}
			});
			coldEffects.add(new ModdedHeatLampEffect(){
				@Override
				public String getDescription(GuiItemDisplay i) {
					return "Increases casting speeds";
				}

				@Override
				public void tickTile(TileEntity te, TileEntityHeatLamp lamp) {
					if (TinkerSmelteryHandler.isCastingBlock(te)) {
						CastingBlockWrapper s = new CastingBlockWrapper(te);
						if (s.timer > 1) {
							int d = -lamp.temperature/15;
							if (d > 0)
								s.timer = Math.max(1, s.timer-d);
						}
						s.write(te);
					}
				}

				@Override
				public ModList getMod() {
					return ModList.TINKERER;
				}

				@Override
				public List<GuiItemDisplay> getRelevantItems() {
					return Arrays.asList(new GuiStackDisplay("TConstruct:SearedBlock"), new GuiStackDisplay("TConstruct:SearedBlock:2"), new GuiStackDisplay("TConstruct:SearedBlockNether"), new GuiStackDisplay("TConstruct:SearedBlockNether:2"));
				}
			});
			heatEffects.add(new ModdedHeatLampEffect(){
				@Override
				public String getDescription(GuiItemDisplay i) {
					return "Keeps fireboxes warm";
				}

				@Override
				public void tickTile(TileEntity te, TileEntityHeatLamp lamp) {
					if (ReikaRailCraftHelper.isSolidFirebox(te)) {
						te = MultiblockControllerFinder.instance.getController(te);
						FireboxWrapper s = new FireboxWrapper(te);
						s.load(te);
						//if (80+25*Math.sin(worldObj.getTotalWorldTime()/350D) >= 100)
						if (s.temperature < 99)
							s.setBurning(5);
						s.write(te);
					}
				}

				@Override
				public ModList getMod() {
					return ModList.RAILCRAFT;
				}

				@Override
				public List<GuiItemDisplay> getRelevantItems() {
					return Arrays.asList(new GuiStackDisplay("Railcraft:machine.beta:5"), new GuiStackDisplay("Railcraft:machine.beta:6"));
				}
			});
			heatEffects.add(new ModdedHeatLampEffect(){
				@Override
				public String getDescription(GuiItemDisplay i) {
					return "Supplants fuel needs (>200C)";
				}

				@Override
				public void tickTile(TileEntity te, TileEntityHeatLamp lamp) {
					if (lamp.temperature >= 200 && ReikaThaumHelper.isAlchemicalFurnace(te)) {
						ReikaThaumHelper.setAlchemicalBurnTime(te, lamp.temperature/50);
					}
				}

				@Override
				public ModList getMod() {
					return ModList.THAUMCRAFT;
				}

				@Override
				public List<GuiItemDisplay> getRelevantItems() {
					return Arrays.asList(new GuiStackDisplay("Thaumcraft:blockStoneDevice"));
				}
			});
			heatEffects.add(new HeatLampEffect(){
				@Override
				public String getDescription(GuiItemDisplay i) {
					return "Supplants fuel needs (>200C)";
				}

				@Override
				public void tickTile(TileEntity te, TileEntityHeatLamp lamp) {
					if (Loader.isModLoaded("Automagy") && lamp.temperature >= 200 && InterfaceCache.ESSENTIADISTILL.instanceOf(te)) {
						this.setEssentiaDistillery(te, lamp);
					}
				}

				@ClassDependent("tuhljin.automagy.api.essentia.IEssentiaDistillery")
				private void setEssentiaDistillery(TileEntity te, TileEntityHeatLamp lamp) {
					IEssentiaDistillery ied = (IEssentiaDistillery)te;
					ied.setFurnaceBurnTime(Math.max(ied.getFurnaceBurnTime(), lamp.temperature/50));
				}

				@Override
				public boolean isActive() {
					return Loader.isModLoaded("Automagy");
				}

				@Override
				public List<GuiItemDisplay> getRelevantItems() {
					return Arrays.asList(new GuiStackDisplay("Automagy:blockBoiler"));
				}
			});
			coldEffects.add(new ModdedHeatLampEffect(){
				@Override
				public String getDescription(GuiItemDisplay i) {
					return "Cools reactors";
				}

				@Override
				public void tickTile(TileEntity te, TileEntityHeatLamp lamp) {
					if (InterfaceCache.IC2NUKE.instanceOf(te) || InterfaceCache.IC2NUKECHAMBER.instanceOf(te)) {
						this.setIC2Reactor(te, lamp);
					}
				}

				@ModDependent(ModList.IC2)
				private void setIC2Reactor(TileEntity te, TileEntityHeatLamp lamp) {
					if (te instanceof IReactorChamber)
						te = (TileEntity)((IReactorChamber)te).getReactor();
					int rem = Math.max(0, -lamp.temperature/10);
					((IReactor)te).addHeat(-rem); //5 == 1 reactor heat vent);
				}

				@Override
				public ModList getMod() {
					return ModList.IC2;
				}

				@Override
				public List<GuiItemDisplay> getRelevantItems() {
					return Arrays.asList(new GuiStackDisplay("IC2:blockGenerator:5"), new GuiStackDisplay("IC2:blockReactorChamber"));
				}
			});
			heatEffects.add(new ModdedHeatLampEffect(){
				@Override
				public String getDescription(GuiItemDisplay i) {
					return "Supplies HU";
				}

				@Override
				public void tickTile(TileEntity te, TileEntityHeatLamp lamp) {

				}

				@Override
				public ModList getMod() {
					return ModList.IC2;
				}

				@Override
				public List<GuiItemDisplay> getRelevantItems() {
					return TileEntityHeatRelay.getHUItems();
				}
			});
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void updateEntity() {
			if (worldObj.isRemote)
				return;
			temperature = Math.max(this.isCold() ? MINTEMP_COLD : MINTEMP, Math.min(this.isCold() ? MAXTEMP_COLD : MAXTEMP, temperature));
			ForgeDirection dir = ((BlockHeatLamp)this.getBlockType()).getSide(worldObj, xCoord, yCoord, zCoord).getOpposite();
			TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
			for (HeatLampEffect e : getEffects(this.isCold())) {
				if (e.isActive())
					e.tickTile(te, this);
			}
		}

		public static Collection<HeatLampEffect> getEffects(boolean cold) {
			return Collections.unmodifiableCollection(cold ? coldEffects : heatEffects);
		}

		private boolean isCold() {
			return BlockHeatLamp.isCold(worldObj, xCoord, yCoord, zCoord);
		}

		@ModDependent(ModList.ROTARYCRAFT)
		protected static List<GuiItemDisplay> getAffectedRCMachines(boolean cold) {
			ArrayList<GuiItemDisplay> li = new ArrayList();
			for (int i = 0; i < MachineRegistry.machineList.length; i++) {
				MachineRegistry m = MachineRegistry.machineList.get(i);
				if (m == MachineRegistry.ENGINE)
					continue;
				Class c = m.getTEClass();
				if (ThermalTile.class.isAssignableFrom(c)) {
					ThermalTile te = (ThermalTile)m.createTEInstanceForRender(0);
					if (canHeat(te, cold)) {
						if (m == MachineRegistry.GEARBOX) {
							for (GearboxTypes gear : GearboxTypes.typeList)
								li.add(new GuiStackDisplay(gear.getGearboxItem(16)));
						}
						else {
							li.add(new GuiStackDisplay(m.getCraftedProduct()));
						}
					}
				}
			}
			for (EngineType e : EngineType.engineList) {
				Class c = e.engineClass;
				if (ThermalTile.class.isAssignableFrom(c)) {
					ThermalTile te = (ThermalTile)e.getTEInstanceForRender();
					if (canHeat(te, cold)) {
						li.add(new GuiStackDisplay(e.getCraftedProduct()));
					}
				}
			}
			if (ModList.REACTORCRAFT.isLoaded()) {
				li.addAll(getAffectedReCMachines(cold));
			}
			return li;
		}

		@ModDependent(ModList.REACTORCRAFT)
		protected static List<GuiItemDisplay> getAffectedReCMachines(boolean cold) {
			ArrayList<GuiItemDisplay> li = new ArrayList();
			for (ReactorTiles r : ReactorTiles.TEList) {
				Class c = r.getTEClass();
				if (ThermalTile.class.isAssignableFrom(c)) {
					ThermalTile te = (ThermalTile)r.createTEInstanceForRender();
					if (canHeat(te, cold)) {
						li.add(new GuiStackDisplay(r.getCraftedProduct()));
					}
				}
			}
			return li;
		}

		private static boolean canHeat(ThermalTile tl, boolean cold) {
			if (ModList.ROTARYCRAFT.isLoaded() && isRotaryHeatTile(tl, cold))
				return false;
			return !ModList.REACTORCRAFT.isLoaded() || !isReactorTile(tl);
		}

		@ModDependent(ModList.ROTARYCRAFT)
		private static boolean isRotaryHeatTile(ThermalTile tl, boolean cold) {
			return tl instanceof TemperatureTE && !(cold ? ((TemperatureTE)tl).canBeCooledWithFins() : ((TemperatureTE)tl).allowExternalHeating());
		}

		@ModDependent(ModList.REACTORCRAFT)
		private static boolean isReactorTile(ThermalTile tl) {
			return tl instanceof ReactorCoreTE;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("temperature", temperature);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			temperature = NBT.getInteger("temperature");
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord);
		}

		@Override
		public int maxrequestHeatTick(ForgeDirection dir) {
			return !this.isCold() && ((SidedBlock)this.getBlockType()).getSide(worldObj, xCoord, yCoord, zCoord) == dir.getOpposite() ? Math.min(10, temperature/50) : 0;
		}

		@Override
		public int requestHeat(ForgeDirection dir, int amt) {
			return Math.min(this.maxrequestHeatTick(dir), amt);
		}

	}

}
