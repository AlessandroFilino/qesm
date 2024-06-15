package com.qesm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public interface STPNBlock extends DotFileConvertible, Serializable{

    public void printBlockInfo(int indentNum);

    // TODO: add enabling token during structured tree generation
    public default boolean addEnablingToken(ProductType enablingToken){
        return false;
    }

    default void printIndent(int indentNum){
        for (int index = 0; index < indentNum; index++) {
            System.out.print("  ");
        }
    }
    
    public default ProductType getSimpleElement(){
        return null;
    }

    public default ArrayList<STPNBlock> getComposedElements(){
        return null;
    }
    
    public UUID getUuid();
    public String getHTMLLabel(Class<?> callerClass);
}
