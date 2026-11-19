package com.mayorista.model; 

import javax.persistence.*; 

@Entity 
@Table(name = "detalle_facturas") 
public class DetalleFactura { 
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 
    
    @ManyToOne 
    @JoinColumn(name = "factura_id") 
    private Factura factura; 
    
    @ManyToOne 
    @JoinColumn(name = "articulo_id", nullable = false) 
    private Articulo articulo; 
    
    @Column(nullable = false) 
    private int cantidad; 
    
    @Column(name = "precio_unitario", nullable = false) 
    private double precioUnitario; 
    
    @Column(nullable = false) 
    private double subtotal; 
    
    public DetalleFactura() {} 
    
    public DetalleFactura(Articulo articulo, int cantidad) { 
        this.articulo = articulo; 
        this.cantidad = cantidad; 
        this.precioUnitario = articulo.getPrecioUnitario(); 
        this.subtotal = this.precioUnitario * cantidad; 
    } 
    
    public Long getId() { return id; } 
    public Factura getFactura() { return factura; } 
    public void setFactura(Factura factura) { this.factura = factura; } 
    public Articulo getArticulo() { return articulo; } 
    public int getCantidad() { return cantidad; } 
    public double getSubtotal() { return subtotal; } 
}