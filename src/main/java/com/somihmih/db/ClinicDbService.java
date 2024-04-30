package com.somihmih.db;

import com.somihmih.er.entity.Admission;
import com.somihmih.er.entity.Patient;

import java.util.Arrays;

public class ClinicDbService {

    private final DBMS dbms;

    public ClinicDbService(DBMS dbms) {
        this.dbms = dbms;
    }

    public void insertPatient(String name, String phone) {
        dbms.insertPatient(new Patient(name, phone));
    }

    public Patient[] getAllActivePatients() {
        return Arrays.stream(dbms.getAllPatients())
                .filter(patient -> !patient.isDeleted())
                .toArray(Patient[]::new);
    }

    public Admission[] getAllActiveAdmissions() {
        return Arrays.stream(dbms.getAllAdmissions())
                .filter(admission -> !admission.isDeleted())
                .toArray(Admission[]::new);
    }

    public Admission[] getActivePatientAdmissions(int patientId) {
        Patient patient = getPatient(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Отсутсвует пацик с ID: " + patientId);
        }
        if (patient.getFirstAdmissionId() == -1) {
            return new Admission[]{};
        }

        return Arrays.stream(dbms.getAllAdmissionsFromFirst(patient.getFirstAdmissionId()))
                .filter(admission -> !admission.isDeleted())
                .toArray(Admission[]::new);
    }

    public void reindexAdmission() {
        System.out.println("Переиндексация приема...");
        dbms.reindexAdmissions();
    }

    public void reindexPatients() {
        System.out.println("Переиндексация пациентов...");
        dbms.reindexPatients();
    }

    public void saveIndices() {
        System.out.println("Сохраняю индексы пациентов...");
        dbms.savePatientIndices();
        System.out.println("Сохраняю индексы записей...");
        dbms.saveAdmissionsIndices();
    }

    public void insertAdmission(int patientId, Admission newAdmission) {
        Patient patient = getPatient(patientId);
        if (patient == null) {
            return;
        }
        dbms.insertAdmission(newAdmission);

        Admission[] admissions = dbms.getAllAdmissionsFromFirst(patient.getFirstAdmissionId());

        if (admissions == null) {
            patient.setAdmissionId(newAdmission.getId());
            dbms.savePatient(patient);
            return;
        }

        Admission lastAdmission = admissions[admissions.length - 1];
        lastAdmission.setNextAdId(newAdmission.getId());
        dbms.saveAdmission(lastAdmission);
    }

    public void reindexAll() {
        reindexPatients();
        reindexAdmission();
    }

    public void updatePatient(Patient patient) {
        if (getPatient(patient.getId()) == null) {
            return;
        }

        dbms.savePatient(patient);
    }

    public void updateAdmission(Admission admission) {
        if (dbms.getAdmission(admission.getId()) == null) {
            System.out.println("C таким id ничего не найдено");
            return;
        }

        dbms.saveAdmission(admission);
    }

    public void deleteAdmission(int id, int patientId) {
        Patient patient = getPatient(patientId);
        if (patient != null && patient.getFirstAdmissionId() == id) {
            int nextAdId = dbms.getAdmission(id).getNextAdId();
            dbms.deleteAdmission(id, -1);
            patient.setAdmissionId(nextAdId);

            return;
        }

        Admission[] patientAdmissions = dbms.getAllAdmissionsFromFirst(patient.getFirstAdmissionId());
        for (Admission admission : patientAdmissions) {
            if (admission.getNextAdId() == id) {
                dbms.deleteAdmission(id, admission.getId());
                return;
            }
        }

        System.out.println("Удаляемый ID не существует или не принадлежит пациенту");
    }

    public void clearDb() {
        dbms.clearDb();
    }

    public void deletePatientWithAdmissions(int id) {
        Patient patient = getPatient(id);
        if (patient == null) {
            return;
        }

        dbms.deletePatientWithAdmissions(patient);
    }

    public Patient getPatient(int id) {
        Patient patient = dbms.getPatient(id);
        if (patient == null) {
            System.out.println("Patient not found");
        }

        return patient;
    }
}
