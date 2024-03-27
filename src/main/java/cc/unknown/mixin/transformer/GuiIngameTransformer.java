package cc.unknown.mixin.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class GuiIngameTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraft.client.gui.GuiIngame")) {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, 0);

			transformRenderDemoMethod(classNode);
			transformRenderBossHealthMethod(classNode);
			transformRenderPumpkinOverlayMethod(classNode);
			transformRenderPortalMethod(classNode);

			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(classWriter);

			return classWriter.toByteArray();
		}
		return basicClass;
	}

	private void transformRenderDemoMethod(ClassNode classNode) {
		for (MethodNode method : classNode.methods) {
			if (method.name.equals("renderDemo")) {
				InsnList instructions = method.instructions;
				instructions.insertBefore(instructions.getFirst(), new InsnNode(Opcodes.RETURN));
			}
		}
	}

	private void transformRenderBossHealthMethod(ClassNode classNode) {
		for (MethodNode method : classNode.methods) {
			if (method.name.equals("renderBossHealth")) {
				InsnList instructions = method.instructions;
				instructions.insertBefore(instructions.getFirst(), new InsnNode(Opcodes.RETURN));
			}
		}
	}

	private void transformRenderPumpkinOverlayMethod(ClassNode classNode) {
		for (MethodNode method : classNode.methods) {
			if (method.name.equals("renderPumpkinOverlay")) {
				InsnList instructions = method.instructions;
				instructions.insertBefore(instructions.getFirst(), new InsnNode(Opcodes.RETURN));
			}
		}
	}

	private void transformRenderPortalMethod(ClassNode classNode) {
		for (MethodNode method : classNode.methods) {
			if (method.name.equals("renderPortal")) {
				InsnList instructions = method.instructions;
				instructions.insertBefore(instructions.getFirst(), new InsnNode(Opcodes.RETURN));
			}
		}
	}

}
