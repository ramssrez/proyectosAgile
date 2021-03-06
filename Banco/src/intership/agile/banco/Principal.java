package intership.agile.banco;

import intership.agile.banco.administradors.AdministradorCliente;
import intership.agile.banco.administradors.AdministradorProducto;
import intership.agile.banco.classhelp.Configuracion;
import intership.agile.banco.classhelp.Validacion;
import intership.agile.banco.handler.PropertyHandler;
import intership.agile.banco.interfaces.ProductoFinanciero;
import intership.agile.banco.model.Cliente;
import intership.agile.banco.model.CuentaCheques;
import intership.agile.banco.model.CuentaInversion;
import intership.agile.banco.model.TarjetaCredito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Principal {
    // Buena práctica de programación: Almacenar nombres de llaves en constantes estáticas
    private static final String PROP_USERNAME = "internship.agile.banco.username";
    private static final String PROP_PASSWORD = "internship.agile.banco.password";
    private static final String DEFAULT_PROPERTIES = "/application-default.properties";
    private static final String APPLICATIONS_PROPERTIES = "application.properties";
    private static final byte TRY_LIMIT = 3;
    private static List<Cliente> clientes = new ArrayList<>();
    private static AdministradorCliente administradorCliente = new AdministradorCliente();
    private static AdministradorProducto administradorProducto;
    private static Integer clienteGeneral;
    private static List<ProductoFinanciero> productoFinancieros;

    public static void main(String[] args) {
        clientes = administradorCliente.listaClientes();
        Configuracion configuracion = new Configuracion();
        configuracion.setMaxLineaCreditoPorIngresoMensual(4.0);
        administradorProducto = new AdministradorProducto(configuracion);
        clienteGeneral = 0;
        productoFinancieros =null;
        /*
        //Lineas de pruebas
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setIngresoMensual(18000);
        Cliente cliente1 = new Cliente();
        cliente1.setNombre("Jose");
        cliente1.setIngresoMensual(19000);
        Cliente cliente2 = new Cliente();
        cliente2.setNombre("Ivan");
        cliente2.setIngresoMensual(17000);
        clientes.add(cliente);
        clientes.add(cliente1);
        clientes.add(cliente2);
        TarjetaCredito tarjetaCredito = new TarjetaCredito(7000);
        CuentaInversion cuentaInversion = new CuentaInversion(1000.0, 0.05,0.15);
        CuentaCheques cuentaCheques = new CuentaCheques(20000,5.0);

        TarjetaCredito tarjetaCredito1 = new TarjetaCredito(8000);
        CuentaInversion cuentaInversion1 = new CuentaInversion(2000.0, 0.05,0.15);
        CuentaCheques cuentaCheques1 = new CuentaCheques(30000,5.0);

        TarjetaCredito tarjetaCredito2 = new TarjetaCredito(9000);
        CuentaInversion cuentaInversion2 = new CuentaInversion(3000.0, 0.05,0.15);
        CuentaCheques cuentaCheques2 = new CuentaCheques(40000,5.0);

        administradorProducto.agregarProducto(cliente1,tarjetaCredito);
        administradorProducto.agregarProducto(cliente1,cuentaCheques);
        administradorProducto.agregarProducto(cliente1,cuentaInversion);

        administradorProducto.agregarProducto(cliente2,tarjetaCredito2);
        administradorProducto.agregarProducto(cliente2,cuentaCheques2);
        administradorProducto.agregarProducto(cliente2,cuentaInversion2);

        administradorProducto.agregarProducto(cliente,tarjetaCredito1);
        administradorProducto.agregarProducto(cliente,cuentaCheques1);
        administradorProducto.agregarProducto(cliente,cuentaInversion1);
         */

        try {
            PropertyHandler.load(DEFAULT_PROPERTIES, APPLICATIONS_PROPERTIES);
            String username, password;
            System.out.println("Inicio de sesión...");

            boolean isLoggedIn = false;
            byte tryCount = 0;
            do {
                System.out.print("Usuario: ");
                username = System.console().readLine();
                System.out.print("Contraseña: ");
                // Se usa System.console() para tener la habilidad de ocultar el password.
                // El uso de System.console() puede tener resultados impredecibles en la terminal del IDE
                // Se recomienda ejecutar la aplicación desde una terminal real.
                password = new String(System.console().readPassword());
                if(Objects.equals(username, PropertyHandler.getStringProperty(PROP_USERNAME)) &&
                        Objects.equals(password, PropertyHandler.getStringProperty(PROP_PASSWORD)))
                    isLoggedIn = true;
                else
                    System.err.println("Usuario y contraseña incorrectos\n\n");
                tryCount++;
            } while (!isLoggedIn && tryCount < TRY_LIMIT);

            if(isLoggedIn) {
                System.out.printf("Bienvenido %s%n", username);
                runCommandListener();
            }
            else{
                System.err.println("Haz rebasado el límite para iniciar sesión, intentalo mas tarde");
            }
            PropertyHandler.persist();
            System.out.println("Programa finalizado");
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.printf("%s: %s%n", e.getClass().getName(), e.getMessage());
        }
    }
    private static void runCommandListener() {
        String command;
        do {
            System.out.print("$ ");
            command = System.console().readLine();
            // Buena práctica de programación: Encapsular la lógica de cada case en un una función separada
            switch (command) {
                case "ayuda":
                    printHelp();
                    break;
                case "crear-cliente":
                    crearCliente();
                    break;
                case "lista-clientes":
                    listaClientes();
                    break;
                case "buscar-cliente":
                    buscarCliente();
                    break;
                case "agregar-producto":
                    agregarProducto();
                    break;
                case "movimiento-productos":
                    movimientoProductos();
                    break;
                case "estado-cuenta":
                    ImpresionEstados();
                    break;
                case "exit":
                    // No recomendado: En la mayoría de los casos, no se recomienda invocar a System.exit(int)
                    //System.exit(0);
                    break;
                default:
                    System.err.printf("\"%s\" No es un comando%n", command);
            }
        } while(!"exit".equalsIgnoreCase(command));
    }
    private static void printHelp() {
        System.out.println("Lista de comandos disponibles ");
        System.out.println("- ayuda: Muestra los comandos disponibles en la aplicación\n" +
                "- crear-cliente: Crea un nuevo cliente.\n" +
                "- lista-clientes: Muestra los clientes con los que se cuenta.\n" +
                "- buscar-cliente: Busca a un cliente con su id.\n" +
                "- agregar-producto: Crea un producto financiero para el cliente\n" +
                "- movimiento-productos: Puede realizar movimientos en los productos\n" +
                "- estado-cuenta: Impresion del estado de cuenta del usuario\n" +
                "- exit");
    }
    private static void printcomandos() {
        System.out.println("Escribe un comando para agregar un nuevo producto ");
        System.out.println("- ayuda: Muestra los comandos disponibles en esta sección\n"+
                "- tarjeta-credito: Crear y agregar tarjeta de credito.\n" +
                "- cuenta-cheques: Crear y agregar cuenta de cheques.\n" +
                "- cuenta-inversion: Crear y agregar cuenta de inversión.\n" +
                "- exit: Salir del menú");
    }
    private static void tarjetaCreditoComandos() {
        System.out.println("Escribe un comando para las diferentes opciones de la tarjeta crédito ");
        System.out.println("- ayuda: Muestra los comandos disponibles en la aplicación\n"+
                "- pagar-tarjeta: Pagar tarjeta.\n" +
                "- cargar-tarjeta: Cargar tarjeta.\n" +
                "- estado-cuenta: Impresión de estado estado de cuenta.\n" +
                "- exit: Salir del menú");
    }
    private static void cuentaChequesComandos() {
        System.out.println("Escribe un comando para las diferentes opciones de la cuenta de cheques ");
        System.out.println("- ayuda: Muestra los comandos disponibles en la aplicación\n"+
                "- reducir-fondos: Realizar un retiro de la cuenta.\n" +
                "- agregar-fondos: Agrega fondos a la cuenta.\n" +
                "- estado-cuenta: Impresión de estado estado de cuenta.\n" +
                "- exit: Salir del menú");
    }
    private static void cuentaInversionComandos() {
        System.out.println("Escribe un comando para las diferentes opciones de la cuenta de cheques ");
        System.out.println("- ayuda: Muestra los comandos disponibles en la aplicación\n"+
                "- reducir-fondos: Realizar un retiro de la cuenta.\n" +
                "- agregar-fondos: Agrega fondos a la cuenta.\n" +
                "- aplicar-corte: Agrega fondos a la cuenta.\n" +
                "- estado-cuenta: Impresión de estado estado de cuenta.\n" +
                "- exit: Salir del menú");
    }
    private static void commandCuentaInversion(ProductoFinanciero productoFinanciero) {
        String command;
        do {
            System.out.print("inversion >- ");
            command = System.console().readLine();
            switch (command) {
                case "reducir-fondos":
                    reducirInversion((CuentaInversion) productoFinanciero);
                    break;
                case "agregar-fondos":
                    agregarInversion((CuentaInversion) productoFinanciero);
                    break;
                case "aplicar-corte":
                    corteInversion((CuentaInversion) productoFinanciero);
                    break;
                case "estado-cuenta":
                    impresionEstadoCuenta(productoFinanciero);
                    break;
                case "ayuda":
                    cuentaInversionComandos();
                    break;
                case "exit":
                    break;
                default:
                    System.err.printf("\"%s\" No es un comando%n", command);
            }
        } while(!"exit".equalsIgnoreCase(command));
        System.out.println("Retornando al menú principal....");
    }
    private static void commandCuentaCheques(ProductoFinanciero productoFinanciero) {
        String command;
        do {
            System.out.print("cheques >- ");
            command = System.console().readLine();
            switch (command) {
                case "reducir-fondos":
                    reducirCheques((CuentaCheques) productoFinanciero);
                    break;
                case "agregar-fondos":
                    agregarCheques((CuentaCheques) productoFinanciero);
                    break;
                case "estado-cuenta":
                    impresionEstadoCuenta(productoFinanciero);
                    break;
                case "ayuda":
                    cuentaChequesComandos();
                    break;
                case "exit":
                    break;
                default:
                    System.err.printf("\"%s\" No es un comando%n", command);
            }
        } while(!"exit".equalsIgnoreCase(command));
        System.out.println("Retornando al menú principal....");
    }
    private static void commandTarjetaCredito(ProductoFinanciero productoFinanciero) {
        String command;
        do {
            System.out.print("tarjeta >- ");
            command = System.console().readLine();
            switch (command) {
                case "pagar-tarjeta":
                    pagarTarjeta((TarjetaCredito) productoFinanciero);
                    break;
                case "cargar-tarjeta":
                    cargarTarjeta((TarjetaCredito) productoFinanciero);
                    break;
                case "estado-cuenta":
                    impresionEstadoCuenta(productoFinanciero);
                    break;
                case "ayuda":
                    tarjetaCreditoComandos();
                    break;
                case "exit":
                    break;
                default:
                    System.err.printf("\"%s\" No es un comando%n", command);
            }
        } while(!"exit".equalsIgnoreCase(command));
        System.out.println("Retornando al menú principal....");
    }
    private static void commandListenerProductos(Cliente cliente, int numero) {
        String command;
        do {
            System.out.println("Productos para " + cliente.getNombre());
            System.out.print("producto >- ");
            command = System.console().readLine();
            switch (command) {
                case "tarjeta-credito":
                    crearTarjeta(cliente);
                    break;
                case "cuenta-cheques":
                    crearCuentaCheques(cliente);
                    break;
                case "cuenta-inversion":
                    crearCuentaInversion(cliente);
                    break;
                case "ayuda":
                    printcomandos();
                    break;
                case "exit":
                    break;
                default:
                    System.err.printf("\"%s\" No es un comando%n", command);
            }
        } while(!"exit".equalsIgnoreCase(command));
        clientes.set(numero,cliente);
        System.out.println("Retornando al menú principal....");
    }
    private static void pagarTarjeta(TarjetaCredito credito){
        credito.pagarTarjeta(Validacion.validarDouble("Ingresa el pago de la tarjeta: "));
    }

    private static void cargarTarjeta(TarjetaCredito credito){
        credito.cargarTarjeta(Validacion.validarDouble("Ingresa el cargo de la tarjeta: "));
    }
    private static void impresionEstadoCuenta(ProductoFinanciero productoFinanciero){
        productoFinanciero.imprimirEstadoCuenta();
    }
    private static void reducirCheques(CuentaCheques cheques){
        cheques.reducirFondos(Validacion.validarDouble("Ingresa el monto de retiro: "));
    }
    private static void agregarCheques(CuentaCheques cheques){
        cheques.agregarFondos(Validacion.validarDouble("Ingresa el monto de ingreso: "));
    }
    private static void reducirInversion(CuentaInversion inversion){
        inversion.reducirFondos(Validacion.validarDouble("Ingresa el monto de retiro: "));
    }
    private static void agregarInversion(CuentaInversion inversion){
        inversion.agregarFondos(Validacion.validarDouble("Ingresa el monto de ingreso: "));
    }
    private static void corteInversion(CuentaInversion inversion){
        inversion.aplicarCorte();
    }

    private static ProductoFinanciero existeProducto( List<ProductoFinanciero> productoFinancieroList, String opcion){
        ProductoFinanciero productoFinanciero = null;
        for (ProductoFinanciero producto : productoFinancieroList){
            if (producto.obtenerNombreClase().equals(opcion)){
                productoFinanciero = producto;
                break;
            }
        }
        return productoFinanciero;
    }

    private static void ImpresionEstados() {
        List<ProductoFinanciero> productoFinancieroList = retornarlistaProductos();
        if (productoFinancieroList != null) {
            System.out.println("El cliente cuenta con los siguientes productos");
            System.out.println("El cliente " + clientes.get(clienteGeneral-1).getNombre() + " tiene");
            for (ProductoFinanciero producto : productoFinancieroList) {
                producto.imprimirEstadoCuenta();
            }
        }
        clienteGeneral =0;
    }
    private static void movimientoProductos(){
        List<ProductoFinanciero> productoFinancieroList = retornarlistaProductos();
        if (productoFinancieroList != null){
            System.out.println("El cliente cuenta con los siguientes productos");
            for (ProductoFinanciero producto: productoFinancieroList){
                System.out.println(producto.toString());
            }
            ProductoFinanciero productoFinanciero = null;
            String opcion = Validacion.validarString("Escribe un producto  por su nombre sin espacios y respetando mayusculas: ");
            try {
                productoFinanciero = existeProducto(productoFinancieroList,opcion);
                int numeroProducto = productoFinancieroList.indexOf(productoFinanciero);
                if (productoFinanciero instanceof TarjetaCredito){
                    tarjetaCreditoComandos();
                    commandTarjetaCredito(productoFinanciero);
                }
                if (productoFinanciero instanceof CuentaCheques){
                    cuentaChequesComandos();
                    commandCuentaCheques(productoFinanciero);
                }
                if (productoFinanciero instanceof CuentaInversion){
                    cuentaInversionComandos();
                    commandCuentaInversion(productoFinanciero);
                    System.out.println("Es una cuenta de inversion");
                }
                productoFinancieroList.set(numeroProducto,productoFinanciero);
                administradorProducto.setMapaProductos(clienteGeneral,productoFinancieroList);
                clienteGeneral = 0;
            }catch (NullPointerException  | ArrayIndexOutOfBoundsException e) {
                System.out.println("No existe el producto seleccionado");
            }
        }

    }
    private static void agregarProducto(){
        Cliente cliente = retornarCliente();
        if (cliente!= null){
            int numeroCliente = clientes.indexOf(cliente);
            printcomandos();
            commandListenerProductos(cliente, numeroCliente);
        }
    }
    private static void crearTarjeta(Cliente cliente){
        TarjetaCredito tarjetaCredito = new TarjetaCredito(Validacion.validarDouble("Ingresa la línea de crédito: "));
        administradorProducto.agregarProducto(cliente,tarjetaCredito);
        System.out.println("Se ha agregado una tarjeta de crédito");
        cliente.setTarjetaCredito(true);
    }
    private static void crearCuentaCheques(Cliente cliente){
        String balanceInicial = "Ingresa el balance inicial ";
        String comisionRetiro = "Ingresa la comisión del retiro ";
        CuentaCheques cuentaCheques = new CuentaCheques(Validacion.validarDouble(balanceInicial),Validacion.validarDouble(comisionRetiro));
        administradorProducto.agregarProducto(cliente,cuentaCheques);
        System.out.println("Se ha agregado una cuenta de cheques");
        cliente.setCuentaCheques(true);
    }
    private static void crearCuentaInversion(Cliente cliente){
        if(cliente.isCuentaCheques()){
            double balanceInicial = Validacion.validarDouble("Ingresa el balance inicial: ");
            double interesCorte = Validacion.validarDouble("Ingresa el interes al corte: ");
            double iva = Validacion.validarDouble("Ingresa el iva: ");
            CuentaInversion cuentaInversion = new CuentaInversion(balanceInicial,interesCorte,iva);
            administradorProducto.agregarProducto(cliente,cuentaInversion);
            System.out.println("Se ha agregado una cuenta de inversión");
            cliente.setCuentaInversion(true);
        }else{
            System.out.println("Se necesita una cuenta de cheques para poder tener una cuenta de inversion");
        }
    }
    private static Cliente retornarCliente() {
        int entero = Validacion.validarEntero("Ingresa el id del cliente: ");
        Cliente cliente = administradorCliente.getCliente(entero, clientes);
        if (cliente == null) {
            System.out.println("Cliente no encontrado");
        }
        return cliente;
    }
    private static List<ProductoFinanciero> retornarlistaProductos(){
        int entero = Validacion.validarEntero("Ingresa el id del cliente: ");
        clienteGeneral = entero;
        List<ProductoFinanciero> productoFinancieroList = null;
        try {
            productoFinancieroList = administradorProducto.getProductos(entero);
        }catch (NullPointerException e){
            System.out.println("El cliente aún no cuenta con productos");
        }
        return productoFinancieroList;
    }
    private static void buscarCliente(){
        int entero = Validacion.validarEntero("Ingresa el id del cliente: ");
        Cliente cliente = administradorCliente.getCliente(entero,clientes);
        if (cliente != null){
            System.out.println("Cliente encontrado");
            System.out.println(cliente.toString());
        }else{
            System.out.println("No se ha encontrado un cliente");
        }
    }
    private static void crearCliente(){
        Cliente cliente = new Cliente();
        cliente.setNombre(Validacion.validarString("Ingresa el nombre: "));
        cliente.setIngresoMensual(Validacion.validarDouble("Ingresa el sueldo mensual: "));
        agregarCliente(cliente);
        System.out.println("El cliente " + cliente.getNombre() + " ha sido creado");
    }
    private static void listaClientes(){
        if (!clientes.isEmpty()){
            impresionClientes();
        }else{
            System.out.println("No se cuenta con registro de clientes");
        }
    }
    private static void impresionClientes(){
        System.out.println("ID : Nombre"  );
        for (Cliente cliente : clientes){
            System.out.println(cliente.getIdCliente() + " : " + cliente.getNombre());
        }
    }
    private static void agregarCliente(Cliente cliente){
        administradorCliente.agregarCliente(cliente);
    }
}
