package com.qesm;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.jgrapht.nio.Attribute;


public abstract class ComposedBlock implements STPNBlock {
    private ArrayList<STPNBlock> composedElements;
    private UUID uuid;

    enum Colors {
        RED,
        BLUE,
        BLACK
    };

    public ComposedBlock(ArrayList<STPNBlock> composedElements) {
        this.composedElements = composedElements;
        this.uuid = UUID.randomUUID();
    }

    @Override
    public void printBlockInfo(int indentNum) {
        printIndent(indentNum);
        System.out.println("BlockType: " + this.getClass().getSimpleName());
        composedElements.forEach(element -> element.printBlockInfo(indentNum + 1));
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public ArrayList<STPNBlock> getComposedElements(){
        return composedElements;
    }

    @Override
    public Map<String, Attribute> getExporterAttributes() {
        // method implementation should be defined in AndBlock and SeqBlock 
        return null;
    }

    @Override
    public String getExporterId() {
        return "_" + uuid.toString().replaceAll("-","_");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }

        ComposedBlock composedBlockToCompare = (ComposedBlock) obj;
        ArrayList<STPNBlock> composedElementsToCompare = composedBlockToCompare.getComposedElements();
        
        for (STPNBlock composedElement : composedElements) {
            Boolean isContained = false;
            for (STPNBlock composedElementToCompare : composedElementsToCompare) {
                if(composedElement.equals(composedElementToCompare)){
                    isContained = true;
                    break;
                } 
            }
            if(!isContained){
                return false;
            }
        }

        for (STPNBlock composedElementToCompare : composedElementsToCompare) {
            Boolean isContained = false;
            for (STPNBlock composedElement : composedElements) {
                if(composedElementToCompare.equals(composedElement)){
                    isContained = true;
                    break;
                } 
            }
            if(!isContained){
                return false;
            }
        }

        return true;
    }

    @Override 
    public String toString(){
        return composedElements.toString();
    }

    @Override
    public String getHTMLLabel(Class<?> callerClass) {
        String openRow = "<TR>";
        String closeRow = "</TR>";
        String openCellData = "<TD>";
        String closeCellData = "</TD>";
        String closeTable = "</TABLE>";
        String closeFont = "</FONT>";

        Colors tableColor = Colors.BLACK;
        Colors fontColor = Colors.BLACK;
        boolean tableBorder = true;
        String label = new String();

        if(callerClass == null){
            tableBorder = false;
        }
        else if(callerClass == AndBlock.class) {
            label += openCellData;
        }
        
         

        if (this.getClass() == SeqBlock.class) {
            fontColor = Colors.RED;
            tableColor = Colors.RED;
            label += (getColoredOpenTable(tableColor, tableBorder) + openRow + openCellData);
            label += getColoredOpenFont(fontColor) + this.getClass().getSimpleName() + closeFont;
            label += (closeCellData + closeRow);
            label += (openRow + openCellData + getColoredOpenTable(tableColor, false));

            for (STPNBlock blockElement : composedElements) {
                label += (openRow + openCellData) + blockElement.getHTMLLabel(SeqBlock.class)
                        + (closeCellData + closeRow);
            }

            label += closeTable + closeCellData + closeRow + closeTable;
        } else if (this.getClass() == AndBlock.class) {
            fontColor = Colors.BLUE;
            tableColor = Colors.BLUE;
            label += (getColoredOpenTable(tableColor, tableBorder) + openRow + openCellData);
            label += getColoredOpenFont(fontColor) + this.getClass().getSimpleName() + closeFont;
            label += (closeCellData + closeRow);
            label += (openRow + openCellData + getColoredOpenTable(tableColor, false) + openRow);

            for (STPNBlock blockElement : composedElements) {
                label += blockElement.getHTMLLabel(AndBlock.class);
            }

            label += closeRow + closeTable + closeCellData + closeRow + closeTable;
        }

        if (callerClass == AndBlock.class) {
            label += closeCellData;
        }

        return label;
    }

    private String getColoredOpenTable(Colors tableColor, boolean border) {
        String color = new String();
        switch (tableColor) {
            case RED:
                color = "red";
                break;
            case BLUE:
                color = "blue";
                break;
            case BLACK:
                color = "black";
                break;

            default:
                break;
        }
        if(border){
            return "<TABLE " + "color='" + color + "' CELLBORDER='0'>";
        }
        else{
            return "<TABLE " + "color='" + color + "' border='0' CELLBORDER='0'>";
        }
        
    }

    private String getColoredOpenFont(Colors fontColor) {
        String color = new String();
        switch (fontColor) {
            case RED:
                color = "red";
                break;
            case BLUE:
                color = "blue";
                break;
            case BLACK:
                color = "black";
                break;

            default:
                break;
        }
        return "<FONT " + "color='" + color + "'>";
    }

}
