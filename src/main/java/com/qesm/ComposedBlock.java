package com.qesm;

import java.util.ArrayList;
import java.util.UUID;

import javax.swing.text.Document;
import javax.swing.text.Element;

public class ComposedBlock implements STPNBlock{

    private ArrayList<STPNBlock> composedElements;
    private UUID uuid;

    public ComposedBlock(ArrayList<STPNBlock> composedElements) {
        this.composedElements = composedElements;
        this.uuid = UUID.randomUUID();
    }

    @Override
    public void printBlockInfo(int indentNum){
        printIndent(indentNum);
        System.out.println("BlockType: " + this.getClass().getSimpleName());
        composedElements.forEach(element -> element.printBlockInfo(indentNum + 1));
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getLabel() {
        String cellOpening = "<TR><TD>";
        String cellClosing = "</TD></TR>";
        String tableOpening = "<TABLE>";
        String tableClosing = "</TABLE>";

        String label = new String();
        label += tableOpening;
        label += cellOpening;

        for (STPNBlock blockElement : composedElements) {
            blockElement.getLabel();
        }


        return label;
        // return this.getClass().getSimpleName();
    }

}
