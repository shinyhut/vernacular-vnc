package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class SecurityResultTest extends Specification {

    def "should decode an authentication succeeded SecurityResult"() {
        given:
        def input = new ByteArrayInputStream([0x00, 0x00, 0x00, 0x00] as byte[])

        when:
        def result = SecurityResult.decode(input, new ProtocolVersion(3, 8))

        then:
        result.success
    }

    def "should decode an RFB Protocol Version 3.7 authentication failed SecurityResult"() {
        given:
        def input = new ByteArrayInputStream([0x00, 0x00, 0x00, 0x01] as byte[])

        when:
        def result = SecurityResult.decode(input, new ProtocolVersion(3, 7))

        then:
        !result.success
        result.errorMessage == null
    }

    def "should decode an RFB Protocol Version 3.8 authentication failed SecurityResult and error message"() {
        given:
        def input = new ByteArrayInputStream(
                ([0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x05] + ('Fail!'.getBytes('US-ASCII') as List)) as byte[])

        when:
        def result = SecurityResult.decode(input, new ProtocolVersion(3, 8))

        then:
        !result.success
        result.errorMessage == 'Fail!'
    }
}
