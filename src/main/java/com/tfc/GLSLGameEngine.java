package com.tfc;

import static org.lwjgl.opengl.GL30.*;

import com.tfc.utils.*;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

//heavily inspired by mrdoob's glsl sandbox
//https://github.com/mrdoob/glsl-sandbox/blob/master/static/index.html
//http://glslsandbox.com/e
public class GLSLGameEngine {
	public static GLSLWindow window = new GLSLWindow();
	public static GLSLShaderProgram renderProgram, dataProgram;
	public static GLSLTarget dataTarget, dataBackTarget;
	public static String fragmentRender, fragmentData;
	private static Properties inputs;
	public static String vertexShader = StreamUtils.readFromCLSilently("vertex_shader.vsh");
	public static int buffer;
	private static final long startingTime = new Date().getTime();
	public static Vector2f dataRes;
	
	public static FileTracker renderShaderFileTracker = new FileTracker("program/render_shader.fsh", (contents)->{
		fragmentRender = contents;
		GLSLShaderProgram newProgram = new GLSLShaderProgram(vertexShader, fragmentRender, inputs);
		if (newProgram.getLog() != null) {
			newProgram.destroy();
			String pattern = "yyyy/MMMM/dd/hh-mm-ss";
			Date time = new Date();
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			File file = new File("logs/" + format.format(time) + ".render_shader.log");
			StreamUtils.readFileOrCreateAndDefault(file.getPath(), newProgram::getLog);
		} else {
			renderProgram.destroy();
			renderProgram = newProgram;
		}
	});
	
	public static FileTracker dataShaderFileTracker = new FileTracker("program/data_shader.fsh", (contents)-> {
		fragmentData = contents;
		GLSLShaderProgram newProgram = new GLSLShaderProgram(vertexShader, fragmentData, inputs);
		if (newProgram.getLog() != null) {
			newProgram.destroy();
			String pattern = "yyyy/MMMM/dd/hh-mm-ss";
			Date time = new Date();
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			File file = new File("logs/" + format.format(time) + ".data_shader.log");
			StreamUtils.readFileOrCreateAndDefault(file.getPath(), newProgram::getLog);
		} else {
			dataProgram.destroy();
			dataProgram = newProgram;
		}
	});
	
	public static FileTracker vertexShaderFileTracker = new FileTracker("program/vertex_shader.vsh", (contents)->{
		vertexShader = contents;
		{
			GLSLShaderProgram newProgram = new GLSLShaderProgram(vertexShader, fragmentData, inputs);
			if (newProgram.getLog() != null) {
				newProgram.destroy();
				String pattern = "yyyy/MMMM/dd/hh-mm-ss";
				Date time = new Date();
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				File file = new File("logs/" + format.format(time) + ".vertex_data_shader.log");
				StreamUtils.readFileOrCreateAndDefault(file.getPath(), newProgram::getLog);
			} else {
				dataProgram.destroy();
				dataProgram = newProgram;
			}
		}
		{
			GLSLShaderProgram newProgram = new GLSLShaderProgram(vertexShader, fragmentRender, inputs);
			if (newProgram.getLog() != null) {
				newProgram.destroy();
				String pattern = "yyyy/MMMM/dd/hh-mm-ss";
				Date time = new Date();
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				File file = new File("logs/" + format.format(time) + ".vertex_render_shader.log");
				StreamUtils.readFileOrCreateAndDefault(file.getPath(), newProgram::getLog);
			} else {
				renderProgram.destroy();
				renderProgram = newProgram;
			}
		}
	});
	
//	public static FileTracker dataTextureSetupTracker = new FileTracker("program/data_texture_setup.lua", (contents)->{
//		dataTextureSetup = new LUAExecutor();
//		dataTextureSetup.compile("program/data_texture_setup.lua");
//		dataRes = new Vector2f(dataTextureSetup.execute("getWidth", LuaValue.valueOf(100), LuaValue.valueOf(100)).toint(), dataTextureSetup.execute("getHeight", LuaValue.valueOf(100), LuaValue.valueOf(100)).toint());
//		dataTarget.update((int)dataRes.x, (int)dataRes.y);
//		dataBackTarget.update((int) dataRes.x, (int) dataRes.y);
//	});
	
	static {
		StreamUtils.readFileOrCreateAndDefault("credits.txt", () -> StreamUtils.readFromCLSilently("credits.txt"));
		inputs = new Properties(StreamUtils.readFileOrCreateAndDefault("program/inputs.properties", () -> StreamUtils.readFromCLSilently("program/inputs.properties")));
		vertexShader = (StreamUtils.readFileOrCreateAndDefault("program/vertex_shader.vsh", () -> vertexShader));
		fragmentRender = (StreamUtils.readFileOrCreateAndDefault("program/render_shader.fsh", () -> StreamUtils.readFromCLSilently("program/render_shader.fsh")));
		renderProgram = new GLSLShaderProgram(vertexShader, fragmentRender, inputs);
		fragmentData = (StreamUtils.readFileOrCreateAndDefault("program/data_shader.fsh", () -> StreamUtils.readFromCLSilently("program/data_shader.fsh")));
		dataProgram = new GLSLShaderProgram(vertexShader, fragmentData, inputs);
		Properties properties = new Properties(StreamUtils.readFileOrCreateAndDefault("program/data_resolution.properties", () -> StreamUtils.readFromCLSilently("program/data_resolution.properties")));
		dataRes = new Vector2f(Integer.parseInt(properties.getValue("width")), Integer.parseInt(properties.getValue("height")));
		dataTarget = new GLSLTarget((int) dataRes.x, (int) dataRes.y);
		dataBackTarget = new GLSLTarget((int) dataRes.x, (int) dataRes.y);
	}
	
	public static long lastFrame;
	
	public static boolean firstFrame = true;
	
	public static void main(String[] args) {
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);
		buffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, buffer);
		glBufferData(GL_ARRAY_BUFFER, new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f}, GL_STATIC_DRAW);
		glViewport(0, 0, window.getWidth(), window.getWidth());
		while (!window.shouldClose()) loop();
		window.destroy();
	}
	
	public static void loop() {
		renderShaderFileTracker.tick();
		dataShaderFileTracker.tick();
		vertexShaderFileTracker.tick();
		Vector2f mouse = new Vector2f(window.getMouseX(), 1 - window.getMouseY());
		Vector2f resolution = new Vector2f(window.getCurrentWidth(), window.getHeight());
		dataProgram.start();
		long currentTime = new Date().getTime();
		long frameTime = currentTime - lastFrame;
		lastFrame = currentTime;
		long time = lastFrame - startingTime;
		dataProgram.updateUniforms(time, frameTime, firstFrame, mouse, resolution, new Vector2f(window.getDragX(), window.getDragY()), new Vector2f(window.getDrag2X(), window.getDrag2Y()), window.pressedKeys, dataRes);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, dataBackTarget.texture);
		glBindFramebuffer(GL_FRAMEBUFFER, dataTarget.framebuffer);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, window.getWidth(), window.getHeight());
		drawQuad();
		dataProgram.stop();
		renderProgram.start();
		renderProgram.updateUniforms(time, frameTime, firstFrame, mouse, resolution, new Vector2f(window.getDragX(), window.getDragY()), new Vector2f(window.getDrag2X(), window.getDrag2Y()), window.pressedKeys, dataRes);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, dataTarget.texture);
		glBindFramebuffer(GL_FRAMEBUFFER, (int) MemoryUtil.NULL);
		drawQuad();
		renderProgram.stop();
		glBegin(GL_QUADS);
		glEnd();
		window.update();
		GLSLTarget tmp = dataTarget;
		dataTarget = dataBackTarget;
		dataBackTarget = tmp;
		if (firstFrame) {
			firstFrame = false;
		}
		try {
			Thread.sleep(-1);
		} catch (Throwable ignored) {
		}
	}
	
	private static void drawQuad() {
		glBegin(GL_QUADS);
		glColor3f(0, 0, 0);
		glVertex2d(-1, -1);
		glColor3f(0, 0, 0);
		glVertex2d(-1, 1);
		glColor3f(0, 0, 0);
		glVertex2d(1, 1);
		glColor3f(0, 0, 0);
		glVertex2d(1, -1);
		glEnd();
	}
}
