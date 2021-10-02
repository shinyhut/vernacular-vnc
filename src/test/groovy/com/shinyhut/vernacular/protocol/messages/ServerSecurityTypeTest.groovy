package com.shinyhut.vernacular.protocol.messages

import com.shinyhut.vernacular.client.exceptions.HandshakingFailedException
import spock.lang.Specification
import spock.lang.Unroll

import static com.shinyhut.vernacular.protocol.messages.SecurityType.MS_LOGON_2
import static com.shinyhut.vernacular.protocol.messages.SecurityType.NONE
import static com.shinyhut.vernacular.protocol.messages.SecurityType.VNC

class ServerSecurityTypeTest extends Specification {

    @Unroll
    def "should decode a valid ServerSecurityType message"() {
        given:
        def input = new ByteArrayInputStream([0x00, 0x00, 0x00, securityType.code] as byte[])

        when:
        def message = ServerSecurityType.decode(input)

        then:
        message.securityType == securityType

        where:
        securityType << [NONE, VNC, MS_LOGON_2]
    }

    def "should throw an exception if the response contains an error message"() {
        given:
        def input = new ByteArrayInputStream(
                ([0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05] + ('Fail!'.getBytes('US-ASCII') as List)) as byte[])

        when:
        ServerSecurityType.decode(input)

        then:
        def e = thrown(HandshakingFailedException)
        e.message == 'VNC handshaking failed. The server returned the following error message: Fail!'
        e.serverMessage == 'Fail!'
    }

}
