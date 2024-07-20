package com.qesm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public interface STPNBlock extends DotFileConvertible, Serializable{

    public String getBlockInfo(int indentNum);

    public default boolean addEnablingToken(AbstractProduct enablingToken){
        return false;
    }

    default String addIndent(int indentNum){
        return "  ".repeat(indentNum);
    }
    
    public default AbstractProduct getSimpleElement(){
        return null;
    }

    public default ArrayList<STPNBlock> getComposedElements(){
        return null;
    }
    
    public UUID getUuid();
    public String getHTMLLabel(Class<?> callerClass);
}
