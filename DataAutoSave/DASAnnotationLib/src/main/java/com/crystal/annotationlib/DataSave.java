package com.crystal.annotationlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/27 18:15
 * 手动控制何时保存和恢复数据
 */
@Retention(RetentionPolicy.CLASS)
@Target( ElementType.FIELD)
public @interface DataSave {
}
