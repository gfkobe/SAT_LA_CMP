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
package edu.buaa.satla.analysis.core.defaults;

import java.util.Collection;

import edu.buaa.satla.analysis.core.interfaces.AbstractState;
import edu.buaa.satla.analysis.core.interfaces.Precision;
import edu.buaa.satla.analysis.core.interfaces.StopOperator;
import edu.buaa.satla.analysis.exceptions.CPAException;

/**
 * Standard stop operator, which always return false
 */
public class StopNeverOperator implements StopOperator {

  @Override
  public boolean stop(AbstractState el, Collection<AbstractState> reached, Precision precision)
    throws CPAException {
    return false;
  }

  private static final StopOperator instance = new StopNeverOperator();

  public static StopOperator getInstance() {
    return instance;
  }

}
