package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class BellTest extends Specification {

    def "should decode a valid Bell message"() {
        given:
        def input = new ByteArrayInputStream([0x02] as byte[])

        when:
        def result = Bell.decode(input)

        then:
        result
    }
}
