package com.nocturna.performance.export.dto;

import java.io.Serializable;
import java.util.Objects;

public class ExportProductID implements Serializable {
    private String part_number;
    private String upc;

    public ExportProductID(String part_number, String upc){
        this.part_number=part_number;
        this.upc=upc;
    }

    public ExportProductID(){
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExportProductID that = (ExportProductID) o;
        return Objects.equals(part_number, that.part_number) && Objects.equals(upc, that.upc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(part_number, upc);
    }
}
