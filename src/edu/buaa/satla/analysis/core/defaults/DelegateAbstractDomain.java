package edu.buaa.satla.analysis.core.defaults;

import edu.buaa.satla.analysis.core.interfaces.AbstractDomain;
import edu.buaa.satla.analysis.core.interfaces.AbstractState;
import edu.buaa.satla.analysis.exceptions.CPAException;

/**
 * Default implementation of the abstract domain which delegates the
 * {@link #join} and {@link #isLessOrEqual} methods to the {@link LatticeAbstractState}
 * implementation.
 *
 * @param <E> Parametrize by the {@link AbstractState} implementation used for
 * the analysis.
 */
public class DelegateAbstractDomain<E extends LatticeAbstractState<E>>
    implements AbstractDomain {
  private DelegateAbstractDomain() {}

  public static <E extends LatticeAbstractState<E>>
        DelegateAbstractDomain<E> getInstance() {
    return new DelegateAbstractDomain<>();
  }


  @Override
  @SuppressWarnings("unchecked")
  public AbstractState join(AbstractState state1, AbstractState state2)
      throws CPAException {
    return ((E) state1).join((E) state2);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean isLessOrEqual(AbstractState state1, AbstractState state2)
      throws CPAException, InterruptedException {
    return ((E) state1).isLessOrEqual((E) state2);
  }
}