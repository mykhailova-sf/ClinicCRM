package com.somihmih.er.entity;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

public class Admission extends PrintableEntity implements Entity{

    public static final int ID_SIZE = Integer.BYTES;
    public static final int MAX_DATE_LEN = 22;
    public static final int MAX_DESCRIPTION_LEN = 50;
    public static final int PATIENT_ID_LEN = Integer.BYTES;

    public void markAsDeleted() {
        this.deleted = true;
    }

    private boolean deleted = false;

    private int id = -1;
    private String date;
    private int patientId = -1;
    private String description = "";
    private int nextAdId = -1;

    public Admission() {
    }

    public Admission(int id, String date, int patientId, int nextAdId, boolean deleted) {
        this.id = id;
        this.date = date;
        this.patientId = patientId;
        this.nextAdId = nextAdId;
        this.deleted = deleted;
    }

    public Admission(int id, String date, int patientId) {
        this(id, date, patientId, -1, false);
    }

    public Admission(String date, int patientId) {
        this(-1, date, patientId, -1, false);
    }

    @Override
    public Admission getClone() {
        Admission admission = new Admission(id, date, patientId, nextAdId, deleted);
        admission.setDescription(description);

        return admission;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getNextAdId() {
        return nextAdId;
    }

    public void setNextAdId(int nextAdId) {
        this.nextAdId = nextAdId;
    }

    @Override
    public void saveYourselfTo(DataOutput outputStream) {

        try {
            String dateToSave = (date.length() < MAX_DATE_LEN) ?
                date + " ".repeat(MAX_DATE_LEN - date.length())
              : date.substring(0, MAX_DATE_LEN);

            outputStream.writeInt(id);
            outputStream.write(dateToSave.getBytes("UTF-16BE"));
            outputStream.writeInt(patientId);
            outputStream.writeInt(nextAdId);
            outputStream.writeBoolean(deleted);
            outputStream.write(
                    normalizeToMaxLen(this.description, MAX_DESCRIPTION_LEN)
                   .getBytes("UTF-16BE")
            );
        } catch (IOException e) {
            System.out.println("Error  " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadYourselfFrom(DataInputStream inputStream) {
        try {
            id = inputStream.readInt();
            byte[] dateAsBytes = new byte[Admission.MAX_DATE_LEN * 2];
            inputStream.readFully(dateAsBytes);
            date = new String(dateAsBytes, "UTF-16").trim();
            patientId = inputStream.readInt();
            nextAdId = inputStream.readInt();
            deleted = inputStream.readBoolean();

            byte[] descriptionAsBytes = new byte[MAX_DESCRIPTION_LEN * 2];
            inputStream.readFully(descriptionAsBytes);
            description = new String(descriptionAsBytes, "UTF-16");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    String getEntityType() {
        return "Admission";
    }

    @Override
    String[] getValuesToPrint() {
        return new String[] {
                "id:" + id,
                "date:'" + date,
                "price:" + patientId,
                "nextAdId:" + nextAdId
        };
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public int getSizeInBytes() {
        return ID_SIZE
                + MAX_DATE_LEN * 2
                + PATIENT_ID_LEN
                + ID_SIZE
                + 1
                + MAX_DESCRIPTION_LEN * 2;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
