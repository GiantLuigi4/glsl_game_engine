package com.tfc.utils;

import static org.lwjgl.opengl.GL30.*;

public class GLSLTarget {
	public int framebuffer, renderbuffer, texture;
	
	public GLSLTarget(int width, int height) {
		create(width, height);
	}
	
	public void update(int width, int height) {
		glDeleteFramebuffers(framebuffer);
		glDeleteRenderbuffers(renderbuffer);
		create(width, height);
		glDeleteTextures(texture);
	}
	
	//port of the create target method from mrdoob's glsl sandbox
	private GLSLTarget create(int width, int height) {
		this.framebuffer = glGenFramebuffers();
		this.renderbuffer = glGenRenderbuffers();
		this.texture = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, this.texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		glBindFramebuffer(GL_FRAMEBUFFER, this.framebuffer);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture, 0);
		
		glBindRenderbuffer(GL_RENDERBUFFER, this.renderbuffer);
		
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, this.renderbuffer);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		return this;
	}
}
