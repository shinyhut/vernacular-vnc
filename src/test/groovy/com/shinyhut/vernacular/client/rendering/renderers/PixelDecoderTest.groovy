package com.shinyhut.vernacular.client.rendering.renderers

import com.shinyhut.vernacular.protocol.messages.ColorMapEntry
import com.shinyhut.vernacular.protocol.messages.PixelFormat
import spock.lang.Specification
import spock.lang.Subject

class PixelDecoderTest extends Specification {

    @Subject
    def decoder = new PixelDecoder()

    def "should decode a valid true color Pixel in the specified format into RGB colour values"() {
        given:
        def pixelFormat = new PixelFormat(16, 16, true, true, 31, 63, 31, 11, 5, 0)
        def pixel = [0xff, 0xff] as byte[]

        when:
        def result = decoder.decode(pixel, pixelFormat, [:])

        then:
        result.red == 255
        result.green == 255
        result.blue == 255
    }

    def "should decode a valid indexed color Pixel in the specified format into RGB colour values"() {
        given:
        def pixelFormat = new PixelFormat(8, 8, true, false, 0, 0, 0, 0, 0, 0)
        def pixel = [1] as byte[]
        def colorMap = [(1 as BigInteger): new ColorMapEntry(1000, 2000, 3000)]

        when:
        def result = decoder.decode(pixel, pixelFormat, colorMap)

        then:
        result.red == 4
        result.green == 8
        result.blue == 12
    }
}
