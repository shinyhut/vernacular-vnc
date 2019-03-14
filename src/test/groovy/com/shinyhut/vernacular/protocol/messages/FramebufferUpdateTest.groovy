package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class FramebufferUpdateTest extends Specification {

    def "should decode a valid FramebufferUpdate message"() {
        given:
        def input = new ByteArrayInputStream(([
                0x00, // message type
                0x00, // padding
                0x00, 0x01 // 1 rectangle
        ]) as byte[])

        when:
        def result = FramebufferUpdate.decode(input)

        then:
        result.numberOfRectangles == 1
    }
}
