package com.shinyhut.vernacular.protocol.auth

import com.shinyhut.vernacular.client.VernacularConfig
import com.shinyhut.vernacular.client.VncSession
import com.shinyhut.vernacular.protocol.messages.ProtocolVersion
import spock.lang.Specification
import spock.lang.Subject

class NoSecurityHandlerTest extends Specification {

    @Subject
    handler = new NoSecurityHandler()

    def "for protocol version 3.3, sends no data and returns a 'success' result"() {
        given:
        def session = new VncSession(Mock(VernacularConfig), new ByteArrayInputStream(), new ByteArrayOutputStream())
        session.protocolVersion = new ProtocolVersion(3, 3)

        when:
        def result = handler.authenticate(session)

        then:
        session.outputStream.toByteArray() == []
        result.success
    }

    def "for protocol version 3.7, should request SecurityType 'NONE' and return a 'success' result"() {
        given:
        def session = new VncSession(Mock(VernacularConfig), new ByteArrayInputStream(), new ByteArrayOutputStream())
        session.protocolVersion = new ProtocolVersion(3, 7)

        when:
        def result = handler.authenticate(session)

        then:
        session.outputStream.toByteArray() == [0x01]
        result.success
    }

    def "for protocol version 3.8, should request SecurityType 'NONE' and return the server's response"() {
        given:
        def input = new ByteArrayInputStream([0x00, 0x00, 0x00, 0x00] as byte[])
        def session = new VncSession(Mock(VernacularConfig), input, new ByteArrayOutputStream())
        session.protocolVersion = new ProtocolVersion(3, 8)

        when:
        def result = handler.authenticate(session)

        then:
        session.outputStream.toByteArray() == [0x01]
        result.success
    }
}
