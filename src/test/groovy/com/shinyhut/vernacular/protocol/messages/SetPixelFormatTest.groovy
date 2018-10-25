package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class SetPixelFormatTest extends Specification {

    def "should encode a valid SetPixelFormat message"() {
        given:
        def message = new SetPixelFormat(new PixelFormat(32, 24, false, true, 256, 256, 256, 0, 8, 16))
        def output = new ByteArrayOutputStream()

        when:
        message.encode(output)

        then:
        output.toByteArray() == [
                0x00, // message type
                0x00, 0x00, 0x00, // padding
                0x20, // 32 bits per pixel
                0x18, // 24 bit depth
                0x00, // big endian = false
                0x01, // true color = true
                0x01, 0x00, // red max 256
                0x01, 0x00, // green max 256
                0x01, 0x00, // blue max 256
                0x00, // red shift 0
                0x08, // green shift 8
                0x10, // blue shift 16
                0x00, 0x00, 0x00 // padding
        ] as byte[]
    }
}
