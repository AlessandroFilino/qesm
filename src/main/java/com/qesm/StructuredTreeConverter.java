package com.qesm;

import java.util.ArrayList;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;
import org.oristool.petrinet.PetriNet;

public class StructuredTreeConverter {

    DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow;
    // TODO: add pdf to processedType during DagGeneration
    StochasticTime pdf;

    public StructuredTreeConverter(DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow) {
        this.structuredWorkflow = structuredWorkflow;
        this.pdf = new UniformTime(0, 1);
    }

    public Activity convertToActivity(){
        
        for (STPNBlock stpnBlock : structuredWorkflow) {
            // find and start conversion from rootBlock;
            if(structuredWorkflow.outDegreeOf(stpnBlock) == 0){
                return recursiveExploration(stpnBlock);
            }
        }
        // Should not happen
        System.err.println("Error: structuredTree without rootBlock");
        return null;
    }

    private Activity recursiveExploration(STPNBlock stpnBlock){
        Activity currenActivity = calculateActivityFromBlock(stpnBlock);

        for (CustomEdge inEdge : structuredWorkflow.incomingEdgesOf(stpnBlock)) {
            STPNBlock stpnBlockChild = structuredWorkflow.getEdgeSource(inEdge);
            Activity childActivity = recursiveExploration(stpnBlockChild);
            currenActivity.addPrecondition(childActivity);
        }

        return currenActivity;
    }
    
    private Activity calculateActivityFromBlock(STPNBlock stpnBlock){

        Activity activity = null;
        if(stpnBlock.getClass() == SimpleBlock.class){
            activity = new Simple(stpnBlock.getSimpleElement().getNameType(), pdf);
        }
        else if (stpnBlock.getClass() == AndBlock.class){
            ArrayList<Activity> activityList = new ArrayList<>();
            for (STPNBlock stpnBlockNested : stpnBlock.getComposedElements()) {
                activityList.add(calculateActivityFromBlock(stpnBlockNested));
            }

            activity = ModelFactory.forkJoin(activityList.toArray(new Activity[0]));
        }
        else if (stpnBlock.getClass() == SeqBlock.class){
            ArrayList<Activity> activityList = new ArrayList<>();
            for (STPNBlock stpnBlockNested : stpnBlock.getComposedElements()) {
                activityList.add(calculateActivityFromBlock(stpnBlockNested));
            }

            activity = ModelFactory.sequence(activityList.toArray(new Activity[0]));
        }
        else{
            System.err.println("Error stpnBlock class: " + stpnBlock.getClass() + " not valid for convertion to Activity");
        }
        return activity;
    }

}
