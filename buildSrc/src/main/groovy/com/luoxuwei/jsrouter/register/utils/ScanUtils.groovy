package com.luoxuwei.jsrouter.register.utils

import com.luoxuwei.jsrouter.register.core.RegisterTransform
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Created by 罗旭维 on 2021/8/14.
 */
class ScanUtils {

    public static boolean shouldProcessPreDexJar(String path) {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    public static void scanJar(File src, File dest) {
        if (src) {
            JarFile jarFile = new JarFile(src)
            def entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement()
                String entryName = jarEntry.name
                if (entryName.startsWith(Consts.ROUTER_CLASS_PACKAGE_NAME)) {
                    InputStream inputStream = jarFile.getInputStream(jarEntry)
                    scanClass(inputStream)
                } else if (entryName == Consts.GENERATE_TO_CLASS_FILE_NAME) {
                    RegisterTransform.fileContainsInitClass = dest
                }
            }
        }
    }

    public static void scanClass(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        ScanClassVisitor cv = new ScanClassVisitor(cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    static class ScanClassVisitor extends ClassVisitor {

        ScanClassVisitor(ClassVisitor cv) {
            super(Opcodes.ASM7, cv)
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            interfaces.each {
                if (it == Consts.INTERFACEE_NAME) {
                    RegisterTransform.classList.add(name)
                }
            }
        }
    }
}