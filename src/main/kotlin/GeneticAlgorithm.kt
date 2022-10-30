import java.lang.Math.random

class GeneticAlgorithm<T>(
    var population: Collection<T>,
    val score: (individual: T) -> Double,
    val cross: (parents: Pair<T, T>) -> T,
    val mutate: (individual: T) -> T,
    val select: (scoredPopulation: Collection<Pair<Double, T>>) -> T) {

    /**
     * Returns the best individual after the given number of optimization epochs.
     *
     * @param epochs number of optimization epochs.
     * @property mutationProbability a value between 0 and 1, which defines the mutation probability of each child.
     */
    fun run(epochs: Int = 1000, mutationProbability: Double = 0.1): T {
        var scoredPopulation = population.map { Pair(score(it), it) }.sortedByDescending { it.first }

        //TODO more cancellation conditions (if some fitness reached, if no change in n-generations)
        for (i in 0..epochs)
            scoredPopulation = scoredPopulation
                .map { Pair(select(scoredPopulation), select(scoredPopulation)) }
                .map { cross(it) }
                .map { if (random() <= mutationProbability) mutate(it) else it }
                .map { Pair(score(it), it) }
                .sortedByDescending { it.first }

        return scoredPopulation.first().second
    }
}

fun <T> rouletteWheelSelection(scoredPopulation: Collection<Pair<Double, T>>): T {
    var value = scoredPopulation.sumOf { it.first } * random()

    for ((fitness, individual) in scoredPopulation) {
        value -= fitness
        if (value <= 0) return individual
    }

    return scoredPopulation.last().second
}