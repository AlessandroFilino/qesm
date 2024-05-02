package com.qesm;

import org.jgrapht.graph.DefaultEdge;

public class CustomEdge extends DefaultEdge{
    int quantityRequired;

    public CustomEdge(){

    }

    public void copyEdge(CustomEdge edgeToCopy){
        setQuantityRequired(edgeToCopy.getQuantityRequired());
    }

    public void setQuantityRequired(int quantityRequired){
        this.quantityRequired = quantityRequired;
    }

    public int getQuantityRequired() {
        return quantityRequired;
    }


}
