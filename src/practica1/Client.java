package practica1;
import java.io.*;
import java.net.*;

public class Client {

    public static void enviarPeticionServer(Socket socket) throws IOException {
        // 1. preparar canal para enviar peticion
        PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

        // peticion HTTP basica
        salida.println("GET / HTTP/1.1");
        // cabeceras peticion
        salida.println("Host: localhost");
        salida.println("User-Agent: Mozilla/5.0 (Java-Client");
        salida.println("Accept: text/html");
        salida.println("Connection: close");
        salida.println(); // linea en blanco final fin petición        
    }

    public static void leerRespuestaServer(Socket socket) throws IOException {
        // 1. canal para leer la respuesta del servidor
        InputStreamReader lectorBytes = new InputStreamReader(socket.getInputStream());
        BufferedReader lectorServer = new BufferedReader(lectorBytes);

        // 2. leer respuesta linea a linea
        String linea;
        while((linea = lectorServer.readLine()) != null) {
            System.out.println("Respuesta: " + linea);
        }        
    }


    public static void main(String[] args) {
    try {
        System.out.println("Conectando...");

        // conexión al servidor en puerto 8080
        Socket socket = new Socket("localhost", 8080);
        System.out.println("Conexion exitosa");

        // enviamos peticion
        enviarPeticionServer(socket);

        // leemos la respuesta
        leerRespuestaServer(socket);

        // cerramos conexion
        socket.close();
        System.out.println("Conexion cerrada");
        
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
