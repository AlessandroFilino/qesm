package com.qesm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.TransientSolutionViewer;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;


public class DAGAnalyzer {

    public DAGAnalyzer() {
    }

    public void analyzeActivity(Activity rootActivity){
        
        AnalysisHeuristicsVisitor visitor = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation());
        // AnalysisHeuristicsVisitor visitor = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new EXPMixtureApproximation());

        // double[] cdf = rootActivity.analyze(rootActivity.max().add(BigDecimal.ONE), rootActivity.getFairTimeTick(), visitor);
        double[] cdf = rootActivity.analyze(BigDecimal.valueOf(6), BigDecimal.valueOf(0.01), visitor);


        ActivityViewer.CompareResults("", List.of("A", "test"), List.of(
                new EvaluationResult("A", cdf, 0, cdf.length, 0.01, 0)
        ));

        // System.out.println("Timestep used: " + rootActivity.getFairTimeTick().toString());

    }

    public void analizePetriNet(PetriNet net, Place pReward, Place pStart){

        RegTransient analysis = RegTransient.builder()
            .greedyPolicy(new BigDecimal("6"), new BigDecimal("0.005"))
            .timeStep(new BigDecimal("0.01"))
            .build();

        Marking marking = new Marking();
        marking.setTokens(pStart, 1);
        TransientSolution<DeterministicEnablingState, RewardRate> solution = TransientSolution.computeRewards(false, analysis.compute(net, marking), pReward.getName());

        // Display transient probabilities
        new TransientSolutionViewer(solution);
    }


    public void testPetriNet1(){
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

        Activity DAG = ModelFactory.DAG(t14, t15, a0, t13, s0, t21, a1, t24);
        
        AnalysisHeuristicsVisitor start = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation());

        // double[] cdf = A.analyze(A.max().add(BigDecimal.ONE), A.getFairTimeTick(), start);
        double[] cdf = DAG.analyze(BigDecimal.valueOf(15), BigDecimal.valueOf(0.1), start);


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

    public void testPetriNet2(){
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
