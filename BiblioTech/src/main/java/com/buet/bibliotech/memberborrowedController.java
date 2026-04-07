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


    public void initialize(URL location, ResourceBundle resources) {
        // 1. Link table columns to our Model variables
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));

        // 2. Load the data
        loadBorrowedBooks();
    }

    private void loadBorrowedBooks() {
        // Use the ID saved during login in ProfileController
        String memberId = ProfileController.currentMemberId;

        if (memberId != null) {
            Database db = Database.getInstance();
            ObservableList<String> names = db.getBorrowedBooksByMember(memberId);
            ObservableList<BorrowedBookModel> tableData = FXCollections.observableArrayList();

            // 3. Loop through names and add a Serial number (1, 2, 3...)
            for (int i = 0; i < names.size(); i++) {
                String serialNum = String.valueOf(i + 1);
                String bookTitle = names.get(i);

                tableData.add(new BorrowedBookModel(serialNum, bookTitle));
            }

            borrowedBooksTable.setItems(tableData);
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

}
