package practica1;

import java.io.*;
import java.net.*;

public class Server {

    // Método para enviar un error 400 (Bad Request)
    public static void enviarError400(Socket cliente) throws IOException {
        PrintWriter respuesta = new PrintWriter(cliente.getOutputStream(), true);

        // 1. Línea de estado con el código de error
        respuesta.println("HTTP/1.1 400 Bad Request");

        // 2. Cabeceras
        respuesta.println("Server: ServidorJava-PPC/1.0");
        respuesta.println("Content-Type: text/html; charset=UTF-8");
        respuesta.println("Connection: close");
        respuesta.println(); // Línea en blanco obligatoria

        // 3. Cuerpo HTML explicativo
        respuesta.println("<html>");
        respuesta.println("<head><title>Error 400 - Petición Incorrecta</title></head>");
        respuesta.println("<body>");
        respuesta.println("<h1>400 Bad Request</h1>");
        respuesta.println("<p>La petición enviada está mal formada o no es válida.</p>");
        respuesta.println("</body>");
        respuesta.println("</html>");
    }

    // Método para enviar un error 404 (Not Found)
    public static void enviarError404(Socket cliente) throws IOException {
        PrintWriter respuesta = new PrintWriter(cliente.getOutputStream(), true);

        // 1. Línea de estado con el código de error
        respuesta.println("HTTP/1.1 404 Not Found");

        // 2. Cabeceras
        respuesta.println("Server: ServidorJava-PPC/1.0");
        respuesta.println("Content-Type: text/html; charset=UTF-8");
        respuesta.println("Connection: close");
        respuesta.println(); // Línea en blanco obligatoria

        // 3. Cuerpo HTML explicativo
        respuesta.println("<html>");
        respuesta.println("<head><title>Error 404 - No Encontrado</title></head>");
        respuesta.println("<body>");
        respuesta.println("<h1>404 Not Found</h1>");
        respuesta.println("<p>El recurso o ruta solicitada no existe en este servidor.</p>");
        respuesta.println("</body>");
        respuesta.println("</html>");
    }    

    public static boolean obtenerPeticionCliente(Socket cliente) throws IOException {
        // 1. obtener flujo de bytes
        InputStream inputBytes = cliente.getInputStream();

        // 2. pasamos de bytes a caracteres legibles
        InputStreamReader lectorBytes = new InputStreamReader(inputBytes);

        // 3. lo metemos a BufferedReader para poder lineas enteras de texto facilmente
        BufferedReader lectorLineas = new BufferedReader(lectorBytes);

        // 4. leer peticion GET y despues cabeceras
        String lineaPeticion = lectorLineas.readLine();

        // comprobamos que se ha enviado una peticion
        if (lineaPeticion == null || lineaPeticion.isEmpty()) {
            System.out.println("No se ha enviado una peticion");
            enviarError400(cliente);
            return false;
        }

        //peticion es algo: "GET / HTTP/1.1"  ---> separamos por " " y procesamos
        String[] partesPeticion = lineaPeticion.split(" ");

        // COMPROBACIONES
        // comprobamos que hay 3 partes en la peticion
        if (partesPeticion.length != 3) {
            System.out.println("La peticion no tiene un formato esperado");
            enviarError400(cliente);
            return false;
        }

        // comprobamos metodo soportado
        if (!partesPeticion[0].equals("GET") && !partesPeticion[0].equals("POST")) {
            System.out.println("Metodo no soportado, solo GET o POST");
            enviarError400(cliente);
            return false;
        }

        // comprobamos version HTTP/1.0 o 1.1
        if (!partesPeticion[2].equals("HTTP/1.0") && !partesPeticion[2].equals("HTTP/1.1")) {
            System.out.println("Version no soportada, solo 1.0 o 1.1");
            enviarError400(cliente);
            return false;
        }

        // comprobamos recurso al que accedemos, de momento solo /
        if (!partesPeticion[1].equals("/")) {
            System.out.println("El recurso solicitado no existe");
            enviarError404(cliente);
            return false;
        }

        String metodo = partesPeticion[0];  // GET o POST
        String url = partesPeticion[1];     //   /
        String version = partesPeticion[2]; // HTTP/1.1 o 1.0
        System.out.println("Metodo: " + metodo);
        System.out.println("Url: " + url);
        System.out.println("Version: " + version);

        String linea;

        while ((linea = lectorLineas.readLine()) != null && !linea.isEmpty()) {
            System.out.println(linea);
        }

        return true;
    }

    public static void construirRespuesta(Socket cliente) throws IOException {
        // 1. obtenemos flujo de salida y creamos PrintWriter (true vacia el buffer)
        PrintWriter respuesta = new PrintWriter(cliente.getOutputStream(), true);

        // 2. escribimos la respuesta, acierto o error (suponemos acierto)
        respuesta.println("HTTP/1.1 200 OK");

        // 3. escribimos cabeceras
        respuesta.println("Server: ServidorJava-PPC/1.0");
        respuesta.println("Content-Type: text/html; charset=UTF-8");
        respuesta.println("Connection: close");

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
                // dentro de if porque obtenerPeticionCliente es bool
                // Solo mandamos respuesta DESDE AQUI si es 200 OK
                // si es error se hace desde dentro del propio metodo
                
                if (obtenerPeticionCliente(cliente)) {
                    // una vez terminamos de leer -> construimos la respuesta            
                    construirRespuesta(cliente);
                }

                // cerrar conexión con cliente
                cliente.close();
                System.out.println("Conexion cerrada con el cliente");
                


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

