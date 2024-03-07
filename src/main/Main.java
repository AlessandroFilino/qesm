package main;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        RawMaterialType raw1 = new RawMaterialType("raw1", 1);
        RawMaterialType raw2 = new RawMaterialType("raw2", 2);

        RequirementType rrw1 = new RequirementType("rrw1", 1, raw1);

        TrasformationType t1 = new TrasformationType("t1", 1, List.of(rrw1));

        RequirementType rt1 = new RequirementType("rt1", 1, t1);
        RequirementType rrw2 = new RequirementType("rrw2", 1, raw2);
        TrasformationType t2 = new TrasformationType("t2", 2, List.of(rt1, rrw2));

        main.getSubGraph(t2);



    }

    public void getSubGraph(ProductType product){
        List<RequirementType> requirements = product.getRequirements();
        if (requirements.isEmpty()){
        } 
        else {
            for (RequirementType requirement : requirements) {
                System.out.println(requirement.getProductType().getNameType());
                getSubGraph(requirement.getProductType());
            }
        }
    }
}
