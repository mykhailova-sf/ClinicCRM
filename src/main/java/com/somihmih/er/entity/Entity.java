package com.somihmih.er.entity;

import java.io.DataInputStream;
import java.io.DataOutput;

public interface Entity {
    void saveYourselfTo(DataOutput outputStream);
    void loadYourselfFrom(DataInputStream inputStream);

    Entity getClone();

    int getId();

    int getSizeInBytes();

    void markAsDeleted();

    boolean isDeleted();

}
