package com.mayorista.model; 

import javax.persistence.*; 
import java.util.ArrayList; 
import java.util.List; 

@Entity 
@Table(name = "facturas") 
public class Factura { 
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; @Column(name = "numero_factura", nullable = false, unique = true) 
    private String numeroFactura; 
    
    @ManyToOne 
    @JoinColumn(name = "cliente_id", nullable = false) 
    private Cliente cliente; 
    
    @Column(name = "fecha_emision", nullable = false) 
    private String fechaEmision; 
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true) 
    private List<DetalleFactura> detalles = new ArrayList<>();

    @Column(nullable = false)
    private double total = 0.0;

    public Factura() {}

    public Factura(String numeroFactura, Cliente cliente, String fechaEmision) {
        this.numeroFactura = numeroFactura;
        this.cliente = cliente;
        this.fechaEmision = fechaEmision;
    }

    public void agregarDetalle(DetalleFactura detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
        this.total += detalle.getSubtotal();
    }

    public Long getId() { return id; }
    public String getNumeroFactura() { return numeroFactura; }
    public Cliente getCliente() { return cliente; }
    public String getFechaEmision() { return fechaEmision; }
    public List<DetalleFactura> getDetalles() { return detalles; }
    public double getTotal() { return total; }
}