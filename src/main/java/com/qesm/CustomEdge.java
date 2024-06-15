package com.qesm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;

import org.jgrapht.graph.DefaultEdge;
import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

public class CustomEdge extends DefaultEdge{
    int quantityRequired;

    public CustomEdge(){

    }

    public CustomEdge(CustomEdge edgeToCopy){
        setQuantityRequired(edgeToCopy.getQuantityRequired());
    }

    public void setQuantityRequired(int quantityRequired){
        this.quantityRequired = quantityRequired;
    }

    public int getQuantityRequired() {
        return quantityRequired;
    }

    @Override
    public String toString() {
        
        return "( " + this.getSource() + " -> " + this.getTarget() + " quantityRequired: " + quantityRequired + " )";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }

        CustomEdge customEdgeToCompare = (CustomEdge) obj;
        if (quantityRequired != customEdgeToCompare.getQuantityRequired()){
            return false;
        }
        if(! customEdgeToCompare.getSource().equals(this.getSource()) ||
           ! customEdgeToCompare.getTarget().equals(this.getTarget())){
            return false;
        }
        
        return true;
    }
}
