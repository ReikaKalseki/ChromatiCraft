package Reika.ChromatiCraft.Auxiliary;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

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
			ENDERCRYSTALGEN("net.minecraft.world.gen.feature.WorldGenSpikes", ""),
			ENDPROVIDER("net.minecraft.world.gen.ChunkProviderEnd", ""),
			REACHDIST("net.minecraft.client.multiplayer.PlayerControllerMP", "");

			private final String obfName;
			private final String deobfName;

			private static final ClassPatch[] list = values();

			private ClassPatch(String deobf, String obf) {
				obfName = obf;
				deobfName = deobf;
			}

			private byte[] apply(byte[] data) {
				ClassNode classNode = new ClassNode();
				ClassReader classReader = new ClassReader(data);
				classReader.accept(classNode, 0);
				switch(this) {
				case ENDERCRYSTALGEN:
					String method = ReikaObfuscationHelper.isDeObfEnvironment() ? "generate" : "";
					Iterator<MethodNode> methods = classNode.methods.iterator();
					while(methods.hasNext()) {
						MethodNode m = methods.next();
						int instantiate_index = -1;
						if ((m.name.equals(method) && m.desc.equals("(Lnet.minecraft.world.World;Ljava.util.Random;III)Z"))) {
							AbstractInsnNode currentNode = null;
							AbstractInsnNode targetNode = null;
							Iterator<AbstractInsnNode> iter = m.instructions.iterator();
							int index = -1;
							while (iter.hasNext()) {
								index++;
								currentNode = iter.next();
								if (currentNode.getOpcode() == -34/*<< placeholder for NEW*/) {
									targetNode = currentNode;
									instantiate_index = index;
								}
							}
						}
					}
					break;
				case ENDPROVIDER:
					break;
				case REACHDIST:
					break;
				}
				return data;
			}
		}

		@Override
		public byte[] transform(String className, String className2, byte[] opcodes) {
			if (!classes.isEmpty()) {
				ClassPatch p = classes.get(className);
				if (p != null) {
					ChromatiCraft.logger.log("Patching class "+className);
					opcodes = p.apply(opcodes);
					classes.remove(className); //for maximizing performance
				}
			}
			return opcodes;
		}

		static {
			for (int i = 0; i < ClassPatch.list.length; i++) {
				ClassPatch p = ClassPatch.list[i];
				String s = ReikaObfuscationHelper.isDeObfEnvironment() ? p.deobfName : p.obfName;
				classes.put(s, p);
			}
		}
	}

}
