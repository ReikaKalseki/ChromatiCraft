/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.Fluid;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import Reika.ChromatiCraft.ChromaGuiHandler;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Block.BlockSelectiveGlass;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.Interfaces.ChargingPoint;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.TargetData;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ProgressElement;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.ChromatiCraft.World.Dimension.WorldProviderChroma;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Worldgen.GenerationInterceptWorld;
import Reika.DragonAPI.Instantiable.Worldgen.GenerationInterceptWorld.TileHook;
import Reika.DragonAPI.Interfaces.Entity.CustomProjectile;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaChunkHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BloodMagicHandler;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class ChromaAux {

	public static final Color[] sideColors = {Color.CYAN, Color.BLUE, Color.YELLOW, Color.BLACK, new Color(255, 120, 0), Color.MAGENTA};
	public static final String[] sideColorNames = {"CYAN", "BLUE", "YELLOW", "BLACK", "ORANGE", "MAGENTA"};

	private static final GenerationInterceptWorld relayWorld = new GenerationInterceptWorld();

	static {
		if (ModList.THAUMCRAFT.isLoaded()) {
			/*
			relayWorld.disallowBlock(ThaumItemHelper.BlockEntry.NODE.getBlock());
			relayWorld.disallowBlock(ThaumItemHelper.BlockEntry.TOTEM.getBlock());
			relayWorld.disallowBlock(ThaumItemHelper.BlockEntry.TILE.getBlock());
			relayWorld.disallowBlock(ThaumItemHelper.BlockEntry.TOTEMNODE.getBlock());
			 */
			relayWorld.addHook(new NodeHook());
		}
	}

	private static class NodeHook implements TileHook {

		@Override
		public void onTileChanged(TileEntity te) {
			if (InterfaceCache.NODE.instanceOf(te)) {
				INode n = (INode)te;
				n.setNodeType(NodeType.NORMAL);
				n.setNodeModifier(NodeModifier.BRIGHT);
				if (te.worldObj.rand.nextInt(4) == 0) {
					float f = 1+te.worldObj.rand.nextFloat()*2;
					AspectList al = n.getAspects();
					for (Aspect a : new HashSet<Aspect>(al.aspects.keySet())) {
						al.aspects.put(a, (int)(f*al.getAmount(a)));
					}
				}
			}
		}

		@Override
		public boolean shouldRun(World world, int x, int y, int z) {
			return BiomeGlowingCliffs.isGlowingCliffs(world.getBiomeGenForCoords(x, z));
		}

	}

	public static void interceptChunkPopulation(int cx, int cz, World world, IChunkProvider generator, IChunkProvider loader) {
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			((WorldProviderChroma)world.provider).getChunkGenerator().onPopulationHook(generator, loader, cx, cz);
		}
		else if (ReikaChunkHelper.chunkContainsBiomeType(world, cx, cz, BiomeGlowingCliffs.class)) {
			relayWorld.link(world);
			GameRegistry.generateWorld(cx, cz, relayWorld, generator, loader);
			relayWorld.runHooks();
		}
		else {
			GameRegistry.generateWorld(cx, cz, world, generator, loader);
		}
	}

	public static final boolean hasGui(World world, int x, int y, int z, EntityPlayer ep) {
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			Object GUI = ChromaGuiHandler.instance.getClientGuiElement(0, ep, world, x, y, z);
			if (GUI != null)
				return true;
		}
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			Object GUI = ChromaGuiHandler.instance.getServerGuiElement(0, ep, world, x, y, z);
			if (GUI != null)
				return true;
		}
		return false;
	}

	public static int get4SidedMetadataFromPlayerLook(EntityLivingBase ep) {
		int i = MathHelper.floor_double((ep.rotationYaw * 4F) / 360F + 0.5D);
		while (i > 3)
			i -= 4;
		while (i < 0)
			i += 4;
		switch (i) {
			case 0:
				return 2;
			case 1:
				return 1;
			case 2:
				return 3;
			case 3:
				return 0;
		}
		return -1;
	}

	public static int get6SidedMetadataFromPlayerLook(EntityLivingBase ep) {
		if (MathHelper.abs(ep.rotationPitch) < 60) {
			int i = MathHelper.floor_double((ep.rotationYaw * 4F) / 360F + 0.5D);
			while (i > 3)
				i -= 4;
			while (i < 0)
				i += 4;
			switch (i) {
				case 0:
					return 2;
				case 1:
					return 1;
				case 2:
					return 3;
				case 3:
					return 0;
			}
		}
		else { //Looking up/down
			if (ep.rotationPitch > 0)
				return 4; //set to up
			else
				return 5; //set to down
		}
		return -1;
	}

	public static int get2SidedMetadataFromPlayerLook(EntityLivingBase ep) {
		int i = MathHelper.floor_double((ep.rotationYaw * 4F) / 360F + 0.5D);
		while (i > 3)
			i -= 4;
		while (i < 0)
			i += 4;

		switch (i) {
			case 0:
				return 0;
			case 1:
				return 1;
			case 2:
				return 0;
			case 3:
				return 1;
		}
		return -1;
	}

	public static boolean shouldSetFlipped(World world, int x, int y, int z) {
		boolean softBelow = ReikaWorldHelper.softBlocks(world, x, y-1, z);
		boolean softAbove = ReikaWorldHelper.softBlocks(world, x, y+1, z);
		if (!softAbove && softBelow) {
			return true;
		}
		return false;
	}

	public static String getMessage(String tag) {
		return StatCollector.translateToLocal("message."+tag);
	}

	public static void writeMessage(String tag) {
		ReikaChatHelper.writeString(getMessage(tag));
	}

	public static void spawnInteractionBallLightning(World world, int x, int y, int z, CrystalElement e) {
		if (!world.isRemote && ChromaOptions.BALLLIGHTNING.getState()) {
			int dx = ReikaRandomHelper.getRandomPlusMinus(x, 16);
			int dz = ReikaRandomHelper.getRandomPlusMinus(z, 16);
			double dy = world.getTopSolidOrLiquidBlock(dx, dz)+ReikaRandomHelper.getSafeRandomInt(8);//ReikaRandomHelper.getRandomPlusMinus(y+0.5, 16);
			world.spawnEntityInWorld(new EntityBallLightning(world, e, dx+0.5, dy+0.5, dz+0.5).setNoDrops());
		}
	}

	public static boolean requiresSpecialSpawnEnforcement(EntityLiving e) {
		String name = e.getClass().getName().toLowerCase(Locale.ENGLISH);
		return name.contains("lycanite");
	}

	public static void doPylonAttack(CrystalElement color, EntityLivingBase e, float amt, boolean taperNew) {
		ChromaSounds.DISCHARGE.playSound(e.worldObj, e.posX, e.posY, e.posZ, 1, 1);

		final float originalAmt = amt;

		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			ProgressStage.SHOCK.stepPlayerTo(ep);
			//DO NOT UNCOMMENT, AS ALLOWS DISCOVERY OF ALL COLORS BEFORE PREREQ//ProgressionManager.instance.setPlayerDiscoveredColor(ep, color, true);
			if (ModList.BLOODMAGIC.isLoaded()) {
				int drain = 5000;
				if (BloodMagicHandler.getInstance().isPlayerWearingFullBoundArmor(ep)) {
					amt *= 10; //counter the 90% reduction
					drain = 50000;
				}
				SoulNetworkHandler.syphonFromNetwork(ep.getCommandSenderName(), drain);
			}

			if (taperNew) {
				if (e.ticksExisted < 600) {
					amt = 1; //1/2 heart for first 30s
				}
				else if (e.ticksExisted <= 1000) {
					amt = 1+(e.ticksExisted-600)/100; //increase by 1/2 heart every 5 seconds, up to 2.5 hearts at 50 seconds
				}
			}
		}

		float last = e.getHealth();

		e.attackEntityFrom(ChromatiCraft.pylonDamage[color.ordinal()], amt);

		if (e.getHealth() > Math.max(0, last-originalAmt)) {
			if (originalAmt >= last) { //kill
				e.setHealth(0.1F);
				e.attackEntityFrom(ChromatiCraft.pylonDamage[color.ordinal()], Float.MAX_VALUE);
			}
			else
				e.setHealth(last-originalAmt);
		}
	}

	public static float getIslandBias(float originalBias, float dx, float dz) {
		float dist = MathHelper.sqrt_double(dx*dx+dz*dz);
		return 50+50*MathHelper.sin(dist*0.0625F); //is 100 at spawn
	}

	public static MultiMap<TargetData, CrystalElement> getBeamColorMixes(Collection<CrystalTarget> c) {
		MultiMap<TargetData, CrystalElement> map = new MultiMap(new MultiMap.ListFactory());
		for (CrystalTarget t : c) {
			map.addValue(new TargetData(t), t.color);
		}
		return map;
	}

	public static void changePylonColor(World world, TileEntityCrystalPylon te, CrystalElement e) {
		try {
			ChunkCoordIntPair ch = new Coordinate(te).asChunkPair();
			world.getChunkProvider().provideChunk(ch.chunkXPos, ch.chunkZPos);
			TileEntityCrystalPylon old = te;
			te = (TileEntityCrystalPylon)world.getTileEntity(te.xCoord, te.yCoord, te.zCoord);
			CrystalNetworker.instance.removeTile(old);
			PylonGenerator.instance.removeCachedPylon(old);
			te.setColor(e);
			BlockArray runes = te.getRuneLocations(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
			for (int i = 0; i < runes.getSize(); i++) {
				Coordinate c = runes.getNthBlock(i);
				if (c.getBlock(te.worldObj) == ChromaBlocks.RUNE.getBlockInstance())
					te.worldObj.setBlockMetadataWithNotify(c.xCoord, c.yCoord, c.zCoord, te.getColor().ordinal(), 3);
			}
			CrystalNetworker.instance.addTile(te);
			PylonGenerator.instance.cachePylon(te);
		}
		catch (Exception ex) {
			ChromatiCraft.logger.logError("Could not change pylon color @ "+te);
			ex.printStackTrace();
		}
	}

	public static void notifyServerPlayersExcept(EntityPlayer ep, ProgressElement p) {
		String sg = EnumChatFormatting.GOLD+ep.getCommandSenderName()+EnumChatFormatting.RESET+" has learned something new: "+p.getFormatting()+p.getTitle();
		WorldServer[] ws = DimensionManager.getWorlds();
		for (int i = 0; i < ws.length; i++) {
			for (EntityPlayer ep2 : ((List<EntityPlayer>)ws[i].playerEntities)) {
				if (ep2 != ep)
					ReikaChatHelper.sendChatToPlayer(ep2, sg);
			}
		}
	}

	public static void notifyServerPlayers(EntityPlayer ep, ProgressElement p) {
		String sg = EnumChatFormatting.GOLD+ep.getCommandSenderName()+EnumChatFormatting.RESET+" has learned something new: "+p.getFormatting()+p.getTitle();
		WorldServer[] ws = DimensionManager.getWorlds();
		for (int i = 0; i < ws.length; i++) {
			for (EntityPlayer ep2 : ((List<EntityPlayer>)ws[i].playerEntities)) {
				if (ep2 != ep) {
					ReikaChatHelper.sendChatToPlayer(ep2, sg);
				}
				else {
					ReikaChatHelper.sendChatToPlayer(ep2, "You have learned something new: "+p.getFormatting()+p.getTitle());
				}
			}
		}
	}
	/*
	public static int overrideLightValue(IBlockAccess world, int x, int y, int z, int val) {
		world = Minecraft.getMinecraft().theWorld;
		int base = defaultLightBrightness((World)world, x, y, z, val);
		double dist = EntityFlyingLight.getClosestLight((World)world, x, y, z);
		//dist = Math.min(dist, Minecraft.getMinecraft().thePlayer.getDistance(x+0.5, y+0.5, z+0.5));
		int l = 0;
		int m = 5;
		if (dist < m) {
			l = 15;
		}
		else if (dist < 15+m) {
			l = 15+m-(int)dist;
		}
		//ReikaJavaLibrary.pConsole(Integer.toHexString(base)+"; "+l, dist < Double.POSITIVE_INFINITY);
		return ReikaMathLibrary.bitRound(base, 20) | (l << 4);
	}

	private static int defaultLightBrightness(World world, int x, int y, int z, int val) {
		int sky = world.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z);
		int block = Math.max(val, world.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z));
		return sky << 20 | block << 4;
	}
	 */

	@Deprecated
	public static void permuteRunes(HashMap<Coordinate, CrystalElement> map, World world, EntityPlayer ep) {

	}

	public static CrystalElement getRune(Fluid fluid) {
		ArrayList<CrystalElement> li = new ArrayList();
		li.add(CrystalElement.CYAN);
		if (fluid.getTemperature() > 900) //900K
			li.add(CrystalElement.ORANGE);
		if (fluid.getTemperature() < 270)
			li.add(CrystalElement.WHITE);
		if (fluid.isGaseous())
			li.add(CrystalElement.LIME);
		if (fluid.getLuminosity() > 0)
			li.add(CrystalElement.BLUE);
		if (fluid.getDensity() > 4000)
			li.add(CrystalElement.RED);
		if (fluid.getName().toLowerCase(Locale.ENGLISH).contains("oil"))
			li.add(CrystalElement.BROWN);
		if (fluid.getName().toLowerCase(Locale.ENGLISH).contains("fuel"))
			li.add(CrystalElement.YELLOW);
		if (fluid.getName().toLowerCase(Locale.ENGLISH).contains("xp") || fluid == ChromatiCraft.chroma)
			li.add(CrystalElement.PURPLE);
		if (fluid.getName().toLowerCase(Locale.ENGLISH).contains("bio") || fluid.getName().toLowerCase(Locale.ENGLISH).contains("honey"))
			li.add(CrystalElement.GREEN);
		return li.get(ReikaRandomHelper.getSafeRandomInt(li.size()));
	}

	public static boolean chargePlayerFromPylon(EntityPlayer player, ChargingPoint te, CrystalElement e, int count) {
		int add = Math.max(1, (int)(PlayerElementBuffer.instance.getChargeSpeed(player)*te.getChargeRateMultiplier(player, e)));
		int n = PlayerElementBuffer.instance.getChargeInefficiency(player);
		int drain = add*n;
		int energy = te.getEnergy(e);
		if (drain > energy) {
			drain = energy;
			add = drain/n;
		}
		if (te.canConduct() && te.allowCharging(player, e) && add > 0 && PlayerElementBuffer.instance.canPlayerAccept(player, e, add)) {
			te.onUsedBy(player, e);
			if (PlayerElementBuffer.instance.addToPlayer(player, e, add))
				te.drain(e, drain);
			ProgressStage.CHARGE.stepPlayerTo(player);
			if (te instanceof TileEntityCrystalPylon)
				ProgressionManager.instance.setPlayerDiscoveredColor(player, ((TileEntityCrystalPylon)te).getColor(), true, true);
			if (player.worldObj.isRemote) {
				//this.spawnParticles(player, e);
				ChromaFX.createPylonChargeBeam(te, player, (count%20)/20D, e);
			}
			return true;
		}
		return false;
	}

	public static List<AxisAlignedBB> interceptEntityCollision(World world, Entity e, AxisAlignedBB box) {
		if (e instanceof EntityPlayer && Chromabilities.ORECLIP.enabledOn((EntityPlayer)e)) {
			return AbilityHelper.instance.getNoclipBlockBoxes((EntityPlayer)e);
		}
		else {
			return getSurrogateCollidingAABBs(world, e, box);//e.worldObj.getCollidingBoundingBoxes(e, box);
		}
	}

	public static List<AxisAlignedBB> getSurrogateCollidingAABBs(World world, Entity ep, AxisAlignedBB box) {
		ArrayList<AxisAlignedBB> li = new ArrayList();

		int i = MathHelper.floor_double(box.minX);
		int j = MathHelper.floor_double(box.maxX + 1.0D);
		int k = MathHelper.floor_double(box.minY);
		int l = MathHelper.floor_double(box.maxY + 1.0D);
		int i1 = MathHelper.floor_double(box.minZ);
		int j1 = MathHelper.floor_double(box.maxZ + 1.0D);

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = i1; l1 < j1; ++l1) {
				if (world.blockExists(k1, 64, l1)) {
					for (int i2 = k - 1; i2 < l; ++i2) {
						Block block;

						if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000) {
							block = world.getBlock(k1, i2, l1);
						}
						else {
							block = Blocks.stone;
						}

						block.addCollisionBoxesToList(world, k1, i2, l1, box, li, ep);
					}
				}
			}
		}

		double d0 = 0.25D;
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(ep, box.expand(d0, d0, d0));

		for (Entity e : list) {
			AxisAlignedBB box2 = e.getBoundingBox();

			if (box2 != null && box2.intersectsWith(box)) {
				li.add(box2);
			}

			box2 = ep.getCollisionBox(e);

			if (box2 != null && box2.intersectsWith(box)) {
				li.add(box2);
			}
		}

		return li;
	}

	public static boolean applyNoclipPhase(EntityPlayer ep) {
		return ep.noClip || Chromabilities.ORECLIP.enabledOn(ep);
	}

	public static AxisAlignedBB getInterceptedCollisionBox(Entity e, World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == ChromaBlocks.SELECTIVEGLASS.getBlockInstance() && (e instanceof IProjectile || e instanceof EntityFireball || e instanceof CustomProjectile)) {
			if (BlockSelectiveGlass.canEntityPass(world, x, y, z, e)) {
				return null;
			}
		}
		else if ((e instanceof IProjectile || e instanceof EntityFireball || e instanceof CustomProjectile) && AbilityHelper.instance.canProjectilePenetrateBlock(world, x, y, z, b, e)) {
			return null;
		}
		return b.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	public static MovingObjectPosition getInterceptedRaytrace(Entity e, Vec3 vec1, Vec3 vec2) {
		return getInterceptedRaytrace(e, vec1, vec2, false, false, false);
	}

	public static MovingObjectPosition getInterceptedRaytrace(Entity e, Vec3 vec1, Vec3 vec2, boolean b1, boolean b2, boolean b3) {
		if (e instanceof IProjectile || e instanceof EntityFireball || e instanceof CustomProjectile) {
			if (AbilityHelper.instance.canProjectilePenetrateBlocks(e)) {
				return AbilityHelper.instance.getProjectileRayTrace(e, vec1, vec2, b1, b2, b3);
			}
		}
		return e.worldObj.func_147447_a(vec1, vec2, b1, b2, b3);
	}

	public static TileEntityLootChest generateLootChest(World world, int x, int y, int z, int m, String s, int bonus) {
		world.setBlock(x, y, z, ChromaBlocks.LOOTCHEST.getBlockInstance(), m, 3);
		TileEntityLootChest te = (TileEntityLootChest)world.getTileEntity(x, y, z);
		te.populateChest(s, null, bonus, world.rand);
		return te;
	}
}
