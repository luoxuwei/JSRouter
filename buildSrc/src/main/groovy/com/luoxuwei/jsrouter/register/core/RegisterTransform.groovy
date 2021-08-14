package com.luoxuwei.jsrouter.register.core

import com.android.build.api.transform.Context
import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.luoxuwei.jsrouter.register.utils.Consts
import com.luoxuwei.jsrouter.register.utils.Logger
import com.luoxuwei.jsrouter.register.utils.ScanUtils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * Created by 罗旭维 on 2021/8/14.
 */
class RegisterTransform extends Transform {
    static ArrayList<String> classList = new ArrayList<>()
    static File fileContainsInitClass;
    Project project
    RegisterTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return Consts.PLUGIN_NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs
                   , Collection<TransformInput> referencedInputs
                   , TransformOutputProvider outputProvider
                   , boolean isIncremental) throws IOException, TransformException, InterruptedException {

        Logger.i('Start scan')

        boolean leftSlash = File.separator == '/'

        //先扫描所有class，找出生成的IRouteRoot类，这里不做任何修改，直接copy到输出
        inputs.forEach { input ->
            input.directoryInputs.forEach { directoryInput ->
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                String root = directoryInput.file.absolutePath
                if (!root.endsWith(File.separator)) {
                    root += File.separator
                }

                directoryInput.file.eachFileRecurse {
                    def path = it.absolutePath.replace(root, '')
                    if (!leftSlash) {
                        path.replaceAll("\\\\", '/')
                    }

                    if (it.isFile() && ScanUtils.shouldProcessClass(path)) {
                        ScanUtils.scanClass(it)
                    }
                }

                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.forEach { jar ->
                String destName = jar.name

                //rename jar file name
                def hexName = DigestUtils.md2Hex(jar.file.absolutePath)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }
                destName = destName + '_' + hexName

                File src = jar.file
                File dest = outputProvider.getContentLocation(destName, jar.contentTypes, jar.scopes, Format.JAR)
                if (ScanUtils.shouldProcessPreDexJar(src.absolutePath)) {
                    ScanUtils.scanJar(src, dest)
                }

                FileUtils.copyFile(src, dest)

            }
        }

        Logger.i("scan class result")
        Logger.i(classList.toString())
        Logger.i("scan JSRouter class result")
        Logger.i(fileContainsInitClass.toString())
    }
}