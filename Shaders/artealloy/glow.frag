#import math
#import geometry
#import color

uniform float distance;

void main() {
	vec2 nodeXY = getScreenPos(0, 0, 0);
	
	float distv = distsq(nodeXY, texcoord);
	float d = 0.9+0.2*sin(time*0.043)+0.04*sin(time*0.293);
	float distfac_color = max(0.0, min(1.25, d-0.045*distv*distance));
	//float distfac_color = max(0.0, min(1.0, 1.0-50.0*distv));
	//float distfac_color2 = max(0.0, min(1.0, 1.0-150.0*distv));
	float distfac_vertex = max(0.0, min(1.0, 1.5-15.0*distv));
	float cf = intensity*distfac_color*0.8;
	//float cf = intensity*distfac_color*0.75;
	//float cf2 = intensity*distfac_color2*0.5;
	float vf = intensity*distfac_vertex*0.006;
	
	vec2 texUV = texcoord.xy;
	
    vec4 color = texture2D(bgl_RenderedTexture, texUV);
	vec3 hsb = rgb2hsb(color.rgb);
	//vec3 c2 = vec3(1.0, 0.6+0.3*sin(time*0.032), 0.0);
	hsb.x = 0.09025+0.03475*sin(time*0.067)+0.0041*sin(time*0.213)+0.0019*sin(time*0.471); //range from 20 to 45 = 1/18 to 1/8 = 0.0555 to 0.125, ampl = 0.03475, ctr = 0.09025
	hsb.y = 1.0;//min(1.0, hsb.z*1.4+0.25);
	float z1 = min(1.0, hsb.z*1.5+0.375);
	float z2 = min(1.0, hsb.z*1.75+0.5);
	hsb.z = mix(z1, z2, 0.5+0.5*sin(time*0.114));
	vec3 c2 = hsb2rgb(hsb);
	color.rgb = mix(color.rgb, c2, cf);
    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}