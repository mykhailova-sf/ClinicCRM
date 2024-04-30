module com.somihmih {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.calendarfx.view;

    opens com.somihmih to javafx.fxml;
    opens com.somihmih.er.entity to javafx.fxml;
    exports com.somihmih;
    exports com.somihmih.er.entity;
}