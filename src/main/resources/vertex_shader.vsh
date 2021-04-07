in vec3 position;
varying vec2 surfacePosition;
uniform vec2 offset;
uniform vec2 surfaceSize;
uniform vec2 resolution;

void main() {
    gl_Position = vec4(position, 1.0);
    float x = position.x <= 0 ? -1 : 1;
    float y = position.y <= 0 ? -1 : 1;
    if (resolution.x < resolution.y) {
        float maxRes = resolution.x / resolution.y;
        surfacePosition = ((vec2(x, y / maxRes) * surfaceSize) + offset);
    } else {
        float maxRes = resolution.y / resolution.x;
        surfacePosition = ((vec2(x / maxRes, y) * surfaceSize) + offset);
    }
}