package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.oristool.eulero.evaluation.approximator.TruncatedExponentialMixtureApproximation;
import org.oristool.eulero.evaluation.heuristics.AnalysisHeuristicsVisitor;
import org.oristool.eulero.evaluation.heuristics.EvaluationResult;
import org.oristool.eulero.evaluation.heuristics.RBFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;
import org.oristool.eulero.ui.ActivityViewer;
import org.oristool.models.pn.Priority;
import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Transition;

public class DagAnalyzerTest {

    
    @Test
    public void testDagFromOris(){
        // TODO TEST: implement test
    }
    // TODO TEST: Change these method to match specific cases (so that we can compare them numerically)

    
    @Test
    void testPetriNet1(){
        // Workflow and corresponding petriNet generated ad hoc. 
        // Transient Analysis conducted with Sirio and Eulero. Result comparison in the end.


        // Workflow generation and analysis
        ListenableDAG<ProductType, CustomEdge> dag = new ListenableDAG<>(CustomEdge.class);
        ProductType v0 = new ProductType("v0", 1, new UniformTime(0,2));
        ProductType v1 = new ProductType("v1", 2, new UniformTime(2,4));
        ProductType v2 = new ProductType("v2", 3, new UniformTime(4,6));
        ProductType v3 = new ProductType("v3", 4, new UniformTime(6,8));
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
        StructuredTreeConverter structuredTreeConverter = new StructuredTreeConverter(structuredTree.getStructuredWorkflow());
        Activity resultActivity = structuredTreeConverter.convertToActivity();

        AnalysisHeuristicsVisitor start = new RBFHeuristicsVisitor(BigInteger.valueOf(10), BigInteger.TEN, new TruncatedExponentialMixtureApproximation());
        double[] cdf = resultActivity.analyze(BigDecimal.valueOf(20), BigDecimal.valueOf(0.1), start);
        ArrayList<Double> solEulero = new ArrayList<Double>();
        for (double value : cdf) {
            solEulero.add(value);
        }


        // Petri net generation

        PetriNet net = new PetriNet();
        Marking marking = new Marking();

        //Generating Nodes
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

        //Generating Connectors
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

        //Generating Properties
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
        tImm0.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        tImm0.addFeature(new Priority(0));
        tImm2.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
        tImm2.addFeature(new Priority(0));


        // PetriNet analysis
        RegTransient analysis = RegTransient.builder()
            .greedyPolicy(new BigDecimal("20"), new BigDecimal("0"))
            .timeStep(new BigDecimal("0.1"))
            .build();

        TransientSolution<DeterministicEnablingState, RewardRate> sirioAnalysis = TransientSolution.computeRewards(false, analysis.compute(net, marking), "pOut");

        double[][][] solutionSirioRaw= sirioAnalysis.getSolution();
        ArrayList<Double> solSirio = new ArrayList<>();
        for (double[][] ds : solutionSirioRaw) {
            for (double[] ds2 : ds) {
                solSirio.add(ds2[0]);
            }
        }

        PetriNet netDerived = new PetriNet();
        Place pOut2 = netDerived.addPlace("FINAL_PLACE");
        Place pIn2 = netDerived.addPlace("STARTING_PLACE");
        resultActivity.buildSTPN(netDerived, pIn2, pOut2, 0);

        RegTransient analysisDerived = RegTransient.builder()
            .greedyPolicy(new BigDecimal("20"), new BigDecimal("0"))
            .timeStep(new BigDecimal("0.1"))
            .build();

        TransientSolution<DeterministicEnablingState, RewardRate> sirioAnalysisDerived = TransientSolution.computeRewards(false, analysisDerived.compute(net, marking), "pOut");

        double[][][] solutionSirioRawDerived= sirioAnalysisDerived.getSolution();
        ArrayList<Double> solSirioDerived = new ArrayList<>();
        for (double[][] ds : solutionSirioRawDerived) {
            for (double[] ds2 : ds) {
                solSirioDerived.add(ds2[0]);
            }
        }

        
        for (int timeIdx = 0; timeIdx < solEulero.size(); timeIdx++) {
            System.out.println(solEulero.get(timeIdx)+ " " + solSirioDerived.get(timeIdx) + " " + solSirio.get(timeIdx));
        }

        assertEquals(solEulero, solSirio);

    }  

    public void testPetriNet1_(){
        // // time limit : 15, time step : 0.1
        StochasticTime pdf12 = new UniformTime(1, 2);
        StochasticTime pdf13 = new UniformTime(0, 1);
        StochasticTime pdf02 = new UniformTime(0, 2);
        StochasticTime pdf08 = new UniformTime(0, 8);
        StochasticTime pdf05 = new UniformTime(0, 5);

        Activity t14 = new Simple("t14", pdf13);
        Activity t15 = new Simple("t15", pdf12);
        Activity a0 = ModelFactory.forkJoin(t14, t15);

        Activity t13 = new Simple("t13", pdf02);
        Activity s0 = ModelFactory.sequence(a0, t13);

        Activity t21 = new Simple("t21", pdf08);

        Activity a1 = ModelFactory.forkJoin(s0, t21);

        Activity t24 = new Simple("t24", pdf05);

        t24.addPrecondition(a1);

        // Activity DAG = ModelFactory.DAG(t14, t15, a0, t13, s0, t21, a1, t24);
        Activity DAG = ModelFactory.DAG(a1, t24);

        
        AnalysisHeuristicsVisitor start = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation());

        // double[] cdf = A.analyze(A.max().add(BigDecimal.ONE), A.getFairTimeTick(), start);
        double[] cdf = DAG.analyze(BigDecimal.valueOf(15), BigDecimal.valueOf(0.1), start);


        // ActivityViewer.CompareResults("./media", true, "test", List.of("A", "test"), 
        //     new EvaluationResult("A", cdf, 0, cdf.length, BigDecimal.valueOf(0.1).doubleValue(), 0));

        ActivityViewer.CompareResults("", List.of("A", "test"), List.of(
                new EvaluationResult("A", cdf, 0, cdf.length, BigDecimal.valueOf(0.1).doubleValue(), 0)
        ));

        // System.out.println("Timestep used: " + p14.getFairTimeTick().toString());

        System.out.println(DAG);

        PetriNet net = new PetriNet();
        Place pOut = net.addPlace("FINAL_PLACE");
        Place pIn = net.addPlace("STARTING_PLACE");
        DAG.buildSTPN(net, pIn, pOut, 0);

        System.out.println(net);
    }

    public void testPetriNet2_(){
        // time limit : 10, time step : 0.1

        StochasticTime pdf01 = new UniformTime(0, 1);
        StochasticTime pdf02 = new UniformTime(0, 2);
        StochasticTime pdf03 = new UniformTime(0, 3);
        StochasticTime pdf04 = new UniformTime(0, 4);
        
        Activity t0 = new Simple("t0", pdf01);
        Activity t1 = new Simple("t1", pdf02);

        Activity s0 = ModelFactory.sequence(t0,t1);

        Activity t3 = new Simple("t3", pdf03);
        Activity t5 = new Simple("t5", pdf04);

        t3.addPrecondition(s0);
        t5.addPrecondition(t3, s0);

        Activity DAG = ModelFactory.DAG(t3, s0, t5);

        AnalysisHeuristicsVisitor start = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation());
        double[] cdf = DAG.analyze(BigDecimal.valueOf(10), BigDecimal.valueOf(0.1), start);


        ActivityViewer.CompareResults("", List.of("A", "test"), List.of(
                new EvaluationResult("A", cdf, 0, cdf.length, BigDecimal.valueOf(0.1).doubleValue(), 0)
        ));
        
        System.out.println(DAG);

        PetriNet net = new PetriNet();
        Place pOut = net.addPlace("FINAL_PLACE");
        Place pIn = net.addPlace("STARTING_PLACE");
        DAG.buildSTPN(net, pIn, pOut, 0);

        System.out.println(net);

    }

    
    public void test3(){
        StochasticTime pdf = new UniformTime(0, 1);
        
        Activity t0 = new Simple("t0", pdf);
        Activity t1 = new Simple("t1", pdf);

        Activity s0 = ModelFactory.sequence(t0,t1);

        Activity t2 = new Simple("t2", pdf);
        Activity t3 = new Simple("t3", pdf);
        

        Activity s1 = ModelFactory.sequence(t2,t3);
        Activity s2 = ModelFactory.forkJoin(s0,s1);

        PetriNet net = new PetriNet();
        Place p0 = net.addPlace("p0");
        Place p1 = net.addPlace("p1");
        
        s2.buildSTPN(net, p0, p1, 0);
        System.out.println(net);

        RegTransient analysis = RegTransient.builder()
            .greedyPolicy(new BigDecimal("3"), new BigDecimal("0.005"))
            .timeStep(new BigDecimal("0.01"))
            .build();

        Marking marking = new Marking();
        marking.setTokens(p0, 1);

        // TransientSolution<DeterministicEnablingState, Marking> solution =
        //     analysis.compute(net, marking);

        TransientSolution<DeterministicEnablingState, RewardRate> solution2 = TransientSolution.computeRewards(false, analysis.compute(net, marking), "p1");

        // Display transient probabilities
        // new TransientSolutionViewer(solution2);
        double[][][] solution2Data = solution2.getSolution();
        ArrayList<Double> solSirio = new ArrayList<>();
        for (double[][] ds : solution2Data) {
            for (double[] ds2 : ds) {
                solSirio.add(ds2[0]);
            }
        }

        System.out.println(solSirio);

        AnalysisHeuristicsVisitor start = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation());
        double[] cdf = s2.analyze(s2.max().add(BigDecimal.ONE), s2.getFairTimeTick(), start);

        ArrayList<Double> solEulero = new ArrayList<Double>();
        for (double value : cdf) {
            solEulero.add(value);
        }

        System.out.println(solEulero);

        // ActivityViewer.CompareResults("", List.of("A", "test"), List.of(
        //         new EvaluationResult("A", cdf, 0, cdf.length, s2.getFairTimeTick().doubleValue(), 0)
        // ));

        // System.out.println("Timestep used: " + s2.getFairTimeTick().toString());


    }
}
