package com.mayorista; 

import com.mayorista.config.JPAUtil; 
import com.mayorista.dao.ArticuloDAO; 
import com.mayorista.model.*; 
import com.mayorista.observer.AlertaStockListener; 
import com.mayorista.observer.StockObserver; 

import java.util.List; 
import java.util.Scanner; 

public class Main { 
    private static ArticuloDAO dao = new ArticuloDAO(); 
    private static StockObserver alertaObserver = new AlertaStockListener(); 
    private static int contadorFacturas = 1; 
    
    public static void main(String[] args) { 
        inicializarBaseDeDatos(); 
        Scanner scanner = new Scanner(System.in); 
        int opcion = -1; 
        
        do { 
            System.out.println("================================================="); 
            System.out.println(" SISTEMA DE GESTIÓN DE STOCK (HIBERNATE / JPA) "); 
            System.out.println("================================================="); 
            System.out.println("1. Listar Todos los Artículos"); 
            System.out.println("2. Listar Artículos por Debajo del Stock Mínimo"); 
            System.out.println("3. Registrar Compra a Proveedor (Suma Stock)"); 
            System.out.println("4. Registrar Venta a Cliente (Descuenta Stock + Alerta)"); 
            System.out.println("5. Listar Facturas Emitidas"); 
            System.out.println("0. Salir"); 
            System.out.print("Seleccione una opción: "); 
            
            try { 
                opcion = Integer.parseInt(scanner.nextLine()); 
                switch (opcion) { 
                    case 1 -> listarArticulos(); 
                    case 2 -> listarStockBajo(); 
                    case 3 -> registrarCompra(scanner); 
                    case 4 -> registrarVenta(scanner); 
                    case 5 -> listarFacturas(); 
                    case 0 -> System.out.println("¡Gracias por utilizar el Sistema de Gestión de Stock!"); 
                    default -> System.out.println("❌ Opción inválida."); 
                } 
            } catch (Exception e) { 
                System.out.println("❌ Error de entrada o procesamiento: " + e.getMessage()); 
            } 
        } while (opcion != 0); 
        
        scanner.close(); 
        JPAUtil.shutdown(); 
    } 
    
    private static void inicializarBaseDeDatos() { 
        if (dao.obtenerTodosArticulos().isEmpty()) { 
            Rubro r1 = new Rubro(1, "Procesadores", "CPUs de alta gama"); 
            Rubro r2 = new Rubro(2, "Placas de Video", "GPUs para gaming"); 
            dao.guardar(r1); 
            dao.guardar(r2); 
            
            Proveedor p1 = new Proveedor(101, "30-71123456-8", "TechSupplier S.A.", "Av. Corrientes 1234", "011-45678900", "contacto@techsupplier.com"); 
            dao.guardar(p1); 
            
            Cliente c1 = new Cliente(501, "Gómez", "Lucas", "38123456", "Calle Falsa 123", "2324-556677", "lucas.gomez@email.com"); 
            dao.guardar(c1); 

            dao.guardar(new Articulo(10, "Intel Core i7-13700K", r1, p1, 15, 5, 450.0)); 
            dao.guardar(new Articulo(11, "AMD Ryzen 5 7600X", r1, p1, 4, 5, 280.0)); 
            dao.guardar(new Articulo(12, "NVIDIA RTX 4070 12GB", r2, p1, 8, 3, 650.0)); 
        } 
    } 
    
    private static void listarArticulos() { 
        System.out.println(" LISTADO DE ARTÍCULOS EN BASE DE DATOS "); 
        List<Articulo> lista = dao.obtenerTodosArticulos();
        for (Articulo a : lista) {
            System.out.println("Código: " + a.getCodigo() + " | " + a.getDescripcion() +
                    " | Stock: " + a.getStockActual() + " (Mín: " + a.getStockMinimo() + ")" +
                    " | Precio: $" + a.getPrecioUnitario());
        }
    }

    private static void listarStockBajo() {
        System.out.println("\n--- ARTÍCULOS POR DEBAJO O EN STOCK MÍNIMO ---");
        List<Articulo> lista = dao.obtenerArticulosBajoStock();
        if (lista.isEmpty()) {
            System.out.println("✅ Todos los artículos superan el stock mínimo.");
        } else {
            for (Articulo a : lista) {
                System.out.println("⚠️ [CRÍTICO] Código: " + a.getCodigo() + " - " + a.getDescripcion() +
                        " | Stock Actual: " + a.getStockActual() + " (Mínimo: " + a.getStockMinimo() + ")");
            }
        }
    }

    private static void registrarCompra(Scanner scanner) {
        listarArticulos();
        System.out.print("Código del artículo a reponer: ");
        int cod = Integer.parseInt(scanner.nextLine());
        Articulo art = dao.buscarArticuloPorCodigo(cod);

        if (art != null) {
            System.out.print("Cantidad comprada al proveedor: ");
            int cant = Integer.parseInt(scanner.nextLine());
            art.agregarStock(cant);
            dao.actualizar(art);
            System.out.println("✅ Stock actualizado en H2. Nuevo Stock: " + art.getStockActual());
        } else {
            System.out.println("❌ Artículo no encontrado.");
        }
    }

    private static void registrarVenta(Scanner scanner) {
        System.out.println("\n--- REGISTRAR VENTA A CLIENTE ---");
        List<Cliente> clientes = dao.obtenerTodosClientes();
        for (Cliente c : clientes) {
            System.out.println("Código: " + c.getCodigo() + " | " + c.getNombreCompleto());
        }
        System.out.print("Código del cliente: ");
        int codCli = Integer.parseInt(scanner.nextLine());
        Cliente cli = dao.buscarClientePorCodigo(codCli);

        if (cli == null) {
            System.out.println("❌ Cliente no encontrado.");
            return;
        }

        String numFactura = "F001-000" + contadorFacturas++;
        Factura factura = new Factura(numFactura, cli, "12/11/2026");

        boolean agregando = true;
        while (agregando) {
            listarArticulos();
            System.out.print("Código del artículo a vender: ");
            int codArt = Integer.parseInt(scanner.nextLine());
            Articulo art = dao.buscarArticuloPorCodigo(codArt);

            if (art != null) {
                System.out.print("Cantidad a vender: ");
                int cant = Integer.parseInt(scanner.nextLine());

                if (art.descontarStock(cant)) {
                    DetalleFactura detalle = new DetalleFactura(art, cant);
                    factura.agregarDetalle(detalle);
                    dao.actualizar(art);

                    if (art.getStockActual() <= art.getStockMinimo()) {
                        alertaObserver.enStockMinimoAlcanzado(art);
                    }
                } else {
                    System.out.println("❌ Stock insuficiente.");
                }
            } else {
                System.out.println("❌ Artículo no encontrado.");
            }

            System.out.print("¿Agregar otro artículo a la factura? (s/n): ");
            if (!scanner.nextLine().equalsIgnoreCase("s")) {
                agregando = false;
            }
        }

        dao.guardar(factura);
        System.out.println("\n✅ Factura " + numFactura + " emitida y guardada en la BD. Total: $" + factura.getTotal());
    }

    private static void listarFacturas() {
        System.out.println("\n--- HISTORIAL DE FACTURAS EMITIDAS (PERSISTIDAS EN BD) ---");
        List<Factura> facturas = dao.obtenerTodasFacturas();
        if (facturas.isEmpty()) {
            System.out.println("No hay facturas en la base de datos.");
        } else {
            for (Factura f : facturas) {
                System.out.println("N°: " + f.getNumeroFactura() + " | Cliente: " + f.getCliente().getNombreCompleto() +
                        " | Total: $" + f.getTotal());
            }
        }
    }
}