package com.mayorista.model; 

import javax.persistence.*; 

@Entity 
@Table(name = "rubros") 

public class Rubro { 
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 
    
    @Column(nullable = false, unique = true) 
    private int codigo; 
    
    @Column(nullable = false) 
    private String nombre; 
    
    private String descripcion; 
    
    public Rubro() {} 
    
    public Rubro(int codigo, String nombre, String descripcion) { 
        this.codigo = codigo; 
        this.nombre = nombre; 
        this.descripcion = descripcion; 
    } 
    
    public Long getId() { return id; } 
    public int getCodigo() { return codigo; } 
    public void setCodigo(int codigo) { this.codigo = codigo; } 
    public String getNombre() { return nombre; } 
    public void setNombre(String nombre) { this.nombre = nombre; } 
    public String getDescripcion() { return descripcion; } 
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; } 
}