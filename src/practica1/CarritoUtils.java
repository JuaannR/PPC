package practica1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarritoUtils {

    // pasar una cadena de IDs separados por comas ("1,1,2,3,3,4")
    // y devuelve un mapa relacionando el ID del producto con su cantidad {1:2, 2:1, 3:2, 4:1}

    public static Map<Integer, Integer> parsearCarrito(String valorCookie) {
        Map<Integer, Integer> itemsCarrito = new HashMap<>();

        if (valorCookie == null || valorCookie.trim().isEmpty()) {
            return itemsCarrito; //si no hay cookie o está vacia, devolvemos mapa vacio
        }

        // separamos la cadena por comas. guardamos en array list
        List<String> listaIds = new ArrayList<>(Arrays.asList(valorCookie.split(",")));
        
        for (String idStr : listaIds) {
            
            if(idStr.trim().isEmpty()) {
                continue;  //saltamos elementos vacios sin lanzar excepciones
            }

            try {
                int idInt = Integer.parseInt(idStr.trim());

                if (Catalogo.obtenerProductoId(idInt) != null) {
                    itemsCarrito.put(idInt, itemsCarrito.getOrDefault(idInt, 0) + 1);
                }
            } catch (NumberFormatException e) {
                System.err.print("ID de carrito no válido encontrado: " + idStr);
            }
        }

        return itemsCarrito;

    }
    
}
