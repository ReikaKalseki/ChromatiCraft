/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

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
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

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

		private static enum ClassPatch {
			//ENDERCRYSTALGEN("net.minecraft.world.gen.feature.WorldGenSpikes", ""),
			ENDPROVIDER("net.minecraft.world.gen.ChunkProviderEnd", ""),
			//ENDPROVIDER2("net.minecraft.world.WorldProviderEnd", ""),
			REACHDIST("net.minecraft.client.multiplayer.PlayerControllerMP", "");

			private final String obfName;
			private final String deobfName;

			private static final ClassPatch[] list = values();

			private ClassPatch(String deobf, String obf) {
				obfName = obf;
				deobfName = deobf;
			}

			private byte[] apply(byte[] data) {
				ClassNode cn = new ClassNode();
				ClassReader classReader = new ClassReader(data);
				classReader.accept(cn, 0);
				switch(this) {/*
				case ENDERCRYSTALGEN: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "", "generate", "(Lnet/minecraft/world/World;Ljava/util/Random;III)Z");
					if (m == null) {
						ReikaJavaLibrary.pConsole("CHROMATICRAFT: Could not find method for "+this+" ASM handler!");
					}
					else {
						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode ain = m.instructions.get(i);
							if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
								MethodInsnNode min = (MethodInsnNode)ain;
								if (min.name.equals("<init>")) {
									String c = "Reika/ChromatiCraft/Entity/EntityChromaEnderCrystal";
									m.instructions.insert(min, new MethodInsnNode(Opcodes.INVOKESPECIAL, c, min.name, m.desc));
									m.instructions.remove(min);
									ReikaJavaLibrary.pConsole("CHROMATICRAFT: Successfully applied "+this+" ASM handler!");
								}
							}
						}
					}
				}
				break;
				/*
				case ENDPROVIDER2: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "", "createChunkGenerator", "()Lnet/minecraft/world/chunk/IChunkProvider;");
					if (m == null) {
						ReikaJavaLibrary.pConsole("CHROMATICRAFT: Could not find method for "+this+" ASM handler!");
					}
					else {
						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode ain = m.instructions.get(i);
							if (ain.getOpcode() == Opcodes.NEW) {
								TypeInsnNode min = (TypeInsnNode)ain;
								String c = "Reika/ChromatiCraft/World/CustomEndProvider";
								m.instructions.insert(min, new TypeInsnNode(Opcodes.NEW, c));
								m.instructions.remove(min);
								ReikaJavaLibrary.pConsole("CHROMATICRAFT: Successfully applied "+this+" ASM handler part 1!");
							}
							if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
								MethodInsnNode min = (MethodInsnNode)ain;
								if (min.name.equals("<init>")) {
									String c = "Reika/ChromatiCraft/World/CustomEndProvider";
									m.instructions.insert(min, new MethodInsnNode(Opcodes.INVOKESPECIAL, c, min.name, m.desc));
									m.instructions.remove(min);
									ReikaJavaLibrary.pConsole("CHROMATICRAFT: Successfully applied "+this+" ASM handler part 2!");
								}
							}
						}
					}
				}
				break;*/

				case ENDPROVIDER: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "", "initializeNoiseField", "([DIIIIII)[D");
					if (m == null) {
						ReikaJavaLibrary.pConsole("CHROMATICRAFT: Could not find method for "+this+" ASM handler!");
					}
					else {
						ReikaASMHelper.removeCodeLine(m, 330);
						ReikaJavaLibrary.pConsole("CHROMATICRAFT: Successfully applied "+this+" ASM handler!");
					}
				}
				break;
				case REACHDIST:
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "", "getBlockReachDistance", "()F");
					if (m == null) {
						ReikaJavaLibrary.pConsole("CHROMATICRAFT: Could not find method for "+this+" ASM handler!");
					}
					else {
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
						m.instructions.insertBefore(index, new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Math", "max", "(FF)F"));
						ReikaJavaLibrary.pConsole("CHROMATICRAFT: Successfully applied "+this+" ASM handler!");
					}
					break;
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
					ReikaJavaLibrary.pConsole("CHROMATICRAFT: Patching class "+className);
					opcodes = p.apply(opcodes);
					classes.remove(className); //for maximizing performance
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
