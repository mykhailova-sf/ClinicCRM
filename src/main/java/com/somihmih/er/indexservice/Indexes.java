package com.somihmih.er.indexservice;

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
