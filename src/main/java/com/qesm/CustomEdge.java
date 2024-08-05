package com.qesm;

import org.jgrapht.graph.DefaultEdge;

public class CustomEdge extends DefaultEdge {
    int quantityRequired;

    public CustomEdge() {

    }

    public CustomEdge(CustomEdge edgeToCopy) {
        setQuantityRequired(edgeToCopy.getQuantityRequired());
    }

    public void setQuantityRequired(int quantityRequired) {
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        CustomEdge customEdgeToCompare = (CustomEdge) obj;
        if (quantityRequired != customEdgeToCompare.getQuantityRequired()) {
            return false;
        }
        if (!customEdgeToCompare.getSource().equals(this.getSource()) ||
                !customEdgeToCompare.getTarget().equals(this.getTarget())) {
            return false;
        }

        return true;
    }

    public Object getSource() {
        return super.getSource();
    }

    public Object getTarget() {
        return super.getTarget();
    }
}
