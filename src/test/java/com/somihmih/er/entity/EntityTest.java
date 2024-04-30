package com.somihmih.er.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutput;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    private Entity entity;

    @BeforeEach
    void setUp() {
        entity = new Entity() {
            @Override
            public void saveYourselfTo(DataOutput outputStream) {
            }

            @Override
            public void loadYourselfFrom(DataInputStream inputStream) {
            }

            @Override
            public Entity getClone() {
                return null;
            }

            @Override
            public int getId() {
                return 0;
            }

            @Override
            public int getSizeInBytes() {
                return 0;
            }

            @Override
            public void markAsDeleted() {
            }

            @Override
            public boolean isDeleted() {
                return false;
            }
        };
    }

    @Test
    void normalizeToMaxLen_WhenNeedToAddSymbols() {
        assertEquals("John      ", entity.normalizeToMaxLen("John", 10));
    }

    @Test
    void normalizeToMaxLen_WhenNeedToCut() {
        assertEquals("John", entity.normalizeToMaxLen("John Doe", 4));
    }
}