package com.luoxuwei.jsrouter.compiler.processor;

import com.google.auto.service.AutoService;
import com.luoxuwei.jsrouter.annotation.JSRoute;
import com.luoxuwei.jsrouter.compiler.model.RouteMeta;
import com.luoxuwei.jsrouter.compiler.utils.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.luoxuwei.jsrouter.compiler.utils.Consts.*;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by 罗旭维 on 2021/8/13.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({ANNOTATION_TYPE_ROUTE})
public class RouteProcessor extends AbstractProcessor {
    Filer mFiler;
    Logger mLogger;
    Types mTypes;
    Elements mElementUtils;

    // Module name, maybe its 'app' or others
    String moduleName = null;
    private Map<String, Set<RouteMeta>> groupMap = new HashMap<>(); // ModuleName and routeMeta.
    private Map<String, String> rootMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mTypes = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mLogger = new Logger(processingEnv.getMessager());
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
        }
        if (StringUtils.isNotEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");

            mLogger.info("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            mLogger.error(NO_MODULE_NAME_TIPS);
            throw new RuntimeException("JSRouter::Compiler >>> No module name, for more information, look at gradle log.");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {
            mLogger.info(">>> Found routes, start... <<<");
            try {
                parseRouteElements(roundEnvironment.getElementsAnnotatedWith(JSRoute.class));
            } catch (IOException e) {
                mLogger.error(e);
            }
            return true;
        }
        return false;
    }

    private void parseRouteElements(Set<? extends Element> routeElements) throws IOException {
        if (CollectionUtils.isEmpty(routeElements)) return;
        TypeElement type_IRouteGroup = mElementUtils.getTypeElement(IROUTE_GROUP);
        TypeMirror baseInterface = mElementUtils.getTypeElement(BASE_JAVASCRIPTE_INTERFACE).asType();

        //begin of JSRouter$$Root$$xxx--loadInto(Map<String, Class<? extends IRouteGroup>> atlas, Map<String, String> index)
        //Map<String, Class<? extends IRouteGroup>
        ParameterizedTypeName inputTypeOfRoot1 = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(type_IRouteGroup))
                )
        );

        //Map<String, String>
        ParameterizedTypeName inputTypeOfRoot2 = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(String.class)
        );

        ParameterSpec rootParamSpec1 = ParameterSpec.builder(inputTypeOfRoot1, "atlas").build();
        ParameterSpec rootParamSpec2 = ParameterSpec.builder(inputTypeOfRoot2, "index").build();
        MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(rootParamSpec1)
                .addParameter(rootParamSpec2);
        //end of JSRouter$$Root$$xxx--loadInto(Map<String, Class<? extends IRouteGroup>> atlas, Map<String, String> index)

        //JSRouter$$Group$$--loadInto(Map<String, Class<?>> atlas)
        //Map<String, Class<?>>
        ParameterizedTypeName inputTypeOfGroup = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(Object.class)
                )
        );


        ParameterSpec groupParamSpec = ParameterSpec.builder(inputTypeOfGroup, "atlas").build();

        for (Element element : routeElements) {
            if (mTypes.isSubtype(element.asType(), baseInterface)) {
                JSRoute route = element.getAnnotation(JSRoute.class);
                RouteMeta routeMeta = new RouteMeta(route, element);
                categories(routeMeta);
            }
        }

        for (Map.Entry<String, Set<RouteMeta>> entry : groupMap.entrySet()) {
            String groupName = entry.getKey();
            MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(groupParamSpec);
            Set<RouteMeta> routeMetas = entry.getValue();
            for (RouteMeta routeMeta : routeMetas) {
                loadIntoMethodOfRootBuilder.addStatement("index.put($S, $S)", routeMeta.getPath(), routeMeta.getGroup());
                loadIntoMethodOfGroupBuilder.addStatement("atlas.put($S, $T.class)", routeMeta.getPath(), ClassName.get((TypeElement) routeMeta.getRawType()));
            }
            // Generate groups
            String groupFileName = NAME_OF_GROUP + groupName;
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(groupFileName)
                            .addJavadoc(WARNING_TIPS)
                            .addSuperinterface(ClassName.get(type_IRouteGroup))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfGroupBuilder.build())
                            .build()
            ).build().writeTo(mFiler);

            mLogger.info(">>> Generated group: " + groupName + "<<<");
            rootMap.put(groupName, groupFileName);
        }

        if (MapUtils.isNotEmpty(rootMap)) {
            // Generate root meta by group name, it must be generated before root, then I can find out the class of group.
            for (Map.Entry<String, String> entry : rootMap.entrySet()) {
                loadIntoMethodOfRootBuilder.addStatement("atlas.put($S, $T.class)", entry.getKey(), ClassName.get(PACKAGE_OF_GENERATE_FILE, entry.getValue()));
            }
        }
        String rootFileName = NAME_OF_ROOT +  moduleName;
        JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(rootFileName)
                        .addModifiers(PUBLIC)
                        .addSuperinterface(ClassName.get(mElementUtils.getTypeElement(ITROUTE_ROOT)))
                        .addMethod(loadIntoMethodOfRootBuilder.build())
                        .addJavadoc(WARNING_TIPS)
                        .build()
        ).build().writeTo(mFiler);
        mLogger.info(">>> Generated root, name is " + rootFileName + " <<<");
    }

    private void categories(RouteMeta routeMete) {
        if (routeVerify(routeMete)) {
            mLogger.info(">>> Start categories, group = " + routeMete.getGroup() + ", path = " + routeMete.getPath() + " <<<");
            Set<RouteMeta> routeMetas = groupMap.get(routeMete.getGroup());
            if (CollectionUtils.isEmpty(routeMetas)) {
                Set<RouteMeta> routeMetaSet = new TreeSet<>(new Comparator<RouteMeta>() {
                    @Override
                    public int compare(RouteMeta r1, RouteMeta r2) {
                        try {
                            return r1.getPath().compareTo(r2.getPath());
                        } catch (NullPointerException npe) {
                            mLogger.error(npe.getMessage());
                            return 0;
                        }
                    }
                });
                routeMetaSet.add(routeMete);
                groupMap.put(routeMete.getGroup(), routeMetaSet);
            } else {
                routeMetas.add(routeMete);
            }
        } else {
            mLogger.warning(">>> Route meta verify error, group is " + routeMete.getGroup() + " <<<");
        }
    }

    //组名可以设置group字段，或在path里带上group，如:/group/path
    //都没有默认用moduleName作为组名
    private boolean routeVerify(RouteMeta meta) {
        String path = meta.getPath();

        if (StringUtils.isEmpty(path)) {
            return false;
        }

        if (StringUtils.isEmpty(meta.getGroup()) && path.startsWith("/")) { // Use default group(the first word in path)
            try {
                String defaultGroup = path.substring(1, path.indexOf("/", 1));
                String name = path.substring(path.indexOf("/", 1) + 1);
                if (StringUtils.isEmpty(defaultGroup)
                        || StringUtils.isEmpty(name)) {
                    return false;
                }

                meta.setGroup(defaultGroup);
                meta.setPath(name);
                return true;
            } catch (Exception e) {
                mLogger.error("Failed to extract default group! " + e.getMessage());
                return false;
            }
        }

        meta.setGroup(moduleName);

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<String>() {{
            this.add(KEY_MODULE_NAME);
        }};
    }
}
