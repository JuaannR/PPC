package practica1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    // variable global eliminada, prueba termianda
    //variable global para guardar el mapa del carrito del cliente actual <idProducto, cantidad>
    //private static Map<Integer, Integer> carritoActual = new HashMap<>();

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

    // Convierte el Map del carrito en una cadena de IDs separados por comas para la cookie
    public static String generarValorCookie(Map<Integer, Integer> carrito) {
        StringBuilder sb = new StringBuilder();
        boolean primero = true;
        for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
            int id = entry.getKey();
            int cantidad = entry.getValue();
            for(int i = 0; i < cantidad; i++) {
                if (!primero) {
                    sb.append(",");
                }
                sb.append(id);
                primero = false;
            }
        }
        return sb.toString();
    }

    public static Map<Integer, Integer> obtenerPeticionCliente(Socket cliente) throws IOException {
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
            return null;
        }

        //peticion es algo: "GET / HTTP/1.1"  ---> separamos por " " y procesamos
        String[] partesPeticion = lineaPeticion.split(" ");

        // COMPROBACIONES
        // comprobamos que hay 3 partes en la peticion
        if (partesPeticion.length != 3) {
            System.out.println("La peticion no tiene un formato esperado");
            enviarError400(cliente);
            return null;
        }


        // comprobamos metodo soportado
        if (!partesPeticion[0].equals("GET") && !partesPeticion[0].equals("POST")) {
            System.out.println("Metodo no soportado, solo GET o POST");
            enviarError400(cliente);
            return null;
        }

        // comprobamos version HTTP/1.0 o 1.1
        if (!partesPeticion[2].equals("HTTP/1.0") && !partesPeticion[2].equals("HTTP/1.1")) {
            System.out.println("Version no soportada, solo 1.0 o 1.1");
            enviarError400(cliente);
            return null;
        }

        // partesPeticion               [0]            [1]        [2]
        // al hacer click en añadir --> GET /?accion=anadir&id=1 HTTP/1.1
        // Separar recurso (ej: "/") de los parametros de la URL (ej: "accion=anadir&id=1")

        String recursoCompleto = partesPeticion[1];  
        String ruta = recursoCompleto;
        String queryString = "";

        // separamos por el ?
        if (recursoCompleto.contains("?")) {
            String[] partesUrl = recursoCompleto.split("\\?", 2);
            ruta = partesUrl[0];  // ruta base, que debe ser "/"
            queryString = partesUrl[1];  // "accion=anadir&id=1"
        }

        // Comrpobamos que la ruta base sea estrictamente "/"
        if (!ruta.equals("/")) {
            enviarError404(cliente);
            return null;
        }

        //variables para extraer la acción solicitada por el usuario
        String accion = null;
        int idProductoParam = -1;

        if (!queryString.isEmpty()) {
            String[] parametros = queryString.split("&"); 

            //parametros[0] = "accion=anadir"
            //parametros[0] = "id=1"

            for (String param : parametros) {
                String[] par = param.split("=");

                //par[0] = "accion" -- "id"
                //par[1] = "anadir" -- "1"

                if (par[0].equals("accion")) {
                    accion = par[1];  // anadir 
                } else if (par[0].equals("id")) {
                    try {
                        idProductoParam = Integer.parseInt(par[1]);  //convierte id a numero entero
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }


        // leemos cabeceras buscando la cookie
        String linea;
        String cookieRecibida = null; //variable para guardar la cookie si existe

        while ((linea = lectorLineas.readLine()) != null && !linea.isEmpty()) {
            System.out.println(linea);

            // Si la línea empieza por "Cookie:, la caputramos"
            if (linea.startsWith("Cookie:")) {
                cookieRecibida = linea.substring("Cookie:".length()).trim();
            } 
        }

        // recuperamos el carrito actual desde la cookie recibida
        Map<Integer, Integer> carritoActual = new HashMap<>();
        
        if (cookieRecibida != null) {
            System.out.println("Cookie detectada, valor: " + cookieRecibida);

            // limpiamos la parte de "carrito=" si la trae
            String cookieLimpia = cookieRecibida;
            if (cookieLimpia.contains("carrito=")) {
                String[] partesCookie = cookieRecibida.split("=");
                if (partesCookie.length > 1 ) {
                    cookieLimpia = partesCookie[1].trim();
                }
            }

            carritoActual = CarritoUtils.parsearCarrito(cookieLimpia);
            System.out.println("Carrito parseado con exito: " + carritoActual);

        } 

        // Modificamos el carrito segun la accion que haya indicado el usuario
        if (accion != null && idProductoParam != -1) {
            if (accion.equals("anadir")) {
                //sumamos una unidad al producto indicado
                carritoActual.put(idProductoParam, carritoActual.getOrDefault(idProductoParam, 0) + 1);
                System.out.println("Accion: Anadido producto ID " + idProductoParam);
            } else if (accion.equals("eliminar")) {
                if (carritoActual.containsKey(idProductoParam)) {
                    int cantidadActual = carritoActual.get(idProductoParam);
                    if (cantidadActual > 1) {
                        carritoActual.put(idProductoParam, cantidadActual - 1);
                    } else {
                        carritoActual.remove(idProductoParam);
                    }
                    System.out.print("Accion: Eliminado 1 producto ID " + idProductoParam);
                }
            }

            // NUEVO: Redireccion HTTP 303 para limipar la URL y evitar bug de recarga de pagian
            String valorCookieNuevo = generarValorCookie(carritoActual);
            PrintWriter respuesta = new PrintWriter(cliente.getOutputStream(),true);
            respuesta.println("HTTP/1.1 303 See Other");
            respuesta.println("Server: ServidorJava-PPC/1.0");
            respuesta.println("Set-Cookie: carrito=" + valorCookieNuevo + "; Path=/; HttpOnly");
            respuesta.println("Location: /"); // Redirige a la raíz limpia
            respuesta.println("Connection: close");
            respuesta.println();
            
            // deolver null para que main no intente construir la respuesta HTML normal
            return null;
        }

        return carritoActual;
    }

    public static void construirRespuesta(Socket cliente, Map<Integer, Integer> carritoActual) throws IOException {
        // obtenemos flujo de salida y creamos PrintWriter (true vacia el buffer)
        PrintWriter respuesta = new PrintWriter(cliente.getOutputStream(), true);

        //generamos el valora actualizado de la cookie a partir del mapa actual
        String valorCookieNuevo = generarValorCookie(carritoActual);

        // escribimos la respuesta y cabeceras
        respuesta.println("HTTP/1.1 200 OK");
        respuesta.println("Server: ServidorJava-PPC/1.0");
        respuesta.println("Content-Type: text/html; charset=UTF-8");

        // cabecera Cookie, enviamos la nueva cookie actualizada al navegador
        respuesta.println("Set-Cookie: carrito=" + valorCookieNuevo + "; Path=/; HttpOnly");
        respuesta.println("Connection: close");

        // linea en blanco para separar cabeceras con el cuerpo
        respuesta.println();
        
        // Cuerpo HTML
        respuesta.println("<html>");
        respuesta.println("<head><title>Tienda Online - Carrito PPC</title></head>");
        respuesta.println("<body>");
        respuesta.println("<h1>Tienda Online (Practica 1 PPC)</h1>");

        // 1. SECCIÓN CATALOGO
        respuesta.println("<h2>Catálogo de Productos</h2>");
        respuesta.println("<ul>");
        // Recorremos todos los productos disponibles en el catálogo global
        for (Map.Entry<Integer, Producto> entry : Catalogo.obtenerTodosProductos().entrySet()) {
            Producto p = entry.getValue();
            respuesta.println("<li>");
            respuesta.println(p.getNombre() + " - " + p.getPrecio() + "€ ");
            // Enlace para añadir este producto específico (llama a la URL con parámetros)
            respuesta.println("<a href='/?accion=anadir&id=" + p.getId() + "'>[+] Añadir</a> ");
            // Enlace para eliminar este producto específico
            respuesta.println("<a href='/?accion=eliminar&id=" + p.getId() + "'>[-] Quitar</a>");
            respuesta.println("</li>");
        }
        respuesta.println("</ul>");

        // 2. SECCIÓN DEL CARRITO DE COMPRA

        respuesta.println("<h2>Tu Carrito de la compra</h2>");

        // Recorremos el mapa carritoActual para pintar los productos
        double granTotal = 0.0;

        if (carritoActual.isEmpty()) {
            respuesta.println("<p>El carrito está vacío.</p>");
        } else {
            respuesta.println("<ul>");
            for (Map.Entry<Integer, Integer> entry : carritoActual.entrySet()) {
                int idProducto = entry.getKey();
                int cantidad = entry.getValue();

                //obtenemos el producto del catalogo
                Producto producto = Catalogo.obtenerProductoId(idProducto);

                if (producto != null) {
                    double subtotal = producto.getPrecio() * cantidad;
                    granTotal = granTotal + subtotal;

                    String subtotalFormateado = String.format(java.util.Locale.US, "%.2f", subtotal);
                    

                    respuesta.println("<li>" + producto.getNombre() + " x " + cantidad + " unidades — Subtotal: " + subtotalFormateado + "€</li>");
                }

            }
            String granTotalFormateado = String.format(java.util.Locale.US, "%.2f", granTotal);
            respuesta.println("</ul>");
            respuesta.println("<h3>Precio Total: " + granTotalFormateado + "€</h3>"); 
        }

        respuesta.println("</body>");
        respuesta.println("</html>");        
    }



    public static void main(String[] args) {
        try {

            //servidor creado en puerto 8080
            ServerSocket server = new ServerSocket(8080);
            System.out.println("Servidor HTTP iniciado en el puerto 8080...");

            while (true) {

                // nos quedamos esperando a que alguien se conecte
                Socket cliente = server.accept();
                System.out.println("Nuevo cliente conectado");
                
                // Lanzamos hilo independeinte para cada cliente
                new Thread(() -> {
                    try {

                // procesar peticion y obtener mapa del carrito actual
                Map<Integer, Integer> carritoCliente = obtenerPeticionCliente(cliente);

                // si el mapa no es null (no hubi error 400/404) construimos respuesta enviando carrito
                if (carritoCliente != null) {
                    construirRespuesta(cliente, carritoCliente);
                }

                // cerrar conexión con cliente
                cliente.close();
                System.out.println("Conexion cerrada con el cliente");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

