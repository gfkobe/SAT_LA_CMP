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

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static edu.buaa.satla.analysis.util.AbstractStates.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.sosy_lab.common.collect.PersistentMap;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.common.time.Timer;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.algorithm.invariants.InvariantGenerator;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.core.interfaces.PrecisionAdjustment;
import org.sosy_lab.cpachecker.core.reachedset.UnmodifiableReachedSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import edu.buaa.satla.analysis.core.predicate.PredicateAbstractState.ComputeAbstractionState;
import edu.buaa.satla.analysis.core.predicate.synthesis.AbstractionInstanceSynthesis;
import edu.buaa.satla.analysis.exceptions.CPAException;
import edu.buaa.satla.analysis.util.predicates.AbstractionFormula;
import edu.buaa.satla.analysis.util.predicates.AbstractionPredicate;
import edu.buaa.satla.analysis.util.predicates.interfaces.BooleanFormula;
import edu.buaa.satla.analysis.util.predicates.interfaces.PathFormulaManager;
import edu.buaa.satla.analysis.util.predicates.interfaces.view.BooleanFormulaManagerView;
import edu.buaa.satla.analysis.util.predicates.interfaces.view.FormulaManagerView;
import edu.buaa.satla.analysis.util.predicates.pathformula.PathFormula;

public class PredicatePrecisionAdjustment implements PrecisionAdjustment {

  // statistics
  final Timer totalPrecTime = new Timer();
  final Timer invariantGenerationTime = new Timer();
  final Timer computingAbstractionTime = new Timer();

  int numAbstractions = 0;
  int numAbstractionsFalse = 0;
  int maxBlockSize = 0;

  private final LogManager logger;
  private final PredicateAbstractionManager formulaManager;
  private final PathFormulaManager pathFormulaManager;
  private final FormulaManagerView fmgr;
  private final BooleanFormulaManagerView bfmgr;

  private @Nullable InvariantGenerator invariantGenerator;
  private @Nullable Map<CFANode, BooleanFormula> invariants = null;

  private final AbstractionInstanceSynthesis precSynthesis;

  public PredicatePrecisionAdjustment(PredicateCPA pCpa,
      InvariantGenerator pInvariantGenerator,
      AbstractionInstanceSynthesis pPrecSynthesis) {

    logger = pCpa.getLogger();
    formulaManager = pCpa.getPredicateManager();
    pathFormulaManager = pCpa.getPathFormulaManager();
    fmgr = pCpa.getFormulaManager();
    bfmgr = fmgr.getBooleanFormulaManager();

    invariantGenerator = checkNotNull(pInvariantGenerator);
    precSynthesis = checkNotNull(pPrecSynthesis);
  }

  @Override
  public PrecisionAdjustmentResult prec(
      AbstractState pElement, Precision pPrecision,
      UnmodifiableReachedSet pElements) throws CPAException, InterruptedException {

    totalPrecTime.start();
    try {
      PredicateAbstractState element = (PredicateAbstractState)pElement;

      if (element instanceof ComputeAbstractionState) {
        PredicatePrecision precision = (PredicatePrecision)pPrecision;

        element = computeAbstraction((ComputeAbstractionState)element, precision);
      }

      return PrecisionAdjustmentResult.create(element, pPrecision, Action.CONTINUE);

    } finally {
      totalPrecTime.stop();
    }
  }

  /**
   * Compute an abstraction.
   */
  private PredicateAbstractState computeAbstraction(
      ComputeAbstractionState element,
      PredicatePrecision precision) throws CPAException, InterruptedException {

    AbstractionFormula abstractionFormula = element.getAbstractionFormula();
    PersistentMap<CFANode, Integer> abstractionLocations = element.getAbstractionLocationsOnPath();
    PathFormula pathFormula = element.getPathFormula();
    CFANode loc = element.getLocation();
    Integer newLocInstance = firstNonNull(abstractionLocations.get(loc), 0) + 1;

    numAbstractions++;
    logger.log(Level.FINEST, "Computing abstraction at instance", newLocInstance, "of node", loc, "in path.");

    maxBlockSize = Math.max(maxBlockSize, pathFormula.getLength());

    // get invariants and add them
    extractInvariants();
    BooleanFormula invariant = invariants.get(loc);
    if (invariant != null) {
      pathFormula = pathFormulaManager.makeAnd(pathFormula, invariant);
    }

    AbstractionFormula newAbstractionFormula = null;

    // compute new abstraction
    computingAbstractionTime.start();
    try {
      // the basic precision
      Collection<AbstractionPredicate> locInstancePreds = precision.getPredicates(loc, newLocInstance);

      // the precision synthesis module might add additional predicates...
      Collection<AbstractionPredicate> synthPreds =
          precSynthesis.getSyntheticPredicates(element, loc, newLocInstance);

      // union of the predicates that should be considered for computing the abstraction
      Collection<AbstractionPredicate> toConsiderPreds = ImmutableList.<AbstractionPredicate>builder()
          .addAll(synthPreds).addAll(locInstancePreds).build();

      // compute a new abstraction with a precision based on `preds`
      newAbstractionFormula = formulaManager.buildAbstraction(
          loc, abstractionFormula, pathFormula, toConsiderPreds);
    } finally {
      computingAbstractionTime.stop();
    }

    // if the abstraction is false, return bottom (represented by empty set)
    if (newAbstractionFormula.isFalse()) {
      numAbstractionsFalse++;
      logger.log(Level.FINEST, "Abstraction is false, node is not reachable");
    }

    // create new empty path formula
    PathFormula newPathFormula = pathFormulaManager.makeEmptyPathFormula(pathFormula);

    // initialize path formula with current invariants
    if (invariant != null) {
      newPathFormula = pathFormulaManager.makeAnd(newPathFormula, invariant);
    }

    // update abstraction locations map
    abstractionLocations = abstractionLocations.putAndCopy(loc, newLocInstance);

    return PredicateAbstractState.mkAbstractionState(bfmgr, newPathFormula,
        newAbstractionFormula, abstractionLocations);
  }

  private void extractInvariants() throws CPAException {
    if (invariants != null) {
      return; // already done
    }

    invariantGenerationTime.start();
    try {

      UnmodifiableReachedSet reached = invariantGenerator.get();
      if (reached.isEmpty()) {
        invariants = ImmutableMap.of(); // no invariants available
        return;
      }

      invariants = new HashMap<>();

      for (AbstractState state : reached) {
        BooleanFormula invariant = extractReportedFormulas(fmgr, state);

        if (!bfmgr.isTrue(invariant)) {
          CFANode loc = extractLocation(state);
          BooleanFormula oldInvariant = invariants.get(loc);
          if (oldInvariant != null) {
            invariant = bfmgr.or(invariant, oldInvariant);
          }

          invariants.put(loc, invariant);
        }
      }

      invariants = ImmutableMap.copyOf(invariants);

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      invariants = ImmutableMap.of(); // no invariants available

    } finally {
      invariantGenerator = null; // to allow GC'ing it and the ReachedSet
      invariantGenerationTime.stop();
    }
  }

  void setInitialLocation(CFANode initialLocation) {
    invariantGenerator.start(initialLocation);
  }
}
