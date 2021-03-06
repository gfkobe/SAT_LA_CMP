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
package edu.buaa.satla.analysis.core.algorithm;

import edu.buaa.satla.analysis.core.reachedset.ReachedSet;
import edu.buaa.satla.analysis.exceptions.CPAException;
import edu.buaa.satla.analysis.exceptions.PredicatedAnalysisPropertyViolationException;

public interface Algorithm {

  /**
   * Run the algorithm on the given set of abstract states and the given waitlist.
   *
   * @param reachedSet Input.
   * @return False if the analysis was unsound (this is not the analysis result!).
   * @throws CPAException
   * @throws InterruptedException
   */
  public boolean run(ReachedSet reachedSet) throws CPAException, InterruptedException, PredicatedAnalysisPropertyViolationException;
}
