package com.qesm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.ProductType.ItemType;

public class RandomDAGGenerator{

    private DirectedAcyclicGraph<ProductType, CustomEdge> dag;

    // Level definition: hop distance from root. A node can belong to more than one level
    private int maxHeight;                  //Max level number
    private int maxWidth;                   //Max number of vertex in each level
    private int maxBranchingUpFactor;       //Max number of incoming edge for each node
    private int maxBranchingDownFactor;     //Max number of outcoming edge for each node
    private int branchingUpProbability;     // Probaility (range [0, 100]) of having more than one up branch
    
    private Random random;
    private int vId;
    private int maxRandomQuantity;
    private ProductType rootNode;
    private Supplier<ProductType> vSupplierRandom;
    private Supplier<ProductType> vSupplierProcessedType;
    public enum PdfType{
        DETERMINISTIC,
        ERLANG,
        EXPOLYNOMIAL,
        EXPONENTIAL,
        HISTOGRAM,
        TRUNCEXPMIX,
        TRUNCEXP,
        UNIFORM
    };
    private PdfType pdfType;

    public RandomDAGGenerator(int maxHeight, int maxWidth, int maxBranchingUpFactor, int maxBranchingDownFactor, int branchingUpProbability, PdfType pdfType){
        
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.maxBranchingUpFactor = maxBranchingUpFactor;
        this.maxBranchingDownFactor = maxBranchingDownFactor;
        this.branchingUpProbability = branchingUpProbability;

        this.random = new Random();
        this.vId = 0;
        this.maxRandomQuantity = 10;
        this.pdfType = pdfType;
        this.vSupplierRandom = new Supplier<ProductType>()
        {
            @Override
            public ProductType get()
            {
                ProductType vertex;

                if(random.nextBoolean()){
                    vertex = new ProductType("v" + vId, ItemType.RAW_MATERIAL);
                    vId++;
                }
                else{
                    vertex = new ProductType("v" + vId, ItemType.PROCESSED);
                    vertex.setQuantityProduced(-1);
                    vertex.setPdf(getRandomPdf());
                    vId++;
                }
                
                return vertex;
            }
        };

        this.vSupplierProcessedType = new Supplier<ProductType>()
        {
            @Override
            public ProductType get()
            {
                ProductType vertex = new ProductType("v" + vId, ItemType.PROCESSED);
                vertex.setQuantityProduced(-1);
                vertex.setPdf(getRandomPdf());
                vId++;
                return vertex;
            }
        };
    }

    private StochasticTime getRandomPdf(){

        StochasticTime pdf = null;
        double eft = Math.round(random.nextDouble(0, 10));
        double lft = Math.round(random.nextDouble(eft + 1, eft + 10));

        switch (pdfType) {
            case DETERMINISTIC:
                pdf = new DeterministicTime(BigDecimal.valueOf(random.nextInt(0, 10)));
                break;
            case ERLANG:
                pdf = new ErlangTime(random.nextInt(1, 10) , random.nextDouble(0, 3) + 0.1);
                break;
            // case EXPOLYNOMIAL:
            //     pdf = new ExpolynomialTime(null, null, null);
            //     break;
            case EXPONENTIAL:
                pdf = new ExponentialTime(BigDecimal.valueOf(random.nextDouble(0, 10) + 0.1));
                break;
            // case HISTOGRAM:
            //     pdf = new HistogramTime(null, null, null, null);
            //     break;
            // case TRUNCEXPMIX:
            //     pdf = new TruncatedExponentialMixtureTime(null, null);
            //     break;
            case TRUNCEXP:
                pdf = new TruncatedExponentialTime(eft, lft, random.nextDouble(0, 10) + 0.1);
                break;
            case UNIFORM:
                pdf = new UniformTime(eft, lft);
                break;
            default:
                System.err.println("Error pdfType: " + pdfType + " not supported in randomGeneration");
                break;
        }
        
        return pdf;
    }

    public void generateGraph(DirectedAcyclicGraph<ProductType, CustomEdge> dag){

        this.dag = dag;

        class DAGPopulator{
            
            private HashMap<ProductType, ArrayList<Integer>> vertexToLevels = new HashMap<ProductType, ArrayList<Integer>>();
            private HashMap<Integer, ArrayList<ProductType>> levelToVertices = new HashMap<Integer, ArrayList<ProductType>>();
            private ArrayList<ProductType> vTargetList = new ArrayList<ProductType>();
            private ArrayList<Integer> changedLevelsToBeValidated;
            private ProductType sourceVertex;
            private ArrayList<ProductType> vTargetListCopy;
            private ProductType targetVertex;



            private void populate(){
                dag.setVertexSupplier(vSupplierProcessedType);

                rootNode = dag.addVertex();

                rootNode.setQuantityProduced(1);
                vertexToLevels.put(rootNode, new ArrayList<Integer>(List.of(0)));
                levelToVertices.put(0, new ArrayList<ProductType>(List.of(rootNode)));
                vTargetList.add(rootNode);

                dag.setVertexSupplier(vSupplierRandom);

                
                while(true) {
                    
                    sourceVertex = dag.addVertex();
                    vTargetListCopy = new ArrayList<ProductType>(vTargetList);

                    // Calculating branchingUpFactor to limit not well nested DAG
                    Integer branchingUpFactor;
                    if(random.nextInt(1, 101) > branchingUpProbability){
                        branchingUpFactor = random.nextInt(maxBranchingUpFactor) + 1;
                    }
                    else{
                        branchingUpFactor = 1;
                    }
                     

                    while (!vTargetListCopy.isEmpty()) {
                        targetVertex = vTargetListCopy.remove(random.nextInt(vTargetListCopy.size()));

                        boolean nonValidTarget = false;
                        changedLevelsToBeValidated = new ArrayList<Integer>();

                        // Calculate possible source levels if it will be connected to target
                        ArrayList<Integer> sourceLevels = new ArrayList<Integer>();
                        for (Integer targeLevel : vertexToLevels.get(targetVertex)) {
                            sourceLevels.add(targeLevel + 1);   
                        }

                        // check maxHeight
                        for (Integer sourceLevel : sourceLevels) {
                            if(sourceLevel > maxHeight){
                                nonValidTarget = true;
                                // System.out.println(sourceVertex.getNameType() + " -> " + targetVertex.getNameType() + " not valid for: maxHeight");
                                break;
                            }  
                        }
                        if(nonValidTarget){
                            continue;
                        }

                        // check maxWidth
                        for (Integer sourceLevel : sourceLevels) {
                            if(!levelToVertices.containsKey(sourceLevel)){
                                levelToVertices.put(sourceLevel, new ArrayList<ProductType>());
                            }
                            
                            if(levelToVertices.get(sourceLevel).size() + 1 > maxWidth){
                                nonValidTarget = true;
                                // System.out.println(sourceVertex.getNameType() + " -> " + targetVertex.getNameType() + " not valid for: maxWidth");
                                break;
                            }
                            
                            // System.out.println("sourceLevel: " + sourceLevel + " width: " + levelToVertices.get(sourceLevel).size());
                            // System.out.println(sourceVertex.getNameType() + " -> " + targetVertex.getNameType());
                            levelToVertices.get(sourceLevel).add(sourceVertex);
                            changedLevelsToBeValidated.add(sourceLevel);
                        }
                        if(nonValidTarget){
                            resetChangesToLevels();
                            continue;
                        }

                        // check maxBranchingDownFactor
                        if(dag.inDegreeOf(targetVertex) + 1 > maxBranchingDownFactor){
                            // System.out.println(sourceVertex.getNameType() + " -> " + targetVertex.getNameType() + " not valid for: BFDown");
                            resetChangesToLevels();
                            continue;
                        }

                        // check maxBranchinUpFactor
                        if(dag.outDegreeOf(sourceVertex) + 1 > branchingUpFactor){  
                            // System.out.println(sourceVertex.getNameType() + " -> " + targetVertex.getNameType() + " not valid for: BFUp");
                            resetChangesToLevels();
                            break;
                        }

                        CustomEdge newEdge = dag.addEdge(sourceVertex, targetVertex);
                        newEdge.setQuantityRequired(random.nextInt(maxRandomQuantity) + 1);

                        // Update vertexToLevels
                        if(!vertexToLevels.containsKey(sourceVertex)){
                            vertexToLevels.put(sourceVertex, new ArrayList<Integer>());
                        }
                        for (Integer sourceLevel : sourceLevels) {
                            vertexToLevels.get(sourceVertex).add(sourceLevel);
                        }

                    }

                    // Update TargetList if vertex is connected to graph
                    if(dag.outDegreeOf(sourceVertex) > 0){
                        if(sourceVertex.getItemType() == ItemType.PROCESSED){
                            vTargetList.add(sourceVertex);
                        }
                    }
                    else{
                        // if vertex is not connected, it means that we can't append any more vertex to the DAG   
                        dag.removeVertex(sourceVertex);
                        break;
                    }

                }
            }

            private void resetChangesToLevels(){
                for (Integer levelFromWhichRemove : changedLevelsToBeValidated) {
                    if(levelToVertices.containsKey(levelFromWhichRemove)){
                        levelToVertices.get(levelFromWhichRemove).remove(sourceVertex);
                        // System.out.println("Removed from level: " + levelFromWhichRemove + " " + sourceVertex.getNameType() + " -> " + targetVertex.getNameType());
                    }
                }
            }

        }

        DAGPopulator dagPopulator = new DAGPopulator();
        dagPopulator.populate();
        setLeafNodes();
        updateQuantityProduced();
    }

    private void setLeafNodes(){
        
        // Copy vertex set to avoid modifying a collection while iterating over it (it can lead to a ConcurrentModificationException)
        Set<ProductType> vertexSetCopy = new HashSet<ProductType>(dag.vertexSet());

        // Substitute every processedType leaf with rawMaterialType
        for (ProductType node : vertexSetCopy) {
            if(dag.inDegreeOf(node) == 0 && node.getItemType() == ItemType.PROCESSED){
                ArrayList<CustomEdge> oldEdges = new ArrayList<CustomEdge>();
                for (CustomEdge oldEdge : dag.outgoingEdgesOf(node)) {
                    oldEdges.add(oldEdge);
                }

                dag.removeVertex(node);
                ProductType newLeaf = new ProductType(node.getNameType(), ItemType.RAW_MATERIAL);
                dag.addVertex(newLeaf);

                for (CustomEdge oldEdge : oldEdges) {
                    dag.addEdge(newLeaf, dag.getEdgeTarget(oldEdge), new CustomEdge(oldEdge));
                }
            }
        }
    }

    private void updateQuantityProduced(){
        // Set quantityProduced according to upper nodes requirements
        for (ProductType node : dag.vertexSet()) {
            if(node.getItemType() == ItemType.PROCESSED && dag.outDegreeOf(node) > 0){
                int totalQuantityNeeded = 0;
                for (CustomEdge outEdge : dag.outgoingEdgesOf(node)) {
                    totalQuantityNeeded += outEdge.getQuantityRequired();
                }
                node.setQuantityProduced(totalQuantityNeeded);
            }
        }
    }


    public ProductType getRootNode() {
        return rootNode;
    }

}