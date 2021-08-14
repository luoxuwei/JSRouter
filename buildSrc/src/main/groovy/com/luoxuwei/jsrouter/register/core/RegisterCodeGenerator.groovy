package com.luoxuwei.jsrouter.register.core

import com.luoxuwei.jsrouter.register.utils.Consts
import org.apache.commons.io.IOUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

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

    }
}