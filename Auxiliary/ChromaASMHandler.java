/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Interfaces.ASMEnum;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@SortingIndex(1001)
@MCVersion("1.7.10")
public class ChromaASMHandler implements IFMLLoadingPlugin {


	@Override
	public String[] getASMTransformerClass() {
		return new String[]{ASMExecutor.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	public static class ASMExecutor implements IClassTransformer {

		private static final HashMap<String, ClassPatch> classes = new HashMap();

		private static enum ClassPatch implements ASMEnum {
			ENDPROVIDER("net.minecraft.world.gen.ChunkProviderEnd", "ara"),
			REACHDIST("net.minecraft.client.multiplayer.PlayerControllerMP", "bje"),
			//CHARWIDTH("Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer"), //Thank you, Optifine T_T
			//CHUNKPOPLN("net.minecraft.world.gen.ChunkProviderServer", "ms"),
			//WORLDLIGHT("net.minecraft.world.World", "ahb"),
			//WORLDLIGHT2("net.minecraft.world.ChunkCache", "ahr"),
			//ENTITYCOLLISION("net.minecraft.entity.Entity", "sa"),
			COLLISIONBOXES("net.minecraft.world.World", "ahb"),
			ENTITYPUSHOUT("net.minecraft.client.entity.EntityPlayerSP", "blk"),
			//RAYTRACEHOOK1("net.minecraft.entity.projectile.EntityArrow", "zc"),
			//RAYTRACEHOOK2("net.minecraft.entity.projectile.EntityThrowable", "zk"),
			//RAYTRACEHOOK3("net.minecraft.entity.projectile.EntityFireball", "ze"),
			STOPSLOWFALL("net.minecraft.entity.EntityLivingBase", "sv"),
			TRANSPARENCY1("net.minecraft.block.BlockDirt", "akl"),
			TRANSPARENCY2("net.minecraft.block.BlockGrass", "alh"),
			TRANSPARENCY3("net.minecraft.block.BlockLiquid", "alw"),
			TRANSPARENCY4("net.minecraftforge.fluids.BlockFluidBase"),
			UPDATEDCLIMATE("climateControl.biomeSettings.ReikasPackage"),
			//FLOWERCACHE("Reika.ChromatiCraft.ModInterface.Bees.EfficientFlowerCache"),
			TEXTURELOAD("net.minecraft.client.renderer.texture.TextureAtlasSprite", "bqd"),
			LOREHANDLER("Reika.ChromatiCraft.Magic.Lore.LoreScripts"),
			ROSETTAHANDLER("Reika.ChromatiCraft.Magic.Lore.RosettaStone"),
			SPLITWORLDLISTS("net.minecraft.client.renderer.RenderList", "bmd"),
			STOPLIGHTUPDATES("net.minecraft.client.multiplayer.WorldClient", "bjf"),
			//F3COORDS("net.minecraftforge.client.GuiIngameForge"),
			;

			private final String obfName;
			private final String deobfName;

			private static final ClassPatch[] list = values();

			private ClassPatch(String name) {
				this(name, name);
			}

			private ClassPatch(String deobf, String obf) {
				obfName = obf;
				deobfName = deobf;
			}

			public void apply(ClassNode cn) {
				switch(this) {
					case ENDPROVIDER: { //THIS WORKS
						if (ModList.ENDEREXPANSION.isLoaded())
							break;
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73187_a", "initializeNoiseField", "([DIIIIII)[D");
						String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_76129_c" : "sqrt_float";
						AbstractInsnNode loc = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/util/MathHelper", func, "(F)F");
						while (loc.getOpcode() != Opcodes.FSTORE) {
							loc = loc.getNext();
						}
						InsnList call = new InsnList();
						call.add(new VarInsnNode(Opcodes.FLOAD, 23)); //+1 to all the arguments because of double_2nd somewhere lower on stack
						call.add(new VarInsnNode(Opcodes.FLOAD, 21));
						call.add(new VarInsnNode(Opcodes.FLOAD, 22));
						call.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/ChromatiCraft/Auxiliary/ChromaAux", "getIslandBias", "(FFF)F", false));
						call.add(new VarInsnNode(Opcodes.FSTORE, 23));
						m.instructions.insert(loc, call);
						ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
						break;
					}
					case REACHDIST: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78757_d", "getBlockReachDistance", "()F");
						m.instructions.insert(new InsnNode(Opcodes.I2F));
						m.instructions.insert(new FieldInsnNode(Opcodes.GETFIELD, "Reika/ChromatiCraft/Auxiliary/Ability/AbilityHelper", "playerReach", "I"));
						m.instructions.insert(new FieldInsnNode(Opcodes.GETSTATIC, "Reika/ChromatiCraft/Auxiliary/Ability/AbilityHelper", "instance", "LReika/ChromatiCraft/Auxiliary/Ability/AbilityHelper;"));
						AbstractInsnNode index = null;
						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode ain = m.instructions.get(i);
							if (ain.getOpcode() == Opcodes.FRETURN) {
								index = ain;
								break;
							}
						}
						if (index != null) {
							m.instructions.insertBefore(index, new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Math", "max", "(FF)F", false));
							ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
						}
						break;
					}/*
				case CHARWIDTH: { //[I to [F
					try {
						Class optifine = Class.forName("optifine.OptiFineClassTransformer");
						ReikaASMHelper.log("Optifine loaded. Editing FontRenderer class.");
					}
					catch (Exception e) {
						ReikaASMHelper.log("Optifine loaded. Not editing FontRenderer class.");
						break;
					}
					/*
					//FieldNode fn = ReikaASMHelper.getFieldByName(cn, "charWidth");
					//cn.fields.remove(fn);
					String field = FMLForgePlugin.RUNTIME_DEOBF ? "field_78286_d" : "charWidth";
					int count = 0;
					boolean primed = false;
					for (MethodNode m : cn.methods) {
						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode ain = m.instructions.get(i);
							if (ain instanceof FieldInsnNode) {
								FieldInsnNode fin = (FieldInsnNode)ain;

								if (fin.name.equals(field) && fin.desc.equals("[I")) {
									fin.desc = "[F";
									count++;
									ReikaASMHelper.log("Successfully applied "+this+" ASM handler x"+count+"!");
									primed = true;
								}
								/*
								if (FMLForgePlugin.RUNTIME_DEOBF && fin.name.equals("charWidth")) {
									fin.name = "field_78286_d";
									fin.owner = "net/minecraft/client/gui/FontRenderer";
								}*//*
							}
							else if (primed && (ain.getOpcode() == Opcodes.IALOAD || ain.getOpcode() == Opcodes.IASTORE)) {
								if (ain.getOpcode() == Opcodes.IALOAD)
									ReikaASMHelper.changeOpcode(ain, Opcodes.FALOAD);
								if (ain.getOpcode() == Opcodes.IASTORE)
									ReikaASMHelper.changeOpcode(ain, Opcodes.FASTORE);
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler x"+count+"b!");
								primed = false;
							}
						}
					}*//*
				break;
				}*//*
					case WORLDLIGHT:
					case WORLDLIGHT2: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72802_i", "getLightBrightnessForSkyBlocks", "(IIII)I");
						m.instructions.clear();
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
						m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
						m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
						m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
						m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/ChromatiCraft/Auxiliary/ChromaAux", "overrideLightValue", "(Lnet/minecraft/world/IBlockAccess;IIII)I", false));
						m.instructions.add(new InsnNode(Opcodes.IRETURN));
						ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
						break;
					}
				 */
					/*
					case ENTITYCOLLISION: {
						String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_72945_a" : "getCollidingBoundingBoxes";
						String sig = "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;";
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70091_d", "moveEntity", "(DDD)V");
						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode ain = m.instructions.get(i);
							if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
								MethodInsnNode min = (MethodInsnNode)ain;
								if (min.owner.contains("World") && min.name.equals(func) && min.desc.equals(sig)) {

									min.owner = "Reika/ChromatiCraft/Auxiliary/ChromaAux";
									min.name = "interceptEntityCollision";
									//min.desc = sig;
									ReikaASMHelper.changeOpcode(min, Opcodes.INVOKESTATIC);

									//Remove ALOAD 0 and GETFIELD worldObj
									AbstractInsnNode world = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(min), Opcodes.GETFIELD, "net/minecraft/entity/Entity", "worldObj", "Lnet/minecraft/world/World;");
									AbstractInsnNode end = world;
									AbstractInsnNode start = end.getPrevious();
									/*
									ArrayList<AbstractInsnNode> li = new ArrayList();
									for (int k = m.instructions.indexOf(start); k <= m.instructions.indexOf(end); k++) {
										li.add(m.instructions.get(k));
									}
									li.add(min);
									ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(li));
					 *//*
									ReikaASMHelper.deleteFrom(m.instructions, start, end);
								}
							}
						}
					}
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
					  */
					case COLLISIONBOXES: {
						String sig = "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;";
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72945_a", "getCollidingBoundingBoxes", sig);
						if (m.attrs != null)
							m.attrs.clear();
						if (m.localVariables != null)
							m.localVariables.clear();
						m.instructions.clear();
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/ChromatiCraft/Auxiliary/ChromaAux", "interceptEntityCollision", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;", false));
						m.instructions.add(new InsnNode(Opcodes.ARETURN));
						ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
						break;
					}
					case ENTITYPUSHOUT: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_145771_j", "func_145771_j", "(DDD)Z");
						if (ReikaASMHelper.checkForClass("api.player.forge.PlayerAPITransformer")) {
							m = ReikaASMHelper.getMethodByName(cn, "localPushOutOfBlocks", "(DDD)Z"); //Try his method instead
						}
						AbstractInsnNode ain = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.GETFIELD);
						if (ain == null)
							ReikaASMHelper.throwConflict(this.toString(), cn, m, "Could not find field lookup");
						m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/ChromatiCraft/Auxiliary/ChromaAux", "applyNoclipPhase", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
						m.instructions.remove(ain);
						ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
						break;
					}/*
					case RAYTRACEHOOK1:
					case RAYTRACEHOOK2:
					case RAYTRACEHOOK3: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70071_h_", "onUpdate", "()V");

						String func1 = FMLForgePlugin.RUNTIME_DEOBF ? "func_149668_a" : "getCollisionBoundingBoxFromPool";
						String func2 = FMLForgePlugin.RUNTIME_DEOBF ? "func_72933_a" : "rayTraceBlocks";
						String func3 = FMLForgePlugin.RUNTIME_DEOBF ? "func_147447_a" : "func_147447_a";

						String world = FMLForgePlugin.RUNTIME_DEOBF ? "field_70170_p" : "worldObj";

						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode ain = m.instructions.get(i);
							if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
								MethodInsnNode min = (MethodInsnNode)ain;
								if (min.name.equals(func1)) {
									VarInsnNode pre = (VarInsnNode)ReikaASMHelper.getLastNonZeroALOADBefore(m.instructions, i);
									//m.instructions.remove(pre);
									pre.var = 0;
									min.owner = "Reika/ChromatiCraft/Auxiliary/ChromaAux";
									ReikaASMHelper.addLeadingArgument(min, "Lnet/minecraft/entity/Entity;");
									min.name = "getInterceptedCollisionBox";
									ReikaASMHelper.changeOpcode(min, Opcodes.INVOKESTATIC);
								}
								else if (min.name.equals(func2)) {
									AbstractInsnNode pre = ReikaASMHelper.getLastFieldRefBefore(m.instructions, i, world);
									m.instructions.remove(pre);
									min.owner = "Reika/ChromatiCraft/Auxiliary/ChromaAux";
									ReikaASMHelper.addLeadingArgument(min, "Lnet/minecraft/entity/Entity;");
									min.name = "getInterceptedRaytrace";
									ReikaASMHelper.changeOpcode(min, Opcodes.INVOKESTATIC);
								}
								else if (min.name.equals(func3)) {
									AbstractInsnNode pre = ReikaASMHelper.getLastFieldRefBefore(m.instructions, i, world);
									m.instructions.remove(pre);
									min.owner = "Reika/ChromatiCraft/Auxiliary/ChromaAux";
									ReikaASMHelper.addLeadingArgument(min, "Lnet/minecraft/entity/Entity;");
									min.name = "getInterceptedRaytrace";
									ReikaASMHelper.changeOpcode(min, Opcodes.INVOKESTATIC);
								}
							}
						}

						//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
						ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
						break;
					}*/
					case STOPSLOWFALL: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70612_e", "moveEntityWithHeading", "(FF)V");

						String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_72899_e" : "blockExists";
						MethodInsnNode blockExistsNode = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/world/World", func, "(III)Z");
						JumpInsnNode ifEqJump = (JumpInsnNode) ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(blockExistsNode), Opcodes.IFEQ);
						LabelNode jumpTo = ifEqJump.label;
						InsnList ins = new InsnList();
						ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/ChromatiCraft/World/Dimension/SkyRiverManagerClient", "stopSlowFall", "()Z", false));
						ins.add(new JumpInsnNode(Opcodes.IFEQ, jumpTo));
						m.instructions.insert(ifEqJump, ins);
						ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
						break;
					}
					case TRANSPARENCY1:
					case TRANSPARENCY2:
					case TRANSPARENCY3:
					case TRANSPARENCY4: {
						InsnList li = new InsnList();

						li.add(new VarInsnNode(Opcodes.ALOAD, 1));
						li.add(new VarInsnNode(Opcodes.ILOAD, 2));
						li.add(new VarInsnNode(Opcodes.ILOAD, 3));
						li.add(new VarInsnNode(Opcodes.ILOAD, 4));
						li.add(new VarInsnNode(Opcodes.ALOAD, 0));
						li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/ChromatiCraft/Auxiliary/ChromaAux", "groundOpacity", "(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraft/block/Block;)I", false));
						li.add(new InsnNode(Opcodes.IRETURN));

						ReikaASMHelper.addMethod(cn, li, "getLightOpacity", "(Lnet/minecraft/world/IBlockAccess;III)I", Modifier.PUBLIC);

						break;
					}
					case UPDATEDCLIMATE: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "freshBiomeSetting", "()LclimateControl/api/BiomeSettings;");
						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode ain = m.instructions.get(i);
							if (ain.getOpcode() == Opcodes.NEW) {
								TypeInsnNode tin = (TypeInsnNode)ain;
								tin.desc = "Reika/ChromatiCraft/ModInterface/ChromaClimateControl";
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");
							}
							else if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
								MethodInsnNode min = (MethodInsnNode)ain;
								min.owner = "Reika/ChromatiCraft/ModInterface/ChromaClimateControl";
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");
							}
						}
						break;
					}/*
					case FLOWERCACHE: {
						cn.superName = "forestry/apiculture/HasFlowersCache";

						String sig = "(Lforestry/api/apiculture/IBee;Lforestry/api/apiculture/IBeeHousing;)Z";
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "hasFlowers", sig);
						m.instructions.clear();

						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, cn.superName, "hasFlowers", sig, false));
						m.instructions.add(new InsnNode(Opcodes.IRETURN));

						sig = "(Lforestry/api/core/INBTTagable;)V";
						m = ReikaASMHelper.getMethodByName(cn, "<init>", sig);
						//m.instructions.insert(new MethodInsnNode(Opcodes.INVOKESPECIAL, cn.superName, "<init>", sig, false));
						//m.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 0));
						MethodInsnNode min = (MethodInsnNode)ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.INVOKESPECIAL);
						min.owner = cn.superName;
						break;
					}*/
					case TEXTURELOAD: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147964_a", "loadSprite", "([Ljava/awt/image/BufferedImage;Lnet/minecraft/client/resources/data/AnimationMetadataSection;Z)V");

						InsnList li = new InsnList();

						li.add(new VarInsnNode(Opcodes.ALOAD, 0));
						li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/ChromatiCraft/Auxiliary/ChromaAux", "onIconLoad", "(L"+cn.name+";)V", false));

						m.instructions.insertBefore(ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.RETURN), li);

						//ReikaASMHelper.log(ReikaASMHelper.clearString(m.instructions));
						ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
						break;
					}
					case LOREHANDLER: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "getDataStream", "()Ljava/io/InputStream;");
						m.instructions.clear();
						m.instructions.add(new LdcInsnNode("Reika.ChromatiCraft.ChromatiCraft"));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false));
						m.instructions.add(new LdcInsnNode("Resources/lore.png"));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getResourceAsStream", "(Ljava/lang/String;)Ljava/io/InputStream;", false));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "javax/imageio/ImageIO", "read", "(Ljava/io/InputStream;)Ljava/awt/image/BufferedImage;", false));
						m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 1));
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Libraries/IO/ImageToStringConverter", "decodeToString", "(Ljava/awt/image/BufferedImage;)Ljava/lang/String;", false));
						m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 2));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64$Decoder;", false));
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Base64$Decoder", "decode", "(Ljava/lang/String;)[B", false));
						m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 3));
						m.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/io/ByteArrayInputStream"));
						m.instructions.add(new InsnNode(Opcodes.DUP));
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/io/ByteArrayInputStream", "<init>", "([B)V", false));
						m.instructions.add(new InsnNode(Opcodes.ARETURN));
						break;
					}
					case ROSETTAHANDLER: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "loadInternalRosettaFile", "()Ljava/util/ArrayList;");
						m.instructions.clear();
						m.instructions.add(new LdcInsnNode("Reika.ChromatiCraft.ChromatiCraft"));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false));
						m.instructions.add(new LdcInsnNode("Resources/rosetta.png"));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getResourceAsStream", "(Ljava/lang/String;)Ljava/io/InputStream;", false));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "javax/imageio/ImageIO", "read", "(Ljava/io/InputStream;)Ljava/awt/image/BufferedImage;", false));
						m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 1));
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Libraries/IO/ImageToStringConverter", "decodeToString", "(Ljava/awt/image/BufferedImage;)Ljava/lang/String;", false));
						m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 2));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64$Decoder;", false));
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Base64$Decoder", "decode", "(Ljava/lang/String;)[B", false));
						m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 3));
						m.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/io/ByteArrayInputStream"));
						m.instructions.add(new InsnNode(Opcodes.DUP));
						m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/io/ByteArrayInputStream", "<init>", "([B)V", false));
						m.instructions.add(new InsnNode(Opcodes.ICONST_1));
						m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/IO/ReikaFileReader", "getFileAsLines", "(Ljava/io/InputStream;Z)Ljava/util/ArrayList;", false));
						m.instructions.add(new InsnNode(Opcodes.ARETURN));
						break;
					}
					case SPLITWORLDLISTS: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78419_a", "callLists", "()V");
						MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, "glCallLists");
						min.owner = "Reika/ChromatiCraft/Auxiliary/Render/WorldRenderIntercept";
						min.name = "callGlLists";
						min.setOpcode(Opcodes.INVOKESTATIC);
						break;
					}
					case STOPLIGHTUPDATES: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72835_b", "tick", "()V");
						String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_73156_b" : "unloadQueuedChunks"; //contrary to name all this does is update light
						MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, func);
						min.owner = "Reika/ChromatiCraft/Auxiliary/ChromaAux";
						min.name = "interceptClientChunkUpdates";
						min.desc = "(Lnet/minecraft/client/multiplayer/ChunkProviderClient;)Z";
						min.setOpcode(Opcodes.INVOKESTATIC);
						break;
					}/*
					case F3COORDS: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "renderHUDText", "(II)V");
						String floor = FMLForgePlugin.RUNTIME_DEOBF ? "func_76142_g" : "wrapAngleTo180_float";
						AbstractInsnNode start = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.LDC, "x: %.5f (%d) // c: %d (%d)");
						int left = 3;
						int decr = 0;
						for (int i = m.instructions.indexOf(start); i < m.instructions.size(); i++) {
							AbstractInsnNode ain = m.instructions.get(i);
							if (ain.getOpcode() == Opcodes.AASTORE) {
								m.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/ChromatiCraft/Auxiliary/ChromaAux", "getPlayerCoordForF3", "(Ljava/lang/Object;)Ljava/lang/Object;", false));
								i += 2;
							}
							else if (ain.getOpcode() == Opcodes.LDC) {
								LdcInsnNode ldc = (LdcInsnNode)ain;
								if (ldc.cst instanceof String) {
									ldc.cst = ((String)ldc.cst).replaceAll("%d", "%.0f");
								}
							}
							else if (ain instanceof MethodInsnNode) {
								String call = ((MethodInsnNode)ain).name;
								if (call.equals(floor)) {
									decr = 1;
								}
							}
							left -= decr;
							if (left <= 0)
								break;
						}
						ReikaASMHelper.log(ReikaASMHelper.clearString(m.instructions));
						break;
					}*/
				}

			}
		}

		@Override
		public byte[] transform(String className, String className2, byte[] opcodes) {
			if (!classes.isEmpty()) {
				ClassPatch p = classes.get(className);
				if (p != null) {
					ReikaASMHelper.activeMod = "ChromatiCraft";
					ReikaASMHelper.log("Patching class "+p.deobfName);

					ClassNode cn = new ClassNode();
					ClassReader classReader = new ClassReader(opcodes);
					classReader.accept(cn, 0);
					p.apply(cn);
					ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS/* | ClassWriter.COMPUTE_FRAMES*/);
					cn.accept(writer);
					opcodes = writer.toByteArray();
					classes.remove(className); //for maximizing performance
					ReikaASMHelper.activeMod = null;
				}
			}
			return opcodes;
		}

		static {
			for (int i = 0; i < ClassPatch.list.length; i++) {
				ClassPatch p = ClassPatch.list[i];
				String s = !FMLForgePlugin.RUNTIME_DEOBF ? p.deobfName : p.obfName;
				classes.put(s, p);
			}
		}
	}

}
