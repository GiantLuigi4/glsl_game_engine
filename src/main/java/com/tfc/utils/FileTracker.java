package com.tfc.utils;

import java.io.File;
import java.util.function.Consumer;

public class FileTracker {
	long lastModified = 0;
	String file;
	Consumer<String> onUpdate;
	
	public FileTracker(String file, Consumer<String> onUpdate) {
		this.file = file;
		this.onUpdate = onUpdate;
		lastModified = new File(file).lastModified();
	}
	
	public void tick() {
		if (lastModified < new File(file).lastModified()) {
			onUpdate.accept(StreamUtils.readFile(file));
			lastModified = new File(file).lastModified();
		}
	}
}
