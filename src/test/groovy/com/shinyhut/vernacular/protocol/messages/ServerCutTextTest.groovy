package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.zip.Deflater

class ServerCutTextTest extends Specification {

    def "should decode a valid ServerCutText message"() {
        given:
        def input = new ByteArrayInputStream([
                0x03, // message type
                0x00, 0x00, 0x00, // padding
                0x00, 0x00, 0x00, 0x04, // text length = 4
                0x74, 0x65, 0x73, 0x74  // text = 'test'

        ] as byte[])

        when:
        def result = ServerCutText.decode(input)

        then:
        result.text == 'test'
    }

    def "should decode UTF-8 ServerCutText message"() {
        given:

        def inputString  = "test  \\uD801\\uDC00"
        def rawInput = ByteBuffer.allocate(4 + inputString.length())
        rawInput.putInt(inputString.length())
        rawInput.put(inputString.bytes)

        ByteBuffer inputBytes = ByteBuffer.allocate(256)
        def compressor = new Deflater()
        compressor.setInput(rawInput.array())
        compressor.finish();
        def len = compressor.deflate(inputBytes)
        compressor.end()
        inputBytes = inputBytes.slice(0, len)

        // int(message type + padding) + int(uncompressed text length) + int(clipboard type) + (compressed data)
        def arraylen = 4 + 4 + 4 + len

        ByteBuffer buffer = ByteBuffer.allocate(arraylen)
        buffer.put(
                [
                        0x03, // message type
                        0x00, 0x00, 0x00, // padding
                ] as byte[]
        )
        buffer.putInt(-(4 + len)) //text length
        buffer.putInt(ClipboardTypes.CLIPBOARD_PROVIDE)

        buffer.put(inputBytes)

        when:
        def result = ServerCutText.decode(new ByteArrayInputStream(buffer.array()))

        then:
        result.text == inputString
    }
}
