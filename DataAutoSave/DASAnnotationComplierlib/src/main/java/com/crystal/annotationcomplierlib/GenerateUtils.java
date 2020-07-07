package com.crystal.annotationcomplierlib;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/21 18:31
 */
public enum GenerateUtils {
    INSTANCE;

    private ClassName mInterface = ClassName.get("com.crystal.dastools", "DASinterface");//保存数据和恢复数据的接口
    private TypeVariableName TtypeVariableName;//泛型参数
    private ClassName bundle = ClassName.get("android.os", "Bundle");//保存数据的类
    private ClassName preferenceTools = ClassName.get("com.crystal.dastools", "PreferenceTools");

    /**
     * @param createClassInfo
     * @param filer
     * @throws IOException
     */
    public void generate(BeCreateClassInfo createClassInfo, Filer filer) throws IOException {
        TtypeVariableName = TypeVariableName.get("T", ClassName.get(createClassInfo.getTargetClassPackageName(), createClassInfo.getTargetClassSimpleClassName()));//泛型T，在这生成不同的类时，泛型参数继承自不同的类

        TypeSpec spec = generateClass(createClassInfo);//生成所需的类
        //生成java文件写入目录
        JavaFile javaFile = JavaFile.builder("com.crystal.dataautosave", spec).build();
        javaFile.writeTo(filer);

    }

    /**
     * @param createClassInfo 要生成的类的信息
     * @return 返回生成的类的TypeSpec
     * <p>
     * 生成恢复数据方法
     */
    private MethodSpec generateGetDataFun(BeCreateClassInfo createClassInfo) {

        StringBuilder stringBuilder = new StringBuilder();
        String VarName;//就是从这个名称的变量中获取的数据

        stringBuilder.append("//$T \n");//为了import preferencetools类

        for (DataAutoSaveFieldInfo info1 : createClassInfo.getFields()) {
            VarName = info1.getFiledName().toString();

            if (info1.isPersistence()) {//如果需要持久化存储，生成持久化存储代码
                stringBuilder.append( "context."+VarName+"= PreferenceTools.get" + SomeUtils.Persistence_Method_SUFFIX.get(info1.getUnErasureTypeClassName()) + "(context,\"" + info1.getKey() + "\");\n");
            }
            if (info1.isUseBundle()) {
                stringBuilder.append("if (bundle.containsKey(\"")
                        .append(VarName)
                        .append("\"))\n")
                        .append("\t context." + VarName)
                        .append("=(" + info1.getFieldType() + ") bundle.get(\"" + VarName + "\");\n");
            }
        }

        MethodSpec RestoreData = MethodSpec.methodBuilder("RestoreData")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(TtypeVariableName, "context")
                .addParameter(bundle, "bundle")
                .addCode(stringBuilder.toString(),preferenceTools)
                .build();
        return RestoreData;

    }

    /**
     * @param createClassInfo 要生成的类的信息
     * @return 返回生成的类的TypeSpec
     * <p>
     * 生成保存数据方法
     */
    private MethodSpec generateSaveDataFun(BeCreateClassInfo createClassInfo) {
        StringBuilder builder = new StringBuilder();
        String VarName;//就是从这个名称的变量中获取的数据

        for (DataAutoSaveFieldInfo info1 : createClassInfo.getFields()) {
            VarName = info1.getFiledName().toString();

            if (info1.isPersistence()) {//如果需要持久化存储，生成持久化存储代码
                builder.append( "PreferenceTools.put" + SomeUtils.Persistence_Method_SUFFIX.get(info1.getUnErasureTypeClassName()) + "(context,\"" + info1.getKey() + "\",context." + VarName + ");\n");
            }
            if (info1.isUseBundle()) {
                builder.append("bundle." + SomeUtils.Bundle_PUT_DATA_PRE_CODE_MAP.get(info1.getTopLevelClassName()))
                        .append("(\"" + VarName + "\",")
                        .append("context." + VarName + ");\n");
            }

        }


        MethodSpec SaveData = MethodSpec.methodBuilder("SaveData")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addCode(builder.toString())
                .addParameter(TtypeVariableName, "context")
                .addParameter(bundle, "bundle")
                .build();
        return SaveData;
    }

    /**
     * @param createClassInfo 要生成的类的信息
     * @return 返回生成的类的TypeSpec
     *
     * <p>
     * 根据beCreateClassInfo生成类
     */
    private TypeSpec generateClass(BeCreateClassInfo createClassInfo) {
        String theClassName = createClassInfo.getWillGenerateClassName();//被构建的类的名称
        //构建带有泛型的 ：  DASinterface<T>
        TypeName parameterizedTypeName = ParameterizedTypeName.get(mInterface, TtypeVariableName);

        TypeSpec genClass = TypeSpec.classBuilder(theClassName)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TtypeVariableName)
                .addSuperinterface(parameterizedTypeName)
                .addMethod(generateGetDataFun(createClassInfo))
                .addMethod(generateSaveDataFun(createClassInfo))
                .build();

        return genClass;
    }
}
