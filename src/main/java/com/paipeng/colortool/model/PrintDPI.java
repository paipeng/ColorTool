package com.paipeng.colortool.model;

public class PrintDPI {
    private String name;
    private Integer dpi;

    public PrintDPI(String name, Integer dpi) {
        super();
        this.name = name;
        this.dpi = dpi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDpi() {
        return dpi;
    }

    public void setDpi(Integer dpi) {
        this.dpi = dpi;
    }
}
