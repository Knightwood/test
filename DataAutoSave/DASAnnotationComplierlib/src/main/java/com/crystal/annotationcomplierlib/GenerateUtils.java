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
    private ClassName dataAutoSaveFieldInfo = ClassName.get("com.crystal.annotationcomplierlib", "DataAutoSaveFieldInfo");

    /**
     * @param value
     * @param filer
     * @throws IOException
     */
    public void generate(BeCreateClassInfo value, Filer filer) throws IOException {
        TtypeVariableName = TypeVariableName.get("T", ClassName.get(value.getTargetClassPackageName(),value.getTargetClassSimpleClassName()));//泛型T，在这生成不同的类时，泛型参数继承自不同的类

        TypeSpec spec = generateClass(value);//生成类
        //生成文件
        JavaFile javaFile = JavaFile.builder("com.crystal.dataautosave", spec).build();
        javaFile.writeTo(filer);

    }

    private MethodSpec generateGetDataFun(BeCreateClassInfo info) {

        StringBuilder stringBuilder = new StringBuilder();
        String VarName;//就是从这个名称的变量中获取的数据

        for (DataAutoSaveFieldInfo info1 : info.getFields()) {
            VarName = info1.getFiledName().toString();

            stringBuilder.append("if (bundle.containsKey(\"")
                    .append(VarName)
                    .append("\"))\n")
                    .append("context." + VarName)
                    .append("=(" + info1.getFieldType() + ") bundle.get(\"" + VarName + "\");\n");
        }

        MethodSpec getData = MethodSpec.methodBuilder("getData")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(TtypeVariableName, "context")
                .addParameter(bundle, "bundle")
                .addCode(stringBuilder.toString())
                .build();
        return getData;

    }

    private MethodSpec generateSaveDataFun(BeCreateClassInfo info) {
        StringBuilder builder=new StringBuilder();
        String VarName;//就是从这个名称的变量中获取的数据

        for (DataAutoSaveFieldInfo info1 : info.getFields()) {
            VarName = info1.getFiledName().toString();

            builder.append("bundle."+SomeUtils.PUT_DATA_PRE_CODE_MAP.get(info1.getFieldType()))
                    .append("(\""+VarName+"\",")
                    .append("context."+VarName+");");
        }


        MethodSpec saveData = MethodSpec.methodBuilder("saveData")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addCode(builder.toString())
                .addParameter(TtypeVariableName, "context")
                .addParameter(bundle, "bundle")
                .build();
        return saveData;
    }

    private TypeSpec generateClass(BeCreateClassInfo value) {
        String theClassName = value.getWillGenerateClassName();//被构建的类的名称
        //构建带有泛型的 ：  DASinterface<T>
        TypeName parameterizedTypeName = ParameterizedTypeName.get(mInterface, TtypeVariableName);

        TypeSpec genClass = TypeSpec.classBuilder(theClassName)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TtypeVariableName)
                .addSuperinterface(parameterizedTypeName)
                .addMethod(generateGetDataFun(value))
                .addMethod(generateSaveDataFun(value))
                .build();

        return genClass;
    }
}
