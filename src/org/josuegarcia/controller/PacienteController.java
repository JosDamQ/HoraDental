package org.josuegarcia.controller;

import java.time.format.*;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.josuegarcia.bean.Paciente;
import org.josuegarcia.db.Conexion;
import org.josuegarcia.report.GenerarReporte;
import org.josuegarcia.system.Principal;


public class PacienteController implements Initializable {
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO}
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Paciente> listaPaciente;
    private DatePicker fNacimiento, fPV;
    @FXML private TextField txtCodigoPaciente;
    @FXML private TextField txtNombresPaciente;
    @FXML private TextField txtApellidosPaciente;
    @FXML private TextField txtSexo;
    @FXML private TextField txtDireccionPaciente;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private GridPane grpFechas;
    @FXML private TableView tblPacientes;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colNombresPaciente;
    @FXML private TableColumn colApellidosPaciente;
    @FXML private TableColumn colSexo;
    @FXML private TableColumn colFechaNacimiento;
    @FXML private TableColumn colDireccionPaciente;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colFechaPrimeraVisita;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fNacimiento = new DatePicker(Locale.ENGLISH);
        fNacimiento.setDateFormat(new SimpleDateFormat("yyy-MM-dd"));
        fNacimiento.getCalendarView().todayButtonTextProperty().set("Today");
        fNacimiento.getCalendarView().setShowWeeks(false);
        grpFechas.add(fNacimiento,3,1);
        fNacimiento.getStylesheets().add("/org/josuegarcia/resource/DatePicker.css");
        fPV = new DatePicker(Locale.ENGLISH);
        fPV.setDateFormat(new SimpleDateFormat("yyy-MM-dd"));
        fPV.getCalendarView().todayButtonTextProperty().set("Today");
        fPV.getCalendarView().setShowWeeks(false);
        grpFechas.add(fPV,3,2);
        fPV.getStylesheets().add("/org/josuegarcia/resource/DatePicker.css");
        fNacimiento.setDisable(true);
        fPV.setDisable(true);
       
    }
    
    
    public void cargarDatos(){
        tblPacientes.setItems(getPaciente());
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("codigoPaciente"));
        colNombresPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, String>("nombresPaciente"));
        colApellidosPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, String>("apellidosPaciente"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente, String>("sexo"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<Paciente, Date>("fechaNacimiento"));
        colDireccionPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, String>("direccionPaciente"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<Paciente, String>("telefonoPersonal"));
        colFechaPrimeraVisita.setCellValueFactory(new PropertyValueFactory<Paciente, Date>("fechaPrimeraVisita"));
    }
    
    public void seleccionarElemento(){
        if (tblPacientes.getSelectionModel().getSelectedItem() != null){
            txtCodigoPaciente.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
            txtNombresPaciente.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getNombresPaciente());
            txtApellidosPaciente.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getApellidosPaciente());
            txtSexo.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getSexo());
            fNacimiento.selectedDateProperty().set(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getFechaNacimiento());
            txtDireccionPaciente.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDireccionPaciente());
            txtTelefonoPersonal.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
            fPV.selectedDateProperty().set(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getFechaPrimeraVisita());  
        }else {
            JOptionPane.showMessageDialog(null, "No selecciono ningun dato");
        }            
    }
   
    
    public ObservableList<Paciente> getPaciente(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_ListarPacientes}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
              lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                                  resultado.getString("nombresPaciente"),
                                  resultado.getString("apellidosPaciente"),
                                  resultado.getString("sexo"),
                                  resultado.getDate("fechaNacimiento"),
                                  resultado.getString("direccionPaciente"),
                                  resultado.getString("telefonoPersonal"),
                                  resultado.getDate("fechaPrimeraVisita")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaPaciente = FXCollections.observableArrayList(lista);
    }
    
    public void nuevo(){
       switch(tipoDeOperacion){
           case NINGUNO:
               activarControles();
               limpiarControles();
               btnNuevo.setText("Guardar");
               btnEliminar.setText("Cancelar");
               btnEditar.setDisable(true);
               btnReporte.setDisable(true);
               imgNuevo.setImage(new Image("/org/josuegarcia/image/guardar.png"));
               imgEliminar.setImage(new Image("org/josuegarcia/image/cancelar.png"));
               tipoDeOperacion = operaciones.GUARDAR;
               break;
               
           case GUARDAR:
               if(txtCodigoPaciente.getText().length() <=0 || txtNombresPaciente.getText().length()<=0 || txtApellidosPaciente.getText().length()<=0 ||txtSexo.getText().length()<=0 || txtDireccionPaciente.getText().length()<=0 || txtTelefonoPersonal.getText().length()<=0 || fNacimiento.getSelectedDate() == null || fPV.getSelectedDate() == null){
                   JOptionPane.showMessageDialog(null, "Debe ingresar todos los datos");
               }else{
               guardar();
               desactivarControles();
               limpiarControles();
               btnNuevo.setText("Nuevo");
               btnEliminar.setText("Eliminar");
               btnEditar.setDisable(false);
               btnReporte.setDisable(false);
               imgNuevo.setImage(new Image("org/josuegarcia/image/Nuevo.png"));
               imgEliminar.setImage(new Image("org/josuegarcia/image/Eliminar.png"));
               tipoDeOperacion = operaciones.NINGUNO;
               cargarDatos();
               }
               break; 
       }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("org/josuegarcia/image/Nuevo.png"));
                imgEliminar.setImage(new Image("org/josuegarcia/image/Eliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;    
            default: 
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Paciente",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_EliminarPaciente(?)}");
                            procedimiento.setInt(1, ((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
                            procedimiento.execute();
                            listaPaciente.remove(tblPacientes.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else {
                        desactivarControles();
                        limpiarControles();
                    }
                    
                 
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                
        }
    }
    
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if (tblPacientes.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    imgEditar.setImage(new Image("org/josuegarcia/image/Actualizar.png")); 
                    imgReporte.setImage(new Image("org/josuegarcia/image/cancelar.png"));
                    activarControles();
                    txtCodigoPaciente.setEditable(false);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else 
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemanto");
                break;
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                imgEditar.setImage(new Image("org/josuegarcia/image/editar.png")); 
                imgReporte.setImage(new Image("org/josuegarcia/image/Reporte.png")); 
                desactivarControles();
                limpiarControles();
                cargarDatos();
                tipoDeOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_EditarPaciente(?,?,?,?,?,?,?,?)}");
            Paciente registro = (Paciente)tblPacientes.getSelectionModel().getSelectedItem();
            registro.setNombresPaciente(txtNombresPaciente.getText());
            registro.setApellidosPaciente(txtApellidosPaciente.getText());
            registro.setSexo(txtSexo.getText());
            registro.setFechaNacimiento(fNacimiento.getSelectedDate());
            registro.setDireccionPaciente(txtDireccionPaciente.getText());
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setFechaPrimeraVisita(fPV.getSelectedDate());
            procedimiento.setInt(1, registro.getCodigoPaciente());
            procedimiento.setString(2, registro.getNombresPaciente());
            procedimiento.setString(3, registro.getApellidosPaciente());
            procedimiento.setString(4, registro.getSexo());
            procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setString(6, registro.getDireccionPaciente());
            procedimiento.setString(7, registro.getTelefonoPersonal());
            procedimiento.setDate(8, new java.sql.Date(registro.getFechaPrimeraVisita().getTime()));
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case NINGUNO:
                imprimirReporte();
                limpiarControles();
                break;
           
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                    
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                imgEditar.setImage(new Image("org/josuegarcia/image/editar.png"));
                imgReporte.setImage(new Image("org/josuegarcia/image/Reporte.png")); 
                tblPacientes.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                break;
                
        }
    }
    
    
    public void guardar(){
        Paciente registro = new Paciente();
        registro.setCodigoPaciente(Integer.parseInt(txtCodigoPaciente.getText()));
        registro.setNombresPaciente(txtNombresPaciente.getText());
        registro.setApellidosPaciente(txtApellidosPaciente.getText());
        registro.setSexo(txtSexo.getText());
        registro.setFechaNacimiento(fNacimiento.getSelectedDate());
        registro.setDireccionPaciente(txtDireccionPaciente.getText());
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setFechaPrimeraVisita(fPV.getSelectedDate());
        try{
           PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_AgregarPaciente(?,?,?,?,?,?,?,?)}");
           procedimiento.setInt(1, registro.getCodigoPaciente());
           procedimiento.setString(2, registro.getNombresPaciente());
           procedimiento.setString(3, registro.getApellidosPaciente());
           procedimiento.setString(4, registro.getSexo());
           procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
           procedimiento.setString(6, registro.getDireccionPaciente());
           procedimiento.setString(7, registro.getTelefonoPersonal());
           procedimiento.setDate(8, new java.sql.Date(registro.getFechaPrimeraVisita().getTime()));
           procedimiento.execute();
           listaPaciente.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoPaciente", null);
        parametros.put("FONDO", GenerarReporte.class.getResource("/org/josuegarcia/image/FondoHorizontal.png"));
        GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de pacientes", parametros);
                
    }
    
    
    
    
    
    
    
    
    
    
    public void desactivarControles(){
        txtCodigoPaciente.setEditable(false);
        txtNombresPaciente.setEditable(false);
        txtApellidosPaciente.setEditable(false);
        txtSexo.setEditable(false);
        txtDireccionPaciente.setEditable(false);
        txtTelefonoPersonal.setEditable(false);
        fPV.setDisable(true);
        fNacimiento.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoPaciente.setEditable(true);
        txtNombresPaciente.setEditable(true);
        txtApellidosPaciente.setEditable(true);
        txtSexo.setEditable(true);
        txtDireccionPaciente.setEditable(true);
        txtTelefonoPersonal.setEditable(true);
        fNacimiento.setDisable(false);
        fPV.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoPaciente.clear();
        txtNombresPaciente.clear();
        txtApellidosPaciente.clear();
        txtSexo.clear();
        txtDireccionPaciente.clear();
        txtTelefonoPersonal.clear(); 
        fNacimiento.selectedDateProperty().set(null);
        fPV.selectedDateProperty().set(null);
        tblPacientes.getSelectionModel().clearSelection();
    }
    
    
    
    
    
    
    
    

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrinciopal) {
        this.escenarioPrincipal = escenarioPrinciopal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    
}
