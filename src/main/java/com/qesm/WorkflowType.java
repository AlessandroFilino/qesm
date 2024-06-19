package com.qesm;

import java.util.HashMap;
import java.util.Set;

import org.jgrapht.graph.DirectedAcyclicGraph;

import com.qesm.ProductType.ItemType;
import com.qesm.RandomDAGGenerator.PdfType;


public class WorkflowType extends AbstractWorkflow<ProductType>{


    public WorkflowType() {
        super(ProductType.class);
    }

    public WorkflowType(DirectedAcyclicGraph<ProductType, CustomEdge> dagToImport) {
        super(dagToImport, ProductType.class);
    }

    public void generateRandomDAG(int maxHeight, int maxWidth, int maxBranchingUpFactor, int maxBranchingDownFactor, int branchingUpProbability, PdfType pdfType) {
        RandomDAGGenerator randDAGGenerator = new RandomDAGGenerator(maxHeight, maxWidth, maxBranchingUpFactor,
                maxBranchingDownFactor, branchingUpProbability, pdfType);
        randDAGGenerator.generateGraph(dag);
    }

    // TODO: Should "toUnshared" be a superclass method?
    public void toUnshared() {
        DAGSharedToUnsharedConverter dagConverter = new DAGSharedToUnsharedConverter(dag, getRootNode());
        dag = dagConverter.makeConversion();
    }

    public WorkflowIstance makeIstance() {
        DirectedAcyclicGraph<ProductIstance, CustomEdge> dagIstance = new DirectedAcyclicGraph<>(CustomEdge.class);

        HashMap<ProductType, ProductIstance> productTypeToProductMap = new HashMap<>();

        // Deepcopy of all vertexes
        for (ProductType vertex : dag.vertexSet()) {
            ProductIstance product = new ProductIstance(vertex);
            dagIstance.addVertex(product);
            productTypeToProductMap.put(vertex, product);
        }

        // Add all the edges from the original DAG to the copy 
        for (CustomEdge edge : dag.edgeSet()) {
            ProductType sourceType = dag.getEdgeSource(edge);
            ProductType targetType = dag.getEdgeTarget(edge);
            dagIstance.addEdge(productTypeToProductMap.get(sourceType), productTypeToProductMap.get(targetType), new CustomEdge(edge));
        }

        WorkflowIstance workflow = new WorkflowIstance(dagIstance);


        for (ProductIstance product : dagIstance.vertexSet()) {
            if(product.getItemType() == ItemType.PROCESSED){
                product.setProductWorkflow(buildSubgraphWorkflow(dagIstance, product));
            }
        }

        return workflow;

    }

    private WorkflowIstance buildSubgraphWorkflow(DirectedAcyclicGraph<ProductIstance, CustomEdge> fullDag, ProductIstance currentVertex){
        DirectedAcyclicGraph<ProductIstance, CustomEdge> subGraph = new DirectedAcyclicGraph<>(CustomEdge.class);

        Set<ProductIstance>  subGraphVertexSet = fullDag.getAncestors(currentVertex); 
        subGraphVertexSet.add(currentVertex);

        // Add all subgraph vertexes
        for (ProductIstance product : subGraphVertexSet) {
            subGraph.addVertex(product);
        }

        // Add all subgraph edges
        for (ProductIstance product : subGraphVertexSet) {
            for (CustomEdge edge : fullDag.edgesOf(product)) {

                ProductIstance sourceProduct = fullDag.getEdgeSource(edge);
                if(!subGraphVertexSet.contains(sourceProduct)){
                    continue;
                }

                ProductIstance targetProduct = fullDag.getEdgeTarget(edge);
                if(!subGraphVertexSet.contains(targetProduct)){
                    continue;
                }
                
                subGraph.addEdge(sourceProduct, targetProduct, edge);
            }
            
        }


        WorkflowIstance subgraphWorkflow = new WorkflowIstance(subGraph);
        return subgraphWorkflow;

    }

}
