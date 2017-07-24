package com.shinyhut.vernacular.protocol.auth

import com.shinyhut.vernacular.client.VernacularConfig
import com.shinyhut.vernacular.client.VncSession
import com.shinyhut.vernacular.client.exceptions.AuthenticationRequiredException
import spock.lang.Specification
import spock.lang.Subject

import java.util.function.Supplier

class VncAuthenticationHandlerTest extends Specification {

    @Subject
    handler = new VncAuthenticationHandler()

    def "should request SecurityType 'VNC', encrypt the server's challenge and send the result"() {
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
        ] as byte[]

        def session = new VncSession('host', 0, config, new ByteArrayInputStream(challenge), new ByteArrayOutputStream())

        when:
        handler.authenticate(session)

        then:
        def output = ((ByteArrayOutputStream) session.outputStream).toByteArray() as List
        output.size() == 17
        output[0] == 0x02 as byte
        output.subList(1, 17) == [
                0xab, 0xd2, 0x63, 0x95, 0xc6, 0xfb, 0x36, 0xaf,
                0x42, 0x13, 0x13, 0x33, 0x96, 0xe3, 0x81, 0xc4
        ] as byte[]
    }

    def "should throw an exception if no password supplier was provided"() {
        given:
        def session = new VncSession('host', 0, Mock(VernacularConfig), Mock(InputStream), Mock(OutputStream))

        when:
        handler.authenticate(session)

        then:
        thrown(AuthenticationRequiredException)
    }
}
