package com.zsy.sass.common.tools;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface HbaseTable {
    String tableName() default "";
}
