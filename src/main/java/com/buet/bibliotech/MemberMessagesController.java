package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MemberMessagesController implements Initializable {

    @FXML private VBox chatDisplayBox;
    @FXML private TextField messageInputField;

    private final String adminID = "admin";
    private String currentMemberID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        currentMemberID = ProfileController.currentMemberId;
        loadChatLogs();
    }

    private void loadChatLogs() {
        if (currentMemberID == null) return;

        chatDisplayBox.getChildren().clear();
        Database db = Database.getInstance();

        ObservableList<MessageModel> messages = db.getChatBetween(currentMemberID, adminID);

        for (MessageModel msg : messages) {
            Label messageLabel = new Label(msg.getMessageText());
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(400);
            messageLabel.setPadding(new Insets(8, 12, 8, 12));

            HBox messageContainer = new HBox();
            if (msg.getSenderID().equals(currentMemberID)) {

                messageLabel.setStyle("-fx-background-color: #3498db; -fx-background-radius: 10; -fx-text-fill: white;");
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
            } else {

                messageLabel.setStyle("-fx-background-color: #223149; -fx-background-radius: 10; -fx-text-fill: white;");
                messageContainer.setAlignment(Pos.CENTER_LEFT);
            }

            messageContainer.getChildren().add(messageLabel);
            VBox.setMargin(messageContainer, new Insets(5, 0, 5, 0));
            chatDisplayBox.getChildren().add(messageContainer);
        }
    }

    @FXML
    void handleSendMessage(ActionEvent event) {
        String text = messageInputField.getText().trim();
        if (text.isEmpty() || currentMemberID == null) return;

        Database db = Database.getInstance();
        boolean success = db.sendMessage(currentMemberID, adminID, text);

        if (success) {
            messageInputField.clear();
            loadChatLogs();
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("memberdashboardview.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Member Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}