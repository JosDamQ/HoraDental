
package org.josuegarcia.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
import org.josuegarcia.bean.DetalleReceta;
import org.josuegarcia.bean.Medicamento;
import org.josuegarcia.bean.Receta;
import org.josuegarcia.db.Conexion;
import org.josuegarcia.report.GenerarReporte;
import org.josuegarcia.system.Principal;

public class DetalleRecetaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO}
    private operaciones tipoDeOperaciones = operaciones.NINGUNO;
    private ObservableList <DetalleReceta> listaDetalleReceta;
    private ObservableList <Receta> listaReceta;
    private ObservableList <Medicamento> listaMedicamento;
    @FXML private TextField txtCodigoDetalleReceta;
    @FXML private TextField txtDosis;
    @FXML private ComboBox cmbCodigoReceta;
    @FXML private ComboBox cmbCodigoMedicamento;
    @FXML private TableView tblDetalleRecetas;
    @FXML private TableColumn colCodigoDetalleReceta;
    @FXML private TableColumn colDosis;
    @FXML private TableColumn colCodReceta;
    @FXML private TableColumn colCodMedicamento;
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
        cmbCodigoMedicamento.setItems(getMedicamento());
        cmbCodigoReceta.setItems(getReceta());
        cmbCodigoMedicamento.setDisable(true);
        cmbCodigoReceta.setDisable(true);
    }
    
    public ObservableList<DetalleReceta>getDetalleReceta(){
        ArrayList<DetalleReceta> lista = new ArrayList<DetalleReceta>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_ListarDetalleRecetas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new DetalleReceta(resultado.getInt("codigoDetalleReceta"),
                                            resultado.getString("dosis"),
                                            resultado.getInt("codigoReceta"),
                                            resultado.getInt("codigoMedicamento")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaDetalleReceta = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Medicamento>getMedicamento(){
        ArrayList<Medicamento> lista = new ArrayList<Medicamento>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_ListarMedicamentos}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Medicamento(resultado.getInt("codigoMedicamento"),
                                        resultado.getString("nombreMedicamento")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaMedicamento = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Receta>getReceta(){
        ArrayList <Receta> lista = new ArrayList<Receta>();
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

    public void cargarDatos(){
        tblDetalleRecetas.setItems(getDetalleReceta());
        colCodigoDetalleReceta.setCellValueFactory(new PropertyValueFactory<DetalleReceta, Integer>("codigoDetalleReceta"));
        colDosis.setCellValueFactory(new PropertyValueFactory<DetalleReceta, String>("dosis"));
        colCodMedicamento.setCellValueFactory(new PropertyValueFactory<DetalleReceta, Integer>("codigoMedicamento"));
        colCodReceta.setCellValueFactory(new PropertyValueFactory<DetalleReceta, Integer>("codigoReceta"));
    }
    
    public Medicamento buscarMedicamento(int codigoMedicamento){
        Medicamento resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_BuscarMedicamento(?)}");
            procedimiento.setInt(1, codigoMedicamento);
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                resultado = new Medicamento(result.getInt("codigoMedicamento"),
                                            result.getString("nombreMedicamento"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public Receta buscarReceta(int codigoReceta){
        Receta resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_BuscarReceta(?)}");
            procedimiento.setInt(1, codigoReceta);
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                resultado = new Receta(result.getInt("codigoReceta"),
                                        result.getDate("fechaReceta"),
                                        result.getInt("NumeroColegiado"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void seleccionarElemento(){
        if(tblDetalleRecetas.getSelectionModel().getSelectedItem() != null){
            txtCodigoDetalleReceta.setText(String.valueOf(((DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem()).getCodigoDetalleReceta()));
            txtDosis.setText(((DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem()).getDosis());
            cmbCodigoMedicamento.getSelectionModel().select(buscarMedicamento(((DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem()).getCodigoMedicamento()));
            cmbCodigoReceta.getSelectionModel().select(buscarReceta(((DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta()));
        }else
            JOptionPane.showMessageDialog(null, "No seleccionaste ningun dato");
    }
    
    public void nuevo(){
        switch(tipoDeOperaciones){
            case NINGUNO:
                limpiarControles();
                activarControles();
                txtCodigoDetalleReceta.setEditable(false);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                imgNuevo.setImage(new Image("org/josuegarcia/image/guardar.png"));
                imgEliminar.setImage(new Image("org/josuegarcia/image/cancelar.png"));
                tipoDeOperaciones = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(txtDosis.getText().length() <=0 || cmbCodigoMedicamento.getValue() == null || cmbCodigoReceta.getValue() == null){
                    JOptionPane.showMessageDialog(null, "Debes ingresar todos los datos");
                }else{
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
    
    public void guardar(){
        DetalleReceta registro = new DetalleReceta();
        registro.setDosis(txtDosis.getText());
        registro.setCodigoReceta(((Receta)cmbCodigoReceta.getSelectionModel().getSelectedItem()).getCodigoReceta());
        registro.setCodigoMedicamento(((Medicamento)cmbCodigoMedicamento.getSelectionModel().getSelectedItem()).getCodigoMedicamento());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_AgregarDetalleReceta(?,?,?)}");
            procedimiento.setString(1, registro.getDosis());
            procedimiento.setInt(2, registro.getCodigoReceta());
            procedimiento.setInt(3, registro.getCodigoMedicamento());
            procedimiento.execute();
            listaDetalleReceta.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperaciones){
            case GUARDAR:
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
                if(tblDetalleRecetas.getSelectionModel().getSelectedItem() != null){
                int respuesta = JOptionPane.showConfirmDialog(null, "¿Estas seguro de eliminar ese registro?","Eliminar Detalle Receta",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                if(respuesta == JOptionPane.YES_OPTION){
                    try{
                        PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_EliminarDetalleReceta(?)}");
                        procedimiento.setInt(1, ((DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem()).getCodigoDetalleReceta());
                        procedimiento.execute();
                        listaDetalleReceta.remove(tblDetalleRecetas.getSelectionModel().getSelectedItem());
                        limpiarControles();
                    }catch(Exception e){
                        e.printStackTrace();
                    }  
                }else{
                    limpiarControles();
                    desactivarControles();
                } 
            }else
                    JOptionPane.showMessageDialog(null, "Debes seleccionar primero un dato");
        }
    }
    
    public void editar(){
        switch(tipoDeOperaciones){
            case NINGUNO:
                if(tblDetalleRecetas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    imgEditar.setImage(new Image("org/josuegarcia/image/Actualizar.png"));
                    imgReporte.setImage(new Image("org/josuegarcia/image/cancelar.png"));
                    activarControles();
                    cmbCodigoMedicamento.setDisable(true);
                    cmbCodigoReceta.setDisable(true);
                    txtCodigoDetalleReceta.setEditable(false);
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{Call sp_EditarDetalleReceta(?,?,?,?)}");
            DetalleReceta registro = (DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem();
            registro.setDosis(txtDosis.getText());
            registro.setCodigoReceta(((Receta)cmbCodigoReceta.getSelectionModel().getSelectedItem()).getCodigoReceta());
            registro.setCodigoMedicamento(((Medicamento)cmbCodigoMedicamento.getSelectionModel().getSelectedItem()).getCodigoMedicamento());
            procedimiento.setInt(1, registro.getCodigoDetalleReceta());
            procedimiento.setString(2, registro.getDosis());
            procedimiento.setInt(3, registro.getCodigoReceta());
            procedimiento.setInt(4, registro.getCodigoMedicamento());
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
                cargarDatos();
                tipoDeOperaciones = operaciones.NINGUNO;
                break;
            case NINGUNO:
                imprimirReporte();
                limpiarControles();
                
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoDetalleReceta", null);
        parametros.put("FONDO", GenerarReporte.class.getResource("/org/josuegarcia/image/FondoReporte.png"));
        GenerarReporte.mostrarReporte("DetalleReceta.jasper", "Reporte de Detalle Receta", parametros);
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
        txtCodigoDetalleReceta.clear();
        txtDosis.clear();
        cmbCodigoMedicamento.getSelectionModel().clearSelection();
        cmbCodigoReceta.getSelectionModel().clearSelection();
        tblDetalleRecetas.getSelectionModel().clearSelection();
        cmbCodigoMedicamento.setValue(null);
        cmbCodigoReceta.setValue(null);
    }
    
    public void activarControles(){
        txtCodigoDetalleReceta.setEditable(true);
        txtDosis.setEditable(true);
        cmbCodigoMedicamento.setDisable(false);
        cmbCodigoReceta.setDisable(false);
    }
    
    public void desactivarControles(){
        txtCodigoDetalleReceta.setEditable(false);
        txtDosis.setEditable(false);
        cmbCodigoMedicamento.setDisable(true);
        cmbCodigoReceta.setDisable(true);
    }
    
}
