package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PendingRequestssController implements Initializable
{
    @FXML private TableView<RequestInfo> PendingRequestsTable;
    @FXML private TableColumn<RequestInfo, String> colMemberName;
    @FXML private TableColumn<RequestInfo, String> colMemberId;
    @FXML private TableColumn<RequestInfo, String> colBookName;
    @FXML private TableColumn<RequestInfo, String> colBookId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        Platform.runLater(() -> {
            showAlert("Instruction", null, "Select a book from the list to Issue.");
        });

        loadData();
    }

    private void loadData() {
        PendingRequestsTable.setItems(Database.getInstance().getPendingRequests());
    }

    @FXML
    public void handleReturnBook(ActionEvent actionEvent) {
        RequestInfo selected = PendingRequestsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean success = Database.getInstance().approveRequest(selected.getBookID(), selected.getMemberID());
            if (success) {

                loadData();// Refresh table
                showAlert("Success", "Request Granted", "Issued book successfully.");

            }
            else {
                showAlert("Error", "Try Again", "You have already Issued the book.");
            }
            }
        }


    public void handleBack(ActionEvent actionEvent) {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboardview.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    }

