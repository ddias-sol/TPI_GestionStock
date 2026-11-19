package com.mayorista.observer; 

import com.mayorista.model.Articulo; 

public interface StockObserver { 
    void enStockMinimoAlcanzado(Articulo articulo); 
}