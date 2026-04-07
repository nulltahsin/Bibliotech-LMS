module com.buet.bibliotech {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    requires org.xerial.sqlitejdbc;
    requires java.desktop;

    // VERY IMPORTANT: Open the main package to both fxml and base
    // javafx.base is what allows TableViews to read your data objects
    opens com.buet.bibliotech to javafx.fxml, javafx.base;

    // Open the db package just in case you use reflection there
    opens com.buet.bibliotech.db to javafx.fxml;

    exports com.buet.bibliotech;
    exports com.buet.bibliotech.db;


    requires jdk.jsobject;
}