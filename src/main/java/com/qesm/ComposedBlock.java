package com.qesm;

import java.util.ArrayList;

public class ComposedBlock implements STPNBlock{

    private ArrayList<STPNBlock> composedElements;

    public ComposedBlock(ArrayList<STPNBlock> composedElements) {
        this.composedElements = composedElements;
    }

    @Override
    public void printBlockInfo(int indentNum){
        printIndent(indentNum);
        System.out.println("BlockType: " + this.getClass().getSimpleName());
        composedElements.forEach(element -> element.printBlockInfo(indentNum + 1));
    }

}
