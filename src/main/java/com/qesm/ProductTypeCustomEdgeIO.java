package com.qesm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.ProductType.ItemType;

public class ProductTypeCustomEdgeIO <T extends ProductType> implements BasicImportExport<T, CustomEdge> {
    final private Function<T, String> vertexIdProvider;
    final private Function<T, Map<String, Attribute>> vertexAttributeProvider;
    final private Function<CustomEdge, Map<String, Attribute>> edgeAttributeProvider;
    final private Supplier<Map<String, Attribute>> graphAttributeProvider;

    final private BiFunction<String, Map<String, Attribute>, T> vertexFactoryFunction;
    final private Function<Map<String, Attribute>, CustomEdge> edgeWithAttributesFactory;

    ProductTypeCustomEdgeIO(Class<T> classType) {

        // Exporter's Providers
        this.vertexIdProvider = v -> v.getNameType();

        this.vertexAttributeProvider = v -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("shape", new DefaultAttribute<String>("circle", AttributeType.STRING));
            if (v.getItemType() == ItemType.RAW_MATERIAL) {
                map.put("color", new DefaultAttribute<String>("blue", AttributeType.STRING));
                map.put("label", new DefaultAttribute<String>(v.getNameType() + "\nRAW_TYPE", AttributeType.STRING));
                map.put("vertex_type", new DefaultAttribute<String>("RawMaterialType", AttributeType.STRING));
            } else {
                map.put("color", new DefaultAttribute<String>("orange", AttributeType.STRING));
                map.put("label",
                        new DefaultAttribute<String>(
                                v.getNameType() + "\nPROCESSED_TYPE" + "\nquantityProduced: " + v.getQuantityProduced(),
                                AttributeType.STRING));
                map.put("vertex_type", new DefaultAttribute<String>("ProcessedType", AttributeType.STRING));
                map.put("quantity_produced", new DefaultAttribute<Integer>(v.getQuantityProduced(), AttributeType.INT));

                // TODO: serialize pdf type and values
                StochasticTime pdf = v.getPdf();

                Class<? extends StochasticTime> pdfClass = pdf.getClass();
                String pdfString = pdfClass.getSimpleName() + ";";
                if(pdfClass == UniformTime.class){
                    pdfString += pdf.getEFT() + ";" + pdf.getLFT();
                }
                else if(pdfClass == ErlangTime.class){
                    
                }
                else if(pdfClass == ExponentialTime.class){

                }
                else if(pdfClass == DeterministicTime.class){
                    // pdfString += pdf.get
                }
                else{
                    System.err.println("Export error: pdfClass " + pdfClass + " not supported");
                    pdfString = null;
                }
                map.put("pdf", new DefaultAttribute<String>(pdfString, AttributeType.STRING));
            }
            return map;
        };

        this.edgeAttributeProvider = e -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();

            map.put("label",
                    new DefaultAttribute<String>("quantityNeeded: " + e.getQuantityRequired(), AttributeType.STRING));
            map.put("quantity_required", new DefaultAttribute<Integer>(e.getQuantityRequired(), AttributeType.INT));

            return map;
        };

        this.graphAttributeProvider = () -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("rankdir", new DefaultAttribute<String>("BT", AttributeType.STRING));
            return map;
        };

        // Importer's Factories
        this.vertexFactoryFunction = (vertexName, attributesMap) -> {

            T vertex = null;
            String vertexType;

            try {
                vertexType = attributesMap.get("vertex_type").toString();
            } catch (NullPointerException e) {
                System.err.println("Import error: unable to find vertex_type field for node: " + vertexName);
                e.printStackTrace();
                return null;
            }

            if (vertexType.equals("RawMaterialType")) {
                try {
                    vertex = classType.getDeclaredConstructor(String.class, ItemType.class).newInstance(vertexName, ItemType.RAW_MATERIAL);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                
                // vertex = new ProductType(vertexName, ItemType.RAW_MATERIAL);
            } else if (vertexType.equals("ProcessedType")) {
                try {
                    Integer quantityProduced = Integer.valueOf(attributesMap.get("quantity_produced").getValue());
                    String pdfString = attributesMap.get("quantity_produced").getValue();
                    
                    // TODO: convert pdf from string to actual StochasticTime
                    StochasticTime pdf = null;
                    try {
                        vertex = classType.getDeclaredConstructor(String.class, ItemType.class).newInstance(vertexName, ItemType.PROCESSED);
                        vertex.setPdf(pdf);
                        vertex.setQuantityProduced(quantityProduced);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    
                    // vertex = new ProductType(vertexName, ItemType.PROCESSED);
                    // vertex.setPdf(pdf);
                    // vertex.setQuantityProduced(quantityProduced);

                } catch (NullPointerException e) {
                    System.err.println("Import error: unable to find all necessary field for node: " + vertexName);
                    e.printStackTrace();
                    return null;
                }
            } else {
                System.err.println("Import error: unknown type for vertexType field: " + vertexName);
                return null;
                
            }

            return vertex;
        };

        this.edgeWithAttributesFactory = (attributesMap) -> {
            CustomEdge edge = new CustomEdge();

            try {
                Integer quantityRequired = Integer.valueOf((attributesMap.get("quantity_required").getValue()));
                edge.setQuantityRequired(quantityRequired);

            } catch (NullPointerException e) {
                System.err.println("Import error: unable to find quantity_required field");
                return edge;
            }

            return edge;
        };
    }

    @Override
    public Function<CustomEdge, Map<String, Attribute>> getEdgeAttributeProvider() {
        return edgeAttributeProvider;
    }

    @Override
    public Supplier<Map<String, Attribute>> getGraphAttributeProvider() {
        return graphAttributeProvider;
    }

    @Override
    public Function<T, Map<String, Attribute>> getVertexAttributeProvider() {
        return vertexAttributeProvider;
    }

    @Override
    public Function<T, String> getVertexIdProvider() {
        return vertexIdProvider;
    }

    @Override
    public BiFunction<String, Map<String, Attribute>, T> getVertexFactoryFunction() {
        return vertexFactoryFunction;
    }

    @Override
    public Function<Map<String, Attribute>, CustomEdge> getEdgeWithAttributesFactory() {
        return edgeWithAttributesFactory;
    }

}
