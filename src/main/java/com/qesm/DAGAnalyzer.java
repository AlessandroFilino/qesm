package com.qesm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.oristool.eulero.evaluation.approximator.DoubleTruncatedEXPApproximation;
import org.oristool.eulero.evaluation.approximator.EXPMixtureApproximation;
import org.oristool.eulero.evaluation.approximator.SOSplineApproximation;
import org.oristool.eulero.evaluation.approximator.SplineBodyEXPTailApproximation;
import org.oristool.eulero.evaluation.approximator.TruncatedExponentialMixtureApproximation;
import org.oristool.eulero.evaluation.heuristics.AnalysisHeuristicsVisitor;
import org.oristool.eulero.evaluation.heuristics.EvaluationResult;
import org.oristool.eulero.evaluation.heuristics.RBFHeuristicsVisitor;
import org.oristool.eulero.evaluation.heuristics.SDFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;
import org.oristool.eulero.ui.ActivityViewer;

public class DAGAnalyzer {
    private Activity rootActivity;

    public DAGAnalyzer(Activity rooActivity) {
        this.rootActivity = rooActivity;
    }

    public void analyze(){
        
        AnalysisHeuristicsVisitor visitor = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation());

        double[] cdf = rootActivity.analyze(rootActivity.max().add(BigDecimal.ONE), rootActivity.getFairTimeTick(), visitor);


        ActivityViewer.CompareResults("", List.of("A", "B", "test"), List.of(
                new EvaluationResult("A", cdf, 0, cdf.length, rootActivity.getFairTimeTick().doubleValue(), 0)
        ));

        System.out.println("Timestep used: " + rootActivity.getFairTimeTick().toString());

    }

    public void test1(){
        StochasticTime pdf12 = new UniformTime(1, 2);
        StochasticTime pdf13 = new UniformTime(0, 1);
        StochasticTime pdf02 = new UniformTime(0, 2);
        StochasticTime pdf08 = new UniformTime(0, 8);
        StochasticTime pdf05 = new UniformTime(0, 5);

        Activity t14 = new Simple("v0", pdf13);
        Activity t15 = new Simple("v1", pdf12);
        Activity t12 = ModelFactory.forkJoin(t14, t15);

        Activity t13 = new Simple("v2", pdf02);
        Activity p13 = ModelFactory.sequence(t12, t13);

        Activity p21 = new Simple("v0", pdf08);

        Activity p23 = ModelFactory.forkJoin(p13, p21);

        Activity t24 = new Simple("t24", pdf05);

        Activity p14 = ModelFactory.sequence(p23, t24);
        
        AnalysisHeuristicsVisitor start = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation());

        // double[] cdf = A.analyze(A.max().add(BigDecimal.ONE), A.getFairTimeTick(), start);
        double[] cdf2 = p14.analyze(p14.max().add(BigDecimal.ONE), p14.getFairTimeTick(), start);


        ActivityViewer.CompareResults("", List.of("A", "B", "test"), List.of(
                new EvaluationResult("B", cdf2, 0, cdf2.length, p14.getFairTimeTick().doubleValue(), 0)
        ));

        System.out.println("Timestep used: " + p14.getFairTimeTick().toString());
    }

    public void test2(){
        StochasticTime pdf = new UniformTime(0, 1);
        
        Activity t0 = new Simple("t0", pdf);
        Activity t1 = new Simple("t1", pdf);

        Activity s0 = ModelFactory.sequence(t0,t1);

        Activity t3 = new Simple("t3", pdf);
        Activity t5 = new Simple("t5", pdf);

        t3.addPrecondition(s0);
        t5.addPrecondition(t3, s0);


        AnalysisHeuristicsVisitor start = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation());
        double[] cdf = t5.analyze(t5.max().add(BigDecimal.ONE), t5.getFairTimeTick(), start);


        ActivityViewer.CompareResults("", List.of("A", "test"), List.of(
                new EvaluationResult("A", cdf, 0, cdf.length, t5.getFairTimeTick().doubleValue(), 0)
        ));

        System.out.println("Timestep used: " + t5.getFairTimeTick().toString());


    }
}
