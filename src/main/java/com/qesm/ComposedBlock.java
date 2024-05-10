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
    public String getHTMLLabel(Class<?> callerClass) {
        String openRow = "<TR>";
        String closeRow = "</TR>";
        String openCellData = "<TD>";
        String closeCellData = "</TD>";
        String closeTable = "</TABLE>";
        String closeFont = "</FONT>";

        String label = new String();
        

        
        if(this.getClass() == SeqBlock.class){
            label += (getColoredOpenTable("red") + openRow + openCellData);
            label += getColoredOpenFont("red") + this.getClass().getSimpleName() + closeFont;
            label += (closeCellData + closeRow);

            for (STPNBlock blockElement : composedElements) {
                label += (openRow + openCellData) + blockElement.getHTMLLabel(SeqBlock.class) + (closeCellData + closeRow);
            }

            label += closeTable;
        }
        else if (this.getClass() == AndBlock.class){
            label += (getColoredOpenTable("blue") + openRow + openCellData);
            label += getColoredOpenFont("blue") + this.getClass().getSimpleName() + closeFont;
            label += (closeCellData + closeRow);
            label += (openRow + openCellData + getColoredOpenTable("black") + openRow);

            for (STPNBlock blockElement : composedElements) {
                label += blockElement.getHTMLLabel(AndBlock.class);
            }

            label += closeRow + closeTable + closeCellData + closeRow + closeTable;
        }

        return label;
    }

    private String getColoredOpenTable(String color){
        return "<TABLE " + "color='" + color + "' CELLBORDER='0'>";
    }

    private String getColoredOpenFont(String color){
        return "<FONT " + "color='" + color + "'>";
    }
}
