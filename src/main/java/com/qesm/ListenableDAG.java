package com.qesm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.ListenableGraph;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.event.VertexSetListener;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;

public class ListenableDAG<V, E> extends DirectedAcyclicGraph<V, E> implements ListenableGraph<V, E> {
    private ArrayList<GraphListener<V, E>> graphListenersForInsertion = new ArrayList<GraphListener<V, E>>();
    private ArrayList<GraphListener<Set<V>, E>> graphListenersForRemoval = new ArrayList<GraphListener<Set<V>, E>>();
    private ArrayList<VertexSetListener<V>> vertexSetListeners = new ArrayList<>();

    public ListenableDAG(Class<? extends E> edgeClass) {
        super(edgeClass);
    }

    public V computeRootNode() {
        for (V node : vertexSet()) {
            if (outDegreeOf(node) == 0) {
                return node;
            }
        }
        System.err.println("ERROR: there isn't a root node");
        return null;
    }

    // TODO: use this directly in AbstractWorkflow not here
    public void checkRootNode() {
        // Check if root node exists and is unique
        Integer rootNodeCounter = 0;
        for (V node : vertexSet()) {
            if (outDegreeOf(node) == 0) {
                rootNodeCounter++;
            }
        }

        System.out.println(this);
        System.out.println(rootNodeCounter);

        if (rootNodeCounter != 1) {
            throw new RuntimeException("Error: Dag's root node not exists or it's not unique");
        }
    }

    @Override
    public void addGraphListener(GraphListener<V, E> l) {
        graphListenersForInsertion.add(l);
    }

    public void addGraphListenerForRemoval(GraphListener<Set<V>, E> l) {
        graphListenersForRemoval.add(l);
    }

    @Override
    public void removeGraphListener(GraphListener<V, E> l) {
        graphListenersForInsertion.remove(l);
    }

    public void removeGraphListenerForRemoval(GraphListener<Set<V>, E> l) {
        graphListenersForRemoval.remove(l);
    }

    @Override
    public void addVertexSetListener(VertexSetListener<V> l) {
        vertexSetListeners.add(l);

    }

    @Override
    public void removeVertexSetListener(VertexSetListener<V> l) {
        vertexSetListeners.remove(l);
    }

    @Override
    public boolean addVertex(V v) {
        if (!checkDuplicateNames(v)) {
            throw new RuntimeException(
                    "Error: a product with the same name of " + v.toString() + " is already present in the dag");
        }
        boolean added = super.addVertex(v);
        if (added && graphListenersForInsertion.size() > 0) {
            checkRootNode();
            notifyVertexAdded(v);

        }

        return added;
    }

    @Override
    public boolean removeVertex(V v) {
        Set<V> ancestors = getAncestors(v);
        Set<V> descendants = getDescendants(v);
        boolean removed = super.removeVertex(v);
        if (removed && graphListenersForRemoval.size() > 0) {
            checkRootNode();
            V rootNode = computeRootNode();
            List<V> nodesToBeRemoved = new ArrayList<>();
            for (V ancestor : ancestors) {
                if (!getDescendants(ancestor).contains(rootNode)) {
                    nodesToBeRemoved.add(ancestor);
                }
            }
            super.removeAllVertices(nodesToBeRemoved);

            notifyVertexRemoved(descendants);

        }

        return removed;
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {

        E edge = super.addEdge(sourceVertex, targetVertex);
        if (edge != null && graphListenersForInsertion.size() > 0) {
            checkRootNode();
            notifyEdgeAdded(edge);
        }

        return edge;
    }

    @Override
    public boolean removeEdge(E e) {
        boolean removed = super.removeEdge(e);
        if (removed && graphListenersForRemoval.size() > 0) {
            checkRootNode();
            notifyEdgeRemoved(e);
        }

        return removed;
    }

    private void notifyVertexAdded(V v) {
        GraphVertexChangeEvent<V> event = new GraphVertexChangeEvent<V>(this, GraphVertexChangeEvent.VERTEX_ADDED, v);
        for (GraphListener<V, E> listener : graphListenersForInsertion) {
            listener.vertexAdded(event);
        }
    }

    private void notifyVertexRemoved(Set<V> descendants) {
        GraphVertexChangeEvent<Set<V>> event = new GraphVertexChangeEvent<Set<V>>(this,
                GraphVertexChangeEvent.VERTEX_REMOVED, descendants);
        for (GraphListener<Set<V>, E> listener : graphListenersForRemoval) {
            listener.vertexRemoved(event);
        }
    }

    private void notifyEdgeAdded(E e) {
        GraphEdgeChangeEvent<V, E> event = new GraphEdgeChangeEvent<V, E>(this, GraphEdgeChangeEvent.EDGE_ADDED, e,
                getEdgeSource(e), getEdgeTarget(e));
        for (GraphListener<V, E> listener : graphListenersForInsertion) {
            listener.edgeAdded(event);
        }
    }

    private void notifyEdgeRemoved(E e) {
        GraphEdgeChangeEvent<Set<V>, E> event = new GraphEdgeChangeEvent<Set<V>, E>(this,
                GraphEdgeChangeEvent.EDGE_REMOVED, e,
                Set.of(getEdgeSource(e)), Set.of(getEdgeTarget(e)));
        for (GraphListener<Set<V>, E> listener : graphListenersForRemoval) {
            listener.edgeRemoved(event);
        }
    }

    private Boolean checkDuplicateNames(V vertexToCheck) {
        if (vertexToCheck instanceof AbstractProduct) {
            AbstractProduct castedVertexToCheck = (AbstractProduct) vertexToCheck;
            String nameToCheck = castedVertexToCheck.getName();
            for (V vertex : vertexSet()) {
                AbstractProduct castedVertex = (AbstractProduct) vertex;
                if (castedVertex.getName().equals(nameToCheck)) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
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

        ListenableDAG<V, E> dagToCompare = uncheckedCast(obj);

        // Convert HashSets to ArrayLists because hashset.equals() is based on
        // hashCode() and we have only overloaded equals() (In all our classes) not
        // hashCode()

        List<V> vertexListToCompare = new ArrayList<>(dagToCompare.vertexSet());
        List<V> vertexList = new ArrayList<>(this.vertexSet());

        // Check if all element of a list are contained in the other and vice versa
        // (very inneficent, need to implement custum hashcode if it will be developed
        // further)
        for (V vertex : vertexList) {
            Boolean isContained = false;
            for (V vertexToCompare : vertexListToCompare) {
                if (vertex.equals(vertexToCompare)) {
                    isContained = true;
                    break;
                }
            }
            if (!isContained) {
                return false;
            }
        }

        for (V vertexToCompare : vertexListToCompare) {
            Boolean isContained = false;
            for (V vertex : vertexList) {
                if (vertexToCompare.equals(vertex)) {
                    isContained = true;
                    break;
                }
            }
            if (!isContained) {
                return false;
            }
        }

        List<E> edgeListToCompare = new ArrayList<>(dagToCompare.edgeSet());
        List<E> edgeList = new ArrayList<>(this.edgeSet());

        for (E customEdge : edgeList) {
            Boolean isContained = false;
            for (E customEdgeToCompare : edgeListToCompare) {
                if (customEdge.equals(customEdgeToCompare)) {
                    isContained = true;
                    break;
                }
            }
            if (!isContained) {
                return false;
            }
        }

        for (E customEdgeToCompare : edgeListToCompare) {
            Boolean isContained = false;
            for (E customEdge : edgeList) {
                if (customEdgeToCompare.equals(customEdge)) {
                    isContained = true;
                    break;
                }
            }
            if (!isContained) {
                return false;
            }
        }

        return true;
    }

    // Jgrapht does the same
    @SuppressWarnings("unchecked")
    private ListenableDAG<V, E> uncheckedCast(Object o) {
        return (ListenableDAG<V, E>) o;
    }

    @Override
    public String toString() {
        String dagInfo = "";
        Iterator<V> iter = new DepthFirstIterator<V, E>(this);
        while (iter.hasNext()) {
            V vertex = iter.next();
            dagInfo += vertex.toString();
            if (this.outDegreeOf(vertex) > 0) {
                dagInfo += " is connected to: \n";
                for (E connectedEdge : this.outgoingEdgesOf(vertex)) {
                    dagInfo += "\t" + connectedEdge.toString() + "\n";
                }
            } else {
                dagInfo += "\n";
            }
        }
        return dagInfo;
    }
}
