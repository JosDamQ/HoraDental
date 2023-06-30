Drop database if exists DBHoraDental2018163;
Create database DBHoraDental2018163;

Use DBHoraDental2018163;

Create table Pacientes(
	codigoPaciente int not null,
    nombresPaciente varchar(50) not null,
    apellidosPaciente varchar(50) not null,
    sexo char not null,
    fechaNacimiento date not null,
    direccionPaciente varchar(100) not null,
    telefonoPersonal varchar(8) not null,
    fechaPrimeraVisita date,
    primary key PK_codigoPaciente(codigoPaciente)
);

Create table Especialidades(
	codigoEspecialidad int not null auto_increment,
    descripcion varchar(100) not null,
    primary key PK_codigoEspecialidad(codigoEspecialidad)
);

Create table Medicamentos(
	codigoMedicamento int not null auto_increment,
    nombreMedicamento varchar(100) not null,
    primary key PK_codigoMedicamento(codigoMedicamento)
);

Create table Doctores(
	numeroColegiado int not null,
    nombresDoctor varchar(50) not null,
    apellidosDoctor varchar(50) not null,
    telefonoContacto varchar(8) not null,
    codigoEspecialidad int not null,
    primary key PK_numeroColegiado(numeroColegiado),
    constraint FK_Doctores_Especialidades foreign key (codigoEspecialidad)
		references Especialidades(codigoEspecialidad)
);

Create table Recetas(
	codigoReceta int not null auto_increment,
    fechaReceta date not null,
    numeroColegiado int not null,
    primary key PK_codigoReceta(codigoReceta),
    constraint FK_Recetas_Doctores foreign key(numeroColegiado)
		references Doctores (numeroColegiado)
);

Create table Citas(
	codigoCita int not null auto_increment,
    fechaCita date not null,
    horaCita time not null,
    tratamiento varchar(150),
    descripcioncondicionActual varchar(255) not null,
    codigoPaciente int not null,
    numeroColegiado int not null,
    primary key PK_codigoCita(codigoCita),
    constraint FK_Citas_Pacientes foreign key(codigoPaciente)
		references Pacientes (codigoPaciente),
	constraint FK_Citas_Doctores foreign key(numeroColegiado)
		references Doctores (numeroColegiado)
);

Create table DetalleReceta(
	codigoDetalleReceta int not null auto_increment,
    dosis varchar(100) not null,
    codigoReceta int not null,
    codigoMedicamento int not null,
    primary key PK_codigoDetalleReceta(codigoDetalleReceta),
    constraint FK_DetalleReceta_Recetas foreign key(codigoReceta)
		references Recetas (codigoReceta),
	constraint FK_DetalleReceta_Medicamentos foreign key(codigoMedicamento)
		references Medicamentos (codigoMedicamento)
);

-- ---------------------------------------------------------------------------------------------
-- -------------------------------Procedimientos almacenados------------------------------------
-- --------------------------------Pacientes---------------------------------------------------
-- -------------------------------Agregar--------------------------------------------------
Delimiter //
	Create procedure sp_AgregarPaciente(in codigoPaciente int,in nombresPaciente varchar(50),in apellidosPaciente varchar(50),
										in sexo char,in fechaNacimiento date,in direccionPaciente varchar(100), in telefonoPersonal varchar(8),
                                        in fechaPrimeraVisita date)
		Begin
			Insert into Pacientes (codigoPaciente,nombresPaciente,apellidosPaciente,sexo,
									fechaNacimiento,direccionPaciente,telefonoPersonal,fechaPrimeraVisita)
			values (codigoPaciente,nombresPaciente,apellidosPaciente,upper(sexo),fechaNacimiento,
					direccionPaciente,telefonoPersonal,fechaPrimeraVisita);
        End//
Delimiter ;

Call sp_AgregarPaciente(1001,'Josue Damian','Garcìa Quiñonez','m','2004-11-26','Zona 8','43482844',now());
Call sp_AgregarPaciente(1002,'Daniel Agusto','Ávila Ortiz','m','1995-3-4','Villa Nueva','23535678',now());
Call sp_AgregarPaciente(1003,'Sofia María','Ávila Ortiz','f','1999-12-6','Villa Nueva','54367688',now());
Call sp_AgregarPaciente(1004,'Joel Stever','Meldon Gutierrez','m','1977-4-30','Mixco','34566555',now());
Call sp_AgregarPaciente(1005,'Cindy Deciré','García Contreras','f','1986-2-11','El frutal 3','24408418',now());	
Call sp_AgregarPaciente(1006,'Bryan Stiver','García Contreras','m','1995-3-4','Guatemala, zona 1','67895462',now());
Call sp_AgregarPaciente(1007,'Tyron Rodrigo','Mendoza Navas','m','1988-12-14','Guatemala, zona 1','45321212',now());
Call sp_AgregarPaciente(1008,'Ana Lucia','Palermo Rodriguez','f','1995-5-29','Coban','34215666',now());
Call sp_AgregarPaciente(1009,'Bryn Stiver','Morales Lopez','m','2000-3-30','Guatemala, zona 6','98998476',now());
Call sp_AgregarPaciente(1010,'Francisco Pedro','Gonzales de la Vega','m','2000-6-30','Guatemala, zona 10','56437862',now());
-- -------------------------------Listar---------------------------------------------------------------------
Delimiter //
	Create procedure sp_ListarPacientes()
		Begin
			Select 
				P.codigoPaciente,
                P.nombresPaciente,
                P.apellidosPaciente,
                P.sexo,
                P.fechaNacimiento,
                P.direccionPaciente,
                P.telefonoPersonal,
                P.fechaPrimeraVisita
			From Pacientes as P;
        End //
Delimiter ;
Call sp_ListarPacientes();
-- -------------------------------Buscar------------------------------------------------
Delimiter //
	Create procedure sp_BuscarPaciente(in codPaciente int)
		Begin
			Select
  				P.codigoPaciente,
                P.nombresPaciente,
                P.apellidosPaciente,
                P.sexo,
                P.fechaNacimiento,
                P.direccionPaciente,
                P.telefonoPersonal,
                P.fechaPrimeraVisita
			From Pacientes as P where codigoPaciente = codPaciente;      
        End // 
Delimiter ;
-- Call sp_BuscarPaciente(1001);
-- -------------------------------Eliminar-----------------------------------------------
Delimiter //
	Create procedure sp_EliminarPaciente(in codPaciente int)
		Begin
			delete from Pacientes 
				where codigoPaciente = codPaciente;
        End //
Delimiter ;
-- Call sp_EliminarPaciente(1001);
-- Call sp_ListarPacientes();
-- -------------------------------Editar------------------------------------------------
Delimiter //
	create procedure sp_EditarPaciente(in codPaciente int,in nombPaciente varchar(50),in apPaciente varchar(50),
										in sx char,in fechaNac date,in dirPaciente varchar(100), in telPersonal varchar(8),
                                        in fechaPV date)
		Begin
			Update Pacientes as P
				set
					P.nombresPaciente = nombPaciente,
					P.apellidosPaciente = apPaciente,
					P.sexo = upper(sx),
					P.fechaNacimiento = fechaNac,
					P.direccionPaciente = dirPaciente,
					P.telefonoPersonal = telPersonal,
					P.fechaPrimeraVisita = fechaPV
                    where P.codigoPaciente = codPaciente;
        End//
Delimiter ;	
-- Call sp_EditarPaciente();
-- --------------------------------------------Especialidades-----------------------------------------------------------------------
-- -------------------------------------------Agregar-------------------------------------------------------------------------------
Delimiter //
	Create procedure sp_AgregarEspecialidad(in descripcion varchar(100))
		Begin
			Insert into Especialidades (descripcion)
            values(descripcion);
        End //
Delimiter ;
 Call sp_AgregarEspecialidad('Odontólogo');
 Call sp_AgregarEspecialidad('Odontopediatra');
 Call sp_AgregarEspecialidad('Ortodoncista');
 Call sp_AgregarEspecialidad('Periodoncista');
 Call sp_AgregarEspecialidad('Endodoncista');
 Call sp_AgregarEspecialidad('Prostodoncista');
 Call sp_AgregarEspecialidad('Cirujano oral');
 Call sp_AgregarEspecialidad('Especialista');
 Call sp_AgregarEspecialidad('Limpiador bucal');
 Call sp_AgregarEspecialidad('Endocionista II');
-- -------------------------------------------Listar----------------------------------------------------------------------------------
Delimiter //
	Create procedure sp_ListarEspecialidades()
		Begin
			Select 
				E.codigoEspecialidad,
				E.descripcion
			from Especialidades as E;
        End //       
Delimiter ;
 Call sp_ListarEspecialidades();
-- -------------------------------------------Buscar-------------------------------------------------------------------------------
Delimiter //
	Create procedure sp_BuscarEspecialidad(in codEspecialidad int)
		Begin
			Select 
				E.codigoEspecialidad,
                E.descripcion
			from Especialidades as E where codigoEspecialidad = codEspecialidad;
        End //
Delimiter ;
-- Call sp_BuscarEspecialidad(1);
-- --------------------------------------------------Eliminar-------------------------------------------------------------------
Delimiter //
	Create procedure sp_EliminarEspecialidad(in codEspecialidad int)
		Begin
			Delete from Especialidades 
				where codigoEspecialidad = codEspecialidad;
        End //
Delimiter ;
-- Call sp_EliminarEspecialidad(2);
-- -----------------------------------------------Editar--------------------------------------------------------------------------
Delimiter //
	Create procedure sp_EditarEspecialidad( in codEspecialidad int,in descrip varchar(100))
		Begin
			Update Especialidades as E
				set E.descripcion = descrip
                where E.codigoEspecialidad = codEspecialidad;
        End //
Delimiter ;
-- Call sp_EditarEspecialidad(1,'Odontologo');
-- ---------------------------------------------Medicamentos-----------------------------------------------------------------
-- --------------------------------------------Agregar-----------------------------------------------------------------
Delimiter //
	Create procedure sp_AgregarMedicamento(in nombreMedicamento varchar(100))
		Begin
			Insert into Medicamentos (nombreMedicamento)
            values (nombreMedicamento);
        End //
Delimiter ;
 Call sp_AgregarMedicamento('Tetraciclina');
 Call sp_AgregarMedicamento('Sulbactam');
 Call sp_AgregarMedicamento('Aspirina');
 Call sp_AgregarMedicamento('Tapcin');
 Call sp_AgregarMedicamento('Anestesia');
 Call sp_AgregarMedicamento('Tetraciclina');
 Call sp_AgregarMedicamento('Amoxcicilina');
 Call sp_AgregarMedicamento('Neurobion');
 Call sp_AgregarMedicamento('Alka D');
 Call sp_AgregarMedicamento('IRS');
-- ----------------------------------------------Listar-----------------------------------------------------------------
Delimiter //
	Create procedure sp_ListarMedicamentos()
		Begin
			Select 
				M.codigoMedicamento,
                M.nombreMedicamento
            from Medicamentos as M;    
        End // 
Delimiter ;
 Call sp_ListarMedicamentos();
-- -----------------------------------------------Buscar----------------------------------------------------------------------
Delimiter //
	Create procedure sp_BuscarMedicamento(in codMedicamento int)
		Begin
			Select 
				M.codigoMedicamento,
                M.nombreMedicamento
            from Medicamentos as M where M.codigoMedicamento = codMedicamento; 
        End //
Delimiter ;	
-- Call sp_BuscarMedicamento(1);
-- ---------------------------------------------Eliminar----------------------------------------------------------------
Delimiter //
	Create procedure sp_EliminarMedicamento(in codMedicamento int)
		Begin
			Delete from Medicamentos 
				where codigoMedicamento = codMedicamento;
        End //	
Delimiter ;
-- Call sp_EliminarMedicamento(2);
-- -----------------------------------------------Editar------------------------------------------------------------------
Delimiter //
	Create procedure sp_EditarMedicamento(in codMedicamento int,in nomMedicamento varchar(100))
		Begin
			Update Medicamentos as M
				set M.nombreMedicamento = nomMedicamento
                where M.codigoMedicamento = codMedicamento;   
        End //
Delimiter ;
-- Call sp_EditarMedicamento(1,'Aspirina');
-- ------------------------------------------------------Doctores--------------------------------------------------------------
-- ------------------------------------------------------Agregar--------------------------------------------------------------
Delimiter //
	Create procedure sp_AgregarDoctor(in numeroColegiado int,in nombresDoctor varchar(50),in apellidosDoctor varchar(50),
										in telefonoContacto varchar(8),in codigoEspecialidad int)
		Begin
			Insert into Doctores (numeroColegiado,nombresDoctor,apellidosDoctor,telefonoContacto,codigoEspecialidad)
            values(numeroColegiado,nombresDoctor,apellidosDoctor,telefonoContacto,codigoEspecialidad);
        End //
Delimiter ;
 Call sp_AgregarDoctor(1,'Andres Alejandro','Garcia Alvarado','78547834',1);
 Call sp_AgregarDoctor(2,'Mía Valentina','Contreras García','34652222',1);
 Call sp_AgregarDoctor(3,'Jaime David','Lopez Lorente','34563433',2);
 Call sp_AgregarDoctor(4,'Scarlet Rubi','Quiñonez de la Fuente','45643226',8);
 Call sp_AgregarDoctor(5,'Jaime David','Perez Rodríguez','12345678',9);
 Call sp_AgregarDoctor(6,'Kevin Estuardo','Lopez Lorente','87654321',2);
 Call sp_AgregarDoctor(7,'Mía Sofía','Contreras García','34652222',4);
 Call sp_AgregarDoctor(8,'Diego Julio','Hernandez Albeño','56385672',4);
 Call sp_AgregarDoctor(9,'Marco Tulio','Hernandez Albeño','56385672',6);
 Call sp_AgregarDoctor(10,'Cristel María','Herrera Castro','56385672',7);
-- ------------------------------------------------------Listar--------------------------------------------------------------
Delimiter //
	Create procedure sp_ListarDoctores()
		Begin
			Select 
				D.numeroColegiado,
                D.nombresDoctor,
                D.apellidosDoctor,
                D.telefonoContacto,
                D.codigoEspecialidad
			from Doctores as D;
        End // 
Delimiter ;
-- Call sp_ListarDoctores();
-- -----------------------------------------------------Buscar----------------------------------------------------------------
Delimiter //
	Create procedure sp_BuscarDoctor(in numColegiado int)
		Begin
			Select 
				D.numeroColegiado,
                D.nombresDoctor,
                D.apellidosDoctor,
                D.telefonoContacto,
                D.codigoEspecialidad
			from Doctores as D where D.numeroColegiado = numColegiado;
        End // 
Delimiter ;
-- Call sp_BuscarDoctor(1);
-- ------------------------------------------------------Eliminar-------------------------------------------------------------
Delimiter //
	Create procedure sp_EliminarDoctor(in numColegiado int)
		Begin
			Delete from Doctores 
				where numeroColegiado = numColegiado;
        End //
Delimiter ;
-- Call sp_EliminarDoctor(2);
-- ------------------------------------------------------Editar----------------------------------------------------------
Delimiter //
	Create procedure sp_EditarDoctor(in numColegiado int, in nomDoctor varchar(50),in apeDoctor varchar(50),in telContacto int,in codEspecialidad int)
		Begin 
			Update Doctores as D
				set D.numeroColegiado = numColegiado,
					D.nombresDoctor = nomDoctor,
                    D.apellidosDoctor = apeDoctor,
                    D.telefonoContacto = telContacto,
                    D.codigoEspecialidad = codEspecialidad
				where numeroColegiado = numColegiado;
        End //
Delimiter ;
-- Call sp_EditarDoctor(1,'Cristian Davida','Lopez Albeño', '12345678',2);
-- -------------------------------------------------Recetas-----------------------------------------------------------------------
-- ---------------------------------------------------Agregar------------------------------------------------------------------------
Delimiter //
	Create procedure sp_AgregarReceta(in fechaReceta date,in numeroColegiado int)
		Begin
			Insert into Recetas(fechaReceta,numeroColegiado)
            values(fechaReceta,numeroColegiado);
        End //
Delimiter ;
 Call sp_AgregarReceta('2022-11-3',1);
 Call sp_AgregarReceta('2013-11-3',6);
 Call sp_AgregarReceta('2007-7-21',1);
 Call sp_AgregarReceta('2019-9-3',3);
 Call sp_AgregarReceta('2022-11-3',2);
 Call sp_AgregarReceta('2015-1-30',3);
 Call sp_AgregarReceta('2020-11-3',9);
 Call sp_AgregarReceta('2021-11-3',4);
 Call sp_AgregarReceta('2018-11-3',1);
 Call sp_AgregarReceta('2022-2-3',5);
-- ---------------------------------------------------Listar-------------------------------------------------------------------------}
Delimiter //
	Create procedure sp_ListarRecetas()
		Begin
			Select 
				R.codigoReceta,
                R.fechaReceta,
                R.numeroColegiado
			from Recetas as R;
        End //
Delimiter ;
 Call sp_ListarRecetas();
-- ---------------------------------------------------Buscar------------------------------------------------------------------------
Delimiter //
	Create procedure sp_BuscarReceta(in codReceta  int)
		Begin
			Select 
				R.codigoReceta,
                R.fechaReceta,
                R.numeroColegiado
			from Recetas as R where R.codigoReceta = codReceta;
        End //
Delimiter ;
-- Call sp_BuscarReceta(1);
-- ---------------------------------------------------Eliminar-----------------------------------------------------------------------
Delimiter // 
	Create procedure sp_EliminarReceta(in codReceta int)
		Begin
			Delete from Recetas 
				where codigoReceta = codReceta;
        End //
Delimiter ;
-- Call sp_EliminarReceta(1);
-- ---------------------------------------------------Editar-------------------------------------------------------------------------
Delimiter //
	Create procedure sp_EditarReceta(in codReceta int, in feReceta date,in numColegiado int)
		Begin
			Update Recetas as R
				Set R.fechaReceta = feReceta,
					R.numeroColegiado = numColegiado
				where R.codigoReceta = codReceta;
        End //
Delimiter ;
-- Call sp_EditarReceta(2,'2021-4-5',1);
-- --------------------------------------------------Citas-------------------------------------------------------------------------
-- ---------------------------------------------------Agregar------------------------------------------------------------------------
Delimiter //
	Create procedure sp_AgregarCita(in fechaCita date, in horaCita time,in tratamiento varchar(150),
									in descripcioncondicionActual varchar(255),in codigoPaciente int,in numeroColegiado int)
		Begin
			Insert into Citas (fechaCita,horaCita,tratamiento,descripcioncondicionActual,codigoPaciente,numeroColegiado)
            values (fechaCita,horaCita,tratamiento,descripcioncondicionActual,codigoPaciente,numeroColegiado);
        End //
Delimiter ;
 Call sp_AgregarCita('2019-4-21','10:11','Lavar dientes','Dientes en mal estado',1001,1);
 Call sp_AgregarCita('2022-4-21','8:54','Usar muela de repuesto','Muela suelta',1004,5);
 Call sp_AgregarCita('2011-5-12','6:22','Usar muela de repuesto','Muela suelta',1003,2);
-- ---------------------------------------------------Listar-------------------------------------------------------------------------
Delimiter //
	Create procedure sp_ListarCitas()
		Begin
			Select 
				C.codigoCita,
                C.fechaCita,
                C.horaCita,
                C.tratamiento,
                C.descripcioncondicionActual,
                C.codigoPaciente,
                C.numeroColegiado
			from Citas as C;
        End //
Delimiter ;
-- Call sp_ListarCitas();
-- ---------------------------------------------------Buscar------------------------------------------------------------------------
Delimiter //
	Create procedure sp_BuscarCita(in codCita int)
		Begin
			Select 
				C.codigoCita,
                C.fechaCita,
                C.horaCita,
                C.tratamiento,
                C.descripcioncondicionActual,
                C.codigoPaciente,
                C.numeroColegiado
			from Citas as C where C.codigoCita = codCita;
        End //
Delimiter ;
-- Call sp_BuscarCita(2);
-- ---------------------------------------------------Eliminar-----------------------------------------------------------------------
Delimiter //
	Create procedure sp_EliminarCita(in codCita int)
		Begin
			Delete from Citas 
				where codigoCita = codCita;
        End //
Delimiter ;
-- Call sp_EliminarCita(2);
-- ---------------------------------------------------Editar-------------------------------------------------------------------------
Delimiter //
	Create procedure sp_EditarCita(in codCita int,in feCita date,in hoCita time,in trata varchar(150),
									in descripcondicionActual varchar(255),in codPaciente int,in numColegiado int)
		Begin
			Update Citas as C
				Set C.fechaCita = feCita,
					C.horaCita = hoCita,
                    C.tratamiento = trata,
                    C.descripcioncondicionActual = descripcondicionActual,
                    C.codigoPaciente = codPaciente,
                    C.numeroColegiado = numColegiado
				where C.codigoCita = codCita;
        End //
Delimiter ;
-- Call sp_EditarCita(1,'2003-7-30','3:45:32','Limpieza bucalezzzzzz','Aplicar pasta especializada',1001,1);
-- ---------------------------------------------------DetalleReceta-------------------------------------------------------------
-- ---------------------------------------------------Agregar------------------------------------------------------------------------
Delimiter //
	Create procedure sp_AgregarDetalleReceta(in dosis varchar(100),in codigoReceta int, in codigoMedicamento int)
		Begin
			Insert into DetalleReceta (dosis,codigoReceta,codigoMedicamento)
            values(dosis,codigoReceta,codigoMedicamento);
        End //
Delimiter ;
 Call sp_AgregarDetalleReceta('1 vez al día',1,2);
 Call sp_AgregarDetalleReceta('2 veces al día',2,1);
 Call sp_AgregarDetalleReceta('1 vez al día',3,3);
 Call sp_AgregarDetalleReceta('1 vez al día',1,3);
 Call sp_AgregarDetalleReceta('1 vez al día',7,5);
 Call sp_AgregarDetalleReceta('3 veces al día',8,4);
 Call sp_AgregarDetalleReceta('3 veces al día',3,7);
 Call sp_AgregarDetalleReceta('1 vez al día',2,9);
 Call sp_AgregarDetalleReceta('en el transcurso de la tarde',6,1);
 Call sp_AgregarDetalleReceta('1 vez al día',6,2);
-- ---------------------------------------------------Listar-------------------------------------------------------------------------
Delimiter //
	Create procedure sp_ListarDetalleRecetas()
		Begin
			Select
				D.codigoDetalleReceta,
                D.dosis,
                D.codigoReceta,
                D.codigoMedicamento
			from DetalleReceta as D;
        End //
Delimiter ;
-- Call sp_ListarDetalleRecetas();
-- ---------------------------------------------------Buscar------------------------------------------------------------------------
Delimiter //
	Create procedure sp_BuscarDetalleReceta(in codDetalleReceta int)
		Begin
			Select
				D.codigoDetalleReceta,
                D.dosis,
                D.codigoReceta,
                D.codigoMedicamento
			from DetalleReceta as D where D.codigoDetalleReceta = codDetalleReceta;
        End //
Delimiter ;
-- Call sp_BuscarDetalleReceta(1);
-- ---------------------------------------------------Eliminar-----------------------------------------------------------------------
Delimiter //
	Create procedure sp_EliminarDetalleReceta(in codDetalleReceta int)
		Begin
			Delete from DetalleReceta 
				where codigoDetalleReceta = codDetalleReceta;
        End //
Delimiter ;
-- Call sp_EliminarDetalleReceta(4);
-- ---------------------------------------------------Editar-------------------------------------------------------------------------
Delimiter //
	Create procedure sp_EditarDetalleReceta(in codDetalleReceta int,in dos varchar(100),in codReceta int,in codMedicamento int)
		Begin
			Update DetalleReceta as D
				set D.dosis = dos,
					D.codigoReceta = codReceta,
                    D.codigoMedicamento = codMedicamento
				where D.codigoDetalleReceta = codDetalleReceta;
        End //
Delimiter ;
-- Call sp_EditarDetalleReceta(2,'Usar protector bucal',2,1);
-- ------------------------------------------------------------------------------------
ALTER USER 'root'@'localhost'identified with mysql_native_password by '12345';

-- ----------------------------------------Login-----------------------------
Create table Usuario(
	codigoUsuario int not null auto_increment,
    nombreUsuario varchar(100) not null,
    apellidoUsuario varchar(100) not null,
    usuarioLogin varchar(50) not null,
    contrasena varchar(50),
    primary key PK_codigoUsuario (codigoUsuario)
);
-- ----------------------------------------------Agregar----------------------------------------------------
Delimiter //
Create procedure sp_AgregarUsuario(in nombreUsuario varchar(100), in apellidoUsuario varchar(100), 
									in usuarioLogin varchar(50), in contrasena varchar(50))
		Begin
			insert into Usuario(nombreUsuario, apellidoUsuario,usuarioLogin,contrasena)
            values(nombreUsuario, apellidoUsuario, usuarioLogin, contrasena);
        End//
Delimiter ;
Call sp_AgregarUsuario('Josue','García', 'jGarcia','@123');
-- ------------------------------------------Listar---------------------------------------------------
Delimiter //
Create procedure sp_ListarUsuarios()
	Begin
		Select  U.codigoUsuario,
				U.nombreUsuario,
				U.apellidoUsuario,
                U.usuarioLogin,
                U.contrasena
		from Usuario as U;
    End//
Delimiter ;
Call sp_ListarUsuarios();
-- -------------------------------------------------------------------------
Create table Login(
	usuarioMaster varchar(50) not null,
    passwordLogin varchar(50) not null,
    primary key PK_usuarioMaster(UsuarioMaster)
);




