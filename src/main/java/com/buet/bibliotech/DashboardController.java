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

public class DashboardController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML private Label totalBooksLabel;
    @FXML private Label totalMembersLabel;
    @FXML private Label booksIssuedLabel;
    @FXML private Label booksDueLabel;
  @FXML
    void handleIssueBook(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("issuebook.fxml"));
            Parent root=loader.load();
            Stage stage=(Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Issue Book");



        }
        catch(IOException e){
            e.printStackTrace();
            System.err.println("Error loading issuebook page");
        }
    }


    @FXML
    void memberspageshow(ActionEvent event){
      try{
          FXMLLoader loader=new FXMLLoader(getClass().getResource("memberspage.fxml"));
          Parent root=loader.load();

          Stage stage= (Stage) ((Node) event.getSource()).getScene().getWindow();
          stage.setScene(new Scene(root));
          stage.setTitle("Members");
                        }
      catch(IOException e){
          e.printStackTrace();
          System.err.println("Error loading Members page");

      }
    }


    public void bookspageshow(ActionEvent actionEvent) {
      try{
          FXMLLoader loader=new FXMLLoader(getClass().getResource("Books.fxml"));
          Parent root=loader.load();

          Stage stage=(Stage)((Node) actionEvent.getSource()).getScene().getWindow();
          stage.setScene(new Scene(root));
          stage.setTitle("Available Books");
      }
      catch(IOException e){
          e.printStackTrace();
          System.err.println("Error loading Members page");
        }
    }

    private void updateSummaryLabels() {
        Database db = Database.getInstance();


        int totalBooks = db.getTotalBooksCount();
        int totalMembers = db.getTotalMembersCount();
        int totalIssued = db.getTotalIssuedBooksCount();
        int pendingReq = db.getPendingRequestsCount();


        totalBooksLabel.setText(String.valueOf(totalBooks));
        totalMembersLabel.setText(String.valueOf(totalMembers));
        booksIssuedLabel.setText(String.valueOf(totalIssued));
        booksDueLabel.setText(String.valueOf(pendingReq));

    }

    @FXML
    private TableView<IssueInfo> pendingReturnsTable;
    @FXML
    private TableColumn<IssueInfo, String> colBookId;
    @FXML
    private TableColumn<IssueInfo, String> colBookName;
    @FXML
    private TableColumn<IssueInfo, String> colMemberId;
    @FXML
    private TableColumn<IssueInfo, String> colMemberName;
    @FXML
    private TableColumn<IssueInfo, String> colIssueDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueTime"));

        loaddata();

    }

    private void loaddata() {
        Database db=Database.getInstance();
        ObservableList<IssueInfo> list= FXCollections.observableArrayList();
        list=db.getPendingReturns();

        pendingReturnsTable.setItems(list);
        updateSummaryLabels(); //table.setItems(list);
    }

    public void LogOut(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> confirm= alert.showAndWait();

        if (confirm.isPresent() && confirm.get()== ButtonType.OK)  {
            try {
                FXMLLoader loader=new FXMLLoader(getClass().getResource("login.fxml"));
                Parent root=loader.load();

                Stage stage=(Stage)((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setTitle("Bibliotech");
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void PendingRequests(ActionEvent actionEvent) {
        try{
            FXMLLoader loader=new FXMLLoader(getClass().getResource("pendingRequests.fxml"));
            Parent root=loader.load();

            Stage stage=(Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Available Books");
        }
        catch(IOException e){
            e.printStackTrace();
            System.err.println("Error loading Members page");
        }
    }
}
