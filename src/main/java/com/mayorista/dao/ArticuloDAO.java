package com.mayorista.dao; 

import com.mayorista.config.JPAUtil; 
import com.mayorista.model.*; 

import javax.persistence.EntityManager; 
import javax.persistence.TypedQuery; 
import java.util.List; 

public class ArticuloDAO { 

    public void guardar(Object entidad) { 
        EntityManager em = JPAUtil.getEntityManager(); 
        try { 
            em.getTransaction().begin(); 
            em.persist(entidad); 
            em.getTransaction().commit(); 
        } catch (Exception e) { 
            if (em.getTransaction().isActive()) em.getTransaction().rollback(); 
            throw e; 
        } finally { 
            em.close(); 
        } 
    } 

    public void actualizar(Object entidad) { 
        EntityManager em = JPAUtil.getEntityManager(); 
        try { 
            em.getTransaction().begin(); 
            em.merge(entidad); 
            em.getTransaction().commit(); 
        } catch (Exception e) { 
            if (em.getTransaction().isActive()) em.getTransaction().rollback(); 
            throw e; 
        } finally { 
            em.close(); 
        } 
    } 
    
    public List<Articulo> obtenerTodosArticulos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery("SELECT a FROM Articulo a", Articulo.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Articulo buscarArticuloPorCodigo(int codigo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery("SELECT a FROM Articulo a WHERE a.codigo = :cod", Articulo.class);
            query.setParameter("cod", codigo);
            List<Articulo> res = query.getResultList();
            return res.isEmpty() ? null : res.get(0);
        } finally {
            em.close();
        }
    }

    public List<Articulo> obtenerArticulosBajoStock() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery("SELECT a FROM Articulo a WHERE a.stockActual <= a.stockMinimo", Articulo.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Cliente> obtenerTodosClientes() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c", Cliente.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Cliente buscarClientePorCodigo(int codigo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c WHERE c.codigo = :cod", Cliente.class);
            query.setParameter("cod", codigo);
            List<Cliente> res = query.getResultList();
            return res.isEmpty() ? null : res.get(0);
        } finally {
            em.close();
        }
    }

    public List<Factura> obtenerTodasFacturas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Factura> query = em.createQuery("SELECT f FROM Factura f", Factura.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}