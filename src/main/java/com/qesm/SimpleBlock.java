package com.qesm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;

public class SimpleBlock implements STPNBlock{

    private AbstractProduct simpleElement;
    private ArrayList<AbstractProduct> enablingTokens;
    private UUID uuid;

    public SimpleBlock(AbstractProduct basicElement) {
        if(!basicElement.isProcessed()){
            throw new RuntimeException();
        }
        this.simpleElement = basicElement;
        this.enablingTokens = new ArrayList<AbstractProduct>();
        this.uuid = UUID.randomUUID();
    }

    public ArrayList<AbstractProduct> getEnablingTokens(){
        return enablingTokens;
    }

    @Override
    public boolean addEnablingToken(AbstractProduct enablingToken) {
        enablingTokens.add(enablingToken);
        return true;
    }


    @Override
    public String getBlockInfo(int indentNum) {
        String blockInfo = addIndent(indentNum);
        blockInfo += simpleElement.getName() + " tokens: ";
        for(AbstractProduct token : enablingTokens){
            blockInfo +=  token.getName() + " ";
        }
        return blockInfo;
    }

    @Override
    public AbstractProduct getSimpleElement() {
        return simpleElement;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public Map<String, Attribute> getExporterAttributes() {
        Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("shape", new DefaultAttribute<String>("box", AttributeType.STRING));
            map.put("label", new DefaultAttribute<String>(this.getHTMLLabel(null), AttributeType.HTML));

            return map;
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

        SimpleBlock simpleBlockToCompare = (SimpleBlock) obj;
        if(! simpleBlockToCompare.getSimpleElement().equals(simpleElement)){
            return false;
        }
        if(! simpleBlockToCompare.getEnablingTokens().equals(enablingTokens)){
            return false;
        }
        
        return true;
    }

    @Override
    public String getHTMLLabel(Class<?> callerClass) {
        String value = new String();
        if(callerClass == SeqBlock.class){
            value = "<TABLE color='black' CELLBORDER='0'><TR><TD>" + simpleElement.getName() + "</TD></TR></TABLE>";
        }
        else if (callerClass == AndBlock.class){
            value = "<TD><TABLE color='black' CELLBORDER='0'><TR><TD>" + simpleElement.getName() + "</TD></TR></TABLE></TD>";
        }
        else{
            value = "<TABLE color='black' border='0' CELLBORDER='0'><TR><TD>" + simpleElement.getName() + "</TD></TR></TABLE>";
        }
        return value;
    }

    @Override
    public String toString() {
        String stringToReturn = simpleElement.getName();
        if(enablingTokens.size() > 0){
            stringToReturn += " enablingTokens: [";
            for (AbstractProduct enablingToken : enablingTokens) {
                stringToReturn += " " + enablingToken.getName(); 
            }
            stringToReturn += " ] ";
        }
        return stringToReturn; 
        
    }

    
}
