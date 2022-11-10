package sim

import kotlin.random.Random
import kotlin.random.nextInt

class ComputeNode(val id: Int, val maxPerStepPerCore: Triple<Int, Int, Int>, val cores: Int) {

    companion object {
        fun newRandom(id: Int) = ComputeNode(id, Triple(Random.nextInt(50..100), Random.nextInt(50..100), Random.nextInt(50..100)), Random.nextInt(2..8))
    }
}

private fun <A: Comparable<A>> smallestPerComponent(a: Triple<A, A, A>, b: Triple<A, A, A>) = Triple(
    if (a.first < b.first) a.first else b.first,
    if (a.second < b.second) a.second else b.second,
    if (a.third < b.third) a.third else b.third
)
