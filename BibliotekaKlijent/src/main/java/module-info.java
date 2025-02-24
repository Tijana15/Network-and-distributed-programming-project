module org.unibl.etf.bibliotekaklijent {
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
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.logging;
    requires com.google.gson;

    opens org.unibl.etf.bibliotekaklijent to javafx.fxml, com.google.gson;
    exports org.unibl.etf.bibliotekaklijent;
    exports org.unibl.etf.bibliotekaklijent.controller;
    opens org.unibl.etf.bibliotekaklijent.controller to javafx.fxml, com.google.gson;
    opens org.unibl.etf.bibliotekaklijent.model to javafx.base, com.google.gson;
    exports org.unibl.etf.bibliotekaklijent.model to com.fasterxml.jackson.databind, com.google.gson;
    exports org.unibl.etf.bibliotekaklijent.util;
    opens org.unibl.etf.bibliotekaklijent.util to com.google.gson, javafx.fxml;

}