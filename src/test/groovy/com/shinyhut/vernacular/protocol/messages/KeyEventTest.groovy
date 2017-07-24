package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class KeyEventTest extends Specification {

    def "should encode a valid KeyEvent message"() {
        given:
        def message = new KeyEvent(123, true)
        def output = new ByteArrayOutputStream()

        when:
        message.encode(output)

        then:
        output.toByteArray() == [0x04, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7B] as byte[]
    }
}
