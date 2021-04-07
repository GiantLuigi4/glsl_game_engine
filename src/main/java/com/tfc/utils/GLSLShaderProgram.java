package com.tfc.utils;

import org.joml.Vector2f;

import java.util.HashMap;

import static org.lwjgl.opengl.GL20.*;

//http://forum.lwjgl.org/index.php?topic=6216.0
public class GLSLShaderProgram {
	private final int programID;
	private final int vertexShaderID;
	private final int fragmentShaderID;
	
	private String log = null;
	
	public String getLog() {
		return log;
	}
	
	private final HashMap<String, Integer> uniformMap = new HashMap<>();
	
	private final HashMap<String, String> keyUniformMap = new HashMap<>();
	
	public GLSLShaderProgram(String srcFrag, String srcVert, Properties keyUniforms) {
		vertexShaderID = loadShader(GL_VERTEX_SHADER, srcFrag);
		fragmentShaderID = loadShader(GL_FRAGMENT_SHADER, srcVert);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		glBindAttribLocation(programID, 0, "position");
		glLinkProgram(programID);
		glValidateProgram(programID);
		getUniformLocation(programID, "time");
		getUniformLocation(programID, "frameTime");
		getUniformLocation(programID, "mouse");
		getUniformLocation(programID, "resolution");
		getUniformLocation(programID, "dataTexture");
		getUniformLocation(programID, "offset");
		getUniformLocation(programID, "surfaceSize");
		getUniformLocation(programID, "isFirstFrame");
		getUniformLocation(programID, "dataResolution");
		for (String entry : keyUniforms.getEntries()) {
			keyUniformMap.put(keyUniforms.getValue(entry), entry);
			getUniformLocation(programID, entry);
		}
	}
	
	public void start() {
		glUseProgram(programID);
	}
	
	public void stop() {
		glUseProgram(0);
	}
	
	public void destroy() {
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		glDeleteProgram(programID);
	}
	
	public void updateUniforms(long time, long frameTime, boolean firstFrame, Vector2f mousePos, Vector2f resolution, Vector2f offset, Vector2f size, Iterable<String> keys, Vector2f dataResolution) {
		glUniform1f(uniformMap.get("time"), time / 1000f);
		glUniform1f(uniformMap.get("frameTime"), frameTime / 1000f);
		glUniform2f(uniformMap.get("mouse"), mousePos.x, mousePos.y);
		glUniform2f(uniformMap.get("resolution"), resolution.x, resolution.y);
		glUniform2f(uniformMap.get("offset"), -offset.x, offset.y);
		glUniform2f(uniformMap.get("surfaceSize"), size.x / 2, size.y / 2);
		glUniform1f(uniformMap.get("isFirstFrame"), firstFrame ? 1f : 0f);
		glUniform2f(uniformMap.get("dataResolution"), dataResolution.x, dataResolution.y);
		for (String value : keyUniformMap.values()) {
			glUniform1f(uniformMap.get(value), 0);
		}
		for (String key : keys) {
			if (keyUniformMap.containsKey(key)) {
				glUniform1f(uniformMap.get(keyUniformMap.get(key)), 1);
			}
		}
	}
	
	private int loadShader(int type, String src) {
		int id = glCreateShader(type);
		glShaderSource(id, src);
		glCompileShader(id);
		if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
			String l = glGetShaderInfoLog(id, 500);
			System.out.println(l);
			glDeleteShader(id);
			id = glCreateShader(type);
			glShaderSource(id, src + "}");
			glCompileShader(id);
			log = l;
		}
		return id;
	}
	
	private int getUniformLocation(int id, String name) {
		int loc = glGetUniformLocation(id, name);
		uniformMap.put(name, loc);
		return loc;
	}
}