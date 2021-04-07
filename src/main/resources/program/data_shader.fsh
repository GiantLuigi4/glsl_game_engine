#ifdef GL_ES
precision highp float;
#endif

uniform float time;
uniform float frameTime;
uniform vec2 dataResolution;
uniform sampler2D backbuffer;
uniform float keyW;
uniform float keyA;
uniform float keyS;
uniform float keyD;
uniform float keyR;
uniform float isFirstFrame;
float firstFrame = isFirstFrame;

vec4 fade(vec4 pos, float fade, float min) {
	vec4 fragCol = texture2D(backbuffer, gl_FragCoord.xy / dataResolution.xy);
	fragCol /= fade;
	fragCol.x = max(fragCol.x, min);
	fragCol.y = fragCol.x;
	fragCol.z = fragCol.x;
	fragCol.w = fragCol.x;
	return fragCol;
}

void main( void ) {
	vec4 fragCol = texture2D(backbuffer, gl_FragCoord.xy / dataResolution.xy);
	vec4 fragColXPos = texture2D(backbuffer, vec2(0, 0) / dataResolution.xy);
	vec4 fragColYPos = texture2D(backbuffer, vec2(2, 0) / dataResolution.xy);
	vec4 jump = texture2D(backbuffer, vec2(0, 2) / dataResolution.xy);

	if (keyR == 1) {
		firstFrame = 1;
	}

	if (gl_FragCoord.x <= 1 && gl_FragCoord.y <= 1) {
		if (firstFrame == 1) {
			gl_FragColor = vec4(0.5, 0.5, 0.5, 0.);
			return;
		}
		fragCol.z = mix(fragCol.z, 0.5, 0.1);
		if (keyD == 1) fragCol.z += ((1f / 256) * 2.);
		if (keyA == 1) fragCol.z -= ((1f / 256) * 2.);
		fragCol.x += (fragCol.z - 0.5) / 10.;
		if (fragCol.x < 0) {
			fragCol.y -= 1f / 256;
			fragCol.x += 1;
			if (fragCol.y < 0) {
				fragCol.x -= 1;
			}
		}
		if (fragCol.x > 1) {
			fragCol.y += 1f / 256;
			fragCol.x -= 1;
			if (fragCol.y > 1) {
				fragCol.x += 1;
			}
		}
	} else if (gl_FragCoord.x <= 2 && gl_FragCoord.y <= 1) {
		if (firstFrame == 1) {
			gl_FragColor = vec4(0.5, 0.5, 0., 0.);
			return;
		}
		fragCol.z = mix(fragCol.z, 0.0, 0.01);
		if (keyW == 1 && jump.y != 0) {
			fragCol.z = max(fragCol.z, 0.5);
			fragCol.z += ((1f / 256) * 2.1);
		}
		if (keyS == 1) fragCol.z -= ((1f / 256) * 2.);
		if (
		fragColXPos.x >= 0.25 && fragColXPos.x <= 0.75 &&
		fragCol.x >= 0.25 && fragCol.x <= 0.5
		) {
			fragCol.z = max(0.5, fragCol.z);
			fragCol.x = 0.5;
		}
		fragCol.x += (fragCol.z - 0.5) / 10.;
		if (fragCol.x < 0) {
			fragCol.y -= 1f / 256;
			fragCol.x += 1;
			if (fragCol.y < 0) {
				fragCol.x -= 1;
			}
		}
		if (fragCol.x > 1) {
			fragCol.y += 1f / 256;
			fragCol.x -= 1;
			if (fragCol.y > 1) {
				fragCol.x += 1;
			}
		}
	} else if (gl_FragCoord.x <= 1. && gl_FragCoord.y <= 2.) {
		fragCol.x = 1;
		if (fragColXPos.x >= 0.25 && fragColXPos.x <= 0.75 && fragColYPos.x >= 0.25 && fragColYPos.x <= 0.5) {
			fragCol.x = 0;
			fragCol.y = (1f / 256) * 50;
		} else if (keyW == 1) {
			fragCol.y -= 1f / 256;
		} else {
			fragCol.y = 0;
		}
		if (fragColYPos.y == 0 && fragColYPos.x == 0) {
			fragCol.x = 1;
			fragCol.y = (1f / 256) * 50;
		}
	} else if (gl_FragCoord.x >= 94 && gl_FragCoord.x <= 95) {
		if (gl_FragCoord.y >= 1 && gl_FragCoord.y <= 2) {
			if (keyA == 1) fragCol = vec4(1, 1, 1, 1);
			else fragCol = fade(gl_FragCoord, 1.1, 0.1);
		} else fragCol = vec4(0, 0, 0, 0);
	} else if (gl_FragCoord.x >= 95 && gl_FragCoord.x <= 96) {
		if (gl_FragCoord.y >= 2 && gl_FragCoord.y <= 3) {
			if (keyW == 1) fragCol = vec4(1, 1, 1, 1);
			else fragCol = fade(gl_FragCoord, 1.1, 0.1);
		} else if (gl_FragCoord.y >= 1 && gl_FragCoord.y <= 3) {
			if (keyS == 1) fragCol = vec4(1, 1, 1, 1);
			else fragCol = fade(gl_FragCoord, 1.1, 0.1);
		} else fragCol = vec4(0, 0, 0, 0);
	} else if (gl_FragCoord.x >= 96 && gl_FragCoord.x <= 97) {
		if (gl_FragCoord.y >= 1 && gl_FragCoord.y <= 2) {
			if (keyD == 1) fragCol = vec4(1, 1, 1, 1);
			else fragCol = fade(gl_FragCoord, 1.1, 0.1);
		} else fragCol = vec4(0, 0, 0, 0);
	} else if (gl_FragCoord.x >= 98 && gl_FragCoord.x <= 99) {
		if (gl_FragCoord.y >= 2 && gl_FragCoord.y <= 3) {
			if (keyR == 1) fragCol = vec4(1, 1, 1, 1);
			else fragCol = fade(gl_FragCoord, 1.1, 0.1);
		} else fragCol = vec4(0, 0, 0, 0);
	} else if (gl_FragCoord.x <= 2 && gl_FragCoord.y <= 2) {
		fragCol = vec4(frameTime, frameTime, frameTime, 1);
	} else {
		fragCol = vec4(0, 0, 0, 0);
	}

	gl_FragColor = fragCol;
}
