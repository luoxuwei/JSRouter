package com.luoxuwei.jsrouter.register

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.luoxuwei.jsrouter.register.core.RegisterTransform
import com.luoxuwei.jsrouter.register.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by 罗旭维 on 2021/8/14.
 */
public class PluginLaunch implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def isApp = project.plugins.hasPlugin(AppPlugin);
        if (isApp) {
            Logger.init(project);
            Logger.i('Project enable jsrouter-register plugin')
            def android = project.extensions.getByType(AppExtension)
            def transform = new RegisterTransform(project)
//            android.registerTransform(transform)
            project.android.registerTransform(transform)
        }
    }
}