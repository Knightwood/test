package com.crystal.annotationcomplierlib;

import com.crystal.annotationlib.AutoSave;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
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
                    e.printStackTrace();
                }
            }
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "没东西");
        }
        return true;
    }

    String mes(TypeElement key, BeCreateClassInfo info) {
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
     * @param annotations
     * @param roundEnv
     * @return 一个类中含有多个被注解的字段，那么，生成类的时候就该遍历被注解的元素。
     * 然后获取他们的父元素，以构建被生成的类。这个被生成的类中含有多个属于他的被注解的字段
     */
    private Map<TypeElement, BeCreateClassInfo> getInfoMap(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, BeCreateClassInfo> infoMap = new LinkedHashMap<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(AutoSave.class)) {//获取被注解的元素
            if (isCanSaveField(element)) {//如果是可以存储的元素
                TypeElement typeElement = (TypeElement) element.getEnclosingElement();//获取被注解元素所属的类元素，也就是被注解字段所属的类的信息

                BeCreateClassInfo beCreateClassInfo = getBeCreateClassInfo(typeElement, infoMap);//创建需要被创造的类的信息，多个element可能有同一个父元素
                beCreateClassInfo.addField(new DataAutoSaveFieldInfo(
                        element.getSimpleName().toString(),
                        element.getAnnotation(AutoSave.class).dataName(),
                        getProcessedFiledType(element)
                ));//添加这个被创建类中包含的所有注解字段的信息

            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "没有注解的元素");
            }
        }

        return infoMap;
    }

    /**
     * @param typeElement 类元素
     * @param infoMap
     * @return
     */
    private BeCreateClassInfo getBeCreateClassInfo(TypeElement typeElement, Map<TypeElement, BeCreateClassInfo> infoMap) {

        //多个element可能有同一个父元素，因此，根据typeElement查询map，保证多个具有相同父元素的元素对应同一个BeCreateClassInfo
        BeCreateClassInfo info = infoMap.get(typeElement);
        if (null == info) {
            String classPackageName = getPackageName(typeElement);//被注解字段所属的类的包名
            String genclassName=getClassName(typeElement, classPackageName);//要生成的类的名称类名
            String qualifiedName=typeElement.getQualifiedName().toString();//被注解字段所属的类的全名

            info = new BeCreateClassInfo(
                    classPackageName,
                    qualifiedName,
                    genclassName
            );
            infoMap.put(typeElement, info);
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
     * @param element 被注解的元素
     * @return 如果符合条件，返回true，表示可以继续处理
     */
    private boolean isCanSaveField(Element element) {
        boolean result = true;
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();//获取父元素，转为类元素

        //如果被注解的元素不是变量，抛出错误
        if (!element.getKind().isField()) {
            result = false;
        }
        //验证字段修饰符,private或static修饰的话，抛出错误
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.STATIC)) {
            result = false;
        }
        //如果被注解的变量类型不在被支持的列表中，抛出错误
        String filedType = getProcessedFiledType(element);
        if (!SUPPORTED_FIELD_TYPE.contains(filedType)) {
            result = false;
        }
        return result;
    }

    /**
     * @param element 被注解的元素
     * @return 返回被注解元素的类型
     */
    private String getProcessedFiledType(Element element) {
        String filedType = getFieldType(element);//获取泛型擦除后的类型
        /*
         *element.asType()
         * 返回此元素定义的类型
         * 例如，对于一般类元素 C<N extends Number>，返回参数化类型 C<N>
         */
        /*TypeMirror typeMirror = element.asType();
        if (!SomeUtils.SUPPORTED_FIELD_TYPE.contains(fieldType)) {
            for (String interfaceType : SomeUtils.SUPPORTED_INTERFACE_TYPE) {
                //如果字段是数组类型，则使用组件类型进行比较
                //非数组类型不能通过**getComponentType()**方法获得元素的Class对象类型
                if (typeMirror instanceof ArrayType) {
                    TypeMirror componentTypeMirror = ((ArrayType) typeMirror).getComponentType();
                }
            }

        }
        return fieldType;*/

        TypeMirror typeMirror = element.asType();// 被注解的元素获取的类型
        if (!SUPPORTED_FIELD_TYPE.contains(filedType)) {//如果支持的类型没有它，比如这是个继承自某一个接口的类型这样
            for (String interfaceType : SUPPORTED_INTERFACE_TYPE) {
                // if filed is array type, use component type to compare
                if (typeMirror instanceof ArrayType) {// 如果，这个子类型是数组类型
                    TypeMirror componentTypeMirror = ((ArrayType) typeMirror).getComponentType();
                    if (isSubtypeOfType(componentTypeMirror, interfaceType)) {//判断这个元素是不是这个interface的子类型
                        filedType = interfaceType + "[]";//是个数组类型，得加上中括号
                        break;
                    }
                } else if (isSubtypeOfType(typeMirror, interfaceType)) {//如果不是数组类型，这个元素的类型是这几个的子类型
                    filedType = interfaceType;//返回的就是他的父类型
                    break;
                }
            }
        }
        return filedType;
    }

    /**
     * @param element 被注解的元素
     * @return 返回被注解元素的类型
     */
    private String getFieldType(Element element) {
        /*TypeMirror typeMirror = element.asType();
        //erasure方法：返回指定类型擦除后的TypeMirror.
        String kindname = types.erasure(typeMirror).toString();
        //不保存泛型的信息，只保留这是什么类型。例如List<?> list = new ArrayList<Object>();
        return kindname;*/
        String name = types.erasure(element.asType()).toString();
        int typeParamStart = name.indexOf('<');
        if (typeParamStart != -1) {
            name = name.substring(0, typeParamStart);
        }
        return name;
    }

    private boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
        if (otherType.equals(typeMirror.toString())) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {//不是个类或者接口
            return false;
        }
        //DeclaredType表示声明的类型，可以是类类型或接口类型。 这包括参数化类型，例如java.util.Set <String>以及原始类型。
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();//返回此类型的实际类型参数。 对于嵌套在参数化类型中的类型（例如Outer <String> .Inner <Number>），仅包括最里面类型的类型参数。
        if (typeArguments.size() > 0) {
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
            typeString.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    typeString.append(',');
                }
                typeString.append('?');
            }
            typeString.append('>');
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (isSubtypeOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }
}
