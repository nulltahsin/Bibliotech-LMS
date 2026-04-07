package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class memberspageController implements Initializable {

    @FXML
    private TableView<member> membersTable;

    @FXML
    private TableColumn<member, String> idCol;
    @FXML
    private TableColumn<member, String> nameCol;
    @FXML
    private TableColumn<member, String> deptCol;
    @FXML
    private TableColumn<member, String> batchCol;


    @FXML
    private TableColumn<member, Void> actionCol;
   @FXML
   private TextField searchField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (membersTable != null) {

            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
            batchCol.setCellValueFactory(new PropertyValueFactory<>("batch"));


            setupActionColumn();


            setupSearchFilter();
        }
    }

    private void setupActionColumn() {
        Callback<TableColumn<member, Void>, TableCell<member, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<member, Void> call(final TableColumn<member, Void> param) {
                return new TableCell<>() {


                    private final Button deleteBtn = new Button();

                    {

                        try {

                            Image img = new Image(getClass().getResourceAsStream("delete.jpg"));
                            ImageView view = new ImageView(img);

                            view.setFitHeight(20);
                            view.setFitWidth(20);
                            view.setPreserveRatio(true);


                            deleteBtn.setGraphic(view);


                            deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");


                            deleteBtn.setOnAction(event -> {

                                member member = getTableView().getItems().get(getIndex());
                                handleDeleteAction(member);
                            });

                        } catch (Exception e) {
                            System.err.println("Error loading delete.png: " + e.getMessage());
                            deleteBtn.setText("Del"); // Fallback text
                        }
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else
                        {

                            setGraphic(deleteBtn);
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
            }
        };

        actionCol.setCellFactory(cellFactory);
    }


    private void setupSearchFilter() {
        Database db = Database.getInstance();
        ObservableList<member> masterdata = db.getMembers();

        FilteredList<member> filteredData = new FilteredList<>(masterdata, p -> true);


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(member -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (member.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (member.getId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return false;
            });

        });

        SortedList<member> sortedData=new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(membersTable.comparatorProperty());

        membersTable.setItems(sortedData);

    }


    private void handleDeleteAction(member memberToDelete) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Deleting Member: " + memberToDelete.getName());
        alert.setContentText("Are you sure? Any books issued to this member will be marked as available.");


        if (alert.showAndWait().get() == ButtonType.OK) {
            Database db = Database.getInstance();
            boolean success = db.deleteMember(memberToDelete.getId());

            if (success) {
               //refresh the page
                setupSearchFilter();

               //show success alert
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setContentText("Member deleted and books returned successfully.");
                successAlert.show();
            }
            else
            {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setContentText("Could not delete member.");
                errorAlert.show();
            }
        }
    }

    private void loadData() {
        Database db = Database.getInstance();
        ObservableList<member> list = db.getMembers();
        membersTable.setItems(list);
    }

    @FXML
    void handleBack(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboardview.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void addmember(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addmember.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Add New Members");
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}