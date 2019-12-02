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

float distsq(vec2 a, vec2 b) {
	float f = float(screenHeight)/float(screenWidth);
	float dx = (a.x-b.x);
	float dy = (a.y-b.y)*f;
	return dx*dx+dy*dy;
}

vec3 rgb2hsb(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

// Function from Iñigo Quiles
// https://www.shadertoy.com/view/MsS3Wc
vec3 hsb2rgb(vec3 c){
    vec3 rgb = clamp(abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
    rgb = rgb * rgb * (3.0 - 2.0 * rgb);
    return c.z * mix(vec3(1.0), rgb, c.y);
}

void main() {
	vec4 clipSpacePos = projection * (modelview * vec4(0.5, 1.0, 0.5, 1.0));
	vec3 ndcSpacePos = clipSpacePos.xyz / clipSpacePos.w;
	vec2 nodeXY = ((ndcSpacePos.xy + 1.0) / 2.0);
	
	float distsq = distsq(nodeXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 1.0-15.0*distsq));
	float distfac_vertex = max(0.0, min(1.0, 2.5-4.0*distsq*distance));
	float cf = intensity*distfac_color*0.8;
	float vf = intensity*distfac_vertex;
	
	vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	vec3 hsb = rgb2hsb(color.rgb);
	
	float wiggle = 0.5*hsb.y*vf/distance;
	
	texcoord.y += 0.41*wiggle*cos(34.5+texcoord.x*3.7*distance+float(time)/2.3);
	texcoord.x += 0.35*wiggle*sin(23.3+texcoord.y*3.1*distance+float(time)/3.8);
	
    color = texture2D(bgl_RenderedTexture, texcoord);

    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}