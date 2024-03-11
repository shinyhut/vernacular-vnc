package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class ClientCutTextExtendedClipboardTest extends Specification {

    def "should encode a valid ClientCutTextExtendedClipboard message"() {
        given:
        def message = new ClientCutTextExtendedClipboard('test')
        def output = new ByteArrayOutputStream()

        when:
        message.encode(output)

        then:
        output.toByteArray() == [
                0x06, // message type
                0x00, 0x00, 0x00, // padding
                -0x01, -0x01, -0x01, -0x15, // -1 * (compressed text length + U32 flags)
                0x10, 0x00, 0x00, 0x01,  // flags
                0x78, -0x64, 0x63, 0x60, 0x60, 0x60, 0x2d, 0x49, 0x2d, 0x2e, 0x61, 0x00, 0x00, 0x06, 0x40, 0x01, -0x3a // compressed text
        ] as byte[]
    }

}
