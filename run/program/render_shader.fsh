#version 120

#ifdef GL_ES
precision highp float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;
uniform vec2 dataResolution;
in vec2 surfacePosition;
uniform sampler2D texture1;
uniform sampler2D dataTexture;
uniform vec2 surfaceSize;
uniform sampler2D backbuffer;

vec2 origin = vec2(0.5, 0.6);
vec2 offset = vec2(0.53, 0.3);
vec2 offset1 = vec2(0.5, 0.2);
vec2 offset2 = vec2(0.55,0.5);

float time1 = time * 3.;

//https://www.shadertoy.com/view/4ljfRD
vec2 getInterp(vec2 start, vec2 end, vec2 pos) {
	float d = distance(start, end);
	float duv = distance(start, pos);
	vec2 interpolated = mix(start, end, clamp(duv / d, 0., 1.));
	return interpolated;
}

float drawLine(vec2 p1, vec2 p2, vec2 uv, float a) {
	//	a *= 2.;
	float one_px = 1. / resolution.x;
	vec2 interpolated = getInterp(p1, p2, uv);
	float r = (1. - ((distance(interpolated, uv) * 10.) / a));
	return r;
}

float drawCircle(vec2 pos, vec2 center, float rad) {
	return max(0., ceil(1. - length(pos - center) / rad));
}

void main( void ) {
	vec2 position = (surfacePosition);
	float dist = length(position / surfaceSize) * 3.;
	//	position *= dist;
	vec4 fragCol = vec4(0);
	vec2 playerX = texture2D(dataTexture, vec2(-1, -1)).xy;
	playerX.x = (playerX.x);
	vec2 playerY = texture2D(dataTexture, vec2(2f / dataResolution.y, -1)).xy;
	playerY.x = (playerY.x);
	position += vec2(playerX.x, playerY.x);
	fragCol.y = drawCircle(position, vec2(playerX.x, playerY.x), 0.025);
	if (mod(position.x, 1) <= 0.725 && mod(position.x, 1) >= 0.275 && mod(position.y, 1) <= 0.475 && mod(position.y, 1) >= 0.275) {
		fragCol = vec4(1., 0, 0, 0);
		if (position.x <= 0.725 && position.x >= 0.275 && position.y <= 0.475 && position.y >= 0.275) {
			fragCol = vec4(0, 0, 1, 0);
		}
	}
	position += vec2(playerX.y * 256, playerY.y * 256);
	if (position.x < -0.025 || position.y < -0.025 || position.x > 257.025 || position.y > 257.025) {
		if (position.y > -0.05 && position.x > -0.05 && position.x < 257.05 && position.y < 257.05) fragCol = vec4(0, 1, 1, 0);
		else fragCol = vec4(0, 0, 0, 0);
	}
	fragCol /= dist;
	vec4 data = texture2D(dataTexture, gl_FragCoord.xy / resolution);
	if (data.x != 0 || data.y != 0 || data.z != 0) fragCol = data;
	//	fragCol = vec4((position.xy / surfaceSize) / 256, dist / 5., 1);
	fragCol += texture2D(backbuffer, gl_FragCoord.xy / resolution);

	gl_FragColor = max(fragCol, 0.);
}
