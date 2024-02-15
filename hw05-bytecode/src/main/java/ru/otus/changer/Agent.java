package ru.otus.changer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Agent {
    public static final Logger logger = LoggerFactory.getLogger(Agent.class);

    private Agent() {
        throw new UnsupportedOperationException("Do not instantiate this class, use statically.");
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("premain");
        logger.info("{}", agentArgs);
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(
                    ClassLoader loader,
                    String className,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer)
                    throws IllegalClassFormatException {
                if (className.equals("ru/otus/testlogging/TestLogging")) {
                    return changeMethod(classfileBuffer);
                }
                return classfileBuffer;
            }
        });
    }

    private static byte[] changeMethod(byte[] originalClass) {
        var cr = new ClassReader(originalClass);
        var cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM7, cw) {
            @Override
            public MethodVisitor visitMethod(
                    int access, String name, String descriptor, String signature, String[] exceptions) {
                var methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (name.equals("calculation")) {
                    return new ChangeMethodVisitor(methodVisitor, access, name, descriptor);
                } else {
                    return methodVisitor;
                }
            }
        };
        cr.accept(cv, Opcodes.ASM7);

        byte[] finalClass = cw.toByteArray();

        try (OutputStream fos = new FileOutputStream("changer.class")) {
            fos.write(finalClass);
        } catch (Exception e) {
            logger.error("error", e);
        }
        return finalClass;
    }

    private static class ChangeMethodVisitor extends AdviceAdapter {
        ChangeMethodVisitor(MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(Opcodes.ASM7, methodVisitor, access, name, descriptor);
        }

        @Override
        public void visitInsn(int opcode) {
            super.visitInsn(opcode);
        }
    }
}
