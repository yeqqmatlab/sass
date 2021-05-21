package com.zsy.sass.common.enums;

public enum ZSYLevelCountType {
    SCORE_COUNT(0,"按分数分档"),NUM_COUNT(1,"按人数分档"),DEFAYLT_COUNT(2,"恢复默认");
    private int value;
    private String name;

    ZSYLevelCountType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
    public String getName(int value){
        for(ZSYLevelCountType type:ZSYLevelCountType.values()){
            if(value==type.value){
                return type.name;
            }
        }
        return "";
    }
}
