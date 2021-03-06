/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package edu.buaa.satla.analysis.core.predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.buaa.satla.analysis.core.predicate.persistence.PredicatePersistenceUtils.splitFormula;
import static edu.buaa.satla.analysis.util.statistics.StatisticsUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import org.sosy_lab.common.Pair;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.FileOption;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.common.io.Files;
import org.sosy_lab.common.io.Path;
import org.sosy_lab.common.io.Paths;
import org.sosy_lab.common.time.NestedTimer;
import org.sosy_lab.common.time.TimeSpan;
import org.sosy_lab.common.time.Timer;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.CPAcheckerResult.Result;
import org.sosy_lab.cpachecker.core.interfaces.MergeOperator;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.core.interfaces.WrapperPrecision;
import org.sosy_lab.cpachecker.core.reachedset.ReachedSet;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import edu.buaa.satla.analysis.core.predicate.persistence.LoopInvariantsWriter;
import edu.buaa.satla.analysis.core.predicate.persistence.PredicateAbstractionsWriter;
import edu.buaa.satla.analysis.core.predicate.persistence.PredicateMapWriter;
import edu.buaa.satla.analysis.util.CFAUtils;
import edu.buaa.satla.analysis.util.predicates.AbstractionManager;
import edu.buaa.satla.analysis.util.predicates.AbstractionPredicate;
import edu.buaa.satla.analysis.util.predicates.BlockOperator;
import edu.buaa.satla.analysis.util.predicates.Solver;
import edu.buaa.satla.analysis.util.predicates.AbstractionManager.RegionCreator;
import edu.buaa.satla.analysis.util.predicates.interfaces.BooleanFormula;
import edu.buaa.satla.analysis.util.predicates.interfaces.PathFormulaManager;
import edu.buaa.satla.analysis.util.predicates.interfaces.ProverEnvironment;
import edu.buaa.satla.analysis.util.predicates.interfaces.RegionManager;
import edu.buaa.satla.analysis.util.predicates.interfaces.ProverEnvironment.AllSatResult;
import edu.buaa.satla.analysis.util.predicates.interfaces.view.BooleanFormulaManagerView;
import edu.buaa.satla.analysis.util.predicates.interfaces.view.FormulaManagerView;
import edu.buaa.satla.analysis.util.predicates.pathformula.CachingPathFormulaManager;
import edu.buaa.satla.analysis.util.predicates.pathformula.PathFormula;
import edu.buaa.satla.analysis.util.statistics.AbstractStatistics;

@Options(prefix="cpa.predicate")
class PredicateCPAStatistics extends AbstractStatistics {

  @Option(secure=true, description="export final predicate map",
          name="predmap.export")
  private boolean exportPredmap = true;

  @Option(secure=true, description="file for exporting final predicate map",
          name="predmap.file")
  @FileOption(FileOption.Type.OUTPUT_FILE)
  private Path predmapFile = Paths.get("predmap.txt");

  @Option(secure=true, name="precondition.file", description="File for exporting the weakest precondition.")
  @FileOption(FileOption.Type.OUTPUT_FILE)
  private Path preconditionFile = Paths.get("precondition.txt");
  @Option(secure=true, name="precondition.export", description="Export the weakest precondition?")
  private boolean preconditionExport = false;

  @Option(secure=true, description="export final loop invariants",
          name="invariants.export")
  private boolean exportInvariants = true;

  @Option(secure=true, description="export invariants as precision file?",
      name="invariants.exportAsPrecision")
  private boolean exportInvariantsAsPrecision = true;

  @Option(secure=true, description="file for exporting final loop invariants",
          name="invariants.file")
  @FileOption(FileOption.Type.OUTPUT_FILE)
  private Path invariantsFile = Paths.get("invariants.txt");

  @Option(secure=true, description="file for precision that consists of invariants.",
          name="invariants.precisionFile")
  @FileOption(FileOption.Type.OUTPUT_FILE)
  private Path invariantPrecisionsFile = Paths.get("invariantPrecs.txt");

  @Option(secure=true, description="file that consists of one abstraction formula for each abstraction state",
      name="abstractions.file")
  @FileOption(FileOption.Type.OUTPUT_FILE)
  private Path abstractionsFile = Paths.get("abstractions.txt");

  private final PredicateCPA cpa;
  private final BlockOperator blk;
  private final RegionManager rmgr;
  private final AbstractionManager absmgr;
  private final PredicateMapWriter precisionWriter;
  private final LoopInvariantsWriter loopInvariantsWriter;
  private final PreconditionWriter preconditionWriter;
  private final PredicateAbstractionsWriter abstractionsWriter;

  private final Timer invariantGeneratorTime;

  public PredicateCPAStatistics(PredicateCPA pCpa, BlockOperator pBlk,
      RegionManager pRmgr, AbstractionManager pAbsmgr, CFA pCfa,
      PreconditionWriter pPreconditions, Timer pInvariantGeneratorTimer, Configuration pConfig)
          throws InvalidConfigurationException {

    cpa = pCpa;
    blk = pBlk;
    rmgr = pRmgr;
    absmgr = pAbsmgr;
    invariantGeneratorTime = checkNotNull(pInvariantGeneratorTimer);

    pConfig.inject(this, PredicateCPAStatistics.class);

    loopInvariantsWriter = new LoopInvariantsWriter(pCfa, cpa.getLogger(), pAbsmgr, cpa.getFormulaManager(), pRmgr);
    abstractionsWriter = new PredicateAbstractionsWriter(cpa.getLogger(), pAbsmgr, cpa.getFormulaManager());
    preconditionWriter = checkNotNull(pPreconditions);

    if (exportPredmap && predmapFile != null) {
      precisionWriter = new PredicateMapWriter(cpa.getConfiguration(), cpa.getFormulaManager());
    } else {
      precisionWriter = null;
    }
  }

  @Override
  public String getName() {
    return "PredicateCPA";
  }

  /**
   * TreeMap to sort output for the user and sets for no duplication.
   */
  private static class MutablePredicateSets {

    private static Supplier<Set<AbstractionPredicate>> hashSetSupplier = new Supplier<Set<AbstractionPredicate>>() {
        @Override
        public Set<AbstractionPredicate> get() {
          return Sets.newHashSet();
        }
      };

    private final SetMultimap<Pair<CFANode, Integer>, AbstractionPredicate> locationInstance;
    private final SetMultimap<CFANode, AbstractionPredicate> location;
    private final SetMultimap<String, AbstractionPredicate> function;
    private final Set<AbstractionPredicate> global;

    private MutablePredicateSets() {
      // Use special multimaps with set-semantics and an ordering only on keys (not on values)
      this.locationInstance = Multimaps.newSetMultimap(
          new TreeMap<Pair<CFANode, Integer>, Collection<AbstractionPredicate>>(
              Pair.<CFANode, Integer>lexicographicalNaturalComparator()),
          hashSetSupplier);

      this.location = Multimaps.newSetMultimap(new TreeMap<CFANode, Collection<AbstractionPredicate>>(), hashSetSupplier);
      this.function = Multimaps.newSetMultimap(new TreeMap<String, Collection<AbstractionPredicate>>(), hashSetSupplier);
      this.global = Sets.newHashSet();
    }

  }

  private void exportPredmapToFile(Path targetFile, MutablePredicateSets predicates) {
    Preconditions.checkNotNull(targetFile);
    Preconditions.checkNotNull(predicates);

    Set<AbstractionPredicate> allPredicates = Sets.newHashSet(predicates.global);
    allPredicates.addAll(predicates.function.values());
    allPredicates.addAll(predicates.location.values());
    allPredicates.addAll(predicates.locationInstance.values());

    try (Writer w = Files.openOutputFile(targetFile)) {
      precisionWriter.writePredicateMap(predicates.locationInstance,
          predicates.location, predicates.function, predicates.global,
          allPredicates, w);

      /**
       * XXX
       *
       */
      try {
        SetMultimap<CFANode, AbstractionPredicate> localPredicates = predicates.location;
        FormulaManagerView fmgrv = cpa.getFormulaManager();
        for (AbstractionPredicate pred : localPredicates.values()) {
          Pair<String, List<String>> p = splitFormula(fmgrv, pred.getSymbolicAtom());
          String predString = p.getFirst();// 谓词的字符串表示

        }

        PathFormulaManager pfmgr = cpa.getPathFormulaManager();

        PredicateAbstractionManager pmgr = cpa.getPredicateManager();
        RegionCreator rmgr = pmgr.getRegionCreator();
        final NestedTimer abstractionEnumTime = new NestedTimer(); // outer: solver time, inner: bdd time
        final Timer abstractionSolveTime = new Timer(); // only the time for solving, not for model enumeration

        // 参考上面的precisionWriter.writePredicateMap生成predMap的方法
        for (Entry<CFANode, Collection<AbstractionPredicate>> e : localPredicates.asMap().entrySet()) {
          CFANode n = e.getKey();
          Collection<AbstractionPredicate> nodePredicates = e.getValue();

          ProverEnvironment thmProver = pmgr.getProverEnvironment();
          // 获取谓词的布尔公式表示集合
          BooleanFormulaManagerView bfmgr = fmgrv.getBooleanFormulaManager();
          BooleanFormula predDef = bfmgr.makeBoolean(true);
          List<BooleanFormula> predVars = new ArrayList<>(nodePredicates.size());
//          List<BooleanFormula> booleanFormulas = new ArrayList<>();
          for (AbstractionPredicate pred : nodePredicates) {
            // get propositional variable and definition of predicate
            BooleanFormula var = pred.getSymbolicVariable();
            BooleanFormula def = pred.getSymbolicAtom();
//            booleanFormulas.add(pred.getSymbolicAtom());

            assert !bfmgr.isFalse(def);
//            def = fmgr.instantiate(def, ssa);
            // build the formula (var <-> def) and add it to the list of definitions
            BooleanFormula equiv = bfmgr.equivalence(var, def);
            predDef = bfmgr.and(predDef, equiv);
            predVars.add(var);
          }

          for (CFAEdge edge : CFAUtils.allLeavingEdges(n)) {
            List<CFAEdge> edges = new ArrayList<>();
            edges.add(edge);
            PathFormula pf = pfmgr.makeFormulaForPath(edges);
            predDef = bfmgr.and(predDef, pf.getFormula());
//            booleanFormulas.add(pf.getFormula());
            String pfStr = pf.toString();

            thmProver.push(predDef);
            AllSatResult satResult = thmProver.allSat(predVars, rmgr, abstractionSolveTime, abstractionEnumTime);
            int cnt = satResult.getCount();
          }
        }
      } catch(Exception e) {

      }
      // 自己加的
//      try {
//        PathFormulaManager pfmgr = cpa.getPathFormulaManager();
//        CFA cfa = GlobalInfo.getInstance().getCFAInfo().get().getCFA();
//
//
//        for (FunctionEntryNode fnode : cfa.getAllFunctionHeads()) {
//          CFAEdge edge = fnode.getLeavingEdge(0);
//          if (edge instanceof MultiEdge) {
//            List<CFAEdge> edges = new ArrayList<>();
//            edges.add(edge);
//            PathFormula pf = pfmgr.makeFormulaForPath(edges);
//            System.out.println(pf);
//          }
//        }
//      } catch (Exception e) {
//        // TODO: handle exception
//      }

    } catch (IOException e) {
      cpa.getLogger().logUserException(Level.WARNING, e, "Could not write predicate map to file");
    }
  }


  @Override
  public void printStatistics(PrintStream out, Result result, ReachedSet reached) {
    PredicateAbstractionManager amgr = cpa.getPredicateManager();

    MutablePredicateSets predicates = new MutablePredicateSets();
    {
      Set<Precision> seenPrecisions = Collections.newSetFromMap(new IdentityHashMap<Precision, Boolean>());

      for (Precision precision : reached.getPrecisions()) {
        if (seenPrecisions.add(precision) && precision instanceof WrapperPrecision) {
          PredicatePrecision preds = ((WrapperPrecision)precision).retrieveWrappedPrecision(PredicatePrecision.class);
          predicates.locationInstance.putAll(preds.getLocationInstancePredicates());
          predicates.location.putAll(preds.getLocalPredicates());
          predicates.function.putAll(preds.getFunctionPredicates());
          predicates.global.addAll(preds.getGlobalPredicates());
        }
      }
    }

    // check if/where to dump the predicate map
    if (exportPredmap && predmapFile != null) {
      exportPredmapToFile(predmapFile, predicates);
    }

    int maxPredsPerLocation = 0;
    for (Collection<AbstractionPredicate> p : predicates.location.asMap().values()) {
      maxPredsPerLocation = Math.max(maxPredsPerLocation, p.size());
    }

    int allLocs = predicates.location.keySet().size();
    int totPredsUsed = predicates.location.size();
    int avgPredsPerLocation = allLocs > 0 ? totPredsUsed/allLocs : 0;

    int allDistinctPreds = absmgr.getNumberOfPredicates();

    if (preconditionExport && preconditionFile != null) {
      preconditionWriter.writePrecondition(preconditionFile, reached, cpa.logger);
    }

    if (exportInvariants && invariantsFile != null) {
      loopInvariantsWriter.exportLoopInvariants(invariantsFile, reached);
    }

    if (abstractionsFile != null) {
      abstractionsWriter.writeAbstractions(abstractionsFile, reached);
    }

    if (exportInvariantsAsPrecision && invariantPrecisionsFile != null) {
      loopInvariantsWriter.exportLoopInvariantsAsPrecision(invariantPrecisionsFile, reached);
    }

    PredicateAbstractionManager.Stats as = amgr.stats;
    PredicateAbstractDomain domain = cpa.getAbstractDomain();
    PredicateTransferRelation trans = cpa.getTransferRelation();
    PredicatePrecisionAdjustment prec = cpa.getPrecisionAdjustment();
    Solver solver = cpa.getSolver();

    CachingPathFormulaManager pfMgr = null;
    if (cpa.getPathFormulaManager() instanceof CachingPathFormulaManager) {
      pfMgr = (CachingPathFormulaManager)cpa.getPathFormulaManager();
    }

    out.println("Number of abstractions:            " + prec.numAbstractions + " (" + toPercent(prec.numAbstractions, trans.postTimer.getNumberOfIntervals()) + " of all post computations)");
    if (prec.numAbstractions > 0) {
      out.println("  Times abstraction was reused:    " + as.numAbstractionReuses);
      out.println("  Because of function entry/exit:  " + valueWithPercentage(blk.numBlkFunctions, prec.numAbstractions));
      out.println("  Because of loop head:            " + valueWithPercentage(blk.numBlkLoops, prec.numAbstractions));
      out.println("  Because of join nodes:           " + valueWithPercentage(blk.numBlkJoins, prec.numAbstractions));
      out.println("  Because of threshold:            " + valueWithPercentage(blk.numBlkThreshold, prec.numAbstractions));
      out.println("  Times precision was empty:       " + valueWithPercentage(as.numSymbolicAbstractions, as.numCallsAbstraction));
      out.println("  Times precision was {false}:     " + valueWithPercentage(as.numSatCheckAbstractions, as.numCallsAbstraction));
      out.println("  Times result was cached:         " + valueWithPercentage(as.numCallsAbstractionCached, as.numCallsAbstraction));
      out.println("  Times cartesian abs was used:    " + valueWithPercentage(as.cartesianAbstractionTime.getNumberOfIntervals(), as.numCallsAbstraction));
      out.println("  Times boolean abs was used:      " + valueWithPercentage(as.booleanAbstractionTime.getNumberOfIntervals(), as.numCallsAbstraction));
      out.println("  Times result was 'false':        " + valueWithPercentage(prec.numAbstractionsFalse, prec.numAbstractions));
    }

    if (trans.satCheckTimer.getNumberOfIntervals() > 0) {
      out.println("Number of satisfiability checks:   " + trans.satCheckTimer.getNumberOfIntervals());
      out.println("  Times result was 'false':        " + trans.numSatChecksFalse + " (" + toPercent(trans.numSatChecksFalse, trans.satCheckTimer.getNumberOfIntervals()) + ")");
    }
    out.println("Number of strengthen sat checks:   " + trans.strengthenCheckTimer.getNumberOfIntervals());
    if (trans.strengthenCheckTimer.getNumberOfIntervals() > 0) {
      out.println("  Times result was 'false':        " + trans.numStrengthenChecksFalse + " (" + toPercent(trans.numStrengthenChecksFalse, trans.strengthenCheckTimer.getNumberOfIntervals()) + ")");
    }
    out.println("Number of coverage checks:         " + domain.coverageCheckTimer.getNumberOfIntervals());
    out.println("  BDD entailment checks:           " + domain.bddCoverageCheckTimer.getNumberOfIntervals());
    if (domain.symbolicCoverageCheckTimer.getNumberOfIntervals() > 0) {
      out.println("  Symbolic coverage check:         " + domain.symbolicCoverageCheckTimer.getNumberOfIntervals());
    }
    out.println("Number of SMT sat checks:          " + solver.satChecks);
    out.println("  trivial:                         " + solver.trivialSatChecks);
    out.println("  cached:                          " + solver.cachedSatChecks);
    out.println();
    out.println("Max ABE block size:                       " + prec.maxBlockSize);
    out.println("Number of predicates discovered:          " + allDistinctPreds);
    if (allDistinctPreds > 0) {
      out.println("Number of abstraction locations:          " + allLocs);
      out.println("Max number of predicates per location:    " + maxPredsPerLocation);
      out.println("Avg number of predicates per location:    " + avgPredsPerLocation);
    }
    int numAbstractions = as.numCallsAbstraction-as.numSymbolicAbstractions;
    if (numAbstractions > 0) {
      int numRealAbstractions = as.numCallsAbstraction - as.numSymbolicAbstractions - as.numCallsAbstractionCached;
      out.println("Total predicates per abstraction:         " + as.numTotalPredicates);
      out.println("Max number of predicates per abstraction: " + as.maxPredicates);
      out.println("Avg number of predicates per abstraction: " + div(as.numTotalPredicates, numRealAbstractions));
      out.println("Number of irrelevant predicates:          " + valueWithPercentage(as.numIrrelevantPredicates, as.numTotalPredicates));
      if (as.trivialPredicatesTime.getNumberOfIntervals() > 0) {
        out.println("Number of trivially used predicates:      " + valueWithPercentage(as.numTrivialPredicates, as.numTotalPredicates));
      }
      if (as.cartesianAbstractionTime.getNumberOfIntervals() > 0) {
        out.println("Number of preds cached for cartesian abs: " + valueWithPercentage(as.numCartesianAbsPredicatesCached, as.numTotalPredicates));
        out.println("Number of preds solved by cartesian abs:  " + valueWithPercentage(as.numCartesianAbsPredicates, as.numTotalPredicates));
      }
      if (as.booleanAbstractionTime.getNumberOfIntervals() > 0) {
        out.println("Number of preds handled by boolean abs:   " + valueWithPercentage(as.numBooleanAbsPredicates, as.numTotalPredicates));
        out.println("  Total number of models for allsat:      " + as.allSatCount);
        out.println("  Max number of models for allsat:        " + as.maxAllSatCount);
        out.println("  Avg number of models for allsat:        " + div(as.allSatCount, as.booleanAbstractionTime.getNumberOfIntervals()));
      }
    }
    out.println();
    if (pfMgr != null) {
      int pathFormulaCacheHits = pfMgr.pathFormulaCacheHits;
      int totalPathFormulaComputations = pfMgr.pathFormulaComputationTimer.getNumberOfIntervals() + pathFormulaCacheHits;
      out.println("Number of path formula cache hits:   " + pathFormulaCacheHits + " (" + toPercent(pathFormulaCacheHits, totalPathFormulaComputations) + ")");
    }

    out.println();

    out.println("Time for post operator:              " + trans.postTimer);
    out.println("  Time for path formula creation:    " + trans.pathFormulaTimer);
    if (pfMgr != null) {
      out.println("    Actual computation:              " + pfMgr.pathFormulaComputationTimer);
    }
    if (trans.satCheckTimer.getNumberOfIntervals() > 0) {
      out.println("  Time for satisfiability checks:    " + trans.satCheckTimer);
    }
    out.println("Time for strengthen operator:        " + trans.strengthenTimer);
    if (trans.strengthenCheckTimer.getNumberOfIntervals() > 0) {
      out.println("  Time for satisfiability checks:    " + trans.strengthenCheckTimer);
    }
    out.println("Time for prec operator:              " + prec.totalPrecTime);
    if (prec.numAbstractions > 0) {
      if (invariantGeneratorTime.getNumberOfIntervals() > 0) {
        out.println("  Time for generating invariants:    " + invariantGeneratorTime);
        out.println("  Time for extracting invariants:    " + prec.invariantGenerationTime);
      }
      out.println("  Time for abstraction:              " + prec.computingAbstractionTime + " (Max: " + prec.computingAbstractionTime.getMaxTime().formatAs(SECONDS) + ", Count: " + prec.computingAbstractionTime.getNumberOfIntervals() + ")");
      if (as.trivialPredicatesTime.getNumberOfIntervals() > 0) {
        out.println("    Relevant predicate analysis:     " + as.trivialPredicatesTime);
      }
      if (as.cartesianAbstractionTime.getNumberOfIntervals() > 0) {
        out.println("    Cartesian abstraction:           " + as.cartesianAbstractionTime);
      }
      if (as.booleanAbstractionTime.getNumberOfIntervals() > 0) {
        out.println("    Boolean abstraction:             " + as.booleanAbstractionTime);
      }
      if (as.abstractionReuseTime.getNumberOfIntervals() > 0) {
        out.println("    Abstraction reuse:              " + as.abstractionReuseTime);
        out.println("    Abstraction reuse implication:  " + as.abstractionReuseImplicationTime);
      }
      out.println("    Solving time:                    " + as.abstractionSolveTime + " (Max: " + as.abstractionSolveTime.getMaxTime().formatAs(SECONDS) + ")");
      out.println("    Model enumeration time:          " + as.abstractionEnumTime.getOuterSumTime().formatAs(SECONDS));
      out.println("    Time for BDD construction:       " + as.abstractionEnumTime.getInnerSumTime().formatAs(SECONDS)   + " (Max: " + as.abstractionEnumTime.getInnerMaxTime().formatAs(SECONDS) + ")");
    }

    MergeOperator merge = cpa.getMergeOperator();
    if (merge instanceof PredicateMergeOperator) {
      out.println("Time for merge operator:             " + ((PredicateMergeOperator)merge).totalMergeTime);
    }

    out.println("Time for coverage check:             " + domain.coverageCheckTimer);
    if (domain.bddCoverageCheckTimer.getNumberOfIntervals() > 0) {
      out.println("  Time for BDD entailment checks:    " + domain.bddCoverageCheckTimer);
    }
    if (domain.symbolicCoverageCheckTimer.getNumberOfIntervals() > 0) {
      out.println("  Time for symbolic coverage checks: " + domain.symbolicCoverageCheckTimer);
    }
    out.println("Total time for SMT solver (w/o itp): " + TimeSpan.sum(solver.solverTime.getSumTime(), as.abstractionSolveTime.getSumTime(), as.abstractionEnumTime.getOuterSumTime()).formatAs(SECONDS));

    if (trans.abstractionCheckTimer.getNumberOfIntervals() > 0) {
      out.println("Time for abstraction checks:       " + trans.abstractionCheckTimer);
      out.println("Time for unsat checks:             " + trans.satCheckTimer + " (Calls: " + trans.satCheckTimer.getNumberOfIntervals() + ")");
    }
    out.println();
    rmgr.printStatistics(out);
  }
}