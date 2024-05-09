package com.qesm;

import java.util.UUID;

public interface STPNBlock {

    public void printBlockInfo(int indentNum);

    public default boolean addEnablingToken(RawMaterialType enablingToken){
        return false;
    }

    default void printIndent(int indentNum){
        for (int index = 0; index < indentNum; index++) {
            System.out.print("  ");
        }
    }
    
    public default ProcessedType getSimpleElement(){
        return null;
    }
    
    public UUID getUuid();
    public String getLabel();
}
