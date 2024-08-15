package com.qesm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.ListenableGraph;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.event.VertexSetListener;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;

public class ListenableDAG<V, E> extends DirectedAcyclicGraph<V, E> implements ListenableGraph<V, E> {
    private ArrayList<GraphListener<V, E>> graphListeners = new ArrayList<GraphListener<V, E>>();
    private ArrayList<VertexSetListener<V>> vertexSetListeners = new ArrayList<>();

    public ListenableDAG(Class<? extends E> edgeClass) {
        super(edgeClass);
    }

    @Override
    public void addGraphListener(GraphListener<V, E> l) {
        graphListeners.add(l);
    }

    @Override
    public void removeGraphListener(GraphListener<V, E> l) {
        graphListeners.remove(l);
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
        if (added && graphListeners.size() > 0) {
            notifyVertexAdded(v);
        }
        return added;
    }

    @Override
    public boolean removeVertex(V v) {
        boolean removed = super.removeVertex(v);
        if (removed && graphListeners.size() > 0) {
            notifyVertexRemoved(v);
        }
        return removed;
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        E edge = super.addEdge(sourceVertex, targetVertex);
        if (edge != null && graphListeners.size() > 0) {
            notifyEdgeAdded(edge);
        }
        return edge;
    }

    @Override
    public boolean removeEdge(E e) {
        boolean removed = super.removeEdge(e);
        if (removed && graphListeners.size() > 0) {
            notifyEdgeRemoved(e);
        }
        return removed;
    }

    private void notifyVertexAdded(V v) {
        GraphVertexChangeEvent<V> event = new GraphVertexChangeEvent<V>(this, GraphVertexChangeEvent.VERTEX_ADDED, v);
        for (GraphListener<V, E> listener : graphListeners) {
            listener.vertexAdded(event);
        }
    }

    private void notifyVertexRemoved(V v) {
        GraphVertexChangeEvent<V> event = new GraphVertexChangeEvent<V>(this, GraphVertexChangeEvent.VERTEX_REMOVED, v);
        for (GraphListener<V, E> listener : graphListeners) {
            listener.vertexRemoved(event);
        }
    }

    private void notifyEdgeAdded(E e) {
        GraphEdgeChangeEvent<V, E> event = new GraphEdgeChangeEvent<V, E>(this, GraphEdgeChangeEvent.EDGE_ADDED, e,
                getEdgeSource(e), getEdgeTarget(e));
        for (GraphListener<V, E> listener : graphListeners) {
            listener.edgeAdded(event);
        }
    }

    private void notifyEdgeRemoved(E e) {
        GraphEdgeChangeEvent<V, E> event = new GraphEdgeChangeEvent<V, E>(this, GraphEdgeChangeEvent.EDGE_REMOVED, e,
                getEdgeSource(e), getEdgeTarget(e));
        for (GraphListener<V, E> listener : graphListeners) {
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
