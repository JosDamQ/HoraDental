package org.josuegarcia.controller;

import java.net.URL;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
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
import javax.swing.JOptionPane;
import org.josuegarcia.bean.Doctor;
import org.josuegarcia.bean.Especialidad;
import org.josuegarcia.db.Conexion;
import org.josuegarcia.report.GenerarReporte;
import org.josuegarcia.system.Principal;


public class DoctorController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO}
    private operaciones tipoDeOperaciones = operaciones.NINGUNO;
    private ObservableList<Doctor> listaDoctor;
    private ObservableList<Especialidad> listaEspecialidad;
    @FXML TextField txtNumeroColegiado;
    @FXML TextField txtNombresDoctor;
    @FXML TextField txtApellidosDoctor;
    @FXML TextField txtTelefonoContacto;
    @FXML ComboBox cmbCodigoEspecialidad;
    @FXML TableView tblDoctores;
    @FXML TableColumn colNumeroColegiado;
    @FXML TableColumn colNombresDoctor;
    @FXML TableColumn colApellidosDoctor;
    @FXML TableColumn colTelefonoContacto;
    @FXML TableColumn colCodEspecialidades;
    @FXML Button btnNuevo;
    @FXML Button btnEliminar;
    @FXML Button btnEditar;
    @FXML Button btnReporte;
    @FXML ImageView imgNuevo;
    @FXML ImageView imgEliminar;
    @FXML ImageView imgEditar;
    @FXML ImageView imgReporte;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoEspecialidad.setItems(getEspecialidad());
        cmbCodigoEspecialidad.setDisable(true);
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
        tblDoctores.setItems(getDoctor());
        colNumeroColegiado.setCellValueFactory(new PropertyValueFactory<Doctor,Integer>("numeroColegiado"));
        colNombresDoctor.setCellValueFactory(new PropertyValueFactory<Doctor,String>("nombresDoctor"));
        colApellidosDoctor.setCellValueFactory(new PropertyValueFactory<Doctor,String>("apellidosDoctor"));
        colTelefonoContacto.setCellValueFactory(new PropertyValueFactory<Doctor,String>("telefonoContacto"));
        colCodEspecialidades.setCellValueFactory(new PropertyValueFactory<Doctor,Integer>("codigoEspecialidad"));
    }
    
    
    
    public void seleccionarElemento(){
        if(tblDoctores.getSelectionModel().getSelectedItem() != null){
            txtNumeroColegiado.setText(String.valueOf(((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getNumeroColegiado()));
            txtNombresDoctor.setText(((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getNombresDoctor());
            txtApellidosDoctor.setText(((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getApellidosDoctor());
            txtTelefonoContacto.setText(((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getTelefonoContacto());
            cmbCodigoEspecialidad.getSelectionModel().select(buscarEspecialidad(((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()));
        }
        else
            JOptionPane.showMessageDialog(null, "No seleccionaste ningun dato");
    } 
    
    
    public Especialidad buscarEspecialidad(int codigoEspecialidad){
        Especialidad resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_BuscarEspecialidad(?)}");
            procedimiento.setInt(1, codigoEspecialidad);
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                resultado = new Especialidad(result.getInt("codigoEspecialidad"),
                                           result.getString("descripcion"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    
    public ObservableList<Especialidad> getEspecialidad(){
        ArrayList<Especialidad> lista = new ArrayList<Especialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_ListarEspecialidades}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"),
                                            resultado.getString("descripcion")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaEspecialidad = FXCollections.observableArrayList(lista);  
    }
    
    public void nuevo(){
        switch(tipoDeOperaciones){
            case NINGUNO:
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                imgNuevo.setImage(new Image("org/josuegarcia/image/guardar.png"));
                imgEliminar.setImage(new Image("org/josuegarcia/image/cancelar.png"));
                tipoDeOperaciones = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(txtNumeroColegiado.getText().length() <=0 || txtNombresDoctor.getText().length() <=0 || txtApellidosDoctor.getText().length() <=0 || txtTelefonoContacto.getText().length() <=0 || cmbCodigoEspecialidad.getValue() == null){
                    JOptionPane.showMessageDialog(null, "Debes ingresar todos los datos");
                }else{
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("org/josuegarcia/image/NuevoDoctor.png"));
                imgEliminar.setImage(new Image("org/josuegarcia/image/EliminarDoctor.png"));
                tipoDeOperaciones = operaciones.NINGUNO;
                cargarDatos();
                }
                break;
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperaciones){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("org/josuegarcia/image/NuevoDoctor.png"));
                imgEliminar.setImage(new Image("org/josuegarcia/image/EliminarDoctor.png"));
                tipoDeOperaciones = operaciones.NINGUNO;
                cargarDatos();
                break;
            default:
                if(tblDoctores.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Estas seguro de eliminar el registro?","Eliminar doctor",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_EliminarDoctor(?)}");
                            procedimiento.setInt(1, ((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getNumeroColegiado());
                            procedimiento.execute();
                            listaDoctor.remove(tblDoctores.getSelectionModel().getSelectedItem());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else {
                        desactivarControles();
                        limpiarControles();
                    }
                }else
                    JOptionPane.showMessageDialog(null, "Debes de seleccionar un dato");
        }
    }
    
    public void editar(){
        switch(tipoDeOperaciones){
            case NINGUNO:
               if(tblDoctores.getSelectionModel().getSelectedItem() != null){
                   btnEditar.setText("Actualizar");
                   btnReporte.setText("Cancelar");
                   btnNuevo.setDisable(true);
                   btnEliminar.setDisable(true);
                   imgEditar.setImage(new Image("org/josuegarcia/image/Actualizar.png"));
                   imgReporte.setImage(new Image("org/josuegarcia/image/cancelar.png"));
                   activarControles();
                   cmbCodigoEspecialidad.setDisable(true);
                   txtNumeroColegiado.setEditable(false);
                   tipoDeOperaciones = operaciones.ACTUALIZAR;
               }else
                   JOptionPane.showMessageDialog(null, "Debes seleccionar un elemento primero");
               break;
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                imgEditar.setImage(new Image("org/josuegarcia/image/EditarDoctor.png"));
                imgReporte.setImage(new Image("org/josuegarcia/image/ReporteDoctor.png"));
                desactivarControles();
                limpiarControles();
                cargarDatos();
                tipoDeOperaciones = operaciones.NINGUNO;
                break;
        }
    }
    
    public void reporte(){
        switch(tipoDeOperaciones){
            case NINGUNO:
                imprimirReporte();
                limpiarControles();
                break;
            case ACTUALIZAR:
                limpiarControles();
                desactivarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                imgEditar.setImage(new Image("org/josuegarcia/image/EditarDoctor.png"));
                imgReporte.setImage(new Image("org/josuegarcia/image/ReporteDoctor.png"));
                tipoDeOperaciones = operaciones.NINGUNO;
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_EditarDoctor(?,?,?,?,?)}");
            Doctor registro = (Doctor)tblDoctores.getSelectionModel().getSelectedItem();
            registro.setNombresDoctor(txtNombresDoctor.getText());
            registro.setApellidosDoctor(txtApellidosDoctor.getText());
            registro.setTelefonoContacto(txtTelefonoContacto.getText());
            registro.setCodigoEspecialidad(((Especialidad)cmbCodigoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
            procedimiento.setInt(1, registro.getNumeroColegiado());
            procedimiento.setString(2, registro.getNombresDoctor());
            procedimiento.setString(3, registro.getApellidosDoctor());
            procedimiento.setString(4, registro.getTelefonoContacto());
            procedimiento.setInt(5, registro.getCodigoEspecialidad());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    
    
    public void guardar(){
        Doctor registro = new Doctor();
        registro.setNumeroColegiado(Integer.parseInt(txtNumeroColegiado.getText()));
        registro.setNombresDoctor(txtNombresDoctor.getText());
        registro.setApellidosDoctor(txtApellidosDoctor.getText());
        registro.setTelefonoContacto(txtTelefonoContacto.getText());
        registro.setCodigoEspecialidad(((Especialidad)cmbCodigoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_AgregarDoctor(?,?,?,?,?)}");
            procedimiento.setInt(1, registro.getNumeroColegiado());
            procedimiento.setString(2, registro.getNombresDoctor());
            procedimiento.setString(3, registro.getApellidosDoctor());
            procedimiento.setString(4, registro.getTelefonoContacto());
            procedimiento.setInt(5, registro.getCodigoEspecialidad());
            procedimiento.execute();
            listaDoctor.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("numeroColegiado", null);
        parametros.put("FONDO", GenerarReporte.class.getResource("/org/josuegarcia/image/FondoHorizontal.png"));
        GenerarReporte.mostrarReporte("ReporteDoctores.jasper", "Reporte Doctores", parametros);
    }
    

    
    public void desactivarControles(){
        txtNumeroColegiado.setEditable(false);
        txtNombresDoctor.setEditable(false);
        txtApellidosDoctor.setEditable(false);
        txtTelefonoContacto.setEditable(false);
        cmbCodigoEspecialidad.setDisable(true);
    }
    
    public void activarControles(){
        txtNumeroColegiado.setEditable(true);
        txtNombresDoctor.setEditable(true);
        txtApellidosDoctor.setEditable(true);
        txtTelefonoContacto.setEditable(true);
        cmbCodigoEspecialidad.setDisable(false);
    }
    
    public void limpiarControles(){
        txtNumeroColegiado.clear();
        txtNombresDoctor.clear();
        txtApellidosDoctor.clear();
        txtTelefonoContacto.clear();
        tblDoctores.getSelectionModel().clearSelection();
        cmbCodigoEspecialidad.getSelectionModel().clearSelection();
        cmbCodigoEspecialidad.setValue(null);
        
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
