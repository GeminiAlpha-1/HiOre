#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

in vec4 vertexColor;
in vec2 localPos;
flat in vec2 halfSize;
flat in vec2 shapeParams;

out vec4 fragColor;

void main() {
    float radius = shapeParams.x;
    float border = shapeParams.y;
    vec2 p = localPos - halfSize;
    vec2 q = abs(p) - halfSize + radius;
    float dist = length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;
    float aa = max(fwidth(dist), 0.75);
    float coverage = 1.0 - smoothstep(-aa, aa, dist);
    if (coverage <= 0.0) {
        discard;
    }
    vec3 rgb = vertexColor.rgb;
    if (border > 0.0) {
        float edge = smoothstep(-border - aa, -border + aa, dist);
        rgb = mix(rgb * 0.32, rgb, edge);
    }
    fragColor = vec4(rgb, vertexColor.a * coverage) * ColorModulator;
}