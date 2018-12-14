package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class ClientCutTextTest extends Specification {

    def "should encode a valid ClientCutText message"() {
        given:
        def message = new ClientCutText('test')
        def output = new ByteArrayOutputStream()

        when:
        message.encode(output)

        then:
        output.toByteArray() == [
                0x06, // message type
                0x00, 0x00, 0x00, // padding
                0x00, 0x00, 0x00, 0x04, // text length = 4
                0x74, 0x65, 0x73, 0x74  // text = 'test'
        ] as byte[]
    }
}
