
http://start.spring.io/

Slide 4
package com.lendingclub.thumbnailer;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class Controller {
@RequestMapping("/")
public String index() {
return "Greetings from Spring Boot!";
}
}


Slide 5


mvn install

java -jar target/thumbnailer-0.0.1-SNAPSHOT.jar

http://127.0.0.1:8080/



-> Greetings from Sprint Boot!


Slide 7
#FROM ImageName
FROM docker.tlcinternal.com:8000/lcapp:latest


#Other image examples:
#FROM java:8
#FROM aoesterer/im:v1

# The EXPOSE instruction informs Docker that the container
# listens on the specified network ports at runtime.
EXPOSE 8080

#ADD copies from source to destination
ADD /target/thumbnailer-0.0.1-SNAPSHOT.jar thumbnailer.jar

#ENTRYPOINT allows you specify a command that will
# execute when you run the container
ENTRYPOINT ["java","-jar","thumbnailer.jar"]
Slide 8
docker build -f Dockerfile -t thumbnailer .
docker images

docker run -p 8080:8080 -t thumbnailer
http://127.0.0.1:8080/
docker ps
docker exec -it 700ec7d20b56 /bin/bash
docker kill 700ec7d20b56


Slide 10
docker run -p 8080:8080 -t docker.<server>.com:8000/lcapp
docker ps
docker exec -it 00e7e18dceb5 /bin/bash
yum install -y ImageMagick
-> 55 binary dependencies installed
curl http://www.pdf995.com/samples/pdf.pdf > sample.pdf
mogrify  -format png -background white -thumbnail 200x200 sample.pdf
docker cp 00e7e18dceb5:/sample-0.png .
docker commit 00e7e18dceb5 lc/imagemagick:v1

Slide 12
<!-- https://mvnrepository.com/artifact/org.apache.commons/commons-exec -->
<dependency>
<groupId>org.apache.commons</groupId>
<artifactId>commons-exec</artifactId>
<version>1.3</version>
</dependency>


Slide 13
#FROM ImageName
FROM lc/imagemagick:v1

# The EXPOSE instruction informs Docker that the container
# listens on the specified network ports at runtime.
EXPOSE 8080

#ADD copies from source to destination
ADD /target/thumbnailer-0.0.1-SNAPSHOT.jar thumbnailer.jar

#ENTRYPOINT allows you specify a command that will
# execute when you run the container
ENTRYPOINT ["java","-jar","thumbnailer.jar"]
Slide 14


package com.lendingclub.thumbnailer;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.commons.exec.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import java.io.*;
import java.nio.file.*;
import java.nio.channels.*;
import java.net.URL;

@RestController
public class Controller {

@RequestMapping("/")
public ResponseEntity<byte[]> index(@RequestParam("url") String urlStr) {
int exitValue =-1;
byte[] imageBytes=null;

long id=System.currentTimeMillis();
String nameOrig="original_"+id;
String nameThumbnail="thumbnail_"+id;

try {

// Download Original
URL inputUrl = new URL(urlStr);
ReadableByteChannel rbc = Channels.newChannel(inputUrl.openStream());
FileOutputStream fos = new FileOutputStream(nameOrig);
fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

// Create Thumbnail
CommandLine cmdLine = CommandLine.parse("mogrify -format png -background white -thumbnail 200x200 "+nameOrig);

PumpStreamHandler streamHandler = new PumpStreamHandler();
DefaultExecutor executor = new DefaultExecutor();
executor.setStreamHandler(streamHandler);

exitValue = executor.execute(cmdLine);

// Read Thumbnail
if(new File(nameOrig+".png").exists()) {
imageBytes = Files.readAllBytes(Paths.get(nameOrig+".png"));
} else if(new File(nameOrig+"-0.png").exists()) {
imageBytes = Files.readAllBytes(Paths.get(nameOrig+"-0.png"));
}

} catch(Exception e) {
System.out.println("Caught Exception"+e);
}

// Serve Thumbnail
return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
}
}
Slide 19
mvn install
docker build -f Dockerfile -t thumbnailer .
docker run -p 8080:8080 -t thumbnailer

http://127.0.0.1:8080/?url=abc.com/e.jpg

http://127.0.0.1:8080/?url=http%3A%2F%2Fwww.pdf995.com%2Fsamples%2Fpdf.pdf
