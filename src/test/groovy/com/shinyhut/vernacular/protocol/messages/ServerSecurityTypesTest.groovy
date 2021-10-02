package com.shinyhut.vernacular.protocol.messages

import com.shinyhut.vernacular.client.exceptions.HandshakingFailedException
import spock.lang.Specification

import static com.shinyhut.vernacular.protocol.messages.SecurityType.MS_LOGON_2
import static com.shinyhut.vernacular.protocol.messages.SecurityType.NONE
import static com.shinyhut.vernacular.protocol.messages.SecurityType.VNC

class ServerSecurityTypesTest extends Specification {

    def "should decode a valid ServerSecurityTypes message"() {
        given:
        def input = new ByteArrayInputStream([0x03, 0x01, 0x02, 0x71] as byte[])

        when:
        def message = ServerSecurityTypes.decode(input)

        then:
        message.securityTypes == [NONE, VNC, MS_LOGON_2]
    }

    def "should throw an exception if the response contains an error message"() {
        given:
        def input = new ByteArrayInputStream(
                ([0x00, 0x00, 0x00, 0x00, 0x05] + ('Fail!'.getBytes('US-ASCII') as List)) as byte[])

        when:
        ServerSecurityTypes.decode(input)

        then:
        def e = thrown(HandshakingFailedException)
        e.message == 'VNC handshaking failed. The server returned the following error message: Fail!'
        e.serverMessage == 'Fail!'
    }

}
