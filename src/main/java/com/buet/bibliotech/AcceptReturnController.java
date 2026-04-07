package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AcceptReturnController {

    @FXML private TableView<IssueInfo> returnTable;
    @FXML private TableColumn<IssueInfo, String> colBookId;
    @FXML private TableColumn<IssueInfo, String> colBookName;
    @FXML private TableColumn<IssueInfo, String> colMemberId;
    @FXML private TableColumn<IssueInfo, Void> colAction;

    @FXML
    public void initialize() {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberID"));

        loadPendingRequests();
        setupActionColumn();
    }

    private void loadPendingRequests() {
        Database db = Database.getInstance();
        ObservableList<IssueInfo> requestedData = db.getReturnRequests();
        returnTable.setItems(requestedData);
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button btnApprove = new Button("✓");
                    btnApprove.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-cursor: hand;");
                    btnApprove.setOnAction(e -> approveReturn(getTableView().getItems().get(getIndex())));

                    Button btnReject = new Button("✗");
                    btnReject.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-cursor: hand;");
                    btnReject.setOnAction(e -> rejectReturn(getTableView().getItems().get(getIndex())));

                    HBox buttons = new HBox(5, btnApprove, btnReject);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void rejectReturn(IssueInfo item) {
        Database db = Database.getInstance();
        // Updated: Using issueID to uniquely identify the transaction
        boolean success = db.rejectReturn(item.getIssueID());
        if(success) {
            System.out.println("Return Rejected for: " + item.getBookName());
            loadPendingRequests();
        }
    }

    private void approveReturn(IssueInfo item) {
        Database db = Database.getInstance();
        // Updated: Using issueID to delete the record AND bookID to update stock
        boolean success = db.approveReturn(item.getIssueID(), item.getBookID());

        if(success) {
            System.out.println("Return Approved for: " + item.getBookName());
            loadPendingRequests();
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboardview.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}