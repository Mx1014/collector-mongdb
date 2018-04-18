package com.nhb.dtu.base;

public interface Device {

	void processReadingFrame(byte[] readingFrame);

	byte[] nextWritingFrame();

	boolean isComplete();

	boolean isIgnoreResponse();

}
