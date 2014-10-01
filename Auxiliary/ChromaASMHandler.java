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

		private static enum ClassPatch {
			ENDPROVIDER("net.minecraft.world.gen.ChunkProviderEnd", "aqr"),
			REACHDIST("net.minecraft.client.multiplayer.PlayerControllerMP", "bje");

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
				switch(this) {
				case ENDPROVIDER: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73187_a", "initializeNoiseField", "([DIIIIII)[D");
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
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78757_d", "getBlockReachDistance", "()F");
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
