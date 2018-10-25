package com.shinyhut.vernacular.protocol.messages

import spock.lang.Shared
import spock.lang.Specification

class PixelFormatTest extends Specification {

    @Shared
    byte[] pixelFormatMessage = [
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
    ]

    def "should decode a valid PixelFormat message"() {
        given:
        def input = new ByteArrayInputStream(pixelFormatMessage)

        when:
        def result = PixelFormat.decode(input)

        then:
        result.bitsPerPixel == 32
        result.depth == 24
        !result.bigEndian
        result.trueColor
        result.redMax == 256
        result.greenMax == 256
        result.blueMax == 256
        result.redShift == 0
        result.greenShift == 8
        result.blueShift == 16
    }

    def "should encode a valid PixelFormat message"() {
        given:
        def message = new PixelFormat(32, 24, false, true, 256, 256, 256, 0, 8, 16)
        def output = new ByteArrayOutputStream()

        when:
        message.encode(output)

        then:
        output.toByteArray() == pixelFormatMessage
    }
}
