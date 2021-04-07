package com.tfc.utils;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;

//https://www.lwjgl.org/guide
//heavily inspired by mc
public class GLSLWindow {
	public long window;
	public int width, height;
	public int mouseX, mouseY;
	public float dragX, dragY;
	public float drag2X = 1;
	public float drag2Y = 1;
	
	public boolean isDragging;
	public boolean isDragging2;
	public int lastDragMouseX, lastDragMouseY;
	public int lastDragMouse2X, lastDragMouse2Y;
	
	public boolean isFocused = false;
	
	public ArrayList<String> pressedKeys = new ArrayList<>();
	
	private static final HashMap<Integer, String> keyMap = new HashMap<>();
	
	static {
		for (Field field : org.lwjgl.glfw.GLFW.class.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.getName().startsWith("GLFW_KEY_")) {
				try {
					keyMap.put((Integer) field.get(null), field.getName().replace("GLFW_KEY_", "").toLowerCase());
				} catch (Throwable ignored) {
					ignored.printStackTrace();
				}
			}
		}
		System.out.println(keyMap.toString());
	}
	
	public GLSLWindow() {
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		window = org.lwjgl.glfw.GLFW.glfwCreateWindow(1000, 1000, "", 0, 0);
		if (window == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		
		GL.createCapabilities();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		glfwSetWindowFocusCallback(window, (a, b) -> {
			isFocused = b;
		});
		
		glfwSetMouseButtonCallback(window, (a, b, c, d) -> {
			if (!isFocused) return;
			if (b == 0 && c == 1) {
				isDragging = true;
				lastDragMouseX = mouseX;
				lastDragMouseY = mouseY;
			} else if (b == 0) {
				isDragging = false;
			} else if (b == 1 && c == 1) {
				isDragging2 = true;
				lastDragMouse2X = mouseX;
				lastDragMouse2Y = mouseY;
			} else if (b == 1) {
				isDragging2 = false;
			}
		});
		
		glfwSetCursorPosCallback(window, (a, x, y) -> {
			if (!isFocused) return;
			mouseX = (int) x;
			mouseY = (int) y;
		});
		
		glfwSetKeyCallback(window, (a, b, c, d, e) -> {
			System.out.println("b: " + decodeChar(b) + ", d: " + d + ", e: " + e);
			if (d == 0 && pressedKeys.contains(decodeChar(b))) {
				pressedKeys.remove(decodeChar(b));
			} else if (d == 1) {
				pressedKeys.add(decodeChar(b));
			}
		});
	}
	
	private static String decodeChar(int code) {
		return keyMap.get(code);
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	public int getCurrentWidth() {
		return width;
	}
	
	public int getCurrentHeight() {
		return height;
	}
	
	public int getWidth() {
		int[] w = new int[1];
		glfwGetWindowSize(window, w, null);
		width = w[0];
		return width;
	}
	
	public int getHeight() {
		int[] h = new int[1];
		glfwGetWindowSize(window, null, h);
		height = h[0];
		return height;
	}
	
	public void update() {
		glfwSwapBuffers(window);
		glfwPollEvents();
	}
	
	public void destroy() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		glfwTerminate();
	}
	
	public float getMouseX() {
		if (width == 0) {
			return 0;
		}
		return (float) mouseX / width;
	}
	
	public float getMouseY() {
		if (height == 0) {
			return 0;
		}
		return (float) mouseY / height;
	}
	
	public float getDragX() {
		if (isDragging) {
			dragX += (mouseX - lastDragMouseX) * (drag2Y);
			lastDragMouseX = mouseX;
		}
		
		if (width == 0) {
			return 0;
		}
		return (float) dragX / 500f;
	}
	
	public float getDragY() {
		if (isDragging) {
			dragY += (mouseY - lastDragMouseY) * (drag2Y);
			lastDragMouseY = mouseY;
		}
		
		if (height == 0) {
			return 0;
		}
		return (float) dragY / 500f;
	}
	
	public float getDrag2X() {
		if (isDragging2) {
//			if (mouseY < lastDragMouse2Y) {
//				drag2Y *= 1.01;
//			} else if (mouseY != lastDragMouse2Y) {
//				drag2Y *= 0.99;
//			}
			lastDragMouse2X = mouseX;
		}
		
		if (width == 0) {
			return 0;
		}
		return (drag2Y);
	}
	
	public float getDrag2Y() {
		if (isDragging2) {
			if (mouseY < lastDragMouse2Y) {
				drag2Y *= ((double) lastDragMouse2Y / mouseY);
			} else if (mouseY != lastDragMouse2Y) {
				drag2Y /= ((double) mouseY / lastDragMouse2Y);
			}
			lastDragMouse2Y = mouseY;
		}
//		drag2Y = 1;
//		drag2Y = Math.max(0.0000001f, drag2Y);
//		drag2Y = Math.min(1000, drag2Y);
		drag2Y = Math.abs(drag2Y);
		
		if (height == 0) {
			return 0;
		}
		return (drag2Y);
	}
	
	public boolean hasResized() {
		int[] w = new int[1];
		int[] h = new int[1];
		glfwGetWindowSize(window, w, h);
		return h[0] != height || w[0] != width;
	}
}
