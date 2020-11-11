/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxml_json_readtotable;

import static fxml_json_readtotable.FXML_JSON_ReadToTable.Persons;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author nando
 */
public class FXMLDocumentController implements Initializable {
    
    
    private Label label;
    @FXML
    private TableView<Person> table;
    @FXML
    private TableColumn<Person, String> Name;
    @FXML
    private TableColumn<Person, String> Phone_adress;
    @FXML
    private TableColumn<Person, String> Comment;
    @FXML
    private Button ChangeSceneButton;
    @FXML
    private Button DeleteButton;
    @FXML
    private Button DetailedButton;
    @FXML
    private Button SaveButton;
    
    private static List<Person> People;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        Name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        Phone_adress.setCellValueFactory(new PropertyValueFactory<>("Phone_adress"));
        Comment.setCellValueFactory(new PropertyValueFactory<>("Comment"));
        table.setEditable(true);
        
        //Makes the given column editable
        Name.setCellFactory(TextFieldTableCell.forTableColumn()); 
        Name.setOnEditCommit((CellEditEvent<Person, String> t) ->{
        ((Person)t.getTableView().getItems().get(
                t.getTablePosition().getRow())
                ).setName(t.getNewValue());
        });
        
        FXML_JSON_ReadToTable.getPersons().forEach((p) ->{
            table.getItems().add(p);
        });
        
        
        DeleteButton.setOnAction(e -> {
            Person selectedItem=table.getSelectionModel().getSelectedItem();
            table.getItems().remove(selectedItem);
            update();
        });
    }    

    //Changes scene to InputScene.fxml
    @FXML
    private void HandleChangeScene(ActionEvent event) {
        try{
            Parent newPersonParent=FXMLLoader.load(getClass().getResource("InputScene.fxml"));
            Scene newPersonScene=new Scene(newPersonParent);
            Stage window=(Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(newPersonScene);
            window.show();
        }
        catch(IOException e){
            System.out.println(e.toString());
        }
        
    }

    @FXML
    private void HandleDeleteRow(ActionEvent event) {
    }

    //Opens Detailed window, gives data of selected row to the other Scene
    @FXML
    private void HandleChangeScene2(ActionEvent event) {
        Person selectedItem=table.getSelectionModel().getSelectedItem();
         try{
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("DetailedScene.fxml"));
            Parent TableViewParent=loader.load();
            Scene newPersonScene=new Scene(TableViewParent);
            
            DetailedSceneController controller= loader.getController();
            controller.initData(selectedItem);
                    
            Stage window=(Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(newPersonScene);
            window.show();
        }
        catch(IOException e){
            System.out.println(e.toString());
        }
    }
    private void update(){
        JSONArray array=new JSONArray();
        List<Person> persons2=new ArrayList<>();
        table.getItems().forEach((p) -> {
            persons2.add(p);
        });
        FXML_JSON_ReadToTable.Persons=persons2;
        FXML_JSON_ReadToTable.Persons.forEach((p) -> {
            JSONObject obj1= new JSONObject();
            obj1.put("Name", p.getName());
            obj1.put("Phone_adress", p.getPhone_adress());
            obj1.put("Comment", p.getComment());

            JSONObject o1= new JSONObject();
            o1.put("Person", obj1);

            array.add(o1);
        });
        
        
        try(FileWriter file=new FileWriter("OutputTemporary.json")){
             file.write(array.toJSONString());
             file.flush();
        }
        
        catch(IOException e){
            e.printStackTrace();
        }
        
    }

    @FXML
    private void HandleSaveData(ActionEvent event) {
        People=new ArrayList<>();
        JSONArray array=new JSONArray();
        JSONParser parser=new JSONParser();
        try{
            Object obj= parser.parse(new FileReader("OutputTemporary.json"));
            JSONArray jsonArray=(JSONArray)obj;
            jsonArray.forEach(p ->parsePersonObj((JSONObject)p));            
        }
        catch(FileNotFoundException e){e.printStackTrace();}
        catch(ParseException e){e.printStackTrace();}
        catch(IOException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}
        
        Persons.forEach((p) -> {
            JSONObject obj1= new JSONObject();
            obj1.put("Name", p.getName());
            obj1.put("Phone_adress", p.getPhone_adress());
            obj1.put("Comment", p.getComment());

            JSONObject o1= new JSONObject();
            o1.put("Person", obj1);

            array.add(o1);
        });
        
        
        try(FileWriter file=new FileWriter("Output.json")){
             file.write(array.toJSONString());
             file.flush();
        }
        
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void parsePersonObj(JSONObject p){
        JSONObject userObj = (JSONObject)p.get("Person");
        Person p1=new Person();
        p1.setName((String)userObj.get("Name"));
        p1.setPhone_adress((String)userObj.get("Phone_adress"));
        p1.setComment((String)userObj.get("Comment"));
        People.add(p1);
    }
}
