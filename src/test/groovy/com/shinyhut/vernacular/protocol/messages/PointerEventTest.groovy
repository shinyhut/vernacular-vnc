package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

import static java.lang.Integer.parseInt

class PointerEventTest extends Specification {

    def "should encode a valid PointerEvent message"() {
        given:
        def message = new PointerEvent(160, 100, [true, false, false, true, false, false, false, true])
        def output = new ByteArrayOutputStream()

        when:
        message.encode(output)

        then:
        output.toByteArray() == [0x05, parseInt('10001001', 2), 0x00, 0xA0, 0x00, 0x64] as byte[]
    }
}
