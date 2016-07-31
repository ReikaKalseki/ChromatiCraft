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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;

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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

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
			CHUNKPOPLN("net.minecraft.world.gen.ChunkProviderServer", "ms"),
			//WORLDLIGHT("net.minecraft.world.World", "ahb"),
			//WORLDLIGHT2("net.minecraft.world.ChunkCache", "ahr"),
			//ENTITYCOLLISION("net.minecraft.entity.Entity", "sa"),
			COLLISIONBOXES("net.minecraft.world.World", "ahb"),
			ENTITYPUSHOUT("net.minecraft.client.entity.EntityPlayerSP", "blk"),
			RAYTRACEHOOK1("net.minecraft.entity.projectile.EntityArrow", "zc"),
			RAYTRACEHOOK2("net.minecraft.entity.projectile.EntityThrowable", "zk"),
			RAYTRACEHOOK3("net.minecraft.entity.projectile.EntityFireball", "ze"),
			STOPSLOWFALL("net.minecraft.entity.EntityLivingBase", "sv"),
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

			public byte[] apply(byte[] data) {
				ClassNode cn = new ClassNode();
				ClassReader classReader = new ClassReader(data);
				classReader.accept(cn, 0);
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
						m.instructions.insert(new FieldInsnNode(Opcodes.GETFIELD, "Reika/ChromatiCraft/Auxiliary/AbilityHelper", "playerReach", "I"));
						m.instructions.insert(new FieldInsnNode(Opcodes.GETSTATIC, "Reika/ChromatiCraft/Auxiliary/AbilityHelper", "instance", "LReika/ChromatiCraft/Auxiliary/AbilityHelper;"));
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
				}*/
					case CHUNKPOPLN: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73153_a", "populate", "(Lnet/minecraft/world/chunk/IChunkProvider;II)V");
						boolean primed = false;
						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode ain = m.instructions.get(i);
							if (ain.getOpcode() == Opcodes.INVOKEINTERFACE) {
								primed = true;
							}
							else if (primed && ain.getOpcode() == Opcodes.INVOKESTATIC) {
								MethodInsnNode min = (MethodInsnNode)ain;
								if (min.owner.contains("GameRegistry") && min.name.equals("generateWorld")) {
									primed = false;

									min.owner = "Reika/ChromatiCraft/Auxiliary/ChromaAux";
									min.name = "interceptChunkPopulation";
									min.desc = "(IILnet/minecraft/world/World;Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;)V";

									ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
									break;
								}
							}
						}
					}
					break;/*
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
					}
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
									ArrayList<String> li = ReikaASMHelper.parseMethodSignature(min);
									li.add(0, "Lnet/minecraft/entity/Entity;");
									min.desc = ReikaASMHelper.compileSignature(li);
									min.name = "getInterceptedCollisionBox";
									ReikaASMHelper.changeOpcode(min, Opcodes.INVOKESTATIC);
								}
								else if (min.name.equals(func2)) {
									AbstractInsnNode pre = ReikaASMHelper.getLastFieldRefBefore(m.instructions, i, world);
									m.instructions.remove(pre);
									min.owner = "Reika/ChromatiCraft/Auxiliary/ChromaAux";
									ArrayList<String> li = ReikaASMHelper.parseMethodSignature(min);
									li.add(0, "Lnet/minecraft/entity/Entity;");
									min.desc = ReikaASMHelper.compileSignature(li);
									min.name = "getInterceptedRaytrace";
									ReikaASMHelper.changeOpcode(min, Opcodes.INVOKESTATIC);
								}
								else if (min.name.equals(func3)) {
									AbstractInsnNode pre = ReikaASMHelper.getLastFieldRefBefore(m.instructions, i, world);
									m.instructions.remove(pre);
									min.owner = "Reika/ChromatiCraft/Auxiliary/ChromaAux";
									ArrayList<String> li = ReikaASMHelper.parseMethodSignature(min);
									li.add(0, "Lnet/minecraft/entity/Entity;");
									min.desc = ReikaASMHelper.compileSignature(li);
									min.name = "getInterceptedRaytrace";
									ReikaASMHelper.changeOpcode(min, Opcodes.INVOKESTATIC);
								}
							}
						}

						//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
						ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
						break;
					}
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
				}

				ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS/* | ClassWriter.COMPUTE_FRAMES*/);
				cn.accept(writer);
				return writer.toByteArray();
			}
		}

		@Override
		public byte[] transform(String className, String className2, byte[] opcodes) {
			if (!classes.isEmpty()) {
				ClassPatch p = classes.get(className);
				if (p != null) {
					ReikaASMHelper.activeMod = "ChromatiCraft";
					ReikaASMHelper.log("Patching class "+p.deobfName);
					opcodes = p.apply(opcodes);
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
