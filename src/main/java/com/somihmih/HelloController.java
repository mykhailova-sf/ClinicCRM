package com.somihmih;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.view.CalendarView;
import com.somihmih.entry.AdmissionEntry;

import javafx.event.EventHandler;

import java.time.LocalDate;

import com.somihmih.db.ClinicDbService;
import com.somihmih.db.NewDbService;
import com.somihmih.er.entity.Admission;
import com.somihmih.er.entity.Patient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HelloController {

    @FXML
    private Label patientId;

    @FXML
    private TextField name;

    @FXML
    private TextField byName;

    @FXML
    private TextField byPhone;

    @FXML
    private TextField phone;

    @FXML
    private ListView list;

    @FXML
    private TableView patientsTable;

    @FXML
    private TableView admissionsTable;

    @FXML
    private DatePicker calendar2;

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
        this.dbService = new NewDbService(list);
//        patientId.setText("");

        readPatientsFromDb();
        updatePatientsList();

        calendarEventHandler = event -> {
            System.out.println(" event ");
            CalendarEvent calendarEvent = (CalendarEvent) event;
            Entry<?> entry = calendarEvent.getEntry();
            if (calendarEvent.getEventType().equals(CalendarEvent.ENTRY_CALENDAR_CHANGED)) {
                System.out.println(" изменение/добавление/удаление записи ");

                if (calendarEvent.isEntryRemoved()) {
                    System.out.println("Удаление entry");
                    Admission admission = ((AdmissionEntry) entry).getAdmission();
                    int id = admission.getId();
                    dbService.deleteAdmission(id, admission.getPatientId());
                } else {
                    Patient patient = (Patient) patientsTable.getSelectionModel().getSelectedItem();
                    if (patient == null) {
                        System.out.println("Пациент не выбран");
                        return;
                    }
                    Admission admission;
                    try {
                        admission = getAdmissionFrom(entry, patient);
                        if (calendarEvent.isEntryAdded() && !(entry instanceof AdmissionEntry)) {
                            System.out.println(" -- Добавление");
                            dbService.insertAdmission(patient.getId(), admission);
                            updateAdmissionsList();

                        } else if (!calendarEvent.isEntryAdded() && entry instanceof AdmissionEntry) {
                            System.out.println(" -- Обновление");
                            dbService.updateAdmission(admission);
                            updateAdmissionsList();
                        } else {
                            System.out.println(" --  ХЗ что делать");
                        }
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

    private Admission getAdmissionFrom(Entry<?> entry, Patient patient) {
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
            return new Admission(id, formattedDateTime, patient.getId());
        }

        return new Admission(formattedDateTime, patient.getId());
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
        for (Patient patient : patients) {
            String lowerCaseName = patient.getName().toLowerCase();
            String lowerCasePhone = patient.getPhoneNumber().toLowerCase();
            boolean isValidByName = maskName.isEmpty() || lowerCaseName.contains(maskName.toLowerCase());
            boolean isValidByPhone = maskPhone.isEmpty() || lowerCasePhone.contains(maskPhone.toLowerCase());
            if (isValidByName && isValidByPhone) {
                patientList.add(patient);
                System.out.println("fillPatientsList: " + patient);
            }
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

        for (Admission admission : admissions) { // переделать на addAll
            admissionsList.add(admission);
            try {
                AdmissionEntry admissionEntry = new AdmissionEntry(admission);
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
    protected void deleteAdmission() {
        Patient patient = (Patient) patientsTable.getSelectionModel().getSelectedItem();
        if (patient == null) {
            System.out.println("Выбери пациента для удаления его записи!");
            return;
        }

        Admission admission = (Admission) admissionsTable.getSelectionModel().getSelectedItem();
        if (admission == null) {
            System.out.println("Выбери Admission для удаления!");
            return;
        }

        dbService.deleteAdmission(admission.getId(), patient.getId());
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

    @FXML
    private void handleDateSelection() {
        // Получаем выбранную дату из DatePicker
        String selectedDate = calendar2.getValue().toString();

        // Выполняем необходимые действия с выбранной датой
        System.out.println("Выбрана новая дата: " + selectedDate);

        // TODO показать админы за дату
    }
}