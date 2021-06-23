package com.droidbaza.data

interface Mapper<Input, Output> {
    fun map(input: Input): Output
}
