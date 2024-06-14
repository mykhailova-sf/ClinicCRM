package com.somihmih.er.entity;

/**
 * Base class for all entities that can be printed.
 * @author SoMihMih
 * @version 1.0
 * @see Entity
 *
 */
abstract class PrintableEntity {

    /**
     * @return the type of the entity
     */
    abstract String getEntityType();

    /**
     * @return the values of the entity to be printed
     */
    abstract String[] getValuesToPrint();

    /**
     * @return true if the entity is deleted, false otherwise
     */
    abstract boolean isDeleted();


    // 4. Template Method
    /**
     * @return the string representation of the entity
     * Uses methods from child classes to get the values to print
     */
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
