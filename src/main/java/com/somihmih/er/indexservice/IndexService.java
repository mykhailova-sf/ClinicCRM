package com.somihmih.er.indexservice;

import java.io.*;
import java.util.Arrays;

/**
 * IndexService implements Indexes
 * @author SoMihMih
 * @version 1.0
 * @see IndexService
 * {@inheritDoc}
 */

/**
 * IndexService implements Indexes
 */

public class IndexService implements Indexes {

    public static final int POSITION_NUM_SIZE = Integer.BYTES;
    public static final int ENTITY_ID_SIZE = Integer.BYTES;

    public static final int MAX_COUNT = 100;

    protected Index[] indexes = new Index[MAX_COUNT];

    protected int count = 0;

    private int currentMaxIndex = 0;
    private final String fileName;

    public IndexService(String fileName) {
        this.fileName = fileName;
        loadIndexes();
    }

    @Override
    public void saveToFile() {
        recreateIndexFile(Arrays.copyOfRange(indexes, 0, count));
    }

    @Override
    public void loadIndexes() {
        indexes = new Index[MAX_COUNT];
        count = 0;
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(fileName))) {
            while (inputStream.available() >= (ENTITY_ID_SIZE + POSITION_NUM_SIZE + 1)) {
                indexes[count] = new Index(inputStream.readInt(), inputStream.readInt(), inputStream.readBoolean());
                if (indexes[count].getEntityId() > currentMaxIndex) {
                    currentMaxIndex = indexes[count].getEntityId();
                }
                count++;
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении из файла: " + e.getMessage());
        }
    }

    @Override
    public void recreateIndexFile(Index[] indices) {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(fileName))) {
            for (Index index : indices) {
                outputStream.writeInt(index.getEntityId());
                outputStream.writeInt(index.getPos());
                outputStream.writeBoolean(index.isDeleted());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи файл: " + e.getMessage());
        }

        loadIndexes();
    }

    @Override
    public void addIndex(Index index) {
        if (index.getPos() == count) {
            indexes[count++] = new Index(index.getEntityId(), index.getPos(), index.isDeleted());
        } else {
            indexes[index.getPos()] = new Index(index.getEntityId(), index.getPos(), index.isDeleted());
        }
    }

    @Override
    public int getNewId() {
        return ++currentMaxIndex;
    }

    @Override
    public Index getNewIndex() {
        int i = 0;
        while (i < count) {
            if (indexes[i].isDeleted()) {
                return new Index(getNewId(), indexes[i].getPos());
            }
            i++;
        }

        return new Index(getNewId(), count);
    }

    @Override
    public int getPosition(int id) {
        Index index = getIndexFor(id);

        return (index != null) ? index.getPos() : -1 ;
    }

    @Override
    public Index getIndexFor(int id) {
        for (Index index : indexes) {

            if (index != null && index.getEntityId() == id) {
                return index;
            }
        }

        return null;
    }
}
