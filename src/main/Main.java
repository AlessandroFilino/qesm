package main;

// import java.util.List;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        RawMaterialType d = new RawMaterialType("d", 1);
        RawMaterialType e = new RawMaterialType("e", 2);
        RawMaterialType f = new RawMaterialType("f", 2);

        TransformationType t1 = new TransformationType("t1", 100, 
            new HashMap<>() {{
                put(d, 10);
                put(e, 5);
            }});
        
        TransformationType t2 = new TransformationType("t1", 200, 
            new HashMap<>() {{
                put(f, 15);
            }});

        ProcessedType b = new ProcessedType("b", 1, t1);
        ProcessedType c = new ProcessedType("c", 1, t2);

        TransformationType t3 = new TransformationType("t3", 300, 
            new HashMap<>() {{
                put(b, 2);
                put(c, 3);
            }});
        
        ProcessedType a = new ProcessedType("a", 1, t3);    

        Node A = new Node(a);

        A.generateGraph();
        A.serializeGraphToJson("./output/example.json");

    }

    // public void getSubGraph(ProductType product){
    //     List<TransformationType> requirements = product.getRequirements();
    //     if (requirements.isEmpty()){
    //     } 
    //     else {
    //         for (TransformationType requirement : requirements) {
    //             System.out.println(requirement.getProductType().getNameType());
    //             getSubGraph(requirement.getProductType());
    //         }
    //     }
    // }
}
