package com.qesm;

import java.math.BigDecimal;

import org.oristool.models.pn.Priority;
import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.TransientSolutionViewer;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Transition;

public class TestPetri {
  public static void main(String[] args) {
    PetriNet net = new PetriNet();
    Marking marking = new Marking();
    
    Place p10 = net.addPlace("p10");
    Place p11 = net.addPlace("p11");
    Place p12 = net.addPlace("p12");
    Place p13 = net.addPlace("p13");
    Place p14 = net.addPlace("p14");
    Place p20 = net.addPlace("p20");
    Place p21 = net.addPlace("p21");
    Place p22 = net.addPlace("p22");
    Place p23 = net.addPlace("p23");
    Place p7 = net.addPlace("p7");
    Place p8 = net.addPlace("p8");
    Place p9 = net.addPlace("p9");
    Transition t12 = net.addTransition("t12");
    Transition t13 = net.addTransition("t13");
    Transition t14 = net.addTransition("t14");
    Transition t15 = net.addTransition("t15");
    Transition t21 = net.addTransition("t21");
    Transition t22 = net.addTransition("t22");
    Transition t23 = net.addTransition("t23");
    Transition t24 = net.addTransition("t24");
    Transition t9 = net.addTransition("t9");

    //Generating Connectors
    net.addPostcondition(t9, p8);
    net.addPostcondition(t12, p12);
    net.addPrecondition(p11, t12);
    net.addPrecondition(p23, t24);
    net.addPrecondition(p21, t23);
    net.addPostcondition(t21, p21);
    net.addPostcondition(t24, p14);
    net.addPrecondition(p10, t12);
    net.addPrecondition(p12, t13);
    net.addPrecondition(p7, t9);
    net.addPrecondition(p22, t22);
    net.addPostcondition(t23, p23);
    net.addPrecondition(p20, t21);
    net.addPostcondition(t9, p9);
    net.addPostcondition(t22, p20);
    net.addPostcondition(t22, p7);
    net.addPostcondition(t13, p13);
    net.addPostcondition(t15, p10);
    net.addPrecondition(p13, t23);
    net.addPrecondition(p8, t14);
    net.addPostcondition(t14, p11);
    net.addPrecondition(p9, t15);

    //Generating Properties
    marking.setTokens(p10, 0);
    marking.setTokens(p11, 0);
    marking.setTokens(p12, 0);
    marking.setTokens(p13, 0);
    marking.setTokens(p14, 0);
    marking.setTokens(p20, 0);
    marking.setTokens(p21, 0);
    marking.setTokens(p22, 1);
    marking.setTokens(p23, 0);
    marking.setTokens(p7, 0);
    marking.setTokens(p8, 0);
    marking.setTokens(p9, 0);
    t12.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
    t12.addFeature(new Priority(0));
    t13.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("0"), new BigDecimal("2")));
    t14.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("1"), new BigDecimal("3")));
    t15.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("1"), new BigDecimal("2")));
    t21.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("0"), new BigDecimal("8")));
    t22.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
    t22.addFeature(new Priority(0));
    t23.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
    t23.addFeature(new Priority(0));
    t24.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("0"), new BigDecimal("5")));
    t9.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
    t9.addFeature(new Priority(0));
    
    RegTransient analysis = RegTransient.builder()
        .greedyPolicy(new BigDecimal("5"), new BigDecimal("0.005"))
        .timeStep(new BigDecimal("0.1"))
        .build();

    TransientSolution<DeterministicEnablingState, Marking> solution =
        analysis.compute(net, marking);

    // Display transient probabilities
    new TransientSolutionViewer(solution);
  }
}
