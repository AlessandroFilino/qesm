package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.oristool.eulero.evaluation.approximator.TruncatedExponentialMixtureApproximation;
import org.oristool.eulero.evaluation.heuristics.AnalysisHeuristicsVisitor;
import org.oristool.eulero.evaluation.heuristics.RBFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.stochastictime.UniformTime;
import org.oristool.models.pn.Priority;
import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.trans.TreeTransient;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Transition;

public class NumericalAnalysisTest {
    private Activity activityV0;
    private Activity activityV1;
    private Activity activityV2;
    private Activity activityV3;

    @Test
    void testWorkflowWellNested() {
        // Workflow and corresponding petriNet generated ad hoc.
        // Transient Analysis conducted with Sirio and Eulero. Result comparison in the end.

        setupWellNested();

        // Eulero
        AnalysisHeuristicsVisitor strat = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN,
                new TruncatedExponentialMixtureApproximation());
        ArrayList<Double> solEuleroV3 = extractSolValueEulero(activityV3, BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);
        ArrayList<Double> solEuleroV2 = extractSolValueEulero(activityV2, BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);
        ArrayList<Double> solEuleroV1 = extractSolValueEulero(activityV1, BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);
        ArrayList<Double> solEuleroV0 = extractSolValueEulero(activityV0, BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);

        // Sirio
        TreeTransient treeTransientAnalysis = TreeTransient.builder()
                .greedyPolicy(new BigDecimal("20"), new BigDecimal("0"))
                .timeStep(new BigDecimal("0.1"))
                .build();
        PetriNet net = new PetriNet();
        Marking marking = new Marking();
        generateWellNestedSirioPetriNetV3(net, marking);
        ArrayList<Double> solSirioV3 = extractSolValueSirio(net, marking, treeTransientAnalysis, "v3");

        net = new PetriNet();
        marking = new Marking();
        generateWellNestedSirioPetriNetV2(net, marking);
        ArrayList<Double> solSirioV2 = extractSolValueSirio(net, marking, treeTransientAnalysis, "v2");

        net = new PetriNet();
        marking = new Marking();
        generateWellNestedSirioPetriNetV1(net, marking);
        ArrayList<Double> solSirioV1 = extractSolValueSirio(net, marking, treeTransientAnalysis, "v1");

        net = new PetriNet();
        marking = new Marking();
        generateWellNestedSirioPetriNetV0(net, marking);
        ArrayList<Double> solSirioV0 = extractSolValueSirio(net, marking, treeTransientAnalysis, "v0");

        for (int i = 0; i < solEuleroV0.size(); i++){
            System.out.println(solEuleroV0.get(i) + "  " + solSirioV0.get(i) + "     " + (Double.compare(solEuleroV0.get(i), solSirioV0.get(i)) == 0));
        }

        assertEquals(solSirioV3, solEuleroV3);
        assertEquals(solSirioV2, solEuleroV2);
        assertEquals(solSirioV1, solEuleroV1);
        assertEquals(solSirioV0, solEuleroV0);
    }

    @Test
    void testWorkflowNotWellNested() {
        //TODO TEST: implement test

    }

    private void generateWellNestedSirioPetriNetV3(PetriNet net, Marking marking){
        //Generating Nodes
        Place p8 = net.addPlace("p8");
        Place v3 = net.addPlace("v3");
        Transition t3__ = net.addTransition("t3__");

        //Generating Connectors
        net.addPostcondition(t3__, v3);
        net.addPrecondition(p8, t3__);

        //Generating Properties
        marking.setTokens(p8, 1);
        marking.setTokens(v3, 0);
        t3__.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("6"), new BigDecimal("8")));
    }

    private void generateWellNestedSirioPetriNetV2(PetriNet net, Marking marking){
        //Generating Nodes
        Place p7 = net.addPlace("p7");
        Place v2 = net.addPlace("v2");
        Transition t2__ = net.addTransition("t2__");

        //Generating Connectors
        net.addPrecondition(p7, t2__);
        net.addPostcondition(t2__, v2);

        //Generating Properties
        marking.setTokens(p7, 1);
        marking.setTokens(v2, 0);
        t2__.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("4"), new BigDecimal("6")));
    }

    private void generateWellNestedSirioPetriNetV1(PetriNet net, Marking marking){
        //Generating Nodes
        Place STARTING_PLACE_ = net.addPlace("STARTING_PLACE_");
        Place p0 = net.addPlace("p0");
        Place p1 = net.addPlace("p1");
        Place p2 = net.addPlace("p2");
        Place p3 = net.addPlace("p3");
        Place p4 = net.addPlace("p4");
        Place v1 = net.addPlace("v1");
        Transition t1_ = net.addTransition("t1_");
        Transition t2_ = net.addTransition("t2_");
        Transition t3_ = net.addTransition("t3_");
        Transition t4 = net.addTransition("t4");
        Transition t5 = net.addTransition("t5");

        //Generating Connectors
        net.addPostcondition(t4, p1);
        net.addPostcondition(t4, p0);
        net.addPrecondition(STARTING_PLACE_, t4);
        net.addPostcondition(t1_, v1);
        net.addPostcondition(t2_, p3);
        net.addPrecondition(p1, t2_);
        net.addPrecondition(p4, t1_);
        net.addPrecondition(p0, t3_);
        net.addPostcondition(t5, p4);
        net.addPrecondition(p2, t5);
        net.addPostcondition(t3_, p2);
        net.addPrecondition(p3, t5);

        //Generating Properties
        marking.setTokens(STARTING_PLACE_, 1);
        marking.setTokens(p0, 0);
        marking.setTokens(p1, 0);
        marking.setTokens(p2, 0);
        marking.setTokens(p3, 0);
        marking.setTokens(p4, 0);
        marking.setTokens(v1, 0);
        t1_.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("2"), new BigDecimal("4")));
        t2_.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("4"), new BigDecimal("6")));
        t3_.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("6"), new BigDecimal("8")));
        t4.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        t4.addFeature(new Priority(0));
        t5.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
    }

    private void generateWellNestedSirioPetriNetV0(PetriNet net, Marking marking){
        //Generating Nodes
        Place STARTING_PLACE = net.addPlace("STARTING_PLACE");
        Place pt1_in = net.addPlace("pt1_in");
        Place pt1_t0 = net.addPlace("pt1_t0");
        Place pt2_in = net.addPlace("pt2_in");
        Place pt2_out = net.addPlace("pt2_out");
        Place pt3_in = net.addPlace("pt3_in");
        Place pt3_out = net.addPlace("pt3_out");
        Place v0 = net.addPlace("v0");
        Transition t0 = net.addTransition("t0");
        Transition t1 = net.addTransition("t1");
        Transition t2 = net.addTransition("t2");
        Transition t3 = net.addTransition("t3");
        Transition tImm0 = net.addTransition("tImm0");
        Transition tImm2 = net.addTransition("tImm2");

        //Generating Connectors
        net.addPostcondition(t0, v0);
        net.addPostcondition(t2, pt2_out);
        net.addPrecondition(pt3_in, t3);
        net.addPostcondition(tImm2, pt1_in);
        net.addPrecondition(pt1_t0, t0);
        net.addPrecondition(pt3_out, tImm2);
        net.addPostcondition(t3, pt3_out);
        net.addPostcondition(tImm0, pt3_in);
        net.addPrecondition(pt2_in, t2);
        net.addPrecondition(pt2_out, tImm2);
        net.addPrecondition(STARTING_PLACE, tImm0);
        net.addPostcondition(t1, pt1_t0);
        net.addPrecondition(pt1_in, t1);
        net.addPostcondition(tImm0, pt2_in);

        //Generating Properties
        marking.setTokens(STARTING_PLACE, 1);
        marking.setTokens(pt1_in, 0);
        marking.setTokens(pt1_t0, 0);
        marking.setTokens(pt2_in, 0);
        marking.setTokens(pt2_out, 0);
        marking.setTokens(pt3_in, 0);
        marking.setTokens(pt3_out, 0);
        marking.setTokens(v0, 0);
        t0.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("0"), new BigDecimal("2")));
        t1.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("2"), new BigDecimal("4")));
        t2.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("4"), new BigDecimal("6")));
        t3.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("6"), new BigDecimal("8")));
        tImm0.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        tImm0.addFeature(new Priority(0));
        tImm2.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        tImm2.addFeature(new Priority(0));
    }

    private ArrayList<Double> extractSolValueSirio(PetriNet net, Marking marking, TreeTransient treeTransientAnalysis, String rewardRate){
        TransientSolution<Marking, RewardRate> sirioAnalysis = TransientSolution.computeRewards(false,
                treeTransientAnalysis.compute(net, marking), rewardRate);
        double[][][] solutionSirioRaw = sirioAnalysis.getSolution();
        ArrayList<Double> solSirio = new ArrayList<>();
        for (double[][] ds : solutionSirioRaw) {
            for (double[] ds2 : ds) {
                solSirio.add(ds2[0]);
            }
        }
        return solSirio;
    }

    private ArrayList<Double> extractSolValueEulero(Activity activity, BigDecimal timeLimit, BigDecimal timeStep, AnalysisHeuristicsVisitor visitor){
        double[] cdf = activity.analyze(timeLimit, timeStep, visitor);
        ArrayList<Double> solEulero = new ArrayList<Double>();
        for (double value : cdf) {
            solEulero.add(value);
        }
        return solEulero;
    }

    private void setupWellNested(){
        // DAG structure:
        //  v0
        //  |
        //  v1
        //  |  \  \
        //  v2 v3 v4
        //  |  |
        //  v5 v6

        // Workflow generation
        ListenableDAG<ProductType, CustomEdge> dag = new ListenableDAG<>(CustomEdge.class);
        ProductType v0 = new ProductType("v0", 1, new UniformTime(0, 2));
        ProductType v1 = new ProductType("v1", 2, new UniformTime(2, 4));
        ProductType v2 = new ProductType("v2", 3, new UniformTime(4, 6));
        ProductType v3 = new ProductType("v3", 4, new UniformTime(6, 8));
        ProductType v4 = new ProductType("v4");
        ProductType v5 = new ProductType("v5");
        ProductType v6 = new ProductType("v6");
        dag.addVertex(v0);
        dag.addVertex(v1);
        dag.addVertex(v2);
        dag.addVertex(v3);
        dag.addVertex(v4);
        dag.addVertex(v5);
        dag.addVertex(v6);
        dag.addEdge(v3, v1);
        dag.addEdge(v2, v1);
        dag.addEdge(v1, v0);
        dag.addEdge(v4, v1);
        dag.addEdge(v5, v2);
        dag.addEdge(v6, v3);
        WorkflowType wf1 = new WorkflowType(dag);


        StructuredTree<ProductType> structuredTreev0 = new StructuredTree<>(wf1.getProductWorkflow(v0).getDag(), ProductType.class);
        structuredTreev0.buildStructuredTree();
        StructuredTreeConverter structuredTreeConverterv0 = new StructuredTreeConverter(structuredTreev0.getStructuredWorkflow());
        activityV0 = structuredTreeConverterv0.convertToActivity();

        StructuredTree<ProductType> structuredTreev1 = new StructuredTree<>(wf1.getProductWorkflow(v1).getDag(), ProductType.class);
        structuredTreev1.buildStructuredTree();
        StructuredTreeConverter structuredTreeConverterv1 = new StructuredTreeConverter(structuredTreev1.getStructuredWorkflow());
        activityV1 = structuredTreeConverterv1.convertToActivity();

        StructuredTree<ProductType> structuredTreev2 = new StructuredTree<>(wf1.getProductWorkflow(v2).getDag(), ProductType.class);
        structuredTreev2.buildStructuredTree();
        StructuredTreeConverter structuredTreeConverterv2 = new StructuredTreeConverter(structuredTreev2.getStructuredWorkflow());
        activityV2 = structuredTreeConverterv2.convertToActivity();

        StructuredTree<ProductType> structuredTreev3 = new StructuredTree<>(wf1.getProductWorkflow(v3).getDag(), ProductType.class);
        structuredTreev3.buildStructuredTree();
        StructuredTreeConverter structuredTreeConverterv3 = new StructuredTreeConverter(structuredTreev3.getStructuredWorkflow());
        activityV3 = structuredTreeConverterv3.convertToActivity();
    }

    private void setupNotWellNested(){

    }
}
