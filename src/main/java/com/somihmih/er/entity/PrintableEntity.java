package com.somihmih.er.entity;

abstract class PrintableEntity {

    abstract String getEntityType();

    abstract String[] getValuesToPrint();

    abstract boolean isDeleted();


    // 4. Template Method
    @Override
    public String toString() {
        String values = "";
        for (String value : getValuesToPrint()) {
            values += value + ", ";
        }
        values = values.substring(0, values.length() - 2);

        String isDeleted = (isDeleted()) ? ", DELETED" : "";

        return getEntityType() + ": (" + values + isDeleted + ")";
    }
}
