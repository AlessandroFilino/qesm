package com.qesm;

import static org.junit.jupiter.api.Assertions.assertAll;
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
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;

public class NumericalAnalysisTest {

    @Test
    void testWorkflowWellNested() {
        // Workflow and corresponding petriNet generated ad hoc.
        // Transient Analysis conducted with Sirio and Eulero. Result comparison in the
        // end.

        // Workflow generation and analysis
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
        Activity activityv0 = structuredTreeConverterv0.convertToActivity();

        StructuredTree<ProductType> structuredTreev1 = new StructuredTree<>(wf1.getProductWorkflow(v1).getDag(), ProductType.class);
        structuredTreev1.buildStructuredTree();
        StructuredTreeConverter structuredTreeConverterv1 = new StructuredTreeConverter(structuredTreev1.getStructuredWorkflow());
        Activity activityv1 = structuredTreeConverterv1.convertToActivity();

        StructuredTree<ProductType> structuredTreev2 = new StructuredTree<>(wf1.getProductWorkflow(v2).getDag(), ProductType.class);
        structuredTreev2.buildStructuredTree();
        StructuredTreeConverter structuredTreeConverterv2 = new StructuredTreeConverter(structuredTreev2.getStructuredWorkflow());
        Activity activityv2 = structuredTreeConverterv2.convertToActivity();

        StructuredTree<ProductType> structuredTreev3 = new StructuredTree<>(wf1.getProductWorkflow(v3).getDag(), ProductType.class);
        structuredTreev3.buildStructuredTree();
        StructuredTreeConverter structuredTreeConverterv3 = new StructuredTreeConverter(structuredTreev3.getStructuredWorkflow());
        Activity activityv3 = structuredTreeConverterv3.convertToActivity();

        AnalysisHeuristicsVisitor strat = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN,
                new TruncatedExponentialMixtureApproximation());

        double[] cdfv0 = activityv0.analyze(BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);
        double[] cdfv1 = activityv1.analyze(BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);
        double[] cdfv2 = activityv2.analyze(BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);
        double[] cdfv3 = activityv3.analyze(BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);

        // for (int i = 0; i < cdfv0.length; i++) {
        //     System.out.println("Time: " + i / 10.0 + "      " + cdfv3[i] + "  " + cdfv2[i] + "  " + cdfv1[i] + "  " + cdfv0[i]);

        // }

        PetriNet net = new PetriNet();
        Marking marking = new Marking();

        generateWellNestedSirioPetriNet(net, marking);

        // ForwardTransientAnalysis

        TreeTransient treeTransientAnalysis= TreeTransient.builder()
                .greedyPolicy(new BigDecimal("20"), new BigDecimal("0"))
                .timeStep(new BigDecimal("0.1"))
                .build();

        TransientSolution<Marking, RewardRate> sirioAnalysis = TransientSolution.computeRewards(false,
                treeTransientAnalysis.compute(net, marking), "v0 == 1; v1 == 1; v2 == 1; v3 == 1");

        double[][][] solutionSirioRaw = sirioAnalysis.getSolution();

        ArrayList<Double> solSiriov0 = new ArrayList<>();
        ArrayList<Double> solSiriov1 = new ArrayList<>();
        ArrayList<Double> solSiriov2 = new ArrayList<>();
        ArrayList<Double> solSiriov3 = new ArrayList<>();
    
        for (double[][] ds : solutionSirioRaw) {
            for (double[] ds2 : ds) {
                solSiriov0.add(ds2[0]);
                solSiriov1.add(ds2[1]);
                solSiriov2.add(ds2[2]);
                solSiriov3.add(ds2[3]);
            }
        }
        System.out.println(solSiriov0);
        System.out.println(" --------- ");
        System.out.println(solSiriov3);
        // StructuredTree<ProductType> structuredTree = new StructuredTree<>(wf1.getDag(), ProductType.class);
        // structuredTree.buildStructuredTree();
        // StructuredTreeConverter structuredTreeConverter = new StructuredTreeConverter(
        //         structuredTree.getStructuredWorkflow());
        // Activity dagActivity = structuredTreeConverter.convertToActivity();

        // AnalysisHeuristicsVisitor strat = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN,
        //         new TruncatedExponentialMixtureApproximation());

        // double[] cdf = dagActivity.analyze(BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);

        // ArrayList<Double> solEulero = new ArrayList<Double>();
        // for (double value : cdf) {
        //     solEulero.add(value);
        // }
        // System.out.println(dagActivity);
        // // DAG(SEQ(v0, v1, AND(v3, v2)))

        // Activity simplev0 = new Simple("v0", new UniformTime(0, 2));
        // Activity simplev1 = new Simple("v1", new UniformTime(2, 4));
        // Activity simplev2 = new Simple("v2", new UniformTime(4, 6));
        // Activity simplev3 = new Simple("v3", new UniformTime(6, 8));
        // Activity andv3v2 = ModelFactory.forkJoin(simplev3, simplev2);
        // // Activity seqv0v1Andv3v2 = ModelFactory.sequence(simplev0, simplev1, andv3v2);
        // // Activity dagActivityCustom = ModelFactory.DAG(simplev0, simplev1, simplev2, simplev3, andv3v2, seqv0v1Andv3v2);
        
        // // System.out.println(dagActivityCustom);
        // // // DAG(v0, v1, v2, v3, AND(v3, v2), SEQ(v0, v1, AND(v3, v2)))
        // // double[] cdf2 = dagActivity.analyze(BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);
        // // ArrayList<Double> solEulero2 = new ArrayList<Double>();
        // // for (double value : cdf2) {
        // //     solEulero2.add(value);
        // // }

        // // Activity seqv1Andv3v2 = ModelFactory.sequence(andv3v2, simplev1);
        // Activity dagAnd = ModelFactory.DAG(andv3v2);
        // // System.out.println(dagSeq);
        // double[] cdf3 = dagAnd.analyze(BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);
        // ArrayList<Double> solEulero3 = new ArrayList<Double>();
        // for (double value : cdf3) {
        //     solEulero3.add(value);
        // }

        // for (int timeIdx = 0; timeIdx < solEulero3.size(); timeIdx++) {
        //     System.out.println("time" + timeIdx + "      " + solEulero.get(timeIdx) + " " + solEulero3.get(timeIdx));
        // }





        // // Petri net generation (should represent workflow dag)

        // PetriNet net = new PetriNet();
        // Marking marking = new Marking();

        // //Generating Nodes
        // Place FINAL_PLACE = net.addPlace("FINAL_PLACE");
        // Place STARTING_PLACE = net.addPlace("STARTING_PLACE");
        // Place pt1_in = net.addPlace("pt1_in");
        // Place pt1_t0 = net.addPlace("pt1_t0");
        // Place pt2_in = net.addPlace("pt2_in");
        // Place pt2_out = net.addPlace("pt2_out");
        // Place pt3_in = net.addPlace("pt3_in");
        // Place pt3_out = net.addPlace("pt3_out");
        // Transition t0 = net.addTransition("t0");
        // Transition t1 = net.addTransition("t1");
        // Transition t2 = net.addTransition("t2");
        // Transition t3 = net.addTransition("t3");
        // Transition tImm0 = net.addTransition("tImm0");
        // Transition tImm2 = net.addTransition("tImm2");

        // //Generating Connectors
        // net.addPostcondition(tImm0, pt3_in);
        // net.addPrecondition(pt1_t0, t0);
        // net.addPrecondition(pt1_in, t1);
        // net.addPrecondition(STARTING_PLACE, tImm0);
        // net.addPostcondition(t2, pt2_out);
        // net.addPrecondition(pt3_out, tImm2);
        // net.addPostcondition(t0, FINAL_PLACE);
        // net.addPrecondition(pt2_in, t2);
        // net.addPostcondition(tImm2, pt1_in);
        // net.addPostcondition(tImm0, pt2_in);
        // net.addPrecondition(pt2_out, tImm2);
        // net.addPostcondition(t1, pt1_t0);
        // net.addPrecondition(pt3_in, t3);
        // net.addPostcondition(t3, pt3_out);

        // //Generating Properties
        // marking.setTokens(FINAL_PLACE, 0);
        // marking.setTokens(STARTING_PLACE, 1);
        // marking.setTokens(pt1_in, 0);
        // marking.setTokens(pt1_t0, 0);
        // marking.setTokens(pt2_in, 0);
        // marking.setTokens(pt2_out, 0);
        // marking.setTokens(pt3_in, 0);
        // marking.setTokens(pt3_out, 0);
        // t0.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("0"), new BigDecimal("2")));
        // t1.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("2"), new BigDecimal("4")));
        // t2.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("4"), new BigDecimal("6")));
        // t3.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("6"), new BigDecimal("8")));
        // tImm0.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        // tImm0.addFeature(new Priority(0));
        // tImm2.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        // tImm2.addFeature(new Priority(0));

        // // ForwardTransientAnalysis

        // TreeTransient treeTransientAnalysis= TreeTransient.builder()
        //         .greedyPolicy(new BigDecimal("20"), new BigDecimal("0"))
        //         .timeStep(new BigDecimal("0.1"))
        //         .build();

        // TransientSolution<Marking, RewardRate> sirioAnalysis = TransientSolution.computeRewards(false,
        //         treeTransientAnalysis.compute(net, marking), "pt2_out");

        // double[][][] solutionSirioRaw = sirioAnalysis.getSolution();
        // ArrayList<Double> solSirio = new ArrayList<>();
        // for (double[][] ds : solutionSirioRaw) {
        //     for (double[] ds2 : ds) {
        //         solSirio.add(ds2[0]);
        //     }
        // }

        // // PetriNet derived from dagActivity using Eulero

        // PetriNet netDerived = new PetriNet();

        // Place pOut2 = netDerived.addPlace("FINAL_PLACE");
        // Place pIn2 = netDerived.addPlace("STARTING_PLACE");
        // Marking markingDerived = new Marking();
        // markingDerived.setTokens(pIn2, 1);

        // dagActivity.buildSTPN(netDerived, pIn2, pOut2, 0);

        // TreeTransient treeTransientAnalysisDerived= TreeTransient.builder()
        //         .greedyPolicy(new BigDecimal("20"), new BigDecimal("0"))
        //         .timeStep(new BigDecimal("0.1"))
        //         .build();

        // TransientSolution<Marking, RewardRate> sirioAnalysisDerived = TransientSolution
        //         .computeRewards(false, treeTransientAnalysisDerived.compute(netDerived, markingDerived), "pv2_out");

        // double[][][] solutionSirioRawDerived = sirioAnalysisDerived.getSolution();
        // ArrayList<Double> solSirioDerived = new ArrayList<>();
        // for (double[][] ds : solutionSirioRawDerived) {
        //     for (double[] ds2 : ds) {
        //         solSirioDerived.add(ds2[0]);
        //     }
        // }

        // // for (int timeIdx = 0; timeIdx < solSirio.size(); timeIdx++) {
        // //     System.out
        // //             .println(solSirioDerived.get(timeIdx) + " " + solSirio.get(timeIdx));
        // //             // .println(solEulero.get(timeIdx) + " " + solSirioDerived.get(timeIdx) + " " + solSirio.get(timeIdx));
        // // }

        // // assertAll("", () -> assertEquals(solEulero, solSirio), () -> assertEquals(solSirio, solSirioDerived));
        // // assertAll("", () -> assertEquals(solSirio, solSirioDerived));
    }

    @Test
    void testWorkflowNotWellNested() {
        //TODO TEST: implement test

    }

    private void generateWellNestedSirioPetriNet(PetriNet net, Marking marking){
        //Generating Nodes
        Place STARTING_PLACE = net.addPlace("STARTING_PLACE");
        Place STARTING_PLACE_ = net.addPlace("STARTING_PLACE_");
        Place p0 = net.addPlace("p0");
        Place p1 = net.addPlace("p1");
        Place p2 = net.addPlace("p2");
        Place p3 = net.addPlace("p3");
        Place p4 = net.addPlace("p4");
        Place p7 = net.addPlace("p7");
        Place p8 = net.addPlace("p8");
        Place pt1_in = net.addPlace("pt1_in");
        Place pt1_t0 = net.addPlace("pt1_t0");
        Place pt2_in = net.addPlace("pt2_in");
        Place pt2_out = net.addPlace("pt2_out");
        Place pt3_in = net.addPlace("pt3_in");
        Place pt3_out = net.addPlace("pt3_out");
        Place v0 = net.addPlace("v0");
        Place v1 = net.addPlace("v1");
        Place v2 = net.addPlace("v2");
        Place v3 = net.addPlace("v3");
        Transition t0 = net.addTransition("t0");
        Transition t1 = net.addTransition("t1");
        Transition t1_ = net.addTransition("t1_");
        Transition t2 = net.addTransition("t2");
        Transition t2_ = net.addTransition("t2_");
        Transition t2__ = net.addTransition("t2__");
        Transition t3 = net.addTransition("t3");
        Transition t3_ = net.addTransition("t3_");
        Transition t3__ = net.addTransition("t3__");
        Transition t4 = net.addTransition("t4");
        Transition t5 = net.addTransition("t5");
        Transition tImm0 = net.addTransition("tImm0");
        Transition tImm2 = net.addTransition("tImm2");

        //Generating Connectors
        net.addPrecondition(p8, t3__);
        net.addPostcondition(t3__, v3);
        net.addPrecondition(p3, t5);
        net.addPostcondition(t2_, p3);
        net.addPrecondition(p4, t1_);
        net.addPostcondition(t5, p4);
        net.addPrecondition(pt2_in, t2);
        net.addPostcondition(tImm0, pt2_in);
        net.addPrecondition(STARTING_PLACE_, t4);
        net.addPrecondition(p2, t5);
        net.addPostcondition(t3_, p2);
        net.addPrecondition(STARTING_PLACE, tImm0);
        net.addPrecondition(pt1_in, t1);
        net.addPostcondition(tImm2, pt1_in);
        net.addPostcondition(t0, v0);
        net.addPrecondition(p7, t2__);
        net.addPostcondition(t2__, v2);
        net.addPrecondition(pt3_in, t3);
        net.addPostcondition(tImm0, pt3_in);
        net.addPrecondition(pt1_t0, t0);
        net.addPostcondition(t1, pt1_t0);
        net.addPrecondition(p1, t2_);
        net.addPostcondition(t4, p1);
        net.addPrecondition(pt3_out, tImm2);
        net.addPostcondition(t3, pt3_out);
        net.addPostcondition(t1_, v1);
        net.addPrecondition(pt2_out, tImm2);
        net.addPostcondition(t2, pt2_out);
        net.addPrecondition(p0, t3_);
        net.addPostcondition(t4, p0);

        //Generating Properties
        marking.setTokens(STARTING_PLACE, 1);
        marking.setTokens(STARTING_PLACE_, 1);
        marking.setTokens(p0, 0);
        marking.setTokens(p1, 0);
        marking.setTokens(p2, 0);
        marking.setTokens(p3, 0);
        marking.setTokens(p4, 0);
        marking.setTokens(p7, 1);
        marking.setTokens(p8, 1);
        marking.setTokens(pt1_in, 0);
        marking.setTokens(pt1_t0, 0);
        marking.setTokens(pt2_in, 0);
        marking.setTokens(pt2_out, 0);
        marking.setTokens(pt3_in, 0);
        marking.setTokens(pt3_out, 0);
        marking.setTokens(v0, 0);
        marking.setTokens(v1, 0);
        marking.setTokens(v2, 0);
        marking.setTokens(v3, 0);
        t0.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("0"), new BigDecimal("2")));
        t1.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("2"), new BigDecimal("4")));
        t1_.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("2"), new BigDecimal("4")));
        t2.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("4"), new BigDecimal("6")));
        t2_.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("4"), new BigDecimal("6")));
        t2__.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("4"), new BigDecimal("6")));
        t3.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("6"), new BigDecimal("8")));
        t3_.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("6"), new BigDecimal("8")));
        t3__.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("6"), new BigDecimal("8")));
        t4.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        t4.addFeature(new Priority(0));
        t5.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        t5.addFeature(new Priority(0));
        tImm0.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        tImm0.addFeature(new Priority(0));
        tImm2.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        tImm2.addFeature(new Priority(0));
    }
}
