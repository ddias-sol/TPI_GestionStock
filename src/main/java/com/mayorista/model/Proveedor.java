package com.mayorista.model; 

import javax.persistence.*; 

@Entity 
@Table(name = "proveedores") 
public class Proveedor { 
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 
    
    @Column(nullable = false, unique = true) 
    private int codigo; 
    
    @Column(name = "cuil_cuit", nullable = false) 
    private String cuilCuit; 
    
    @Column(name = "razon_social", nullable = false) 
    private String razonSocial; 
    
    private String direccion; 
    private String telefono; 
    private String email; 
    
    public Proveedor() {}

    public Proveedor(int codigo, String cuilCuit, String razonSocial, String direccion, String telefono, String email) { 
        this.codigo = codigo; 
        this.cuilCuit = cuilCuit; 
        this.razonSocial = razonSocial; 
        this.direccion = direccion; 
        this.telefono = telefono; 
        this.email = email; 
    } 
    
    public Long getId() { return id; } 
    public int getCodigo() { return codigo; } 
    public String getCuilCuit() { return cuilCuit; } 
    public String getRazonSocial() { return razonSocial; } 
    public String getDireccion() { return direccion; } 
    public String getTelefono() { return telefono; } 
    public String getEmail() { return email; } 
}