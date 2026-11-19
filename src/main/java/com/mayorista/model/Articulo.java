package com.mayorista.model; 

import javax.persistence.*; 

@Entity 
@Table(name = "articulos") 
public class Articulo { 
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 
    
    @Column(nullable = false, unique = true) 
    private int codigo; 
    
    @Column(nullable = false) 
    private String descripcion; 
    
    @ManyToOne 
    @JoinColumn(name = "rubro_id", nullable = false) 
    private Rubro rubro; 
    
    @ManyToOne 
    @JoinColumn(name = "proveedor_id", nullable = false) 
    private Proveedor proveedor; 
    
    @Column(name = "stock_actual", nullable = false) 
    private int stockActual; 
    
    @Column(name = "stock_minimo", nullable = false) 
    private int stockMinimo; 
    
    @Column(name = "precio_unitario", nullable = false) 
    private double precioUnitario; 
    
    public Articulo() {} 
    
    public Articulo(int codigo, String descripcion, Rubro rubro, Proveedor proveedor, int stockActual, int stockMinimo, double precioUnitario) { 
        this.codigo = codigo; 
        this.descripcion = descripcion; 
        this.rubro = rubro; 
        this.proveedor = proveedor; 
        this.stockActual = stockActual; 
        this.stockMinimo = stockMinimo; 
        this.precioUnitario = precioUnitario; } 
        
        public void agregarStock(int cantidad) { 
            this.stockActual += cantidad; 
        } 
        
        public boolean descontarStock(int cantidad) { 
            if (this.stockActual >= cantidad) { 
                this.stockActual -= cantidad; 
                return true; 
            } 
            return false; 
        } 
        
        public Long getId() { return id; } 
        public int getCodigo() { return codigo; } 
        public String getDescripcion() { return descripcion; } 
        public Rubro getRubro() { return rubro; } 
        public Proveedor getProveedor() { return proveedor; } 
        public int getStockActual() { return stockActual; } 
        public int getStockMinimo() { return stockMinimo; } 
        public double getPrecioUnitario() { return precioUnitario; } 
    }