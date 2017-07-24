package com.shinyhut.vernacular.client.rendering.renderers

import com.shinyhut.vernacular.protocol.messages.PixelFormat
import spock.lang.Specification
import spock.lang.Subject

class PixelDecoderTest extends Specification {

    @Subject
    def decoder = new PixelDecoder()

    def "should decode a valid Pixel in the specified format into RGB colour values"() {
        given:
        def pixelFormat = new PixelFormat(16, 16, true, true, 31, 63, 31, 11, 5, 0)
        def pixel = [0xff, 0xff] as byte[]

        when:
        def result = decoder.decode(pixel, pixelFormat)

        then:
        result.red == 255
        result.green == 255
        result.blue == 255
    }
}
