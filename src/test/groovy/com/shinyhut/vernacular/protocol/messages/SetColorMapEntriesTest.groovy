package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class SetColorMapEntriesTest extends Specification {

    def "should decode a valid SetColorMapEntries message"() {
        given:
        def input = new ByteArrayInputStream([
                0x01, // message type
                0x00, // padding
                0x00, 0x05, // first color
                0x00, 0x03, // number of colors
                0xff, 0xff, 0x00, 0x00, 0x00, 0x00, // color 1 (full red)
                0x00, 0x00, 0xff, 0xff, 0x00, 0x00, // color 2 (full green)
                0x00, 0x00, 0x00, 0x00, 0xff, 0xff  // color 3 (full blue)

        ] as byte[])

        when:
        def result = SetColorMapEntries.decode(input)

        then:
        result.firstColor == 5
        result.colors.size() == 3
        result.colors.red == [65535, 0, 0]
        result.colors.green == [0, 65535, 0]
        result.colors.blue == [0, 0, 65535]
    }
}
