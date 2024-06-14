package com.somihmih.er.indexservice;

// 3. IndexServiceWithLogs via Decorator pattern
public class IndexServiceWithLogs implements Indexes {

    private final String name;
    private IndexService indexService;

    public IndexServiceWithLogs(IndexService indexService, String name) {
        this.indexService = indexService;
        this.name = name;
    }

    @Override
    public void saveToFile() {
        System.out.println(name + "_ Saving indexes to file");
        indexService.saveToFile();
    }

    @Override
    public void loadIndexes() {
        System.out.println(name + "_ Loading indexes from file");
        indexService.loadIndexes();
    }

    @Override
    public void recreateIndexFile(Index[] indices) {
        System.out.println(name + "_ Recreating index file");
        indexService.recreateIndexFile(indices);
    }

    @Override
    public void addIndex(Index index) {
        System.out.println(name + "_ Adding index");
        indexService.addIndex(index);
    }

    @Override
    public int getNewId() {
        int newId = indexService.getNewId();
        System.out.println(name + "_ Getting new id: " + newId);

        return newId;
    }

    @Override
    public Index getNewIndex() {
        Index newIndex = indexService.getNewIndex();
        System.out.println(name + "_ Getting new index: " + newIndex);

        return newIndex;
    }

    @Override
    public int getPosition(int id) {
        int position = indexService.getPosition(id);
        System.out.println(name + "_ Getting position for id: " + id + " position: " + position);

        return position;
    }

    @Override
    public Index getIndexFor(int id) {
        Index index = indexService.getIndexFor(id);
        System.out.println(name + "_ Getting index for id: " + id + " index: " + index);

        return index;
    }
}
