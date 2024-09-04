package com.qesm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.traverse.DepthFirstIterator;

public abstract class AbstractWorkflow<V extends AbstractProduct> implements DotFileConverter<V>, Serializable {

    protected DirectedAcyclicGraph<V, CustomEdge> dag;
    protected HashMap<V, AbstractWorkflow<V>> productToSubWorkflowMap;
    protected transient final Class<V> vertexClass;
    protected Boolean isTopTierGraph = false;

    public AbstractWorkflow(Class<V> vertexClass, Boolean isTopTierGraph) {
        this.vertexClass = vertexClass;
        this.dag = new DirectedAcyclicGraph<V, CustomEdge>(CustomEdge.class);
        this.isTopTierGraph = isTopTierGraph;

        if (isTopTierGraph) {
            this.productToSubWorkflowMap = new HashMap<V, AbstractWorkflow<V>>();
            updateAllSubgraphs();
        }
    }

    public AbstractWorkflow(DirectedAcyclicGraph<V, CustomEdge> dagToImport, Class<V> vertexClass,
            Boolean isTopTierGraph) {
        this.vertexClass = vertexClass;
        this.dag = dagToImport;
        this.isTopTierGraph = isTopTierGraph;

        validateWorkflow();
        if (isTopTierGraph) {
            this.productToSubWorkflowMap = new HashMap<V, AbstractWorkflow<V>>();
            updateAllSubgraphs();
        }
    }

    public String computeParallelismValue() {

        // First convert to Unshared (saving a reference to originalDag to restore at
        // the end)
        DirectedAcyclicGraph<V, CustomEdge> originalDag = dag;
        toUnshared();

        // Then compute metrics
        int A = 0;
        // A paramater: sum all incoming edges of Processed node if they have more than
        // 2 incoming edges
        for (V node : dag.vertexSet()) {
            if (node.getItemGroup() == AbstractProduct.ItemGroup.PROCESSED && dag.inDegreeOf(node) >= 2) {
                A += dag.inDegreeOf(node);
            }
        }
        ;

        // B parameter: Load Balance Factor
        float B = 0;

        V unsharedRootNode = computeRootNode();

        Set<V> processedChildNodes = dag.incomingEdgesOf(unsharedRootNode).stream().map(dag::getEdgeSource)
                .filter(v -> v.isProcessed()).collect(Collectors.toSet());
        if (processedChildNodes.size() > 1) {
            // processedChildNodes.forEach(n -> System.out.println("Node: " + n.getName() +
            // " - Item: " + n.getItemGroup()));
            int[] numNodesInSubgraphs = new int[processedChildNodes.size()];
            int index = 0;
            for (V node : processedChildNodes) {
                numNodesInSubgraphs[index] = dag.getAncestors(node).size();
                // System.out.println("Index: " + index + ", Node: " + node.getName() + " -> " +
                // numNodesInSubgraphs[index]);
                index++;
            }

            // for (int i = 0; i < numNodesInSubgraphs.length; i++){
            // System.out.println("[" + i +"] Val " + numNodesInSubgraphs[i]);
            // }

            float mean = (Arrays.stream(numNodesInSubgraphs).sum()) / processedChildNodes.size();
            // System.out.println("Mean: " + mean);
            float sumOfSquares = 0;
            for (int i = 0; i < processedChildNodes.size(); i++) {
                sumOfSquares += Math.pow((numNodesInSubgraphs[i] - mean), 2);
            }
            float loadBalanceFactor = 1.0f / (processedChildNodes.size()) * sumOfSquares;
            B = loadBalanceFactor;
        }

        double C = 0;

        Set<V> totalProcessedNodes = dag.getAncestors(unsharedRootNode).stream().filter(v -> v.isProcessed())
                .collect(Collectors.toSet());
        Set<V> processedLeavesNodes = totalProcessedNodes.stream()
                .filter(v -> dag.incomingEdgesOf(v).stream().allMatch(e -> !dag.getEdgeSource(e).isProcessed()))
                .collect(Collectors.toSet());
        List<Integer> distancesToRoot = processedLeavesNodes.stream().map(v -> dag.getDescendants(v).size())
                .collect(Collectors.toList());

        double averageDistance = distancesToRoot.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        C = distancesToRoot.stream().map(distance -> Math.pow(distance - averageDistance, 2)).reduce(0.0,
                (a, b) -> a + b);
        C /= processedLeavesNodes.size();

        // 1 - 9 : (16 + 16)/2 = 16
        // 1 - 1 -8 : (5,43 +5,43 + 21.8)/3 = 10.88
        // 1 - 8 - 8 : (21.71 + 5.44 + 5.44)/3 = 10.86

        Integer totalProcessedNodesNum = totalProcessedNodes.size();
        Double maxUnbalance = Math.pow(totalProcessedNodesNum / 2.0 - 1, 2)
                + Math.pow(totalProcessedNodesNum / 2.0 - totalProcessedNodesNum - 1, 2);
        // C /= maxUnbalance * 100;
        C = C == 0 ? 0 : 100 * (Math.log(C + 1) / Math.log(maxUnbalance + 1));
        // System.out.println(C);

        // Restore originalDag
        this.dag = originalDag;

        // TODO: Probably we don't need to validate again, control just to be sure
        // validateWorkflow();

        return "A: " + A + "    OldLoadUnbalanceFactor: " + Math.round(B * 100.0) / 100.0
                + "    NewLoadUnbalanceFactor: " + Math.round(C * 100.0) / 100.0 + "%";
    }

    @Override
    public DirectedAcyclicGraph<V, CustomEdge> cloneDag() {
        return uncheckedCast(dag.clone());
    }

    @Override
    public void setDag(DirectedAcyclicGraph<V, CustomEdge> dagToSet) {
        dag = dagToSet;
        validateWorkflow();
        if (isTopTierGraph) {

            updateAllSubgraphs();
        }
    }

    @Override
    public Class<V> getVertexClass() {
        return this.vertexClass;
    }

    public V computeRootNode() {
        V root = null;
        for (V node : dag.vertexSet()) {
            if (dag.outDegreeOf(node) == 0) {
                root = node;
                break;
            }
        }
        return root;
    }

    public AbstractWorkflow<V> getProductWorkflow(V node) {
        return productToSubWorkflowMap.get(node);
    }

    public HashMap<V, AbstractWorkflow<V>> getProductToSubWorkflowMap() {
        return this.productToSubWorkflowMap;
    }

    public Boolean isTopTier() {
        return isTopTierGraph;
    }

    @Override
    public String toString() {
        return dag.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        AbstractWorkflow<V> workflowToCompare = uncheckedCast(obj);

        if (!this.dag.equals(workflowToCompare.cloneDag())) {
            return false;
        }
        return true;
    }

    public <T extends AbstractProduct, P extends AbstractWorkflow<T>> boolean equalsNodesAttributes(
            AbstractWorkflow<T> workflowToCompare) {
        if (this == workflowToCompare) {
            return true;
        }
        if (workflowToCompare == null) {
            return false;
        }

        if (dag.vertexSet().size() != workflowToCompare.dag.vertexSet().size()) {
            return false;
        }

        if (dag.edgeSet().size() != workflowToCompare.dag.edgeSet().size()) {
            return false;
        }

        ArrayList<CustomEdge> workflowEdges = new ArrayList<>(dag.edgeSet());
        ArrayList<CustomEdge> workflowToCompareEdges = new ArrayList<>(workflowToCompare.dag.edgeSet());

        for (CustomEdge customEdge : workflowEdges) {
            Boolean edgeFound = false;
            for (CustomEdge customEdgeToCompare : workflowToCompareEdges) {
                if (customEdge.equalsNodesAttributes(customEdgeToCompare)) {
                    edgeFound = true;
                    break;
                }
            }
            if (!edgeFound) {
                return false;
            }
        }

        ArrayList<V> workflowNodes = new ArrayList<>();
        ArrayList<T> workflowToCompareNodes = new ArrayList<>();

        Iterator<V> iterWorkflow = new DepthFirstIterator<V, CustomEdge>(dag);
        while (iterWorkflow.hasNext()) {
            workflowNodes.add(iterWorkflow.next());
        }

        Iterator<T> iterWorkflowToCompare = new DepthFirstIterator<T, CustomEdge>(workflowToCompare.dag);
        while (iterWorkflowToCompare.hasNext()) {
            workflowToCompareNodes.add(iterWorkflowToCompare.next());
        }

        Integer totalNodes = dag.vertexSet().size();
        for (int nodeNumber = 0; nodeNumber < totalNodes; nodeNumber++) {
            V node = workflowNodes.get(nodeNumber);
            T nodeToCompare = workflowToCompareNodes.get(nodeNumber);
            if (!node.equalsAttributes(nodeToCompare)) {
                return false;
            }
        }

        return true;
    }

    public void toUnshared() {
        DAGSharedToUnsharedConverter<V> dagConverter = new DAGSharedToUnsharedConverter<V>(dag, computeRootNode(),
                vertexClass);
        dag = dagConverter.makeConversion();
        validateWorkflow();
        if (isTopTierGraph) {

            updateAllSubgraphs();
        }
    }

    public Optional<V> findProduct(String productName) {
        for (V product : dag.vertexSet()) {
            if (product.getName().equals(productName)) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    // Jgrapht does the same
    @SuppressWarnings("unchecked")
    private <T> T uncheckedCast(Object o) {
        return (T) o;
    }

    // Exposing CRUD methods of dag
    public CustomEdge connectVertex(V newVertex, V targetVertex) {
        boolean added = dag.addVertex(newVertex);
        if (added) {
            // If we enter here it means it's first time we create vertex and it's not
            // possible that there are edges associated to this vertex. For this reason the
            // addEdge method is always going to return an Edge
            CustomEdge e = dag.addEdge(newVertex, targetVertex);
            validate(Set.of(targetVertex), Validation.LEAF_NODES);
            buildChangedSubGraphs(dag.getDescendants(newVertex));

            return e;
        }
        return null;
    }

    public boolean removeVertex(V v) {
        if (v == null || !dag.containsVertex(v)) {
            return false;
        }
        Set<V> ancestors = dag.getAncestors(v);
        Set<V> descendants = dag.getDescendants(v);
        V rootNode = computeRootNode();
        boolean removed = false;
        if (v != rootNode) {
            removed = dag.removeVertex(v);
        }

        // If succefully removed, compute and remove also all pendents nodes (nodes that
        // can't reach root node)
        if (removed) {
            List<V> nodesToBeRemoved = new ArrayList<>();
            for (V ancestor : ancestors) {
                if (!dag.getDescendants(ancestor).contains(rootNode)) {
                    nodesToBeRemoved.add(ancestor);
                }
            }
            dag.removeAllVertices(nodesToBeRemoved);
            buildChangedSubGraphs(descendants);
        }

        return removed;
    }

    public CustomEdge addEdge(V sourceVertex, V targetVertex) {

        CustomEdge edge = dag.addEdge(sourceVertex, targetVertex);
        if (edge != null) {
            validate(Set.of(targetVertex), Validation.LEAF_NODES);
            Set<V> changedVertexes = dag.getDescendants(targetVertex);
            changedVertexes.add(targetVertex);
            buildChangedSubGraphs(changedVertexes);
        }

        return edge;
    }

    public boolean removeEdge(V sourceVertex, V targetVertex) {
        return removeEdge(dag.getEdge(sourceVertex, targetVertex));
    }

    public boolean removeEdge(CustomEdge e) {
        if (e == null) {
            return false;
        }
        V targetVertex = dag.getEdgeTarget(e);
        V sourceVertex = dag.getEdgeSource(e);
        V rootNode = computeRootNode();
        Set<V> possiblyPendantVertexes = dag.getAncestors(sourceVertex);
        possiblyPendantVertexes.add(sourceVertex);
        boolean removed = dag.removeEdge(e);
        if (removed) {
            List<V> nodesToBeRemoved = new ArrayList<>();
            for (V possiblyPendantVertex : possiblyPendantVertexes) {
                if (!dag.getDescendants(possiblyPendantVertex).contains(rootNode)) {
                    nodesToBeRemoved.add(possiblyPendantVertex);
                }
            }
            dag.removeAllVertices(nodesToBeRemoved);
            Set<V> changedVertexes = dag.getDescendants(targetVertex);
            changedVertexes.add(targetVertex);
            buildChangedSubGraphs(changedVertexes);
        }

        return removed;
    }

    // Validation methods
    private enum Validation {
        ROOT_NODE,
        LEAF_NODES
    }

    private Boolean checkRootNode() {
        // Check if root node exists and is unique
        Integer rootNodeCounter = 0;
        for (V node : dag.vertexSet()) {
            if (dag.outDegreeOf(node) == 0) {
                rootNodeCounter++;
            }
        }

        if (rootNodeCounter != 1) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean checkLeafNodes(Set<V> vertexToCheck) {
        // all raw_materials should be leaf nodes
        for (V node : vertexToCheck) {
            if (node.isRawMaterial() && dag.inDegreeOf(node) != 0) {
                return false;
            }
        }
        return true;
    }

    public void validateWorkflow() throws WorkflowValidationException, RuntimeException {
        validate(dag.vertexSet(), Validation.ROOT_NODE, Validation.LEAF_NODES);
    }

    private void validate(Set<V> vertexToCheck, Validation... args)
            throws WorkflowValidationException, RuntimeException {

        for (Validation validation : args) {
            // Apply a single validation
            switch (validation) {
                case ROOT_NODE:
                    if (!checkRootNode()) {
                        throw new WorkflowValidationException("Validation error: Root node (duplicate or none)");
                    }
                    break;
                case LEAF_NODES:
                    if (!checkLeafNodes(vertexToCheck)) {
                        throw new WorkflowValidationException("Validation error: leaves nodes (wrong itemGroup)");
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown validation enum value");
            }
        }
    }

    protected void updateAllSubgraphs() {
        buildChangedSubGraphs(dag.vertexSet());
    }

    protected abstract AbstractWorkflow<V> buildWorkflow(DirectedAcyclicGraph<V, CustomEdge> dag);

    protected void buildChangedSubGraphs(Set<V> vertexSet) {
        for (V product : vertexSet) {
            if (product.isProcessed()) {
                productToSubWorkflowMap.put(product, buildWorkflow(createSubgraph(dag, product)));
            }
        }
    }

    protected DirectedAcyclicGraph<V, CustomEdge> createSubgraph(DirectedAcyclicGraph<V, CustomEdge> originalDAG,
            V root) {
        DirectedAcyclicGraph<V, CustomEdge> subgraphDAG = new DirectedAcyclicGraph<>(CustomEdge.class);

        Set<V> subgraphVertices = originalDAG.getAncestors(root);
        subgraphVertices.add(root);

        // Add vertices and edges to the subgraph
        for (V vertex : subgraphVertices) {
            subgraphDAG.addVertex(vertex);
        }

        for (V vertex : subgraphVertices) {
            Set<CustomEdge> edges = originalDAG.outgoingEdgesOf(vertex);
            for (CustomEdge edge : edges) {
                V target = originalDAG.getEdgeTarget(edge);
                if (subgraphVertices.contains(target)) {
                    CustomEdge subgraphEdge = new CustomEdge();
                    subgraphEdge.setQuantityRequired(edge.getQuantityRequired());
                    subgraphDAG.addEdge(vertex, target, subgraphEdge);
                }
            }
        }

        return subgraphDAG;
    }

    @Override
    public Supplier<Map<String, Attribute>> getGraphAttributeProvider() {
        Supplier<Map<String, Attribute>> graphAttributeProvider = () -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("rankdir", new DefaultAttribute<String>("BT", AttributeType.STRING));
            map.put("label",
                    new DefaultAttribute<String>("\"" + computeParallelismValue() + "\"", AttributeType.STRING));
            map.put("labelloc", new DefaultAttribute<String>("\"t\"", AttributeType.STRING));
            map.put("labeljust", new DefaultAttribute<String>("\"l\"", AttributeType.STRING));
            map.put("fontsize", new DefaultAttribute<Integer>(30, AttributeType.INT));
            return map;
        };

        return graphAttributeProvider;
    }
}
