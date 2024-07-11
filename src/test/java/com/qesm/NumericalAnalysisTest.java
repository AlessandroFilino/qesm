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

        StructuredTree<ProductType> structuredTree = new StructuredTree<>(wf1.getDag(), ProductType.class);
        structuredTree.buildStructuredTree();
        StructuredTreeConverter structuredTreeConverter = new StructuredTreeConverter(
                structuredTree.getStructuredWorkflow());
        Activity dagActivity = structuredTreeConverter.convertToActivity();

        AnalysisHeuristicsVisitor strat = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN,
                new TruncatedExponentialMixtureApproximation());

        double[] cdf = dagActivity.analyze(BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), strat);

        ArrayList<Double> solEulero = new ArrayList<Double>();
        for (double value : cdf) {
            solEulero.add(value);
        }

        // Petri net generation (should represent workflow dag)

        PetriNet net = new PetriNet();
        Marking marking = new Marking();

        // Generating Nodes
        Place p1 = net.addPlace("p1");
        Place p3 = net.addPlace("p3");
        Place p4 = net.addPlace("p4");
        Place p5 = net.addPlace("p5");
        Place p6 = net.addPlace("p6");
        Place p7 = net.addPlace("p7");
        Place pIn = net.addPlace("pIn");
        Place pOut = net.addPlace("pOut");
        Transition t0 = net.addTransition("t0");
        Transition t2 = net.addTransition("t2");
        Transition t3 = net.addTransition("t3");
        Transition t4 = net.addTransition("t4");
        Transition tImm0 = net.addTransition("tImm0");
        Transition tImm2 = net.addTransition("tImm2");

        // Generating Connectors
        net.addPrecondition(pIn, tImm0);
        net.addPostcondition(tImm0, p1);
        net.addPrecondition(p1, t3);
        net.addPostcondition(tImm0, p3);
        net.addPrecondition(p4, t2);
        net.addPostcondition(t2, p5);
        net.addPrecondition(p5, t0);
        net.addPostcondition(t0, pOut);
        net.addPrecondition(p3, t4);
        net.addPostcondition(tImm2, p4);
        net.addPostcondition(t4, p6);
        net.addPrecondition(p6, tImm2);
        net.addPostcondition(t3, p7);
        net.addPrecondition(p7, tImm2);

        // Generating Properties
        marking.setTokens(p1, 0);
        marking.setTokens(p3, 0);
        marking.setTokens(p4, 0);
        marking.setTokens(p5, 0);
        marking.setTokens(p6, 0);
        marking.setTokens(p7, 0);
        marking.setTokens(pIn, 1);
        marking.setTokens(pOut, 0);
        t0.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("0"), new BigDecimal("2")));
        t2.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("2"), new BigDecimal("4")));
        t3.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("4"), new BigDecimal("6")));
        t4.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("6"), new BigDecimal("8")));
        tImm0.addFeature(
                StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        tImm0.addFeature(new Priority(0));
        tImm2.addFeature(
                StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        tImm2.addFeature(new Priority(0));

        // ForwardTransientAnalysis

        TreeTransient treeTransientAnalysis= TreeTransient.builder()
                .greedyPolicy(new BigDecimal("20"), new BigDecimal("0"))
                .timeStep(new BigDecimal("0.1"))
                .build();

        TransientSolution<Marking, RewardRate> sirioAnalysis = TransientSolution.computeRewards(false,
                treeTransientAnalysis.compute(net, marking), "pOut");

        double[][][] solutionSirioRaw = sirioAnalysis.getSolution();
        ArrayList<Double> solSirio = new ArrayList<>();
        for (double[][] ds : solutionSirioRaw) {
            for (double[] ds2 : ds) {
                solSirio.add(ds2[0]);
            }
        }

        // PetriNet derived from dagActivity using Eulero

        PetriNet netDerived = new PetriNet();

        Place pOut2 = netDerived.addPlace("FINAL_PLACE");
        Place pIn2 = netDerived.addPlace("STARTING_PLACE");
        Marking markingDerived = new Marking();
        markingDerived.setTokens(pIn2, 1);

        dagActivity.buildSTPN(netDerived, pIn2, pOut2, 0);

        TreeTransient treeTransientAnalysisDerived= TreeTransient.builder()
                .greedyPolicy(new BigDecimal("20"), new BigDecimal("0"))
                .timeStep(new BigDecimal("0.1"))
                .build();

        TransientSolution<Marking, RewardRate> sirioAnalysisDerived = TransientSolution
                .computeRewards(false, treeTransientAnalysisDerived.compute(netDerived, markingDerived), "FINAL_PLACE");

        double[][][] solutionSirioRawDerived = sirioAnalysisDerived.getSolution();
        ArrayList<Double> solSirioDerived = new ArrayList<>();
        for (double[][] ds : solutionSirioRawDerived) {
            for (double[] ds2 : ds) {
                solSirioDerived.add(ds2[0]);
            }
        }

        for (int timeIdx = 0; timeIdx < solEulero.size(); timeIdx++) {
            System.out
                    .println(solEulero.get(timeIdx) + " " + solSirioDerived.get(timeIdx) + " " + solSirio.get(timeIdx));
        }

        assertAll("", () -> assertEquals(solEulero, solSirio), () -> assertEquals(solSirio, solSirioDerived));
    }

    @Test
    void testWorkflowNotWellNested() {
        //TODO TEST: implement test
    }

}
