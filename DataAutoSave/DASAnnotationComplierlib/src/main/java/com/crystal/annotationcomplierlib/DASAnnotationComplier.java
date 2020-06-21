package com.crystal.annotationcomplierlib;

import com.crystal.annotationlib.AutoSave;
import com.google.auto.service.AutoService;

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
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.crystal.annotationcomplierlib.SomeUtils.SUFFIX;
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
 * */

/**
 * 处理autosave注解
 */
@AutoService(AutoSave.class)
public class DASAnnotationComplier extends AbstractProcessor {
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
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

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    /**
     * @param annotations 请求处理的注解的类型
     * @param roundEnv    包含有注解信息的
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, BeCreateClassInfo> infoMap = getInfoMap(annotations, roundEnv);
        if (infoMap != null) {
            for (Map.Entry<TypeElement, BeCreateClassInfo> entry : infoMap.entrySet()) {
                GenerateUtils.INSTANCE.generate(entry.getKey(), entry.getValue());
            }
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

        for (Element element : roundEnv.getElementsAnnotatedWith(AutoSave.class)) {//获取被注解的元素
            if (isAccessibleField(element)) {
                TypeElement typeElement = (TypeElement) element.getEnclosingElement();//类元素，也就是被注解字段所属的类的信息

                BeCreateClassInfo beCreateClassInfo = getBeCreateClassInfo(typeElement, infoMap);//创建需要被创造的类的信息，多个element可能有同一个父元素
                beCreateClassInfo.addField(new DataAutoSaveFieldInfo(
                        element.getSimpleName().toString(),
                        element.getAnnotation(AutoSave.class).dataName(),
                        getProcessedFiledType(element)
                ));//添加这个被创建类中包含的所有注解字段的信息

            }


        }

        return null;
    }

    /**
     * @param typeElement 类元素
     * @param infoMap
     * @return
     */
    private BeCreateClassInfo getBeCreateClassInfo(TypeElement typeElement, Map<TypeElement, BeCreateClassInfo> infoMap) {

        //多个element可能有同一个父元素，因此，根据typeElement查询map，保证只有一个BeCreateClassInfo的value
        BeCreateClassInfo info = infoMap.get(typeElement);
        if (null == info) {
            String classPackage = getPackageName(typeElement);

            info = new BeCreateClassInfo(
                    classPackage,
                    getClassName(typeElement, classPackage),
                    typeElement.getQualifiedName().toString()
            );
        }
        return info;
    }

    private String getPackageName(TypeElement typeElement) {
        return elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
    }

    private String getClassName(TypeElement typeElement, String packageName) {
        int packageLen = packageName.length() + 1;
        return typeElement.getQualifiedName().toString().substring(packageLen).replace('.', '$') + SUFFIX;
    }

    private boolean isAccessibleField(Element element) {

        return true;
    }

    private String getProcessedFiledType(Element element) {
        return "ksjbdckdsbf";
    }
}
