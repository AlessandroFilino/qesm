package com.qesm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.jgrapht.graph.DirectedAcyclicGraph;
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

public class DAGAnalyzer {
    private DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow;

    public DAGAnalyzer(DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow) {
        this.structuredWorkflow = structuredWorkflow;
    }

    public void analyze(){
        
        StochasticTime pdf = new UniformTime(0, 1);
        Activity v0 = new Simple("v0", pdf);
        Activity v1 = new Simple("v1", pdf);
        Activity v2 = new Simple("v2", pdf);
        Activity v3 = new Simple("v3", pdf);
        Activity v4 = new Simple("v4", pdf);
        Activity v5 = new Simple("v5", pdf);

        Activity A = ModelFactory.sequence(v0, v1, v2);
        Activity B = ModelFactory.sequence(v3, v4, v5);
        
        AnalysisHeuristicsVisitor start = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation());

        double[] cdf = A.analyze(A.max().add(BigDecimal.ONE), A.getFairTimeTick(), start);
        double[] cdf2 = B.analyze(B.max().add(BigDecimal.ONE), B.getFairTimeTick(), start);

        for (double d : cdf2) {
            System.out.println(d);
        }
        
        ActivityViewer.CompareResults("", List.of("A", "B", "test"), List.of(
                new EvaluationResult("A", cdf, 0, cdf.length, 0.01, 0)
        ));

    }
}
