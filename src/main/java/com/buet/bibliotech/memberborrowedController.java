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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class memberborrowedController implements Initializable {

    @FXML
    private TableView<BorrowedBookModel> borrowedBooksTable;
    @FXML
    private TableColumn<BorrowedBookModel, String> colSerial;
    @FXML
    private TableColumn<BorrowedBookModel, String> colBookName;
    @FXML
    private TableColumn<BorrowedBookModel, String> colBookId;

    @FXML
    private TableColumn<BorrowedBookModel, String> colAuthor;


    public void initialize(URL location, ResourceBundle resources) {

        colSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));


        loadBorrowedBooks();
    }

    private void loadBorrowedBooks() {

        String memberId = ProfileController.currentMemberId;
        //saved in profilecontroller during login

        if (memberId != null) {
            Database db = Database.getInstance();

            ObservableList<BorrowedBookModel> data = db.getBorrowedBooksByMember(memberId);
            borrowedBooksTable.setItems(data);

        }
    }

    public void handleBack(ActionEvent actionEvent) {
        try{
            FXMLLoader loader=new FXMLLoader(getClass().getResource("memberdashboardview.fxml"));
            Parent root=loader.load();

            Stage stage=(Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Member Dashboard");
        }
        catch(IOException e){
            e.printStackTrace();
            System.err.println("Error loading Dashboard");
        }
    }

    public void handleReturnBook(ActionEvent actionEvent) {
    }
}
