package com.mayorista.observer; 

import com.mayorista.model.Articulo; 

public class AlertaStockListener implements StockObserver { 
    @Override 
    public void enStockMinimoAlcanzado(Articulo articulo) { 
        System.out.println("⚠️ [ALERTA EN TIEMPO REAL - PATRÓN OBSERVER]"); 
        System.out.println(" → El artículo '" + articulo.getDescripcion() + "' alcanzó o superó su stock mínimo."); 
        System.out.println(" → Stock Actual: " + articulo.getStockActual() + " | Stock Mínimo: " + articulo.getStockMinimo()); 
    } 
}