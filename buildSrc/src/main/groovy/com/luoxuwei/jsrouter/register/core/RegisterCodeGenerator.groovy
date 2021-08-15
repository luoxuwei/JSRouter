package com.luoxuwei.jsrouter.register.core

import com.luoxuwei.jsrouter.register.utils.Consts
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import com.luoxuwei.jsrouter.register.utils.Logger

/**
 * Created by 罗旭维 on 2021/8/15.
 */
class RegisterCodeGenerator {
    File jarFile
    List<String> classList;

    private RegisterCodeGenerator(File file, List<String> list) {
        jarFile = file
        classList = list
    }

    static void insertInitCodeTo(File file, List<String> list) {
        if (file && file.getName().endsWith('.jar') && !list.isEmpty()) {
            new RegisterCodeGenerator(file, list).insertCode()
        }
    }

    void insertCode() {
        def optJar = new File(jarFile.getParent(), jarFile.name + ".opt")
        if (optJar.exists())
            optJar.delete()
        def file = new JarFile(jarFile)
        Enumeration entries = file.entries()
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement()
            String entryName = jarEntry.getName()
            ZipEntry zipEntry = new ZipEntry(entryName)
            jarOutputStream.putNextEntry(zipEntry)

            InputStream inputStream = file.getInputStream(jarEntry)
            if (Consts.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                Logger.i('Insert init code to class >> ' + entryName)
                jarOutputStream.write(referHackWhenInit(inputStream))
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            inputStream.close()
            jarOutputStream.closeEntry()
        }

        jarOutputStream.close()
        file.close()

        if (jarFile.exists())
            jarFile.delete()
        optJar.renameTo(jarFile)
    }

    private byte[] referHackWhenInit(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        MyClassVisitor cv = new MyClassVisitor(cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    class MyClassVisitor extends ClassVisitor {

        MyClassVisitor(ClassVisitor cv) {
            super(Opcodes.ASM7, cv)
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions)
            //在com.luoxuwei.jsrouter.JSRouter.loadRouterByPlugin方法中注入加载路由组root类的代码
            if (name == Consts.GENERATE_TO_METHOD_NAME) {
                mv = new RouteMethodVisitor(mv)
            }
            return mv
        }
    }

    class RouteMethodVisitor extends MethodVisitor {

        RouteMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM7, mv)
        }

        @Override
        void visitInsn(int opcode) {
            //在return之前插入加载代码
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                classList.each {
                    it = it.replaceAll("/", ".")
                    //调用loadRouter加载root类
                    mv.visitLdcInsn(it)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                            Consts.GENERATE_TO_CLASS_NAME,
                            Consts.REGISTER_METHOD_NAME,
                            "(Ljava/lang/String;)V",
                            false)
                }
            }
            super.visitInsn(opcode)
        }
    }
}