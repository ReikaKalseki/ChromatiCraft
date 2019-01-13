/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Ability;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.RainbowTreeEffects;
import Reika.ChromatiCraft.Auxiliary.Event.DimensionPingEvent;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.StructurePair;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Entity.EntityAbilityFireball;
import Reika.ChromatiCraft.Entity.EntityNukerBall;
import Reika.ChromatiCraft.Items.Tools.ItemInventoryLinker;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.ModInterface.TileEntityLifeEmitter;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFireFX;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledBlockPlace;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledPacket;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledSoundEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.ParticleController.BlendListColorController;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import Reika.ReactorCraft.Entities.EntityRadiation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.BlockFluidBase;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;


public class AbilityCalls {

	public static void convertBufferToLP(EntityPlayer ep, int data) {
		if (data > 0)
			ep.heal(data); //undo damage dealt
		PlayerElementBuffer.instance.removeFromPlayer(ep, TileEntityLifeEmitter.getLumensPerHundredLP());
	}

	@SideOnly(Side.CLIENT)
	public
	static void doNukerFX(World world, int x, int y, int z, EntityPlayer ep) {
		double lx = x+0.5-ep.posX;
		double ly = y+0.5-ep.posY;
		double lz = z+0.5-ep.posZ;
		ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(BlockKey.getAt(world, x, y, z).asItemStack());
		int c = tag == null || tag.isEmpty() ? 0x22aaff : tag.asWeightedRandom().getRandomEntry().getColor();
		for (double d = 0.125; d <= 1; d += 0.03125/2) {
			double dx = ep.posX+d*lx;
			double dy = ep.posY+d*ly;
			double dz = ep.posZ+d*lz;
			EntityBlurFX fx = new EntityBlurFX(world, dx, dy, dz).setLife(5).setAlphaFading().setScale(0.5F).setColor(c);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	//@ModDependent(ModList.FORESTRY)
	public static void analyzeBees(EntityPlayer ep) {
		int slot = (int)(ep.worldObj.getTotalWorldTime()%ep.inventory.mainInventory.length);
		ItemStack is = ep.inventory.mainInventory[slot];
		AbilityHelper.instance.analyzeGenes(is);
	}

	public static void setNoclipState(EntityPlayer ep, boolean set) {
		if (AbilityHelper.instance.isNoClipEnabled != set) {
			AbilityHelper.instance.isNoClipEnabled = set;
			if (set) {
				AbilityHelper.instance.onNoClipEnable(ep);
			}
			else {
				AbilityHelper.instance.onNoClipDisable(ep);
			}
			ChromatiCraft.logger.debug("Noclip state changed to "+set);
		}
		else if (set) {
			if (ep.worldObj.isRemote && ep.ticksExisted%24 == 0)
				ReikaSoundHelper.playClientSound(ChromaSounds.NOCLIPRUN, ep, 1, 1);
		}
		//ep.noClip = set;// && ((ep.capabilities.allowFlying && ep.capabilities.isFlying) || ep.isSneaking() || KeyWatcher.instance.isKeyDown(ep, Key.JUMP));
		/*if (ep.noClip) {
			ep.moveEntity(-ep.motionX, -ep.motionY, -ep.motionZ);
			List<AxisAlignedBB> li = ep.worldObj.getCollidingBoundingBoxes(ep, ep.boundingBox.addCoord(ep.motionX, ep.motionY, ep.motionZ));//AbilityHelper.instance.getNoclipBlockBoxes(ep);
			//ReikaJavaLibrary.pConsole(locs);

			double d6 = ep.motionX;
			double d7 = ep.motionY;
			double d8 = ep.motionZ;

			AxisAlignedBB epbox = ep.boundingBox;//.addCoord(ep.motionX, ep.motionY, ep.motionZ);

			//ReikaJavaLibrary.pConsole("S: "+epbox+"+"+li+"="+(li.isEmpty() ? false : li.get(0).intersectsWith(epbox))+" & "+ep.motionY, Side.SERVER);

			for (AxisAlignedBB box : li) {
				ep.motionY = box.calculateYOffset(box, ep.motionY);
			}

			epbox.offset(0.0D, ep.motionY, 0.0D);

			if (!ep.field_70135_K && d7 != ep.motionY) {
				ep.motionZ = 0.0D;
				ep.motionY = 0.0D;
				ep.motionX = 0.0D;
			}

			boolean flag1 = ep.onGround || d7 != ep.motionY && d7 < 0.0D;
			int j;

			for (AxisAlignedBB box : li) {
				ep.motionX = box.calculateXOffset(box, ep.motionX);
			}

			epbox.offset(ep.motionX, 0.0D, 0.0D);

			if (!ep.field_70135_K && d6 != ep.motionX) {
				ep.motionZ = 0.0D;
				ep.motionY = 0.0D;
				ep.motionX = 0.0D;
			}

			for (AxisAlignedBB box : li) {
				ep.motionZ = box.calculateZOffset(box, ep.motionZ);
			}

			epbox.offset(0.0D, 0.0D, ep.motionZ);

			if (!ep.field_70135_K && d8 != ep.motionZ) {
				ep.motionZ = 0.0D;
				ep.motionY = 0.0D;
				ep.motionX = 0.0D;
			}

			ep.posX = (epbox.minX + epbox.maxX) / 2.0D;
			ep.posY = epbox.minY + ep.yOffset - ep.ySize;
			ep.posZ = (epbox.minZ + epbox.maxZ) / 2.0D;
			ep.isCollidedHorizontally = d6 != ep.motionX || d8 != ep.motionZ;
			ep.isCollidedVertically = d7 != ep.motionY;
			ep.onGround = d7 != ep.motionY && d7 < 0.0D;
			ep.isCollided = ep.isCollidedHorizontally || ep.isCollidedVertically;
			//ep.updateFallState(ep.motionY, ep.onGround);

			if (d6 != ep.motionX) {
				ep.motionX = 0.0D;
			}

			if (d7 != ep.motionY) {
				ep.motionY = 0.0D;
			}

			if (d8 != ep.motionZ) {
				ep.motionZ = 0.0D;
			}


			//ReikaJavaLibrary.pConsole("E: "+epbox+"+"+li+"="+(li.isEmpty() ? false : li.get(0).intersectsWith(epbox))+" & "+ep.motionY, Side.SERVER);
		}*/
	}

	public static boolean spawnLightning(EntityPlayer ep, int power) {
		if (!ep.worldObj.isRemote) {
			MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 128, false);
			if (mov != null) {
				World world = ep.worldObj;
				int x = mov.blockX;
				int y = mov.blockY;
				int z = mov.blockZ;
				if (world.canBlockSeeTheSky(x, y+1, z) && ReikaPlayerAPI.playerCanBreakAt((WorldServer)ep.worldObj, x, y, z, (EntityPlayerMP)ep)) {
					world.addWeatherEffect(new EntityLightningBolt(world, x+0.5, y+0.5, z+0.5));
					int r = 2+power*4;
					if (power == 2) {
						new FlyingBlocksExplosion(world, x+0.5, y-2.5, z+0.5, 6).setTumbling(new LightningTumble(world, x, y, z, r)).doExplosion();
					}
					else if (power == 1) {
						world.newExplosion(null, x+0.5, y-0.5, z+0.5, 4, true, true);
					}
					for (int i = -r; i <= r; i++) {
						for (int j = -r; j <= r; j++) {
							for (int k = -r; k <= r; k++) {
								int dx = x+i;
								int dy = y+j;
								int dz = z+k;
								if (ReikaWorldHelper.flammable(world, dx, dy, dz))
									ReikaWorldHelper.ignite(world, dx, dy, dz);
							}
						}
					}
					return true;
				}
				else {
					ChromaSounds.ERROR.playSound(ep);
					return false;
				}
			}
		}
		return false;
	}

	public static void teleportPlayerMenu(EntityPlayer ep) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.TELEPORT.ordinal(), ep.worldObj, 0, 0, 0);
	}

	public static void causeShockwave(EntityPlayer ep) {
		if (ep.worldObj.isRemote) {
			spawnShockwaveParticles(ep);
		}
		else {
			ChromaSounds.SHOCKWAVE.playSound(ep);
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(ep.posX, ep.posY, ep.posZ, ep.posX, ep.posY, ep.posZ).expand(16, 4, 16);
			List<EntityLivingBase> li = ep.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
			for (EntityLivingBase e : li) {
				if (e != ep && ReikaMathLibrary.py3d(e.posX-ep.posX, 0, e.posZ-ep.posZ) <= 16) {
					ReikaEntityHelper.knockbackEntity(ep, e, 4);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void spawnShockwaveParticles(EntityPlayer ep) {
		for (int i = 0; i < 360; i++) {
			double dx = Math.cos(Math.toRadians(i));
			double dz = Math.sin(Math.toRadians(i));
			double vx = dx*0.5;
			double vz = dz*0.5;
			EntityCenterBlurFX fx = new EntityCenterBlurFX(ep.worldObj, ep.posX, ep.posY-1.62+0.1, ep.posZ, vx, 0, vz).setColor(0x0080ff).setScale(2);
			fx.noClip = false;
			if (i%4 == 0) {
				fx.setColor(0xffffff);
			}
			else if (i%2 == 0) {
				fx.setColor(0x0000ff);
			}
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);

			if (i%30 == 0) {
				for (double d = 0.25; d <= 16; d += 0.5) {
					EntityCenterBlurFX fx2 = new EntityCenterBlurFX(ep.worldObj, ep.posX+dx*d, ep.posY-1.62+0.1, ep.posZ+dz*d, 0, 0, 0).setScale(4);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
				}
			}
		}
	}

	public static void addInvPage(EntityPlayer ep) {
		if (ep.worldObj.isRemote)
			return;
		AbilityHelper.instance.addInventoryPage(ep);
		PlayerElementBuffer.instance.removeFromPlayer(ep, AbilityHelper.instance.getElementsFor(Chromabilities.HOTBAR));
	}

	public static void setPlayerMaxHealth(EntityPlayer ep, int value) {
		float factor = value/10F;
		//ReikaJavaLibrary.pConsole(added+":"+add+":"+ep.getMaxHealth());
		ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).removeModifier(new AttributeModifier(Chromabilities.HEALTH_UUID, "Chroma", 0, 2));
		if (value > 0) {
			ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier(Chromabilities.HEALTH_UUID, "Chroma", factor, 2));
			//if (added > 0)
			//	ep.heal(added);
		}
		ep.setHealth(Math.min(ep.getHealth(), ep.getMaxHealth()));
		if (ep instanceof EntityPlayerMP)
			AbilityHelper.instance.syncHealth((EntityPlayerMP)ep);
	}

	public static void attractItemsAndXP(EntityPlayer ep, int range, boolean nc) {
		World world = ep.worldObj;
		double x = ep.posX;
		double y = ep.posY+1.5;
		double z = ep.posZ;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(range, range, range);
		List<EntityItem> inbox = world.getEntitiesWithinAABB(EntityItem.class, box);
		for (EntityItem ent : inbox) {
			if (ent.isDead)
				continue;
			ReikaEntityHelper.setInvulnerable(ent, true);
			if (ent.delayBeforeCanPickup == 0) {
				double dx = (x+0.5 - ent.posX);
				double dy = (y+0.5 - ent.posY);
				double dz = (z+0.5 - ent.posZ);
				double ddt = ReikaMathLibrary.py3d(dx, dy, dz);
				if (ReikaMathLibrary.py3d(dx, 0, dz) < 1) {
					ent.onCollideWithPlayer(ep);
				}
				else {
					ent.motionX += dx/ddt/ddt/1;
					ent.motionY += dy/ddt/ddt/2;
					ent.motionZ += dz/ddt/ddt/1;
					ent.motionX = MathHelper.clamp_double(ent.motionX, -0.75, 0.75);
					ent.motionY = MathHelper.clamp_double(ent.motionY, -0.75, 0.75);
					ent.motionZ = MathHelper.clamp_double(ent.motionZ, -0.75, 0.75);
					if (ent.posY < y)
						ent.motionY += 0.125;
					if (ent.posY < 0)
						ent.motionY = Math.max(1, ent.motionY);
					if (!world.isRemote)
						ent.velocityChanged = true;
				}
			}
			if (ent.age >= ent.lifespan-5)
				ent.age = 0;
			if (nc)
				ent.noClip = true;
			if (!ent.getEntityData().hasKey("cc_magnetized"))
				ent.getEntityData().setString("cc_magnetized", ep.getUniqueID().toString());
		}
		List<EntityXPOrb> inbox2 = world.getEntitiesWithinAABB(EntityXPOrb.class, box);
		for (EntityXPOrb ent : inbox2) {
			if (ent.isDead)
				continue;
			ReikaEntityHelper.setInvulnerable(ent, true);
			double dx = (x+0.5 - ent.posX);
			double dy = (y+0.5 - ent.posY);
			double dz = (z+0.5 - ent.posZ);
			double ddt = ReikaMathLibrary.py3d(dx, dy, dz);
			if (ReikaMathLibrary.py3d(dx, 0, dz) < 1) {
				ent.onCollideWithPlayer(ep);
			}
			else {
				ent.motionX += dx/ddt/ddt/2;
				ent.motionY += dy/ddt/ddt/2;
				ent.motionZ += dz/ddt/ddt/2;
				ent.motionX = MathHelper.clamp_double(ent.motionX, -0.75, 0.75);
				ent.motionY = MathHelper.clamp_double(ent.motionY, -0.75, 0.75);
				ent.motionZ = MathHelper.clamp_double(ent.motionZ, -0.75, 0.75);
				if (ent.posY < y)
					ent.motionY += 0.1;
				if (ent.posY < 0)
					ent.motionY = Math.max(1, ent.motionY);
				if (!world.isRemote)
					ent.velocityChanged = true;
			}
			if (ent.xpOrbAge >= 6000)
				ent.xpOrbAge = 0;
			if (nc)
				ent.noClip = true;
			if (!ent.getEntityData().hasKey("cc_magnetized"))
				ent.getEntityData().setString("cc_magnetized", ep.getUniqueID().toString());
		}
	}

	public static void setReachDistance(EntityPlayer player, int dist) {
		if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
			EntityPlayerMP ep = (EntityPlayerMP)player;
			ep.theItemInWorldManager.setBlockReachDistance(dist > 0 ? dist : 5);
		}
		else {
			AbilityHelper.instance.playerReach = dist;
		}
	}

	public static void destroyBlocksAround(EntityPlayer ep, int power) {
		if (power <= 0)
			return;
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY)+1;
		int z = MathHelper.floor_double(ep.posZ);
		int r = power;
		if (!ep.worldObj.isRemote) {
			ItemHashMap<Integer> drops = new ItemHashMap();
			for (int i = -r; i <= r; i++) {
				for (int j = -r; j <= r; j++) {
					for (int k = -r; k <= r; k++) {
						int dx = x+i;
						int dy = y+j;
						int dz = z+k;
						if (ReikaMathLibrary.py3d(i, j, k) <= r+0.5) {
							Block b = ep.worldObj.getBlock(dx, dy, dz);
							if (b != Blocks.air && b.isOpaqueCube() && b.blockHardness >= 0) {
								int meta = ep.worldObj.getBlockMetadata(dx, dy, dz);
								if (b instanceof SemiUnbreakable && ((SemiUnbreakable)b).isUnbreakable(ep.worldObj, dx, dy, dz, meta)) {
									continue;
								}
								if (ep.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
									if (b == Blocks.grass || b == Blocks.stone || b == Blocks.dirt) {
										if (ep.worldObj.getBlockMetadata(dx, dy, dz) == 1)
											continue;
									}
								}
								if (ReikaPlayerAPI.playerCanBreakAt((WorldServer)ep.worldObj, dx, dy, dz, (EntityPlayerMP)ep)) {
									if (power > b.getExplosionResistance(ep, ep.worldObj, dx, dy, dz, ep.posX, ep.posY, ep.posZ)/12F) {
										ArrayList<ItemStack> li = b.getDrops(ep.worldObj, dx, dy, dz, meta, 0);
										if (b instanceof BlockTieredResource) {
											BlockTieredResource bt = (BlockTieredResource)b;
											li.clear();
											if (bt.isPlayerSufficientTier(ep.worldObj, dx, dy, dz, ep))
												li.addAll(bt.getHarvestResources(ep.worldObj, dx, dy, dz, 0, ep));
											else
												li.addAll(bt.getNoHarvestResources(ep.worldObj, dx, dy, dz, 0, ep));
										}
										ForgeEventFactory.fireBlockHarvesting(li, ep.worldObj, b, dx, dy, dz, meta, 0, 1, false, ep);
										for (ItemStack is : li) {
											Integer get = drops.get(is);
											int val = get == null ? 0 : get.intValue();
											drops.put(is, val+is.stackSize);
										}
										b.removedByPlayer(ep.worldObj, ep, dx, dy, dz, true);
										ReikaSoundHelper.playBreakSound(ep.worldObj, dx, dy, dz, b, 0.1F, 1F);
										ep.worldObj.setBlockToAir(dx, dy, dz);
									}
								}
							}
						}
					}
				}
			}
			for (ItemStack is : drops.keySet()) {
				int amt = drops.get(is);
				int max = is.getMaxStackSize();
				while (amt > 0) {
					int drop = Math.min(max, amt);
					amt -= drop;
					DecimalPosition pos = ReikaRandomHelper.getRandomSphericalPosition(x+0.5, y+0.5, z+0.5, r);
					//ReikaJavaLibrary.pConsole(drop+" of "+is+" @ "+pos);
					ReikaItemHelper.dropItem(ep.worldObj, pos.xCoord, pos.yCoord, pos.zCoord, ReikaItemHelper.getSizedItemStack(is, drop));
				}
			}
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1).expand(r, r, r);
			List<EntityXPOrb> li = ep.worldObj.getEntitiesWithinAABB(EntityXPOrb.class, box);
			int amt = 0;
			for (EntityXPOrb e : li) {
				if (e.getDistance(x+0.5, y+0.5, z+0.5) <= r+0.5) {
					amt += e.xpValue;
					e.setDead();
				}
			}
			ReikaWorldHelper.splitAndSpawnXP(ep.worldObj, x+0.5, y+0.5, z+0.5, amt);
		}
		ep.playSound("random.explode", power/6F, 2-power/6F);
	}

	static boolean shiftArea(WorldServer world, BlockBox box, ForgeDirection dir, int dist, EntityPlayerMP ep) {
		FilledBlockArray moved = new FilledBlockArray(world);
		BlockArray toDel = new BlockArray();
		toDel.setWorld(world);
		int air = 0;
		for (int i = 0; i < box.getSizeX(); i++) {
			for (int j = 0; j < box.getSizeY(); j++) {
				for (int k = 0; k < box.getSizeZ(); k++) {
					int x = i+box.minX;
					int y = j+box.minY;
					int z = k+box.minZ;
					Block b = world.getBlock(x, y, z);
					int meta = world.getBlockMetadata(x, y, z);
					if (!ep.capabilities.isCreativeMode) {
						if (b instanceof SemiUnbreakable && ((SemiUnbreakable)b).isUnbreakable(world, x, y, z, meta))
							continue;
					}
					if (ep.capabilities.isCreativeMode || !ReikaBlockHelper.isUnbreakable(world, x, y, z, b, meta, ep)) {
						if (!b.hasTileEntity(meta) || ChromaOptions.SHIFTTILES.getState()) {
							//if (ReikaWorldHelper.softBlocks(world, dx, dy, dz)) {
							moved.setBlock(x, y, z, b, meta);
							toDel.addBlockCoordinate(x, y, z);
							//}
							if (b.isAir(world, x, y, z))
								air++;
						}
					}
				}
			}
		}
		moved.offset(dir, dist);

		int factor = (int)(Math.pow((box.getVolume()-air), 1.25)*dist/5D);
		ElementTagCompound cost = AbilityHelper.instance.getUsageElementsFor(Chromabilities.SHIFT, ep).scale(factor);
		boolean nrg = PlayerElementBuffer.instance.playerHas(ep, cost);
		boolean flag = false;
		if (nrg && ReikaPlayerAPI.playerCanBreakAt(world, toDel, ep) && ReikaPlayerAPI.playerCanBreakAt(world, moved, ep)) {
			BlockArray toDrop = BlockArray.getIntersectedBox(toDel, moved);
			toDrop.setWorld(world);
			for (ItemStack is : toDrop.getAllDroppedItems(world, 0, ep)) {
				//ReikaPlayerAPI.addOrDropItem(is, ep);
			}
			toDel.clearArea();
			moved.place();
			PlayerElementBuffer.instance.removeFromPlayer(ep, cost);
			flag = true;
		}
		else {
			flag = false;
			ChromaSounds.ERROR.playSound(ep);
		}
		Chromabilities.SHIFT.setToPlayer(ep, false);
		return flag;
	}

	public static void healPlayer(EntityPlayer ep, int health) {
		ep.heal(health);
	}

	public static void launchFireball(EntityPlayer ep, int charge) {
		double[] look = ReikaVectorHelper.getPlayerLookCoords(ep, 2);
		EntityAbilityFireball ef = new EntityAbilityFireball(ep.worldObj, ep, look[0], look[1]+1, look[2]);
		Vec3 lookv = ep.getLookVec();
		ef.motionX = lookv.xCoord/5;
		ef.motionY = lookv.yCoord/5;
		ef.motionZ = lookv.zCoord/5;
		ef.accelerationX = ef.motionX;
		ef.accelerationY = ef.motionY;
		ef.accelerationZ = ef.motionZ;
		ef.field_92057_e = charge;
		ef.posY = ep.posY+1;
		if (!ep.worldObj.isRemote) {
			ep.worldObj.playSoundAtEntity(ep, "mob.ghast.fireball", 1, 1);
			ep.worldObj.spawnEntityInWorld(ef);
		}
	}

	public static void stopArrows(EntityPlayer ep) {
		if (!ep.worldObj.isRemote) {
			AxisAlignedBB box = ep.boundingBox.expand(6, 4, 6);
			List<EntityArrow> li = ep.worldObj.getEntitiesWithinAABB(EntityArrow.class, box);
			for (EntityArrow e : li) {
				if (e.shootingEntity != ep && (!(e.shootingEntity instanceof EntityPlayer) || MinecraftServer.getServer().isPVPEnabled())) { //bounceback code
					e.motionX *= -0.10000000149011612D;
					e.motionY *= -0.10000000149011612D;
					e.motionZ *= -0.10000000149011612D;
					e.rotationYaw += 180.0F;
					e.prevRotationYaw += 180.0F;
					e.ticksInAir = 0;
				}
			}
		}
	}

	public static void deAggroMobs(EntityPlayer ep) {
		AxisAlignedBB box = ep.boundingBox.expand(12, 12, 12);
		List<EntityMob> li = ep.worldObj.getEntitiesWithinAABB(EntityMob.class, box);
		for (EntityMob e : li) {
			if (!(e instanceof EntityEnderman || e instanceof EntityPigZombie)) {
				if (e.getEntityToAttack() == ep || e.getEntityToAttack() == null) {
					//e.setAttackTarget(null);
					//e.attackEntityFrom(DamageSource.causeMobDamage(ReikaEntityHelper.getDummyMob(ep.worldObj, e.posX, e.posY, e.posZ)), 0);
				}
				if (e instanceof EntityCreeper) {
					EntityCreeper ec = (EntityCreeper)e;
					if (ec.getEntityToAttack() != ep) {
						ec.setCreeperState(-1);
						ec.getDataWatcher().updateObject(18, (byte)0);
						ec.timeSinceIgnited = 0;
					}
				}
			}
			/*
			List<EntityAITaskEntry> tasks = e.targetTasks.taskEntries;
			for (int k = 0; k < tasks.size(); k++) {
				EntityAIBase a = tasks.get(k).action;
				if (a instanceof EntityAINearestAttackableTarget) {
					EntityAINearestAttackableTarget nat = (EntityAINearestAttackableTarget)a;
					nat.targetEntitySelector = new AbilityHelper.PlayerExemptAITarget(nat.targetEntitySelector);
				}
			}*/
		}
	}

	public static void breakSurroundingBlocks(EntityPlayer ep) {
		if (!ep.worldObj.isRemote) {
			for (int i = 0; i < 6; i++) {
				double ANGLE = 35;//22;
				double phi = ReikaRandomHelper.getRandomPlusMinus(ep.rotationYawHead+90, ANGLE);
				double theta = ReikaRandomHelper.getRandomPlusMinus(-ep.rotationPitch, ANGLE);
				double[] xyz = ReikaPhysicsHelper.polarToCartesian(1, theta, phi);
				Coordinate c = null;
				for (double d = 0; d <= 8; d += 0.125) {
					double dx = ep.posX+xyz[0]*d;
					double dy = ep.posY+1.62+xyz[1]*d;
					double dz = ep.posZ+xyz[2]*d;
					int x = MathHelper.floor_double(dx);
					int y = MathHelper.floor_double(dy);
					int z = MathHelper.floor_double(dz);
					Block b = ep.worldObj.getBlock(x, y, z);
					if (!b.isAir(ep.worldObj, x, y, z) && !ReikaBlockHelper.isLiquid(b) && b != Blocks.mob_spawner && !ReikaBlockHelper.isUnbreakable(ep.worldObj, x, y, z, b, ep.worldObj.getBlockMetadata(x, y, z), ep)) {
						if (ep.worldObj.getEntitiesWithinAABB(EntityNukerBall.class, ReikaAABBHelper.getBlockAABB(x, y, z)).isEmpty()) {
							c = new Coordinate(x, y, z);
							break;
						}
					}
				}
				if (c != null) {
					EntityNukerBall enb = new EntityNukerBall(ep.worldObj, ep, c);
					ep.worldObj.spawnEntityInWorld(enb);
					ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.NUKERLOC.ordinal(), new PacketTarget.RadiusTarget(ep, 64), c.xCoord, c.yCoord, c.zCoord, ep.getEntityId());
				}
			}
		}
	}

	public static void tickFireRain(EntityPlayer ep) {
		World world = ep.worldObj;
		if (world.isRemote) {
			doFireRainParticles(ep);
		}
		else {
			int x = MathHelper.floor_double(ep.posX);
			int z = MathHelper.floor_double(ep.posZ);
			BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
			int n = 4;
			if (biome.biomeName.contains("Dark Forest"))
				n = 32;
			for (int m = 0; m < n; m++) {
				int dr = m < 32 ? 128 : m < 64 ? 64 : 32;
				int dx = ReikaRandomHelper.getRandomPlusMinus(x, dr);
				int dz = ReikaRandomHelper.getRandomPlusMinus(z, dr);
				int dy = ReikaWorldHelper.getTopNonAirBlock(world, dx, dz, true);
				ReikaWorldHelper.ignite(world, dx, dy, dz);
				int r = m > 256 && world.rand.nextInt(4) == 0 ? 2 : 1;
				for (int i = -r; i <= r; i++) {
					for (int j = -r; j <= r; j++) {
						for (int k = -r; k <= r; k++) {
							int ddx = dx+i;
							int ddy = dy+j;
							int ddz = dz+k;
							Block b = world.getBlock(ddx, ddy, ddz);
							int meta = world.getBlockMetadata(ddx, ddy, ddz);
							ModWoodList wood = ModWoodList.getModWoodFromLeaf(b, meta);
							//ReikaJavaLibrary.pConsole(new BlockKey(b, meta)+" > "+wood);
							if (wood == ModWoodList.DARKWOOD) {
								//ReikaJavaLibrary.pConsole(new Coordinate(ddx, ddy, ddz));
								world.setBlock(ddx, ddy, ddz, Blocks.fire);
								if (world.rand.nextInt(60) == 0)
									world.newExplosion(ep, ddx+0.5, ddy+0.5, ddz+0.5, 8, true, true);
							}
						}
					}
				}
				/*
			if (world.rand.nextInt(20) == 0) {
				ReikaWorldHelper.temperatureEnvironment(world, dx, dy, dz, 910);
			}
			else if (world.rand.nextInt(200) == 0) {
				ReikaWorldHelper.temperatureEnvironment(world, dx, dy, dz, 1510);
			}
				 */
				ChromaSounds.FIRE.playSoundAtBlock(world, dx, dy, dz, 1.5F, 1+world.rand.nextFloat()*0.5F);
			}
			if (ep.ticksExisted%4 == 0) {
				ChromaSounds.FIRE.playSound(ep, 0.3F, 0.2F+world.rand.nextFloat());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void doFireRainParticles(EntityPlayer ep) {
		int n = 1+ep.worldObj.rand.nextInt(8);
		for (int i = 0; i < n; i++) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(ep.posX, 32);
			double rz = ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 32);
			double ry = ReikaRandomHelper.getRandomPlusMinus(ep.posY+32, 32);
			float g = (float)ReikaRandomHelper.getRandomPlusMinus(0.5, 0.25);
			int l = 200;
			EntityFX fx = new EntityFireFX(ep.worldObj, rx, ry, rz).setGravity(g).setScale(8).setLife(l);
			if (ep.worldObj.rand.nextInt(8) == 0)
				((EntityFireFX)fx).setExploding();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public static boolean doLightCast(EntityPlayer ep) {
		Coordinate c = new Coordinate(ep).offset(0, 1, 0);
		ProgressiveBreaker b = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(ep.worldObj, c.xCoord, c.yCoord, c.zCoord, 200);
		b.call = new LightCast(ep);
		b.player = ep;
		b.hungerFactor = 0;
		b.causeUpdates = false;
		b.breakAir = true;
		ChromaSounds.LIGHTCAST.playSound(ep);
		return true;
	}

	public static boolean doJump(EntityPlayer ep, int power) {
		ep.motionY += power/2D*(1+ep.worldObj.rand.nextDouble());
		ep.velocityChanged = true;
		ep.fallDistance -= 100;
		ChromaSounds.RIFT.playSound(ep, 1, 2);
		return true;
	}

	public static boolean doLaserPulse(EntityPlayer ep) {
		World world = ep.worldObj;
		MovingObjectPosition p = ReikaPlayerAPI.getLookedAtBlock(ep, 128, false);
		if (p == null || p.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
			return false;
		if (!world.canBlockSeeTheSky(p.blockX, p.blockY+1, p.blockZ))
			return false;
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue() && p.blockY < 90)
			return false;
		double px = p.blockX+0.5;
		double py = p.blockY+0.5;
		double pz = p.blockZ+0.5;
		if (world.isRemote) {
			doLaserPunchParticles(ep, px, py, pz);
		}
		else {
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(px, py, pz, px, py, pz).expand(64, 32, 64);
			List<EntityLiving> li = world.getEntitiesWithinAABB(EntityLiving.class, box);
			for (EntityLiving e : li) {
				e.attackEntityFrom(new ReikaEntityHelper.WrappedDamageSource(ChromatiCraft.pylonDamage[CrystalElement.BLUE.ordinal()], ep), Integer.MAX_VALUE);
			}
			double r = ReikaRandomHelper.getRandomPlusMinus(10D, 2D);
			double h = ReikaRandomHelper.getRandomBetween(r, r*4);
			for (int i = -(int)Math.ceil(r); i <= Math.ceil(r); i++) {
				for (int k = -(int)Math.ceil(r); k <= Math.ceil(r); k++) {
					for (int j = -(int)Math.ceil(h); j <= Math.ceil(h); j++) {
						if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r, h, r)) {
							double d = ReikaMathLibrary.py3d(i, 0, k);
							double dx = px+i;
							double dy = py+j;
							double dz = pz+k;
							int dpx = MathHelper.floor_double(dx);
							int dpy = MathHelper.floor_double(dy);
							int dpz = MathHelper.floor_double(dz);
							Block b = world.getBlock(dpx, dpy, dpz);
							int meta = world.getBlockMetadata(dpx, dpy, dpz);
							if (b == Blocks.bedrock && dpy <= 4)
								continue;
							if (b instanceof SemiUnbreakable && ((SemiUnbreakable)b).isUnbreakable(world, dpx, dpy, dpz, meta))
								continue;
							if (ReikaPlayerAPI.playerCanBreakAt((WorldServer)world, dpx, dpy, dpz, (EntityPlayerMP)ep)) {
								boolean flag = false;
								if ((0.5+0.5*world.rand.nextDouble())*d < r*(0.5+world.rand.nextDouble()*0.5)) {
									if (ReikaBlockHelper.isOre(b, meta)) {
										ItemStack is = ReikaBlockHelper.getSilkTouch(world, dpx, dpy, dpz, b, meta, ep, false);
										ItemStack out = FurnaceRecipes.smelting().getSmeltingResult(is);
										if (out != null) {
											out = out.copy();
											out.stackSize *= 2;
											EntityItem ei = ReikaItemHelper.dropItem(world, dx, dy, dz, out);
											ReikaEntityHelper.setInvulnerable(ei, true);
										}
									}
									else if (b instanceof BlockTieredResource && ((BlockTieredResource)b).isPlayerSufficientTier(world, dpx, dpy, dpz, ep)) {
										for (ItemStack is : ((BlockTieredResource)b).getHarvestResources(world, dpx, dpy, dpz, 3, ep)) {
											EntityItem ei = ReikaItemHelper.dropItem(world, dx, dy, dz, is);
											ReikaEntityHelper.setInvulnerable(ei, true);
										}
									}
									if (b instanceof BlockTNT) {
										((BlockTNT)b).func_150114_a(world, dpx, dpy, dpz, 1, ep); //NOT meta
									}
									world.setBlock(dpx, dpy, dpz, Blocks.air);
								}
							}
						}
					}
				}
			}
			for (float f = 0.1F; f <= 2; f *= 2) {
				ReikaSoundHelper.playSoundFromServer(world, px, py, pz, "random.explode", 2, f, true);
				ReikaSoundHelper.playSoundFromServer(world, ep.posX, ep.posY, ep.posZ, "random.explode", 1, f, true);
			}
			ChromaSounds.LASER.playSound(ep, 2, 1);
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	private static void doLaserPunchParticles(EntityPlayer ep, double px, double py, double pz) {
		int n = 2048+ep.worldObj.rand.nextInt(16384);
		double maxr = 32;
		for (int i = 0; i < n; i++) {
			double a = ep.worldObj.rand.nextDouble()*360;
			double r = ReikaRandomHelper.getRandomBetween(0, maxr);
			double rx = px+r*Math.sin(Math.toRadians(a));
			double rz = pz+r*Math.cos(Math.toRadians(a));
			double ry = ReikaRandomHelper.getRandomPlusMinus(py+1.5, 1)+(ep.worldObj.getTopSolidOrLiquidBlock(MathHelper.floor_double(rx), MathHelper.floor_double(rz))-ep.worldObj.getTopSolidOrLiquidBlock(MathHelper.floor_double(px), MathHelper.floor_double(pz)));
			int l = 40+ep.worldObj.rand.nextInt(120);
			double v = ReikaRandomHelper.getRandomPlusMinus(0.25, 0.125)/32D;
			double vx = (rx-px)*v;
			double vz = (rz-pz)*v;
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			float f = (float)(r/maxr);
			float s = (float)ReikaRandomHelper.getRandomPlusMinus(8D, 4D)+4*(1-f);
			int c = f < 0.5 ? ReikaColorAPI.mixColors(0xffffff, 0x00a0ff, 1-(f*2)) : ReikaColorAPI.mixColors(0x0000ff, 0x00a0ff, (f-0.5F)*2);
			EntityFX fx = new EntityBlurFX(ep.worldObj, rx, ry, rz, vx, vy, vz).setColor(c).setScale(s).setLife(l).setRapidExpand().setColliding();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		for (int i = 0; i < n/16; i++) {
			double a = ep.worldObj.rand.nextDouble()*360;
			double r = ReikaRandomHelper.getRandomPlusMinus(maxr+24, 4);
			double rx = px+r*Math.sin(Math.toRadians(a));
			double rz = pz+r*Math.cos(Math.toRadians(a));
			double ry = ReikaRandomHelper.getRandomPlusMinus(py+1.5, 1)+(ep.worldObj.getTopSolidOrLiquidBlock(MathHelper.floor_double(rx), MathHelper.floor_double(rz))-ep.worldObj.getTopSolidOrLiquidBlock(MathHelper.floor_double(px), MathHelper.floor_double(pz)));
			int l = 40+ep.worldObj.rand.nextInt(120);
			double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625)/64D;
			double vx = (rx-px)*v;
			double vz = (rz-pz)*v;
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			float s = (float)ReikaRandomHelper.getRandomPlusMinus(16D, 4D);
			int c = 0xa000ff;
			EntityFX fx = new EntityBlurFX(ep.worldObj, rx, ry, rz, vx, vy, vz).setColor(c).setScale(s).setLife(l).setRapidExpand().setColliding();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		for (double dy = py; dy < 1024; dy += 1) {
			EntityFX fx = new EntityBlurFX(ep.worldObj, px, dy, pz).setColor(0xffffff).setScale(16).setLife(120).setRapidExpand();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public static void doGrowthAura(EntityPlayer ep) {
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);
		if (ep.worldObj.isRemote) {
			doGrowthAuraParticles(ep, x, y, z);
		}
		else {
			RainbowTreeEffects.doRainbowTreeEffects(ep.worldObj, x, y, z, 4, 0.25, ep.worldObj.rand, false);
			for (int i = 0; i < 8; i++) {
				int dx = ReikaRandomHelper.getRandomPlusMinus(x, 8);
				int dz = ReikaRandomHelper.getRandomPlusMinus(z, 8);
				int dy = ReikaRandomHelper.getRandomPlusMinus(y, 2);
				ReikaWorldHelper.fertilizeAndHealBlock(ep.worldObj, dx, dy, dz);
				Block b = ep.worldObj.getBlock(dx, dy, dz);
				if (ModList.THAUMCRAFT.isLoaded() && b == ThaumItemHelper.BlockEntry.NODE.getBlock()) {
					healNodes(ep.worldObj, dx, dy, dz);
				}
				else {
					//if (b.canSustainPlant(ep.worldObj, dx, dy, dz, ForgeDirection.UP, Blocks.red_flower) && ep.worldObj.getBlock(dx, dy+1, dz).isAir(ep.worldObj, dx, dy+1, dz))
					if (ep.worldObj.rand.nextInt(b == Blocks.grass ? 18 : 6) == 0) {
						EntityPlayer fake = ReikaPlayerAPI.getFakePlayerByNameAndUUID((WorldServer)ep.worldObj, "Random", UUID.randomUUID());
						fake.setCurrentItemOrArmor(0, ReikaItemHelper.bonemeal.copy());
						ItemDye.applyBonemeal(fake.getCurrentEquippedItem().copy(), ep.worldObj, dx, dy, dz, fake);
					}
					else {
						b.updateTick(ep.worldObj, dx, dy, dz, ep.worldObj.rand);
					}
				}
			}
			if (ModList.REACTORCRAFT.isLoaded() && ep.worldObj.rand.nextInt(40) == 0) {
				cleanRadiation(ep);
			}
		}
	}

	@ModDependent(ModList.THAUMCRAFT)
	private static void healNodes(World world, int x, int y, int z) {
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

	@ModDependent(ModList.REACTORCRAFT)
	private static void cleanRadiation(EntityPlayer ep) {
		AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(ep, 8);
		for (EntityRadiation e : ((List<EntityRadiation>)ep.worldObj.getEntitiesWithinAABB(EntityRadiation.class, box))) {
			e.clean();
		}
	}

	@SideOnly(Side.CLIENT)
	private static void doGrowthAuraParticles(EntityPlayer ep, int x, int y, int z) {
		for (int i = 0; i < 4; i++)
			ChromaFX.doGrowthWandParticles(ep.worldObj, ReikaRandomHelper.getRandomPlusMinus(x, 4), y-1, ReikaRandomHelper.getRandomPlusMinus(z, 4));
		for (int i = 0; i < 6; i++) {
			//for (double a = 0; a < 360; a += 12.5) {
			double a = ep.worldObj.rand.nextDouble()*360;
			double r = ReikaRandomHelper.getRandomPlusMinus(2, 0.5);
			float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
			float s = 1.5F+ep.worldObj.rand.nextFloat();
			double dx = ep.posX+r*Math.cos(Math.toRadians(a));
			double dy = ep.posY-1.62;
			double dz = ep.posZ+r*Math.sin(Math.toRadians(a));
			int c = CrystalElement.MAGENTA.getColor();
			int l = 20+ep.worldObj.rand.nextInt(20);
			EntityFX fx = new EntityBlurFX(ep.worldObj, dx, dy, dz).setGravity(g).setScale(s).setColor(c).setLife(l).setRapidExpand().setIcon(ChromaIcons.CENTER);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		//}
	}

	public static void doDimensionPing(EntityPlayer ep) {
		if (ep.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			int x = MathHelper.floor_double(ep.posX);
			int z = MathHelper.floor_double(ep.posZ);

			for (StructurePair s : ChunkProviderChroma.getStructures()) {
				if (s.generator.isComplete()) {
					ChunkCoordIntPair loc = s.generator.getEntryLocation();
					int px = loc.chunkXPos << 4;
					int pz = loc.chunkZPos << 4;
					double dx = px-x;
					double dz = pz-z;
					double dist = ReikaMathLibrary.py3d(dx, 0, dz);
					double ang = ReikaDirectionHelper.getCompassHeading(dx, dz);
					double factor = Math.pow(dist, 1.6);
					factor = factor/20000D;
					int delay = Math.max(1, (int)factor);
					//ReikaJavaLibrary.pConsole(s.color+": DD="+dist+", ang="+ang+", factor="+factor+", delay="+delay);
					ScheduledSoundEvent evt = new DimensionPingEvent(s.color, ep, dist, ang);
					TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(evt), delay);
				}
			}
		}
		else {
			ChromaSounds.ERROR.playSound(ep);
		}
	}

	private static boolean isValidWaterBlocks(Block id, Block idbelow) {
		return (idbelow instanceof BlockLiquid || idbelow instanceof BlockFluidBase) && !((id instanceof BlockLiquid || id instanceof BlockFluidBase));
	}

	public static void waterRun(EntityPlayer ep) {
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);

		Block id = ep.worldObj.getBlock(x, y-1, z);
		Block idbelow = ep.worldObj.getBlock(x, y-2, z);

		if (isValidWaterBlocks(id, idbelow) && ReikaMathLibrary.py3d(ep.motionX, 0, ep.motionZ) >= 0.15) {
			ep.fallDistance = 0;
			if (ep instanceof EntityPlayerMP) {
				((EntityPlayerMP)ep).playerNetServerHandler.floatingTickCount = 0;
			}
			for (int i = 0; i < 8; i++)
				ReikaParticleHelper.RAIN.spawnAt(ep.worldObj, ReikaRandomHelper.getRandomPlusMinus(ep.posX, 0.25), ep.posY-1, ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 0.25));
			if (ep.ticksExisted%2 == 0)
				ep.playSound("random.splash", 0.0625F+ep.worldObj.rand.nextFloat()*0.25F, 0.25F+ep.worldObj.rand.nextFloat());

			ep.motionY = Math.max(0, ep.motionY);
			ep.setPosition(ep.posX, (int)ep.posY+0.7, ep.posZ);
			ep.addVelocity(0.05*ep.motionX, 0, 0.05*ep.motionZ);
		}

	}

	public static void superbuild(World world, int x, int y, int z, ForgeDirection dir, Block b, int meta, ItemStack is, EntityPlayer ep) {
		boolean reached = false;
		boolean hasBlock = true;
		boolean hitBlock = false;

		int delay = 1;

		double maxd = ReikaMathLibrary.py3d(ep.posX-x-0.5, ep.posY-y-0.5, ep.posZ-z-0.5);

		boolean reachedX = dir.offsetX != 0 && (dir.offsetX < 0 ? x <= ep.posX : x >= ep.posX);
		boolean reachedY = dir.offsetY != 0 && (dir.offsetY < 0 ? y <= ep.posY-1 : y >= ep.posY-1); //on this direction, stop at foot level
		boolean reachedZ = dir.offsetZ != 0 && (dir.offsetZ < 0 ? z <= ep.posZ : z >= ep.posZ);
		reached = reachedX || reachedY || reachedZ;

		ElementTagCompound tag = AbilityHelper.instance.getUsageElementsFor(Chromabilities.SUPERBUILD, ep).scale(0.05F);

		while (!reached && hasBlock && !hitBlock && PlayerElementBuffer.instance.playerHas(ep, tag)) {
			x += dir.offsetX;
			y += dir.offsetY;
			z += dir.offsetZ;
			hitBlock = !ReikaWorldHelper.softBlocks(world, x, y, z);
			if (!hitBlock) {
				//world.setBlock(x, y, z, b, meta, 3);
				//ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), world, x, y, z, 32, Block.getIdFromBlock(b), meta);
				if (!ep.capabilities.isCreativeMode)
					is.stackSize--;
				hasBlock = is.stackSize > 0;

				reachedX = dir.offsetX != 0 && (dir.offsetX < 0 ? x <= ep.posX : x >= ep.posX);
				reachedY = dir.offsetY != 0 && (dir.offsetY < 0 ? y <= ep.posY-1 : y >= ep.posY-1); //on this direction, stop at foot level
				reachedZ = dir.offsetZ != 0 && (dir.offsetZ < 0 ? z <= ep.posZ : z >= ep.posZ);
				reached = reachedX || reachedY || reachedZ;

				TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new ScheduledBlockPlace(world, x, y, z, b, meta)), delay);
				double d = ReikaMathLibrary.py3d(ep.posX-x-0.5, ep.posY-y-0.5, ep.posZ-z-0.5);
				float v = (float)(0.5*(1-d/maxd));
				TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new ScheduledSoundEvent(ChromaSounds.RIFT, world, ep.posX, ep.posY, ep.posZ, v, 2)), delay);
				TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new ScheduledPacket(ChromatiCraft.packetChannel, ChromaPackets.SUPERBUILD.ordinal(), world, x, y, z, 64, dir.ordinal())), delay);

				PlayerElementBuffer.instance.removeFromPlayer(ep, tag);

				delay = delay+(int)(5/Math.pow(delay, 0.33));
			}
		}

		if (!ep.capabilities.isCreativeMode) {
			is.stackSize--; //to compensate for the first block
			ep.setCurrentItemOrArmor(0, is);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doSuperbuildFX(World world, int x, int y, int z, ForgeDirection dir) {
		EntityBlurFX fx;
		double o = 0.0625;
		double ox = dir.offsetX != 0 ? 0 : o;
		double oy = dir.offsetY != 0 ? 0 : o;
		double oz = dir.offsetZ != 0 ? 0 : o;
		for (double d = -ox; d <= 1+ox; d += 0.0625) {
			createSuperbuildParticle(world, x+d, y-oy, z-oz);
			createSuperbuildParticle(world, x+d, y+1+oy, z-oz);
			createSuperbuildParticle(world, x+d, y-oy, z+1+oz);
			createSuperbuildParticle(world, x+d, y+1+oy, z+1+oz);
		}
		for (double d = -oy; d <= 1+oy; d += 0.0625) {
			createSuperbuildParticle(world, x-ox, y+d, z-oz);
			createSuperbuildParticle(world, x+1+ox, y+d, z-oz);
			createSuperbuildParticle(world, x-ox, y+d, z+1+oz);
			createSuperbuildParticle(world, x+1+ox, y+d, z+1+oz);
		}
		for (double d = -oz; d <= 1+oz; d += 0.0625) {
			createSuperbuildParticle(world, x-ox, y-oy, z+d);
			createSuperbuildParticle(world, x+1+ox, y-oy, z+d);
			createSuperbuildParticle(world, x-ox, y+1+oy, z+d);
			createSuperbuildParticle(world, x+1+ox, y+1+oy, z+d);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void createSuperbuildParticle(World world, double px, double py, double pz) {
		EntityBlurFX fx = new EntityBlurFX(world, px, py, pz).setLife(30).setScale(0.625F).setRapidExpand().setAlphaFading();
		fx.setColorController(new BlendListColorController(new ColorBlendList(10, 0xffffff, 0x22aaff, 0x0000ff, 0x000000)));
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	public static void doChestCollection(EntityPlayerMP ep) {
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);

		x = ReikaRandomHelper.getRandomPlusMinus(x, ep.getRNG().nextBoolean() ? 3 : 9);
		z = ReikaRandomHelper.getRandomPlusMinus(z, ep.getRNG().nextBoolean() ? 3 : 9);
		y = ReikaRandomHelper.getRandomPlusMinus(y, ep.getRNG().nextBoolean() ? 1 : 4);

		if (!ReikaPlayerAPI.playerCanBreakAt((WorldServer)ep.worldObj, x, y, z, ep)) {
			return;
		}

		Block b = ep.worldObj.getBlock(x, y, z);
		if (b instanceof BlockChest || b instanceof BlockLootChest) {
			TileEntity te = ep.worldObj.getTileEntity(x, y, z);
			if (te instanceof IInventory) {
				if (te instanceof TileEntityLootChest) {
					TileEntityLootChest tc = (TileEntityLootChest)te;
					if (!tc.isAccessibleBy(ep))
						return;
				}
				IInventory ii = (IInventory)te;
				int s = ii.getSizeInventory()-1;
				for (int i = 0; i <= s; i++) {
					ItemStack is = ii.getStackInSlot(i);
					if (is != null) {
						if (ItemInventoryLinker.tryLinkItem(ep, is)) {
							emptySlot(ep, x, y, z, b, ii, i, s);
						}
						else if (ReikaInventoryHelper.addToIInv(is, ep.inventory)) {
							emptySlot(ep, x, y, z, b, ii, i, s);
						}
						else {
							break;
						}
					}
					if (i == s && ii.getStackInSlot(s) == null)
						breakChest(ep, x, y, z, b); //made it to last slot and successfully emptied it, or it was empty
				}
			}
		}
	}

	private static void emptySlot(EntityPlayer ep, int x, int y, int z, Block b, IInventory ii, int slot, int size) {
		ii.setInventorySlotContents(slot, null);
		if (slot == size) { //chest is empty, since only makes it to last slot if all slots before are empty
			breakChest(ep, x, y, z, b);
		}
	}

	private static void breakChest(EntityPlayer ep, int x, int y, int z, Block b) {
		int meta = ep.worldObj.getBlockMetadata(x, y, z);
		ItemStack is = ReikaBlockHelper.getSilkTouch(ep.worldObj, x, y, z, b, meta, ep, false);
		if (is != null) {
			if (!ItemInventoryLinker.tryLinkItem(ep, is) && !ReikaInventoryHelper.addToIInv(is, ep.inventory))
				return;
		}
		ep.worldObj.setBlock(x, y, z, Blocks.air);
		ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), ep.worldObj, x, y, z, 24, Block.getIdFromBlock(b), meta);
		ReikaSoundHelper.playBreakSound(ep.worldObj, x, y, z, b);
		//ReikaWorldHelper.dropAndDestroyBlockAt(ep.worldObj, x, y, size, ep, true, true);
	}
	/*
	@SideOnly(Side.CLIENT)
	public static void doChestCollectionFX(EntityPlayer ep) {
		Collection<LightningBolt> c = AbilityHelper.instance.getCollectionBeamsForPlayer(ep);
		for (LightningBolt b : c) {
			ChromaFX.renderBolt(b, ReikaRenderHelper.getPartialTickTime(), 192, 0.1875, 6);
			b.update();
		}
	}*/

}
