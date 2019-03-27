package com.shinyhut.vernacular.protocol.auth

import com.shinyhut.vernacular.client.VernacularConfig
import com.shinyhut.vernacular.client.VncSession
import com.shinyhut.vernacular.protocol.messages.ProtocolVersion
import spock.lang.Specification
import spock.lang.Subject

class NoSecurityHandlerTest extends Specification {

    @Subject
    handler = new NoSecurityHandler()

    def "should request SecurityType 'NONE' and, for RFB Protocol Version 3.7, return a 'success' result"() {
        given:
        def session = new VncSession('host', 0, Mock(VernacularConfig), new ByteArrayInputStream(), new ByteArrayOutputStream())
        session.protocolVersion = new ProtocolVersion(3, 7)

        when:
        def result = handler.authenticate(session)

        then:
        session.outputStream.toByteArray() == [0x01]
        result.success
    }

    def "should request SecurityType 'NONE' and, for RFB Protocol Version 3.8, return the server's response"() {
        given:
        def input = new ByteArrayInputStream([0x00, 0x00, 0x00, 0x00] as byte[])
        def session = new VncSession('host', 0, Mock(VernacularConfig), input, new ByteArrayOutputStream())
        session.protocolVersion = new ProtocolVersion(3, 8)

        when:
        def result = handler.authenticate(session)

        then:
        session.outputStream.toByteArray() == [0x01]
        result.success
    }
}
