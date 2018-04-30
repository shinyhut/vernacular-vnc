package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class ServerInitTest extends Specification {

    def "should decode a valid ServerInit message"() {
        given:
        def input = new ByteArrayInputStream([
                0x03, 0x20, // framebuffer width = 800
                0x02, 0x58, // framebuffer height = 600
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // pixel format
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x04, // server name length = 4
                0x74, 0x65, 0x73, 0x74 // server name = 'test'

        ] as byte[])

        when:
        def result = ServerInit.decode(input)

        then:
        result.framebufferWidth == 800
        result.framebufferHeight == 600
        result.pixelFormat
        result.name == 'test'
    }
}
