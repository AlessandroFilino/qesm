package com.qesm;

import java.util.ArrayList;

import org.jgrapht.ListenableGraph;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.event.VertexSetListener;
import org.jgrapht.graph.DirectedAcyclicGraph;

public class ListenableDAG<V,E> extends DirectedAcyclicGraph<V,E> implements ListenableGraph<V, E>{
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
        GraphEdgeChangeEvent<V, E> event = new GraphEdgeChangeEvent<V,E>(this, GraphEdgeChangeEvent.EDGE_ADDED, e, getEdgeSource(e), getEdgeTarget(e));
        for (GraphListener<V, E> listener : graphListeners) {
            listener.edgeAdded(event);
        }
    }

    private void notifyEdgeRemoved(E e) {
        GraphEdgeChangeEvent<V, E> event = new GraphEdgeChangeEvent<V,E>(this, GraphEdgeChangeEvent.EDGE_REMOVED, e, getEdgeSource(e), getEdgeTarget(e));
        for (GraphListener<V, E> listener : graphListeners) {
            listener.edgeRemoved(event);
        }
    }

}
