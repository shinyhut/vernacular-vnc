package com.shinyhut.vernacular.protocol.auth

import com.shinyhut.vernacular.client.VernacularConfig
import com.shinyhut.vernacular.client.VncSession
import spock.lang.Specification
import spock.lang.Subject

class NoSecurityHandlerTest extends Specification {

    @Subject
    handler = new NoSecurityHandler()

    def "should request SecurityType 'NONE'"() {
        given:
        def session = new VncSession('host', 0, Mock(VernacularConfig), new ByteArrayInputStream(), new ByteArrayOutputStream())

        when:
        handler.authenticate(session)

        then:
        session.outputStream.toByteArray() == [0x01]
    }
}
