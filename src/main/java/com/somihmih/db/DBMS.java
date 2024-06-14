package com.somihmih.db;

import com.somihmih.er.entity.Admission;
import com.somihmih.er.indexservice.Index;
import com.somihmih.er.indexservice.IndexService;
import com.somihmih.er.entity.Patient;
import com.somihmih.er.entity.Entity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class DBMS {

    private static DBMS instance;

    // For Singleton pattern
    public static DBMS getInstance() {
        if (instance == null) {
            instance = new DBMS();
        }
        return instance;
    }

    public static final String ADMISSIONS = "./dbfiles/addmissions";
    public static final String PATIENTS = "./dbfiles/patients";
    public static final String ADMISSION_INDEXES = "./dbfiles/addmissionIndexes";
    public static final String PATIENTS_INDEXES = "./dbfiles/patientsIndexes";

    private final IndexService patientIndexService;
    private final IndexService admissionIndexService;

    private DBMS() {
        patientIndexService = new IndexService(PATIENTS_INDEXES);
        admissionIndexService = new IndexService(ADMISSION_INDEXES);
        System.out.println("DBMS started");
    }

    public void insertPatient(Patient patient) {
        Index index = patientIndexService.getNewIndex(); // Index:{patient-Id, position-In-Table, deleted-mark}
        patient.setId(index.getEntityId());

        System.out.println("Position to insert: " + index.getPos());
        saveEntityToPositionIn(patient, index.getPos(), PATIENTS);

        patientIndexService.addIndex(index);
        patientIndexService.saveToFile();
    }

    public void insertAdmission(Admission addmission) {
        System.out.println("Я буду сохранять запись на дату " + addmission.getDate());

        Index index = admissionIndexService.getNewIndex();
        addmission.setId(index.getEntityId());
        saveEntityToPositionIn(addmission, index.getPos(), ADMISSIONS);

        admissionIndexService.addIndex(index);
        admissionIndexService.saveToFile();
    }

    public Patient[] getAllPatients() {
        Entity[] entities = readFromFile(PATIENTS, new Patient());
        Patient[] patients = new Patient[entities.length];
        int i = 0;
        for (Entity entity : entities) {
            patients[i++] = (Patient) entity;
        }
        return patients;
    }

    public void savePatient(Patient patient) {
        int position = patientIndexService.getPosition(patient.getId());
        saveEntityToPositionIn(patient, position, PATIENTS);
    }

    private void saveEntityToPositionIn(Entity entity, int position, String fileName) {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "rw")) {
            file.seek(position * entity.getSizeInBytes());
            entity.saveYourselfTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAdmission(Admission admission) {
        int position = admissionIndexService.getPosition(admission.getId());
        saveEntityToPositionIn(admission, position, ADMISSIONS);
    }

    public Entity[] readAllAdmissions() {
        return  readFromFile(ADMISSIONS, new Admission());
    }

    private Entity[] readFromFile(String fileName, Entity entity) {
        int count = 0;
        Entity[] entities = new Entity[100];
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(fileName))) {
            while (inputStream.available() >= entity.getSizeInBytes()) {
                entity.loadYourselfFrom(inputStream);
                entities[count++] = entity.getClone();
            }
        } catch (IOException exception) {
            System.out.println("Ошибка при чтении из файла " + fileName + " : " + exception.getMessage());
        }

        return Arrays.copyOfRange(entities, 0, count);
    }
    private Entity readFromFileOneEntity(String fileName, Entity entity, int position) {
        if (position < 0) {
            return null;
        }
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(fileName))) {
            inputStream.skipBytes(position * entity.getSizeInBytes());
            entity.loadYourselfFrom(inputStream);
            return entity.getClone();
        } catch (IOException e) {
            System.out.println("Ошибка при чтении из файла: " + e.getMessage());
        }

        return null;
    }

    public void reindexPatients() {
        patientIndexService.recreateIndexFile(getIndices(getAllPatients()));
    }

    public void reindexAdmissions() {
        admissionIndexService.recreateIndexFile(getIndices(readAllAdmissions()));
    }

    private static Index[] getIndices(Entity[] entities) {
        Index[] indices = new Index[entities.length];

        for (int pos = 0; pos < entities.length; pos++) {
            indices[pos] = new Index(entities[pos].getId(), pos, entities[pos].isDeleted());
        }
        return indices;
    }

    public void savePatientIndices() {
        patientIndexService.saveToFile();
    }

    public void saveAdmissionsIndices() {
        admissionIndexService.saveToFile();
    }

    public Patient getPatient(int patientId) {
        int patientPos = patientIndexService.getPosition(patientId);

        return (Patient) readFromFileOneEntity(PATIENTS, new Patient(), patientPos);
    }
    public Admission getAdmission(int id) {
        int position = admissionIndexService.getPosition(id);

        return (Admission) readFromFileOneEntity(ADMISSIONS, new Admission(), position);
    }

    public void deletePatient(int id) {
        Patient patient = getPatient(id);
        if (patient == null) {
            return;
        }

        patient.markAsDeleted();
        savePatient(patient);
        patientIndexService.getIndexFor(id).setDeleted();
        patientIndexService.saveToFile();
    }

    public void deleteAdmission(int id) {
        deleteAdmission(id, -1);
    }

    public void deleteAdmission(int id, int prevId) {
        Admission admission = getAdmission(id);
        if (admission == null) {
            return;
        }

        if (prevId != -1) {
            Admission prevAdmission = getAdmission(prevId);
            prevAdmission.setNextAdId(admission.getNextAdId());
            saveAdmission(prevAdmission);
        }

        admission.markAsDeleted();
        admissionIndexService.getIndexFor(id).setDeleted();
        admissionIndexService.saveToFile();

        saveAdmission(admission);
    }

    public void clearDb() {
        String[] files = new String[]{ADMISSIONS, PATIENTS, ADMISSION_INDEXES, PATIENTS_INDEXES};

        for (String file : files) {
            System.out.println("Removing: " + file);
            try {
                Files.delete(Paths.get(file));
            } catch (IOException e) {
                System.out.println("clearDb error: " + e.getMessage());
            }
        }


    }

    public Admission[] getAllAdmissionsFromFirst(int firstAdmissionId) {
        if (firstAdmissionId == -1) {
            return null;
        }
        Admission[] admissions = new Admission[IndexService.MAX_COUNT];
        int count = 0;
        int currentAdmissionId = firstAdmissionId;
        do {
            admissions[count] = getAdmission(currentAdmissionId);
            currentAdmissionId = admissions[count].getNextAdId();
            count++;
        } while (currentAdmissionId != -1);

        return Arrays.copyOfRange(admissions, 0, count);
    }

    public void deletePatientWithAdmissions(Patient patient) {
        Admission[] admissions = getAllAdmissionsFromFirst(patient.getFirstAdmissionId());

        deletePatient(patient.getId());
        for (Admission admission : admissions) {
            deleteAdmission(admission.getId());
        }
    }

    public Admission[] getAllAdmissions() {
        Entity[] entities = readFromFile(ADMISSIONS, new Admission());
        Admission[] admissions = new Admission[entities.length];
        int i = 0;
        for (Entity entity : entities) {
            admissions[i++] = (Admission) entity;
        }
        return admissions;
    }
}
