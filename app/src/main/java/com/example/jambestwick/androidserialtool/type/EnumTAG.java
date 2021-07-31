package com.example.jambestwick.androidserialtool.type;

/**
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/11/5<p>
 * <p>更新时间：2019/11/5<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public enum EnumTAG {
    END(0, "SUCCESS"),
    NOT_END(1, "NOT_END"),
    NOT_HEAD(2, "NOT_START"),
    DATA_LEN_ERROR(3, "DATA_LEN_ERROR"),
    DATA_XOR_ERROR(4, "DATA_XOR_ERROR");
    private int key;
    private String value;

    EnumTAG(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
