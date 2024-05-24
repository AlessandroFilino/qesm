package com.qesm;

import java.util.ArrayList;
import java.util.UUID;

public class SimpleBlock implements STPNBlock{

    private ProductType simpleElement;
    private ArrayList<ProductType> enablingTokens;
    private UUID uuid;

    public SimpleBlock(ProductType basicElement) {
        this.simpleElement = basicElement;
        this.enablingTokens = new ArrayList<ProductType>();
        this.uuid = UUID.randomUUID();
    }

    @Override
    public boolean addEnablingToken(ProductType enablingToken) {
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
    public ProductType getSimpleElement() {
        return simpleElement;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getHTMLLabel(Class<?> callerClass) {
        String value = new String();
        if(callerClass == SeqBlock.class){
            value = "<TABLE color='black' CELLBORDER='0'><TR><TD>" + simpleElement.getNameType() + "</TD></TR></TABLE>";
        }
        else if (callerClass == AndBlock.class){
            value = "<TD><TABLE color='black' CELLBORDER='0'><TR><TD>" + simpleElement.getNameType() + "</TD></TR></TABLE></TD>";
        }
        else{
            value = "<TABLE color='black' border='0' CELLBORDER='0'><TR><TD>" + simpleElement.getNameType() + "</TD></TR></TABLE>";
        }
        return value;
    }

}
