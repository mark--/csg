package de.markschaefer.csg

fun Double.format(digits: Int) = "%+.${digits}f".format(this)
