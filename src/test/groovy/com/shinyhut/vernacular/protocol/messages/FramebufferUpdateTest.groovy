package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class FramebufferUpdateTest extends Specification {

    def "should decode a valid FramebufferUpdate message"() {
        given:
        def pixels = new byte[16]
        new Random().nextBytes(pixels)
        def input = new ByteArrayInputStream(([
                0x00, // message type
                0x00, // padding
                0x00, 0x01, // 1 rectangle
                0x00, 0xA0, // x = 160
                0x00, 0x64, // y = 100
                0x00, 0x02, // width = 2
                0x00, 0x02, // height = 2
                0x00, 0x00, 0x00, 0x00 // raw encoding
        ] + (pixels as List)) as byte[])

        when:
        def result = FramebufferUpdate.decode(input, 32)

        then:
        result.rectangles.size() == 1
        result.rectangles[0].x == 160
        result.rectangles[0].y == 100
        result.rectangles[0].width == 2
        result.rectangles[0].height == 2
        result.rectangles[0].pixelData == pixels
    }
}
