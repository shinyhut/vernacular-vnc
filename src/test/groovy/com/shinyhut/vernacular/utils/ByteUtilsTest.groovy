package com.shinyhut.vernacular.utils

import spock.lang.Specification

import static java.lang.Integer.parseInt

class ByteUtilsTest extends Specification {

    def "should reverse the bit sequence of the specified byte and return the result"() {
        given:
        def input = parseInt(inputBits, 2).byteValue()

        when:
        def result = ByteUtils.reverseBits(input)

        then:
        result == parseInt(outputBits, 2).byteValue()

        where:
        inputBits  | outputBits
        '01001101' | '10110010'
        '10110000' | '00001101'
    }

    def "should reverse the bit sequence of each of the specified bytes and return the results"() {
        given:
        def input = [0xf0, 0x0f] as byte[]

        when:
        def result = ByteUtils.reverseBits(input)

        then:
        result == [0x0f, 0xf0] as byte[]
    }
}