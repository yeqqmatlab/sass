package com.zsy.sass.common.exception;

/**
 * Created by gj on 19-4-22.
 * 标记 标记后的bean配置可以不加载
 * 配合
 * @ComponentScan(value = "cn.wyn",
 *         excludeFilters = { @ComponentScan.Filter(
 *         type = FilterType.ANNOTATION,
 *         classes = {ZSYMark.class})}
 *         使用
 */
public @interface ZSYMark {
}
