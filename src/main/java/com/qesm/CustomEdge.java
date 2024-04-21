package com.qesm;

import org.jgrapht.graph.DefaultEdge;

public class CustomEdge extends DefaultEdge{
    int quantityRequired;

    public CustomEdge(){

    }

    public void setQuantityRequired(int quantityRequired){
        this.quantityRequired = quantityRequired;
    }

    public int getQuantityRequired() {
        return quantityRequired;
    }

}
