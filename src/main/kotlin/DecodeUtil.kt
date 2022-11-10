fun grayToBin(gray: List<Int>): List<Int> {
    val bin = (1..gray.size).map { gray[0] }.toMutableList()

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

fun binToDec(bin: List<Int>) : Int {
    var n = 0
    for ((index, value) in bin.reversed().withIndex()) {
        n += value * (1 shl index)
    }
    return n
}