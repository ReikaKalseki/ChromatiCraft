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
	vec4 clipSpacePos = projection * (modelview * vec4(0.5, 3.0, 0.5, 1.0));
	vec3 ndcSpacePos = clipSpacePos.xyz / clipSpacePos.w;
	vec2 nodeXY = ((ndcSpacePos.xy + 1.0) / 2.0);
	
	float distsq = distsq(nodeXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 1.0-7.5*distsq));
	float distfac_vertex = max(0.0, min(1.0, 2.5-75.0*distsq));
	float cf = intensity*distfac_color*0.8;
	float vf = intensity*distfac_vertex;
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
    
    float r = color.r;
    float g = color.g;
    float b = color.b;
	
	//float gs = (r+g+b)/3;
	float gs = r*0.2989+g*0.5870+b*0.1140;
	
	float wiggle = (0.125+gs*0.875)*vf*0.02;
	
	texcoord.y += 0.41*wiggle*sin(34.5+texcoord.x*85.7+float(time)/2.3);
	texcoord.x += 0.33*wiggle*sin(23.3+texcoord.y*81.8+float(time)/2.1);
	
    color = texture2D(bgl_RenderedTexture, texcoord);
	
	vec3 color2 = color.rgb;
	color2.r += 0.5+0.5*sin(45.4+float(time)/4.7);
	color2.g += 0.5+0.5*sin(27.9+float(time)/5.3);
	color2.b += 0.5+0.5*sin(36.1+float(time)/5.1);
	
	color.rgb = mix(color.rgb, color2, cf);
	
    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}