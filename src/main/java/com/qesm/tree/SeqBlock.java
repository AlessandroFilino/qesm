package com.qesm.tree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;

public class SeqBlock extends ComposedBlock {

    public SeqBlock(ArrayList<STPNBlock> composedElements) {
        super(composedElements);
    }

    @Override
    public Map<String, Attribute> getExporterAttributes() {
        Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
        map.put("shape", new DefaultAttribute<String>("box", AttributeType.STRING));
        map.put("color", new DefaultAttribute<String>("red", AttributeType.STRING));
        map.put("label", new DefaultAttribute<String>(this.getHTMLLabel(null), AttributeType.HTML));

        return map;
    }

    @Override
    public String toString() {
        return "SeqBlock: [" + super.toString() + "]";
    }
}
