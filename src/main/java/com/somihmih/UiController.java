package com.somihmih;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.view.CalendarView;
import com.somihmih.db.DBMS;
import com.somihmih.entry.AdmissionEntry;

import com.somihmih.er.utils.TitleBuilder;
import javafx.event.EventHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.somihmih.db.ClinicDbService;
import com.somihmih.er.entity.Admission;
import com.somihmih.er.entity.Patient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UiController {

    @FXML
    private TextField name;

    @FXML
    private TextField byName;

    @FXML
    private TextField byPhone;

    @FXML
    private TextField phone;

    @FXML
    private TableView patientsTable;

    @FXML
    private CalendarView calendarView;

    Patient[] patients;

    Admission[] admissions;

    CalendarSource calendarSource = new CalendarSource("Источник календаря");;
    EventHandler calendarEventHandler;

    ObservableList<Patient> patientList = FXCollections.observableArrayList();
    ObservableList<Admission> admissionsList = FXCollections.observableArrayList();

    private ClinicDbService dbService;
    @FXML
    public void initialize() {
        this.dbService = new ClinicDbService(DBMS.getInstance());
        readPatientsFromDb();
        updatePatientsList();

        calendarEventHandler = event -> {
            CalendarEvent calendarEvent = (CalendarEvent) event;
            Entry<?> entry = calendarEvent.getEntry();
            if (calendarEvent.getEventType().equals(CalendarEvent.ENTRY_INTERVAL_CHANGED)
                    && (entry instanceof AdmissionEntry admissionEntry)
            ) {
                Admission admission = getAdmissionFrom(entry, admissionEntry.getAdmission().getPatientId());
                dbService.updateAdmission(admission);
            }
            if (calendarEvent.getEventType().equals(CalendarEvent.ENTRY_TITLE_CHANGED)
                    && (entry instanceof AdmissionEntry admissionEntry)
                    && admissionEntry.getTitle().contains(";")
            ) {
                String[] titleParts = admissionEntry.getTitle().split(";");
                String[] oldTitleParts = calendarEvent.getOldText().split(";");
                System.out.println("oldTitleParts: " + oldTitleParts[0]);
                System.out.println("titleParts: " + titleParts[0]);
                if (titleParts[0].equals(oldTitleParts[0])) {
                    Admission admission = getAdmissionFrom(entry, admissionEntry.getAdmission().getPatientId());
                    admission.setDescription(
                            titleParts.length > 1 ? titleParts[1].trim() : ""
                    );
                    dbService.updateAdmission(admission);
                }

            }
            if (calendarEvent.getEventType().equals(CalendarEvent.ENTRY_CALENDAR_CHANGED)) {
                if (calendarEvent.isEntryRemoved() && entry instanceof AdmissionEntry admissionEntry) {
                    Admission admission = admissionEntry.getAdmission();
                    dbService.deleteAdmission(admission.getId(), admission.getPatientId());
                } else if (calendarEvent.isEntryAdded() && !(entry instanceof AdmissionEntry)) {
                    try {
                        Patient patient = (Patient) patientsTable.getSelectionModel().getSelectedItem();
                        if (patient == null) {
                            System.out.println("No selected patient");
                            return;
                        }

                        if (entry.getStartAsLocalDateTime().isBefore(LocalDateTime.now()) ) {
                            entry.setHidden(true);
                            entry.setTitle("Cannot add retroactively");
                            return;
                        }

                        dbService.insertAdmission(
                                patient.getId(),
                                getAdmissionFrom(entry, patient.getId())
                        );
                        updateAdmissionsList();
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        };
        updateAdmissionsList();

        TableSelectionModel<Patient> selectionModel = patientsTable.getSelectionModel();

        selectionModel.selectedItemProperty().addListener(
                (observable, oldPatient, selectedPatient) -> onSelectPatient(selectedPatient)
        );

        selectionModel.selectedItemProperty().addListener(
                (observable, oldPatient, selectedPatient) -> onSelectPatient(selectedPatient)
        );

        byName.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePatientsList();
        });

        byPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePatientsList();
        });
    }

    private Admission getAdmissionFrom(Entry<?> entry, int patientId) {
        LocalDate startDate = entry.getStartDate();
        int startDayOfMonth = startDate.getDayOfMonth();
        int endDayOfMonth = entry.getEndDate().getDayOfMonth();
        if (startDayOfMonth != endDayOfMonth) {
            System.out.println("начaло и конец - должна быть одной датой");
            updateAdmissionsList();

            throw new IllegalArgumentException("начaло и конец - должна быть одной датой");
        }
        String formattedDateTime = String.format("%02d:%02d:%04d %02d:%02d-%02d:%02d",
                startDayOfMonth,
                startDate.getMonth().getValue(),
                startDate.getYear(),
                entry.getStartTime().getHour(),
                entry.getStartTime().getMinute(),
                entry.getEndTime().getHour(),
                entry.getEndTime().getMinute()); // 21.04.2024 15:00-16:00

        System.out.println("Time: " + formattedDateTime);
        if (entry.getUserObject() instanceof Admission) {
            int id = ((Admission) entry.getUserObject()).getId();
            return new Admission(id, formattedDateTime, patientId);
        }

        return new Admission(formattedDateTime, patientId);
    }

    private void onSelectPatient(Patient selectedPatient) {
        if (selectedPatient != null) {
            System.out.println("Выбрана строка: " + selectedPatient);
            name.setText(selectedPatient.getName());
            phone.setText(selectedPatient.getPhoneNumber());

            updateAdmissionsList();
        }
    }

    @FXML
    protected void onShowAllPatients() {
        updatePatientsList();
    }

    @FXML
    protected void onShowAllAdmissions() {
        patientsTable.getSelectionModel().clearSelection();
        updateAdmissionsList();
    }

    private void updatePatientsList() {
        patientList.clear();
        String maskName = byName.getText();
        String maskPhone = byPhone.getText();

        // 7. Iterator pattern
        IteratorForFilteredPatients iterator = new IteratorForFilteredPatients(maskName, maskPhone, patients);

        while (iterator.hasNext()) {
            Patient patient = iterator.next();
            patientList.add(patient);
            System.out.println("fillPatientsList: " + patient);
        }
    }

    private void readPatientsFromDb() {
        patients = dbService.getAllActivePatients();
    }

    private void readAllAdmissionsFromDb() {
        admissions = dbService.getAllActiveAdmissions();
    }

    private void updateAdmissionsList() {
        Patient patient = (Patient) patientsTable.getSelectionModel().getSelectedItem();
        if (patient == null) {
            readAllAdmissionsFromDb();
        } else {
            admissions = dbService.getActivePatientAdmissions(patient.getId());
        }

        admissionsList.clear();

        Calendar calendar = new Calendar("Clinique 1");
        calendarSource.getCalendars().clear();
        calendarView.getCalendarSources().clear();
        calendarSource.getCalendars().add(calendar);
        calendarSource.getCalendars().get(0).addEventHandler(calendarEventHandler);

        calendarView.getCalendarSources().add(calendarSource);

        for (Admission admission : admissions) {
            admissionsList.add(admission); // нужен ли нам грид старой версии?
            try {
                Patient currentPatient = dbService.getPatient(admission.getPatientId());
                String title = new TitleBuilder(admission).setPatient(currentPatient).buildTitle();

                AdmissionEntry admissionEntry = new AdmissionEntry(admission, title.trim());
                admissionEntry.setCalendar(calendar);
            } catch (Exception e) {
                System.out.println("updateAdmissionsList error, admission: " + admission);
                System.out.println("error: " + e.getMessage());
            }
        }
    }

    public ObservableList<Patient> getPatientList() {
        return patientList;
    }

    public ObservableList<Admission> getAdmissionsList() {
        return admissionsList;
    }

    @FXML
    private void onExit() {
        System.out.println("exitApplication");
        dbService.saveIndices();
        Platform.exit();
    }

    @FXML
    private void onReindex() {
        System.out.println("reindexAll");
        dbService.reindexAll();
    }

    @FXML
    protected void insertNewPatient() {
        dbService.insertPatient(name.getText(), phone.getText());
        readPatientsFromDb();
        updatePatientsList();
    }
    @FXML
    protected void deletePatient() {
        Patient patient = (Patient) patientsTable.getSelectionModel().getSelectedItem();
        if (patient == null) {
            return;
        }

        dbService.deletePatientWithAdmissions(patient.getId());

        readPatientsFromDb();
        updatePatientsList();
        updateAdmissionsList();
    }

    @FXML
    protected void updatePatient() {
        Patient patient = (Patient) patientsTable.getSelectionModel().getSelectedItem();
        if (patient == null) {
            return;
        }

        patient.setName(name.getText());
        patient.setPhoneNumber(phone.getText());
        dbService.updatePatient(patient);
        readPatientsFromDb();
        updatePatientsList();
    }

    public void onClearDb() {
        dbService.clearDb();
        readPatientsFromDb();
        updatePatientsList();
        updateAdmissionsList();
    }
}