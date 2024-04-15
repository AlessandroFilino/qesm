package com.qesm;

import java.util.HashMap;
import java.util.UUID;

public class GraphStats {
    private int totNodes;
    private int graphDepth;     
    private HashMap<Integer, Integer> levelWidthCount;
    private HashMap<UUID, Integer> nodeToNumChildren;

    public GraphStats(){
        this.totNodes = 0;
        this.graphDepth = 0;     
        this.levelWidthCount = new HashMap<Integer, Integer>();
        this.nodeToNumChildren = new HashMap<UUID, Integer>();
    }

    public GraphStats(int totNodes, int graphDepth, HashMap<Integer, Integer> levelWidthCount,
            HashMap<UUID, Integer> nodeToNumChildren) {
        this.totNodes = totNodes;
        this.graphDepth = graphDepth;
        this.levelWidthCount = new HashMap<Integer, Integer>();
        this.nodeToNumChildren = new HashMap<UUID, Integer>();
    }
    public int getTotNodes() {
        return totNodes;
    }
    public void setTotNodes(int totNodes) {
        this.totNodes = totNodes;
    }
    public int getGraphDepth() {
        return graphDepth;
    }
    public void setGraphDepth(int graphDepth) {
        this.graphDepth = graphDepth;
    }
    public HashMap<Integer, Integer> getLevelWidthCount() {
        return levelWidthCount;
    }
    public HashMap<UUID, Integer> getNodeToNumChildren() {
        return nodeToNumChildren;
    }
}
