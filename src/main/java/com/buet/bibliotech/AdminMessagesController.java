package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import com.buet.bibliotech.MessageModel;
import com.buet.bibliotech.member;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminMessagesController implements Initializable {


    @FXML private TableView<member> memberListView;
    @FXML private TableColumn<member, String> colMemberName;

    @FXML private Label chatTitleLabel;
    @FXML private VBox chatDisplayBox;
    @FXML private TextField messageInputField;

    private String selectedMemberID = null;
    private final String adminID = "admin";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colMemberName.setCellValueFactory(new PropertyValueFactory<>("name"));

        loadMemberList();

        // Updated selection listener for TableView
        memberListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedMemberID = newValue.getId();
                chatTitleLabel.setText("Chatting with: " + newValue.getName());
                loadChatLogs();
            }
        });
    }

    private void loadMemberList() {
        Database db = Database.getInstance();
        ObservableList<member> members = db.getMembers();

        if (members != null) {
            memberListView.setItems(members);
        }
    }

    private void loadChatLogs() {
        if (selectedMemberID == null) return;

        chatDisplayBox.getChildren().clear();
        Database db = Database.getInstance();
        ObservableList<MessageModel> messages = db.getChatBetween(adminID, selectedMemberID);

        if (messages != null) {
            for (MessageModel msg : messages) {
                Label messageLabel = new Label(msg.getMessageText());
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(300);
                messageLabel.setPadding(new Insets(8, 12, 8, 12));

                HBox messageContainer = new HBox();
                if (msg.getSenderID().equals(adminID)) {
                    messageLabel.setStyle("-fx-background-color: #27ae60; -fx-background-radius: 10; -fx-text-fill: white; -fx-font-size: 14px;");
                    messageContainer.setAlignment(Pos.CENTER_RIGHT);
                } else {
                    messageLabel.setStyle("-fx-background-color: #223149; -fx-background-radius: 10; -fx-text-fill: white; -fx-font-size: 14px;");
                    messageContainer.setAlignment(Pos.CENTER_LEFT);
                }

                messageContainer.getChildren().add(messageLabel);
                VBox.setMargin(messageContainer, new Insets(5, 0, 5, 0));
                chatDisplayBox.getChildren().add(messageContainer);
            }
        }
    }

    @FXML
    void handleSendMessage(ActionEvent event) {
        String text = messageInputField.getText().trim();
        if (text.isEmpty() || selectedMemberID == null) return;

        Database db = Database.getInstance();
        boolean success = db.sendMessage(adminID, selectedMemberID, text);

        if (success) {
            messageInputField.clear();
            loadChatLogs();
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Dashboardview.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}