uniform int coreRed;
uniform int coreGreen;
uniform int coreBlue;

uniform float distance;

void main() {
    vec2 coreXY = getScreenPos(0.5, 0.5, 0.5);
	
	float distv = distsq(coreXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 1.0-0.6*distv*distance)-min(1.0, 0.04/(distv*distance)));
	float distfac_vertex = max(0.0, min(1.0, 3.5-40.0*distv*distance));
	float cf = intensity*distfac_color*0.55;
	float vf = intensity*distfac_vertex;
	
	texcoord = mix(texcoord, coreXY, vf/7.5);
	
    vec4 orig = texture2D(bgl_RenderedTexture, texcoord);
	
	float r = float(coreRed)/255.0;
	float g = float(coreGreen)/255.0;
	float b = float(coreBlue)/255.0;
	/*
	vec3 hsl = rgb2hsb(clamp(orig.rgb*vec3(1.0+r/2.5*cf, 1.0+g/2.5*cf, 1.0+b/2.5*cf), 0, 1));
	vec3 pylon = rgb2hsb(vec3(r, g, b));
	
	vec3 shifted = hsb2rgb(vec3(pylon.x, float(float(pylon.y)+float(hsl.y))/2.0, hsl.z));
	
	vec3 net = mix(orig.rgb, shifted, cf);
	*/
	//vec3 pylon = rgb2hsb(vec3(r, g, b));
	//pylon.z *= cf;
	//pylon = hsb2rgb(pylon);
	vec3 pylon = vec3(r*cf, g*cf, b*cf);
	vec3 net = orig.rgb+pylon;
	
    gl_FragColor = vec4(net.r, net.g, net.b, orig.a);
}