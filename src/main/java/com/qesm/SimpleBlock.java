package com.qesm;

import java.util.ArrayList;

public class SimpleBlock implements STPNBlock{

    private ProcessedType simpleElement;
    private ArrayList<RawMaterialType> enablingTokens;

    public SimpleBlock(ProcessedType basicElement) {
        this.simpleElement = basicElement;
        this.enablingTokens = new ArrayList<RawMaterialType>();
    }

    @Override
    public boolean addEnablingToken(RawMaterialType enablingToken) {
        enablingTokens.add(enablingToken);
        return true;
    }


    @Override
    public void printBlockInfo(int indentNum) {
        printIndent(indentNum);
        System.out.println(simpleElement.getNameType() + " tokens: ");
        enablingTokens.forEach(token -> System.out.print(token.getNameType() + " "));
    }

    @Override
    public ProcessedType getSimpleElement() {
        return simpleElement;
    }

    // @Override
    // public boolean equals(Object arg0) {
    //     if(arg0.getClass() != SimpleBlock.class){
    //         return false;
    //     }
    //     else{
    //         SimpleBlock blockToCheck = (SimpleBlock)arg0;
    //         System.out.println("è uguale: " + this.simpleElement.getNameType() + "  " + blockToCheck.getSimpleElement().getNameType());
    //         return this.simpleElement == blockToCheck.getSimpleElement();
    //     }
    // }

}
