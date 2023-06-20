package Reika.ChromatiCraft.Auxiliary.Ability;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import Reika.ChromatiCraft.Auxiliary.Ability.GrowAuraEffect.BlockBasedGrowAuraEffect;
import Reika.ChromatiCraft.Magic.RainbowTreeEffects;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent.UpdateFlags;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.ReactorCraft.Entities.EntityRadiation;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;

public class GrowAuraEffects {

	public static final GrowAuraEffect rainbowLeafFX = new GrowAuraEffect() {
		@Override
		public void performEffect(EntityPlayer ep, int x, int y, int z, int power) {
			RainbowTreeEffects.instance.doRainbowTreeEffects(ep.worldObj, x, y, z, 4, 0.25, DragonAPICore.rand, false);
		}
		@Override
		public boolean isEffectViable() {
			return true;
		}
		@Override
		public String getGuiLabel() {
			return "Ecological Restoration";
		}
	};

	public static final GrowAuraEffect cleanRadiation = new GrowAuraEffect() {
		@Override
		@ModDependent(ModList.REACTORCRAFT)
		public void performEffect(EntityPlayer ep, int x, int y, int z, int power) {
			AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(ep, 8);
			for (EntityRadiation e : ((List<EntityRadiation>)ep.worldObj.getEntitiesWithinAABB(EntityRadiation.class, box))) {
				e.clean();
			}
		}
		@Override
		public boolean isEffectViable() {
			return ModList.REACTORCRAFT.isLoaded() && DragonAPICore.rand.nextInt(40) == 0;
		}
		@Override
		public String getGuiLabel() {
			return "Decontamination";
		}
	};

	public static final GrowAuraEffect butterflyAttract = new GrowAuraEffect() {
		@Override
		public void performEffect(EntityPlayer ep, int x, int y, int z, int power) {
			ReikaBeeHelper.attractButterflies(ep.worldObj, ep.posX, ep.posY, ep.posZ, 32, null);
		}
		@Override
		public boolean isEffectViable() {
			return ModList.FORESTRY.isLoaded();
		}
		@Override
		public String getGuiLabel() {
			return "Lepidopterological Appeal";
		}
	};

	public static final BlockBasedGrowAuraEffect fertilize = new BlockBasedGrowAuraEffect() {
		@Override
		public void performEffect(EntityPlayer ep, int x, int y, int z, int power, Block b) {
			ReikaWorldHelper.fertilizeAndHealBlock(ep.worldObj, x, y, z);
		}
		@Override
		public boolean isEffectViable() {
			return true;
		}
		@Override
		public String getGuiLabel() {
			return "Fertilization";
		}
		@Override
		public int getNumberPerTick(EntityPlayer ep, int x, int y, int z, int power) {
			return 6;
		}
		@Override
		public int getXZRange() {
			return 8;
		}
		@Override
		public int getYRange() {
			return 1;
		}
		@Override
		public boolean isValid(World world, int x, int y, int z, Block b) {
			return true;
		}
	};

	public static final BlockBasedGrowAuraEffect nodeHeal = new BlockBasedGrowAuraEffect() {
		@Override
		@ModDependent(ModList.THAUMCRAFT)
		public void performEffect(EntityPlayer ep, int x, int y, int z, int power, Block b) {
			World world = ep.worldObj;
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof INode) {
				INode n = (INode)te;
				AspectList al = n.getAspects();
				Aspect a = ReikaJavaLibrary.getRandomCollectionEntry(world.rand, al.aspects.keySet());
				if (a != null) {
					if (n.getNodeVisBase(a) > al.getAmount(a)) {
						n.addToContainer(a, 1);
					}
				}
				if (world.rand.nextInt(8) == 0) {
					if (world.rand.nextInt(4) == 0) {
						NodeModifier m = n.getNodeModifier();
						if (m != NodeModifier.BRIGHT)
							n.setNodeModifier(m == NodeModifier.FADING ? NodeModifier.PALE : NodeModifier.BRIGHT);
					}
					else {
						NodeType t = n.getNodeType();
						if (t != NodeType.PURE && t != NodeType.NORMAL) {
							n.setNodeType(t == NodeType.HUNGRY || t == NodeType.TAINTED ? NodeType.DARK : t == NodeType.DARK ? NodeType.UNSTABLE : NodeType.NORMAL);
						}
					}
				}
			}
		}
		@Override
		public boolean isEffectViable() {
			return ModList.THAUMCRAFT.isLoaded();
		}
		@Override
		public String getGuiLabel() {
			return "Node Repair";
		}
		@Override
		public int getNumberPerTick(EntityPlayer ep, int x, int y, int z, int power) {
			return 8;
		}
		@Override
		public int getXZRange() {
			return 8;
		}
		@Override
		public int getYRange() {
			return 2;
		}
		@Override
		public boolean isValid(World world, int x, int y, int z, Block b) {
			return b == ThaumItemHelper.BlockEntry.NODE.getBlock();
		}
	};

	public static final BlockBasedGrowAuraEffect growTick = new BlockBasedGrowAuraEffect() {
		@Override
		public void performEffect(EntityPlayer ep, int x, int y, int z, int power, Block b) {
			BlockTickEvent.fire(b, ep.worldObj, x, y, z, DragonAPICore.rand, UpdateFlags.getForcedUnstoppableTick());
		}
		@Override
		public boolean isEffectViable() {
			return true;
		}
		@Override
		public String getGuiLabel() {
			return "Growth Boost";
		}
		@Override
		public int getNumberPerTick(EntityPlayer ep, int x, int y, int z, int power) {
			return 9;
		}
		@Override
		public int getXZRange() {
			return 6;
		}
		@Override
		public int getYRange() {
			return 3;
		}
		@Override
		public boolean isValid(World world, int x, int y, int z, Block b) {
			return true;
		}
	};

	public static final BlockBasedGrowAuraEffect bonemeal = new BlockBasedGrowAuraEffect() {
		@Override
		public void performEffect(EntityPlayer ep, int x, int y, int z, int power, Block b) {
			if (ep.worldObj.rand.nextInt(b == Blocks.grass ? 18 : 6) == 0) {
				EntityPlayer fake = ReikaPlayerAPI.getFakePlayerByNameAndUUID((WorldServer)ep.worldObj, "Random", Chromabilities.FAKE_UUID);
				fake.setCurrentItemOrArmor(0, ReikaItemHelper.bonemeal.copy());
				ItemDye.applyBonemeal(fake.getCurrentEquippedItem().copy(), ep.worldObj, x, y, z, fake);
			}
		}
		@Override
		public boolean isEffectViable() {
			return true;
		}
		@Override
		public String getGuiLabel() {
			return "Bonemeal";
		}
		@Override
		public int getNumberPerTick(EntityPlayer ep, int x, int y, int z, int power) {
			return 6;
		}
		@Override
		public int getXZRange() {
			return 5;
		}
		@Override
		public int getYRange() {
			return 2;
		}
		@Override
		public boolean isValid(World world, int x, int y, int z, Block b) {
			return true;
		}
	};

}
