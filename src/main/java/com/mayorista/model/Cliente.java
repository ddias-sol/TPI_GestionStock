package com.mayorista.model; 

import javax.persistence.*; 

@Entity 
@Table(name = "clientes") 
public class Cliente { 
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 
    
    @Column(nullable = false, unique = true) 
    private int codigo; 
    
    @Column(nullable = false) 
    private String apellido; 
    
    @Column(nullable = false) 
    private String nombre; 
    
    @Column(nullable = false) 
    private String dni; 
    
    private String direccion; 
    private String telefono; 
    private String email; 
    
    public Cliente() {} 
    
    public Cliente(int codigo, String apellido, String nombre, String dni, String direccion, String telefono, String email) { 
        this.codigo = codigo; 
        this.apellido = apellido; 
        this.nombre = nombre; 
        this.dni = dni; 
        this.direccion = direccion; 
        this.telefono = telefono; 
        this.email = email; 
    } 
    
    public Long getId() { return id; } 
    public int getCodigo() { return codigo; } 
    public String getApellido() { return apellido; } 
    public String getNombre() { return nombre; } 
    public String getNombreCompleto() { return apellido + ", " + nombre; } 
    public String getDni() { return dni; } 
    public String getDireccion() { return direccion; } 
    public String getTelefono() { return telefono; } 
    public String getEmail() { return email; } 
    
    public void setDireccion(String direccion) { this.direccion = direccion; } 
    public void setTelefono(String telefono) { this.telefono = telefono; } 
    public void setEmail(String email) { this.email = email; } 
}