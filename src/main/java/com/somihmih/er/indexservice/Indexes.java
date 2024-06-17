package com.somihmih.er.indexservice;

/**
 * Indexes
 * @author SoMihMih
 * @version 1.0
 * @see com.somihmih.er.indexservice.Indexes
 * {@inheritDoc}
 */

/**
 * Indexes interface (For IndexService and IndexServiceWithLogs)
 */

public interface Indexes {
    void saveToFile();

    void loadIndexes();

    void recreateIndexFile(Index[] indices);

    void addIndex(Index index);

    int getNewId();

    Index getNewIndex();

    int getPosition(int id);

    Index getIndexFor(int id);
}
