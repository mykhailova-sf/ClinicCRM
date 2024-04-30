package com.somihmih.er.indexservice;

public class Index {
    private final int entityId;
    private final int pos;
    private boolean deleted = false;

    public Index(int id, int pos) {
        this.entityId = id;
        this.pos = pos;
    }

    public Index(int id, int pos, boolean deleted) {
        this.entityId = id;
        this.pos = pos;
        this.deleted = deleted;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getPos() {
        return pos;
    }

    public void setDeleted() {
        deleted = true;
    }

    @Override
    public String toString() {
        return "Index{" +
                "id=" + entityId +
                ", pos=" + pos +
                ((deleted) ? ", DELETED" : ", ACTIVE") +
                '}';
    }

    public boolean isDeleted() {
        return deleted;
    }
}
