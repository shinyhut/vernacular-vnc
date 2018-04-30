package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

import static com.shinyhut.vernacular.protocol.messages.Encoding.*

class SetEncodingsTest extends Specification {

    def "should encode a valid SetEncodings message"() {
        given:
        def message = new SetEncodings(RRE, COPYRECT, RAW, DESKTOP_SIZE)
        def output = new ByteArrayOutputStream()

        when:
        message.encode(output)

        then:
        output.toByteArray() == [
                0x02, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x02,
                0x00, 0x00, 0x00, 0x01,
                0x00, 0x00, 0x00, 0x00,
                0xFF, 0xFF, 0xFF, 0x21
        ] as byte[]
    }
}
