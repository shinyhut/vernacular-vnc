package com.shinyhut.vernacular.protocol.auth

import com.shinyhut.vernacular.client.VernacularConfig
import com.shinyhut.vernacular.client.VncSession
import com.shinyhut.vernacular.client.exceptions.AuthenticationRequiredException
import com.shinyhut.vernacular.protocol.messages.ProtocolVersion
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.util.function.Supplier

class VncAuthenticationHandlerTest extends Specification {

    @Subject
    handler = new VncAuthenticationHandler()

    def "for protocol version 3.3, should encrypt the server's challenge and send the result"() {
        given:
        def config = Mock(VernacularConfig) {
            1 * getPasswordSupplier() >> new Supplier<String>() {
                @Override
                String get() {
                    'password'
                }
            }
        }

        def challenge = [
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10
        ]

        def response = [0x00, 0x00, 0x00, 0x00]

        def input = new ByteArrayInputStream((challenge + response) as byte[])

        def session = new VncSession(config, input, new ByteArrayOutputStream())
        session.protocolVersion = new ProtocolVersion(3, 3)

        when:
        def result = handler.authenticate(session)

        then:
        def output = ((ByteArrayOutputStream) session.outputStream).toByteArray() as List
        output.size() == 16
        output.subList(0, 16) == [
                0xab, 0xd2, 0x63, 0x95, 0xc6, 0xfb, 0x36, 0xaf,
                0x42, 0x13, 0x13, 0x33, 0x96, 0xe3, 0x81, 0xc4
        ] as byte[]

        result.success
    }

    @Unroll
    def "for protocol version >= 3.7, should request SecurityType 'VNC', encrypt the server's challenge and send the result"() {
        given:
        def config = Mock(VernacularConfig) {
            1 * getPasswordSupplier() >> new Supplier<String>() {
                @Override
                String get() {
                    'password'
                }
            }
        }

        def challenge = [
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10
        ]

        def response = [0x00, 0x00, 0x00, 0x00]

        def input = new ByteArrayInputStream((challenge + response) as byte[])

        def session = new VncSession(config, input, new ByteArrayOutputStream())
        session.protocolVersion = new ProtocolVersion(3, minorProtocolVersion)

        when:
        def result = handler.authenticate(session)

        then:
        def output = ((ByteArrayOutputStream) session.outputStream).toByteArray() as List
        output.size() == 17
        output[0] == 0x02 as byte
        output.subList(1, 17) == [
                0xab, 0xd2, 0x63, 0x95, 0xc6, 0xfb, 0x36, 0xaf,
                0x42, 0x13, 0x13, 0x33, 0x96, 0xe3, 0x81, 0xc4
        ] as byte[]

        result.success

        where:
        minorProtocolVersion << [7, 8]
    }

    def "should throw an exception if no password supplier was provided"() {
        given:
        def session = new VncSession(Mock(VernacularConfig), Mock(InputStream), Mock(OutputStream))

        when:
        handler.authenticate(session)

        then:
        thrown(AuthenticationRequiredException)
    }
}
