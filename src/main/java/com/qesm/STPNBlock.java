package com.qesm;

import java.util.ArrayList;

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
    
}
