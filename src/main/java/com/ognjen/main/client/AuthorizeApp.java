package com.ognjen.main.client;

import com.ognjen.main.server.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

// App for login / registering the player and choosing a chatroom (Server) to chat in.

public class AuthorizeApp extends Application {
    private Server currentSelectedServer;
    private String serverHubHost = "localhost";
    private Integer serverHubPort = 5555;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login/Register App");

        // Create GridPane for layout
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Create tabs
        createServerInfoArea(grid);
        TabPane tabPane = new TabPane();
        tabPane.prefWidthProperty().bind(primaryStage.widthProperty());

        // Add Login and Register tabs
        createLoginTab(tabPane);
        createRegisterTab(tabPane);

        // Add TabPane to the grid
        grid.add(tabPane, 3, 0);

        // Create scene and set stage
        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);

        // Set stage to full-screen
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    private void createServerInfoArea(GridPane grid) {
        try {
            Socket socket = new Socket(serverHubHost, serverHubPort);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            output.writeObject("getServers");
            Object receivedObject = input.readObject();

            ArrayList<Server> servers = (ArrayList<Server>) receivedObject;
            ObservableList<Server> items = FXCollections.observableArrayList(servers);
            ComboBox<Server> comboBox = new ComboBox<>(items);
            Label resultLabel = new Label("Selected Server: None");

            // Add an event handler to the ComboBox
            comboBox.setOnAction(event -> {
                String selectedItem = String.valueOf(comboBox.getSelectionModel().getSelectedItem());
                currentSelectedServer = comboBox.getSelectionModel().getSelectedItem();
                resultLabel.setText("Selected item: " + selectedItem);
            });

            VBox vbox = new VBox(10);
            vbox.getChildren().addAll(comboBox, resultLabel);
            vbox.setMinSize(600, 200);
            grid.add(vbox, 0, 0);
            closeConnection(socket, output, input);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void createLoginTab(TabPane tabPane) {
        Tab loginTab = new Tab("Login");

        VBox loginVBox = new VBox(10);
        TextField usernameLogin = new TextField();
        PasswordField passwordLogin = new PasswordField();
        Button loginButton = new Button("Login");
        Label info = new Label();

        // Handle Login button click event
        loginButton.setOnAction(event -> {
            String username = usernameLogin.getText();
            String password = passwordLogin.getText();

            if (currentSelectedServer != null) {
                try {
                    Socket socket = new Socket(serverHubHost, serverHubPort);
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                    DataContainer dataContainer = new DataContainer("login", username, password, currentSelectedServer);
                    output.writeObject(dataContainer);
                    String s = (String) input.readObject();
                    if ("success_login".equals(s)) {
                        openChatApp((Stage) tabPane.getScene().getWindow(), username, currentSelectedServer.getHost(), currentSelectedServer.getPort());
                    } else if ("failed_login".equals(s)) {
                        Platform.runLater(() -> info.setText("Failed Login (wrong password maybe), try again!"));
                    }
                    closeConnection(socket, output, input);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        loginVBox.getChildren().addAll(new Label("Username:"), usernameLogin, new Label("Password:"), passwordLogin, loginButton, info);
        loginTab.setContent(loginVBox);

        tabPane.getTabs().add(loginTab);
    }

    private void createRegisterTab(TabPane tabPane) {
        Tab registerTab = new Tab("Register");

        VBox registerVBox = new VBox(10);
        TextField usernameRegister = new TextField();
        PasswordField passwordRegister = new PasswordField();
        PasswordField confirmPasswordRegister = new PasswordField();
        Button registerButton = new Button("Register");
        Label info = new Label();

        // Handle Register button click event
        registerButton.setOnAction(event -> {
            String username = usernameRegister.getText();
            String password = passwordRegister.getText();
            String confirmPassword = confirmPasswordRegister.getText();
            if (currentSelectedServer != null) {
                try {
                    if (password.equals(confirmPassword)) {
                        Socket socket = new Socket(serverHubHost, serverHubPort);
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                        DataContainer dataContainer = new DataContainer("register", username, password, currentSelectedServer);
                        output.writeObject(dataContainer);
                        String s = (String) input.readObject();
                        if ("success_register".equals(s)) {
                            info.setText("Registration Successful!");
                        } else if ("failed_register".equals(s)) {
                            info.setText("Failed register (account already registered), try again!");
                        }
                        closeConnection(socket, output, input);
                    }else {
                        info.setText("Failed register (password mismatch), try again!");
                    }
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        registerVBox.getChildren().addAll(new Label("Username:"), usernameRegister, new Label("Password:"), passwordRegister,
                new Label("Confirm Password:"), confirmPasswordRegister, registerButton, info);
        registerTab.setContent(registerVBox);

        tabPane.getTabs().add(registerTab);
    }
    private void openChatApp(Stage stage, String username, String hostname, Integer port) throws IOException {
        Platform.runLater(() -> {
            // Close the current stage
            stage.close();
            // Open the ChatApp
            ChatApp chatApp = null;
            try {
                chatApp = new ChatApp(username, hostname, port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage chatAppStage = new Stage();
            try {
                chatApp.start(chatAppStage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void closeConnection(Socket socket, ObjectOutputStream output, ObjectInputStream input) {
        try {
            if (output != null) {
                output.writeObject("terminate");
                output.close();
            }
            if (input != null) {
                input.close();
            }
            if (socket != null) {
                socket.close(); // Close the client socket with the ServerHub
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
