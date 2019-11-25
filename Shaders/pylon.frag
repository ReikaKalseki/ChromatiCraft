varying vec2 texcoord;

uniform sampler2D bgl_RenderedTexture;

uniform int time;
uniform int screenWidth;
uniform int screenHeight;
uniform mat4 modelview;
uniform mat4 projection;
uniform float screenX;
uniform float screenY;

uniform int pylonRed;
uniform int pylonGreen;
uniform int pylonBlue;
uniform float intensity;

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
    vec4 orig = texture2D(bgl_RenderedTexture, texcoord);
    
    float gs = orig.r*0.2989+orig.g*0.5870+orig.b*0.1140;
	
	vec3 hsl = rgb2hsb(orig.rgb);
	vec3 pylon = rgb2hsb(vec3(float(pylonRed)/255.0, float(pylonGreen)/255.0, float(pylonBlue)/255.0));
	
	//vec3 shifted = vec3(gs*pylonRed/255, gs*pylonGreen/255, gs*pylonBlue/255);
	vec3 shifted = hsb2rgb(vec3(pylon.x, float(float(pylon.y)+float(hsl.y))/2.0, hsl.z));
	
	vec3 net = mix(orig.rgb, shifted, intensity);
    
    gl_FragColor = vec4(net.x, net.y, net.z, orig.a);
}