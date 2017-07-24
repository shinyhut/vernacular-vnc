package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

import static com.shinyhut.vernacular.protocol.messages.Encoding.RAW

class SetEncodingsTest extends Specification {

    def "should encode a valid SetEncodings message"() {
        given:
        def message = new SetEncodings(RAW)
        def output = new ByteArrayOutputStream()

        when:
        message.encode(output)

        then:
        output.toByteArray() == [0x02, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00] as byte[]
    }
}
