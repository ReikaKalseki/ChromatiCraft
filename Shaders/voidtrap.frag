varying vec2 texcoord;

uniform sampler2D bgl_RenderedTexture;

uniform int time;
uniform int screenWidth;
uniform int screenHeight;
uniform mat4 modelview;
uniform mat4 projection;
uniform vec3 focus;

uniform float intensity;

uniform float distance;
uniform float rotation;

float distsq(vec2 a, vec2 b) {
	float f = float(screenHeight)/float(screenWidth);
	float dx = (a.x-b.x);
	float dy = (a.y-b.y)*f;
	return dx*dx+dy*dy;
}

vec2 rotate(vec2 pt, vec2 origin, float a) {
	pt.x *= float(screenWidth);
	pt.y *= float(screenHeight);
	origin.x *= float(screenWidth);
	origin.y *= float(screenHeight);
	vec2 r = pt-origin;
	float s = sin(a);
	float c = cos(a);
	mat2 m = mat2(c, -s, s, c);
	vec2 ret = m * r;
	ret += origin;
	ret.x /= float(screenWidth);
	ret.y /= float(screenHeight);
	return ret;
}

void main() {
	vec4 clipSpacePos = projection * (modelview * vec4(0.5, 0.5, 0.5, 1.0));
	vec3 ndcSpacePos = clipSpacePos.xyz / clipSpacePos.w;
	vec2 nodeXY = ((ndcSpacePos.xy + 1.0) / 2.0);
	
	float distsq = distsq(nodeXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 1.8-0.875*distsq*distance));
	float distfac_vertex = max(0.0, min(1.0, 1.0-1.0*distsq*distance));
	float cf = distfac_color;
	float vf = intensity*distfac_vertex;
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
    float r = color.r;
    float g = color.g;
    float b = color.b;
	float gs = r*0.2989+g*0.5870+b*0.1140;
	float rot = 0.125*vf*3.1416*2*rotation/pow(max(0.002, distsq), 0.25);///max(0.02, min(1, distsq*2.4));
	texcoord = rotate(texcoord, nodeXY, rot);
	
	vec4 color2 = texture2D(bgl_RenderedTexture, texcoord);
	
	float fac = max(0, (abs(rotation)-0.5))*2;
	fac *= cf;
	color.rgb = mix(color2.rgb, color.rgb, fac);

    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}