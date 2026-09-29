package practica1;
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
    try {
        System.out.println("Conectando...");

        // 1. conexión al servidor en puerto 8080
        Socket socket = new Socket("localhost", 8080);
        System.out.println("Conexion exitosa");

        // 2. preparar canal para enviar petición
        PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

        // escribimos la petición HTTP básica
        salida.println("GET / HTTP/1.1");
        salida.println("Host: localhost");
        salida.println(); // linea en blanco final fin petición

        // 3. canal para leer la respeusa del servidor
        InputStreamReader lectorBytes = new InputStreamReader(socket.getInputStream());
        BufferedReader lectorServer = new BufferedReader(lectorBytes);

        // 4. leer respuesta linea a linea
        String linea;
        while((linea = lectorServer.readLine()) != null) {
            System.out.println("Respuesta: " + linea);
        }

        // 5. cerrar conexion
        socket.close();
        System.out.println("Conexion cerrada");
        
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
