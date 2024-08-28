package com.qesm;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.oristool.eulero.evaluation.approximator.TruncatedExponentialMixtureApproximation;
import org.oristool.eulero.evaluation.heuristics.AnalysisHeuristicsVisitor;
import org.oristool.eulero.evaluation.heuristics.EvaluationResult;
import org.oristool.eulero.evaluation.heuristics.RBFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
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

import com.qesm.RandomDAGGenerator.PdfType;

public class Main {
    public static void main(String[] args) {

        ensureFolderExists("media");
        ensureFolderExists("output");

        WorkflowType graphTest = new WorkflowType();
        graphTest.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        // for (int i = 0; i < 20; i++){
        // graphTest.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);

        // String title = graphTest.computeParallelismValue();

        // graphTest.toUnshared();
        // graphTest.exportDotFile("./output/" + title + ".dot");
        // Renderer.renderDotFile("./output/" + title + ".dot", "./media/" + title +
        // ".svg");
        // }
        // WorkflowIstance workflow = graphTest.makeIstance();
        // workflow.exportDotFile("./output/shared_Workflow.dot");
        // Renderer.renderDotFile("./output/shared_Workflow.dot",
        // "./media/shared_Workflow.svg");

        // Optional<ProductIstance> optionalProduct = workflow.findProduct("v0");
        // if(optionalProduct.isPresent()){
        // ProductIstance product = optionalProduct.get();
        // System.out.println("Name: " + product.getName());
        // }

        // graphTest.exportDagToDotFile("./output/sharedDAG.dot");
        // graphTest.importDagFromDotFile("./output/sharedDAG.dot");
        // graphTest.exportDagToDotFile("./output/sharedDAG.dot");
        // Renderer.renderDotFile("./output/sharedDAG.dot", "./media/shared.svg", 3);
        // graphTest.toUnshared();

        // graphTest.exportDagToDotFile("./output/unsharedDAG.dot");
        // // graphTest.importDagFromDotFile("./output/unsharedDAG.dot");
        // Renderer.renderDotFile("./output/unsharedDAG.dot", "./media/unshared.svg",
        // 3);

        // Workflow workflow = graphTest.makeIstance();
        // workflow.exportDagToDotFile("./output/istance.dot");
        // Renderer.renderDotFile("./output/istance.dot", "./media/istance.svg", 3);
        // int i = 2;
        // for (Product product : workflow.getDag().vertexSet()) {
        // if(product.getItemType() == ItemType.PROCESSED){
        // i--;
        // if(i == 0){
        // product.getProductWorkflow().exportDagToDotFile("./output/istanceSubgraph.dot");
        // Renderer.renderDotFile("./output/istanceSubgraph.dot",
        // "./media/istanceSubgraph.svg", 3);
        // }
        // }

        // }

        StructuredTree<ProductType> structuredTree = new StructuredTree<ProductType>(graphTest.getDagCopy(),
                ProductType.class);

        structuredTree.buildStructuredTree();

        String structuredTreeDotFolder = mkEmptyDir("./output/structuredTree");
        String structuredTreeMediaFolder = mkEmptyDir("./media/structuredTree");

        structuredTree.buildStructuredTreeAndExportSteps(structuredTreeDotFolder, false);
        Renderer.renderAllDotFile(structuredTreeDotFolder, structuredTreeMediaFolder);

        StructuredTreeConverter structuredTreeConverter = new StructuredTreeConverter(
                structuredTree.getStructuredWorkflow());
        Activity rootActivity = structuredTreeConverter.convertToActivity();
        System.out.println(rootActivity);

        PetriNet net = new PetriNet();
        Place pOut = net.addPlace("FINAL_PLACE");
        Place pIn = net.addPlace("STARTING_PLACE");
        rootActivity.buildSTPN(net, pIn, pOut, 0);

        // System.out.println(net);

        analyzeActivity(rootActivity);
        analizePetriNet(net, pOut, pIn);

    }

    public static Activity generateTestActivity() {
        StochasticTime pdf = new ExponentialTime(BigDecimal.valueOf(5));

        Activity t0 = new Simple("t0", pdf);

        return ModelFactory.DAG(t0);
    }

    public static Activity generateTestActivity2() {
        StochasticTime pdf = new UniformTime(0, 1);

        Activity t0 = new Simple("t0", pdf);
        Activity t1 = new Simple("t1", pdf);

        Activity s0 = ModelFactory.sequence(t0, t1);

        Activity t3 = new Simple("t3", pdf);
        Activity t5 = new Simple("t5", pdf);

        t3.addPrecondition(s0);
        t5.addPrecondition(t3);
        return ModelFactory.DAG(t3, s0, t5);
    }

    public static void analyzeActivity(Activity rootActivity) {
        System.setProperty("java.awt.headless", "false");

        AnalysisHeuristicsVisitor visitor = new RBFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN,
                new TruncatedExponentialMixtureApproximation());

        double[] cdf = rootActivity.analyze(rootActivity.max().add(BigDecimal.ONE), rootActivity.getFairTimeTick(),
                visitor);

        ActivityViewer.CompareResults("", List.of("A", "test"), List.of(
                new EvaluationResult("A", cdf, 0, cdf.length, 0.01, 0)));

        // System.out.println("Timestep used: " +
        // rootActivity.getFairTimeTick().toString());

    }

    public static void analizePetriNet(PetriNet net, Place pReward, Place pStart) {

        RegTransient analysis = RegTransient.builder()
                .greedyPolicy(new BigDecimal("6"), new BigDecimal("0.005"))
                .timeStep(new BigDecimal("0.01"))
                .build();

        Marking marking = new Marking();
        marking.setTokens(pStart, 1);
        TransientSolution<DeterministicEnablingState, RewardRate> solution = TransientSolution.computeRewards(false,
                analysis.compute(net, marking), pReward.getName());

        // Display transient probabilities
        new TransientSolutionViewer(solution);
    }

    public static String mkEmptyDir(String folderPath) {
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.getName().endsWith(".dot") || file.getName().endsWith(".svg")) {
                    file.delete();
                }
            }
        } else {
            folder.mkdir();
        }

        return folderPath;
    }

    public static void ensureFolderExists(String folderPath) {
        // Create a File object with the folder path
        File folder = new File(folderPath);

        // Check if the folder exists
        if (!folder.exists()) {
            // If the folder doesn't exist, try to create it
            boolean created = folder.mkdir();
            if (created) {
                System.out.println("The folder " + folderPath + " was created successfully.");
            } else {
                System.out.println("Error creating folder " + folderPath);
            }
        }
    }
}