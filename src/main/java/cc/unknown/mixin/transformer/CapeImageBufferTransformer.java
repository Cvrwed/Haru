package cc.unknown.mixin.transformer;

import java.util.Iterator;
import java.util.function.BiConsumer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class CapeImageBufferTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (name.equals("CapeUtils"))
			return transformCapeUtils(bytes);
		if (name.equals("cc.unknown.utils.memory.CapeImageBuffer"))
			return transformMethods(bytes, this::transformCapeImageBuffer);
		if (transformedName.equals("net.minecraft.client.resources.AbstractResourcePack"))
			return transformMethods(bytes, this::transformAbstractResourcePack);
		return bytes;
	}

	private byte[] transformMethods(byte[] bytes, BiConsumer<ClassNode, MethodNode> transformer) {
		ClassReader classReader = new ClassReader(bytes);
		ClassNode classNode = new ClassNode();
		classReader.accept((ClassVisitor) classNode, 0);
		classNode.methods.forEach(m -> transformer.accept(classNode, m));
		ClassWriter classWriter = new ClassWriter(0);
		classNode.accept(classWriter);
		return classWriter.toByteArray();
	}

	private byte[] transformCapeUtils(byte[] bytes) {
		ClassWriter classWriter = new ClassWriter(2);
		RemappingClassAdapter adapter = new RemappingClassAdapter(classWriter, new Remapper() {
			public String map(String typeName) {
				if (typeName.equals("CapeUtils$1"))
					return "cc.unknown.utils.hook.CapeImageBuffer".replace('.', '/');
				return typeName;
			}
		});
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept((ClassVisitor) adapter, 8);
		return classWriter.toByteArray();
	}

	private void transformCapeImageBuffer(ClassNode clazz, MethodNode method) {
		Iterator<AbstractInsnNode> iter = method.instructions.iterator();
		while (iter.hasNext()) {
			AbstractInsnNode insn = iter.next();
			if (insn instanceof MethodInsnNode) {
				MethodInsnNode methodInsn = (MethodInsnNode) insn;
				if (methodInsn.name.equals("parseCape")) {
					methodInsn.owner = "CapeUtils";
					continue;
				}
				if (methodInsn.name.equals("setLocationOfCape")) {
					methodInsn.setOpcode(182);
					methodInsn.owner = "net/minecraft/client/entity/AbstractClientPlayer";
					methodInsn.desc = "(Lnet/minecraft/util/ResourceLocation;)V";
				}
			}
		}
	}

	private void transformAbstractResourcePack(ClassNode clazz, MethodNode method) {
		if ((method.name.equals("getPackImage") || method.name.equals("func_110586_a")) && method.desc.equals("()Ljava/awt/image/BufferedImage;")) {
			Iterator<AbstractInsnNode> iter = method.instructions.iterator();
			while (iter.hasNext()) {
				AbstractInsnNode insn = iter.next();
				if (insn.getOpcode() == 176)
					method.instructions.insertBefore(insn, (AbstractInsnNode) new MethodInsnNode(184, "cc.unknown.utils.hook.ResourcePackImageScaler".replace('.', '/'), "scalePackImage", "(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false));
			}
		}
	}
}
