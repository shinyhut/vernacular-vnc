package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class ServerCutTextTest extends Specification {

    def "should decode a valid ServerCutText message"() {
        given:
        def input = new ByteArrayInputStream([
                0x03, // message type
                0x00, 0x00, 0x00, // padding
                0x00, 0x00, 0x00, 0x04, // text length = 4
                0x74, 0x65, 0x73, 0x74  // text = 'test'

        ] as byte[])

        when:
        def result = ServerCutText.decode(input)

        then:
        result.text == 'test'
    }
}
