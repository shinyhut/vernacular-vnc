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

    def "should decode a valid Rectangle in DESKTOPSIZE encoding"() {
        given:
        def bitsPerPixel = 32
        def input = new ByteArrayInputStream(([
                0x00, 0x00, // x = 0
                0x00, 0x00, // y = 0
                0x00, 0x02, // width = 2
                0x00, 0x02, // height = 2
                0xFF, 0xFF, 0xFF, 0x21 // desktopsize encoding
        ]) as byte[])

        when:
        def result = Rectangle.decode(input, bitsPerPixel)

        then:
        result.x == 0
        result.y == 0
        result.width == 2
        result.height == 2
        result.pixelData == [] as byte[]
    }

    def "should decode a valid Rectangle in RRE encoding"() {
        given:
        def bitsPerPixel = 32
        def pixels = [
                0x00, 0x00, 0x00, 0x01, // 1 subrectangle
                0x01, 0x02, 0x03, 0x04, // background pixel value
                0x04, 0x03, 0x02, 0x01, // subrectangle1 pixel value
                0x01, 0x02, // subrectangle 1 x-position
                0x03, 0x04, // subrectangle 1 y-position
                0x05, 0x06, // subrectangle 1 width
                0x07, 0x08, // subrectangle 1 height
        ] as byte[]
        def input = new ByteArrayInputStream(([
                0x00, 0xA0, // x = 160
                0x00, 0x64, // y = 100
                0x00, 0x02, // width = 2
                0x00, 0x02, // height = 2
                0x00, 0x00, 0x00, 0x02 // RRE encoding
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

    def "should decode a valid Rectangle in HEXTILE encoding"() {
        given:
        def bitsPerPixel = 8
        def tile1Pixels = new byte[256]
        new Random().nextBytes(tile1Pixels)
        def pixels = ([
                0x01, // tile 1 sub-encoding (raw)
        ] + (tile1Pixels as List) + [
                0x02, // tile 2 sub-encoding (background only)
                0xff, // tile 2 background color
        ]) as byte[]
        def input = new ByteArrayInputStream(([
                0x00, 0xA0, // x = 160
                0x00, 0x64, // y = 100
                0x00, 0x20, // width = 32
                0x00, 0x10, // height = 16
                0x00, 0x00, 0x00, 0x05 // HEXTILE encoding
        ] + (pixels as List)) as byte[])

        when:
        def result = Rectangle.decode(input, bitsPerPixel)

        then:
        result.x == 160
        result.y == 100
        result.width == 32
        result.height == 16
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
                0x00, 0x00, 0x00, 0x0f // TRLE encoding
        ] + (pixels as List)) as byte[])

        when:
        Rectangle.decode(input, bitsPerPixel)

        then:
        thrown UnsupportedEncodingException
    }
}
