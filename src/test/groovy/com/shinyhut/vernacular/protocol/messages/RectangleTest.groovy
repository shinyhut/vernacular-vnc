package com.shinyhut.vernacular.protocol.messages

import com.shinyhut.vernacular.client.exceptions.UnsupportedEncodingException
import spock.lang.Specification

class RectangleTest extends Specification {

    def "should decode a valid Rectangle in RAW encoding"() {
        given:
        def bitsPerPixel = 32
        def pixels = new byte[16]
        new Random().nextBytes(pixels)
        def input = new ByteArrayInputStream(([
            0x00, 0xA0, // x = 160
            0x00, 0x64, // y = 100
            0x00, 0x02, // width = 2
            0x00, 0x02, // height = 2
            0x00, 0x00, 0x00, 0x00 // raw encoding
        ] + (pixels as List)) as byte[])

        when:
        def result = Rectangle.decode(input, bitsPerPixel)

        then:
        result.x == 160
        result.y == 100
        result.width == 2
        result.height == 2
        result.pixelData == pixels
    }

    def "should decode a valid Rectangle in COPYRECT encoding"() {
        given:
        def bitsPerPixel = 32
        def pixels = new byte[4]
        new Random().nextBytes(pixels)
        def input = new ByteArrayInputStream(([
                0x00, 0xA0, // x = 160
                0x00, 0x64, // y = 100
                0x00, 0x02, // width = 2
                0x00, 0x02, // height = 2
                0x00, 0x00, 0x00, 0x01 // copyrect encoding
        ] + (pixels as List)) as byte[])

        when:
        def result = Rectangle.decode(input, bitsPerPixel)

        then:
        result.x == 160
        result.y == 100
        result.width == 2
        result.height == 2
        result.pixelData == pixels
    }

    def "should throw an exception if we try to decode a Rectangle with an unsupported pixel format"() {
        given:
        def bitsPerPixel = 32
        def pixels = new byte[16]
        new Random().nextBytes(pixels)
        def input = new ByteArrayInputStream(([
                0x00, 0xA0, // x = 160
                0x00, 0x64, // y = 100
                0x00, 0x02, // width = 2
                0x00, 0x02, // height = 2
                0x00, 0x00, 0x00, 0x05 // hextile encoding
        ] + (pixels as List)) as byte[])

        when:
        Rectangle.decode(input, bitsPerPixel)

        then:
        thrown UnsupportedEncodingException
    }
}
