#ifdef GL_ES
precision highp float;
#endif

uniform float time;
uniform float frameTime;
uniform vec2 dataResolution;
uniform sampler2D backbuffer;
uniform float keySpaceA;
uniform float mouseLeft;
uniform float isFirstFrame;
float firstFrame = isFirstFrame;
float keySpace = max(keySpaceA, mouseLeft);

vec4 fade(vec4 pos, float fade, float min) {
	vec4 fragCol = texture2D(backbuffer, gl_FragCoord.xy / dataResolution.xy);
	fragCol /= fade;
	fragCol.x = max(fragCol.x, min);
	fragCol.y = fragCol.x;
	fragCol.z = fragCol.x;
	fragCol.w = fragCol.x;
	return fragCol;
}

//https://www.shadertoy.com/view/4tKfRc
float hash(float n) { return fract(sin(n) * 1e4); }
float hash(vec2 p) { return fract(1e4 * sin(17.0 * p.x + p.y * 0.1) * (0.1 + abs(sin(p.y * 13.0 + p.x)))); }
float noise(float x) {
	float i = floor(x);
	float f = fract(x);
	float u = f * f * (3.0 - 2.0 * f);
	float v = mix(hash(i), hash(i + 1.0), u);
	return (v >= 0.75) ? 1 : v >= 0.5 ? 0.5 : 0;
}

void main( void ) {
	vec4 fragCol = texture2D(backbuffer, gl_FragCoord.xy / dataResolution.xy);
	vec4 jumpFrag = texture2D(backbuffer, vec2(2, 0) / dataResolution.xy);
	vec4 yPosFrag = texture2D(backbuffer, vec2(0, 0) / dataResolution.xy);
	vec4 spaceToggle = texture2D(backbuffer, vec2(2, 0) / dataResolution.xy);
	vec4 invertCollision = texture2D(backbuffer, vec2(0, 2) / dataResolution.xy);

	if (gl_FragCoord.x <= 1 && gl_FragCoord.y <= 1) {
		fragCol.x += 1f/ 255;
		if (fragCol.x < 0) {
			fragCol.y -= 1f / 255;
			fragCol.x += 1;
			if (fragCol.y < 0) fragCol.x -= 1;
		}
		if (fragCol.x > 1) {
			fragCol.y += 1f / 255;
			fragCol.x -= 1;
			if (fragCol.y > 1) fragCol.x += 1;
		}
		if (noise(fragCol.x + fragCol.y * 255) == invertCollision.x) {
			fragCol = vec4(0, 0, 0, 0);
		}
	} else if (gl_FragCoord.x <= 2 && gl_FragCoord.y <= 1) {
		fragCol = vec4(0, 0, 0, 1);
		if (keySpace == 1) fragCol = vec4(1, 1, 1, 1);
	} else if (gl_FragCoord.x <= 1 && gl_FragCoord.y <= 2) {
		if (keySpace == 1 && spaceToggle.x != 1) fragCol = 1 - fragCol;
	} else if (gl_FragCoord.x >= 95 && gl_FragCoord.x <= 99) {
		if (gl_FragCoord.y >= 1 && gl_FragCoord.y <= 2) {
			if (keySpace == 1) fragCol = vec4(1, 1, 1, 1);
			else fragCol = fade(gl_FragCoord, 1.1, 0.1);
		} else fragCol = vec4(0, 0, 0, 0);
	} else {
		fragCol = vec4(0, 0, 0, 0);
	}

	gl_FragColor = fragCol;
}
