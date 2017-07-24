package com.shinyhut.vernacular.protocol.messages

import spock.lang.Specification

class ClientInitTest extends Specification {

    def "should encode a valid ClientInit message"() {
        given:
        def message = new ClientInit(shared)
        def output = new ByteArrayOutputStream()

        when:
        message.encode(output)

        then:
        output.toByteArray() == [byteValue] as byte[]

        where:
        shared | byteValue
        true   | 1
        false  | 0
    }
}
