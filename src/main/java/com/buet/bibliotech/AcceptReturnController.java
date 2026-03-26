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
        // কলামগুলোর সাথে মডেলে থাকা ভ্যারিয়েবলগুলোর নাম মিল আছে কি না দেখুন
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberID"));

        // এই লাইনটি খুবই গুরুত্বপূর্ণ - এটি টেবিল লোড করে
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
                    // সবুজ টিক বাটন (Accept)
                    Button btnApprove = new Button("✓");
                    btnApprove.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-cursor: hand;");
                    btnApprove.setOnAction(e -> approveReturn(getTableView().getItems().get(getIndex())));

                    // লাল ক্রস বাটন (Reject)
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
        System.out.println("Return Rejected for: " + item.getBookName());
        loadPendingRequests(); // টেবিল রিফ্রেশ
    }
    private void approveReturn(IssueInfo item) {
        Database db = Database.getInstance();

        // এখানে item.getBookId() এর পরিবর্তে item.getBookID() লিখুন (ID বড় হাতের)
        boolean success = db.approveReturn(item.getBookID());

        if(success) {
            System.out.println("Return Approved for: " + item.getBookName());
            // ২. টেবিল রিফ্রেশ করা
            loadPendingRequests();
        }
    }
    @FXML
    public void handleBack(ActionEvent event) {
        try {
            // ফাইলের নাম admindashboard.fxml থেকে পরিবর্তন করে Dashboardview.fxml দেওয়া হলো
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