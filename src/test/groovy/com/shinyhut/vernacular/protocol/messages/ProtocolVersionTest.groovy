package com.shinyhut.vernacular.protocol.messages

import com.shinyhut.vernacular.client.exceptions.InvalidMessageException
import spock.lang.Specification

class ProtocolVersionTest extends Specification {

    def "should decode a valid ProtocolVersion message"() {
        given:
        def input = new ByteArrayInputStream(
                [0x52, 0x46, 0x42, 0x20, 0x30, 0x30, 0x32, 0x2e, 0x30, 0x30, 0x33, 0x0a] as byte[]
        )

        when:
        def message = ProtocolVersion.decode(input)

        then:
        message.major == 2
        message.minor == 3
    }

    def "should throw an exception if the ProtocolVersion message is invalid"() {
        given:
        def input = new ByteArrayInputStream(
                [0x53, 0x46, 0x42, 0x20, 0x30, 0x30, 0x33, 0x2e, 0x30, 0x30, 0x33, 0x0a] as byte[]
        )

        when:
        ProtocolVersion.decode(input)

        then:
        def e = thrown InvalidMessageException
        e.messageType == 'ProtocolVersion'
    }

    def "should encode a valid ProtocolVersion message"() {
        given:
        def message = new ProtocolVersion(2, 3)
        def output = new ByteArrayOutputStream()

        when:
        message.encode(output)

        then:
        output.toByteArray() == [0x52, 0x46, 0x42, 0x20, 0x30, 0x30, 0x32, 0x2e, 0x30, 0x30, 0x33, 0x0a]
    }
}
