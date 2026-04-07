package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class DashboardController implements Initializable {

    @FXML private Label totalBooksLabel;
    @FXML private Label totalMembersLabel;
    @FXML private Label booksIssuedLabel;

    @FXML private Label PendingReturnsLabel;

    @FXML private PieChart categoryPieChart;
    @FXML private BarChart<String, Number> activityBarChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loaddata();


    }

    private void loaddata() {
        Database db = Database.getInstance();

        //refresh charts

        categoryPieChart.setData(db.getBookCategoryData());
        activityBarChart.getData().clear();

        XYChart.Series<String, Number> series = db.getTopBorrowersData();

        if (series != null) {
            activityBarChart.getData().add(series);
        }

        updateSummaryLabels();
    }

    private void updateSummaryLabels() {
        Database db = Database.getInstance();
        totalBooksLabel.setText(String.valueOf(db.getTotalBooksCount()));
        totalMembersLabel.setText(String.valueOf(db.getTotalMembersCount()));
        booksIssuedLabel.setText(String.valueOf(db.getTotalIssuedBooksCount()));
       PendingReturnsLabel.setText(String.valueOf(db.getPendingReturnsCount()));

    }



    @FXML
    void handleIssueBook(ActionEvent event){
        navigateTo(event, "issuebook.fxml", "Issue Book");
    }

    @FXML
    public void handleAcceptReturn(ActionEvent event) {
        navigateTo(event, "accept_return.fxml", "Accept Return Requests");
    }

    @FXML
    void memberspageshow(ActionEvent event){
        navigateTo(event, "memberspage.fxml", "Members");
    }

    @FXML
    public void bookspageshow(ActionEvent actionEvent) {
        navigateTo(actionEvent, "Books.fxml", "Available Books");
    }

    @FXML
    public void PendingRequests(ActionEvent actionEvent) {
        navigateTo(actionEvent, "pendingRequests.fxml", "Pending Requests");
    }

    @FXML
    public void PendingReturns(ActionEvent actionEvent) {
        navigateTo(actionEvent, "IssuedBooks.fxml", "Issued Books");
    }

    private void navigateTo(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading: " + fxmlFile);
        }
    }

    @FXML
    void handleMessages(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("admin_messages.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin - Messaging System");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Messaging page: " + e.getMessage());
        }
    }

    @FXML
    public void LogOut(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");
        Optional<ButtonType> confirm = alert.showAndWait();

        if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
            navigateTo(actionEvent, "login.fxml", "Bibliotech");
        }
    }
}