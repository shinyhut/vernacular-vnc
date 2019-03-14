package com.shinyhut.vernacular.client.rendering.renderers

import com.shinyhut.vernacular.protocol.messages.ColorMapEntry
import com.shinyhut.vernacular.protocol.messages.PixelFormat
import spock.lang.Specification

class PixelDecoderTest extends Specification {

    def "should decode a valid true color Pixel in the specified format into RGB color values"() {
        given:
        def decoder = new PixelDecoder([:])
        def pixelFormat = new PixelFormat(16, 16, true, true, 31, 63, 31, 11, 5, 0)
        def pixel = [0xff, 0xff] as byte[]
        def input = new ByteArrayInputStream(pixel)

        when:
        def result = decoder.decode(input, pixelFormat)

        then:
        result.red == 255
        result.green == 255
        result.blue == 255
    }

    def "should decode a valid indexed color Pixel in the specified format into RGB color values"() {
        given:
        def pixelFormat = new PixelFormat(8, 8, true, false, 0, 0, 0, 0, 0, 0)
        def pixel = [1] as byte[]
        def colorMap = [(1 as BigInteger): new ColorMapEntry(1000, 2000, 3000)]
        def decoder = new PixelDecoder(colorMap)
        def input = new ByteArrayInputStream(pixel)

        when:
        def result = decoder.decode(input, pixelFormat)

        then:
        result.red == 4
        result.green == 8
        result.blue == 12
    }
}
