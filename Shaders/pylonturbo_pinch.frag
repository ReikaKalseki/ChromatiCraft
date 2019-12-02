varying vec2 texcoord;

uniform sampler2D bgl_RenderedTexture;

uniform int time;
uniform int screenWidth;
uniform int screenHeight;
uniform mat4 modelview;
uniform mat4 projection;
uniform vec3 focus;

uniform float intensity;

float distsq(vec2 a, vec2 b) {
	float f = float(screenHeight)/float(screenWidth);
	float dx = (a.x-b.x);
	float dy = (a.y-b.y)*f;
	return dx*dx+dy*dy;
}

void main() {
	vec4 clipSpacePos = projection * (modelview * vec4(0.5, 0.5, 0.5, 1.0));
	vec3 ndcSpacePos = clipSpacePos.xyz / clipSpacePos.w;
	vec2 nodeXY = ((ndcSpacePos.xy + 1.0) / 2.0);
	
	float distsq = distsq(nodeXY, texcoord);
	
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);

    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}