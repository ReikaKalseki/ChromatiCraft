#import math
#import color

void main() {	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float gs = getVisualBrightness(color.rgb);
	vec3 hsb = rgb2hsb(color.rgb);

	hsb.z = min(1.0, hsb.z*(gs+max(0.0, hsb.y-0.5)*0.5)*2.0);
	hsb.y = min(1.0, hsb.y*1.25);
	vec3 res = mix(color.rgb, hsb2rgb(hsb), intensity*0.6);

    gl_FragColor = vec4(res.x, res.y, res.z, color.a);
}