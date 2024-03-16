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

    def "should decode a valid ServerCutText extended format message"() {
        given:
        def input = new ByteArrayInputStream([
                0x03, // message type
                0x00, 0x00, 0x00, // padding
                -0x01, -0x01, -0x01, -0x15, // data length = -21
                0x10, 0x00, 0x00, 0x01,  // flags
                0x78, -0x64, 0x63, 0x60, 0x60, 0x60, 0x2d, 0x49, 0x2d, 0x2e, 0x61, 0x00, 0x00, 0x06, 0x40, 0x01, -0x3a // compressed text

        ] as byte[])

        when:
        def result = ServerCutText.decode(input)

        then:
        result.text == 'test'
    }
}
