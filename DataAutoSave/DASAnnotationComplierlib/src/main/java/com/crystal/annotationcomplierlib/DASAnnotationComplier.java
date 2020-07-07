package com.crystal.annotationcomplierlib;

import com.crystal.annotationlib.AutoSave;
import com.crystal.annotationlib.DataSave;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.crystal.annotationcomplierlib.SomeUtils.SUPPORTED_FIELD_TYPE;
import static com.crystal.annotationcomplierlib.SomeUtils.SUPPORTED_INTERFACE_TYPE;

/*
 * <p>
 * Element:
 * 表示程序元素，例如包，类或方法。 每个元素代表一个静态的语言级别的结构（例如，不代表虚拟机的运行时结构）。
 * 应该使用equals（Object）方法比较元素。 不能保证任何特定元素总是由同一对象表示。
 * 要基于Element对象的类实现操作，请使用访问者或使用getKind方法的结果。 在这种建模层次结构中，使用instanceof不一定是确定对象有效类的可靠习惯，因为实现可能选择让单个对象实现多个Element子接口
 * </p>
 *
 * <p>
 * TypeElement:  表示一个类或接口程序元素。 提供对有关类型及其成员的信息的访问。 请注意，枚举类型是一种类，注释类型是一种接口。
 * 虽然TypeElement表示类或接口元素，但DeclaredType表示类或接口类型，后者是前者的使用（或调用）。 这种区别在泛型类型中最明显，单个元素可以定义整个类型族。
 * 例如，元素java.util.Set对应于参数化类型java.util.Set <String>和java.util.Set <Number>（以及许多其他类型），还对应于原始类型java.util.Set。
 * 此接口的返回元素列表的每个方法都将按照程序信息的基础源的自然顺序返回它们。 例如，如果基础信息源是Java源代码，则将按源代码顺序返回元素。
 * </p>
 *
 * <p>
 *	1. TypeElement fooClass = ... ;
	2. for (Element e : fooClass.getEnclosedElements()){ // iterate over children  遍历他的孩子
	3.    Element parent = e.getEnclosingElement();  // parent == fooClass 父元素
    }
 * Element代表程序的元素，在注解处理过程中，编译器会扫描所有的Java源文件，并将源码中的每一个部分都看作特定类型的 Element。
 * Element可以代表包、类、接口、方法、字段等多种元素种类，具体看getKind()方法中所指代的种类，每个Element 代表一个静态的、语言级别的构件。。
 *
 * 源码中的每个部分都作为一个Element，而TypeElement对应着一种更具体的类型元素.例如类。然而，TypeElement并不包含类本身的信息。
 * 你可以从TypeElement中获取类的名字，但是你获取不到类的信息，例如它的父类。这种信息需要通过TypeMirror获取。你可以通过调用elements.asType()获取元素的TypeMirror。
 * </p>
 *
 * <p>
 * TypeElement.getQualifiedName():
 * 返回此类型元素的全限定名称。 更准确地说，它返回规范名称。 对于没有规范名称的本地和匿名类，将返回一个空名称。
 * 泛型类型的名称不包含对其形式类型参数的任何引用。 例如，接口java.util.Set <E>的标准名称是“ java.util.Set”。 嵌套类型使用“。” 作为分隔符，例如“ java.util.Map.Entry”。
 * </p>
 *
 * <p>
 * element.getEnclosingElement()：也就是获取父元素
 * 如果此元素的声明包含在另一个元素的声明内，则返回该另一个元素。
 * 如果这是top-level类型，则返回其 package。
 * 如果这是一个package，则返回null。
 * 如果这是类型参数，则返回该类型参数的通用元素。
 * 如果这是方法或构造函数参数，则返回声明该参数的可执行元素。
 * </p>
 *
 * <p>
 * TypeMirror：
 * 表示Java编程语言中的类型。 类型包括基本类型，声明的类型（类和接口类型），数组类型，类型变量和null类型。
 * 还表示了通配符类型参数，可执行文件的签名和返回类型，以及与程序包和关键字void对应的伪类型。
 * 应该使用“类型”中的实用程序方法来比较类型。 不能保证任何特定类型始终由同一对象表示。
 * 要基于TypeMirror对象的类实现操作，请使用访问者或使用getKind方法的结果。
 * 在这种建模层次结构中，使用instanceof不一定是确定对象有效类的可靠习惯，因为实现可以选择让单个对象实现多个TypeMirror子接口。
 * </p>
 * */

/**
 * 处理autosave注解
 */
@AutoService(AutoSave.class)
public class DASAnnotationComplier extends AbstractProcessor {
    /**
     * 前缀
     */
    public static final String SUFFIX = "$$DataAutoSave";

    private Elements elements;
    private Types types;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        SomeUtils.INSTANCE.init();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    /**
     * @return 返回支持的注解的Set<String>
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(AutoSave.class.getCanonicalName());
        types.add(DataSave.class.getCanonicalName());
        return types;
    }

    /**
     * @param annotations 请求处理的注解的类型
     * @param roundEnv    包含有注解信息的
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, BeCreateClassInfo> infoMap = getInfoMap(annotations, roundEnv);
        if (!infoMap.isEmpty()) {
            for (Map.Entry<TypeElement, BeCreateClassInfo> entry : infoMap.entrySet()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, mes(entry.getKey(), entry.getValue()));

                try {
                    GenerateUtils.INSTANCE.generate(entry.getValue(), filer);
                } catch (IOException e) {
                    error(entry.getKey(), "无法生成类，typeElement： %s： %s", entry.getValue(), e.getMessage());
                }
            }
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "没东西");
        }
        return true;
    }

    /**
     * @param annotations
     * @param roundEnv
     * @return 一个类中含有多个被注解的字段，那么，生成类的时候就该遍历被注解的元素。
     * 然后获取他们的父元素，以构建被生成的类。这个被生成的类中含有多个属于他的被注解的字段
     */
    private Map<TypeElement, BeCreateClassInfo> getInfoMap(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, BeCreateClassInfo> infoMap = new LinkedHashMap<>();
        DataAutoSaveFieldInfo tmp;
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoSave.class)) {//获取被注解的元素
            if (canProcess(element)) {//如果是可以处理的元素，此时还未判断是不是可以被存储的
                TypeElement typeElement = (TypeElement) element.getEnclosingElement();//获取被注解元素所属的类元素，也就是被注解字段所属的类的信息

                BeCreateClassInfo beCreateClassInfo = getWillGeneratedClassInfo(typeElement, infoMap);//创建需要被创造的类的信息，多个element可能有同一个父元素
                tmp = new DataAutoSaveFieldInfo(
                        element.getSimpleName().toString(),
                        getErasuredFieldType(element),
                        getProcessedFiledType((VariableElement) element),
                        getFieldType(element),
                        element.getAnnotation(AutoSave.class).dataName()
                );
                if (procressConfilct(tmp, element)) {//处理使用bundle和persistence的冲突，如果是可以存储的，添加进beCreateClassInfo的list中
                    beCreateClassInfo.addField(tmp);//添加这个被创建类中包含的所有注解字段的信息
                } else {
                    log(element, "被注解的变量类型不在被支持的列表中");
                }
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "没有注解的元素");
            }
        }

        return infoMap;
    }

    /**
     * @param tmp     被注解字段信息
     * @param element 被注解元素
     *                元素可能被注释使用bundle，也可能同时被注释使用永久化存储，但是，即使被同事注视了两者，这个元素也可能不在被支持列表中
     * @return
     */
    private boolean procressConfilct(DataAutoSaveFieldInfo tmp, Element element) {
        boolean useBundle = true;
        boolean useSharedPreference = false;

        //获取被注解的属性结果
        useBundle = element.getAnnotation(AutoSave.class).useBundle();
        useSharedPreference = element.getAnnotation(AutoSave.class).Persistence();

        String processedFiledType = getProcessedFiledType((VariableElement) element);//如果被注解的变量类型不在被支持的列表中，抛出错误，用于bundle
        if (null == processedFiledType) {//使用bundle存储，但是支持列表中不支持这种类型
            useBundle = false;
        }
        if (useSharedPreference)
            useSharedPreference = canPersistence(element);//如果使用永久化存储，判断是不是可以使用永久化存储

        tmp.setPersistence(useSharedPreference);
        tmp.setUseBundle(useBundle);

        return useBundle || useSharedPreference;
    }

    /**
     * @param element
     * @return 持久化列表中是不是支持这个元素类型
     */
    private boolean canPersistence(Element element) {
        String type = getFieldType(element);
        boolean b=SomeUtils.SUPPORTED_PERSISTENCE_FIELD_TYPE.contains(type);
        log(element,"判断永久化存储列表 ：%s,,,%s",type,b);
        return b;
    }

    /**
     * @param element 被注解的元素
     * @return 是否可以持久化存储
     * 如果注解中要求持久化存储，验证这个元素的类型是不是在持久化存储的支持列表中，是的话，返回true
     */
    private boolean isCanPersistence(Element element) {
        boolean before = element.getAnnotation(AutoSave.class).Persistence();//元素是否要求被永久化
        String type = element.asType().toString();
        boolean result;//判断能不能被永久化
        if (before) {
            //验证是不是变量和public修饰
            result = canProcess(element) && SomeUtils.SUPPORTED_PERSISTENCE_FIELD_TYPE.contains(type);
        } else {
            result = false;
        }
        log(element, "element:\n " +
                "simpleName: %S \n " +
                "element asType: %s \n" +
                "element.astype.getkind: %s \n" +
                "element擦除泛型 : %s \n", element.getSimpleName().toString(), element.asType().toString(), element.asType().getKind().name(), getErasuredFieldType(element));

        return result;

    }

    /**
     * @param element 被注解的元素
     * @return 如果符合条件，返回true，表示可以继续处理
     */
    private boolean isCanSaveField(Element element) {
        boolean result = true;
        result = canProcess(element);//验证是不是变量和public修饰

        //如果被注解的变量类型不在被支持的列表中，抛出错误，用于bundle
        String processedFiledType = getProcessedFiledType((VariableElement) element);
        if (null == processedFiledType) {
            error(element, "被注解的变量类型不在被支持的列表中");
            result = false;
        }
        return result;
    }


    /**
     * @param typeElement 类元素
     * @param infoMap
     * @return
     */
    private BeCreateClassInfo getWillGeneratedClassInfo(TypeElement typeElement, Map<TypeElement, BeCreateClassInfo> infoMap) {

        //多个element可能有同一个父元素，因此，根据typeElement查询map，保证多个具有相同父元素的元素对应同一个BeCreateClassInfo
        BeCreateClassInfo info = infoMap.get(typeElement);
        if (null == info) {
            String classPackageName = getPackageName(typeElement);//被注解字段所属的类的包名
            String genclassName = getClassName(typeElement, classPackageName);//要生成的类的名称类名
            String qualifiedName = typeElement.getQualifiedName().toString();//被注解字段所属的类的全名

            info = new BeCreateClassInfo(
                    classPackageName,
                    qualifiedName,
                    genclassName
            );
            infoMap.put(typeElement, info);

            log(typeElement, "getWillGeneratedClassInfo方法:\n 被注解字段所属的类的包名： %s,\n" +
                            " 要生成的类的名称类名: %s, \n " +
                            "被注解字段所属的类的全名: %s \n",
                    classPackageName, genclassName, qualifiedName);
        }
        return info;
    }

    /**
     * @param typeElement 获取包名
     * @return
     */
    private String getPackageName(TypeElement typeElement) {
        return elements.getPackageOf(typeElement).getQualifiedName().toString();
    }

    /**
     * @param typeElement
     * @param packageName
     * @return 获取类名
     */
    private String getClassName(TypeElement typeElement, String packageName) {
        int packageLen = packageName.length() + 1;//获取的全名是包括包名加类名的，这里进行处理
        return typeElement.getQualifiedName().toString().substring(packageLen).replace('.', '$') + SUFFIX;
    }

    /**
     * @param element 被注解元素
     * @return 若是public修饰，或这是变量，返回true，标识这是可以被处理的。此时未判断在不在支持列表里
     */
    private boolean canProcess(Element element) {
        //如果被注解的元素不是变量，抛出错误
        if (!element.getKind().isField()) {
            error(element, "被注解元素不受支持");
            return false;
        }
        //验证字段修饰符,private或static修饰的话，抛出错误
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.STATIC)) {
            error(element, "字段不能被private或static修饰");
            return false;
        }
        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0)
            message = String.format(message, args);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);

    }

    /**
     * @param element
     * @param message 带或者不带格式化的字符串
     * @param args    格式化字符串的对象
     *                打印消息
     */
    private void log(Element element, String message, Object... args) {
        if (args.length > 0)
            message = String.format(message, args);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }

    /**
     * @param key
     * @param info
     * @return 打印一些信息
     */
    private String mes(TypeElement key, BeCreateClassInfo info) {
        StringBuilder builder = new StringBuilder();
        builder.append("注解字段所属类包名： " + info.getTargetClassPackageName() + "\n")
                .append("被生成类的类名： " + info.getWillGenerateClassName() + "\n")
                .append("注解字段所属类全名： " + info.getTargetClassQualifiedName() + "\n")
                .append("变量名称： " + info.getFields().get(0).getFiledName() + "\n")
                .append("变量类型： " + info.getFields().get(0).getFieldType() + "\n");
        StringBuilder builder1 = new StringBuilder();
        builder1.append("TypeElement\n")
                .append(key.getQualifiedName() + "\n")
                .append(key.getSimpleName() + "\n");
        builder.append(builder1);

        return builder.toString();
    }

    /**
     * @param element 被注解的元素
     * @return 返回被注解元素的继承自的类型
     * <p>
     * 处理被注解元素，获取到它继承自哪个类。
     * 例如：
     * public Ch[] testCh;  testCh就是一个com.crystal.dataautosave.Ch[]类型的变量，
     * 而Ch类继承自android.os.Parcelable
     * 因此，对于数组类型Ch[]，得获取它的组件类型（COM.CRYSTAL.DATAAUTOSAVE.CH ）然后进行判断
     * 处理后，返回android.os.Parcelable[]类型
     * <p>
     * 测试结果：
     * 调用getFieldType得到的结果：  android.os.Parcelable[]
     * public Ch[] testCh;
     * ^
     * 直接获取typeMirror结果：  com.crystal.dataautosave.Ch[]
     * 获取组件类型： COM.CRYSTAL.DATAAUTOSAVE.CH
     * 对应支持的interfaceType的tpeMirror:  android.os.Parcelable
     */
    private String getProcessedFiledType(VariableElement element) {
        String fieldType = getErasuredFieldType(element);//获取泛型擦除后的类型
        /*
         *element.asType()
         * 返回此元素定义的类型
         * 例如，对于一般类元素 C<N extends Number>，返回参数化类型 C<N>
         */

        StringBuilder stringBuilder = new StringBuilder();//接口类型，测试用
        StringBuilder componentStr = new StringBuilder();//获取组件类型，测试用


        TypeMirror typeMirror = element.asType();// 被注解的元素获取的类型,还没有去除泛型
        if (!SUPPORTED_FIELD_TYPE.contains(fieldType)) {//如果支持的类型没有它，比如这是个继承自某一个接口的类型这样
            for (String supportInterfaceType : SUPPORTED_INTERFACE_TYPE) {

                TypeMirror interfaceTypeMirror = elements.getTypeElement(supportInterfaceType).asType();

                stringBuilder.append(interfaceTypeMirror.toString() + "/分割线/");//测试用

                if (typeMirror instanceof ArrayType) {// 如果，这个子类型是数组类型
                    TypeMirror componentTypeMirror = ((ArrayType) typeMirror).getComponentType();//获取组件类型

                    componentStr.append(componentTypeMirror.toString());//测试用

                    if (types.isSubtype(componentTypeMirror, interfaceTypeMirror)) {//判断这个它是不是这个interface类型的子类型
                        fieldType = supportInterfaceType + "[]";//是个数组类型，得加上中括号
                        break;
                    }
                } else if (types.isSubtype(typeMirror, interfaceTypeMirror)) {//如果不是数组类型，这个元素的类型是这几个的子类型
                    fieldType = supportInterfaceType;//返回的就是他的父类型
                    break;
                } else {//无法识别的类型，返回null
                    fieldType = null;
                    break;
                }
            }
        }

        log(element, "getProcessedFiledType方法:\n 调用getFieldType得到的结果：  %s \n " +
                "直接typeMirror.toString()结果： %s \n" +
                "获取组件类型： %S \n" +
                "interfaceType的tpeMirror: %s \n", fieldType, typeMirror.toString(), componentStr, stringBuilder.toString());//测试用

        return fieldType;
    }

    /**
     * @param element 被注解的元素
     * @return 返回被注解元素的类型
     * 例如： public Ch[] testCh;  testCh就是一个com.crystal.dataautosave.Ch[]类型的变量，而Ch类继承自android.os.Parcelable
     * 结果返回com.crystal.dataautosave.Ch[]
     */
    private String getErasuredFieldType(Element element) {
        //erasure方法：返回指定类型擦除后的TypeMirror.
        //不保存泛型的信息，只保留这是什么类型。例如List<?> list = new ArrayList<Object>();
        String name = types.erasure(element.asType()).toString();
        log(element, "getErasuredFieldType方法: \n 获取到的去除泛型的类型： %s \n ", name);
        return name;
    }

    private String getFieldType(Element element) {
        String result;
        result = element.asType().toString();
        log(element, "getFieldType方法，未擦除时的类型： %s", result);
        return result;
    }
}
