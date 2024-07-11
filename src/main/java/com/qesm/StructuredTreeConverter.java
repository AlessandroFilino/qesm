package com.qesm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;


public class StructuredTreeConverter {

    private DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow;
    private HashSet<Activity> notWellNestedActivities;
    private HashMap<STPNBlock, Activity> blocksAlreadyConverted; 


    public StructuredTreeConverter(DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow) {
        this.structuredWorkflow = structuredWorkflow;
        // this.pdf = new ExponentialTime(BigDecimal.valueOf(5));
        this.notWellNestedActivities = new HashSet<>();
        this.blocksAlreadyConverted = new HashMap<>();
    }

    public Activity convertToActivity(){
        
        for (STPNBlock stpnBlock : structuredWorkflow) {
            // find and start conversion from rootBlock;
            if(structuredWorkflow.outDegreeOf(stpnBlock) == 0){
                Activity rootActivity = recursiveExploration(stpnBlock);
                if(rootActivity.pre().isEmpty()){
                    return ModelFactory.DAG(rootActivity);
                }
                else{
                    return ModelFactory.DAG(notWellNestedActivities.toArray(new Activity[0]));
                }
            }
        }
        // Should not happen
        System.err.println("Error: structuredTree without rootBlock");
        return null;
    }

    private Activity recursiveExploration(STPNBlock stpnBlock){
        if(blocksAlreadyConverted.containsKey(stpnBlock)){
            return blocksAlreadyConverted.get(stpnBlock);
        }
        else{
            Activity currentActivity = calculateActivityFromBlock(stpnBlock);
            blocksAlreadyConverted.put(stpnBlock, currentActivity);

            for (CustomEdge inEdge : structuredWorkflow.incomingEdgesOf(stpnBlock)) {
                STPNBlock stpnBlockChild = structuredWorkflow.getEdgeSource(inEdge);
                Activity childActivity = recursiveExploration(stpnBlockChild);
                currentActivity.addPrecondition(childActivity);
                // System.out.println(currentActivity.name() + " -> " + childActivity.name());
                notWellNestedActivities.add(currentActivity);
                notWellNestedActivities.add(childActivity);
            }
    
            return currentActivity;
        }
    }
    
    private Activity calculateActivityFromBlock(STPNBlock stpnBlock){

        Activity activity = null;
        if(stpnBlock.getClass() == SimpleBlock.class){
            activity = new Simple(stpnBlock.getSimpleElement().getName(), stpnBlock.getSimpleElement().getPdf().get());
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
