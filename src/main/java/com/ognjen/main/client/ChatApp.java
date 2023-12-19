package com.ognjen.main.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Main app used to chat.

public class ChatApp extends Application {
    private static final long serialVersionUID = 1L;

    private Socket socket;
    private PrintWriter out;
    private ObjectInputStream input;
    private String username;
    private boolean admin;
    private String hostname;
    private Integer port;
    private Stage stage;
    private TextField messageTextField;
    private VBox vbox;
    private TextArea chatArea;
    private boolean authorizationAppOpened = false;

    public ChatApp(String u, String h, Integer p) throws IOException {
        hostname = h;
        port = p;
        username = u;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Chat Client");
        stage = primaryStage;

        messageTextField = new TextField();
        chatArea = new TextArea();
        chatArea.setEditable(false);
        // Consume the event to prevent further processing
        Button sendButton = new Button("Send");

        // Handle send button click event
        sendButton.setOnAction(event -> {
            sendMessage();
        });

        vbox = new VBox(10);
        vbox.getChildren().addAll(chatArea, messageTextField, sendButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> closeApp());
        primaryStage.show();

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket(hostname, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            Scanner in = new Scanner(socket.getInputStream());
            out.println("isAdmin " + username);
            while (!in.hasNextLine()) {
                Thread.sleep(1000);
            }
            admin = Boolean.parseBoolean(in.nextLine());
            out.println("getMessages");
            new Thread(() -> {
                while (in.hasNextLine()) {
                    String nextLine = in.nextLine();
                    if (nextLine.startsWith("message")) {
                        String[] split = nextLine.split(" ");
                        StringBuilder msg = new StringBuilder();
                        for (var i = 1; i < split.length; i++) {
                            msg.append(split[i]);
                            msg.append(" ");
                        }
                        Platform.runLater(() -> addMessage(msg.toString()));
                    }
                    in.reset();
                }
            }).start();
            if (admin) {
                addAdminOptions();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void addMessage(String message) {
        chatArea.appendText(message + '\n');
    }
    private void sendMessage() {
        if (messageTextField.getText().length() > 3) {
            String message = username + ": " + messageTextField.getText();
            out.println("message " + message);
            out.flush();
            messageTextField.clear();
        }
    }

    private void addAdminOptions() {
        Label admin = new Label("Admin Korisnik");
        vbox.getChildren().add(admin);
    }

    private void closeApp() {
        try {
            socket.close();
            if (input != null) {
                input.close();
            }
            if (out != null) {
                out.close();
            }
            openAuthorizationApp();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void openAuthorizationApp() throws IOException {
        if (!authorizationAppOpened) {
            AuthorizeApp app = new AuthorizeApp();
            authorizationAppOpened = true;

            Platform.runLater(() -> {
                Stage authorizationAppStage = new Stage();
                stage.close();
                app.start(authorizationAppStage);
            });
        }
    }
}
