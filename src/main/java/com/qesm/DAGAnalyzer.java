package com.qesm;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.activitytypes.ActivityType;
import org.oristool.eulero.modeling.activitytypes.SEQType;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

public class DAGAnalyzer {
    private DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow;

    public DAGAnalyzer(DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow) {
        this.structuredWorkflow = structuredWorkflow;
    }

    public void analyze(){
        
        StochasticTime pdf = new UniformTime(0, 1);
        Activity v0 = new Simple("v0", pdf);
        Activity v1 = new Simple("v1", pdf);

        

        ActivityType A = new SEQType(new ArrayList<>(List.of(v0, v1)));
        
        A
    }
}
