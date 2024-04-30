package com.somihmih.er.indexservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IndexServiceTest {

    @Test
    void testGetNewIndex() {
        IndexService indexService = new IndexService("") {
            @Override
            public void loadIndexes() {
                // do not read file
                indexes = new Index[]{
                        new Index(0, 0),
                        new Index(1, 1)
                };
                count = indexes.length;
            }
        };

        Index newIndex = indexService.getNewIndex();
        assertEquals(2, newIndex.getPos());
        assertFalse(newIndex.isDeleted());
    }

    @Test
    void testGetNewIndex_whenThereIsDeletedIndex() {
        IndexService indexService = new IndexService("") {
            @Override
            public void loadIndexes() {
                // do not read file
                indexes = new Index[]{
                        new Index(0, 0),
                        new Index(1, 1, true),
                        new Index(2, 3)
                };
                count = indexes.length;
            }
        };

        Index newIndex = indexService.getNewIndex();
        assertEquals(1, newIndex.getPos());
        assertFalse(newIndex.isDeleted());
    }
}