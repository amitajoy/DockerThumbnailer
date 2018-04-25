package com.amit.thumbnailer;

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
public class ThumbnailerController {

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