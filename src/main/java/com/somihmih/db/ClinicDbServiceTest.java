package com.somihmih.db;

import com.somihmih.er.entity.Admission;
import com.somihmih.er.entity.Patient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ClinicDbServiceTest {

    @Test
    void insertAdmission_whenPatientNotExists() {
        DBMS dbms = Mockito.mock(DBMS.class);

        Mockito.when(dbms.getPatient(666))
               .thenReturn(null);

        ClinicDbService dbService = new ClinicDbService(dbms);
        dbService.insertAdmission(666, null);

        Mockito.verify(dbms, Mockito.times(0)).insertAdmission(Mockito.any());
    }

    @Test
    void insertAdmission_WhenItIsFirst() {
        DBMS dbms = Mockito.mock(DBMS.class);

        Patient patient = new Patient("John", "+3666");
        Mockito.when(dbms.getPatient(666))
               .thenReturn(patient);
        Mockito.when(dbms.getAllAdmissionsFromFirst(Mockito.anyInt()))
               .thenReturn(null);

        ClinicDbService dbService = new ClinicDbService(dbms);
        dbService.insertAdmission(666, new Admission());

        Mockito.verify(dbms, Mockito.times(1)).savePatient(patient);
    }

    @Test
    void insertAdmission() {
        DBMS dbms = Mockito.mock(DBMS.class);

        Patient patient = new Patient("John", "+3666");
        int patientId = 666;
        Mockito.when(dbms.getPatient(patientId))
               .thenReturn(patient);

        Admission lastAdmission = new Admission();
        Mockito.when(dbms.getAllAdmissionsFromFirst(Mockito.anyInt()))
               .thenReturn(new Admission[]{lastAdmission});

        ClinicDbService dbService = new ClinicDbService(dbms);
        Admission newAdmission = new Admission();
        dbService.insertAdmission(patientId, newAdmission);

        Mockito.verify(dbms, Mockito.times(1)).insertAdmission(newAdmission);
        Mockito.verify(dbms, Mockito.times(1)).saveAdmission(lastAdmission);
    }
}