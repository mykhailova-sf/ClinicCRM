package com.somihmih.db;

import com.somihmih.er.entity.Admission;
import com.somihmih.er.entity.Patient;
import com.somihmih.er.indexservice.Index;
import com.somihmih.er.entity.Entity;
import javafx.collections.ObservableList;

import java.util.Arrays;

public class ClinicDbService {

    protected final DBMS dbms = new DBMS();

    public void insertPatient(String patientInfo) {
        String[] patientField = patientInfo.split(",");

        String name = patientField[0];
        String phoneNumber = patientField[1];

        insertPatient(new Patient(name, phoneNumber));
    }

    public void insertPatient(String name, String phone) {
        insertPatient(new Patient(name, phone));
    }

    private void insertPatient(Patient patient) {
        System.out.println("Я буду сохранять пациента " + patient.getName());
        dbms.insertPatient(patient);
        System.out.println("ОК - Пациент сохранен");
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

    public Admission[] getActivePatientAdmissions(Patient patient) {
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

    public void showPatientIndexes() {
        System.out.println("Индексы пациентов");
        Index[] indexes = dbms.getPatientIndices();
        showIndices(indexes);
    }

    public void showAddmissionIndexes() {
        System.out.println("Индексы записей");
        showIndices(dbms.getAddmissionIndices());
    }

    private static void showIndices(Index[] indices) {
        for (Index index : indices) {
            System.out.println(index);
        }
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

    public void readAllAdmissions() {
        for(Entity entity : dbms.readAllAdmissions()) {
            System.out.println(entity.toString());
        }
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
            // Удаляемый адмишн действительно принадлежит этому пациенту
            int nextAdId = dbms.getAdmission(id).getNextAdId();
            dbms.deleteAdmission(id, -1);
            patient.setAdmissionId(nextAdId);

            return;
        }

        Admission[] patientAdmissions = dbms.getAllAdmissionsFromFirst(patient.getFirstAdmissionId());
        for (Admission admission : patientAdmissions) {
            if (admission.getNextAdId() == id) {
                // Удаляемый адмишн действительно принадлежит этому пациенту
                dbms.deleteAdmission(id, admission.getId());
                return;
            }
        }

        System.out.println("Удаляемый ID не существует или не принадлежит пациенту");
    }

    public void clearDb() {
        dbms.clearDb();
    }

    public void showPatientWithAdmission(int id) {
        Patient patient = getPatient(id);
        if (patient == null) {
            return;
        }

        System.out.println("Patient " + patient.getName() + ", tel:" + patient.getPhoneNumber());

        Admission[] admissions = dbms.getAllAdmissionsFromFirst(patient.getFirstAdmissionId());

        if (admissions == null) {
            System.out.println("Admissions empty");
            return;
        }

        for (Admission admission : admissions) {
            System.out.println(admission.toString());
        }
    }

    public void deletePatientWithAdmissions(int id) {
        Patient patient = getPatient(id);
        if (patient == null) {
            return;
        }

        dbms.deletePatientWithAdmissions(patient);
    }

    private Patient getPatient(int id) {
        Patient patient = dbms.getPatient(id);
        if (patient == null) {
            System.out.println("Patient not found");
        }

        return patient;
    }
}
