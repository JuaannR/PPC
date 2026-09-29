package practica1;

import java.io.*;
import java.net.*;

public class Server {

    public static void obtenerPeticionCliente(Socket cliente) throws IOException {
        // 1. obtener flujo de bytes
        InputStream inputBytes = cliente.getInputStream();

        // 2. pasamos de bytes a caracteres legibles
        InputStreamReader lectorBytes = new InputStreamReader(inputBytes);

        // 3. lo metemos a BufferedReader para poder lineas enteras de texto facilmente
        BufferedReader lectorLineas = new BufferedReader(lectorBytes);

        // 4. leer lineas y mostrar en pantalla ->1 peticion GET y despues cabeceras
        String linea;

        while ((linea = lectorLineas.readLine()) != null && !linea.isEmpty()) {
            System.out.println(linea);
        }
    }

    public static void construirRespuesta(Socket cliente) throws IOException {
        // 1. obtenemos flujo de salida y creamos PrintWriter (true vacia el buffer)
        PrintWriter respuesta = new PrintWriter(cliente.getOutputStream(), true);

        // 2. escribimos la respuesta, acierto o error (suponemos acierto)
        respuesta.println("HTTP/1.1 200 OK");

        // 3. escribimos cabeceras
        respuesta.println("Content-Type: text/html; charset=UTF-8");

        // 4. linea en blanco para separar cabeceras con el cuerpo
        respuesta.println();

        // 5. Escribimos el cuerpo de la respuesta en HTML
        respuesta.println("<html>");
        respuesta.println("<head><title>ola</title></head>");
        respuesta.println("<body>");
        respuesta.println("<h1>Odio Java y la FIUM chicos</h1>");
        respuesta.println("<h1>Odio mucho a Maria :(</h1>");
        respuesta.println("<p>Si lees esto es porque funciona :)</p>");
        respuesta.println("</body>");
        respuesta.println("</html>");
        
        
    }


    public static void main(String[] args) {
        try {

            //servidor creado en puerto 8080
            ServerSocket server = new ServerSocket(8080);

            while (true) {

                // nos quedamos esperando a que alguien se conecte
                Socket cliente = server.accept();
                System.out.println("Nuevo cliente conectado");

                // una vez se conecta un cliente -> leemos petición
                
                obtenerPeticionCliente(cliente);

                // una vez terminamos de leer -> construimos la respuesta
                
                construirRespuesta(cliente);

                // cerrar conexión con cliente
                cliente.close();
                System.out.println("Conexión cerrada con el cliente");
                


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

