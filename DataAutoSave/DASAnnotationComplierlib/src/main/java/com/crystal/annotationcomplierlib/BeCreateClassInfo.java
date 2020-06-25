package com.crystal.annotationcomplierlib;

import java.util.ArrayList;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/21 18:17
 * 要生成的类的描述信息
 */

/*
 package com.crystal.dataautosave;//包名

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.crystal.annotationlib.AutoSave;

public class MainActivity extends AppCompatActivity {//被注解的元素所属的类

    @AutoSave
    public String testField;//被注解的字段

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testField = "123";
    }
}
//使用typeElement生成BeCreateClassInfo，
 String classPackageName = getPackageName(typeElement);//字段所属的类的包名
 tring genclassName=getClassName(typeElement, classPackageName);//要生成的类的名称
String qualifiedName=typeElement.getQualifiedName().toString();//字段所属的类的全名
    info = new BeCreateClassInfo(
           classPackageName,
           genclassName,
           qualifiedName

       );

  字段所属的类的包名： com.crystal.dataautosave
  要生成的类的名称类名： MainActivity$$DataAutoSave
  字段所属的类的全名： com.crystal.dataautosave.MainActivity

  字段名称： testField
  字段类型： java.lang.String
*
* */


public class BeCreateClassInfo {
    private String targetClassPackageName;//字段所属的类的包名
    private String willGenerateClassName;//要生成的类的名称（类名+前缀的格式），不包括包名
    private String targetClassQualifiedName;//注解字段所属的类名，（包名+类名）
    private String targetClassSimpleClassName;//被注解字段所属类的名称只有类名，不包括包名

    //这个被生成的类的信息包含有不止一个“在类中被注解的变量”的元素
    private ArrayList<DataAutoSaveFieldInfo> fieldInfos = new ArrayList<>();

    public BeCreateClassInfo(String targetClassPackageName, String targetClassQualifiedName, String willGenerateClassName) {
        this.targetClassPackageName = targetClassPackageName;
        this.targetClassQualifiedName = targetClassQualifiedName;
        this.willGenerateClassName = willGenerateClassName;
        //获取类名，不包括包名  例如 com.crystal.dataautosave.MainActivity ，只保留MainActivity
        this.targetClassSimpleClassName = targetClassQualifiedName.trim().replace(targetClassPackageName + ".", "");
    }

    /**
     * @param field 被注解字段的信息
     *              把一个类中被注解的字段的信息放入list
     */
    public void addField(DataAutoSaveFieldInfo field) {
        fieldInfos.add(field);
    }

    public ArrayList<DataAutoSaveFieldInfo> getFields() {
        return fieldInfos;
    }

    public String getTargetClassPackageName() {
        return targetClassPackageName;
    }

    public void setTargetClassPackageName(String targetClassPackageName) {
        this.targetClassPackageName = targetClassPackageName;
    }

    public String getWillGenerateClassName() {
        return willGenerateClassName;
    }

    public void setWillGenerateClassName(String willGenerateClassName) {
        this.willGenerateClassName = willGenerateClassName;
    }

    public String getTargetClassQualifiedName() {
        return targetClassQualifiedName;
    }

    public void setTargetClassQualifiedName(String targetClassQualifiedName) {
        this.targetClassQualifiedName = targetClassQualifiedName;
    }

    public String getTargetClassSimpleClassName() {
        return targetClassSimpleClassName;
    }
}
