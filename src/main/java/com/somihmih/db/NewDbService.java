package com.somihmih.db;

import com.somihmih.er.entity.Patient;
import javafx.scene.control.ListView;

public class NewDbService extends ClinicDbService {

    private final ListView list;

    public NewDbService(ListView listView) {
        this.list = listView;
    }

    @Override
    public Patient[] getAllActivePatients() {
        int i = 0;
        Patient[] allActivePatients = super.getAllActivePatients();
        for(Patient patient : allActivePatients) {
            list.getItems().add(patient.toString());
            i++;
        }

        return allActivePatients;
    }

}
