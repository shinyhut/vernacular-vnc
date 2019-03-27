package com.shinyhut.vernacular.protocol.handshaking

import com.shinyhut.vernacular.client.VncSession
import com.shinyhut.vernacular.client.exceptions.UnsupportedProtocolVersionException
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static java.lang.String.format

class ProtocolVersionNegotiatorTest extends Specification {

    @Subject
    negotiator = new ProtocolVersionNegotiator()

    @Unroll
    def "should request the highest RFB protocol version supported by both the client and the server"() {
        given:
        def bytes = new ByteArrayInputStream(format("RFB %03d.%03d\n", serverMajor, serverMinor).bytes)
        def out = new ByteArrayOutputStream()
        def session = Mock(VncSession)
        _ * session.inputStream >> bytes
        _ * session.outputStream >> out

        when:
        negotiator.negotiate(session)

        then:
        out.toByteArray() == format("RFB %03d.%03d\n", selectedMajor, selectedMinor).bytes

        where:
        serverMajor | serverMinor | selectedMajor | selectedMinor
        3           | 7           | 3             | 7
        3           | 8           | 3             | 8
        3           | 9           | 3             | 8
    }

    def "should throw an exception if the server does not support the minimum RFB protocol version supported by this client"() {
        given:
        def bytes = new ByteArrayInputStream("RFB 003.003\n".bytes)
        def session = Mock(VncSession)
        _ * session.inputStream >> bytes

        when:
        negotiator.negotiate(session)

        then:
        thrown UnsupportedProtocolVersionException
    }
}
