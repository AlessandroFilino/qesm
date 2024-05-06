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
}
