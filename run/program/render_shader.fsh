#ifdef GL_ES
precision highp float;
#endif

uniform float time;
uniform vec2 resolution;
uniform vec2 dataResolution;
in vec2 surfacePosition;
uniform sampler2D dataTexture;

float drawCircle(vec2 pos, vec2 center, float rad) {
	return max(0., ceil(1. - length(pos - center) / rad));
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
	vec2 position = (surfacePosition);
	vec4 fragCol;
	position.y += abs(position.x);
	position.x = 0;
	vec2 playerY = texture2D(dataTexture, vec2(-1, -1)).xy;
	//	position += -vec2(0, playerY.x + playerY.y * 256);
	fragCol += vec4(0, drawCircle(position, vec2(0, -playerY.y * 255 - (playerY.x) - 0.02), 0.005), 0, 1);
	fragCol += vec4(0, drawCircle(position, vec2(0, 0), 0.01), 0, 1);
	position -= -vec2(0, playerY.x + 0.5);
	vec4 data = texture2D(dataTexture, gl_FragCoord.xy / resolution);
	vec4 invertCollision = texture2D(dataTexture, vec2(0, 2) / dataResolution.xy);
	float collision = noise(playerY.x + (playerY.y * 255) + surfacePosition.y + abs(surfacePosition.x));
	if (invertCollision.x == 0) {
		if (collision == 1) {
			collision = 0;
		} else if (collision == 0) {
			collision = 1;
		}
	}
	fragCol += collision;
	if (data.x != 0 || data.y != 0 || data.z != 0) fragCol = data;
	gl_FragColor = max(fragCol, 0.);
}
