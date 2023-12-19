module com.ognjen.main.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens com.ognjen.main.client to javafx.fxml;
    exports com.ognjen.main.client;
}