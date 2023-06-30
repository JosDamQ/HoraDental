/*
Josue Damian Garcìa Quiñonez
IN5AM
2018163
Fecha de craciôn : 5/4/2022
Fecha de modificacion: 10/5/2022 
 */
package org.josuegarcia.system;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.josuegarcia.controller.CitaController;
import org.josuegarcia.controller.DetalleRecetaController;
import org.josuegarcia.controller.DoctorController;
import org.josuegarcia.controller.EspecialidadController;
import org.josuegarcia.controller.LoginController;
import org.josuegarcia.controller.MedicamentosController;
import org.josuegarcia.controller.MenuPrincipalController;
import org.josuegarcia.controller.PacienteController;
import org.josuegarcia.controller.ProgramadorController;
import org.josuegarcia.controller.RecetaController;
import org.josuegarcia.controller.UsuarioController;



public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/josuegarcia/view/";
    private Stage escenarioPrincipal;
    private Scene escena;
    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        this.escenarioPrincipal = escenarioPrincipal;
        this.escenarioPrincipal.setTitle("Hora Dental");
        escenarioPrincipal.getIcons().add(new Image("/org/josuegarcia/image/Icono.png"));
//        Parent root = FXMLLoader.load(getClass().getResource("/org/josuegarcia/view/MenuPrincipalView.fxml"));
//        Scene escena = new Scene(root);
//        escenarioPrincipal.setScene(escena);
//        menuPrincipal();
        ventanaLogin();
        escenarioPrincipal.show();
    }
    
    public void ventanaLogin(){
        try{
            LoginController vistaLogin = (LoginController) cambiarEscena("LoginView.fxml",323,481);
            vistaLogin.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaUsuario(){
        try{
            UsuarioController vistaUsuario = (UsuarioController) cambiarEscena("UsuarioView.fxml",752,434);
            vistaUsuario.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void menuPrincipal(){
        try{
            MenuPrincipalController ventanaMenu = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml",498,400);
            ventanaMenu.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void ventanaProgramador(){
        try{
            ProgramadorController ventanaProgramador = (ProgramadorController)cambiarEscena("ProgramadorView.fxml",600,400);
            ventanaProgramador.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaPacientes(){
        try{
            PacienteController vistaPaciente = (PacienteController) cambiarEscena ("PlantillaPacienteView.fxml",885,434);
            vistaPaciente.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEspecialidades(){
        try{
            EspecialidadController vistaEspecialidad = (EspecialidadController) cambiarEscena ("PlantillaEspecialidadesView.fxml",752,434);
            vistaEspecialidad.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicamentos(){
        try{
            MedicamentosController vistaMedicamentos = (MedicamentosController) cambiarEscena ("PlantillaMedicamentosView.fxml",752,434);
            vistaMedicamentos.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaDoctores(){
        try{
            DoctorController vistaDoctores = (DoctorController) cambiarEscena("PlantillaDoctoresView.fxml",885,434);
            vistaDoctores.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaRecetas(){
        try{
            RecetaController vistaReceta = (RecetaController) cambiarEscena("PlantillaRecetasView.fxml",756,434);
            vistaReceta.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaDetalleRecetas(){
        try{
            DetalleRecetaController vistaDetalleReceta = (DetalleRecetaController) cambiarEscena("PlantillaDetalleRecetaView.fxml",756,434);
            vistaDetalleReceta.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaCitas(){
        try{
            CitaController vistaCita = (CitaController) cambiarEscena("PlantillaCitasView.fxml",929,434);
            vistaCita.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
    
    public Initializable cambiarEscena(String fxml,int ancho,int alto)throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
        return resultado;
    }
    
    
    
    
    
    
    
    
    
    
    
    
}
