package practica1;

import java.util.HashMap;
import java.util.Map;

public class Catalogo {

    public static Map<Integer, Producto> productos = new HashMap<>();
    
    public static  Producto arroz = new Producto(1, "1kg de Arroz", 1.20);
    public static Producto pan = new Producto(2, "Barra de Pan", 0.90);
    public static Producto pasta = new Producto(3, "1kg de Pasta", 1.10);
    public static Producto leche = new Producto(4, "1 litro de Leche", 0.85);
    public static Producto galleta = new Producto(5, "Paquete de Galletas", 0.50);
    public static Producto chuche = new Producto(6, "Paquete de Chuches", 0.30);
    public static Producto aceite = new Producto(7, "1 litro de Aceite", 2.30);
    public static Producto azucar = new Producto(8, "Paquete de sal 1kg", 1.90);
    public static Producto cafe = new Producto(9, "Cafe molido 250g", 2.00);
    public static Producto huevo = new Producto(10, "Docena de huevos", 3.30);

    // bloque estático para inicializar productos del mercado
    // al ser static, mapa y metodo pertenece a la clase, no objeto
    // solo 1 catalogo para todos los clientes

    static {
        productos.put(1, arroz);
        productos.put(2, pan);
        productos.put(3, pasta);
        productos.put(4, leche);
        productos.put(5, galleta);
        productos.put(6, chuche);
        productos.put(7, aceite);
        productos.put(8, azucar);
        productos.put(9, cafe);
        productos.put(10, huevo);

    }

    public static Producto obtenerProductoId(int id) {
        return productos.get(id);
    }

    public static Map<Integer, Producto> obtenerTodosProductos() {
        return productos;
    }
    
}
