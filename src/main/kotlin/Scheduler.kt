import sim.ComputeNode
import sim.WorkLoad
import kotlin.math.pow
import kotlin.math.sin

fun findSchedule(nodes: List<ComputeNode>, workloads: List<WorkLoad>) {
    val populationSize = 50

    var nodeBits = 1
    while (2f.pow(nodeBits) < nodes.size) {
        nodeBits+=1
    }
    var workBits = 1
    while (2f.pow(workBits) < workloads.size) {
        workBits +=1
    }
    val individualSize = workBits + nodeBits

    val population = (1..populationSize).map { (1..individualSize).map { if (Math.random() < 0.5) 0 else 1 } }

    population[0]
        .chunked(individualSize)
        .map { Pair(it.subList(0, workBits), it.subList(workBits+1, individualSize)) }
        .map { Pair(grayToBin(it.first), grayToBin(it.second)) }


    val algorithm = GeneticAlgorithm(
        population,
        score = { sin(it.sum().toDouble()) },
        //TODO two/single point crossovers
        cross = { it.first.mapIndexed { index, i -> if (Math.random() < 0.5) i else it.second[index] } },
        mutate = { it.map { if (Math.random() < 0.9) it else if (Math.random() < 0.5) 0 else 1 } },
        select = ::rouletteWheelSelection
    )

    val result = algorithm.run()

    print("Best individual: ")
    result.forEach { print(it) }
}

fun grayToBin(gray: List<Int>): List<Int> {
    var bin = (1..gray.size).map { gray[0] }.toMutableList()

    for ((index, value ) in gray.withIndex()) {
        if (index == 0) {
            bin[index] = gray[index]
        } else if (value == 0) {
            bin[index] = bin[index-1]
        } else {
            bin[index] = bin[index-1].xor(1)
        }

    }
    return bin
}