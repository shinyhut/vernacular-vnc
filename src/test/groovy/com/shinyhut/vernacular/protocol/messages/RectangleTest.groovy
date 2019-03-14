package com.shinyhut.vernacular.protocol.messages

import com.shinyhut.vernacular.client.exceptions.UnsupportedEncodingException
import spock.lang.Specification

class RectangleTest extends Specification {

    def "should decode a valid Rectangle"() {
        given:
        def input = new ByteArrayInputStream(([
                0x00, 0xA0, // x = 160
                0x00, 0x64, // y = 100
                0x00, 0x02, // width = 2
                0x00, 0x02, // height = 2
                0x00, 0x00, 0x00, 0x00 // raw encoding
        ]) as byte[])

        when:
        def result = Rectangle.decode(input)

        then:
        result.x == 160
        result.y == 100
        result.width == 2
        result.height == 2
        result.encoding == Encoding.RAW
    }

    def "should throw an exception if we try to decode a Rectangle with an unsupported pixel format"() {
        given:
        def input = new ByteArrayInputStream(([
                0x00, 0xA0, // x = 160
                0x00, 0x64, // y = 100
                0x00, 0x02, // width = 2
                0x00, 0x02, // height = 2
                0x00, 0x00, 0x00, 0x0f // TRLE encoding
        ]) as byte[])

        when:
        Rectangle.decode(input)

        then:
        thrown UnsupportedEncodingException
    }



}
