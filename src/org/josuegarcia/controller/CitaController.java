package org.josuegarcia.controller;


import com.jfoenix.controls.JFXTimePicker;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
import org.josuegarcia.bean.Cita;
import org.josuegarcia.bean.Doctor;
import org.josuegarcia.bean.Paciente;
import org.josuegarcia.db.Conexion;
import org.josuegarcia.system.Principal;


public class CitaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones {NUEVO,GUARDAR,ACTUALIZAR,ELIMINAR,CANCELAR,EDITAR,NINGUNO}
    private  operaciones tipoDeOperaciones = operaciones.NINGUNO;
    private ObservableList<Cita> listaCita;
    private ObservableList<Paciente> listaPaciente;
    private ObservableList<Doctor> listaDoctor;
    private DatePicker fCita;
    @FXML private JFXTimePicker jfxCita;
//    @FXML private TextField txtHoraCita;
    @FXML private TextField txtCodigoCita;
    @FXML private TextField txtTratamiento;
    @FXML private TextField txtDescripcionActual;
    @FXML private GridPane grpCitas;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private ComboBox cmbNumeroColegiado;
    @FXML private TableView tblCitas;
    @FXML private TableColumn colCodigoCita;
    @FXML private TableColumn colFechaCita;
    @FXML private TableColumn colHoraCita;
    @FXML private TableColumn colTratamiento;
    @FXML private TableColumn colDescripcionCondicionActual;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colNumeroColegiado;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoPaciente.setItems(getPaciente());
        cmbNumeroColegiado.setItems(getDoctor());
        cmbCodigoPaciente.setDisable(true);
        cmbNumeroColegiado.setDisable(true);
        fCita = new DatePicker(Locale.ENGLISH);
        fCita.setDateFormat(new SimpleDateFormat("yy-MM-dd"));
        fCita.getCalendarView().todayButtonTextProperty().set("Today");
        fCita.getCalendarView().setShowWeeks(false);
        grpCitas.add(fCita, 3, 0);
        fCita.getStylesheets().add("/org/josuegarcia/resource/DatePicker.css");
        fCita.setDisable(true);
        jfxCita.setDisable(true);
    }
    
    public void cargarDatos(){
        tblCitas.setItems(getCita());
        colCodigoCita.setCellValueFactory(new PropertyValueFactory<Cita, Integer>("codigoCita"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory<Cita, Date>("fechaCita"));
        colHoraCita.setCellValueFactory(new PropertyValueFactory<Cita, Time>("horaCita"));
        colTratamiento.setCellValueFactory(new PropertyValueFactory<Cita, String>("tratamiento"));
        colDescripcionCondicionActual.setCellValueFactory(new PropertyValueFactory<Cita, String>("descripcioncondicionActual"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Cita, Integer>("codigoPaciente"));
        colNumeroColegiado.setCellValueFactory(new PropertyValueFactory<Cita, Integer>("numeroColegiado"));
    }
    
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1, codigoPaciente);
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                resultado = new Paciente(result.getInt("codigoPaciente"),
                                            result.getString("nombresPaciente"),
                                            result.getString("apellidosPaciente"),
                                            result.getString("sexo"),
                                            result.getDate("fechaNacimiento"),
                                            result.getString("direccionPaciente"),
                                            result.getString("telefonoPersonal"),
                                            result.getDate("fechaPrimeraVisita"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
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
    
    public void seleccionarElemento(){
        if(tblCitas.getSelectionModel().getSelectedItem() != null){
        txtCodigoCita.setText(String.valueOf(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoCita()));
        txtTratamiento.setText(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getTratamiento());
//        txtHoraCita.setText(String.valueOf(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getHoraCita()));
        jfxCita.setValue(((((Cita)tblCitas.getSelectionModel().getSelectedItem()).getHoraCita())).toLocalTime());
        txtDescripcionActual.setText(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getDescripcioncondicionActual());
        cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
        cmbNumeroColegiado.getSelectionModel().select(buscarDoctor(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getNumeroColegiado()));
        fCita.selectedDateProperty().set(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getFechaCita());
//        jfxCita.setValue(LocalTime.MAX);
        }else
            JOptionPane.showMessageDialog(null, "No seleccionaste ningun dato");
        
    }
   
    
//    <>
    public ObservableList<Cita> getCita(){
        ArrayList<Cita> lista = new ArrayList<Cita>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_ListarCitas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Cita(resultado.getInt("codigoCita"),
                                resultado.getDate("fechaCita"),
                                resultado.getTime("horaCita"),
                                resultado.getString("tratamiento"),
                                resultado.getString("descripcioncondicionActual"),
                                resultado.getInt("codigoPaciente"),
                                resultado.getInt("numeroColegiado")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaCita = FXCollections.observableArrayList(lista);
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
    
    public void nuevo(){
        switch(tipoDeOperaciones){
            case NINGUNO:
                limpiarControles();
                activarControles();
                txtCodigoCita.setEditable(false);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                imgNuevo.setImage(new Image("org/josuegarcia/image/guardar.png"));
                imgEliminar.setImage(new Image("org/josuegarcia/image/cancelar.png"));
                tipoDeOperaciones = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(txtTratamiento.getText().length() <=0 || txtDescripcionActual.getText().length() <=0 || cmbCodigoPaciente.getValue() == null || cmbNumeroColegiado.getValue() == null || fCita.getSelectedDate() == null || jfxCita.getValue() == null){
                    JOptionPane.showMessageDialog(null, "Debe ingresar los datos completos");
                }else {
                guardar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
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
    
    public void guardar(){
        Cita registro = new Cita();
        registro.setFechaCita(fCita.getSelectedDate());
//        registro.setHoraCita(Time.valueOf(txtHoraCita.getText()));
        registro.setHoraCita(java.sql.Time.valueOf(jfxCita.getValue()));
        registro.setTratamiento(txtTratamiento.getText());
        registro.setDescripcioncondicionActual(txtDescripcionActual.getText());
        registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        registro.setNumeroColegiado(((Doctor)cmbNumeroColegiado.getSelectionModel().getSelectedItem()).getNumeroColegiado());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_AgregarCita(?,?,?,?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setTime(2, registro.getHoraCita());
            procedimiento.setString(3, registro.getTratamiento());
            procedimiento.setString(4, registro.getDescripcioncondicionActual());
            procedimiento.setInt(5, registro.getCodigoPaciente());
            procedimiento.setInt(6, registro.getNumeroColegiado());
            procedimiento.execute();
            listaCita.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void eliminar(){
        switch (tipoDeOperaciones){
            case GUARDAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("org/josuegarcia/image/Agregar.png"));
                imgEliminar.setImage(new Image("org/josuegarcia/image/Quitar.png"));
                tipoDeOperaciones = operaciones.NINGUNO;
                cargarDatos();
                break;
            default :
                if(tblCitas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Estas seguro de eliminar este registro?","Eliminar Cita",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{ Call sp_EliminarCita(?)}");
                            procedimiento.setInt(1, ((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoCita());
                            procedimiento.execute();
                            listaCita.remove(tblCitas.getSelectionModel().getSelectedItem());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        limpiarControles();
                        desactivarControles();
                    }
                }else
                    JOptionPane.showMessageDialog(null, "Debes seleccionar un dato");
        }
    }
    
    public void editar(){
        switch (tipoDeOperaciones){
            case NINGUNO:
                if(tblCitas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    imgEditar.setImage(new Image("org/josuegarcia/image/Actualizar.png"));
                    imgReporte.setImage(new Image("org/josuegarcia/image/cancelar.png"));
                    activarControles();
                    cmbCodigoPaciente.setDisable(true);
                    cmbNumeroColegiado.setDisable(true);
                    txtCodigoCita.setEditable(false);
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
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_EditarCita(?,?,?,?,?,?,?)}");
            Cita registro = (Cita)tblCitas.getSelectionModel().getSelectedItem();
            registro.setFechaCita(fCita.getSelectedDate());
//            registro.setHoraCita(Time.valueOf(txtHoraCita.getText()));
            registro.setHoraCita(java.sql.Time.valueOf(jfxCita.getValue()));
            registro.setTratamiento(txtTratamiento.getText());
            registro.setDescripcioncondicionActual(txtDescripcionActual.getText());
            registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            registro.setNumeroColegiado(((Doctor)cmbNumeroColegiado.getSelectionModel().getSelectedItem()).getNumeroColegiado());
            procedimiento.setInt(1, registro.getCodigoCita());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setTime(3, registro.getHoraCita());
            procedimiento.setString(4, registro.getTratamiento());
            procedimiento.setString(5, registro.getDescripcioncondicionActual());
            procedimiento.setInt(6, registro.getCodigoPaciente());
            procedimiento.setInt(7, registro.getNumeroColegiado());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void reporte(){
        switch(tipoDeOperaciones){
            case ACTUALIZAR:
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
        }
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
    
    public void limpiarControles(){
        txtCodigoCita.clear();
        txtTratamiento.clear();
//        txtHoraCita.clear();
        txtDescripcionActual.clear();
        cmbCodigoPaciente.getSelectionModel().clearSelection();
        cmbNumeroColegiado.getSelectionModel().clearSelection();
        cmbCodigoPaciente.setValue(null);
        cmbNumeroColegiado.setValue(null);
        fCita.selectedDateProperty().set(null);
        jfxCita.setValue(null);
        
        tblCitas.getSelectionModel().clearSelection();
    }
    
    public void activarControles(){
        txtCodigoCita.setEditable(true);
        txtTratamiento.setEditable(true);
//        txtHoraCita.setEditable(true);
        txtDescripcionActual.setEditable(true);
        cmbCodigoPaciente.setDisable(false);
        cmbNumeroColegiado.setDisable(false);
        fCita.setDisable(false);
        jfxCita.setDisable(false);
    }
    
    public void desactivarControles(){
        txtCodigoCita.setEditable(false);
        txtTratamiento.setEditable(false);
//        txtHoraCita.setEditable(false);
        txtDescripcionActual.setEditable(false);
        cmbCodigoPaciente.setDisable(true);
        cmbNumeroColegiado.setDisable(true); 
        jfxCita.setDisable(true);
    }
    
        
}
