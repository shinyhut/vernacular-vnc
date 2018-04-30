# Vernacular VNC

Vernacular is a pure Java Virtual Network Computing (VNC) client library. The bundled 'Vernacular Viewer' demonstrates how to use it in a Swing based desktop application.

Vernacular aims to implement the latest Remote Framebuffer Protocol Version 3.8, which is available here:

https://tools.ietf.org/html/rfc6143

## Current Status

### Handshake Messages

| Message         | Status |
|-----------------|--------|
| ProtocolVersion | Done   |
| Security        | Done   |
| SecurityResult  | Done   |

### Security Types

| Type | Status |
|------|--------|
| NONE | Done   |
| VNC  | Done   |

### Initialization Messages

| Message    | Status |
|------------|--------|
| ClientInit | Done   |
| ServerInit | Done   |

### Client-to-Server Messages

| Message                  | Status |
|--------------------------|--------|
| SetPixelFormat           | Done   |
| SetEncodings             | Done   |
| FramebufferUpdateRequest | Done   |
| KeyEvent                 | Done   |
| PointerEvent             | Done   |
| ClientCutText            | TODO   |

### Server-to-Client Messages

| Message                  | Status |
|--------------------------|--------|
| FramebufferUpdate        | Done   |
| SetColorMapEntries       | TODO   |
| Bell                     | Done   |
| ServerCutText            | Done   |

### Encodings

| Encoding | Status |
|----------|--------|
| Raw      | Done   |
| CopyRect | Done   |
| RRE      | Done   |
| Hextile  | TODO   |
| TRLE     | TODO   |
| ZRLE     | TODO   |

Note: only True Colour rendering is supported with a configurable colour depth of 8, 16 or 24 bits per pixel.

### Pseudo-Encodings

| Encoding    | Status |
|-------------|--------|
| Cursor      | TODO   |
| DesktopSize | Done   |

