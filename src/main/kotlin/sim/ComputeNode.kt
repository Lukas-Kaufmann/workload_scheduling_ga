package sim

import div
import minus
import plus
import times
import kotlin.random.Random
import kotlin.random.nextInt

class ComputeNode(val id: Int, val maxPerStepPerCore: Triple<Int, Int, Int>, val cores: Int) {

    companion object {
        fun newRandom(id: Int) = ComputeNode(id, Triple(Random.nextInt(50..100), Random.nextInt(50..100), Random.nextInt(50..100)), Random.nextInt(4..16))
    }

    fun run(steps: Int, workloads: MutableList<WorkLoad>): Double {
        val utilizations = mutableListOf<Triple<Double, Double, Double>>()

        repeat(steps) { step ->
            var maxTotal = maxPerStepPerCore * cores

            val reqRes = workloads
                .map { Pair(it, it.requiredResources(step)) }

            val demand = reqRes.map { it.second }.fold(Triple(0, 0, 0)) { sum, element -> sum + element }

            utilizations.add(demand / maxTotal)

            reqRes.forEach {
                var givenResources = smallestPerComponent(it.second, maxPerStepPerCore)
                givenResources = smallestPerComponent(givenResources, maxTotal)
                maxTotal -= givenResources
                it.first.runWithResources(givenResources)
            }
        }
        return utilizations.map {
            it.toList().average()
        }.average()
    }
}

private fun <A: Comparable<A>> smallestPerComponent(a: Triple<A, A, A>, b: Triple<A, A, A>) = Triple(
    if (a.first < b.first) a.first else b.first,
    if (a.second < b.second) a.second else b.second,
    if (a.third < b.third) a.third else b.third
)
