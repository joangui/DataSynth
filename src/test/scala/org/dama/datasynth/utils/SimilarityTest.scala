package org.dama.datasynth.utils

import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

/**
  * Created by aprat on 21/12/16.
  */
class SimilarityTest extends AssertionsForJUnit {

  @Test def testBuildSimilarity = {
    val samples = List((0,1),(0,1),(0,1))
    val similarity = new Similarity(samples)
    assertTrue(similarity(0,1) == 1.0)
    assertTrue(similarity(0,0) == 0.0)
    assertTrue(similarity(1,1) == 0.0)
  }

}
