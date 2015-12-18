package edu.buaa.satla.analysis.core.defaults;

import edu.buaa.satla.analysis.core.interfaces.AbstractState;
import edu.buaa.satla.analysis.exceptions.CPAException;

/**
 * Interface for the abstract state which supports joining and partial
 * order comparison.
 *
 * Using this class in conjunction with {@link DelegateAbstractDomain}
 * saves the user from writing {@link edu.buaa.satla.analysis.core.interfaces.AbstractDomain}
 * implementation which just delegates the method to the abstract state.
 */
public interface LatticeAbstractState<T extends LatticeAbstractState<T>>
    extends AbstractState{

  /**
   * Delegate method for convenience.
   *
   * See {@link edu.buaa.satla.analysis.core.interfaces.AbstractDomain#join}
   * for the description.
   */
  T join(T other) throws CPAException;

  /**
   * Delegate method for convenience.
   *
   * See {@link edu.buaa.satla.analysis.core.interfaces.AbstractDomain#isLessOrEqual}
   * for the description.
   */
  boolean isLessOrEqual(T other) throws CPAException, InterruptedException;
}
