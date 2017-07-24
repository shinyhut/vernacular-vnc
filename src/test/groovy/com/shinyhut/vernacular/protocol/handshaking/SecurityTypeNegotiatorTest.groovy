package com.shinyhut.vernacular.protocol.handshaking

import com.shinyhut.vernacular.client.VncSession
import com.shinyhut.vernacular.protocol.auth.NoSecurityHandler
import com.shinyhut.vernacular.protocol.auth.VncAuthenticationHandler
import spock.lang.Specification
import spock.lang.Subject

import static com.shinyhut.vernacular.protocol.messages.SecurityType.NONE
import static com.shinyhut.vernacular.protocol.messages.SecurityType.VNC

class SecurityTypeNegotiatorTest extends Specification {

    @Subject
    def negotiator = new SecurityTypeNegotiator()

    def "should prefer 'No Authentication' if offered, or VNC authentication otherwise"() {
        given:
        def message = [serverTypes.size()]
        serverTypes.each { message += it.ordinal() }
        def bytes = new ByteArrayInputStream(message as byte[])
        def session = Mock(VncSession)

        when:
        def handler = negotiator.negotiate(session)

        then:
        1 * session.inputStream >> bytes
        handler.class == selected

        where:
        serverTypes | selected
        [NONE]      | NoSecurityHandler
        [NONE, VNC] | NoSecurityHandler
        [VNC]       | VncAuthenticationHandler
    }

}
