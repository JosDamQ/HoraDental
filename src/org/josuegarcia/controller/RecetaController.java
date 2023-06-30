package org.josuegarcia.controller;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.josuegarcia.bean.Doctor;
import org.josuegarcia.bean.Receta;
import org.josuegarcia.db.Conexion;
import org.josuegarcia.report.GenerarReporte;
import org.josuegarcia.system.Principal;


public class RecetaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,CANCELAR,ACTUALIZAR,NINGUNO}
    private operaciones tipoDeOperaciones = operaciones.NINGUNO;
    private ObservableList<Receta>listaReceta;
    private ObservableList<Doctor>listaDoctor;
    private DatePicker fReceta;
    @FXML private TextField txtCodigoReceta;
    @FXML private TableView tblRecetas;
    @FXML private ComboBox cmbNumeroColegiado;
    @FXML private TableColumn colCodigoReceta;
    @FXML private TableColumn colFechaReceta;
    @FXML private TableColumn colNumeroColegiado;
    @FXML private GridPane grpFechas;
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
        cmbNumeroColegiado.setItems(getDoctor());
        cmbNumeroColegiado.setDisable(true);
        fReceta = new DatePicker(Locale.ENGLISH);
        fReceta.setDateFormat(new SimpleDateFormat("yyy-MM-dd"));
        fReceta.getCalendarView().todayButtonTextProperty().set("Today");
        fReceta.getCalendarView().setShowWeeks(false);
        grpFechas.add(fReceta, 3, 1);
        fReceta.getStylesheets().add("/org/josuegarcia/resource/DatePicker.css");
        fReceta.setDisable(true);
    }
    
    public ObservableList<Receta> getReceta(){
        ArrayList<Receta> lista = new ArrayList<Receta>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_ListarRecetas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Receta(resultado.getInt("codigoReceta"),
                                    resultado.getDate("fechaReceta"),
                                    resultado.getInt("numeroColegiado")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaReceta = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Doctor> getDoctor(){
        ArrayList<Doctor> lista = new ArrayList<Doctor>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_ListarDoctores}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Doctor(resultado.getInt("numeroColegiado"),
                                        resultado.getString("nombresDoctor"),
                                        resultado.getString("apellidosDoctor"),
                                        resultado.getString("telefonoContacto"),
                                        resultado.getInt("codigoEspecialidad")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaDoctor = FXCollections.observableArrayList(lista);
    }

    public void cargarDatos(){
        tblRecetas.setItems(getReceta());
        colCodigoReceta.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("codigoReceta"));
        colFechaReceta.setCellValueFactory(new PropertyValueFactory<Receta,Date>("fechaReceta"));
        colNumeroColegiado.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("numeroColegiado"));
    }
    
    public void seleccionarElemento(){
        if(tblRecetas.getSelectionModel().getSelectedItem() != null){
            txtCodigoReceta.setText(String.valueOf(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta()));
            fReceta.selectedDateProperty().set(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getFechaReceta());
            cmbNumeroColegiado.getSelectionModel().select(buscarDoctor(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getNumeroColegiado()));
        }else
            JOptionPane.showMessageDialog(null, "No seleccionaste ningun dato");
    }
    
    public Doctor buscarDoctor(int numeroColegiado){
        Doctor resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_BuscarDoctor(?)}");
            procedimiento.setInt(1, numeroColegiado);
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                resultado = new Doctor(result.getInt("numeroColegiado"),
                                        result.getString("nombresDoctor"),
                                        result.getString("apellidosDoctor"),
                                        result.getString("telefonoContacto"),
                                        result.getInt("codigoEspecialidad"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void nuevo(){
        switch(tipoDeOperaciones){
            case NINGUNO:
                cmbNumeroColegiado.getSelectionModel().clearSelection();
                limpiarControles();
                activarControles();
                txtCodigoReceta.setEditable(false);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                imgNuevo.setImage(new Image("org/josuegarcia/image/guardar.png"));
                imgEliminar.setImage(new Image("org/josuegarcia/image/cancelar.png"));
                tipoDeOperaciones = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(cmbNumeroColegiado.getValue() == null || fReceta.getSelectedDate() == null){
                    JOptionPane.showMessageDialog(null, "Debes ingresar todos los datos");
                }
                else{
                guardar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("org/josuegarcia/image/Agregar.png"));
                imgEliminar.setImage(new Image("org/josuegarcia/image/Quitar.png"));
                tipoDeOperaciones = operaciones.NINGUNO;
                cargarDatos();
                }
                break;
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperaciones){
            case GUARDAR:
                cmbNumeroColegiado.getSelectionModel().clearSelection();
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("org/josuegarcia/image/Agregar.png"));
                imgEliminar.setImage(new Image("org/josuegarcia/image/Quitar.png"));
                tipoDeOperaciones = operaciones.NINGUNO;
                cargarDatos();
                break;
            default:
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Estas seguro de eliminar el registro?","Eliminar Receta",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_EliminarReceta(?)}");
                            procedimiento.setInt(1, ((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta());
                            procedimiento.execute();
                            listaReceta.remove(tblRecetas.getSelectionModel().getSelectedItem());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else {
                        limpiarControles();
                        desactivarControles();
                    }
                }else
                    JOptionPane.showMessageDialog(null, "Debes seleccionar un dato primero");
        }
    }
    
    public void editar(){
        switch (tipoDeOperaciones){
            case NINGUNO:
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    imgEditar.setImage(new Image("org/josuegarcia/image/Actualizar.png"));
                    imgReporte.setImage(new Image("org/josuegarcia/image/cancelar.png"));
                    activarControles();
                    cmbNumeroColegiado.setDisable(true);
                    txtCodigoReceta.setEditable(false);
                    tipoDeOperaciones = operaciones.ACTUALIZAR;
                }else 
                    JOptionPane.showMessageDialog(null, "Debes seleccionar un dato primero");
                break;
            case ACTUALIZAR:
                actualizar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                imgEditar.setImage(new Image("org/josuegarcia/image/editartexto.png"));
                imgReporte.setImage(new Image("org/josuegarcia/image/reportenegocios.png"));
                cargarDatos();
                tipoDeOperaciones = operaciones.NINGUNO;
                break;
        }
    }
    
    public void reporte(){
        switch(tipoDeOperaciones){
            case ACTUALIZAR:
                cmbNumeroColegiado.getSelectionModel().clearSelection();
                limpiarControles();
                desactivarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                imgEditar.setImage(new Image("org/josuegarcia/image/editartexto.png"));
                imgReporte.setImage(new Image("org/josuegarcia/image/reportenegocios.png"));
                tipoDeOperaciones = operaciones.NINGUNO;
                break;
            case NINGUNO:
                if(tblRecetas.getSelectionModel().getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un dato a mostrar");
                }else{
                    imprimirReporte();
                    limpiarControles();
                }
                break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        int codReceta  = ((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta();
        parametros.put("codReceta", codReceta);
        parametros.put("FONDO", GenerarReporte.class.getResource("/org/josuegarcia/image/FondoReporte.png"));
        GenerarReporte.mostrarReporte("Receta.jasper", "Reporte de Receta", parametros);
        
    }
    
    public void actualizar(){
        try{
        PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_EditarReceta(?,?,?)}");
        Receta registro = (Receta)tblRecetas.getSelectionModel().getSelectedItem();
        registro.setFechaReceta(fReceta.getSelectedDate());
        registro.setNumeroColegiado(((Doctor)cmbNumeroColegiado.getSelectionModel().getSelectedItem()).getNumeroColegiado());
        procedimiento.setInt(1, registro.getCodigoReceta());
        procedimiento.setDate(2, new java.sql.Date(registro.getFechaReceta().getTime()));
        procedimiento.setInt(3, registro.getNumeroColegiado());
        procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void guardar(){
        Receta registro = new Receta();
//        registro.setCodigoReceta(Integer.parseInt(txtCodigoReceta.getText()));
        registro.setFechaReceta(fReceta.getSelectedDate());
        registro.setNumeroColegiado(((Doctor)cmbNumeroColegiado.getSelectionModel().getSelectedItem()).getNumeroColegiado());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_AgregarReceta(?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaReceta().getTime()));
            procedimiento.setInt(2, registro.getNumeroColegiado());
            procedimiento.execute();
            listaReceta.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
//    public void limpiarCmb(){
//        cmbNumeroColegiado.getSelectionModel().clearSelection();
//    }
    
    public void activarControles(){
        txtCodigoReceta.setEditable(true);
        cmbNumeroColegiado.setDisable(false);
        fReceta.setDisable(false);
    }
    
    public void desactivarControles(){
        txtCodigoReceta.setEditable(false);
        cmbNumeroColegiado.setDisable(true);
        fReceta.setDisable(true);
    }
    
    public void limpiarControles(){
        txtCodigoReceta.clear();
        tblRecetas.getSelectionModel().clearSelection();
        cmbNumeroColegiado.getSelectionModel().clearSelection();
        tblRecetas.getSelectionModel().clearSelection();
        fReceta.selectedDateProperty().set(null);
        cmbNumeroColegiado.setValue(null);
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    
    
}
