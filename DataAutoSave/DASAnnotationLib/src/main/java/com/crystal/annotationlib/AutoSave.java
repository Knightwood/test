package com.crystal.annotationlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据自动存储注解的定义,用于生命周期结束时保存恢复数据
 * 2020.6.20 by kiylx
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface AutoSave {
    String dataName() default "";

    boolean Persistence() default false;

    boolean useBundle() default true;
}
