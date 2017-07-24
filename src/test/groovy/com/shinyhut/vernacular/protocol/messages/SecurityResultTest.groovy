package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class SecurityResultTest extends Specification {

    def "should decode a valid SecurityResult message"() {
        given:
        def input = new ByteArrayInputStream([0x00, 0x00, 0x00, 0x00] as byte[])

        when:
        def result = SecurityResult.decode(input)

        then:
        result.success
    }

    def "should return failure and an error message if the response indicates failure"() {
        given:
        def input = new ByteArrayInputStream(
                ([0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x05] + ('Fail!'.getBytes('US-ASCII') as List)) as byte[])

        when:
        def result = SecurityResult.decode(input)

        then:
        !result.success
        result.errorMessage == 'Fail!'
    }
}
