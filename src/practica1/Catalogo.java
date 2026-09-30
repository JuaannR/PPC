package practica1;

import java.util.HashMap;
import java.util.Map;

public class Catalogo {

    public static Map<Integer, Producto> productos = new HashMap<>();
    
    public static  Producto arroz = new Producto(1, "Arroz", 1.20);
    public static Producto pan = new Producto(2, "Pan", 0.90);
    public static Producto pasta = new Producto(3, "Pasta", 1.10);
    public static Producto leche = new Producto(4, "Leche", 0.85);

    // bloque estático para inicializar productos del mercado
    // al ser static, mapa y metodo pertenece a la clase, no objeto
    // solo 1 catalogo para todos los clientes

    static {
        productos.put(1, arroz);
        productos.put(2, pan);
        productos.put(3, pasta);
        productos.put(4, leche);
    }

    public static Producto obtenerProductoId(int id) {
        return productos.get(id);
    }

    public static Map<Integer, Producto> obtenerTodosProductos() {
        return productos;
    }
    
}
