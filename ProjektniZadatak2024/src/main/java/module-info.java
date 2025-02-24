module org.unibl.etf.projektnizadatak2024 {
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
    requires jakarta.ws.rs;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.logging;
    requires com.google.gson;
    requires amqp.client;
    requires java.sql;

    opens org.unibl.etf.projektnizadatak2024 to javafx.fxml;
    exports org.unibl.etf.projektnizadatak2024;
    exports org.unibl.etf.projektnizadatak2024.controllers;
    opens org.unibl.etf.projektnizadatak2024.controllers to javafx.fxml;
    exports org.unibl.etf.projektnizadatak2024.model to com.fasterxml.jackson.databind, com.google.gson;
    opens org.unibl.etf.projektnizadatak2024.model to javafx.base, com.google.gson;


}