# Vernacular VNC
[![Latest release](https://img.shields.io/github/release/shinyhut/vernacular-vnc.svg)](https://github.com/shinyhut/vernacular-vnc/releases/latest)

Vernacular is a pure Java Virtual Network Computing (VNC) remote desktop client library. Vernacular is open-source and
released under the MIT license. 

## Usage

The Vernacular .jar file is executable, and it can be used as a simple but functional VNC client. However, Vernacular
is primarily intended to be used as a library in third-party applications: 

```
 package vernaculardemo;

 import com.shinyhut.vernacular.client.VernacularClient;
 import com.shinyhut.vernacular.client.VernacularConfig;
 import com.shinyhut.vernacular.client.rendering.ColorDepth;
 
 public class VernacularDemo {
 
     public static void main(String[] args) throws Exception {
 
         VernacularConfig config = new VernacularConfig();
         VernacularClient client = new VernacularClient(config);
 
         // Select 8-bits per pixel indexed color, or 8/16/24 bits per pixel true color
         config.setColorDepth(ColorDepth.BPP_8_INDEXED);
 
         // Set up callbacks for the various events that can happen in a VNC session
 
         // Exception handler
         config.setErrorListener(ex -> ex.printStackTrace());
 
         // Password supplier - this is only invoked if the remote server requires authentication
         config.setPasswordSupplier(() -> "my secret password");
 
         // Handle system bell events from the remote host
         config.setBellListener(v -> System.out.println("DING!"));
 
         // Receive content copied to the remote clipboard
         config.setServerCutTextListener(text -> System.out.println("Received copied text: " + text));
 
         // Receive screen updates from the remote host
         // The 'image' parameter is a java.awt.Image containing a current snapshot of the remote desktop
         // Expect this event to be triggered several times per second
         config.setFramebufferUpdateListener(image -> {
             int width = image.getWidth(null);
             int height = image.getHeight(null);
             System.out.println(String.format("Received a %dx%d screen update", width, height));
         });
 
         // Start the VNC session
         String host = "myvncserver";
         int port = 5900;
         client.start(host, port);
 
         // Move the mouse. Screen coordinates are relative to the top-left. 
         client.moveMouse(400, 300);
 
         // Click a mouse button. Buttons are numbered 1 - 3
         // 'Clicking' means sending a button pressed event followed by a button released event.
         client.updateMouseButton(1, true);
         client.updateMouseButton(1, false);
 
         // Type some text. 'Typing' a character means sending a key pressed event followed by a key released event.
         // Keys are identified by X11 KeySyms, see https://cgit.freedesktop.org/xorg/proto/x11proto/plain/keysymdef.h
         // For standard ASCII characters, KeySyms are generally the same as their ASCII code
         client.keyPress('t', true);
         client.keyPress('t', false);
         client.keyPress('e', true);
         client.keyPress('e', false);
         client.keyPress('s', true);
         client.keyPress('s', false);
         client.keyPress('t', true);
         client.keyPress('t', false);
 
         // Let the VNC session continue as long as required
         Thread.sleep(10000);
 
         // Terminate the VNC session and cleanup
         client.stop();
     }
 }
```

For a more realistic example, see [Vernacular Viewer](https://github.com/shinyhut/vernacular-vnc/blob/master/src/main/java/com/shinyhut/vernacular/VernacularViewer.java) in the source distribution, which demonstrates how to use Vernacular to build a working remote desktop application.
