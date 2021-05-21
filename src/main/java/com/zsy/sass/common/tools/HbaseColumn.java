package com.zsy.sass.common.tools;


import java.lang.annotation.*;

/**
 *  created by yqq
 *  date 2021-5-21
 * 自定义注解 : 用于描叙hbase字段所属的列族和列
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface HbaseColumn {

    String family() default "";

    String qualifier() default "";

    boolean timestamp() default false;


}
