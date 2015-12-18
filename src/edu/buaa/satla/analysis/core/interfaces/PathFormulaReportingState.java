package edu.buaa.satla.analysis.core.interfaces;

import edu.buaa.satla.analysis.util.predicates.interfaces.view.FormulaManagerView;
import edu.buaa.satla.analysis.util.predicates.pathformula.PathFormula;
import edu.buaa.satla.analysis.util.predicates.pathformula.SSAMap;

/**
 * Report the over-approximation of the abstract state using the formula
 * language.
 *
 * Similar to {@link FormulaReportingState}, but uses {@link PathFormula} instead.
 */
public interface PathFormulaReportingState extends AbstractState{
  /**
   *
   * @param manager Manager used to create the formulas.
   * @param outMap {@link SSAMap} on the output constraints.
   * @param inputMap {@link SSAMap} on the input constraints.
   * @return Formula together with an updated output {@link SSAMap}
   */
  public PathFormula getFormulaApproximation(
      FormulaManagerView manager, SSAMap outMap, SSAMap inputMap);

}
